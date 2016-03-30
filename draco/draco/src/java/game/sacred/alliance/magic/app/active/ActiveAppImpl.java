package sacred.alliance.magic.app.active;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActiveBaseItem;
import com.game.draco.message.item.ActivePanelDetailBaseItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;
import com.game.draco.message.response.C2300_ActivePanelRespMessage;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.active.vo.ActiveLogInfo;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.ActiveStatus;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveAppImpl implements ActiveApp {

	private Map<Short,Active> activeMap = new LinkedHashMap<Short,Active>();
	private Map<ActiveType,Active> onlyOneActiveMap = new HashMap<ActiveType,Active>();
	private Map<Integer,ActiveSupport> activeAppsMap;
	private BaseDAO baseDAO;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
				baseItems.add(item);
			}catch(Exception ex){
				logger.error("activeId=" + active.getId(),ex);
			}
		}
		return baseItems;
	}
	
	@Override
	public C2301_ActivePanelDetailRespMessage obtainActiveDetail(RoleInstance role, short activeId) {
		Active active = this.getActive(activeId);
		if(null == active){
			return null;
		}
		ActiveSupport support = this.activeAppsMap.get(Integer.valueOf(active.getType()));
		if(null == support){
			return null;
		}
		support.checkReset(role, active);
		C2301_ActivePanelDetailRespMessage resp = support.getActiveDetail(role, active);
		return resp;
	}
	
	@Override
	public void stop() {
	}

	/*public Map<Integer, ActiveSupport> getActiveAppsMap() {
		if(null == activeAppsMap){
			return new HashMap<Integer,ActiveSupport>();
		}
		return activeAppsMap;
	}*/
	
	public ActiveSupport getActiveSupport(int activeType) {
		if(null == activeAppsMap){
			return null ;
		}
		return activeAppsMap.get(activeType);
	}

	public void setActiveAppsMap(Map<Integer, ActiveSupport> activeAppsMap) {
		this.activeAppsMap = activeAppsMap;
	}

	@Override
	public void loadRoleActive(RoleInstance role) {
		List<ActiveLogInfo> roleActives = baseDAO.selectList(ActiveLogInfo.class,"roleId",role.getRoleId());
		Map<Short,ActiveLogInfo> activeLogMap = role.getActiveLogMap();
		for(ActiveLogInfo activeLog : roleActives){
			if(null == activeLog){
				continue;
			}
			short activeId = activeLog.getActiveId();
			activeLogMap.put(activeId, activeLog);
		}
	}

	@Override
	public void saveRoleActive(RoleInstance role) {
		Map<Short,ActiveLogInfo> activeLogMap = role.getActiveLogMap();
		for(Map.Entry<Short,ActiveLogInfo> entry:activeLogMap.entrySet()){
			try{
				if(null == entry){
					continue;
				}
				short activeId = entry.getKey();
				ActiveLogInfo activeLog = entry.getValue();
				Active active = this.getActive(activeId);
				if(null == active || null == activeLog){
					baseDAO.delete(ActiveLogInfo.class, "roleId", role.getRoleId(), "activeId", activeId);
					continue;
				}
				baseDAO.saveOrUpdate(activeLog);
			}catch(Exception e){
				this.logger.error("save role active error:", e);
			}
		}
	}
	
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
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
	public Message createActivePanelListMsg(RoleInstance role, short activeId) {
		C2300_ActivePanelRespMessage resp = new C2300_ActivePanelRespMessage();
		List<ActiveBaseItem> actives = this.obtainActiveList(role);
		if(Util.isEmpty(actives)){
			return resp;
		}
		this.sortActives(actives);
		resp.setActives(actives);
		resp.setId(activeId);
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
		item.setDesc(active.getDesc());
		List<Integer> goodsList = active.getRewardGoodsList();
		if(Util.isEmpty(goodsList)){
			return  ;
		}
		List<GoodsLiteItem> rewardItems = new ArrayList<GoodsLiteItem>();
		for(int goodsId : goodsList){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb){
				continue ;
			}
			rewardItems.add(gb.getGoodsLiteItem());
		}
		item.setRewardItems(rewardItems);
	}
	
}
