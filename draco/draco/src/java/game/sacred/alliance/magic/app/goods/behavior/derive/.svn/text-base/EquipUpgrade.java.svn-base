package sacred.alliance.magic.app.goods.behavior.derive;

import java.text.MessageFormat;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.EquipUpgradeParam;
import sacred.alliance.magic.app.goods.behavior.result.EquipUpgradeResult;
import sacred.alliance.magic.app.goods.derive.EquipUpgradeConfig;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class EquipUpgrade extends AbstractGoodsBehavior{

	public EquipUpgrade(){
		this.behaviorType = GoodsBehaviorType.EquipUpgrade ;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		EquipUpgradeParam upgradeParam = (EquipUpgradeParam) param;
		if (EquipUpgradeParam.PARAM_INFO == upgradeParam.getParamType()) {
			// 查看信息
			return this.infoCond(upgradeParam.getBagType(), upgradeParam
					.getGoodsInstanceId(), upgradeParam.getRole());
		}
		if (EquipUpgradeParam.PARAM_EXEC == upgradeParam.getParamType()) {
			// 升级操作
			EquipUpgradeResult result = this.upgradeCond(upgradeParam
					.getBagType(), upgradeParam.getGoodsInstanceId(),
					upgradeParam.getRole());
			if(!result.isSuccess()){
				return result ;
			}
			return this.doUpgrade(upgradeParam.getRole(),result);
		}
		if (EquipUpgradeParam.PARAM_TARGET_INFO == upgradeParam.getParamType()) {
			// 目标信息
			EquipUpgradeResult result = this.infoCond(upgradeParam
					.getBagType(), upgradeParam.getGoodsInstanceId(),
					upgradeParam.getRole());
			if(!result.isSuccess()){
				return result ;
			}
			return this.targetInfo(upgradeParam.getRole(),result);
		}
		return null;
	}
	
	/**
	 * 获得升级目标的信息
	 * @param role
	 * @param result
	 * @return
	 */
	private EquipUpgradeResult targetInfo(RoleInstance role,EquipUpgradeResult result){
		//标识为失败
		result.failure();
		
		RoleGoods equipGoods = result.getEquipGoods();
		GoodsEquipment targetTemplate = result.getTargetEquipment();
		
		//这里一定得clone
		RoleGoods targetRoleGoods = equipGoods.clone();
		//修改模板ID
		targetRoleGoods.setGoodsId(targetTemplate.getId());
		result.setTargetRoleGoods(targetRoleGoods);
		
		result.success();
		return result ;
	}
	
	/**
	 * 升级具体逻辑
	 * @param result
	 * @return
	 */
	private EquipUpgradeResult doUpgrade(RoleInstance role,EquipUpgradeResult result){
		//标识为失败
		result.failure();
		
		RoleGoods equipGoods = result.getEquipGoods();
		GoodsEquipment targetTemplate = result.getTargetEquipment() ;
		
		EquipUpgradeConfig config = result.getEquipUpgradeConfig();
		//删除物品操作
		Result delRs = GameContext.getUserGoodsApp().deleteForBagByMap(role, config.getMaterialMap(), OutputConsumeType.gem_upgrade);
		if(!delRs.isSuccess()){
			result.setInfo(delRs.getInfo());
			return result;
		}
		//扣除钱
		GoodsDeriveSupport.payMoney(role, config.getGameMoney(), OutputConsumeType.gem_upgrade);
		//如果装备穿着的需要重新计算属性
		int bagType = equipGoods.getStorageType() ;
		boolean on = (StorageType.equip.getType() == bagType);
		
		AttriBuffer oldBuffer = null ;
		if(on){
			oldBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
		}
		//修改绑定状态
		equipGoods.setBind(BindingType.already_binding.getType());
		//升级有可能宝石孔会发生变化
		if(targetTemplate.getOpenHoleNum() 
				> result.getEquipTemplate().getOpenHoleNum()){
			RoleGoodsHelper.incrHoles(equipGoods, targetTemplate.getOpenHoleNum());
		}
		//修改模板ID
		equipGoods.setGoodsId(targetTemplate.getId());
		
		try {
			if (on) {
				// 重新计算属性并且通知客户端
				AttriBuffer newBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
				newBuffer.append(oldBuffer.reverse());
				
				boolean onEquipBag = StorageType.equip.getType() == bagType;
				GameContext.getUserAttributeApp().changeAttribute(role,
						newBuffer);
				role.getBehavior().notifyAttribute() ;
				if(onEquipBag){
					//套装
					GameContext.getSuitApp().suitChanged(role, targetTemplate, result.getEquipTemplate());
				}
			}
			// 将最新的装备信息push给客户端
			GoodsDeriveSupport.notifyGoodsInfo(role, equipGoods, equipGoods.getStorageType());
		}catch(Exception ex){
			logger.error("",ex);
		}
		//世界广播
		this.broadcast(role, result);
		result.success();
		return result ;
	}
	
	
	
	private EquipUpgradeResult upgradeCond(byte bagType,String goodsInstanceId,RoleInstance role){
		EquipUpgradeResult result = this.infoCond(bagType, goodsInstanceId, role);
		if(!result.isSuccess()){
			return result ;
		}
		//重新设置为失败
		result.failure();
		boolean on = (StorageType.equip.getType() == bagType);
		
		if(on){
			//如果是装备着的物品需要判断目标物品是否可使用
			GoodsEquipment targetEquipment = result.getTargetEquipment();
			if(role.getLevel() < targetEquipment.getLvLimit() ){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.GOODS_ON_CANOT_UPGRADE_BY_TARGET, 
						String.valueOf(targetEquipment.getLvLimit())));
				return result ;
			}
		}
		
		EquipUpgradeConfig config = result.getEquipUpgradeConfig();
		if(role.getSilverMoney() < config.getGameMoney()){
			//提示用户游戏币不足
			result.setInfo(GameContext.getI18n().getText(TextId.ROLE_NO_MONEY));
			return result ;
		}
		//判断材料是否足够
		Map<Integer,Integer> materialMap = config.getMaterialMap();
		for(Map.Entry<Integer, Integer> entry : materialMap.entrySet()){
			int goodsId = entry.getKey() ;
			int num = entry.getValue() ;
			int roleNum = role.getRoleBackpack().countByGoodsId(goodsId);
			if(roleNum < num){
				//材料数目不够
				result.setInfo(Status.GOODS_NO_ENOUGH.getTips());
				return result ;
			}
		}
		//标识为成功
		result.success();
		return result ;
	}
	
	private EquipUpgradeResult infoCond(byte bagType,String goodsInstanceId,RoleInstance role){
		EquipUpgradeResult result = new EquipUpgradeResult();
		StorageType storageType = StorageType.get(bagType);
		RoleGoods equipGoods = GameContext.getUserGoodsApp()
				.getRoleGoods(role, storageType, goodsInstanceId);
		if (equipGoods == null) {
			result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			return result ;
		}
		GoodsEquipment ge = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, equipGoods.getGoodsId());
		if(null == ge){
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NOT_EQUIPMENT));
			return result;
		}
		GoodsEquipment target = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, ge.getUpgradeId());
		if(null == target){
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_CANOT_UPGRADE));
			return result;
		}
		EquipUpgradeConfig config = GameContext.getGoodsApp().getEquipUpgradeConfig(ge.getLevel(),
				ge.getQualityType(), ge.getEquipslotType());
		if(null == config){
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_CANOT_UPGRADE));
			return result;
		}
		result.success();
		result.setEquipGoods(equipGoods);
		result.setEquipTemplate(ge);
		result.setEquipUpgradeConfig(config);
		result.setTargetEquipment(target);
		return result ;
	}
	
	private void broadcast(RoleInstance role, EquipUpgradeResult result){
		try{
			EquipUpgradeConfig equipUpgradeConfig = result.getEquipUpgradeConfig();
			if(null == equipUpgradeConfig) {
				return;
			}
			String broadcast = equipUpgradeConfig.getBroadcast();
			if(null == broadcast) {
				return;
			}
			GoodsEquipment equipTemplate = result.getEquipTemplate();
			GoodsEquipment targetEquipment = result.getTargetEquipment();
			if(null == equipTemplate || null == targetEquipment) {
				return;
			}
			String equipGoodsContent = Wildcard.getChatGoodsContent(equipTemplate.getId(), ChannelType.Publicize_Personal);
			String targetGoodsContent = Wildcard.getChatGoodsContent(targetEquipment.getId(), ChannelType.Publicize_Personal);
			String message = MessageFormat.format(broadcast,role.getRoleName(),equipGoodsContent,targetGoodsContent);							
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		}catch(Exception e){
			logger.error("EquipUpgrade.broadcast",e);
		}
	}
}
