package com.game.draco.app.pet;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.ChallengeResultType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.pet.config.AttributePetBornConfig;
import com.game.draco.app.pet.config.AttributePetLevelConfig;
import com.game.draco.app.pet.config.AttributePetQualityConfig;
import com.game.draco.app.pet.config.AttributePetRateConfig;
import com.game.draco.app.pet.config.AttributePetTypeConfig;
import com.game.draco.app.pet.config.PetPvpConfig;
import com.game.draco.app.pet.config.PetStarUpConfig;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.pet.vo.PetMosaicResult;
import com.game.draco.app.pet.vo.PetSwallowResult;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.PetMosaicRuneItem;
import com.game.draco.message.item.PetBattleItem;
import com.game.draco.message.item.PetListItem;
import com.game.draco.message.item.PetSwallowItem;
import com.game.draco.message.item.SkillSimpleItem;

public interface PetApp extends Service, AppSupport {
	
	public List<PetListItem> getPetList(RoleInstance role);
	
	public List<AttriTypeStrValueItem> getRolePetAttriStrItemList(RolePet rolePet);
	
	public List<SkillSimpleItem> getSkillSimpleItemList(RolePet rolePet);
	
	public Result petExchange(RoleInstance role, int petId);
	
	public Result petStarUp(RoleInstance role, RolePet rolePet);
	
	public Result petOnBattle(RoleInstance role, RolePet rolePet);
	
	public Result petOffBattle(RoleInstance role);
	
	public Result usePetGoods(RoleInstance role, RoleGoods roleGoods, boolean confirm);
	
	public byte[] getHoleInfo(RolePet rolePet);
	
	public int getMaxExp(RolePet rolePet);
	
	public boolean isPetMaxStar(RolePet rolePet);
	
	public int getStarShadowNumber(RolePet rolePet);
	
	public List<AttriTypeStrValueItem> getStarChangeAttriList(RolePet rolePet);
	
	public List<AttriTypeStrValueItem> getFullStarAttriList(RolePet rolePet);
	
	public PetSwallowResult petSwallow(RoleInstance role, RolePet rolePet, List<PetSwallowItem> swallowList);
	
	public int getPetBattleScore(RolePet rolePet);
	
	public byte getPetSkillLevel(RolePet rolePet, short skillId);
	
	public PetMosaicResult rolePetMosaicRune(RoleInstance role, RolePet rolePet, RoleGoods rg, byte holeNum);
	
	public PetMosaicResult rolePetDismountRune(RoleInstance role, RolePet rolePet, byte holeNum);
	
	public Result petChallenge(RoleInstance role, String targetRoleId, String targetRoleName, int targetPetId, byte opType);
	
	public void challengeOver(RoleInstance role, AsyncPvpBattleInfo battleInfo, ChallengeResultType type);
	
	public void sendPvpInfo(RoleInstance role, byte opType, boolean isRefresh);
	
	public Result refreshPvpInfoList(RoleInstance role);
	
	public AttributePetRateConfig getAttributePetRateConfig(int petId);
	
	public AttributePetTypeConfig getAttributePetTypeConfig();
	
	public AttributePetBornConfig getAttributePetBornConfig(byte quality, byte star);
	
	public AttributePetQualityConfig getAttributePetQualityConfig(byte quality, byte star);
	
	public AttributePetLevelConfig getAttributePetLevelConfig(int level);
	
	public PetPvpConfig getPetPvpConfig();
	
	public PetBattleItem getOnBattlePetItem(String roleId);
	
	public void rolePetUseSkill(RoleInstance role, AbstractRole targetRole);
	
	public RolePet getBattleRolePet(String roleId);
	
	public byte getOpenHoleStar(byte hole);
	
	public AttriBuffer getOnBattleRolePetBuffer(RoleInstance role);
	
	public int getRolePetNumber(String roleId);
	
	public List<PetMosaicRuneItem> getMosaicRuneItemList(RolePet rolePet);
	
	/**
	 * @see 查看某玩家出战宠物，如果目标没有宠物，返回null
	 * @param targetId 角色ID
	 * @return
	 */
	public RolePet getShowRolePet(String targetId);
	
	public PetStarUpConfig getPetStarUpConfig(byte quality, byte star);
	
	public int getMosaicLevelRuneNum(String roleId, byte level);
	
	/**
	 * 增加出战宠物经验
	 * @param role
	 * @param exp
	 */
	public void changeBattlePetExp(RoleInstance role, int exp);
	
	public AttriBuffer getPetAttriBuffer(int petId, int petLevel, int quality, byte star);
	
}
