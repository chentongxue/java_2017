package sacred.alliance.magic.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.message.request.C0222_SearchRoadReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 为客户端自动寻路提供支持
 */
public class SearchRoadAction extends BaseAction<C0222_SearchRoadReqMessage> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C0222_SearchRoadReqMessage req) {
		RoleInstance instance = this.getCurrentRole(context);
		/*SearchRoadRespMessage resp = new SearchRoadRespMessage();
		if(instance == null){
			return null;
		}
		  try {
		    	//先骑马再寻路
		    	//GameContext.getRideHorseApplication().sitUpHorse(instance);
				String userMapId = instance.getMapId();
				// 获取客户端请求信息
				byte type = req.getType();
				byte way = req.getWay();
				// NPC模板ID
				if (type == 1) {
					List<PointItem> points = GameContext.getAutoSearchRoadApp()
						.searchNPC(userMapId, req.getId(),null);

					if (points != null) {
						resp.setResultId((byte) 1);
						resp.setSearchMapPoint(points);
						return resp;
					}
				}
				// 任务ID
				else if(type == 2){
					Point point = GameContext.getAutoSearchRoadApp().getQuestTarget(instance, req.getQuestId(), null);
					GameContext.getUserMapApp().gotoTargetMapPoint(instance, way, point);
					return null;
				}

			} catch (Exception e) {
				logger.error("自动寻路异常", e);
				resp.setResultId((byte) 0);
				resp.setInfo("此任务不能自动寻路!");
				return resp;
			}*/
			return null;
	}
	
}
