package com.game.draco;

import java.util.Date;

import org.project.protobuf.codec.bytes.ByteProtoBuffer;
import org.quartz.CronTrigger;

import sacred.alliance.magic.admin.AdminApp;
import sacred.alliance.magic.app.active.ActiveApp;
import sacred.alliance.magic.app.active.ActiveSupport;
import sacred.alliance.magic.app.active.angelchest.AngelChestApp;
import sacred.alliance.magic.app.active.dps.ActiveDpsApp;
import sacred.alliance.magic.app.active.siege.ActiveSiegeApp;
import sacred.alliance.magic.app.ai.AiApp;
import sacred.alliance.magic.app.announce.AnnounceApp;
import sacred.alliance.magic.app.arena.Arena1V1App;
import sacred.alliance.magic.app.arena.Arena3V3App;
import sacred.alliance.magic.app.arena.ArenaApp;
import sacred.alliance.magic.app.arena.learn.ArenaLearnApp;
import sacred.alliance.magic.app.arena.top.ArenaTopApp;
import sacred.alliance.magic.app.attri.AttriApp;
import sacred.alliance.magic.app.attri.calct.CalctManager;
import sacred.alliance.magic.app.auction.AuctionApp;
import sacred.alliance.magic.app.battle.BattleApp;
import sacred.alliance.magic.app.battle.calculate.CalculateExpApp;
import sacred.alliance.magic.app.broadcast.BroadcastApp;
import sacred.alliance.magic.app.carnival.CarnivalApp;
import sacred.alliance.magic.app.charge.ChargeApp;
import sacred.alliance.magic.app.chest.ChestApp;
import sacred.alliance.magic.app.clienttarget.ClientTargetApp;
import sacred.alliance.magic.app.config.ChangeParamByServer;
import sacred.alliance.magic.app.config.ChargeConfig;
import sacred.alliance.magic.app.config.DoorDogConfig;
import sacred.alliance.magic.app.config.EnvConfig;
import sacred.alliance.magic.app.config.FactionConfig;
import sacred.alliance.magic.app.config.HeartBeatConfig;
import sacred.alliance.magic.app.config.IdFactoryConfig;
import sacred.alliance.magic.app.config.InviteConfig;
import sacred.alliance.magic.app.config.OpenSecretkeyConfig;
import sacred.alliance.magic.app.config.ParasConfig;
import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.app.config.PlatformConfig;
import sacred.alliance.magic.app.config.QuickBuyConfig;
import sacred.alliance.magic.app.config.SetConfig;
import sacred.alliance.magic.app.config.SkillConfig;
import sacred.alliance.magic.app.config.TimingWriteDBConfig;
import sacred.alliance.magic.app.config.TokenSecretkeyConfig;
import sacred.alliance.magic.app.count.CountApp;
import sacred.alliance.magic.app.crontab.SchedulerApp;
import sacred.alliance.magic.app.doordog.DoorDogApp;
import sacred.alliance.magic.app.fall.FallApp;
import sacred.alliance.magic.app.goods.GoodsApp;
import sacred.alliance.magic.app.goods.GoodsContainerApp;
import sacred.alliance.magic.app.invite.InviteApp;
import sacred.alliance.magic.app.log.StatLogApp;
import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.app.map.worldmap.WorldMapApp;
import sacred.alliance.magic.app.menu.MenuApp;
import sacred.alliance.magic.app.msgcenter.MessageCenter;
import sacred.alliance.magic.app.notify.AreaServerClient;
import sacred.alliance.magic.app.notify.AreaServerNotifyApp;
import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.app.path.AutoSearchRoadApp;
import sacred.alliance.magic.app.pk.PkApp;
import sacred.alliance.magic.app.quickbuy.QuickBuyApp;
import sacred.alliance.magic.app.rate.RateApp;
import sacred.alliance.magic.app.recall.RecallApp;
import sacred.alliance.magic.app.recoup.RecoupApp;
import sacred.alliance.magic.app.role.RoleBornApp;
import sacred.alliance.magic.app.role.RoleRebornApp;
import sacred.alliance.magic.app.set.PublicSetApp;
import sacred.alliance.magic.app.summon.SummonApp;
import sacred.alliance.magic.app.token.TokenApp;
import sacred.alliance.magic.app.trading.TradingApp;
import sacred.alliance.magic.app.treasure.TreasureApp;
import sacred.alliance.magic.app.user.UserAttributeApp;
import sacred.alliance.magic.app.user.UserGoodsApp;
import sacred.alliance.magic.app.user.UserMapApp;
import sacred.alliance.magic.app.user.UserRoleApp;
import sacred.alliance.magic.app.user.UserWarehouseApp;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.client.Client;
import sacred.alliance.magic.client.http.HttpJsonClient;
import sacred.alliance.magic.codec.impl.bytes.IoBufferMessageParser;
import sacred.alliance.magic.codec.impl.bytes.MinaSecurity;
import sacred.alliance.magic.component.DefaultEventPublisher;
import sacred.alliance.magic.component.script.ScriptSupport;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.server.IceServer;
import sacred.alliance.magic.core.server.MinaServer;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.dao.IdFactoryDAO;
import sacred.alliance.magic.dao.RankDAO;
import sacred.alliance.magic.dao.RoleDAO;
import sacred.alliance.magic.dao.impl.MailDAOImpl;
import sacred.alliance.magic.dao.impl.QuestDAOImpl;
import sacred.alliance.magic.dao.impl.SocialDAOImpl;
import sacred.alliance.magic.dao.impl.UnionDAOImpl;
import sacred.alliance.magic.data.CollectPointLoader;
import sacred.alliance.magic.data.GoodsLoader;
import sacred.alliance.magic.log.LogApp;
import sacred.alliance.magic.module.i18n.LanguageHandler;
import sacred.alliance.magic.scheduler.job.MapLoop;
import sacred.alliance.magic.service.IllegalWordsService;
import sacred.alliance.magic.service.RoleService;
import sacred.alliance.magic.shutdown.ShutDownApp;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.MD5;
import sacred.alliance.magic.util.ParallelRun;
import sacred.alliance.magic.util.Util;

import com.game.draco.app.accumulatelogin.AccumulateLoginApp;
import com.game.draco.app.alchemy.AlchemyApp;
import com.game.draco.app.asyncarena.AsyncArenaApp;
import com.game.draco.app.asyncarena.RoleAsyncArenaApp;
import com.game.draco.app.asyncarena.RoleAsyncArenaStorage;
import com.game.draco.app.asyncpvp.AsyncPvpApp;
import com.game.draco.app.buff.BuffApp;
import com.game.draco.app.buff.UserBuffApp;
import com.game.draco.app.camp.balance.CampBalanceApp;
import com.game.draco.app.camp.war.CampWarApp;
import com.game.draco.app.chat.ChannelManager;
import com.game.draco.app.chat.ChatApp;
import com.game.draco.app.choicecard.ChoiceCardApp;
import com.game.draco.app.choicecard.RoleChoiceCardApp;
import com.game.draco.app.compass.CompassApp;
import com.game.draco.app.copy.CopyLogicApp;
import com.game.draco.app.copy.line.CopyLineApp;
import com.game.draco.app.copy.team.CopyTeamApp;
import com.game.draco.app.dailyplay.DailyPlayApp;
import com.game.draco.app.drama.DramaApp;
import com.game.draco.app.drama.UserDramaApp;
import com.game.draco.app.enhanceoption.EnhanceOptionApp;
import com.game.draco.app.equip.EquipApp;
import com.game.draco.app.exchange.ExchangeApp;
import com.game.draco.app.forward.ForwardApp;
import com.game.draco.app.giftcode.GiftCodeApp;
import com.game.draco.app.goblin.GoblinApp;
import com.game.draco.app.hero.HeroApp;
import com.game.draco.app.hero.HeroStorage;
import com.game.draco.app.hero.UserHeroApp;
import com.game.draco.app.hero.arena.HeroArenaApp;
import com.game.draco.app.hero.arena.HeroArenaStorage;
import com.game.draco.app.hint.HintApp;
import com.game.draco.app.hitcombo.HitComboApp;
import com.game.draco.app.horse.HorseApp;
import com.game.draco.app.horse.RoleHorseApp;
import com.game.draco.app.horse.RoleHorseStorage;
import com.game.draco.app.inslogic.InsLogicDataApp;
import com.game.draco.app.levelgift.LevelGiftApp;
import com.game.draco.app.luckybox.LuckyBoxApp;
import com.game.draco.app.mail.MailApp;
import com.game.draco.app.medal.MedalApp;
import com.game.draco.app.nostrum.NostrumApp;
import com.game.draco.app.npc.NpcApp;
import com.game.draco.app.npc.inspire.NpcInspireApp;
import com.game.draco.app.npc.npcfunction.NpcFunctionApp;
import com.game.draco.app.npc.npcfunction.NpcTeachApp;
import com.game.draco.app.npc.refresh.NpcRefreshApp;
import com.game.draco.app.npc.refreshrule.RefreshRuleApp;
import com.game.draco.app.npc.transfer.NpcTransferApp;
import com.game.draco.app.operate.OperateActiveApp;
import com.game.draco.app.operate.discount.DiscountApp;
import com.game.draco.app.operate.donate.DonateApp;
import com.game.draco.app.operate.firstpay.FirstPayApp;
import com.game.draco.app.operate.growfund.GrowFundApp;
import com.game.draco.app.operate.monthcard.MonthCardApp;
import com.game.draco.app.operate.payextra.PayExtraApp;
import com.game.draco.app.operate.simple.SimpleApp;
import com.game.draco.app.pet.PetApp;
import com.game.draco.app.pet.PetStorage;
import com.game.draco.app.pet.UserPetApp;
import com.game.draco.app.qualify.QualifyApp;
import com.game.draco.app.qualify.QualifyStorage;
import com.game.draco.app.quest.QuestApp;
import com.game.draco.app.quest.UserQuestApp;
import com.game.draco.app.quest.poker.QuestPokerApp;
import com.game.draco.app.quest.service.QuestServiceApp;
import com.game.draco.app.rank.RankApp;
import com.game.draco.app.recovery.RecoveryApp;
import com.game.draco.app.richman.RichManApp;
import com.game.draco.app.richman.UserRichManApp;
import com.game.draco.app.role.systemset.SystemSetApp;
import com.game.draco.app.rune.RuneApp;
import com.game.draco.app.shop.ShopApp;
import com.game.draco.app.shop.ShopTimeApp;
import com.game.draco.app.shopsecret.ShopSecretApp;
import com.game.draco.app.sign.SignApp;
import com.game.draco.app.skill.SkillApp;
import com.game.draco.app.skill.UserSkillApp;
import com.game.draco.app.social.SocialApp;
import com.game.draco.app.social.config.SocialConfig;
import com.game.draco.app.speciallogic.SpecialLogicApp;
import com.game.draco.app.store.NpcStoreApp;
import com.game.draco.app.survival.SurvivalApp;
import com.game.draco.app.survival.SurvivalBattleApp;
import com.game.draco.app.talent.RoleTalentApp;
import com.game.draco.app.talent.TalentApp;
import com.game.draco.app.target.TargetApp;
import com.game.draco.app.target.UserTargetApp;
import com.game.draco.app.team.TeamApp;
import com.game.draco.app.title.TitleApp;
import com.game.draco.app.tower.TowerApp;
import com.game.draco.app.union.UnionApp;
import com.game.draco.app.union.UnionAuctionApp;
import com.game.draco.app.union.UnionDataApp;
import com.game.draco.app.union.UnionInstanceApp;
import com.game.draco.app.union.battle.UnionBattleApp;
import com.game.draco.app.unionbattle.UnionIntegralBattleApp;
import com.game.draco.app.unionbattle.UnionIntegralBattleDataApp;
import com.game.draco.app.vip.VipApp;
import com.game.draco.app.worldlevel.WorldLevelApp;
import com.google.common.eventbus.EventBus;

public class GameContext {

	private final static int TEST = 1;
	private final static int OFFICIAL = 0;
	
	private static EventBus eventBus = new EventBus("eventbus") ;
	
	public static EventBus getEventBus(){
		return eventBus ;
	}
	
	public static final ChannelSession emptyChannelSession = new EmptyChannelSession();

	// 服务器当前协议版本号
	public static final short PROTO_VERSION = 1;
	private static int appId = 5 ;

	static GameContext context;
	// 服务器启动时间
	public static final Date systemStartTime = new Date();
	// 游戏开服时间
	public static Date gameStartDate;
	public static String gameStartDateStr = "";
	private static final String gameStartFormat = "yyyyMMdd";
	//10s
	private static int continuationTimeout = 10000;
	
	private static HttpJsonClient httpJsonClient;
	private static InviteConfig inviteConfig ;
	private static DoorDogApp doorDogApp ;
	
	public static MD5 md5 = new MD5();
	private static LanguageHandler i18n ;
	private static DonateApp donateApp ;
	
	/**
	 * 是否测试服务器
	 * 
	 * @return
	 */
	public static boolean isTestServer() {
		return TEST == GameContext.getTest();
	}
	
	/**
	 * 是否正式服务器
	 * 
	 * @return
	 */
	public static boolean isOfficialServer() {
		return OFFICIAL == GameContext.getTest();
	}

	public GameContext() {
		GameContext.context = this;
	}

	public static GameContext getGameContext() {
		return GameContext.context;
	}
	
	private static MailDAOImpl mailDAO;
	private static TokenApp tokenApp;
	private static TokenSecretkeyConfig tokenSecretkeyConfig;
	private static OpenSecretkeyConfig openSecretkeyConfig;
	
	//秘药
	private static NostrumApp nostrumApp;
	
	//随机任务
	private static QuestPokerApp questPokerApp;
	
	// 副本
	private static CopyLogicApp copyLogicApp;
	private static CopyLineApp copyLineApp;

	
	// VIP
	private static VipApp vipApp;

	//活动-DPS
	private static ActiveDpsApp activeDpsApp;
	//鼓舞
	private static NpcInspireApp npcInspireApp;
	
	// 活动-怪物攻城
	private static ActiveSiegeApp activeSiegeApp;
	// 冲级奖励
	private static LevelGiftApp levelGiftApp;
	// 装备特效
	private static MedalApp medalApp;
	// 符文
	private static RuneApp runeApp;

	private static IceServer iceServer;
	
	private static RoleDAO roleDAO;
	private static TradingApp tradingApp;

	// 加载线程数设置
	private static int threadsPools;
	private static MinaSecurity minaSecurity;
	// npc功能
	private static NpcFunctionApp npcFunctionApp;
	// npc买卖
	private static NpcStoreApp npcStoreApp;

	private static DefaultEventPublisher userSocketChannelEventPublisher;
	private static AuctionApp auctionApp;
	private static MinaServer minaServer;
	private static UserBuffApp userBuffApp;
	private static AiApp aiApp;
	
	private static QuickBuyApp quickBuyApp;
	private static QuickBuyConfig quickBuyConfig;

	private static ShopApp shopApp;
	private static ShopTimeApp shopTimeApp;
	
	private static SocialConfig socialConfig;
	private static SystemSetApp systemSetApp;

	private static BuffApp buffApp;

	private static GoodsApp goodsApp;

	private static QuestDAOImpl questDAO;
	
	private static QuestApp questApp;
	
	private static QuestServiceApp questServiceApp;

	private static SkillApp skillApp;

	private static MapApp mapApp;

	private static CalctManager calctManager;

	private static UserAttributeApp userAttributeApp;

	private static UserGoodsApp userGoodsApp;

	private static UserQuestApp userQuestApp;

	private static UserMapApp userMapApp;

	private static UserRoleApp userRoleApp;

	private static UserSkillApp userSkillApp;

	private static BattleApp battleApp;

	private static NpcApp npcApp;

	private static MessageCenter messageCenter;

	private static SkillConfig skillConfig;

	private static GoodsLoader goodsLoader;

	private static OnlineCenter onlineCenter;

	private static FallApp fallApp;

	private static CollectPointLoader collectPointLoader;

	private static PathConfig pathConfig;

	private static ParasConfig parasConfig;

	private static TeamApp teamApp;

	private static BaseDAO baseDAO;

	private static RoleRebornApp roleRebornApp;

	private static RoleBornApp roleBornApp;

	private static CronTrigger sysResetCopyCronTrigger;

	private static AreaServerNotifyApp areaServerNotifyApp;

	private static ChatApp chatApp;

	private static TimingWriteDBConfig timingWriteDBConfig;

	private static CalculateExpApp calculateExpApp;

	private static RoleService roleService;

	private static AnnounceApp announceApp;// 系统广播


	private static RateApp rateApp;

	private static IllegalWordsService illegalWordsService;

	private static ChargeApp chargeApp;
	//停止服务器
	private static ShutDownApp shutDownApp;

	private static NpcTeachApp npcTeachApp;

	private static NpcTransferApp npcTransferApp;

	private static ActiveApp activeApp;

	private static AutoSearchRoadApp autoSearchRoadApp;

	private static FactionConfig factionConfig;
	// 积分赛排队

	private static HeartBeatConfig heartBeatConfig;
	
	private static ChangeParamByServer changeParamByServer;

	private static Client ucClient;
	
	private static Client rolemeetClient;

	// 多钱程加裁
	private static ParallelRun parallelRun;

	private static ScriptSupport pyScriptSupport;

	private static IoBufferMessageParser ioBufferMessageParser;
	private static ByteProtoBuffer bytesProtoBuffer;

	private static AttriApp attriApp;

	private static ChannelManager channelManager;

	// 擂台赛
	private static ArenaApp arenaApp;
	
	private static Arena3V3App arena3V3App;
	// 幸运转盘，淘宝-上古法阵
	private static CompassApp compassApp;
	private static MapLoop mapLoop;
	// 大地图
	private static WorldMapApp worldMapApp;
	private static ActiveSupport activeArenaSupport;
	// 邮件
	private static MailApp mailApp;
	// 社交
	private static SocialApp socialApp;
	private static SocialDAOImpl socialDAO;

	// 日志
	private static StatLogApp statLogApp;
	// 兑换
	private static ExchangeApp exchangeApp;
	// 藏宝图
	private static TreasureApp treasureApp;
	// 排行榜
	private static RankApp rankApp;
	private static RankDAO rankDAO;
	// 激活码
	private static GiftCodeApp giftCodeApp;
	// npc刷新
	private static NpcRefreshApp npcRefreshApp;
	// 计数
	private static CountApp countApp;
	// 折扣活动
	private static DiscountApp discountApp;
	// 称号
	private static TitleApp titleApp;
	// 排行榜活动
//	private static ActiveRankApp activeRankApp;
	private static int test;
	private static RecoupApp recoupApp;

	private static PlatformConfig webPayConfig;
	// 客户端参数
	private static ClientTargetApp clientTargetApp;

	// 副本自动组队
	private static CopyTeamApp copyTeamApp;

	private static IdFactoryConfig idFactoryConfig;

	private static IdFactoryDAO idFactoryDAO;

	private static AreaServerClient areaServerClient;
	
	private static SchedulerApp schedulerApp;

	private static MenuApp menuApp ;

	private static UserWarehouseApp userWarehouseApp;
	
	private static SummonApp summonApp;
	
	private static Arena1V1App arena1V1App ;
	
	private static CarnivalApp carnivalApp;
	//背包扩充
	private static GoodsContainerApp goodsContainerApp ;
	private static RefreshRuleApp refreshRuleApp;
	
	private static ShopSecretApp shopSecretApp;
	
	private static HintApp hintApp;
	
	private static AngelChestApp angelChestApp ;
	
	private static ChargeConfig chargeConfig;
	
	private static BroadcastApp broadcastApp;
	
	private static PlatformConfig platformConfig;
	private static AdminApp adminApp ;
	private static InviteApp inviteApp ;
	private static ArenaTopApp arenaTopApp ;
	
	//宝箱app
	private static ChestApp chestApp ;
	//回归奖励
	private static RecallApp recallApp;
	
	private static DoorDogConfig doorDogConfig;
	private static LogApp logApp;
	private static EnvConfig envConfig;
	
	private static PkApp pkApp;
	
	private static CampBalanceApp campBalanceApp;
	
	private static SetConfig captchaCmdConfig ;
	
	private static PublicSetApp publicSetApp;
	
	private static DramaApp dramaApp;
	
	private static UserDramaApp userDramaApp;
	
	private static HeroApp heroApp ;
	private static UserHeroApp userHeroApp ;
	private static HeroArenaApp heroArenaApp;
	private static HeroArenaStorage heroArenaStorage;
	
	//宠物
	private static PetApp petApp;
	private static UserPetApp userPetApp;
	private static PetStorage petStorage;
	
	//大富翁
	private static RichManApp richManApp;
	private static UserRichManApp userRichManApp;

	//目标玩法
	private static TargetApp targetApp;
	private static UserTargetApp userTargetApp;

	private static HorseApp horseApp ;
	
	private static RoleHorseApp roleHorseApp ;
	
	private static RoleHorseStorage roleHorseStorage;
	
	//异步pvp
	private static AsyncPvpApp asyncPvpApp;
	
	private static SignApp signApp ;
	//炼金
	private static AlchemyApp alchemyApp;
	//幸运宝箱
	private static LuckyBoxApp luckyBoxApp;
	
	private static AsyncArenaApp asyncArenaApp;
	
	private static RoleAsyncArenaApp roleAsyncArenaApp;
	
	private static CampWarApp campWarApp ;
	
	private static ArenaLearnApp arenaLearnApp ;
	
	private static UnionApp unionApp;
	private static UnionDataApp unionDataApp;
	private static UnionDAOImpl unionDAO;
	private static UnionInstanceApp unionInstanceApp;
	private static UnionAuctionApp unionAuctionApp;
	private static HeroStorage heroStorage ;
	private static RoleAsyncArenaStorage roleAsyncArenaStorage ;
	private static InsLogicDataApp insLogicApp;
	
	//连续登陆
	private static AccumulateLoginApp accumulateLoginApp;
	
	private static EquipApp equipApp ;
	
	
	private static ChoiceCardApp choiceCardApp;
	
	private static RoleChoiceCardApp roleChoiceCardApp;
	
	private static DailyPlayApp dailyPlayApp ;
	
	private static ForwardApp forwardApp ;
	
	// 排位赛
	private static QualifyApp qualifyApp;
	private static QualifyStorage qualifyStorage;
	
	private static EnhanceOptionApp enhanceOptionApp ;
	
	// 哥布林活动
	private static GoblinApp goblinApp;
	
	
	// 运营活动
	private static OperateActiveApp operateActiveApp;
	private static FirstPayApp firstPayApp;
	private static MonthCardApp monthCardApp;
	private static PayExtraApp payExtraApp;
	private static GrowFundApp growFundApp;
	private static SimpleApp simpleApp;
	
	// 世界等级
	private static WorldLevelApp worldLevelApp;
	
	// 一键恢复
	private static  RecoveryApp recoveryApp;
	
	private static HitComboApp hitComboApp ;
	
	private static TalentApp talentApp;
	
	private static RoleTalentApp roleTalentApp;
	
	private static UnionBattleApp unionBattleApp;
	
	private static TowerApp towerApp;
	
	//生存战场
	private static SurvivalBattleApp survivalBattleApp;
		
	private static SurvivalApp survivalApp;
	
	private static SpecialLogicApp specialLogicApp;
	
	private static UnionIntegralBattleApp unionIntegralBattleApp;
	
	private static UnionIntegralBattleDataApp unionIntegralBattleDataApp;
	
	public static UnionIntegralBattleDataApp getUnionIntegralBattleDataApp() {
		return unionIntegralBattleDataApp;
	}

	public void setUnionIntegralBattleDataApp(
			UnionIntegralBattleDataApp unionIntegralBattleDataApp) {
		GameContext.unionIntegralBattleDataApp = unionIntegralBattleDataApp;
	}

	public static UnionIntegralBattleApp getUnionIntegralBattleApp() {
		return unionIntegralBattleApp;
	}

	public void setUnionIntegralBattleApp(
			UnionIntegralBattleApp unionIntegralBattleApp) {
		GameContext.unionIntegralBattleApp = unionIntegralBattleApp;
	}

	public static SpecialLogicApp getSpecialLogicApp() {
		return specialLogicApp;
	}

	public void setSpecialLogicApp(SpecialLogicApp specialLogicApp) {
		GameContext.specialLogicApp = specialLogicApp;
	}

	public static SurvivalBattleApp getSurvivalBattleApp() {
		return survivalBattleApp;
	}

	public void setSurvivalBattleApp(SurvivalBattleApp survivalBattleApp) {
		GameContext.survivalBattleApp = survivalBattleApp;
	}

	public static SurvivalApp getSurvivalApp() {
		return survivalApp;
	}

	public void setSurvivalApp(SurvivalApp survivalApp) {
		GameContext.survivalApp = survivalApp;
	}

	public static UnionBattleApp getUnionBattleApp(){
		return unionBattleApp;
	}
	public void setUnionBattleApp(UnionBattleApp battleApp){
		GameContext.unionBattleApp = battleApp;
	}
	
	public static TowerApp getTowerApp(){
		return towerApp;
	}
	
	public void setTowerApp(TowerApp towerApp){
		GameContext.towerApp = towerApp;
	}
	
	public static RoleTalentApp getRoleTalentApp() {
		return roleTalentApp;
	}

	public void setRoleTalentApp(RoleTalentApp roleTalentApp) {
		GameContext.roleTalentApp = roleTalentApp;
	}

	public static TalentApp getTalentApp() {
		return talentApp;
	}

	public void setTalentApp(TalentApp talentApp) {
		GameContext.talentApp = talentApp;
	}

	public static HitComboApp getHitComboApp() {
		return hitComboApp;
	}

	public void setHitComboApp(HitComboApp hitComboApp) {
		GameContext.hitComboApp = hitComboApp;
	}

	public static RecoveryApp getRecoveryApp() {
		return recoveryApp;
	}
	public void setRecoveryApp(RecoveryApp ra){
		GameContext.recoveryApp = ra;
	}
	
	public static QualifyApp getQualifyApp() {
		return qualifyApp;
	}
	
	public void setQualifyApp(QualifyApp qualifyApp) {
		GameContext.qualifyApp = qualifyApp;
	}
	
	public static QualifyStorage getQualifyStorage() {
		return qualifyStorage;
	}
	
	public void setQualifyStorage(QualifyStorage qualifyStorage) {
		GameContext.qualifyStorage = qualifyStorage;
	}
	
	public static OperateActiveApp getOperateActiveApp() {
		return operateActiveApp;
	}
	
	public void setOperateActiveApp(OperateActiveApp operateActiveApp) {
		GameContext.operateActiveApp = operateActiveApp;
	}
	
	public static MonthCardApp getMonthCardApp() {
		return monthCardApp;
	}
	
	public void setMonthCardApp(MonthCardApp monthCardApp) {
		GameContext.monthCardApp = monthCardApp;
	}
	
	public static FirstPayApp getFirstPayApp() {
		return firstPayApp;
	}
	
	public void setFirstPayApp(FirstPayApp firstPayApp) {
		GameContext.firstPayApp = firstPayApp;
	}
	
	public static GrowFundApp getGrowFundApp() {
		return growFundApp;
	}
	
	public void setGrowFundApp(GrowFundApp growFundApp) {
		GameContext.growFundApp = growFundApp;
	}
	
	public static PayExtraApp getPayExtraApp() {
		return payExtraApp;
	}
	
	public void setPayExtraApp(PayExtraApp payExtraApp) {
		GameContext.payExtraApp = payExtraApp;
	}
	
	public static SimpleApp getSimpleApp() {
		return simpleApp;
	}
	
	public void setSimpleApp(SimpleApp simpleApp) {
		GameContext.simpleApp = simpleApp;
	}
	
	public static GoblinApp getGoblinApp() {
		return goblinApp;
	}
	
	public void setGoblinApp(GoblinApp goblinApp) {
		GameContext.goblinApp = goblinApp;
	}
	
	public static WorldLevelApp getWorldLevelApp() {
		return worldLevelApp;
	}
	
	public void setWorldLevelApp(WorldLevelApp worldLevelApp) {
		GameContext.worldLevelApp = worldLevelApp;
	}
	
	public static EnhanceOptionApp getEnhanceOptionApp() {
		return enhanceOptionApp;
	}
	
	public void setEnhanceOptionApp(EnhanceOptionApp app) {
		GameContext.enhanceOptionApp = app;
	}
	

	public static EquipApp getEquipApp() {
		return equipApp;
	}

	public void setEquipApp(EquipApp equipApp) {
		GameContext.equipApp = equipApp;
	}

	public static HeroStorage getHeroStorage() {
		return heroStorage;
	}

	public void setHeroStorage(HeroStorage heroStorage) {
		GameContext.heroStorage = heroStorage;
	}

	public static ArenaLearnApp getArenaLearnApp() {
		return arenaLearnApp;
	}

	public void setArenaLearnApp(ArenaLearnApp arenaLearnApp) {
		GameContext.arenaLearnApp = arenaLearnApp;
	}

	public static CampWarApp getCampWarApp() {
		return campWarApp;
	}

	public void setCampWarApp(CampWarApp campWarApp) {
		GameContext.campWarApp = campWarApp;
	}

	public static AlchemyApp getAlchemyApp(){
		return alchemyApp;
	}
	public void setAlchemyApp(AlchemyApp alchemyApp){
		GameContext.alchemyApp = alchemyApp;
	}
	
	public static SignApp getSignApp() {
		return signApp;
	}

	public void setSignApp(SignApp signApp) {
		GameContext.signApp = signApp;
	}

	public static HeroApp getHeroApp() {
		return heroApp;
	}

	public void setHeroApp(HeroApp heroApp) {
		GameContext.heroApp = heroApp;
	}

	public static UserHeroApp getUserHeroApp() {
		return userHeroApp;
	}

	public void setUserHeroApp(UserHeroApp userHeroApp) {
		GameContext.userHeroApp = userHeroApp;
	}
	
	public static PetApp getPetApp() {
		return petApp;
	}
	
	public void setPetApp(PetApp petApp) {
		GameContext.petApp = petApp;
	}
	
	public static UserPetApp getUserPetApp() {
		return userPetApp;
	}
	
	public void setUserPetApp(UserPetApp userPetApp) {
		GameContext.userPetApp = userPetApp;
	}
	
	public static PetStorage getPetStorage() {
		return petStorage;
	}

	public void setPetStorage(PetStorage petStorage) {
		GameContext.petStorage = petStorage;
	}
	
	public static RichManApp getRichManApp() {
		return richManApp;
	}

	public void setRichManApp(RichManApp richManApp) {
		GameContext.richManApp = richManApp;
	}
	
	public static UserRichManApp getUserRichManApp() {
		return userRichManApp;
	}

	public void setUserRichManApp(UserRichManApp userRichManApp) {
		GameContext.userRichManApp = userRichManApp;
	}
	
	public static TargetApp getTargetApp() {
		return targetApp;
	}

	public void setTargetApp(TargetApp targetApp) {
		GameContext.targetApp = targetApp;
	}

	
	public static UserTargetApp getUserTargetApp() {
		return userTargetApp;
	}

	public void setUserTargetApp(UserTargetApp userTargetApp) {
		GameContext.userTargetApp = userTargetApp;
	}
	
	public static AsyncPvpApp getAsyncPvpApp() {
		return asyncPvpApp;
	}

	public void setAsyncPvpApp(AsyncPvpApp asyncPvpApp) {
		GameContext.asyncPvpApp = asyncPvpApp;
	}

	public static DramaApp getDramaApp() {
		return dramaApp;
	}

	public void setDramaApp(DramaApp dramaApp) {
		GameContext.dramaApp = dramaApp;
	}
	
	public static UserDramaApp getUserDramaApp() {
		return userDramaApp;
	}

	public void setUserDramaApp(UserDramaApp userDramaApp) {
		GameContext.userDramaApp = userDramaApp;
	}
	
	public static SetConfig getCaptchaCmdConfig() {
		return captchaCmdConfig;
	}

	public void setCaptchaCmdConfig(SetConfig captchaCmdConfig) {
		GameContext.captchaCmdConfig = captchaCmdConfig;
	}


	public static InviteApp getInviteApp() {
		return inviteApp;
	}

	public void setInviteApp(InviteApp inviteApp) {
		GameContext.inviteApp = inviteApp;
	}

	public static AdminApp getAdminApp() {
		return adminApp;
	}

	public void setAdminApp(AdminApp adminApp) {
		GameContext.adminApp = adminApp;
	}


	public static Arena1V1App getArena1V1App() {
		return arena1V1App;
	}

	public void setArena1V1App(Arena1V1App arena1V1App) {
		GameContext.arena1V1App = arena1V1App;
	}
 

	public static SchedulerApp getSchedulerApp() {
		return schedulerApp;
	}

	public void setSchedulerApp(SchedulerApp schedulerApp) {
		GameContext.schedulerApp = schedulerApp;
	}

	public static AreaServerClient getAreaServerClient() {
		return areaServerClient;
	}

	public void setAreaServerClient(AreaServerClient areaServerClient) {
		GameContext.areaServerClient = areaServerClient;
	}

	public static IdFactoryDAO getIdFactoryDAO() {
		return idFactoryDAO;
	}

	public void setIdFactoryDAO(IdFactoryDAO idFactoryDAO) {
		GameContext.idFactoryDAO = idFactoryDAO;
	}

	public static IdFactoryConfig getIdFactoryConfig() {
		return idFactoryConfig;
	}

	public void setIdFactoryConfig(IdFactoryConfig idFactoryConfig) {
		GameContext.idFactoryConfig = idFactoryConfig;
	}

	public static CopyTeamApp getCopyTeamApp() {
		return copyTeamApp;
	}

	public void setCopyTeamApp(CopyTeamApp copyTeamApp) {
		GameContext.copyTeamApp = copyTeamApp;
	}

//	public static ActiveRankApp getActiveRankApp() {
//		return activeRankApp;
//	}
//
//	public void setActiveRankApp(ActiveRankApp activeRankApp) {
//		GameContext.activeRankApp = activeRankApp;
//	}

	

	public static MedalApp getMedalApp() {
		return medalApp;
	}

	public void setMedalApp(MedalApp medalApp) {
		GameContext.medalApp = medalApp;
	}
	
	public static RuneApp getRuneApp() {
		return runeApp;
	}
	
	public void setRuneApp(RuneApp runeApp) {
		GameContext.runeApp = runeApp;
	}

	public static DiscountApp getDiscountApp() {
		return discountApp;
	}

	public void setDiscountApp(DiscountApp discountApp) {
		GameContext.discountApp = discountApp;
	}

	public static CountApp getCountApp() {
		return countApp;
	}

	public void setCountApp(CountApp countApp) {
		GameContext.countApp = countApp;
	}

	public static TreasureApp getTreasureApp() {
		return treasureApp;
	}

	public void setTreasureApp(TreasureApp treasureApp) {
		GameContext.treasureApp = treasureApp;
	}

	public static RankApp getRankApp() {
		return rankApp;
	}

	public void setRankApp(RankApp rankApp) {
		GameContext.rankApp = rankApp;
	}

	public static RankDAO getRankDAO() {
		return rankDAO;
	}

	public void setRankDAO(RankDAO rankDAO) {
		GameContext.rankDAO = rankDAO;
	}


	public static ExchangeApp getExchangeApp() {
		return exchangeApp;
	}

	public void setExchangeApp(ExchangeApp exchangeApp) {
		GameContext.exchangeApp = exchangeApp;
	}

	public static WorldMapApp getWorldMapApp() {
		return worldMapApp;
	}

	public void setWorldMapApp(WorldMapApp worldMapApp) {
		GameContext.worldMapApp = worldMapApp;
	}

	public static MailApp getMailApp() {
		return mailApp;
	}

	public void setMailApp(MailApp mailApp) {
		GameContext.mailApp = mailApp;
	}

	public static MapLoop getMapLoop() {
		return mapLoop;
	}

	public void setMapLoop(MapLoop mapLoop) {
		GameContext.mapLoop = mapLoop;
	}


	public static ScriptSupport getPyScriptSupport() {
		return pyScriptSupport;
	}

	public void setPyScriptSupport(ScriptSupport pyScriptSupport) {
		GameContext.pyScriptSupport = pyScriptSupport;
	}


	public static Client getUcClient() {
		return ucClient;
	}

	public void setUcClient(Client ucClient) {
		GameContext.ucClient = ucClient;
	}
	
	public static Client getRolemeetClient() {
		return rolemeetClient;
	}

	public void setRolemeetClient(Client rolemeetClient) {
		GameContext.rolemeetClient = rolemeetClient;
	}

	public static FactionConfig getFactionConfig() {
		return factionConfig;
	}

	public void setFactionConfig(FactionConfig factionConfig) {
		GameContext.factionConfig = factionConfig;
	}

	public static AutoSearchRoadApp getAutoSearchRoadApp() {
		return autoSearchRoadApp;
	}

	public void setAutoSearchRoadApp(AutoSearchRoadApp autoSearchRoadApp) {
		GameContext.autoSearchRoadApp = autoSearchRoadApp;
	}

	public static ShutDownApp getShutDownApp() {
		return shutDownApp;
	}

	public void setShutDownApp(ShutDownApp shutDownApp) {
		GameContext.shutDownApp = shutDownApp;
	}

	public static GoodsLoader getGoodsLoader() {
		return goodsLoader;
	}

	public void setGoodsLoader(GoodsLoader goodsLoader) {
		GameContext.goodsLoader = goodsLoader;
	}

	public static CalculateExpApp getCalculateExpApp() {
		return calculateExpApp;
	}

	public void setCalculateExpApp(CalculateExpApp calculateExpApp) {
		GameContext.calculateExpApp = calculateExpApp;
	}

	public static ChatApp getChatApp() {
		return chatApp;
	}

	public void setChatApp(ChatApp chatApp) {
		GameContext.chatApp = chatApp;
	}

	public void setAreaServerNotifyApp(AreaServerNotifyApp areaServerNotifyApp) {
		GameContext.areaServerNotifyApp = areaServerNotifyApp;
	}

	public static AreaServerNotifyApp getAreaServerNotifyApp() {
		return areaServerNotifyApp;
	}

	public static CronTrigger getSysResetCopyCronTrigger() {
		return sysResetCopyCronTrigger;
	}

	public void setSysResetCopyCronTrigger(CronTrigger sysResetCopyCronTrigger) {
		GameContext.sysResetCopyCronTrigger = sysResetCopyCronTrigger;
	}

	public static TeamApp getTeamApp() {
		return teamApp;
	}

	public void setTeamApp(TeamApp teamApp) {
		GameContext.teamApp = teamApp;
	}

	public static SkillApp getSkillApp() {
		return skillApp;
	}

	public void setSkillApp(SkillApp skillApp) {
		GameContext.skillApp = skillApp;
	}

	public static UserAttributeApp getUserAttributeApp() {
		return userAttributeApp;
	}

	public void setUserAttributeApp(UserAttributeApp userAttributeApp) {
		GameContext.userAttributeApp = userAttributeApp;
	}

	public static UserGoodsApp getUserGoodsApp() {
		return userGoodsApp;
	}

	public void setUserGoodsApp(UserGoodsApp userGoodsApp) {
		GameContext.userGoodsApp = userGoodsApp;
	}

	public static UserBuffApp getUserBuffApp() {
		return userBuffApp;
	}

	public void setUserBuffApp(UserBuffApp userBuffApp) {
		GameContext.userBuffApp = userBuffApp;
	}

	public static UserMapApp getUserMapApp() {
		return userMapApp;
	}

	public void setUserMapApp(UserMapApp userMapApp) {
		GameContext.userMapApp = userMapApp;
	}

	public static UserRoleApp getUserRoleApp() {
		return userRoleApp;
	}

	public void setUserRoleApp(UserRoleApp userRoleApp) {
		GameContext.userRoleApp = userRoleApp;
	}

	public static UserSkillApp getUserSkillApp() {
		return userSkillApp;
	}

	public void setUserSkillApp(UserSkillApp userSkillApp) {
		GameContext.userSkillApp = userSkillApp;
	}

	public static BattleApp getBattleApp() {
		return battleApp;
	}

	public void setBattleApp(BattleApp battleApp) {
		GameContext.battleApp = battleApp;
	}

	public static BuffApp getBuffApp() {
		return buffApp;
	}

	public void setBuffApp(BuffApp buffApplication) {
		GameContext.buffApp = buffApplication;
	}

	public static MapApp getMapApp() {
		return mapApp;
	}

	public void setMapApp(MapApp mapApp) {
		GameContext.mapApp = mapApp;
	}

	public static AiApp getAiApp() {
		return aiApp ;
	}

	public void setAiApp(AiApp aiApp) {
		GameContext.aiApp = aiApp;
	}

	public static NpcApp getNpcApp() {
		return npcApp;
	}

	public void setNpcApp(NpcApp npcApp) {
		GameContext.npcApp = npcApp;
	}

	public static MessageCenter getMessageCenter() {
		return messageCenter;
	}

	public void setMessageCenter(MessageCenter messageCenter) {
		GameContext.messageCenter = messageCenter;
	}

	public static SkillConfig getSkillConfig() {
		return skillConfig;
	}

	public void setSkillConfig(SkillConfig skillConfig) {
		GameContext.skillConfig = skillConfig;
	}

	public static GoodsApp getGoodsApp() {
		return goodsApp;
	}

	public void setGoodsApp(GoodsApp goodsApp) {
		GameContext.goodsApp = goodsApp;
	}

	public static QuestApp getQuestApp() {
		return questApp;
	}

	public void setQuestApp(QuestApp questApp) {
		GameContext.questApp = questApp;
	}

	public static UserQuestApp getUserQuestApp() {
		return userQuestApp;
	}

	public void setUserQuestApp(UserQuestApp userQuestApp) {
		GameContext.userQuestApp = userQuestApp;
	}

	public static OnlineCenter getOnlineCenter() {
		return onlineCenter;
	}

	public void setOnlineCenter(OnlineCenter onlineCenter) {
		GameContext.onlineCenter = onlineCenter;
	}

	public static FallApp getFallApp() {
		return fallApp;
	}

	public void setFallApp(FallApp fallApp) {
		GameContext.fallApp = fallApp;
	}

	public static CollectPointLoader getCollectPointLoader() {
		return collectPointLoader;
	}

	public void setCollectPointLoader(CollectPointLoader collectPointLoader) {
		GameContext.collectPointLoader = collectPointLoader;
	}

	public static PathConfig getPathConfig() {
		return pathConfig;
	}

	public void setPathConfig(PathConfig pathConfig) {
		GameContext.pathConfig = pathConfig;
	}

	public static ParasConfig getParasConfig() {
		return parasConfig;
	}

	public void setParasConfig(ParasConfig parasConfig) {
		GameContext.parasConfig = parasConfig;
	}

	public static BaseDAO getBaseDAO() {
		return baseDAO;
	}

	public void setBaseDAO(BaseDAO baseDAO) {
		GameContext.baseDAO = baseDAO;
	}

	public static RoleRebornApp getRoleRebornApp() {
		return roleRebornApp;
	}

	public void setRoleRebornApp(RoleRebornApp roleRebornApp) {
		GameContext.roleRebornApp = roleRebornApp;
	}


	public static RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		GameContext.roleService = roleService;
	}

	public static RateApp getRateApp() {
		return rateApp;
	}

	public void setRateApp(RateApp rateApp) {
		GameContext.rateApp = rateApp;
	}

	public static int getAppId() {
		return appId;
	}

	public void setAppId(int appId) {
		GameContext.appId = appId;
	}

	public static ChargeApp getChargeApp() {
		return chargeApp;
	}

	public void setChargeApp(ChargeApp chargeApp) {
		GameContext.chargeApp = chargeApp;
	}

	public static MinaServer getMinaServer() {
		return minaServer;
	}

	public static IllegalWordsService getIllegalWordsService() {
		return illegalWordsService;
	}

	public void setIllegalWordsService(IllegalWordsService illegalWordsService) {
		GameContext.illegalWordsService = illegalWordsService;
	}

	public void setMinaServer(MinaServer minaServer) {
		GameContext.minaServer = minaServer;
	}

	public static NpcTeachApp getNpcTeachApp() {
		return npcTeachApp;
	}

	public void setNpcTeachApp(NpcTeachApp npcTeachApp) {
		GameContext.npcTeachApp = npcTeachApp;
	}

	public static NpcTransferApp getNpcTransferApp() {
		return npcTransferApp;
	}

	public void setNpcTransferApp(NpcTransferApp npcTransferApp) {
		GameContext.npcTransferApp = npcTransferApp;
	}


	public static HeartBeatConfig getHeartBeatConfig() {
		return heartBeatConfig;
	}

	public void setHeartBeatConfig(HeartBeatConfig heartBeatConfig) {
		GameContext.heartBeatConfig = heartBeatConfig;
	}

	public static ParallelRun getParallelRun() {
		return parallelRun;
	}

	public static void setParallelRun(ParallelRun parallelRun) {
		GameContext.parallelRun = parallelRun;
	}

	public static IoBufferMessageParser getIoBufferMessageParser() {
		return ioBufferMessageParser;
	}

	public void setIoBufferMessageParser(
			IoBufferMessageParser ioBufferMessageParser) {
		GameContext.ioBufferMessageParser = ioBufferMessageParser;
	}

	public static ByteProtoBuffer getBytesProtoBuffer() {
		return bytesProtoBuffer;
	}

	public void setBytesProtoBuffer(ByteProtoBuffer bytesProtoBuffer) {
		GameContext.bytesProtoBuffer = bytesProtoBuffer;
	}

	public static AttriApp getAttriApp() {
		return attriApp;
	}

	public void setAttriApp(AttriApp attriApp) {
		GameContext.attriApp = attriApp;
	}

	public static CalctManager getCalctManager() {
		return calctManager;
	}

	public void setCalctManager(CalctManager calctManager) {
		GameContext.calctManager = calctManager;
	}

	public static ChannelManager getChannelManager() {
		return channelManager;
	}

	public void setChannelManager(ChannelManager channelManager) {
		GameContext.channelManager = channelManager;
	}


	public static RoleBornApp getRoleBornApp() {
		return roleBornApp;
	}

	public void setRoleBornApp(RoleBornApp roleBornApp) {
		GameContext.roleBornApp = roleBornApp;
	}

	public static AuctionApp getAuctionApp() {
		return auctionApp;
	}

	public void setAuctionApp(AuctionApp auctionApp) {
		GameContext.auctionApp = auctionApp;
	}

	public static NpcFunctionApp getNpcFunctionApp() {
		return npcFunctionApp;
	}

	public void setNpcFunctionApp(NpcFunctionApp npcFunctionApp) {
		GameContext.npcFunctionApp = npcFunctionApp;
	}

	public static int getServerId() {
		return Integer.parseInt(areaServerNotifyApp.getServerId());
	}

	public static String getServerName() {
		return areaServerClient.getServerStatus().getServerName();
	}

	public static DefaultEventPublisher getUserSocketChannelEventPublisher() {
		return userSocketChannelEventPublisher;
	}

	public void setUserSocketChannelEventPublisher(
			DefaultEventPublisher userSocketChannelEventPublisher) {
		GameContext.userSocketChannelEventPublisher = userSocketChannelEventPublisher;
	}

	public static int getThreadsPools() {
		if(threadsPools > 0){
			return threadsPools ;
		}
		return Runtime.getRuntime().availableProcessors();
	}

	public void setThreadsPools(int threadsPools) {
		GameContext.threadsPools = threadsPools;
	}

	public static MinaSecurity getMinaSecurity() {
		return minaSecurity;
	}

	public void setMinaSecurity(MinaSecurity minaSecurity) {
		GameContext.minaSecurity = minaSecurity;
	}

	public static NpcStoreApp getNpcStoreApp() {
		return npcStoreApp;
	}

	public void setNpcStoreApp(NpcStoreApp npcStoreApp) {
		GameContext.npcStoreApp = npcStoreApp;
	}

	public static SystemSetApp getSystemSetApp() {
		return systemSetApp;
	}

	public void setSystemSetApp(SystemSetApp systemSetApp) {
		GameContext.systemSetApp = systemSetApp;
	}

	public static ActiveApp getActiveApp() {
		return activeApp;
	}

	public void setActiveApp(ActiveApp activeApp) {
		GameContext.activeApp = activeApp;
	}


	public static ArenaApp getArenaApp() {
		return arenaApp;
	}

	public void setArenaApp(ArenaApp arenaApp) {
		GameContext.arenaApp = arenaApp;
	}

	public static ActiveSupport getActiveArenaSupport() {
		return activeArenaSupport;
	}

	public void setActiveArenaSupport(ActiveSupport activeArenaSupport) {
		GameContext.activeArenaSupport = activeArenaSupport;
	}

	public static SocialApp getSocialApp() {
		return socialApp;
	}

	public void setSocialApp(SocialApp socialApp) {
		GameContext.socialApp = socialApp;
	}

	public static SocialDAOImpl getSocialDAO() {
		return socialDAO;
	}

	public void setSocialDAO(SocialDAOImpl socialDAO) {
		GameContext.socialDAO = socialDAO;
	}

	public static TradingApp getTradingApp() {
		return tradingApp;
	}

	public void setTradingApp(TradingApp tradingApp) {
		GameContext.tradingApp = tradingApp;
	}

	public static RoleDAO getRoleDAO() {
		return roleDAO;
	}

	public void setRoleDAO(RoleDAO roleDAO) {
		GameContext.roleDAO = roleDAO;
	}

	public static SocialConfig getSocialConfig() {
		return socialConfig;
	}

	public void setSocialConfig(SocialConfig socialConfig) {
		GameContext.socialConfig = socialConfig;
	}

	public static ShopApp getShopApp() {
		return shopApp;
	}

	public void setShopApp(ShopApp shopApp) {
		GameContext.shopApp = shopApp;
	}


	public static StatLogApp getStatLogApp() {
		return statLogApp;
	}

	public void setStatLogApp(StatLogApp statLogApp) {
		GameContext.statLogApp = statLogApp;
	}

	public static QuickBuyApp getQuickBuyApp() {
		return quickBuyApp;
	}

	public void setQuickBuyApp(QuickBuyApp quickBuyApp) {
		GameContext.quickBuyApp = quickBuyApp;
	}

	public static AnnounceApp getAnnounceApp() {
		return announceApp;
	}

	public void setAnnounceApp(AnnounceApp announceApp) {
		GameContext.announceApp = announceApp;
	}


	public static IceServer getIceServer() {
		return iceServer;
	}

	public void setIceServer(IceServer iceServer) {
		GameContext.iceServer = iceServer;
	}

	public static NpcRefreshApp getNpcRefreshApp() {
		return npcRefreshApp;
	}

	public void setNpcRefreshApp(NpcRefreshApp npcRefreshApp) {
		GameContext.npcRefreshApp = npcRefreshApp;
	}


	public static TimingWriteDBConfig getTimingWriteDBConfig() {
		return timingWriteDBConfig;
	}

	public void setTimingWriteDBConfig(TimingWriteDBConfig timingWriteDBConfig) {
		GameContext.timingWriteDBConfig = timingWriteDBConfig;
	}

	public static TitleApp getTitleApp() {
		return titleApp;
	}

	public void setTitleApp(TitleApp titleApp) {
		GameContext.titleApp = titleApp;
	}

	public static Date getGameStartDate() {
		return gameStartDate;
	}

	public static int getTest() {
		return test;
	}

	public void setTest(int test) {
		GameContext.test = test;
	}

	public static String getGameStartDateStr() {
		return gameStartDateStr;
	}

	public void setGameStartDateStr(String gameStartDateStr) {
		GameContext.gameStartDateStr = gameStartDateStr;
		String error = "GameContext.gameStartDateStr is empty.Please set up the game server open time.";
		if (Util.isEmpty(gameStartDateStr)) {
			Log4jManager.CHECK.error(error);
			Log4jManager.checkFail();
		}
		GameContext.gameStartDate = DateUtil.str2Date(gameStartDateStr,
				GameContext.gameStartFormat);
		if (null == GameContext.gameStartDate) {
			Log4jManager.CHECK.error(error);
			Log4jManager.checkFail();
		}
	}


	public static ActiveSiegeApp getActiveSiegeApp() {
		return activeSiegeApp;
	}

	public void setActiveSiegeApp(ActiveSiegeApp activeSiegeApp) {
		GameContext.activeSiegeApp = activeSiegeApp;
	}

	
	public static VipApp getVipApp() {
		return vipApp;
	}
	
	public void setVipApp(VipApp vipApp) {
		GameContext.vipApp = vipApp;
	}

	public static RecoupApp getRecoupApp() {
		return recoupApp;
	}

	public void setRecoupApp(RecoupApp recoupApp) {
		GameContext.recoupApp = recoupApp;
	}

	public static PlatformConfig getWebPayConfig() {
		return webPayConfig;
	}

	public void setWebPayConfig(PlatformConfig webPayConfig) {
		GameContext.webPayConfig = webPayConfig;
	}

	public static ClientTargetApp getClientTargetApp() {
		return clientTargetApp;
	}

	public void setClientTargetApp(ClientTargetApp clientTargetApp) {
		GameContext.clientTargetApp = clientTargetApp;
	}


	public static CopyLogicApp getCopyLogicApp() {
		return copyLogicApp;
	}

	public void setCopyLogicApp(CopyLogicApp copyLogicApp) {
		GameContext.copyLogicApp = copyLogicApp;
	}

	public static MenuApp getMenuApp() {
		return menuApp;
	}

	public void setMenuApp(MenuApp menuApp) {
		GameContext.menuApp = menuApp;
	}

	public static CompassApp getCompassApp() {
		return compassApp;
	}

	public void setCompassApp(CompassApp compassApp) {
		GameContext.compassApp = compassApp;
	}

	public static UserWarehouseApp getUserWarehouseApp() {
		return userWarehouseApp;
	}

	public void setUserWarehouseApp(UserWarehouseApp userWarehouseApp) {
		GameContext.userWarehouseApp = userWarehouseApp;
	}

	public static SummonApp getSummonApp() {
		return summonApp;
	}

	public void setSummonApp(SummonApp summonApp) {
		GameContext.summonApp = summonApp;
	}

	public static QuestServiceApp getQuestServiceApp() {
		return questServiceApp;
	}

	public void setQuestServiceApp(QuestServiceApp questServiceApp) {
		GameContext.questServiceApp = questServiceApp;
	}

	public static ShopTimeApp getShopTimeApp() {
		return shopTimeApp;
	}

	public void setShopTimeApp(ShopTimeApp shopTimeApp) {
		GameContext.shopTimeApp = shopTimeApp;
	}

	
	public static ActiveDpsApp getActiveDpsApp() {
		return activeDpsApp;
	}

	public void setActiveDpsApp(ActiveDpsApp activeDpsApp) {
		GameContext.activeDpsApp = activeDpsApp;
	}

	public static CarnivalApp getCarnivalApp() {
		return carnivalApp;
	}

	public void setCarnivalApp(CarnivalApp carnivalApp) {
		GameContext.carnivalApp = carnivalApp;
	}

	public static NpcInspireApp getNpcInspireApp() {
		return npcInspireApp;
	}

	public void setNpcInspireApp(NpcInspireApp npcInspireApp) {
		GameContext.npcInspireApp = npcInspireApp;
	}

	public static GoodsContainerApp getGoodsContainerApp() {
		return goodsContainerApp;
	}

	public void setGoodsContainerApp(GoodsContainerApp goodsContainerApp) {
		GameContext.goodsContainerApp = goodsContainerApp;
	}

	public static QuestDAOImpl getQuestDAO() {
		return questDAO;
	}

	public void setQuestDAO(QuestDAOImpl questDAO) {
		GameContext.questDAO = questDAO;
	}

	public static RefreshRuleApp getRefreshRuleApp() {
		return refreshRuleApp;
	}

	public void setRefreshRuleApp(RefreshRuleApp refreshRuleApp) {
		GameContext.refreshRuleApp = refreshRuleApp;
	}

	public static QuickBuyConfig getQuickBuyConfig() {
		return quickBuyConfig;
	}

	public void setQuickBuyConfig(QuickBuyConfig quickBuyConfig) {
		GameContext.quickBuyConfig = quickBuyConfig;
	}

	public static ShopSecretApp getShopSecretApp() {
		return shopSecretApp;
	}

	public void setShopSecretApp(ShopSecretApp shopSecretApp) {
		GameContext.shopSecretApp = shopSecretApp;
	}

	public static HintApp getHintApp() {
		return hintApp;
	}

	public void setHintApp(HintApp hintApp) {
		GameContext.hintApp = hintApp;
	}

	public static AngelChestApp getAngelChestApp() {
		return angelChestApp;
	}

	public void setAngelChestApp(AngelChestApp angelChestApp) {
		GameContext.angelChestApp = angelChestApp;
	}

	public static ChargeConfig getChargeConfig() {
		return chargeConfig;
	}

	public void setChargeConfig(ChargeConfig chargeConfig) {
		GameContext.chargeConfig = chargeConfig;
	}

	public static BroadcastApp getBroadcastApp() {
		return broadcastApp;
	}

	public void setBroadcastApp(BroadcastApp broadcastApp) {
		GameContext.broadcastApp = broadcastApp;
	}

	public static PlatformConfig getPlatformConfig() {
		return platformConfig;
	}

	public void setPlatformConfig(PlatformConfig platformConfig) {
		GameContext.platformConfig = platformConfig;
	}

	public static int getContinuationTimeout() {
		return continuationTimeout;
	}

	public void setContinuationTimeout(int continuationTimeout) {
		GameContext.continuationTimeout = continuationTimeout;
	}

	public static HttpJsonClient getHttpJsonClient() {
		return httpJsonClient;
	}

	public void setHttpJsonClient(HttpJsonClient httpJsonClient) {
		GameContext.httpJsonClient = httpJsonClient;
	}

	public static InviteConfig getInviteConfig() {
		return inviteConfig;
	}

	public void setInviteConfig(InviteConfig inviteConfig) {
		GameContext.inviteConfig = inviteConfig;
	}

	public static ArenaTopApp getArenaTopApp() {
		return arenaTopApp;
	}

	public void setArenaTopApp(ArenaTopApp arenaTopApp) {
		GameContext.arenaTopApp = arenaTopApp;
	}

	public static DoorDogApp getDoorDogApp() {
		return doorDogApp;
	}

	public void setDoorDogApp(DoorDogApp doorDogApp) {
		GameContext.doorDogApp = doorDogApp;
	}

	public static ChestApp getChestApp() {
		return chestApp;
	}

	public void setChestApp(ChestApp chestApp) {
		GameContext.chestApp = chestApp;
	}

	public static RecallApp getRecallApp() {
		return recallApp;
	}

	public void setRecallApp(RecallApp recallApp) {
		GameContext.recallApp = recallApp;
	}

	public static OpenSecretkeyConfig getOpenSecretkeyConfig() {
		return openSecretkeyConfig;
	}

	public void setOpenSecretkeyConfig(OpenSecretkeyConfig openSecretkeyConfig) {
		GameContext.openSecretkeyConfig = openSecretkeyConfig;
	}

	public static DoorDogConfig getDoorDogConfig() {
		return doorDogConfig;
	}

	public void setDoorDogConfig(DoorDogConfig doorDogConfig) {
		GameContext.doorDogConfig = doorDogConfig;
	}

	public static EnvConfig getEnvConfig() {
		return envConfig;
	}

	public void setEnvConfig(EnvConfig envConfig) {
		GameContext.envConfig = envConfig;
	}

	public static LogApp getLogApp() {
		return logApp;
	}

	public void setLogApp(LogApp logApp) {
		GameContext.logApp = logApp;
	}

	public static TokenSecretkeyConfig getTokenSecretkeyConfig() {
		return tokenSecretkeyConfig;
	}

	public void setTokenSecretkeyConfig(TokenSecretkeyConfig tokenSecretkeyConfig) {
		GameContext.tokenSecretkeyConfig = tokenSecretkeyConfig;
	}

	public static TokenApp getTokenApp() {
		return tokenApp;
	}

	public void setTokenApp(TokenApp tokenApp) {
		GameContext.tokenApp = tokenApp;
	}

	public static PkApp getPkApp() {
		return pkApp;
	}

	public void setPkApp(PkApp pkApp) {
		GameContext.pkApp = pkApp;
	}

	public static CampBalanceApp getCampBalanceApp() {
		return campBalanceApp;
	}

	public void setCampBalanceApp(CampBalanceApp campBalanceApp) {
		GameContext.campBalanceApp = campBalanceApp;
	}

	public static MailDAOImpl getMailDAO() {
		return mailDAO;
	}

	public void setMailDAO(MailDAOImpl mailDAO) {
		GameContext.mailDAO = mailDAO;
	}

	public static LanguageHandler getI18n() {
		return i18n;
	}

	public void setI18n(LanguageHandler i18n) {
		GameContext.i18n = i18n;
	}

	public static PublicSetApp getPublicSetApp() {
		return publicSetApp;
	}

	public void setPublicSetApp(PublicSetApp publicSetApp) {
		GameContext.publicSetApp = publicSetApp;
	}

	public static ChangeParamByServer getChangeParamByServer() {
		return changeParamByServer;
	}

	public void setChangeParamByServer(
			ChangeParamByServer changeParamByServer) {
		GameContext.changeParamByServer = changeParamByServer;
	}

	public static CopyLineApp getCopyLineApp() {
		return copyLineApp;
	}

	public void setCopyLineApp(CopyLineApp copyLineApp) {
		GameContext.copyLineApp = copyLineApp;
	}

	public static NostrumApp getNostrumApp() {
		return nostrumApp;
	}

	public void setNostrumApp(NostrumApp nostrumApp) {
		GameContext.nostrumApp = nostrumApp;
	}

	public static GiftCodeApp getGiftCodeApp() {
		return giftCodeApp;
	}

	public void setGiftCodeApp(GiftCodeApp giftCodeApp) {
		GameContext.giftCodeApp = giftCodeApp;
	}

	public static LevelGiftApp getLevelGiftApp() {
		return levelGiftApp;
	}

	public void setLevelGiftApp(LevelGiftApp levelGiftApp) {
		GameContext.levelGiftApp = levelGiftApp;
	}

	public static QuestPokerApp getQuestPokerApp() {
		return questPokerApp;
	}

	public void setQuestPokerApp(QuestPokerApp questPokerApp) {
		GameContext.questPokerApp = questPokerApp;
	}

	public static HorseApp getHorseApp() {
		return horseApp;
	}

	public void setHorseApp(HorseApp horseApp) {
		GameContext.horseApp = horseApp;
	}

	public static RoleHorseApp getRoleHorseApp() {
		return roleHorseApp;
	}

	public void setRoleHorseApp(RoleHorseApp roleHorseApp) {
		GameContext.roleHorseApp = roleHorseApp;
	}

	public static LuckyBoxApp getLuckyBoxApp() {
		return luckyBoxApp;
	}

	public void setLuckyBoxApp(LuckyBoxApp luckyBoxApp) {
		GameContext.luckyBoxApp = luckyBoxApp;
	}
	
	public static AsyncArenaApp getAsyncArenaApp() {
		return asyncArenaApp;
	}

	public void setAsyncArenaApp(AsyncArenaApp asyncArenaApp) {
		GameContext.asyncArenaApp = asyncArenaApp;
	}

	public static RoleAsyncArenaApp getRoleAsyncArenaApp() {
		return roleAsyncArenaApp;
	}

	public void setRoleAsyncArenaApp(RoleAsyncArenaApp roleAsyncArenaApp) {
		GameContext.roleAsyncArenaApp = roleAsyncArenaApp;
	}

	public static HeroArenaApp getHeroArenaApp() {
		return heroArenaApp;
	}

	public void setHeroArenaApp(HeroArenaApp heroArenaApp) {
		GameContext.heroArenaApp = heroArenaApp;
	}

	public static HeroArenaStorage getHeroArenaStorage() {
		return heroArenaStorage;
	}

	public void setHeroArenaStorage(HeroArenaStorage heroArenaStorage) {
		GameContext.heroArenaStorage = heroArenaStorage;
	}

	public static UnionApp getUnionApp() {
		return unionApp;
	}

	public void setUnionApp(UnionApp unionApp) {
		GameContext.unionApp = unionApp;
	}

	public static UnionDataApp getUnionDataApp() {
		return unionDataApp;
	}

	public void setUnionDataApp(UnionDataApp unionDataApp) {
		GameContext.unionDataApp = unionDataApp;
	}

	public static UnionDAOImpl getUnionDAO() {
		return unionDAO;
	}

	public void setUnionDAO(UnionDAOImpl unionDAO) {
		GameContext.unionDAO = unionDAO;
	}

	public static UnionInstanceApp getUnionInstanceApp() {
		return unionInstanceApp;
	}

	public void setUnionInstanceApp(UnionInstanceApp unionInstanceApp) {
		GameContext.unionInstanceApp = unionInstanceApp;
	}

	public static UnionAuctionApp getUnionAuctionApp() {
		return unionAuctionApp;
	}

	public void setUnionAuctionApp(UnionAuctionApp unionAuctionApp) {
		GameContext.unionAuctionApp = unionAuctionApp;
	}

	public static RoleHorseStorage getRoleHorseStorage() {
		return roleHorseStorage;
	}

	public void setRoleHorseStorage(RoleHorseStorage roleHorseStorage) {
		GameContext.roleHorseStorage = roleHorseStorage;
	}

	public static RoleAsyncArenaStorage getRoleAsyncArenaStorage() {
		return roleAsyncArenaStorage;
	}

	public void setRoleAsyncArenaStorage(
			RoleAsyncArenaStorage roleAsyncArenaStorage) {
		GameContext.roleAsyncArenaStorage = roleAsyncArenaStorage;
	}

	public static InsLogicDataApp getInsLogicApp() {
		return insLogicApp;
	}

	public void setInsLogicApp(InsLogicDataApp insLogicApp) {
		GameContext.insLogicApp = insLogicApp;
	}

	public static AccumulateLoginApp getAccumulateLoginApp() {
		return accumulateLoginApp;
	}

	public void setAccumulateLoginApp(AccumulateLoginApp accumulateLoginApp) {
		GameContext.accumulateLoginApp = accumulateLoginApp;
	}

	public static ChoiceCardApp getChoiceCardApp() {
		return choiceCardApp;
	}

	public void setChoiceCardApp(ChoiceCardApp choiceCardApp) {
		GameContext.choiceCardApp = choiceCardApp;
	}

	public static RoleChoiceCardApp getRoleChoiceCardApp() {
		return roleChoiceCardApp;
	}

	public void setRoleChoiceCardApp(RoleChoiceCardApp roleChoiceCardApp) {
		GameContext.roleChoiceCardApp = roleChoiceCardApp;
	}

	public static DailyPlayApp getDailyPlayApp() {
		return dailyPlayApp;
	}

	public void setDailyPlayApp(DailyPlayApp dailyPlayApp) {
		GameContext.dailyPlayApp = dailyPlayApp;
	}

	public static ForwardApp getForwardApp() {
		return forwardApp;
	}

	public void setForwardApp(ForwardApp forwardApp) {
		GameContext.forwardApp = forwardApp;
	}

	public static DonateApp getDonateApp() {
		return donateApp;
	}

	public void setDonateApp(DonateApp donateApp) {
		GameContext.donateApp = donateApp;
	}

	public static Arena3V3App getArena3V3App() {
		return arena3V3App;
	}

	public void setArena3V3App(Arena3V3App arena3v3App) {
		arena3V3App = arena3v3App;
	}

}