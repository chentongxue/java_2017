package sacred.alliance.magic.debug.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.debug.message.item.NpcInfoItem;
import com.game.draco.debug.message.item.RoleInfoItem;
import com.game.draco.debug.message.request.C10011_GetMapRoleReqMessage;
import com.game.draco.debug.message.response.C10011_GetMapRoleRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class GetMapRoleAction extends ActionSupport<C10011_GetMapRoleReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10011_GetMapRoleReqMessage req) {
		C10011_GetMapRoleRespMessage resp = new C10011_GetMapRoleRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String mapInstanceId = req.getMapInstanceId();
			if(mapInstanceId == null || mapInstanceId.length()<=0){
				resp.setInfo(GameContext.getI18n().getText(TextId.MAP_ID_NULL));
				return resp;
			}
			MapInstance instance = GameContext.getMapApp().getMapInstance(mapInstanceId);
			if(instance != null){
				Collection<NpcInstance> npcList = instance.getNpcList();
				if(!Util.isEmpty(npcList)) {
					List<NpcInfoItem> npcItems = new ArrayList<NpcInfoItem>();
					for(NpcInstance npc : npcList) {
						NpcInfoItem item = new NpcInfoItem();
						item.setInstanceId(npc.getNpc().getNpcid());
						item.setInstanceName(npc.getNpcname());
						item.setTempId(npc.getNpcid());
						item.setMapId(instance.getMap().getMapId());
						item.setMapName(instance.getMap().getMapConfig().getMapdisplayname());
						item.setMapX(npc.getMapX());
						item.setMapY(npc.getMapY());
						npcItems.add(item);
					}
					resp.setNpcItems(npcItems);
				}
				Collection<RoleInstance> roleList = instance.getRoleList();
				if(!Util.isEmpty(roleList)) {
					List<RoleInfoItem> roleItems = new ArrayList<RoleInfoItem>();
					for(RoleInstance role : roleList) {
						RoleInfoItem item = new RoleInfoItem();
						item.setInstanceId(role.getRoleId());
						item.setInstanceName(role.getRoleName());
						item.setGender(role.getSex());
//						item.setCareer((byte)role.getCareer());
						item.setMapId(instance.getMap().getMapId());
						item.setMapName(instance.getMap().getMapConfig().getMapdisplayname());
						item.setMapX(role.getMapX());
						item.setMapY(role.getMapY());
						item.setLevel(role.getLevel());
						item.setExp(role.getExp());
						item.setGoldMoney(role.getGoldMoney());
						//item.setBindMoney(role.getBindingGoldMoney());
						item.setSilverMoney(role.getSilverMoney());
						item.setCampId(role.getCampId());
						item.setLoginIp(role.getLoginIp());
						item.setChannelId(role.getChannelId());
						item.setChannelUserId(role.getChannelUserId());
						item.setUserId(role.getUserId());
						item.setUserName(role.getUserName());
						roleItems.add(item);
					}
					if(roleItems != null && roleItems.size() > 0){
						Collections.sort(roleItems, new Comparator<RoleInfoItem>() {
							public int compare(RoleInfoItem item1,RoleInfoItem item2) {
								int level1 = item1.getLevel();
								int level2 = item2.getLevel();
								if(level1<level2){
									return 1;
								}else if(level1 == level2){
									return 0;
								}else{
									return -1;
								}
							}
						});
					}
					resp.setRoleItems(roleItems);
				}
				resp.setMapInstance(mapInstanceId);
				return resp;
			}else{
				resp.setInfo(GameContext.getI18n().messageFormat(TextId.GET_MAP_ROLE_FAIL, mapInstanceId));
				resp.setNpcItems(null);
				resp.setRoleItems(null);
			}
			return resp;
		}catch(Exception e){
			logger.error("GetMapRoleAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
