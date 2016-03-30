package sacred.alliance.magic.vo.map;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.union.config.UnionActivityInfo;
import com.game.draco.app.union.config.UnionDpsResult;
import com.game.draco.app.union.config.instance.UnionInsBoss;
import com.game.draco.app.union.domain.instance.RoleDps;
import com.game.draco.message.item.ActiveDpsRankItem;
import com.game.draco.message.push.C2360_ActiveDpsRankNotifyMessage;
import com.game.draco.message.push.C2363_ActiveDpsStopTimeNotifyMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MapUnionInstance extends MapInstance implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	//广播周期
	private LoopCount broadcastLoopCount = new LoopCount(5*1000); 
	
	private int count ;
	
	private int index = 0 ;

	private String unionId;
	
	private byte activityId;
	
	private String [] weekCd;
	
	private int week;
	
	//BOSS 数据
	private java.util.Map<String,java.util.Map<Byte,Integer>> bossStateMap = Maps.newConcurrentMap();
	
	private java.util.Map<Byte,java.util.Map<Integer,RoleDps>> roleDpsMap = Maps.newConcurrentMap();
	
	public MapUnionInstance(Map map, String unionId){
		super(map);
		this.unionId = unionId;
		this.activityId = (byte)map.getMapConfig().getCopyId();
		java.util.Map<Byte,UnionActivityInfo> activityMap = GameContext.getUnionDataApp().getUnionActivityMap();
		if(activityMap.containsKey(activityId)){
			if(activityMap.get(activityId).getCd() != null){
				weekCd = activityMap.get(activityId).getCd().split(",");
			}
			week = DateUtil.getWeek();
		}
	}
	
	@Override
	protected void enter(AbstractRole role) {
		super.enter(role);
		//进入地图显示倒计时
		this.broadcastStopTime(role);
	}
	
	@Override
	public void initNpc(boolean loadNpc) {
		UnionInsBoss unionInsBoss = GameContext.getUnionDataApp().getUnionInsBossMap(activityId);
		if(null == unionInsBoss || Util.isEmpty(unionInsBoss.getGroupId())){
			return ;
		}
		String [] groupArr = unionInsBoss.getGroupId().split(",");
		if(null == groupArr || 0 == groupArr.length){
			return ;
		}
		boolean isCreate = false;
		for (String groupId : groupArr) {
			if (isCreate) {
				break;
			}
			Set<UnionDpsResult> npcArr = GameContext.getUnionDataApp()
					.getUnionDpsResult(Byte.parseByte(groupId));
			for (UnionDpsResult npc : npcArr) {
				boolean flag = isCreateNpc(npc.getBossId());
				if (!flag) {
					continue;
				}
				UnionDpsResult result = GameContext.getUnionDataApp()
						.getUnionDpsResultByBossId(npc.getBossId());
				NpcBorn npcBorn = new NpcBorn();
				NpcTemplate npcTemplate = context.getNpcApp().getNpcTemplate(
						npc.getBossId());
				if (npcTemplate == null) {
					continue;
				}
				npcBorn.setBornmapgxbegin(result.getMapX());
				npcBorn.setBornmapgybegin(result.getMapY());
				npcBorn.setBornmapgxend(result.getMapX());
				npcBorn.setBornmapgyend(result.getMapY());
				npcBorn.setBornnpccount(1);
				npcBorn.setBornnpcid(npc.getBossId());
				npcBorn.setBornNpcDir(Direction.DOWN.getType());
				
				// 获得NPC模板
				bornIndex++;
				summonCreateNpc(npcBorn);
				java.util.Map<Byte, Integer> pro = Maps.newHashMap();
				pro.put((byte) 0, npcTemplate.getMaxHP());
				bossStateMap.put(npcTemplate.getNpcid(), pro);
				
				//TODO：创建此boss相对应的障碍物
				if(!Util.isEmpty(npc.getBlockId())){
					List<NpcBorn> listNpcBorn = this.getNpcBornList();
					if (null == listNpcBorn || 0 == listNpcBorn.size()) {
						continue;
					}
					
					for (NpcBorn born : listNpcBorn) {
						if(born.getBornnpcid().equals(npc.getBlockId())){
							this.bornIndex++;
							this.npcBorn(this.bornIndex, born,false);
						}
					}
				}
				
				isCreate = true;
			}
		}
	}
	
	
	@Override
	public boolean npcBorn(int bornIndex, NpcBorn npcBorn,boolean whenCreateMap) {
		int bornNum = npcBorn.getBornnpccount();
		if (bornNum <= 0) {
			return true;
		}
		String bornnpcid = npcBorn.getBornnpcid();
		NpcTemplate npcTemplate = context.getNpcApp().getNpcTemplate(
				bornnpcid);
		if (null == npcTemplate) {
			return true;
		}
		boolean isMapBaffle = (npcTemplate.getNpctype() == NpcType.baffle.getType());
		if(isMapBaffle){
			//创建地图障碍物
			createMapBaffle(npcTemplate, npcBorn, bornIndex, whenCreateMap,bornNum);
			return true ;
		}
		//创建NPC
		createNpcInstance(npcTemplate, npcBorn, bornIndex, whenCreateMap,bornNum);
		java.util.Map<Byte, Integer> pro = Maps.newHashMap();
		pro.put((byte)0,npcTemplate.getMaxHP());
		bossStateMap.put(npcTemplate.getNpcid(), pro);
		return true ;
	}
	
	@Override
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
		try {
			if(null == attacker || null == victim || 0 == hurt){
				return;
			}
			//只有玩家攻击怪的时候才累计伤害值
			if(RoleType.PLAYER != attacker.getRoleType() || RoleType.NPC != victim.getRoleType()){
				return;
			}
			NpcInstance npc = (NpcInstance) victim;
			
			byte groupId = GameContext.getUnionDataApp().getGroupId(npc.getNpcid());
			if(groupId != -1){
				addRoleDps((RoleInstance)attacker,groupId,hurt);
			}
			
			//不是活动中的BOSS
		} catch (RuntimeException e) {
			logger.error("", e);
		}
	}
	
	/**
	 * 添加DPS
	 * @param roleId
	 * @param bossId
	 * @param dps
	 */
	private void addRoleDps(RoleInstance role,byte groupId,int dps){
		if(role.hasUnion()){
			RoleDps record = new RoleDps();
			int roleDps = getRoleDps(role,groupId);
			record.setDps(dps + roleDps);
			record.setRoleId(role.getIntRoleId());
			record.setRoleName(role.getRoleName());
			addRoleDpsMap(record,groupId);
		}
	}
	
	/**
	 * 获得角色dps
	 */
	private int getRoleDps(RoleInstance role,byte groupId){
		if(!roleDpsMap.containsKey(groupId)){
			return 0;
		}
		java.util.Map<Integer,RoleDps> roleMap = roleDpsMap.get(groupId);
		if(Util.isEmpty(roleMap)){
			return 0;
		}
		if(!roleMap.containsKey(role.getIntRoleId())){
			return 0;
		}
		return roleMap.get(role.getIntRoleId()).getDps();
	}
	
	/**
	 * 添加角色DPS数据
	 * @param record
	 */
	private void addRoleDpsMap(RoleDps record,byte groupId){
		java.util.Map<Integer,RoleDps> roleMap = null;
		if(roleDpsMap.containsKey(groupId)){
			roleMap = roleDpsMap.get(groupId);
		}
		if(Util.isEmpty(roleMap)){
			roleMap = Maps.newConcurrentMap();
			roleDpsMap.put(groupId, roleMap);
		}
		roleMap.put(record.getRoleId(),record);
	}
	
	private void sortRoleDps(List<RoleDps> list){
		Collections.sort(list, new Comparator<RoleDps>() {
			public int compare(RoleDps info1, RoleDps info2) {
				if(info1.getDps() > info2.getDps()){
					return -1;
				}
				if(info1.getDps() < info2.getDps()){
					return 1;
				}
				return 0;
			}
		});
	}
	
	
	
	@Override
	protected void updateSub() throws ServiceException {
		super.updateSub();
		if(isOverActivity()){
			kickAllRole();
		}
		if(broadcastLoopCount.isReachCycle()){
			notifyRankMessage(this);
		}
	}
	
	private boolean isOverActivity(){
		if(weekCd != null && weekCd.length > 0){
			int sysWeek = DateUtil.getWeek();
			if(week == sysWeek){
				return false;
			}
			this.week = sysWeek ;
			for(String w : weekCd){
				int wk = Integer.parseInt(w)+1;
				if(wk > 7){
					wk = 1;
				}
				if(wk == sysWeek){
					return true ;
				}
			}
		}
		return false;
	}
	
	public  boolean isReachCycle(){
		index ++ ;
		boolean isDo = (index >= count) ;
		if(isDo){
			index = 0 ;
		}
		return isDo ;
	}

	/**
	 * 将所有玩家传出当前地图
	 */
	private void kickAllRole(){
		for(RoleInstance role : this.getRoleList()){
			if(null == role){
				continue;
			}
			this.kickRole(role);
		}
	}
	
	@Override
	protected void kickRole(RoleInstance role){
		super.kickRole(role);
	}
	
	@Override
	protected void npcDeathDiversity(AbstractRole attacker, AbstractRole victim){
		NpcInstance npc = (NpcInstance)victim;
		
		if(!isBoss(npc.getNpcid())){
			return;
		}
		
		RoleInstance role = (RoleInstance)attacker.getMasterRole();
		
		GameContext.getUnionInstanceApp().addUnionKillBossRecord(role.getUnionId(),  npc.getNpcid());
		// 击杀boss，世界广播
		GameContext.getUnionInstanceApp().broadcast(role, npc);
	}
	
	@Override
	public void npcDeath(NpcInstance npc) {
		super.npcDeath(npc);
		if(!isBoss(npc.getNpcid())){
			return ;
		}
		
		byte groupId = GameContext.getUnionDataApp().getGroupId(npc.getNpcid());
		if(-1 == groupId){
			return ;
		}
		java.util.Map<Byte, Integer> pro = Maps.newHashMap();
		pro.put((byte)1,npc.getMaxHP());
		bossStateMap.put(npc.getNpcid(),pro);
	
		if(!isKillAll(npc.getNpcid())){
			return ;
		}
		
		if(!roleDpsMap.containsKey(groupId)){
			return;
		}
		
		java.util.Map<Integer,RoleDps> roleMap = roleDpsMap.get(groupId);
		if(Util.isEmpty(roleMap)){
			return;
		}
		for(Entry<Integer,RoleDps> dps : roleMap.entrySet()){
			GameContext.getUnionInstanceApp().addDps(dps.getValue().getRoleId(),activityId, groupId, dps.getValue().getDps());
		}
		
		int maxHp = 0;
		Set<UnionDpsResult> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(groupId);
		for(UnionDpsResult boss : bossArr){
			if(bossStateMap.containsKey(boss.getBossId())){
				maxHp += bossStateMap.get(boss.getBossId()).get((byte)1);
			}
			if(!Util.isEmpty(boss.getBlockId())){
				this.clearBaffle(boss.getBlockId());
			}
		}
		GameContext.getUnionInstanceApp().setBossState(unionId,activityId , groupId, (byte)1, maxHp);
		roleDpsMap.clear();
		bossStateMap.clear();
		//初始化NPC
		initNpc(true);
	}
	
	private boolean isCreateNpc(String npcId){
		if(!isBoss(npcId)){
			return false ;
		}
		byte groupId = GameContext.getUnionDataApp().getGroupId(npcId);
		byte state = GameContext.getUnionInstanceApp().getInsBossState(unionId, activityId, groupId);
		return 0 == state ;
	}
	
	private boolean isKillAll(String npcId){
		UnionDpsResult dps = GameContext.getUnionDataApp().getUnionDpsResultByBossId(npcId);
		boolean flag = true;
		Set<UnionDpsResult> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(dps.getGroupId());
		if(bossArr.size() > 1){
			for(UnionDpsResult boss : bossArr){
				if(bossStateMap.containsKey(boss.getBossId())){
					if(bossStateMap.get(boss.getBossId()).containsKey((byte)0)){
						flag = false;
					}
				}else{
					flag = false;
				}
			}
		}
		return flag;
	}
	
	private boolean isBoss(String npcId){
		return null != GameContext.getUnionDataApp().getUnionDpsResultByBossId(npcId);
	}
	
	@Override
	public boolean canDestroy() {
		// 副本中有用户不能销毁
		return !this.hasPlayer() ;
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		Point targetPoint = ((RoleInstance)role).getCopyBeforePoint();
		role.setMapId(targetPoint.getMapid());
		role.setMapX(targetPoint.getX());
		role.setMapY(targetPoint.getY());
	}

	@Override
	protected String createInstanceId() {
		instanceId = unionId + activityId + instanceIdGenerator.incrementAndGet();
		return instanceId;
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		RoleInstance r = (RoleInstance)role;
		if(r.hasUnion()){
			return true;
		}
		return false;
	}

	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		
	}

	@Override
	protected void deathLog(AbstractRole victim) {
		
	}

	@Override
	public void useGoods(int goodsId) {
		
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
	/**
	 * 广播伤害排名消息
	 * @param hurtList
	 */
	private void notifyRankMessage(MapInstance mapInstance){
		//输出血量排序
		if(null == roleDpsMap || Util.isEmpty(roleDpsMap)){
			return;
		}
		List<ActiveDpsRankItem> dpsRankList = new ArrayList<ActiveDpsRankItem>();
		List<RoleDps> list = Lists.newArrayList();
		if(!Util.isEmpty(roleDpsMap)){
			for(Entry<Byte,java.util.Map<Integer,RoleDps>> roleDps : roleDpsMap.entrySet()){
				if(Util.isEmpty(roleDps.getValue())){
					continue;
				}
				list.addAll(roleDps.getValue().values());
			}
		}
			
		sortRoleDps(list);
		
		short i=0;
		for(RoleDps dps : list){
			ActiveDpsRankItem item = new ActiveDpsRankItem();
			item.setIndex(++i);
			item.setRoleName(dps.getRoleName());
			item.setDpsValue(dps.getDps());
			item.setRoleId(dps.getRoleId());
			//构建显示排名的信息
			dpsRankList.add(item);
		}
		C2360_ActiveDpsRankNotifyMessage message = new C2360_ActiveDpsRankNotifyMessage();
		message.setDpsRankList(dpsRankList);
		//地图内广播
		mapInstance.broadcastMap(null, message);
	}
	
	/**
	 * 通知活动倒计时
	 * @param role 角色对象,为NULL表示地图内广播
	 */
	private void broadcastStopTime(AbstractRole role){
		try {
			//剩余时间（秒）
			C2363_ActiveDpsStopTimeNotifyMessage message = new C2363_ActiveDpsStopTimeNotifyMessage();
			message.setTime(-1);
			if(null != role){
				role.getBehavior().sendMessage(message);
			}else{
				//地图内广播消息
				this.broadcastMap(null, message);
			}
		} catch (RuntimeException e) {
			logger.error("MapDpsInstance.notifyDpsStopTime error: ", e);
		}
	}

}
