package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10028_DismissRoleReqMessage;
import com.game.draco.debug.message.response.C10028_DismissRoleRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class DismissRoleAction extends ActionSupport<C10028_DismissRoleReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10028_DismissRoleReqMessage req) {
		C10028_DismissRoleRespMessage resp = new C10028_DismissRoleRespMessage();
		try{
			List<String> failedList = new ArrayList<String>();
			for(String info : req.getInfos()){
				if(Util.isEmpty(info)){
					continue;
				}
				RoleInstance role =  null;
				try {
					if(req.getInfoType() == 0){
						role = GameContext.getUserRoleApp().getRoleByRoleId(info);
					}else if(req.getInfoType() == 1){
						role = GameContext.getUserRoleApp().getRoleByRoleName(info);
					}
				} catch (ServiceException e) {
					this.logger.error("DismissRoleAction error: ", e);
				}
				if(role == null){
					failedList.add(info);
					continue;
				}
				int[] types = req.getTypes();
				Date now = new Date();
				for(int type : types){
					//1:解除隔离
					if(1 == type){
						role.setFrozenEndTime(now);
					}
					//2:解除禁言
					if(2 == type){
						role.setForbidType(0);
						role.setForbidEndTime(null);
					}
				}
				//更新库
				GameContext.getUserRoleApp().updateFrozenAndForbid(role);
			}
			int size = failedList.size();
			if(size > 0){
				String[] failedInfos = new String[size];
				for(int i=0; i<size; i++){
					failedInfos[i] = failedList.get(i);
				}
				resp.setFailedInfos(failedInfos);
			}
			return resp;
		}catch(Exception e){
			logger.error("DismissRoleAction error: ",e);
			return resp;
		}
	}

}
