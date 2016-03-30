package sacred.alliance.magic.app.goods.wing;

import java.util.List;

import com.game.draco.message.response.C0573_GoodsWingGridRespMessage;
import com.game.draco.message.response.C0571_GoodsWingRespMessage;
import com.game.draco.message.response.C0572_GoodsWingUpgradeRespMessage;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

public interface WingApp extends Service{
	/**
	 * 翅膀升级
	 * @param role
	 * @return
	 */
	public C0572_GoodsWingUpgradeRespMessage wingUpgrade(RoleInstance role);
	
	/**
	 * 命格喂养
	 * @param role
	 * @param wingGridId
	 * @return
	 */
	public Result growWingGrid(RoleInstance role, byte wingGridId);
	
	/**
	 * 获取命格属性
	 * @param wingGirdId
	 * @param level
	 * @return
	 */
	public List<AttriItem> getWingGridAttri(byte wingGirdId, int level);
	
	/**
	 * 获取命格配置
	 * @param wingGirdId
	 * @param level
	 * @return
	 */
	public WingGridConfig getWingGrid(byte wingGirdId, int level);
	
	/**
	 * 获取翅膀面板信息
	 * @param role
	 * @return
	 */
	public C0571_GoodsWingRespMessage getGoodsWingInfo(RoleInstance role);
	
	/**
	 * 获取翅膀命格面板信息
	 * @param role
	 * @param wingGridId
	 * @return
	 */
	public C0573_GoodsWingGridRespMessage getGoodsWingGridInfo(RoleInstance role, byte wingGridId);
	
	/**
	 * 加载翅膀
	 * @param role
	 */
	public void loadRoleWing(RoleInstance role);
	
	/**
	 * 初始化翅膀命格
	 * @param roleGoods
	 * @param templateHoles
	 */
	public void initRoleGoodsWingGird(RoleGoods roleGoods, int templateHoles);
	
	/**
	 * 一键培养
	 * @param role
	 * @param wingGridId
	 * @return
	 */
	public Result allGrowWingGrid(RoleInstance role, byte wingGridId);
	
	/**
	 * 获取可用的翅膀
	 * @param role
	 * @return
	 */
	public RoleGoods getWingGoods(RoleInstance role);
}
