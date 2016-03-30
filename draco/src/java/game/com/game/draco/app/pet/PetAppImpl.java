package com.game.draco.app.pet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.app.goods.behavior.result.UseResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.QualityType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.PetBehavior;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hint.HintAppImpl;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.app.pet.config.AttributePetBornConfig;
import com.game.draco.app.pet.config.AttributePetLevelConfig;
import com.game.draco.app.pet.config.AttributePetQualityConfig;
import com.game.draco.app.pet.config.AttributePetRateConfig;
import com.game.draco.app.pet.config.AttributePetTypeConfig;
import com.game.draco.app.pet.config.PetAttribute;
import com.game.draco.app.pet.config.PetLevelUpConfig;
import com.game.draco.app.pet.config.PetListConfig;
import com.game.draco.app.pet.config.PetPvpConfig;
import com.game.draco.app.pet.config.PetPvpRefresh;
import com.game.draco.app.pet.config.PetStarHoleConfig;
import com.game.draco.app.pet.config.PetStarUpConfig;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.GoodsPetAid;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.pet.domain.RolePetBattleList;
import com.game.draco.app.pet.domain.RolePetShow;
import com.game.draco.app.pet.domain.RolePetStatus;
import com.game.draco.app.pet.vo.Constant;
import com.game.draco.app.pet.vo.PetMosaicResult;
import com.game.draco.app.pet.vo.PetPvpInfoListResult;
import com.game.draco.app.pet.vo.PetSwallowResult;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.target.cond.TargetCondType;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.HeroBattleItem;
import com.game.draco.message.item.HintGoodsTermItem;
import com.game.draco.message.item.HintRulesItem;
import com.game.draco.message.item.PetBattleItem;
import com.game.draco.message.item.PetListItem;
import com.game.draco.message.item.PetMosaicRuneItem;
import com.game.draco.message.item.PetPvpRoleInfoItem;
import com.game.draco.message.item.PetSwallowItem;
import com.game.draco.message.item.SkillSimpleItem;
import com.game.draco.message.push.C0004_TipTitleNotifyMessage;
import com.game.draco.message.push.C1667_PetOnBattleNotifyMessage;
import com.game.draco.message.push.C1668_PetOffBattleNotifyMessage;
import com.game.draco.message.request.C1661_PetGoodsToShadowReqMessage;
import com.game.draco.message.response.C1662_PetPvpInfoListRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PetAppImpl implements PetApp {

	private final static byte ZERO = 0;
	private final static byte ONE = 1;
	private final static byte TWO = 2;
	private final static int FIVE = 5;
	private final static int TEN = 10;
	private final static int TWENTY = 20;
	private final static int MAX_LEVEL = 200;
	private final static short PET_GOODS_TO_SHADOW_CMDID = new C1661_PetGoodsToShadowReqMessage().getCommandId();

	private Logger logger = LoggerFactory.getLogger(this.getClass());// 日志记录
	private List<PetListConfig> petListConfig = Lists.newArrayList();// 宠物图签配置
	private Map<String, PetLevelUpConfig> petLevelUpConfig = Maps.newHashMap();// 升级配置
	private Map<String, PetStarUpConfig> petStarUpConfig = Maps.newHashMap();// 升星配置
	private Map<String, PetStarHoleConfig> petStarHoleConfig = Maps.newHashMap();// 星级孔数配置
	private Map<String, AttributePetRateConfig> attriPetRateConfig = Maps.newHashMap();// 宠物属性系数配置
	private AttributePetTypeConfig attriPetTypeConfig = new AttributePetTypeConfig();// 属性系数配置
	private Map<String, AttributePetLevelConfig> attriPetLevelConfig = Maps.newHashMap();// 等级属性系数配置
	private Map<String, AttributePetBornConfig> attriPetBornConfig = Maps.newHashMap();// 宠物初始属性系数配置
	private Map<String, AttributePetQualityConfig> attriPetQualityConfig = Maps.newHashMap();// 成长品质属性系数配置
	private PetPvpConfig petPvpConfig = new PetPvpConfig();// 宠物抢夺配置
	private Map<Integer, PetPvpRefresh> petPvpRefreshConfig = Maps.newHashMap();// 宠物抢夺信息刷新配置

	public static List<AttributeType> attributeTypeList = Lists.newArrayList(
			AttributeType.petAtk, AttributeType.atk, AttributeType.maxHP,
			AttributeType.rit, AttributeType.breakDefense,
			AttributeType.critAtk, AttributeType.critRit, AttributeType.dodge,
			AttributeType.hit);

	private PetLevelUpConfig getPetLevelUpConfig(byte quality, int level) {
		return this.petLevelUpConfig.get(quality + "_" + String.valueOf(level));
	}

	@Override
	public AttributePetRateConfig getAttributePetRateConfig(int petId) {
		return this.attriPetRateConfig.get(String.valueOf(petId));
	}

	@Override
	public AttributePetTypeConfig getAttributePetTypeConfig() {
		return this.attriPetTypeConfig;
	}

	@Override
	public AttributePetBornConfig getAttributePetBornConfig(byte quality, byte star) {
		return this.attriPetBornConfig.get(quality + "_" + star);
	}

	@Override
	public AttributePetQualityConfig getAttributePetQualityConfig(byte quality, byte star) {
		return this.attriPetQualityConfig.get(quality + "_" + star);
	}

	@Override
	public AttributePetLevelConfig getAttributePetLevelConfig(int level) {
		return this.attriPetLevelConfig.get(String.valueOf(level));
	}

	@Override
	public PetStarUpConfig getPetStarUpConfig(byte quality, byte star) {
		return this.petStarUpConfig.get(quality + "_" + star);
	}

	private PetStarHoleConfig getPetStarHoleConfig(byte star) {
		return this.petStarHoleConfig.get(String.valueOf(star));
	}

	private PetPvpRefresh getPetPvpRefreshConfig(int level) {
		return this.petPvpRefreshConfig.get(level);
	}

	@Override
	public PetPvpConfig getPetPvpConfig() {
		return this.petPvpConfig;
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		this.loadPetListConfig(xlsPath);
		this.loadPetLevelUpConfig(xlsPath);
		this.loadPetStarUpConfig(xlsPath);
		this.loadAttributePetRateConfig(xlsPath);
		this.loadAttributePetTypeConfig(xlsPath);
		this.loadAttributePetBornConfig(xlsPath);
		this.loadAttributePetLevelConfig(xlsPath);
		this.loadAttributePetQualityConfig(xlsPath);
		this.loadPetStarHoleConfig(xlsPath);
		this.loadPetPvpPvpConfig(xlsPath);
		this.loadPetPvpRefreshConfig(xlsPath);
	}

	@Override
	public void stop() {
	}

	/**
	 * 加载配置
	 */
	// 加载宠物图鉴配置
	private void loadPetListConfig(String xlsPath) {
		String fileName = XlsSheetNameType.pet_list.getXlsName();
		String sheetName = XlsSheetNameType.pet_list.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.petListConfig = XlsPojoUtil.sheetToList(xlsPath + fileName, sheetName, PetListConfig.class);
			for (PetListConfig listConfig : petListConfig) {
				listConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载升级配置
	private void loadPetLevelUpConfig(String xlsPath) {
		String fileName = XlsSheetNameType.pet_level_up.getXlsName();
		String sheetName = XlsSheetNameType.pet_level_up.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.petLevelUpConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, PetLevelUpConfig.class);
			for (PetLevelUpConfig levelConfig : this.petLevelUpConfig.values()) {
				if (null == levelConfig) {
					continue;
				}
				levelConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载升星配置
	private void loadPetStarUpConfig(String xlsPath) {
		String fileName = XlsSheetNameType.pet_star_up.getXlsName();
		String sheetName = XlsSheetNameType.pet_star_up.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.petStarUpConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, PetStarUpConfig.class);
			for (PetStarUpConfig starConfig : this.petStarUpConfig.values()) {
				if (starConfig == null) {
					continue;
				}
				starConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载星级孔数配置
	private void loadPetStarHoleConfig(String xlsPath) {
		String fileName = XlsSheetNameType.pet_star_hole.getXlsName();
		String sheetName = XlsSheetNameType.pet_star_hole.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.petStarHoleConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, PetStarHoleConfig.class);
			for (PetStarHoleConfig starConfig : this.petStarHoleConfig.values()) {
				if (starConfig == null) {
					continue;
				}
				starConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载宠物属性配置
	private void loadAttributePetRateConfig(String xlsPath) {
		String fileName = XlsSheetNameType.attribute_pet_rate.getXlsName();
		String sheetName = XlsSheetNameType.attribute_pet_rate.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.attriPetRateConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, AttributePetRateConfig.class);
			for (AttributePetRateConfig rateConfig : this.attriPetRateConfig.values()) {
				if (null == rateConfig) {
					continue;
				}
				rateConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载属性系数配置
	private void loadAttributePetTypeConfig(String xlsPath) {
		String fileName = XlsSheetNameType.attribute_pet_type.getXlsName();
		String sheetName = XlsSheetNameType.attribute_pet_type.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.attriPetTypeConfig = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, AttributePetTypeConfig.class);
			this.attriPetTypeConfig.init();
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载出生属性系数配置
	private void loadAttributePetBornConfig(String xlsPath) {
		String fileName = XlsSheetNameType.attribute_pet_born.getXlsName();
		String sheetName = XlsSheetNameType.attribute_pet_born.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.attriPetBornConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, AttributePetBornConfig.class);
			for (AttributePetBornConfig bornConfig : this.attriPetBornConfig.values()) {
				if (null == bornConfig) {
					continue;
				}
				bornConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载品质属性系数配置
	private void loadAttributePetQualityConfig(String xlsPath) {
		String fileName = XlsSheetNameType.attribute_pet_quality.getXlsName();
		String sheetName = XlsSheetNameType.attribute_pet_quality.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.attriPetQualityConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, AttributePetQualityConfig.class);
			for (AttributePetQualityConfig qualityConfig : this.attriPetQualityConfig.values()) {
				if (null == qualityConfig) {
					continue;
				}
				qualityConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载等级属性系数配置
	private void loadAttributePetLevelConfig(String xlsPath) {
		String fileName = XlsSheetNameType.attribute_pet_level.getXlsName();
		String sheetName = XlsSheetNameType.attribute_pet_level.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.attriPetLevelConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, AttributePetLevelConfig.class);
			for (AttributePetLevelConfig levelConfig : this.attriPetLevelConfig.values()) {
				if (null == levelConfig) {
					continue;
				}
				levelConfig.init(fileInfo);
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载宠物抢夺配置
	private void loadPetPvpPvpConfig(String xlsPath) {
		String fileName = XlsSheetNameType.pet_pvp_config.getXlsName();
		String sheetName = XlsSheetNameType.pet_pvp_config.getSheetName();
		try {
			this.petPvpConfig = XlsPojoUtil.getEntity(xlsPath + fileName, sheetName, PetPvpConfig.class);
			this.petPvpConfig.init();

			if (null == petPvpConfig) {
				Log4jManager.CHECK.error("PetApp not config the PetPvpConfig,file=" + xlsPath + fileName + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}

			String mapId = petPvpConfig.getMapId();
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if (null == map) {
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("PetApp The map is not exist. mapId = " + mapId + ",file=" + xlsPath + fileName + " sheet=" + sheetName);
			}
			// 将地图逻辑修改为pet类型
			if (!map.getMapConfig().changeLogicType(MapLogicType.pet)) {
				Log4jManager.CHECK.error("PetApp The map logic type config error. mapId= " + fileName);
				Log4jManager.checkFail();
			}
		} catch (Exception ex) {
			Log4jManager.CHECK.error("loadExel error : sourceFile = " + fileName + " sheetName =" + sheetName, ex);
			Log4jManager.checkFail();
		}
	}

	// 加载宠物抢夺列表刷新配置
	private void loadPetPvpRefreshConfig(String xlsPath) {
		String fileName = XlsSheetNameType.pet_pvp_refresh.getXlsName();
		String sheetName = XlsSheetNameType.pet_pvp_refresh.getSheetName();
		String fileInfo = "load excel error:fileName=" + fileName + ",sheetName=" + sheetName + ".";
		try {
			this.petPvpRefreshConfig = XlsPojoUtil.sheetToGenericMap(xlsPath + fileName, sheetName, PetPvpRefresh.class);
			for (PetPvpRefresh refreshConfig : this.petPvpRefreshConfig.values()) {
				if (null == refreshConfig) {
					continue;
				}
			}
		} catch (Exception ex) {
			logger.error(fileInfo, ex);
			Log4jManager.checkFail();
		}
	}

	// 上线
	@Override
	public int onLogin(RoleInstance role, Object context) {
		String roleId = role.getRoleId();
		// 加载宠物列表
		List<RolePet> petList = GameContext.getBaseDAO().selectList(RolePet.class, RolePet.MASTER_ID, roleId);
		for (RolePet pet : petList) {
			pet.setQuality(this.getPetQuality(pet));
			pet.setRole(role);
			// 设置宠物campId,用来在战斗中确定势力关系
			pet.setCampId(role.getCampId());
			// 初始化行为类
			pet.setBehavior(new PetBehavior(pet));
			GameContext.getUserPetApp().addRolePet(roleId, pet);
			this.initSkill(pet);
		}
		// 加载状态信息
		RolePetStatus status = GameContext.getPetStorage().getRolePetStatus(roleId);
		if (null == status) {
			status = new RolePetStatus();
			status.setRoleId(roleId);
		}
		GameContext.getUserPetApp().addRolePetStatus(roleId, status);
		RolePet onBattle = GameContext.getUserPetApp().getOnBattleRolePet(roleId);
		if (null != onBattle) {
			onBattle.setOnBattle(TWO);
			this.reCalct(onBattle);
		}
		return 1;
	}

	public void initSkill(RolePet pet) {
		// 普通攻击
		GoodsPet template = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, pet.getPetId());
		if (null == template) {
			logger.error("hero template not exist,heroId=" + pet.getPetId());
			return;
		}
		this.initSkill(pet, template.getCommonSkill());
		for (short skillId : template.getSkillList()) {
			this.initSkill(pet, skillId);
		}
	}

	private void initSkill(RolePet pet, short skillId) {
		RoleSkillStat stat = pet.getSkillMap().get(skillId);
		if (null != stat) {
			return;
		}
		stat = new RoleSkillStat();
		stat.setSkillId(skillId);
		stat.setSkillLevel(this.getPetSkillLevel(pet, skillId));
		stat.setRoleId("PET_" + pet.getPetId());
		stat.setLastProcessTime(this.getLastProcessTimeFromStore(stat));
		pet.getSkillMap().put(skillId, stat);
	}

	private long getLastProcessTimeFromStore(RoleSkillStat stat) {
		return 0;
	}

	// 下线
	@Override
	public int onLogout(RoleInstance role, Object context) {
		String roleId = role.getRoleId();
		try {
			this.savaRolePetStatus(roleId);
			this.saveShowRolePet(roleId);
			if (null != this.getBattleRolePet(roleId)) {
				GameContext.getPetStorage().saveRolePetBattle(roleId, role.getBattleScore());
			}
			this.onCleanup(roleId, context);
		} catch (Exception ex) {
			logger.error("pet app petstorage save pets error,roleId=" + roleId, ex);
			return 0;
		}
		return 1;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		try {
			GameContext.getUserPetApp().cleanRolePetDate(roleId);
		} catch (Exception ex) {
			logger.error("Pet app onCleanup error!" + roleId, ex);
			return 0;
		}
		return 1;
	}

	// 下线保存宠物状态
	private void savaRolePetStatus(String roleId) {
		RolePetStatus status = GameContext.getUserPetApp().getRolePetStatus(roleId);
		GameContext.getPetStorage().saveRolePetStatus(status);
	}

	// 下线保存出战宠物信息
	private void saveShowRolePet(String roleId) {
		RolePet onBattle = GameContext.getUserPetApp().getOnBattleRolePet(roleId);
		if (null == onBattle) {
			GameContext.getPetStorage().saveShowRolePet(roleId, null);
			return;
		}
		RolePetShow petShow = new RolePetShow();
		petShow.setPetId(onBattle.getPetId());
		petShow.setLevel(onBattle.getLevel());
		petShow.setQuality(onBattle.getQuality());
		petShow.setStar(onBattle.getStar());
		petShow.setBattleScore(onBattle.getScore());
		petShow.setMosaicRuneList(onBattle.getMosaicRuneList());
		GameContext.getPetStorage().saveShowRolePet(roleId, petShow);
	}

	// 获得抢夺宠物
	@Override
	public RolePet getBattleRolePet(String roleId) {
		RolePet battlePet = GameContext.getUserPetApp().getOnBattleRolePet(roleId);
		if (null == battlePet) {
			return this.getMaxScoreRolePet(roleId);
		}
		return battlePet;
	}

	// 得到战力最高的宠物
	private RolePet getMaxScoreRolePet(String roleId) {
		Map<Integer, RolePet> petMap = GameContext.getUserPetApp().getAllRolePet(roleId);
		if (Util.isEmpty(petMap)) {
			return null;
		}
		RolePet battlePet = null;
		for (RolePet pet : petMap.values()) {
			if (null == pet) {
				continue;
			}
			if (null == battlePet) {
				battlePet = pet;
				continue;
			}
			if (pet.getScore() > battlePet.getScore()) {
				battlePet = pet;
			}
		}
		return battlePet;
	}

	// 宠物图鉴
	@Override
	public List<PetListItem> getPetList(RoleInstance role) {
		String roleId = role.getRoleId();
		List<PetListItem> petListItem = Lists.newArrayList();
		for (PetListConfig listConfig : this.petListConfig) {
			int petId = listConfig.getPetId();
			GoodsPet petTemplate = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, listConfig.getPetId());
			if (null == petTemplate) {
				continue;
			}
			PetListItem item = new PetListItem();
			item.setPetId(petTemplate.getId());
			item.setName(petTemplate.getName());
			item.setImageId(petTemplate.getImageId());
			item.setQuality(petTemplate.getQualityType());

			// 是否已拥有该宠物
			RolePet rolePet = GameContext.getUserPetApp().getRolePet(roleId, petId);
			// 未拥有宠物
			if (null == rolePet) {
				item.setStatus(ZERO);
				item.setLevel(petTemplate.getLevel());
				item.setShadowId(petTemplate.getShadowId());
				item.setShadowNum(petTemplate.getShadowNum());
				item.setStar(petTemplate.getStar());
				item.setDesc(petTemplate.getDesc());
				item.setSwallowExp(this.getSwallowExp(petId, null));
				petListItem.add(item);
				continue;
			}

			// 已拥有宠物
			// 判断是否为出战宠物
			if (GameContext.getUserPetApp().isOnBattle(roleId, listConfig.getPetId())) {
				item.setStatus(TWO);
			} else {
				item.setStatus(ONE);
			}
			item.setHaveRune(rolePet.isMosaicRune() ? ONE : ZERO);
			item.setLevel(rolePet.getLevel());
			item.setStar(rolePet.getStar());
			item.setShadowId(petTemplate.getShadowId());
			item.setShadowNum(petTemplate.getShadowNum());
			item.setSwallowExp(this.getSwallowExp(petId, rolePet));
			petListItem.add(item);
		}
		return petListItem;
	}

	// 宠物属性列表
	@Override
	public List<AttriTypeStrValueItem> getRolePetAttriStrItemList(RolePet rolePet) {
		AttriBuffer buffer = this.getRolePetAttriBuffer(rolePet);
		List<AttriTypeStrValueItem> list = Lists.newArrayList();
		for (AttributeType attriType : attributeTypeList) {
			if (null == attriType) {
				continue;
			}
			AttriItem attriItem = buffer.getAttriItem(attriType);
			if (null == attriItem) {
				continue;
			}
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(attriItem.getAttriTypeValue());
			item.setValue(String.valueOf((int) attriItem.getValue()));
			list.add(item);
		}
		return list;
	}

	// 获得宠物的技能列表
	@Override
	public List<SkillSimpleItem> getSkillSimpleItemList(RolePet rolePet) {
		int petId = rolePet.getPetId();
		GoodsPet petTemplate = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
		if (null == petTemplate) {
			return null;
		}
		List<SkillSimpleItem> skillList = Lists.newArrayList();
		for (short skillId : petTemplate.getSkillList()) {
			Skill petSkill = GameContext.getSkillApp().getSkill(skillId);
			if (null == petSkill) {
				continue;
			}
			if (petSkill.isNormalAttack()) {
				continue;
			}
			SkillSimpleItem item = new SkillSimpleItem();
			item.setImageId(petSkill.getIconId());
			item.setSkillName(petSkill.getName());
			item.setLevel(this.getPetSkillLevel(rolePet, skillId));
			item.setSkillId(skillId);
			skillList.add(item);
		}
		return skillList;
	}

	// 获得升星宠物属性变化列表
	@Override
	public List<AttriTypeStrValueItem> getStarChangeAttriList(RolePet rolePet) {
		int petId = rolePet.getPetId();
		GoodsPet petTemplate = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
		if (null == petTemplate) {
			return null;
		}
		int level = rolePet.getLevel();
		byte quality = petTemplate.getQualityType();
		byte star = rolePet.getStar();
		byte newStar = (byte) (star + ONE);
		AttriBuffer oldBuffer = this.getPetAttriBuffer(petId, level, quality, star);
		AttriBuffer newBuffer = this.getPetAttriBuffer(petId, level, quality, newStar);
		newBuffer.append(oldBuffer.reverse());
		// 创建List
		List<AttriTypeStrValueItem> list = Lists.newArrayList();
		for (AttributeType attriType : attributeTypeList) {
			if (null == attriType) {
				continue;
			}
			AttriItem attriItem = newBuffer.getAttriItem(attriType);
			if (null == attriItem) {
				continue;
			}
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(attriItem.getAttriTypeValue());
			item.setValue(String.valueOf((int) attriItem.getValue()));
			list.add(item);
		}
		return list;
	}

	// 宠物兑换
	@Override
	public Result petExchange(RoleInstance role, int petId) {
		String roleId = role.getRoleId();
		RolePet rolePet = GameContext.getUserPetApp().getRolePet(roleId, petId);
		Result result = new Result();
		if (null != rolePet) {
			result.setInfo(GameContext.getI18n().getText(TextId.Pet_Had_Owned));
			return result;
		}
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
		if (null == goodsPet) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		int shadowId = goodsPet.getShadowId();
		int shadowNum = goodsPet.getShadowNum();
		Result goodResult = GameContext.getUserGoodsApp().deleteForBag(role, shadowId, shadowNum, OutputConsumeType.pet_exchange_consume);
		if (null == goodResult || !goodResult.isSuccess()) {
			return goodResult;
		}
		rolePet = this.createRolePet(role, goodsPet);
		return result.success();
	}

	// 宠物升星
	@Override
	public Result petStarUp(RoleInstance role, RolePet rolePet) {
		Result result = new Result();
		if (this.isPetMaxStar(rolePet)) {
			result.setInfo(this.getText(TextId.Pet_Hava_Max_Star));
			return result;
		}
		int petId = rolePet.getPetId();
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
		int shadowId = goodsPet.getShadowId();
		int needShadowNum = this.starUpNeedShadowNum(rolePet);
		int havaShadowNum = role.getRoleBackpack().countByGoodsId(shadowId);
		byte star = rolePet.getStar();
		AttriBuffer oldBuffer = this.getRolePetAttriBuffer(rolePet);
		if (havaShadowNum >= needShadowNum) {
			Result goodResult = GameContext.getUserGoodsApp().deleteForBag(role, shadowId, needShadowNum, OutputConsumeType.pet_starup_consume);
			if (null == goodResult || !goodResult.isSuccess()) {
				result.setInfo(this.getText(TextId.SYSTEM_ERROR));
				return result;
			}
			byte newStar = (byte) (star + ONE);
			rolePet.setStar(newStar);
			rolePet.setStarProgress(ZERO);
			this.changePetSkillLevel(rolePet);
			this.updateRolePet(rolePet);
			if (rolePet.getOnBattle() == TWO) {
				// 重新计算属性
				this.reCalct(rolePet);
				AttriBuffer newBuffer = this.getRolePetAttriBuffer(rolePet);
				this.rolePetChangeAttriEffect(role, oldBuffer, newBuffer);
			}
			GameContext.getHeroApp().onPetStarChanged(Integer.parseInt(role.getRoleId()), petId, rolePet.getQuality(), newStar, rolePet.getQuality(), star);// 通知英雄改变情缘
			// 通知客户端红点提示规则变化
			this.pushHintRulesChange(role, rolePet);
			this.broadcastStar(role, rolePet);
			result.setInfo(this.getText(TextId.Pet_Star_Up_Success));
			result.setResult(TWO);
			return result;
		}
		Result goodResult = GameContext.getUserGoodsApp().deleteForBag(role, shadowId, havaShadowNum, OutputConsumeType.pet_starup_consume);
		if (null == goodResult || !goodResult.isSuccess()) {
			result.setInfo(GameContext.getI18n().getText(TextId.Pet_Shadow_Not_Enough));
			return result;
		}
		int starProgress = rolePet.getStarProgress() + havaShadowNum;
		rolePet.setStarProgress(starProgress);
		this.updateRolePet(rolePet);// 更改并入库
		result.setInfo(GameContext.getI18n().getText(TextId.Pet_Star_Up_Success));
		return result.success();
	}

	/**
	 * 升星成功世界喊话
	 * 
	 * @param role
	 * @param config
	 * @param petId
	 */
	private void broadcastStar(RoleInstance role, RolePet rolePet) {
		try {
			PetStarUpConfig petStarUpConfig = this.getPetStarUpConfig(rolePet.getQuality(), rolePet.getStar());
			if (null == petStarUpConfig) {
				return;
			}
			String broadcastInfo = petStarUpConfig.getBroadcastTips(role, rolePet.getPetId());
			if (Util.isEmpty(broadcastInfo)) {
				return;
			}
			GameContext.getChatApp().sendSysMessage(ChatSysName.Goods_UpgradeStar, ChannelType.Publicize_Personal, broadcastInfo, null, null);
		} catch (Exception e) {
			logger.error("PetAppImpl.broadcastStar error", e);
		}
	}

	/**
	 * 获得高品质宠物广播
	 * 
	 * @param role
	 * @param goodsPet
	 */
	private void broadcastCard(RoleInstance role, GoodsPet goodsPet) {
		try {
			// 如果获得宠物品质低于橙色，不予广播
			if (QualityType.orange.getType() > goodsPet.getQualityType()) {
				return;
			}
			String message = GameContext.getI18n().getText(TextId.BROAD_CAST_CARD_PET).replace(Wildcard.Role_Name, Util.getColorRoleName(role, ChannelType.Publicize_Personal))
					.replace(Wildcard.GoodsName, Wildcard.getChatGoodsName(goodsPet.getId(), ChannelType.Publicize_Personal));
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Publicize_Personal, message, null, null);
		} catch (Exception e) {
			logger.error("PetAppImpl.broadcastCard error!", e);
		}
	}

	private void changePetSkillLevel(RolePet rolePet) {
		Map<Short, RoleSkillStat> skillMap = rolePet.getSkillMap();
		if (Util.isEmpty(skillMap)) {
			return;
		}
		for (RoleSkillStat stat : skillMap.values()) {
			stat.setSkillLevel(this.getPetSkillLevel(rolePet, stat.getSkillId()));
		}
	}

	// 宠物升星所需碎片
	private int starUpNeedShadowNum(RolePet rolePet) {
		int progress = rolePet.getStarProgress();
		int maxShadow = this.getStarShadowNumber(rolePet);
		return maxShadow - progress;
	}

	// 获得宠物战斗力
	@Override
	public int getPetBattleScore(RolePet rolePet) {
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(rolePet.getMasterId());
		if (!isOnline) {
			return rolePet.getScore();
		}
		int battleScore = GameContext.getAttriApp().getAttriBattleScore(this.getRolePetAttriBuffer(rolePet));
		return battleScore;
	}

	// 获得宠物属性Buffer
	private AttriBuffer getRolePetAttriBuffer(RolePet rolePet) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		// 基本
		buffer.append(this.getPetAttriBuffer(rolePet.getPetId(), rolePet.getLevel(), rolePet.getQuality(), rolePet.getStar()));
		// 符文
		buffer.append(this.getRuneAttriBuffer(rolePet));
		return buffer;
	}

	// 获得宠物基本属性
	@Override
	public AttriBuffer getPetAttriBuffer(int petId, int petLevel, int quality, byte star) {
		PetAttribute levelRate = this.getAttributePetLevelConfig(petLevel);
		PetAttribute petRate = this.getAttributePetRateConfig(petId);
		PetAttribute qualityRate = this.getAttributePetQualityConfig((byte) quality, star);
		PetAttribute typeRate = this.getAttributePetTypeConfig();
		PetAttribute bornRate = null;
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
		if (null != goodsPet) {
			bornRate = this.getAttributePetBornConfig((byte) quality, goodsPet.getStar());
		}
		// 遍历属性
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for (AttributeType at : attributeTypeList) {
			byte attriType = at.getType();
			float bornValue = this.getAttriValue(bornRate, attriType) * this.getAttriValue(petRate, attriType) * this.getAttriValue(typeRate, attriType);
			float growValue = this.getAttriValue(qualityRate, attriType) * this.getAttriValue(levelRate, attriType) * this.getAttriValue(petRate, attriType)
					* this.getAttriValue(typeRate, attriType);
			int totalValue = Math.max(0, (int) bornValue) + Math.max(0, (int) growValue);
			buffer.append(at, totalValue, false);
		}
		return buffer;
	}

	// 获得属性系数值
	private float getAttriValue(PetAttribute rate, byte type) {
		if (null == rate) {
			return 1;
		}
		return rate.getValue(type);
	}

	// 获得符文加成属性
	private AttriBuffer getRuneAttriBuffer(RolePet rolePet) {
		Map<Byte, MosaicRune> mosaicRuneMap = rolePet.getMosaicRuneMap();
		if (Util.isEmpty(mosaicRuneMap)) {
			return null;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for (MosaicRune mosaicRune : mosaicRuneMap.values()) {
			if (null == mosaicRune) {
				continue;
			}
			buffer.append(mosaicRune.getAttriList());
		}
		return buffer;
	}

	// 宠物召回
	@Override
	public Result petOffBattle(RoleInstance role) {
		String roleId = role.getRoleId();
		RolePet oldPet = GameContext.getUserPetApp().getOnBattleRolePet(roleId);
		if (null == oldPet) {
			Result result = new Result();
			result.setInfo(this.getText(TextId.ERROR_INPUT));
			return result;
		}
		oldPet.setOnBattle(UserPetAppImpl.OFF_BATTLE);
		GameContext.getUserPetApp().petOffBattle(roleId);
		// 重新计算属性
		AttriBuffer oldBuffer = this.getRolePetAttriBuffer(oldPet);
		this.rolePetChangeAttriEffect(role, oldBuffer, null);
		C1668_PetOffBattleNotifyMessage message = new C1668_PetOffBattleNotifyMessage();
		message.setRoleId(Integer.parseInt(roleId));
		// 通知地图玩家
		this.summonNotify(role, message);
		Result result = new Result();
		result.setInfo(GameContext.getI18n().getText(TextId.Pet_Off_Ballte_Success));
		result.setResult(ONE);
		return result;
	}

	// 通知玩家
	private void summonNotify(RoleInstance role, Message msg) {
		role.getBehavior().sendMessage(msg);
		MapInstance map = role.getMapInstance();
		if (null == map) {
			return;
		}
		map.broadcastMap(role, msg);
	}

	// 宠物出战（替换出战宠物）
	@Override
	public Result petOnBattle(RoleInstance role, RolePet rolePet) {
		String roleId = role.getRoleId();
		Result result = new Result();
		RolePet oldPet = GameContext.getUserPetApp().getOnBattleRolePet(roleId);
		GameContext.getUserPetApp().setOnBattleRolePet(roleId, oldPet, rolePet);
		// 重新计算属性
		this.reCalct(rolePet);
		AttriBuffer oldBuffer = AttriBuffer.createAttriBuffer();
		if (null != oldPet) {
			oldBuffer = this.getRolePetAttriBuffer(oldPet);
		}
		AttriBuffer newBuffer = this.getRolePetAttriBuffer(rolePet);
		this.rolePetChangeAttriEffect(role, oldBuffer, newBuffer);

		C1667_PetOnBattleNotifyMessage message = new C1667_PetOnBattleNotifyMessage();
		message.setRoleId(Integer.parseInt(roleId));
		PetBattleItem battlePet = this.getOnBattlePetItem(roleId);
		message.setBattlePet(battlePet);
		// 通知周围玩家宠物出战(PUSH C1667)
		this.summonNotify(role, message);
		result.setInfo(GameContext.getI18n().getText(TextId.Pet_On_Ballte_Success));
		result.setResult(TWO);
		return result;
	}

	// 重新计算宠物属性
	private void reCalct(RolePet rolePet) {
		try {
			GameContext.getUserAttributeApp().reCalct(rolePet);
			// 设置pet的curHP, curMP
			rolePet.setCurHP(rolePet.get(AttributeType.maxHP));
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	// 属性变化英雄角色
	private void rolePetChangeAttriEffect(RoleInstance role, AttriBuffer oldBuffer, AttriBuffer newBuffer) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		if (null != oldBuffer) {
			buffer.append(oldBuffer.reverse());
		}
		if (null != newBuffer) {
			buffer.append(newBuffer);
		}
		// 修改角色属性值
		GameContext.getUserAttributeApp().changeAttribute(role, buffer);
		role.getBehavior().notifyAttribute();
	}

	// 使用背包中的宠物物品
	@Override
	public Result usePetGoods(RoleInstance role, RoleGoods roleGoods, boolean confirm) {
		Result result = new Result();
		try {
			GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, roleGoods.getGoodsId());
			if (null == goodsPet) {
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return result;
			}

			// 判断是否已经拥有了此宠物，如果有转换成碎片
			RolePet rolePet = GameContext.getUserPetApp().getRolePet(role.getRoleId(), goodsPet.getId());
			if (null != rolePet) {
				return this.petGoodsToShadow(role, roleGoods, confirm, goodsPet);
			}

			// 删除物品，添加宠物
			GoodsResult gr = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, roleGoods.getId(), ONE, OutputConsumeType.pet_goods_use);
			if (!gr.isSuccess()) {
				return gr;
			}
			try {
				// 添加宠物
				result = this.usePetTemplate(role, goodsPet);
				if (result.isSuccess()) {
					String tips = GameContext.getI18n().messageFormat(TextId.Pet_Goods_Use_Success, goodsPet.getName());
					result.setInfo(tips);
				}
			} catch (Exception e) {
				logger.error("petExchange error,petId=" + goodsPet.getId() + " roleId=" + role.getRoleId(), e);
				result.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			}
			return result;
		} catch (Exception ex) {
			logger.error("usePetGoods error", ex);
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result;
		}
	}

	// 获得宠物
	private Result usePetTemplate(RoleInstance role, GoodsPet goodsPet) {
		this.createRolePet(role, goodsPet);
		return new Result().success();
	}

	// 新获得的宠物
	private RolePet createRolePet(RoleInstance role, GoodsPet goodsPet) {
		String roleId = role.getRoleId();
		RolePet rolePet = new RolePet();
		rolePet.setMasterId(roleId);
		rolePet.setPetId(goodsPet.getId());
		rolePet.setStar(goodsPet.getStar());
		rolePet.setLevel(goodsPet.getLevel());
		rolePet.setQuality(this.getPetQuality(rolePet));
		rolePet.setScore(this.getPetBattleScore(rolePet));
		rolePet.setRole(role);
		// 设置宠物campId,用来在战斗中确定势力关系
		rolePet.setCampId(role.getCampId());
		// 初始化行为类
		rolePet.setBehavior(new PetBehavior(rolePet));
		GameContext.getUserPetApp().addRolePet(roleId, rolePet);// 保存到内存中
		GameContext.getUserPetApp().insertRolePet(rolePet);// 入库
		GameContext.getTargetApp().updateTarget(role, TargetCondType.PetNum);// 目标系统
		GameContext.getHeroApp().onPetAdded(Integer.parseInt(roleId), rolePet.getPetId(), rolePet.getQuality(), rolePet.getStar());// 通知情缘变化
		// 通知红点提示规则变化
		this.pushHintRulesChange(role, rolePet);
		// 世界走马灯广播
		this.broadcastCard(role, goodsPet);
		return rolePet;
	}

	private void pushHintRulesChange(RoleInstance role, RolePet rolePet) {
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		if (null == goodsPet) {
			return;
		}
		HintRulesItem hintRulesItem = new HintRulesItem();
		hintRulesItem.setType(HintAppImpl.HINT_PET);
		hintRulesItem.setTargetId(rolePet.getPetId());
		List<HintGoodsTermItem> hintGoodsList = Lists.newArrayList();
		short needNum = (short) this.getStarShadowNumber(rolePet);
		// 如果是最大星级
		if (this.isPetMaxStar(rolePet)) {
			needNum = Short.MAX_VALUE;
		}
		hintGoodsList.add(new HintGoodsTermItem(goodsPet.getShadowId(), needNum));
		hintRulesItem.setHintGoodsTermList(hintGoodsList);
		GameContext.getHintApp().pushHintRulesChange(role, hintRulesItem);
	}

	// 转换成碎片
	private Result petGoodsToShadow(RoleInstance role, RoleGoods roleGoods, boolean confirm, GoodsPet goodsPet) {
		if (confirm) {
			// 直接转换为影子
			List<GoodsOperateBean> addList = Lists.newArrayList(new GoodsOperateBean(goodsPet.getShadowId(), goodsPet.getShadowNum(), roleGoods.getBind()));
			GoodsResult gr = GameContext.getUserGoodsApp().addDelGoodsForBag(role, addList, OutputConsumeType.pet_goods_to_shadow, roleGoods, roleGoods.getCurrOverlapCount(),
					null, OutputConsumeType.goods_use);
			if (!gr.isSuccess()) {
				return gr;
			}
			Result result = new Result();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsPet.getShadowId());
			String tips = GameContext.getI18n().messageFormat(TextId.Pet_goods_to_shadow_success_tips, goodsPet.getName(), gb.getName(), String.valueOf(goodsPet.getShadowNum()));
			result.setInfo(tips);
			result.success();
			return result;
		}
		// 二次确认提示用户是否转换为影子
		UseResult result = new UseResult();
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsPet.getShadowId());
		String tips = GameContext.getI18n().messageFormat(TextId.Pet_Goods_To_shadow_Confirm, goodsPet.getName(), gb.getName(), String.valueOf(goodsPet.getShadowNum()));
		result.setMustConfirm(true);
		result.setInfo(tips);
		result.setConfirmCmdId(PET_GOODS_TO_SHADOW_CMDID);
		// 物品实例ID
		result.setConfirmInfo(roleGoods.getId());
		result.success();
		return result;
	}

	// 孔位信息
	@Override
	public byte[] getHoleInfo(RolePet rolePet) {
		GoodsPet goods = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		int holeNum = this.getPetStarHoleConfig(rolePet.getStar()).getHoleNum() - 1;
		int maxHoleNum = this.getPetStarHoleConfig(goods.getMaxStar()).getHoleNum();
		byte[] holeInfo = new byte[maxHoleNum];
		for (int i = 0; i < maxHoleNum; i++) {
			if (i <= holeNum) {
				holeInfo[i] = 1;
				continue;
			}
			holeInfo[i] = 0;
		}
		return holeInfo;
	}

	// 当前等级最大经验
	@Override
	public int getMaxExp(RolePet rolePet) {
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		byte quality = goodsPet.getQualityType();
		PetLevelUpConfig levelConfig = this.getPetLevelUpConfig(quality, rolePet.getLevel());
		return levelConfig.getMaxExp();
	}

	// 是否达到最大星级
	@Override
	public boolean isPetMaxStar(RolePet rolePet) {
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		return rolePet.getStar() >= goodsPet.getMaxStar();
	}

	// 当前星级所需最大碎片数
	@Override
	public int getStarShadowNumber(RolePet rolePet) {
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		byte quality = goodsPet.getQualityType();
		PetStarUpConfig starConfig = this.getPetStarUpConfig(quality, rolePet.getStar());
		if (null == starConfig) {
			return 0;
		}
		return starConfig.getShadowNumber();
	}

	// 宠物吞噬
	@Override
	public PetSwallowResult petSwallow(RoleInstance role, RolePet rolePet, List<PetSwallowItem> swallowList) {
		PetSwallowResult result = this.petSwallowCond(role, rolePet, swallowList);
		if (!result.isSuccess()) {
			return result;
		}
		List<RoleGoods> goodsList = result.getGoodsList();
		List<RoleGoods> danList = result.getDanList();
		Map<String, Integer> singleExpMap = result.getSingleExpMap();
		List<RolePet> petList = result.getPetList();
		int oldLevel = rolePet.getLevel();
		int maxLevel = role.getLevel();
		// 将结果设置为失败
		result.failure();
		AttriBuffer oldBuffer = AttriBuffer.createAttriBuffer();
		boolean isOnBattle = GameContext.getUserPetApp().isOnBattle(role.getRoleId(), rolePet.getPetId());
		if (isOnBattle) {
			oldBuffer = this.getRolePetAttriBuffer(rolePet);
		}
		try {
			// 优先使用经验丹
			int targetLevel = rolePet.getLevel();
			for (RoleGoods goods : danList) {
				if (rolePet.getLevel() >= role.getLevel()) {
					if (Util.isEmpty(goodsList) && Util.isEmpty(petList)) {
						result.setInfo(GameContext.getI18n().getText(TextId.Pet_level_canot_gt_role_level));
						return result;
					}
					break;
				}
				if (null == goods) {
					continue;
				}
				int count = goods.getCurrOverlapCount();
				int needCount = maxLevel - oldLevel;
				if (count >= needCount) {
					// 删除物品
					GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, goods.getId(), needCount, OutputConsumeType.pet_swallow_consume);
					if (!goodsResult.isSuccess()) {
						result.setInfo(goodsResult.getInfo());
						return result;
					}
					rolePet.setLevel(maxLevel);
					this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
					// 已升到最大等级
					break;
				}
				// 删除物品
				GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, goods.getId(), count, OutputConsumeType.pet_swallow_consume);
				if (!goodsResult.isSuccess()) {
					result.setInfo(goodsResult.getInfo());
					return result;
				}
				targetLevel += count;
				rolePet.setLevel(targetLevel);
				this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
			}
			// 计算到达最大等级需要的经验
			long needMaxExp = this.reachMaxLevelNeedExp(role, rolePet);
			long addExp = 0;
			// 先消耗物品
			for (RoleGoods goods : goodsList) {
				long needExp = needMaxExp - addExp;
				long singleExp = singleExpMap.get(goods.getId());
				long n = needExp / singleExp;
				long needNum = (needExp % singleExp) == 0 ? n : n + 1;
				int currOverlapCount = goods.getCurrOverlapCount();
				if (currOverlapCount >= needNum) {
					// 删除物品
					GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, goods.getId(), (int) needNum, OutputConsumeType.pet_swallow_consume);
					if (!goodsResult.isSuccess()) {
						result.setInfo(goodsResult.getInfo());
						return result;
					}
					// 已经满级可以提前返回
					this.swallowToMaxLevel(rolePet, maxLevel);
					this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
					result.setSwallowExp(this.getSwallowExp(rolePet.getPetId(), rolePet));
					return result;
				}
				// 删除物品
				GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, goods.getId(), OutputConsumeType.pet_swallow_consume);
				if (!goodsResult.isSuccess()) {
					result.setInfo(goodsResult.getInfo());
					return result;
				}
				// 此处的物品数量必须在物品删除前获得，物品删除后会将物品数量设置为0
				addExp += singleExp * currOverlapCount;
			}
			// 消耗宠物
			for (RolePet rp : petList) {
				long needExp = needMaxExp - addExp;
				long singleExp = this.getSwallowExp(rp.getPetId(), rp);
				if (singleExp >= needExp) {
					// 删除宠物
					this.deleteSwallowPet(rp);
					// 收集已经删除的英雄返回给客户端
					result.getSwallowPetList().add(rp.getPetId());
					// 已经满级可以提前返回
					this.swallowToMaxLevel(rolePet, maxLevel);
					this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
					result.setSwallowExp(this.getSwallowExp(rp.getPetId(), rolePet));
					return result;
				}
				// 收集已经删除的英雄返回给客户端
				result.getSwallowPetList().add(rp.getPetId());
				// 删除宠物
				this.deleteSwallowPet(rp);
				addExp += singleExp;
			}
			// 计算addExp可以升级
			// 因为开始等级为影响当前等级，所以需要将影响的当前经验+进去
			addExp += rolePet.getExp();
			int targetLv = rolePet.getLevel();
			for (int lv = rolePet.getLevel(); lv <= maxLevel; lv++) {
				PetLevelUpConfig lup = this.getPetLevelUpConfig(rolePet.getQuality(), lv);
				int thisLvMaxExp = lup.getMaxExp();
				if (addExp < thisLvMaxExp) {
					rolePet.setExp((int) addExp);
					rolePet.setLevel(targetLv);
					this.updateRolePet(rolePet);
					this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
					result.setSwallowExp(this.getSwallowExp(rolePet.getPetId(), rolePet));
					return result;
				} else {
					// 升级
					targetLv++;
					addExp -= thisLvMaxExp;
				}
			}
			this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
			result.setSwallowExp(this.getSwallowExp(rolePet.getPetId(), rolePet));
			return result;
		} finally {
			if (isOnBattle) {
				// 计算效果
				if (TWO == result.getStatus()) {
					this.rolePetLevelAttriEffect(role, oldLevel, oldBuffer, rolePet);
					// 目标系统
					GameContext.getTargetApp().updateTarget(role, TargetCondType.PetLevel);
				}
			}
		}
	}

	/**
	 * 增加出战宠物经验
	 * @param role
	 * @param exp
	 */
	@Override
	public void changeBattlePetExp(RoleInstance role, int exp) {
		PetSwallowResult result = new PetSwallowResult();
		RolePet rolePet = GameContext.getUserPetApp().getOnBattleRolePet(role.getRoleId());
		if (null == rolePet) {
			return;
		}
		// 当前等级
		int oldLevel = rolePet.getLevel();
		// 获取升级之前的加成
		AttriBuffer oldBuffer = this.getRolePetAttriBuffer(rolePet);
		try {
			int maxLevel = role.getLevel();
			long needMaxExp = this.reachMaxLevelNeedExp(role, rolePet);
			// 如果超过最大等级需求经验
			if (exp >= needMaxExp) {
				this.swallowToMaxLevel(rolePet, maxLevel);
				this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
				return;
			}
			// 计算addExp可以升级
			// 因为开始等级为影响当前等级，所以需要将影响的当前经验+进去
			exp += rolePet.getExp();
			int targetLv = rolePet.getLevel();
			for (int lv = rolePet.getLevel(); lv <= maxLevel; lv++) {
				PetLevelUpConfig lup = this.getPetLevelUpConfig(rolePet.getQuality(), lv);
				int thisLvMaxExp = lup.getMaxExp();
				if (exp < thisLvMaxExp) {
					rolePet.setExp(exp);
					rolePet.setLevel(targetLv);
					this.updateRolePet(rolePet);
					this.flagSuccessPetSwallowResult(result, rolePet, oldLevel);
				} else {
					targetLv++;// 升级
					exp -= thisLvMaxExp;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			// 计算效果
			if (TWO == result.getStatus()) {
				this.rolePetLevelAttriEffect(role, oldLevel, oldBuffer, rolePet);
				// 目标系统
				GameContext.getTargetApp().updateTarget(role, TargetCondType.PetLevel);
			}
		}
	}

	// 升级影响变化
	private void rolePetLevelAttriEffect(RoleInstance role, int oldLevel, AttriBuffer oldBuffer, RolePet rolePet) {
		try {
			// 未出战或等级未变化
			if (oldLevel == rolePet.getLevel()) {
				return;
			}
			AttriBuffer newBuffer = this.getRolePetAttriBuffer(rolePet);
			this.reCalct(rolePet);
			this.rolePetChangeAttriEffect(role, oldBuffer, newBuffer);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	// 删除被吞噬的宠物
	private void deleteSwallowPet(RolePet rolePet) {
		try {
			String roleId = rolePet.getMasterId();
			// 邮件返回宠物镶嵌宝石
			if (rolePet.isMosaicRune()) {
				Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
				mail.setRoleId(rolePet.getMasterId());
				mail.setSendRole(this.getText(TextId.SYSTEM));
				mail.setTitle(this.getText(TextId.Hero_swallow_equip_mail_title));
				mail.setContent(this.getText(TextId.Hero_swallow_equip_mail_content));
				mail.setSendSource(OutputConsumeType.pet_swallow_renu_mail.getType());
				Map<Byte, MosaicRune> mosaicRuneMap = rolePet.getMosaicRuneMap();
				if (!Util.isEmpty(mosaicRuneMap)) {
					for (MosaicRune mosaicRune : mosaicRuneMap.values()) {
						if (null == mosaicRune) {
							continue;
						}
						RoleGoods roleGoods = mosaicRune.getRoleGoods(roleId);
						roleGoods.setStorageType(StorageType.mail.getType());
						mail.addRoleGoods(roleGoods);
					}
					// 不能在遍历的时候删除
					rolePet.clearRune();
					// 发送邮件
					GameContext.getMailApp().sendMailAsync(mail);
				}
			}
			// 通知情缘变化
			GameContext.getHeroApp().onPetRemoved(Integer.parseInt(roleId), rolePet.getPetId(), rolePet.getQuality(), rolePet.getStar());
			GameContext.getUserPetApp().removeRolePet(rolePet.getMasterId(), rolePet.getPetId());
			GameContext.getUserPetApp().deleteRolePet(roleId, rolePet.getPetId());// 删除数据（入库）
		} catch (Exception e) {
			logger.error("PetAppImpl.deleteSwallowPet error!", e);
		}
	}

	// 获得文本信息
	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	// 吞噬确认
	private PetSwallowResult petSwallowCond(RoleInstance role, RolePet rolePet, List<PetSwallowItem> swallowList) {
		PetSwallowResult result = new PetSwallowResult();
		if (null == rolePet) {
			// 提示参数错误
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if (Util.isEmpty(swallowList)) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 不能超过角色等级
		int oldLevel = rolePet.getLevel();
		if (oldLevel >= role.getLevel()) {
			PetLevelUpConfig config = this.getPetLevelUpConfig(rolePet.getQuality(), oldLevel);
			if (rolePet.getExp() >= config.getMaxExp() - 1) {
				result.setInfo(GameContext.getI18n().getText(TextId.Pet_level_canot_gt_role_level));
				return result;
			}
		}
		// 判断要吞噬的物品或宠物是否存在
		for (PetSwallowItem item : swallowList) {
			String id = String.valueOf(item.getId());
			if (Util.isEmpty(id)) {
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return result;
			}
			if (Constant.PET_SWALLOW_GOODS_TYPE == item.getType()) {
				RoleGoods rg = GameContext.getUserGoodsApp().getRoleGoods(role, StorageType.bag, id, 0);
				if (null == rg) {
					result.setInfo(GameContext.getI18n().getText(TextId.Pet_swallow_target_not_exist));
					return result;
				}
				int singleExp = this.getSingleSwallowExp(rg);
				if (-1 == singleExp) {
					result.getDanList().add(rg);
					continue;
				}
				if (0 >= singleExp && singleExp != -1) {
					// 此物品不支持吞噬
					result.setInfo(GameContext.getI18n().messageFormat(TextId.Pet_goods_canot_swallow, this.getGoodsName(rg)));
					return result;
				}
				result.getSingleExpMap().put(rg.getId(), singleExp);
				result.getGoodsList().add(rg);
				continue;
			}
			if (!Util.isNumber(id)) {
				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
				return result;
			}
			int petId = Integer.parseInt(item.getId());
			// 宠物
			RolePet swallowPet = GameContext.getUserPetApp().getRolePet(role.getRoleId(), petId);
			if (null == swallowPet) {
				result.setInfo(GameContext.getI18n().getText(TextId.Pet_swallow_target_not_exist));
				return result;
			}
			if (swallowPet.getPetId() == rolePet.getPetId()) {
				// 不能为自己
				result.setInfo(GameContext.getI18n().getText(TextId.Pet_Not_Swallow_Self));
				return result;
			}
			// 不能吞噬目前已经出战未的英雄
			RolePet onBattlePet = GameContext.getUserPetApp().getOnBattleRolePet(role.getRoleId());
			if (null != onBattlePet && onBattlePet.getPetId() == swallowPet.getPetId()) {
				// 当前出战的宠物不能吞噬
				result.setInfo(GameContext.getI18n().getText(TextId.Hero_swallow_onbattle_canot_self));
				return result;
			}
			result.getPetList().add(swallowPet);
		}
		result.setRolePet(rolePet);
		result.success();
		return result;
	}

	// 获得吞噬经验
	private int getSingleSwallowExp(RoleGoods goods) {
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
		if (null == gb) {
			return 0;
		}
		if (GoodsType.GoodsPet.getType() == gb.getGoodsType()) {
			return ((GoodsPet) gb).getSwallowExp();
		}
		if (GoodsType.GoodsPetAid.getType() == gb.getGoodsType()) {
			return ((GoodsPetAid) gb).getSwallowExp();
		}
		return 0;
	}

	// 获得物品名称
	private String getGoodsName(RoleGoods goods) {
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goods.getGoodsId());
		if (null == gb) {
			return "";
		}
		return gb.getName();
	}

	// 达到最大等级所需经验
	private long reachMaxLevelNeedExp(RoleInstance role, RolePet rolePet) {
		int currLv = rolePet.getLevel();
		int maxLv = role.getLevel();
		if (currLv > maxLv) {
			return 0;
		}
		byte quality = rolePet.getQuality();
		PetLevelUpConfig lup = this.getPetLevelUpConfig(quality, currLv);
		long total = Math.max(0, lup.getMaxExp() - rolePet.getExp());
		for (int lv = rolePet.getLevel() + 1; lv < maxLv; lv++) {
			lup = this.getPetLevelUpConfig(quality, lv);
			total += lup.getMaxExp();
		}
		// 如果最大等级小于200，则计算到最大等级升级所需经验减1
		if (maxLv < MAX_LEVEL && currLv < maxLv) {
			total += this.getPetLevelUpConfig(quality, maxLv).getMaxExp() - 1;
		}
		return total;
	}

	// 吞噬达到最大等级
	private void swallowToMaxLevel(RolePet rolePet, int maxLevel) {
		rolePet.setLevel(maxLevel);
		if (maxLevel < MAX_LEVEL) {
			PetLevelUpConfig config = this.getPetLevelUpConfig(rolePet.getQuality(), maxLevel);
			if (null != config) {
				rolePet.setExp(config.getMaxExp() - 1);
			}
		} else {
			rolePet.setExp(0);
		}
		this.updateRolePet(rolePet);
	}

	// 更新宠物（更新战斗力）
	private void updateRolePet(RolePet rolePet) {
		rolePet.setScore(this.getPetBattleScore(rolePet));
		GameContext.getUserPetApp().rolePetUpdate(rolePet.getMasterId(), rolePet);
	}

	// 标识吞噬状态
	private void flagSuccessPetSwallowResult(PetSwallowResult result, RolePet rolePet, int oldLevel) {
		result.setStatus((oldLevel == rolePet.getLevel()) ? ONE : TWO);
	}

	// 获得吞噬经验
	private long getSwallowExp(int petId, RolePet rolePet) {
		long total = 0;
		if (null != rolePet) {
			total = (this.getReachNowLevelExp(rolePet) + rolePet.getExp());
		}
		// 模板上能获得的基本经验
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
		if (null != goodsPet) {
			total += goodsPet.getSwallowExp();
		}
		return total;
	}

	private long getReachNowLevelExp(RolePet rolePet) {
		long exp = 0;
		for (PetLevelUpConfig config : this.petLevelUpConfig.values()) {
			if (config.getQuality() == rolePet.getQuality()) {
				if (config.getLevel() < rolePet.getLevel()) {
					exp += config.getMaxExp();
				}
			}
		}
		return exp;
	}

	// 获得宠物品质
	private byte getPetQuality(RolePet rolePet) {
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		if (null == goodsPet) {
			return ZERO;
		}
		return goodsPet.getQualityType();
	}

	// 获得宠物技能等级
	@Override
	public byte getPetSkillLevel(RolePet rolePet, short skillId) {
		PetStarUpConfig starConfig = this.getPetStarUpConfig(rolePet.getQuality(), rolePet.getStar());
		if (null == starConfig) {
			return 0;
		}
		Skill skill = GameContext.getSkillApp().getSkill(skillId);
		if (null == skill) {
			return 0;
		}
		if (starConfig.getSkillLevel() > skill.getMaxLevel()) {
			return (byte) skill.getMaxLevel();
		}
		return starConfig.getSkillLevel();
	}

	// 宠物镶嵌宝石
	@Override
	public PetMosaicResult rolePetMosaicRune(RoleInstance role, RolePet rolePet, RoleGoods roleRune, byte hole) {
		PetMosaicResult result = this.mosaicRuneCondition(role, rolePet, roleRune, hole);
		if (result.isIgnore()) {
			return result;
		}
		// 验证条件失败
		if (!result.isSuccess()) {
			return result;
		}
		// 扣除游戏币
		if (result.getMosaicMoney() > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease, result.getMosaicMoney(), OutputConsumeType.rune_mosaic_pet);
			role.getBehavior().notifyAttribute();
		}
		// 删除宝石
		Result delResult = GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, roleRune.getId(), 1, OutputConsumeType.rune_mosaic_pet);
		if (!delResult.isSuccess()) {
			result.setInfo(delResult.getInfo());
			return result;
		}
		// 获取符文附加属性
		List<AttriItem> attriList = this.getMosaicRuneAttriList(roleRune);
		MosaicRune mosaicRune = new MosaicRune(hole, roleRune.getGoodsId(), attriList);
		// 镶嵌宝石
		rolePet.mosaicRune(mosaicRune);
		// 入库
		this.updateRolePet(rolePet);
		if (rolePet.getOnBattle() == TWO) {
			this.reCalct(rolePet);
			this.changeMosaicRuneEffect(role, mosaicRune, false);
		}
		// 调用目标系统
		GameContext.getTargetApp().updateTarget(role, TargetCondType.PetMosaic);
		result.setBattleScore(rolePet.getScore());
		result.setHoleNum(hole);
		result.setInfo(this.getText(TextId.Goods_Mosaic_Success));
		PetMosaicRuneItem mosaicRuneItem = new PetMosaicRuneItem();
		mosaicRuneItem.setHole(hole);
		mosaicRuneItem.setGoodsLiteItem(mosaicRune.getGoodsLiteItem());
		result.setMosaicRuneItem(mosaicRuneItem);
		result.setPetId(rolePet.getPetId());
		result.success();
		return result;
	}

	// 镶嵌宝石确认
	private PetMosaicResult mosaicRuneCondition(RoleInstance role, RolePet rolePet, RoleGoods roleRune, byte hole) {
		// 验证镶嵌金币
		PetMosaicResult result = new PetMosaicResult();
		int mosaMoney = GameContext.getGoodsApp().getMosaicConfig().getMosaicMoney();
		// 【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, mosaMoney);
		if (ar.isIgnore()) {
			result.setIgnore(true);
			return result;
		}
		if (!ar.isSuccess()) {
			result.setInfo(Status.GOODS_DERIVE_MOSAIC_MONEY_LESS.getTips());
			return result;
		}
		// 验证孔位信息
		byte[] holeInfo = this.getHoleInfo(rolePet);
		if (ZERO == holeInfo[hole]) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		if (ZERO != rolePet.getMosaicRuneId(hole)) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, roleRune.getGoodsId());
		if (null == goodsRune) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 验证孔位规则
		byte rule = GameContext.getRuneApp().getMoasicRules(ONE)[hole];
		if (rule < goodsRune.getSecondType()) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		// 验证互斥符文
		if (rolePet.isMosaicSameTypeRune(goodsRune)) {
			result.setInfo(this.getText(TextId.GOODS_MOSAIC_SAME_TYPE));
			return result;
		}
		result.setMosaicMoney(mosaMoney);
		result.success();
		return result;
	}

	// 获得符文附加属性
	private List<AttriItem> getMosaicRuneAttriList(RoleGoods roleRune) {
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, roleRune.getGoodsId());
		if (ONE == goodsRune.getSecondType()) {
			return goodsRune.getAttriItemList();
		}
		return roleRune.getAttrVarList();
	}

	// 通知角色镶嵌符文变化属性影响
	private void changeMosaicRuneEffect(RoleInstance role, MosaicRune mosaicRune, boolean isDismount) {
		List<AttriItem> attriList = mosaicRune.getAttriList();
		if (Util.isEmpty(attriList)) {
			return;
		}
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(attriList);
		if (isDismount) {
			buffer.reverse();
		}
		GameContext.getUserAttributeApp().changeAttribute(role, buffer);
		role.getBehavior().notifyAttribute();
	}

	/**
	 * @Title: rolePetDismountRune
	 * @Description: TODO(宠物宝石卸下)
	 * @param role
	 * @param rolePet
	 * @param hole
	 * @return
	 * @see com.game.draco.app.pet.PetApp#rolePetDismountRune(sacred.alliance.magic.vo.RoleInstance, com.game.draco.app.pet.domain.RolePet, byte)
	 */
	@Override
	public PetMosaicResult rolePetDismountRune(RoleInstance role, RolePet rolePet, byte hole) {
		PetMosaicResult result = this.dismountRuneCondition(role, rolePet, hole);
		if (!result.isSuccess()) {
			return result;
		}
		// 物品不存在
		RoleGoods mosaicRoleGoods = this.createRoleGoods(role, rolePet.getMosaicRune(hole));
		if (null == mosaicRoleGoods) {
			result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_EXISTS));
			return result;
		}
		// 判断物品能否添加
		if (!this.canPutGoods(role, mosaicRoleGoods)) {
			result.setInfo(GameContext.getI18n().getText(TextId.Bag_Is_Full));
			return result;
		}
		// 扣除游戏币
		if (result.getMosaicMoney() > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease, result.getMosaicMoney(),
					OutputConsumeType.rune_discharge_pet_consume);
			role.getBehavior().notifyAttribute();
		}
		// 宠物移除宝石
		MosaicRune mosaicRune = rolePet.dismountRune(hole);
		// 入库
		this.updateRolePet(rolePet);
		// 添加拆除的物品
		GoodsResult goodsResult = this.addRoleGoods(role, mosaicRoleGoods);
		if (result.isIgnore()) {
			return result;
		}
		if (!goodsResult.isSuccess()) {
			result.setInfo(goodsResult.getInfo());
			return result;
		}
		// 如果是出战宠物，重新计算属性
		if (rolePet.getOnBattle() == TWO) {
			this.reCalct(rolePet);
			this.changeMosaicRuneEffect(role, mosaicRune, true);
		}
		// 调用目标系统
		GameContext.getTargetApp().updateTarget(role, TargetCondType.PetMosaic);
		result.success();
		result.setInfo(this.getText(TextId.Goods_Gem_Remove_Success));
		result.setPetId(rolePet.getPetId());
		result.setHoleNum(hole);
		result.setBattleScore(rolePet.getScore());
		result.setIsHavaRune(rolePet.isMosaicRune() ? ONE : ZERO);
		return result;
	}

	/**
	 * 添加物品
	 * @param role
	 * @param roleGoods
	 * @return
	 */
	private GoodsResult addRoleGoods(RoleInstance role, RoleGoods roleGoods) {
		return GameContext.getUserGoodsApp().addGoodsForBag(role, roleGoods, OutputConsumeType.rune_discharge_pet);
	}
	
	/**
	 * 背包是否可添加
	 * @param role
	 * @param roleGoods
	 * @return
	 */
	private boolean canPutGoods(RoleInstance role, RoleGoods roleGoods) {
		List<RoleGoods> goodsList = Lists.newArrayList();
		goodsList.add(roleGoods);
		return GameContext.getUserGoodsApp().canPutGoods(role, goodsList);
	}
	
	/**
	 * 创建宝石物品实例
	 * @param role
	 * @param mosaicRune
	 * @return
	 */
	private RoleGoods createRoleGoods(RoleInstance role, MosaicRune mosaicRune) {
		GoodsRune goodsRune = GameContext.getGoodsApp().getGoodsTemplate(GoodsRune.class, mosaicRune.getGoodsId());
		if (null == goodsRune) {
			return null;
		}
		RoleGoods roleGoods = goodsRune.createSingleRoleGoods(role.getRoleId(), 1);
		roleGoods.setBind(BindingType.already_binding.getType());
		// 如果是多属性宝石，将之前属性赋值给宝石
		if (1 != goodsRune.getSecondType()) {
			roleGoods.setAttrVarList((ArrayList<AttriItem>) mosaicRune.getAttriList());
		}
		return roleGoods;
	}

	private PetMosaicResult dismountRuneCondition(RoleInstance role, RolePet rolePet, byte hole) {
		PetMosaicResult result = new PetMosaicResult();
		// 【游戏币/潜能/钻石不足弹板】 判断
		int mosaMoney = GameContext.getGoodsApp().getMosaicConfig().getExciseMoney();
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, mosaMoney);
		if (ar.isIgnore()) {
			result.setIgnore(true);
			return result;
		}
		if (!ar.isSuccess()) {
			result.setInfo(Status.GOODS_DERIVE_MOSAIC_MONEY_LESS.getTips());
			return result;
		}
		// 验证孔位信息
		if (ZERO == rolePet.getMosaicRuneId(hole)) {
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result;
		}
		result.setMosaicMoney(mosaMoney);
		result.success();
		return result;
	}

	// 宠物抢夺
	@Override
	public Result petChallenge(RoleInstance role, String targetRoleId, String targetRoleName, int targetPetId, byte opType) {
		Result result = new Result();
		try {
			if (this.getPvpRemainNum(role.getRoleId(), ZERO) <= 0) {
				return result.setInfo(this.getText(TextId.Pet_pvp_lack_num));
			}
			if (this.getPvpRemainNum(targetRoleId, ONE) <= 0) {
				String info = GameContext.getI18n().messageFormat(TextId.Pet_pvp_target_num, targetRoleName);
				return result.setInfo(info);
			}
			AsyncPvpBattleInfo info = new AsyncPvpBattleInfo();
			info.setRoleId(role.getRoleId());
			info.setRoleName(role.getRoleName());
			info.setTargetRoleId(targetRoleId);
			info.setTargetRoleName(targetRoleName);
			info.setOpType(opType);
			info.setPetId(targetPetId);
			GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, targetPetId);
			if (null != goodsPet) {
				info.setPetName(goodsPet.getName());
			}
			GameContext.getAsyncPvpApp().addAsyncPvpBattleInfo(info);
			// 切换地图
			Point targetPoint = new Point(this.petPvpConfig.getMapId(), this.petPvpConfig.getMapX(), this.petPvpConfig.getMapY());
			GameContext.getUserMapApp().changeMap(role, targetPoint);
			return result.success();
		} catch (Exception ex) {
			logger.error("PetAppImpl.challenge error", ex);
			return result;
		}
	}

	// 获得抢夺剩余次数
	private int getPvpRemainNum(String roleId, byte type) {
		RolePetStatus status = null;
		int remainNum = 0;
		if (type == ZERO) {
			status = GameContext.getUserPetApp().getRolePetStatus(roleId);
			if (null != status) {
				status.reset();
				remainNum = this.petPvpConfig.getRobNum() - status.getRobNum();
			}
			return remainNum;
		}
		if (GameContext.getOnlineCenter().isOnlineByRoleId(roleId)) {
			status = GameContext.getUserPetApp().getRolePetStatus(roleId);
		} else {
			status = GameContext.getPetStorage().getRolePetStatus(roleId);
		}
		if (null != status) {
			status.reset();
			remainNum = this.petPvpConfig.getRevengeNum() - status.getRevengeNum();
		}
		return remainNum;
	}

	// 发送抢夺信息
	@Override
	public void sendPvpInfo(RoleInstance role, byte opType, boolean isRefresh) {
		try {
			PetPvpInfoListResult result = this.getPvpInfoList(role, opType, isRefresh);
			C1662_PetPvpInfoListRespMessage respMsg = new C1662_PetPvpInfoListRespMessage();
			if (ZERO == opType) {
				respMsg.setRemainNum((byte) this.getPvpRemainNum(role.getRoleId(), opType));
			}
			respMsg.setType(opType);
			List<AsyncPvpRoleAttr> pvpRoleAttrList = result.getPvpRoleAttrList();
			if (Util.isEmpty(pvpRoleAttrList)) {
				role.getBehavior().sendMessage(respMsg);
				return;
			}
			List<PetPvpRoleInfoItem> roleInfoItems = Lists.newArrayList();
			for (AsyncPvpRoleAttr roleAttr : pvpRoleAttrList) {
				int petId = roleAttr.getPetId();
				GoodsPet template = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, petId);
				if (null == template) {
					continue;
				}

				PetPvpRoleInfoItem item = new PetPvpRoleInfoItem();
				item.setRoleId(roleAttr.getRoleId());
				item.setRoleName(roleAttr.getRoleName());
				item.setBattleScore(roleAttr.getBattleScore());
				item.setPetId(roleAttr.getPetId());
				item.setPetName(template.getName());
				item.setRoleLevel((byte) role.getLevel());
				HeroBattleItem heroBattleItem = this.createHeroBattleItem(roleAttr.getRoleId());
				if (null == heroBattleItem) {
					continue;
				}
				item.setHeroBattleItem(heroBattleItem);
				roleInfoItems.add(item);
			}

			// 排序
			Comparator<PetPvpRoleInfoItem> comparator = new Comparator<PetPvpRoleInfoItem>() {
				@Override
				public int compare(PetPvpRoleInfoItem r1, PetPvpRoleInfoItem r2) {
					if (r1.getBattleScore() < r2.getBattleScore()) {
						return 1;
					}
					return 0;
				}
			};
			Collections.sort(roleInfoItems, comparator);
			respMsg.setPvpRoleInfoItemList(roleInfoItems);
			PetPvpRefresh refresh = this.getPetPvpRefreshConfig(role.getLevel());
			if (null != refresh) {
				respMsg.setSilverMoney(refresh.getSilverMoney());
			}
			role.getBehavior().sendMessage(respMsg);

		} catch (Exception ex) {
			logger.error("goddessAppImpl.sendPvpInfoPanel error", ex);
		}
	}

	private HeroBattleItem createHeroBattleItem(String roleId) {
		RoleHero roleHero = this.getRoleHero(roleId);
		if (null == roleHero) {
			return null;
		}
		GoodsHero goodsHero = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, roleHero.getHeroId());
		if (null == goodsHero) {
			return null;
		}
		HeroBattleItem item = new HeroBattleItem();
		item.setImageId(goodsHero.getImageId());
		item.setLevel((byte) roleHero.getLevel());
		item.setQuality(roleHero.getQuality());
		item.setStar(roleHero.getStar());
		item.setGearId(goodsHero.getGearId());
		item.setSeriesId(goodsHero.getSeriesId());
		return item;
	}

	private RoleHero getRoleHero(String roleId) {
		List<RoleHero> roleHeroList = GameContext.getHeroApp().getRoleSwitchableHeroList(roleId);
		if (Util.isEmpty(roleHeroList)) {
			return null;
		}
		return roleHeroList.get(0);
	}

	// 得到玩家列表
	private PetPvpInfoListResult getPvpInfoList(RoleInstance role, byte opType, boolean isRefresh) {
		PetPvpInfoListResult result = new PetPvpInfoListResult();
		List<String> roleIdList = this.getPvpRoleIdList(role, opType, isRefresh);
		// 得到抢夺信息列表
		List<AsyncPvpRoleAttr> pvpRoleAttrList = Lists.newArrayList();
		if (Util.isEmpty(roleIdList)) {
			result.setPvpRoleAttrList(pvpRoleAttrList);
			result.success();
			return result;
		}
		List<String> offlineRoleIds = Lists.newArrayList();
		for (String roleId : roleIdList) {
			RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if (null == targetRole) {
				offlineRoleIds.add(roleId);
				continue;
			}
			AsyncPvpRoleAttr attr = new AsyncPvpRoleAttr(targetRole);
			RolePet rolePet = this.getBattleRolePet(targetRole.getRoleId());
			if (null != rolePet) {
				attr.setPetId(rolePet.getPetId());
			}
			pvpRoleAttrList.add(attr);
		}
		if (!Util.isEmpty(offlineRoleIds)) {
			pvpRoleAttrList.addAll(GameContext.getAsyncPvpApp().getAsyncPvpRoleAttrList(offlineRoleIds));
		}
		result.setPvpRoleAttrList(pvpRoleAttrList);
		result.success();
		return result;
	}

	// 获得抢夺玩家ID列表
	private List<String> getPvpRoleIdList(RoleInstance role, byte opType, boolean isRefresh) {
		List<String> roleIdList = Lists.newArrayList();
		if (ONE == opType) {
			RolePetStatus status = GameContext.getUserPetApp().getRolePetStatus(role.getRoleId());
			if (null == status) {
				return null;
			}
			roleIdList.addAll(status.getRoberRoleIdSet());
			// 防止复仇列表过长，取最新的20条
			int size = roleIdList.size();
			if (size > TWENTY) {
				return roleIdList.subList(size - TWENTY, size);
			}
			return roleIdList;
		}
		RolePetBattleList petBattle = GameContext.getPetStorage().getRolePetBattleList(role.getRoleId());
		List<String> oldBattleList = Lists.newArrayList();
		if (null == petBattle) {
			roleIdList = this.getNewPvpRoleIdList(role, oldBattleList);
			return roleIdList;
		}
		oldBattleList = petBattle.getBattleRoleList();
		if (Util.isEmpty(oldBattleList)) {
			roleIdList = this.getNewPvpRoleIdList(role, oldBattleList);
			return roleIdList;
		}
		if (isRefresh) {
			roleIdList = this.getNewPvpRoleIdList(role, oldBattleList);
			return roleIdList;
		}
		Date now = new Date();
		if (DateUtil.sameDay(now, petBattle.getOperateDate())) {
			return oldBattleList;
		}
		roleIdList = this.getNewPvpRoleIdList(role, oldBattleList);
		return roleIdList;

	}

	// 得到新的抢夺ID列表
	private List<String> getNewPvpRoleIdList(RoleInstance role, List<String> oldBattleList) {
		int battleScore = role.getBattleScore();
		// 根据战斗力，取得抢夺列表
		Set<String> backupRoleIdSet = this.getBattleRoleList(role.getRoleId(), battleScore, TEN, oldBattleList);
		if (backupRoleIdSet.size() < FIVE) {
			backupRoleIdSet = this.getBattleRoleList(role.getRoleId(), battleScore, TWENTY, oldBattleList);
		}

		// 不够5个从老列表中随机抽取补足
		if (backupRoleIdSet.size() <= FIVE) {
			if (!Util.isEmpty(oldBattleList)) {
				if (backupRoleIdSet.size() + oldBattleList.size() <= FIVE) {
					backupRoleIdSet.addAll(oldBattleList);
					List<String> battleList = new ArrayList<String>(backupRoleIdSet);
					this.saveRolePetBattleList(role, battleList);
					return battleList;
				}
				while (backupRoleIdSet.size() < FIVE) {
					String roleId = oldBattleList.get(Util.randomInt(0, oldBattleList.size() - 1));
					backupRoleIdSet.add(roleId);
				}
			}
			List<String> battleList = new ArrayList<String>(backupRoleIdSet);
			this.saveRolePetBattleList(role, battleList);
			return battleList;
		}
		// 大于5个则按随机算法取
		List<String> backupRoleIds = new ArrayList<String>(backupRoleIdSet);
		Set<String> realRoleIdSet = new HashSet<String>();
		int size = backupRoleIds.size();
		while (realRoleIdSet.size() < FIVE) {
			String roleId = backupRoleIds.get(Util.randomInt(0, size - 1));
			if (realRoleIdSet.contains(roleId)) {
				continue;
			}
			realRoleIdSet.add(roleId);
		}
		List<String> battleList = new ArrayList<String>(realRoleIdSet);
		this.saveRolePetBattleList(role, battleList);
		return battleList;
	}

	// 保存抢夺列表到SSDB
	private void saveRolePetBattleList(RoleInstance role, List<String> battleList) {
		String roleId = role.getRoleId();
		RolePetBattleList petBattleList = new RolePetBattleList();
		petBattleList.setBattleRoleList(battleList);
		petBattleList.setOperateDate(new Date());
		petBattleList.setRoleId(roleId);
		GameContext.getPetStorage().saveRolePetBattleList(roleId, petBattleList);
	}

	// 从SSDB中取出可参加抢夺的角色ID列表
	private Set<String> getBattleRoleList(String roleId, int battleScore, int limit, List<String> oldBattleList) {
		Map<String, String> battleScoreMap = GameContext.getPetStorage().getRolePetBattleScores(roleId, battleScore, limit);
		if (null == battleScoreMap) {
			return null;
		}

		// 将原列表中的ID移除出去
		if (!Util.isEmpty(oldBattleList)) {
			for (String oldId : oldBattleList) {
				battleScoreMap.remove(oldId);
			}
		}

		Set<String> backupRoleIds = Sets.newHashSet();
		for (Entry<String, String> entry : battleScoreMap.entrySet()) {
			String targetId = entry.getKey();
			if (Util.isEmpty(targetId)) {
				continue;
			}
			if (roleId.equals(targetId)) {
				continue;
			}
			// 判断目标的被抢夺次数
			if (this.getPvpRemainNum(targetId, ONE) <= 0) {
				continue;
			}
			backupRoleIds.add(targetId);
		}
		return backupRoleIds;
	}

	// 刷新抢夺列表
	@Override
	public Result refreshPvpInfoList(RoleInstance role) {
		Result result = new Result();
		PetPvpRefresh refresh = this.getPetPvpRefreshConfig(role.getLevel());
		if (null == refresh) {
			return result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
		}
		// 判断刷新列表条件
		int silverMoney = refresh.getSilverMoney();
		if (silverMoney <= 0) {
			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
		}
		// 【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, silverMoney);
		if (ar.isIgnore()) {
			return ar;
		}
		if (!ar.isSuccess()) {
			return result.setInfo(GameContext.getI18n().messageFormat(TextId.NOT_ENOUGH_GAME_MOENY, silverMoney));
		}
		if (silverMoney > 0) {
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, OperatorType.Decrease, silverMoney, OutputConsumeType.pet_pvp_refresh_consume);
			role.getBehavior().notifyAttribute();
		}
		// 发送新的宠物抢夺列表
		this.sendPvpInfo(role, ZERO, true);
		return result.success();
	}

	// 抢夺结束处理
	@Override
	public void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType type) {
		if (null == role) {
			return;
		}
		String roleId = role.getRoleId();
		try {
			if (null == battleInfo) {
				return;
			}
			RolePetStatus status = GameContext.getUserPetApp().getRolePetStatus(roleId);
			if (null == status) {
				return;
			}
			// 失败
			if (type == ChallengeResultType.Lose) {
				C0004_TipTitleNotifyMessage tipMsg = new C0004_TipTitleNotifyMessage();
				tipMsg.setTitle(this.getText(TextId.Pet_PvP_Title));
				byte opType = battleInfo.getOpType();
				if (opType == ZERO) {
					tipMsg.setMsgContext(this.getText(TextId.Pet_PvP_Rob_Lose));
					role.getBehavior().sendMessage(tipMsg);
					return;
				}
				tipMsg.setMsgContext(this.getText(TextId.Pet_PvP_Revenge_Lose));
				role.getBehavior().sendMessage(tipMsg);
				return;
			}
			// 成功处理结果
			this.handleAttacker(role, battleInfo, status);
			this.handleDefender(role, battleInfo);
		} catch (Exception e) {
			logger.error("PetApp.challengeOver error: ", e);
		}
	}

	// 处理发起挑战方
	private void handleAttacker(RoleInstance role, AsyncPvpBattleInfo info, RolePetStatus status) {
		byte opType = info.getOpType();
		// 更新计数,复仇不计
		if (ZERO == opType) {
			status.updateNum(opType);
		}
		// 发奖
		List<GoodsOperateBean> goodsList = Lists.newArrayList();
		String tips = null;
		String context = null;
		if (opType == ZERO) {
			goodsList.add(this.petPvpConfig.getRobAwardGoods());
			this.sendMail(role.getRoleId(), this.getText(TextId.Pet_PvP_Rob_Mail), goodsList);
			tips = GameContext.getI18n().getText(TextId.Pet_PvP_Rob_Mail);
			context = GameContext.getI18n().messageFormat(TextId.Pet_pvp_rob_win, info.getTargetRoleName(), info.getPetName());
		} else {
			goodsList.add(this.petPvpConfig.getRevengeAwardGoods());
			this.sendMail(role.getRoleId(), this.getText(TextId.Pet_PvP_Rev_Mail), goodsList);
			status.getRoberRoleIdSet().remove(info.getTargetRoleId());
			tips = GameContext.getI18n().getText(TextId.Pet_PvP_Rev_Mail);
			context = GameContext.getI18n().getText(TextId.Pet_pvp_revenge_win);
		}
		// 弹板提示结果
		C0004_TipTitleNotifyMessage tipMsg = new C0004_TipTitleNotifyMessage();
		tipMsg.setTitle(tips);
		tipMsg.setMsgContext(context);
		role.getBehavior().sendMessage(tipMsg);
		this.sendPvpInfo(role, opType, false);
	}

	// 发送邮件
	private void sendMail(String roleId, String context, List<GoodsOperateBean> goodsList) {
		String title = this.getText(TextId.Pet_PvP_Title);
		OutputConsumeType ocType = OutputConsumeType.pet_pvp_award_mail;
		GameContext.getMailApp().sendMail(roleId, title, context, MailSendRoleType.System.getName(), ocType.getType(), goodsList);
	}

	// 处理被挑战方
	private void handleDefender(RoleInstance attackerRole, AsyncPvpBattleInfo info) {
		if (info.getOpType() != ZERO) {
			return;
		}
		String attackerRoleId = attackerRole.getRoleId();
		String defenderRoleId = info.getTargetRoleId();
		RolePetStatus status = null;
		// 在线
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(defenderRoleId);
		if (isOnline) {
			status = GameContext.getUserPetApp().getRolePetStatus(defenderRoleId);
		} else {
			status = GameContext.getPetStorage().getRolePetStatus(defenderRoleId);
		}
		if (null == status) {
			return;
		}
		status.updateNum(ONE);
		status.getRoberRoleIdSet().add(attackerRoleId);
		if (!isOnline) {
			GameContext.getPetStorage().saveRolePetStatus(status);
		}
		String context = GameContext.getI18n().messageFormat(TextId.Pet_Pvp_Def_Mail, attackerRole.getRoleName(), info.getPetName());
		this.sendMail(defenderRoleId, context, null);
	}

	// 获得出战宠物信息
	@Override
	public PetBattleItem getOnBattlePetItem(String roleId) {
		PetBattleItem item = new PetBattleItem();
		RolePet rolePet = GameContext.getUserPetApp().getOnBattleRolePet(roleId);
		if (null == rolePet) {
			return item;
		}
		item.setLevel((byte) rolePet.getLevel());
		item.setPetInstanceId(rolePet.getPetInstanceId());
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		if (null != goodsPet) {
			item.setPetName(goodsPet.getName());
			item.setPetResId((short) goodsPet.getResId());
			item.setRatio(goodsPet.getRatio());
			item.setAttackDistance(goodsPet.getAttackDistance());
		}
		return item;
	}

	// 使用宠物技能
	@Override
	public void rolePetUseSkill(RoleInstance role, AbstractRole targetRole) {
		try {
			RolePet rolePet = GameContext.getUserPetApp().getOnBattleRolePet(role.getRoleId());
			if (null == rolePet) {
				return;
			}
			if (null == targetRole || targetRole.isDeath()) {
				return;
			}
			// 更新宠物位置
			rolePet.setMapX(role.getMapX());
			rolePet.setMapY(role.getMapY());
			rolePet.setDir(role.getDir());
			rolePet.setTarget(targetRole);
			short skillId = rolePet.selectSkillId();
			if (skillId <= 0) {
				// 没有可用技能
				return;
			}
			// 首先执行buff(法宝的buff是没有在主循环中执行的)
			GameContext.getUserBuffApp().runBuff(rolePet, 0);
			GameContext.getUserSkillApp().useSkill(rolePet, skillId, 0, false);
		} catch (Exception ex) {
			logger.error("RolePetUseSkill error", ex);
		}
	}

	// 满星后宠物属性列表显示当前属性
	@Override
	public List<AttriTypeStrValueItem> getFullStarAttriList(RolePet rolePet) {
		return this.getRolePetAttriStrItemList(rolePet);
	}

	// 获得可以打开该孔的星级
	@Override
	public byte getOpenHoleStar(byte hole) {
		for (int i = 0; i < this.petStarHoleConfig.size(); i++) {
			PetStarHoleConfig config = this.getPetStarHoleConfig((byte) i);
			if (null == config) {
				continue;
			}
			if (config.getHoleNum() >= hole + 1) {
				return config.getStar();
			}
		}
		return 0;
	}

	// 获得出战宠物加成
	@Override
	public AttriBuffer getOnBattleRolePetBuffer(RoleInstance role) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		RolePet onBattle = GameContext.getUserPetApp().getOnBattleRolePet(role.getRoleId());
		if (null != onBattle) {
			buffer.append(this.getRolePetAttriBuffer(onBattle));
		}
		return buffer;
	}

	// 获得拥有宠物个数
	@Override
	public int getRolePetNumber(String roleId) {
		Map<Integer, RolePet> all = GameContext.getUserPetApp().getAllRolePet(roleId);
		if (Util.isEmpty(all)) {
			return 0;
		}
		return all.size();
	}

	// 获得镶嵌符文列表
	@Override
	public List<PetMosaicRuneItem> getMosaicRuneItemList(RolePet rolePet) {
		Map<Byte, MosaicRune> mosaicMap = rolePet.getMosaicRuneMap();
		if (Util.isEmpty(mosaicMap)) {
			return null;
		}
		List<PetMosaicRuneItem> runeItemList = Lists.newArrayList();
		for (Entry<Byte, MosaicRune> mosaicRune : mosaicMap.entrySet()) {
			PetMosaicRuneItem item = new PetMosaicRuneItem();
			item.setHole(mosaicRune.getValue().getHole());
			item.setGoodsLiteItem(mosaicRune.getValue().getGoodsLiteItem());
			runeItemList.add(item);
		}
		return runeItemList;
	}

	@Override
	public RolePet getShowRolePet(String targetId) {
		if (GameContext.getOnlineCenter().isOnlineByRoleId(targetId)) {
			return GameContext.getUserPetApp().getOnBattleRolePet(targetId);
		}
		RolePetShow showRolePet = GameContext.getPetStorage().getShowRolePet(targetId);
		if (null == showRolePet) {
			return null;
		}
		return showRolePet.createRolePet();
	}

	@Override
	public int getMosaicLevelRuneNum(String roleId, byte level) {
		Map<Integer, RolePet> allRolePet = GameContext.getUserPetApp().getAllRolePet(roleId);
		if (Util.isEmpty(allRolePet)) {
			return 0;
		}
		int total = 0;
		for (RolePet rolePet : allRolePet.values()) {
			if (null == rolePet) {
				continue;
			}
			total += rolePet.countGemLevel(level);
		}
		return total;
	}

}
