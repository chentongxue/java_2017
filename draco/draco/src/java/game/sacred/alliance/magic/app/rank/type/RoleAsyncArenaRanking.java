package sacred.alliance.magic.app.rank.type;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankLogCountDB;
import sacred.alliance.magic.app.rank.RankType;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：roleId#roleName#honor#
 * 返回的日志格式：sort#log日志格式
 * 过滤条件:无
 */
public class RoleAsyncArenaRanking extends RankRoleLogic {
	
	private static RoleAsyncArenaRanking instance = new RoleAsyncArenaRanking();
	
	public static RoleAsyncArenaRanking getInstance(){
		return instance;
	}

	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		if(!rankInfo.isActiveRank() || data1<=0 || !rankInfo.isInStatDate()){
			return ;
		}
	}
	
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo) {
		AsyncArenaRole asyncArenaRole = GameContext.getRoleAsyncArenaApp().getRoleAsyncArenaInfo(role);
		if(asyncArenaRole == null){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId());
		sb.append(CAT);
		sb.append(role.getRoleName());
		sb.append(CAT);
		sb.append(asyncArenaRole.getHistoryHonor());
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		rankInfo.getLogger().info(sb.toString()) ;
	}

	@Override
	public RankType getRankType() {
		return RankType.Role_Async_Arena_Ranking;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 等级 阵营 荣誉值
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//荣誉
		item.setData1(this.get(cols,3));
		//角色名
		item.setData4(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleArena> roleArenaList = GameContext.getRankDAO().selectAllArena("limit", getRecordLimit(rankInfo)*5);
		
		for(RoleArena roleArena : roleArenaList){
			if(null == roleArena){
				continue;
			}
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleArena.getRoleId());
			if(null == role){
				continue ;
			}
			role.setRoleArena(roleArena);
			printLog(role, rankInfo);
		}
	}

}
