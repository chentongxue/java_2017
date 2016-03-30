package sacred.alliance.magic.app.rank.type;

import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.base.CampType;
import com.game.draco.message.item.RankDetailItem;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankType;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * /**
 * log日志格式：roleId#honor#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RoleHonor extends RankRoleLogic {

	private static RoleHonor instance = new RoleHonor();
	private RoleHonor(){
	}
	
	public static RoleHonor getInstance(){
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
		sb.append(role.getHonor());
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		rankInfo.getLogger().info(sb.toString()) ;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 门派 阵营 荣誉
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols,4));
		//公会名
		item.setData2(this.getUnionName(this.get(cols,9)));
		//阵营名
		item.setData3(CampType.get(Byte.parseByte(this.get(cols,8))).getName());
		//荣誉
		item.setData4(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleInstance> roleList = GameContext.getRankDAO().selectHonorRole("career", 
				rankInfo.getSubType(), 
				"limit", getRecordLimit(rankInfo));
		if(Util.isEmpty(roleList)){
			return ;
		}
		for(RoleInstance role : roleList){
			if(null == role){
				continue;
			}
			printLog(role, rankInfo);
		}
	}
	
	@Override
	public RankType getRankType() {
		return RankType.Role_Honor ;
	}

}
