package sacred.alliance.magic.action;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.MapDataInfo;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0201_MapGetDataReqMessage;
import com.game.draco.message.response.C0201_MapGetDataRespMessage;

public class MapGetDataAction extends BaseAction<C0201_MapGetDataReqMessage> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private void logError(String mapId,String versionId){
    	 logger.warn("MapGetDataRespMessage was null,pls check the map data config,mapId=" 
    			 + mapId + " versionId = " + versionId + " mapId = " + mapId);
    }
    
    @Override
	public Message execute(ActionContext context, C0201_MapGetDataReqMessage reqMsg) {
    	try {
            String mapId = reqMsg.getMapId();
            if(Util.isEmpty(mapId)){
            	return null ;
            }
            MapDataInfo dataInfo = GameContext.getMapApp().getMapData(mapId); 
       	 	if (null == dataInfo) {
       	 		this.logError(mapId,"");
       	 		return null;
       	 	}
       	 	this.printLog(this.getCurrentRole(context), dataInfo.getData());
       	 	C0201_MapGetDataRespMessage respMsg = new C0201_MapGetDataRespMessage();
       	 	respMsg.setCompress((byte)1);
       	 	respMsg.setMapData(dataInfo.getData());
            
            /*byte[] original = GameContext.getMapApp().getMapData(mapId);
       	 	if (null != original && original.length>0) {
       	 		context.getSession().write(original);
       	 		this.printLog(this.getCurrentRole(context), original);
       	 		return null;
       	 	}
       	 	this.logError(mapId,"");*/
       	 	
           /* String versionIds = context.getSession().getAttribute("versionId").toString();
            String[] versionId = Util.stringToArray(versionIds);
            if(null == versionId || versionId.length == 0){
            	this.logError(mapId,versionIds);
            	return null ;
            }
            for(int i=0;i<versionId.length;i++){
            	 byte[] original = GameContext.getMapApplication().getMapData(versionId[i] + "_" +mapId);
            	 if (null != original && original.length>0) {
            		 context.getSession().write(original);
            		 return null;
            	 }
            }
            this.logError(mapId,versionIds);*/
            return respMsg;
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
	}
    
    /**
     * 打印地图数据日志
     * @param role
     * @param original
     */
    private void printLog(RoleInstance role, byte[] original){
	 	if(null == role || null == original){
	 		return;
	 	}
	 	String roleId = role.getRoleId();
	 	Set<String> roleIdSet = GameContext.getParasConfig().getLogMessagePrintRoleIdSet();
	 	if(Util.isEmpty(roleIdSet) || !roleIdSet.contains(roleId)){
	 		return;
	 	}
	 	StringBuffer buffer = new StringBuffer();
	 	buffer.append("userId=").append(role.getUserId())
		 	.append(",userName=").append(role.getUserName())
		 	.append(",roleId=").append(roleId)
		 	.append(",roleName=").append(role.getRoleName())
		 	.append(",MapData.length=").append(original.length);
	 	this.logger.info("MapGetDataAction print MapData log: " + buffer.toString());
    }
    
}
