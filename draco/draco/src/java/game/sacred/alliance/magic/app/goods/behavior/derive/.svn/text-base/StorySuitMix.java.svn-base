package sacred.alliance.magic.app.goods.behavior.derive;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.StorySuitMixParam;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.behavior.result.StorySuitMixResult;
import sacred.alliance.magic.app.goods.derive.StorySuitConfig;
import sacred.alliance.magic.app.goods.derive.StorySuitEquipConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class StorySuitMix extends AbstractGoodsBehavior{

	public StorySuitMix(){
		this.behaviorType = GoodsBehaviorType.StorySuitMix;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		StorySuitMixParam suitMixParam = (StorySuitMixParam) param;
		StorySuitMixResult result = this.targetEquipCond(suitMixParam);
		if(!result.isSuccess()){
			return result;
		}
		//兑换装备
		if(StorySuitMixParam.PARAM_EXCHANGE == suitMixParam.getParamType()) {
			return this.doExchange(suitMixParam, result);
		}
		//马上获得
		if(StorySuitMixParam.PARAM_TAKE_NOW == suitMixParam.getParamType()) {
			return this.doTakeNow(suitMixParam, result);
		}
		//查看目标装备信息
		if(StorySuitMixParam.PARAM_TARGET_INFO == suitMixParam.getParamType()) {
			return this.targetInfo(suitMixParam.getRole(), suitMixParam, result);
		}
		return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
	}
	
	private StorySuitMixResult targetEquipCond(StorySuitMixParam suitMixParam){
		StorySuitMixResult result = new StorySuitMixResult();
		short suitGroupId = suitMixParam.getSuitGroupId();
		byte level = suitMixParam.getGoodsLevel();
		byte equipslotType = suitMixParam.getEquipslotType();
		StorySuitConfig suitConfig = GameContext.getGoodsApp().getStorySuitConfig(suitGroupId);
		StorySuitEquipConfig equipConfig = GameContext.getGoodsApp().getStorySuitEquipConfig(suitGroupId, level, equipslotType);
		if(null == suitConfig || null == equipConfig){
			result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
			return result;
		}
		RoleInstance role = suitMixParam.getRole();
		if(role.getLevel() < suitConfig.getRoleLevel()){
			result.setInfo(GameContext.getI18n().getText(TextId.STORY_SUIT_ROLE_LEVEL_NOT_ENOUGH));
			return result;
		}
		int goodsId = equipConfig.getGoodsId();
		GoodsEquipment targetEquipment = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, goodsId);
		if(null == targetEquipment){
			result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			return result;
		}
		result.success();
		result.setTargetEquipment(targetEquipment);
		result.setStorySuitConfig(suitConfig);
		result.setStorySuitEquipConfig(equipConfig);
		return result;
	}
	
	private StorySuitMixResult doExchange(StorySuitMixParam suitMixParam, StorySuitMixResult result){
		result = this.exchangeCond(suitMixParam, result);
		if(!result.isSuccess()){
			return result;
		}
		result.failure();
		RoleInstance role = suitMixParam.getRole();
		StorySuitEquipConfig equipConfig = result.getStorySuitEquipConfig();
		//删除材料
		GoodsResult goodsRes = GameContext.getUserGoodsApp().deleteForBagByMap(role, equipConfig.getMaterialMap(), OutputConsumeType.story_suit_consume);
		if(!goodsRes.isSuccess()){
			result.setInfo(goodsRes.getInfo());
			return result;
		}
		RoleGoods equipGoods = result.getEquipGoods();
		GoodsEquipment targetTemplate = result.getTargetEquipment() ;
		//修改绑定状态
		equipGoods.setBind(BindingType.already_binding.getType());
		//升级有可能宝石孔会发生变化
		if(targetTemplate.getOpenHoleNum() > result.getEquipment().getOpenHoleNum()){
			RoleGoodsHelper.incrHoles(equipGoods, targetTemplate.getOpenHoleNum());
		}
		//修改模板ID
		equipGoods.setGoodsId(targetTemplate.getId());
		//如果装备着，重计算属性
		try{
			int bagType = suitMixParam.getBagType();
			if(StorageType.equip.getType() == bagType){
				AttriBuffer oldBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
				// 重新计算属性并且通知客户端
				AttriBuffer newBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
				newBuffer.append(oldBuffer.reverse());
				boolean onEquipBag = StorageType.equip.getType() == bagType;
				GameContext.getUserAttributeApp().changeAttribute(role,
						newBuffer);
				role.getBehavior().notifyAttribute() ;
				if(onEquipBag){
					//套装
					GameContext.getSuitApp().suitChanged(role, targetTemplate, result.getTargetEquipment());
				}
			}
			// 将最新的装备信息push给客户端
			GoodsDeriveSupport.notifyGoodsInfo(role, equipGoods, equipGoods.getStorageType());
		}catch(Exception ex){
			logger.error(this.getClass().getName() + " doExchange error: ",ex);
		}
		result.success();
		return result;
	}
	
	private StorySuitMixResult exchangeCond(StorySuitMixParam suitMixParam, StorySuitMixResult result){
		result.failure();
		StorageType storageType = StorageType.get(suitMixParam.getBagType());
		RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(suitMixParam.getRole(), storageType, suitMixParam.getGoodsInstanceId());
		if(null == equipGoods){
			result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			return result;
		}
		int goodsId = equipGoods.getGoodsId();
		GoodsEquipment equipment = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, goodsId);
		if(null == equipment){
			result.setInfo(GameContext.getI18n().getText(TextId.NO_GOODS));
			return result;
		}
		StorySuitConfig suitConfig = result.getStorySuitConfig();
		if(suitConfig.getRelySuitGroupId() != equipment.getSuitGroupId()){
			result.setInfo(GameContext.getI18n().getText(TextId.STORY_SUIT_NOT_SAME_GROUP));
			return result;
		}
		if(suitMixParam.getGoodsLevel() != equipment.getLevel()){
			result.setInfo(GameContext.getI18n().getText(TextId.STORY_SUIT_NOT_SAME_LEVEL));
			return result;
		}
		result.success();
		result.setEquipGoods(equipGoods);
		result.setEquipment(equipment);
		return result;
	}
	
	private StorySuitMixResult doTakeNow(StorySuitMixParam suitMixParam, StorySuitMixResult result){
		result.failure();
		RoleInstance role = suitMixParam.getRole();
		StorySuitEquipConfig equipConfig = result.getStorySuitEquipConfig();
		int goldMoney = equipConfig.getGoldMoney();
		//可以通过元宝直接购买
		if(goldMoney > 0){
			if(role.getGoldMoney() < goldMoney){
				result.setInfo(GameContext.getI18n().getText(TextId.STORY_SUIT_GOLD_NOT_ENOUGH));
				return result;
			}
			//添加物品
			GoodsResult goodsRes = GameContext.getUserGoodsApp().addGoodsForBag(role, equipConfig.getGoodsId(), 1, OutputConsumeType.story_suit_exchange);
			if(!goodsRes.isSuccess()){
				result.setInfo(goodsRes.getInfo());
				return result;
			}
			//扣钱
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, goldMoney, OutputConsumeType.story_suit_consume);
			role.getBehavior().notifyAttribute();
			result.success();
			return result;
		}
		//跳转到指定功能界面
		//TODO:
		result.setInfo("This feature is in development ...");
		return result;
	}
	
	/**
	 * 获得升级目标的信息
	 * @param role
	 * @param result
	 * @return
	 */
	private StorySuitMixResult targetInfo(RoleInstance role, StorySuitMixParam suitMixParam, StorySuitMixResult result){
		//标识为失败
		result.failure();
		GoodsEquipment targetTemplate = result.getTargetEquipment();
		StorageType storageType = StorageType.get(suitMixParam.getBagType());
		RoleGoods equipGoods = GameContext.getUserGoodsApp().getRoleGoods(suitMixParam.getRole(), storageType, suitMixParam.getGoodsInstanceId());
		RoleGoods targetRoleGoods = null;
		if(null != equipGoods){
			//这里一定得clone
			targetRoleGoods = equipGoods.clone();
			//修改模板ID
			targetRoleGoods.setGoodsId(targetTemplate.getId());
		}
		//目标的物品信息
		result.setTargetBaseItem(targetTemplate.getGoodsBaseInfo(targetRoleGoods));
		result.success();
		return result ;
	}
	
}
