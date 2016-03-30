package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.CarnivalItem;
import com.game.draco.debug.message.request.C10040_CarnivalReqMessage;
import com.game.draco.debug.message.response.C10040_CarnivalRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.CarnivalRankInfo;
import sacred.alliance.magic.util.Util;

public class CarnivalAction extends ActionSupport<C10040_CarnivalReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10040_CarnivalReqMessage req) {
		C10040_CarnivalRespMessage resp = new C10040_CarnivalRespMessage();
		try{
			List<CarnivalRankInfo> list = GameContext.getBaseDAO().selectAll(CarnivalRankInfo.class);
			if(Util.isEmpty(list)){
				return resp;
			}
			List<CarnivalItem> items = new ArrayList<CarnivalItem>();
			CarnivalItem item = null;
			for(CarnivalRankInfo info : list) {
				if(null == info){
					continue;
				}
				item = new CarnivalItem();
				item.setActiveId(info.getActiveId());
				item.setCampId(info.getCampId());
				item.setCareer(info.getCareer());
				item.setName(info.getName());
				item.setRank(info.getRank());
				item.setTargetId(info.getTargetId());
				item.setTargetValue(info.getTargetValue());
				items.add(item);
			}
			resp.setItems(items);
			return resp;
		}catch(Exception e){
			logger.error("debug Carnival error: ",e);
			return resp;
		}
	}

}
