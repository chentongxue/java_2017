package com.game.draco.app.mail.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.domain.MailAccessory;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.request.C1002_MailInfoReqMessage;
import com.game.draco.message.response.C1002_MailInfoRespMessage;

public class MailInfoAction extends BaseAction<C1002_MailInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C1002_MailInfoReqMessage req) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			C1002_MailInfoRespMessage resp = new C1002_MailInfoRespMessage();
			Mail mail = GameContext.getMailApp().getMailInfoByDB(req.getMailId());
			if(null == mail || !mail.getRoleId().equals(role.getRoleId())){
				resp.setType(Status.FAILURE.getInnerCode());
				resp.setInfo(Status.Mail_Not_Exist.getTips());
				return resp;
			}
			resp.setType(Status.SUCCESS.getInnerCode());
			//resp.setContent(mail.getContent());
			//邮件内容调用下面接口
			resp.setContent(GameContext.getMailApp().getDisplayContext(mail, role));
			List<AttriTypeValueItem> list = new ArrayList<AttriTypeValueItem>();
			if(mail.getExp() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.exp.getType());
				item.setAttriValue(mail.getExp());
				list.add(item);
			}
			if(mail.getGold() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.goldMoney.getType());
				item.setAttriValue(mail.getGold());
				list.add(item);
			}
			if(mail.getSilverMoney() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.gameMoney.getType());
				item.setAttriValue(mail.getSilverMoney());
				list.add(item);
			}
			if(mail.getPotential() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.potential.getType());
				item.setAttriValue(mail.getPotential());
				list.add(item);
			}
			if(mail.getDkp() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.dkp.getType());
				item.setAttriValue(mail.getDkp());
				list.add(item);
			}
			if (mail.getHonor() > 0) {
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.honor.getType());
				item.setAttriValue(mail.getHonor());
				list.add(item);
			}
		
			resp.setList(list);
			resp.setPayGold(mail.getPayGold());
			if(!mail.isLook()){
				mail.setLook(1);
				GameContext.getBaseDAO().update(mail);
			}
			if(!mail.isExistGoods()){
				return resp;
			}
			List<MailAccessory> maList = GameContext.getMailApp().getMailAccessoryListByDB(role.getRoleId(), req.getMailId());
			if(null == maList || 0 == maList.size()){
				return resp;
			}
			List<GoodsLiteItem> goodsInfo = new ArrayList<GoodsLiteItem>();
			for(MailAccessory ma : maList){
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(ma.getTemplateId());
				if(null == gb){
					continue;
				}
				GoodsLiteItem item = gb.getGoodsLiteItem();
				//改变绑定类型与数量
				item.setBindType((byte) ma.getBind());
				item.setNum((short) ma.getNum());
				goodsInfo.add(item);
			}
			resp.setGoodsInfo(goodsInfo);
			return resp;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
}
