package sacred.alliance.magic.app.config;

public class HeartBeatConfig extends PropertiesConfig{
	
	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public int getHeartBeat() {
		return Integer.parseInt(getConfig("heartBeat"));
	}
	
	/**加速隔离时间*/
	public int getPermitTime(){
		return Integer.parseInt(getConfig("permitTime"));
	}
	/**加速是否踢人*/
	public boolean getSpeedUpExit(){
		return Boolean.parseBoolean(getConfig("speedUpExit"));
	}
	/**心跳判断消息发送间隔时间*/
	public short getSpeedUpMsgSendTime(){
		return Short.parseShort(getConfig("speedUpMsgSendTime"));
	}
	
	/** 心跳超时入库开关 */
	public boolean getHeartBeatOffline(){
		return Boolean.parseBoolean(getConfig("heartBeatOffline"));
	}
	/**
	 * 偏移量
	 * @return
	 */
	public int getSpeedUpOffsetTime(){
		return Integer.parseInt(getConfig("speedUpOffsetTime"));
	}
	
	/**
	 * 加速几次踢下线
	 * @return
	 */
	public int getSpeedUpMaxCount(){
		return Integer.parseInt(getConfig("speedUpMaxCount"));
	}
	
	public int getSpeedNotUpMinCount(){
		return Integer.parseInt(getConfig("speedNotUpMinCount"));
	}
	
	/**
	 * 是否在心跳中判断加速
	 * @return
	 */
	public boolean isOpenSpeedUpByActiveTest(){
		return Boolean.parseBoolean(getConfig("openSpeedUpByActiveTest"));
	}
	
	/**
	 * 客户端切换到后台进程最长时间(毫秒)[20分钟]
	 * @return
	 */
	public int getClientHomebackMillistime(){
		return Integer.parseInt(getConfig("client.homeback.millistime"));
	}
}
