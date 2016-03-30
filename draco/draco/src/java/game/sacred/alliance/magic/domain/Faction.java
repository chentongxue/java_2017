//package sacred.alliance.magic.domain;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import com.game.draco.GameContext;
//
//import lombok.Data;
//import sacred.alliance.magic.app.faction.godbeast.FactionSoulRecord;
//import sacred.alliance.magic.base.FactionPositionType;
//import sacred.alliance.magic.base.FactionPowerType;
//import sacred.alliance.magic.base.FactionTechnologyType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.base.SaveDbStateType;
//import sacred.alliance.magic.constant.Cat;
//import sacred.alliance.magic.constant.FormatConstant;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.util.DateUtil;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public @Data class Faction implements Cloneable, java.io.Serializable{
//	
//	private String factionId;//门派ID
//	private String factionName;//门派名称
//	private byte factionLevel;//门派等级
//	private byte status;//门派状态
//	private int leaderId;//会长角色ID
//	private String leaderName;//会长名称
//	private Date createDate;//创建时间
//	private String factionDesc = "千秋万载，一统江湖!";//门派公告
//	private int memberNum;//门派当前人数
//	private int contribution;//门派贡献
//	private int integral;//门派积分
//	private int maxIntegral;//门派积分上限
//	private int resource;//门派资源
//	private int maxMemberNum;//门派人数上限
//	
//	private Object factionRoleLock = new byte[0];//门派成员锁
//	private Object integralLock = new byte[0];//积分变化锁
//	private Map<Integer,FactionBuild> buildingMap = new HashMap<Integer,FactionBuild>();//门派已有的建筑（库中查询）
//	
//	private Map<Short, String> copyProgMap = new HashMap<Short, String>();//副本进度(副本ID,副本容器ID)
//	private Map<Integer,FactionRole> positionRoleMap = new HashMap<Integer,FactionRole>();
//	private Object synchContributeLock = new byte[0];//同步总贡献变化锁
//	private SaveDbStateType saveDbStateType = SaveDbStateType.Initialize;//持久化状态（决定是否更新库）
//	private Object impeachLock = new byte[0];//弹劾锁
//	
//	private int factionMoney;//捐献得到的钱
//	private Object factionMoneyLock = new byte[0];//公会金钱锁
//	private Map<Integer,Integer> factionSkillMap = new LinkedHashMap<Integer,Integer>();//门派已有的技能
//	
//	private Object factionSoulFeedLock = new byte[0];//公会神兽成长锁
//	private FactionSoulRecord factionSoulRecord;
//	private Object factionSoulFlyLock = new byte[0];//公会神兽飞升锁
//	
//	private byte factionCamp;//门派阵营
//	
//	private int maxDonateCount;//最大捐献次数
//	private int donateCount;//捐献次数
//	private Date donateTime;//捐献时间
//	private Map<Integer, SummonDbInfo> summonDbInfo = new HashMap<Integer, SummonDbInfo>();// 召唤记录
//	
//	public void addCopyProgress(short copyId, String progress){
//		this.copyProgMap.put(copyId, progress);
//	}
//	
//	public void removeCopyProgress(short copyId){
//		this.copyProgMap.remove(copyId);
//	}
//	
//	public String getCopyProgress(short copyId){
//		return this.copyProgMap.get(copyId);
//	}
//	
//	public Map<Short, String> getCopyProgMap(){
//		return this.copyProgMap;
//	}
//	
//	public void offlineLog(){
//		StringBuffer sb = new StringBuffer();
//		sb.append(factionId);
//		sb.append(Cat.pound);
//		sb.append(factionName);
//		sb.append(Cat.pound);
//		sb.append(factionLevel);
//		sb.append(Cat.pound);
//		sb.append(status);
//		sb.append(Cat.pound);
//		sb.append(leaderId);
//		sb.append(Cat.pound);
//		sb.append(leaderName);
//		sb.append(Cat.pound);
//		sb.append(DateUtil.getTimeByDate(createDate));
//		sb.append(Cat.pound);
//		sb.append(factionDesc);
//		sb.append(Cat.pound);
//		sb.append(memberNum);
//		sb.append(Cat.pound);
//		sb.append(contribution);
//		sb.append(Cat.pound);
//		sb.append(integral);
//		sb.append(Cat.pound);
//		sb.append(resource);
//		sb.append(Cat.pound);
//		sb.append(factionMoney);
//		sb.append(Cat.pound);
//		Log4jManager.FACTION_LOG.info(sb.toString());
//	}
//	
//	public String getCreateTime(){
//		return DateUtil.date2FormatDate(this.createDate, FormatConstant.FactionCreateFormat);
//	}
//	
//	public boolean isFull(){
//		return this.memberNum >= this.maxMemberNum;
//	}
//	
//	/**
//	 * 根据职位获得职位的昵称
//	 * @param type
//	 * @param nickName
//	 */
//	public String getPositionNick(byte type){
//		return FactionPositionType.getPosition(type).getName();
//	}
//	
//	/**
//	 * 能否被弹劾
//	 * @return
//	 */
//	public Result canImpeach(RoleInstance role){
//		Result result = new Result();
//		if(!GameContext.getFactionApp().getPowerTypeSet(role).contains(FactionPowerType.Impeach)){
//			return result.setInfo(Status.Faction_Impeach_No_Position.getTips());
//		}
//		Map<Integer,FactionRole> frMap = GameContext.getFactionApp().getFactionRoleMap(factionId);
//		if(null == frMap) {
//			return result.setInfo(Status.Faction_FAILURE.getTips());
//		}
//		FactionRole fr = frMap.get(leaderId);
//		if(null == fr){
//			return result.setInfo(Status.Faction_Impeach_Err.getTips());
//		}
//		Date now = new Date();
//		int diffDay = DateUtil.dateDiffDay(now, fr.getOfflineTime());
//		if(diffDay < GameContext.getFactionApp().getImpeachDay()){
//			return result.setInfo(Status.Faction_Impeach_Err.getTips());
//		}
//		return result.success();
//	}
//	
//	/** ========================= */
//	/**
//	 * 获取门派科技的信息
//	 * @return
//	 */
//	public FactionTechnology getTechnologyByType(FactionTechnologyType type){
//		if(type == null){
//			return null;
//		}
//		FactionTechnology te = new FactionTechnology();
//		te.setTechnologyType(type.getType());
//		te.setTechnologyName(type.getName());
//		return te;
//	}
//	
//	public Faction clone() throws CloneNotSupportedException{
//		return (Faction)super.clone();
//	}
//	
//	public boolean canDonate(){
//		Date now = new Date();
//		if(!DateUtil.sameDay(new Date(), getDonateTime())){
//			setDonateCount(0);
//			setDonateTime(now);;
//		}
//		return donateCount < maxDonateCount ? true : false;
//	}
//}
