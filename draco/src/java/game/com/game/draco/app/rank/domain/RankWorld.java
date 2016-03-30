package com.game.draco.app.rank.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.rank.type.RankType;

import lombok.Data;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;

public @Data class RankWorld{
	private static final String CAT = "," ;
	private int groupId ;
	private String groupName ;
	private short groupImageId ;
	private String name;
	private String rankIds;
	
	/**
	 * 当前组合下的排行榜列表
	 */
	private List<RankInfo> rankInfoList = null;
	/**
	 * 当前组合的排行榜逻辑类型(同一组合下必须相同)
	 */
	private RankType rankType = null  ;
	
	public void init(){
		if(Util.isEmpty(rankIds)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("init rankWorld=" + name + " not config rankIds");
			return ;
		}
		String[] ids = Util.splitString(rankIds,CAT);
		for(int i = 0; i < ids.length; i++){
			int rankId = Integer.valueOf(ids[i]);
			RankInfo rankInfo = GameContext.getRankApp().getRankInfo(rankId);
			if(null == rankInfo){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("init rankWorld=" + name + " config rankId= " + rankId + " not exist in allRankMap");
				continue;
			}
			if(null ==  rankType){
				rankType = RankType.get(rankInfo.getType()) ;
			}else if(rankType.getType() != rankInfo.getType()){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("init rankWorld=" + name + " rankIds are not same type");
			}
			if(null == rankInfoList){
				rankInfoList = new ArrayList<RankInfo>();
			}
			rankInfoList.add(rankInfo);
		}
		if(Util.isEmpty(this.rankInfoList)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("init rankWorld=" + name + " config rankIds error");
		}
	}
	public String toStr(){
		String s = "groupId = "+this.groupId +"," +
		"groupName = "+ this.groupName +"," +
		"groupImageId = "+this.groupImageId + "," +
		"name = " + this.name + "," +
		"rankIds = " + rankIds + "," +
		"rankType = "+ this.rankType;
		if(rankInfoList!=null&&rankInfoList.size()>0){
			s += "rankInfoList = " + Arrays.toString(this.rankInfoList.toArray());
		}
		return s;
	}
//	public static void main(String args[]){
//		try {
//			RankWorld.class.newInstance();
//		} catch (InstantiationException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//	}
}
