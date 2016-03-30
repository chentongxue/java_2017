package sacred.alliance.magic.app.arena;

import java.util.Collection;

import lombok.Data;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.vo.AbstractRole;

public  @Data class ArenaResult extends Result{
	private Collection<AbstractRole> roleList = null ;
	//因为匹配超时系统自动取消报名
	//未知原因报名信息一直都存在也无法取消,故有此代码容错
	private boolean cancelByMatchTimeout = false ;
	private ApplyState currentApplyState = ApplyState.not_apply ;
	
	public boolean isSuccess(){
		return SUCCESS == this.getResult() ;
	}
	
	public void releaseRoleList(){
		if(null == roleList){
			return ;
		}
		roleList.clear();
		roleList = null ;
	}
	
}
