package sacred.alliance.magic.app.arena;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;

public class ArenaEmpty extends Arena{
	@Override
	protected ArenaResult applyCheck(RoleInstance role) {
		//提示参数错误
		ArenaResult result = new ArenaResult();
		result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
		return result;
	}
	
	@Override
	public void systemClose(){
		
	}
	
	@Override
	protected ArenaResult applyCancelCheck(RoleInstance role) {
		//提示参数错误
		ArenaResult result = new ArenaResult();
		result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
		return result;
	}


	@Override
	public void systemMatch(){
		
	}


	@Override
	public void setArgs(Object paramObject) {
		
	}

	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		
	}


	@Override
	protected ApplyInfo createApplyInfo(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArenaResult matchNo(String roleId, ApplyInfo info) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArenaResult matchYes(String roleId, ApplyInfo info) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean filterMatch(ApplyInfo info) {
		return true;
	}

	@Override
	public ArenaType getArenaType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArenaResult apply(RoleInstance role) {
		// 判断活动是否开启
		ArenaResult result = new ArenaResult();
		result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		return result;
	}
	

}
