//package sacred.alliance.magic.app.active.rank;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.ActivePanelDetailRankItem;
//import com.game.draco.message.item.ActiveRankDetailItem;
//import com.game.draco.message.response.C2301_ActivePanelDetailRespMessage;
//import com.game.draco.message.response.C2319_ActiveRankDetailRespMessage;
//
//import sacred.alliance.magic.app.active.vo.Active;
//import sacred.alliance.magic.app.rank.domain.RankInfo;
//import sacred.alliance.magic.app.rank.domain.RankLogRoleInfo;
//import sacred.alliance.magic.app.rank.domain.RankRewardRank;
//import sacred.alliance.magic.base.ActiveStatus;
//import sacred.alliance.magic.base.ActiveType;
//import sacred.alliance.magic.base.XlsSheetNameType;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.domain.RankDbInfo;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.util.XlsPojoUtil;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class ActiveRankAppImpl implements ActiveRankApp {
//
//	private Map<Short, ActiveRankInfo> allActiveRankMap;
//
//	public Map<Short, ActiveRankInfo> getAllActiveRankMap() {
//		return allActiveRankMap;
//	}
//
//	@Override
//	public void setArgs(Object arg0) {
//		
//	}
//
//	@Override
//	public void start() {
//		this.loadActiveRankList();
//	}
//
//	@Override
//	public void stop() {
//		
//	}
//	
//	@Override
//	public void checkReset(RoleInstance role, Active active) {
//		
//	}
//
//	@Override
//	public ActiveStatus getActiveStatus(RoleInstance role, Active active) {
//		if(!active.isTimeOpen() || !active.isSuitLevel(role)){
//			return ActiveStatus.NotOpen;
//		}
//		return ActiveStatus.CanAccept;
//	}
//
//	@Override
//	public C2301_ActivePanelDetailRespMessage getActiveDetail(RoleInstance role,
//			Active active) {
//		ActiveRankInfo aRankItem = this.getAllActiveRankMap().get(active.getId());
//		if(null == aRankItem){
//			return null;
//		}
//		ActivePanelDetailRankItem detailItem = new ActivePanelDetailRankItem();
//		detailItem.setRewardTime(aRankItem.getRewardStartTime() + CAT + aRankItem.getRewardEndTime());
//		detailItem.setType(ActiveType.Rank.getType());
//		//公用赋值
//		GameContext.getActiveApp().buildActivePanelDetailBaseItem(detailItem, active);
//		
//		C2301_ActivePanelDetailRespMessage resp = new C2301_ActivePanelDetailRespMessage();
//		resp.setDetailItem(detailItem);
//		return resp;
//	}
//	
//	private void loadActiveRankList(){
//		String fileName = XlsSheetNameType.active_rank.getXlsName();
//		String sheetName = XlsSheetNameType.active_rank.getSheetName();
//		try{
//			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
//			allActiveRankMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, ActiveRankInfo.class);
//		}catch (Exception ex){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
//		}
//		if(Util.isEmpty(this.allActiveRankMap)){
//			Log4jManager.checkFail();
//			Log4jManager.CHECK.error("not any config: sourceFile = "+fileName +" sheetName ="+sheetName);
//		}
//		
//		List<Short> delList = new ArrayList<Short>();
//		for(Entry<Short, ActiveRankInfo> entry : allActiveRankMap.entrySet()){
//			ActiveRankInfo activeRankItem = entry.getValue();
//			if(null == activeRankItem){
//				continue;
//			}
//			if(!activeRankItem.init()){
//				delList.add(entry.getKey());
//			}
//		}
//		//删除初始化不成功的排行榜活动
//		for(Short delKey : delList){
//			allActiveRankMap.remove(delKey);
//		}
//	}
//	
//	@Override
//	public void loadRoleRank(RoleInstance role) {
//		List<RankDbInfo> rankDbInfoList = GameContext.getBaseDAO().selectList(RankDbInfo.class, "roleId", role.getRoleId());
//		if(null==rankDbInfoList || rankDbInfoList.size()==0){
//			return;
//		}
//		Map<Integer, RankDbInfo> rankDbInfoMap = role.getRankDbInfo();
//		for(RankDbInfo rankDbInfo : rankDbInfoList){
//			if(null == rankDbInfo){
//				continue;
//			}
//			rankDbInfo.setExistRecord(true);
//			rankDbInfoMap.put(rankDbInfo.getRankId(), rankDbInfo);
//		}
//	}
//
//	@Override
//	public void saveRoleRank(RoleInstance role) {
//		Map<Integer, RankDbInfo> rankDbInfoMap = role.getRankDbInfo();
//		if(rankDbInfoMap.size() == 0){
//			return ;
//		}
//		for(Entry<Integer, RankDbInfo> entry : rankDbInfoMap.entrySet()){
//			RankDbInfo rankDbInfo = entry.getValue();
//			if(null == rankDbInfo){
//				continue;
//			}
//			if(rankDbInfo.isExistRecord()){
//				GameContext.getBaseDAO().update(rankDbInfo);
//			}
//			else{
//				GameContext.getBaseDAO().insert(rankDbInfo);
//				rankDbInfo.setExistRecord(true);
//			}
//		}
//	}
//	
//	@Override
//	public void offlineLog(RoleInstance role) {
//		try{
//			Map<Integer, RankDbInfo> rankDbInfoMap = role.getRankDbInfo();
//			if(rankDbInfoMap.size() == 0){
//				return ;
//			}
//			for(Entry<Integer, RankDbInfo> entry : rankDbInfoMap.entrySet()){
//				RankDbInfo rankDbInfo = entry.getValue();
//				if(null == rankDbInfo){
//					continue;
//				}
//				Log4jManager.OFFLINE_RANK_ACTIVE_DB_LOG.info(rankDbInfo.getSelfInfo());
//			}
//			
//		}catch(Exception e){
//		}
//	}
//
//	@Override
//	public byte getRewardStat(RoleInstance role, RankInfo rankItem) {
//		//如果不是活动排行榜
//		ActiveRankInfo aRankItem = rankItem.getActiveRankInfo();
//		if(null == aRankItem){
//			return ActiveRankApp.REWARD_STAT_ERROR;
//		}
//		int rankId = rankItem.getId();
//		RankDbInfo rankDbInfo = role.getRankDbInfo().get(rankId);
//		if(null != rankDbInfo && rankDbInfo.getReward() == RankDbInfo.REWARDED_YES){
//			return ActiveRankApp.REWARD_STAT_REWARDED;
//		}
//		RankLogRoleInfo roleInfo = GameContext.getRankApp().getRoleRank(rankId, role.getRoleId());
//		if(null == roleInfo){
//			return ActiveRankApp.REWARD_STAT_NO;
//		}
//		List<RankRewardRank> rewardRankList = GameContext.getRankApp().getRewardRankList(roleInfo.getLevel(), roleInfo.getCamp(), roleInfo.getGender(), rankId);
//		if(null == rewardRankList){
//			return ActiveRankApp.REWARD_STAT_NO;
//		}
//		boolean isInRewardRank = false;
//		for(RankRewardRank rewardRank : rewardRankList){
//			if(null == rewardRank){
//				continue;
//			}
//			short rank = roleInfo.getRank();
//			if(rewardRank.getRankStart() <= rank && rewardRank.getRankEnd() >= rank){
//				isInRewardRank = true;
//				break;
//			}
//		}
//		//当前排名没有奖项
//		if(!isInRewardRank){
//			return REWARD_STAT_NO;
//		}
//		if(aRankItem.isInRewardDate()){
//			return ActiveRankApp.REWARD_STAT_ENABLE;
//		}
//		else{
//			return ActiveRankApp.REWARD_STAT_DISABLE;
//		}
//	}
//
//	@Override
//	public boolean isOutDate(Active active) {
//		return active.isOutDate();
//	}
//
//	@Override
//	public void realTimeWriteDB(RankDbInfo rankDbInfo) {
//		if(rankDbInfo.isExistRecord()){
//			GameContext.getBaseDAO().update(rankDbInfo);
//		}
//		else{
//			GameContext.getBaseDAO().insert(rankDbInfo);
//			rankDbInfo.setExistRecord(true);
//		}
//	}
//
//	@Override
//	public Message createRankDetailMsg(RoleInstance role, short activeId) {
//		ActiveRankInfo activeRank = this.getAllActiveRankMap().get(activeId);
//		if(null == activeRank || activeRank.isOutDate()){
//			return null;
//		}
//		//没有关联的排行榜
//		List<RankInfo> rankItemList = activeRank.getRankInfoList();
//		if(null == rankItemList || rankItemList.size() == 0){
//			return null;
//		}
//		List<ActiveRankDetailItem> aRankDetailList = new ArrayList<ActiveRankDetailItem>();
//		byte rankType = -1;
//		for(RankInfo rankItem : rankItemList){
//			if(null == rankItem){
//				continue;
//			}
//			if(rankType == -1){
//				rankType = rankItem.getType();
//			}
//			ActiveRankInfo aRankItem = rankItem.getActiveRankInfo();
//			if(null == aRankItem){
//				continue;
//			}
//			ActiveRankDetailItem aRankDetailItem = new ActiveRankDetailItem();
//			aRankDetailItem.setRankId(rankItem.getId());
//			aRankDetailItem.setStatus(GameContext.getActiveRankApp().getRewardStat(role, rankItem));
//			aRankDetailItem.setTagResId(rankItem.getTagResId());
//			aRankDetailList.add(aRankDetailItem);
//		}
//		
//		C2319_ActiveRankDetailRespMessage respMsg = new C2319_ActiveRankDetailRespMessage();
//		respMsg.setActiveRankDetailList(aRankDetailList);
//		respMsg.setType(rankType);
//		respMsg.setIndex((byte)0);
//		return respMsg;
//	}
//
//	@Override
//	public short getActiveNum(RoleInstance role, Active active) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//}
