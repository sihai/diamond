package com.galaxy.hsf.network.waverider.config;


/**
 * 
 * 系统配置
 * 
 * @author sihai
 *
 */
public class WaveriderConfig {
	
	// Network
	public static final int  WAVERIDER_DEFAULT_PORT = 8206;								// Master默认监听端口
	public static final long WAVERIDER_DEFAULT_HEART_BEAT_INTERVAL = 60L;			// 默认心跳间隔，单位秒
	public static final long WAVERIDER_DEFAULT_NETWORK_TIME_OUT = 1000L;				// 默认网络超时时间，单位毫秒
	
	// Session
	public static final int  WAVERIDER_DEFAULT_PRE_INIT_SESSION_COUNT = 4;				// Master预初始化Session个数
	public static final int  WAVERIDER_DEFAULT_INCREASE_SESSION_COUNT = 2;				// Master每次新增Session个数
	public static final int  WAVERIDER_DEFAULT_MAX_SESSION_COUNT = 32;					// Master支持的最大Session个数
	public static final long WAVERIDER_DEFAULT_SESSION_RECYCLE_INTERVAL = WAVERIDER_DEFAULT_HEART_BEAT_INTERVAL * 2;	// Master session回收间隔，单位秒
	
	// Slave
	public static final long WAVERIDER_DEFAULT_SLAVE_COMMAND_PRODUCE_INTERVAL = 30L;	// Slave调用CommandProvider产生命令时间间隔, 单位秒

	private Mode mode;

	// Master
	private int port = WAVERIDER_DEFAULT_PORT;						// port
	
	// Session
	private int preInitSessionCount = WAVERIDER_DEFAULT_PRE_INIT_SESSION_COUNT;
	private int increaseSessionCount = WAVERIDER_DEFAULT_INCREASE_SESSION_COUNT;
	private int maxSessionCount = WAVERIDER_DEFAULT_MAX_SESSION_COUNT;
	private long sessionRecycleInterval = WAVERIDER_DEFAULT_SESSION_RECYCLE_INTERVAL;
	
	// Slave
	private String masterAddress;									// Master address
	private boolean isMasterCandidate;								// 
	private Long slaveCommandProduceInterval = WAVERIDER_DEFAULT_SLAVE_COMMAND_PRODUCE_INTERVAL;
	
	// Heartbeat

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMasterAddress() {
		return masterAddress;
	}

	public void setMasterAddress(String masterAddress) {
		this.masterAddress = masterAddress;
	}
	
	public boolean isMasterCandidate() {
		return isMasterCandidate;
	}

	public void setMasterCandidate(boolean isMasterCandidate) {
		this.isMasterCandidate = isMasterCandidate;
	}
	
	public int getPreInitSessionCount()
	{
		return preInitSessionCount;
	}

	public int getIncreaseSessionCount()
	{
		return increaseSessionCount;
	}

	public int getMaxSessionCount()
	{
		return maxSessionCount;
	}

	public long getSessionRecycleInterval()
	{
		return sessionRecycleInterval;
	}

	public void setPreInitSessionCount(int preInitSessionCount)
	{
		this.preInitSessionCount = preInitSessionCount;
	}

	public void setIncreaseSessionCount(int increaseSessionCount)
	{
		this.increaseSessionCount = increaseSessionCount;
	}

	public void setMaxSessionCount(int maxSessionCount)
	{
		this.maxSessionCount = maxSessionCount;
	}

	public void setSessionRecycleInterval(long sessionRecycleInterval)
	{
		this.sessionRecycleInterval = sessionRecycleInterval;
	}
	
	public Long getSlaveCommandProduceInterval() {
		return slaveCommandProduceInterval;
	}

	public void setSlaveCommandProduceInterval(Long slaveCommandProduceInterval) {
		this.slaveCommandProduceInterval = slaveCommandProduceInterval;
	}
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	public static enum Mode {
		MASTER,
		SLAVE;
	}
}
