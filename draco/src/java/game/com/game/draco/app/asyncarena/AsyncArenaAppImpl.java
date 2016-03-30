package com.game.draco.app.asyncarena;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;

import org.python.google.common.collect.Lists;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncBase;
import com.game.draco.app.asyncarena.config.AsyncBuy;
import com.game.draco.app.asyncarena.config.AsyncClubDes;
import com.game.draco.app.asyncarena.config.AsyncGroup;
import com.game.draco.app.asyncarena.config.AsyncGroupReward;
import com.game.draco.app.asyncarena.config.AsyncMap;
import com.game.draco.app.asyncarena.config.AsyncRankDes;
import com.game.draco.app.asyncarena.config.AsyncRankReward;
import com.game.draco.app.asyncarena.config.AsyncRefresh;
import com.game.draco.app.asyncarena.config.AsyncReward;
import com.game.draco.app.asyncarena.config.AsyncSort;

public class AsyncArenaAppImpl implements AsyncArenaApp{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//免费次数
	static byte freeNum = 16;
	
	@Getter @Setter private AsyncBase asyncBase = null;
	//异步竞技场分组数据
	@Getter @Setter private Map<Byte,AsyncGroup> groupMap = null;
	
	//异步竞技场刷新数据
	@Getter @Setter private Map<String,AsyncRefresh> refreshMap = null;
	
	//异步竞技场奖励数据
	@Getter @Setter private Map<String,AsyncReward> rewardMap = null;
	
	//异步竞技场排行奖励数据
	@Getter @Setter private List<AsyncRankReward> rankRewardList = null;
	
	//异步竞技场筛选排序规则数据
	@Getter @Setter private List<AsyncSort> sortList = null;

	//异步竞技场排行榜描述
	@Getter @Setter private List<AsyncRankDes> rankDesList = null;

	//异步竞技场赛场描述
	@Getter @Setter private List<AsyncClubDes> clubDesList = null;
	
	//异步竞技场赛场描述
	@Getter @Setter private AsyncMap asyncMap = null;
	
	//异步竞技场购买数据
	@Getter @Setter private Map<String,AsyncBuy> buyMap = null;
	
	@Getter @Setter private Map<Integer,List<AsyncGroupReward>> rewardGroupMap = null;
	
	@Override
	public void start() {
		try{
			//加载异步竞技场基础数据
			loadAsyncBaseConfig();
			//加载异步竞技场分组数据
			loadAsyncGroupConfig();
			//加载异步竞技场刷新数据
			loadAsyncRefreshConfig();
			//加载异步竞技场奖励数据
			loadAsyncRewardConfig();
			//加载异步竞技场排行奖励数据
			loadAsyncRankRewardConfig();
			//加载异步竞技场筛选排序数据
			loadAsyncSortConfig();
			//加载异步竞技场排行榜描述
			loadAsyncRankDesConfig();
			//加载异步竞技场赛场描述
			loadAsyncClubDesConfig();
			//加载异步竞技场地图数据
			loadAsyncMapConfig();
			//加载异步竞技场购买数据
			loadAsyncBuyConfig();
			//加载异步竞技场奖励数据
			loadAsyncGroupRewardConfig();
			
		}catch(Exception e){
			logger.error("start is error",e);
		}
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void setArgs(Object args) {
		
	}

	@Override
	public void onjoinGame(RoleInstance role) {
		
	}

	@Override
	public void onLeaveGame(RoleInstance role) {
		
	}
	
	/**
	 * 加载异步竞技场分组数据
	 */
	private void loadAsyncGroupConfig(){
		try{
			String fileName = XlsSheetNameType.async_group_config.getXlsName();
			String sheetName = XlsSheetNameType.async_group_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			groupMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AsyncGroup.class);
			if(groupMap == null || groupMap.isEmpty()){
				Log4jManager.CHECK.error("not config the groupMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadAsyncGroupConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场基础数据
	 */
	private void loadAsyncBaseConfig(){
		try{
			String fileName = XlsSheetNameType.async_base_config.getXlsName();
			String sheetName = XlsSheetNameType.async_base_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<AsyncBase> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, AsyncBase.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the async_base_config,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
			asyncBase = list.get(0);
		}catch(Exception e){
			logger.error("async_base_config is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场刷新数据
	 */
	private void loadAsyncRefreshConfig(){
		try{
			String fileName = XlsSheetNameType.async_refresh_config.getXlsName();
			String sheetName = XlsSheetNameType.async_refresh_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			refreshMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AsyncRefresh.class);
			if(refreshMap == null || refreshMap.isEmpty()){
				Log4jManager.CHECK.error("not config the refreshMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadAsyncRefreshConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场奖励数据
	 */
	private void loadAsyncRewardConfig(){
		try{
			String fileName = XlsSheetNameType.async_reward_config.getXlsName();
			String sheetName = XlsSheetNameType.async_reward_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			rewardMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AsyncReward.class);
			if(rewardMap == null || rewardMap.isEmpty()){
				Log4jManager.CHECK.error("not config the rewardMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadAsyncRewardConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场排行奖励数据
	 */
	private void loadAsyncRankRewardConfig(){
		try{
			String fileName = XlsSheetNameType.async_rank_config.getXlsName();
			String sheetName = XlsSheetNameType.async_rank_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			rankRewardList = XlsPojoUtil.sheetToList(sourceFile, sheetName, AsyncRankReward.class);
			if(rankRewardList == null || rankRewardList.isEmpty()){
				Log4jManager.CHECK.error("not config the rankRewardList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadAsyncRankRewardConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场筛选排序数据
	 */
	private void loadAsyncSortConfig(){
		try{
			String fileName = XlsSheetNameType.async_sort_config.getXlsName();
			String sheetName = XlsSheetNameType.async_sort_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			//异步竞技场筛选排序规则数据
			sortList = XlsPojoUtil.sheetToList(sourceFile, sheetName, AsyncSort.class);
			if(sortList == null || sortList.isEmpty()){
				Log4jManager.CHECK.error("not config the sortList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadAsyncSortConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场排行榜描述
	 */
	private void loadAsyncRankDesConfig(){
		try{
			String fileName = XlsSheetNameType.async_rankdes_config.getXlsName();
			String sheetName = XlsSheetNameType.async_rankdes_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			//异步竞技场筛选排序规则数据
			rankDesList = XlsPojoUtil.sheetToList(sourceFile, sheetName, AsyncRankDes.class);
			if(rankDesList == null || rankDesList.isEmpty()){
				Log4jManager.CHECK.error("not config the rankDesList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadAsyncRankDesConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场赛场描述
	 */
	private void loadAsyncClubDesConfig(){
		try{
			String fileName = XlsSheetNameType.async_clubdes_config.getXlsName();
			String sheetName = XlsSheetNameType.async_clubdes_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			//异步竞技场筛选排序规则数据
			clubDesList = XlsPojoUtil.sheetToList(sourceFile, sheetName, AsyncClubDes.class);
			if(clubDesList == null || clubDesList.isEmpty()){
				Log4jManager.CHECK.error("not config the clubDesList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}
		}catch(Exception e){
			logger.error("loadAsyncClubDesConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场场景数据
	 */
	private void loadAsyncMapConfig(){
		try{
			String fileName = XlsSheetNameType.async_map_config.getXlsName();
			String sheetName = XlsSheetNameType.async_map_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			//异步竞技场筛选排序规则数据
			asyncMap = XlsPojoUtil.getEntity(sourceFile, sheetName, AsyncMap.class);
			if(asyncMap == null){
				Log4jManager.CHECK.error("not config the mapList,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				sacred.alliance.magic.app.map.Map map = GameContext.getMapApp().getMap(asyncMap.getMapId());
				if(!map.getMapConfig().changeLogicType(MapLogicType.asyncArena)) {
					Log4jManager.CHECK.error("AsyncArenaAppImpl The map logic type config error. mapId= "	+ fileName);
					Log4jManager.checkFail();
				}
			}
		}catch(Exception e){
			logger.error("loadAsyncMapConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场购买数据
	 */
	private void loadAsyncBuyConfig(){
		try{
			String fileName = XlsSheetNameType.async_buy_config.getXlsName();
			String sheetName = XlsSheetNameType.async_buy_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			buyMap = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, AsyncBuy.class);
			if(buyMap == null || buyMap.isEmpty()){
				Log4jManager.CHECK.error("not config the buyMap,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				byte num = 0;
				for(Entry<String, AsyncBuy> buy : buyMap.entrySet()){
					if(buy.getValue().getVipLevel() == -1){
						num++;
					}
				}
				freeNum= num;
			}
				
		}catch(Exception e){
			logger.error("loadAsyncBuyConfig is error",e);
		}
	}
	
	/**
	 * 加载异步竞技场奖励数据
	 */
	private void loadAsyncGroupRewardConfig(){
		try{
			String fileName = XlsSheetNameType.async_group_reward_config.getXlsName();
			String sheetName = XlsSheetNameType.async_group_reward_config.getSheetName();
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<AsyncGroupReward> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, AsyncGroupReward.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("not config the loadAsyncGroupRewardConfig,file=" + sourceFile + " sheet=" + sheetName);
				Log4jManager.checkFail();
			}else{
				rewardGroupMap = Maps.newHashMap();
				for(AsyncGroupReward group : list){
					if(rewardGroupMap.containsKey(group.getGroupId())){
						rewardGroupMap.get(group.getGroupId()).add(group);
					}else{
						List<AsyncGroupReward> groupList = Lists.newArrayList();
						groupList.add(group);
						rewardGroupMap.put(group.getGroupId(), groupList);
					}
				}
			}
		}catch(Exception e){
			logger.error("loadAsyncRankRewardConfig is error",e);
		}
	}

	@Override
	public Map<Byte, AsyncGroup> getAsyncGroupMap() {
		return groupMap;
	}

	@Override
	public AsyncRefresh getAsyncRefresh(byte vipLevel, byte refNum) {
		if(refreshMap.containsKey(vipLevel + Cat.underline + refNum)){
			return refreshMap.get(vipLevel + Cat.underline + refNum);
		}
		return null;
	}

	@Override
	public AsyncReward getAsyncRewardByRoleLevel(String key) {
		if(rewardMap.containsKey(key)){
			return rewardMap.get(key);
		}
		return null;
	}

	@Override
	public List<AsyncRankReward> getAsyncRankRewardList() {
		return rankRewardList;
	}

	@Override
	public List<AsyncSort> getAsyncSortList() {
		return sortList;
	}

	@Override
	public List<AsyncRankDes> getAsyncRankDesList() {
		return rankDesList;
	}

	@Override
	public List<AsyncClubDes> getAsyncClubDesList() {
		return clubDesList;
	}

	@Override
	public AsyncBuy getAsyncBuy(byte vipLevel, byte buyNum) {
		if(buyMap.containsKey(vipLevel + Cat.underline + buyNum)){
			return buyMap.get(vipLevel + Cat.underline + buyNum);
		}
		return null;
	}

	@Override
	public byte freeNum() {
		return freeNum;
	}

	@Override
	public List<AsyncGroupReward> getAsyncGroupRewardList(int groupId) {
		return rewardGroupMap.get(groupId);
	}
	
	

}
