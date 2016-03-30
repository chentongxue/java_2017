package sacred.alliance.magic.domain;

import java.util.Date;

import lombok.Data;

public @Data class Rate {
	
	private int type;//倍率类型，1：经验
	private Date startTime;//开始时间
	private Date endTime;//结束时间
	private int rate;
	private int rate1;

	public boolean inTime(){
		if(null == this.startTime || null == this.endTime){
			return false ;
		}
		long now = System.currentTimeMillis();
		return now >= this.startTime.getTime() && now<= this.endTime.getTime();
	}
	
}
