package sacred.alliance.magic.app.hint;

import java.util.Set;
import sacred.alliance.magic.vo.RoleInstance;

public interface HintSupport {
	
	/**
	 * 获取可领取提示特效（同一个APP中可能有多个）
	 * @param role
	 * @return
	 */
	public Set<HintId> getHintIdSet(RoleInstance role);
	
	/**
	 * 提示特效变化
	 * @param role
	 * @param hintId
	 */
	public void hintChange(RoleInstance role, HintId hintId);
	
}
