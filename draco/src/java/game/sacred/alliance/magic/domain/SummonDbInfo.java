package sacred.alliance.magic.domain;

import java.util.Date;

import lombok.Data;

public @Data class SummonDbInfo {
	
	public SummonDbInfo(){
		
	}
	
	public SummonDbInfo(int id, String targetId, byte type, byte times, Date lastExTime, Date expiredTime){
		this.id = id;
		this.targetId = targetId;
		this.times = times;
		this.lastExTime = lastExTime;
		this.expiredTime = expiredTime;
		this.type = type;
	}
	
	private int id;
	private String targetId;
	private byte type;
	private int times; //已兑换次数
	private Date lastExTime;
	private Date expiredTime;
	
	private boolean existRecord;
}
