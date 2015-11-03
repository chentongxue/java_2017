package game_observer;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色
 */
public class HumanObject{

	public static int CLOSE_DELAY = 10;
	public int loadingNum = 0;								//正在加载玩家数据时的计数器 当等于0时代表加载完毕
	public long loadingPID = 0;								//正在加载玩家数据时的请求ID


	//客户端地图状态已准备完毕
	public boolean isClientStageReady;		
	//正在切换地图中
	public boolean isStageSwitching = false;	
	//玩家登陆状态判断 临时属性 0=无状态 1=登陆中 2=今日首次登陆中 
	public int loginStageState;	
	//是否本心跳监控玩家属性变化
	private boolean isHumanInfoListen;
	
	/* 聊天 */
	public long informLastSayTime;								//最后一次发言时间
	/* 级别技能SN：Level */
	public Map<Integer, Integer> relSkills = new HashMap<Integer, Integer>();
	/*伙伴碎片信息*/
	public Map<Integer,Integer> fragInfo = new HashMap<>();  		  //伙伴碎片数量MAP
	public Map<Integer,Integer> rareFragInfo = new HashMap<>();   //可以兑换稀有伙伴的碎片
	/*伙伴副本挑战次数     {副本类型：当日挑战次数}*/
	public Map<Integer,Integer> genTaskFightTimes = new HashMap<>();
	/*伙伴副本挑战时间记录*/
	public long genTaskFightTime = 0;
	
	
}
