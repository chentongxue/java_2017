package sacred.alliance.magic.app.charge;

import java.util.Date;
import java.util.List;

import com.game.draco.app.AppSupport;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.ChargeRecord;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.vo.RoleInstance;

public interface ChargeApp extends AppSupport{	
	
	/**
	 * 获取充值面板信息
	 * @param role
	 * @return
	 */
	public Message getChargeMoneyListRespMessage(RoleInstance role);
	
	/**
	 * 更新充值数据库表
	 * @param role
	 */
	public void updateUserGold(RoleInstance role);
	
	/**
	 * 得到时间段内的充值总金条数
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public int getPayGold(String userId,Date startDate,Date endDate);
	
	/**
	 * 角色上下线时打印充值情况日志
	 * @param upr
	 * @param isUp
	 */
	public void printRolePayLog(RolePayRecord upr, String type);
	
	/**
	 * 查询帐号的充值记录
	 * @param role 角色
	 * @param size 查询记录条数
	 * @return
	 */
	public List<ChargeRecord> getUserChargeRecordList(RoleInstance role, int size);
	
	public boolean isUseMoogameId(int channelId) ;
	
	public boolean isRecordShowGameMoney(int channelId);
	
	public boolean isPayOpen() ;
	
}
