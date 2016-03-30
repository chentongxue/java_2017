package sacred.alliance.magic.app.count.type;


public enum CountType {

	//key、转换类型、是否每天重置、是否特殊处理
	TaobaoTotal(0,new Integer(0),DelType.Day,OperateType.Add,false,"淘宝总次数"),
	TaobaoLand(1,new Integer(0),DelType.Day,OperateType.Add,false,"淘宝地宫"), 
	TaobaoGod(2,new Integer(0),DelType.Day,OperateType.Add,false,"淘宝妖宫"), 
	TaobaoSky(3,new Integer(0),DelType.Day,OperateType.Add,false,"淘宝天宫"), 
	//FreeTransport(4,new Integer(0),DelType.Day,OperateType.Add,false), //当天免费传输数目
	//FreeReborn(5,new Integer(0),DelType.Day,OperateType.Add,false), //当天免费原地复活数目
	//FlowerNum(6,new Integer(0),DelType.No,OperateType.Increment,false), //收到鲜花总数
	//TodayFlowerNum(7,new Integer(0),DelType.Day,OperateType.Increment,false), //今日收到鲜花数量
	PraiseTimes(8,new Integer(0),DelType.No,OperateType.Add,false,"点赞总次数"), 
	TodayPraiseTimes(9,new Integer(0),DelType.Day,OperateType.Add,false,"今天点赞的次数"), 
	ReceivePraiseTimes(10,new Integer(0),DelType.No,OperateType.Add,false,"被赞总次数"), 
	TodayReceivePraiseTimes(11,new Integer(0),DelType.Day,OperateType.Add,false,"今天被赞的次数"), 
	HaveReceivePraiseGift(12,new Byte((byte)0),DelType.Day,OperateType.Modify,false,"是否已经领取被赞奖励"), 
	TransmissionTimes(13,new Integer(0),DelType.Day,OperateType.Add,false,"传功次数"), 
	ReceiveTransmissionTimes(14,new Integer(0),DelType.Day,OperateType.Add,false,"被传功次数"), 
	FriendBatchNum(15,new Integer(0),DelType.Day,OperateType.Add,false,"批量加友次数"), 
	HeroCopyEnter(16,new Integer(0),DelType.Day,OperateType.Add,false,"英雄副本今日进入次数"), 
	HeroCopyBuy(17,new Integer(0),DelType.Day,OperateType.Add,false,"英雄副本今日购买次数"), 
	TeamCopyEnter(18,new Integer(0),DelType.Day,OperateType.Add,false,"组队副本今日进入次数"), 
	TeamCopyBuy(19,new Integer(0),DelType.Day,OperateType.Add,false,"组队副本今日购买次数"), 
	KillCount(20,new Integer(0),DelType.No,OperateType.Modify,false,"杀人数，buff清除时清空"), 
	TodayKillCount(21,new Integer(0),DelType.Day,OperateType.Modify,false,"杀人排行榜计数，每天清空"), 
	LevelGift(22,new String(),DelType.No,OperateType.Modify,false,"领取等级奖励的等级列表"), 
	MonthSign(23,new Integer(0),DelType.Month,OperateType.Modify,false,"当前月签名信息"), 
	CurrSignRecv(24,new Integer(0),DelType.No,OperateType.Modify,false,"当前签名领取奖励信息"), 
	CurrSignTimes(25,new Integer(0),DelType.No,OperateType.Modify,false,"当前签名次数"), 
	RepairSignTimes(26,new Integer(0),DelType.Month,OperateType.Modify,false,"当月已经补签的次数"), 
	//WhichRound(27,new Integer(0),DelType.No,OperateType.Modify,false,"罗盘活动第几轮，用于判断积分奖励是否清零"),
	//CompassRewardPoints(28,new Integer(0),DelType.No,OperateType.Modify,false,"罗盘活动积分奖励"),
	AlchemyNoBreakOutCount(29,new Byte((byte)0),DelType.Day,OperateType.Modify,false,"炼金当日连续几次未出现暴击"), 
	AlchemyCountJsonStr(30,new String(),DelType.Day,OperateType.Modify,false,"炼金次数"), 
	LuckyBoxUsedTimes(31,new Integer(0),DelType.No,OperateType.Modify,false,"幸运转盘使用次数"), 
	LuckyBoxCountJsonStr(32,new String(),DelType.No,OperateType.Modify,false,"幸运转盘位置对应个数"), // 8 个{"2_1":3,"1":0,"4":0,"4":5,"4":5,"4":5,"4":0}
	LuckyBoxPlaceJsonStr(33,new String(),DelType.No,OperateType.Modify,false,"是0则不记 已开幸运宝箱位置"), // 0-8 个{"2_1":3,"4":5,"4":5,"4":5}
	LuckyBoxLastOpenTime(34,new Long(0),DelType.No,OperateType.Modify,false,"幸运转盘最后开启时间"), 
	LuckyBoxRefreshTimes(35,new Integer(0),DelType.No,OperateType.Modify,false,"幸运转盘刷新次数"), 
	AccumulateLoginAwardReceiveDays(36,new Integer(0),DelType.No,OperateType.Modify,false,"连续登录领取奖励"), 
	AccumulateLoginDays(37,new Integer(0),DelType.No,OperateType.Modify,false,"登录天数"), 
	TodayHookExp(38,new Integer(0),DelType.Day,OperateType.Modify,true,"当前挂机获得经验"), 
	TodayHookCleanTimes(39,new Integer(0),DelType.Day,OperateType.Modify,true,"当前天清除挂机疲劳度次数"), 
	ChallengeTimes(40,new Byte((byte)0),DelType.Day,OperateType.Add,false,"当日挑战次数"), //当日挑战次数
	BuyChallengeTimes(41,new Byte((byte)0),DelType.Day,OperateType.Add,false,"当前购买次数"), 
	CodChallengeTimes(42,new Byte((byte)0),DelType.Day,OperateType.Add,false,"立即冷却挑战次数"), 
	ChallengeTime(43,new Long(0),DelType.No,OperateType.Modify,false,"上次挑战时间"), 
	JoinApp(44,new Long(0),DelType.Day,OperateType.Modify,true,"是否参加了某个功能模块"), 
	SurvivalTimes(45,new Integer(0),DelType.Day,OperateType.Add,false,"每日生存战场领奖次数"), 
	UnionMemberDonate(46,new Byte((byte)0),DelType.Day,OperateType.Add,false,"公会捐献"), 
	UnionBuff(47,new Byte((byte)0),DelType.Day,OperateType.Add,false,"公会Buff"), 
	PublishTime(48,new Long(0),DelType.No,OperateType.Modify,false,"上次发布时间"),
	UnionMemberGemDonate(49,new Byte((byte)0),DelType.Day,OperateType.Add,false,"公会钻石捐献"), 
	YesterDayOnlineTimeSeconds(50, new Integer(0), DelType.No, OperateType.Modify, false, "昨天的在线时间（秒）"),
	ToDayOnlineTimeSeconds(51, new Integer(0), DelType.No, OperateType.Modify, false, "今天的在线时间（秒）"),
    ToDayTowerResetNum(52,new Integer(0),DelType.Day,OperateType.Add,false,"今天爬塔重置次数"),
    TowerRaidsLastTime(53,new Long(0),DelType.No, OperateType.Modify, false, "爬塔上次扫荡时间")
	;
	
	private final int type;
	private final Object o;
	private final DelType delType; // -1不处理 0天 1周 2月 3年
	private final OperateType operateType; //  0添加 1删除 2变更 3添加多次
	private final boolean isSpecial; // 是否特殊处理
	private final String desc;//说明
	
	private CountType(int type,Object o, DelType delType,OperateType operateType,boolean isSpecial ,String desc) {
		this.type = type;
		this.o = o;
		this.delType = delType;
		this.operateType = operateType;
		this.isSpecial = isSpecial;
		this.desc = desc;
	}

	public static CountType get(int type){
		for(CountType mt : values()){
			if(mt.getType() == type){
				return mt ;
			}
		}
		return null ;
	}

	public int getType() {
		return type;
	}

	public Object getO() {
		return o;
	}

	public DelType getDelType() {
		return delType;
	}

	public OperateType getOperateType() {
		return operateType;
	}

	public boolean isSpecial() {
		return isSpecial;
	}
	
	public String getDesc(){
		return this.desc;
	}
}
