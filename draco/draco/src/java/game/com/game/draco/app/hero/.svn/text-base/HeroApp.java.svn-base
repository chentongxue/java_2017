package com.game.draco.app.hero;

import java.util.List;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.hero.config.HeroBaseConfig;
import com.game.draco.app.hero.config.HeroEquipOpen;
import com.game.draco.app.hero.config.HeroLevelup;
import com.game.draco.app.hero.config.HeroLove;
import com.game.draco.app.hero.config.HeroLuck;
import com.game.draco.app.hero.config.HeroLuckGoodsConfig;
import com.game.draco.app.hero.config.HeroQualityUpgrade;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.hero.vo.HeroQualityUpgradeResult;
import com.game.draco.app.hero.vo.HeroSwallowResult;
import com.game.draco.app.hero.vo.LuckLotteryResult;
import com.game.draco.message.item.HeroInfoItem;
import com.game.draco.message.item.HeroMarkingItem;
import com.game.draco.message.item.HeroSwallowItem;
import com.game.draco.message.response.C1264_HeroLuckPanelRespMessage;
import com.game.draco.message.response.C1268_HeroQualityInfoRespMessage;

public interface HeroApp extends Service{

	HeroBaseConfig getHeroBaseConfig() ;
	HeroLevelup getHeroLevelup(int heroQuality,int level);
	HeroEquipOpen getHeroEquipOpen(int equipPosId);
	HeroLuck getHeroLuck(int luckTypeId);
	HeroLuckGoodsConfig getHeroLuckGoodsConfig(int luckTypeId,boolean first) ;
	int getSwallowExp(RoleHero roleHero) ;
	public int getMaxLevel(int heroQuality) ;
	public int getBattleScore(RoleHero roleHero);
	Result onBattle(RoleInstance role,int heroId) ;
	void login(RoleInstance role) ;
	void logout(RoleInstance role) ;
	Result useHeroGoods(RoleInstance role,RoleGoods roleGoods,boolean confirm) throws ServiceException ;
	Result useHeroTemplate(RoleInstance role,GoodsHero goodsHero) throws Exception ;
	AttriBuffer getHeroAttriBuffer(RoleHero hero) ;
	AttriBuffer getAttriBuffer(RoleInstance role) ;
	/**
	 * 装备位是否为开启状态
	 * 包括:
	 * 1) 免费默认开启
	 * 2）用户开启
	 * @param hero
	 * @param pos
	 * @return
	 */
	boolean isEquipPosOpenOrFreeOpen(RoleInstance role,int pos) ;
	public Result openEquipPos(RoleInstance role,int pos) ;
	public HeroSwallowResult heroSwallow(RoleInstance role,int sourceHeroId,List<HeroSwallowItem> swallowList) ;
	public HeroMarkingItem buildHeroMarkingItem(RoleHero hero,byte markingType) ;
	public Result markingOn(RoleInstance role,int heroId,byte markingType);
	public Result markingOff(RoleInstance role,int heroId,byte markingType) ;
	public void saveRoleHero(RoleHero roleHero) ;
	public List<Integer> getHeroExchangeList() ;
	public Result heroExchange(RoleInstance role,int heroId) ;
	public C1264_HeroLuckPanelRespMessage buildHeroLuckPanel(RoleInstance role) ;
	public LuckLotteryResult heroLuckLottery(RoleInstance role,byte typeId);
	public HeroLove getHeroLove(int heroTid,byte loveType) ;
	public byte getHeroLoveStatus(RoleHero hero,byte loveType) ;
	
	public void onHorseChanged(int roleId,int currentHorseId,int preHorseId);
	public void onGoddessChanged(int roleId,int currentGoddessId,int preGoddessId);
	public AttriBuffer getBaseAttriBuffer(int heroId,int heroLevel,int quality,int star);
	public HeroInfoItem getHeroInfoItem(RoleHero hero) ;
	public HeroQualityUpgrade getHeroQualityUpgrade(int quality,int star);
	public boolean isReachMaxQuality(RoleHero hero) ;
	public AttriBuffer getHeroGivenAttriBuffer(RoleHero hero, int givenHeroLevel,
			HeroLoveType givenLoveType, int givenLoveTargetId,
			int givenQuality, int givenStar);
	
	public List<AttributeType> getAttributeTypeList() ;
	
	public HeroQualityUpgradeResult heroQualityUpgrade(RoleInstance role,int heroId);
	
	public C1268_HeroQualityInfoRespMessage buildHeroQualityInfoMessage(RoleHero hero);
	
	public short getRoleHeroHeadId(String roleId) ;
	
	public RoleHero insertHeroDb(String roleId,GoodsHero goodsHero);
}
