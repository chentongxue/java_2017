package sacred.alliance.magic.app.active;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

public interface ActiveSupport {
	
	/**
	 * 活动详情
	 * 注:可根据活动实际需求,增加resp字段,但需要在type方面注明并与客户端联调
	 * */
	public Message getActiveDetail(RoleInstance role, Active active);
	
	/**
	 * 活动即时状态（请直接调用active.getStatus()方法）
	 * 注:各活动需要自己实现此方法
	 * */
	public ActiveStatus getActiveStatus(RoleInstance role, Active active);
	
	/**
	 * 判断活动重置
	 * */
	public void checkReset(RoleInstance role, Active active);
	
	/**
	 * 活动是否过期
	 * @param active
	 * @return
	 */
	public boolean isOutDate(Active active);

	/**
	 * 是否有免费次数（红点提示）
	 * @param role
	 * @param active
	 * @return
	 */
	public boolean getActiveHint(RoleInstance role, Active active);
	
}
