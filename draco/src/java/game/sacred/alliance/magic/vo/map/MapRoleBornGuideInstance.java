package sacred.alliance.magic.vo.map;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.MapNpcBornData;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleBorn;
import sacred.alliance.magic.vo.RoleBornGuide;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.DeathNotifySelfItem;

public class MapRoleBornGuideInstance extends MapInstance{
	private LoopCount timeOutLoop  = new LoopCount(2000); //2s
	protected final LoopCount mapStateLoop = new LoopCount(1000);//1秒
	private long timeout = 1*60*1000 ; //1min
	
	private Date startTime = new Date() ;
	private final int mapStateRefreshMaxIndex = 2 ;
	private int mapStateRefreshIndex = 0 ;
	private String npcRuleId = "";
	private int ruleMaxSize;//刷怪最大个数
	protected MapState mapState = MapState.init;
	private int ruleIndex = 0;//已刷怪序列 npcRuleList的索引
	private List<NpcBorn> npcBornList;
	private RoleInstance role = null ;
	
	enum MapState{
		init,//初始
		refresh,//刷怪
		refresh_end,//刷怪结束
		time_over,//倒计时结束
		;
	}
	
	public MapRoleBornGuideInstance(Map map) {
		super(map);
	}
	
	public void exitMap(AbstractRole role) {
		try{
			super.exitMap(role);
		}catch(Exception ex){
		}
		try {
			this.exit(role);
		} finally {
			this.destroy();
		}
	}
	
	@Override
	public void destroy(){
		this.role = null ;
		super.destroy();
	}
	
	@Override
	public boolean npcBorn(int bornIndex, NpcBorn npcBorn,boolean whenCreateMap){
		boolean ret = super.npcBorn(bornIndex, npcBorn, whenCreateMap);
		RoleBornGuide guide = GameContext.getRoleBornApp()
				.getRoleBornGuide();
		if(null == guide || Util.isEmpty(guide.getGiveHeroNpcId())){
			return ret ;
		}
		if(guide.getGiveHeroId() >0 && 
				npcBorn.getBornnpcid().equals(guide.getGiveHeroNpcId())){
			//添加给予的英雄ID
			GameContext.getHeroApp().useHeroBySystem(this.role, guide.getGiveHeroId());
		}
		return ret ;
	}
	
	
	@Override
	public Point getRebornPoint(RoleInstance role,RebornType type){
		return this.getBornPoint() ;
	}
	
	private Point getBornPoint(){
		RoleBorn roleBorn = GameContext.getRoleBornApp().getRoleBorn();
		return roleBorn.getBornPoint();
	}
	
	private void exit(AbstractRole role){
		// 重新设置用户坐标
		Point p = this.getBornPoint();
		role.setMapId(p.getMapid());
		role.setMapX(p.getX());
		role.setMapY(p.getY());
		
		//!!!! 必须先完美复活，因为删除英雄的时候会自己切换到其他英雄，导致判断失败
		this.perfectBody(role);
				
		//删除buff
		RoleBornGuide guide = GameContext.getRoleBornApp()
				.getRoleBornGuide();
		if (guide.getBuffId() > 0) {
			GameContext.getUserBuffApp().delBuffStat(role, guide.getBuffId(), false);
		}
		
		if(guide.getGiveHeroId() >0){
			GameContext.getHeroApp().deleteHeroBySystem((RoleInstance)role, guide.getGiveHeroId());
		}
	}
	
	/**
	 * 将所有玩家传出当前地图
	 */
	private void kickAllRole(){
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			this.kickRole(role);
		}
	}
	
	private boolean isTimeout(){
		return (System.currentTimeMillis() - this.startTime.getTime()) >= this.timeout ;
	}
	
	private boolean isMustDestroy(){
		return (System.currentTimeMillis() - this.startTime.getTime()) >= this.timeout*1.5 ;
	}
	
	@Override
	protected void enter(AbstractRole role) {
		super.enter(role);
		try {
			if(RoleType.PLAYER == role.getRoleType()){
				// 添加buff
				RoleBornGuide guide = GameContext.getRoleBornApp()
						.getRoleBornGuide();
				if(guide.getTimeout() > 0){
					this.timeout = guide.getTimeout()*1000 ;
				}
				if (guide.getBuffId() > 0) {
					GameContext.getUserBuffApp().addBuffStat(role, role,
							guide.getBuffId(), 1);
				}
				this.role = (RoleInstance)role ;
			}
		} catch (Exception ex) {
			logger.error("",ex);
		}
	}
	
	/**
	 * 切换到刷怪状态
	 * 开始计时
	 */
	private void change_mapState_init_to_refresh(){
		this.mapState = MapState.refresh;
		this.startTime = new Date() ;
	}
	
	/**
	 * 初始化状态的逻辑
	 * 找到刷怪规则，切换到刷怪状态
	 */
	private void do_mapState_init(){
		// 添加buff
		RoleBornGuide guide = GameContext.getRoleBornApp().getRoleBornGuide();
		this.npcRuleId = guide.getNpcRuleId() ;
		this.ruleMaxSize = GameContext.getRefreshRuleApp().getRefreshMax(Integer.parseInt(this.npcRuleId));
		//必须切换到刷怪状态，里面会发副本倒计时
		this.change_mapState_init_to_refresh();
	}
	
	@Override
	public void updateSub()  throws ServiceException{
		super.updateSub();
		if(timeOutLoop.isReachCycle() && this.isTimeout()){
			this.kickAllRole();
		}
		//根据不同状态做相应的处理
		if(mapStateLoop.isReachCycle()){
			switch(this.mapState){
			case init:
				this.do_mapState_init();
				break;
			case refresh:
				this.mapStateRefreshIndex ++ ;
				if(this.mapStateRefreshIndex >= this.mapStateRefreshMaxIndex){
					this.mapStateRefreshIndex = 0 ;
					this.do_mapState_refresh();
				}
				break;
			}
		}
	}
	
	/**
	 * 刷怪状态逻辑
	 * 根据规则刷怪，无怪可刷或已刷完怪切到刷怪结束状态
	 */
	private void do_mapState_refresh(){
		if(this.ruleIndex >= this.ruleMaxSize){
			//如果没有怪可刷，则切换到刷怪结束状态 
			//刷完怪，切换到刷怪结束状态
			this.change_mapState_to_refresh_end();
			return;
		}
		int ruleId = Integer.parseInt(this.npcRuleId);
		this.ruleIndex = GameContext.getRefreshRuleApp().refresh(ruleId, this.ruleIndex, startTime, this, true);
	}
	
	/**
	 * 切换到刷怪结束状态
	 */
	private void change_mapState_to_refresh_end(){
		this.mapState = MapState.refresh_end;
	}
	
	@Override
	public List<NpcBorn> getNpcBornList(){
		if(null == this.npcBornList){
			this.npcBornList = new ArrayList<NpcBorn>();
			MapNpcBornData bornData = this.map.getNpcBornData();
			if(null != bornData){
				this.npcBornList.addAll(bornData.getNpcborn());
			}
		}
		return this.npcBornList;
	}
	
	protected List<DeathNotifySelfItem> rebornOptionFilter(RoleInstance role){
		return null ;
	}
	
	protected void kickRole(RoleInstance role){
		try{
			GameContext.getUserMapApp().changeMap(role,this.getBornPoint());
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	@Override
	protected String createInstanceId() {
		instanceId = "guide_" + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}

	@Override
	public boolean canDestroy() {
		return (this.getRoleCount() == 0) 
				|| this.isMustDestroy();
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
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

}
