package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.request.C1101_RoleAttrPanelReqMessage;
import com.game.draco.message.response.C1101_RoleAttrPanelRespMessage;

public class RoleAttrPanelAction extends BaseAction<C1101_RoleAttrPanelReqMessage> {

	private List<AttributeType> list = Lists.newArrayList(
			AttributeType.atk, AttributeType.maxHP,
			AttributeType.rit, AttributeType.breakDefense,
			AttributeType.critAtk, AttributeType.critRit,
			AttributeType.dodge, AttributeType.hit);

	@Override
	public Message execute(ActionContext context, C1101_RoleAttrPanelReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null ;
		}
		C1101_RoleAttrPanelRespMessage respMsg = new C1101_RoleAttrPanelRespMessage();
		List<AttriTypeStrValueItem> showAttrItemes = new ArrayList<AttriTypeStrValueItem>();
		for (AttributeType attriType : list) {
			AttriTypeStrValueItem item = new AttriTypeStrValueItem();
			item.setType(attriType.getType());
			item.setValue(AttributeType.formatValue(attriType.getType(), role.get(attriType)));
			showAttrItemes.add(item);
		}
		respMsg.setShowAttrItemes(showAttrItemes);
		return respMsg;
	}
}
