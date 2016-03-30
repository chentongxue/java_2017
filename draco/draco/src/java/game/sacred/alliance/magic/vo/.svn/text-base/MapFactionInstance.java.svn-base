//package sacred.alliance.magic.vo;
//
//
//import com.game.draco.GameContext;
//import com.game.draco.app.npc.NpcInstanceFactroy;
//import com.game.draco.app.npc.domain.NpcInstance;
//import com.game.draco.message.push.C0601_DeathNotifyMessage;
//
//import sacred.alliance.magic.app.map.Map;
//import sacred.alliance.magic.util.Util;
//
//public class MapFactionInstance extends MapCopyInstance{
//	
//	private String factionId;
//	
//	public MapFactionInstance(Map map, String factionId){
//		super(map);
//		this.factionId = factionId;
//	}
//	
//	@Override
//	public void initNpc(boolean loadNpc) {
//		super.initNpc(loadNpc);
//		//加载公会建筑
//		java.util.Map<String,NpcInstance> buildNpcMap = GameContext.getFactionApp().createBuildNpcInstance(this.factionId);
//		if(Util.isEmpty(buildNpcMap)){
//			return;
//		}
//		for(NpcInstance npcInstance : buildNpcMap.values()){
//			if(null == npcInstance){
//				continue;
//			}
//			npcInstance.setMapInstance(this);
//			// 构建用户行为
//			NpcInstanceFactroy.createNpcBehavior(npcInstance);
//			this.addAbstractRole(npcInstance);
//		}
//	}
//	
//	@Override
//	public void npcDeath(NpcInstance npc) {
//		// 从NPC列表中删除
//		this.removeAbstractRole(npc);
//		// 通知同地图的用户
//		C0601_DeathNotifyMessage message = new C0601_DeathNotifyMessage();
//		message.setInstanceId(npc.getIntRoleId());
//		broadcastMap(null, message);
//		GameContext.getFactionApp().buildNpcDeath(factionId, npc);
//	}
//	
//	
//	@Override
//	public boolean canDestroy() {
//		return super.canDestroy();
//	}
//	
//	@Override
//	public void destroy() {
//		super.destroy();
//	}
//	
//	@Override
//	public void exitMap(AbstractRole role) {
//		super.exitMap(role);
//		Point targetPoint = ((RoleInstance)role).getCopyBeforePoint();
//		role.setMapId(targetPoint.getMapid());
//		role.setMapX(targetPoint.getX());
//		role.setMapY(targetPoint.getY());
//	}
//}
