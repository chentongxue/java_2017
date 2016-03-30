package sacred.alliance.magic.app.goods.behavior.derive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.RecastingParam;
import sacred.alliance.magic.app.goods.behavior.result.RecastingResult;
import sacred.alliance.magic.app.goods.derive.EquipRecatingAttrWeightConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingConfig;
import sacred.alliance.magic.app.goods.derive.EquipRecatingLockConfig;
import sacred.alliance.magic.app.goods.derive.RecatingBoundBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.medal.MedalType;

public class GoodsRecasting extends AbstractGoodsBehavior {
	
	public GoodsRecasting() {
		this.behaviorType = GoodsBehaviorType.Recasting;
	}
	
	@Override
	public Result operate(AbstractParam param) {
		RecastingParam input = (RecastingParam) param;
		RoleInstance role = input.getRole();
		
		RecastingResult result = new RecastingResult();
		this.baseCond(role, input, result);
		if (!result.isSuccess()) {
			return result;
		}
		// 普通洗练
		int operateType = input.getType();
		this.cond(role, input, result, operateType);
		if (!result.isSuccess()||result.isIgnore()) {
			return result;
		}
		//扣除消耗
		int goodsId = result.getDelGoodsId();
		if(goodsId > 0){
			Result goodsRes = GameContext.getUserGoodsApp().deleteForBag(role, goodsId, result.getDelGoodsNum(), OutputConsumeType.goods_recast);
			if(!goodsRes.isSuccess()){
				return goodsRes;
			}
		}
		//随机属性
		this.buildAttrs(result, input, operateType);
		GoodsDeriveSupport.payMoney(role, result.getDelGameMoney(), OutputConsumeType.goods_recast);
		int gold = result.getDelGoldMoney();
		if(gold > 0){
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease, gold, OutputConsumeType.goods_recast);
			role.getBehavior().notifyAttribute();
		}
		//新属性生效
		this.updateRoleAttr(role, result,input.getTargetId());
		return result.success();
	}

	private void baseCond(RoleInstance role, RecastingParam input, RecastingResult result) {
		result.failure();
		RoleGoods equipGoods = input.getEquipGoods();
		GoodsEquipment template = GoodsDeriveSupport.getGoodsEquipment(equipGoods.getGoodsId());
		if (null == template || template.getAttriNum() <= 0) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_RECASTING_NOT_RECASTING));
			return;
		}
		EquipRecatingConfig config = GameContext.getGoodsApp().getEquipRecatingConfig(equipGoods.getQuality(),equipGoods.getStar());
		if (null == config) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_RECASTING_NOT_RECASTING));
			return;
		}
		byte[] lockIndex = input.getLockIndex();
		this.getLockList(equipGoods, lockIndex, result);
		if (!result.isSuccess()) {
			return;
		}
		result.setConfig(config);
		result.setEquipGoods(equipGoods);
		result.setTemplate(template);
		result.success();
	}
	
	private void getLockList(RoleGoods roleGoods, byte[] lockIndex, RecastingResult result) {
		if (null == lockIndex || 0 == lockIndex.length) {
			result.success();
			return;
		}
		ArrayList<AttriItem> attrVarList = roleGoods.getAttrVarList();
		if (Util.isEmpty(attrVarList)) {
			result.failure();
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return;
		}
		List<AttriItem> lockList = new ArrayList<AttriItem>();
		for (byte index : lockIndex) {
			if (index < 0 || index >= attrVarList.size()) {
				result.failure();
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return;
			}
			lockList.add(attrVarList.get(index));
		}
		result.setLockAttriItems(lockList);
		result.success();
	}
	
	private float getLockRatio(int lockNum){
		short value = GameContext.getGoodsApp().getEquipRecatingLockRatio((byte) lockNum);
		float ratio = value/EquipRecatingLockConfig.LOCK_RATIO_BASE_VALUE;
		if(ratio < 1){
			ratio = 1;
		}
		return ratio;
	}

	private void cond(RoleInstance role, RecastingParam input, RecastingResult result, int operateType) {
		result.failure();
		EquipRecatingConfig config = result.getConfig();
		// 判断锁
		int lockNum = 0;
		if (!Util.isEmpty(result.getLockAttriItems())) {
			lockNum = result.getLockAttriItems().size();
		}
		float ratio = this.getLockRatio(lockNum);
		//普通洗练
		if(RecastingParam.RECASTING_TYPE_NORMAL == operateType){
			int delGameMoney = (int) (config.getGameMoney() * ratio);
			result.setDelGameMoney(delGameMoney);
			//判断游戏币是否足够
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, delGameMoney);
			if(ar.isIgnore()){
				result.setIgnore(true);
				return;
			}
			if(!ar.isSuccess()){
				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_RECASTING_MONEY_NOT_ENOUGH));
				return;
			}
//			if(role.getSilverMoney() < delGameMoney){
//				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_RECASTING_MONEY_NOT_ENOUGH));
//				return;
//			}
			int goodsId = config.getMaterial();
			if(goodsId > 0){
				int delGoodsNum = (int) (config.getNum() * ratio);
				result.setDelGoodsNum(config.getMaterial());
				result.setDelGoodsNum(delGoodsNum);
				//判断洗练石是否足够
				int count = role.getRoleBackpack().countByGoodsId(goodsId);
				if(count < delGoodsNum){
					result.setInfo(GameContext.getI18n().getText(TextId.GOODS_RECASTING_MONEY_NOT_ENOUGH));
					return;
				}
			}
			result.success();
			return;
		}
		if(RecastingParam.RECASTING_TYPE_GOLD == operateType){//钻石洗练
			int delGoldMoney = (int) (config.getGoldMoney() * ratio);
			result.setDelGoldMoney(delGoldMoney);
			if(role.getGoldMoney() < delGoldMoney){
				result.setInfo(GameContext.getI18n().getText(TextId.GOODS_RECASTING_MONEY_NOT_ENOUGH));
				return;
			}
			result.success();
			return;
		}
		result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
		result.failure();
	}
	
	private void buildAttrs(RecastingResult result, RecastingParam input, int operateType) {
		int maxAttrNum = result.getTemplate().getAttriNum();
		if(maxAttrNum <= 0){
			return;
		}
 		byte[] lockIndex = input.getLockIndex();
		int lockNum = lockIndex.length;
		int rmAttrNum = maxAttrNum - lockNum;
		if(rmAttrNum <= 0){
			return;
		}
		// 生成属性
		ArrayList<AttriItem> attris = this.randomAttrList(result, input, operateType, rmAttrNum);
		List<AttriItem> exist = result.getLockAttriItems();
		// 将已经存在的属性放入原来的位置
		if (!Util.isEmpty(exist)) {
			int index = 0;
			for (AttriItem item : exist) {
				int pos = lockIndex[index];
				index++;
				if (pos > attris.size()) {
					attris.add(item);
					continue;
				}
				attris.add(pos, item);
			}
		}
		result.setNewAttris(attris);
	}
	
	private ArrayList<AttriItem> randomAttrList(RecastingResult result, RecastingParam input, int operateType, int rmAttrNum){
		ArrayList<AttriItem> attris = new ArrayList<AttriItem>();
		if(rmAttrNum <= 0){
			return attris;
		}
		List<Byte> attrList = this.randomAttrType(result.getLockAttriItems(), rmAttrNum);
		int quality = result.getEquipGoods().getQuality();
		int star = result.getEquipGoods().getStar() ;
		boolean isUseGold = RecastingParam.RECASTING_TYPE_GOLD == operateType;//是否是钻石洗练
		boolean hasMaxQuality = false;//是否有最高品质的属性
		for(byte attrType : attrList){
			EquipRecatingAttrWeightConfig attrWeight = GameContext.getGoodsApp().getEquipRecatingAttrWeightConfig(attrType, quality,star);
			if(null == attrWeight){
				continue;
			}
			RecatingBoundBean bean = attrWeight.randomAttrBound(isUseGold);
			if(null == bean){
				continue;
			}
			attris.add(new AttriItem(attrType, bean.randomValue(), 0));
			if(QualityType.golden.getType() == bean.getQualityType()){
				hasMaxQuality = true;
			}
		}
		//首次&&钻石洗练，必出一个金色属性
		if(isUseGold && !hasMaxQuality){
			//如果是首次洗练，并且是钻石洗练
			if(Util.isEmpty(result.getEquipGoods().getAttrVarList())){
				AttriItem firstItem = attris.get(0);
				byte firstType = firstItem.getAttriTypeValue();
				EquipRecatingAttrWeightConfig attrWeight = GameContext.getGoodsApp().getEquipRecatingAttrWeightConfig(firstType, quality,star);
				if(null != attrWeight){
					RecatingBoundBean firstBean = attrWeight.maxQualityBound();
					if(null != firstBean){
						firstItem.setValue(firstBean.randomValue());
					}
				}
			}
		}
		return attris;
	}
	
	private List<Byte> randomAttrType(List<AttriItem> lockAttriItems, int attrNum){
		List<Byte> attrList = new ArrayList<Byte>();
		if(attrNum <= 0){
			return attrList;
		}
		//已经存在的属性集合，判断属性是否重复
		Set<Byte> existSet = new HashSet<Byte>();
		if(!Util.isEmpty(lockAttriItems)){
			for(AttriItem ai : lockAttriItems){
				if(null == ai){
					continue;
				}
				existSet.add(ai.getAttriTypeValue());
			}
		}
		List<Byte> allAttrList = GameContext.getGoodsApp().getEquipRecatingAttrList();
		int size = allAttrList.size();
		for(int i=1; i<=attrNum;){
			int index = RandomUtil.randomInt(size);
			byte attrType = allAttrList.get(index);
			//属性不能重复
			if(existSet.contains(attrType)){
				continue;
			}
			i++;
			existSet.add(attrType);
			attrList.add(attrType);
		}
		return attrList;
	}

	private void updateRoleAttr(RoleInstance role, RecastingResult result,int targetId) {
		RoleGoods equipGoods = result.getEquipGoods();
		// 随机装备随机到的属性(AttriItem中的value为随机列,而非具体的属性值)
		byte bagType = (byte) equipGoods.getStorageType();
		// 如果是穿着的装备需要计算人身上的属性变化
		boolean on = isOnBattleHero(role.getRoleId(),bagType, targetId);
		AttriBuffer oldBuffer = null;
		if (on) {
			oldBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
		}
		//设置新生成属性
		equipGoods.setAttrVarList(result.getNewAttris());
		// 绑定状态
		equipGoods.setBind(BindingType.already_binding.getType());
		// 重新计算基本属性中的属性,并且通知同步基本属性
		GoodsDeriveSupport.initBaseAttrNotifyGoodsInfo(role, equipGoods, bagType);
		if (on) {
			// 穿着的装备才需要重新计算属性
			AttriBuffer newBuffer = RoleGoodsHelper.getAttriBuffer(equipGoods);
			newBuffer.append(oldBuffer.reverse());
			GameContext.getUserAttributeApp().changeAttribute(role, newBuffer);
			role.getBehavior().notifyAttribute();
			//不再激发
			//boolean onEquipBag = isOnMedal(role.getRoleId(),bagType, targetId);
			//if(onEquipBag){
			//	GameContext.getMedalApp().updateMedal(role, MedalType.XiLian, equipGoods);
			//}
		}
		boolean isOnSwitchHero = isOnSwitchHero(role.getRoleId(), bagType, targetId);
		if (isOnSwitchHero) {
			//同步其他英雄的战斗力
			//当前出战的英雄已经计算了战斗力
			GameContext.getHeroApp().syncBattleScore(role, targetId,!on);
		}
	}

}
