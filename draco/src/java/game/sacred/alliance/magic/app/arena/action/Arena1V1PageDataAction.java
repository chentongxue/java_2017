package sacred.alliance.magic.app.arena.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.message.item.Arena1V1PageDataItem;
import com.game.draco.message.request.C3860_Arena1V1PageDataReqMessage;
import com.game.draco.message.response.C3860_Arena1V1PageDataRespMessage;

import sacred.alliance.magic.app.arena.domain.Arena1V1RealTime;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class Arena1V1PageDataAction extends Arena1V1AbstractAction<C3860_Arena1V1PageDataReqMessage>{

	@Override
	public Message execute(ActionContext context, C3860_Arena1V1PageDataReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C3860_Arena1V1PageDataRespMessage respMsg = new C3860_Arena1V1PageDataRespMessage();
		List<Arena1V1RealTime> allData = GameContext.getArena1V1App().getArena1V1RealTime();
		if(Util.isEmpty(allData)){
			respMsg.setTotalPage((short)1);
			return respMsg ;
		}
		int recordSize = allData.size() ;
		int totalPage = this.getPage(recordSize);
		
		int page = reqMsg.getPage() ;
		if(page <=1){
			page = 1 ;
		}
		List<Arena1V1PageDataItem> pageDataList = new ArrayList<Arena1V1PageDataItem>();
		int pageSize = GameContext.getArena1V1App().getPageSize() ;
		int start = (page-1)*pageSize ;
		for(int i = start ;i < start + pageSize;i++){
			if(i >= recordSize){
				break ;
			}
			Arena1V1RealTime realData = allData.get(i);
			Arena1V1PageDataItem item = new Arena1V1PageDataItem();
			item.setBattleScore(realData.getBattleScore());
			item.setCamp(realData.getCampId());
			item.setRank((short)(i+1));
			item.setRoleName(realData.getRoleName());
			item.setTotalScore(realData.getScore());
            pageDataList.add(item);
            RoleInstance onlineRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(realData.getRoleId());
            if(null == onlineRole){
                continue ;
            }
			item.setBattleScore(onlineRole.getBattleScore());
            realData.setBattleScore(onlineRole.getBattleScore());
		}
		respMsg.setCurrentPage((short)page);
		respMsg.setTotalPage((short)totalPage);
		respMsg.setPageDataList(pageDataList);
		return respMsg;
	}

}
