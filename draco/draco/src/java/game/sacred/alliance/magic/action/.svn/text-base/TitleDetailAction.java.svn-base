package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.app.title.TitleStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.domain.TitleRecord;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.TitleWearingItem;
import com.game.draco.message.request.C2349_TitleDetailReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C2349_TitleDetailRespMessage;

public class TitleDetailAction extends BaseAction<C2349_TitleDetailReqMessage>{

	@Override
	public Message execute(ActionContext context, C2349_TitleDetailReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int titleId = reqMsg.getTitleId() ;
		GoodsTitle title = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class, titleId);
		if(null == title){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Title_Goods_Null.getTips()); 
		}
		TitleRecord roleTitle = GameContext.getTitleApp().getRoleTitle(role, titleId);
		C2349_TitleDetailRespMessage respMsg = new C2349_TitleDetailRespMessage();
		TitleWearingItem wearingItem = Converter.getTitleWearingItem(title);
		respMsg.setItem(wearingItem);
		respMsg.setDesc(title.getDesc());
		respMsg.setPay(title.getPay());
		respMsg.setForever(title.isForever()?(byte)1:(byte)0);
		List<AttriTypeValueItem> attrList = new ArrayList<AttriTypeValueItem>();
		for(AttriItem ai : title.getAttriItemList()){
			if(null == ai){
				continue;
			}
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(ai.getAttriTypeValue());
			item.setAttriValue((int) ai.getValue());
			attrList.add(item);
		}
		respMsg.setAttrList(attrList);
		if(null == roleTitle){
			respMsg.setStatus(TitleStatus.Lack.getType());
			return respMsg ;
		}
		respMsg.setStatus(roleTitle.getActivateState());
		respMsg.setExpired(roleTitle.isTimeout()?(byte)1:(byte)0);
		respMsg.setExpiredTime(roleTitle.getStrDueTime());
		return respMsg ;
	}

}
