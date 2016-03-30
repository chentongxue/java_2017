package sacred.alliance.magic.vo;


import java.util.List;
import java.util.Set;

import org.python.google.common.collect.Maps;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.exception.ServiceException;

import com.game.draco.GameContext;
import com.game.draco.app.drama.config.DramaTriggerType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.union.config.UnionDpsResult;
import com.game.draco.app.union.domain.instance.RoleDps;

public class MapUnionTeamInstance extends MapInstance implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;

	private String unionId;
	
	private byte activityId;
	
	//BOSS 数据
	private java.util.Map<String,java.util.Map<Byte,Integer>> bossStateMap = Maps.newConcurrentMap();
	
	private java.util.Map<Byte,java.util.Map<Integer,RoleDps>> roleDpsMap = Maps.newConcurrentMap();
	
	public MapUnionTeamInstance(Map map, String unionId){
		super(map);
		this.unionId = unionId;
		this.activityId = (byte)map.getMapConfig().getCopyId();
	}
	
	@Override
	public void initNpc(boolean loadNpc) {
		if (!loadNpc) {
			return;
		}
		List<NpcBorn> listNpcBorn = this.getNpcBornList();
		if (null == listNpcBorn || 0 == listNpcBorn.size()) {
			return;
		}
		
		for (NpcBorn npcBorn : listNpcBorn) {
			// 获得NPC模板
			bornIndex++;
			npcBorn(this.bornIndex, npcBorn,true);
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
		triggerDrama(bornnpcid, DramaTriggerType.NpcBorn);
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
			record.setGroupId(groupId);
			record.setDps(dps);
			record.setRoleId(role.getIntRoleId());
			record.setUnionId(role.getUnionId());
			addRoleDpsMap(record);
		}
	}
	
	/**
	 * 获得角色dps
	 */
	private int getRoleDps(RoleInstance role,byte groupId){
		java.util.Map<Integer,RoleDps> dpsMap = null;
		if(roleDpsMap.containsKey(groupId)){
			dpsMap = roleDpsMap.get(groupId);
			if(dpsMap.containsKey(role.getIntRoleId())){
				return dpsMap.get(role.getIntRoleId()).getDps();
			}
		}
		return 0;
	}
	
	/**
	 * 添加角色DPS数据
	 * @param record
	 */
	private void addRoleDpsMap(RoleDps record){
		java.util.Map<Integer,RoleDps> dpsMap = null;
		if(roleDpsMap.containsKey(record.getGroupId())){
			dpsMap = roleDpsMap.get(record.getGroupId());
		}else{
			dpsMap = Maps.newConcurrentMap();
			dpsMap.put(record.getRoleId(),record);
			roleDpsMap.put(record.getGroupId(), dpsMap);
		}
	}
	
	@Override
	protected void updateSub() throws ServiceException {
		super.updateSub();
		boolean isOver = GameContext.getUnionApp().isOverActivity(activityId);
		if(isOver){
			kickAllRole();
		}
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
	public void npcDeath(NpcInstance npc) {
		super.npcDeath(npc);
		
		byte groupId = GameContext.getUnionDataApp().getGroupId(npc.getNpcid());
		if(groupId != -1){
			java.util.Map<Integer,RoleDps> dpsMap = null;
			if(roleDpsMap.containsKey(groupId)){
				dpsMap = roleDpsMap.get(groupId);
//				for(Entry<Integer,Dps> dps : dpsMap.entrySet()){
//					GameContext.getUnionInstanceApp().addDps(dps.getValue().getRoleId(),dps.getValue().getUnionId(),activityId, groupId, dps.getValue().getDps());
//				}
			}
			
			java.util.Map<Byte, Integer> pro = Maps.newHashMap();
			pro.put((byte)1,npc.getMaxHP());
			bossStateMap.put(npc.getNpcid(),pro);
		
			if(isKillAll(npc.getNpcid())){
				int maxHp = 0;
				Set<String> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(groupId);
				for(String bossId : bossArr){
					if(bossStateMap.containsKey(bossId)){
						maxHp += bossStateMap.get(bossId).get((byte)1);
					}
				}
				GameContext.getUnionInstanceApp().setTeamBossState(unionId,activityId , groupId, (byte)1, maxHp,dpsMap);
			}
		}
	}
	
	private boolean isKillAll(String npcId){
		UnionDpsResult dps = GameContext.getUnionDataApp().getUnionDpsResult(npcId);
		boolean flag = true;
		Set<String> bossArr = GameContext.getUnionDataApp().getUnionDpsResult(dps.getGroupId());
		if(bossArr.size() > 1){
			for(String bossId : bossArr){
				if(bossStateMap.containsKey(bossId)){
					if(bossStateMap.get(bossId).containsKey((byte)0)){
						flag = false;
					}
				}
			}
		}
		return flag;
	}
	
	private boolean isBoss(String npcId){
		UnionDpsResult dps = GameContext.getUnionDataApp().getUnionDpsResult(npcId);
		if(dps != null){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canDestroy() {
		if (this.hasPlayer()) {
			// 副本中有用户不能销毁
			return false;
		}
		return true;
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

}
