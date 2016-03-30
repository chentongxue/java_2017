package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.MailGoodsItem;
import com.game.draco.debug.message.request.C10031_MailRecoupAddReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.recoup.Recoup;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class MailRecoupAddAction extends ActionSupport<C10031_MailRecoupAddReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10031_MailRecoupAddReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		try{
			Recoup recoup = new Recoup();
			recoup.setSenderName(reqMsg.getSenderName());
			recoup.setTitle(reqMsg.getTitle());
			recoup.setContext(reqMsg.getContent());
			recoup.setBindMoney(reqMsg.getBindMoney());
			recoup.setGameMoney(reqMsg.getSilverMoney());
			recoup.setStartTime(reqMsg.getStartTime());
			recoup.setEndTime(reqMsg.getEndTime());
			recoup.setChannelId(reqMsg.getChannelId());
			List<GoodsOperateBean> goodsList = new ArrayList<GoodsOperateBean>();
			for(MailGoodsItem item : reqMsg.getGoodsList()){
				if(null == item){
					continue;
				}
				goodsList.add(GoodsOperateBean.createAddGoodsBean(item.getGoodsId(), item.getGoodsNum(), item.getBind()));
			}
			recoup.setGoodsList(goodsList);
			GameContext.getRecoupApp().insertRecoup(recoup);
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}catch(Exception e){
			logger.error("MailRecoupAddAction error: ",e);
			resp.setType((byte)RespTypeStatus.FAILURE);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}
	
}
