package com.game.draco.app.mail.vo;
import lombok.Data;

public @Data class MailMoneyRank {
	
	private String roleId;//角色ID
	private int number;//邮件数量
	private int totalMoney;//邮件中游戏币总和
	private String roleName;//角色名称
	private int level;//角色等级
	
}
