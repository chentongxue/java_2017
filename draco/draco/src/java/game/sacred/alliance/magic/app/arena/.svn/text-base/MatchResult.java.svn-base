package sacred.alliance.magic.app.arena;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.util.Util;

public @Data class MatchResult {

	private List<ArenaMatch> success = new ArrayList<ArenaMatch>();
	private List<ApplyInfo> remain = new ArrayList<ApplyInfo>();
	//避免重复报名/匹配成功
	private Set<Long> infoSet = new HashSet<Long>();
	
	public void destroy(){
		this.destroy(success);
		this.destroy(remain);
		if(null != infoSet){
			this.infoSet.clear();
			this.infoSet = null ;
		}
	}

	private void destroy(List list){
		if(null != list){
			list.clear();
			list = null ;
		}
	}
	
	public boolean containsApplyInfo(long id){
		return this.infoSet.contains(id);
	}
	
	public void addSuccess(ArenaMatch match){
		if(null == match){
			return ;
		}
		long id1 = match.getTeam1().getId();
		long id2 = match.getTeam2().getId();
		if(this.containsApplyInfo(id1) 
				|| this.containsApplyInfo(id2)){
			//取消两者
			match.cancelAll();
			return ;
		}
		this.infoSet.add(id1);
		this.infoSet.add(id2);
		this.success.add(match);
	}
	
	public void addRemain(ApplyInfo info){
		if(null == info){
			return ;
		}
		if(this.containsApplyInfo(info.getId())){
			info.setCancel(true);
			return ;
		}
		this.infoSet.add(info.getId());
		this.remain.add(info);
	}
	
	/**存在成功*/
	private boolean isExistSuccess(){
		return !Util.isEmpty(this.success);
	}
	
	/**存在未匹配*/
	private boolean isExistRemain(){
		return !Util.isEmpty(this.remain);
	}
	
	public void addMatchResult(MatchResult matchResult){
		if(null == matchResult){
			return ;
		}
		if(matchResult.isExistSuccess()){
			//this.success.addAll(matchResult.getSuccess());
			for(ArenaMatch match : matchResult.getSuccess()){
				this.addSuccess(match);
			}
		}
		if(matchResult.isExistRemain()){
			//this.remain.addAll(matchResult.getRemain());
			for(ApplyInfo info : matchResult.getRemain()){
				this.addRemain(info);
			}
		}
	}
	
	
}
