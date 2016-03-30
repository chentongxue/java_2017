package com.game.draco.app.rank.logic;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.unionbattle.domain.UnionIntegralRank;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：UnionId#integral#level#creatTime##unionId#uionname#leaderId#leadername
 * 返回的日志格式：sort#log日志格式
 */
public class RankUnionIntegralLogic extends RankUnionLogic{
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static RankUnionIntegralLogic instance = new RankUnionIntegralLogic();
	private RankUnionIntegralLogic(){
	}
	
	public static RankUnionIntegralLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(Union t, RankInfo rankInfo) {
		return true;
	}

	@Override
	public void count(Union t, RankInfo rankInfo, int data1, int data2) {
		//无需实现此接口
	}
	/**
	 * 定时调用
     * keyId#integral#level#uioncreatedate#unionId#uionname#leaderId#leadername
	 */
	@Override
	protected void doPrintLog(Union t, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {
		if(t == null){
			return;
		}
		Map<String, UnionIntegralRank> map = GameContext.getUnionIntegralBattleApp().getIntegralMap();
		if(Util.isEmpty(map)){
			return;
		}
		UnionIntegralRank uk = map.get(t.getUnionId());
		if(null == uk){
			return;
		}
		if(uk.getIntegral() == 0){
			return;
		}
		try {
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleId(String.valueOf(t.getLeaderId()));
			StringBuilder sb = new StringBuilder();
			sb.append(t.getUnionId()).append(CAT);
			sb.append(uk.getOldIntegral()).append(CAT);
			sb.append(t.getUnionLevel()).append(CAT);
			sb.append(t.getCreateTime()).append(CAT);
			sb.append(t.getUnionId()).append(CAT);//删除
			sb.append(t.getUnionName()).append(CAT);
			sb.append(getRoleBaseInfo(role));
			doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
		} catch (ServiceException e) {
			logger.error(e.toString());
		}


	}
	/** 玩家基本信息占六个栏位*/
	public String getRoleBaseInfo(RoleInstance role){
		String unionName = getUnionName(role.getUnionId());
		
		StringBuffer sb = new StringBuffer();
		sb.append(role.getRoleId());
		sb.append(CAT);
		sb.append(role.getRoleName());
		sb.append(CAT);
		sb.append(role.getSex());
		sb.append(CAT);
		sb.append(role.getLevel());
		sb.append(CAT);
		sb.append(role.getUnionId());
		sb.append(CAT);
		sb.append(unionName);
		return sb.toString();
	}
	@Override
	public RankType getRankType() {
		return RankType.Union_Integral ;
	}

	@Override
	public void initLogData(RankInfo rankInfo) {
		Map<String, Union> UnionMap = GameContext.getUnionApp().getUnionMap();
		if(Util.isEmpty(UnionMap)){
			return ;
		}
		for(Union t : UnionMap.values()){
			this.printLog4init(t, rankInfo);
		}
	}
	/*
	 * keyId#integral#uioncreatedate#unionId#uionname#leaderId#leadername
	 1 keyId
	 2 #integral
	 3 #level
	 4 #uioncreatedate
	 5 #unionId
	 6 #uionname
	 7 #leaderId
	 8 #leadername
	 公会积分 > 公会等级 > 公会创建时间
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		//名次、公会名、（所属阵营）、公会会长、积分
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//公会名
		item.setData1(this.get(cols, 6));
		//公会会长
		item.setData2(this.get(cols, 8));
		//公会积分
		item.setData3(this.get(cols, 2));
		//UnionId
		item.setKey(this.get(cols, 1)) ;
		return item ;
	}

}
