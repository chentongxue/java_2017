package com.game.draco.app.rank.logic;

import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.app.richman.domain.RoleRichMan;
import com.game.draco.base.CampType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 大富翁排行榜
 * 无筛选条件
 * 单独的UI，显示在大富翁的UI上
 */
public class RankRoleRichManLogic extends RankRoleLogic {

	private static RankRoleRichManLogic instance = new RankRoleRichManLogic();
	private RankRoleRichManLogic(){
	}
	
	public static RankRoleRichManLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
		
	}
	/**
	 *  #keyId#todaycoupon#totalcoupon#createtime
	 *  #roleId#roleName#gender#level#campId#uionId#unionName
	 *  打印线上的数据，包括玩家上下线和定时打印
	 */
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {

		int roleId = role.getIntRoleId();
		int todaycoupon = GameContext.getRichManApp().getTodayCoupon(roleId);
		int totalcoupon = GameContext.getRichManApp().getTotalCoupon(roleId);
		//无分数不记录
		if (todaycoupon <= 0 && totalcoupon <= 0) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(role.getRoleId()).append(CAT);
		sb.append(todaycoupon).append(CAT);
		sb.append(totalcoupon).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(getRoleBaseInfo(role));

		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
	}
	/*
	 * #keyId#todaycoupon#totalcoupon#createtime#roleId#roleName#gender#level#campId#uionId#unionName
	 1 #keyId
	 2 #todaycoupon
	 3 #totalcoupon
	 4 #createtime
	 5 #roleId
	 6 #roleName
	 7 #gender
	 8 #level
	 9 #campId
	 10 #uionId
	 11 #unionName
	 * 今日点券 > 总点券数 > 角色创建时间
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		//名次、角色名、所属公会名、所属阵营、今日获得点券
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//角色名
		item.setData1(this.get(cols, 6));
		//公会名
		item.setData2(this.get(cols, 10));//11
		//阵营名
//		item.setData3(CampType.get(Byte.parseByte(this.get(cols, 9))).getName());
		//今日获得点券
		item.setData3(this.get(cols,2));
		//roleId
		item.setKey(this.get(cols, 1)) ;
		return item ;
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		int limit = getRecordLimit(rankInfo);//100
		
		List<RoleRichMan> rmList = GameContext.getRankDAO().selectAllRichMan("limit", limit);
		if(Util.isEmpty(rmList)){
			return;
		}
		for (RoleRichMan roleRichMan : rmList) {
			if(roleRichMan==null){
				continue;
			}
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleRichMan.getRoleId()+"");
			if(null == role){
				continue ;
			}
			StringBuilder sb = new StringBuilder();
			try{
				int todaycoupon = roleRichMan.getTodayCoupon();
				int totalcoupon = roleRichMan.getTotalCoupon();
				sb.append(role.getRoleId()).append(CAT);
				sb.append(todaycoupon).append(CAT);
				sb.append(totalcoupon).append(CAT);
				sb.append(getRoleCreatTimeStr(role)).append(CAT);
				sb.append(getRoleBaseInfo(role));
			}catch (Exception e) {
				continue;
			}
			doWriteLogFile(rankInfo, false, null, sb.toString());
		}
	}
	
	@Override
	public RankType getRankType() {
		return RankType.ROLE_RICHMAN ;
	}

}
