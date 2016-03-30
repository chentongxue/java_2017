package com.game.draco.app.talent;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.core.Service;

import com.game.draco.app.talent.config.TalentBase;
import com.game.draco.app.talent.config.TalentCondition;
import com.game.draco.app.talent.config.TalentConsumeInfo;
import com.game.draco.app.talent.config.TalentDes;
import com.game.draco.app.talent.config.TalentGroup;
import com.game.draco.app.talent.config.TalentInfo;
import com.game.draco.app.talent.config.TalentLevelUp;
import com.game.draco.app.talent.config.TalentShop;

public interface TalentApp extends Service{

	//天赋基础数据
	Map<Integer,List<TalentBase>> getTalentBaseMap();
	
	//天赋基础数据
	Map<Integer,TalentInfo> getTalentInfoMap();
	
	//天赋升级数据
	TalentLevelUp getTalentLevelUp(int level);
	
	//天赋排行数据
	Map<Integer,Integer> getTalentRankMap();
	
	//天赋消耗数据
	TalentConsumeInfo getTalentConsumeInfo(byte type);
	
	//天赋描述数据
	TalentDes getTalentDes(int talentId);
	
	//天赋条件数据
	TalentCondition getTalentCondition(byte id);
	
	//天赋组数据
	List<TalentGroup> getTalentGroupList(byte type);
	
	//商店数据
	TalentShop getTalentShop();
	
	//初始时获得的天赋点总数
	int getInitTalentPoint() ;

}
