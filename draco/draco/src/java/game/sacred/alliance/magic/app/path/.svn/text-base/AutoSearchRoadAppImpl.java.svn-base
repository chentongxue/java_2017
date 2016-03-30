package sacred.alliance.magic.app.path;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.config.ParasConfig;
import sacred.alliance.magic.app.map.point.JumpMapPoint;

import com.game.draco.GameContext;
import com.game.draco.message.item.PointItem;

public class AutoSearchRoadAppImpl implements AutoSearchRoadApp {
	private ParasConfig parasConfig;
	
	private SearchMapPoint findRoad(String p1_startMap, //起始地图
									String p2_arriveMap//到达目标地图
									) {
		
		List<SearchMapPoint> open = new ArrayList<SearchMapPoint>();// 搜索集合
		List<SearchMapPoint> close = new ArrayList<SearchMapPoint>();// 已经搜索过的集合

		/** 获取起始地图的连接关系即各跳转点 **/
		List<JumpMapPoint> curMapPoint = GameContext.getMapApp().getMap(p1_startMap)
												.getJumpMapPointCollection().getPointList();
		/** 判断跳转点所连接的地图是否为目标地图 **/
		for (JumpMapPoint p : curMapPoint) {
			if (p.getTomapid().equals(p2_arriveMap)) {// 返回
				return new SearchMapPoint(p, null);
			}
			open.add(new SearchMapPoint(p, null));// 加入搜索集合中
		}

		/** 计时点 **/
		long start = System.currentTimeMillis();
		int outTime = parasConfig.getSearchRoadOutTime();//(单位/毫秒)
		
		/** 4.遍历搜索集合 **/
		while (open.size() > 0) {
			long end = System.currentTimeMillis();// 超时设为2秒
			if ((end - start) > outTime){
				break;
			}
			SearchMapPoint temp = open.get(0);// 临时变量
			if (hasSearchMapPoint(close, temp)){// 如果已经搜索过
				continue;
			}
			close.add(temp);// 把这地图实例放入已经搜索过的集合
			open.remove(0);// 删除
			/** 获取所连接地图的跳转点 **/
			List<JumpMapPoint> cmp = GameContext.getMapApp().getMap(temp.getPoint().getTomapid())
												.getJumpMapPointCollection().getPointList();
			for (JumpMapPoint jp : cmp) {
				if (jp.getTomapid().equals(p2_arriveMap)) {// 是，返回
					return new SearchMapPoint(jp, temp);
				}
				SearchMapPoint smp = new SearchMapPoint(jp, temp);// 否，加入搜索集合中
				if (!hasSearchMapPoint(open, smp)
						&& !hasSearchMapPoint(close, smp))
					open.add(smp);
			}
		} 

		return null;
	}

	/*public static void main() {
		AutoSearchRoadApplicationImpl impl = new AutoSearchRoadApplicationImpl();
		SearchMapPoint mp = impl.findRoad("zhigaoqiuling06", "xuewushuangqi01");

		while (mp != null) {
			System.out.println("地图ID：" + mp.getPoint().getMapid() + "("
					+ mp.getPoint().getX() + "," + mp.getPoint().getY() + ")"
					+ "连接地图ID:" + mp.getPoint().getTomapid());
			mp = mp.getFatherPoint();
		}

	}*/

	private List<PointItem> searchRoad(String p1_srcMapId, 
									   String p2_arriveMapId)
		throws Exception {

		if (p1_srcMapId == null || p1_srcMapId.length() <= 0
				|| p2_arriveMapId == null || p2_arriveMapId.length() <= 0) {
			return null;
		}

		List<PointItem> pointList = new ArrayList<PointItem>();

		SearchMapPoint searchPoint = findRoad(p1_srcMapId, p2_arriveMapId);

		if (null == searchPoint)
			return null;

		while (searchPoint != null) {
			PointItem point = new PointItem();
			point.setMapX((short) searchPoint.getPoint().getX());
			point.setMapY((short) searchPoint.getPoint().getY());
			pointList.add(point);
			searchPoint = searchPoint.getFatherPoint();
		}
		// 进行倒序，从起始开始添加的
		Object[] temp = pointList.toArray();
		pointList.clear();
		for (int i = temp.length; i > 0; i--) {
			int a = i - 1;
			pointList.add((PointItem) temp[a]);
		}
		return pointList;
	}

	// 搜索过返回true
	boolean hasSearchMapPoint(List<SearchMapPoint> list, SearchMapPoint point) {
		int x = point.getPoint().getX();
		int y = point.getPoint().getY();
		for (SearchMapPoint s : list) {
			int X = s.getPoint().getX();
			int Y = s.getPoint().getY();
			if (x == X && y == Y){
				return true;
			}
		}

		return false;
	}

	// 遍历Open集合
	void outList(List<SearchMapPoint> list) {
		for (SearchMapPoint p : list) {
			System.out.print("地图ID" + p.getPoint().getMapid() + "("
					+ p.getPoint().getX() + "," + p.getPoint().getDesY() + ")"
					+ "\t");
		}
	}
	
	
	/*//	 寻找NPC
	public List<PointItem> searchNPC(String p1_userMapId, 
									 String p2_npcId,
									 RaceType raceType)
		throws Exception {

		if (Util.isEmpty(p1_userMapId) || Util.isEmpty(p2_npcId))
			return null;

		// logger.info("我在:"+p1_userMapId+"\t寻找npcId:"+p2_npcId);
		List<Point> pointList = GameContext.getMapApp().whereNpcBorn(p2_npcId);// 获取NPC出生地

		if (Util.isEmpty(pointList)) {
			// logger.info("没有这NPC");
			return null;
		}
		Point p = pointList.get(0);
		String npcMapId = p.getMapid();// NPC所在的地图
		// NPC的坐标
		PointItem pt = new PointItem();
		pt.setMapX((short) p.getX());
		pt.setMapY((short) p.getY());

		// 判断该NPC是否在玩家目前所在的地图中
		if (npcMapId.equals(p1_userMapId)) {
			List<PointItem> points = new ArrayList<PointItem>();
			points.add(pt);
			// logger.info("这NPC在本地图,坐标:("+item.getMapX()+","+item.getMapY()+")");
			return points;
		}
		// 查询路程坐标
		List<PointItem> pointItems = searchRoad(p1_userMapId,npcMapId,raceType);

		if (pointItems == null)
			return null;

		// 加入NPC的坐标
		pointItems.add(pt);

		return pointItems;
	}*/
	
	/*// 任务
	@Override
	public List<PointItem> searchQuestNpc(RoleInstance p1_role,      // 角色实例对象
										           int p2_questId,   // 任务ID
										        String p3_id,        // NPC、物品、地图...id
										      RaceType raceType)     // 阵营        
		throws Exception {

		// logger.info("====任务类型寻路====p1:"+p1_role+"\t " + "p2:"+p2_questId+"\t"+"p3:"+p3_id);
		if (p1_role == null || p2_questId == 0) {
			return null;
		}

		Quest quest;// 任务信息
		QuestPhase phase;// 任务阶段
		QuestTerm term = null; // 任务条件
		String userMapId = p1_role.getMapId(); // 玩家所处地图
		List<PointItem> pointItem; // 返回坐标集合
		List<QuestTerm> termList; // 完成条件

		//int[] count;// 任务进度

		*//** 获取任务信息 **//*
		quest = GameContext.getQuestApp().getQuest(p2_questId);
		if (quest == null) {
			return null;
		}
		phase = quest.getCurrentPhase(p1_role);// 获取当前任务阶段

		*//** 任务完成阶段 **//*
		if (phase instanceof SubmitQuestPhase) {
			// logger.info("任务可提交阶段");
			if (quest.canSubmit(p1_role)) {
				pointItem = searchRoad(userMapId, 
									phase.getMapId(),
									new PointItem((short) phase.getMapX(), (short) phase.getMapY()),
									raceType);
				return pointItem;
			}
			return null;
		}

		*//** 接任务阶段 **//*
		if (phase instanceof AcceptQuestPhase) {
			// logger.info("接任务阶段");
			if (quest.canAccept(p1_role)) {
				pointItem = searchRoad(userMapId, 
									phase.getMapId(),
									new PointItem((short) phase.getMapX(), (short) phase.getMapY()),
									raceType);
				return pointItem;
			}
			return null;

		}
		*//** 任务完成进度 **//*
		//count = quest.getCurrentComplete(p1_role);
		termList = phase.termList();// 完成条件
		int i = 0;
		if (Util.isEmpty(p3_id)) {
			*//** 遍历未完成的任务条件 **//*
			for (QuestTerm t : termList) {
				if(t.getCount() <= phase.getCurrentNum(p1_role,i)){
					i++;
					continue;
				}
				term = t;
				break;
//				int c = t.getCount();
//				if (c <= count[i]) {
//					i++;
//					continue;
//				}
//				term = t;
//				break;
			}
		} else {
			*//** 遍历与id相同的任务条件 **//*
			for (QuestTerm t : termList) {
				String id = t.getId();
				if (id.equals(p3_id)) {
					term = t;
					break;
				}
			}
		}
		if (term == null)
			return null;

		pointItem = searchRoad(userMapId, 
				               term.getMapId(), 
				               new PointItem((short) term.getMapX(), (short) term.getMapY()),
		                       raceType);

		return pointItem;
	}
*/
	// 寻路
	/*public List<PointItem> searchRoad(String srcMapId,    // 起始地图
			                          String arriveMapId, // 目标地图
			                          PointItem point,    // 终止点
			                          RaceType raceType)  // 阵营
		throws Exception {

		if (Util.isEmpty(srcMapId) || Util.isEmpty(arriveMapId))
			return null;

		List<PointItem> pointItem = null;
		// 起始、目标地图为同一地图
		if (srcMapId.equals(arriveMapId) && point != null) {
			if (point.getMapX() > 0 || point.getMapY() > 0) {
				pointItem = new ArrayList<PointItem>();
				pointItem.add(point);
				return pointItem;
			}
			return null;
		}
		pointItem = searchRoad(srcMapId, arriveMapId, raceType);

		if (null == pointItem)
			return null;

		if (point != null && (point.getMapX() > 0 || point.getMapY() > 0)) {
			pointItem.add(point);
		}
		return pointItem;
	}*/
	
	/** 返回任务目标点坐标 
	 *  id:怪npcId、收集物品id..
	 * **/
	/*public Point getQuestTarget(RoleInstance role, int questId, String id){
		if (role == null || questId == 0) {
			return null;
		}
		Quest quest;// 任务信息
		QuestPhase phase;// 任务阶段
		QuestTerm term = null; // 任务条件
		List<QuestTerm> termList; // 完成条件
		Point point = new Point();
		
		// 获取任务信息 
		quest = GameContext.getQuestApp().getQuest(questId);
		if (quest == null) {
			return null;
		}
		phase = quest.getCurrentPhase(role);// 获取当前任务阶段

		if (phase instanceof SubmitQuestPhase 
				|| phase instanceof AcceptQuestPhase) {
			if (quest.canSubmit(role) || quest.canAccept(role)) {
				point.setMapid(phase.getMapId());
				point.setX(phase.getMapX());
				point.setY(phase.getMapY());
				return point;
			}
			return null;
		}
		
		// 任务完成进度 
		termList = phase.termList();// 完成条件
		int i = 0;
		if (Util.isEmpty(id)) {
			*//** 遍历未完成的任务条件 **//*
			for (QuestTerm t : termList) {
				if(t.getCount() <= phase.getCurrentNum(role,i)){
					i++;
					continue;
				}
				term = t;
				break;
			}
		} else {
			*//** 遍历与id相同的任务条件 **//*
			String str;
			for (QuestTerm t : termList) {
				str = t.getParameter();
				if (str.equals(id)) {
					term = t;
					break;
				}
			}
		}
		if (term == null){
			return null;
		}
		point.setMapid(term.getMapId());
		point.setX(term.getMapX());
		point.setY(term.getMapY());
		return point;
	}*/

	public ParasConfig getParasConfig() {
		return parasConfig;
	}

	public void setParasConfig(ParasConfig parasConfig) {
		this.parasConfig = parasConfig;
	}

	/*@Override
	public List<PointItem> searchQuest(RoleInstance role, int questId, byte index) throws Exception {
		Quest quest = GameContext.getQuestApp().getQuest(questId);
		if(null == quest){
			return null;
		}
		String currMapId = role.getMapId();
		PointItem pointItem = new PointItem();
		if(index >= 0){
			List<QuestTerm> termList = quest.getTermList(role);
			if(Util.isEmpty(termList)){
				return null;
			}
			QuestTerm term = termList.get(index);
			if(null != term){
				pointItem.setMapX((short) term.getMapX());
				pointItem.setMapY((short) term.getMapY());
				return this.searchRoad(currMapId, term.getMapId(), pointItem, RaceType.animal);
			}
		}
		QuestPhase phase = null;
		List<QuestPhase> phaseList = quest.getPhaseList();
		int size = phaseList.size();
		if(-1 == index){//寻路到当前阶段
			phase = quest.getCurrentPhase(role);
		}else if(-2 == index){//寻路到接任务处
			phase = phaseList.get(0);
		}else if(-3 == index){//寻路到交任务处
			phase = phaseList.get(size-1);
		}
		if(null == phase){
			return null;
		}
		if(phase instanceof SubmitQuestPhase || phase instanceof AcceptQuestPhase){
			pointItem.setMapX((short) phase.getMapX());
			pointItem.setMapY((short) phase.getMapY());
			return this.searchRoad(currMapId, phase.getMapId(), pointItem, RaceType.animal);
		}
		QuestTerm term = null;//任务条件
		List<QuestTerm> termList = phase.termList();//完成条件
		int i = 0;
		for (QuestTerm t : termList) {
			if(t.getCount() <= phase.getCurrentNum(role,i)){
				i++;
				continue;
			}
			term = t;
			break;
		}
		if(null != term){
			pointItem.setMapX((short) term.getMapX());
			pointItem.setMapY((short) term.getMapY());
			return this.searchRoad(currMapId, term.getMapId(), pointItem, RaceType.animal);
		}
		return null;
	}*/
}
