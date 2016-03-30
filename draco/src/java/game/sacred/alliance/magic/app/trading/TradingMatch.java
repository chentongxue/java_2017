package sacred.alliance.magic.app.trading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0061_TradingCancelUserExecInternalMessage;
import com.game.draco.message.internal.C0062_TradingDoneUserExecInternalMessage;
import com.game.draco.message.internal.C0060_TradingLockUserExecInternalMessage;
import com.game.draco.message.item.StorageContainerItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C3905_TradingLockNotifyMessage;
import com.game.draco.message.push.C3908_TradingStateNotifyMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import lombok.Data;
import lombok.Getter;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public @Data class TradingMatch {
	private static AtomicLong ID_GEN = new AtomicLong(0);
	private static final int GOODS_MAX_SIZE = 10 ;
	
	@Getter private final long id ;
	private String roleA ;
	private String userA ;
	private String roleB ;
	private String userB ;
	private int moneyA ;
	private int moneyB ;
	private List<RoleGoods> goodsA ;
	private List<RoleGoods> goodsB ;
	private State matchState = State.initial ;
	private State stateA = State.initial ;
	private State stateB = State.initial ;
	
	private byte seq = -1 ;
	
	public TradingMatch(){
		this.id = ID_GEN.incrementAndGet();
	}
	
	public RoleGoods getRoleGoods(String roleId,String goodsId){
		List<RoleGoods> list = this.isRoleA(roleId)?goodsA:goodsB;
		if(Util.isEmpty(list)){
			return null ;
		}
		for(RoleGoods rg : list){
			if(rg.getId().equals(goodsId)){
				return rg ;
			}
		}
		return null ;
	}
	
	private void notifyState(String roleId){
		synchronized (this){
			seq++ ;
			boolean isA = this.isRoleA(roleId);
			State selfState = isA?stateA:stateB ;
			State targetState = isA?stateB:stateA ;
			C3908_TradingStateNotifyMessage msg = new C3908_TradingStateNotifyMessage();
			msg.setSeq(seq);
			msg.setStatus(matchState.getType());
			msg.setSelfStatus(selfState.getType());
			msg.setTargetStatus(targetState.getType());
			GameContext.getMessageCenter().send(null, isA?userA:userB, msg);
		}
	}
	
	private void notifyState(){
		synchronized (this) {
			this.notifyState(roleA);
			this.notifyState(roleB);
		}
	}
	
	private boolean isRoleA(RoleInstance role){
		return role.getRoleId().equals(this.roleA);
	}
	
	private boolean isRoleA(String roleId){
		return roleId.equals(this.roleA);
	}
	
	
	private boolean empty(int money,String[] goodsList){
		return 0>=money &&(null == goodsList || 0 == goodsList.length) ;
	}
	
	public Result lock(RoleInstance role, int money, String[] goodsList) {
		synchronized (this) {
			Result result = new Result();
			// 判断交易状态
			if (State.initial != this.matchState) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_MATCH_STATE_INITAIL));
				return result;
			}
			boolean isA = this.isRoleA(role);
			State targetState = isA?this.stateB:this.stateA ;
			if(State.lock == targetState){
				int targetMoney = isA?moneyB:moneyA;
				if(empty(money,goodsList) && 0>= targetMoney && Util.isEmpty(isA?goodsB:goodsA) ){
					//都为空关闭交易
					this.cancel(CancelReason.emptytrading, null);
					result.setInfo(GameContext.getI18n().getText(TextId.TRADING_MATCH_EMPTY_TRADING));
					return result ;
				}
			}
			// 发送消息到单用户单线程中执行
			C0060_TradingLockUserExecInternalMessage reqMsg = new C0060_TradingLockUserExecInternalMessage();
			reqMsg.setRole(role);
			reqMsg.setMatch(this);
			reqMsg.setMoney(money);
			reqMsg.setGoods(goodsList);
			GameContext.getUserSocketChannelEventPublisher().publish(
					role.getUserId(), reqMsg, TradingApp.emptyChannelSession);

			result.success();
			return result;
		}
	}
	
	private C3905_TradingLockNotifyMessage buildLockNotify(int money,List<RoleGoods> roleGoodsList){
		C3905_TradingLockNotifyMessage lockNotify = new C3905_TradingLockNotifyMessage();
		lockNotify.setMoneyNum(money);
		if(Util.isEmpty(roleGoodsList)){
			return lockNotify ;
		}
		List<StorageContainerItem> items = new ArrayList<StorageContainerItem>();
		byte index = 0 ;
		for(RoleGoods rg : roleGoodsList){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(rg.getGoodsId());
			if(null == gb){
				continue ;
			}
			StorageContainerItem item = new StorageContainerItem();
			item.setGoodsInstanceId(rg.getId());
			item.setCount((byte)rg.getCurrOverlapCount());
			item.setIndex(index++);
			item.setBaseItem(gb.getGoodsBaseInfo(rg));
			items.add(item);
		}
		lockNotify.setItems(items);
		return lockNotify ;
	}
	
	/**
	 * 执行物品上架(单用户单线程)
	 * @param role
	 * @param money
	 * @param goodsList
	 */
	public Result lockExec(RoleInstance role, int money, String[] goodsList) {
		Result result = new Result();
		if(null == role || !GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId())){
			//不在线
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_MATCH_LOGOUT));
			return result ;
		}
		List<RoleGoods> roleGoodsList = null;
		boolean isA = this.isRoleA(role);
		synchronized (this) {
			// 对当前用户的状态,交易状态进行判断
			if (this.matchState != State.initial) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_MATCH_INITAIL));
				return result ;
			}
			State selfState = isA?this.stateA:this.stateB ;
			if(State.lock == selfState){
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_REPEAT_ADD));
				return result ;
			}
			// 具体锁定逻辑
			if (money < 0) {
				result.setInfo(GameContext.getI18n().getText(TextId.Sys_Param_Error));
				return result;
			}
			if (null != goodsList && goodsList.length > GOODS_MAX_SIZE) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_REPEAT_ADD_MAX));
				return result;
			}
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, money);
			if(ar.isIgnore()){
				return ar;
			}
			if(!ar.isSuccess()){
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_MONEY_NOT_ENOUGH) +money + AttributeType.gameMoney.getName());
				return result;
			}
//			if (role.getSilverMoney() < money) {
//				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_MONEY_NOT_ENOUGH) +money + AttributeType.silverMoney.getName());
//				return result;
//			}
			// 判断是否有相关物品,并且物品不能为绑定状态
			if (null != goodsList) {
				roleGoodsList = new ArrayList<RoleGoods>();
				for (String goodsId : goodsList) {
					if (Util.isEmpty(goodsId)) {
						result.setInfo(GameContext.getI18n().getText(TextId.TRADING_GOODS_PARAM_ERROR));
						return result;
					}
					RoleGoods rg = role.getRoleBackpack()
							.getRoleGoodsByInstanceId(goodsId);
					if (null == rg) {
						result.setInfo(GameContext.getI18n().getText(TextId.TRADING_GOODS_NOT_EXISTS));
						return result;
					}
					if (RoleGoodsHelper.hadBind(rg)) {
						result.setInfo(GameContext.getI18n().getText(TextId.TRADING_GOODS_BIND));
						return result;
					}
					roleGoodsList.add(rg);
				}
			}
		}
		if(!Util.isEmpty(roleGoodsList)){
			// 删除物品(保留格子)
			Result goodsResult = GameContext.getUserGoodsApp().deleteForBagRemainPlace(role, roleGoodsList,
					roleGoodsList.size(), OutputConsumeType.trade_up);
			if(!goodsResult.isSuccess()){
				result.setInfo(goodsResult.getInfo());
				return result;
			}
		}
		
		if(money>0){
			// 删除钱
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.gameMoney, OperatorType.Decrease, money,
					OutputConsumeType.trade_up);
			role.getBehavior().notifyAttribute();
		}

		synchronized (this) {
			//给对方发送上架物品消息
			if (isA) {
				this.goodsA = roleGoodsList;
				this.moneyA = money;
				GameContext.getMessageCenter().send(null, this.userB, 
						this.buildLockNotify(money, roleGoodsList));
			} else {
				this.goodsB = roleGoodsList;
				this.moneyB = money;
				GameContext.getMessageCenter().send(null, this.userA, 
						this.buildLockNotify(money, roleGoodsList));
			}
			result.success();
			// 判断交易状态是否取消
			if (State.cancel == this.matchState) {
				return result;
			}
			// 设置自己的交易状态
			State targetState = null ;
			if (isA) {
				this.stateA = State.lock;
				targetState = this.stateB;
			} else {
				this.stateB = State.lock;
				targetState = this.stateA;
			}
			// 判断对方是否为锁定状态
			if (State.lock != targetState) {
				//给自己同步状态
				this.notifyState(isA?roleA:roleB);
				return result;
			}
			//判断对方的交易的物品数目是否>自己交易的数目,
			//如果是则需要将角色再次申请多余的预留格子
			TradingResult gridResult = this.canRemainGrid();
			if(!gridResult.isSuccess()){
				this.cancel(CancelReason.bagfull, gridResult.getWho());
				result.setInfo(gridResult.getInfo());
				return gridResult ;
			}
			// 对方也已经锁定则将交易设置为锁定
			this.matchState = State.lock;
			//同时通知双方
			this.notifyState();
		}
		return result;
	}
	
	private TradingResult canRemainGrid(RoleInstance role,boolean isA){
		TradingResult result = new TradingResult();
		result.setWho(role);
		if(null == role){
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_ROLE_LOGOUT_CANCEL));
			return result;
		}
		int selfSize = isA?this.getGoodsSize(this.goodsA):this.getGoodsSize(this.goodsB);
		int targetSize = isA?this.getGoodsSize(this.goodsB):this.getGoodsSize(this.goodsA);
		if(targetSize > selfSize){
			//申请多余的预留格子
			GoodsResult deleteResult = GameContext.getUserGoodsApp().deleteForBagRemainPlace(role, 
					null, targetSize-selfSize, OutputConsumeType.trade_given);
			if(!deleteResult.isSuccess()){
				//申请失败
				result.setInfo(role.getRoleName() + CancelReason.bagfull.getTips());
				return result;
			}
		}
		result.success();
		return result ;
	}
	
	/**
	 * 在双方都锁定的的时候判断预留格子是否足够
	 * @return
	 */
	private TradingResult canRemainGrid(){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(this.roleA);
		TradingResult result = this.canRemainGrid(role, true);
		if(!result.isSuccess()){
			return result ;
		}
		result.failure();
		role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(this.roleB);
		result = this.canRemainGrid(role, false);
		if(!result.isSuccess()){
			return result ;
		}
		result.success();
		return result ;
	}
	
	private int getGoodsSize(List<RoleGoods> goods){
		if(Util.isEmpty(goods)){
			return 0 ;
		}
		return goods.size() ;
	}
	
	/**
	 * 单用户单线程执行取消操作
	 * @param currentRoleId
	 * @param isSystem
	 * @param canceler
	 * @return
	 */
	public Result cancelExec(String currentRoleId,CancelReason reason,RoleInstance canceler){
		Result result = new Result();
		//将个人物品回滚
		boolean isA = this.isRoleA(currentRoleId);
		int money = isA?this.moneyA:this.moneyB;
		List<RoleGoods> goods = isA?this.goodsA:this.goodsB;
		RoleInstance currentRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(currentRoleId);
		if(null == currentRole){
			this.done(currentRoleId, money, goods,OutputConsumeType.trade_down);
		}else{
			this.done(currentRole, money, goods, OutputConsumeType.trade_down);
			//给roleId发送-3通知交易成功
			//TipNotifyMessage tips = new TipNotifyMessage("交易取消");
			//GameContext.getMessageCenter().send(null, isA?this.userA:this.userB, tips);
		}
		//清除交易标识
		this.cleanTradingFlag(currentRole);
		result.success();
		return result ;
	}
	
	private void done(String roleId,int money,
			List<RoleGoods> goods,OutputConsumeType ocType){
		//直接入库操作
		if(!Util.isEmpty(goods)){
			try {
				for(RoleGoods rg : goods){
					rg.setRoleId(roleId);
				}
				GameContext.getUserGoodsApp().insertDbRoleGoods(goods);
			} catch (Exception e) {
				//TODO:打日志
			}
		}
		if(money>0){
			int value = GameContext.getRoleDAO().changeSilverMoney(roleId, money);
			if(value <=0){
				//TODO:打日志
			}
		}
	}
	
	private void done(RoleInstance currentRole,int money,
			List<RoleGoods> goods,OutputConsumeType ocType){
		//在线直接操作内存
		//!!! goods为空也得调用
		//!!!!!!!!!!!!!
		GoodsResult result = GameContext.getUserGoodsApp().addGoodsForBagPlusRemain(currentRole, goods, ocType);
		if(!result.isSuccess()){
				//TODO:添加失败的发送邮件
		}
		if(money>0){
			GameContext.getUserAttributeApp().changeRoleMoney(currentRole, AttributeType.gameMoney, 
					OperatorType.Add, money, ocType);
			currentRole.getBehavior().notifyAttribute();
		}
	}
	
	/**
	 * 取消交易
	 * @param role
	 */
	public Result cancel(CancelReason reason,RoleInstance canceler){
		synchronized (this) {
			// 取消
			Result result = new Result();
			if (State.cancel == this.getMatchState()) {
				// 已经取消无需在操作
				result.success();
				return result;
			}
			if (State.trading == this.getMatchState()) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_ING_NOT_CANCEL));
				return result;
			}
			if (State.complete == this.getMatchState()) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_OVER));
				return result;
			}

			// 可以取消
			this.setMatchState(State.cancel);
			// 将交易对象从cache中删除
			GameContext.getTradingApp().removeTradingMatch(this.getId());
			// 交易回滚
			C0061_TradingCancelUserExecInternalMessage rollbackA = new C0061_TradingCancelUserExecInternalMessage();
			rollbackA.setReason(reason);
			rollbackA.setMatch(this);
			rollbackA.setRollbackRoleId(this.getRoleA());
			rollbackA.setCanceler(canceler);

			// 发送消息到双方消息队列进行取消操作
			GameContext.getUserSocketChannelEventPublisher().publish(
					this.getUserA(), rollbackA, TradingApp.emptyChannelSession);

			C0061_TradingCancelUserExecInternalMessage rollbackB = new C0061_TradingCancelUserExecInternalMessage();
			rollbackA.setReason(reason);
			rollbackB.setMatch(this);
			rollbackB.setRollbackRoleId(this.getRoleB());
			rollbackA.setCanceler(canceler);

			GameContext.getUserSocketChannelEventPublisher().publish(
					this.getUserB(), rollbackB, TradingApp.emptyChannelSession);

			// 发送消息通知双方交易取消
			this.notifyState();
			// tips提示
			this.cancelNotifyTips(reason, canceler);
			result.success();
			return result;
		}
	}
	
	private void cancelNotifyTips(CancelReason reason,RoleInstance canceler){
		if(null == canceler){
			Message tips = null ;
			if(CancelReason.timeout == reason){
				//超时用-3,客户端展现过快
				tips = new C0002_ErrorRespMessage((short)0,reason.getTips());
			}else{
				tips = new C0003_TipNotifyMessage(reason.getTips());
			}
			GameContext.getMessageCenter().send(null, this.userA, tips);
			GameContext.getMessageCenter().send(null, this.userB, tips);
			return ;
		}
		// 非系统取消只需要通知非取消方即可
		C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage(canceler.getRoleName() + reason.getTips());
		boolean isA = this.isRoleA(canceler);
		GameContext.getMessageCenter().send(null, isA?this.userB:this.userA, tips);
	}
	
	
	/**
	 * 交易执行(单用户单线程)
	 * @param role
	 * @return
	 */
	public Result tradingExec(String roleId){
		Result result = new Result();
		boolean isA = this.isRoleA(roleId);
		int money = isA?this.moneyB:this.moneyA ;
		List<RoleGoods> goods = isA?this.goodsB:this.goodsA ;
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == role){
			//入库
			this.done(roleId, money, goods, OutputConsumeType.trade_output);
		}else{
			this.done(role, money, goods, OutputConsumeType.trade_output);
		}
		//将role交易标识清除
		this.cleanTradingFlag(role);
		result.success();
		
		String targetRoleId = roleA;
		if(isA){
			targetRoleId = roleB;
		}
		GameContext.getStatLogApp().tradingLog(id, roleId, targetRoleId, goods, money, OutputConsumeType.trade_output);
		return result ;
	}
	
	private void cleanTradingFlag(RoleInstance role){
		if(null == role){
			return ;
		}
		role.setTradingId(0);
	}
	
	private TradingResult canPut(RoleInstance role,boolean isA){
		TradingResult result = new TradingResult();
		result.setWho(role);
		if(null == role){
			result.setInfo(GameContext.getI18n().getText(TextId.TRADING_ROLE_LOGOUT_CANCEL));
			return result;
		}
		boolean canPutA = GameContext.getUserGoodsApp().canPutGoodsPlusRemain(
				role,isA?this.goodsB:this.goodsA);
		if(!canPutA){
			result.setInfo(role.getRoleName() + GameContext.getI18n().getText(TextId.TRADING_BAG_FULL));
			return result;
		}
		result.success();
		return result ;
	}
	
	/**
	 * 在交易的时刻判断是否能够放入对方物品
	 * @return
	 */
	private TradingResult canPut(){
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(this.roleA);
		TradingResult result = this.canPut(role, true);
		if(!result.isSuccess()){
			return result ;
		}
		role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(this.roleB);
		result = this.canPut(role, false);
		if(!result.isSuccess()){
			return result ;
		}
		result.success();
		return result ;
	}
	
	/**
	 * 请求交易物品
	 * @param role
	 */
	public Result trading(RoleInstance role){
		synchronized (this) {
			Result result = new Result();
			if (null == role) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_CUR_ROLE_LOGOUT));
				return result;
			}
			// 判断交易状态
			if (State.cancel == this.matchState) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_CANCEL_NOT_OPER));
				return result;
			}
			if (State.trading == this.matchState) {
				result.success();
				return result;
			}
			if (State.lock != this.matchState) {
				result.setInfo(GameContext.getI18n().getText(TextId.TRADING_NOT_LOCK_NOT_OPER));
				return result;
			}

			boolean isA = this.isRoleA(role);
			//判断双方背包是否已满
			TradingResult tradingResult = this.canPut();
			if(!tradingResult.isSuccess()){
				this.cancel(CancelReason.bagfull, tradingResult.getWho());
				result.setInfo(tradingResult.getInfo());
				return result ;
			}
			result.failure();
			
			// 将自己状态设置为交易
			if(isA){
				this.stateA = State.trading ;
			}else{
				this.stateB = State.trading ;
			}
			// 判断对方是否已经为交易状态
			State targetState = isA ? this.stateB : this.stateA;
			if (State.trading != targetState) {
				result.success();
				this.notifyState();
				return result;
			}
			//判断对方是否背包已满
			
			// 将交易设置为交易状态
			this.matchState = State.trading;
			// 将交易从cache中删除
			GameContext.getTradingApp().removeTradingMatch(this.id);

			// 发消息到用户线程执行交换物品操作
			C0062_TradingDoneUserExecInternalMessage msgA = new C0062_TradingDoneUserExecInternalMessage();
			msgA.setMatch(this);
			msgA.setRoleId(roleA);
			GameContext.getUserSocketChannelEventPublisher().publish(
					this.getUserA(), msgA, TradingApp.emptyChannelSession);

			C0062_TradingDoneUserExecInternalMessage msgB = new C0062_TradingDoneUserExecInternalMessage();
			msgB.setMatch(this);
			msgB.setRoleId(roleB);
			GameContext.getUserSocketChannelEventPublisher().publish(
					this.getUserB(), msgB, TradingApp.emptyChannelSession);
			// 通知双方状态
			this.notifyState();
			// 通知完成
			this.tradingCompleteNotifyTips();
			result.success();
			return result;
		}
	}
	
	private void tradingCompleteNotifyTips(){
		C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage(GameContext.getI18n().getText(TextId.TRADING_SUCCESS));
		GameContext.getMessageCenter().send(null, this.userA, tips);
		GameContext.getMessageCenter().send(null, this.userB, tips);
	}

	
}
