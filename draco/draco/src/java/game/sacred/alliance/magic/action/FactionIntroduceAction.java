//package sacred.alliance.magic.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.FactionMemberItem;
//import com.game.draco.message.request.C1704_FactionIntroduceReqMessage;
//import com.game.draco.message.response.C0002_ErrorRespMessage;
//import com.game.draco.message.response.C1704_FactionIntroduceRespMessage;
//
//import sacred.alliance.magic.app.faction.FactionUpgrade;
//import sacred.alliance.magic.base.FactionPowerType;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionRole;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionIntroduceAction extends BaseAction<C1704_FactionIntroduceReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1704_FactionIntroduceReqMessage reqMsg) {
//		try {
//			RoleInstance role = this.getCurrentRole(context);
//			if(!role.hasUnion()){
//				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.FACTION_NOT_HAVE_FACTION));
//			}
//			Faction faction = GameContext.getFactionApp().getFaction(role);
////			String factionId = faction.getFactionId();
//			//弹劾更新，超过弹劾保护时间，更换帮主
////			GameContext.getFactionApp().impeachUpdate(factionId);
//			//门派界面
//			C1704_FactionIntroduceRespMessage resp = new C1704_FactionIntroduceRespMessage();
//			resp.setFactionName(faction.getFactionName());
//			resp.setFactionLevel(faction.getFactionLevel());
//			resp.setLeaderName(faction.getLeaderName());
//			resp.setMemberNum((short) faction.getMemberNum());
//			resp.setMaxMemberNum((short) faction.getMaxMemberNum());
//			resp.setFactionNotice(faction.getFactionDesc());
//			resp.setFactionContribution(faction.getContribution());
//			
//			FactionUpgrade fu = GameContext.getFactionApp().getFactionUpgradeMap().get(faction.getFactionLevel());
//			resp.setMaxFactionContribution(fu.getContribution());
//			resp.setFactionMoney(faction.getFactionMoney());
//			List<FactionRole> frList = GameContext.getFactionApp().getFactionRoleList(role.getFactionId());
//			if(Util.isEmpty(frList)){
//				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.SYSTEM_ERROR));
//			}
//			List<FactionRole> sortList = GameContext.getFactionApp().getFactionRoleListByOnline(frList);
//			List<FactionMemberItem> factionMemberList = new ArrayList<FactionMemberItem>();
//			for(FactionRole fr : sortList){
//				if(null == fr){
//					continue;
//				}
//				FactionMemberItem item = new FactionMemberItem();
//				int roleId = fr.getRoleId();
//				item.setRoleId(roleId);
//				item.setCareer(fr.getCareer());
//				item.setRoleName(fr.getRoleName());
//				item.setRoleLevel((byte)fr.getRoleLevel());
//				item.setPosition(fr.getPosition());
//				item.setContribution(fr.getTotalContribution());
//				item.setOnline((byte)0);
//				item.setLastOfflineTime(fr.getLastOfflineTime());
//				RoleInstance member = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//				if(null != member){
//					//门派成员在线时，角色名和等级从角色对象上取
//					item.setRoleName(member.getRoleName());
//					item.setRoleLevel((byte) member.getLevel());
//					item.setOnline((byte) 1);
//					item.setLastOfflineTime(this.getText(TextId.Faction_Role_Online));
//				}
//				factionMemberList.add(item);
//			}
//			resp.setFactionMemberList(factionMemberList);
//			//改名标识
//			boolean canModify = GameContext.getFactionApp().canModifyFactionName(faction.getFactionName());
//			resp.setChangeNameFlag((canModify && GameContext.getFactionApp().haveFactionPowerType(role, FactionPowerType.Modify_Name))?(byte)1:(byte)0);
//			return resp;
//		} catch (Exception e) {
//			this.logger.error("FactionIntroduceAction", e);
//			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.Faction_FAILURE));
//		}
//	}
//}
