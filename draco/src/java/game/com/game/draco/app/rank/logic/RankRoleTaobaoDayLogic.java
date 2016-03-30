package com.game.draco.app.rank.logic;

import com.game.draco.GameContext;
import com.game.draco.app.compass.CompassType;
import com.game.draco.app.compass.config.Compass;
import com.game.draco.app.rank.domain.RankDbInfo;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLogCountDB;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.ArrayList;
import java.util.List;


/**
 * 每日淘宝
 */
public class RankRoleTaobaoDayLogic extends RankRoleLogic{

	private static RankRoleTaobaoDayLogic instance = new RankRoleTaobaoDayLogic();
	private RankRoleTaobaoDayLogic(){
	}
	
	public static RankRoleTaobaoDayLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		if(!rankInfo.isInStatDate()){
			return ;
		}
		this.doPrintLog(role,rankInfo,false,null);
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
		if(CompassType.taitan.getType() == rankInfo.getSubType()){
			return count.getTodayTaitan() ;
		}
		return count.getTodayJulong() ;
	}


	@Override
	public RankType getRankType() {
		return RankType.Role_Taobao ;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 公会 阵营 淘宝次数
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
		//淘宝数
		item.setData3(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}

	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleCount> roleCountList = GameContext.getRankDAO().selectTaobao(
				rankInfo.getSubType(), this.getRecordLimit(rankInfo));
		if(Util.isEmpty(roleCountList)){
			return ;
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
		sb.append(getRoleBaseInfo(role));
		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
		
	}
}
