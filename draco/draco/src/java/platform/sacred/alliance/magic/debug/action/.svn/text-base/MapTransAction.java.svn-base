package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10002_MapTransReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class MapTransAction extends ActionSupport<C10002_MapTransReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10002_MapTransReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String reqInfo = reqMsg.getInfo();
			byte infoType = reqMsg.getInfoType();
			String mapId = reqMsg.getMapId();
			int mapX = reqMsg.getMapX();
			int mapY = reqMsg.getMapY();
			RoleInstance role = null;
			boolean isOnline = true;//角色是否在线
			//查询在线角色
			if(infoType == 0){
				role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(reqInfo);
			}else if(infoType == 1){
				role = GameContext.getOnlineCenter().getRoleInstanceByRoleName(reqInfo);
			}
			//角色不在线，从库中查询
			if(null == role){
				if(infoType == 0){
					role = GameContext.getUserRoleApp().getRoleByRoleId(reqInfo);
				}else if(infoType == 1){
					role = GameContext.getUserRoleApp().getRoleByRoleName(reqInfo);
				}
				if(null == role){
					resp.setInfo(GameContext.getI18n().getText(TextId.ROLE_NO_EXSIT_FAIL));
					return resp;
				}
				isOnline = false;
			}
			//未填写地图，设置为当前地图
			if(Util.isEmpty(mapId)){
				mapId = role.getMapId();
			}
			Map map = GameContext.getMapApp().getMap(mapId);
			if(null == map){
				resp.setInfo(GameContext.getI18n().getText(TextId.MAP_NO_EXSIT_FAIL));
				return resp;
			}
			//坐标未填写，设置为地图的安全点
			if(mapX <= 0 || mapY <= 0){
				mapX = map.getMapConfig().getMaporiginx();
				mapY = map.getMapConfig().getMaporiginy();
			}
			
			if(isOnline){//角色在线，切换地图
				String info = this.changeMap(role, mapId, mapX, mapY);
				if(info != null){
					resp.setInfo(info);
					return resp;
				}
			}else{//角色不在线，修改坐标
				role.setMapId(mapId);
				role.setMapX(mapX);
				role.setMapY(mapY);
				//角色信息入库
				GameContext.getBaseDAO().update(role);
			}
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}catch(Exception e){
			logger.error("MapTransAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
		
	}
	
	/**
	 * 在线角色切换地图
	 * @param role
	 * @param mapId
	 * @param mapX
	 * @param mapY
	 * @return
	 * @throws Exception
	 */
	private String changeMap(RoleInstance role,String mapId,int mapX,int mapY)throws Exception{
		try{
			Point targetPoint = new Point(mapId,mapX,mapY);
			ChangeMapResult changeMapResult = GameContext.getUserMapApp().changeMap(role, targetPoint);
			String info = null;
			if(!changeMapResult.isSuccess()){
				info = changeMapResult.getDesc() ;
			}
			return info;
		}catch(Exception e){
			throw new Exception("MapTransAction changeMap error: ",e);
		}
	}
}
