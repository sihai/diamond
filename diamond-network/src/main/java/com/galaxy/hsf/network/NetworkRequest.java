/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import com.galaxy.hsf.network.exception.NetworkException;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.network.Packet;

/**
 * 
 * @author sihai
 *
 */
public class NetworkRequest {

	/**
	 * 
	 */
	private String id;
	
	/**
	 * 
	 */
	private Object payload;
	
	/**
	 * 
	 * @param id
	 * @param payload
	 */
	public NetworkRequest(String id, Object payload) {
		this.id = id;
		this.payload = payload;
	}
	
	public String getId() {
		return id;
	}

	public Object getPayload() {
		return payload;
	}

	/**
	 * XXX 目前直接用java的序列化
	 * @return
	 * @throws NetworkException
	 */
	public ByteBuffer marshall() throws NetworkException {
		ByteArrayOutputStream bout = null;
		DeflaterOutputStream dout = null;
		ObjectOutputStream oout = null;
		try {
			bout = new ByteArrayOutputStream();
			dout = new DeflaterOutputStream(bout, new Deflater(Deflater.BEST_SPEED));
			oout = new ObjectOutputStream(dout);
			oout.writeObject(this);
			return ByteBuffer.wrap(bout.toByteArray());
		} catch (IOException e) {
			throw new NetworkException("OOPS, OMG Not possible", e);
		} finally {
			try {
				if (oout != null) {
					oout.close();
				}

				if (dout != null) {
					dout.close();
				}

				if (bout != null) {
					bout.close();
				}
			} catch (IOException ex) {
				throw new NetworkException("OOPS, OMG Not possible", ex);
			}
		}
	}
	
	/**
	 * XXX 目前直接用java的序列化
	 * @param buffer
	 * @return
	 * @throws NetworkException
	 */
	public static NetworkRequest unmarshall(ByteBuffer buffer) throws NetworkException {
		ByteArrayInputStream bin = null;
		InflaterInputStream iin = null;
		ObjectInputStream oin = null;

		try {
			bin = new ByteArrayInputStream(buffer.array(), Packet.getHeaderSize() + Command.getHeaderSize(), buffer.remaining());
			iin = new InflaterInputStream(bin);
			oin = new ObjectInputStream(iin);

			return (NetworkRequest)oin.readObject();
		} catch (IOException e) {
			throw new NetworkException("OOPS, OMG Not possible", e);
		} catch (ClassNotFoundException e) {
			throw new NetworkException("OOPS", e);
		} finally {
			try {
				if (oin != null)
					oin.close();

				if (iin != null)
					iin.close();

				if (bin != null)
					bin.close();

				oin = null;
				iin = null;
				bin = null;
			} catch (IOException ex) {
				throw new NetworkException("OOPS, OMG Not possible", ex);
			}
		}
	}
}
