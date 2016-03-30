package sacred.alliance.magic.app.active.dps;

import lombok.Data;
import sacred.alliance.magic.base.Result;

public @Data class DpsIndex {
	
	private short activeId;//活动ID
	private int roleLevel;//角色等级
	private int minIndex;//名次下限
	private int maxIndex;//名次上限
	private int rewardId;//奖励ID
	
	public Result checkAndInit(){
		Result result = new Result();
		String info = "activeId=" + this.activeId + ",";
		if(this.activeId <= 0){
			return result.setInfo(info + "config error.");
		}
		if(this.roleLevel <= 0){
			return result.setInfo(info + "roleLevel config error.");
		}
		if(this.minIndex <= 0 || this.maxIndex <= 0 || this.minIndex > this.maxIndex){
			return result.setInfo(info + "minIndex or maxIndex config error.");
		}
		return result.success();
	}
	
	/**
	 * 是否在名次之内
	 * @param index
	 * @return
	 */
	public boolean isWithin(int index){
		return index >= this.minIndex && index <= this.maxIndex;
	}
	
}
