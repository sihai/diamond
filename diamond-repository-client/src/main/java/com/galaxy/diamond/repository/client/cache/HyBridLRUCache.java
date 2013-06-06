/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.repository.client.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author sihai
 *
 */
public class HyBridLRUCache<K, V> extends AbstractCache<K, V> {

	private static final Log logger = LogFactory.getLog(HyBridLRUCache.class);
	
	private Cache<K, V> memoryCache;
	private Cache<K, V> fileCache;
	
	private final ReadWriteLock _m_rw_lock_;
	private final ReadWriteLock _f_rw_lock_;
	
	/**
	 * 
	 * @param maxEntriesInMemory 	多少条目能驻留内存
	 * @param maxEntries			最多缓存多少条目 (maxEntries = maxEntriesInMemory + maxEntriesInFile)
	 * @param fileName				local file name for file cache
	 */
	public HyBridLRUCache(int maxEntriesInMemory, int maxEntries, String fileName) {
		
		/**
		 * 
		 */
		memoryCache = new MemoryLRUCache<K, V>(maxEntriesInMemory);
		/**
		 * 最终内存中的也会写入File的
		 */
		fileCache = new FileLRUCache<K, V>(maxEntries, fileName);
		memoryCache.register(new ItemEliminateListener<K, V>() {

			@Override
			public void onEliminated(K key, V v) {
				try {
					_f_rw_lock_.writeLock().lock();
					fileCache.put(key, v);
				}  finally {
					_f_rw_lock_.writeLock().unlock();
				}
			}
			
		});
		fileCache.register(new ItemEliminateListener<K, V>() {

			@Override
			public void onEliminated(K key, V v) {
				logger.warn(String.format("Eliminated one from file (local cache), key:%s", key.toString()));
			}
			
		});
		_m_rw_lock_ = new ReentrantReadWriteLock();
		_f_rw_lock_ = new ReentrantReadWriteLock();
	}
	
	@Override
	public void initialize() {
		super.initialize();
		memoryCache.register(getConverter());
		fileCache.register(getConverter());
		((AbstractCache)memoryCache).initialize();
		((AbstractCache)fileCache).initialize();
	}
	
	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		((AbstractCache)memoryCache).stop();
		((AbstractCache)fileCache).stop();
	}

	@Override
	public V get(K key) {
		try {
			// 先查内存
			_m_rw_lock_.readLock().lock();
			V v = memoryCache.get(key);
			if(null == v) {
				return v;
			}
			// 如果内存miss, 查文件
			try {
				_f_rw_lock_.readLock().lock();
				v = fileCache.get(key);
				return v;
			} finally {
				_f_rw_lock_.readLock().unlock();
			}
			
		} finally {
			_m_rw_lock_.readLock().unlock();
		}
	}

	@Override
	public void put(K key, V value) {
		try {
			// 直接写入到memory, 如果memory中被淘汰的, 会被ItemEliminateListener写入到file
			_m_rw_lock_.writeLock().lock();
			memoryCache.put(key, value);
		}  finally {
			_m_rw_lock_.writeLock().unlock();
		}
	}

	@Override
	public void delete(K key) {
		// FIXME 其实只有内存中没有时, 才需要去文件中删除
		try {
			// 先删内存
			_m_rw_lock_.writeLock().lock();
			memoryCache.delete(key);
			// 再删文件中的 
			try {
				_f_rw_lock_.writeLock().lock();
				fileCache.delete(key);
			} finally {
				_f_rw_lock_.writeLock().unlock();
			}
			
		} finally {
			_m_rw_lock_.writeLock().unlock();
		}
	}
}
