package sacred.alliance.magic.app.goods.behavior.derive;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.StrengthenParam;
import sacred.alliance.magic.app.goods.behavior.result.StrengthenResult;
import sacred.alliance.magic.app.goods.derive.EquipStrengthenConfig;
import sacred.alliance.magic.app.goods.derive.EquipStrengthenVip;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.GoodsStrengthenType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.medal.MedalType;
import com.game.draco.app.target.cond.TargetCondType;

public class GoodsStrengthen extends AbstractGoodsBehavior {

	public GoodsStrengthen() {
		this.behaviorType = GoodsBehaviorType.Strengthen;
	}

	@Override
	public Result operate(AbstractParam param) {
		StrengthenParam stParam = (StrengthenParam) param;
		RoleInstance role = stParam.getRole();
		RoleGoods equipGoods = stParam.getEquipGoods();
		byte strengthenType = 0 ;//(byte) stParam.getStrengthentype();
		int targetId = stParam.getTargetId() ;

		byte operate = stParam.getOperateType();
		if (operate == StrengthenParam.STRENGTHEN_INFO) {
			return this.strengthenCondition(role, equipGoods);
		} else if (operate == StrengthenParam.STRENGTHEN_EXEC) {
			Result result = this.strengthenExec(role, equipGoods,
					strengthenType,targetId);
			if(result.isIgnore()){
				return result;
			}
			if (result instanceof StrengthenResult) {
				StrengthenResult stResult = (StrengthenResult) result;
				// 强化日志
				GameContext.getStatLogApp().equipStrengLog(role,
						(StrengthenParam) param, stResult);
				if(result.isSuccess()){
					// 重新计算基本属性中的属性,并且通知同步基本属性
					GoodsDeriveSupport.initBaseAttrNotifyGoodsInfo(role, equipGoods, equipGoods.getStorageType());
					GameContext.getTargetApp().updateTarget(role, TargetCondType.HeroEquipStrength);
				}
			}
			return result;
		} 
		StrengthenResult result = new StrengthenResult();
		result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
		return result;
	}

	/**
	 * 强化相关条件
	 * 
	 * @param role
	 * @param equRG
	 * @return
	 */
	private StrengthenResult strengthenCondition(RoleInstance role,
			RoleGoods equRG) {
		StrengthenResult result = StrengthenResult.newFail();
		if (null == role) {
			// 用户不在线
			result.setInfo(GameContext.getI18n()
					.getText(TextId.ROLE_NOT_ONLINE));
			return result;
		}
		// 获得装备模板属性
		GoodsEquipment equipGoods = GoodsDeriveSupport.getGoodsEquipment(equRG
				.getGoodsId());
		if (null == equipGoods) {
			result.setResult(RespTypeStatus.FAILURE);
			result.setInfo(GameContext.getI18n().getText(
					TextId.DERIVE_NO_EQUIPMENT));
			return result;
		}
		// 此物品不可强化
		if (equipGoods.getMaxStrengthenLevel() <= 0) {
			result.setInfo(GameContext.getI18n().getText(
					TextId.GOODS_CANOT_STRENGTHEN));
			return result;
		}
		result.setResult(StrengthenResult.SUCCESS);
		result.setRoleGoods(equRG);
		result.setGoodsTemplate(equipGoods);
		return result;
	}

	/**
	 * 强化操作执行逻辑
	 * 
	 * @param role
	 * @param equipGoods
	 * @param strengthenType
	 * @return
	 */
	private Result strengthenExec(RoleInstance role, RoleGoods equipGoods,
			byte strengthenType,int targetId) {
		StrengthenResult result = this.baseCond(role, equipGoods,
				strengthenType);
		if (!result.isSuccess()) {
			return result;
		}
		// 目标强化等级
		// 获得当前装备强化等级、品质
		RoleGoods equRG = result.getRoleGoods();
		int strengthenNum = equRG.getStrengthenLevel();
		int targetStrengthenNum = strengthenNum + 1;
		EquipStrengthenConfig strengthenObj = GameContext.getGoodsApp()
				.getEquipStrengthenConfig(targetStrengthenNum);
		int money = strengthenObj.getGameMoney();
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, money);
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess()){
			result.setInfo(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GAME_MONEY));
			result.failure();
			return result;
		}
		Map<Integer, Integer> delGoodsMap = new HashMap<Integer, Integer>();
		// 强化材料
		int materialId = strengthenObj.getMaterialId();
		int mustMaterialNum = strengthenObj.getMaterialNum();
		if (materialId > 0 && mustMaterialNum > 0) {
			delGoodsMap.put(materialId, mustMaterialNum);
		}
		// 是否需要保底符
		//int stoneId = strengthenObj.getStoneId();
		//int mustStoneNum = strengthenObj.getStoneNum();
		//GoodsStrengthenType st = result.getStrengthenType();
		/*if (st == GoodsStrengthenType.no_downgrade && stoneId > 0
				&& mustStoneNum > 0) {
			delGoodsMap.put(stoneId, mustStoneNum);// 保底强化，扣保底符
		}*/
		// 快捷购买逻辑
		Result res = GameContext.getQuickBuyApp().doQuickBuy(role, delGoodsMap,
				OutputConsumeType.goods_streng, null);
		//快速购买里亦有消耗判断逻辑
		if (!res.isSuccess()) {
			return res;
		}
		// 扣除钱币
		GoodsDeriveSupport
				.payMoney(role, money, OutputConsumeType.goods_streng);
		// 获得强化结果
		int lvChanged = this.getChangedLevel(role,strengthenObj);
		this.strengthenUpdate(role, result, strengthenObj, lvChanged,targetId);
		return result;
	}
	
	private int getChangedLevel(RoleInstance role ,EquipStrengthenConfig obj){
		if(!RandomUtil.on(obj.getHitProb())){
			return 0 ;
		}
		//获得暴击配置
		/*int vipTimes = GameContext.getVipApp().getVipPrivilegeTimes(role.getRoleId(), 
				VipPrivilegeType.EQUIP_STRENGTHEN_CRIT.getType(), "");
		if(vipTimes <=0){
			return 1 ;
		}*/
		int vipLevel = GameContext.getVipApp().getVipLevel(role);
		EquipStrengthenVip vipConfig = GameContext.getGoodsApp().getEquipStrengthenVip(vipLevel) ;
		if(null == vipConfig){
			return 1 ;
		}
		Integer value = Util.getWeightCalct(vipConfig.getWeightsMap());
		if(null == value || value <1 ){
			return 1 ;
		}
		return value ;
	}

	private void strengthenUpdate(RoleInstance role, StrengthenResult result,
			EquipStrengthenConfig strengthenObj, int lvChanged, int targetId) {
		RoleGoods equRG = result.getRoleGoods();
		int oldLevel = equRG.getStrengthenLevel() ;
		//不能超过用户最大等级
		int maxLevel = GameContext.getAreaServerNotifyApp().getMaxLevel();
		//修正等级变化
		if((oldLevel + lvChanged) > maxLevel){
			lvChanged = maxLevel - oldLevel ;
		}
		
		boolean isChanged = (0 != lvChanged);
		// 强化之后，装备变为绑定
		equRG.setBind(BindingType.already_binding.getType());
		// 是否穿着
		byte bagType = (byte) equRG.getStorageType();
		boolean on = isOnBattleHero(role.getRoleId(),bagType, targetId);

		AttriBuffer oldBuffer = null;
		if (isChanged) {
			// 强化等级发生了变化
			if (on) {
				oldBuffer = RoleGoodsHelper.getAttriBuffer(equRG);
			}
			int newStarNum = oldLevel + lvChanged;
			// 修改装备强化等级
			equRG.setStrengthenLevel((short) Math.max(0, newStarNum));
		}
		result.setStarNumChanged(lvChanged);
		result.setResult(Result.SUCCESS);
		try {

			if (isChanged) {
				// 穿着的装备才需要重新计算属性
				// 需要重新计算属性
				if(on){
					AttriBuffer newBuffer = RoleGoodsHelper.getAttriBuffer(equRG);
					newBuffer.append(oldBuffer.reverse());
					GameContext.getUserAttributeApp().changeAttribute(role,
							newBuffer);
					role.getBehavior().notifyAttribute();
				}
				if (isOnSwitchHero(role.getRoleId(),bagType, targetId)) {
					// 胸章，3英雄都有效果
					GameContext.getMedalApp().updateMedal(role,
							MedalType.QiangHua, equRG);
					//同步其他英雄的战斗力
					//当前出战的英雄已经计算了战斗力
					GameContext.getHeroApp().syncBattleScore(role, targetId,!on);
				}
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
		//广播
		this.broadcast(lvChanged, role, result);
	}


	/**
	 * 世界广播
	 * @param lvChanged
	 * @param role
	 * @param result
	 */
	private void broadcast(int lvChanged, RoleInstance role, StrengthenResult result) {
		try {
			if (lvChanged <= 0) {
				return ;
			}
			for (int i = 0; i < lvChanged; i++) {
				// 遍历强化的等级中有没有世界喊话
				EquipStrengthenConfig broadcastConfig = GameContext.getGoodsApp().getEquipStrengthenConfig(result.getRoleGoods().getStrengthenLevel() - i);
				if (null == broadcastConfig) {
					continue ;
				}
				String broadcastInfo = broadcastConfig.getBroadcastTips(role, result.getRoleGoods());
				if (Util.isEmpty(broadcastInfo)) {
					continue ;
				}
				GameContext.getChatApp().sendSysMessage(ChatSysName.Goods_Strengthen, ChannelType.Publicize_Personal, broadcastInfo, null, null);
				break;
			}
		} catch (Exception ex) {
			logger.error("strengthen broadcast error", ex);
		}
	}

	private StrengthenResult baseCond(RoleInstance role, RoleGoods equipGoods,
			byte strengthenType) {
		GoodsStrengthenType st = GoodsStrengthenType.get(strengthenType);
		if (null == st) {
			StrengthenResult result = StrengthenResult.newFail();
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		StrengthenResult result = this.strengthenCondition(role, equipGoods);
		if (!result.isSuccess()) {
			return result;
		}
		result.setStrengthenType(st);
		result.setResult(Result.FAIL);
		result.setInfo("");

		RoleGoods equRG = result.getRoleGoods();
		GoodsEquipment goodsTemplate = result.getGoodsTemplate();
		// 获得当前装备强化等级、品质
		int strengthenNum = equRG.getStrengthenLevel();
		// 是否可强化到最大等级
		if (strengthenNum >= goodsTemplate.getMaxStrengthenLevel()) {
			result.setInfo(GameContext.getI18n().getText(
					TextId.GOODS_MAX_STRENGTHEN_LV));
			return result;
		}
		//是否符合角色等级控制
		if(strengthenNum > role.getLevel()){
			result.setInfo(GameContext.getI18n().messageFormat(
					TextId.GOODS_STRENGTHEN_ROLE_LV_LOW,String.valueOf(strengthenNum)));
			return result;
		}else if(strengthenNum == role.getLevel()){
			result.setInfo(GameContext.getI18n().messageFormat(
					TextId.GOODS_STRENGTHEN_ROLE_LV_LOW,String.valueOf(strengthenNum + 1)));
			return result;
		}
		result.success();
		return result;
	}

}
