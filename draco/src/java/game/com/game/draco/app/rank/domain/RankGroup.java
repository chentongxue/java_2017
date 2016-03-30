package com.game.draco.app.rank.domain;

import java.util.ArrayList;
import java.util.List;


public class RankGroup {

	private int groupId ;
	private String groupName ;
//	private short groupImageId ;
	
	private List<RankWorld> rankWorldList = new ArrayList<RankWorld>() ;
	
	public void addRankWorld(RankWorld rankWorld){
		if(null == rankWorld){
			return ;
		}
		this.rankWorldList.add(rankWorld);
		this.groupId = rankWorld.getGroupId();
		this.groupName = rankWorld.getGroupName() ;
//		this.groupImageId = rankWorld.getGroupImageId() ;
	}
	
	public int getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

//	public short getGroupImageId() {
//		return groupImageId;
//	}

	public List<RankWorld> getRankWorldList() {
		return rankWorldList;
	}
}
