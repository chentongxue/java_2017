package sacred.alliance.magic.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.vo.MailMoneyRank;

public class MailDAOImpl extends BaseDAOImpl{
	
	/**
	 * 根据邮件ID批量查询邮件
	 * @param mailIds
	 * @return
	 */
	public List<Mail> getBatchMail(String[] mailIds){
		List<Mail> mailList = new ArrayList<Mail>();
		if(null == mailIds || 0 == mailIds.length){
			return mailList;
		}
		Map map = new HashMap();
		map.put("mailIds", mailIds);
		mailList = this.getSqlMapClientTemplate().queryForList("Mail.getBatchMail", map);
		return mailList;
	}
	
	/**
	 * 查询角色的未读邮件数量
	 * @param roleId
	 * @return
	 */
	public int getUnreadMailNum(String roleId){
		Map map = new HashMap();
		map.put("roleId", roleId);
		return (Integer) this.getSqlMapClientTemplate().queryForObject("Mail.countUnreadMail", map);
	}
	
	/**
	 * 删除没有附件的邮件
	 * @param roleId 角色ID
	 * @param mailId 邮件ID
	 * @return
	 */
	public int deleteEmptyMail(String roleId, String mailId) {
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("mailId", mailId);
		return this.getSqlMapClientTemplate().delete("Mail.deleteEmptyMail", map);
	}
	
	/**
	 * 查询邮件游戏币总和排名
	 * @param sendSource 邮件来源 <=0表示不限
	 * @param totalMoney 游戏币总和大于等于此值
	 * @param start 记录开始位置
	 * @param size 记录条数
	 * @return
	 */
	public List<MailMoneyRank> getMoneyRankList(int sendSource, int totalMoney, int start, int size){
		if(size <=0){
			return null;
		}
		Map map = new HashMap();
		if(sendSource > 0){
			map.put("sendSource", sendSource);
		}
		map.put("totalMoney", totalMoney);
		map.put("start", start);
		map.put("size", size);
		return this.getSqlMapClientTemplate().queryForList("Mail.getMoneyRankList", map);
	}
	
	/**
	 * 修改邮件冻结状态
	 * @param roleId 角色ID
	 * @param mailId 邮件ID
	 * @param freeze 冻结状态
	 * @return
	 */
	public int modifyMailFreeze(String roleId, String mailId, int freeze){
		Map map = new HashMap();
		map.put("roleId", roleId);
		map.put("mailId", mailId);
		map.put("freeze", freeze);
		return this.getSqlMapClientTemplate().update("Mail.modifyMailFreeze", map);
	}
	
}
