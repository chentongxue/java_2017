package sacred.alliance.magic.app.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.message.item.ActiveBaseItem;
import com.game.draco.message.item.ActivePanelDetailBaseItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C2300_ActivePanelRespMessage;
import com.google.common.collect.Sets;

public class ActiveAppImpl implements ActiveApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Short,Active> activeMap = new LinkedHashMap<Short,Active>();
	private Map<ActiveType,Active> onlyOneActiveMap = new HashMap<ActiveType,Active>();
	private Map<Byte,ActiveSupport> activeAppsMap;

	
	@Override
	public void setArgs(Object obj) {
	}

	@Override
	public void start() {
		this.loadActiveData();
	}
	
	/**
	 * 加载活动总表的配置
	 */
	private void loadActiveData() {
		String fileName = XlsSheetNameType.active_total.getXlsName();
		String sheetName = XlsSheetNameType.active_total.getSheetName();
		String error = "load excel error:fileName=" + fileName + ",sheetName="
				+ sheetName + ".";
		try {
			String pathName = GameContext.getPathConfig().getXlsPath();
			Map<String, Active> map = XlsPojoUtil.sheetToLinkedMap(pathName
					+ fileName, sheetName, Active.class);
			// init上下文
			ActiveInitContext initContext = new ActiveInitContext();

			for (Active active : map.values()) {
				if (null == active) {
					continue;
				}
				short activeId = active.getId();
				// 活动ID小于等于0
				if (activeId <= 0) {
					Log4jManager.CHECK.error(error + "id=" + activeId
							+ ".Active id config error.");
					Log4jManager.checkFail();
					continue;
				}
				byte type = active.getType();
				// 活动类型配置错误
				ActiveType activeType = ActiveType.get(type);
				if (null == activeType) {
					Log4jManager.CHECK.error(error + "id=" + activeId
							+ ",type=" + type + ".The type is not exist.");
					Log4jManager.checkFail();
					continue;
				}
				// 系统不支持的活动，无须加载
				if (!activeType.isUsable()) {
					Log4jManager.CHECK.error(error + "id=" + activeId
							+ ",type=" + type
							+ ".This type of active is disabled!");
					Log4jManager.checkFail();
					continue;
				}
				// 是唯一性的活动，配置了多个
				if (activeType.isOnlyone()
						&& initContext.getActiveTypeSet().contains(type)) {
					Log4jManager.CHECK
							.error(error
									+ "id="
									+ activeId
									+ ",type="
									+ type
									+ ".The active of this type is onlyone. Can't config More than one");
					Log4jManager.checkFail();
					continue;
				}
				if(activeType.isOnlyone()){
					onlyOneActiveMap.put(activeType, active);
				}
				// 检测并初始化活动配置
				Result result = active.checkInit(initContext);
				if (!result.isSuccess()) {
					Log4jManager.CHECK.error(error + result.getInfo());
					Log4jManager.checkFail();
					continue;
				}
				// 将活动类型缓存到验证集合中
				initContext.getActiveTypeSet().add(type);

				this.activeMap.put(activeId, active);
			}
		} catch (Exception e) {
			Log4jManager.CHECK.error(error);
			Log4jManager.checkFail();
		}
	}

	@Override
	public List<ActiveBaseItem> obtainActiveList(RoleInstance role) {
		List<ActiveBaseItem> baseItems = new ArrayList<ActiveBaseItem>();
		for(Active active : this.activeMap.values()){
			try {
				if (null == active || !active.canDisplay(role)) {
					continue;
				}
				ActiveStatus status = active.getStatus(role);
				if(ActiveStatus.Miss == status){
					continue;
				}
				ActiveBaseItem item = new ActiveBaseItem();
				item.setId(active.getId());
				item.setName(active.getName());
				item.setStatus(status.getType());
				item.setTime(active.getTimeRegions().toString());
				item.setLevel(active.getMinLevel());
				item.setType(active.getType());
				item.setHint(active.getActiveHint(role));
				item.setImageId(active.getIconId());
				item.setActiveType(active.getTimeLimit());
				item.setActiveDesc(active.getDesc());
				item.setShowLevel(active.getShowLevel());
				baseItems.add(item);
			}catch(Exception ex){
				logger.error("activeId=" + active.getId(),ex);
			}
		}
		return baseItems;
	}
	
	@Override
	public Message obtainActiveDetail(RoleInstance role, short activeId) {
		Active active = this.getActive(activeId);
		if(null == active){
			return null;
		}
		ActiveSupport support = this.getActiveSupport(active.getType());
		if(null == support){
			return null;
		}
		support.checkReset(role, active);
		//需要判断当前是否已经开启
		ActiveStatus status = active.getStatus(role);
		if(ActiveStatus.Miss == status){
			return null ;
		}
		if(ActiveStatus.NotOpen == status){
			return new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.Active_Not_Open)) ;
		}
		return support.getActiveDetail(role, active);
	}
	
	@Override
	public void stop() {
	}

	
	public ActiveSupport getActiveSupport(byte activeType) {
		if(null == activeAppsMap){
			return null ;
		}
		return activeAppsMap.get(activeType);
	}

	public void setActiveAppsMap(Map<Byte, ActiveSupport> activeAppsMap) {
		this.activeAppsMap = activeAppsMap;
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		return 1 ;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}
	
	@Override
	public Active getActive(short activeId) {
		return activeMap.get(activeId);
	}
	
	@Override
	public Active getOnlyOneActive(ActiveType activeType){
		if(null == activeType){
			return null ;
		}
		return this.onlyOneActiveMap.get(activeType);
	}

	@Override
	public Message createActivePanelListMsg(RoleInstance role) {
		C2300_ActivePanelRespMessage resp = new C2300_ActivePanelRespMessage();
		List<ActiveBaseItem> actives = this.obtainActiveList(role);
		if(Util.isEmpty(actives)){
			return resp;
		}
		this.sortActives(actives);
		resp.setActives(actives);
		return resp;
	}
	
	private void sortActives(List<ActiveBaseItem> activeList){
		Collections.sort(activeList, new Comparator<ActiveBaseItem>() {
			public int compare(ActiveBaseItem item1, ActiveBaseItem item2) {
				ActiveStatus status1 = ActiveStatus.getActiveStatus(item1.getStatus());
				ActiveStatus status2 = ActiveStatus.getActiveStatus(item2.getStatus());
				if(status1 == null || status2 == null){
					return 0;
				}
				if(status1.getSortValue() < status2.getSortValue()){
					return -1;
				}
				if(status1.getSortValue() > status2.getSortValue()){
					return 1;
				}
				return 0;
			}
		});
	}

	@Override
	public Collection<Active> getAllActive() {
		return this.activeMap.values();
	}

	@Override
	public void buildActivePanelDetailBaseItem(ActivePanelDetailBaseItem item, Active active) {
		item.setActiveId(active.getId());
	}

	@Override
	public Set<HintType> getHintTypeSet(RoleInstance role) {
		try {
			if (this.hasHint(role)) {
				Set<HintType> set = Sets.newHashSet();
				set.add(HintType.active);
				return set;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 是否有需要红点提示的活动
	 * @param role
	 * @return
	 */
	@Override
	public boolean hasHint(RoleInstance role) {
		if (Util.isEmpty(this.activeMap)) {
			return false;
		}
		for (Active active : this.activeMap.values()) {
			if (null == active || !active.isSuitLevel(role)) {
				continue;
			}
			ActiveSupport support = this.getActiveSupport(active.getType());
			if (null != support && support.getActiveHint(role, active)) {
				return true;
			}
		}
		return false;
	}
	
}
