package sacred.alliance.magic.app.arena;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.message.item.Arena3V3LevelDescItem;
import com.game.draco.message.response.C3866_Arena3V3LevelDescRespMessage;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.arena.config.ArenaBuffConfig;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.app.arena.config.ArenaMapConfig;
import sacred.alliance.magic.app.arena.config.ArenaMapRule;
import sacred.alliance.magic.app.arena.config.Reward3V3Config;
import sacred.alliance.magic.app.arena.config.ScoreResult;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.team.Team;
import com.game.draco.message.response.C3863_Arena3V3DetailRespMessage;

public class Arena3V3AppImpl implements Arena3V3App,Service{

	private static final Logger logger = LoggerFactory.getLogger(Arena3V3AppImpl.class);
	
	private Map<Integer,ArenaMapConfig> mapConfigMap = new HashMap<Integer,ArenaMapConfig>();
	private Map<Integer,ArenaMapRule> mapRuleMap = new HashMap<Integer,ArenaMapRule>();
	private List<Reward3V3Config> reward3V3List = new ArrayList<Reward3V3Config>();
	private Map<Integer, List<ArenaBuffConfig>> buffConfigMap = new HashMap<Integer, List<ArenaBuffConfig>>();
	private boolean isOpenDarkDoor = false;
	
	
	@Override
	public ArenaMapRule getArenaMapRule(int arenaType){
		try{
			ArenaMapConfig config = this.mapConfigMap.get(arenaType);
			if(null == config){
				return null;
			}
			return this.mapRuleMap.get(config.getWeightRuleId());
		}catch(Exception e){
			logger.error("getArenaMapRule error",e);
		}
		return null;
	}
	
	@Override
	public ScoreResult getScoreResult(RoleInstance role, BattleResult result, ArenaMatch match, int killNum){
		ScoreResult scoreResult = new ScoreResult();
		try{
			float maxArenaLevel = -1;
			
			int battleLevelDiff = 0;
			int team1Score = match.getTeam1().getScore();
			int team2Score = match.getTeam2().getScore();
			if(match.isTeam1(role.getRoleId())){
				battleLevelDiff = team1Score - team2Score;
				maxArenaLevel = team1Score;
			}else{
				battleLevelDiff = team2Score - team1Score;
				maxArenaLevel = team2Score;
			}
			
			Reward3V3Config teamReward = this.getReward3V3Config(maxArenaLevel);
			if(null == teamReward){
				return null;
			}
			
			Reward3V3Config roleReward = this.getReward3V3Config(role.getRoleArena().getLastArenaLevel3v3());
			if(null == roleReward){
				return null;
			}
			//预期胜率=1/(1+10^(-等级差的绝对值/400))
			float expected = (float)(1/(1+Math.pow(10,-((double)battleLevelDiff/400))));
			float resultScore = 0;
			if(result == BattleResult.win){
				resultScore = 1;
			}else if(result == BattleResult.draw){
				resultScore = 0.5f;
			}
			//比赛前等级+系数*（结果分-预期胜率）
			float curArenaLevel = teamReward.getModulus() * (resultScore + (killNum * teamReward.getKillModulus()) - expected);
			scoreResult.setArenaLevel(curArenaLevel);
			scoreResult.setScore(result == BattleResult.win ? roleReward.getWinScore() : roleReward.getLoseScore());
			scoreResult.setMaxCycleScore(roleReward.getMaxCycleScore());
			scoreResult.setMaxRoleScore(roleReward.getMaxRoleScore());
		}catch(Exception e){
			logger.error("ArenaApp.getScoreResult error",e);
		}
		return scoreResult;
	}
	
	private Reward3V3Config getReward3V3Config(float battleLevel){
		for(Reward3V3Config config : this.reward3V3List){
			if(null == config){
				continue;
			}
			if(config.isSuitLevel(battleLevel)){
				return config;
			}
		}
		return null;
	}

    private Reward3V3Config getNextReward3V3Config(float battleLevel){
        for(Reward3V3Config config : this.reward3V3List){
            if(null == config){
                continue;
            }
            if(config.isSuitLevel(battleLevel)){
                continue ;
            }
            return config ;
        }
        return reward3V3List.get(reward3V3List.size()-1);
    }

    @Override
    public Message getArena3V3LevelDescMessage(RoleInstance role) {
        C3866_Arena3V3LevelDescRespMessage respMsg = new C3866_Arena3V3LevelDescRespMessage() ;
        if(Util.isEmpty(reward3V3List)){
            return respMsg ;
        }
        List<Arena3V3LevelDescItem> descList = Lists.newArrayList();
        for(Reward3V3Config config : this.reward3V3List){
            if(null == config){
                continue;
            }
            Arena3V3LevelDescItem item = new Arena3V3LevelDescItem();
            item.setName(config.getName());
            item.setValue(config.getArenaMinLevel());
            descList.add(item);
        }
        respMsg.setDescList(descList);
        return respMsg ;
    }
	
	
	@Override
	public C3863_Arena3V3DetailRespMessage getArena3V3DetailRespMessage(RoleInstance role){
		C3863_Arena3V3DetailRespMessage resp = new C3863_Arena3V3DetailRespMessage();
		ArenaType arenaType = ArenaType._3V3;
		if(this.isOpenDarkDoor){
			arenaType = ArenaType._3V3_DARK_DOOR;
		}
		ArenaConfig config = GameContext.getArenaApp().getArenaConfig(arenaType);
		if(null == config){
			return null;
		}
		Reward3V3Config lastReward = this.getReward3V3Config(role.getRoleArena().getLastArenaLevel3v3());
		if(null == lastReward){
			return null;
		}
		RoleArena roleArena = role.getRoleArena();
		if(null == roleArena){
			return null;
		}
        float currentArenaLevel3v3 = roleArena.getArenaLevel3v3() ;
		Reward3V3Config currentReward = this.getReward3V3Config(currentArenaLevel3v3);
		if(null == currentReward){
			return null;
		}
		resp.setActiveId(config.getActiveId());
        resp.setCurrentArenaLevel3v3((int)currentArenaLevel3v3);
        resp.setCurrentArenaLevel3v3Name(currentReward.getName());

        Reward3V3Config nextReward = this.getNextReward3V3Config(currentArenaLevel3v3);
        resp.setNextArenaLevel3v3(nextReward.getArenaMinLevel());
        resp.setNextArenaLevel3v3Name(nextReward.getName());

		resp.setCycle3v3Score(roleArena.getCycle3v3Score());
		resp.setMaxCycle3v3Score(lastReward.getMaxCycleScore());
        resp.setWin(roleArena.getCycleWin3v3());
        resp.setFail(roleArena.getCycleFail3v3());
		resp.setArenaScore(role.getArena3v3Score());
		resp.setMaxArenaScore(lastReward.getMaxRoleScore());
		resp.setDesc(config.getDesc());
		//兑换参数
		resp.setExchangeParam(config.getExchangeMenuId());
		ApplyInfo info = GameContext.getArenaApp().getApplyInfo(role.getRoleId());
		if(null == info){
			return resp;
		}
		Team team = role.getTeam();
		if(team == null || team.isLeader(role)) {
			resp.setArenaType(ApplyState.had_apply.getType());
		}else{
			resp.setArenaType((byte)2);
		}
		Date applyDate = new Date(info.getCreateDate());
		resp.setTime(DateUtil.dateDiffSecond(applyDate, new Date()));
		return resp;
	}
	
	@Override
	public short getBuffId(int arenaType){
		List<ArenaBuffConfig> list = this.buffConfigMap.get(arenaType);
		if(Util.isEmpty(list)){
			return 0;
		}
		int dateDiff = DateUtil.dateDiffDay(GameContext.gameStartDate, new Date());
		for(ArenaBuffConfig config : list){
			if(null == config){
				continue;
			}
			
			if(config.isSuitDay(dateDiff)){
				return config.getBuffId();
			}
		}
		return 0;
	}
	
	@Override
	public void pushArena3v3RespMessage(RoleInstance role) {
		try{
			Team team = role.getTeam();
			if(team != null && !team.isLeader(role)){
				return;
			}
			if(this.isOpenDarkDoor){
				//TODO:
				//GameContext.getDarkDoorApp().applyState(role.getRoleId(), (byte)ArenaType._3V3_DARK_DOOR.getType());
				return;
			}
			C3863_Arena3V3DetailRespMessage resp = this.getArena3V3DetailRespMessage(role);
			if(null == resp) {
				return;
			}
			role.getBehavior().sendMessage(resp);
		}catch(Exception e){
			logger.error("ArenaApp.pushArena3v3RespMessage error:",e);
		}
	}

	@Override
	public void openDarkDoor(boolean flag) {
		this.isOpenDarkDoor = flag;
	}

	@Override
	public boolean isOpenDarkDoor() {
		return this.isOpenDarkDoor;
	}
	
	private void loadAreanMapRule(){
		//加载擂台赛配置
		String fileName = XlsSheetNameType.arena_map_rule.getXlsName();
		String sheetName = XlsSheetNameType.arena_map_rule.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			mapRuleMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,ArenaMapRule.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
		}
		if(Util.isEmpty(this.mapRuleMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
		for(ArenaMapRule rule : mapRuleMap.values()){
			if(null == rule){
				continue;
			}
			String mapId = rule.getMapId();
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if(null == map){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("The arena map is not exist.mapId = " + mapId);
				return ;
			}
			if(!map.getMapConfig().changeLogicType(MapLogicType.arena3V3)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("map not exist or change map:" + mapId + " to "
						+ MapLogicType.arena3V3.getType() +" fail");
			}
			rule.init();
		}
	}
	
	private void loadReward3V3(){
		//加载擂台赛配置
		String fileName = XlsSheetNameType.arena_reward_3v3.getXlsName();
		String sheetName = XlsSheetNameType.arena_reward_3v3.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			reward3V3List = XlsPojoUtil.sheetToList(sourceFile, sheetName,Reward3V3Config.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
		}
		if(Util.isEmpty(this.reward3V3List)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
		for(Reward3V3Config config : reward3V3List){
			config.init();
		}
	}
	
	private void loadBuffConfig(){
		//加载擂台赛配置
		String fileName = XlsSheetNameType.arena_buff_config.getXlsName();
		String sheetName = XlsSheetNameType.arena_buff_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<ArenaBuffConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName,ArenaBuffConfig.class);
			if(Util.isEmpty(list)){
				return;
			}
			for(ArenaBuffConfig config : list){
				if(null == config){
					continue;
				}
				int arenaType = config.getArenaType();
				if(!buffConfigMap.containsKey(arenaType)){
					buffConfigMap.put(arenaType, new ArrayList<ArenaBuffConfig>());
				}
				buffConfigMap.get(arenaType).add(config);
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
		}
	}

	private void loadAreanMapConfig(){
		//加载擂台赛配置
		String fileName = XlsSheetNameType.arena_map_config.getXlsName();
		String sheetName = XlsSheetNameType.arena_map_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			mapConfigMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,ArenaMapConfig.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
		}
		if(Util.isEmpty(this.mapConfigMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
		}
		for(ArenaMapConfig config : mapConfigMap.values()){
			if(null == config){
				continue;
			}
			config.init();
		}
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		this.loadAreanMapConfig();
		this.loadAreanMapRule();
		this.loadReward3V3();
		this.loadBuffConfig();
	}

	@Override
	public void stop() {
		
	}
}
