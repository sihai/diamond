/**
 * waverider
 * 
 */

package com.galaxy.hsf.network.waverider.master;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.waverider.State;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.network.Packet;
import com.galaxy.hsf.network.waverider.session.SessionState;

/**
 * 
 * Master端统计信息
 * 
 * @author raoqiang
 *
 */
public class MasterState extends State {
	
	private static final Log logger = LogFactory.getLog(MasterState.class);
	
	private static final long serialVersionUID = 7412463722733033955L;
	
	private String 				ip;						//
	private Integer				port;					//
	private List<SessionState>	sessionStateList;		//
	
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	
	public List<SessionState> getSessionStateList() {
		return sessionStateList;
	}
	public void setSessionStateList(List<SessionState> sessionStateList) {
		this.sessionStateList = sessionStateList;
	}
	
	public static MasterState fromByteBuffer(ByteBuffer buffer) {
		ByteArrayInputStream bin = null;
		ObjectInputStream oin = null;
		try {
			bin = new ByteArrayInputStream(buffer.array(), Packet.getHeaderSize() + Command.getHeaderSize(), buffer.remaining());
			oin = new ObjectInputStream(bin);
			return (MasterState)oin.readObject();
		} catch(IOException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} catch(ClassNotFoundException e) {
			logger.error(e);
			throw new RuntimeException(e);
		} finally {
			try	{
				if(oin != null) {
					oin.close();
				}
				if(bin != null) {
					bin.close();
				}
			} catch(IOException e){
				logger.error(e);
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
		sb.append(", ip:");
		sb.append(ip);
		sb.append(", port:");
		sb.append(port);
		sb.append(", sessionStateList:{");
		for(SessionState sessionState : sessionStateList) {
			sb.append("sessionState:");
			sb.append(sessionState);
			sb.append(",");
		}
		if(sb.charAt(sb.length() - 1) == ',') {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("}");
		sb.append("}");
		
		return sb.toString();
	}
}
