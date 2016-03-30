package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.debug.message.item.UnionRoleItem;
import com.game.draco.debug.message.request.C10062_FactionRoleListReqMessage;
import com.game.draco.debug.message.response.C10062_FactionRoleListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class FactionRoleListAction extends ActionSupport<C10062_FactionRoleListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10062_FactionRoleListReqMessage reqMsg) {
		C10062_FactionRoleListRespMessage resp = new C10062_FactionRoleListRespMessage();
		try{
			String unionId = reqMsg.getUnionId();
			Union un = GameContext.getUnionApp().getUnion(unionId);
			if(null == un){
				return resp;
			}
			resp.setUnionId(unionId);
			resp.setUnionName(un.getUnionName());
			List<UnionMember> ml = GameContext.getUnionApp().getUnionMemberList(unionId);
			if(Util.isEmpty(ml)){
				return resp;
			}
			List<UnionRoleItem> unionRoleList = new ArrayList<UnionRoleItem>();
			for(UnionMember um : ml){
				if(null == um){
					continue;
				}
				//直接从DB获得
				RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleId(String.valueOf(um.getRoleId()));
				
				UnionRoleItem item = new UnionRoleItem();
				item.setCreateDate(new Date(um.getCreateTime()));
				item.setDkp(um.getDkp());
				item.setLevel(um.getLevel());
				item.setOfflineTime(um.getOfflineTime());
				
				item.setPositionName(um.getPositionNick(um.getPosition()));
				item.setRoleId(um.getRoleId());
				item.setRoleName(um.getRoleName());
				item.setUnionId(um.getUnionId());
				item.setUserId(role.getUserId());
				item.setCamp(role.getCampId());
				
				item.setOnline(GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId()) ? (byte) 1 : (byte) 0);
				unionRoleList.add(item);
			}
			resp.setFactionRoleList(unionRoleList);
			return resp;
		}catch(Exception e){
			this.logger.error("UnionRoleListAction error: ", e);
			return resp;
		}
	}

}
