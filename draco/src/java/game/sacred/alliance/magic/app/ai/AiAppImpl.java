package sacred.alliance.magic.app.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.ai.support.NpcAiScriptSupport;
import sacred.alliance.magic.app.ai.config.AsyncPvpAiConfig;
import sacred.alliance.magic.app.ai.config.AutoMaxHp;
import sacred.alliance.magic.app.ai.config.NormalAiConfig;
import sacred.alliance.magic.app.config.PathConfig;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.app.summon.SummonGroup;
import sacred.alliance.magic.app.summon.SummonRule;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.script.ScriptSupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.npc.type.NpcType;
import com.google.common.collect.Maps;


public class AiAppImpl extends AiApp {
	private final static String ROLE_COPY_AI_ID = "rolecopyai";
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private PathConfig pathConfig;
	private ScriptSupport scriptSupport;
	private NpcAiScriptSupport npcAiScriptSupport;
	private Map<String, NormalAiConfig> normalAiConifgMap;
	private Map<Integer, List<NpcBorn>> refreshRuleMap = new HashMap<Integer,List<NpcBorn>>();
	private Map<Integer, SummonGroup> refreshGroupMap = new HashMap<Integer,SummonGroup>();
	private Map<Integer, AiBossRefreshRule> bossRefreshRuleMap = new HashMap<Integer,AiBossRefreshRule>();
	private Map<String,Map<Integer,Float>> npcAutoMaxHpMap = Maps.newHashMap();
	
	
	@Override
	public boolean isAutoMapHpNpc(String npcId) {
		return  null != this.getAutoMaxHpConfig(npcId);
	}
	
	@Override
	public Map<Integer,Float> getAutoMaxHpConfig(String npcId){
		return npcAutoMaxHpMap.get(npcId);
	}
	
	public Ai getAi(AbstractRole role) {
		RoleType roleType = role.getRoleType();
		if(RoleType.NPC != roleType 
				&& RoleType.COPY != roleType){
			return new DefaultAi("defaultai") ;
		}
		
		NpcInstance self = (NpcInstance)role ;
		Ai ai = null;
		if(RoleType.NPC == roleType) {
			ai = aiMap.get(self.getNpc().getNpcid());
		}
		else {
			ai = aiMap.get(ROLE_COPY_AI_ID);
		}
		Ai selfAi = null ;
		if(null == ai){
			//或者给默认AI
			selfAi = new DefaultAi(self.getNpcid()); 
		}else{
			selfAi = (Ai)ai.clone();
		}
		
		//设置Ai的角色
		selfAi.setRole(role);
		//通过状态机工厂获得状态机实例
		StateMachine sm = new StateMachine(role) ;
		//设置Ai的状态机
		selfAi.setStateMachine(sm);
		role.setAi(selfAi);
		//init方法里面进行一系列初始化,eg: 给npc添加上相关技能,
		selfAi.init();
		return selfAi ;
	}
	
	public Ai getAsyncPvpAi(AbstractRole role, AsyncPvpRoleAttr npc) {
		if(RoleType.NPC == role.getRoleType()){
			AsyncPvpAi ai = new AsyncPvpAi();
			ai.setRole(role);
			StateMachine sm = new StateMachine(role) ;
			//设置Ai的状态机
			ai.setStateMachine(sm);
            role.setAi(ai);
            
            AsyncPvpAiConfig aiConfig = new AsyncPvpAiConfig();
            aiConfig.setAsyncPvpRoleAttr(npc);
            aiConfig.init();
            
            ai.setAiConfig(aiConfig);
            //init方法里面进行一系列初始化,eg: 给npc添加上相关技能,
            ai.init();
            
			return ai ;
		}
		return new DefaultAi("defaultai") ;
	}
	
	public void setPathConfig(PathConfig pathConfig) {
		this.pathConfig = pathConfig;
	}

	public void setScriptSupport(ScriptSupport scriptSupport) {
		this.scriptSupport = scriptSupport;
	}

	@Override
	public void start() {
		try {
			//加载NPC 动态调整maxHp
			this.loadAutoMaxHp();
			
			Map<String,List<BossAction>> linkMap = new HashMap<String,List<BossAction>>();
			//加载bossAction
			for(BossAction ba : this.loadBossAction()){
				int intId = ba.getLinkId();
				if(intId <=0){
					continue ;
				}
				//初始化
				ba.init();
				String linkId = String.valueOf(intId);
				List<BossAction> currentList = linkMap.get(linkId);
				if(null == currentList){
					currentList = new ArrayList<BossAction>();
					currentList.add(ba);
					linkMap.put(linkId, currentList);
				}else{
					currentList.add(ba);
				}
			}
			
			//加载AI刷怪规则
			this.initRefreshRule();
			//加载AI刷怪权重组
			this.initRefreshGroup();
			//加载BOSS刷怪规则
			this.initBossRefreshRule();
			
			normalAiConifgMap = buildNpcAiData();
			//优先加载脚本AI
			//加载java脚本AI
			this.npcAiScriptSupport.loadScript();
			//加载python脚本AI
			this.scriptSupport.loadScript(this.pathConfig.getAiPath());
			
			//注册普通NPC AI
			for(Iterator<NormalAiConfig> it = normalAiConifgMap.values().iterator();it.hasNext();){
			     NormalAiConfig aiCfg = it.next();
			     if(aiMap.containsKey(aiCfg.getId())){
			         //脚本已经存在,优先考虑脚本AI
			         continue ;
			     }
			     //初始化bossaction链
			     String links = aiCfg.getActionLinks();
			     String[] linkArr = StringUtil.splitString(links,",");
			     if(null != linkArr && linkArr.length > 0){
			    	 BossAction[][] bossActions = new BossAction[linkArr.length][];
			    	 for(int i=0;i<linkArr.length;i++){
			    		 String linkId = linkArr[i].trim();
			    		 List<BossAction> linkActions = linkMap.get(linkId);
			    		 if(null == linkActions){
			    			Log4jManager.checkFail();
			    			Log4jManager.CHECK.error("AiConfig bossAction Link not exist,linkId=" + linkId + " npcId=" + aiCfg.getId());
			    			return;
			    		 }
			    		 bossActions[i] = linkActions.toArray(new BossAction[linkActions.size()]);
			    	 }
			    	 aiCfg.setBossActions(bossActions);
			     }
			     
			     
			     aiCfg.init();
			     String aiId = aiCfg.getId();
			     ConfigurableAi ai = null;
			     if(aiId.equals(ROLE_COPY_AI_ID)) {
			    	 ai = new RoleCopyAi();
			     }
			     else {
			    	 ai = new ConfigurableAi();
			     }
			     ai.setAiId(aiCfg.getId());
			     AiApp.registerAi(ai);
			}
		} catch (Exception e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("AiApplication.start error:", e);
			logger.error("AiApplication.start error:", e);
		}
	}

	@Override
	public void stop() {
		
	}
	
	private List<BossAction> loadBossAction(){
		String fileName =  XlsSheetNameType.boss_action.getXlsName();
		String sheetName = XlsSheetNameType.boss_action.getSheetName();
		String path = pathConfig.getXlsPath();
		String sourceFile = path + fileName;
		return XlsPojoUtil.sheetToList(sourceFile, sheetName, BossAction.class);
	}
	
	
	private Map<String, NormalAiConfig> buildNpcAiData() {
		String fileName =  XlsSheetNameType.npc_ai.getXlsName();
		String sheetName = XlsSheetNameType.npc_ai.getSheetName();
		String path = pathConfig.getXlsPath();
		String sourceFile = path + fileName;
		Map<String, NormalAiConfig> npcAiMap = XlsPojoUtil.sheetToMap(sourceFile, sheetName, NormalAiConfig.class);
		if(null == npcAiMap || 0 >= npcAiMap.size()) {
			return new HashMap<String, NormalAiConfig>();
		}
		return npcAiMap;
	}

    public State<NpcInstance> getNpcDefaultState(NpcInstance role){
         //设置默认状态
        State defaultState = StateFactory.getState(StateType.State_Idle, role, null);
        if(role.getNpcActionType() == NpcActionType.ANIMAL){
            //随机走
            defaultState = StateFactory.getState(StateType.State_Random_Move, role, null);
        }else if(role.getNpcActionType() == NpcActionType.GUARD){
            //不动，但有固定点
            //默认不动
        }else if(role.getNpcActionType() == NpcActionType.PATROLMAN && null != role.getWalkPath()){
            //固定线路行走
            //判断是否有路线,如果没有也静止
            defaultState = StateFactory.getState(StateType.State_WayPoint_Move, role, role.getWalkPath());
        }
        return defaultState ;
    }
    
	public Map<String, NormalAiConfig> getNormalAiConfigMap() {
		return normalAiConifgMap;
	}

	@Override
	public void setArgs(Object args) {
		
	}
	
	public void setNpcAiScriptSupport(NpcAiScriptSupport npcAiScriptSupport) {
		this.npcAiScriptSupport = npcAiScriptSupport;
	}
	
	private void loadAutoMaxHp(){
		String fileName =  XlsSheetNameType.npc_ai_auto_maxhp.getXlsName();
		String sheetName = XlsSheetNameType.npc_ai_auto_maxhp.getSheetName();
		String path = pathConfig.getXlsPath();
		String sourceFile = path + fileName;
		List<AutoMaxHp> list =  XlsPojoUtil.sheetToList(sourceFile, sheetName, AutoMaxHp.class);
		if(Util.isEmpty(list)){
			this.npcAutoMaxHpMap.clear();
			return ;
		}
		Map<String,Map<Integer,Float>> allMap = Maps.newHashMap();
		for(AutoMaxHp auto : list){
			if(null == auto){
				continue ;
			}
			auto.init();
			String npcId = auto.getNpcId() ;
			Map<Integer,Float> map = allMap.get(npcId);
			if(null == map){
				map = Maps.newHashMap();
				allMap.put(npcId, map);
			}
			for(int i=auto.getMinAttackerNum();i<=auto.getMaxAttackerNum();i++){
				map.put(i, auto.getMaxHpRate());
			}
		}
		for(String npcId : allMap.keySet()){
			//判断key=0是否存在
			//判断key是否连续
			//判断最大key是否合法
			Map<Integer,Float> value = allMap.get(npcId);
			if(null == value.get(AutoMaxHp.MIN_NUM)){
				this.checkFail("npc auto maxhp config error,npcId=" + npcId + " not config minAttackerNum=0");
			}
			int maxNum = value.size()-1 ;
			if(maxNum > AutoMaxHp.MAX_NUM){
				this.checkFail("npc auto maxhp config error,npcId=" + npcId + " maxAttackerNum must <= " + AutoMaxHp.MAX_NUM);
			}
			if(null == value.get(maxNum)){
				this.checkFail("npc auto maxhp config error,npcId=" + npcId );
			}
		}
		this.npcAutoMaxHpMap = allMap ;
	}
	
	private void checkFail(String str) {
		Log4jManager.CHECK.error(str);
		Log4jManager.checkFail();
	}


	private List<SummonRule> loadRefreshRule(){
		String fileName =  XlsSheetNameType.npc_death_refresh_rule.getXlsName();
		String sheetName = XlsSheetNameType.npc_death_refresh_rule.getSheetName();
		String path = pathConfig.getXlsPath();
		String sourceFile = path + fileName;
		return XlsPojoUtil.sheetToList(sourceFile, sheetName, SummonRule.class);
	}
	
	private void initRefreshRule(){
		//加载AI刷怪规则
		for(SummonRule npcRefreshRule : this.loadRefreshRule()) {
			int ruleId = npcRefreshRule.getRuleId();
			List<NpcBorn> refreshList = refreshRuleMap.get(ruleId);
			if(null == refreshList){
				refreshList = new ArrayList<NpcBorn>();
				refreshList.add(npcRefreshRule.getNpcBorn());
				refreshRuleMap.put(ruleId, refreshList);
			}else{
				refreshList.add(npcRefreshRule.getNpcBorn());
			}
		}
	}
	
	private void initRefreshGroup(){
		String fileName =  XlsSheetNameType.npc_death_refresh_group.getXlsName();
		String sheetName = XlsSheetNameType.npc_death_refresh_group.getSheetName();
		try{
			String path = pathConfig.getXlsPath();
			String sourceFile = path + fileName;
		
			refreshGroupMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, SummonGroup.class);
			
			for(SummonGroup aiNpcRefreshGroup : refreshGroupMap.values()) {
				aiNpcRefreshGroup.init();
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}


	@Override
	public void deathRefresh(int groupId, AbstractRole role) {
		try{
			if(groupId == 0) {
				return;
			}
			SummonGroup aiNpcRefreshGroup = refreshGroupMap.get(groupId);
			if(null == aiNpcRefreshGroup) {
				return;
			}
			Integer id = aiNpcRefreshGroup.getWeightRuleId();
			if(null == id) {
				return;
			}
			int ruleId = id.intValue();
			if(ruleId == 0) {
				return;
			}
			List<NpcBorn> refreshRuleList = refreshRuleMap.get(ruleId);
			if(null == refreshRuleList || refreshRuleList.size() == 0) {
				return;
			}
			for(NpcBorn npcBorn : refreshRuleList) {
				role.getMapInstance().npcBorn(-1, npcBorn, false);
			}
		}catch(Exception e){
			logger.error("AiApplication.deathRefresh error:" + e);
		}
	}
	
	private void initBossRefreshRule(){
		String fileName =  XlsSheetNameType.boss_refresh_group.getXlsName();
		String sheetName = XlsSheetNameType.boss_refresh_group.getSheetName();
		try{
			String path = pathConfig.getXlsPath();
			String sourceFile = path + fileName;
			bossRefreshRuleMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AiBossRefreshRule.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
	}
	
	@Override
	public void bossRefresh(int ruleId, AbstractRole role) {
		try{
			AiBossRefreshRule rule = bossRefreshRuleMap.get(ruleId);
			if(null == rule) {
				return;
			}
			int x = role.getMapX();
			int y = role.getMapY();
			String npcId1 = rule.getNpcId1();
			int npcCount1 = rule.getNpcCount1();
			if(!Util.isEmpty(npcId1) && npcCount1 > 0) {
				role.getMapInstance().summonCreateNpcByNum(npcId1, x, y, rule.getCircle(), npcCount1);
			}
			
			String npcId2 = rule.getNpcId2();
			int npcCount2 = rule.getNpcCount2();
			if(!Util.isEmpty(npcId2) && npcCount2 > 0) {
				role.getMapInstance().summonCreateNpcByNum(npcId2, x, y, rule.getCircle(), npcCount2);
			}
			
		}catch(Exception e){
			logger.error("AiApplication.bossRefresh error:" + e);
		}
	}
	
	@Override
	public void bossSpilt(AbstractRole sprite,BossAction action) {
		try{
			String spriteId = action.getExecData2();
			int count = Integer.parseInt(action.getExecData1());
			boolean flag = Integer.parseInt(action.getExecData3()) == 1 ? true : false;
			int x = sprite.getMapX();
			int y = sprite.getMapY();
			sprite.getMapInstance().summonCreateNpcByNum(spriteId , x, y, 50, count);
			if(flag){
				NpcInstance npc = (NpcInstance)sprite;
				sprite.getMapInstance().removeAbstractRole(npc);
				sprite.getMapInstance().notifyNpcDeath(npc);
			}
			
		}catch(Exception e){
			logger.error("AiApplication.bossRefresh error:" + e);
		}
	}

	@Override
	public void bossSpiltDefinition(AbstractRole sprite,BossAction action) {
		try{
			String [] data2 = action.getExecData2().split(",");
			String spriteId = data2[0];
			int creX = Integer.parseInt(data2[1]);
			int creY = Integer.parseInt(data2[2]);
			int circle = Integer.parseInt(data2[3]);
			
			String [] data3 = action.getExecData3().split(",");
			String delNpcId = data3[0];
			Point point = new Point();
			point.setX(Integer.parseInt(data3[1]));
			point.setY(Integer.parseInt(data3[2]));
			
			int count = Integer.parseInt(action.getExecData1());
			
			NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(delNpcId);
			if (null == npcTemplate) {
				return;
			}
			
			Collection<NpcInstance> npcList = sprite.getMapInstance().getNpcList();
			for(NpcInstance n : npcList){
				if(n.isDeath()){
					continue;
				}
				if(n.getNpcid().equals(delNpcId) 
						&& n.getMapX() == point.getX() 
						&& n.getMapY() == point.getY()){
					sprite.getMapInstance().npcDeath(n);
				}
			}
			sprite.getMapInstance().summonCreateNpcByNum(spriteId , creX, creY, circle, count);
			
		}catch(Exception e){
			logger.error("AiApplication.bossRefresh error:" + e);
		}
		
	}
	
	@Override
	public void bossSpiltBlock(AbstractRole sprite,BossAction action) {
		try{
			String [] data2 = action.getExecData2().split(",");
			String spriteId = data2[0];
			int creX = Integer.parseInt(data2[1]);
			int creY = Integer.parseInt(data2[2]);
			int circle = Integer.parseInt(data2[3]);
			
			String [] data3 = action.getExecData3().split(",");
			String delNpcId = data3[0];
			Point point = new Point();
			point.setX(Integer.parseInt(data3[1]));
			point.setY(Integer.parseInt(data3[2]));
			
			int count = Integer.parseInt(action.getExecData1());
			
			NpcTemplate npcTemplate = GameContext.getNpcApp().getNpcTemplate(delNpcId);
			if (null == npcTemplate) {
				return;
			}
			
			boolean isMapBaffle = (npcTemplate.getNpctype() == NpcType.baffle.getType());
			
			sprite.getMapInstance().mapBaffleDeath(delNpcId, point, isMapBaffle);
			
			sprite.getMapInstance().summonCreateNpcByNum(spriteId , creX, creY, circle, count);
			
		}catch(Exception e){
			logger.error("AiApplication.bossRefresh error:" + e);
		}
		
	}
}
