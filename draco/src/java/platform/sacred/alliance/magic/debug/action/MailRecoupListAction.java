package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.MailRecoupItem;
import com.game.draco.debug.message.request.C10030_MailRecoupListReqMessage;
import com.game.draco.debug.message.response.C10030_MailRecoupListRespMessage;

import sacred.alliance.magic.app.recoup.Recoup;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;

public class MailRecoupListAction extends ActionSupport<C10030_MailRecoupListReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10030_MailRecoupListReqMessage reqMsg) {
		C10030_MailRecoupListRespMessage resp = new C10030_MailRecoupListRespMessage();
		Collection<Recoup> collection = GameContext.getRecoupApp().getAllRecoup();
		if(Util.isEmpty(collection)){
			return resp;
		}
		List<MailRecoupItem> recoupList = new ArrayList<MailRecoupItem>();
		for(Recoup recoup : collection){
			if(null == recoup){
				continue;
			}
			MailRecoupItem item = new MailRecoupItem();
			item.setId(recoup.getId());
			item.setSenderName(recoup.getSenderName());
			item.setTitle(recoup.getTitle());
			item.setContent(recoup.getContext());
			item.setBindMoney(recoup.getBindMoney());
			item.setSilverMoney(recoup.getGameMoney());
			item.setGoodsInfo(recoup.getGoodsInfo());
			item.setStartTime(recoup.getStartTime());
			item.setEndTime(recoup.getEndTime());
			recoupList.add(item);
		}
		resp.setRecoupList(recoupList);
		return resp;
	}
	
}
