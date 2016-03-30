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
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
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
		String condition = this.condition(role, equipment,doffWearParam.getStorageType());
		if(condition != null){
			return result.setInfo(condition);
		}
		//获得目标容器
		DefaultBackpack pack = GameContext.getUserGoodsApp().getStorage(
				role, doffWearParam.getStorageType());
		if(null == pack || !(pack instanceof BaseEquipBackpack)){
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		
		this.wear(role, roleGoods,equipment,result,(BaseEquipBackpack)pack);
		//脱下的装备
		RoleGoods doffGoods = result.getDoffRoleGoods();
		boolean doffIsNull = (null == doffGoods) ;
		try {
			// 更新装备特效
			if ((doffIsNull && roleGoods.getStarNum() > 0)
					|| (!doffIsNull && doffGoods.getStarNum() != roleGoods
							.getStarNum())) {
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
			
			if (RoleGoodsHelper.hadMosaicGem(roleGoods)
					|| (!doffIsNull && RoleGoodsHelper.hadMosaicGem(doffGoods))) {
				// 镶嵌
				GameContext.getMedalApp().updateMedal(role,
						MedalType.XiangQian, null);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		//套装
		GoodsBase doffTemplage = null ;
		if(!doffIsNull){
			doffTemplage = GameContext.getGoodsApp().getGoodsBase(doffGoods.getGoodsId());
		}
		GameContext.getSuitApp().suitChanged(role, equipment, doffTemplage);
		return result.setResult(DoffWearResult.SUCCESS);
	}
	
	
	
	/**
	 * ====================================================
	 * 1.判断是否为装备
	 * 2.是否同职业
	 * 3.等级是否满足
	 * =====================================================
	 */
	protected String condition(RoleInstance role, GoodsEquipment equipment,StorageType storageType){
		if(equipment == null){
			return GameContext.getI18n().getText(TextId.WEAR_EQUIPMENT_NOT_EQUIPMENT);
		}
		int lvLimit = equipment.getLvLimit();
		if(role.getLevel() < lvLimit){
			return GameContext.getI18n().messageFormat(TextId.WEAR_EQUIPMENT_LEVEL_NOT_MEET, lvLimit);
		}
		//判断容器类型和装备类型是否匹配
		if(null != storageType.getOnGoodsType() && 
				storageType.getOnGoodsType().getType() != equipment.getGoodsType()){
			return GameContext.getI18n().getText(TextId.ERROR_INPUT);
		}
		//英雄需要判断其他条件
		if(StorageType.hero == storageType){
			RoleHero roleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			if(null == roleHero){
				return GameContext.getI18n().getText(TextId.Hero_not_have_on_battle_hero);
			}
			//判断装备位置是否已经开启
			if(!GameContext.getHeroApp().isEquipPosOpenOrFreeOpen(
					role, equipment.getEquipslotType())){
				return GameContext.getI18n().getText(TextId.Hero_equip_pos_not_open);
			}
		}
		return null;
	}
	
	
	
	/** 穿装备操作 */
	private void wear(RoleInstance role, RoleGoods roleGoods, 
			GoodsEquipment equipment, DoffWearResult result,
			BaseEquipBackpack pack){
		
		RoleGoods doffGoods = pack.wear(roleGoods);
		result.setDoffRoleGoods(doffGoods);
		result.setWearRoleGoods(roleGoods);
		
		AttriBuffer doffBuff = AttriBuffer.createAttriBuffer(); 
		AttriBuffer wearBuff = RoleGoodsHelper.getAttriBuffer(roleGoods);
		if(doffGoods != null){
			doffBuff = RoleGoodsHelper.getAttriBuffer(doffGoods);
		}
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
		AttriBuffer newBuff = wearBuff.append(doffBuff.reverse());
		this.singleTrail(role, newBuff);
	}
	
	
	/** 属性计算 */
	private void singleTrail(RoleInstance role, AttriBuffer attriBuff){
		GameContext.getUserAttributeApp().changeAttribute(role, attriBuff);
		role.getBehavior().notifyAttribute();
	}
}
