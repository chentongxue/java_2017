package sacred.alliance.magic.app.goods.behavior;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.BaseEquipBackpack;
import sacred.alliance.magic.app.goods.DefaultBackpack;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.goods.behavior.param.AbstractParam;
import sacred.alliance.magic.app.goods.behavior.param.DoffWearParam;
import sacred.alliance.magic.app.goods.behavior.result.DoffWearResult;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.equip.config.StarUpgradeFormula;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.medal.MedalType;


/**
 * 穿装备
 * @author Wang.K
 *
 */
public class EquipOn extends AbstractGoodsBehavior{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public EquipOn() {
		this.behaviorType = GoodsBehaviorType.Wear;
	}

	/**
	 * =================================================
	 * 此方法不包括对时装的穿脱
	 * 1.判断是否为装备
	 * 2.判断职业
	 * 3.替换穿戴位置的装备，设置绑定规则
	 * 4.属性重计算
	 * 5.封装返回对象供调用者组装返回消息
	 * =================================================
	 * 
	 */
	@Override
	public DoffWearResult operate(AbstractParam param) {
		
		DoffWearResult result = new DoffWearResult();
		DoffWearParam doffWearParam = (DoffWearParam)param;
		RoleInstance role = doffWearParam.getRole();
		RoleGoods roleGoods = doffWearParam.getDoffWearGoods();
		int goodsId = roleGoods.getGoodsId();
		GoodsEquipment equipment = GameContext.getGoodsApp().getGoodsTemplate(GoodsEquipment.class, goodsId);
		Result condition = this.condition(role, equipment,doffWearParam.getStorageType(),doffWearParam.getTargetId(),roleGoods);
		if(!condition.isSuccess()){
			return result.setInfo(condition.getInfo());
		}
		//获得目标容器
		DefaultBackpack pack = GameContext.getUserGoodsApp().getStorage(
				role, doffWearParam.getStorageType(),doffWearParam.getTargetId());
		if(null == pack || !(pack instanceof BaseEquipBackpack)){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		
		this.wear(role, roleGoods,equipment,result,(BaseEquipBackpack)pack,doffWearParam.getTargetId());
		//不是3出战英雄
		if(!isOnSwitchHero(role.getRoleId(),pack.getStorageType().getType(), doffWearParam.getTargetId())){
			return result.setResult(DoffWearResult.SUCCESS);
		}
		//3出战英雄时更新胸章
		RoleGoods doffGoods = result.getDoffRoleGoods();
		boolean doffIsNull = (null == doffGoods) ;
		try {
			// 更新装备特效
			if ((doffIsNull && roleGoods.getStrengthenLevel() > 0)
					|| (!doffIsNull && doffGoods.getStrengthenLevel() != roleGoods
							.getStrengthenLevel())) {
				// 两装备强化等级不一样
				GameContext.getMedalApp().updateMedal(role,
						MedalType.QiangHua, null);
			}
			// 洗练
			if (!Util.isEmpty(roleGoods.getAttrVarList())
					|| (!doffIsNull && !Util
							.isEmpty(doffGoods.getAttrVarList()))) {
				GameContext.getMedalApp().updateMedal(role,
						MedalType.XiLian, null);
			}
			
			if (RoleGoodsHelper.hadMosaicRune(roleGoods)
					|| (!doffIsNull && RoleGoodsHelper.hadMosaicRune(doffGoods))) {
				// 镶嵌
				GameContext.getMedalApp().updateMedal(role,
						MedalType.XiangQian, null);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		return result.setResult(DoffWearResult.SUCCESS);
	}


	/**
	 * ====================================================
	 * 1.判断是否为装备
	 * 2.是否同职业
	 * 3.等级是否满足
	 * =====================================================
	 */
	protected Result condition(RoleInstance role, GoodsEquipment equipment,
			StorageType storageType,int targetId,RoleGoods roleGoods){
		Result result = new Result() ;
		result.failure() ;
		if(equipment == null){
			result.setInfo(this.getText(TextId.WEAR_EQUIPMENT_NOT_EQUIPMENT));
			return result ;
		}
		int lvLimit = equipment.getLvLimit();
		if(role.getLevel() < lvLimit){
			result.setInfo(this.messageFormat(TextId.WEAR_EQUIPMENT_ROLE_LEVEL_NOT_MEET, lvLimit));
			return result ;
		}
		//判断容器类型和装备类型是否匹配
		if(null != storageType.getOnGoodsType() && 
				storageType.getOnGoodsType().getType() != equipment.getGoodsType()){
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result ;
		}
		//英雄需要判断其他条件
		if(StorageType.hero == storageType){
			RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(role.getRoleId(), targetId);
			if(null == roleHero){
				result.setInfo(this.getText(TextId.ERROR_INPUT));
				return result ;
			}
			int equipslotType = GameContext.getEquipApp().getHeroEquipslotType(roleHero.getHeroId(), roleGoods.getGoodsId());
			if(equipslotType < 0){
				result.setInfo(this.getText(TextId.WEAR_EQUIPMENT_CAN_NOT_WEAR_HERE));
				return result ;
			}
			//判断当前位置是否开启
			if(roleHero.getQuality() < GameContext.getEquipApp().getEquipOpenQuality(equipslotType)){
				result.setInfo(this.getText(TextId.WEAR_EQUIPMENT_HERO_QUALITY_TOO_LOW));
				return result ;
			}
			StarUpgradeFormula formula = GameContext.getEquipApp().getStarUpgradeFormula(
					roleGoods.getGoodsId(), roleGoods.getQuality(), roleGoods.getStar()) ;
			if(null != formula && roleHero.getLevel() < formula.getHeroLevel()){
				result.setInfo(this.messageFormat(TextId.WEAR_EQUIPMENT_HERO_LEVEL_NOT_MEET,
						formula.getHeroLevel()));
				return result ;
			}
		}
		result.success();
		result.setInfo(null);
		return result;
	}
	
	
	
	/** 穿装备操作 */
	private void wear(RoleInstance role, RoleGoods roleGoods, 
			GoodsEquipment equipment, DoffWearResult result,
			BaseEquipBackpack pack,int heroId){
		
		RoleGoods doffGoods = pack.wear(roleGoods);
		result.setDoffRoleGoods(doffGoods);
		result.setWearRoleGoods(roleGoods);
		
		byte bagType = pack.getStorageType().getType() ;
		boolean isOn = isOnBattleHero(role.getRoleId(), bagType, heroId);
		
		AttriBuffer wearBuff = null ;
		if(isOn){
			AttriBuffer doffBuff = AttriBuffer.createAttriBuffer(); 
			wearBuff = RoleGoodsHelper.getAttriBuffer(roleGoods);
			if(doffGoods != null){
				doffBuff = RoleGoodsHelper.getAttriBuffer(doffGoods);
			}
			wearBuff.append(doffBuff.reverse()) ;

		}

        //通知装备配方改变
        GameContext.getEquipApp().onHeroEquipFormulaChanged(role, heroId, roleGoods.getGridPlace());
		
		if(!RoleGoodsHelper.isForever(roleGoods)){
			//限时物品激活时间
			Date endDateTime = roleGoods.getExpiredTime();
	        if(Util.isEmpty(endDateTime)){
	        	Date endDate = DateUtil.add(new Date(), Calendar.MINUTE, roleGoods.getDeadline());
	        	roleGoods.setExpiredTime(endDate);
	        	endDateTime = endDate;
	        }
	        result.setExpiredTime(DateUtil.getMinFormat(endDateTime));
		}
		//重新计算属性，只计算单个装备对玩家属性的影响
		this.changeEquipAttribute(role, wearBuff);
		if (isOnSwitchHero(role.getRoleId(),bagType, heroId)) {
			//同步其他英雄的战斗力
			//当前出战的英雄已经计算了战斗力
			GameContext.getHeroApp().syncBattleScore(role, heroId,!isOn);
		}
	}
	
	
	/** 属性计算 */
	private void changeEquipAttribute(RoleInstance role, AttriBuffer attriBuff){
		if(null == attriBuff || attriBuff.isEmpty()){
			return ;
		}
		GameContext.getUserAttributeApp().changeAttribute(role, attriBuff);
		role.getBehavior().notifyAttribute();
	}
}
