package sacred.alliance.magic.app.attri.action;

import java.util.Collection;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriHurtRestrictItem;
import com.game.draco.message.item.AttriHurtSeriesItem;
import com.game.draco.message.request.C0114_AttriHurtSeriesReqMessage;
import com.game.draco.message.response.C0114_AttriHurtSeriesRespMessage;
import com.google.common.collect.Lists;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.attri.config.AttriHurtRestrict;
import sacred.alliance.magic.app.attri.config.AttriHurtSeries;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class AttriHurtSeriesAction extends BaseAction<C0114_AttriHurtSeriesReqMessage> {

	@Override
	public Message execute(ActionContext context,C0114_AttriHurtSeriesReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		Collection<AttriHurtSeries> hurtSeriesList = GameContext.getAttriApp().getAttriHurtSeriesList();

		C0114_AttriHurtSeriesRespMessage respMsg = new C0114_AttriHurtSeriesRespMessage();

		if(!Util.isEmpty(hurtSeriesList)){
			List<AttriHurtSeriesItem> itemList = Lists.newArrayList();
			for(AttriHurtSeries series : hurtSeriesList) {
				AttriHurtSeriesItem item = new AttriHurtSeriesItem();
				item.setSeriesId(series.getSeriesId());
				item.setSeriesName(series.getSeriesName());
				itemList.add(item);
			}
			respMsg.setSeriesItemList(itemList);
		}

		Collection<AttriHurtRestrict> restrictList = GameContext.getAttriApp().getAttriHurtRestrictList();
		if(Util.isEmpty(restrictList)){
			return respMsg ;
		}
		List<AttriHurtRestrictItem> restrictItems = Lists.newArrayList() ;
		for(AttriHurtRestrict it : restrictList){
			AttriHurtRestrictItem item = new AttriHurtRestrictItem();
			item.setSeriesId(it.getSeriesId());
			item.setRestrictId(it.getRestrictId());
			restrictItems.add(item);
		}
		respMsg.setRestrictItemList(restrictItems);
		return respMsg;
	}

}
