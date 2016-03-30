package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.MapLineItem;
import com.game.draco.message.request.C0254_MapLineListReqMessage;
import com.game.draco.message.response.C0254_MapLineListRespMessage;

import sacred.alliance.magic.base.MapLineType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;
import sacred.alliance.magic.vo.MapLineInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class MapLineListAction extends BaseAction<C0254_MapLineListReqMessage>{

	@Override
	public Message execute(ActionContext context, C0254_MapLineListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance){
			return null ;
		}
		C0254_MapLineListRespMessage respMsg = new C0254_MapLineListRespMessage();
		List<MapLineItem> items = new ArrayList<MapLineItem>();
		MapLineContainer container = GameContext.getMapApp().getMapLineContainer(role.getMapId());
		Collection<MapLineInstance> list = container.getMapInstances();
		for(MapLineInstance lineInstance : list){
//			int lineNum = lineInstance.getLineId();
//			if(lineNum == role.getLineId()){
//				continue ;
//			}
			
			int roleCount = lineInstance.getRoleCount();
			int limitRoleCount = lineInstance.getMap().getMapConfig().getMaxRoleCount();
			MapLineItem item = new MapLineItem();
			item.setLineId((byte)lineInstance.getLineId());
			item.setStatus((byte)MapLineType.getMapLineStatus(roleCount, limitRoleCount));
			items.add(item);
		}
		
		Collections.sort(items, new Comparator<MapLineItem>(){
			@Override
			public int compare(MapLineItem line1, MapLineItem line2) {
				if(line1.getLineId() > line2.getLineId()){
					return 1;
				}
				return -1;
			}
		});

		respMsg.setItems(items);
		return respMsg ;
		
	}

}
