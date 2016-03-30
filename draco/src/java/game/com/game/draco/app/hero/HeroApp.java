package com.game.draco.app.hero;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hero.config.HeroBaseConfig;
import com.game.draco.app.hero.config.HeroLevelup;
import com.game.draco.app.hero.config.HeroQualityUpgrade;
import com.game.draco.app.hero.domain.HeroEquip;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.vo.HeroQualityUpgradeResult;
import com.game.draco.app.hero.vo.HeroSwallowResult;
import com.game.draco.message.item.HeroInfoItem;
import com.game.draco.message.item.HeroLoveItem;
import com.game.draco.message.item.HeroSwallowItem;
import com.game.draco.message.response.C1268_HeroQualityInfoRespMessage;

public interface HeroApp extends Service, AppSupport{

	HeroBaseConfig getHeroBaseConfig() ;
	HeroLevelup getHeroLevelup(int heroQuality,int level);
	int getSwallowExp(RoleHero roleHero) ;
	public int getMaxLevel(int heroQuality) ;
	public void syncBattleScore(RoleInstance role,int heroId,boolean notify);
	public int getBattleScore(RoleHero roleHero);
	public int getNotOnBattleScore(String roleId);
	Result onBattle(RoleInstance role,int heroId) ;
	Result useHeroGoods(RoleInstance role,RoleGoods roleGoods,boolean confirm) throws ServiceException ;
	Result useHeroTemplate(RoleInstance role,GoodsHero goodsHero) ;
	AttriBuffer getHeroAttriBuffer(RoleHero hero) ;
	AttriBuffer getAttriBuffer(RoleInstance role) ;
	
	public HeroSwallowResult heroSwallow(RoleInstance role,int sourceHeroId,List<HeroSwallowItem> swallowList) ;
	public void saveRoleHero(RoleHero roleHero) ;
	public List<Integer> getHeroIdentifyList() ;
	public Result heroExchange(RoleInstance role,int heroId) ;
	
	public void onHorseStarChanged(int roleId,int horseId,int quality,int star,int preQuality,int preStar);
	public void onHorseAdded(int roleId,int horseId,int quality,int star);
	public void onPetStarChanged(int roleId,int petId,int quality,int star,int preQuality,int preStar);
	public void onPetAdded(int roleId,int petId,int quality,int star) ;
	public void onPetRemoved(int roleId,int horseId,int quality,int star);
	
	public AttriBuffer getBaseAttriBuffer(int heroId,int heroLevel,int quality,int star);
	public HeroInfoItem getHeroInfoItem(RoleHero hero) ;
	public HeroQualityUpgrade getHeroQualityUpgrade(int quality,int star);
	public boolean isReachMaxQuality(RoleHero hero) ;
	public AttriBuffer getHeroGivenAttriBuffer(RoleHero hero, int givenHeroLevel,
			int givenQuality, int givenStar);
	
	public List<AttributeType> getAttributeTypeList() ;
	
	public HeroQualityUpgradeResult heroQualityUpgrade(RoleInstance role,int heroId);
	
	public C1268_HeroQualityInfoRespMessage buildHeroQualityInfoMessage(RoleHero hero);
	
	public short getRoleHeroHeadId(String roleId) ;
	
	public short getRoleHeroResId(String roleId);
	
	public short getHeroHeadId(int heroId ) ;
	
	public RoleHero getRoleHero(String roleId,int heroId) ;
	
	public RoleHero insertHeroDb(String roleId,GoodsHero goodsHero);
	
	//public List<HeroSwitchableInfoItem> getSwitchableHeroInfoList(String roleId);
	
	//public List<HeroSwitchableInfoItem> getNonSwitchableHeroInfoList(String roleId);
	
	public Result updateSwitchableHero(RoleInstance role,int[] heroList,int[] helpHeroList) ;
	
	public Result systemUpdateSwitchableHero(RoleInstance role,int[] heroList,int[] helpHeroList) ;
	
	public Message getHeroMainUiMessage(RoleInstance role) ;
	
	public short getOnBattleCd(RoleInstance role);
	
	public List<RoleHero> getRoleSwitchableHeroList(String roleId) ;
	
	public Result systemAutoOnBattle(RoleInstance role,int heroId);
	
	public byte getMaxSwitchableHeroNum(RoleInstance role) ;
	
	public List<HeroLoveItem> getHeroLoveItemList(int heroId) ;
	
	public List<HeroLoveItem> getHeroLoveItemList(String roleId,int heroId) ;
	
	public boolean isSwitchableHero(String roleId,int heroId);
	
	public boolean isOnBattleHero(String roleId,int heroId) ;
	

	/**
	 * 取当前出战的英雄的战斗力
	 * @param roleId
	 * @return
	 */
	public int getOnBattleHeroBattleScore(String roleId);

	public void onRoleLevelUp(RoleInstance role) ;
	
	/**
	 * 角色的英雄中xx级英雄的个数
	 * @param roleId
	 * @return 英雄个数
	 */
	public int getHeroLevelNum(String roleId, int level);
	
	/**
	 * 返回角色英雄个数
	 * @param roleId
	 * @return
	 */
	public int getRoleHeroNum(String roleId);
	
	/**
	 * 角色的英雄中xx品质xx星级的个数,eg：quality=1, star=2则：quality>1 或者 (quality=1,star>=1)
	 * @param roleId
	 * @param quality
	 * @param star
	 * @return
	 */
	public int getHeroQualityStarNum(String roleId, byte quality, byte star);
	
	public int getEquipStrengthenLevel(String roleId,int level);
	
	public int getEquipMosaicLevel(String roleId,int level);
	
	public int getEquipQualityNum(String roleId,int quality);
	
	public int getSkillLevelNum(String roleId,int level) ;
	
	public HeroEquip getHeroEquipCache(String roleId) ;
	
	public List<RoleGoods> getAllHeroEquipList(String roleId) ;
	
	public int getAstaff(String roleId,int heroId) ;
	
	public byte getMaxStar(int quality) ;
	
	void initSkill(RoleHero hero);
	
	void preToStore(RoleHero hero);
	
	/**
	 * 物品为英雄物品 && 角色当前没有此英雄时使用成功
	 * 否则使用失败
	 * @param role
	 * @param goodsId
	 * @return
	 */
	public Result useHeroGoods(RoleInstance role,int goodsId) ;
	
	public Message getHeroSwitchUiMessage(RoleInstance role)  ;
	
	public void addHeroExp(RoleInstance role,int exp) ;
	
	public Result useHeroBySystem(RoleInstance role,int heroId) ;
	
	public Result deleteHeroBySystem(RoleInstance role,int heroId) ;
	
	public void hpHealth(RoleInstance role) ;
	
	public void pushHeroMusicList(RoleInstance role) ;

	public void pushHeroEquipOpenCond(RoleInstance role) ;
	
	public void switchableHeroPerfectBody(RoleInstance role) ;
	public Map<String, List<RoleGoods>> buildHeroEquipMap(String roleId);
	
	public byte[] getSwitchOpenLevel() ;
	public byte[] getHelpOpenLevel() ;
	List<RoleHero> helpHeros(String roleId);
}
