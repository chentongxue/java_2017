package com.game.draco.app.hint;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hint.config.HintConfig;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseProp;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.pet.config.PetStarUpConfig;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.HintGoodsTermItem;
import com.game.draco.message.item.HintHeroRulesItem;
import com.game.draco.message.item.HintRulesItem;
import com.game.draco.message.item.HintSkillRulesItem;
import com.game.draco.message.item.HintTimeNotifyItem;
import com.game.draco.message.item.HintUIItem;
import com.game.draco.message.push.C1171_HintAddNotifyMessage;
import com.game.draco.message.push.C1172_HintRemoveNotifyMessage;
import com.game.draco.message.push.C1173_HintRulesRespMessage;
import com.game.draco.message.push.C1174_HintUIRespMessage;
import com.game.draco.message.push.C1175_HintNotifyRulesChangeRespMessage;
import com.game.draco.message.push.C1176_HintTimeNotifyMessage;
import com.game.draco.message.push.C1177_HintTimeUpdateMessage;
import com.game.draco.message.push.C1178_HintNotifySkillChangeRespMessage;
import com.game.draco.message.response.C1170_HintListRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class HintAppImpl implements HintApp {
	
	public static final byte HINT_HERO = 0;
	public static final byte HINT_HORSE = 1;
	public static final byte HINT_PET = 2;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<HintSupport> hintSupportList;
	private List<HintTimeSupport> hintTimeSupportList;
	private Map<Byte, HintConfig> hintUIMap = Maps.newHashMap();
	
	@Override
	public void pushHintListMessage(RoleInstance role) {
		try {
			if(null == this.hintSupportList){
				return;
			}
			Set<Byte> idSet = Sets.newHashSet();
			for(HintSupport support : this.hintSupportList){
				if(null == support){
					continue;
				}
				Set<HintType> hintIdSet = support.getHintTypeSet(role);
				if(Util.isEmpty(hintIdSet)){
					continue;
				}
				for(HintType hintId : hintIdSet){
					if(null == hintId){
						continue;
					}
					idSet.add(hintId.getId());
				}
			}
			int size = idSet.size();
			if(size <= 0){
				return;
			}
			byte[] ids = new byte[size];
			int i = 0;
			for(byte id : idSet){
				ids[i] = id;
				i ++;
			}
			C1170_HintListRespMessage message = new C1170_HintListRespMessage();
			message.setIds(ids);
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error("HintApp.pushHintListMessage error: ", e);
		}
	}
	
	@Override
	public void hintChange(RoleInstance role, HintType hintId, boolean hasHint) {
		try {
			if(hasHint){
				this.hintAddNotify(role, hintId);
			}else{
				this.hintRemoveNotify(role, hintId);
			}
		} catch (Exception e) {
			this.logger.error("HintApp.hintChange error: ", e);
		}
	}
	
	/**
	 * 通知新增特效
	 * @param role
	 * @param hintId
	 */
	private void hintAddNotify(RoleInstance role, HintType hintId) {
		try {
			C1171_HintAddNotifyMessage message = new C1171_HintAddNotifyMessage();
			message.setId(hintId.getId());
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error("HintApp.addHint error: ", e);
		}
	}

	/**
	 * 通知特效消失
	 * @param role
	 * @param hintId
	 */
	private void hintRemoveNotify(RoleInstance role, HintType hintId) {
		try {
			C1172_HintRemoveNotifyMessage message = new C1172_HintRemoveNotifyMessage();
			message.setId(hintId.getId());
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error("HintApp.removeHint error: ", e);
		}
	}
	
	/**
	 * 发消息
	 * @param role
	 * @param message
	 */
	protected void sendMessage(RoleInstance role, Message message){
		GameContext.getMessageCenter().sendSysMsg(role, message);
	}

	@Override
	public void sysPushHintMsg() {
		try {
			//没有用户在线
			if(GameContext.getOnlineCenter().onlineUserSize() <= 0){
				return;
			}
			for(RoleInstance role : GameContext.getOnlineCenter().getAllOnlineRole()){
				if(null == role){
					continue;
				}
				this.pushHintListMessage(role);
			}
		} catch (Exception e) {
			this.logger.error("HintApp.sysPushHintMsg error: ", e);
		}
	}
	
	public List<HintSupport> getHintSupportList() {
		return hintSupportList;
	}

	public void setHintSupportList(List<HintSupport> hintSupportList) {
		this.hintSupportList = hintSupportList;
	}
	
	public List<HintTimeSupport> getHintTimeSupportList() {
		return this.hintTimeSupportList;
	}

	public void setHintTimeSupportList(List<HintTimeSupport> hintTimeSupportList) {
		this.hintTimeSupportList = hintTimeSupportList;
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		this.loadHintConfig(xlsPath);
	}

	@Override
	public void stop() {
	}
	
	private void loadHintConfig(String xlsPath) {
		String fileName = XlsSheetNameType.hint_rules.getXlsName();
		String sheetName = XlsSheetNameType.hint_rules.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.hintUIMap = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, HintConfig.class);
			for (HintConfig config : this.hintUIMap.values()) {
				if (null == config) {
					continue;
				}
			}
		} catch (Exception e) {
			logger.error(fileInfo, e);
			Log4jManager.checkFail();
		}
	}

	@Override
	public void pushHintRulesMessage(RoleInstance role) {
		C1173_HintRulesRespMessage message = new C1173_HintRulesRespMessage();
		message.setHintRulesList(this.getHintRulesList(role));
		message.setHintSkillRulesList(this.getHeroSkillRulesList(role));
		this.sendMessage(role, message);
	}
	
	private List<HintRulesItem> getHintRulesList(RoleInstance role) {
		List<HintRulesItem> rulesList = Lists.newArrayList();
		rulesList.addAll(this.getHeroHintRulesList(role));
		rulesList.addAll(this.getHorseHintRulesList(role));
		rulesList.addAll(this.getPetHintRulesList(role));
		return rulesList;
	}
	
	private List<HintRulesItem> getHeroHintRulesList(RoleInstance role) {
		List<HintRulesItem> hintRulesList = Lists.newArrayList();
		Collection<RoleHero> heroList = GameContext.getUserHeroApp().getAllRoleHero(role.getRoleId());
		if (Util.isEmpty(heroList)) {
			return hintRulesList;
		}
		for (RoleHero hero : heroList) {
			if (null == hero) {
				continue;
			}
			GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, hero.getHeroId());
			if (null == goodsHero) {
				continue;
			}
			HintRulesItem item = new HintRulesItem();
			item.setType(HINT_HERO);
			item.setTargetId(goodsHero.getId());
			// 所需物品及数量
			List<HintGoodsTermItem> hintGoodsTermList = Lists.newArrayList();
			short needNumber = (short) GameContext.getHeroApp().getHeroQualityUpgrade(hero.getQuality(), hero.getStar()).getNextShadowNum();
			// 如果升星到最高等级，则消耗物品数量为最大值
			if (GameContext.getHeroApp().isReachMaxQuality(hero)) {
				needNumber = Short.MAX_VALUE;
			}
			hintGoodsTermList.add(new HintGoodsTermItem(goodsHero.getShadowId(), needNumber));
			item.setHintGoodsTermList(hintGoodsTermList);
			hintRulesList.add(item);
		}
		return hintRulesList;
	}
	
	private List<HintRulesItem> getHorseHintRulesList(RoleInstance role) {
		List<HintRulesItem> hintRulesList = Lists.newArrayList();
		Map<Integer, RoleHorse> horseMap = GameContext.getRoleHorseApp().getAllRoleHorseByRoleId(Integer.parseInt(role.getRoleId()));
		if (Util.isEmpty(horseMap)) {
			return hintRulesList;
		}
		for (RoleHorse horse : horseMap.values()) {
			if (null == horse) {
				continue;
			}
			HorseBase base = GameContext.getHorseApp().getHorseBaseById(horse.getHorseId());
			if(base == null){
				continue;
			}
			HorseProp horseProp = GameContext.getRoleHorseApp().getNextHorsePorp(horse.getHorseId(),
					horse.getQuality(),horse.getStar());
			if(null == horseProp){
				continue ;
			}
			HintRulesItem item = new HintRulesItem();
			item.setType(HINT_HORSE);
			item.setTargetId(horse.getHorseId());
			// 所需物品及数量
			List<HintGoodsTermItem> hintGoodsTermList = Lists.newArrayList();
			// 所需物品及数量
			short needNumber = (short) horseProp.getGoodsNum();
			if (needNumber <= 0) {
				continue;
			}
			// 如果升星到最高等级，则消耗物品数量为最大值
			if (GameContext.getRoleHorseApp().isReachMaxStar(horse)) {
				needNumber = Short.MAX_VALUE;
			}
			hintGoodsTermList.add(new HintGoodsTermItem(horseProp.getGoodsId(), needNumber));
			item.setHintGoodsTermList(hintGoodsTermList);
			hintRulesList.add(item);
		}
		return hintRulesList;
	}
	
	private List<HintRulesItem> getPetHintRulesList(RoleInstance role) {
		List<HintRulesItem> hintRulesList = Lists.newArrayList();
		Map<Integer, RolePet> petMap = GameContext.getUserPetApp().getAllRolePet(role.getRoleId());
		if (Util.isEmpty(petMap)) {
			return hintRulesList;
		}
		for (RolePet pet : petMap.values()) {
			if (null == pet) {
				continue;
			}
			GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, pet.getPetId());
			if (null == goodsPet) {
				continue;
			}
			HintRulesItem item = new HintRulesItem();
			item.setType(HINT_PET);
			item.setTargetId(goodsPet.getId());
			List<HintGoodsTermItem> hintGoodsTermList = Lists.newArrayList();
			// 所需物品及数量
			PetStarUpConfig config = GameContext.getPetApp().getPetStarUpConfig(pet.getQuality(), pet.getStar());
			short needNumber = 0;
			if (null == config) {
				needNumber = Short.MAX_VALUE;
			} else {
				needNumber = (short) config.getShadowNumber();
			}
			// 如果升星到最高等级，则消耗物品数量为最大值
			if (GameContext.getPetApp().isPetMaxStar(pet)) {
				needNumber = Short.MAX_VALUE;
			}
			hintGoodsTermList.add(new HintGoodsTermItem(goodsPet.getShadowId(), needNumber));
			item.setHintGoodsTermList(hintGoodsTermList);
			hintRulesList.add(item);
		}
		return hintRulesList;
	}
	
	private List<HintHeroRulesItem> getHeroSkillRulesList(RoleInstance role) {
		List<HintHeroRulesItem> skillRulesList = Lists.newArrayList();
		Collection<RoleHero> heroList = GameContext.getUserHeroApp().getAllRoleHero(role.getRoleId());
		if (Util.isEmpty(heroList)) {
			return skillRulesList;
		}
		for (RoleHero hero : heroList) {
			if (null == hero) {
				continue;
			}
			GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, hero.getHeroId());
			if (null == goodsHero) {
				continue;
			}
			List<HintSkillRulesItem> hintSkillRulesList = Lists.newArrayList();
			for (short skillId : goodsHero.getSkillIdList()) {
				HintSkillRulesItem skillItem = new HintSkillRulesItem();
				skillItem.setSkillId(skillId);
				Skill skill = GameContext.getSkillApp().getSkill(skillId);
				int skillLevel = 0;
				RoleSkillStat skillStat = hero.getSkillMap().get(skillId);
				if (null != skillStat) {
					skillLevel = skillStat.getSkillLevel();
				}
				SkillDetail skillDetail = skill.getSkillDetail(skillLevel + 1);
				if (null == skillDetail) {
					skillItem.setLevel(Short.MAX_VALUE);
					hintSkillRulesList.add(skillItem);
					continue;
				}
				skillItem.setLevel((short) skillDetail.getLevel());
				hintSkillRulesList.add(skillItem);
			}
			HintHeroRulesItem item = new HintHeroRulesItem();
			item.setHeroId(hero.getHeroId());
			item.setHintSkillRulesList(hintSkillRulesList);
			skillRulesList.add(item);
		}
		return skillRulesList;
	}

	@Override
	public void pushHintUITreeMessage(RoleInstance role) {
		C1174_HintUIRespMessage message = new C1174_HintUIRespMessage();
		message.setHintUIList(this.getHintUIList());
		this.sendMessage(role, message);
	}
	
	private List<HintUIItem> getHintUIList() {
		if (Util.isEmpty(this.hintUIMap)) {
			return null;
		}
		List<HintUIItem> list = Lists.newArrayList();
		for (HintConfig config : this.hintUIMap.values()) {
			if (null == config) {
				continue;
			}
			HintUIItem item = new HintUIItem();
			item.setId(config.getId());
			item.setUiTree(config.getUiTree());
			item.setParentId(config.getParentId());
			list.add(item);
		}
		return list;
	}

	@Override
	public void pushHintRulesChange(RoleInstance role, HintRulesItem item) {
		C1175_HintNotifyRulesChangeRespMessage message = new C1175_HintNotifyRulesChangeRespMessage();
		message.setHintRulesItem(item);
		this.sendMessage(role, message);
	}

	@Override
	public void pushHintTimeListMessage(RoleInstance role) {
		try {
			if(null == this.hintTimeSupportList){
				return;
			}
			List<HintTimeNotifyItem> list = Lists.newArrayList();
			for(HintTimeSupport support : this.hintTimeSupportList){
				if(null == support){
					continue;
				}
				List<HintTimeNotifyItem> hintItemList = support.getHintTimeNotifyList(role);
				if(Util.isEmpty(hintItemList)){
					continue;
				}
				list.addAll(hintItemList);
			}
			int size = list.size();
			if(size <= 0){
				return;
			}
			C1176_HintTimeNotifyMessage message = new C1176_HintTimeNotifyMessage();
			message.setHintTimeNotifyList(list);
			this.sendMessage(role, message);
		} catch (Exception e) {
			this.logger.error("HintApp.pushHintListMessage error: ", e);
		}
		
	}

	@Override
	public void pushHintTimeChangeMessage(RoleInstance role, HintTimeNotifyItem hintTimeNotifyItem) {
		C1177_HintTimeUpdateMessage message = new C1177_HintTimeUpdateMessage();
		message.setHintTimeNotifyItem(hintTimeNotifyItem);
		this.sendMessage(role, message);
	}

	@Override
	public void pushHintSkillChange(RoleInstance role, HintHeroRulesItem hintHeroRulesItem) {
		C1178_HintNotifySkillChangeRespMessage message = new C1178_HintNotifySkillChangeRespMessage();
		message.setHintHeroRulesItem(hintHeroRulesItem);
		this.sendMessage(role, message);
	}

}
