package sacred.alliance.magic.open.constant;

import sacred.alliance.magic.base.Result;

import com.mofun.open.base.state.StateCode;

public class OpenResult extends Result {
	
	private StateCode stateCode = StateCode.ERROR;
	
	@Override
	public OpenResult success() {
		this.result = Result.SUCCESS;
		this.stateCode = StateCode.SUCCESS;
		return this;
	}
	
	@Override
	public OpenResult failure() {
		this.result = Result.FAIL;
		return this;
	}
	
	@Override
	public OpenResult setInfo(String info) {
		this.info = info;
		return this;
	}
	
	public OpenResult failure(StateCode stateCode){
		this.stateCode = stateCode;
		return this.failure();
	}
	
	public OpenResult failure(StateCode stateCode, String info){
		this.stateCode = stateCode;
		this.info = info;
		return this.failure();
	}

	public StateCode getStateCode() {
		return stateCode;
	}
	
}
