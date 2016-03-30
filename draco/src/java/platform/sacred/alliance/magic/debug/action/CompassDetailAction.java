package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.compass.config.Compass;
import com.game.draco.app.compass.config.CompassAward;
import com.game.draco.debug.message.item.CompassDetailItem;
import com.game.draco.debug.message.request.C10035_CompassDetailReqMessage;
import com.game.draco.debug.message.response.C10035_CompassDetailRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.GoodsBase;

public class CompassDetailAction extends ActionSupport<C10035_CompassDetailReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10035_CompassDetailReqMessage reqMsg) {
		C10035_CompassDetailRespMessage resp = new C10035_CompassDetailRespMessage();
		Compass compass = GameContext.getCompassApp().getCompass(reqMsg.getId());
		if(null == compass){
			return resp;
		}
		List<CompassDetailItem> compassDetailList = new ArrayList<CompassDetailItem>();
		for(CompassAward award : compass.getAwardList()){
			if(null == award){
				continue;
			}
			CompassDetailItem item = new CompassDetailItem();
			item.setGoodsId(award.getAward());
			item.setGoodsNum((byte) award.getNum());
			item.setOdds(award.getOdds());
			GoodsBase goodsBase = award.getAwardGoods();
			if(null != goodsBase){
				item.setGoodsName(goodsBase.getName());
			}
			compassDetailList.add(item);
		}
		resp.setCompassDetailList(compassDetailList);
		return resp;
	}
	
}
