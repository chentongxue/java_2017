package sacred.alliance.magic.vo;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.exchange.domain.ExchangeDbInfo;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.quest.Quest;
import com.game.draco.app.quest.domain.RoleQuestDailyFinished;
import com.game.draco.app.quest.domain.RoleQuestLogInfo;
import com.game.draco.app.rank.domain.RankDbInfo;
import com.game.draco.app.role.systemset.domain.RoleSystemSet;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.team.PlayerTeam;
import com.game.draco.app.team.Team;
import com.game.draco.app.title.domain.TitleRecord;
import com.game.draco.app.union.domain.Union;
import lombok.Getter;
import lombok.Setter;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.goods.IntervalTimeItem;
import sacred.alliance.magic.app.goods.RoleBackpack;
import sacred.alliance.magic.app.goods.WarehousePack;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.*;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.*;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoleInstance extends AbstractRole {
	public RoleInstance() {
		this.roleType = RoleType.PLAYER;
	}
	
	/*
	 * 
	 * 是否在剧情模式
	 * 剧情模式下npc看人为中立
	 * 角色行走,使用技能中断剧情模式
	 */
	@Getter @Setter private boolean dramaState = false ;
	@Getter @Setter private AtomicInteger hitCombo = new AtomicInteger(0);
	@Getter @Setter private int braveSoul;//勇者之魂(可通过分解英雄等得到)
	@Getter @Setter private int wildBlood;//狂野之血(可通过分解宠物等得到)
	
	private String resType = "webp" ;
	/**
	 * 是否登录完成
	 * 当角色进入地图后，发送请求广告消息表示登录完成
	 */
	private boolean loginCompleted = false ;
	private String loginIp;//登录IP地址
	private String channelUserId;//渠道用户ID
	private String channelAccessToken;//渠道token
	private String channelRefreshToken;//渠道刷新token
	private String ostype;//登录系统类型
	/**
	 * 挂机经验提示标识
	 * 0 继续弹出
	 * 1 不再弹出
	 */
	@Setter @Getter private byte hookExpFlag = 0 ;

	private Map<Integer,IntervalTimeItem> timeMap = new HashMap<Integer,IntervalTimeItem>();
	
	public Map<Integer, IntervalTimeItem> getTimeMap() {
		return timeMap;
	}

	public void setTimeMap(Map<Integer, IntervalTimeItem> timeMap) {
		this.timeMap = timeMap;
	}
	
	@Setter @Getter private int campPrestige ;//阵营声望

	private boolean offlined = false; // 下线入库标识

	// 快速购买
	private Message currCanQuickBuyReqMessage = null;// 当前可进行快速购买的请求消息（单用户单线程请求消息）
	private Message lastTrigQuickBuyReqMessage = null;// 上次触发快速购买的请求消息（单用户单线程请求消息）
	private boolean quickBuyconfirm = false;//快捷购买的确认购买标记
	private boolean quickBuyAutoConfirm = false;//快捷购买自动确认标记

	private int loginLevel = 0;
	/**
	 * 推荐阵营ID
	 */
	@Setter @Getter private byte recommendCampId = -1 ;

	// 角色背包容器对象
	private int backpackCapacity;// 背包容量
	private int warehoseCapacity;//仓库容量
	private RoleBackpack roleBackpack;// 角色背包容器对象
	private WarehousePack warehousePack; //角色仓库容器
	private Lock goodsLock = new ReentrantLock(); // 物品容器锁

	private RoleArena roleArena;	// 角色擂台赛记录

	private long arenaLearnInviteTime; // 切磋邀请时间

	// 角色属性变化,飘字列表
	private AttrFontInfo[] attrFontList = new AttrFontInfo[ParasConstant.ATTRFONT_MAX_SIZE];
	private int attrFontNextIndex = 0;

	// 角色任务
	private int lastFinishQuestId = 0;//最后完成任务ID（数据库保存）
	private Set<Integer> questTimeLimitSet = new HashSet<Integer>(); // 限时任务（用于提高性能）
	private RoleQuestDailyFinished questDailyFinished;//已经完成的日常任务
	private Map<Integer, RoleQuestLogInfo> questLogMap = new HashMap<Integer, RoleQuestLogInfo>(); // 正在做的任务日志
	private LoopCount questLoopCount = new LoopCount(LoopConstant.ROLE_QUEST_CYCLE);
	
	// 通行证
	private String userId;
	private String roleName;
	private short passportType; // 通行证类型
	private String userName;
	private int honor; // 荣誉
	private int potential;// 潜能
	private int lq;// 灵气
	private int silverMoney;// 银币
	private int roleConsumeGold;//角色消费的总金条
	private int rolePayGold;//充值总金条
	private int dkp;
	
	private byte sex;// 性别
	private byte career;// 职业
	//最高亲密度等级
	private int maxIntimateLevel = 0;
	/**
	 * 创建角色时的serverId
	 */
	private int createServerId = 0 ;
	
	private Team team ; 
	
	/**
	 * 战斗力(主要用于战斗力排行榜初始化)
	 */
	private int battleScore ; 
	private int wingResId ;//翅膀资源
	private int clothesResId;//衣服资源
	
	/**
	 * 登录，切换英雄的时候初始化
	 */
	@Setter @Getter private byte heroSeriesId ; //系id
	@Setter @Getter private byte heroGearId ; //档位id
	@Setter @Getter private short heroHeadId = RespTypeStatus.DEFAULT_HERO_HEAD_ID; //英雄头像id

	private AtomicBoolean stateLock = new AtomicBoolean(false);

	private String unionId = ""; // 公会id
	private Map<String, FactionContribute> factionContributeMap = new HashMap<String, FactionContribute>();// 公会贡献度

	private int regChannelId = 0;// 注册渠道ID
	private int channelId = 0;// 登录渠道ID
	private Date userRegTime = null; // 注册时间
	private int historyOnlineTime;
	private long dayOnlineTime;
	private Date createTime; // 角色创建时间
	private Date lastLoginTime; // 上次登录时间
	private Date lastOffTime; // 最后下线时间
	private int levelUpTime = 0;// 玩家等级升级所用时间(默认为0)
	private Date lastLevelUpTime;// 上次升级时间(不入库)

	//聊天频道最后说话时间
	private Date[] chatLastSpeakTime;

	private long tradingApplyTime = 0;// 交易请求时间
	private long teamApplyTime = 0;// 组队请求时间
	private long unionBeInviteTime = 0;// 被帮主招纳时间
	private long lastKeFuHelpTime = 0;// 上次客服求助时间

	private RoleSystemSet systemSet;// 系统设置（聊天、组队、交易等）
	private Date frozenEndTime; // 角色隔离截止时间
	private int forbidType; // 1:全部禁言 2:除世界和私聊之外的禁言
	private Date forbidEndTime; // 角色禁言禁止时间
	private Date frozenBeginTime; // 角色隔离起始时间
	private String frozenMemo; // 角色隔离原因
	private Date forbidBeginTime; // 角色禁言起始时间
	private String forbidMemo; // 角色禁言原因
	private long lastHeartNumber = System.currentTimeMillis();// 记录心跳时间
	private long lastWriteDBTime = System.currentTimeMillis(); // 记录上次发定时入库协议时间

	// 已经领取的补偿id
	private String receiveRecoup = "";
	private Set<String> receiveRecoupSet = new HashSet<String>();

	private short screenWidth;
	private short screenHeight;
	private long tradingId = 0;// 交易中的交易id

	// 拍卖行
	private long auctionSearchTime = 0;// 拍卖行上次搜索时间
	private long auctionMyShelfSearchTime = 0;// 拍卖行自己货架搜索时间

	// 排行榜
	private RoleCount roleCount = null;// 排行榜计数

	// 兑换
	private Map<Integer, ExchangeDbInfo> exchangeDbInfo = new HashMap<Integer, ExchangeDbInfo>();// 物品兑换记录
	private long activationDateTime;// 激活码领取时间(用于激活码领取CD计算使用)

	// 过图操作标识
	private AtomicBoolean jumpMap = new AtomicBoolean(false);

	// 玩家称号<称号ID，称号对像>
	private Map<Integer, TitleRecord> titleMap = new HashMap<Integer, TitleRecord>();
	// 当前激活的称号
	private List<TitleRecord> currTitleList = null ;

	// 付费玩家
	private boolean payUser;

	private Point copyBeforePoint = null;// 进入副本之前的角色位置
	private String copyLostReLoginInfo = null;//副本掉线重新登录的信息(副本容器ID,地图实例ID)
	// 副本容器ID
	private String copyContainerId = null;

	private byte factionSalaryCount;
	private Date factionActiveTime;
	private String factionDonate;
	private Map<Integer,Integer> factionDonateMap = new HashMap<Integer, Integer>();
	
	private Map<Integer, SummonDbInfo> summonDbInfo = new HashMap<Integer, SummonDbInfo>();// 召唤记录
	
	//充值对像
	private RolePayRecord rolePayRecord = new RolePayRecord();
	
	private Date dayLoginTime; //每天登陆的时间(计算每天在线时长用)
	
	private long angelChestTime;//开宝箱计算用的时间
	
	private int speedUpCount;//加速次数
	private int speedUpNotCount;//未加速加速次数
	private AtomicInteger mapChangeSeq = new AtomicInteger(0); //玩家跳地图次数，每请求1次自动加1
	
	private boolean queryGamble = false;
	
	private byte pkStatus;
	
	@Getter @Setter private short protectBuffId;
	private int heroCoin; //徽章
	@Getter @Setter private int arena3v3Score ;
	
	@Getter @Setter
	private int prestigePoints;// 威望
	@Getter @Setter
	private int totalPrestigePoints;// 累计威望
	
	//不需要计算战斗力的属性
	private AttriBuffer bsNoAffectBuffer = AttriBuffer.createAttriBuffer();
	
	public AttriBuffer getBsNoAffectBuffer() {
		return bsNoAffectBuffer;
	}

	public RolePayRecord getRolePayRecord() {
		return rolePayRecord;
	}

	public void setRolePayRecord(RolePayRecord rolePayRecord) {
		this.rolePayRecord = rolePayRecord;
	}
	
	public int getGoldMoney() {
		return this.getRolePayRecord().getCurrMoney();
	}

	public void setGoldMoney(int goldMoney) {
		if(goldMoney>ParasConstant.GOLD_MONEY){
			this.getRolePayRecord().setCurrMoney(ParasConstant.GOLD_MONEY);
		}else{
			this.getRolePayRecord().setCurrMoney(goldMoney);
		}
	}
	
	public int getTotalCoupon() {
		return GameContext.getRichManApp().getTotalCoupon(this.getIntRoleId());
	}
	
	public void setTotalCoupon(int coupon) {
		GameContext.getRichManApp().setTotalCoupon(this.getIntRoleId(), coupon);
	}
	
	public int getTodayCoupon() {
		return GameContext.getRichManApp().getTodayCoupon(this.getIntRoleId());
	}
	
	public void setToadyCoupon(int todayCoupon) {
		GameContext.getRichManApp().setTodayCoupon(this.getIntRoleId(), todayCoupon);
	}
	
	public Point originalCopyBeforePoint(){
		return this.copyBeforePoint ;
	}
	
	/**
	 * 获取角色进入副本之前的合法位置
	 * !!!!!! 
	 * 不要在MapInstance(包括子类).getRebornPoint(x,x)方法中调用
	 * 否则会发生相互调用的问题
	 * !!!!!!
	 */
	@Override
	public Point getCopyBeforePoint() {
		//①首先判断进副本之前的位置是否合法
		if(null != this.copyBeforePoint && this.copyBeforePoint.isDefaultMap()){
			return this.copyBeforePoint;
		}
		//②其次判断当前地图的死亡复活点是否合法
		MapInstance mapInstance = this.getMapInstance();
		if(null != mapInstance){
			Point point = mapInstance.getRebornPoint(this, RebornType.rebornPoint);
			if(null != point && point.isDefaultMap()){
				return point;
			}
		}
		//③最后取副本的容错点
		return GameContext.getCopyLogicApp().getFailurePoint();
	}
	
	/** 进入副本前，记录角色的位置 */
	@Override
	public void setCopyBeforePoint(String mapId, int x, int y) {
		if(Util.isEmpty(mapId)){
			return ;
		}
		Point p = new Point(mapId, x, y);
		if(!p.isDefaultMap()){
			return ;
		}
		this.copyBeforePoint = new Point(mapId, x, y);
	}


	/* 返回公会等级 */
	public int getUnionLevel() {
		Union union = getUnion();
		if (union == null) {
			return 0;
		}
		return union.getUnionLevel();
	}


	/** 获取角色在某个门派的贡献值 */
	public int getFactionContributeValue(String factionId) {
		FactionContribute contribute = this.getFactionContribute(factionId);
		return null == contribute ? 0 : contribute.getContribute();
	}
	
	/** 获取角色在某个门派的贡献值 */
	public int getFactionTotalContributeValue(String factionId) {
		FactionContribute contribute = this.getFactionContribute(factionId);
		return null == contribute ? 0 : contribute.getTotalContribute();
	}


	/**
	 * 获取角色在某个门派的贡献度
	 * 
	 * @param factionId
	 * @return
	 */
	public FactionContribute getFactionContribute(String factionId) {
		return this.factionContributeMap.get(factionId);
	}

	public long getArenaLearnInviteTime() {
		return arenaLearnInviteTime;
	}

	public void setArenaLearnInviteTime(long arenaLearnInviteTime) {
		this.arenaLearnInviteTime = arenaLearnInviteTime;
	}

	public boolean isPayUser() {
		return payUser;
	}

	public void setPayUser(boolean payUser) {
		this.payUser = payUser;
	}

	// 排行榜活动
	private Map<Integer, RankDbInfo> rankDbInfo = new HashMap<Integer, RankDbInfo>();

	/** 排行榜活动数据库信息 */
	public Map<Integer, RankDbInfo> getRankDbInfo() {
		return rankDbInfo;
	}

	public void setRankDbInfo(Map<Integer, RankDbInfo> rankDbInfo) {
		this.rankDbInfo = rankDbInfo;
	}

	/** 添加称号 */
	public void addTitle(TitleRecord title) {
		titleMap.put(title.getTitleId(), title);
	}
	
	public void addCurrentTitle(TitleRecord title){
		if(null == title){
			return ;
		}
		title.activate();
		if(null == this.currTitleList){
			this.currTitleList = new ArrayList<TitleRecord>();
		}
		currTitleList.add(title);
	}
	
	/**
	 * 是否需要刷新属性
	 * @param title
	 * @return
	 */
	public boolean removeCurrentTitle(TitleRecord title){
		if(null == title){
			return false;
		}
		title.cancel();
		if(null == this.currTitleList){
			return false;
		}
		for(Iterator<TitleRecord> it = this.currTitleList.iterator();it.hasNext();){
			TitleRecord current = it.next() ;
			if(current.getTitleId() != title.getTitleId()){
				continue ;
			}
			it.remove();
			return true;
		}
		return false;
	}

	/** 获取称号 */
	public TitleRecord getTitleRecord(int titleId) {
		return titleMap.get(titleId);
	}
	

	/** 获取任务日志记录 */
	public RoleQuestLogInfo getQuestLogInfo(int questId) {
		return this.questLogMap.get(questId);
	}

	/** 是否曾经完成过某个任务 */
	public boolean hasFinishQuest(int questId) {
		if(this.lastFinishQuestId == questId){
			return true;
		}
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		if(null == quest){
			return false;
		}
		//主线任务，节点位置在最后完成任务节点之前，表示已完成。
		if(quest.isMainLine()){
			Quest lastFinishQuest = GameContext.getQuestApp().getQuest(this.lastFinishQuestId);
			//如果
			if(null == lastFinishQuest){
				return false;
			}
			return quest.getChainIndex() <= lastFinishQuest.getChainIndex();
		}
		if(quest.isDailyQuest()){
			return this.questDailyFinished.hasFinishedQuest(questId);
		}
		return false;
	}

	/** 是否正在做某个任务 */
	public boolean hasReceiveQuestNow(int questId) {
		return this.questLogMap.containsKey(questId);
	}


	@Override
	public boolean set(byte enumValue, int value) {
		AttributeType attriType = AttributeType.get(enumValue);
		switch (attriType) {
		case honor:
			this.setHonor(value);return true;
		case potential:
			this.setPotential(value);return true;
		case goldMoney:this.setGoldMoney(value);return true;
		case gameMoney:this.setSilverMoney(value);return true;
		case residueGoldMoney:this.setRoleConsumeGold(value);return true;
		case lq :
			this.setLq(value); return true ;
		case coupon:
			this.setTotalCoupon(value); return true;
		case todayCoupon:
			this.setToadyCoupon(value); return true;
		case dkp:
			this.setDkp(value); return true;
		case campPrestige :
			this.setCampPrestige(value); return true ;
		case heroCoin :
			this.setHeroCoin(value);return true;
		case expHook :
			this.getRoleCount().changeTimes(CountType.TodayHookExp,value); return true ;//setTodayHookExp(value);
		case maxExpHook :
			//不用操作
			return true ;
		case prestigePoints:
			this.setPrestigePoints(value);
			return true ;
		case totalPrestigePoints:
			this.setTotalPrestigePoints(value);
			return true ;
		case kingPoints:
			return true ;
		case superGodPoints:
			return true ;
		case tulongPoints:
			return true ;
		case petAtk:
			return true;
		case braveSoul:
			setBraveSoul(value);
			return true;
		case wildBlood:
			setWildBlood(value);
			return true;
		case talent:
			return true;
		case arena3V3Score:
			this.setArena3v3Score(value);
			return true ;
		default:
			return super.set(enumValue, value);
		}

	}

	public int get(AttributeType attriType) {
		if (null == attriType) {
			return 0;
		}
		switch (attriType) {
		case honor:
			return this.getHonor();
		case potential:
			return this.getPotential();
		case battleScore:
			return this.getBattleScore();
		case goldMoney:return this.getGoldMoney();
		case gameMoney:return this.getSilverMoney();
		case residueGoldMoney:return this.getRoleConsumeGold();
		case sex:return this.getSex();
		/*case curPower: return this.getCurPower() ;
		case maxPower: return this.getMaxPower() ;*/
		case lq :
			return this.getLq();
		case coupon:
			return this.getTotalCoupon();
		case todayCoupon:
			return this.getTodayCoupon();
		case dkp:
			return GameContext.getUnionApp().getUnionMemberDkp(getUnionId(),getIntRoleId());
		case campPrestige :
			return this.getCampPrestige() ;
		case heroCoin:
			return this.getHeroCoin();
		case expHook :
			return this.getRoleCount().getRoleTimesToInt(CountType.TodayHookExp);//getTodayHookExp() ;
		case maxExpHook :
			return GameContext.getAttriApp().getMaxValue(this, AttributeType.expHook);
		case prestigePoints:
			return this.getPrestigePoints() ;
		case totalPrestigePoints:
			return this.getTotalPrestigePoints();
		case kingPoints:
			return 0 ;
		case superGodPoints:
			return 0 ;
		case tulongPoints:
			return 0 ;
		case braveSoul: 
			return this.getBraveSoul();
		case wildBlood: 
			return this.getWildBlood();     
		case talent:
			return GameContext.getRoleTalentApp().getRoleTalent(getIntRoleId()).getTalent();
		case arena3V3Score :
			return this.getArena3v3Score();
		default:
			return super.get(attriType);
		}
	}

	public boolean hasUnion() {
		return !Util.isEmpty(unionId);
	}

	public Union getUnion() {
		return GameContext.getUnionApp().getUnion(this);
	}
	
	public String toString(String flag) {
		StringBuffer buffer = new StringBuffer();
		if (flag != null && flag.length() > 0) {
			buffer.append(flag).append(",");
		}
		buffer.append(this.getUserName()).append(",");
		buffer.append(this.getRoleId()).append(",");
		buffer.append(this.getLevel()).append(",");
		buffer.append(this.getExp()).append(",");
		buffer.append(this.getSilverMoney()).append(",");
		buffer.append(this.getGoldMoney()).append(",");
		buffer.append(this.getRoleConsumeGold()).append(",");
		buffer.append(this.getHonor());
		return buffer.toString();
	}

	public void offlineLog() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(roleId);
			sb.append(Cat.pound);
			sb.append(userId);
			sb.append(Cat.pound);
			sb.append(userName);
			sb.append(Cat.pound);
			sb.append(channelId);
			sb.append(Cat.pound);
			sb.append(roleName);
			sb.append(Cat.pound);
			sb.append(this.getSex());
			sb.append(Cat.pound);
			sb.append(this.getCareer());
			sb.append(Cat.pound);
			sb.append(this.getLevel());
			sb.append(Cat.pound);
			sb.append(this.getExp());
			sb.append(Cat.pound);;
			sb.append(this.getSilverMoney());
			sb.append(Cat.pound);
			sb.append(this.getMapId());
			sb.append(Cat.pound);
			sb.append(this.getMapX());
			sb.append(Cat.pound);
			sb.append(this.getMapY());
			sb.append(Cat.pound);
			sb.append(dayOnlineTime);
			sb.append(Cat.pound);
			sb.append(historyOnlineTime);
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(this.getLastLoginTime()));
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(this.getCreateTime()));
			sb.append(Cat.pound);
			sb.append(backpackCapacity);
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(lastOffTime));
			sb.append(Cat.pound);
			sb.append(this.getCurHP());
			sb.append(Cat.pound);
			sb.append(unionId);
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(frozenBeginTime));
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(frozenEndTime));
			sb.append(Cat.pound);
			sb.append(frozenMemo);
			sb.append(Cat.pound);
			sb.append(forbidType);
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(forbidBeginTime));
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(forbidEndTime));
			sb.append(Cat.pound);
			sb.append(forbidMemo);
			sb.append(Cat.pound);
			sb.append(this.getTotalExp());
			sb.append(Cat.pound);
			sb.append(0);
			sb.append(Cat.pound);
			sb.append("");
			sb.append(Cat.pound);
			sb.append(levelUpTime);
			sb.append(Cat.pound);
			sb.append(this.getRolePayGold());
			sb.append(Cat.pound);
			sb.append(this.getRoleConsumeGold());
			sb.append(Cat.pound);
			sb.append(receiveRecoup);
			sb.append(Cat.pound);

			Log4jManager.OFFLINE_ROLE_DB_LOG.info(sb.toString());
		} catch (Exception e) {
			logger.error("logoutLog:", e);
		}
	}


	/** 得到当然使用的技能 */
	public Map<Short, RoleSkillStat> getCurrentSkillMap() {
		return this.getSkillMap();
	}


	public RoleArena getRoleArena() {
		if (null != this.roleArena) {
			this.roleArena.check();
		}
		return roleArena;
	}


	/**
	 * ****************************** getter/setter
	 * *********************************************
	 */

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getHistoryOnlineTime() {
		return historyOnlineTime;
	}

	public void setHistoryOnlineTime(int historyOnlineTime) {
		this.historyOnlineTime = historyOnlineTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
		this.setDayLoginTime(lastLoginTime);
	}

	public String getUnionId() {
		if(!Util.isEmpty(unionId) || GameContext.getOnlineCenter().isOnlineByRoleId(roleId)){
			return unionId;
		}
		return GameContext.getUnionApp().getUnionId(getIntRoleId());
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public Map<Integer, RoleQuestLogInfo> getQuestLogMap() {
		return questLogMap;
	}

	public void setQuestLogMap(Map<Integer, RoleQuestLogInfo> questLogMap) {
		this.questLogMap = questLogMap;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public int getBackpackCapacity() {
		return backpackCapacity;
	}

	// 设置背包格子数小于默认背包格子数时，设置为默认格子数
	public void setBackpackCapacity(int backpackCapacity) {
		if (backpackCapacity < ParasConstant.ROLE_BACKPACK_DEF_NUM) {
			backpackCapacity = ParasConstant.ROLE_BACKPACK_DEF_NUM;
		}
		this.backpackCapacity = backpackCapacity;
	}

	public Date getLastOffTime() {
		return lastOffTime;
	}

	public void setLastOffTime(Date lastOffTime) {
		this.lastOffTime = lastOffTime;
	}

	public Date getFrozenEndTime() {
		return frozenEndTime;
	}

	public void setFrozenEndTime(Date frozenEndTime) {
		this.frozenEndTime = frozenEndTime;
	}

	public int getForbidType() {
		return forbidType;
	}

	public void setForbidType(int forbidType) {
		this.forbidType = forbidType;
	}

	public Date getForbidEndTime() {
		return forbidEndTime;
	}

	public void setForbidEndTime(Date forbidEndTime) {
		this.forbidEndTime = forbidEndTime;
	}

	public LoopCount getQuestLoopCount() {
		return questLoopCount;
	}

	public void setQuestLoopCount(LoopCount questLoopCount) {
		this.questLoopCount = questLoopCount;
	}

	public Date getUserRegTime() {
		return userRegTime;
	}

	public void setUserRegTime(Date userRegTime) {
		this.userRegTime = userRegTime;
	}

	public short getPassportType() {
		return passportType;
	}

	public void setPassportType(short passportType) {
		this.passportType = passportType;
	}

	public long getDayOnlineTime() {
		return dayOnlineTime;
	}

	public void setDayOnlineTime(long dayOnlineTime) {
		this.dayOnlineTime = dayOnlineTime;
	}

	public short getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(short screenWidth) {
		this.screenWidth = screenWidth;
	}

	public short getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(short screenHeight) {
		this.screenHeight = screenHeight;
	}


	public long getTradingId() {
		return tradingId;
	}

	public void setTradingId(long tradingId) {
		this.tradingId = tradingId;
	}

	public long getLastHeartNumber() {
		return lastHeartNumber;
	}

	public void setLastHeartNumber(long lastHeartNumber) {
		this.lastHeartNumber = lastHeartNumber;
	}

	public long getLastWriteDBTime() {
		return lastWriteDBTime;
	}

	public void setLastWriteDBTime(long lastWriteDBNumber) {
		this.lastWriteDBTime = lastWriteDBNumber;
	}

	public void setFrozenBeginTime(Date frozenBeginTime) {
		this.frozenBeginTime = frozenBeginTime;
	}

	public void setFrozenMemo(String frozenMemo) {
		this.frozenMemo = frozenMemo;
	}

	public void setForbidBeginTime(Date forbidBeginTime) {
		this.forbidBeginTime = forbidBeginTime;
	}

	public void setForbidMemo(String forbidMemo) {
		this.forbidMemo = forbidMemo;
	}

	public Date getFrozenBeginTime() {
		return frozenBeginTime;
	}

	public String getFrozenMemo() {
		return frozenMemo;
	}

	public Date getForbidBeginTime() {
		return forbidBeginTime;
	}

	public String getForbidMemo() {
		return forbidMemo;
	}

	public AtomicBoolean getStateLock() {
		return stateLock;
	}

	public void setStateLock(AtomicBoolean stateLock) {
		this.stateLock = stateLock;
	}


	/** 升级影响值 */
	public void roleLevelUp(int upLevel) {
		this.updateLevelUpTime(upLevel);
		this.setLastLevelUpTime(new Date());
		this.setLevelUpTime(0);
	}

	/** 更新等级升级时间 */
	public void updateLevelUpTime(int upLevel) {
		this.updateLevelUpTime();
		// 角色升级日志
		GameContext.getStatLogApp().roleLevelUpLog(this, upLevel);
	}

	/** 更新等级升级时间(下线时调用) */
	public void updateLevelUpTime() {
		try {
			Date d = this.lastLoginTime;
			if (null != this.getLastLevelUpTime()) {
				d = this.getLastLevelUpTime();
			}
			int time = DateUtil.dateDiffSecond(d, new Date());
			this.setLevelUpTime(time + this.getLevelUpTime());
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public boolean hasSkill(short skillId) {
		return this.getSkillMap().containsKey(skillId);
	}

	public RoleSkillStat getSkillStat(short skillId) {
		return this.getSkillMap().get(skillId);
	}

	public Date[] getChatLastSpeakTime() {
		return chatLastSpeakTime;
	}

	public void setChatLastSpeakTime(Date[] chatLastSpeakTime) {
		this.chatLastSpeakTime = chatLastSpeakTime;
	}


	public Set<Integer> getQuestTimeLimitSet() {
		return questTimeLimitSet;
	}

	public void setQuestTimeLimitSet(Set<Integer> questTimeLimitSet) {
		this.questTimeLimitSet = questTimeLimitSet;
	}

	public AtomicBoolean getJumpMap() {
		return jumpMap;
	}

	public void setJumpMap(AtomicBoolean jumpMap) {
		this.jumpMap = jumpMap;
	}
	
	public AttrFontInfo[] getAttrFontList() {
		return attrFontList;
	}

	public void setAttrFontList(AttrFontInfo[] attrFontList) {
		this.attrFontList = attrFontList;
	}

	public int getAttrFontNextIndex() {
		return attrFontNextIndex;
	}

	public void setAttrFontNextIndex(int attrFontNextIndex) {
		this.attrFontNextIndex = attrFontNextIndex;
	}

	public RoleSystemSet getSystemSet() {
		return systemSet;
	}

	public void setSystemSet(RoleSystemSet systemSet) {
		this.systemSet = systemSet;
	}

	

	public void setRoleArena(RoleArena roleArena) {
		this.roleArena = roleArena;
	}

	public RoleBackpack getRoleBackpack() {
		return roleBackpack;
	}

	public void setRoleBackpack(RoleBackpack roleBackpack) {
		this.roleBackpack = roleBackpack;
	}

	public Lock getGoodsLock() {
		return goodsLock;
	}

	public void setGoodsLock(Lock goodsLock) {
		this.goodsLock = goodsLock;
	}

	public long getTradingApplyTime() {
		return tradingApplyTime;
	}

	public void setTradingApplyTime(long tradingApplyTime) {
		this.tradingApplyTime = tradingApplyTime;
	}

	public long getTeamApplyTime() {
		return teamApplyTime;
	}

	public void setTeamApplyTime(long teamApplyTime) {
		this.teamApplyTime = teamApplyTime;
	}

	public int getLoginLevel() {
		return loginLevel;
	}

	public void setLoginLevel(int loginLevel) {
		this.loginLevel = loginLevel;
	}


	public int getLevelUpTime() {
		return levelUpTime;
	}

	public void setLevelUpTime(int levelUpTime) {
		this.levelUpTime = levelUpTime;
	}

	public Date getLastLevelUpTime() {
		return lastLevelUpTime;
	}

	public void setLastLevelUpTime(Date lastLevelUpTime) {
		this.lastLevelUpTime = lastLevelUpTime;
	}
	
	public boolean isQuickBuyconfirm() {
		return quickBuyconfirm;
	}

	public void setQuickBuyconfirm(boolean quickBuyconfirm) {
		this.quickBuyconfirm = quickBuyconfirm;
	}

	public boolean isQuickBuyAutoConfirm(int payGoldMoney) {
		/*
		 * 如果有自动确认的元宝上限
		 * 当支付元宝数超过上限时，每次都会弹出确认面板
		 */
		int maxGold = GameContext.getQuickBuyConfig().getAutoConfirmMaxGold();
		if(maxGold > 0 && payGoldMoney > maxGold){
			return false;
		}
		return quickBuyAutoConfirm;
	}

	public void setQuickBuyAutoConfirm(boolean quickBuyAutoConfirm) {
		this.quickBuyAutoConfirm = quickBuyAutoConfirm;
	}

	public long getAuctionSearchTime() {
		return auctionSearchTime;
	}

	public void setAuctionSearchTime(long auctionSearchTime) {
		this.auctionSearchTime = auctionSearchTime;
	}

	public long getAuctionMyShelfSearchTime() {
		return auctionMyShelfSearchTime;
	}

	public void setAuctionMyShelfSearchTime(long auctionMyShelfSearchTime) {
		this.auctionMyShelfSearchTime = auctionMyShelfSearchTime;
	}

	public int getRegChannelId() {
		return regChannelId;
	}

	public void setRegChannelId(int regChannelId) {
		this.regChannelId = regChannelId;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public long getLastKeFuHelpTime() {
		return lastKeFuHelpTime;
	}

	public void setLastKeFuHelpTime(long lastKeFuHelpTime) {
		this.lastKeFuHelpTime = lastKeFuHelpTime;
	}

	public Message getCurrCanQuickBuyReqMessage() {
		return currCanQuickBuyReqMessage;
	}

	public void setCurrCanQuickBuyReqMessage(Message currCanQuickBuyReqMessage) {
		this.currCanQuickBuyReqMessage = currCanQuickBuyReqMessage;
	}

	public Message getLastTrigQuickBuyReqMessage() {
		return lastTrigQuickBuyReqMessage;
	}

	public void setLastTrigQuickBuyReqMessage(Message lastTrigQuickBuyReqMessage) {
		this.lastTrigQuickBuyReqMessage = lastTrigQuickBuyReqMessage;
	}

	public RoleCount getRoleCount() {
		if(null != roleCount){
			roleCount.resetDay();
		}
		return roleCount;
	}
	
	public RoleCount originalRoleCount() {
		return roleCount;
	}

	public void setRoleCount(RoleCount roleCount) {
		this.roleCount = roleCount;
	}

	public Map<Integer, ExchangeDbInfo> getExchangeDbInfo() {
		return exchangeDbInfo;
	}

	public void setExchangeDbInfo(Map<Integer, ExchangeDbInfo> exchangeDbInfo) {
		this.exchangeDbInfo = exchangeDbInfo;
	}

	public long getActivationDateTime() {
		return activationDateTime;
	}

	public void setActivationDateTime(long activationDateTime) {
		this.activationDateTime = activationDateTime;
	}


	public Map<Integer, TitleRecord> getTitleMap() {
		return titleMap;
	}

	public List<TitleRecord> getCurrTitleList() {
		return currTitleList;
	}

	
	public String getReceiveRecoup() {
		return receiveRecoup;
	}

	public void setReceiveRecoup(String receiveRecoup) {
		this.receiveRecoup = receiveRecoup;
	}

	public Set<String> getReceiveRecoupSet() {
		return receiveRecoupSet;
	}

	public void setReceiveRecoupSet(Set<String> receiveRecoupSet) {
		this.receiveRecoupSet = receiveRecoupSet;
	}


	public boolean isOfflined() {
		return offlined;
	}

	public void setOfflined(boolean offlined) {
		this.offlined = offlined;
	}

	public long getUnionBeInviteTime() {
		return unionBeInviteTime;
	}

	public void setUnionBeInviteTime(long unionBeInviteTime) {
		this.unionBeInviteTime = unionBeInviteTime;
	}

	public Map<String, FactionContribute> getFactionContributeMap() {
		return factionContributeMap;
	}

	public void setFactionContributeMap(
			Map<String, FactionContribute> factionContributeMap) {
		this.factionContributeMap = factionContributeMap;
	}

	public int getHonor() {
		return honor;
	}

	public void setHonor(int honor) {
		this.honor = honor;
	}

	public int getPotential() {
		return potential;
	}

	public void setPotential(int potential) {
		this.potential = Util.maxZero(potential);
	}
	
	public int getLq() {
		return lq;
	}
	
	public void setLq(int lq) {
		this.lq = lq;
	}

	public int getDkp() {
		return dkp;
	}

	public void setDkp(int dkp) {
		this.dkp = dkp;
	}

	public RoleQuestDailyFinished getQuestDailyFinished() {
		return questDailyFinished;
	}

	public void setQuestDailyFinished(RoleQuestDailyFinished questDailyFinished) {
		this.questDailyFinished = questDailyFinished;
	}
	
	
	public void syncBattleScore(){
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		buffer.append(this.bsNoAffectBuffer);
		buffer.reverse();
		buffer.append(this.getAttriCache().values());
		int battleScore = GameContext.getAttriApp().getAttriBattleScore(buffer);
		//技能
		battleScore += GameContext.getSkillApp().getSkillBattleScore(this.skillMap);
		//加上未出战的英雄战斗力
		battleScore += GameContext.getHeroApp().getNotOnBattleScore(this.getRoleId());
		this.battleScore = battleScore ;
	}
	
	public void setWarehoseCapacity(int warehoseCapacity) {
		this.warehoseCapacity = warehoseCapacity;
	}

	public int getWarehoseCapacity() {
		return warehoseCapacity;
	}

	public WarehousePack getWarehousePack() {
		return warehousePack;
	}

	public void setWarehousePack(WarehousePack warehousePack) {
		this.warehousePack = warehousePack;
	}

	public byte getFactionSalaryCount() {
		isSameDay();
		return factionSalaryCount;
	}

	public void setFactionSalaryCount(byte factionSalaryCount) {
		this.factionSalaryCount = factionSalaryCount;
	}
	
	public Date getFactionActiveTime() {
		return factionActiveTime;
	}

	public void setFactionActiveTime(Date factionActiveTime) {
		this.factionActiveTime = factionActiveTime;
	}
	
	private void isSameDay(){
		Date now = new Date();
		if(DateUtil.sameDay(new Date(), getFactionActiveTime())){
			return;
		}
		setFactionSalaryCount((byte)0);
		setFactionDonateMap(new HashMap<Integer, Integer>());
		setFactionActiveTime(now);
	}

	public Map<Integer, SummonDbInfo> getSummonDbInfo() {
		return summonDbInfo;
	}

	public void setSummonDbInfo(Map<Integer, SummonDbInfo> summonDbInfo) {
		this.summonDbInfo = summonDbInfo;
	}

	public int getBattleScore() {
		return battleScore;
	}

	public void setBattleScore(int battleScore) {
		this.battleScore = battleScore;
	}
	
	/**
	 * 同步外貌资源
	 */
	public void syncOutwardRes() {
		RoleShape info = GameContext.getUserRoleApp().getRoleShape(this.roleId);
		// 顺便更新下角色对象上数据
		this.setClothesResId(info.getClothesResId());
		this.setWingResId(info.getWingResId());
	}

	public int getWingResId() {
		return wingResId;
	}

	public void setWingResId(int wingResId) {
		this.wingResId = wingResId;
	}

	public int getClothesResId() {
		return clothesResId;
	}

	public void setClothesResId(int clothesResId) {
		this.clothesResId = clothesResId;
	}
	
	/** 副本容器ID,地图实例ID */
	public String getCopyLostReLoginInfo() {
		return copyLostReLoginInfo;
	}
	
	/** 副本容器ID,地图实例ID */
	public void setCopyLostReLoginInfo(String copyLostReLoginInfo){
		this.copyLostReLoginInfo = copyLostReLoginInfo;
	}


	public String getChannelRefreshToken() {
		return channelRefreshToken;
	}

	public void setChannelRefreshToken(String channelRefreshToken) {
		this.channelRefreshToken = channelRefreshToken;
	}

	public String getChannelAccessToken() {
		return channelAccessToken;
	}

	public void setChannelAccessToken(String channelAccessToken) {
		this.channelAccessToken = channelAccessToken;
	}

	public String getChannelUserId() {
		return channelUserId;
	}

	public void setChannelUserId(String channelUserId) {
		this.channelUserId = channelUserId;
	}

	public Date getDayLoginTime() {
		return dayLoginTime;
	}

	public void setDayLoginTime(Date dayLoginTime) {
		this.dayLoginTime = dayLoginTime;
	}

	public String getFactionDonate() {
		return factionDonate;
	}

	public void setFactionDonate(String factionDonate) {
		this.factionDonate = factionDonate;
	}

	public Map<Integer, Integer> getFactionDonateMap() {
		isSameDay();
		return factionDonateMap;
	}

	public void setFactionDonateMap(Map<Integer, Integer> factionDonateMap) {
		this.factionDonateMap = factionDonateMap;
	}
	
	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}

	public boolean isLoginCompleted() {
		return loginCompleted;
	}

	public void setLoginCompleted(boolean loginCompleted) {
		this.loginCompleted = loginCompleted;
	}

	public String getCopyContainerId() {
		return copyContainerId;
	}

	public void setCopyContainerId(String copyContainerId) {
		this.copyContainerId = copyContainerId;
	}

	public long getAngelChestTime() {
		return angelChestTime;
	}

	public void setAngelChestTime(long angelChestTime) {
		this.angelChestTime = angelChestTime;
	}

	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}


	public int getSpeedUpCount() {
		return speedUpCount;
	}

	public void setSpeedUpCount(int speedUpCount) {
		this.speedUpCount = speedUpCount;
	}

	public int getSpeedUpNotCount() {
		return speedUpNotCount;
	}

	public void setSpeedUpNotCount(int speedUpNotCount) {
		this.speedUpNotCount = speedUpNotCount;
	}
	

//	public FactionWarGambleInfo getFactionWarGambleInfo() {
//		return factionWarGambleInfo;
//	}
//
//	public void setFactionWarGambleInfo(FactionWarGambleInfo factionWarGambleInfo) {
//		this.factionWarGambleInfo = factionWarGambleInfo;
//	}

	public boolean isQueryGamble() {
		return queryGamble;
	}

	public void setQueryGamble(boolean queryGamble) {
		this.queryGamble = queryGamble;
	}

	public AtomicInteger getMapChangeSeq() {
		return mapChangeSeq;
	}

	public String getOstype() {
		return ostype;
	}

	public void setOstype(String ostype) {
		this.ostype = ostype;
	}

	public byte getPkStatus() {
		return pkStatus;
	}

	public void setPkStatus(byte pkStatus) {
		this.pkStatus = pkStatus;
	}

	public byte getSex() {
		return sex;
	}

	public void setSex(byte sex) {
		this.sex = sex;
	}

	public byte getCareer() {
		return career;
	}
	
	public void setCareer(byte career) {
		this.career = career;
	}
	
	public int getRoleConsumeGold() {
		return roleConsumeGold;
	}

	public void setRoleConsumeGold(int roleConsumeGold) {
		this.roleConsumeGold = roleConsumeGold;
	}

	public int getRolePayGold() {
		return rolePayGold;
	}

	public void setRolePayGold(int rolePayGold) {
		this.rolePayGold = rolePayGold;
	}


	public int getSilverMoney() {
		return silverMoney;
	}

	public void setSilverMoney(int silverMoney) {
		if(silverMoney>=ParasConstant.SILVER_MONEY){
			this.silverMoney = ParasConstant.SILVER_MONEY;
			try {
				GameContext.getChatApp().sendSysMessage(ChatSysName.System,
						ChannelType.Private,
						AttributeType.gameMoney.getName() + GameContext.getI18n().getText(TextId.REACH_UPEER_LIMIT_TIPS), null,
						this);
			} catch (Exception e) {
				logger.error("",e);
			}
		}else{
			this.silverMoney = silverMoney;
		}
	}

	public Team getTeam() {
		return team;
	}
	
	public PlayerTeam getPlayerTeam() {
		if (null == team) {
			return null;
		}
		return (PlayerTeam) team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
	public boolean hasTeam(){
		return (null != team) ;
	}
	
	public int getMaxIntimateLevel() {
		return maxIntimateLevel;
	}

	public void setMaxIntimateLevel(int maxIntimateLevel) {
		this.maxIntimateLevel = maxIntimateLevel;
	}

	public int getLastFinishQuestId() {
		return lastFinishQuestId;
	}

	public void setLastFinishQuestId(int lastFinishQuestId) {
		this.lastFinishQuestId = lastFinishQuestId;
	}

	@Override
	public byte getAttriGearId() {
		GoodsHero template = this.getOnbattleHeroTemplate();
		if(null == template) {
			return 0;
		}
		return template.getGearId();
	}

	@Override
	public byte getAttriSeriesId() {
		GoodsHero template = this.getOnbattleHeroTemplate();
		if(null == template) {
			return 0;
		}
		return template.getSeriesId();
	}
	
	private GoodsHero getOnbattleHeroTemplate() {
		RoleHero onbattle = GameContext.getUserHeroApp().getOnBattleRoleHero(this.getRoleId());
		if(null == onbattle) {
			return null;
		}
		GoodsHero template = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, onbattle.getHeroId());
		return template;
	}
	
	public AbstractRole getMasterRole() {
		return this;
	}

	public int getHeroCoin() {
		return heroCoin;
	}

	public void setHeroCoin(int heroCoin) {
		this.heroCoin = heroCoin;
	}
	
	public int getCreateServerId() {
		return createServerId;
	}

	public void setCreateServerId(int createServerId) {
		this.createServerId = createServerId;
	}

}