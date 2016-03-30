package com.game.draco.app.mail.domain;

import java.util.Date;

import lombok.Data;

import org.springframework.beans.BeanUtils;

import sacred.alliance.magic.domain.RoleGoods;

public @Data class MailGoods extends RoleGoods implements java.io.Serializable{
	
	private String mailId;
	private Date sendTime;
	
	public RoleGoods createRoleGoods(){
		RoleGoods rg = new RoleGoods();
		BeanUtils.copyProperties(this,rg);
		return rg;
	}
	
	public static MailGoods createMailGoods(RoleGoods rg,String mailId,Date sendTime){
		MailGoods mg = new MailGoods();
		mg.mailId = mailId;
		mg.sendTime = sendTime;
		BeanUtils.copyProperties(rg,mg);
		return mg;
	}
	
}
