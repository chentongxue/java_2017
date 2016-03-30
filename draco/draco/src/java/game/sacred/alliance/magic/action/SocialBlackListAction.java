package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.SocialBlackItem;
import com.game.draco.message.request.C1209_SocialBlackListReqMessage;
import com.game.draco.message.response.C1209_SocialBlackListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleSocialRelation;
import sacred.alliance.magic.vo.RoleInstance;

public class SocialBlackListAction extends BaseAction<C1209_SocialBlackListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1209_SocialBlackListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		String selfRoleId = role.getRoleId();
		List<SocialBlackItem> blackList = new ArrayList<SocialBlackItem>();
		for(RoleSocialRelation relation : GameContext.getSocialApp().getBlackList(role)){
			if(null == relation){
				continue;
			}
			SocialBlackItem item = new SocialBlackItem();
			String otherRoleId = relation.getOtherRoleId(selfRoleId);
			item.setRoleId(Integer.valueOf(otherRoleId));
			item.setRoleName(relation.getRoleName(otherRoleId));
			item.setSex(relation.getSex(otherRoleId));
			item.setCamp(relation.getCamp(otherRoleId));
			item.setCareer(relation.getCareer(otherRoleId));
			RoleInstance otherRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(otherRoleId);
			if(null != otherRole){
				item.setRoleLevel((byte) otherRole.getLevel());
				//角色名从在线角色上取
				item.setRoleName(otherRole.getRoleName());
				item.setOnline((byte) 1);
			}
			blackList.add(item);
		}
		this.sortFriendList(blackList);
		C1209_SocialBlackListRespMessage resp = new C1209_SocialBlackListRespMessage();
		resp.setBlackList(blackList);
		return resp;
	}
	
	/**
	 * 好友列表排序
	 * @param friendList
	 */
	private void sortFriendList(List<SocialBlackItem> blackList){
		Collections.sort(blackList, new Comparator<SocialBlackItem>(){
			
			@Override
			public int compare(SocialBlackItem item1, SocialBlackItem item2) {
				if(item1.getOnline() > item2.getOnline()){
					return -1;
				}
				if(item1.getOnline() < item2.getOnline()){
					return 1;
				}
				return 0;
			}});
	}
	
}
