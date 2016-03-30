package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.debug.message.item.NpcInstanceItem;
import com.game.draco.debug.message.item.RoleInstanceItem;
import com.game.draco.debug.message.request.C10007_MapRoleAndNpcReqMessage;
import com.game.draco.debug.message.response.C10007_MapRoleAndNpcRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

/*获得地图上的实例*/
public class MapRoleAndNpcAction extends ActionSupport<C10007_MapRoleAndNpcReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10007_MapRoleAndNpcReqMessage reqMsg) {
		C10007_MapRoleAndNpcRespMessage resp = new C10007_MapRoleAndNpcRespMessage();
		try{
			String roleName = reqMsg.getRoleName();
			RoleInstance roleInstance = GameContext.getOnlineCenter().getRoleInstanceByRoleName(roleName);
			if(null == roleInstance){
				return null ;
			}
			MapInstance mapInstance = roleInstance.getMapInstance();
			Collection<RoleInstance> roleList = mapInstance.getRoleList();
			if(roleList!=null && roleList.size() > 0) {
				List<RoleInstanceItem> roleItems = new ArrayList<RoleInstanceItem>();
				for(RoleInstance role : roleList) {
					RoleInstanceItem item = new RoleInstanceItem();
					item.setInstanceId(role.getRoleId());
					item.setInstanceName(role.getRoleName());
					item.setMapId(role.getMapId());
					item.setMapX(role.getMapX());
					item.setMapY(role.getMapY());
					roleItems.add(item);
				}
				resp.setRoleItems(roleItems);
			}
			
			Collection<NpcInstance> npcList = mapInstance.getNpcList();
			if(npcList!=null && npcList.size() > 0) {
				List<NpcInstanceItem> npcItems = new ArrayList<NpcInstanceItem>();
				for(NpcInstance npc : npcList) {
					NpcInstanceItem item = new NpcInstanceItem();
					item.setInstanceId(npc.getRoleId());
					item.setInstanceName(npc.getNpcname());
					item.setMapId(npc.getMapId());
					item.setTempId(npc.getNpc().getNpcid());
					item.setMapX(npc.getMapX());
					item.setMapY(npc.getMapY());
					npcItems.add(item);
				}
				resp.setNpcItems(npcItems);
			}
			return resp;
		}catch(Exception e){
			logger.error("MapRoleAndNpcAction error: ",e);
			return resp;
		}
	}
}
