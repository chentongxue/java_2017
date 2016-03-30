package sacred.alliance.magic.debug.action;

import java.text.MessageFormat;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.debug.message.item.MailGoodsItem;
import com.game.draco.debug.message.request.C10018_MailSendReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

public class MailSendAction extends ActionSupport<C10018_MailSendReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10018_MailSendReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			RoleInstance roleInstance = null;
			String reqInfo = reqMsg.getRoleInfo();
			if(reqMsg.getInfoType() == 0){
				roleInstance = GameContext.getUserRoleApp().getRoleByRoleId(reqInfo);
			}else if(reqMsg.getInfoType() == 1){
				roleInstance = GameContext.getUserRoleApp().getRoleByRoleName(reqInfo);
			}
			if(null == roleInstance){
				resp.setInfo(GameContext.getI18n().getText(TextId.ROLE_OFFLINE_FAIL));
				return resp;
			}
			List<MailGoodsItem> goodsList = reqMsg.getGoodsList();
			if(null != goodsList && goodsList.size() > Mail.MaxAccessoryNum){
				resp.setInfo(GameContext.getI18n().messageFormat(TextId.MAIL_SEND_TOO_MANY_ACCESSORY, Mail.MaxAccessoryNum));
				return resp;
			}
			Status status = GameContext.getMailApp().sendMail(this.getMail(reqMsg,roleInstance));
			if(!status.isSuccess()){
				resp.setInfo(status.getTips());
				return resp;
			}
			resp.setType((byte)RespTypeStatus.SUCCESS);
			resp.setInfo(status.getTips());
			return resp;
		}catch(Exception e){
			logger.error("MailSendAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}
	
	private Mail getMail(C10018_MailSendReqMessage req,RoleInstance roleInstance){
		try{
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setContent(req.getContent());
			mail.setPayGold(req.getPayMoney());
			mail.setTitle(req.getTitle());
			mail.setRoleId(roleInstance.getRoleId());
			mail.setGold(req.getGold());
			mail.setExp(req.getExp());
			mail.setSilverMoney(req.getSilverMoney());
			mail.setBindGold(req.getBindGold());
			mail.setSendRole(MailSendRoleType.GM.getName());
			mail.setSendSource(OutputConsumeType.gm_output_mail.getType());
			List<MailGoodsItem> goodsList = req.getGoodsList();
			if(null != goodsList){
				for(MailGoodsItem mgi : goodsList){
					BindingType bindType = BindingType.get(mgi.getBind());
					Status status = mail.addMailAccessory(mgi.getGoodsId(), mgi.getGoodsNum(), bindType);
					if(!status.isSuccess()){
						return null;
					}
				}
			}
			return mail;
		}catch(Exception e){
			logger.error("MailSendAction getMail:",e);
		}
		return null;
	}
	
}
