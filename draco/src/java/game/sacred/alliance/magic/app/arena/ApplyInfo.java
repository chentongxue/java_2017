package sacred.alliance.magic.app.arena;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 
 * 报名信息
 *
 */
public @Data class ApplyInfo {
	private final static Logger logger = LoggerFactory.getLogger(ApplyInfo.class);
	private final static AtomicLong KEY_GEN = new AtomicLong(0);
	
	public ApplyInfo(){
		this.id = KEY_GEN.incrementAndGet();
	}
	
	private long id ;
	//报名时间
	private long createDate ;
	//报名类型
	private short activeId ;
	private String leaderId ;
	//报名者列表
	private Set<String> appliers = new HashSet<String>();
	//当前竞技场积分
	private int score ;
	//角色等级,1v1时先按照level排序,然后按照rate排序
	private int level ;
	//是否已经取消
	//取消报名的时候将cancel=true,而没有将从报名队列中删除,系统匹配时
	//根据此字段进行过滤
	private boolean cancel = false ;
	//是否已经匹配
	private ArenaMatch match = null ; 
	
	private int teamRoleNum ;
	//以下字段跨服使用
	private String containerId;
	private String mapInstanceId;
	private String mapId;
	private int x;
	private int y;

	/**
	 * 只有当对方全部放弃,当前队伍队长同意再次排队时此方法才会调用
	 */
	public void reset(){
		this.cancel = false ;
		this.match = null ;
		this.createDate = System.currentTimeMillis();
		
	}
	
	public void addApplyRole(String roleId){
		if(null == roleId){
			return ;
		}
		this.appliers.add(roleId);
	}
	
	public void removeApplyRole(String roleId){
		if(null == roleId){
			return ;
		}
		this.appliers.remove(roleId);
	}
	
	/**
	 * 对方已经全部取消发送消息给队长是否继续排队
	 */
	public void sendApplyKeepConfirm(ArenaMatch match) {
		// 二次确认消息
		if(match.getStatus() != ArenaMatchStatus.common){
			//已经发送过
			return ;
		}
		try {
			RoleInstance role = GameContext.getOnlineCenter()
					.getRoleInstanceByRoleId(this.leaderId);
			if(null != role){
				C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage();
				message.setAffirmCmdId(ArenaApp.ARENA_APPLY_KEEP_CONFIRM_CMD);
				message.setAffirmParam(ArenaApp.SELECTED);
				message.setCancelCmdId(ArenaApp.ARENA_APPLY_KEEP_CONFIRM_CMD);
				message.setCancelParam(ArenaApp.UN_SELECTED);
				message.setInfo(GameContext.getI18n().getText(TextId.ARENA_APPLY_KEEP_CONFIRM));
				message.setTime((byte)ArenaApp.DEFAULT_MATCH_CONFIRM_TIME);
				message.setTimeoutCmdId(ArenaApp.ARENA_APPLY_KEEP_CONFIRM_TIME_OVER_CMD);
				// 默认取消
				message.setTimeoutParam(ArenaApp.UN_SELECTED);
				role.getBehavior().sendMessage(message);
			}
		} catch (Exception ex) {
			logger.error("",ex);
		}
		match.setSendKeepRoleId(this.leaderId);
		match.setStatus(ArenaMatchStatus.sendkeep);
		GameContext.getArenaApp().addKeepTimeoutListener(match);
	}
	
	public boolean isMember(String roleId){
		if(null == roleId){
			return false ;
		}
		return this.appliers.contains(roleId);
	}
	
	public void destory(){
		this.appliers.clear();
		this.appliers = null ;
		this.match = null ;
	}
	
	public boolean removeApplyInfo(String roleId){
		ApplyInfo info = GameContext.getArenaApp().getApplyInfo(roleId);
		if(null == info){
			return true ;
		}
		if(info.getId() == this.getId()){
			GameContext.getArenaApp().removeApplyInfo(roleId);
			return true ;
		}
		return false ;
	}
	
	public void cancelAll(){
		if(null != this.appliers){
			for (String roleId : this.appliers) {
				this.removeApplyInfo(roleId);
			}
		}
		destory();
	}
	
	public boolean filterMatch(){
		if(this.cancel || GameContext.getArenaApp().getArena(this.activeId).filterMatch(this)){
			//已经取消
			//将报名信息移除
			for (String roleId : this.appliers) {
				this.removeApplyInfo(roleId);
			}
			return true ;
		}
		return false ;
	}
}
