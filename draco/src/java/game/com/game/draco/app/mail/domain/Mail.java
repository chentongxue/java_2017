package com.game.draco.app.mail.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.mail.type.MailFreezeType;

import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;

public @Data class Mail {
	
	private String mailId;
	private String roleId;
	
	private int gold;
	private int bindGold;
	private int silverMoney;
	private int exp;
	private int potential;
	private int honor;
	private int dkp;
	
	private int look;
	private Date sendTime;
	private String content = "";
	private String title;
	private String sendRole;
	private int existGoods;
	private int sendSource;//邮件的来源类型
	private int contentId;//文本ID
	private int payGold;
	private int freeze;
	
	/***********非数据库字段*****/
	public static final int MaxAccessoryNum = 10 ;//邮件最大附件数量
	private static final Logger logger = LoggerFactory.getLogger(Mail.class);
	private List<MailAccessory> maList = new ArrayList<MailAccessory>();
	private List<RoleGoods> rgList = new ArrayList<RoleGoods>();
	
	private String logContent="";
	
	public String getContent(){
		if(null == this.content){
			return "" ;
		}
		return this.content ;
	}
	
	/**
	 * 是否是付费邮件
	 * @return
	 */
	public boolean isPayMail(){
		return payGold > 0;
	}
	
	/**
	 * 支付金条是否足够
	 * @param gold
	 * @return
	 */
	public boolean isPayGoldEnough(int gold){
		return gold < this.payGold;
	}
	public int getPayGold(){
		return this.payGold;
	}
	public boolean isExistGoods(){
		return existGoods == 1;
	}
	public boolean isExistAttri(){
		return gold != 0 || bindGold != 0 || silverMoney != 0 || exp != 0 || potential != 0 || honor != 0 || dkp !=0;
	}
	public boolean isLook(){
		return look == 1;
	}
	
	public Mail(String mailId){
		this.mailId = mailId ;
		sendTime = new Date();
	}
	
	public Mail(){
		
	}
	
	public Status addRoleGoods(RoleGoods rg){
		if(null == rg){
			return null ;
		}
		rg.setRoleId(roleId);//设置些物品为收信人ID
		RoleGoodsHelper.destructor(rg);
		rgList.add(rg);
		return addMailAccessory(rg.getId(), rg.getGoodsId(), 
				rg.getCurrOverlapCount(),BindingType.get(rg.getBind()));
	}
	
	/**添加邮件附件*/
	public Status addMailAccessory(int templateId,int num ,BindingType bindType){
		return addMailAccessory(null,templateId, num , bindType);
	}
	
	private Status addMailAccessory(String instanceId,int templateId,int num ,BindingType bind){
		MailAccessory ma = new MailAccessory();
		ma.setBind(bind.getType());
		ma.setMailId(mailId);
		ma.setSendTime(sendTime);
		ma.setNum(num);
		ma.setRoleId(roleId);
		ma.setTemplateId(templateId);
		ma.setInstanceId(instanceId);
		maList.add(ma);
		if(isError()){
			return Status.Mail_Add_Goods_Err;
		}
		return Status.SUCCESS;
	}

	private boolean isError(){
		return maList.size()>rgList.size() && rgList.size() != 0;
	}
	
	public Status save(){
		this.setExistGoods(0);
		this.addAccessoryInfo();
		if(0 != maList.size()){
			this.setExistGoods(1);
		}
		GameContext.getMailApp().insertMail(this);
		if(0 == maList.size()){
			return Status.SUCCESS;
		}
		GameContext.getMailApp().insertMailAccessory(maList);
		GameContext.getMailApp().insertMailGoods(this.roleGoodsToMailGoods());
		return Status.SUCCESS;
	}
	private void addAccessoryInfo(){
		try{
			StringBuffer info = new StringBuffer("\n");
			if(this.gold > 0){
				info.append(AttributeType.goldMoney.getName());
				info.append("x");
				info.append(gold);
				info.append("\n");
			}
			if(this.silverMoney > 0){
				info.append(AttributeType.gameMoney.getName());
				info.append("x");
				info.append(silverMoney);
				info.append("\n");
			}
			if(this.exp > 0){
				info.append(AttributeType.exp.getName());
				info.append("x");
				info.append(exp);
				info.append("\n");
			}
			if(this.potential > 0){
				info.append(AttributeType.potential.getName());
				info.append("x");
				info.append(potential);
				info.append("\n");
			}
			if (this.honor > 0) {
				info.append(AttributeType.honor.getName());
				info.append("x");
				info.append(honor);
				info.append("\n");
			}
			if(this.dkp > 0){
				info.append(AttributeType.dkp.getName());
				info.append("x");
				info.append(dkp);
				info.append("\n");
			}
			GoodsBase gb;
			if(maList.size() > 0){
				for(MailAccessory ma : maList){
					if(!Util.isEmpty(ma.getInstanceId())){
						continue ;
					}
					gb = GameContext.getGoodsApp().getGoodsBase(ma.getTemplateId());
					if(null == gb){
						continue ;
					}
					info.append(gb.getName());
					info.append("x");
					info.append(ma.getNum());
					info.append("\n");
				}
			}
			if(rgList.size() > 0){
				for(RoleGoods rg : rgList){
					gb = GameContext.getGoodsApp().getGoodsBase(rg.getGoodsId());
					if(null == gb){
						continue ;
					}
					info.append(gb.getName());
					info.append("x");
					info.append(rg.getCurrOverlapCount());
					info.append("\n");
				}
			}
			if(info.length() > 2){
				this.content += info;
				this.logContent += info;
				this.logContent = this.logContent.replace("\n", " ");
			}
		}catch(Exception e){
			logger.error("addAccessoryInfo()：",e);
		}
	}
	private List<MailGoods> roleGoodsToMailGoods(){
		List<MailGoods> mgList = new ArrayList<MailGoods>();
		for(RoleGoods rg : rgList){
			mgList.add(MailGoods.createMailGoods(rg, mailId, sendTime));
		}
		return mgList;
	}
	
	/**
	 * 是否被冻结
	 * @return
	 */
	public boolean isFreeze(){
		return MailFreezeType.Freeze.getType() == this.freeze;
	}
	
	/**
	 * 获取冻结的邮件标题
	 * 未冻结，正常显示
	 * 已冻结，红色警示
	 * @return
	 */
	public String getTitleFreeze(){
		if(MailFreezeType.Freeze.getType() != this.freeze){
			return this.title;
		}
		return GameContext.getI18n().getText(TextId.Mail_Freeze_Title_Pre) + this.title;
	}

}
