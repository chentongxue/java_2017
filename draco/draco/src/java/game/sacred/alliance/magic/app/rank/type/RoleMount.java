package sacred.alliance.magic.app.rank.type;

import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankType;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.RankDetailItem;


/**
 * 
 * log日志格式：roleId#进化等级#战斗力#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RoleMount extends RankRoleLogic{

	private static RoleMount instance = new RoleMount();
	private RoleMount(){
	}
	
	public static RoleMount getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance t, RankInfo rankInfo) {
		//根据职业匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != t.getCareer()){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance t, RankInfo rankInfo, int data1, int data2) {
		
	}

	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo) {/*
		sacred.alliance.magic.domain.RoleMount mount = role.getRoleMount() ;
		if(null == mount){
			return ;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(role.getRoleId());
		sb.append(CAT);
		//进阶等级
		sb.append(mount.getMountLevel());
		sb.append(CAT);
		//战斗力
		sb.append(mount.getRealBattleScore());
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		rankInfo.getLogger().info(sb.toString());
	*/}

	
	@Override
	public RankType getRankType() {
		return RankType.Role_Mount ;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		return null ;
		/*
		//排名 角色名 阵营 进化等级 战斗力
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols,5));
		//阵营
		item.setData2(CampType.get(Byte.parseByte(this.get(cols,9))).getName());
		//进化等级
		
		byte mountLevel = Byte.valueOf(this.get(cols,2));
		item.setData3(this.getStarAndQuality(mountLevel));
		//战斗力
		item.setData4(this.get(cols,3));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	*/}
	
	@Override
	public void initLogData(RankInfo rankInfo){/*
		List<sacred.alliance.magic.domain.RoleMount> roleMountList = GameContext.getRankDAO().selectAllMount("limit", getRecordLimit(rankInfo)*5);
		for(sacred.alliance.magic.domain.RoleMount mount : roleMountList){
			if(null == mount){
				continue;
			}
			
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", mount.getRoleId());
			if(null == role){
				continue ;
			}
			role.setRoleMount(mount);
			printLog(role, rankInfo);
		}
	*/}
	
	
	
	@Override
	public void frozenRoleOffRankLog(RoleInstance role, RankInfo rankInfo) {/*
		sacred.alliance.magic.domain.RoleMount mount = role.getRoleMount() ;
		if(null == mount){
			return ;
		}
		rankInfo.getLogger().info(LOG_OFFRANK_FLAG + role.getRoleId());
	*/}

}
