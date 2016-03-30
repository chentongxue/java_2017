package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.RoleInfoItem;
import com.game.draco.debug.message.request.C10012_GetOnlineUserReqMessage;
import com.game.draco.debug.message.response.C10012_GetOnlineUserRespMessage;

import sacred.alliance.magic.app.map.MapApp;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

public class GetOnlineUserAction extends ActionSupport<C10012_GetOnlineUserReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10012_GetOnlineUserReqMessage req) {
		C10012_GetOnlineUserRespMessage resp = new C10012_GetOnlineUserRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			Collection<RoleInstance> userList = GameContext.getOnlineCenter().getAllOnlineRole();
			if(userList == null || userList.size()<=0){
				resp.setType((byte)RespTypeStatus.SUCCESS);
				return resp;
			}
			//int curPage = req.getCurPage();
			int number = req.getNumber();
			String mapId = req.getMapId();
			int minLevel = req.getMinLevel();
			int maxLevel = req.getMaxLevel();
//			byte[] careers = req.getCareers();
			byte[] campIds = req.getCampIds();
			//阵营过滤
			boolean isCampLimit = false;
			Set<Byte> campSet = new HashSet<Byte>();
			if(null != campIds && campIds.length >0){
				for(byte campId : campIds){
					campSet.add(campId);
				}
				isCampLimit = campSet.size() < 3;
			}
			//职业过滤
//			boolean isCareerLimit = false;
//			Set<Byte> careerSet = new HashSet<Byte>();
//			if(null != careers && careers.length > 0){
//				for(byte career : careers){
//					careerSet.add(career);
//				}
//				isCareerLimit = careerSet.size() < 3;
//			}
			List<RoleInfoItem> roleItems = new ArrayList<RoleInfoItem>();
			resp.setType((byte)RespTypeStatus.SUCCESS);
			MapApp mapApplication = GameContext.getMapApp();
			for(RoleInstance role : userList){
				if(null == role){
					continue;
				}
				//过滤地图条件
				if(mapId != null && mapId.trim().length()>0 && !role.getMapId().equals(mapId)){
					continue;
				}
				//过滤阵营
				byte campId = role.getCampId();
				if(isCampLimit && !campSet.contains(campId)){
					continue;
				}
//				//过滤职业条件
//				byte career = (byte)instance.getCareer();
//				if(isCareerLimit && !careerSet.contains(career)){
//					continue;
//				}
				//过滤等级条件
				int level = role.getLevel();
				if(minLevel > 0 && level < minLevel){
					continue;
				}
				if(maxLevel > 0 && level > maxLevel){
					continue;
				}
				RoleInfoItem item = new RoleInfoItem();
				item.setInstanceId(role.getRoleId());
				item.setInstanceName(role.getRoleName());
				item.setGender(role.getSex());
//				item.setCareer(career);
				item.setLevel(level);
				item.setExp(role.getExp());
				item.setGoldMoney(role.getGoldMoney());
				//item.setBindMoney(role.getBindingGoldMoney());
				item.setSilverMoney(role.getSilverMoney());
				item.setMapId(role.getMapId());
				item.setCampId(role.getCampId());
				item.setLoginIp(role.getLoginIp());
				item.setChannelId(role.getChannelId());
				item.setChannelUserId(role.getChannelUserId());
				item.setUserId(role.getUserId());
				item.setUserName(role.getUserName());
				if(role.getMapInstance()!= null){
					item.setMapInstanceId(role.getMapInstance().getInstanceId());
				}else{
					item.setMapInstanceId(null);
				}
				item.setMapName(mapApplication.getMap(role.getMapId()).getMapConfig().getMapdisplayname());
				item.setMapX(role.getMapX());
				item.setMapY(role.getMapY());
				roleItems.add(item);
			}
			//排序
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
			int size = roleItems.size();
			resp.setTotalPage(0 == size%number?size/number:size/number + 1);
			resp.setRoleItems(roleItems);
			return resp;
		}catch(Exception e){
			logger.error("GetOnlineUserAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
