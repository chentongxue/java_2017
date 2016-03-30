//package sacred.alliance.magic.app.active.rank;
//
//import java.util.Map;
//
//import sacred.alliance.magic.app.active.ActiveSupport;
//import sacred.alliance.magic.app.rank.domain.RankInfo;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.Service;
//import sacred.alliance.magic.domain.RankDbInfo;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public interface ActiveRankApp extends ActiveSupport, Service{
//	public static final byte REWARD_STAT_ERROR = -1; //错误，不存在活动排行等
//	public static final byte REWARD_STAT_NO = 0; //无奖励
//	public static final byte REWARD_STAT_DISABLE = 1; //有奖励但尚不能领取
//	public static final byte REWARD_STAT_ENABLE = 2; //可领奖
//	public static final byte REWARD_STAT_REWARDED = 3; //已领奖
//	
//	public final String CAT = "--";
//	/**
//	 * 上线加载排行榜活动数据
//	 */
//	public abstract void loadRoleRank(RoleInstance role);
//	
//	/**
//	 * 下线排行榜活动数据入库
//	 * */
//	public abstract void saveRoleRank(RoleInstance role);
//	/**
//	 * 返回所有的活动排行榜 
//	 */
//	public Map<Short, ActiveRankInfo> getAllActiveRankMap();
//	/**
//	 * 返回活动排行榜领奖状态：0:无奖励 1:有奖励但尚不能领取 2:可领奖 3:已领奖
//	 * @param role
//	 * @param rankItem
//	 * @return -1 :error
//	 */
//	public byte getRewardStat(RoleInstance role, RankInfo rankItem);
//	
//	/**
//	 * 排行榜领奖信息实时入库
//	 */
//	public abstract void realTimeWriteDB(RankDbInfo rankDbInfo);
//
//	/**
//	 * 入库失败日志
//	 * @param role
//	 */
//	public abstract void offlineLog(RoleInstance role);
//  /**
//   * 创建活动排行榜详细信息消息
//   * @param role
//   * @param activeId
//   * @return
//   */
//	public Message createRankDetailMsg(RoleInstance role, short activeId);
//	
//}
