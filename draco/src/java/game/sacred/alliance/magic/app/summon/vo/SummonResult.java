package sacred.alliance.magic.app.summon.vo;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
//判断是否可以summon
public class SummonResult extends Result{
	Status status;
	public Status getStatus(){
		return status;
	}
	
	public SummonResult setStatus(Status status){
		this.status = status;
		return this;
	}
	
	public SummonResult setInfo(String info) {
		super.setInfo(info);
		return this;
	}
	
	public SummonResult success(){
		super.success();
		return this;
	}
	
	public SummonResult failure(){
		super.failure();
		return this;
	}
	
	public SummonResult ignore() {
		super.setIgnore(true);
		return this;
	}
}
