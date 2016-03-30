package com.game.draco.app.rank.logic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.app.tower.domain.RoleTowerGate;
import com.game.draco.app.tower.domain.RoleTowerInfo;
import com.game.draco.message.item.RankDetailItem;

/**
 * 【爬塔】排行榜
 */
public class RankRoleTowerLogic extends RankRoleLogic{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static RankRoleTowerLogic instance = new RankRoleTowerLogic();
	private RankRoleTowerLogic(){
	}
	
	public static RankRoleTowerLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//爬塔排行不区分
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL){
			return false;
		}
		RoleTowerInfo towerInfo = GameContext.getTowerApp().getRoleTowerInfo(role.getRoleId());
		if(towerInfo == null){
			return false;
		}
		RoleTowerGate maxGate = towerInfo.getMaxGate();
		if(maxGate == null){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
	}
	
	public RankType getRankType() {
		return RankType.Tower ;
	}

	/*
	 * keyId#gate#layer#stars#level#exp#creatTime#BASE_INFO
	 1 keyId
	 2 #gate
	 3 #layer
	 4 #stars
	 5 #level
	 6 #exp
	 7 #creatTime
	 8 #BASE
	 (sort -t"#" -k 2nr  -k 3nr -k 4nr -k 5nr -k 6nr -k 7nr)
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		//排名 角色名 关卡 层数
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols, 9));
		//关卡
		item.setData2(this.get(cols, 2));
		//层
		item.setData3(this.get(cols,3));
		//星星
		item.setData4(this.get(cols,4));
		//roleId
		item.setKey(this.get(cols,1)) ;
		return item ;
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleTowerGate> roleTowerGates = GameContext.getRankDAO().selectAllTowerGate(
				"limit", this.getRecordLimit(rankInfo));
		if(Util.isEmpty(roleTowerGates)){
			return ;
		}
		for(RoleTowerGate rt : roleTowerGates){
			if(null == rt){
				continue;
			}
			//role是否
			RoleInstance role;
			try {
				role = GameContext.getUserRoleApp().getRoleByRoleId(rt.getRoleId());
			} catch (ServiceException e) {
				logger.error(e.toString());
				continue;
			}
			if(null == rt){
				continue;
			}
			if (!this.canPrintLog(role, rankInfo)) {
				continue;
			}
			if (this.doFrozenRole(role, rankInfo)) {
				continue;
			}
			this.printInitLog(rankInfo, rt, role);
		}
		
	}
	/*
	 * keyId#gate#layer#stars#level#exp#creatTime#BASE_INFO
	 */
	private void printInitLog(RankInfo rankInfo, RoleTowerGate rt, RoleInstance role) {
		if(null == rt || null == role){
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId()).append(CAT);
		sb.append(getTowerRoleData(rt)).append(CAT);
		sb.append(role.getLevel()).append(CAT);
		sb.append(role.getExp()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(getRoleBaseInfo(role));
		doWriteLogFile(rankInfo, false, null, sb.toString());
	}

	//除了封停的账号
	private boolean doFrozenRole(RoleInstance role, RankInfo rankInfo) {
		if (role.getFrozenEndTime() == null) {
			return false;
		}
        this.offRankLog(rankInfo,role.getRoleId());
		return true;
	}
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo,
			boolean schedulerFlag, String timeStr) {
		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId()).append(CAT);
		sb.append(getTowerRoleData(role.getRoleId())).append(CAT);
		sb.append(role.getLevel()).append(CAT);
		sb.append(role.getExp()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(getRoleBaseInfo(role));
		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
	}
	
	private StringBuilder getTowerRoleData(String roleId){
		StringBuilder sb = new StringBuilder();
		RoleTowerInfo towerInfo = GameContext.getTowerApp().getRoleTowerInfo(roleId);
		RoleTowerGate maxGate = towerInfo.getMaxGate();
		int gate = maxGate.getGateId();
		int layer = maxGate.getMaxLayer();
		byte stars = maxGate.totalStar();
		sb.append(gate).append(CAT).append(layer).append(CAT).append(stars);
		return sb;
	}
	private StringBuilder getTowerRoleData(RoleTowerGate maxGate){
		StringBuilder sb = new StringBuilder();
		int gate = maxGate.getGateId();
		int layer = maxGate.getMaxLayer();
		byte stars = maxGate.totalStar();
		sb.append(gate).append(CAT).append(layer).append(CAT).append(stars);
		return sb;
	}
}
