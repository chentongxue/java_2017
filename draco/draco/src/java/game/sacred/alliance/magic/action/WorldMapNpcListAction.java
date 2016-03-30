package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.MonsterItem;
import com.game.draco.message.item.NpcItem;
import com.game.draco.message.request.C0251_WorldMapNpcListReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0251_WorldMapNpcListRespMessage;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class WorldMapNpcListAction extends BaseAction<C0251_WorldMapNpcListReqMessage> {

	@Override
	public Message execute(ActionContext context, C0251_WorldMapNpcListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		String mapId = req.getMapId();
		Map map = GameContext.getMapApp().getMap(mapId);
		if(null == map){
			C0002_ErrorRespMessage errorMsg = new C0002_ErrorRespMessage();
			errorMsg.setReqCmdId(req.getCommandId());
			errorMsg.setInfo(this.getText(TextId.ERROR_INPUT));
			return errorMsg ;
		}
		C0251_WorldMapNpcListRespMessage respMsg = new C0251_WorldMapNpcListRespMessage();
		List<NpcItem> npcItems = new ArrayList<NpcItem>();
		List<MonsterItem> monsterItems = new ArrayList<MonsterItem>();
		GameContext.getMapApp().buildWorldMapNpcItems(role, mapId, npcItems, monsterItems);
		respMsg.setMapId(mapId);
		respMsg.setNpcItems(npcItems);
		respMsg.setMonsterItems(monsterItems);
		return respMsg;
	}

}
