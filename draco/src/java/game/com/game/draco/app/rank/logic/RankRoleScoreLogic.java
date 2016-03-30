package com.game.draco.app.rank.logic;

import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：roleId#score#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 
 * <DE>排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RankRoleScoreLogic extends RankRoleLogic {
	private static RankRoleScoreLogic instance = new RankRoleScoreLogic();

	private RankRoleScoreLogic() {
	}

	public static RankRoleScoreLogic getInstance() {
		return instance;
	}

	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		// 根据阵营匹配
		byte subType = rankInfo.getSubType();
		if (subType != RANK_ALL && subType != role.getCampId()) {
			return false;
		}
		// 判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {

	}

	/**
	 * 日志格式：
	 * keyId#score#level#exp#createtime#roleId#roleName#gender#level#campId#uionId#unionName
	 * 1988000007#794#9#2#1401194291000#1988000007#尤格尔·逐日#0#9#0##
	 */
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo,
			boolean schedulerFlag, String timeStr) {
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId()).append(CAT);
		sb.append(this.getRankBattleScore(role)).append(CAT);
		sb.append(role.getLevel()).append(CAT);
		sb.append(role.getExp()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(getRoleBaseInfo(role));
		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
	}

	private int getRankBattleScore(RoleInstance role) {
		return GameContext.getAttriApp().getEffectBattleScore(role);
	}

	/*
	 * 结果eg: 4#19878000007#794#9#2#1401194291000#1988000007#�ȸ������#0#9#0##
	 * 日志对照： 1988000007#794#9#2#1401194291000#1988000007#尤格尔·逐日#0#9#0## 定义对照：
	 * 角色基本信息BASE keyId#score#level#exp#createtime#
	 * roleId#roleName#gender#level#campId#uionId#unionName 完整对照 -1 名次 0
	 * keyId#1988000007 1 score#794 2 level#9 3 exp#2 4 createtime#1401194291000
	 * 5 roleId#1988000007 6 roleName#尤格尔·逐日 7 gender#0 8 level#9 9 campId#ZERO
	 * 10 uionId#NONE 11 unionName
	 * 
	 * 1#17000001#2987#18#93128#1402998071000#17000001#������˹������#1#18#2##
	 * 2#17000002#2278#1#535#1403173897000#17000002#��ʲ��ˡ�׷��#1#1#-1##
	 * 3#497000068#1690#2#588#1402997687000#497000068#��˹��¡��׻�#1#2#-1##
	 * 4#1988000007#794#9#2#1401194291000#1988000007#�ȸ������#0#9#0##
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		// 排名 角色名 公会 阵营 战斗力
		String[] cols = Util.splitStr(row, CAT);
		if (Util.isEmpty(cols)) {
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]);
		item.setRank(nowRank);
		// 角色名
		item.setData1(this.get(cols, 7));
		// 公会名
		// item.setData2(this.getUnionName(this.get(cols,9)));
		item.setData2(this.get(cols, 11));//12
//		// 阵营名
//		item.setData3(CampType.get(Byte.parseByte(this.get(cols, 10))).getName());
		// 战斗力
		item.setData3(this.get(cols, 2));
		// roleId
		item.setKey(this.get(cols, 1));
		return item;
	}

	@Override
	public void initLogData(RankInfo rankInfo) {
		List<RoleInstance> roleList = GameContext.getRankDAO().selectScoreRole(
				"campId", rankInfo.getSubType(), "limit",
				getRecordLimit(rankInfo));
		if (Util.isEmpty(roleList)) {
			return;
		}
		for (RoleInstance role : roleList) {
			if (null == role) {
				continue;
			}
			printLog4init(role, rankInfo);
		}
	}

	@Override
	public RankType getRankType() {
		return RankType.ROLE_SCORE;
	}

}
