package sacred.alliance.magic.vo.map;

import java.util.Collection;
import java.util.Date;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.copy.config.CopyMapRoleRule;
import com.game.draco.app.copy.line.config.CopyLineConfig;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.message.push.C0273_CopyLinePassScoreNotifyMessage;

public class MapCopyLineInstance extends MapInstance {
	
	protected final LoopCount mapStateLoop = new LoopCount(1000);//1秒
	private short copyId;//副本ID
	//private byte chapterId = 0;//章节ID
	//private byte copyIndex = 0;//副本序列
	private CopyLineConfig copyLineConfig;
	private MapState mapState = MapState.init;
	private MapSign mapSign = MapSign.init;
	private String ruleId;
	private int ruleMaxSize;//刷怪最大个数
	private int ruleIndex = 0;//已刷怪序列 npcRuleList的索引
	private Date startTime;//开始时间
	protected boolean passed = false;
	private Date timeOverDate;//超时时间
	private final int timeOverKickTime = 60 * 1000;//超时结束后地图可停留时间（毫秒）
	
	public MapCopyLineInstance(sacred.alliance.magic.app.map.Map map) {
		super(map);
		this.copyId = map.getMapConfig().getCopyId();
		this.copyLineConfig = GameContext.getCopyLineApp().getCopyLineConfig(this.copyId);
		/*if(null != this.copyLineConfig){
			this.chapterId = this.copyLineConfig.getChapterId();
			this.copyIndex = this.copyLineConfig.getCopyIndex();
		}*/
	}
	
	enum MapState{
		init,//初始
		refresh,//刷怪
		refresh_end,//刷怪结束
		time_over,//倒计时结束
		;
	}
	
	enum MapSign{
		init,
		pass,//通关标记
		end,
		;
	}
	
	@Override
	protected void updateSub() throws ServiceException {
		super.updateSub();
		//1秒循环一次
		if(this.mapStateLoop.isReachCycle()){
			//判断是否超时，若超时则将地图切换到超时状态
			this.timeOver();
			
			switch(this.mapState){
			case init:
				this.do_mapState_init();
				break;
			case refresh:
				this.do_mapState_refresh();
				break;
			case refresh_end:
				this.do_mapState_refreshEnd();
				break;
			case time_over:
				this.do_mapState_timeOver();
				break;
			}
			
			//根据地图上通关标记，处理通关的逻辑
			switch(this.mapSign){
			case init:
				//如果通关，标记为已通关
				if(this.passed){
					this.mapSign = MapSign.pass;
				}
				break;
			case pass:
				this.do_mapSign_Pass_Logic();
				break;
			case end:
				break;
			}
		}
	}
	
	/**
	 * 初始化状态的逻辑
	 * 找到刷怪规则，切换到刷怪状态
	 */
	private void do_mapState_init(){
		RoleInstance role = this.getFirstRole();
		if(null == role){
			return;
		}
		CopyMapRoleRule rule = GameContext.getCopyLogicApp().getCopyMapRoleRule(role, this.map.getMapId());
		//如果没有找到合适的规则，有可能是地图里种怪了
		if(null != rule){
			this.ruleId = rule.getRuleId();
			this.ruleMaxSize = GameContext.getRefreshRuleApp().getRefreshMax(Integer.parseInt(this.ruleId));
		}
		//必须切换到刷怪状态，里面会发副本倒计时
		this.change_mapState_init_to_refresh();
	}
	
	private RoleInstance getFirstRole(){
		for(RoleInstance role : this.getRoleList()){
			if(null != role){
				return role;
			}
		}
		return null;
	}
	
	/**
	 * 切换到刷怪状态
	 * 开始计时
	 */
	private void change_mapState_init_to_refresh(){
		this.mapState = MapState.refresh;
		//开始计时
		Date now = new Date();
		this.startTime = now;
		//通知客户端当前地图倒计时
		//this.notifyMapRemainTime();
	}
	
	/**
	 * 刷怪状态逻辑
	 * 根据规则刷怪，无怪可刷或已刷完怪切到刷怪结束状态
	 */
	private void do_mapState_refresh(){
		if(this.ruleIndex >= this.ruleMaxSize){
			//如果没有怪可刷，则切换到刷怪结束状态 
			//刷完怪，切换到刷怪结束状态
			this.mapState = MapState.refresh_end;
			return;
		}
		int ruleId = Integer.parseInt(this.ruleId);
		this.ruleIndex = GameContext.getRefreshRuleApp().refresh(ruleId, this.ruleIndex, startTime, this, true);
	}
	
	/**
	 * 刷怪结束状态的逻辑
	 * 判断是否通关
	 */
	private void do_mapState_refreshEnd(){
		//判断是否通关
		this.isPass();
	}
	
	/**
	 * 判断是否通关
	 * 怪死亡的时候，有判断副本是否通关
	 * 因此只需要在刷怪结束后调用此方法
	 */
	private void isPass(){
		try{
			if(this.passed){
				return ;
			}
			//没有通关条件，则不发送通关消息
			if(!this.copyLineConfig.hasPassCondition()){
				this.passed = true;
				return ;
			}
			//没有通关
			if(!this.isCopyPass()){
				return ;
			}
			this.passed = true;
		}catch(Exception e){
			logger.error(this.getClass().getName() + ".isPass error: ", e);
		}
	}
	
	private boolean isCopyPass(){
		if(null == this.copyLineConfig){
			return true;
		}
		//没有通关配置
		if(!this.copyLineConfig.hasPassCondition()){
			return true;
		}
		if(this.copyLineConfig.isNeedKillAll() && this.hasEnemy(this.getNpcList())){
			return false;
		}
		if(this.hasBoss(this.getNpcList(), this.copyLineConfig.getNeedKillNpcId())){
			return false;
		}
		return true;
	}
	
	private boolean hasEnemy(Collection<NpcInstance> npcList){
		if(Util.isEmpty(npcList)){
			return false;
		}
		for(NpcInstance npc : npcList){
			if(npc.getNpc().getNpctype() == NpcType.monster.getType()){
				return true;
			}
		}
		return false;
	}
	
	private boolean hasBoss(Collection<NpcInstance> npcList, String npcId){
		if(Util.isEmpty(npcList)){
			return false;
		}
		for(NpcInstance npc : npcList){
			if(npc.getNpcid().equals(npcId)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断倒计时是否结束逻辑
	 * （如果计时结束，切换到倒计时结束状态）
	 */
	private void timeOver(){
		//如果已经是超时状态，不需要处理
		if(MapState.time_over == this.mapState){
			return;
		}
		//判断是否超时，切换到超时状态
		if(this.isTimeOver()){
			this.mapState = MapState.time_over;
			this.timeOverDate = new Date();
		}
	}
	
	/**
	 * 当前地图倒计时是否结束
	 */
	private boolean isTimeOver(){
		//地图倒计时时间（单位：秒）
		int limitTime = this.copyLineConfig.getTimeLimit();
		//限时时间<=0表示不需要倒计时
		if(limitTime <= 0 || null == this.startTime){
			return false;
		}
		int mapTime = DateUtil.getSecondMargin(this.startTime);
		return mapTime > limitTime;
	}
	
	/**
	 * 倒计时结束逻辑
	 */
	private void do_mapState_timeOver(){
		//已经通关，不需要将角色传出副本
		if(this.passed){
			//容错
			if(null == this.timeOverDate){
				this.timeOverDate = new Date();
			}
			//判断超时结束的时间，超过了可停留时间，将地图内所有人传出
			long time = DateUtil.getMillisecondGap(this.timeOverDate);
			if(time > this.timeOverKickTime){
				this.kickAllRole();
			}
			return;
		}
		this.kickAllRole();
	}

	/**
	 * 将地图内所有玩家传出副本
	 */
	private void kickAllRole(){
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			//满血满蓝
			this.perfectBody(role);
			this.kickRole(role);
		}
	}
	
	/**
	 * 通关状态逻辑
	 * 统计通关时间
	 * 发通关提示信息
	 */
	private void do_mapSign_Pass_Logic(){
		//通关提示信息
		this.notifyPass();
		//标记为结束
		this.mapSign = MapSign.end;
		//每个人的通关逻辑
		this.mapPassForEachRole();
	}
	
	private void notifyPass(){
		String passTips = this.copyLineConfig.getPassTips();
		if(!Util.isEmpty(passTips)){
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, passTips, null, this);
		}
	}
	
	private void mapPassForEachRole(){
		try {
			for(RoleInstance role: this.getRoleList()){
				if(null == role){
					continue;
				}
				this.calculateScore(role);
			}
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".mapPassForEachRole error: ", e);
		}
	}
	
	
	/**
	 * 计算评分
	 * @param role
	 */
	private void calculateScore(RoleInstance role){
		try {
			//TODO:
			byte starScore = (byte) RandomUtil.randomInt(1, 5);
			GameContext.getCopyLineApp().disposeCopyPass(role, this.copyId, starScore);
			//通关评分
			C0273_CopyLinePassScoreNotifyMessage message = new C0273_CopyLinePassScoreNotifyMessage();
			message.setConsumeHP(0);
			message.setHpStar((byte) 3);
			message.setPassStar(starScore);
			message.setTime(50);
			message.setTimeStar((byte) 3);
			role.getBehavior().sendMessage(message);
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".calculateScore error: ", e);
		}
	}
	
	@Override
	public boolean canDestroy() {
		//地图中没有人就可以销毁
		return 0 == this.getRoleCount();
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		if(role instanceof RoleInstance){
			Result result = GameContext.getCopyLineApp().canEnter((RoleInstance) role, this.copyId);
			return result.isSuccess();
		}
		return false;
	}

	@Override
	protected String createInstanceId() {
		instanceId = "copyline_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}

	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		
	}

	@Override
	protected void deathLog(AbstractRole victim) {
		
	}

	@Override
	public void useGoods(int goodsId) {
		
	}
	
	@Override
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void enter(AbstractRole role) {
		super.enter(role);
	}

}
