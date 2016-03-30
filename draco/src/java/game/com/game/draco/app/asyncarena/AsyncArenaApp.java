package com.game.draco.app.asyncarena;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

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

public interface AsyncArenaApp extends Service{
	
	/**
	 * 登陆
	 * @param role
	 */
	public void onjoinGame(RoleInstance role);
	
	/**
	 * 下线
	 * @param role
	 */
	public void onLeaveGame(RoleInstance role);
	
	/**
	 * 获得异步竞技场分组数据
	 */
	public Map<Byte,AsyncGroup> getAsyncGroupMap();
	
	/**
	 * 获得异步竞技场刷新数据
	 */
	public AsyncRefresh getAsyncRefresh(byte vipLevel,byte refNum);
	
	/**
	 * 获得异步竞技场奖励数据
	 */
	public AsyncReward getAsyncRewardByRoleLevel(String key);

	/**
	 * 获得异步竞技场排行奖励数据
	 */
	public List<AsyncRankReward> getAsyncRankRewardList();
	
	/**
	 * 获得异步竞技场筛选排序数据
	 */
	public List<AsyncSort> getAsyncSortList();
	
	/**
	 * 获得异步竞技场排行榜描述
	 */
	public List<AsyncRankDes> getAsyncRankDesList();
	
	/**
	 * 获得异步竞技场赛场描述
	 */
	public List<AsyncClubDes> getAsyncClubDesList();
	
	/**
	 * 获得异步竞技场场景数据
	 */
	public AsyncMap getAsyncMap();
	
	/**
	 * 获得异步竞技场购买数据
	 */
	public AsyncBuy getAsyncBuy(byte vipLevel,byte buyNum);
	
	/**
	 * 获得异步竞技场免费次数
	 */
	public byte freeNum();
	
	public List<AsyncGroupReward> getAsyncGroupRewardList(int groupId);
	
	public AsyncBase getAsyncBase();

}