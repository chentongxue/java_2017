package sacred.alliance.magic.app.active.dps;

import sacred.alliance.magic.base.Result;
import lombok.Data;

public @Data class DpsHurtPoint {
	
	private int hurtPoint;//伤害输出突破值
	private float ratio;//伤害比例系数
	private String broadcast;//广播消息
	
	public Result checkAndInit(){
		Result result = new Result();
		if(this.hurtPoint <= 0){
			return result.setInfo("hurtPoint=" + this.hurtPoint + ",config error!");
		}
		if(this.ratio <= 0){
			return result.setInfo("hurtPoint=" + this.hurtPoint + ",ratio=" + this.ratio + ",ratio config error!");
		}
		return result.success();
	}
	
}
