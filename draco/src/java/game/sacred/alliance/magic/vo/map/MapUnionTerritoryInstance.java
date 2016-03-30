package sacred.alliance.magic.vo.map;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.HatredTarget;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleBornGuide;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.union.domain.Union;

public class MapUnionTerritoryInstance extends MapInstance{
	private final LoopCount mapStateLoop = new LoopCount(1000);//1秒
	public final static short HP_RATE_FULL = 10000 ;
	private AtomicBoolean summonFlag = new AtomicBoolean(false);
	
	private NpcInstance summonNpc;
	
	private String unionId;
	
	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	private int summonGroupId;
	
	public NpcInstance getSummonNpc() {
		return summonNpc;
	}

	public void setSummonNpc(NpcInstance summonNpc) {
		this.summonNpc = summonNpc;
	}

	public int getSummonGroupId() {
		return summonGroupId;
	}

	public void setSummonGroupId(int summonGroupId) {
		this.summonGroupId = summonGroupId;
	}

	public MapUnionTerritoryInstance(Map map) {
		super(map);
	}
	
	public void exitMap(AbstractRole role) {
		super.exitMap(role);
		if (role.getRoleType() != RoleType.PLAYER) {
			return;
		}
		RoleInstance roleInstance = (RoleInstance) role;
		Point targetPoint = roleInstance.getCopyBeforePoint();
		if (null != targetPoint) {
			role.setMapId(targetPoint.getMapid());
			role.setMapX(targetPoint.getX());
			role.setMapY(targetPoint.getY());
		}
		exit(role);
	}
	
	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, RoleInstance target) {
		if(summonNpc != null){
			if(summonNpc.getIntRoleId() == npc.getIntRoleId() && target.hasUnion()){
				return ForceRelation.enemy;
			}
		}
		return ForceRelation.friend;
	}
	
	@Override
	protected ForceRelation getForceRelation(RoleInstance role, RoleInstance target) {
		if(role.getUnionId().equals(target.getUnionId())){
			return ForceRelation.friend;
		}
		return ForceRelation.enemy;
	}
	
	@Override
	protected ForceRelation getForceRelation(RoleInstance role, NpcInstance target) {
		if(summonNpc != null){
			if(summonNpc.getIntRoleId() == target.getIntRoleId() && role.hasUnion()){
				return ForceRelation.enemy;
			}
		}
		return ForceRelation.friend;
	}
	
	@Override
	public void npcDeath(NpcInstance npc) {
		npc.setCurHP(0);
		super.npcDeath(npc);
		if(summonNpc != null){
			if(summonNpc.getIntRoleId() == npc.getIntRoleId()){
				HatredTarget hatred = npc.getHatredTarget();
				if(null != hatred.getHatredMap()){
					List<String> hatredList = hatred.getHatredList();
					GameContext.getUnionApp().killSummonNpc(unionId,npc.getNpcname(),summonGroupId,hatredList);
				}
			}
		}
	}
	
	@Override
	protected ForceRelation getForceRelation(NpcInstance npc, NpcInstance target) {
		return ForceRelation.friend;
	}
	
	private void exit(AbstractRole role){
				
		//删除buff
		RoleBornGuide guide = GameContext.getRoleBornApp()
				.getRoleBornGuide();
		if(guide == null){
			return;
		}
		if (guide.getBuffId() > 0) {
			GameContext.getUserBuffApp().delBuffStat(role, guide.getBuffId(), false);
		}
		
		if(guide.getGiveHeroId() >0){
			GameContext.getHeroApp().deleteHeroBySystem((RoleInstance)role, guide.getGiveHeroId());
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
			if(!role.hasUnion()){
				this.kickRole(role);
			}
		}
	}
	
	@Override
	protected void enter(AbstractRole role) {
		if(role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		super.enter(role);
		RoleInstance r = (RoleInstance)role;
		if (summonFlag.compareAndSet(false, true)){ 
			Union union = GameContext.getUnionApp().getUnion(r.getUnionId());
			if(union != null){
				if(union.isBossState()){
					GameContext.getUnionApp().summonNpc(r,false,true);
				}
			}
		}
		
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message) {
		super.broadcastMap(role, message, 0);
	}
	
	@Override
	public void broadcastScreenMap(AbstractRole role, Message message,int expireTime){
		super.broadcastMap(role, message, expireTime);
	}
	
	
	@Override
	public void updateSub()  throws ServiceException{
		super.updateSub();
		if (this.mapStateLoop.isReachCycle()) {
			kickAllRole();
		}
	}
	
	@Override
	public boolean canDestroy() {
		boolean flag = false;
		if(this.getRoleCount() == 0){
			flag = true;
		}
		return flag;
	}

	@Override
	public boolean canEnter(AbstractRole role) {
		return true;
	}

	/**
	 * 角色死亡在不同地图的处理差异
	 * @param attacker 攻击者
	 * @param victim 受害者
	 */
	@Override
	protected void deathDiversity(AbstractRole attacker, AbstractRole victim) {
		if(victim.getRoleType() != RoleType.PLAYER){
			return;
		}
	}

	@Override
	protected void deathLog(AbstractRole victim) {
		
	}

	@Override
	public void useGoods(int goodsId) {
		
	}

	@Override
	protected String createInstanceId() {
		return this.instanceId;
	}
	
	@Override
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
	}

	@Override
	public void destroy() {
		super.destroy();
	}
	
}
