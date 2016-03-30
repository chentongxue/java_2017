//package sacred.alliance.magic.debug.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.debug.message.item.FactionRoleItem;
//import com.game.draco.debug.message.request.C10062_FactionRoleListReqMessage;
//import com.game.draco.debug.message.response.C10062_FactionRoleListRespMessage;
//
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.core.action.ActionSupport;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.UnionMember;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionRoleListAction extends ActionSupport<C10062_FactionRoleListReqMessage>{
//	
//	@Override
//	public Message execute(ActionContext context, C10062_FactionRoleListReqMessage reqMsg) {
//		C10062_FactionRoleListRespMessage resp = new C10062_FactionRoleListRespMessage();
//		try{
//			String factionId = reqMsg.getFactionId();
//			Faction faction = GameContext.getFactionApp().getFaction(factionId);
//			if(null == faction){
//				return resp;
//			}
//			resp.setFactionId(factionId);
//			resp.setFactionName(faction.getFactionName());
//			List<UnionMember> frList = GameContext.getFactionApp().getFactionRoleList(factionId);
//			if(Util.isEmpty(frList)){
//				return resp;
//			}
//			List<FactionRoleItem> factionRoleList = new ArrayList<FactionRoleItem>();
//			for(UnionMember fr : frList){
//				if(null == fr){
//					continue;
//				}
//				FactionRoleItem item = new FactionRoleItem();
//				int roleId = fr.getRoleId();
//				item.setRoleId(roleId);
//				item.setCareer(fr.getCareer());
//				item.setRoleName(fr.getRoleName());
//				int frLevel = fr.getRoleLevel();
//				item.setRoleLevel((byte) frLevel);
//				item.setPosition(fr.getPosition());
//				item.setContribution(fr.getContribution());
//				item.setPrestige(fr.getPrestige());
//				item.setJoinTime(fr.getCreateDate());
//				item.setLastOfflineTime(fr.getOfflineTime());
//				item.setSignature(fr.getSignature());
//				item.setOnline((byte) 0);
//				RoleInstance member = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//				if(null != member){
//					//门派成员在线时，角色名和等级从角色对象上取
//					item.setRoleName(member.getRoleName());
//					item.setRoleLevel((byte) member.getLevel());
//					item.setOnline((byte) 1);
//				}
//				factionRoleList.add(item);
//			}
//			resp.setFactionRoleList(factionRoleList);
//			return resp;
//		}catch(Exception e){
//			this.logger.error("FactionRoleListAction error: ", e);
//			return resp;
//		}
//	}
//
//}
