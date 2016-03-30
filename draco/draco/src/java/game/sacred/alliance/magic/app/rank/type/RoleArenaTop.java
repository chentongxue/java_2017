package sacred.alliance.magic.app.rank.type;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.base.CampType;
import com.game.draco.message.item.RankDetailItem;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankType;
import sacred.alliance.magic.domain.RoleArena;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 
 * log日志格式：roleId#score#cycle1v1Score#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RoleArenaTop extends RankRoleLogic {
	private static RoleArenaTop instance = new RoleArenaTop();
	private RoleArenaTop(){
	}
	
	public static RoleArenaTop getInstance(){
		return instance ;
	}

	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据职业匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != role.getCareer()){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		
	}

	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId());
		sb.append(CAT);
		int topScore = 0 ;
		int cycle1v1Score = 0 ;
		RoleArena roleArena = role.getRoleArena();
		if(null != roleArena){
			topScore = roleArena.getTopScore() ;
			cycle1v1Score = roleArena.getCycle1v1Score() ;
		}
		sb.append(topScore);
		sb.append(CAT);
		sb.append(cycle1v1Score);
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		rankInfo.getLogger().info(sb.toString()) ;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 门派 阵营 大师赛积分
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols,5));
		//门派名
		item.setData2(getUnionName(this.get(cols,10)));
		//阵营名
		item.setData3(CampType.get(Byte.parseByte(this.get(cols,9))).getName());
		//大师赛积分
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
	
	@Override
	public RankType getRankType() {
		return RankType.Role_ArenaTop ;
	}

}
