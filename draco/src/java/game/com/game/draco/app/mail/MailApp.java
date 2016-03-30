package com.game.draco.app.mail;

import java.util.List;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.domain.MailAccessory;
import com.game.draco.app.mail.domain.MailAttriBean;
import com.game.draco.app.mail.domain.MailGoods;
import com.game.draco.app.mail.type.MailFreezeType;

public interface MailApp {
	
	/**
	 * 得到角色的邮件列表
	 */
	public Status getMailList(RoleInstance role, int currentPage, int pageSize);

	/**
	 * 邮件入库
	 */
	public void insertMail(Mail mail);

	/**
	 * 邮件附件入库
	 */
	public void insertMailAccessory(List<MailAccessory> maList);

	/**
	 * 邮件物品入库
	 */
	public void insertMailGoods(List<MailGoods> mgList);

	/**
	 * 发送邮件
	 */
	public Status sendMail(Mail mail) throws ServiceException;

	public void sendMail(String toRoleId, String title, String content, String sendRole, int sendSource,
			int goldMoney,int silverMoney, List<GoodsOperateBean> goodsList);
	
	public void sendMail(String toRoleId, String title, String content,
			String sendRole, int sendSource, List<GoodsOperateBean> goodsList);
	
	public void sendMail(String toRoleId, String title, String content, String sendRole, 
			int sendSource, List<GoodsOperateBean> goodsList,MailAttriBean attriBean);
	
	/** 异步发邮件 */
	public void sendMailAsync(Mail mail);
	
	/** 异步发邮件 */
	public void sendMailAsync(String toRoleId, String title, String content, String sendRole, int sendSource,
			int goldMoney, int silverMoney, int exp, List<GoodsOperateBean> goodsList);
	
	/** 异步发邮件 */
	public void sendMailAsync(String toRoleId,String title,String content,
			String sendRole, int sendSource, List<GoodsOperateBean> goodsList,
			List<RoleGoods> goodsInstanceList);
	
	public void sendMailAsync(String toRoleId,String title,String content,
			String sendRole, int sendSource, List<GoodsOperateBean> goodsList);
	
	/** 异步发邮件 */
	public void sendMailAsync(String toRoleId, String title, String content, String sendRole, 
			int sendSource, List<GoodsOperateBean> goodsList, MailAttriBean attriBean);
	
	/**
	 * 提取邮件附件
	 */
	public Result pickMailAccessory(RoleInstance role, String mailId, boolean isPayPick) throws ServiceException;

	/**
	 * 删除邮件
	 */
	public Status delMail(RoleInstance role, String mailId) throws ServiceException;

	/**
	 * 批量提取附件
	 */
	public Result pickMoreMailAccessory(RoleInstance role, String[] mailIds);

	/**
	 * 批量删除邮件
	 */
	public Status delMoreMail(RoleInstance role, String[] mailIds);

	/**
	 * 定时任务删除过期邮件
	 */
	public void clearOutTimeMail();

	/**
	 * 得到数据库附件
	 */
	public List<MailAccessory> getMailAccessoryListByDB(String roleId, String mailId);

	/**
	 * 邮件对像
	 */
	public Mail getMailInfoByDB(String mailId);

	/**
	 * 存在未读取的邮件
	 */
	public boolean isExistUnreadMail(String roleId);
	
	/**
	 * 获得邮件显示内容
	 */
	public String getDisplayContext(Mail mail,RoleInstance role) ;
	
	
	public void addMailDisplayConextLogic(MailDisplayContextLogic logic) ;
	
	/**
	 * 二次确认提取收费邮件
	 */
	public Result pickTrueMailAccessory(RoleInstance role, String mailId) throws ServiceException;
	
	/**
	 * 获取角色的未读邮件数量
	 */
	public int getUnreadMailNumber(RoleInstance role);
	
	/**
	 * 修改邮件冻结状态
	 */
	public Result modifyMailFreeze(String roleId, String mailId, MailFreezeType freezeType);
	
	/**
	 * 查询角色的所有邮件（GM工具）
	 */
	public List<Mail> selectMailListForDebug(String roleId);
	
}