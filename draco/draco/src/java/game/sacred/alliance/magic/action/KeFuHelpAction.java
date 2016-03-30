package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2701_KeFuHelpReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import platform.message.request.C5800_BugInfoReqMessage;
import platform.message.response.C5800_BugInfoRespMessage;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class KeFuHelpAction extends BaseAction<C2701_KeFuHelpReqMessage> {
	
	@Override
	public Message execute(ActionContext context, C2701_KeFuHelpReqMessage req) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			long currTime = System.currentTimeMillis();
			int gap = GameContext.getParasConfig().getKefuHelpGapTime();
			if(currTime-role.getLastKeFuHelpTime() < gap*1000){
				return new C0002_ErrorRespMessage(req.getCommandId(), Status.Sys_KeFu_Help_Re_Submit.getTips());
			}
			role.setLastKeFuHelpTime(currTime);
			C5800_BugInfoReqMessage bugMsg = new C5800_BugInfoReqMessage();
			bugMsg.setRoleId(role.getRoleId());
			bugMsg.setRoleName(role.getRoleName());
			bugMsg.setProblem(req.getInfo());
			bugMsg.setProblemType(req.getType());
			bugMsg.setServerId(GameContext.getAreaServerNotifyApp().getServerId());
			bugMsg.setAppId(GameContext.getAppId());
			bugMsg.setUserId(role.getUserId());
			bugMsg.setUserName(role.getUserName());
			bugMsg.setChannelId(role.getChannelId());
			bugMsg.setChannelUserId(role.getChannelUserId());
			//bugMsg.setChannelUserName("");
			bugMsg.setPayGoldMoney(role.getRolePayRecord().getPayGold());
			//获取客服平台的HTTP地址
			String url = GameContext.getPlatformConfig().getBugInfoHttpUrl();
			C5800_BugInfoRespMessage bugRespMsg = (C5800_BugInfoRespMessage) GameContext.getHttpJsonClient().sendMessage(bugMsg, url);
			C0003_TipNotifyMessage resp = new C0003_TipNotifyMessage();
			//KeFuHelpRespMessage resp = new KeFuHelpRespMessage();
			resp.setMsgContext(Status.Sys_KeFu_Submit_Success.getTips());
			//resp.setType(RespTypeStatus.SUCCESS);
			if(1 != bugRespMsg.getStatus()){
				resp.setMsgContext(bugRespMsg.getInfo());
				//resp.setType(RespTypeStatus.FAILURE);
			}
			return resp;
		}catch(Exception e){
			logger.error("",e);
			return new C0002_ErrorRespMessage(req.getCommandId(),this.getText(TextId.SYSTEM_ERROR));
		}
	}
}
