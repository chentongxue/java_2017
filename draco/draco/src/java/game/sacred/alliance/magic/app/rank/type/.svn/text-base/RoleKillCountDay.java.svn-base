package sacred.alliance.magic.app.rank.type;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.base.CampType;
import com.game.draco.message.item.RankDetailItem;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankLogCountDB;
import sacred.alliance.magic.app.rank.RankType;
import sacred.alliance.magic.domain.RankDbInfo;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 
 * log日志格式：roleId#killCountDay#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 过滤条件:淘宝类型
 */
public class RoleKillCountDay extends RankRoleLogic {
	
	private static RoleKillCountDay instance = new RoleKillCountDay();
	
	public static RoleKillCountDay getInstance(){
		return instance;
	}

	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据阵营匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != role.getCampId()){
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
		//统计活动期间内杀人数
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
		return count.getTodayKillCount();
	}

	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo) {
		int data = this.getData(role, rankInfo);
		if(data <=0){
			return ;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId());
		sb.append(CAT);
		sb.append(data);
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		rankInfo.getLogger().info(sb.toString()) ;
	}

	@Override
	public RankType getRankType() {
		return RankType.Role_KillCount_Day;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 等级 阵营 当天杀人数
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols,4));
		//公会
		item.setData2(this.getUnionName(this.get(cols,7)));
		//阵营名
		item.setData3(CampType.get(Byte.parseByte(this.get(cols,8))).getName());
		//杀人数
		item.setData4(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RankLogCountDB> rankLogCountDBList = GameContext.getRankDAO().selectTodayKillCount("campId", 
				rankInfo.getSubType(),"limit", getRecordLimit(rankInfo));
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

}
