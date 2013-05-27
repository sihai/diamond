/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.repository.client.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * LRU Cache (in file), 非线程安全
 * @author sihai
 *
 */
public class FileLRUCache<K,V> extends AbstractCache<K, V> {

	private static final String CTRL_SUFFIX = "ctrl";
	private static final int INCREMENT_OFFSET = 4096;
	
	private static final Log logger = LogFactory.getLog(FileLRUCache.class);
	
	private final int maxEntries;
	
	/**
	 * store of index in memory
	 */
	private LinkedHashMap<K, Index> indexStore;
	
	/**
	 * 
	 */
	private LinkedList<Index> frees;
	
	/**
	 * 
	 */
	private final String fileName;
	
	/**
	 * 
	 */
	private File ctrlFile;
	
	/**
	 * 
	 */
	private int offset;
	
	/**
	 * 
	 */
	private RandomAccessFile dataFile;
	
	/**
	 * 
	 */
	public FileLRUCache(String fileName) {
		this(DEFAULT_MAX_ENTRIES, fileName);
	}
	
	/**
	 * 
	 * @param maxEntries
	 */
	public FileLRUCache(int maxEntries, String fileName) {
		this.maxEntries = maxEntries;
		this.fileName = fileName;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		
		this.indexStore = new LinkedHashMap<K, Index>(maxEntries) {
			protected boolean removeEldestEntry(Map.Entry<K, Index> eldest) {
				boolean isNeedRemove = indexStore.size() > FileLRUCache.this.maxEntries;
				if(isNeedRemove) {
					insertFree(eldest.getValue());
					// fire
					fireEliminated(eldest.getKey(), FileLRUCache.this.get(eldest.getKey()));
					System.out.println(String.format("Eliminated one, key:%s", eldest.getKey()));
				}
				return isNeedRemove;
			}
		};
		this.frees = new LinkedList<Index>();
		
		// 
		initCtrl();
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		sync();
	}

	@Override
	public V get(K key) {
		try {
			Index index = indexStore.get(key);
			return null == index ? null : readValue(index);
		} catch (IOException e) {
			logger.error(String.format("Read data file:%s failed", fileName), e);
			return null;
		}
	}

	@Override
	public void put(K key, V value) {
		byte[] bytes = getConverter().toBytes(value);
		Index index = indexStore.get(key);
		if(null == index) {
			// NEW
			
			index = findFree(bytes.length);
			if(null == index) {
				index = newIndex(key, bytes.length);
			}
			index.key = key;
			indexStore.put(key, index);
		} else {
			// TODO UPDATE
		}
		
		try {
			writeValue(index, bytes);
		} catch (IOException e) {
			logger.error(String.format("Write data file:%s failed", fileName), e);
		}
	}
	
	@Override
	public void delete(K key) {
		Index index = indexStore.remove(key);
		if(null != index) {
			insertFree(index);
		}
	}

	/**
	 * 
	 */
	private void initCtrl() {
		// init index
		String ctrlFileName = String.format("%s.%s", fileName, CTRL_SUFFIX);
		ObjectInputStream in = null;
		
		// create index file is need
		this.ctrlFile = new File(ctrlFileName);
		if(!this.ctrlFile.exists()) {
			try {
				this.ctrlFile.createNewFile();
			} catch (IOException e) {
				logger.error(String.format("Create index file:%s failed", ctrlFileName), e);
				throw new RuntimeException(e);
			}
		}
		
		try {
			in = new ObjectInputStream(new FileInputStream(ctrlFile));
			Ctrl ctrl = (Ctrl)in.readObject();
			this.offset = ctrl.offset;
			for(Index index : ctrl.indexs) {
				indexStore.put(index.key, index);
			}
			this.frees.addAll(ctrl.frees);
			ctrl.indexs.clear();
			ctrl.frees.clear();
			ctrl = null;
		} catch (IOException e) {
			logger.error(String.format("Read ctrl file:%s failed", ctrlFileName), e);
		} catch (ClassNotFoundException e) {
			logger.error(String.format("Read ctrl file:%s failed", ctrlFileName), e);
			throw new RuntimeException(e);
		} finally {
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) {}
			}
		}
		
		// init data file
		try {
			this.dataFile = new RandomAccessFile(fileName, "rw");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	private void sync() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(ctrlFile));
			out.writeObject(new Ctrl(offset, indexStore.values(), frees));
			out.flush();
		} catch (IOException e) {
			logger.error(String.format("Wrtie ctr file:%s failed", ctrlFile.getAbsolutePath()), e);
		} finally {
			if(null != out) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws IOException
	 */
	private V readValue(Index index) throws IOException {
		byte[] buffer = new byte[index.length];
		this.dataFile.seek(index.offset);
		this.dataFile.read(buffer);
		return getConverter().fromBytes(buffer);
	}
	
	/**
	 * 
	 * @param offset
	 * @param bytes
	 * @throws IOException
	 */
	private void writeValue(Index index, byte[] bytes) throws IOException {
		if(index.length != bytes.length) {
			throw new IllegalArgumentException(String.format("index.length must equal value(bytes).length, index.length:%d, bytes.length:%d", index.length, bytes.length));
		}
		this.dataFile.seek(index.offset);
		this.dataFile.write(bytes);
	}
	
	/**
	 * 
	 * @param size
	 * @return
	 */
	private Index findFree(int size) {
		
		if(frees.isEmpty()) {
			return null;
		}
		
		Index index = new Index(null, 0, size);
		int offset = Collections.binarySearch(frees, index);
		if(offset >= 0) {
			// 刚好相等
			return frees.remove(offset);
		} else {
			// 
			int i = - offset - 2;
			for(; i >= 0; i--) {
				index = frees.get(i);
				if(index.length < size) {
					break;
				}
			}
			if(i + 1 < frees.size()) {
				index = frees.remove(i + 1);
				insertFree(new Index(null, index.offset + size, index.length - size));
				return new Index(null, index.offset, size);
			}
			return null;
		}
	}
	
	/**
	 * 
	 * @param index
	 */
	private void insertFree(Index index) {
		int offset = Collections.binarySearch(frees, index);
		if(offset >= 0) {
			// 刚好相等
			frees.add(offset + 1, index);
		} else {
			// 
			frees.add(-offset - 1, index);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param size
	 * @return
	 */
	private Index newIndex(K key, int size) {
		int length = INCREMENT_OFFSET - size;
		Index index = new Index(key, offset, size);
		insertFree(new Index(null, offset + size, length));
		offset += INCREMENT_OFFSET;
		return index;
	}
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	private class Index implements Comparable<Index>, Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -7420291791933411561L;

		/**
		 * key
		 */
		K key;
		
		/**
		 * offset in file
		 */
		int offset;
		
		/**
		 * length of value
		 */
		int length;
		
		/**
		 * 
		 * @param key
		 * @param offset
		 * @param length
		 */
		Index(K key, int offset, int length) {
			this.key = key;
			this.offset = offset;
			this.length = length;
		}
		
		@Override
		public int compareTo(Index index) {
			return length - index.length;
		}
	}
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	private class Ctrl implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1838215806180878274L;
		
		int offset;
		Collection<Index> indexs;
		List<Index> frees;
		
		Ctrl(int offset, Collection<Index> indexs, List<Index> frees) {
			this.offset = offset;
			this.indexs = indexs;
			this.frees = frees;
		}
	}
	
	public static void main(String[] args) {
		FileLRUCache cache = new FileLRUCache<String, String>("/tmp/filelrucache.cache");
		cache.initialize();
		cache.register(new ValueConverter<String>() {

			@Override
			public byte[] toBytes(String v) {
				return v.getBytes();
			}

			@Override
			public String fromBytes(byte[] bytes) {
				return new String(bytes);
			}
			
		});
		
		for(int i = 0; i < 10240; i++) {
			cache.put(String.format("key:%d", i), String.format("value:%d", i));
		}
		for(int i = 0; i < 1024; i++) {
			if(!String.format("value:%d", i).equals(cache.get(String.format("key:%d", i)))) {
				throw new RuntimeException("Wrong cache");
			}
		}
		cache.stop();
	}
}
