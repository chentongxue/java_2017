//package sacred.alliance.magic.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.FactionApplyJoinItem;
//import com.game.draco.message.request.C1706_FactionApplyJoinListReqMessage;
//import com.game.draco.message.response.C1706_FactionApplyJoinListRespMessage;
//
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.UnionMember;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionApplyJoinListAction extends BaseAction<C1706_FactionApplyJoinListReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1706_FactionApplyJoinListReqMessage reqMsg) {
//		RoleInstance role = this.getCurrentRole(context);
//		List<UnionMember> list = GameContext.getFactionApp().getApplyJoinList(role.getFactionId());
//		List<FactionApplyJoinItem> factionJoinList = new ArrayList<FactionApplyJoinItem>();
//		for(UnionMember factionRole : list){
//			if(null == factionRole){
//				continue;
//			}
//			FactionApplyJoinItem item = new FactionApplyJoinItem();
//			item.setRoleId(factionRole.getRoleId());
//			item.setRoleName(factionRole.getRoleName());
//			item.setRoleLevel((byte) factionRole.getRoleLevel());
//			item.setCareer(factionRole.getCareer());
//			factionJoinList.add(item);
//		}
//		C1706_FactionApplyJoinListRespMessage resp = new C1706_FactionApplyJoinListRespMessage();
//		resp.setFactionJoinList(factionJoinList);
//		return resp;
//	}
//
//}
