package sacred.alliance.magic.app.arena.top;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.push.C2362_ActiveDpsRewardNotifyMessage;
import com.game.draco.message.request.C3862_ArenaTopEnterReqMessage;

public class ArenaTopAppImpl implements ArenaTopApp,Service{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//private final static String SelectForArenaTop = "selectForArenaTop" ;
	private final static int TODAY_BATTER_NUM = 0 ;
	
	private static final short ENTER_ARENA_TOP_MAP_CMDID = new C3862_ArenaTopEnterReqMessage().getCommandId() ;
	private TopMapConfig topMapConfig ;
	private List<Point> points  ;
	private Map<Integer,List<TopRewardConfig>> rewardMap = new HashMap<Integer,List<TopRewardConfig>>();
	private Active active = null ;
	private Set<String> racerSet = null ;
	private Object ininRacerLock = new byte[0] ;
	private List<Integer> activeWeeks = new ArrayList<Integer>();
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	public Result canJoin(RoleInstance role){
		Result result = new Result();
		if(!active.isTimeOpen() || !active.isSuitLevel(role)){
			result.setInfo(this.getText(TextId.ARENA_TOP_ACTIVE_NOT_OPEN));
			return result ;
		}
		//判断是否在参赛者名单中
		this.initRacer();
		boolean in = (null != this.racerSet && this.racerSet.contains(role.getRoleId()));
		if(!in){
			result.setInfo(this.getText(TextId.ARENA_TOP_CANOT_JOIN));
			return result ;
		}
		result.success();
		return result ;
	}
	
	@Override
	public void cleanRacers(){
		//清空参赛者列表,以便于下次活动开启重新加载
		this.racerSet = null ;
	}
	
	@Override
	public void racersMailAlert() {
		try {
			if (null == this.active) {
				return;
			}
			// 判断大师赛活动今天是否开启
			if (!this.active.isDayNowActive()) {
				return;
			}
			this.cleanRacers();
			this.initRacer();
			for (String roleId : this.racerSet) {
				//发送邮件提醒
				Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
				mail.setTitle(this.getText(TextId.ARENA_TOP_RACER_MAIL_TITLE));
				mail.setSendRole(this.getText(TextId.ARENA_TOP_NAME ));
				mail.setContent(this.getText(TextId.ARENA_TOP_RACER_MAIL_CONTEXT));
				mail.setRoleId(roleId);
				GameContext.getMailApp().sendMail(mail);
			}
		}catch(Exception ex){
			logger.error("racersMailAlert error",ex);
		}
	}
	
	public void initRacer(){
		if(null != racerSet){
			return ;
		}
		// 初始化参赛者名单
		synchronized (ininRacerLock) {
			if(null != racerSet){
				return ;
			}
			this.doInitRacer() ;
		}
	}
	
	private void doInitRacer(){
		Set<String> set = new HashSet<String>();
		//两部分角色有参赛资格
		//1.当天1v1进入前10名者
		/*List<Arena1V1History> list1 = GameContext.getBaseDAO().selectList(Arena1V1History.class, SelectForArenaTop);
		if(!Util.isEmpty(list1)){
			for(Arena1V1History it : list1){
				set.add(it.getRoleId());
			}
		}*/
		//2.当前赛季积累总份前30名
		Date date = this.getPreOpenDay(new Date());
		date = DateUtil.addDayToDate(date, 1);
		String dateStr = DateUtil.date2FormatDate(date, "yyyy-MM-dd HH:mm:ss");
		int selectNum = this.getTopMapConfig().getMaxRoleNum()-TODAY_BATTER_NUM ;
		List<RoleArena> list2 = GameContext.getBaseDAO().selectListByParms(RoleArena.class, "", dateStr, String.valueOf(selectNum),"", "", "", "");
		if(!Util.isEmpty(list2)){
			for(RoleArena it : list2){
				set.add(it.getRoleId());
			}
		}
		this.racerSet = set ;
	}

	public Date getNextOpenDay(Date now){
		Calendar calendar = Calendar.getInstance();  
        calendar.setTime(now);  
        calendar.set(Calendar.HOUR_OF_DAY, 0);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        calendar.set(Calendar.MILLISECOND, 0);  
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int max = this.activeWeeks.size()-1 ;
		for(int i=0;i <= max;i++){
			int w = activeWeeks.get(i);
			if( w >= week){
				return DateUtil.getWeekDay(calendar.getTime(), 0, w);
			}
		}
		if(max < 0){
			return DateUtil.getWeekDay(calendar.getTime(), 1, 1);
		}
		//后一周的最前
		return DateUtil.getWeekDay(calendar.getTime(), 1, this.activeWeeks.get(0));
	}
	
	private Date getPreOpenDay(Date now){
		Calendar calendar = Calendar.getInstance();  
        calendar.setTime(now);  
        calendar.set(Calendar.HOUR_OF_DAY, 0);  
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
        calendar.set(Calendar.MILLISECOND, 0);  
        
        int week = calendar.get(Calendar.DAY_OF_WEEK);
		int max = this.activeWeeks.size()-1 ;
		for(int i= max ;i>=0;i--){
			int w = this.activeWeeks.get(i);
			if(week > w){
				//本周的星期几
				return DateUtil.getWeekDay(calendar.getTime(), 0, w);
			}
		}
		if(max < 0){
			return DateUtil.getWeekDay(calendar.getTime(), 1, 1);
		}
		//上周的最后一个日期
		return DateUtil.getWeekDay(calendar.getTime(), -1, this.activeWeeks.get(max));
	}

	
	@Override
	public void resetArenaTopScore(RoleArena roleArena){
		Date now = new Date();
		Date topDate = roleArena.getTopDate() ;
		if(null == topDate){
			roleArena.setTopDate(now);
			return ;
		}
		if(DateUtil.sameDay(this.getNextOpenDay(now),this.getNextOpenDay(topDate))){
			//同一周期
			return ;
		}
		//重置
		roleArena.setTopDate(now);
		roleArena.setTopScore(0);
	}
	
	
	
	@Override
	public void sendReward(String roleId,String roleName,int level,int rank,int num){
		//！！！！ 本语句一定不能注释
		//清空参赛者列表,以便于下次活动开启重新加载
		this.cleanRacers();
		try {
			TopRewardConfig rewardConfig =this.getTopRewardConfig(level, rank);
			if (null == rewardConfig) {
				return;
			}
			String context = GameContext.getI18n().messageFormat(TextId.ARENA_TOP_MAIL_CONTEXT,roleName,rank);
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setTitle(this.getText(TextId.ARENA_TOP_MAIL_TITILE));
			mail.setSendRole(this.getText(TextId.ARENA_TOP_NAME ));
			mail.setContent(context);
			mail.setRoleId(roleId);
			mail.setExp(rewardConfig.getExp());
			mail.setSilverMoney(rewardConfig.getGameMoney());
			mail.setSendSource(OutputConsumeType.arena_Top_Reward_Mail.getType());
			for (GoodsOperateBean bean : rewardConfig.getAddGoods()) {
				if (null == bean) {
					continue;
				}
				mail.addMailAccessory(bean.getGoodsId(), bean.getGoodsNum(),
						bean.getBindType());
			}
			//调用者为地图主循环，异步发送邮件
			GameContext.getMailApp().sendMailAsync(mail);
			
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			if(null != role){
				//活动结束，显示奖励面板
				C2362_ActiveDpsRewardNotifyMessage message = new C2362_ActiveDpsRewardNotifyMessage();
				message.setIndex((short) rank);
				message.setDepValue(num);
				message.setAttrList(rewardConfig.getShowAttrList());
				message.setGoodsList(rewardConfig.getShowGoodsList());
				role.getBehavior().sendMessage(message);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
	}
	
	
	private TopRewardConfig getTopRewardConfig(int level, int rank){
		return this.getTopRewardConfig(this.rewardMap.get(level), rank);
	}
	
	@Override
	public Active getActive() {
		return active ;
	}
	
	@Override
	public TopMapConfig getTopMapConfig(){
		return topMapConfig ;
	}
	
	@Override
	public Point safePoint() {
		//已经判断points一定会有数据
		//随机一个点
		return this.points.get(RandomUtil.randomInt(points.size()));
	}

	@Override
	public List<Short> getEnterMapBuffList() {
		if(null == topMapConfig){
			return null ;
		}
		return topMapConfig.getBuffList() ;
	}
	
	private void loadTopMapConfig(){
		String fileName = XlsSheetNameType.arena_top_config.getXlsName();
		String sheetName = XlsSheetNameType.arena_top_config.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TopMapConfig> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TopMapConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile="+fileName +" sheetName="+sheetName);
			}
			TopMapConfig select = list.get(0);
			String mapId = select.getMapId() ;
			sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(mapId);
			if(null == map){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("map not exist,mapId:" + mapId +" sourceFile="+fileName +" sheetName="+sheetName);
				return ;
			}
			if(!map.getMapConfig().changeLogicType(MapLogicType.arenaTop)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("change map:" + mapId + " to areanTop fail");
				return ;
			}
			//初始化
			select.init();
			this.topMapConfig = select ;
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile=" + fileName + "sheetName=" + sheetName, ex);
		}
	}
	
	private void loadPoints(){
		String fileName = XlsSheetNameType.arean_top_enter_points.getXlsName();
		String sheetName = XlsSheetNameType.arean_top_enter_points.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<Point> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, Point.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile="+fileName +" sheetName="+sheetName);
				return ;
			}
			String mapId = this.topMapConfig.getMapId() ;
			for(Point point : list){
				point.setMapid(mapId);
			}
			this.points = list ;
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile=" + fileName + "sheetName=" + sheetName, ex);
		}
	}
	
	
	
	private void loadReward(){
		String fileName = XlsSheetNameType.arean_top_reward.getXlsName();
		String sheetName = XlsSheetNameType.arean_top_reward.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TopRewardConfig> configList = XlsPojoUtil.sheetToList(sourceFile, sheetName, TopRewardConfig.class);
			//排序
			Collections.sort(configList, new Comparator<TopRewardConfig>(){
				@Override
				public int compare(TopRewardConfig config1, TopRewardConfig config2) {
					if(config1.getMinLevel() > config2.getMinLevel()){
						return 1 ;
					}
					if(config1.getMinLevel() < config2.getMinLevel()){
						return -1 ;
					}
					if(config1.getMaxLevel() > config2.getMaxLevel()){
						return 1 ;
					}
					if(config1.getMaxLevel() < config2.getMaxLevel()){
						return -1 ;
					}
					if(config1.getStartRank() > config2.getStartRank()){
						return 1 ;
					}
					if(config1.getStartRank() < config2.getStartRank()){
						return -1 ;
					}
					if(config1.getEndRank() > config2.getEndRank()){
						return 1 ;
					}
					if(config1.getEndRank() < config2.getEndRank()){
						return -1 ;
					}
					return 0;
				}
			});
			int maxRank = 0 ;
			Map<Integer,List<TopRewardConfig>> map = new HashMap<Integer,List<TopRewardConfig>>();
			for(TopRewardConfig config : configList){
				maxRank = Math.max(maxRank, config.getEndRank());
				config.init();
				List<TopRewardConfig> rewardList = map.get(config.getMinLevel());
				if(null == rewardList){
					rewardList = new ArrayList<TopRewardConfig>();
					for(int i=config.getMinLevel();i<=config.getMaxLevel();i++){
						map.put(i, rewardList);
					}
				}
				rewardList.add(config);
			}
			this.rewardMap = map ;
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
		}
	}
	
	
	private TopRewardConfig getTopRewardConfig(List<TopRewardConfig> configList, int rank){
		if(Util.isEmpty(configList) || rank <=0){
			return null ;
		}
		for(TopRewardConfig config : configList){
			if(rank >= config.getStartRank() && rank <= config.getEndRank() ){
				return config ;
			}
		}
		return null ;
	}
	
	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		this.loadTopMapConfig() ;
		this.loadPoints() ;
		this.loadReward() ;
		Active active = GameContext.getActiveApp().getOnlyOneActive(ActiveType.ArenaTop);
		if(null == active){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not config the active for activeType:ActiveType.ArenaTop");
			return ;
		}
		List<Integer> weeks = new ArrayList<Integer>();
		for(String s : active.getWeekTerm().split(Cat.comma)){
			Integer v = Integer.parseInt(s) ;
			if(weeks.contains(v)){
				continue ;
			}
			weeks.add(v);
		}
		if(Util.isEmpty(weeks)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("active weekterms not config,activeId=" + active.getId());
			return ;
		}
		//从小到大排序
		Collections.sort(weeks, new Comparator<Integer>(){
			@Override
			public int compare(Integer a, Integer b) {
				return a >= b ? 1 :-1 ;
			}
		});
		this.activeWeeks = weeks ;
		this.active = active ;
	}

	@Override
	public void stop() {
	}


	@Override
	public List<NpcFunctionItem> getNpcFunction(RoleInstance role,
			NpcInstance npc) {
		if(null == this.active){
			return null ;
		}
		String npcId = active.getNpcId(role.getCampId());
		if(Util.isEmpty(npcId) || !npc.getNpc().getNpcid().equals(npcId)){
			return null ;
		}
		if(!active.isSuitLevel(role) || !active.isTimeOpen()){
			return null ;
		}
		List<NpcFunctionItem> list = new ArrayList<NpcFunctionItem>();
		NpcFunctionItem item = new NpcFunctionItem();
		item.setTitle(active.getName());
		item.setCommandId(ENTER_ARENA_TOP_MAP_CMDID);
		item.setParam("");
		list.add(item);
		return list;
	}

}
