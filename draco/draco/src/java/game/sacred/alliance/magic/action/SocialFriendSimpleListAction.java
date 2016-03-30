package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.SocialFriendSimpleItem;
import com.game.draco.message.request.C1206_SocialFriendSimpleListReqMessage;
import com.game.draco.message.response.C1206_SocialFriendSimpleListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleSocialRelation;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialFriendSimpleListAction extends BaseAction<C1206_SocialFriendSimpleListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1206_SocialFriendSimpleListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		String roleId = role.getRoleId();
		List<SocialFriendSimpleItem> friendList = new ArrayList<SocialFriendSimpleItem>();
		for(RoleSocialRelation relation : GameContext.getSocialApp().getFriendList(role)){
			if(null == relation){
				continue;
			}
			String otherRoleId = relation.getOtherRoleId(roleId);
			RoleInstance otherRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(otherRoleId);
			if(null == otherRole){
				continue;
			}
			SocialFriendSimpleItem item = new SocialFriendSimpleItem();
			item.setRoleId(Integer.valueOf(otherRoleId));
			//名称从角色身上取
			item.setRoleName(otherRole.getRoleName());
			item.setSex(otherRole.getSex());
			item.setCamp(otherRole.getCampId());
			item.setCareer(otherRole.getCareer());
			item.setRoleLevel((byte) otherRole.getLevel());
			//更新好友记录中，在线玩家的名称、性别、阵营、职业（update时不会入库）
			relation.updateOnlineRole(otherRole);
			friendList.add(item);
		}
		this.sortFriendSimpleList(friendList);
		C1206_SocialFriendSimpleListRespMessage resp = new C1206_SocialFriendSimpleListRespMessage();
		resp.setFriendList(friendList);
		return resp;
	}
	
	/**
	 * 好友列表排序
	 * @param friendList
	 */
	private void sortFriendSimpleList(List<SocialFriendSimpleItem> friendList){
		Collections.sort(friendList, new Comparator<SocialFriendSimpleItem>(){
			@Override
			public int compare(SocialFriendSimpleItem item1, SocialFriendSimpleItem item2) {
				byte level1 = item1.getRoleLevel();
				byte level2 = item2.getRoleLevel();
				if(level1 > level2){
					return -1;
				}
				if(level1 < level2){
					return 1;
				}
				return 0;
			}
		});
	}
	
}
