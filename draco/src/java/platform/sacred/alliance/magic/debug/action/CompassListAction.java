package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.app.compass.config.Compass;
import com.game.draco.debug.message.item.CompassItem;
import com.game.draco.debug.message.request.C10034_CompassListReqMessage;
import com.game.draco.debug.message.response.C10034_CompassListRespMessage;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.GoodsBase;

import java.util.ArrayList;
import java.util.List;

public class CompassListAction extends ActionSupport<C10034_CompassListReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10034_CompassListReqMessage reqMsg) {
		C10034_CompassListRespMessage resp = new C10034_CompassListRespMessage();
		List<CompassItem> compassList = new ArrayList<CompassItem>();
		for(Compass compass : GameContext.getCompassApp().getAllCompass()){
			if(null == compass){
				continue;
			}
			CompassItem item = new CompassItem();
			item.setId(compass.getTaobaoId());
			item.setName(compass.getName());
			item.setGoldMoney(compass.getConsume1());
			int goodsId = compass.getGoodsId();
			item.setGoodsId(goodsId);
			GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null != goodsBase){
				item.setGoodsName(goodsBase.getName());
			}
			compassList.add(item);
		}
		resp.setCompassList(compassList);
		return resp;
	}
	
}
