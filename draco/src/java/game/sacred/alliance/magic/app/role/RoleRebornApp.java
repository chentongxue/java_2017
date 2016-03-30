package sacred.alliance.magic.app.role;

import java.util.List;

import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RebornPointDetail;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.DeathNotifySelfItem;


public interface RoleRebornApp extends Service{
	
	/**
	 * 玩家复活
	 * @param role
	 * @param rebornType
	 * @param isQuickBuy 是否是快速购买复活道具
	 * @return
	 * @throws ServiceException
	 */
	public Result roleReborn(RoleInstance role, byte rebornType) throws ServiceException;
	
	/**
	 * 技能复活
	 * @param dieRole
	 * @param cureRole
	 * @param cureHp
	 * @param cureMp
	 * @return
	 */
	public Result skillReborn(AbstractRole dieRole,AbstractRole cureRole,int cureHp,int cureMp);
	
	
	public RebornPointDetail getRebornPointDetail(String mapId,AbstractRole role);
	
	/**
	 * 通知角色自身死亡
	 * @param role
	 * @param attacker
	 */
	public void notifySelfDeath(RoleInstance role, AbstractRole attacker);
	
	/**
	 * 通知角色自身死亡
	 * @param role 当前角色
	 * @param attacker 攻击者
	 * @param optionList 过滤后的复活选项
	 */
	public void notifySelfDeath(RoleInstance role, AbstractRole attacker, List<DeathNotifySelfItem> optionList);
	
	/**
	 * 死亡复活选项
	 * @param role
	 * @return
	 */
	public List<DeathNotifySelfItem> getRebornOption(RoleInstance role);
	
	public RebornMode getRebornMode(RebornType type);
	
	/**
	 * 金条复活确认
	 * @param role
	 * @param confirmInfo
	 * @return
	 */
	public Result rebornConfirm(RoleInstance role,String confirmInfo);

	Result reborn(RoleInstance role, RebornMode mode, Point targetPoint);
}
