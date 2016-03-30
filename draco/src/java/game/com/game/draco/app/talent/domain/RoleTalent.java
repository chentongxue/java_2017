package com.game.draco.app.talent.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

import com.game.draco.GameContext;
import com.game.draco.app.talent.config.TalentInfo;
import com.google.common.collect.Lists;

public @Data class RoleTalent {
	
	public final static String ROLE_ID = "roleId" ;
	//角色ID
	private int roleId;
	//总天赋值
	private int talent ;
	//天赋值1
	private int talent1;
	//天赋值2
	private int talent2;
	//天赋值3
	private int talent3;
	//天赋值4
	private int talent4;
	//天赋值5
	private int talent5;
	
	public int talentValue(int talentId) {
		switch (talentId) {
		case 1:
			return this.talent1;
		case 2:
			return this.talent2;
		case 3:
			return this.talent3;
		case 4:
			return this.talent4;
		case 5:
			return this.talent5;
		default:
			return 0;
		}
	}
	
	public void updateTalent(int talentId,int talentValue){
		switch (talentId) {
		case 1:
			this.talent1 = talentValue ;
			return ;
		case 2:
			this.talent2 = talentValue ;
			return ;
		case 3:
			this.talent3 = talentValue ;
			return ;
		case 4:
			this.talent4 = talentValue ;
			return ;
		case 5:
			this.talent5 = talentValue ;
			return ;
		}
	}
	
	private Comparator<RoleTalentTemp> roleTalentComparator = new Comparator<RoleTalentTemp>(){
		@Override
		public int compare(RoleTalentTemp t1, RoleTalentTemp t2) {
			if(t1.getTalent() > t2.getTalent()){
				return -1;
			}
			if(t1.getTalent() < t2.getTalent()){
				return 1;
			}
			return 0;
		}
	} ;
	
	private Comparator<RoleTalentTemp> roleTalentIdComparator = new Comparator<RoleTalentTemp>(){
		@Override
		public int compare(RoleTalentTemp t1, RoleTalentTemp t2) {
			if(t1.getTalentId() < t2.getTalentId()){
				return -1;
			}
			if(t1.getTalentId() > t2.getTalentId()){
				return 1;
			}
			return 0;
		}
	} ;
	
	public List<RoleTalentTemp> getRoleTalentTempList(){
		List<RoleTalentTemp> list = Lists.newArrayList();
		Map<Integer,TalentInfo> talentInfoMap = GameContext.getTalentApp().getTalentInfoMap();
		for(Entry<Integer,TalentInfo> map : talentInfoMap.entrySet()){
			RoleTalentTemp item = new RoleTalentTemp();
			item.setTalentId(map.getKey());
			item.setTalent(talentValue(map.getKey()));
			list.add(item);
		}
		Collections.sort(list,roleTalentComparator);
		return list;
	}
	
	public List<RoleTalentTemp> getRoleTalentRankList(){
		List<RoleTalentTemp> list = Lists.newArrayList();
		Map<Integer,TalentInfo> talentInfoMap = GameContext.getTalentApp().getTalentInfoMap();
		for(Entry<Integer,TalentInfo> map : talentInfoMap.entrySet()){
			RoleTalentTemp item = new RoleTalentTemp();
			item.setTalentId(map.getKey());
			item.setTalent(talentValue(map.getKey()));
			list.add(item);
		}
		Collections.sort(list,roleTalentIdComparator);
		return list;
	}

}
