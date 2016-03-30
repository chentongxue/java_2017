package sacred.alliance.magic.domain;

import java.util.Date;

import lombok.Data;
import sacred.alliance.magic.base.BroadcastState;
import sacred.alliance.magic.util.DateUtil;

public @Data class SysAnnouncement {
	
	protected int id;
	protected String content;
	protected Date startTime;
	protected Date endTime;
	protected int timeGap;
	protected int state;//3:关闭 2：开启
	protected int index = 0;
	protected byte announceType;//0:GM添加 1：系统添加
	
	public SysAnnouncement(){
		
	}
	
	public SysAnnouncement(String content,Date startTime,Date endTime,
			int timeGap,int state, byte announceType){
		this.content = content;
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeGap = timeGap;
		this.state = state;
		this.announceType = announceType;
	}
	
	/**
	 * 判断是否可发送
	 * @param now
	 * @return
	 */
	public boolean canPublish(Date now){
		if(this.state != BroadcastState.open.getType()){
			return false;
		}
		return canSend(now);
	}
	
	public boolean canSend(Date now){
		if(now.getTime() < this.startTime.getTime()){
			return false ;
		}
		if(now.getTime() > this.endTime.getTime()){
			return false ;
		}
		return this.isGap();
	}
	
	/**间隔时间验证*/
	protected boolean isGap(){
		if(this.index >= Integer.MAX_VALUE){
			this.index = 0;
		}
		if(timeGap != 0 && 0 != this.index % this.timeGap){
			this.index++;
			return false;
		}
		this.index++;
		return true;
	}
	
	/**
	 * 校正初始化信息
	 * @param now
	 */
	public void checkIndex(Date now){
		if(this.startTime.after(now)){
			return;
		}
		long minute = DateUtil.dateDiffMinute(now, this.startTime);
		if(minute > Integer.MAX_VALUE){
			return;
		}
		this.index = (int) minute;
	}
	
}
