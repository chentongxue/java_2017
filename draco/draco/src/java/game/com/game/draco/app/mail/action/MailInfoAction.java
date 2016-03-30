package com.game.draco.app.mail.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.domain.MailAccessory;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.request.C1002_MailInfoReqMessage;
import com.game.draco.message.response.C1009_MailInfoRespMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.RoleInstance;

public class MailInfoAction extends BaseAction<C1002_MailInfoReqMessage>{

	@Override
	public Message execute(ActionContext context, C1002_MailInfoReqMessage req) {
		try{
			RoleInstance role = this.getCurrentRole(context);
			if(role == null){
				return null;
			}
			C1009_MailInfoRespMessage resp = new C1009_MailInfoRespMessage();
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
			if(mail.getBindGold() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.bindingGoldMoney.getType());
				item.setAttriValue(mail.getBindGold());
				list.add(item);
			}
			if(mail.getSilverMoney() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.silverMoney.getType());
				item.setAttriValue(mail.getSilverMoney());
				list.add(item);
			}
//			if(mail.getContribute() > 0){
//				AttriTypeValueItem item = new AttriTypeValueItem();
//				item.setAttriType(AttributeType.contribute.getType());
//				item.setAttriValue(mail.getContribute());
//				list.add(item);
//			}
			if(mail.getZp() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.potential.getType());
				item.setAttriValue(mail.getZp());
				list.add(item);
			}
			/*if(mail.getMagicSoul() > 0){
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.magicSoul.getType());
				item.setAttriValue(mail.getMagicSoul());
				list.add(item);
			}*/
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
