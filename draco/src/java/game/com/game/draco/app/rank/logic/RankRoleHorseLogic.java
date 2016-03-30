package com.game.draco.app.rank.logic;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;
import com.game.draco.GameContext;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：roleId#exp#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RankRoleHorseLogic extends RankRoleLogic {

	private static RankRoleHorseLogic instance = new RankRoleHorseLogic();
	private RankRoleHorseLogic(){
	}
	
	public static RankRoleHorseLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据职业匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != role.getCampId()){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		
	}
	/**
	 * keyId#battleScore#horseQuality#horselevel#createtime#horseId#horseName#
	 * roleId#roleName#gender#campId#uionId#uionName
	 */
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {
		if(role==null){
			return;
		}
		
		Map<Integer,RoleHorse> map = GameContext.getRoleHorseApp().getAllRoleHorseByRoleId(role.getIntRoleId());
		if(Util.isEmpty(map)){
			return;
		}
		for(Map.Entry<Integer, RoleHorse> entry:map.entrySet())
		{   
			RoleHorse roleHorse = entry.getValue();
			if(roleHorse==null){
				continue;
			}
			try 
			{
				String s = getLogMessage(role, roleHorse);
				doWriteLogFile(rankInfo, schedulerFlag, timeStr, s);
			} catch (Exception e) {
				continue;
			}
		}
		
	}

	private String getLogMessage(RoleInstance role, RoleHorse roleHorse) throws Exception {
		StringBuilder sb = new StringBuilder();
		int horseBattleScore = roleHorse.getBattleScore();
		//坐骑名称
		String horseName = GameContext.getRoleHorseApp().getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar()).getHorseName();
		sb.append(role.getRoleId()).append(UNDERLINE_CAT).append(roleHorse.getHorseId()).append(CAT);
		sb.append(horseBattleScore).append(CAT);
		sb.append(roleHorse.getQuality()).append(CAT);
		sb.append(roleHorse.getStar()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(roleHorse.getHorseId()).append(CAT);
		sb.append(horseName).append(CAT);  
		sb.append(getRoleBaseInfo(role));
		return sb.toString();
	}
	/*
	 1 #keyId
	 2 #horseBattleScore
	 3 #horseQuality
	 4 #horse Star
	 5 #createtime
	 6 #horseId
	 7 #horseName
	 * 
	 8 #roleId
	 9 #roleName
	 10 #gender
	 11 #level
	 12 #campId
	 13 #uionId
	 14 #unionName
	 * 名次、坐骑名、坐骑阶数、所属角色名、该坐骑的战斗力
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//坐骑名
		item.setData1(this.get(cols, 7));
		//坐骑阶数
		item.setData2(getHorseQualityStar(cols));//this.get(cols, 3) +Cat.slash+ this.get(cols, 4));
		//所属角色名
		item.setData3(this.get(cols, 9));
		//该坐骑的战斗力
		item.setData4(this.get(cols, 2));
		//roleId 便于查看角色信息
		item.setKey(this.get(cols, 8)) ;
		return item ;
	}
	private String getHorseQualityStar(String[] cols) {
		if(Util.isEmpty(cols)){
			return null;
		}
		return this.get(cols, 3) + getText(TextId.RANK_HORSE_QUALITY) + this.get(cols, 4) + getText(TextId.RANK_HORSE_STAR);
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}
	/**
	 * 初始化，从DB里取数据
	 */
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleHorse> roleHorseList = GameContext.getRankDAO().selectHorseRole("campId", 
				rankInfo.getSubType(), 
				"limit", getRecordLimit(rankInfo));
		
		if(Util.isEmpty(roleHorseList)){
			return ;
		}
		for(RoleHorse roleHorse : roleHorseList){
			if(roleHorse==null){
				continue;
			}
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleHorse.getRoleId()+"");
			if(null == role){
				continue ;
			}
			try 
			{
				String s = getLogMessage(role, roleHorse);
				doWriteLogFile(rankInfo, false, null, s);
			} catch (Exception e) {
				continue;
			}
		}
	}
	
	@Override
	public RankType getRankType() {
		return RankType.ROLE_HORSE ;
	}

}
