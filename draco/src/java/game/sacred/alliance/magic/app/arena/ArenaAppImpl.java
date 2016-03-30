package sacred.alliance.magic.app.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.module.cache.SimpleCache;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0054_ArenaMatchConfirmTimeoverMessage;
import com.game.draco.message.internal.C0055_ArenaKeepConfirmTimeoverMessage;

public class ArenaAppImpl implements ArenaApp,Service{
	private static final Logger logger = LoggerFactory.getLogger(ArenaAppImpl.class);
	private static final Arena ARENA_EMPTY = new ArenaEmpty();
	private Map<String,ArenaConfig> allConfigMap = new HashMap<String,ArenaConfig>();
	private Map<Integer,ArenaConfig>  arenaType_ConfigMap = new HashMap<Integer,ArenaConfig>();
	
	private Map<String,ApplyInfo> allRoleApplyInfo = new ConcurrentHashMap<String, ApplyInfo>();
	private Map<String,ArenaMatch> allArenaMatch = new ConcurrentHashMap<String, ArenaMatch>();
	private Map<String,Arena> allActiveArena = new HashMap<String,Arena>();
	
	private Cache<String, ArenaMatch> matchCache = null ;
	private AtomicBoolean watch = new AtomicBoolean(false);
	private long preWatchTime = System.currentTimeMillis();
	private long watchArenaMatchInterval = 1*60*1000 ; //1分钟
	
	
	private void watchArenaMatch(){
		if(!watch.compareAndSet(false, true)){
			return ;
		}
		long now = System.currentTimeMillis();
		if(now-this.preWatchTime < watchArenaMatchInterval){
			watch.set(false);
			return ;
		}
		int size = this.allArenaMatch.size();
		try {
			if (size > 0) {
				List<ArenaMatch> list = new ArrayList<ArenaMatch>();
				list.addAll(allArenaMatch.values());
				for (ArenaMatch match : list) {
					try {
						if (ArenaMatchStatus.destory == match.getStatus()) {
							continue;
						}
						if (match.isTimeout()) {
							match.cancelAll();
						}
					} catch (Exception ex) {

					}
				}
			}
		}finally{
			this.preWatchTime = now ;
			watch.set(false);
		}
		logger.info("arenaMatch size=" + size + " time=" + (System.currentTimeMillis()-now));
	}
	
	@Override
	public ApplyInfo getApplyInfo(String roleId) {
		if(null == roleId){
			return null ;
		}
		return this.allRoleApplyInfo.get(roleId);
	}

	public Arena getArena(int activeId){
		Arena arena = allActiveArena.get(String.valueOf(activeId));
		if(null != arena){
			return arena;
		}
		return ARENA_EMPTY ;
	}
	
	@Override
	public ArenaResult apply(RoleInstance role,int activeId) {
		return this.getArena(activeId).apply(role);
	}
	
	@Override
	public ArenaResult applyCancel(RoleInstance role,int activeId) {
		return this.getArena(activeId).applyCancel(role);
	}

	@Override
	public ArenaResult applyKeep(String roleId, String selected) {
		ArenaResult result = new ArenaResult();
		ApplyInfo info = this.getApplyInfo(roleId);
		if(null == info || null == info.getMatch()){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		int activeId = info.getActiveId();
		boolean select = false ;
		if(null == selected || selected.equals(SELECTED)){
			select = true ;
		}
		return this.getArena(activeId).applyKeep(roleId, select);
	}

	@Override
	public ArenaResult matchConfirm(String roleId, String selected) {
		ArenaResult result = new ArenaResult();
		ApplyInfo info = this.getApplyInfo(roleId);
		if(null == info){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return result ;
		}
		int activeId = info.getActiveId();
		boolean select = false ;
		if(null == selected || selected.equals(SELECTED)){
			select = true ;
		}
		return this.getArena(activeId).matchConfirm(roleId, select);
	}

	@Override
	public void setArgs(Object paramObject) {
		
	}
	
	
	
	
	private void loadAreanConfig(){
		//加载擂台赛配置
		String fileName = XlsSheetNameType.arena_list.getXlsName();
		String sheetName = XlsSheetNameType.arena_list.getSheetName();
		List<ArenaConfig> configList = null ;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			configList = XlsPojoUtil.sheetToList(sourceFile, sheetName,ArenaConfig.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName,ex);
		}

		if(null == configList){
			return ;
		}
		//检测配置的擂台赛类型是否存在
		//检测相关的活动是否已经配置
		this.allConfigMap.clear();
		this.arenaType_ConfigMap.clear();
		
		for(ArenaConfig config : configList){
			boolean thisOk = true ;
			ArenaType arenaType = ArenaType.get(config.getArenaType());
			if(null == arenaType){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("sourceFile="+ fileName +" sheetName="+sheetName
						+ " config error,arenaType=" + config.getArenaType() + " not exists");
				thisOk = false ;
			}
			//修改地图逻辑类型
			String mapId = config.getMapId() ;
			MapConfig mapConfig = GameContext.getMapApp().getMapConfig(mapId);
			if(null == mapConfig || !mapConfig.changeLogicType(arenaType.getMapLogicType())){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("map not exist or change map:" + mapId + " to " + arenaType.getMapLogicType() +" fail");
				thisOk = false ;
			}
			
			arenaType_ConfigMap.put(config.getArenaType(), config);
			
			String activeKey = String.valueOf(config.getActiveId());
			//切磋配置
			if(arenaType == ArenaType._LEARN){
				//初始化
				config.init();
				allConfigMap.put(activeKey, config);
				continue ;
			}
			
			//判断活动是否存在
			Active active = GameContext.getActiveApp().getActive(Short.valueOf(activeKey));
			if(null == active){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("sourceFile="+ fileName +" sheetName="+sheetName
						+ " config error,activeId=" + config.getActiveId() + " not exists");
				thisOk = false ;
			}
			
			if(allConfigMap.containsKey(activeKey)){
				//active重复配置
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("sourceFile="+ fileName +" sheetName="+sheetName
						+ " config error,activeId=" + config.getActiveId() + " duplicat config");
				thisOk = false ;
			}
			//初始化
			config.init();
			allConfigMap.put(activeKey, config);
			//构建擂台赛逻辑实例
			try {
				if (thisOk) {
					Arena arena = arenaType.createArena();
					arena.active = active;
					arena.config = config;
					arena.manager = this;
					//!!!统一在外面启动
					//arena.start();
					allActiveArena.put(activeKey, arena);
				}
			}catch(Exception ex){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("create arena error,activeId=" + config.getActiveId(),ex);
			}
		}
		configList.clear();
		configList = null ;
	}
	
	

	@Override
	public void start() {
		this.loadAreanConfig();
		this.initCache();
		//启动Arena
		for(Arena arena : allActiveArena.values()){
			arena.start();
		}
	}
	
	private void initCache(){
		this.matchCache = new SimpleCache<String,ArenaMatch>();
		//默认超时时间是10s
		this.matchCache.setTimeToLiveMillisecond(DEFAULT_MATCH_CONFIRM_MILLIS_TIME);
		this.matchCache.addCacheListener(new CacheListener<String,ArenaMatch>(){
			@Override
			public void entryAccessed(
					CacheEvent<String, ArenaMatch> paramCacheEvent) {
				
			}

			@Override
			public void entryAdded(
					CacheEvent<String, ArenaMatch> event) {
			}

			@Override
			public void entryCleared(
					CacheEvent<String, ArenaMatch> event) {
			}

			@Override
			public void entryExpired(
					CacheEvent<String, ArenaMatch> event) {
			}

			@Override
			public void entryRemoved(
					CacheEvent<String, ArenaMatch> event) {
				//超时方法
				ArenaMatch match = event.getValue();
				if(null == match){
					return ;
				}
				doTimeoutListener(event.getKey(),match);
			}

			@Override
			public void entryUpdated(
					CacheEvent<String, ArenaMatch> event) {
			}
		});
		this.matchCache.start();
	}
	
	private static final String MATCH_TIME_OUT_PREFIX = "0_" ;
	private static final String KEEP_TIME_OUT_PREFIX = "1_" ;
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	private void doTimeoutListener(String key,ArenaMatch match){
		if(null == key){
			return ;
		}
		if(key.startsWith(MATCH_TIME_OUT_PREFIX)){
			//match
			C0054_ArenaMatchConfirmTimeoverMessage reqMsg = new C0054_ArenaMatchConfirmTimeoverMessage();
			reqMsg.setMatch(match);
			GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
			return ;
		}
		if(key.startsWith(KEEP_TIME_OUT_PREFIX)){
			//keep
			C0055_ArenaKeepConfirmTimeoverMessage reqMsg = new C0055_ArenaKeepConfirmTimeoverMessage();
			reqMsg.setMatch(match);
			GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
			return ;
		}
	}
	
	@Override
	public void addMatchTimeoutListener(ArenaMatch match){
		if(null == match){
			return ;
		}
		this.matchCache.put(MATCH_TIME_OUT_PREFIX + match.getKey(), match);
	}
	
	@Override
	public void addKeepTimeoutListener(ArenaMatch match){
		if(null == match){
			return ;
		}
		this.matchCache.put(KEEP_TIME_OUT_PREFIX + match.getKey(), match);
	}

	@Override
	public void stop() {
		if(null != this.matchCache){
			this.matchCache.destroy();
		}
	}

	

	@Override
	public ArenaConfig getArenaConfig(String activeId) {
		if(null == activeId){
			return null ;
		}
		return allConfigMap.get(activeId);
	}
	
	@Override
	public ArenaConfig getArenaConfig(ArenaType arenaType){
		if(null == arenaType){
			return null ;
		}
		return this.arenaType_ConfigMap.get(arenaType.getType());
	}

	@Override
	public Map<String, ApplyInfo> getAllRoleApplyInfo() {
		return this.allRoleApplyInfo;
	}

	@Override
	public void removeApplyInfo(String roleId) {
		if(null == roleId){
			return ;
		}
		ApplyInfo info = this.allRoleApplyInfo.remove(roleId);
		//有可能此对象已经进入匹配列表,删除的情况下,必须将此设置为取消
		//否则有可能出现出现相同的人出现在同一匹配对象中
		if(null != info){
			info.setCancel(true);
		}
	}

	@Override
	public void systemMatch(int activeId) {
		this.getArena(activeId).systemMatch();
		this.watchArenaMatch();
	}
	
	@Override
	public void systemClose(int activeId) {
		this.getArena(activeId).systemClose();
		this.watchArenaMatch();
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		RoleArena ra = GameContext.getBaseDAO().selectEntity(RoleArena.class, RoleArena.ROLE_ID, role.getRoleId());
		if(null != ra){
			ra.check();
			ra.setOld(true);
			role.setRoleArena(ra);
			return 1;
		}
		ra = new RoleArena();
		ra.setRoleId(role.getRoleId());
		role.setRoleArena(ra);
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			RoleArena ra = role.getRoleArena();
			if(null == ra){
				return 1;
			}
			if(ra.isOld()){
				GameContext.getBaseDAO().update(ra);
				return 1;
			}
			GameContext.getBaseDAO().insert(ra);
			ra.setOld(true);
		} catch (Exception ex) {
			GameContext.getArenaApp().offlineLog(role);
			Log4jManager.OFFLINE_ERROR_LOG.error("arena error,roleId=" + role.getRoleId()
					+ ",userId=" + role.getUserId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void offlineLog(RoleInstance role) {
		try{
			RoleArena ra = role.getRoleArena();
			if(null == ra){
				return ;
			}
			StringBuffer sb = new StringBuffer();
			sb.append(ra.getRoleId());
			sb.append(Cat.pound);
			sb.append(ra.getWin1v1());
			sb.append(Cat.pound);
			sb.append(ra.getFail1v1());
			sb.append(Cat.pound);
			sb.append(ra.getCycleWin1v1());
			sb.append(Cat.pound);
			sb.append(ra.getCycleFail1v1());
			sb.append(Cat.pound);
			sb.append(DateUtil.getTimeByDate(ra.getCycleDate()));
			sb.append(Cat.pound);
			sb.append(ra.getJoin1v1());
			sb.append(Cat.pound);
			sb.append(ra.getCycleJoin1v1());
			Log4jManager.OFFLINE_ARENA_DB_LOG.info(sb.toString());
		}catch(Exception e){
			logger.error("logoutLog:",e);
		}
	}

	@Override
	public boolean canUseSkill(int skillId) {
		return true ;
	}
	
	@Override
	public boolean canUseGoods(int goodsId) {
		return true ;
	}
	

	@Override
	public boolean isApplyMap(String mapId) {
		return true ;
	}

	@Override
	public void addArenaMatch(ArenaMatch match) {
		if(null == match){
			return ;
		}
		this.allArenaMatch.put(match.getKey(), match);
	}

	@Override
	public void removeArenaMatch(String key) {
		if(null == key){
			return ;
		}
		this.allArenaMatch.remove(key);
	}

	
}
