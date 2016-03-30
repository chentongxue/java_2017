package com.game.draco.app.rank;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.rank.domain.RankDbInfo;
import com.game.draco.app.rank.domain.RankGroup;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.domain.RankLayout;
import com.game.draco.app.rank.domain.RankLogData;
import com.game.draco.app.rank.domain.RankLogRoleInfo;
import com.game.draco.app.rank.domain.RankReward;
import com.game.draco.app.rank.domain.RankRewardRank;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.app.union.domain.Union;

public interface RankApp extends Service, AppSupport{
	
	public RankLayout getRankLayout(int rankType) ;
	
	public RankInfo getRankInfo(int rankId) ;
	
	public List<RankGroup> getRankGroupList() ;
	
	/**
	 * 获得活动排行榜list
	 * @return
	 */
	public void addActiveRank(RankInfo rankInfo);

	
	public List<RankRewardRank> getRewardRankList(int level, byte sex, int rankId);
	
	/**
	 * 返回角色当前能获得的奖励
	 * @return
	 */
	public RankReward getRankReward(RankLogRoleInfo roleInfo, short rank, int rankId);
	
	public RankReward getRankReward(String key);
	
	public void printRoleRankLog(RoleInstance role, boolean schedulerFlag, String timeStr);
	
	/**
	 * 定时打印在线用户的排行日志（每小时第57分钟）
	 */
	public void printRankLogTimer();
	/**
	 * 定时切换日志文件（每小时第5秒）
	 */
	public void switchRankLogTimer();
	
	/**
	 * 根据排行榜大类型，小类型，玩家职业决定是否取的排行榜数据库信息
	 * @param role
	 * @param ranInfo
	 * @return
	 */
	public RankDbInfo getRankDbInfo(RoleInstance role, RankInfo ranInfo);
	/**
	 * 某种排行榜主体下榜的时候输出日志，比如幻兽打包
	 * @param rankType 排行榜类型
	 * @param id 主体id
	 */
	public void printLogOffRank(RankType rankType, String id);
	

	/**
	 * 公会排行榜下线
	 * @param union
	 */
	public void unionOffRank(String unionId) ;
	
	/**
	 * 装备排行榜下线
	 * @param roleGoods
	 */
	public void equipOffRank(RoleGoods roleGoods) ;

	public void updateTaobao(RoleInstance role,short taobaoType) ;
	
	public void updateDonate(RoleInstance role,int rankId) ;
	/**
	 * 0811,0813
	 * 得到排行榜分页数据
	 * 返回文件头格式###2012-11-11-09#curPage#totalPage#总记录数
	 * 请求url：http://host:port/data/appid/serverId/RankId/least(时间：2012-07-17-16)/页码
	 * @return RankLogData
	 */
	public RankLogData getPageData(int rankId, int page);

	/**
	 * 0812
	 * 得到某个玩家的某个排行榜的名次
	 * 返回文件头格式###2012-11-11-09
	 * 请求url：http://host:port/rolesort/appid/serverId/RankId/least(时间：2012-07-17-16)/roleId
	 * @return 名称
	 */
	public RankLogRoleInfo getRoleRank(int rankId, String roleId);
	/**
	 * 处理发奖周期为一个小时
	 */
	public void reward();
	
	/**
	 * 用db数据初始化log
	 */
	public RankInitResult initLogDataFormDB(int[] rankIds);
	/**
	 * gm工具补救发奖
	 * @param rankId 排行榜id
	 * @param date 补救发奖的日期 格式（2012-03-15-08）
	 * @return
	 */
	public Result rewardByMailFromGM(int rankId, String date);
	
	public String[] getPageOriginalData(int rankId,short page);
	
	
	/**
	 * 玩家不在线处理排行榜除名
	 * @param roleId
	 */
	public void offlineRoleOffRank(String roleId);
	
	/**
	 * 转阵营角色相关排行榜下榜
	 * @param roleId
	 * @param oldCampId 转阵营前的阵营id
	 */
	public void roleChangeCampOffRank(String roleId, byte oldCampId);
	public void writeLogFile(String fileName, String logStr);
	/**
	 * 获得18个最强公会的id
	 * @return
	 * @date 2015-3-3 下午05:44:23
	 */
	public List<String> getRankedUnionIds(int unionidsNum);

}
