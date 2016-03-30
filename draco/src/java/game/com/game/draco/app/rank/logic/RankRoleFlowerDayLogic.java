package com.game.draco.app.rank.logic;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankDbInfo;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogCountDB;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;


/**
 * 每日鲜花榜
 */
public class RankRoleFlowerDayLogic extends RankRoleLogic{

	private static RankRoleFlowerDayLogic instance = new RankRoleFlowerDayLogic();
	private RankRoleFlowerDayLogic(){
	}
	
	public static RankRoleFlowerDayLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据性别匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != role.getSex()){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		if(!rankInfo.isActiveRank() || data1<=0 || !rankInfo.isInStatDate()){
			return ;
		}
		//统计活动期间内充值
		RankDbInfo rankDbInfo = GameContext.getRankApp().getRankDbInfo(role, rankInfo);
		if(null == rankDbInfo){
			return ;
		}
		rankDbInfo.setCount0(rankDbInfo.getCount0() + data1);
	}
	
	
	private int getData(RoleInstance role, RankInfo rankInfo){
		if(rankInfo.isActiveRank()){
			RankDbInfo rankDbInfo = role.getRankDbInfo().get(rankInfo.getId());
			if(null == rankDbInfo){
				return 0 ;
			}
			return rankDbInfo.getCount0();
		}
		RoleCount count = role.getRoleCount() ;
		if(null == count){
			return 0 ;
		}
		return count.getTodayFlowerNum() ;
	}


	@Override
	public RankType getRankType() {
		return RankType.Role_Flower_Day ;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 公会 阵营 鲜花次数
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols, 7));
		//公会名
		item.setData2(this.get(cols, 11));//12
		//阵营名
//		item.setData3(CampType.get(Byte.parseByte(this.get(cols, 10)))
//				.getName());
		//鲜花数
		item.setData3(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}

	@Override
	public void initLogData(RankInfo rankInfo){
		List<RankLogCountDB> rankLogCountDBList = GameContext.getRankDAO().selectTodayFlower("sex", rankInfo.getSubType(), 
				"limit", this.getRecordLimit(rankInfo));
		if(Util.isEmpty(rankLogCountDBList)){
			return ;
		}
		List<RoleCount> roleCountList = new ArrayList<RoleCount>() ;
		for(RankLogCountDB rankLogCountDB : rankLogCountDBList){
			if(null == rankLogCountDB){
				continue;
			}
			roleCountList.add(this.getRoleCount(rankLogCountDB));
		}
		initRoleCountLog(roleCountList, rankInfo);
	}
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo,
			boolean schedulerFlag, String timeStr) {
		int data = this.getData(role, rankInfo);
		if(data <= 0){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId()).append(CAT);
		sb.append(data).append(CAT);
		sb.append(role.getLevel()).append(CAT);
		sb.append(role.getExp()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		try {
			sb.append(getRoleBaseInfo(role));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
		
	}
}
