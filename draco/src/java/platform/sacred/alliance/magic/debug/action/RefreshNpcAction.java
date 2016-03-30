package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10019_RefreshNpcReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.MapInstance;

public class RefreshNpcAction extends ActionSupport<C10019_RefreshNpcReqMessage>{

	@Override
	public Message execute(ActionContext arg0, C10019_RefreshNpcReqMessage req) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String npcId = req.getNpcId();
			String mapInstanceId = req.getMapId();
			int mapX = req.getMapX();
			int mapY = req.getMapY();
			if(npcId == null || npcId.trim().length()<=0 || mapInstanceId == null || mapInstanceId.trim().length()<=0){
				resp.setInfo(GameContext.getI18n().getText(TextId.REFRESH_NPC_PARAM_ERROR));
				return resp;
			}
			String[] mapInstanceIds = mapInstanceId.split(",");
			for(int i=0; i<mapInstanceIds.length; i++){
				MapInstance mapInstance = GameContext.getMapApp().getMapInstance(mapInstanceIds[i]);
				if(mapInstance == null){
					continue;
				}
				mapInstance.summonCreateNpc(npcId,mapX,mapY);
			}
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}catch(Exception e){
			this.logger.error("RefreshNpcAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
