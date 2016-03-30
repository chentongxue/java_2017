package sacred.alliance.magic.app.benefit;

import com.game.draco.message.response.C1903_BenefitPanelRespMessage;

import sacred.alliance.magic.app.hint.HintSupport;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

public interface BenefitApp extends Service, HintSupport {
	
	
	public int getLoginCountDays(RoleInstance role) ;

	/**
	 * 角色登录时设置离线时间
	 * @param role
	 */
	public void initRoleOfflineTime(RoleInstance role);

	/**
	 * 领取离线经验
	 * @param role
	 * @param index
	 * @return
	 */
	public Result takeOfflineExp(RoleInstance role, byte index);
	
	/**
	 * 每日登录领奖
	 * @param role
	 * @return
	 */
	public Result takeLoginCountReward(RoleInstance role);
	
	/**
	 * 获取福利面板的消息
	 * @param role
	 * @return
	 */
	public C1903_BenefitPanelRespMessage getBenefitPanelRespMessage(RoleInstance role);
	
	/**
	 * 登录时弹出福利面板
	 * @param role
	 * @return
	 */
	public C1903_BenefitPanelRespMessage popupBenefitPanel(RoleInstance role);

}
