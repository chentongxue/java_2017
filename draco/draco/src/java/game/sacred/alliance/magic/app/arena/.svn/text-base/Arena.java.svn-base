package sacred.alliance.magic.app.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0053_ArenaCloseInternalMessage;
import com.game.draco.message.internal.C0052_ArenaMatchInternalMessage;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.response.C3851_ArenaApplyCancelRespMessage;
import com.game.draco.message.response.C3850_ArenaApplyRespMessage;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.arena.config.ArenaConfig;
import sacred.alliance.magic.channel.EmptyChannelSession;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public abstract class Arena implements Service{
	
	private final static Logger logger = LoggerFactory.getLogger(Arena.class);
	private final static ChannelSession emptyChannelSession = new EmptyChannelSession();
	private final static int DEFAULT_MATCH_INTERVAL = 30 ;
	protected final static float DEFAULT_WEEK_SUCCESS_RATE = 50.00f ;
	protected Comparator<ApplyInfo> comparator ;
	protected Active active ;
	protected ArenaConfig config ;
	protected ArenaApp manager ;
	//报名者列表(等待匹配列表)
	protected List<ApplyInfo> applyList = new ArrayList<ApplyInfo>();
	private Thread matchThread = null ;
	private boolean matchRunning = false ;
	
	private boolean preActiveOpen = false ;
	
	public void activeOpenWhenServerStart(){
		this.preActiveOpen = true ;
	}
	
	protected abstract ArenaResult applyCheck(RoleInstance role) ;
	
	protected abstract ApplyInfo createApplyInfo(RoleInstance role,Object context) ;
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	protected ArenaResult applyCancelCheck(RoleInstance role) {
		//查看是否已经报名
		ArenaResult result = new ArenaResult();
		ApplyInfo applyInfo = manager.getApplyInfo(role.getRoleId());
		if(null == applyInfo || applyInfo.getActiveId() != this.active.getId()){
			result.setInfo(this.getText(TextId.ARENA_NOT_APPLY_NOT_CANCEL));
			return result ;
		}
		ArenaMatch match = applyInfo.getMatch();
		if(null != match){
			//!!!!!
			//未知原因报名信息一直都存在也无法取消,故有下面代码
			if(match.isTimeout()){
				//将直接的直接删除吧
				applyInfo.setCancel(true);
				manager.removeApplyInfo(role.getRoleId());
				result.setInfo(this.getText(TextId.ARENA_MATCH_TIME_OUT));
				result.setCancelByMatchTimeout(true);
				return result;
			}
			//设置为已报名状态
			result.setCurrentApplyState(ApplyState.had_apply);
			result.setInfo(this.getText(TextId.ARENA_MATCH_NOT_CANCEL));
			return result ;
		}
		result.success();
		return result;
	}
	
	protected abstract ArenaResult matchYes(String roleId,ApplyInfo info) ;
	
	protected abstract ArenaResult matchNo(String roleId,ApplyInfo info) ;
	
	private void notifyCancelSuccess(String roleId){
		C3851_ArenaApplyCancelRespMessage notifyMsg = new C3851_ArenaApplyCancelRespMessage();
		notifyMsg.setStatus(RespTypeStatus.SUCCESS);
		notifyMsg.setApplyState(ApplyState.not_apply.getType());
		GameContext.getMessageCenter().sendByRoleId("", roleId, notifyMsg);
	}
	
	private void notifyApplySuccess(String roleId){
		C3850_ArenaApplyRespMessage notifyMsg = new C3850_ArenaApplyRespMessage();
		notifyMsg.setStatus(RespTypeStatus.SUCCESS);
		notifyMsg.setApplyState(ApplyState.had_apply.getType());
		GameContext.getMessageCenter().sendByRoleId("", roleId, notifyMsg);
	}
	
	public ArenaResult matchConfirm(String roleId,boolean select){
		ArenaResult result = new ArenaResult();
		ApplyInfo info = GameContext.getArenaApp().getApplyInfo(roleId);
		if(null == info){
			result.setInfo(this.getText(TextId.ARENA_NOT_APPLY));
			return result ;
		}
		ArenaMatch match = info.getMatch();
		if(null == match){
			result.setInfo(this.getText(TextId.ARENA_NOT_MATCH));
			return result ;
		}
		if(select){
			//确定
			return this.matchYes(roleId,info);
		}
		result =  this.matchNo(roleId,info);
		//通知取消者刷新面板
		if(result.isSuccess()){
			this.notifyCancelSuccess(roleId);
		}
		return result ;
	}
	
	public ArenaResult applyKeep(String roleId,boolean select){
		ArenaResult result = new ArenaResult();
		ApplyInfo info = GameContext.getArenaApp().getApplyInfo(roleId);
		ArenaMatch match = info.getMatch();
		if(null == match){
			result.setInfo(this.getText(TextId.ARENA_NOT_MATCH_NOT_OPER));
			return result ;
		}
		ApplyInfo otherTeam = match.getOtherTeam(info);
		if(!match.teamAllCancel(otherTeam)){
			result.setInfo(this.getText(TextId.ARENA_TARGET_NOT_CANCEL_NOT_OPER));
			return result ;
		}
		if(select){
			if(match.teamAllCancel(info)){
				//自己也取消了
				match.cancelAll();
				result.setInfo(this.getText(TextId.ARENA_ALL_CANCEL_NOT_OPER));
				return result ;
			}
			otherTeam.destory();
			match.destory();
			info.reset();
			//重新放入排队队列
			this.applyList.add(info);
			
			//通知保持者刷新面板
			this.notifyApplySuccess(roleId);
			
			result.success();
			return result ;
		}
		//取消
		info.getMatch().cancelAll();
		//通知取消者成功，让其刷新面板
		this.notifyCancelSuccess(roleId);
		result.success();
		return result ;
	}
	
	public void systemClose(){
		for(ApplyInfo info : this.applyList){
			try {
				if (null == info) {
					continue;
				}
				if(null != info.getAppliers()){
					for (String roleId : info.getAppliers()) {
						GameContext.getArenaApp().removeApplyInfo(roleId);
					}
				}
				info.destory();
			}catch(Exception ex){
			}
		}
		this.applyList.clear();
	}
	
	public ArenaResult apply(RoleInstance role){
		//判断活动是否开启
		ArenaResult result = new ArenaResult();
		if(!active.isTimeOpen()){
			result.setInfo(this.getText(TextId.ARENA_ACTIVE_NOT_OPEN));
			return result ;
		}
		result = this.applyCheck(role);
		if(!result.isSuccess()){
			return result ;
		}
		//将角色设置为自动报名
		this.onAutoApply(role);
		
		//构建报名者信息
		ApplyInfo info = this.createApplyInfo(role,result.getRoleList());
		//放入等待匹配队列
		this.applyList.add(info);
		//加入到所有报名集合中(一用户同一时刻只能参加一种擂台赛)
		for(String roleId : info.getAppliers()){
			manager.getAllRoleApplyInfo().put(roleId, info);
		}
		//可以将result中的roleList释放
		result.releaseRoleList();
		//当前已经报名状态
		result.setCurrentApplyState(ApplyState.had_apply);
		return result ;
	}
	
	public  ArenaResult applyCancel(RoleInstance role) {
		//判断活动是否开启
		ArenaResult result = new ArenaResult();
		if(!active.isTimeOpen()){
			result.setInfo(this.getText(TextId.ARENA_ACTIVE_CLOSE_NOT_CANCEL));
			return result ;
		}
		result = this.applyCancelCheck(role);
		if(result.isCancelByMatchTimeout()){
			//!!!!!
			//未知原因报名信息一直都存在也无法取消,故有下面代码
			result.success();
			return result ;
		}
 		if(!result.isSuccess()){
			return result ;
		}
 		//取消自动报名
 		this.offAutoApply(role);
 		
		ApplyInfo info = manager.getApplyInfo(role.getRoleId());
		//标注取消
		info.setCancel(true);
		//删除总报名信息
		for(String roleId : info.getAppliers()){
			manager.getAllRoleApplyInfo().remove(roleId);
		}
		//设置为未报名状态
		result.setCurrentApplyState(ApplyState.not_apply);
		return result ;
	}
	
	protected void onAutoApply(RoleInstance role){
		
	}
	
	protected void offAutoApply(RoleInstance role){
		
	}
	
	public void systemMatch(){
		if(0 == this.applyList.size() ){
			return ;
		}
		List<ApplyInfo> toDoList = new ArrayList<ApplyInfo>();
		toDoList.addAll(this.applyList);
		this.applyList.clear();
		//一个报名的时候,需要及时通知因为条件不符合而推出报名队列
		MatchResult matchResult = this.matchRule(toDoList);
		if(null == matchResult){
			return ;
		}
		//将未匹配成功的继续放入匹配队列
		if(null != matchResult.getRemain()){
			this.applyList.addAll(matchResult.getRemain());
		}
		if(null != matchResult.getSuccess()){
			for(ArenaMatch match : matchResult.getSuccess()){
				try{
					//发送匹配成功确认面板
					match.sendMatchConfirm();
				}catch(Exception ex){
					logger.error("",ex);
				}
			}
		}
		matchResult.destroy();
		matchResult = null ;
	}
	
	
	protected void sendApplyKeepConfirm(RoleInstance role){
		if(null == role){
			return ;
		}
		C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage();
		message.setAffirmCmdId(ArenaApp.ARENA_APPLY_KEEP_CONFIRM_CMD);
		message.setAffirmParam(ArenaApp.SELECTED);
		message.setCancelCmdId(ArenaApp.ARENA_APPLY_KEEP_CONFIRM_CMD);
		message.setCancelParam(ArenaApp.UN_SELECTED);
		message.setInfo(this.getText(TextId.ARENA_TARGET_CANCEL_KEEP_APPLY));
		message.setTime((byte)ArenaApp.DEFAULT_MATCH_CONFIRM_TIME);
		message.setTimeoutCmdId(ArenaAppImpl.ARENA_APPLY_KEEP_CONFIRM_CMD);
		//默认取消
		message.setTimeoutParam(ArenaAppImpl.UN_SELECTED);
		role.getBehavior().sendMessage(message);
	}
	
	
	
	
	/**
	 * 判断两队是否在同一等级区间
	 * @param a1
	 * @param a2
	 * @return
	 */
	protected boolean inLevelRange(ApplyInfo a1, ApplyInfo a2) {
		return config.getLevelRangeIndex(a1.getLevel()) == 
			config.getLevelRangeIndex(a2.getLevel()) ;
	}
	
	
	protected MatchResult matchRule(List<ApplyInfo> toDoList){
		if(Util.isEmpty(toDoList)){
			return null ;
		}
		//对等级,周胜率进行降序排列
		Collections.sort(toDoList,this.comparator);
		MatchResult result = new MatchResult();
		int toDoSize = toDoList.size();
		for(int index=0;index<toDoSize;){
			int step = 1 ;
			int nextIndex = 0 ;
			ApplyInfo current = toDoList.get(index);
			if(current.filterMatch()){
				index++;
				continue ;
			}
			while(true){
				nextIndex = index + step ;
				if(nextIndex>=toDoSize){
					result.addRemain(current);
					return result ;
				}
				ApplyInfo next = toDoList.get(nextIndex);
				if(next.filterMatch()){
					step ++ ;
					continue ;
				}
				if(!this.inLevelRange(current, next)){
					result.addRemain(current);
					index = nextIndex ;
					break ;
				}
				//匹配成功
				ArenaMatch match = ArenaMatch.create(current, next, config);
				if(null != match){
					result.addSuccess(match);
				}else{
					current.cancelAll();
					next.cancelAll();
				}
				index = nextIndex + 1 ;
				break ;
			}
		}
		return result;
	
	}
	
	private int getMatchInterval(){
		return this.config.getMatchInterval()>0?this.config.getMatchInterval()*1000:DEFAULT_MATCH_INTERVAL*1000;
	}
	
	protected void sendSystemMatchReq(){
		C0052_ArenaMatchInternalMessage reqMsg = new C0052_ArenaMatchInternalMessage();
		reqMsg.setActiveId(config.getActiveId());
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
	}
	
	protected void sendSystemCloseReq(){
		C0053_ArenaCloseInternalMessage reqMsg = new C0053_ArenaCloseInternalMessage();
		reqMsg.setActiveId(config.getActiveId());
		GameContext.getUserSocketChannelEventPublisher().publish(null, reqMsg, emptyChannelSession);
	}
	
	protected Comparator<ApplyInfo> createComparator(){
		return new ApplyInfoComparator(this.config);
	}

	@Override
	public void start() {
		this.matchRunning = true ;
		//生成比较器
		this.comparator = this.createComparator();
		//启动系统匹配线程
		matchThread = new Thread(new Runnable(){
			@Override
			public void run() {
				while(matchRunning){
					try {
						Thread.sleep(getMatchInterval());
					} catch (Exception e) {
					}
					try {
						boolean openNow = active.isTimeOpen();
						if(!preActiveOpen && openNow){
							//活动开始
							activeStart();
						}
						if (openNow) {
							sendSystemMatchReq();
						} else {
							sendSystemCloseReq();
						}
						if(preActiveOpen && !openNow){
							activeStop();
						}
						if(preActiveOpen && openNow){
							activeIng();
						}
						//更新状态
						preActiveOpen = openNow ;
					}catch(Exception ex){
						logger.error("",ex);
					}
				}
			}
		});
		matchThread.setName("match thread for active:" + this.config.getActiveId());
		//matchThread.setDaemon(true);
		matchThread.start();
	}
	
	protected void activeStart() {
		
	}
	
	protected void activeStop() {
		
	}
	
	protected void activeIng() {
		
	}

	@Override
	public void stop() {
		matchRunning = false ;
	}
	
	@Override
	public void setArgs(Object paramObject) {
	}
	
	public boolean filterMatch(ApplyInfo info){
		for (Iterator<String> it = info.getAppliers().iterator();it.hasNext();) {
			String roleId = it.next();
			RoleInstance role = GameContext.getOnlineCenter()
					.getRoleInstanceByRoleId(roleId);
			if (null == role) {
				//已经下线
				return true ;
			}
		}
		return false ;
	}
	
	public abstract ArenaType getArenaType();
}
