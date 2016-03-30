package sacred.alliance.magic.kefu.action;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;

import platform.message.request.C5801_BugInfoKeFuReplyReqMessage;
import platform.message.response.C5801_BugInfoKeFuReplyRespMessage;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

public class KeFuReplyProblemAction extends ActionSupport<C5801_BugInfoKeFuReplyReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C5801_BugInfoKeFuReplyReqMessage req) {
		C5801_BugInfoKeFuReplyRespMessage resp = new C5801_BugInfoKeFuReplyRespMessage();
		resp.setStatus((byte) 0);
		try{
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setSendRole(MailSendRoleType.GM.getName());
			mail.setRoleId(req.getRoleId());
			mail.setTitle(GameContext.getI18n().getText(TextId.Kefu_reply_mail_title));
			mail.setContent(req.getInfo());
			Status status = GameContext.getMailApp().sendMail(mail);
			if(!status.isSuccess()){
				resp.setInfo(status.getTips());
				return resp;
			}
			resp.setStatus((byte) 1);
			return resp;
		}catch(Exception e){
			logger.error("",e);
			resp.setInfo("ERROR");
			return resp;
		}
	}
}
