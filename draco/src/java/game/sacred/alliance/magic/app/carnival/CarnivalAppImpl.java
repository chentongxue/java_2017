package sacred.alliance.magic.app.carnival;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.item.CarnivalActiveItem;
import com.game.draco.message.item.CarnivalRankItem;
import com.game.draco.message.response.C1187_CarnivalDetailRespMessage;
import com.game.draco.message.response.C1186_CarnivalRespMessage;

import sacred.alliance.magic.base.ActiveCarnivalStatus;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.SaveDbStateType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.dao.impl.CarnivalDAOImpl;
import sacred.alliance.magic.domain.CarnivalDbInfo;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class CarnivalAppImpl implements CarnivalApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private CarnivalDAOImpl carnivalDAO;
	//配置
	Map<Short, CarnivalActive> allActiveMap = new HashMap<Short, CarnivalActive>();
	Map<Integer, CarnivalItem> allItemMap = new HashMap<Integer,CarnivalItem>();
	Map<Integer, CarnivalRule> allRuleMap = new HashMap<Integer,CarnivalRule>();
	Map<Integer, List<CarnivalItem>> allItemTypeMap = new HashMap<Integer,List<CarnivalItem>>();
	Map<Integer, List<CarnivalItem>> allItemActiveIdMap = new HashMap<Integer, List<CarnivalItem>>();
	Map<Integer, Map<Integer, CarnivalRankReward>> allRankRewardMap = new HashMap<Integer, Map<Integer,CarnivalRankReward>>();
	Map<Integer, Map<Byte, CarnivalReward>> allRewardMap = new HashMap<Integer, Map<Byte,CarnivalReward>>();
	private boolean loadSuccess = true;
	//数据
	Map<String, Map<Integer, CarnivalDbInfo>> allRoleDataMap = new ConcurrentHashMap<String,Map<Integer, CarnivalDbInfo>>();
	
	@Override
	public void start() {
		allActiveMap = loadCarnivalActive();
		allItemMap = loadCarnivalItem();
		allRuleMap = loadCarnivalRule();
		allRankRewardMap = loadRankReward();
		allRewardMap = loadReward();
		this.init();
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	@Override
	public Result reload(){
		
		Map<Short, CarnivalActive> _allActiveMap = loadCarnivalActive();
		Map<Integer, CarnivalItem> _allItemMap = loadCarnivalItem();
		Map<Integer, CarnivalRule> _allRuleMap = loadCarnivalRule();
		Map<Integer, Map<Integer, CarnivalRankReward>> _allRankRewardMap = loadRankReward();
		Map<Integer, Map<Byte, CarnivalReward>> _allRewardMap = loadReward();
		
		Result result = new Result();
		if(!this.loadSuccess){
			return result.setInfo(this.getText(TextId.CARNIVAL_RELOAD_FAILURE));
		}
		
		allActiveMap = _allActiveMap;
		allItemMap = _allItemMap;
		allRuleMap = _allRuleMap;
		allRankRewardMap = _allRankRewardMap;
		allRewardMap = _allRewardMap;
		
		this.init();
		
		return result.success();
	}
	
	public void setCarnivalDAO(CarnivalDAOImpl carnivalDAO) {
		this.carnivalDAO = carnivalDAO;
	}

	private Map<Short, CarnivalActive> loadCarnivalActive() {
		//加载配置项
		String fileName = XlsSheetNameType.carnival_active.getXlsName();
		String sheetName = XlsSheetNameType.carnival_active.getSheetName();
		Map<Short, CarnivalActive> map = new HashMap<Short, CarnivalActive>();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<CarnivalActive> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, CarnivalActive.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
				this.loadSuccess = false;
				return null;
			}
			for(CarnivalActive active : list){
				active.initServerId();
				
				Result result = active.checkInit();
				
				if(!active.isCanOpen()){
					continue;
				}
				
				if(!result.isSuccess()){
					Log4jManager.checkFail();
					Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName + "." + result.getInfo());
					this.loadSuccess = false;
				}
				map.put(active.getId(), active);
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
			this.loadSuccess = false;
		}
		return map;
	}
	
	private Map<Integer, CarnivalItem> loadCarnivalItem() {
		//加载配置项
		String fileName = XlsSheetNameType.carnival_item.getXlsName();
		String sheetName = XlsSheetNameType.carnival_item.getSheetName();
		Map<Integer, CarnivalItem> _allItemMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			_allItemMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, CarnivalItem.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
			this.loadSuccess = false;
		}
		if(Util.isEmpty(_allItemMap)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
			this.loadSuccess = false;
		}
		return _allItemMap;
	}
	
	private Map<Integer, CarnivalRule> loadCarnivalRule() {
		//加载配置
		String fileName = XlsSheetNameType.carnival_rule.getXlsName();
		String sheetName = XlsSheetNameType.carnival_rule.getSheetName();
		Map<Integer, CarnivalRule> _allRuleMap = null;
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			_allRuleMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, CarnivalRule.class);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
			this.loadSuccess = false;
		}
		return _allRuleMap;
	}
	
	private void init() {
		if(Util.isEmpty(allActiveMap)){
			return;
		}
		for(CarnivalRule rule : allRuleMap.values()) {
			if(null == rule) {
				continue;
			}
			rule.init();
		}
		
		for(CarnivalItem carnivalItem : this.allItemMap.values()) {
			if(null == carnivalItem) {
				continue;
			}
			
			CarnivalActive active = this.allActiveMap.get((short)carnivalItem.getActiveId());
			if(null == active) {
				continue;
			}
			
			if(!active.isCanOpen()){
				continue;
			}
			
			CarnivalRule carnivalRule = this.allRuleMap.get(carnivalItem.getRuleId());
			if(null == carnivalRule) {
				continue;
			}
			if(!carnivalItem.init(carnivalRule)){
				continue;
			}
			
			carnivalItem.setCarnivalRule(this.allRuleMap.get(carnivalItem.getRuleId()));
			
			int carnivalTypeId = carnivalItem.getCarnivalRule().getCarnivalTypeId();
			if(!allItemTypeMap.containsKey(carnivalTypeId)) {
				allItemTypeMap.put(carnivalTypeId, new ArrayList<CarnivalItem>());
			}
			allItemTypeMap.get(carnivalTypeId).add(carnivalItem);
			
			int activeId = carnivalItem.getActiveId();
			if(!allItemActiveIdMap.containsKey(activeId)) {
				allItemActiveIdMap.put(activeId, new ArrayList<CarnivalItem>());
			}
			allItemActiveIdMap.get(activeId).add(carnivalItem);
		}
	}
	
	private Map<Integer, Map<Integer, CarnivalRankReward>> loadRankReward(){
		String fileName = XlsSheetNameType.carnival_rankReward.getXlsName();
		String sheetName = XlsSheetNameType.carnival_rankReward.getSheetName();
		Map<Integer, Map<Integer, CarnivalRankReward>> _allRankRewardMap = new HashMap<Integer, Map<Integer,CarnivalRankReward>>();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			for(CarnivalRankReward rankReward : XlsPojoUtil.sheetToList(sourceFile, sheetName, CarnivalRankReward.class)){
				if(null == rankReward){
					continue;
				}
				
				int itemId = rankReward.getItemId();
				if(!_allRankRewardMap.containsKey(itemId)){
					_allRankRewardMap.put(itemId, new HashMap<Integer,CarnivalRankReward>());
				}
				_allRankRewardMap.get(itemId).put(rankReward.getRank(), rankReward);
			}
		}catch(Exception e){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
			this.loadSuccess = false;
		}
		return _allRankRewardMap;
	}
	
	private Map<Integer, Map<Byte, CarnivalReward>> loadReward(){
		String fileName = XlsSheetNameType.carnival_reward.getXlsName();
		String sheetName = XlsSheetNameType.carnival_reward.getSheetName();
		Map<Integer, Map<Byte, CarnivalReward>> _allRewardMap = new HashMap<Integer, Map<Byte,CarnivalReward>>();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			for(CarnivalReward reward : XlsPojoUtil.sheetToList(sourceFile, sheetName, CarnivalReward.class)){
				if(null == reward){
					continue;
				}
				
				reward.init();
				
				int rewardKey = reward.getRewardKey();
				if(!_allRewardMap.containsKey(rewardKey)){
					_allRewardMap.put(rewardKey, new HashMap<Byte,CarnivalReward>());
				}
				_allRewardMap.get(rewardKey).put(reward.getCareer(), reward);
			}
		}catch(Exception e){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "+fileName +" sheetName ="+sheetName+",the system will shutdown .... ",e);
			this.loadSuccess = false;
		}
		return _allRewardMap;
	}
	
	@Override
	public void doRank(){
		for(CarnivalItem carnivalItem : this.allItemMap.values()) {
			if(null == carnivalItem) {
				continue;
			}
			CarnivalActive active = this.allActiveMap.get((short)carnivalItem.getActiveId());
			if(null == active) {
				continue;
			}
			if(!active.isServerCanShow()){
				continue;
			}
			try{
				if(carnivalItem.isInStatDate()) {
					carnivalItem.getCarnivalRank();
					continue;
				}
				//如果活动结束，排名、发奖
				if(carnivalItem.isTimeToReward()){
					carnivalItem.getCarnivalRank();
					carnivalItem.reward();
				}
			}catch(Exception e){
				logger.error("CarnivalApp.doRank error:",e);
			}
		}
	}
	
	@Override
	public void roleDataCount(RoleInstance role, int value, int subValue ,CarnivalType type) {
		try{
			if(value <=0){
				return;
			}
			List<CarnivalItem> itemList = allItemTypeMap.get(type.getType());
			if(Util.isEmpty(itemList)) {
				return;
			}
			String roleId = role.getRoleId();
			for(CarnivalItem item : itemList) {
				if(null == item) {
					continue;
				}
				if(!item.isInStatDate()){
					continue;
				}
				
				if(!allRoleDataMap.containsKey(roleId)) {
					allRoleDataMap.put(roleId, new HashMap<Integer, CarnivalDbInfo>());
				}
				Map<Integer, CarnivalDbInfo> dbMap = allRoleDataMap.get(roleId);
				int itemId = item.getItemId();
				
				CarnivalDbInfo info = dbMap.get(itemId);
				if(null == info) {
					info = new CarnivalDbInfo(itemId, roleId, role.getRoleName(), role.getCampId(), role.getCareer());
					info.setSaveDbStateType(SaveDbStateType.Insert);
					dbMap.put(itemId, info);
				}
				int infoValue = 0;
				int infoSubValue = 0;
				if(type != CarnivalType.Role_Tower) {
					infoValue = info.getTargetValue() + value;
					infoSubValue = info.getSubTargetValue() + subValue;
				}else{
					//通天塔取大值
					if(info.getTargetValue() > value) {
						continue;
					}
					if(info.getTargetValue() == value && info.getSubTargetValue() >= subValue) {
						continue;
					}
					infoValue = value;
					infoSubValue = subValue;
				}
				info.setTargetValue(infoValue);
				info.setSubTargetValue(infoSubValue);
				if(SaveDbStateType.Insert != info.getSaveDbStateType()){
					info.setSaveDbStateType(SaveDbStateType.Update);
				}
			}
		}catch(Exception e){
			logger.error("CarnivalApp.recharge error:",e);
		}
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context){
		try {
			String roleId = role.getRoleId();
			Map<Integer, CarnivalDbInfo> dbMap = allRoleDataMap.get(roleId);
			if(Util.isEmpty(dbMap)) {
				return 1;
			}
			
			for(CarnivalDbInfo info : dbMap.values()) {
				SaveDbStateType state = info.getSaveDbStateType();
				if(SaveDbStateType.Initialize == state){
					continue;
				}
				if(SaveDbStateType.Insert == state){
					GameContext.getBaseDAO().insert(info);
				}else if(SaveDbStateType.Update == state){
					GameContext.getBaseDAO().update(info);
				}
			}
			allRoleDataMap.remove(roleId);
		} catch (RuntimeException e) {
			this.logger.error("CarnivalApp.offlineRole error: ", e);
			GameContext.getCarnivalApp().offlineLog(role);
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"ActiveCarnivalApp.offline error,roleId=" + role.getRoleId() + ",userId="
							+ role.getUserId(), e);
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
			String roleId = role.getRoleId();
			Map<Integer, CarnivalDbInfo> dbMap = allRoleDataMap.get(roleId);
			if(!Util.isEmpty(dbMap)) {
				return;
			}
			
			for(CarnivalDbInfo info : dbMap.values()) {
				StringBuffer sb = new StringBuffer();
				sb.append(info.getId());
				sb.append(Cat.pound);
				sb.append(info.getActiveId());
				sb.append(Cat.pound);
				sb.append(info.getTargetId());
				sb.append(Cat.pound);
				sb.append(info.getName());
				sb.append(Cat.pound);
				sb.append(info.getCampId());
				sb.append(Cat.pound);
				sb.append(info.getCareer());
				sb.append(Cat.pound);
				sb.append(info.getTargetValue());
				sb.append(Cat.pound);
				sb.append(info.getSubTargetValue());
				sb.append(Cat.pound);
				Log4jManager.OFFLINE_CARNIVAL.info(sb.toString());
			}
		}catch(Exception e){
			logger.error("saveRoleExchangeLog:",e);
		}
	}
	
	@Override
	public int onLogin(RoleInstance role, Object context){
		try{
			String roleId = role.getRoleId();
			List<CarnivalDbInfo> list = GameContext.getBaseDAO().selectList(CarnivalDbInfo.class, "targetId", roleId);
			if(Util.isEmpty(list)){
				return 1;
			}
			Map<Integer, CarnivalDbInfo> map = new HashMap<Integer, CarnivalDbInfo>();
			for(CarnivalDbInfo info : list) {
				CarnivalItem item = this.allItemMap.get(info.getActiveId());
				if(null == item) {
					continue;
				}
				if(!item.isInStatDate()) {
					continue;
				}
				map.put(info.getActiveId(), info);
			}
			allRoleDataMap.put(roleId, map);
		} catch (RuntimeException e) {
			this.logger.error("CarnivalApp.loginRole error: ", e);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public void setArgs(Object arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Map<Integer, CarnivalDbInfo>> getAllRoleData() {
		return this.allRoleDataMap;
	}
	
	@Override
	public List<CarnivalDbInfo> getActiveData(int itemId, int roleValue) {
		List<CarnivalDbInfo> list = new ArrayList<CarnivalDbInfo>();
		try{
			list = this.carnivalDAO.getActiveData(itemId, roleValue);
		}catch(Exception e){
			this.logger.error("CarnivalApp.getActiveData error: ", e);
		}
		return list;
	}

	@Override
	public List<CarnivalDbInfo> getActiveDataBySize(int itemId, int start,
			int end) {
		List<CarnivalDbInfo> list = new ArrayList<CarnivalDbInfo>();
		try{
			list = this.carnivalDAO.getActiveDataBySize(itemId, start, end);
		}catch(Exception e){
			this.logger.error("CarnivalApp.getActiveDataBySize error: ", e);
		}
		return list;
	}

	@Override
	public List<CarnivalRankInfo> getRoleMonutSort(int start, int end) {
		List<CarnivalRankInfo> list = new ArrayList<CarnivalRankInfo>();
		try{
			list = this.carnivalDAO.getRoleMonutSort(start, end);
		}catch(Exception e){
			this.logger.error("CarnivalApp.getRoleMonutSort error: ", e);
		}
		return list;
	}

	@Override
	public List<CarnivalDbInfo> getCampActiveDataByColumn(int itemId) {
		List<CarnivalDbInfo> list = new ArrayList<CarnivalDbInfo>();
		try{
			list = this.carnivalDAO.getCampActiveDataByColumn(itemId);
		}catch(Exception e){
			this.logger.error("CarnivalApp.getCampActiveDataByColumn error: ", e);
		}
		return list;
	}
	
	@Override
	public List<CarnivalDbInfo> getCareerActiveDataByColumn(int itemId) {
		List<CarnivalDbInfo> list = new ArrayList<CarnivalDbInfo>();
		try{
			list = this.carnivalDAO.getCareerActiveDataByColumn(itemId);
		}catch(Exception e){
			this.logger.error("CarnivalApp.getCareerActiveDataByColumn error: ", e);
		}
		return list;
	}
	
	@Override
	public CarnivalReward getCarnivalReward(int activeId, int rank, byte career) {
		Map<Integer, CarnivalRankReward> rankRewardMap = this.allRankRewardMap.get(activeId);
		if(Util.isEmpty(rankRewardMap)) {
			return null;
		}
		
		CarnivalRankReward rankReward = rankRewardMap.get(rank);
		if(null == rankReward) {
			return null;
		}
		
		Map<Byte, CarnivalReward> rewardMap = this.allRewardMap.get(rankReward.getRewardKey());
		if(Util.isEmpty(rewardMap)){
			return null;
		}
		
		CarnivalReward carnivalReward = rewardMap.get(career);
		if(null == carnivalReward){
			return null;
		}
		
		return carnivalReward;
	}
	
	@Override
	public C1186_CarnivalRespMessage getActiveCarnival(short activeId) {
		C1186_CarnivalRespMessage resp = new C1186_CarnivalRespMessage();
		List<CarnivalActiveItem> list = new ArrayList<CarnivalActiveItem>();
		try{
			CarnivalActive active = this.allActiveMap.get(activeId);
			if(null == active) {
				return resp;
			}
			
			if(!active.isTimeOpen()) {
				return resp;
			}
			
			List<CarnivalItem> itemList = allItemActiveIdMap.get((int)active.getId());
			if(Util.isEmpty(itemList)){
				return resp;
			}
			CarnivalActiveItem carnivalactiveItem = null;
			for(CarnivalItem item : itemList) {
				carnivalactiveItem = new CarnivalActiveItem();
				carnivalactiveItem.setItemId((short)item.getItemId());
				carnivalactiveItem.setName(TextId.CARNIVAL_NOT_OPEN);
				ActiveCarnivalStatus status = ActiveCarnivalStatus.NotOpen;
				if(item.isOutDate()) {
					status = ActiveCarnivalStatus.OutDate;
					carnivalactiveItem.setName(item.getName());
				}else if(item.isInStatDate()) {
					status = ActiveCarnivalStatus.InStatDate;
					carnivalactiveItem.setName(item.getName());
				}
				carnivalactiveItem.setTimeDesc(item.getTimeDesc());
				carnivalactiveItem.setStatus(status.getType());
				list.add(carnivalactiveItem);
			}
			
			resp.setListItems(list);
			resp.setActiveName(active.getName());
			resp.setActiveDesc(active.getDesc());
		}catch(Exception e){
			this.logger.error("CarnivalApp.getCarnivalItem error: ", e);
		}
		return resp;
	}

	@Override
	public C1187_CarnivalDetailRespMessage getActiveCarnivalDetail(int itemId, byte career) {
		C1187_CarnivalDetailRespMessage resp = new C1187_CarnivalDetailRespMessage();
		try{
			CarnivalItem carnivalItem = allItemMap.get(itemId);
			if(null == carnivalItem) {
				return resp;
			}
			
			resp.setTimeDesc(carnivalItem.getTimeDesc());
			resp.setRewardList(carnivalItem.getRewardGoodsMap().get(career));
			resp.setAllRewardList(carnivalItem.getAllRewardGoodsMap().get(career));
			
			if(carnivalItem.isInStatDate() || carnivalItem.isOutDate()) {
				resp.setName(carnivalItem.getName());
				resp.setRewardStr(carnivalItem.getRewardDesc());
				resp.setAllRewardStr(carnivalItem.getAllRewardDesc());
				resp.setActiveTime(carnivalItem.getActiveTimeStr() + Cat.blank + this.getText(TextId.CARNIVAL_OVER_STR));
			}else{
				resp.setName(this.getText(TextId.CARNIVAL_NOT_OPEN));
				resp.setRewardStr(this.getText(TextId.CARNIVAL_NOT_OPEN));
				resp.setAllRewardStr(this.getText(TextId.CARNIVAL_NOT_OPEN));
				resp.setActiveTime(carnivalItem.getActiveTimeStr() + Cat.blank + this.getText(TextId.CARNIVAL_OPEN_STR));
				return resp;
			}
			
			List<CarnivalRankInfo> rankList = carnivalItem.getRankList();
			if(Util.isEmpty(rankList)) {
				return resp;
			}
			List<CarnivalRankItem> rankItemList = new ArrayList<CarnivalRankItem>();
			CarnivalRankItem rankItem = null;
			for(CarnivalRankInfo info : rankList) {
				rankItem = new CarnivalRankItem();
				rankItem.setCampId(info.getCampId());
				rankItem.setCareer(info.getCareer());
				rankItem.setName(info.getName());
				rankItem.setValue(info.getTargetValue());
				rankItemList.add(rankItem);
			}
			resp.setRankList(rankItemList);
		}catch(Exception e){
			this.logger.error("CarnivalApp.getActiveCarnivalDetail error: ", e);
		}
		return resp;
	}
	
	@Override
	public CarnivalActive getCarnivalActive(){
		try{
			if(Util.isEmpty(this.allActiveMap)){
				return null;
			}
			for(CarnivalActive active : this.allActiveMap.values()){
				if(!active.isCanOpen()){
					continue ;
				}
				return active;
			}
			
		}catch (Exception e) {
			logger.error("CarnivalApp.getCarnivalActive error",e);
		}
		return null;
	}
}
