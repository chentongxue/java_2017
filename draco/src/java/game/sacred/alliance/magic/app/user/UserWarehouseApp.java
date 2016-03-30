package sacred.alliance.magic.app.user;

import java.util.List;

import com.game.draco.app.AppSupport;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 仓库
 */
public interface UserWarehouseApp extends AppSupport{
	int BATCH_TAKE_ALL = 1; //全部提取
	int BATCH_TAKE_EQU = 2; //提取装备
	int BATCH_TAKE_CON = 3; //提取消耗

	/** 加载仓库物品 */
	void loadWarehouseGoods(RoleInstance role);
	
	/** 背包物品存入仓库 */
	public GoodsResult put(RoleInstance role, RoleGoods roleGoods, OutputConsumeType ocType);
	public GoodsResult put(RoleInstance role, List<RoleGoods> list, OutputConsumeType ocType);
	
	
	/** 仓库取回物品到背包 */
	public GoodsResult take(RoleInstance role, RoleGoods roleGoods, OutputConsumeType ocType);
	public GoodsResult take(RoleInstance role, List<RoleGoods> list, OutputConsumeType ocType);
	
	/** 仓库批量提取 */
	public GoodsResult batchTake(RoleInstance role, int takeType);
	
	/** 整理仓库 */
	public void reorganization(RoleInstance role,OutputConsumeType ocType);
	
	/**
	 * 离开公会检测没有公会时，清空仓库物品并且发邮件
	 * @param role
	 */
	public void clearWarehouseAndMail(String roleId);
	
	public void sendGoodsByMail(String roleId,List<RoleGoods> allGoods);
}
