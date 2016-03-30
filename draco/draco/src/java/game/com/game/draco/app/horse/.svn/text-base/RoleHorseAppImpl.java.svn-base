package com.game.draco.app.horse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;

import org.python.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseAdditionProp;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseExp;
import com.game.draco.app.horse.config.HorseRace;
import com.game.draco.app.horse.config.ManshipAdditionProp;
import com.game.draco.app.horse.config.ManshipConsume;
import com.game.draco.app.horse.config.ManshipDes;
import com.game.draco.app.horse.config.ManshipLevelFilter;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.vo.RoleHorseLevelUpResult;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.AddAttriTypeValueItem;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.RoleHorseInfoItem;
import com.game.draco.message.item.RoleHorseListItem;
import com.game.draco.message.item.RoleHorseManshipInfoItem;
import com.game.draco.message.push.C2608_RoleHorseNewRespMessage;
import com.game.draco.message.push.C2609_RoleHorseUpgradeNotifyMessage;
import com.game.draco.message.response.C2601_RoleHorseInfoRespMessage;
import com.game.draco.message.response.C2604_RoleHorseManshipInfoRespMessage;
import com.game.draco.message.response.C2605_RoleHorseUpgradeInfoRespMessage;
import com.google.common.collect.Maps;

public class RoleHorseAppImpl implements RoleHorseApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public final static float TEN_THOUSAD_F = 10000.f;
	/**
	 * 在线角色坐骑列表 key: roleId value: horseMap<坐骑ID，坐骑对象>
	 */
	@Getter
	private Map<Integer, Map<Integer, RoleHorse>> roleHorseMap = Maps.newConcurrentMap();

	@Override
	public Map<Integer, RoleHorse> getAllRoleHorseByRoleId(int roleId) {
		if(roleHorseMap != null && !roleHorseMap.isEmpty()){
			if (roleHorseMap.containsKey(roleId)) {
				return roleHorseMap.get(roleId);
			}
		}
		return null;
	}

	@Override
	public void onJoinGame(RoleInstance role) {
		try{
			// 初始化角色坐骑数据
			List<RoleHorse> horseList = GameContext.getBaseDAO().selectList(
					RoleHorse.class, RoleHorse.ROLE_ID,role.getIntRoleId());
			
			addRoleHorseMap(role,horseList);
			
		}catch(Exception ex){
			logger.error(ex.getMessage());
		}
	}

	@Override
	public void onLeaveGame(int roleId) {
		roleHorseMap.remove(roleId);
	}
	
	/**
	 * 初始化数据
	 */
	private void addRoleHorseMap(RoleInstance role,List<RoleHorse> horseList){
		try{
			if(horseList != null && !horseList.isEmpty()){
				for(RoleHorse roleHorse : horseList){
					//初始化坐骑
					HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
					initSkill(roleHorse,horseData.getSkillId());
					
					SkillLearnFunc learnFunc = GameContext.getUserSkillApp().getSkillLearnFunc(SkillSourceType.Horse);
					learnFunc.innerAddSkill(role,String.valueOf(roleHorse.getHorseId()),horseData.getSkillId(),roleHorse.getLevel());
					
					if(roleHorseMap.containsKey(roleHorse.getRoleId())){
						Map<Integer, RoleHorse> map = roleHorseMap.get(roleHorse.getRoleId());
						map.put(roleHorse.getHorseId(), roleHorse);
					}else{
						Map<Integer, RoleHorse> map = Maps.newConcurrentMap();
						map.put(roleHorse.getHorseId(),roleHorse);
						roleHorseMap.put(roleHorse.getRoleId(),map);
					}
				}
			}
		}catch(Exception e){
			logger.error("addRoleHorseMap is error",e);
		}
	}
	
	@Override
	public boolean addRoleHorseExp(RoleInstance role,RoleHorse roleHorse,int exp){
		boolean flag = false;
		try{
			if(roleHorse != null){
				HorseExp horseExp = GameContext.getHorseApp().getHorseExpByLevelQuality(String.valueOf(roleHorse.getLevel() + Cat.underline + roleHorse.getQuality()));
				//判断坐骑经验是否高出当前等级经验
				if((roleHorse.getExp() + exp) >= horseExp.getExp()){
					//获取当前坐骑数据
					HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
					//坐骑等级+1
					roleHorse.setLevel((byte)(roleHorse.getLevel() + 1));
					//初始化坐骑新等级经验
					if(roleHorse.getLevel() >= horseData.getMaxLevel()){
						roleHorse.setExp(0);
						roleHorse.setLevelUpHorseNum(0);
					}else{
						roleHorse.setExp((roleHorse.getExp() + exp)  - horseExp.getExp());
						roleHorse.setLevelUpHorseNum((roleHorse.getExp() + exp)  - horseExp.getExp());
					}
					
					//改变属性逻辑
					updRoleHorseProp(role,roleHorse);
					//发送初始化消息
					role.getBehavior().sendMessage(sendC2601_RoleHorseInfoRespMessage(role,roleHorse.getHorseId()));
					
					SkillLearnFunc learnFunc = GameContext.getUserSkillApp().getSkillLearnFunc(SkillSourceType.Horse);
					
					learnFunc.learnSkill(role, horseData.getSkillId(),String.valueOf(roleHorse.getHorseId()));
					flag = true;
				}else{
					roleHorse.setExp(roleHorse.getExp() + exp);
				}
				saveOrUpdRoleHorse(roleHorse);
			}
		}catch(Exception e){
			logger.error("addRoleHorseExp is error",e);
		}
		return flag;
	}
	
	@Override
	public RoleHorseLevelUpResult levelUp(RoleInstance role, int horseId){
		RoleHorseLevelUpResult result = new RoleHorseLevelUpResult();
		try{
			//获得角色坐骑
			RoleHorse roleHorse = getRoleHorse(role.getIntRoleId(),horseId);
			//获得坐骑基础数据
			HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(horseId);
			//坐骑等级是否大于等于自身等级
			if(roleHorse.getLevel() >= horseData.getMaxLevel()){
				result.setInfo(GameContext.getI18n().getText(TextId.HORSE_ERR_MAX_LEVEL));
				return result ;
			}
			
			//获取下一级坐骑数据
			HorseExp horseExp = GameContext.getHorseApp().getHorseExpByLevelQuality(String.valueOf((roleHorse.getLevel()+1) + Cat.underline + roleHorse.getQuality()));
			
			//判断背包里是否有该物品
			boolean flag = GameContext.getUserGoodsApp().isExistGoodsForBag(role, horseExp.getGoodsId());
			if(!flag){
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(horseExp.getGoodsId());
				String goodsName = Wildcard.getQualityGoodsName(goodsBase);
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_ERR_NO_GOODS,goodsName));
				return result ;
			}
			
			//获得物品数量
			int goodsNum = role.getRoleBackpack().countByGoodsId(horseExp.getGoodsId());
			
			if(goodsNum >= horseExp.getExp()){
				goodsNum = horseExp.getExp();
			}else{
				//记录消耗数
				roleHorse.setLevelUpHorseNum(roleHorse.getLevelUpHorseNum() + goodsNum);
			}
			
		    //扣除物品
			GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, horseExp.getGoodsId(),goodsNum,
					OutputConsumeType.horse_level_consume);
			if(!gr.isSuccess()){
				result.setInfo(gr.getInfo());
				return result ;
			}
			
			//先拿物品数量当经验计算
			boolean isLevelUp = addRoleHorseExp(role,roleHorse,goodsNum);
			if(!isLevelUp){
				result.setHorseId(horseId);
				result.setBattleScore(getBattleScore(roleHorse,false));
				result.setExp(roleHorse.getExp());
				result.success();
			}else{
				result.setLevelUp(true);
			}
			return result ;
		}catch(Exception ex){
			logger.error("levelUp is error " ,ex);
		}
		return result ;
		
	}
	
	/**
	 * 更新坐骑属性 (按等级计算)
	 * @param role
	 * @param roleHorse
	 */
	private void updRoleHorseProp(RoleInstance role,RoleHorse roleHorse){
		try{
			//重置坐骑属性
			//当前属性
			int newLevel = roleHorse.getLevel();
			//上一级属性
			int preLevel = newLevel -1;
			
			HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
			
			//去掉上一级加成属性
			HorseAdditionProp horseAdditionProp = GameContext.getHorseApp().getHorseAdditionPropById(roleHorse.getHorseId() + Cat.underline + preLevel);
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			additionBuffer.append(horseBase.getAttriItemList());
			additionBuffer.append(horseAdditionProp.getAttriItemList());
			additionBuffer.reverse();
			
			//加上本级加成属性
			HorseAdditionProp newHorseAdditionProp = GameContext.getHorseApp().getHorseAdditionPropById(roleHorse.getHorseId() + Cat.underline + newLevel);
			additionBuffer.append(horseBase.getAttriItemList());
			additionBuffer.append(newHorseAdditionProp.getAttriItemList());
			// 修改角色属性值
			GameContext.getUserAttributeApp().changeAttribute(role, additionBuffer);
			role.getBehavior().notifyAttribute();
			
		}catch(Exception ex){
			logger.error("updRoleHorseProp is error " ,ex);
		}
	}
	
	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		Map<Integer, RoleHorse> allMap = getAllRoleHorseByRoleId(role.getIntRoleId());
		RoleHorse roleHorse = null;
		if(allMap != null && !allMap.isEmpty()){
			for(Entry<Integer,RoleHorse> horse : allMap.entrySet()){
				buffer.append(getRoleHorseAllProp(horse.getKey(),horse.getValue().getLevel(),horse.getValue().getManshipLevel()));
				if(horse.getValue().getState() == (byte)1){
					roleHorse = horse.getValue();
				}
			}
			if(roleHorse != null){
				HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
				if(horseBase != null){
					buffer.append(AttributeType.speed.getType(), horseBase.getMoveSpeed(),false);
				}
			}
		}
		return buffer;
	}
	
	/**
	 * 坐骑升品减属性
	 * @param role
	 * @param roleHorse
	 */
	private void cutRoleHorseProp(RoleInstance role,RoleHorse roleHorse){
		try{
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			additionBuffer.append(getRoleHorseAllProp(roleHorse.getHorseId(),roleHorse.getLevel(),roleHorse.getManshipLevel()));
			additionBuffer.reverse();
		}catch(Exception e){
			logger.error("cutRoleHorseProp is error",e);
		}
	}
	
	/**
	 * 坐骑升品加属性
	 * @param role
	 * @param roleHorse
	 */
	private void addRoleHorseProp(RoleInstance role,RoleHorse roleHorse){
		try{
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			additionBuffer.append(getRoleHorseAllProp(roleHorse.getHorseId(),roleHorse.getLevel(),roleHorse.getManshipLevel()));
			// 修改角色属性值
			GameContext.getUserAttributeApp().changeAttribute(role, additionBuffer);
			role.getBehavior().notifyAttribute();
		}catch(Exception e){
			logger.error("addRoleHorseProp is error",e);
		}
	}
	
	/**
	 * 更新坐骑骑术属性
	 * @param role
	 * @param roleHorse
	 */
	private void updRoleHorseManshipProp(RoleInstance role,RoleHorse roleHorse){
		try{
			//当前属性
			int newLevel = roleHorse.getManshipLevel();
			//上一级属性
			int preLevel = newLevel -1;
			
			modifyRoleHorseManshipProp(role,roleHorse.getHorseId(),preLevel,newLevel);
			
			saveOrUpdRoleHorse(roleHorse);
		}catch(Exception e){
			logger.error("updRoleHorseManshipProp is error",e);
		}
	}
	
	/**
	 * 更新坐骑骑术属性对人的影响
	 * @param role
	 * @param preLevel
	 * @param newLevel
	 */
	private void modifyRoleHorseManshipProp(RoleInstance role,int horseId,int preLevel,int newLevel){
		try{
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			RoleHorse roleHorse = getRoleHorse(role.getIntRoleId(), horseId);
			additionBuffer.append(getRoleHorseAllProp(horseId,roleHorse.getLevel(),preLevel));
			additionBuffer.reverse();
			
			AttriBuffer newAdditionBuffer = AttriBuffer.createAttriBuffer();
			//加上本级加成属性
			newAdditionBuffer.append(getRoleHorseAllProp(horseId,roleHorse.getLevel(),newLevel));
			// 修改角色属性值
			GameContext.getUserAttributeApp().changeAttribute(role, newAdditionBuffer);
			role.getBehavior().notifyAttribute();
			
		}catch(Exception e){
			logger.error("modifyRoleHorseManshipProp is error",e);
		}
	}
	
	/**
	 * 获得坐骑属性
	 */
	private List<AttriItem> getRoleHorseAllProp(int horseId,int level,int rideLevel){
		try{
			HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(horseId);
			HorseAdditionProp horseAdditionProp = GameContext.getHorseApp().getHorseAdditionPropById(horseId + Cat.underline + level);
			ManshipAdditionProp manshipHorseAdditionProp = GameContext.getHorseApp().getManshipAdditionPropByLevel(rideLevel);
			
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			additionBuffer.append(horseData.getAttriItemList());
			if(horseAdditionProp != null){
				additionBuffer.append(horseAdditionProp.getAttriItemList());
			}
			if(manshipHorseAdditionProp != null){
				float rate = manshipHorseAdditionProp.getAdditionProp() / TEN_THOUSAD_F;
				if(rate > 0){
					additionBuffer.rate(rate);
					//需要再加一次原始值
					additionBuffer.append(horseData.getAttriItemList());
					additionBuffer.append(horseAdditionProp.getAttriItemList());
				}
			}
			
			
			Map<Byte,AttriItem> attriItemMap = additionBuffer.getMap();
			List<AttriItem> list = Lists.newArrayList();
			if(attriItemMap != null && !attriItemMap.isEmpty()){
				for(Entry<Byte,AttriItem> item : attriItemMap.entrySet()){
					list.add(item.getValue());
				}
			}
			return list;
		}catch(Exception e){
			logger.error("getRoleHorseAllProp is error",e);
			return null;
		}
	}
	
	@Override
	public int getBattleScore(RoleHorse roleHorse,boolean flag) {
		try{
			HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
			int horseId = roleHorse.getHorseId();
			if(flag){
				horseId = horseBase.getNextHorseId();
			}
			HorseAdditionProp hrseAdditionProp = GameContext.getHorseApp().getHorseAdditionPropById(horseId + Cat.underline + roleHorse.getLevel());
			
			ManshipAdditionProp manshipAdditionProp = GameContext.getHorseApp().getManshipAdditionPropByLevel(roleHorse.getManshipLevel());
			
			List<AttriItem> baseItems = horseBase.getAttriItemList();
			
			List<AttriItem> additionItems = hrseAdditionProp.getAttriItemList();

			//基础战力
			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			buffer.append(baseItems);
			buffer.append(additionItems);
			float rate = manshipAdditionProp.getAdditionProp() / TEN_THOUSAD_F;
			if(rate > 0){
				buffer.rate(rate);
				buffer.append(baseItems);
				buffer.append(additionItems);
			}
			return GameContext.getAttriApp().getAttriBattleScore(buffer);
		}catch(Exception e){
			logger.error("modifyRoleHorseManshipProp is error",e);
			return 0;
		}
	}
	
	@Override
	public boolean isUpgrade(int roleId, int horseId) {
		RoleHorse roleHorse = getRoleHorse(roleId,horseId);
		//最大品质
		HorseUpgradeType [] types = HorseUpgradeType.values();
		if(roleHorse.getQuality() >= types.length-1){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isLevelUp(int roleId, int horseId) {
		RoleHorse roleHorse = getRoleHorse(roleId,horseId);
		if(roleHorse != null){
			if(roleHorse.getState() == HorseConstants.HORSE_STATE_ON){
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 坐骑更新
	 */
	@Override
	public void saveOrUpdRoleHorse(RoleHorse roleHorse){
		try{
			if(roleHorseMap.containsKey(roleHorse.getRoleId())){
				Map<Integer, RoleHorse> map = roleHorseMap.get(roleHorse.getRoleId());
				map.put(roleHorse.getHorseId(), roleHorse);
			}else{
				Map<Integer, RoleHorse> map = Maps.newConcurrentMap();
				map.put(roleHorse.getHorseId(),roleHorse);
				roleHorseMap.put(roleHorse.getRoleId(),map);
			}
			//数据库
			GameContext.getBaseDAO().saveOrUpdate(roleHorse);
		}catch(Exception e){
			logger.error("saveOrUpdRoleHorse is error",e);
		}
	}
	
	@Override
	public RoleHorse getRoleHorse(int roleId, int horseId) {
		if(roleHorseMap.containsKey(roleId)){
			Map<Integer, RoleHorse> horseMap = roleHorseMap.get(roleId);
			if(horseMap != null && horseMap.containsKey(horseId)){
				RoleHorse roleHorse = horseMap.get(horseId);
				return roleHorse;
			}
		}
		return null;
	}

	@Override
	public RoleHorseLevelUpResult manshipLevelUp(RoleInstance role, int horseId) {
		RoleHorseLevelUpResult result = new RoleHorseLevelUpResult();
		try{
			//获得角色坐骑
			RoleHorse roleHorse = getRoleHorse(role.getIntRoleId(),horseId);
			
			ManshipConsume manshipConsume = GameContext.getHorseApp().getManshipConsumeByLevel(roleHorse.getManshipLevel());
			
			GoodsBase goodsBase= GameContext.getGoodsApp().getGoodsBase(manshipConsume.getGoodsId());
			String goodsName = Wildcard.getQualityGoodsName(goodsBase);
			if( manshipConsume.getGoodsId() > 0){
				//判断背包里是否有该物品
				boolean flag = GameContext.getUserGoodsApp().isExistGoodsForBag(role, manshipConsume.getGoodsId());
				if(!flag){
					result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_ERR_NO_GOODS,goodsName));
					return result ;
				}
			}
	
			
			//获得物品数量
			int count = role.getRoleBackpack().countByGoodsId(manshipConsume.getGoodsId());
			if(count < manshipConsume.getGoodsNum()){
				//提示物品不足
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_MANSHIP_LEVELUP_CONSUME_GOODS, manshipConsume.getGoodsNum(),goodsName));
				return result ;
			}
			
			int silverMoney = manshipConsume.getGoldMoney();
			if(silverMoney > 0) {
				if(role.getSilverMoney() < silverMoney) {
					//提示游戏币不足
					result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_MANSHIP_LEVELUP_CONSUME_MONEY, silverMoney));
					return result ;
				}
			}
			
			int zp = manshipConsume.getZp();
			if(zp > 0) {
				if(role.getPotential() < zp) {
					//提示潜能不足
					result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_MANSHIP_LEVELUP_CONSUME_ZP, zp));
					return result ;
				}
			}
			
			if( manshipConsume.getGoodsId() > 0){
				//扣除物品
				GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, manshipConsume.getGoodsId(),manshipConsume.getGoodsNum(),
						OutputConsumeType.horse_manship_level_consume_goods);
				if(!gr.isSuccess()){
					result.setInfo(gr.getInfo());
					return result ;
				}
			}
			//扣除消耗
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, 
					OperatorType.Decrease, silverMoney, OutputConsumeType.horse_manship_level_consume_money);
		
			//扣除消耗
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential, 
					OperatorType.Decrease, zp, OutputConsumeType.horse_manship_level_consume_zp);
			
			roleHorse.setManshipLevel((short)(roleHorse.getManshipLevel() + 1));
			updRoleHorseManshipProp(role,roleHorse);
			
			role.getBehavior().sendMessage(sendC2604_RoleHorseManshipInfoRespMessage(role.getIntRoleId(), horseId));
			
			result.setHorseId(horseId);
			result.setBattleScore(getBattleScore(roleHorse,false));
			result.setExp(roleHorse.getManshipLevel());
			result.success();
			return result ;
		}catch(Exception e){
			logger.error("manshipLevelUp is error",e);
			result.setInfo("manshipLevelUp is error");
			return result ;
		}
	}

	@Override
	public C2601_RoleHorseInfoRespMessage sendC2601_RoleHorseInfoRespMessage(RoleInstance role,int horseId) {
		try{
			C2601_RoleHorseInfoRespMessage respMsg = new C2601_RoleHorseInfoRespMessage();
	
			HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseId);
	
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(), horseId);
			
			HorseExp horseExp = GameContext.getHorseApp().getHorseExpByLevelQuality(String.valueOf(roleHorse.getLevel() + Cat.underline + roleHorse.getQuality()));
			
			RoleHorseInfoItem roleHorseInfoItem = new RoleHorseInfoItem();
			
			//获取技能数据
			Skill skill = GameContext.getSkillApp().getSkill(horseBase.getSkillId());
			
			//属性数据
			List<AddAttriTypeValueItem> horsePropItemList = new ArrayList<AddAttriTypeValueItem>();
			
			//消耗物品数据
			List<GoodsLiteNamedItem> goodsList = new ArrayList<GoodsLiteNamedItem>();
			
			roleHorseInfoItem.setHorseId(roleHorse.getHorseId());
			roleHorseInfoItem.setHorseLevel((byte)roleHorse.getLevel());
			roleHorseInfoItem.setExp(roleHorse.getExp());
			roleHorseInfoItem.setMaxExp(horseExp.getExp());
			roleHorseInfoItem.setMaxLevel((byte)horseBase.getMaxLevel());
			roleHorseInfoItem.setMoveSpeed(horseBase.getMoveSpeed());
			roleHorseInfoItem.setQuality(roleHorse.getQuality());
			roleHorseInfoItem.setSkillId(horseBase.getSkillId());
			if(skill != null){
				roleHorseInfoItem.setSkillImageId(skill.getIconId());
				roleHorseInfoItem.setSkillName(skill.getName());
				RoleSkillStat horseSkill = roleHorse.getSkillMap().get(horseBase.getSkillId());
				if(horseSkill != null){
					roleHorseInfoItem.setSkillDes(skill.getSkillDetail(horseSkill.getSkillLevel()).getDesc());
				}
			}
			
			List<AttriItem> attriItemItemList = getRoleHorseAllProp(horseId,roleHorse.getLevel(),roleHorse.getManshipLevel());
			
			for(AttriItem item : attriItemItemList){
				AddAttriTypeValueItem propItem = new AddAttriTypeValueItem();
				propItem.setAttriType(item.getAttriTypeValue());
				propItem.setAttriValue((int)item.getValue());
				horsePropItemList.add(propItem);
			}
			
			if(roleHorse.getLevel()+1 <= horseBase.getMaxLevel()){
				//下一级附加值
				List<AttriItem> addAttriItemItemList = getRoleHorseAllProp(horseId,roleHorse.getLevel()+1,roleHorse.getManshipLevel());
				for(AttriItem addAttItem : addAttriItemItemList){
					for(AddAttriTypeValueItem value : horsePropItemList ){
						if(value.getAttriType() == addAttItem.getAttriTypeValue()){
							value.setAddAttriValue((int)addAttItem.getValue() - value.getAttriValue());
							break;
						}
					}
				}
			}

			int battleScore = GameContext.getRoleHorseApp().getBattleScore(roleHorse,false);
			roleHorseInfoItem.setBattleScore(battleScore);
			roleHorseInfoItem.setHorsePropItem(horsePropItemList);
			GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(horseExp.getGoodsId());
			if(goodsBase != null){
				goodsItem.setGoodsId(horseExp.getGoodsId());
				goodsItem.setNum((short)horseExp.getExp());
				goodsItem.setGoodsImageId(goodsBase.getImageId());
				goodsItem.setGoodsLevel((byte)goodsBase.getLevel());
				goodsItem.setBindType(goodsBase.getBindType());
				goodsItem.setGoodsName(goodsBase.getName());
				goodsItem.setQualityType(goodsBase.getQualityType());
			}
			goodsList.add(goodsItem);
			roleHorseInfoItem.setGoodsList(goodsList);
			
			respMsg.setHorseInfo(roleHorseInfoItem);
			
			return respMsg;
		}catch(Exception e){
			logger.error("sendC2601_RoleHorseInfoRespMessage is error",e);
			return null ;
		}
	}

	@Override
	public C2604_RoleHorseManshipInfoRespMessage sendC2604_RoleHorseManshipInfoRespMessage(
			int roleId, int horseId) {
		
		try{
			C2604_RoleHorseManshipInfoRespMessage respMsg = new C2604_RoleHorseManshipInfoRespMessage();
			
			HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseId);
			
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(roleId, horseId);
			
			RoleHorseManshipInfoItem roleHorseManshipInfoItem = new RoleHorseManshipInfoItem();
			
			//消耗物品数据
			List<GoodsLiteNamedItem> goodsList = new ArrayList<GoodsLiteNamedItem>();
			
			short maxManshipLevel = 0;
			String typeName = "";
			//获取骑术数据
			List<ManshipDes> manshipDesList = GameContext.getHorseApp().getManshipDesList();
			byte manshipType = 1;
			if(manshipDesList != null && !manshipDesList.isEmpty()){
				int i = 0;
				for(ManshipDes manship : manshipDesList){
					i++;
					if(roleHorse.getManshipLevel() < manship.getMaxLevel()){
						maxManshipLevel = manship.getMaxLevel();
						typeName = manship.getDes();
						break;
					}
					if(i == manshipDesList.size()){
						maxManshipLevel = manship.getMaxLevel();
						typeName = manship.getDes();
					}
					manshipType++;
				}
			
			}
	
			//金币消耗
			List<AttriTypeValueItem> moneyList = new ArrayList<AttriTypeValueItem>();
			ManshipConsume manshipConsume = GameContext.getHorseApp().getManshipConsumeByLevel(roleHorse.getManshipLevel());
			
			if(manshipConsume != null){
				AttriTypeValueItem moneyItem = new AttriTypeValueItem();
				moneyItem.setAttriType(AttributeType.silverMoney.getType());
				moneyItem.setAttriValue(manshipConsume.getGoldMoney());
				moneyList.add(moneyItem);
				AttriTypeValueItem zpItem = new AttriTypeValueItem();
				zpItem.setAttriType(AttributeType.potential.getType());
				zpItem.setAttriValue(manshipConsume.getZp());
				moneyList.add(zpItem);
			}
			
			roleHorseManshipInfoItem.setManshipType(manshipType);
			roleHorseManshipInfoItem.setMoneyList(moneyList);
			
			roleHorseManshipInfoItem.setHorseId(roleHorse.getHorseId());
			
			roleHorseManshipInfoItem.setLevel(roleHorse.getManshipLevel());
			
			roleHorseManshipInfoItem.setMaxLevel(maxManshipLevel);
			HorseRace horseRace = GameContext.getHorseApp().getHorseRaceByType(horseBase.getRace());
			//种族名称
			roleHorseManshipInfoItem.setRace(horseRace.getRaceName());
			//骑术等级
			roleHorseManshipInfoItem.setTypeName(typeName);
			//骑术名称
			roleHorseManshipInfoItem.setName(horseRace.getManshipName());
			
			//骑术等级限制
			ManshipLevelFilter filter = GameContext.getHorseApp().getManshipLevelFilterByType(horseBase.getRace() + Cat.underline + (roleHorse.getManshipLevel()+1));
			if(filter != null){
				roleHorseManshipInfoItem.setLimitRoleLevel(filter.getRoleLevel());
			}
			roleHorseManshipInfoItem.setBattleScore(getBattleScore(roleHorse,false));
			
			int newLevel = 0;
			if(roleHorse.getManshipLevel()+1 >= maxManshipLevel){
				newLevel = maxManshipLevel;
			}else{
				newLevel = roleHorse.getManshipLevel()+1;
			}
			
			//属性数据
			List<AddAttriTypeValueItem> horsePropItemList = new ArrayList<AddAttriTypeValueItem>();
			
			List<AttriItem> attriItemItemList = getRoleHorseAllProp(horseId,roleHorse.getLevel(),roleHorse.getManshipLevel());
			
			for(AttriItem item : attriItemItemList){
				AddAttriTypeValueItem propItem = new AddAttriTypeValueItem();
				propItem.setAttriType(item.getAttriTypeValue());
				propItem.setAttriValue((int)item.getValue());
				horsePropItemList.add(propItem);
			}
			
			if(roleHorse.getLevel()+1 <= horseBase.getMaxLevel()){
				//下一级附加值
				List<AttriItem> addAttriItemItemList = getRoleHorseAllProp(horseId,roleHorse.getLevel(),newLevel);
				for(AttriItem addAttItem : addAttriItemItemList){
					for(AddAttriTypeValueItem value : horsePropItemList ){
						if(value.getAttriType() == addAttItem.getAttriTypeValue()){
							value.setAddAttriValue((int)addAttItem.getValue() - value.getAttriValue());
							break;
						}
					}
				}
			}
			roleHorseManshipInfoItem.setHorsePropItem(horsePropItemList);
			//物品消耗
			GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(manshipConsume.getGoodsId());
			if(goodsBase != null){
				goodsItem.setGoodsId(manshipConsume.getGoodsId());
				goodsItem.setNum(manshipConsume.getGoodsNum());
				goodsItem.setGoodsImageId(goodsBase.getImageId());
				goodsItem.setGoodsLevel((byte)goodsBase.getLevel());
				goodsItem.setBindType(goodsBase.getBindType());
				goodsItem.setGoodsName(goodsBase.getName());
				goodsItem.setQualityType(goodsBase.getQualityType());
				goodsList.add(goodsItem);
			}
			roleHorseManshipInfoItem.setGoodsList(goodsList);
			respMsg.setMashipInfo(roleHorseManshipInfoItem);
			
			return respMsg;
		}catch(Exception e){
			logger.error("sendC2604_RoleHorseManshipInfoRespMessage is error",e);
			return null ;
		}
	}

	@Override
	public C2605_RoleHorseUpgradeInfoRespMessage sendC2605_RoleHorseUpgradeInfoRespMessage(
			RoleInstance role, int horseId) {
		try{
			C2605_RoleHorseUpgradeInfoRespMessage respMsg = new C2605_RoleHorseUpgradeInfoRespMessage();
			//是否最高级
			boolean flag = isUpgrade(role.getIntRoleId(),horseId);
			byte isMax = flag == true ? (byte)1 : (byte)0;
			
			//当前坐骑
			HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseId);
			
			//下阶坐骑
			HorseBase nextHorseBase = GameContext.getHorseApp().getHorseBaseById(horseBase.getNextHorseId());
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(), horseId);
			if(nextHorseBase != null && isMax != (byte)1){
				
				respMsg.setHorseId(horseId);
				respMsg.setNextImageId(nextHorseBase.getImageId());
				respMsg.setNextQuality(nextHorseBase.getQuality());
				respMsg.setNextHorseName(nextHorseBase.getName());
				respMsg.setExp(roleHorse.getUpgradeHorseNum());
				respMsg.setMaxExp(horseBase.getGoodsNum());
				
				//下一阶战力
				respMsg.setUpgradeBattleScore(getBattleScore(roleHorse,true));
				
			    //属性数据
			  	List<AttriTypeStrValueItem> horsePropItemList = new ArrayList<AttriTypeStrValueItem>();
				//属性数据
				for(AttriItem item : nextHorseBase.getAttriItemList()){
					AttriTypeStrValueItem propItem = new AttriTypeStrValueItem();
					propItem.setType(item.getAttriTypeValue());
					propItem.setValue(AttributeType.formatValue(item.getAttriTypeValue(),item.getValue()));
					horsePropItemList.add(propItem);
				}
				
				respMsg.setHorsePropItemList(horsePropItemList);
			
			  	//消耗物品数据
			  	List<GoodsLiteNamedItem> goodsList = new ArrayList<GoodsLiteNamedItem>();
			  	GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
				GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(horseBase.getGoodsId());
				if(goodsBase != null){
					goodsItem.setGoodsId(horseBase.getGoodsId());
					goodsItem.setNum(horseBase.getGoodsNum());
					goodsItem.setGoodsImageId(goodsBase.getImageId());
					goodsItem.setGoodsLevel((byte)goodsBase.getLevel());
					goodsItem.setBindType(goodsBase.getBindType());
					goodsItem.setGoodsName(goodsBase.getName());
					goodsItem.setQualityType(goodsBase.getQualityType());
					goodsList.add(goodsItem);
				}
				respMsg.setGoodsList(goodsList);
			}else{
				respMsg.setMaxLevelFlag((byte)1);
			}
			return respMsg;
		}catch(Exception e){
			logger.error("sendC2605_RoleHorseUpgradeInfoRespMessage is error",e);
			return null ;
		}
	}

	@Override
	public RoleHorseLevelUpResult upgradeHorseLevelUpConsume(RoleInstance role,
			int horseId) {
		RoleHorseLevelUpResult result = new RoleHorseLevelUpResult();
		try{
			//获得角色坐骑
			RoleHorse roleHorse = getRoleHorse(role.getIntRoleId(),horseId);
			//获得坐骑基础数据
			HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(horseId);
			
			HorseBase nextHorseData = GameContext.getHorseApp().getHorseBaseById(horseData.getNextHorseId());
			
			//判断是否可以升品
			boolean flag = isUpgrade(role.getIntRoleId(),horseId);
			if(flag || nextHorseData == null){
				result.setInfo(GameContext.getI18n().getText(TextId.HORSE_UPGRADE_ERR_MAX_LEVEL));
				return result ;
			}
			
			GoodsBase goodsBase= GameContext.getGoodsApp().getGoodsBase(horseData.getGoodsId());
			String goodsName = Wildcard.getQualityGoodsName(goodsBase);
			//判断背包里是否有该物品
			flag = GameContext.getUserGoodsApp().isExistGoodsForBag(role, horseData.getGoodsId());
			if(!flag){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_ERR_NO_GOODS,goodsName));
				return result ;
			}
			
			//获得物品数量
			int goodsNum = role.getRoleBackpack().countByGoodsId(horseData.getGoodsId());
			if(goodsNum < horseData.getGoodsNum()){
				//提示物品不足
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_UPGRADE_LEVELUP_CONSUME_GOODS, horseData.getGoodsNum(),goodsName));
				return result ;
			}
			
			if(goodsNum >= horseData.getGoodsNum()){
				goodsNum = horseData.getGoodsNum();
			}
			
		    //扣除物品
			GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, horseData.getGoodsId(),goodsNum,
					OutputConsumeType.horse_upgrade_consume);
			if(!gr.isSuccess()){
				result.setInfo(gr.getInfo());
				return result ;
			}
			
			//先拿物品数量当经验计算
			boolean isUpgrade = roleHorseUpgrade(role,roleHorse,goodsNum);
			result.setLevelUp(isUpgrade);
			result.setHorseId(horseId);
			result.setBattleScore(getBattleScore(roleHorse,false));
			result.setExp(roleHorse.getUpgradeHorseNum());
			result.success();
			return result ;
		}catch(Exception e){
			logger.error("upgradeHorseLevelUpConsume is error",e);
			result.setInfo("upgradeHorseLevelUpConsume is error");
			return result ;
		}
	}
	
	@Override
	public boolean roleHorseUpgrade(RoleInstance role,RoleHorse roleHorse,int num){
		boolean flag = false;
		try{
			if(roleHorse != null){
				//获得坐骑基础数据
				HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
				//判断坐骑经验是否高出当前等级经验
				if((roleHorse.getUpgradeHorseNum() + num) >= horseData.getGoodsNum()){
					int oldHorseId = roleHorse.getHorseId();
					//老的坐骑减属性
					cutRoleHorseProp(role,roleHorse);
					
					//删掉原坐骑
					removeRoleHorse(role.getIntRoleId(),roleHorse.getHorseId());
					
					//升品后的坐骑
					HorseBase nextHorseData = GameContext.getHorseApp().getHorseBaseById(horseData.getNextHorseId());
					roleHorse.setHorseId(nextHorseData.getId());
					roleHorse.setUpgradeHorseNum(0);
					
					//当前品质
					Map<Short,Integer> oldMap= GameContext.getHorseApp().getHorseExpMap(roleHorse.getQuality());
					Integer exp = oldMap.get(roleHorse.getLevel());
					
					roleHorse.setQuality( (byte)(roleHorse.getQuality() +1));
					
					short level = roleHorse.getLevel();
					//新品质
					Map<Short,Integer> newMap= GameContext.getHorseApp().getHorseExpMap(roleHorse.getQuality());
					//降级操作
					for(Entry<Short,Integer> e : newMap.entrySet()){
						if(exp >= e.getValue()){
							level = e.getKey();
						}
					}
					roleHorse.setLevel(level);
					
					//保存新坐骑
					roleHorse.setFlag((byte)0);
					saveOrUpdRoleHorse(roleHorse);
					
					//新品质的的坐骑加属性
					addRoleHorseProp(role,roleHorse);
					
					//是否最大品质
					boolean isMaxQuality = isUpgrade(role.getIntRoleId(),roleHorse.getHorseId());
					//更新坐骑列表
					if(!isMaxQuality){
						C2608_RoleHorseNewRespMessage msg = new C2608_RoleHorseNewRespMessage();
						RoleHorseListItem horseItem = GameContext.getRoleHorseApp().getRoleHorseItem(role.getIntRoleId(),roleHorse.getHorseId());
						msg.setOldHorseId(oldHorseId);
						msg.setHorseItem(horseItem);
						role.getBehavior().sendMessage(msg);
					}
					
					//发送初始化消息
					role.getBehavior().sendMessage(sendC2605_RoleHorseUpgradeInfoRespMessage(role,roleHorse.getHorseId()));
					
					//视野通知
					C2609_RoleHorseUpgradeNotifyMessage notifyMessage = new C2609_RoleHorseUpgradeNotifyMessage();
					notifyMessage.setHorseId(roleHorse.getHorseId());
					notifyMessage.setRoleId(role.getIntRoleId());
					notifyMessage.setImageId(horseData.getImageId());
					notifyMessage.setState(roleHorse.getState());
					broadcast(role,notifyMessage);
					
					flag = true;
				}else{
					roleHorse.setUpgradeHorseNum(roleHorse.getUpgradeHorseNum() + num);
					saveOrUpdRoleHorse(roleHorse);
				}
			}
			return flag;
		}catch(Exception e){
			logger.error("roleHorseUpgrade is error",e);
			return flag ;
		}
	}
	
	@Override
	public RoleHorse onBattle(RoleInstance role, int horseId,byte state) {
		Map<Integer, RoleHorse> horseMap = getAllRoleHorseByRoleId(role.getIntRoleId());
		
		RoleHorse oldHorse = null;
		if(horseMap.containsKey(horseId)){
			oldHorse = horseMap.get(horseId);
			if(oldHorse.getState() == state){
				return oldHorse;
			}else{
				oldHorse = null;
			}
		}
		for(Entry<Integer,RoleHorse> horse: horseMap.entrySet()){
			if(horse.getKey() == horseId){
				//如果当前状态为未骑乘
				if(state == (byte)1){
					//出战
					horse.getValue().setState((byte)1);
					//添加新英雄的技能
					role.getSkillMap().putAll(horse.getValue().getSkillMap());
					
				}else{
					//休息
					horse.getValue().setState((byte)0);
					
					oldHorse = horse.getValue();
					
//					//删除此英雄技能
//					for(Short skillId : horse.getValue().getSkillMap().keySet()){
//						role.delSkillStat(skillId);
//					}
				}
			}else{
				if(horse.getValue().getState() == (byte)1){
					//删除此英雄技能
					for(Short skillId : horse.getValue().getSkillMap().keySet()){
						role.delSkillStat(skillId);
					}
					oldHorse = horse.getValue();
				}
				//休息
				horse.getValue().setState((byte)0);
			}
			saveOrUpdRoleHorse(horse.getValue());
		}
		calcHorseMoveSpeed(role,oldHorse);
		return getRoleHorse(role.getIntRoleId(),horseId);
	}

	@Override
	public RoleHorse getOnBattleRoleHorse(int roleId) {
		RoleHorse roleHorse = null;
		try{
			Map<Integer, RoleHorse> horseMap = getAllRoleHorseByRoleId(roleId);
			if(horseMap != null && !horseMap.isEmpty()){
				for(Entry<Integer,RoleHorse> horse: horseMap.entrySet()){
					if(horse.getValue().getState() == (byte)1){
						HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(horse.getKey());
						initSkill(horse.getValue(),horseData.getSkillId());
						roleHorse = horse.getValue();
						break;
					}
				}
			}
		}catch(Exception e){
			logger.error("getOnBattleRoleHorse",e);
		}
		
		return roleHorse;
	}
	
	//初始化坐骑技能
	private void initSkill(RoleHorse horse,short skillId){
		RoleSkillStat stat = horse.getSkillMap().get(skillId);
		if(null != stat){
			return ;
		}
		stat = new RoleSkillStat(); 
		stat.setSkillId(skillId);
		stat.setSkillLevel(horse.getLevel());
		stat.setRoleId(String.valueOf(horse.getRoleId()));
		stat.setLastProcessTime(0);
		horse.getSkillMap().put(skillId, stat);
	}
	
	/**
	 * 删坐骑
	 * @param horseId
	 */
	private void removeRoleHorse(int roleId,int horseId){
		Map<Integer,RoleHorse> horseMap = getAllRoleHorseByRoleId(roleId);
		RoleHorse roleHorse = horseMap.get(horseId);
		roleHorse.setFlag((byte)1);
		saveOrUpdRoleHorse(roleHorse);
		horseMap.remove(horseId);
	}
	
	/**
	 * 广播变更消息
	 * @param role
	 * @param msg
	 */
	@Override
	public void broadcast(RoleInstance role,Message msg){
		role.getBehavior().sendMessage(msg);
		MapInstance map = role.getMapInstance() ;
		if(null == map){
			return ;
		}
		map.broadcastMap(role, msg);
	}

	@Override
	public List<RoleHorseListItem> getRoleHorseList(int roleId) {
		
		Map<Integer,RoleHorse> horseBaseMap  = GameContext.getRoleHorseApp().getAllRoleHorseByRoleId(roleId);
		
		List<RoleHorseListItem> horseList = new ArrayList<RoleHorseListItem>();
		
		for(Entry<Integer,RoleHorse> roleHorse : horseBaseMap .entrySet()){
			HorseBase base = GameContext.getHorseApp().getHorseBaseById(roleHorse.getKey());
			int battleScore = GameContext.getRoleHorseApp().getBattleScore(roleHorse.getValue(),false);
			RoleHorseListItem item = new RoleHorseListItem();
			item.setHorseId(roleHorse.getKey());
			item.setBattleScore(battleScore);
			item.setState(roleHorse.getValue().getState());
			item.setQuality(roleHorse.getValue().getQuality());
			item.setHorseName(base.getName());
			item.setHorseLevel((byte)roleHorse.getValue().getLevel());
			item.setHorseImageId(base.getImageId());
			item.setHorseIconId(base.getIconId());
			horseList.add(item);
		}
		
		//排序
		Collections.sort(horseList, new Comparator<RoleHorseListItem>(){
			@Override
			public int compare(RoleHorseListItem h1, RoleHorseListItem h2) {
				if(h1.getState() > h2.getState()){
					return 0;
				}else{
					if(h1.getHorseLevel() < h2.getHorseLevel()){
						if(h1.getQuality() < h2.getQuality()){
							return 1;
						}else{
							return 0;
						}
					}else{
						return -1;
					}
				}
			}
		});
		return horseList;
	}
	
	@Override
	public RoleHorseListItem getRoleHorseItem(int roleId,int horseId) {
		
		RoleHorse horse  = GameContext.getRoleHorseApp().getRoleHorse(roleId, horseId);
		
		HorseBase base = GameContext.getHorseApp().getHorseBaseById(horse.getHorseId());
		int battleScore = GameContext.getRoleHorseApp().getBattleScore(horse,false);
		RoleHorseListItem item = new RoleHorseListItem();
		item.setHorseId(horse.getHorseId());
		item.setBattleScore(battleScore);
		item.setState(horse.getState());
		item.setQuality(horse.getQuality());
		item.setHorseName(base.getName());
		item.setHorseLevel((byte)horse.getLevel());
		item.setHorseImageId(base.getImageId());
		item.setHorseIconId(base.getIconId());
		
		return item;
	}

	@Override
	public boolean isUseGoodsHorse(int roleId,int goodsHorseId) {
		//获得坐骑基础数据
		HorseBase goodsHorseBase = GameContext.getHorseApp().getHorseBaseById(goodsHorseId);
		Map<Integer, RoleHorse> roleHorseMap = getAllRoleHorseByRoleId(roleId);
		boolean flag = false;
		if(roleHorseMap != null && !roleHorseMap.isEmpty()){
			for(Entry<Integer,RoleHorse> horseMap : roleHorseMap.entrySet()){
				HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseMap.getKey());
				if(goodsHorseBase.getRace() == horseBase.getRace()){
					if(goodsHorseBase.getQuality() > horseMap.getValue().getQuality()){
						flag = true;
					}else{
						flag = false;
					}
					break;
				}else{
					flag = true;
				}
			}
		}else{
			flag = true;
		}
		return flag;
	}

	@Override
	public void addRoleHorse(RoleInstance role, int goodsHorseId) {
		//获得坐骑基础数据
		HorseBase goodsHorseBase = GameContext.getHorseApp().getHorseBaseById(goodsHorseId);
		RoleHorse roleHorse = null;
		//是否通知
		boolean notifyFlag = false;
		Map<Integer, RoleHorse> roleHorseMap = getAllRoleHorseByRoleId(role.getIntRoleId());
		if(roleHorseMap != null && !roleHorseMap.isEmpty()){
			for(Entry<Integer,RoleHorse> horseMap : roleHorseMap.entrySet()){
				HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horseMap.getKey());
				if(goodsHorseBase.getRace() == horseBase.getRace()){
					if(goodsHorseBase.getQuality() > horseMap.getValue().getQuality()){
						if(horseBase.getId() != goodsHorseBase.getId()){
							roleHorse = horseMap.getValue();
							notifyFlag = true;
							break;
						}
					}else{
						notifyFlag = false;
					}
				}else{
					notifyFlag = true;
				}
			}
		}else{
			notifyFlag = true;
		}
		if(notifyFlag){
			initRoleHorse(role,roleHorse,goodsHorseId);
		}
	}
	
	/**
	 * 初始化坐骑
	 */
	private void initRoleHorse(RoleInstance role, RoleHorse horse,int goodsHorseId){
		RoleHorse roleHorse = new RoleHorse();
		HorseBase base = GameContext.getHorseApp().getHorseBaseById(goodsHorseId);
		if(horse != null){
			roleHorse.setQuality(base.getQuality());
			roleHorse.setState(horse.getState());
			
			//当前品质
			Map<Short,Integer> oldMap= GameContext.getHorseApp().getHorseExpMap(horse.getQuality());
			Integer exp = oldMap.get(horse.getLevel());
			
			short level = horse.getLevel();
			//新品质
			Map<Short,Integer> newMap= GameContext.getHorseApp().getHorseExpMap(roleHorse.getQuality());
			//降级操作
			for(Entry<Short,Integer> e : newMap.entrySet()){
				if(exp >= e.getValue()){
					level = e.getKey();
				}
			}
			roleHorse.setLevel(level);
			
			roleHorse.setExp(horse.getExp());
			roleHorse.setRoleId(horse.getRoleId());
			roleHorse.setLevelUpHorseNum(horse.getLevelUpHorseNum());
			roleHorse.setManshipLevel(horse.getManshipLevel());
			roleHorse.setUpgradeHorseNum(horse.getUpgradeHorseNum());
			
			//老的坐骑减属性
			cutRoleHorseProp(role,horse);
			
			//删掉原坐骑
			removeRoleHorse(role.getIntRoleId(),horse.getHorseId());
			
			//保存新坐骑
			roleHorse.setFlag((byte)0);
		}else{
			roleHorse.setRoleId(role.getIntRoleId());
			roleHorse.setQuality(base.getQuality());
			roleHorse.setExp(0);
			roleHorse.setLevel((short)1);
			roleHorse.setLevelUpHorseNum(0);
			roleHorse.setUpgradeHorseNum(0);
			roleHorse.setManshipLevel((byte)0);
			//骑乘状态 
			roleHorse.setState((byte)0);
		}
		roleHorse.setHorseId(goodsHorseId);
		saveOrUpdRoleHorse(roleHorse);
		
		//新品质的的坐骑加属性
		addRoleHorseProp(role,roleHorse);
		
		//视野通知
		C2609_RoleHorseUpgradeNotifyMessage notifyMessage = new C2609_RoleHorseUpgradeNotifyMessage();
		notifyMessage.setHorseId(roleHorse.getHorseId());
		notifyMessage.setRoleId(role.getIntRoleId());
		notifyMessage.setImageId(base.getImageId());
		notifyMessage.setState(roleHorse.getState());
		broadcast(role,notifyMessage);
		
	}
	
	/**
	 * 计算骑乘坐骑移动速度
	 */
	@Override
	public void calcHorseMoveSpeed(RoleInstance role,RoleHorse oldHorse){
		RoleHorse roleHorse = getOnBattleRoleHorse(role.getIntRoleId());
		HorseBase horseBase = null;
		AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
		if(oldHorse != null){
			horseBase = GameContext.getHorseApp().getHorseBaseById(oldHorse.getHorseId());
			if(horseBase != null){
				additionBuffer.append(AttributeType.speed.getType(), horseBase.getMoveSpeed(),false);
				additionBuffer.reverse();
			}
		}
		if(roleHorse != null){
			horseBase = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
			if(horseBase != null){
				additionBuffer.append(AttributeType.speed.getType(), horseBase.getMoveSpeed(),false);
			}
		}
		GameContext.getUserAttributeApp().changeAttribute(role, additionBuffer);
		role.getBehavior().notifyAttribute();
	}

	@Override
	public HorseBase getHorseResId(int roleId) {
		HorseBase horse = null;
		RoleHorse roleHorse  = GameContext.getRoleHorseApp().getOnBattleRoleHorse(roleId);
		if(roleHorse != null){
			horse = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
		}
		return horse;
	}
	
}
