package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.attri.calct.FormulaCalct;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.request.C1101_RoleAttrPanelReqMessage;
import com.game.draco.message.response.C1101_RoleAttrPanelRespMessage;

public class RoleAttrPanelAction extends BaseAction<C1101_RoleAttrPanelReqMessage> {

	private static List<AttributeType> list = new ArrayList<AttributeType>();

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
			
			int attValue = 0;
			if(AttributeType.phyAtk == attriType 
					|| AttributeType.iceAtk == attriType
				    || AttributeType.fireAtk == attriType) {
				attValue = role.getAtkAttrDisplay(attriType);
			}
			else if(AttributeType.phyRit == attriType 
					|| AttributeType.iceRit == attriType
				    || AttributeType.fireRit == attriType) {
				attValue = role.getRitAttrDisplay(attriType);
			}
			else {
				attValue = role.get(attriType);
			}
			/*if (AttributeType.hit == attriType) {
				// 面板显示：实际值-10000
				attValue -= FormulaCalct.DEFAULT_HIT_VALUE;
			}*/
			item.setValue(AttributeType.formatValue(attriType.getType(), attValue));
			showAttrItemes.add(item);
		}
		respMsg.setShowAttrItemes(showAttrItemes);
		return respMsg;
	}

	static {
		list.add(AttributeType.phyAtk);
		list.add(AttributeType.phyRit);
		list.add(AttributeType.iceAtk);
		list.add(AttributeType.iceRit);
		list.add(AttributeType.fireAtk);
		list.add(AttributeType.fireRit);
		list.add(AttributeType.critAtk);
		list.add(AttributeType.critRit);
		list.add(AttributeType.hit);
		list.add(AttributeType.dodge);
		list.add(AttributeType.battleScore);
	}
}
