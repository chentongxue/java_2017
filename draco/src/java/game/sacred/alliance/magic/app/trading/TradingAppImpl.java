package sacred.alliance.magic.app.trading;

import java.text.MessageFormat;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0063_TradingTimeoutInternalMessage;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C3901_TradingInviteNotifyMessage;
import com.game.draco.message.push.C3903_TradingStartNotifyMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.module.cache.CacheEvent;
import sacred.alliance.magic.module.cache.CacheListener;
import sacred.alliance.magic.module.cache.SimpleCache;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class TradingAppImpl implements TradingApp,Service{
	private Cache<Long,TradingMatch> tradingCache = null ;
	//交易超时时间
	private int timeoutMillisecond = 3*60*1000 ; //3分钟
	private int tradingDistance = 800 ;//交易最远距离

	public void setTimeoutMillisecond(int timeoutMillisecond) {
		this.timeoutMillisecond = timeoutMillisecond;
	}

	public void setTradingDistance(int tradingDistance) {
		this.tradingDistance = tradingDistance;
	}


	private TradingMatch createTradingMatch(RoleInstance roleA, RoleInstance roleB) {
		TradingMatch match = new TradingMatch();
		match.setRoleA(roleA.getRoleId());
		match.setUserA(roleA.getUserId());
		match.setRoleB(roleB.getRoleId());
		match.setUserB(roleB.getUserId());
		//在role上设置交易id
		roleA.setTradingId(match.getId());
		roleB.setTradingId(match.getId());
		//放入过期cache
		tradingCache.put(match.getId(), match);
		return match;
	}

	@Override
	public TradingMatch getTradingMatch(RoleInstance role) {
		if(null == role || 0 == role.getTradingId()){
			return null ;
		}
		return tradingCache.getQuiet(role.getTradingId());
	}

	@Override
	public void removeTradingMatch(long id) {
		tradingCache.removeQuiet(id);
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		tradingCache = new SimpleCache<Long,TradingMatch>();
		tradingCache.setTimeToLiveMillisecond(timeoutMillisecond);
		tradingCache.addCacheListener(new CacheListener<Long,TradingMatch>(){
			@Override
			public void entryAccessed(CacheEvent<Long, TradingMatch> arg0) {
			}

			@Override
			public void entryAdded(CacheEvent<Long, TradingMatch> arg0) {
			}

			@Override
			public void entryCleared(CacheEvent<Long, TradingMatch> arg0) {
			}

			@Override
			public void entryExpired(CacheEvent<Long, TradingMatch> arg0) {
			}

			@Override
			public void entryRemoved(CacheEvent<Long, TradingMatch> event) {
				TradingMatch match = event.getValue();
				if(null == match){
					return ;
				}
				C0063_TradingTimeoutInternalMessage reqMsg = new C0063_TradingTimeoutInternalMessage();
				reqMsg.setMatch(match);
				//发送到交易但线程处理
				GameContext.getUserSocketChannelEventPublisher().publish(null, 
						reqMsg, TradingApp.emptyChannelSession);
			}

			@Override
			public void entryUpdated(CacheEvent<Long, TradingMatch> arg0) {
			}
			
		});
		tradingCache.start();
	}


	
	@Override
	public void stop() {
		
	}

	@Override
	public void logout(RoleInstance role) {
		TradingMatch match = this.getTradingMatch(role);
		if(null == match){
			return ;
		}
		match.cancel(CancelReason.logout, role);
	}
	
	private boolean inTradingDistance(RoleInstance role,RoleInstance targetRole){
		MapInstance map1 = role.getMapInstance();
		if(null == map1){
			return false ;
		}
		MapInstance map2 = targetRole.getMapInstance();
		if(null == map2){
			return false ;
		}
		if(!map1.getInstanceId().equals(map2.getInstanceId())){
			return false ;
		}
		return (Math.abs(role.getMapX() - targetRole.getMapX()) 
				+ Math.abs(role.getMapY() - targetRole.getMapY())) 
			< tradingDistance ;
	}
	

	private Result condition(RoleInstance role,RoleInstance targetRole){
		Result result = new Result();
		if(null == role){
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_CAN_NOT_LOGOUT));
			return result ;
		}
		if(null == targetRole){
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_TARGET_LOGOUT));
			return result ;
		}
		if(role.getIntRoleId() == targetRole.getIntRoleId()){
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_CAN_NOT_SELF));
			return result ;
		}
		if(0 != role.getTradingId()){
			//已经在交易中
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_ING));
			return result ;
		}
		
		if(0 != targetRole.getTradingId()){
			//已经在交易中
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_TARGET_ING));
			return result ;
		}
		//判断两者距离
		if(!this.inTradingDistance(role, targetRole)){
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_TARGET_TOO_LONG));
			return result ;
		}
		//判断双方等级
		int openLevel = GameContext.getParasConfig().getTradingOpenRoleLevel();
		if(role.getLevel() < openLevel || targetRole.getLevel() < openLevel){
			result.setInfo(GameContext.getI18n().messageFormat(TextId.TRADING_ROLE_LEVEL_CANOT_TIPS,openLevel));
			return result ;
		}
		result.success();
		return result ;
	}
	
	@Override
	public Result invite(RoleInstance role, int targetRoleId) {
		//判断是否游戏开启交易系统
		if(!GameContext.getPublicSetApp().isTrade()){
			Result result = new Result();
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_SYSTEM_CLOSE));
			return result ;
		}
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(targetRoleId));
		//判断目标是否允许交易
		if(null != targetRole && targetRole.getSystemSet().isTradeShield()){
			Result result = new Result();
			result.setInfo(targetRole.getRoleName() + GameContext.getI18n().getText(TextId.TRADING_SETTING_CLOSE));
			return result ;
		}
		if(null != targetRole && 
				GameContext.getSocialApp().isShieldByTarget(
						role.getRoleId(), String.valueOf(targetRoleId))){
			//自己在对方黑名单中
			Result result = new Result();
			result.setInfo(GameContext.getI18n().messageFormat(TextId.TRADING_TARGET_SHEILD, targetRole.getRoleName()));
			return result ;
		}
		Result result = this.condition(role, targetRole);
		if(!result.isSuccess()){
			return result ;
		}
		result.failure();
		//判断对方是否繁忙
		long currTime = System.currentTimeMillis();
		if(currTime - targetRole.getTradingApplyTime() 
				<= TimeoutConstant.Trading_Reply_Timeout){
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_TARGET_BUSY));
			return result ;
		}
		targetRole.setTradingApplyTime(currTime);
		//给对方发送交易邀请提醒
		C3901_TradingInviteNotifyMessage notifyMsg = new C3901_TradingInviteNotifyMessage();
		notifyMsg.setRoleId(role.getIntRoleId());
		notifyMsg.setRoleName(role.getRoleName());
		targetRole.getBehavior().sendMessage(notifyMsg);
		result.success();
		return result;
	}
	

	@Override
	public Result cancel(RoleInstance role) {
		TradingMatch match = this.getTradingMatch(role);
		if(null == match){
			Result result = new Result();
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_NOT_ING));
			return result ;
		}
		return match.cancel(CancelReason.rolecancel, role);
	}

	@Override
	public Result trading(RoleInstance role) {
		//交易
		TradingMatch match = this.getTradingMatch(role);
		if(null == match){
			Result result = new Result();
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_NOT_ING));
			return result ;
		}
		return match.trading(role);
	}

	@Override
	public Result lock(RoleInstance role, int money, String[] goods) {
		//上架
		TradingMatch match = this.getTradingMatch(role);
		if(null == match){
			Result result = new Result();
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_NOT_ING));
			return result ;
		}
		return match.lock(role, money, goods);
	}

	@Override
	public Result reply(RoleInstance role, int inviteId, byte replyType) {
		//判断是否游戏开启交易系统
		if(!GameContext.getPublicSetApp().isTrade()){
			Result result = new Result();
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_SYSTEM_CLOSE));
			return result ;
		}
		role.setTradingApplyTime(0);
		boolean cancel = (0 == replyType);
		if(cancel){
			//直接发送消息给对方告知拒绝交易
			C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
			notifyMsg.setMsgContext(role.getRoleName() + GameContext.getI18n().getText(TextId.TRADING_TARGET_REFUSE));
			GameContext.getMessageCenter().sendByRoleId(null, String.valueOf(inviteId), notifyMsg);
			return new Result().success();
		}
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(inviteId));
		//同意交易
		//判断条件
		Result result = this.condition(role, targetRole);
		if(!result.isSuccess()){
			return result ;
		}
		//创建交易对象
		TradingMatch match = this.createTradingMatch(role, targetRole);
		//给双方发送通知面板消息
		C3903_TradingStartNotifyMessage notifyA = new C3903_TradingStartNotifyMessage();
		notifyA.setRoleId(targetRole.getIntRoleId());
		notifyA.setRoleName(targetRole.getRoleName());
		
		C3903_TradingStartNotifyMessage notifyB = new C3903_TradingStartNotifyMessage();
		notifyB.setRoleId(role.getIntRoleId());
		notifyB.setRoleName(role.getRoleName());
		//发送
		role.getBehavior().sendMessage(notifyA);
		targetRole.getBehavior().sendMessage(notifyB);
		return result;
	}

	


}
