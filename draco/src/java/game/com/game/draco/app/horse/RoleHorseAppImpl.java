package com.game.draco.app.horse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import lombok.Getter;

import org.python.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHorse;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hint.HintAppImpl;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseExchange;
import com.game.draco.app.horse.config.HorseLuckProb;
import com.game.draco.app.horse.config.HorseProp;
import com.game.draco.app.horse.config.HorseSkill;
import com.game.draco.app.horse.config.HorseSkillLimit;
import com.game.draco.app.horse.config.HorseStar;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.horse.domain.RoleHorseCache;
import com.game.draco.app.horse.domain.RoleHorseSkill;
import com.game.draco.app.horse.vo.RoleHorseLevelUpResult;
import com.game.draco.app.horse.vo.RoleHorseSkillResult;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.message.item.AddAttriTypeValueItem;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsBaseHorseItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.HintGoodsTermItem;
import com.game.draco.message.item.HintRulesItem;
import com.game.draco.message.item.HorseSkillItem;
import com.game.draco.message.item.RoleHorseInfoItem;
import com.game.draco.message.item.RoleHorseItem;
import com.game.draco.message.push.C2609_RoleHorseUpgradeNotifyMessage;
import com.game.draco.message.request.C2614_HorseGoodsToShadowReqMessage;
import com.game.draco.message.response.C2601_RoleHorseInfoRespMessage;
import com.game.draco.message.response.C2605_RoleHorseUpgradeInfoRespMessage;
import com.game.draco.message.response.C2612_RoleHorseSkillInfoRespMessage;
import com.game.draco.message.response.C2613_RoleHorseSkillListRespMessage;
import com.google.common.collect.Maps;

public class RoleHorseAppImpl implements RoleHorseApp {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final short HORSE_GOODS_TO_SHADOW_CMDID = new C2614_HorseGoodsToShadowReqMessage().getCommandId();
	
	public final static float TEN_THOUSAD_F = 10000.f;
	/**
	 * 在线角色坐骑列表 key: roleId value: horseMap<坐骑ID，坐骑对象>
	 */
	@Getter
	private Map<Integer, Map<Integer, RoleHorse>> roleHorseMap = Maps.newConcurrentMap();

	
	@Override
	public Map<Integer, RoleHorse> getAllRoleHorseByRoleId(int roleId) {
		if(Util.isEmpty(this.roleHorseMap)){
			return null ;
		}
		return roleHorseMap.get(roleId);
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try{
			// 初始化角色坐骑数据
			List<RoleHorse> horseList = GameContext.getBaseDAO().selectList(
					RoleHorse.class, RoleHorse.ROLE_ID,role.getIntRoleId());
			if(Util.isEmpty(horseList)){
				return 1;
			}
			
			//上线后不再使用该功能			
			validRoleHorseSkill(horseList);
			
			addRoleHorseMap(role,horseList);
			
			RoleHorse roleHorse  = GameContext.getRoleHorseApp().getOnBattleRoleHorse(role.getIntRoleId());
			if(roleHorse == null){
				int horseId = getBestStrongHorse(role);
				onBattle(role,horseId,(byte)1);
			}
		}catch(Exception ex){
			logger.error("onJoinGame",ex);
			return 0;
		}
		return 1;
	}
	
	//上线后不再使用该功能
	private void validRoleHorseSkill(List<RoleHorse> horseList){
		for(RoleHorse roleHorse : horseList){
			List<HorseSkill> skillList = GameContext.getHorseApp().getHorseSkillList(roleHorse.getHorseId());
			if(Util.isEmpty(skillList)){
				continue;
			}
			List<RoleHorseSkill> horseSkillList = getRoleHorseSkill(roleHorse.getRoleId(),roleHorse.getHorseId());
			for(RoleHorseSkill roleHorseSkill : horseSkillList){
				for(HorseSkill skill : skillList){
					if(roleHorseSkill.getHorseId() == skill.getHorseId() 
							&& roleHorseSkill.getSkillId() != skill.getSkillId()){
						deleteRoleHorseSkill(roleHorseSkill.getRoleId(),roleHorseSkill.getHorseId(),roleHorseSkill.getSkillId());
						roleHorseSkill.setSkillId(skill.getSkillId());
						GameContext.getRoleHorseApp().saveOrUpdRoleHorseSkill(roleHorseSkill);
						break;
					}
				}
			}
		}
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		int roleId = role.getIntRoleId();
		try {
			// 下线存储骑乘坐骑数据
			saveRoleHorseOnBattleData(roleId);
			//下先保存坐骑数据
			Map<Integer, RoleHorse> map = roleHorseMap.get(roleId);
			if(map != null && !map.isEmpty()){
				for(Entry<Integer,RoleHorse> roleHorse : map.entrySet()){
					HorseBase base = GameContext.getHorseApp().getHorseBaseById(roleHorse.getKey());
					if(base == null){
						continue;
					}
					roleHorse.getValue().setBattleScore(getBattleScore(roleHorse.getValue(), false));
					saveOrUpdRoleHorse(roleHorse.getValue());
				}
			}
			
		}catch(Exception ex){
			logger.error("roleHorse onLeaveGame error,roleId=" + roleId,ex);
			return 0;
		}
		roleHorseMap.remove(roleId);
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		roleHorseMap.remove(roleId);
		return 1 ;
	}
	
	private void saveRoleHorseOnBattleData(int roleId){
		RoleHorseCache cache = getRoleHorseBattleCache(roleId);
		if(cache != null){
			GameContext.getRoleHorseStorage().saveRoleHorseOnBattle(cache);
		}
	}
	
	@Override
	public RoleHorseCache getRoleHorseBattleCache(int roleId){
		RoleHorse roleHorse = getOnBattleRoleHorse(roleId);
		RoleHorseCache cache = null;
		if(roleHorse != null){
			HorseProp horseProp = getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
			cache = new RoleHorseCache();
			cache.setRoleId(roleId);
			cache.setResId(horseProp.getResId());
			cache.setName(horseProp.getHorseName());
			cache.setBattleScore(getBattleScore(roleHorse, false));
			cache.setQuality(roleHorse.getQuality());
			
			cache.setIconId(horseProp.getIconId());
			
			List<AttriTypeStrValueItem> horsePropItemList = new ArrayList<AttriTypeStrValueItem>();
			//属性数据
		  	List<AttriItem> attriItemItemList = getRoleHorseAllProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
			for(AttriItem item : attriItemItemList){
				AttriTypeStrValueItem propItem = new AttriTypeStrValueItem();
				propItem.setType(item.getAttriTypeValue());
				propItem.setValue(AttributeType.formatValue(item.getAttriTypeValue(),item.getValue()));
				horsePropItemList.add(propItem);
			}
			
			cache.setHorsePropItem(horsePropItemList);
			
//			List<RoleHorseSkill> horseSkillList = roleHorse.getSkillList();
//			for(RoleHorseSkill horseSkill : horseSkillList){
//				HorseSkillItem skillItem = new HorseSkillItem();
//				Skill skill = GameContext.getSkillApp().getSkill(horseSkill.getSkillId());
//				skillItem.setSkillId(horseSkill.getSkillId());
//				if(null != skill){
//					skillItem.setSkillImageId(skill.getIconId());
//					skillItem.setSkillName(skill.getName());
//					cache.getHorseSkillItemList().add(skillItem);
//				}
//			}
		}
		return cache;
	}
	
	/**
	 * 初始化数据
	 */
	private void addRoleHorseMap(RoleInstance role,List<RoleHorse> horseList){
		try{
			if(Util.isEmpty(horseList)){
				return ;
			}
			for(RoleHorse roleHorse : horseList){
				//坐骑技能
				List<RoleHorseSkill> horseSkillList = getRoleHorseSkill(role.getIntRoleId(),roleHorse.getHorseId());
				roleHorse.setSkillList(horseSkillList);
				
				for(RoleHorseSkill skill : horseSkillList){
					SkillLearnFunc learnFunc = GameContext.getUserSkillApp().getSkillLearnFunc(SkillSourceType.Horse);
					learnFunc.innerAddSkill(role,String.valueOf(roleHorse.getHorseId()),skill.getSkillId(),skill.getLevel());
				}
				
				/*if(roleHorseMap.containsKey(roleHorse.getRoleId())){
					Map<Integer, RoleHorse> map = roleHorseMap.get(roleHorse.getRoleId());
					map.put(roleHorse.getHorseId(), roleHorse);
				}else{
					Map<Integer, RoleHorse> map = Maps.newConcurrentMap();
					map.put(roleHorse.getHorseId(),roleHorse);
					roleHorseMap.put(roleHorse.getRoleId(),map);
				}*/
				
				Map<Integer, RoleHorse> map = roleHorseMap.get(roleHorse.getRoleId());
				if(null == map){
					map = Maps.newHashMap();
					roleHorseMap.put(roleHorse.getRoleId(),map);
				}
				map.put(roleHorse.getHorseId(),roleHorse);
			}
		
		}catch(Exception e){
			logger.error("addRoleHorseMap is error",e);
		}
	}
	
	/**
	 * 获得坐骑技能
	 * @param horseId
	 * @return
	 */
	@Override
	public List<RoleHorseSkill> getRoleHorseSkill(int roleId,int horseId){
		return GameContext.getBaseDAO().selectList(RoleHorseSkill.class, RoleHorseSkill.ROLE_ID,roleId,RoleHorseSkill.HORSE_ID,horseId);
	}
	
	@Override
	public RoleHorseLevelUpResult levelUp(RoleInstance role, int horseId) {
		RoleHorseLevelUpResult result = new RoleHorseLevelUpResult();
		try {
			// 获得角色坐骑
			RoleHorse roleHorse = getRoleHorse(role.getIntRoleId(), horseId);
			int preQuality = roleHorse.getQuality();
			int preStar = roleHorse.getStar();
			result = isLevelUp(role, roleHorse);

			if (result.isSuccess()) {

				roleHorse.setStarNum(0);
				byte oldQuality = roleHorse.getQuality();
				byte oldStar = roleHorse.getStar();

				byte highStar = GameContext.getHorseApp().getHorseHighStar(
						roleHorse.getHorseId(), oldQuality);
				byte highQuality = GameContext.getHorseApp()
						.getHorseHighQuailty(roleHorse.getHorseId());
				if (roleHorse.getStar() < highStar) {
					roleHorse.setStar((byte) (oldStar + 1));
				} else {
					if (roleHorse.getQuality() < highQuality) {
						roleHorse
								.setQuality((byte) (roleHorse.getQuality() + 1));
						roleHorse.setStar(GameContext.getHorseApp()
								.getHorseLowStar(roleHorse.getHorseId(),
										roleHorse.getQuality()));
					}
				}
				updRoleHorseProp(role, horseId, roleHorse.getQuality(),
						roleHorse.getStar(), oldQuality, oldStar);
				HorseProp horseProp = getHorseProp(horseId,
						roleHorse.getQuality(), roleHorse.getStar());
				result.setInfo(GameContext.getI18n().messageFormat(
						horseProp.getDes()));
				// 英雄姻缘
				GameContext.getHeroApp().onHorseStarChanged(
						role.getIntRoleId(), roleHorse.getHorseId(),
						roleHorse.getQuality(), roleHorse.getStar(),
						preQuality, preStar);
				// 通知红点提示规则变化
				this.pushHintRulesChange(role, roleHorse);
				// 世界广播
				this.broadcast(role, horseProp);
				RoleHorse onHorse = getOnBattleRoleHorse(role.getIntRoleId());
				if(onHorse != null){
					if(onHorse.getHorseId() == roleHorse.getHorseId()){
						broadcastHorse(role, horseProp.getResId(), onHorse.getHorseId(), onHorse.getState());
					}
				}
			}
			saveOrUpdRoleHorse(roleHorse);
			result.setHorseId(horseId);
			result.setStarNum(roleHorse.getStarNum());
			return result;
		} catch (Exception ex) {
			logger.error("levelUp is error ", ex);
		}
		return result;
	}
	
	/**
	 * 走马灯广播升星成功
	 * @param role
	 * @param formula
	 */
	private void broadcast(RoleInstance role, HorseProp horseProp) {
		try {
			String broadcastInfo = horseProp.getBroadcastTips(role);
			if (Util.isEmpty(broadcastInfo)) {
				return;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.Goods_UpgradeStar, ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception e) {
			logger.error("equipUpgradeStar broadcast error", e);
		}
	}
	
	/**
	 * 更新坐骑属性 (按等级计算)
	 * @param role
	 * @param roleHorse
	 */
	private void updRoleHorseProp(RoleInstance role,int horseId,byte newQuality,byte newStar,byte oldQuality,byte oldStar){
		try{
			
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			additionBuffer.append(getRoleHorseAllProp(horseId, oldQuality,oldStar));
			additionBuffer.reverse();
			
			//加上本级加成属性
			additionBuffer.append(getRoleHorseAllProp(horseId,newQuality,newStar));
			
			// 修改角色属性值
			GameContext.getUserAttributeApp().changeAttribute(role, additionBuffer);
			role.getBehavior().notifyAttribute();
			
		}catch(Exception ex){
			logger.error("updRoleHorseProp is error " ,ex);
		}
	}
	
	@Override
	public AttriBuffer getAttriBuffer(RoleInstance role) {
		Map<Integer, RoleHorse> allMap = getAllRoleHorseByRoleId(role.getIntRoleId());
		if (Util.isEmpty(allMap)) {
			return null;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		RoleHorse roleHorse = null;
		for (Entry<Integer, RoleHorse> horse : allMap.entrySet()) {
			buffer.append(getRoleHorseAllProp(horse.getKey(), horse.getValue().getQuality(), horse.getValue().getStar()));
			if (horse.getValue().getState() == (byte) 1) {
				roleHorse = horse.getValue();
			}
		}
		if (null == roleHorse) {
			return buffer;
		}
		//速度
		HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
		if (horseBase != null) {
			HorseProp horseProp = getHorseProp(roleHorse.getHorseId(), roleHorse.getQuality(), roleHorse.getStar());
			buffer.append(AttributeType.speed.getType(), horseProp.getMoveSpeed(), false);
		}
		return buffer;
	}
	
	/**
	 * 获得坐骑属性
	 */
	private List<AttriItem> getRoleHorseAllProp(int horseId,byte quality,byte star){
		try{
			HorseBase horseData = GameContext.getHorseApp().getHorseBaseById(horseId);
			HorseProp horseProp = getHorseProp(horseId,quality,star);
			
			AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
			if(horseData != null){
				additionBuffer.append(horseData.getAttriItemList());
			}
			if(horseProp != null){
				additionBuffer.append(horseProp.getAttriItemList());
			}
			List<AttriItem> list = Lists.newArrayList();
			if(additionBuffer.isEmpty()){
				return list ;
			}
			list.addAll(additionBuffer.getMap().values());
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
			
			HorseProp hrseProp = getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
			
			if(flag){
				hrseProp = getNextHorsePorp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
			}
			
			List<AttriItem> baseItems = horseBase.getAttriItemList();
			
			//基础战力
			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			buffer.append(baseItems);
			if(hrseProp != null){
				List<AttriItem> additionItems = hrseProp.getAttriItemList();
				buffer.append(additionItems);
			}
			
			buffer.precToValue();
			int battleScore = GameContext.getAttriApp().getAttriBattleScore(buffer);
			battleScore += GameContext.getSkillApp().getSkillBattleScore(packRoleSkillStat(roleHorse.getRoleId(),roleHorse.getSkillList()));
			return battleScore;
			
		}catch(Exception e){
			logger.error("modifyRoleHorseManshipProp is error",e);
			return 0;
		}
	}
	
	@Override
	public RoleHorseLevelUpResult isLevelUp(RoleInstance role,RoleHorse roleHorse) {
		RoleHorseLevelUpResult result = new RoleHorseLevelUpResult();
		if(roleHorse != null){
//			if(roleHorse.getState() == HorseConstants.HORSE_STATE_ON){
//				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_ONBATTLE_LEVELUP_ERR));
//				return result;
//			}
			
			if(this.isReachMaxStar(roleHorse)){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_HIGH_QUALITY_ERR));
				return result;
			}
			
			HorseProp horseProp = getNextHorsePorp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
			
			if(horseProp == null){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_HIGH_QUALITY_ERR));
				return result;
			}
			//判断背包里是否有该物品
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(horseProp.getGoodsId());
			boolean flag = GameContext.getUserGoodsApp().isExistGoodsForBag(role, horseProp.getGoodsId());
			if(!flag){
				String goodsName = Wildcard.getQualityGoodsName(goodsBase);
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_ERR_NO_GOODS,goodsName));
				return result ;
			}
			
			//获得物品数量
			int goodsNum = role.getRoleBackpack().countByGoodsId(horseProp.getGoodsId());
			
			if(goodsNum + roleHorse.getStarNum() >= horseProp.getGoodsNum()){
				int num = goodsNum + roleHorse.getStarNum() - horseProp.getGoodsNum();
				goodsNum -= num; 
				result.success();
			}else{
				//记录消耗数
				roleHorse.setStarNum(roleHorse.getStarNum() + goodsNum);
				result.setResult((byte)2);
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_STARUP_STAGE,horseProp.getGoodsNum() - roleHorse.getStarNum(),goodsBase.getName()));
			}
			
		    //扣除物品
			GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, horseProp.getGoodsId(),goodsNum,
					OutputConsumeType.horse_level_consume);
			if(!gr.isSuccess()){
				result.failure();
				result.setInfo(gr.getInfo());
				return result;
			}
		}
		return result;
	}
	
	@Override
	public boolean isReachMaxStar(RoleHorse roleHorse) {
		byte highStar = GameContext.getHorseApp().getHorseHighStar(roleHorse.getHorseId(),roleHorse.getQuality());
		byte highQuality = GameContext.getHorseApp().getHorseHighQuailty(roleHorse.getHorseId());
		return roleHorse.getQuality() >= highQuality && roleHorse.getStar() >= highStar;
	}
	
	private void pushHintRulesChange(RoleInstance role, RoleHorse roleHorse) {
		
		HintRulesItem hintRulesItem = new HintRulesItem();
		hintRulesItem.setType(HintAppImpl.HINT_HORSE);
		hintRulesItem.setTargetId(roleHorse.getHorseId());
		List<HintGoodsTermItem> hintGoodsList = Lists.newArrayList();
		short needNum = Short.MAX_VALUE;
		HorseProp horseProp = null;
		if (this.isReachMaxStar(roleHorse)) {
			horseProp = this.getHorseProp(roleHorse.getHorseId(), roleHorse.getQuality(), roleHorse.getStar());
			if (null == horseProp) {
				return;
			}
		} else {
			horseProp = getNextHorsePorp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
			if (null == horseProp) {
				return;
			}
			needNum = horseProp.getGoodsNum();
		}
		hintGoodsList.add(new HintGoodsTermItem(horseProp.getGoodsId(), needNum));
		hintRulesItem.setHintGoodsTermList(hintGoodsList);
		GameContext.getHintApp().pushHintRulesChange(role, hintRulesItem);
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
	public C2601_RoleHorseInfoRespMessage sendC2601_RoleHorseInfoRespMessage(RoleInstance role,int horseId) {
		try{
			C2601_RoleHorseInfoRespMessage respMsg = new C2601_RoleHorseInfoRespMessage();
	
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(), horseId);
			
			RoleHorseInfoItem roleHorseInfoItem = new RoleHorseInfoItem();
			
			roleHorseInfoItem.setHorseId(roleHorse.getHorseId());
			
		  	List<AttriTypeStrValueItem> horsePropItemList = new ArrayList<AttriTypeStrValueItem>();
			//属性数据
		  	List<AttriItem> attriItemItemList = getRoleHorseAllProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
			for(AttriItem item : attriItemItemList){
				AttriTypeStrValueItem propItem = new AttriTypeStrValueItem();
				propItem.setType(item.getAttriTypeValue());
				propItem.setValue(AttributeType.formatValue(item.getAttriTypeValue(),item.getValue()));
				horsePropItemList.add(propItem);
			}
			
			roleHorseInfoItem.setHorsePropItem(horsePropItemList);
			
			respMsg.setHorseInfo(roleHorseInfoItem);
			
			return respMsg;
		}catch(Exception e){
			logger.error("sendC2601_RoleHorseInfoRespMessage is error",e);
			return null ;
		}
	}
	
	@Override
	public RoleHorse onBattle(RoleInstance role, int horseId,byte state) {
		Map<Integer, RoleHorse> horseMap = getAllRoleHorseByRoleId(role.getIntRoleId());
		if(Util.isEmpty(horseMap)){
			return null ;
		}
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
					role.getSkillMap().putAll(packRoleSkillStat(role.getIntRoleId(),horse.getValue().getSkillList()));
					
				}else{
					//休息
					horse.getValue().setState((byte)0);
					oldHorse = horse.getValue();
					
				}
			}else{
				if(horse.getValue().getState() == (byte)1){
					oldHorse = horse.getValue();
				}
				//休息
				horse.getValue().setState((byte)0);
			}
		}
		
		if(getOnBattleRoleHorse(role.getIntRoleId()) != null){
			for(Entry<Integer,RoleHorse> horse: horseMap.entrySet()){
				if(horse.getValue().getState() == (byte)0){
					//删除此英雄技能
					for(RoleHorseSkill horseSkill : horse.getValue().getSkillList()){
						role.delSkillStat(horseSkill.getSkillId());
					}
				}
			}
		}
		calcHorseMoveSpeed(role,oldHorse);
		return getRoleHorse(role.getIntRoleId(),horseId);
	}
	
	@Override
	public Map<Short,RoleSkillStat> packRoleSkillStat(int roleId,List<RoleHorseSkill> horseSkillList){
		Map<Short,RoleSkillStat> skillMap = Maps.newHashMap();
		for(RoleHorseSkill horseSkill : horseSkillList){
			RoleSkillStat skillStat = new RoleSkillStat();
			skillStat.setLastProcessTime(0);
			skillStat.setRoleId(String.valueOf(roleId));
			skillStat.setSkillId(horseSkill.getSkillId());
			skillStat.setSkillLevel(horseSkill.getLevel());
			skillMap.put(horseSkill.getSkillId(), skillStat);
		}
		return skillMap;
	}

	@Override
	public RoleHorse getOnBattleRoleHorse(int roleId) {
		RoleHorse roleHorse = null;
		try{
			Map<Integer, RoleHorse> horseMap = getAllRoleHorseByRoleId(roleId);
			if(horseMap != null && !horseMap.isEmpty()){
				for(Entry<Integer,RoleHorse> horse: horseMap.entrySet()){
					if(horse.getValue().getState() == (byte)1){
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
	
	/**
	 * 广播变更消息
	 * @param role
	 * @param msg
	 */
	private void broadcast(RoleInstance role,Message msg){
		role.getBehavior().sendMessage(msg);
		MapInstance map = role.getMapInstance() ;
		if(null == map){
			return ;
		}
		map.broadcastMap(role, msg);
	}

	@Override
	public List<RoleHorseItem> getRoleHorseList(RoleInstance role) {
		List<RoleHorseItem> horseList = new ArrayList<RoleHorseItem>();
		//角色坐骑
		Map<Integer,RoleHorse> horseMap  = GameContext.getRoleHorseApp().getAllRoleHorseByRoleId(role.getIntRoleId());
		//坐骑原型
		Map<Integer,HorseBase> horseBaseMap = GameContext.getHorseApp().getHorseBaseMap();
		
		for(Entry<Integer,HorseBase> entry : horseBaseMap .entrySet()){
			int horseId = entry.getKey() ;
			HorseBase horseBase = entry.getValue() ;
			//拥有的坐骑
			RoleHorse roleHorse = Util.fromMap(horseMap, horseId) ;
			
			RoleHorseItem item = new RoleHorseItem();
			item.setHorseId(horseId);
			item.setFlag((byte)0);
			item.setQuality(horseBase.getQuality());
			item.setStar(horseBase.getStar());
			if(null != roleHorse){
				//拥有
				item.setFlag((byte)1);
				int battleScore = GameContext.getRoleHorseApp().getBattleScore(roleHorse,false);
				item.setBattleScore(battleScore);
				item.setState(roleHorse.getState());
				item.setQuality(roleHorse.getQuality());
				item.setStar(roleHorse.getStar());
			}else{
				HorseExchange exchange = GameContext.getHorseApp().getHorseExchange(horseId);
				item.setNeedGoodsNum((short)exchange.getExchangeNum());
				item.setGoodsId(exchange.getGoodsId());
			}
			HorseProp horseProp = getHorseProp(horseId,item.getQuality(),item.getStar());
			if(null != horseProp){
				item.setResId(horseProp.getResId());
				item.setHorseIconId(horseProp.getIconId());
				item.setHorseName(horseProp.getHorseName());
			}
			//最大星
			item.setMaxStar(GameContext.getHorseApp().getHorseHighStar(horseId,item.getQuality()));
			
			horseList.add(item);
		}
		
		//排序
		Collections.sort(horseList,this.roleHorseItemComparator);
		return horseList;
	}

	@Override
	public boolean isUseGoodsHorse(int roleId,int goodsHorseId) {
		return true;
	}

	@Override
	public Result addRoleHorse(RoleInstance role, int horseId) {
		return initRoleHorse(role,horseId);
	}
	
	/**
	 * 初始化坐骑
	 */
	private Result initRoleHorse(RoleInstance role, int horseId){
		Result result = new Result();
		RoleHorse roleHorse = new RoleHorse();
		HorseBase base = GameContext.getHorseApp().getHorseBaseById(horseId);
		roleHorse.setHorseId(horseId);
		roleHorse.setRoleId(role.getIntRoleId());
		roleHorse.setState((byte)0);
		roleHorse.setStarNum(0);
		roleHorse.setQuality(base.getQuality());
		roleHorse.setStar(base.getStar());
		
		//添加坐骑技能
		List<HorseSkill> skillList = GameContext.getHorseApp().getHorseSkillList(horseId);
		List<RoleHorseSkill> horseSkillList = Lists.newArrayList();
		for(HorseSkill skill : skillList){
			RoleHorseSkill horseSkill = new RoleHorseSkill();
			horseSkill.setHorseId(horseId);
			horseSkill.setLevel((byte)1);
			horseSkill.setSkillId(skill.getSkillId());
			horseSkill.setRoleId(role.getIntRoleId());
			horseSkillList.add(horseSkill);
			GameContext.getRoleHorseApp().saveOrUpdRoleHorseSkill(horseSkill);
		}
		roleHorse.setSkillList(horseSkillList);
		//目标系统
		GameContext.getTargetApp().updateTarget(role, TargetCondType.HorseNum);
		saveOrUpdRoleHorse(roleHorse);
		//英雄姻缘
		GameContext.getHeroApp().onHorseAdded(role.getIntRoleId(), roleHorse.getHorseId(), roleHorse.getQuality(), roleHorse.getStar());
		
		HorseProp horseProp = getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
		
		// 红点提示规则
		this.pushHintRulesChange(role, roleHorse);
		
		AttriBuffer additionBuffer = AttriBuffer.createAttriBuffer();
		
		//加上本级加成属性
		additionBuffer.append(getRoleHorseAllProp(horseId,roleHorse.getQuality(),roleHorse.getStar()));
		
		// 修改角色属性值
		GameContext.getUserAttributeApp().changeAttribute(role, additionBuffer);
		role.getBehavior().notifyAttribute();
		
		//视野广播
		GameContext.getRoleHorseApp().broadcastHorse(role, horseProp.getResId(), roleHorse.getHorseId(), roleHorse.getState());
		
		result.success();
		
		result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_GOODS_USE_SUCCESS_TIPS,horseProp.getHorseName()));
		
		return result;
		
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
				HorseProp horseProp = getHorseProp(oldHorse.getHorseId(),oldHorse.getQuality(),oldHorse.getStar());
				additionBuffer.append(AttributeType.speed.getType(), horseProp.getMoveSpeed(),false);
				additionBuffer.reverse();
			}
		}
		if(roleHorse != null){
			horseBase = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
			if(horseBase != null){
				HorseProp horseProp = getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
				additionBuffer.append(AttributeType.speed.getType(), horseProp.getMoveSpeed(),false);
			}
		}
		GameContext.getUserAttributeApp().changeAttribute(role, additionBuffer);
		role.getBehavior().notifyAttribute();
	}

	@Override
	public HorseProp getOnBattleHorseProp(RoleInstance role) {
		RoleHorse roleHorse  = GameContext.getRoleHorseApp().getOnBattleRoleHorse(role.getIntRoleId());
		HorseProp horseProp = null;
		if(roleHorse != null){
			horseProp = getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
		}
		return horseProp;
	}

	@Override
	public int getRoleHorseNum(int roleId) {
		if(roleHorseMap.containsKey(roleId)){
			return roleHorseMap.get(roleId).size();
		}
		return 0;
	}
	
	
	
	//TODO:调用的地方需要判断
	@Override
	public int getBestStrongHorse(RoleInstance role){
		/*List<RoleHorseItem> horseList = getRoleHorseList(role);
		if(horseList == null || horseList.isEmpty()){
			return -1;
		}
		return horseList.get(0).getHorseId();*/
		
		Map<Integer, RoleHorse> roleHorseMap = getAllRoleHorseByRoleId(role.getIntRoleId());
		if(Util.isEmpty(roleHorseMap)){
			return 0 ;
		}
		RoleHorse strongHorse = null ;
		for(RoleHorse horse : roleHorseMap.values() ){
			if(null == strongHorse 
					|| horse.getBattleScore() > strongHorse.getBattleScore() ){
				HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(horse.getHorseId());
				if(horseBase == null){
					continue;
				}
				strongHorse = horse ;
			}
		}
		return (null == strongHorse) ? 0 : strongHorse.getHorseId() ;
	}

	@Override
	public void saveOrUpdRoleHorseSkill(RoleHorseSkill horseSkill) {
		GameContext.getBaseDAO().saveOrUpdate(horseSkill);
	}
	
	private void deleteRoleHorseSkill(int roleId, int horseId,short skillId) {
		
		GameContext.getBaseDAO().delete(RoleHorseSkill.class, RoleHorseSkill.ROLE_ID, roleId, RoleHorseSkill.HORSE_ID, horseId,RoleHorseSkill.SKILL_ID,skillId);
	}

	@Override
	public Result exchange(RoleInstance role,int horseId) {
		Result result = new Result();
		try{
			Map<Integer, RoleHorse> roleHorseMap = getAllRoleHorseByRoleId(role.getIntRoleId());
			if(roleHorseMap != null && roleHorseMap.containsKey(horseId)){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_HAVE_ERR));
				return result;
			}
			HorseExchange horseExchange = GameContext.getHorseApp().getHorseExchange(horseId);
			if(null == horseExchange){
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return result;
			}
			int haveGoodsNum = role.getRoleBackpack().countByGoodsId( horseExchange.getGoodsId());
			if(haveGoodsNum < horseExchange.getExchangeNum()){
				result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_HAVE_GOODS_ERR));
				return result;
			}
			  //扣除物品
			GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role,
					horseExchange.getGoodsId(),horseExchange.getExchangeNum(),
					OutputConsumeType.horse_exchange_consume);
			if(!gr.isSuccess()){
				result.setInfo(gr.getInfo());
				return result;
			}
			//添加坐骑
			result = addRoleHorse(role, horseId);
		}catch(Exception e){
			logger.error("Exchange",e);
		}
		return result;
	}
	
	/**
	 * 训练技能
	 * @param roleHorse
	 * @param skillId
	 * @return
	 */
	@Override
	public RoleHorseSkillResult trainSkill(RoleInstance role,RoleHorse roleHorse,RoleHorseSkill horseSkill){
		RoleHorseSkillResult result = new RoleHorseSkillResult();
		try{
			
			String key = horseSkill.getSkillId() + Cat.underline + horseSkill.getLevel();
			HorseSkillLimit limit = GameContext.getHorseApp().getSkillLimit(key);
			
			if(roleHorse.getQuality() < limit.getQuality() && roleHorse.getStar() < limit.getStar()){
				HorseStar star = GameContext.getHorseApp().getHorseStar(limit.getQuality());
				if(star != null){
					result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_SKILL_QS_ERR,star.getColor()+star.getStar()));
				}else{
					result.setInfo(GameContext.getI18n().messageFormat(TextId.SYSTEM_ERROR));
				}
				return result ;
			}
			
			if(limit.getGoodsId() > 0){
			
				GoodsBase goodsBase= GameContext.getGoodsApp().getGoodsBase(limit.getGoodsId());
				String goodsName = Wildcard.getQualityGoodsName(goodsBase);
				if( limit.getGoodsId() > 0){
					//判断背包里是否有该物品
					boolean flag = GameContext.getUserGoodsApp().isExistGoodsForBag(role, limit.getGoodsId());
					if(!flag){
						result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_ERR_NO_GOODS,goodsName));
						return result ;
					}
				}
				
				//获得物品数量
				int count = role.getRoleBackpack().countByGoodsId(limit.getGoodsId());
				if(count < limit.getGoodsNum()){
					//提示物品不足
					result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_MANSHIP_LEVELUP_CONSUME_GOODS, limit.getGoodsNum(),goodsName));
					return result ;
				}
			}
			
			int silverMoney = limit.getSilverMoney();
			if(silverMoney > 0) {
				if(role.getSilverMoney() < silverMoney) {
					//【游戏币/潜能/钻石不足弹板】 判断
					Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, silverMoney);
					if(ar.isIgnore()){
						result.setIgnore(true);
						return result;
					}
					if(!ar.isSuccess()){
						//提示游戏币不足
						result.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_MANSHIP_LEVELUP_CONSUME_MONEY, silverMoney));
						return result ;
					}
				}
				//扣除消耗
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, 
						OperatorType.Decrease, silverMoney, OutputConsumeType.horse_manship_level_consume_money);
				role.getBehavior().notifyAttribute();
			}
			
			if( limit.getGoodsId() > 0){
				//扣除物品
				GoodsResult gr = GameContext.getUserGoodsApp().deleteForBag(role, limit.getGoodsId(),limit.getGoodsNum(),
						OutputConsumeType.horse_manship_level_consume_goods);
				if(!gr.isSuccess()){
					result.setInfo(gr.getInfo());
					return result ;
				}
			}
			
			//幸运值每次增加
			Random rand = new Random();
			int temp = limit.getRandMaxLuck() - limit.getRandMinLuck() + 1;
			int randLuck = rand.nextInt(temp) + limit.getRandMinLuck();
			
			List<HorseLuckProb> list = GameContext.getHorseApp().getLuckProbList(limit.getSchemeId());
			if(Util.isEmpty(list)){
				result.setInfo(GameContext.getI18n().getText(TextId.Skill_MaxLevel_Fail));
				return result ;
			}
			for(HorseLuckProb luck : list){
				if(horseSkill.getLuck() >= luck.getLuckLower() && horseSkill.getLuck() <= luck.getLuckHigh()){
					int prob = luck.getProb();
					boolean flag = RandomUtil.on(prob);
					if(flag){
						if(roleHorse.getState() == (byte)1){
							role.delSkillStat(horseSkill.getSkillId());
							
							RoleSkillStat stat = new RoleSkillStat(); 
							stat.setSkillId(horseSkill.getSkillId());
							stat.setSkillLevel(horseSkill.getLevel() + 1);
							stat.setRoleId(role.getRoleId());
							stat.setLastProcessTime(0);
							role.getSkillMap().put(horseSkill.getSkillId(), stat);
						}
						horseSkill.setLevel((short)(horseSkill.getLevel() + 1));
						horseSkill.setLuck(0);
						result.setFlag(true);
					}
					break;
				}
			}
			if(!result.isFlag()){
				horseSkill.setLuck(horseSkill.getLuck() + randLuck);
			}
			saveOrUpdRoleHorseSkill(horseSkill);
			result.setLuck(horseSkill.getLuck());
			result.success();
		}catch(Exception e){
			logger.error("trainSkill",e);
		}
		return result;
	}

	@Override
	public C2612_RoleHorseSkillInfoRespMessage sendC2612_RoleHorseSkillInfoRespMessage(
			RoleInstance role, RoleHorse roleHorse, RoleHorseSkill horseSkill) {
		
		C2612_RoleHorseSkillInfoRespMessage respMsg = new C2612_RoleHorseSkillInfoRespMessage();
		
		String key = horseSkill.getSkillId() + Cat.underline + horseSkill.getLevel();
		HorseSkillLimit limit = GameContext.getHorseApp().getSkillLimit(key);
		
		Skill skill = GameContext.getSkillApp().getSkill(horseSkill.getSkillId());
		respMsg.setLevel(horseSkill.getLevel());
		respMsg.setMaxLevel((short)skill.getMaxLevel());
		if(null != skill){
			SkillDetail  skd = skill.getSkillDetail(horseSkill.getLevel());
			if(skd != null){
				respMsg.setSkillDes(skd.getDesc());
			}
		}
		
		//消耗物品数据
		List<GoodsLiteNamedItem> goodsList = new ArrayList<GoodsLiteNamedItem>();
		
		GoodsLiteNamedItem goodsItem = new GoodsLiteNamedItem();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(limit.getGoodsId());
		if(goodsBase != null){
			goodsItem.setGoodsId(limit.getGoodsId());
			goodsItem.setNum(limit.getGoodsNum());
			goodsItem.setGoodsImageId(goodsBase.getImageId());
			goodsItem.setGoodsLevel((byte)goodsBase.getLevel());
			goodsItem.setBindType(goodsBase.getBindType());
			goodsItem.setGoodsName(goodsBase.getName());
			goodsItem.setQualityType(goodsBase.getQualityType());
			goodsList.add(goodsItem);
		}
		
		//金币消耗
		List<AttriTypeValueItem> moneyList = new ArrayList<AttriTypeValueItem>();
		
		AttriTypeValueItem moneyItem = new AttriTypeValueItem();
		moneyItem.setAttriType(AttributeType.gameMoney.getType());
		moneyItem.setAttriValue(limit.getSilverMoney());
		moneyList.add(moneyItem);
		
		respMsg.setGoodsList(goodsList);
		respMsg.setMoneyList(moneyList);
		respMsg.setLuck(horseSkill.getLuck());
		int maxLuck = GameContext.getHorseApp().getMaxLuckProb(limit.getSchemeId());
		respMsg.setMaxLuck(maxLuck);
		
		if(roleHorse.getQuality() < limit.getQuality() && roleHorse.getStar() < limit.getStar()){
			HorseStar star = GameContext.getHorseApp().getHorseStar(limit.getQuality());
			respMsg.setInfo(GameContext.getI18n().messageFormat(TextId.HORSE_SKILL_QS_ERR,star.getColor()+star.getStar()));
		}else{
			if(horseSkill.getLevel() < skill.getMaxLevel()){
				respMsg.setFlag(true);
			}
		}
		
		return respMsg;
	}
	
	@Override
	public HorseProp getHorseProp(int horseId,byte quality,byte star){
		String key = horseId + Cat.underline + quality + Cat.underline + star;
		return GameContext.getHorseApp().getHorsePropById(key);
	}
	
	/**
	 * 获得升级本级数据
	 * @param horseId
	 * @param quality
	 * @param star
	 * @return
	 */
	@Override
	public HorseProp getNextHorsePorp(int horseId,byte quality,byte star){
		byte maxQuality = GameContext.getHorseApp().getHorseHighQuailty(horseId);
		byte maxStar = GameContext.getHorseApp().getHorseHighStar(horseId,quality);
		
		if(quality >= maxQuality && star >= maxStar){
			return null;
		}
		
		if(star < maxStar){
			star += 1;
		}else{
			quality += 1;
			star = GameContext.getHorseApp().getHorseLowStar(horseId,quality);
		}
		String key = horseId + Cat.underline + quality + Cat.underline + star;
		return GameContext.getHorseApp().getHorsePropById(key);
	}

	@Override
	public C2605_RoleHorseUpgradeInfoRespMessage sendC2605_RoleHorseUpgradeInfoRespMessage(
			RoleInstance role, RoleHorse roleHorse) {
		
		C2605_RoleHorseUpgradeInfoRespMessage respMsg = new C2605_RoleHorseUpgradeInfoRespMessage();
		
		//当前坐骑最大品质 
		byte maxQuality = GameContext.getHorseApp().getHorseHighQuailty(roleHorse.getHorseId());
		//当前坐骑最大星级
		byte maxStar = GameContext.getHorseApp().getHorseHighStar(roleHorse.getHorseId(),roleHorse.getQuality());
		
		if(roleHorse.getQuality() >= maxQuality && roleHorse.getStar() >= maxStar){
			respMsg.setFlag(true);
//			return respMsg;
		}
		
		respMsg.setHorseId(roleHorse.getHorseId());

		HorseProp horseProp = getHorseProp(roleHorse.getHorseId(), roleHorse.getQuality(), roleHorse.getStar());
		HorseProp nexthorseProp = getNextHorsePorp(roleHorse.getHorseId(), roleHorse.getQuality(), roleHorse.getStar());
		if(nexthorseProp != null){
			respMsg.setMaxProgress(nexthorseProp.getGoodsNum());
			respMsg.setProgress(roleHorse.getStarNum());
			respMsg.setNextQuality(nexthorseProp.getQuality());
			respMsg.setNextStar(nexthorseProp.getStar());
			
			respMsg.setNextHorseName(nexthorseProp.getHorseName());
			respMsg.setNextResId(nexthorseProp.getResId());
		}
		//下一级战力
		respMsg.setNextBattleScore(getBattleScore(roleHorse,true));
		
	  	List<AddAttriTypeValueItem> horsePropItemList = new ArrayList<AddAttriTypeValueItem>();
		
		List<AttriItem> attriItemItemList = getRoleHorseAllProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
		
		for(AttriItem item : attriItemItemList){
			AddAttriTypeValueItem propItem = new AddAttriTypeValueItem();
			propItem.setAttriType(item.getAttriTypeValue());
			propItem.setAttriValue((int)item.getValue());
			horsePropItemList.add(propItem);
		}
		
		byte newStar = roleHorse.getStar();
		byte newQuality = roleHorse.getQuality();
		if(!respMsg.isFlag()){
			if(newStar < maxStar){
				newStar += 1;
			}else{
				newQuality += 1;
				newStar = GameContext.getHorseApp().getHorseLowStar(roleHorse.getHorseId(), newQuality);
			}
		}
		//下阶坐骑最大星级
		maxStar = GameContext.getHorseApp().getHorseHighStar(roleHorse.getHorseId(), newQuality);
		respMsg.setMaxStar(maxStar);
		
		if(respMsg.isFlag()){
			newStar = GameContext.getHorseApp().getHorseLowStar(roleHorse.getHorseId(), newQuality);
			if(roleHorse.getStar() - newStar >= 0){
				newStar = (byte)(roleHorse.getStar() - 1);
			}else{
				newStar = GameContext.getHorseApp().getHorseHighStar(roleHorse.getHorseId(), (byte)(newQuality - 1));
				newQuality -= 1;
			}
		}
		
		//下一级附加值
		List<AttriItem> addAttriItemItemList = getRoleHorseAllProp(roleHorse.getHorseId(),newQuality,newStar);
		for(AttriItem addAttItem : addAttriItemItemList){
			for(AddAttriTypeValueItem value : horsePropItemList ){
				if(value.getAttriType() == addAttItem.getAttriTypeValue()){
					if(addAttItem.getValue() - value.getAttriValue() > 0){
						value.setAddAttriValue((int)(addAttItem.getValue() - value.getAttriValue()));
					}else{
						value.setAddAttriValue((int)(value.getAttriValue() - addAttItem.getValue()));
					}
					break;
				}
			}
		}
		
		respMsg.setHorsePropItemList(horsePropItemList);
		
		//下阶物品消耗
		GoodsLiteNamedItem goodsLiteItem = null;
		List<GoodsLiteNamedItem> goodsLiteItemList = new ArrayList<GoodsLiteNamedItem>();
		goodsLiteItem = new GoodsLiteNamedItem();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(horseProp.getGoodsId());
		goodsLiteItem.setGoodsId(goodsBase.getId());
		goodsLiteItem.setGoodsName(goodsBase.getName());
		goodsLiteItem.setBindType(horseProp.getBinded());
		if(nexthorseProp != null){
			goodsLiteItem.setNum(nexthorseProp.getGoodsNum());
		}
		goodsLiteItem.setQualityType(goodsBase.getQualityType());
		goodsLiteItem.setGoodsLevel((byte)goodsBase.getLevel());
		goodsLiteItemList.add(goodsLiteItem);
		respMsg.setGoodsList(goodsLiteItemList);
		return respMsg;
	}

	@Override
	public C2613_RoleHorseSkillListRespMessage sendC2613_RoleHorseSkillListRespMessage(
			RoleInstance role, RoleHorse roleHorse) {
		
		C2613_RoleHorseSkillListRespMessage respMsg = new C2613_RoleHorseSkillListRespMessage();
		
		List<RoleHorseSkill> horseSkillList = roleHorse.getSkillList();
		//获取技能数据
		for(RoleHorseSkill horseSkill : horseSkillList){
			HorseSkillItem skillItem = new HorseSkillItem();
			//获取技能数据
			Skill skill = GameContext.getSkillApp().getSkill(horseSkill.getSkillId());
			skillItem.setSkillId(horseSkill.getSkillId());
			if(null != skill){
				skillItem.setSkillImageId(skill.getIconId());
				skillItem.setSkillName(skill.getName());
				skillItem.setMaxLevel((short)skill.getMaxLevel());
				respMsg.getHorseSkillItemList().add(skillItem);
				skillItem.setFlag((byte)skill.getSkillApplyType().ordinal());
			}
			skillItem.setLevel(horseSkill.getLevel());
		}
		return respMsg;
	}
	
	@Override
	public Result useHorseGoods(RoleInstance role, RoleGoods roleGoods,boolean confirm) throws ServiceException{
		try {
			Result result = new Result();
			GoodsHorse goodsHorse = GameContext.getGoodsApp().getGoodsTemplate(
					GoodsHorse.class, roleGoods.getGoodsId());
			if (null == goodsHorse) {
				result.setInfo(GameContext.getI18n().getText(
						TextId.ERROR_INPUT));
				return result;
			}
			
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getRoleHorse(role.getIntRoleId(),goodsHorse.getHorseId());
			if(null != roleHorse){
				//判断是否已经拥有了此英雄
				//如果有则走英雄物品与影子的兑换逻辑
				return horseGoodsToShadow(role, roleGoods, confirm, goodsHorse) ;
			}
			
			GoodsResult gr = GameContext.getUserGoodsApp()
					.deleteForBagByInstanceId(role, roleGoods.getId(), 1,
							OutputConsumeType.horse_goods_use);
			if (!gr.isSuccess()) {
				return gr;
			}
			return this.useHorseTemplate(role, goodsHorse);
		}catch(Exception ex){
			throw new ServiceException("useHorseGoods error",ex) ;
		}
	}

	@Override
	public Result useHorseTemplate(RoleInstance role, GoodsHorse goodsHorse) throws ServiceException{
		try {
			this.addRoleHorse(role,goodsHorse.getHorseId());
			return new Result().success();
		}catch(Exception ex){
			throw new ServiceException("useHorseTemplate error",ex) ;
		}
	}
	
	private Result horseGoodsToShadow(RoleInstance role, 
			RoleGoods roleGoods,boolean confirm,GoodsHorse goodsHorse){
		if(confirm){
			int count = goodsHorse.getShadowNum() * roleGoods.getCurrOverlapCount();
			//直接转换为影子
			List<GoodsOperateBean> addList = Lists.newArrayList(
					new GoodsOperateBean(goodsHorse.getShadowId(),count,
							roleGoods.getBind()));
			GoodsResult gr = GameContext.getUserGoodsApp().addDelGoodsForBag(role, addList, 
					OutputConsumeType.horse_goods_to_shadow,
					roleGoods,roleGoods.getCurrOverlapCount(), null, OutputConsumeType.goods_use);
			if(!gr.isSuccess()){
				return gr ;
			}
			Result result = new Result();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsHorse.getShadowId());
			String tips = GameContext.getI18n().messageFormat(TextId.HORSE_GOODS_TO_SHADOW_SUCCESS_TIPS,
					goodsHorse.getName(),gb.getName(),String.valueOf(count));
			result.setInfo(tips);
			result.success();
			return result ;
		}
		//二次确认提示用户是否转换为影子
		UseResult result = new UseResult();
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsHorse.getShadowId());
		String tips = GameContext.getI18n().messageFormat(TextId.HORSE_GOODS_TO_SHADOW_CONFIRM_TIPS,
				goodsHorse.getName(),gb.getName(),String.valueOf(goodsHorse.getShadowNum() * roleGoods.getCurrOverlapCount()));
		result.success();
		result.setMustConfirm(true);
		result.setInfo(tips);
		result.setConfirmCmdId(HORSE_GOODS_TO_SHADOW_CMDID);
		//物品实例ID
		result.setConfirmInfo(roleGoods.getId());
		return result;
	}

	Comparator<RoleHorseItem> roleHorseItemComparator = new Comparator<RoleHorseItem>(){
		@Override
		public int compare(RoleHorseItem h1, RoleHorseItem h2) {
			//1.出战
			if(h1.getState() > h2.getState()){
				return -1;
			}
			if(h1.getState() < h2.getState()){
				return 1;
			}
			//拥有
			if(h1.getFlag() > h2.getFlag()){
				return -1;
			}
			if(h1.getFlag() < h2.getFlag()){
				return 1;
			}
			//品质
			if(h1.getQuality() > h2.getQuality()){
				return -1;
			}
			if(h1.getQuality() < h2.getQuality()){
				return 1;
			}
			//星
			if(h1.getStar() > h2.getStar()){
				return -1;
			}
			if(h1.getStar() < h2.getStar()){
				return 1;
			}
			return 0;
		}
	} ;


	@Override
	public void broadcastHorse(RoleInstance role, int resId, int horseId, byte state) {
		//视野通知
		C2609_RoleHorseUpgradeNotifyMessage msg = new C2609_RoleHorseUpgradeNotifyMessage();
		msg.setRoleId(role.getIntRoleId());
		msg.setHorseId(horseId);
		msg.setState(state);
		msg.setResId(resId);
		broadcast(role,msg);
	}
	
	
}
