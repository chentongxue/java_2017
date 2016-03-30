package sacred.alliance.magic.app.trading;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.vo.RoleInstance;

public interface TradingApp {
	public final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	
	public TradingMatch getTradingMatch (RoleInstance role) ;
	
	public void removeTradingMatch(long id) ;
	
	public void logout(RoleInstance role);
	
	
	/**
	 * 邀请交易
	 * @param role
	 * @param targetRoleId
	 * @return
	 */
	public Result invite(RoleInstance role,int targetRoleId);
	
	/**
	 * 答复交易
	 * @param role
	 * @param inviteId
	 * @param replyType
	 * @return
	 */
	public Result reply(RoleInstance role,int inviteId,byte replyType);
	
	/**
	 * 用户取消交易
	 * @param role
	 * @return
	 */
	public Result cancel(RoleInstance role) ;
	
	/**
	 * 用户上架物品(锁定交易)
	 * @param role
	 * @param money
	 * @param goods
	 * @return
	 */
	public Result lock(RoleInstance role,int money,String[] goods) ;
	
	/**
	 * 用户交易
	 * @param role
	 * @return
	 */
	public Result trading(RoleInstance role) ;
	
	
	
	
}
