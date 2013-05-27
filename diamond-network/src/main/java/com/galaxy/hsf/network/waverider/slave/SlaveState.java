package com.galaxy.hsf.network.waverider.slave;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.waverider.State;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.master.MasterState;
import com.galaxy.hsf.network.waverider.network.Packet;
import com.galaxy.hsf.network.waverider.session.SessionState;

/**
 * 
 * Slave 端统计信息
 * 
 * @author raoqiang
 *
 */
public class SlaveState extends State {
	
	private static final Log logger = LogFactory.getLog(SlaveState.class);
	
	private static final long serialVersionUID = 9106821624690079506L;

	private Boolean isMasterCandidate = false;
	

	public Boolean getIsMasterCandidate() {
		return isMasterCandidate;
	}
	public void setIsMasterCandidate(Boolean isMasterCandidate) {
		this.isMasterCandidate = isMasterCandidate;
	}
	
	public static SlaveState fromByteBuffer(ByteBuffer buffer) {
		ByteArrayInputStream bin = null;
		ObjectInputStream oin = null;
		try {
			bin = new ByteArrayInputStream(buffer.array(), Packet.getHeaderSize() + Command.getHeaderSize(), buffer.remaining());
			oin = new ObjectInputStream(bin);
			return (SlaveState)oin.readObject();
		} catch(IOException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} catch(ClassNotFoundException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} finally {
			if(oin != null) {
				try	{
					oin.close();
				} catch(IOException e){
					logger.error(e);
				}
			}
		}
	}
	
	public ByteBuffer toByteBuffer() {
		ByteArrayOutputStream bout = null;
		ObjectOutputStream oout = null;
		try {
			bout = new ByteArrayOutputStream();
			oout = new ObjectOutputStream(bout);
			oout.writeObject(this);
			oout.flush();
			return ByteBuffer.wrap(bout.toByteArray());
		} catch(IOException e) {
			throw new RuntimeException(e);
		} finally {
			try	{
				if(oout != null) {
					oout.close();
				}
				if(bout != null) {
					bout.close();
				}
			} catch(IOException e){
				logger.error(e);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{id:");
		sb.append(id);
		sb.append(", isMasterCandidate:");
		sb.append(isMasterCandidate);
		sb.append("}");
		
		return sb.toString();
	}
}
