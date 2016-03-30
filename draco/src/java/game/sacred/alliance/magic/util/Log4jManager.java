package sacred.alliance.magic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jManager {
	private static boolean canStart = true;
	public Log4jManager() {
	}
	
	/**
	 * 根据数据校验结果判断是否正常启动系统
	 */
	public static boolean canStart() {
		return canStart;
	}

	/**
	 * 设置数据校验状态
	 * 
	 * @param canStart
	 */
	public static void checkFail() {
		if (canStart == false) {
			return;
		}
		canStart = false;
	}

	public static Logger defaultLog(Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	public static Logger defaultLog(String name) {
		return LoggerFactory.getLogger(name);
	}

	public static Logger getLogger(Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	public static Logger getLogger(String name) {
		return LoggerFactory.getLogger(name);
	}

	public static final Logger CHECK = LoggerFactory.getLogger("check");
	public static final Logger AT_LOG = LoggerFactory.getLogger("at");
	public static final Logger LOOP_LOG = LoggerFactory.getLogger("loop");
	public static final Logger USER_LINE = LoggerFactory.getLogger("userline");
	public static final Logger CHARGE_MONEY_LOG = LoggerFactory.getLogger("chargemoneylog");
	public static final Logger CHANGE_MONEY_ERROR = LoggerFactory.getLogger("changemoneyerror");
	public static final Logger CHANGE_DKP_ERROR = LoggerFactory.getLogger("changedkperror");
	public static final Logger USER_PAY = LoggerFactory.getLogger("userpay");
	
	//拍卖行行
	public static final Logger LOG_AUCTION_PUT = LoggerFactory.getLogger("auctionput");
	public static final Logger LOG_AUCTION_REMOVE = LoggerFactory.getLogger("auctionremove");
	public static final Logger LOG_AUCTION_EXPIRED = LoggerFactory.getLogger("auctionexpired");
	
	//加速日志
	public static final Logger SPEEDUP_LOG = LoggerFactory.getLogger("speeduplog");
	//排行榜
	public static final Logger RANK_REWARD_BYMAIL_LOG = LoggerFactory.getLogger("rankrewardbymail");
	
	//乐翻天全民捐献日志
	public static final Logger DONATE_SCORE_LOG = LoggerFactory.getLogger("donate_score");
	
	//下线入库需要打印的日志
	//角色
	public static final Logger OFFLINE_COUNT_DB_LOG = LoggerFactory.getLogger("offline_countdb");
	//物品
	public static final Logger OFFLINE_GOODS_DB_LOG = LoggerFactory.getLogger("offline_goods");
	//排行榜
	public static final Logger OFFLINE_ROLE_DB_LOG = LoggerFactory.getLogger("offline_role");
	//排行榜活动
	public static final Logger OFFLINE_RANK_ACTIVE_DB_LOG = LoggerFactory.getLogger("offline_rankactivedb");
	//擂台赛
	public static final Logger OFFLINE_ARENA_DB_LOG = LoggerFactory.getLogger("offline_arenadb");
	//系统设置
	public static final Logger OFFLINE_SYS_SET_DB_LOG = LoggerFactory.getLogger("offline_syssetdb");
	//兑换信息
	public static final Logger OFFLINE_EXCHANGE_DB_LOG = LoggerFactory.getLogger("offline_exchangedb");
	//坐骑
	public static final Logger OFFLINE_MOUNT_DB_LOG = LoggerFactory.getLogger("offline_mountdb");
	//VIP
	public static final Logger OFFLINE_VIP_DB_LOG = LoggerFactory.getLogger("offline_vipdb");
	//VIP充值
	public static final Logger VIP_DB_LOG = LoggerFactory.getLogger("vip_db");
	//折扣活动
	public static final Logger OFFLINE_DISCOUNT_ACTIVE_DB_LOG = LoggerFactory.getLogger("offline_discountactivedb");
	//下线时出现的error日志
	public static final Logger OFFLINE_ERROR_LOG = LoggerFactory.getLogger("offline_error");
//	//门派信息
	public static final Logger UNION_LOG = LoggerFactory.getLogger("union");
//	//门派成员信息
	public static final Logger UNION_MEMBER_LOG = LoggerFactory.getLogger("union_member");
	//召唤信息
	public static final Logger OFFLINE_SUMMON_DB_LOG = LoggerFactory.getLogger("offline_summondb");
	//嘉年华符合条件的发奖
	public static final Logger CARNIVAL_ALL_REWARD = LoggerFactory.getLogger("carnival_all_reward");
	//嘉年华已经发奖日志
	public static final Logger CARNIVAL_ALREADY_REWARD = LoggerFactory.getLogger("carnival_already_reward");
	//嘉年华符合条件的发奖
	public static final Logger OFFLINE_CARNIVAL = LoggerFactory.getLogger("offline_carnival");
	//阵营信息
	public static final Logger CAMP_LOG = LoggerFactory.getLogger("camp");
	//神秘商店信息
	public static final Logger OFFLINE_SHOP_SECRET = LoggerFactory.getLogger("offline_shop_secret");
	//管理控制
	public static final Logger ADMIN_LOG = LoggerFactory.getLogger("admin");
	//主线、支线任务完成日志
	public static final Logger QUEST_MAIN_BRANCH_COMPLETE = LoggerFactory.getLogger("quest_main_branch_complete");
	//回归奖励日志
	public static final Logger RECALL_AWARD_LOG = LoggerFactory.getLogger("recall_award");
	//npc定时刷新日志
	public static final Logger NPC_REFRESH_LOG = LoggerFactory.getLogger("npc_refresh");
	//npc定时刷新死亡日志
	public static final Logger NPC_REFRESH_DEATH_LOG = LoggerFactory.getLogger("npc_refresh_death");
	//npc定时刷新日志
	public static final Logger NPC_REFRESH_SPEAK_LOG = LoggerFactory.getLogger("npc_refresh_speak");
	// 排位赛排行榜日志
	public static final Logger QUALIFY_RANK = LoggerFactory.getLogger("qualify_rank");
//	//门派资金日志
//	public static final Logger FACTION_MONEY = LoggerFactory.getLogger("faction_money");
//	//门派押注获奖门派日志
//	public static final Logger FACTION_GAMBLE = LoggerFactory.getLogger("faction_gamble");
	
}
