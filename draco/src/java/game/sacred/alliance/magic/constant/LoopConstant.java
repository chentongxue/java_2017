package sacred.alliance.magic.constant;

public class LoopConstant {

	public static final long HIT_COMBO_CYCLE = 1*1000 ;
	/**判断是否销毁地图实例,周期可以到分钟级别*/
	public static final long MAP_INSTANCE_DESTRPY_CYCLE = 30*1000 ;
	public static final long MAP_INSTANCE_DEFAULT_CYCLE = 1*1000 ;
	public static final long MAP_INSTANCE_LONELYROLE_CYCLE = 10*1000 ;
	public static final long ROLE_BUFF_DEFAULT_CYCLE = 250 ;
	public static final long HERO_HP_HEALTH_CYCLE = 5*1000 ;
	public static final long NPC_DEFAULT_CYCLE = 1*1000 ;
	public static final long NPC_TARGETED_MOVE_CYCLE = 50 ;
	public static final long NPC_RANDOM_MOVE_CYCLE = 8*1000 ;
	public static final long ROLE_DEFAULT_RECOVERY_CYCLE = 5*1000 ;
	public static final long NPC_ESCAPE_CYCLE = 100 ;
	public static final long ROLE_QUEST_CYCLE = 1000*30;
	public static final long REBORN_NPC_CYCLE = 1000*2;
	
	public static final long MONSTER_REFRESH_CYCLE = 1000*5;
	
	/***/
	public static final long MAP_COPY_TIME = 5*1000;
	public static final long TIMEREFRESH_NPC_CYCLE = 1000*60;	
	
	/** 交易的默认时间 */
	public final static long TRADING_CACHE_MILLIS = 1*60*1000;
	
	public static final long ANGERVALUE_CIRCLE_CYCLE = 2*1000 ;
	
	
	/** 地图刷怪逻辑遍历时间 */
	public static final long NPC_REFRESH_CYCLE = 5 * 1000;
	
	/** 称号过期周期 */
	public final static long TITLE_TIMEOUT_CYCLE = 2*60*1000;
	
	/** 定时入库周期 30分钟 */
	public final static long TIMING_WRITEDB_CYCLE = 30*60*1000;
	
	/**怪物攻城刷新NPC时间*/
	public static final long SIEGE_CIRCLE_CYCLE = 1*1000 ;
	
	/**神仙宝箱时间*/
	public static final long ANGEL_CHEST_REFRESH_CIRCLE_CYCLE = 5*1000 ;
	
	public static final long ANGEL_CHEST_RESET_CIRCLE_CYCLE = 10*1000 ;
	/** 大富翁状态时间 */
	public static final long RICHMAN_CIRCLE_CYCLE = 500 ;
}
