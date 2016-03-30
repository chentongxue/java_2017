package com.game.draco.app.rank.logic;

import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：roleId#exp#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 */
public class RankRolePetLogic extends RankRoleLogic {

	private static RankRolePetLogic instance = new RankRolePetLogic();

	private RankRolePetLogic() {
	}

	public static RankRolePetLogic getInstance() {
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
	 * keyId#Petscore#PetGrade#Petlevel#createtime#PetId#Petname#
	 * roleId#roleName#gender#campId#uionId#uionName
	 */
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {
		if (role == null)
			return;
		String roleId = role.getRoleId();
		Map<Integer, RolePet> gsMap = GameContext.getUserPetApp().getAllRolePet(roleId);
		if (Util.isEmpty(gsMap)) {
			return;
		}
		for (RolePet rp : gsMap.values()) {
			if (Util.isEmpty(rp)) {
				return;
			}
			try {
				StringBuilder sb = getLogMessage(role, roleId, rp);
				doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());

			} catch (Exception e) {
				continue;
			}
		}
	}

	private StringBuilder getLogMessage(RoleInstance role, String roleId, RolePet rp) throws Exception {
		StringBuilder sb = new StringBuilder();
		// keyId#
		sb.append(roleId).append(UNDERLINE_CAT).append(rp.getPetId()).append(CAT);
		int battleScore = rp.getScore();
		sb.append(battleScore).append(CAT);
		sb.append(rp.getQuality()).append(CAT);
		sb.append(rp.getStar()).append(CAT);
		sb.append(rp.getLevel()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(rp.getPetId()).append(CAT);
		String gsName = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rp.getPetId()).getName();
		sb.append(gsName).append(CAT);
		sb.append(getRoleBaseInfo(role));
		return sb;
	}

	/*
	 * 1 keyId 
	 * 2 #petscore 
	 * 3 #petquality 
	 * 4 #petstar 
	 * 5 #petlevel 
	 * 6 #createtime 
	 * 7 #petId 
	 * 8 #petname=
	 * 9 #roleId 
	 * 10 #roleName 
	 * 11 #gender 
	 * 12 #campId 
	 * 13 #uionId 
	 * 14 #uionName
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		String[] cols = Util.splitStr(row, CAT);
		if (Util.isEmpty(cols)) {
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]);
		item.setRank(nowRank);
		// 宠物名
		item.setData1(this.get(cols, 8));
		// 宠物星级
		item.setData2(this.get(cols, 4));
		// 所属角色名
		item.setData3(this.get(cols, 10));
		// 该宠物的战斗力
		item.setData4(this.get(cols, 2));
		// roleId 便于查看角色信息
		item.setKey(this.get(cols, 8));
		return item;
	}

	@Override
	public void initLogData(RankInfo rankInfo) {
		List<RolePet> rsList = GameContext.getRankDAO().selectPetRole("campId", rankInfo.getSubType(), "limit", getRecordLimit(rankInfo));
		if (Util.isEmpty(rsList)) {
			return;
		}
		for (RolePet rp : rsList) {
			if (null == rp) {
				continue;
			}
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", rp.getMasterId() + "");
			if (role == null) {
				continue;
			}
			if (null == role) {
				continue;
			}
			try {
				StringBuilder sb = getLogMessage(role, role.getRoleId(), rp);
				doWriteLogFile(rankInfo, false, null, sb.toString());
			} catch (Exception e) {
				continue;
			}
		}
	}

	@Override
	public RankType getRankType() {
		return RankType.ROLE_PET;
	}

}
