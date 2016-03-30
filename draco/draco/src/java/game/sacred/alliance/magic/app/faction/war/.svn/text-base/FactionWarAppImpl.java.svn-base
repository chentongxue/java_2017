//package sacred.alliance.magic.app.faction.war;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import sacred.alliance.magic.app.active.vo.Active;
//import sacred.alliance.magic.app.arena.ArenaMatch;
//import sacred.alliance.magic.app.chat.ChannelType;
//import sacred.alliance.magic.app.chat.ChatSysName;
//import sacred.alliance.magic.app.faction.godbeast.FactionSoulFlyConfig;
//import sacred.alliance.magic.app.faction.godbeast.FactionSoulInspireBuffConfig;
//import sacred.alliance.magic.app.faction.godbeast.FactionSoulInspireConfig;
//import sacred.alliance.magic.app.faction.godbeast.FactionSoulInspireCostType;
//import sacred.alliance.magic.app.faction.godbeast.FactionSoulInspireType;
//import sacred.alliance.magic.app.faction.godbeast.FactionSoulRecord;
//import sacred.alliance.magic.app.faction.war.config.FactionWarAwardConfig;
//import sacred.alliance.magic.app.faction.war.config.FactionWarAwardFactionConfig;
//import sacred.alliance.magic.app.faction.war.config.FactionWarAwardRoleConfig;
//import sacred.alliance.magic.app.faction.war.config.FactionWarAwardRule;
//import sacred.alliance.magic.app.faction.war.config.FactionWarBoradcastConfig;
//import sacred.alliance.magic.app.faction.war.config.FactionWarConfig;
//import sacred.alliance.magic.app.faction.war.config.FactionWarGambleConfig;
//import sacred.alliance.magic.app.faction.war.domain.FactionWarGambleInfo;
//import sacred.alliance.magic.app.faction.war.domain.FactionWarInfo;
//import sacred.alliance.magic.app.faction.war.result.GambleResult;
//import sacred.alliance.magic.app.goods.GoodsOperateBean;
//import sacred.alliance.magic.app.map.data.MapConfig;
//import sacred.alliance.magic.base.ActiveType;
//import sacred.alliance.magic.base.AttributeType;
//import sacred.alliance.magic.base.FactionPowerType;
//import sacred.alliance.magic.base.MapLogicType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.base.XlsSheetNameType;
//import sacred.alliance.magic.component.id.IdFactory;
//import sacred.alliance.magic.component.id.IdType;
//import sacred.alliance.magic.constant.Cat;
//import sacred.alliance.magic.constant.ParasConstant;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.dao.impl.FactionDAOImpl;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionRole;
//import sacred.alliance.magic.util.DateUtil;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.RandomUtil;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.util.Wildcard;
//import sacred.alliance.magic.util.XlsPojoUtil;
//import sacred.alliance.magic.vo.MapInstance;
//import sacred.alliance.magic.vo.RoleInstance;
//
//import com.game.draco.GameContext;
//import com.game.draco.app.mail.domain.Mail;
//import com.game.draco.app.mail.domain.MailAttriBean;
//import com.game.draco.app.mail.type.MailSendRoleType;
//import com.game.draco.app.npc.domain.NpcInstance;
//import com.game.draco.app.npc.type.NpcFuncShowType;
//import com.game.draco.message.item.FactionWarItem;
//import com.game.draco.message.item.NpcFunctionItem;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
//import com.game.draco.message.push.C1742_FactionWarRankNotifyMessage;
//import com.game.draco.message.request.C1746_FactionWarConfirmReqMessage;
//import com.game.draco.message.request.C1747_FactionWarInspireReqMessage;
//import com.game.draco.message.response.C1741_FactionWarRespMessage;
//import com.game.draco.message.response.C1745_FactionWarGambleInfoRespMessage;
//
//public class FactionWarAppImpl implements FactionWarApp{
//	private final static Logger logger = LoggerFactory.getLogger(ArenaMatch.class);
//	private Map<String, FactionWarMatch> allFactionWarMatch = new ConcurrentHashMap<String, FactionWarMatch>();
//	private Map<String, FactionWarInfo> factionWarInfoMap = new LinkedHashMap<String, FactionWarInfo>();
//	private Active factionWarActive;
//	private Object createLock = new byte[0]; 
//	private FactionWarConfig factionWarConfig;
//	private final static int DEFAULT_FACTION_COUNT = 12;
//	private String emptyFactionId;
//	private String firstFactionId;
//	private String secondFactionId;
//	private String thirdFactionId;
//	private String leastFactionId;
//	private int curRound = 0;
//	private int beginRound = 0;
//	private String[] factionWarArr = new String[DEFAULT_FACTION_COUNT];
//	private AtomicBoolean create = new AtomicBoolean(false);//表示是否已经创建了本轮门派战 !!!!!本轮打完时 一定要重置为false，否无法生成下一轮门派战
//	private Map<String, Integer> awardConfigMap = new HashMap<String, Integer>();//rule
//	private Map<Integer, FactionWarAwardRule> awardRuleMap = new HashMap<Integer, FactionWarAwardRule>();
//	private Map<Integer, FactionWarAwardRoleConfig> awardRoleConfigMap = new HashMap<Integer, FactionWarAwardRoleConfig>();
//	private Map<Integer, FactionWarGambleConfig> factionWarGambleMap = new HashMap<Integer, FactionWarGambleConfig>();
//	protected static final int RANK_SIZE = 3;
//	private Map<Integer, FactionWarAwardFactionConfig> factionAwardMap = new HashMap<Integer, FactionWarAwardFactionConfig>();
//	private int totalMoney = 0;
//	private Map<Integer, FactionWarBoradcastConfig> broadcastMap = new HashMap<Integer, FactionWarBoradcastConfig>();
//	private C0007_ConfirmationNotifyMessage message;
//	private static final int DEFAULT_TIME = 10 ;
//	private static final short FACTION_WAR_CONFIRM_CMD = new C1746_FactionWarConfirmReqMessage().getCommandId();
//	private static final int REMAINTIME_OFF_SET = 2;
//	private static final short FACTION_WAR_INSPIRE_CMD = new C1747_FactionWarInspireReqMessage().getCommandId();
//	private static final String FACTION_WAR_INSPIRE_BUFF_ID = "buffId";
//	private FactionDAOImpl factionDAO;
//
//	@Override
//	public void start() {
//		initActive();
//		initFactionWarConfig();
//		initFactionWarAwardConfig();
//		initFactionWarAwardRule();
//		initFactionWarAwardRoleConfig();
//		initFactionWar();
//		initFactionWarGamble();
//		initFactionWarFactionAward();
//		initFactionWarBroad();
//	}
//	
//	/**
//	 * 加载factionWarConfig
//	 */
//	private void initFactionWarConfig(){
//		String fileName = XlsSheetNameType.faction_war_config.getXlsName();
//		String sheetName = XlsSheetNameType.faction_war_config.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			List<FactionWarConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionWarConfig.class);
//			if(Util.isEmpty(list)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +", sheetName ="+sheetName);
//			}
//			this.factionWarConfig = list.get(0);
//			if(null == factionWarConfig) {
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +", sheetName ="+sheetName);
//			}
//			
//			this.factionWarConfig.init();
//			
//			String mapId = factionWarConfig.getMapId();
//			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
//			if(null == map){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: The map is not exist.mapId = " + mapId);
//			}
//			//将地图逻辑修改为factionWar类型
//			map.getMapConfig().setLogictype((byte) MapLogicType.factionWar.getType());
//			
//			this.totalMoney = this.factionWarConfig.getGambleMoney();
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + ",sheetName = " + sheetName, ex);
//		}
//	}
//	
//	/**
//	 * 加载门派奖励
//	 */
//	private void initFactionWarAwardConfig(){
//		String fileName = XlsSheetNameType.faction_war_award_config.getXlsName();
//		String sheetName = XlsSheetNameType.faction_war_award_config.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			List<FactionWarAwardConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionWarAwardConfig.class);
//			if(Util.isEmpty(list)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//			}
//			for(FactionWarAwardConfig config : list) {
//				String key = getAwardKey(config.getRounds(), config.getPosition());
//				awardConfigMap.put(key, config.getRuleId());
//			}
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
//		}
//	}
//	
//	/**
//	 * 加载奖励规则
//	 */
//	private void initFactionWarAwardRule(){
//		String fileName = XlsSheetNameType.faction_war_award_rule.getXlsName();
//		String sheetName = XlsSheetNameType.faction_war_award_rule.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			awardRuleMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, FactionWarAwardRule.class);
//			if(Util.isEmpty(awardRuleMap)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//			}
//			for(FactionWarAwardRule rule : awardRuleMap.values()){
//				if(null == rule){
//					continue;
//				}
//				rule.init();
//			}
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
//		}
//	}
//	
//	/**
//	 * 加载个人奖励
//	 */
//	private void initFactionWarAwardRoleConfig(){
//		String fileName = XlsSheetNameType.faction_war_role_award_config.getXlsName();
//		String sheetName = XlsSheetNameType.faction_war_role_award_config.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			awardRoleConfigMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, FactionWarAwardRoleConfig.class);
//			if(Util.isEmpty(awardRoleConfigMap)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//			}
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
//		}
//	}
//	
//	/**
//	 * 加载押注
//	 */
//	private void initFactionWarGamble(){
//		String fileName = XlsSheetNameType.faction_war_gamble.getXlsName();
//		String sheetName = XlsSheetNameType.faction_war_gamble.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			List<FactionWarGambleConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, FactionWarGambleConfig.class);
//			if(Util.isEmpty(list)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//			}
//			//排序
//			Collections.sort(list, new Comparator<FactionWarGambleConfig>(){
//				@Override
//				public int compare(FactionWarGambleConfig config1, FactionWarGambleConfig config2) {
//					if(config1.getMinLevel() > config2.getMinLevel()){
//						return 1 ;
//					}
//					if(config1.getMinLevel() < config2.getMinLevel()){
//						return -1 ;
//					}
//					if(config1.getMaxLevel() > config2.getMaxLevel()){
//						return 1 ;
//					}
//					if(config1.getMaxLevel() < config2.getMaxLevel()){
//						return -1 ;
//					}
//					return 0;
//				}
//			});
//			
//			this.factionWarGambleMap.clear();
//			
//			for(FactionWarGambleConfig config : list){
//				config.init();
//				for(int i=config.getMinLevel();i<=config.getMaxLevel();i++){
//					this.factionWarGambleMap.put(i, config);
//				}
//			}
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
//		}
//	}
//	
//	/**
//	 * 加载门派奖励
//	 */
//	private void initFactionWarFactionAward(){
//		String fileName = XlsSheetNameType.faction_war_faction_award.getXlsName();
//		String sheetName = XlsSheetNameType.faction_war_faction_award.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			factionAwardMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, FactionWarAwardFactionConfig.class);
//			if(Util.isEmpty(factionAwardMap)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//			}
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
//		}
//	}
//	
//	/**
//	 * 加载喊话
//	 */
//	private void initFactionWarBroad(){
//		String fileName = XlsSheetNameType.faction_war_broadcast.getXlsName();
//		String sheetName = XlsSheetNameType.faction_war_broadcast.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			broadcastMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, FactionWarBoradcastConfig.class);
//			if(Util.isEmpty(broadcastMap)){
//				Log4jManager.checkFail();
//				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//			}
//		}catch(Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
//		}
//	}
//	
//	private String getAwardKey(int rounds, int position) {
//		return rounds + Cat.underline + position;
//	}
//
//	@Override
//	public void addFactionWarMatch(FactionWarMatch match) {
//		if(null == match){
//			return ;
//		}
//		this.allFactionWarMatch.put(match.getKey(), match);
//	}
//	
//	@Override
//	public void createWar(){
//		try{
//			if(!isFactionWarOpen()){
//				return ;
//			}
//			
//			if(create.get()){
//				return;
//			}
//			
//			synchronized (createLock) {
//				if(create.get()){
//					return;
//				}
//				List<Faction> factionList = GameContext.getFactionApp().getFactionWarFactionList(4);
//				if(Util.isEmpty(factionList)){
//					return;
//				}
//				this.resetFactionWar();
//				this.matchFactionWar(factionList);
//				create.set(true);
//			}
//		}catch(Exception e){
//			logger.error("FactionWarApp.createWar error",e);
//		}
//	}
//	
//	/**
//	 * 加载门派战信息
//	 */
//	private void initFactionWar(){
//		try{
//			List<FactionWarInfo> list = GameContext.getBaseDAO().selectAll(FactionWarInfo.class);
//			if(Util.isEmpty(list)){
//				createWar();
//				return;
//			}
//			Date createTime = null;
//			for(FactionWarInfo info : list) {
//				if(null == info){
//					continue;
//				}
//				if(null == createTime){
//					createTime = info.getCreateTime();
//				}
//				String factionId = info.getFactionId();
//				this.factionWarArr[info.getFactionWarIndex()] = factionId;
//				if(info.getRounds() == 5){
//					this.firstFactionId = factionId;
//					this.curRound = 5;
//				}
//				if(info.getRounds() == 4){
//					this.secondFactionId = factionId;
//				}
//				if(info.getRounds() == 3){
//					this.thirdFactionId = factionId;
//				}
//				this.factionWarInfoMap.put(info.getFactionId(), info);
//			}
//			if(isFactionWarOpen()){
//				//如果在创建时间内，并且记录是当天的表示之前已经生产记录了，需要重新生成对阵
//				//但是如果队伍不足12个，会有已经晋级到第2轮的，需要把所有队伍的轮次置成第一轮并且重新生成对阵
//				if(DateUtil.sameDay(createTime, new Date())){
//					this.curRound = 1;
//					this.beginRound = 1;
//					this.resetRound(list, curRound);
//					this.buildFirstRoundMatch();
//					int gambleMoney = 0;
//					try{
//						gambleMoney = factionDAO.getFacrtionWarGambleMoney();
//					}catch(Exception e){
//						logger.error("FactionWarApp.initFactionWar.getFacrtionWarGambleMoney error",e);
//					}
//					this.totalMoney += gambleMoney;
//					create.set(true);
//					return;
//				}
//				createWar();
//			}else{
//				//本轮不打了
//				//发押注的奖励
//				this.accountGamble();
//			}
//		}catch(Exception e){
//			logger.error("FactionWarApp.initFactionWar error",e);
//		}
//	}
//	
//	private void resetRound(List<FactionWarInfo> list, int rounds){
//		for(FactionWarInfo info : list) {
//			if(null == info){
//				continue;
//			}
//			info.setRounds(rounds);
//		}
//	}
//	
//	private void resetFactionWar(){
//		this.emptyFactionId = "";
//		this.firstFactionId = "";
//		this.secondFactionId = "";
//		this.thirdFactionId = "";
//		this.leastFactionId = "";
//		this.curRound = 0;
//		this.beginRound = 0;
//		this.factionWarInfoMap.clear();
//		this.factionWarArr = new String[DEFAULT_FACTION_COUNT];
//		GameContext.getBaseDAO().deleteAll(FactionWarInfo.class);
//	}
//	
//	private void matchFactionWar(List<Faction> factionList){
//		try{
//			List<Faction> list = new ArrayList<Faction>();
//			list.addAll(factionList);
//			//打乱顺序
//			Collections.shuffle(list);
//			//不足12个补null
//			int size = list.size();
//			if(size < DEFAULT_FACTION_COUNT){
//				for(int i=0;i<DEFAULT_FACTION_COUNT-size;i++){
//					list.add(null);
//				}
//			}
//			this.curRound = 1;
//			this.beginRound = 1;
//			int index = -1;
//			for(int i=0; i<DEFAULT_FACTION_COUNT/6; i++){
//				//list的size=12，所以index可以++两个循环
//				Faction faction1 = list.get(++index);
//				Faction faction2 = list.get(++index);
//				Faction faction3 = list.get(++index);
//				Faction faction4 = list.get(++index);
//				Faction faction5 = list.get(++index);
//				Faction faction6 = list.get(++index);
//				
//				FactionWarInfo factionWarInfo1 = null;
//				FactionWarInfo factionWarInfo2 = null;
//				FactionWarInfo factionWarInfo3 = null;
//				FactionWarInfo factionWarInfo4 = null;
//				FactionWarInfo factionWarInfo5 = null;
//				FactionWarInfo factionWarInfo6 = null;
//				if(null != faction1){
//					String factionId = faction1.getFactionId();
//					factionWarInfo1 = new FactionWarInfo(faction1, curRound, i);
//					factionWarInfoMap.put(factionId, factionWarInfo1);
//					factionWarArr[i] = factionId;
//				}
//				
//				if(null != faction2){
//					String factionId = faction2.getFactionId();
//					factionWarInfo2 = new FactionWarInfo(faction2, curRound, i+2);
//					factionWarInfoMap.put(factionId, factionWarInfo2);
//					factionWarArr[i+2] = factionId;
//				}
//				
//				if(null != faction3){
//					String factionId = faction3.getFactionId();
//					factionWarInfo3 = new FactionWarInfo(faction3, curRound, i+4);
//					factionWarInfoMap.put(factionId, factionWarInfo3);
//					factionWarArr[i+4] = factionId;
//				}
//				
//				if(null != faction4){
//					String factionId = faction4.getFactionId();
//					factionWarInfo4 = new FactionWarInfo(faction4, curRound, i+6);
//					factionWarInfoMap.put(factionId, factionWarInfo4);
//					factionWarArr[i+6] = faction4.getFactionId();
//				}
//				
//				if(null != faction5){
//					String factionId = faction5.getFactionId();
//					factionWarInfo5 = new FactionWarInfo(faction5, curRound, i+8);
//					factionWarInfoMap.put(factionId, factionWarInfo5);
//					factionWarArr[i+8] = faction5.getFactionId();
//				}
//				
//				if(null != faction6){
//					String factionId = faction6.getFactionId();
//					factionWarInfo6 = new FactionWarInfo(faction6, curRound, i+10);
//					factionWarInfoMap.put(factionId, factionWarInfo6);
//					factionWarArr[i+10] = faction6.getFactionId();
//				}
//			}
//			//入库
//			for(FactionWarInfo info : factionWarInfoMap.values()){
//				if(null == info){
//					continue;
//				}
//				GameContext.getBaseDAO().insert(info);
//			}
//			//创建第一轮比赛
//			this.buildFirstRoundMatch();
//			//把总钱数设置成0,显示用
//			this.totalMoney = this.factionWarConfig.getGambleMoney();
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	private void buildMatch(){
//		try{
//			List<String> curRoundList = new ArrayList<String>();
//			for(String factionId : factionWarArr){
//				if(Util.isEmpty(factionId)) {
//					continue;
//				}
//				FactionWarInfo info = this.factionWarInfoMap.get(factionId);
//				if(null == info){
//					continue;
//				}
//				if(info.getRounds() != curRound){
//					continue;
//				}
//				curRoundList.add(factionId);
//			}
//			
//			//如果只有3支队伍，需要产生一个轮空队伍
//			int size = curRoundList.size();
//			if(size == 3) {
//				int random = Util.randomInt(0, size - 1);
//				emptyFactionId = curRoundList.get(random);
//				curRoundList.remove(random);
//				FactionWarInfo info = this.factionWarInfoMap.get(emptyFactionId);
//				info.setRounds(info.getRounds() + 1);
//				GameContext.getBaseDAO().update(info);
//			}
//			
//			if(curRoundList.size() > 1 && curRoundList.size()%2 > 0){
//				curRoundList.add(null);
//			}
//			
//			for(int i=0; i<curRoundList.size() - 1; i+=2) {
//				String factionId1 = curRoundList.get(i);
//				String factionId2 = curRoundList.get(i+1);
//				FactionWarInfo info1 = null;
//				FactionWarInfo info2 = null;
//				if(!Util.isEmpty(factionId1)) {
//					info1 = this.factionWarInfoMap.get(factionId1);
//				}
//				if(!Util.isEmpty(factionId2)) {
//					info2 = this.factionWarInfoMap.get(factionId2);
//				}
//				if(null != info1 && null != info2){
//					FactionWarMatch.create(info1, info2, factionWarConfig, curRound, beginRound);
//					continue;
//				}
//				if(null != info1 && null == info2){
//					info1.setRounds(info1.getRounds() + 1);
//					GameContext.getBaseDAO().update(info1);
//					continue;
//				}
//				if(null == info1 && null != info2){
//					info2.setRounds(info2.getRounds() + 1);
//					GameContext.getBaseDAO().update(info2);
//					continue;
//				}
//			}
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	/**
//	 * 构建第一轮战斗
//	 * 第一轮战斗有轮空的可能
//	 */
//	private void buildFirstRoundMatch(){
//		try {
//			for(int i=0; i<factionWarArr.length - 1; i+=2){
//				String factionId1 = factionWarArr[i];
//				String factionId2 = factionWarArr[i + 1];
//				FactionWarInfo info1 = null;
//				FactionWarInfo info2 = null;
//				if(!Util.isEmpty(factionId1)) {
//					info1 = this.factionWarInfoMap.get(factionId1);
//				}
//				if(!Util.isEmpty(factionId2)) {
//					info2 = this.factionWarInfoMap.get(factionId2);
//				}
//				if(null != info1 && null != info2){
//					FactionWarMatch.create(info1, info2, factionWarConfig, curRound, beginRound);
//					continue;
//				}
//				if(null != info1 && null == info2){
//					info1.setRounds(info1.getRounds() + 1);
//					GameContext.getBaseDAO().update(info1);
//					continue;
//				}
//				if(null == info1 && null != info2){
//					info2.setRounds(info2.getRounds() + 1);
//					GameContext.getBaseDAO().update(info2);
//					continue;
//				}
//			}
//			if(curRoundOver()){
//				//如果第一轮没有对阵，直接生成第2轮
//				curRound++;
//				beginRound = curRound;
//				this.buildMatch();
//			}
//		} catch (Exception e) {
//			logger.error("",e);
//		}
//	}
//	
//	@Override
//	public void factionWarOver(FactionWarMatch match, String mapWinFactionId){
//		try{
//			if(match.isOver()){
//				return;
//			}
//			String winFactionId = mapWinFactionId;
//			FactionWarInfo winInfo = this.factionWarInfoMap.get(winFactionId);
//			if(null == winInfo){
//				winInfo = match.getFaction1();
//				winFactionId = winInfo.getFactionId();
//			}
//			winInfo.getMatch().setOver(true);
//			winInfo.reset();
//			String loseFactionId = match.getOtherId(winFactionId);
//			FactionWarInfo loseInfo = this.factionWarInfoMap.get(loseFactionId);
//			if(null == loseInfo){
//				return;
//			}
//			loseInfo.getMatch().setOver(true);
//			loseInfo.reset();
//			//广播
//			this.broadCastEnd(match, winFactionId, loseFactionId);
//			//进行下一轮
//			winInfo.setRounds(winInfo.getRounds() + 1);
//			GameContext.getBaseDAO().update(winInfo);
//			if(curRound == 4){
//				firstFactionId = winFactionId;
//				secondFactionId = loseFactionId;
//				this.curRound++;
//				//已经决出冠军，开始发奖逻辑
//				this.factionWarOverReward();
//				//已经决出冠军,发押注的奖励
//				this.accountGamble();
//				//本轮结束
//				this.allFactionWarOver();
//				return;
//			}
//			
//			if(curRound == 3){
//				thirdFactionId = loseFactionId;
//			}
//			this.createNextFactionWar();
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	private void factionWarOverReward(){
//		try{
//			for(FactionWarInfo info : this.factionWarInfoMap.values()) {
//				if(null == info){
//					continue;
//				}
//				String firstFactionName = "";
//				FactionWarInfo firstInfo = this.factionWarInfoMap.get(firstFactionId);
//				if(null != firstInfo){
//					firstFactionName = firstInfo.getFactionName();
//				}
//				int factionRounds = info.getRounds();
//				this.awardFaction(info.getFactionId(), factionRounds);
//				List<FactionRole> factionRoleList = GameContext.getFactionApp().getFactionRoleList(info.getFactionId());
//				for(FactionRole role : factionRoleList){
//					if(null == role){
//						continue;
//					}
//					String key = getAwardKey(factionRounds, role.getPosition());
//					int ruleId = this.awardConfigMap.get(key);
//					FactionWarAwardRule rule = this.awardRuleMap.get(ruleId);
//					if(null == rule){
//						continue;
//					}
//					String mailContent = MessageFormat.format(rule.getMailContent(), firstFactionName);
//					this.sendGoodsByMail(String.valueOf(role.getRoleId()), rule.getBindingGoldMoney(),
//							rule.getSilverMoney(), rule.getContribute(), 
//							rule.getZp(), rule.getGoodsList(), rule.getMailTitle(), mailContent);
//				}
//			}
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	private void awardFaction(String factinId, int rounds){
//		try{
//			Faction faction = GameContext.getFactionApp().getFaction(factinId);
//			if(null == faction){
//				return;
//			}
//			FactionWarAwardFactionConfig config = this.factionAwardMap.get(rounds);
//			if(null == config){
//				return;
//			}
//			GameContext.getFactionFuncApp().changeFactionMoney(faction, OperatorType.Add, config.getFactionMoney(), OutputConsumeType.faction_money_war, "");
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	private void sendGoodsByMail(String roleId, int bindGold, int silverMoney, int contribute, int zp, 
//			List<GoodsOperateBean> goodsList,String title, String content){
//		OutputConsumeType ocType = OutputConsumeType.faction_war_reward;
//		MailAttriBean bean = new MailAttriBean();
//		bean.setBindGold(bindGold);
//		bean.setSilverMoney(silverMoney);
//		bean.setContribute(contribute);
//		bean.setZp(zp);
//		GameContext.getMailApp().sendMail(roleId, title, content,
//				MailSendRoleType.System.getName(), 
//				ocType.getType(), goodsList,bean);
//	}
//	
//	private void allFactionWarOver(){
//		this.create.set(false);
//		this.allFactionWarMatch.clear();
//	}
//	
//	private void createNextFactionWar(){
//		try{
//			synchronized (createLock) {
//				if(!curRoundOver()) {
//					return;
//				}
//				this.allFactionWarMatch.clear();
//				//上轮结束，当前轮次+1
//				curRound++;
//				this.buildMatch();
//			}
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	private boolean curRoundOver(){
//		try{
//			if(Util.isEmpty(allFactionWarMatch)){
//				return true;
//			}
//			for(FactionWarMatch match : allFactionWarMatch.values()) {
//				if(null == match){
//					continue;
//				}
//				if(!match.isOver()){
//					return false;
//				}
//			}
//			return true;
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		return false;
//	}
//	
//	private boolean isFactionWarOpen(){
//		if(null == factionWarActive) {
//			return false;
//		}
//		if(factionWarActive.isOutDate()){
//			return false;
//		}
//		if(!factionWarActive.isDayNowActive()){
//			return false;
//		}
//		if(!factionWarConfig.canCreate()){
//			return false;
//		}
//		return true;
//	}
//	
//	/**
//	 * 找到阵营战活动
//	 */
//	private void initActive(){
//		Collection<Active> list = GameContext.getActiveApp().getAllActive();
//		if(Util.isEmpty(list)){
//			return;
//		}
//		for(Active active : list){
//			if(ActiveType.FactionWar.getType() != active.getType()){
//				continue ;
//			}
//			factionWarActive = active;
//			break;
//		}
//	}
//	
//	@Override
//	public Result enterFactionWar(RoleInstance role){
//		Result result = new Result();
//		try{
//			result = this.canEnterFactionWar(role);
//			if(!result.isSuccess()){
//				return result;
//			}
//			FactionWarInfo info = this.factionWarInfoMap.get(role.getFactionId());
//			role.getBehavior().changeMap(info.getPoint());
//		}catch(Exception e){
//			logger.error("enterFactionWar error",e);
//		}
//		return result;
//	}
//	
//	private Result canEnterFactionWar(RoleInstance role) {
//		Result result = new Result();
//		try{
//			MapInstance mapIn = role.getMapInstance();
//			if(null != mapIn && isInFactionWarMap(mapIn)){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_HAS_ENTER));
//			}
//			
//			Faction faction = GameContext.getFactionApp().getFaction(role);
//			FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//			if(null == faction || null == factionRole){
//				return result.setInfo(Status.Faction_Not_Own.getTips());
//			}
//			String factionId = faction.getFactionId();
//			FactionWarInfo info = factionWarInfoMap.get(factionId);
//			if(null == info){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_NOT_HAS));
//			}
//			
//			FactionWarMatch match = info.getMatch();
//			if(null == match){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_MATCH_NOT_HAS));
//			}
//			
//			long now = System.currentTimeMillis();
//			long beginTime = match.getBeginTime();
//			long enterTime = beginTime - this.factionWarConfig.getBeforeEnterTime() * 60 * 1000;
//			if(now < enterTime){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_NOT_OPEN));
//			}
//			
//			if(now > beginTime){
//				//如果已经开始，如果在开始前进去过，则可以进入，否则无法进入
//				boolean roleDeathExit = info.getRoleDeathSet().contains(role.getRoleId());
//				boolean roleHasEnter = info.getFactionKilledMap().containsKey(role.getRoleId());
//				if(roleHasEnter && !roleDeathExit){
//					return result.success();
//				}
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_IS_OPEN));
//			}
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		return result.success();
//	}
//	
//	private boolean isInFactionWarMap(MapInstance mapInstance) {
//		MapConfig mapConfig = mapInstance.getMap().getMapConfig();
//		if(null == mapConfig) {
//			return false;
//		}
//		MapLogicType type = mapConfig.getMapLogicType();
//		if(type == MapLogicType.factionWar){
//			return true;
//		}
//		return false;
//	}
//	
//	@Override
//	public FactionWarInfo getFactionWarInfo(String factionId) {
//		return this.factionWarInfoMap.get(factionId);
//	}
//	
//	@Override
//	public Active getActive(){
//		return this.factionWarActive;
//	}
//	
//	@Override
//	public FactionWarInfo getTimeOverWinFaction(FactionWarMatch match, String mapInstanceId){
//		try{
//			FactionWarInfo faction1Info = match.getFaction1();
//			FactionWarInfo faction2Info = match.getFaction2();
//			if(null == faction1Info || null == faction2Info) {
//				return null;
//			}
//			
//			int liveSize1 = this.getMapLiveListSize(faction1Info.getRoleSet(), mapInstanceId, faction1Info.getFactionId());
//			int liveSize2 = this.getMapLiveListSize(faction2Info.getRoleSet(), mapInstanceId, faction2Info.getFactionId());
//			if(liveSize1 > liveSize2){
//				return faction1Info;
//			}
//			if(liveSize1 < liveSize2){
//				return faction2Info;
//			}
//			
//			Faction faction1 = GameContext.getFactionApp().getFaction(faction1Info.getFactionId());
//			Faction faction2 = GameContext.getFactionApp().getFaction(faction2Info.getFactionId());
//			if(null == faction1 && null == faction2){
//				return faction1Info;
//			}
//			
//			FactionSoulRecord record1 = faction1.getFactionSoulRecord();
//			FactionSoulRecord record2 = faction2.getFactionSoulRecord();
//			int level1 = record1.getLevel();
//			int level2 = record2.getLevel();
//			if(level1 > level2) {
//				return faction1Info;
//			}
//			if(level1 < level2) {
//				return faction2Info;
//			}
//			
//			int growValue1 = record1.getGrowValue();
//			int growValue2 = record2.getGrowValue();
//			if(growValue1 > growValue2) {
//				return faction1Info;
//			}
//			if(growValue1 < growValue2) {
//				return faction2Info;
//			}
//			
//			if(faction1.getCreateDate().before(faction2.getCreateDate())){
//				return faction1Info;
//			}
//			if(faction2.getCreateDate().before(faction1.getCreateDate())){
//				return faction2Info;
//			}
//			
//			int random = Util.randomInt(1, 2);
//			if(random == 1){
//				return faction1Info;
//			}
//			if(random == 2){
//				return faction2Info;
//			}
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		return null;
//	}
//	
//	@Override
//	public int getMapLiveListSize(Set<String> roleSet, String mapInstanceId, String factionId){
//		int total = 0 ;
//		for(String roleId:roleSet){
//			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
//			if(null == role){
//				continue ;
//			}
//			if(role.isDeath()){
//				continue ;
//			}
//			if(!role.hasFaction()){
//				continue ;
//			}
//			if(!role.getFactionId().equals(factionId)){
//				continue ;
//			}
//			MapInstance mapInstance = role.getMapInstance();
//			if(null == mapInstance){
//				continue ;
//			}
//			if(!mapInstance.getInstanceId().equals(mapInstanceId)){
//				//没有在当前地图
//				continue ;
//			}
//			total++ ;
//		}
//		return total ;
//	}
//	
//	@Override
//	public C1741_FactionWarRespMessage getFactionWarRespMessage(RoleInstance role){
//		C1741_FactionWarRespMessage resp = new C1741_FactionWarRespMessage();
//		try{
//			this.createWar();
//			this.getRoleGamble(role);
//			List<FactionWarItem> list = new ArrayList<FactionWarItem>();
//			FactionWarItem item = null;
//			for(String factionId : this.factionWarArr){
//				item = new FactionWarItem();
//				if(Util.isEmpty(factionId)){
//					list.add(item);
//					continue;
//				}
//				FactionWarInfo info = this.factionWarInfoMap.get(factionId);
//				if(null == info){
//					list.add(item);
//					continue;
//				}
//				
//				item.setFactionId(factionId);
//				item.setFactionName(info.getFactionName());
//				Faction faction = GameContext.getFactionApp().getFaction(factionId);
//				if(null != faction){
//					item.setFactionCamp(faction.getFactionCamp());
//				}
//				item.setRounds((byte)info.getRounds());
//				list.add(item);
//			}
//			resp.setList(list);
//			resp.setCurRounds((byte)this.curRound);
//			resp.setTotalMoney(this.totalMoney);
//			long openTime = this.factionWarConfig.getCurRoundOpenTime(curRound,beginRound);
//			long now = System.currentTimeMillis();
//			if(now >= openTime){
//				resp.setRemainTime(0);
//			}else{
//				int remainTime = (int)((openTime - now)/1000 - factionWarConfig.getBeforeEnterTime()  * 60);
//				resp.setRemainTime(remainTime + REMAINTIME_OFF_SET);
//			}
//			Result result = this.canEnterFactionWar(role);
//			if(result.isSuccess()){
//				resp.setCanEnter((byte)1);
//			}
//			
//			FactionWarGambleInfo info = role.getFactionWarGambleInfo();
//			if(null != info){
//				resp.setGambleFactionId(info.getFactionId());
//				resp.setGambleMoney(info.getMoney());
//				resp.setCanGamble((byte)0);
//			}else{
//				if(this.isFactionWarOpen()){
//					resp.setCanGamble((byte)1);
//				}
//			}
//			
////			if(Util.isEmpty(firstFactionId)){
////				return resp;
////			}
//			//发送排名信息
//			C1742_FactionWarRankNotifyMessage message = new C1742_FactionWarRankNotifyMessage();
//			message.setFirstFactionId(firstFactionId);
//			message.setSecondFactionId(secondFactionId);
//			message.setThirdFactionId(thirdFactionId);
//			message.setEmptyFactionId(emptyFactionId);
//			message.setLeastFactionId(leastFactionId);
//			role.getBehavior().sendMessage(message);
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		return resp;
//	}
//	
//	private void getRoleGamble(RoleInstance role){
//		try{
//			if(role.isQueryGamble()){
//				return;
//			}
//			FactionWarGambleInfo info = GameContext.getBaseDAO().selectEntity(FactionWarGambleInfo.class, "roleId", role.getRoleId());
//			if(null != info){
//				role.setFactionWarGambleInfo(info);
//			}
//			role.setQueryGamble(true);
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	@Override
//	public FactionWarAwardRoleConfig getRoleAwardConfig(int rounds) {
//		return this.awardRoleConfigMap.get(rounds);
//	}
//	
//	@Override
//	public boolean hasFactionWar(String factionId){
//		if(this.factionWarActive.isOutDate()){
//			return false;
//		}
//		if(!this.factionWarActive.isDayNowActive()){
//			return false;
//		}
//		if(!this.factionWarConfig.warTime()){
//			return false;
//		}
//		if(!this.factionWarInfoMap.containsKey(factionId)){
//			return false;
//		}
//		return true;
//	}
//	
//	@Override
//	public GambleResult gameble(RoleInstance role, String factionId, int money){
//		GambleResult result = new GambleResult();
//		try{
//			result = this.canGameble(role, factionId, money);
//			if(!result.isSuccess()){
//				return result;
//			}
//			FactionWarGambleConfig config = result.getFactionWarGambleConfig();
//			if(null == config){
//				result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_CAN_NOT));
//				return result;
//			}
//			
//			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.silverMoney, OperatorType.Decrease, money, OutputConsumeType.faction_war_gamble);
//			role.getBehavior().notifyAttribute();
//			
//			FactionWarGambleInfo info = new FactionWarGambleInfo();
//			info.setFactionId(factionId);
//			info.setMoney(money);
//			info.setRoleId(role.getRoleId());
//			GameContext.getBaseDAO().insert(info);
//			role.setFactionWarGambleInfo(info);
//			this.totalMoney += money;
//			result.setTotalMoney(this.totalMoney);
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		result.success();
//		return result;
//	}
//	
//	private GambleResult canGameble(RoleInstance role, String factionId, int money){
//		GambleResult result = new GambleResult();
//		try{
//			if(!isFactionWarOpen()){
//				result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_NOT_OPEN));
//				return result;
//			}
//			this.getRoleGamble(role);
//			if(null != role.getFactionWarGambleInfo()){
//				result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_ALREADY_DONE));
//				return result;
//			}
//			if(!this.factionWarInfoMap.containsKey(factionId)){
//				result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_FACTION_NOT_EXIST));
//				return result;
//			}
//			if(!this.factionWarConfig.canGamble()){
//				result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_BEGIN));
//				return result;
//			}
//			int roleLevel = role.getLevel();
//			FactionWarGambleConfig config = this.factionWarGambleMap.get(roleLevel);
//			if(null == config){
//				result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_CAN_NOT));
//				return result;
//			}
//			if(!config.getMoneySet().contains(money)){
//				result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
//				return result;
//			}
//			if(role.getSilverMoney() < money){
//				result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_MONEY_NOT_ENOUGH));
//				return result;
//			}
//			result.setFactionWarGambleConfig(config);
//			result.setFactionId(factionId);
//			result.success();
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		return result;
//	}
//	
//	/**
//	 * 押注发奖
//	 */
//	private void accountGamble(){
//		try{
//			List<FactionWarGambleInfo> list = GameContext.getBaseDAO().selectList(FactionWarGambleInfo.class, "", "");
//			if(Util.isEmpty(list)){
//				return;
//			}
//			
//			//排序
//			Collections.sort(list, new Comparator<FactionWarGambleInfo>(){
//				@Override
//				public int compare(FactionWarGambleInfo config1, FactionWarGambleInfo config2) {
//					if(config1.getMoney() < config2.getMoney()){
//						return -1 ;
//					}
//					if(config1.getMoney() > config2.getMoney()){
//						return 1 ;
//					}
//					return 0;
//				}
//			});
//			
//			this.leastFactionId = list.get(0).getFactionId();
//			int minMoney = list.get(0).getMoney();
//			int size = Util.getSubListSize(list.size(), RANK_SIZE);
//			List<FactionWarGambleInfo> factionGambleList = list.subList(0, size);
//			
//			int totalMoney = this.factionWarConfig.getGambleMoney();
//			for(FactionWarGambleInfo info : list){
//				if(null == info){
//					continue;
//				}
//				totalMoney += info.getMoney();
//			}
//			
//			for(FactionWarGambleInfo info : factionGambleList){
//				if(info.getFactionId().equals(this.leastFactionId)){
//					continue;
//				}
//				int _money = info.getMoney();
//				if(_money == minMoney){
//					info.setMoney(_money += this.factionWarConfig.getSingleGambleMoney());
//				}
//			}
//			int sysAwardMoney = (int)(totalMoney * this.factionWarConfig.getGambleModulus());
//			double rate = sysAwardMoney/minMoney;
//			boolean lowRate = false;
//			//取2位，4舍5入
//			rate = Math.round(rate * 100);
//			rate = rate/100;
//			if(rate < 6){
//				lowRate = true;
//				rate = 6;
//			}
//			int count = 100;
//			int start = 0;
//			String mailTitle = GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_MAIL_TITLE);
//			String rankContent = this.getGambleRankStr(factionGambleList);
//			String winMailContent = this.getMailContent(rate, true) + rankContent;
//			String loseMailContent = this.getMailContent(rate, false) + rankContent;
//			while(true){
//				List<FactionWarGambleInfo> roleInfoList = GameContext.getBaseDAO().selectList(FactionWarGambleInfo.class, "start", start, "count", count);
//				
//				if(Util.isEmpty(roleInfoList)){
//					break;
//				}
//				for(FactionWarGambleInfo info : roleInfoList){
//					if(null == info){
//						continue;
//					}
//					if(info.getFactionId().equals(this.leastFactionId)){
//						int awardMoney = 0;
//						if(lowRate){
//							awardMoney = (int)(info.getMoney() * rate);
//						}else{
//							awardMoney = (int)((double)info.getMoney() / minMoney * sysAwardMoney);
//						}
//						sendGambleMail(info.getRoleId(), awardMoney, mailTitle, winMailContent);
//					}else{
//						sendGambleMail(info.getRoleId(), 0, mailTitle, loseMailContent);
//					}
//				}
//				
//				int listSize = roleInfoList.size();
//				if(listSize < count){
//					break;
//				}
//				start += count;
//			}
//			//发完奖删库
//			GameContext.getBaseDAO().deleteAll(FactionWarGambleInfo.class);
//			//支持最少的门派日志
//			FactionWarInfo factionWarInfo = this.factionWarInfoMap.get(this.leastFactionId);
//			if(null != factionWarInfo){
//				Log4jManager.FACTION_GAMBLE.info(GameContext.getServerId() + ":" +factionWarInfo.getFactionName());
//			}
//		}catch(Exception e){
//			logger.error("",e);
//		}
//	}
//	
//	private String getMailContent(double rate, boolean flag){
//		FactionWarInfo factionWarInfo = this.factionWarInfoMap.get(this.leastFactionId);
//		if(null == factionWarInfo){
//			return null;
//		}
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append(GameContext.getI18n().messageFormat(TextId.FACTION_WAR_GAMBLE_FACTION,factionWarInfo.getFactionName())).append("\n");
//		if(flag){
//			sb.append(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_WIN)).append("\n");
//		}else{
//			sb.append(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_LOSE)).append("\n");
//		}
//		sb.append(GameContext.getI18n().messageFormat(TextId.FACTION_WAR_GAMBLE_GIVE, rate)).append("\n");
//		return sb.toString();
//	}
//	
//	private String getGambleRankStr(List<FactionWarGambleInfo> list){
//		StringBuffer sb = new StringBuffer();
//		for(FactionWarGambleInfo info : list){
//			FactionWarInfo factionWarInfo = this.factionWarInfoMap.get(info.getFactionId());
//			if(null == factionWarInfo){
//				return null;
//			}
//			sb.append(GameContext.getI18n().messageFormat(TextId.FACTION_WAR_GAMBLE_RANK,factionWarInfo.getFactionName(), info.getMoney())).append("\n");
//		}
//		return sb.toString();
//	}
//	
//	private void sendGambleMail(String roleId, int silverMoney, String title, String content){
//		OutputConsumeType ocType = OutputConsumeType.faction_war_gamble_reward;
//		try {
//			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
//			mail.setSendRole(MailSendRoleType.System.getName());
//			mail.setTitle(title);
//			mail.setContent(content);
//			mail.setRoleId(roleId);
//			mail.setSendSource(ocType.getType());
//			mail.setSilverMoney(silverMoney);
//			GameContext.getMailApp().sendMail(mail);
//		}catch(Exception e){
//			logger.error("sendGambleMail error",e);
//		}
//	}
//	
//	@Override
//	public C1745_FactionWarGambleInfoRespMessage getGambleInfo(RoleInstance role, String factionId){
//		C1745_FactionWarGambleInfoRespMessage resp = new C1745_FactionWarGambleInfoRespMessage();
//		try{
//			int roleLevel = role.getLevel();
//			FactionWarGambleConfig config = this.factionWarGambleMap.get(roleLevel);
//			if(null == config){
//				C0003_TipNotifyMessage message = new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_CAN_NOT));
//				role.getBehavior().sendMessage(message);
//				return null;
//			}
//			Faction faction = GameContext.getFactionApp().getFaction(factionId);
//			if(null == faction){
//				C0003_TipNotifyMessage message = new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.FACTION_WAR_GAMBLE_FACTION_NOT_EXIST));
//				role.getBehavior().sendMessage(message);
//				return null;
//			}
//			resp.setFactionId(factionId);
//			resp.setFactionName(faction.getFactionName());
//			resp.setFactionLevel(faction.getFactionLevel());
//			resp.setFactionCamp(faction.getFactionCamp());
//			resp.setCurNum((byte)faction.getMemberNum());
//			resp.setMaxNum((byte)faction.getMaxMemberNum());
//			resp.setGambleMoneyOne(config.getMoney1());
//			resp.setGambleMoneyTwo(config.getMoney2());
//			resp.setGambleMoneyThree(config.getMoney3());
//			resp.setRate(this.factionWarConfig.getMaxGambleRate());
//		}catch(Exception e){
//			logger.error("",e);
//		}
//		return resp;
//	}
//	
//	private void broadCastEnd(FactionWarMatch match, String winFactionId, String loseFactionId){
//		try{
//			FactionWarBoradcastConfig config = this.broadcastMap.get(match.getRounds());
//			if(null == config){
//				return;
//			}
//			String msg = config.getEndMessage();
//			if(Util.isEmpty(msg)){
//				return;
//			}
//			String winFactionName = this.getFactionName(winFactionId);
//			String loseFactionName = this.getFactionName(loseFactionId);
//			String str = MessageFormat.format(msg, winFactionName, loseFactionName);
//			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.World, str, null, null);
//		}catch(Exception e){
//			logger.error("broadCastEnd error:",e);
//		}
//	}
//	
//	@Override
//	public void broadCastBegin(FactionWarMatch match) {
//		try{
//			FactionWarBoradcastConfig config = this.broadcastMap.get(match.getRounds());
//			if(null == config){
//				return;
//			}
//			String msg = config.getBeginMessage();
//			if(Util.isEmpty(msg)){
//				return;
//			}
//			String winFactionName = this.getFactionName(match.getFaction1().getFactionId());
//			String loseFactionName = this.getFactionName(match.getFaction2().getFactionId());
//			String str = MessageFormat.format(msg, winFactionName, loseFactionName);
//			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.World, str, null, null);
//		}catch(Exception e){
//			logger.error("broadCastBegin error:",e);
//		}
//	}
//	
//	private String getFactionName(String factionId){
//		FactionWarInfo info = this.factionWarInfoMap.get(factionId);
//		if(null == info){
//			return "";
//		}
//		return info.getFactionName();
//	}
//	
//	@Override
//	public void notifyEnterFactionWar(FactionWarMatch match, String mapInstanceId){
//		try{
//			String factionId1 = match.getFaction1().getFactionId();
//			String factionId2 = match.getFaction2().getFactionId();
//			
//			if(!Util.isEmpty(factionId1)){
//				List<FactionRole> list = GameContext.getFactionApp().getFactionRoleList(factionId1);
//				if(!Util.isEmpty(list)){
//					sendEnterListMessage(list, mapInstanceId);
//				}
//			}
//			
//			if(!Util.isEmpty(factionId2)){
//				List<FactionRole> list = GameContext.getFactionApp().getFactionRoleList(factionId2);
//				if(!Util.isEmpty(list)){
//					sendEnterListMessage(list, mapInstanceId);
//				}
//			}
//		}catch(Exception e){
//			logger.error("notifyEnterFactionWar error",e);
//		}
//	}
//	
//	private void sendEnterListMessage(List<FactionRole> list, String mapInstanceId){
//		try {
//			for(FactionRole fr : list){
//				String roleId = String.valueOf(fr.getRoleId());
//				RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
//				if (null == role) {
//					return;
//				}
//				MapInstance mapInstance = role.getMapInstance();
//				if(null == mapInstance){
//					continue ;
//				}
//				if(mapInstance.getInstanceId().equals(mapInstanceId)){
//					//在当前地图,不通知
//					continue ;
//				}
//				sendEnterMessage(roleId);
//			}
//		}catch(Exception e){
//			logger.error("sendEnterListMessage error",e);
//		}
//	}
//	
//	private void sendEnterMessage(String roleId){
//		try {
//			RoleInstance role = GameContext.getOnlineCenter()
//					.getRoleInstanceByRoleId(roleId);
//			if (null == role) {
//				return;
//			}
//			if (null == message) {
//				message = new C0007_ConfirmationNotifyMessage();
//				message.setAffirmCmdId(FACTION_WAR_CONFIRM_CMD);
//				message.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_ENTER));
//				message.setTime((byte)DEFAULT_TIME);
//			}
//			role.getBehavior().sendMessage(message);
//		}catch(Exception e){
//			logger.error("sendEnterMessage error",e);
//		}
//	}
//	
//	@Override
//	public void refreshFactionSoul(FactionWarMatch match, MapInstance mapInstance){
//		try{
//			if(null == match){
//				return;
//			}
//			FactionWarInfo info1 = match.getFaction1();
//			if(null != info1){
//				refreshSoul(mapInstance, info1.getFactionId());
//			}
//			FactionWarInfo info2 = match.getFaction2();
//			if(null != info2){
//				refreshSoul(mapInstance, info2.getFactionId());
//			}
//		}catch(Exception e){
//			logger.error("refreshFactionSoul error",e);
//		}
//	}
//	
//	private void refreshSoul(MapInstance mapInstance, String factionId){
//		try{
//			if(Util.isEmpty(factionId)){
//				return;
//			}
//			FactionWarInfo info = this.factionWarInfoMap.get(factionId);
//			Faction faction = GameContext.getFactionApp().getFaction(factionId);
//			if(null == faction || null == info){
//				return;
//			}
//			FactionSoulRecord record = faction.getFactionSoulRecord();
//			if(null == record){
//				return;
//			}
//			FactionSoulFlyConfig factionSoulFlyConfig = GameContext.getFactionSoulApp().getFactionSoulFly(record.getSoulId(), record.getFlyNum());
//			if(null == factionSoulFlyConfig){
//				return;
//			}
//			
//			mapInstance.summonCreateNpc(info.getNpcBorn(), factionSoulFlyConfig.getResId());
//		}catch(Exception e){
//			logger.error("refreshSoul error",e);
//		}
//	}
//	
//	@Override
//	public List<NpcFunctionItem> getNpcFunction(RoleInstance role, NpcInstance npc) {
//		List<NpcFunctionItem> functionList = new ArrayList<NpcFunctionItem>();
//		if(!role.hasFaction()){
//			return functionList;
//		}
//		String factionId = role.getFactionId();
//		FactionWarInfo info = this.factionWarInfoMap.get(factionId);
//		if(null == info){
//			return functionList;
//		}
//		
//		String npcId1 = this.factionWarConfig.getFactionNpc1();
//		String npcId2 = this.factionWarConfig.getFactionNpc2();
//		if(!npcId1.equals(npc.getNpc().getNpcid()) && !npcId2.equals(npc.getNpc().getNpcid())){
//			return functionList;
//		}
//		List<FactionSoulInspireConfig> inspireList = GameContext.getFactionSoulApp().getSoulInspire(factionId);
//		if(Util.isEmpty(inspireList)){
//			return functionList;
//		}
//		
//		NpcFunctionItem item = null;
//		for(FactionSoulInspireConfig config : inspireList){
//			if(null == config){
//				continue;
//			}
//			item = new NpcFunctionItem();
//			item.setType(NpcFuncShowType.KeepParents.getType());
//			item.setTitle(config.getFunctionName());
//			item.setCommandId(FACTION_WAR_INSPIRE_CMD);
//			item.setParam(getInspireParam(config.getBuffId()));
//			functionList.add(item);
//		}
//		return functionList;
//	}
//	
//	private String getInspireParam(short buffId){
//		return FACTION_WAR_INSPIRE_BUFF_ID + "=" +  buffId;
//	}
//	
//	@Override
//	public Result factionWarInpire(RoleInstance role, String param) {
//		Result result = new Result();
//		try {
//			//验证参数，找到buff的信息
//			if(Util.isEmpty(param)){
//				return result.setInfo(Status.Sys_Param_Error.getTips());
//			}
//			Map<String,String> paramMap = Util.urlParamParser(param);
//			String paramBuffId = paramMap.get(FACTION_WAR_INSPIRE_BUFF_ID);
//			if(Util.isEmpty(paramBuffId)) {
//				return result.setInfo(Status.Sys_Param_Error.getTips());
//			}
//			
//			short buffId = Short.parseShort(paramBuffId);
//			if(buffId <= 0){
//				return result.setInfo(Status.Sys_Param_Error.getTips());
//			}
//			FactionSoulInspireType inspireType = GameContext.getFactionSoulApp().getInspireBuffType(buffId);
//			int roleBuffLevel = role.getBuffLevel(buffId);
//			if(inspireType == FactionSoulInspireType.Role){
//				//单加
//				int buffLevel = roleBuffLevel + 1;
//				FactionSoulInspireBuffConfig config = GameContext.getFactionSoulApp().getSoulInspireBuff(buffId, buffLevel);
//				if(null == config){
//					return result.setInfo(Status.Npc_Inspire_Max.getTips());
//				}
//				result = this.addRoleBuff(role, config, buffLevel);
//				if(!result.isSuccess()){
//					return result;
//				}
//			}
//			if(inspireType == FactionSoulInspireType.FactionRole){
//				//群加
//				int buffLevel = roleBuffLevel + 1;
//				FactionSoulInspireBuffConfig config = GameContext.getFactionSoulApp().getSoulInspireBuff(buffId, buffLevel);
//				if(null == config){
//					buffLevel = roleBuffLevel;
//					config = GameContext.getFactionSoulApp().getSoulInspireBuff(buffId, buffLevel);
//					if(null == config){
//						return result.setInfo(Status.Npc_Inspire_Max.getTips());
//					}
//				}
//				result = addFactionRoleBuff(role, config, buffLevel);
//				if(!result.isSuccess()){
//					return result;
//				}
//			}
//			return result.success();
//		}catch (Exception e) {
//			logger.error("factionWarInpire error",e);
//		}
//		return result;
//	}
//	
//	private Result addRoleBuff(RoleInstance role, FactionSoulInspireBuffConfig config, int buffLevel){
//		Result result = canInpire(role, config);
//		if(!result.isSuccess()){
//			return result;
//		}
//		int randNum = RandomUtil.randomInt(1,(int)ParasConstant.PERCENT_BASE_VALUE);
//		if(randNum > config.getRatio()){
//			result.failure();
//			return result.setInfo(config.getFailInfo());
//		}
//		FactionSoulInspireCostType type = FactionSoulInspireCostType.get(config.getCostType());
//		cost(role, type, config.getCostValue());
//		addBuff(role, config, buffLevel);
//		return result.success();
//	}
//	
//	private Result addFactionRoleBuff(RoleInstance role, FactionSoulInspireBuffConfig config, int buffLevel){
//		Result result = canInpire(role, config);
//		if(!result.isSuccess()){
//			return result;
//		}
//		int randNum = RandomUtil.randomInt(1,(int)ParasConstant.PERCENT_BASE_VALUE);
//		if(randNum > config.getRatio()){
//			result.failure();
//			return result.setInfo(config.getFailInfo());
//		}
//		FactionSoulInspireCostType type = FactionSoulInspireCostType.get(config.getCostType());
//		cost(role, type, config.getCostValue());
//		
//		MapInstance mapInstance = role.getMapInstance();
//		if(null == mapInstance){
//			result.failure();
//			return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_INPIRE_ERROR));
//		}
//		for(RoleInstance player : mapInstance.getRoleList()){
//			if(null == player){
//				continue;
//			}
//			if(!player.getFactionId().equals(role.getFactionId())){
//				continue;
//			}
//			addBuff(player, config, buffLevel);
//		}
//		return result.success();
//	}
//	
//	private void addBuff(RoleInstance role, FactionSoulInspireBuffConfig config, int buffLevel){
//		//增加buff
//		GameContext.getUserBuffApp().addBuffStat(role, role, config.getBuffId(), buffLevel);
//		//成功提示信息
//		String successInfo = config.getSuccessInfo();
//		if(!Util.isEmpty(successInfo)){
//			role.getBehavior().sendMessage(new C0003_TipNotifyMessage(successInfo));
//		}
//	}
//	
//	private void cost(RoleInstance role, FactionSoulInspireCostType type, int value){
//		AttributeType attrType = type.getAttrType();
//		if(FactionSoulInspireCostType.factionMoney != type){
//			GameContext.getUserAttributeApp().changeRoleMoney(role, attrType,
//					OperatorType.Decrease, value, OutputConsumeType.faction_war_inprie);
//			role.getBehavior().notifyAttribute();
//		}else{
//			GameContext.getFactionFuncApp().changeFactionMoney(role, OperatorType.Decrease, value, OutputConsumeType.faction_money_war_inpire);
//		}
//	}
//	
//	private Result canInpire(RoleInstance role, FactionSoulInspireBuffConfig config){
//		Result result = new Result();
//		if(config.getCostValue() <= 0){
//			return result.success();
//		}
//		MapInstance mapInstance = role.getMapInstance();
//		if(null == mapInstance){
//			return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_INPIRE_ERROR));
//		}
//		MapLogicType mapLogicType = mapInstance.getMap().getMapConfig().getMapLogicType();
//		if(mapLogicType != MapLogicType.factionWar){
//			return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_INPIRE_ERROR));
//		}
//		FactionSoulInspireCostType type = FactionSoulInspireCostType.get(config.getCostType());
//		if(null == type){
//			return result.setInfo(GameContext.getI18n().getText(TextId.Sys_Error));
//		}
//		if(FactionSoulInspireCostType.factionMoney == type){
//			Faction faction = GameContext.getFactionApp().getFaction(role);
//			if(null == faction){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_NOT_HAVE_FACTION));
//			}
//			if(!GameContext.getFactionApp().getPowerTypeSet(role).contains(FactionPowerType.FactionInpire)){
//				return result.setInfo(GameContext.getI18n().getText(TextId.FACTION_WAR_INPIRE_MONEY_NO_POSITION));
//			}
//			if(faction.getFactionMoney() < config.getCostValue()){
//				return result.setInfo(GameContext.getI18n().getText(TextId.Faction_Money_Not_Enough));
//			}
//			return result.success();
//		}
//		int costValue = config.getCostValue();
//		AttributeType attrType = type.getAttrType();
//		if(role.get(attrType) < costValue){
//			return result.setInfo(Status.Npc_Inspire_Cost_Not_Enough.getTips().replace(Wildcard.AttrType, attrType.getName()));
//		}
//		return result.success();
//	}
//	
//	@Override
//	public void setArgs(Object arg0) {
//		
//	}
//
//	@Override
//	public void stop() {
//		
//	}
//
//	public void setFactionDAO(FactionDAOImpl factionDAO) {
//		this.factionDAO = factionDAO;
//	}
//	
//	@Override
//	public void exitFaction(RoleInstance role){
//		try{
//			Faction faction = GameContext.getFactionApp().getFaction(role);
//			FactionRole factionRole = GameContext.getFactionApp().getFactionRole(role);
//			if(null == faction || null == factionRole){
//				return;
//			}
//			String factionId = faction.getFactionId();
//			FactionWarInfo info = factionWarInfoMap.get(factionId);
//			if(null == info){
//				return;
//			}
//			String roleId = role.getRoleId();
//			if(info.getFactionKilledMap().containsKey(role.getRoleId())){
//				info.getFactionKilledMap().remove(roleId);
//			}
//		}catch(Exception e){
//			logger.error("FactionWarApp.exitFaction error",e);
//		}
//	}
//}
