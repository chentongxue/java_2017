package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.map.MapProperty;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.channel.mina.MinaChannelSession;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.ScreenConstant;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.ProbabilityMachine;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffLostType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.skill.vo.SkillContext;
import com.game.draco.app.union.FunType;
import com.game.draco.message.internal.C0050_RoleOfflineInternalMessage;
import com.game.draco.message.item.AttrFontItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
import com.game.draco.message.response.C0402_RoleAttributeFontListRespMessage;
import com.game.draco.message.response.C0403_RoleAttributeFontRespMessage;

public class RoleInstanceBehavior extends AbstractRoleBehavior<RoleInstance> {

	private ChannelSession channelSession;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private RoleInstance role;
	private LoopCount buffLoopCount = new LoopCount(
			LoopConstant.ROLE_BUFF_DEFAULT_CYCLE);
	private LoopCount titleTimeoutLoopCircle = new LoopCount(
			LoopConstant.TITLE_TIMEOUT_CYCLE);
	private LoopCount timingWriteDBLoopCircle = new LoopCount(
			LoopConstant.TIMING_WRITEDB_CYCLE);
	private LoopCount richManLoopCount = new LoopCount(
			LoopConstant.RICHMAN_CIRCLE_CYCLE);
	private LoopCount heroHpHealthCount = new LoopCount(LoopConstant.HERO_HP_HEALTH_CYCLE) ;
	private LoopCount hitComboLoopCount = new LoopCount(LoopConstant.HIT_COMBO_CYCLE) ;
	

	public RoleInstanceBehavior(RoleInstance t, ChannelSession channelSession) {
		super(t);
		this.role = t;
		this.channelSession = channelSession;
	}

	public void setChannelSession(ChannelSession channelSession) {
		this.channelSession = channelSession;
	}
	
	@Override
	public long getLastIoReadTime(){
		if(null == this.channelSession){
			return -1 ;
		}
		MinaChannelSession session = (MinaChannelSession)this.channelSession ;
		return session.getIoSession().getLastReadTime();
	}
	
	@Override
	public long getLastIoWriteTime(){
		if(null == this.channelSession){
			return -1 ;
		}
		MinaChannelSession session = (MinaChannelSession)this.channelSession ;
		return session.getIoSession().getLastWriteTime();
	}

	public void update() throws ServiceException {
		if (this.buffLoopCount.isReachCycle()) {
			MapInstance mapInstance = role.getMapInstance();
			if (null != mapInstance) {
				long timeDiff = mapInstance.getWorldTime().getTimeDiff();
				GameContext.getUserBuffApp().runBuff(role, timeDiff);
			}
		}
		if(this.hitComboLoopCount.isReachCycle()){
			//处理连击数
			GameContext.getHitComboApp().clearHitCombo(role);
		}
		
		if (role.getQuestLoopCount().isReachCycle()) {
			GameContext.getUserQuestApp().updateQuest(role);
		}

		if (this.titleTimeoutLoopCircle.isReachCycle()) {
			//称号
			GameContext.getTitleApp().timeout(role);
		}
		if (this.timingWriteDBLoopCircle.isReachCycle()) {
			GameContext.getOnlineCenter().timingWriteDBSendMsg(role);
		}
		if (this.richManLoopCount.isReachCycle()) {
			GameContext.getRichManApp().notifyRichManRoleStatOver(role);
		}
		if(this.heroHpHealthCount.isReachCycle()
				&& GameContext.getMapApp().canMapProperty(role, MapProperty.canHpHealth.getType())
				&& role.getCurHP()>0){
			GameContext.getHeroApp().hpHealth(role);
		}
	}

	@Override
	public void stopMove() {

	}

	@Override
	public void enterMap() throws ServiceException {
		GameContext.getUserMapApp().enter(role);
		// 任务相关
		GameContext.getUserQuestApp().enterMap(role);
		try {
			if (role.getTradingId() != 0) {
				// 取消交易
				GameContext.getTradingApp().cancel(role);
			}
		} catch (Exception ex) {
		}

		MapInstance instance = role.getMapInstance();
		if (null == instance) {
			return;
		}
		
		instance.enter(role);
		
		// 地图内广播进入地图信息
		for (RoleInstance other : instance.getRoleList()) {
			try {
				if (null == other) {
					continue;
				}
				if (other.getRoleId().equals(role.getRoleId())) {
					continue;
				}
				C0204_MapUserEntryNoticeRespMessage pushMsg = new C0204_MapUserEntryNoticeRespMessage();
				RoleBodyItem item = Converter.getRoleBodyItem(role, other);
				pushMsg.setItem(item);
				GameContext.getMessageCenter().send("", other.getUserId(),
						pushMsg);
			} catch (Exception ex) {
				logger.error("", ex);
			}
		}
		// 累加地图在线人数
		// instance.incrementRoleCount();
		role.setLineId(instance.getLineId());
	}

	@Override
	public void exitMap() throws ServiceException {
		context.getUserMapApp().exitMap(role);
	}

	@Override
	public void changeMap(Point targetPoint) throws ServiceException {
		MapInstance oldMapInstance = role.getMapInstance();
		// 切换地图
		ChangeMapResult type = context.getUserMapApp().changeMap(role, targetPoint);
		if (type.isSuccess()) {
			role.getHatredTarget().clearHatredMap(oldMapInstance);
		}
		// 下面代码有问题,此时角色的地图实例为null
		/*
		 * if(type==1){ role.getHatredTarget().clearHatredMap(); }
		 */
	}

	@Override
	public void notifyForceType() {
		// 势力不会修改,不需要操作
	}


	@Override
	public void notifyBattleAttrIncome(NpcInstance dieNpc) {
		try {
			if (null == role || null == dieNpc) {
				return;
			}
			NpcTemplate npcTemplate = dieNpc.getNpc() ;
			//经验
			int dieNpcExp = npcTemplate.getExp();
			if (dieNpcExp > 0) {
				GameContext.getCalculateExpApp().calculateExp(role, dieNpc);
			}
			boolean notify = false ;
			//dkp
			if(npcTemplate.getDkp()>0){
//				GameContext.getUserAttributeApp().changeRoleDkp(role, AttributeType.dkp, 
//						OperatorType.Add, npcTemplate.getDkp(),OutputConsumeType.monster_fall);
				GameContext.getUnionApp().changeMemberDkp(role, npcTemplate.getDkp(), OperatorType.Add, FunType.monsterFallDkp, true);
				notify = true ;
			}
			//游戏币
			int min = npcTemplate.getMinMoney();
			int max = npcTemplate.getMaxMoney();
			int money = 0;
			if(max >0 && min > 0 && max>=min){
				money = ProbabilityMachine.getRandomNum(min, max);
				GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.gameMoney, 
						OperatorType.Add, money, OutputConsumeType.monster_fall);
				notify = true ;
			}
			int potential = npcTemplate.getPotential() ;
			if(potential > 0){
				GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.potential, 
						OperatorType.Add, potential,OutputConsumeType.monster_fall);
				notify = true ;
			}
			if(notify){
				role.getBehavior().notifyAttribute() ;
			}
			//打印日志
			GameContext.getStatLogApp().roleKillMonsterLog(role, dieNpc, money);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}


	@Override
	public void notifyPosition(Message resp) throws ServiceException {
		// 位置变化，遍历地图所有玩家
		// 当被遍历玩家未关闭屏蔽，直接发送给他
		// 当被遍历玩家与本人组队，则直接发送
		// 当本人在被遍历玩家仇恨列表中，则直接发送
		// 当被遍历玩家设置屏蔽同阵营，而且被遍历玩家与本人非同阵营，则发送
		// 当被遍历玩家设置屏蔽敌对阵营，而且被遍历玩家与本人非敌对阵营，则发送
		// 其余不发送
		if (role == null) {
			return;
		}

		MapInstance mapInstance = this.role.getMapInstance();
		if (mapInstance == null) {
			return;
		}
        if(!mapInstance.isNormalLive(role)){
            //非正常状态不广播
            return ;
        }
		MapConfig mapConfig = mapInstance.getMap().getMapConfig();
		if(null != mapConfig && mapConfig.getLogictype() == MapLogicType.richman.getType()) {
			//如果是大富翁地图则不需要广播位置同步
			return ;
		}
		if(null == mapConfig || mapConfig.isPvpMap()) {
			//如果是PVP地图全图转发位置消息
			mapInstance.broadcastMap(role, resp, 2000);
			return;
		}
		int broadcastAllMax =  GameContext.getParasConfig().getBroadcastAllMax();
		if(mapConfig.getBroadcastAllMax() > 0) {
			broadcastAllMax = mapConfig.getBroadcastAllMax();
		}
		if(broadcastAllMax >= mapInstance.getRoleCount()) {
			//如果地图内的人数小于配置的人数，全图转发
			mapInstance.broadcastMap(role, resp, 2000);
			return;
		}
		//地图内人数超过配置的人数，同屏转发
		mapInstance.broadcastScreenMap(role, resp, 2000);
	}

	public RoleInstance getRole() {
		return role;
	}

	@Override
	public void notifyNpcMsg(String content) {

	}

	@Override
	public void autoLevelup() {
		// 判断当前等级是否符合自动升级条件
		/*
		 * if (role.getLevel() >= GameContext.getUserRoleApplication()
		 * .getHandUpgradeConfig().getPlayerGrade()) { return; }
		 */
		// 调用手动升级方法
		GameContext.getUserRoleApp().handUpLevel(role);
	}

	@Override
	public void death(AbstractRole attacker) {
		try {
			// 取消交易
			if (role.getTradingId() != 0) {
				GameContext.getTradingApp().cancel(role);
			}
			// 清仇恨列表
			role.getHatredTarget().clearHatredMap();
			// 清除buff
			role.delAllBuffStat(BuffLostType.dieLost);
		} catch (Exception ex) {
			logger.error("", ex);
		}
		// 调用当前地图实例的死亡方法
		role.getMapInstance().roleDeath(attacker, role);
	}

	@Override
	public void sendMessage(Message message,int awaitMillis) {
		if (null == message) {
			return;
		}
		if(awaitMillis <=0){
			this.channelSession.write(message);
			return ;
		}
		MinaChannelSession session = (MinaChannelSession)this.channelSession ;
		session.getIoSession().write(message).awaitUninterruptibly(awaitMillis);
	}
	

	@Override
	public void closeNetLink() {
		/*if (null == this.channelSession) {
			return;
		}
		this.channelSession.close();*/
		
		if(this.isChannelSessionEnable()){
			//连接关闭触发下线
			this.channelSession.close();
			return ;
		}
		//连接已经不可用
		//保证单用户单线程允许
		C0050_RoleOfflineInternalMessage reqMsg = new C0050_RoleOfflineInternalMessage();
		reqMsg.setRole(role);
		reqMsg.setUserId(role.getUserId());
		reqMsg.setSession(this.channelSession);
		GameContext.getUserSocketChannelEventPublisher().publish(
				role.getUserId(), reqMsg, this.channelSession);
	}
	
	
	private boolean isChannelSessionEnable(){
		if (null == this.channelSession) {
			return false;
		}
		IoSession ioSession = ((MinaChannelSession) channelSession).getIoSession();
		if (null == ioSession || !ioSession.isConnected()) {
			return false ;
		}
		return true ;
	}
	

	@Override
	protected void sendSkillApplyMessage(Message msg, Message affixMessage,
			String srcUserId, String destUserId) {
		if (null == destUserId) {
			return;
		}
		super.sendSkillApplyMessage(msg, affixMessage, srcUserId, destUserId);
	}

	@Override
	public void addEvent(Message message) {
		if (null == message) {
			return;
		}
		GameContext.getUserSocketChannelEventPublisher().publish(
				role.getUserId(), message, this.channelSession);
	}

	@Override
	public void addCumulateEvent(Message message) {
		if (null == message) {
			return;
		}
		GameContext.getUserSocketChannelEventPublisher().publish(
				role.getUserId(), message, this.channelSession, true);
	}

	@Override
	public void addTargetFont(AttrFontSizeType sizeType, AttrFontColorType colorType,
			int value, AbstractRole targetRole){
		
		if (null == targetRole
				|| this.role.getRoleId().equals(targetRole.getRoleId())) {
			return;
		}
		// 目标不在自己视野范围之内，不需要飘字
		if (!this.inSelfEyes(targetRole)) {
			return;
		}
		this.buildFontInfo(sizeType, colorType, value, targetRole
				.getIntRoleId(), this.role.getIntRoleId());
	}

	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value) {
		this.buildFontInfo(sizeType, colorType, value, role.getIntRoleId(), 0);
	}

	private void buildFontInfo(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, int ownerId, int attackerId) {
		AttrFontInfo info = GameContext.getBattleApp().creatAttrFontInfo(sizeType, colorType, value,
				ownerId, attackerId);
		if(null == info) {
			return ;
		}
		int index = role.getAttrFontNextIndex();
		if(index >= ParasConstant.ATTRFONT_MAX_SIZE){
			index = 0;
		}
		role.getAttrFontList()[index] = info;
		int nextIndex = index + 1;
		if(nextIndex >= ParasConstant.ATTRFONT_MAX_SIZE){
			nextIndex = 0;
		}
		role.setAttrFontNextIndex(nextIndex);
	}
	
	@Override
	public void notifyAttrFont() {
		Map<Integer, List<AttrFontItem>> fontMap = null;
		AttrFontInfo[] attrFonts = role.getAttrFontList();
		for (int i = 0; i < ParasConstant.ATTRFONT_MAX_SIZE; i++) {
			AttrFontInfo attrFont = attrFonts[i];// 取出i位置的数据
			attrFonts[i] = null;// 将i位置指向空
			if (null == attrFont) {
				continue;
			}
			int roleId = attrFont.getRoleId();
			if (null == fontMap) {
				fontMap = new HashMap<Integer, List<AttrFontItem>>();
			}
			
			List<AttrFontItem> items = fontMap.get(roleId);
			if (null == items) {
				items = new ArrayList<AttrFontItem>();
				fontMap.put(roleId, items);
			}
			//属性通知
			AttrFontItem item = new AttrFontItem();
			item.setType(attrFont.getFontType());
			item.setValue(attrFont.getValue());
			items.add(item);
		}
		if (Util.isEmpty(fontMap)) {
			return;
		}
		for (Iterator<Map.Entry<Integer, List<AttrFontItem>>> it = fontMap.entrySet().iterator();it.hasNext();) {
			Map.Entry<Integer, List<AttrFontItem>> entry = it.next();
			List<AttrFontItem> items = entry.getValue();
			if (Util.isEmpty(items)) {
				continue;
			}
			int roleId = entry.getKey();
			// 如果只有一条飘字效果，则发送-403
			if (1 == items.size()) {
				C0403_RoleAttributeFontRespMessage resp = new C0403_RoleAttributeFontRespMessage();
				resp.setRoleId(roleId);
				AttrFontItem item = items.get(0);
				resp.setType(item.getType());
				resp.setValue(item.getValue());
				this.context.getMessageCenter().sendSysMsg(role, resp,
						TimeoutConstant.Notify_AttrFont_Msg_Timeout);
				continue;
			}
			// 有多个飘字效果，发送-402
			C0402_RoleAttributeFontListRespMessage message = new C0402_RoleAttributeFontListRespMessage();
			message.setRoleId(roleId);
			message.setItems(items);
			this.context.getMessageCenter().sendSysMsg(role, message,
					TimeoutConstant.Notify_AttrFont_Msg_Timeout);
		}
	}

	@Override
	public boolean inSelfEyes(AbstractRole abstractRole) {
		int screenWidth = role.getScreenWidth() + ScreenConstant.OFFSET;
		int screenHeight = role.getScreenHeight() + ScreenConstant.OFFSET;
		// 判断是否在同一屏幕内，如果不在同一屏幕内不发消息
		int originX = role.getMapX() - screenWidth / 2;
		int originY = role.getMapY() - screenHeight / 2;
		if (Util.inRectangle(originX, originY, abstractRole.getMapX(),
				abstractRole.getMapY(), screenWidth, screenHeight)) {
			return true;
		} else {
			return false;
		}
	}
	
	
	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context) {
		AttrFontInfo info = GameContext.getBattleApp().creatAttrFontInfo(sizeType, colorType, value,
				role.getIntRoleId(), 0);
		if(null == info) {
			return ;
		}
		if(sizeType == AttrFontSizeType.Attr){
			info.setSize(role.getAttriSeriesId());
		}
		context.addAttrFontInfo(role.getIntRoleId(), info);
	}
	
	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context,AbstractRole attack) {
		AttrFontInfo info = GameContext.getBattleApp().creatAttrFontInfo(sizeType, colorType, value,
				role.getIntRoleId(), 0);
		if(null == info) {
			return ;
		}
		if(sizeType == AttrFontSizeType.Attr){
			info.setSize(attack.getAttriSeriesId());
		}
		context.addAttrFontInfo(role.getIntRoleId(), info);
	}
	
	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole,
			SkillContext context) {
		if (null == targetRole
				|| this.role.getRoleId().equals(targetRole.getRoleId())) {
			return;
		}
		// 目标不在自己视野范围之内，不需要飘字
		if (!this.inSelfEyes(targetRole)) {
			return;
		}
		int ownerId = targetRole.getIntRoleId();
		AttrFontInfo info = GameContext.getBattleApp().creatAttrFontInfo(sizeType, colorType, value,
				ownerId, this.role.getIntRoleId());
		if(null == info) {
			return ;
		}
		if(sizeType == AttrFontSizeType.Attr){
			info.setSize(role.getAttriSeriesId());
		}
		
		context.addAttrFontInfo(role.getIntRoleId(), info);
	}

}
