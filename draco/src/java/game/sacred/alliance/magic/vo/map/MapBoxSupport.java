package sacred.alliance.magic.vo.map;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.AngelChestInfoItem;
import com.game.draco.message.item.AngelChestTypeItem;
import com.game.draco.message.push.C2375_ActiveAngelChestNotExistNotifyMessage;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C2370_ActiveAngelChestListRespMessage;
import com.game.draco.message.response.C2371_ActiveAngelChestRemoveRespMessage;

import sacred.alliance.magic.app.chest.ChestRefreshInfo;
import sacred.alliance.magic.app.chest.ChestRefreshRange;
import sacred.alliance.magic.app.chest.ChestTypeInfo;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 神仙福地宝箱支持
 * 
 *
 */
public class MapBoxSupport {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private AtomicLong angelChestIdGen = new AtomicLong(0);
	private final int num = 5;//因为客户端以每五秒走动数据
	//刷新size
	@Setter @Getter private int refreshSize = 0 ;
	@Setter @Getter private List<ChestRefreshInfo> refreshList = null ;
	/**
	 * 当前地图的所有宝箱
	 */
	@Setter @Getter private java.util.Map<String,AngelChestInfoItem> chestMap = new ConcurrentHashMap<String, AngelChestInfoItem>();
	@Setter @Getter private java.util.Map<String, Long> chestRefreshTimeMap = new ConcurrentHashMap<String, Long>();
	private java.util.Map<String, Long> lastRoleReadyTime = new ConcurrentHashMap<String, Long>();
	private java.util.Map<String, String> lastRoleReadyChest = new ConcurrentHashMap<String, String>();
	
	private MapInstance mapInstance ;
	private String mapId  ;
	private OutputConsumeType consumeType ;
	private OutputConsumeType mailConsumeType ;
	private String mailConText ;
	private MailSendRoleType mailSendRoleType ;
	
	public MapBoxSupport(MapInstance mapInstance,OutputConsumeType consumeType,
			OutputConsumeType mailConsumeType,String mailConText,
			MailSendRoleType mailSendRoleType) {
		this.mapInstance = mapInstance ;
		this.consumeType = consumeType ;
		this.mailConsumeType = mailConsumeType ;
		this.mailConText = mailConText ;
		this.mailSendRoleType = mailSendRoleType ;
		
		String mapId = mapInstance.getMap().getMapId();
		this.mapId = mapId ;
		refreshList = GameContext.getChestApp().getMapChestRefreshList(
				mapId);
		if (!Util.isEmpty(refreshList)) {
			refreshSize = refreshList.size();
		}
	}
	
	public void cleanData(){
		chestMap.clear();
		chestRefreshTimeMap.clear();
		this.lastRoleReadyChest.clear();
		this.lastRoleReadyTime.clear();
	}
	
	public void destroy(){
		this.cleanData();
		this.chestMap = null ;
		this.chestRefreshTimeMap = null ;
		//!!! 下面清除refreshList的代码不能存在
		//这里的refreshList是共有的配置
		/*if(null != this.refreshList){
			this.refreshList.clear();
			this.refreshList = null ;
		}*/
	}
	
	private void openChest(RoleInstance role,AngelChestInfoItem chest){
		if(null == chest || null == role){
			return ;
		}
		GameContext.getChestApp().openChest(role, chest.getType(),this.consumeType,
				this.mailConsumeType,this.mailConText,
				this.mailSendRoleType);
		this.reset(role);
	}
	
	private void openReadyEvent(RoleInstance role,String eventKey){
		this.reset(role);
		AngelChestInfoItem chest = this.chestMap.get(eventKey) ;
		if(null == chest){
			this.notifyChestNotExist(role, eventKey);
			return ;
		}
		ChestTypeInfo chestTypeInfo = GameContext.getChestApp().getChestTypeInfo(chest.getType());
		if(null == chestTypeInfo){
			return ;
		}
		//判断距离
		int dis_offset = GameContext.getParasConfig().getChestDisOffset() + (role.getSpeed()*num) ;
		int dis = Point.math_DistPointPoint(role.getMapX()-chest.getX(),role.getMapY()-chest.getY());
		if(dis > dis_offset) {
			//提示玩家距离太远
			return ;
		}
		//设置开启的宝箱ID和时间
		this.lastRoleReadyChest.put(role.getRoleId(), eventKey);
		this.lastRoleReadyTime.put(role.getRoleId(), System.currentTimeMillis());
	}
	
	private void openEvent(RoleInstance role,String eventKey){
		if(Util.isEmpty(eventKey)){
			return ;
		}
		AngelChestInfoItem chest = this.chestMap.get(eventKey) ;
		if(null == chest){
			this.notifyChestNotExist(role, eventKey);
			return ;
		}
		//判断以前是否发送过准备消息
		String boxKey = this.lastRoleReadyChest.get(role.getRoleId());
		if(Util.isEmpty(boxKey) || !boxKey.equals(eventKey)){
			//开启的宝箱id不相同
			this.reset(role);
			return ;
		}
		//判断时间
		Long readyTime = this.lastRoleReadyTime.get(role.getRoleId());
		if(null == readyTime){
			this.reset(role);
			return ;
		}
		long refreshTime = 0;
		String id = chest.getId();
		if(this.chestRefreshTimeMap.containsKey(id)) {
			refreshTime = this.chestRefreshTimeMap.get(id);
		}
		boolean canOpen = GameContext.getChestApp().canOpen(role, refreshTime, readyTime,chest);
		if(!canOpen) {
			this.reset(role);
			return ;
		}
		synchronized (chest) {
			chest = this.chestMap.get(eventKey) ;
			if(null == chest){
				this.notifyChestNotExist(role, eventKey);
				return ;
			}
			//从已有中删除
			this.chestMap.remove(eventKey);
			//把刷新时间删除
			this.chestRefreshTimeMap.remove(id);
		}
		
		//开箱子获得物品逻辑
		this.openChest(role, chest);
		if(null == mapInstance){
			return ;
		}
		//广播消失
		C2371_ActiveAngelChestRemoveRespMessage respMsg = new C2371_ActiveAngelChestRemoveRespMessage();
		respMsg.setId(chest.getId());
		mapInstance.broadcastMap(null, respMsg);
	}
	
	public void doEvent(RoleInstance role,MapInstanceEvent event){
		if(null == event || null == role){
			return ;
		}
		String eventKey = event.getEventKey() ;
		if(event.getEventType() == MapInstanceEvent.EventType.chestOpen){
			this.openEvent(role, eventKey);
			return ;
		}
		if(event.getEventType() == MapInstanceEvent.EventType.chestOpenReady){
			this.openReadyEvent(role, eventKey);
			return ;
		}
	}
	
	private void notifyChestNotExist(RoleInstance role,String id){
		C2375_ActiveAngelChestNotExistNotifyMessage respMsg = new C2375_ActiveAngelChestNotExistNotifyMessage();
		respMsg.setId(id);
		role.getBehavior().sendMessage(respMsg);
		//提示玩家已经被开启
		C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage();
		notifyMsg.setMsgContext(GameContext.getI18n().getText(TextId.ANGELCHEST_HAD_OPEN));
		role.getBehavior().sendMessage(notifyMsg);
	}
	

	public List<AngelChestInfoItem> refresh(ChestRefreshInfo born){
		if(null == born || born.getChestNum() <=0){
			return null;
		}
		//随机取刷新区域
		int startRange = RandomUtil.randomInt(born.getRangeList().length);
		List<AngelChestInfoItem> list = new ArrayList<AngelChestInfoItem>();
		for(int i=0;i<born.getChestNum();i++){
			int rangIndex =  (startRange + i)%born.getRangeList().length ;
			ChestRefreshRange range = GameContext.getChestApp().getChestRefreshRange(born.getRangeList()[rangIndex]);
			if(null == range){
				continue ;
			}
			int bornX = Util.randomInRange(range.getBornmapgxbegin(), range.getBornmapgxend()-range.getBornmapgxbegin());
			int bornY = Util.randomInRange(range.getBornmapgybegin(), range.getBornmapgyend()-range.getBornmapgybegin());
			AngelChestInfoItem instance = new AngelChestInfoItem();
			instance.setType((byte)born.getChestType());
			instance.setId(String.valueOf(angelChestIdGen.incrementAndGet()));
			instance.setX((short)bornX);
			instance.setY((short)bornY);
			list.add(instance);
			String id = instance.getId();
			//放入列表
			chestMap.put(id, instance);
			//把时间放入
			chestRefreshTimeMap.put(id, System.currentTimeMillis());
		}
		//广播已经刷出
		if(!Util.isEmpty(born.getBroadcast())){
			GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.Map, born.getBroadcast(), null,mapInstance);
		}
		return list ;
	}
	
	public void enter(AbstractRole role){
		this.enter(role, (byte)1, "");
	}
	
	public void enter(AbstractRole role,byte activeStatus,String nextOpenTimeStr){
		if(null == role 
				|| role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		//设置时间
		this.reset((RoleInstance)role);
		//进入地图时发送相关宝箱信息
		this.sendAngelChestList(role, activeStatus,nextOpenTimeStr);
	}
	
	public int chestSize(){
		return chestMap.size() ;
	}
	
	private void sendAngelChestList(AbstractRole role,byte status,String nextOpenTime){
		C2370_ActiveAngelChestListRespMessage respMsg = new C2370_ActiveAngelChestListRespMessage();
		List<ChestTypeInfo> chestTypeList = GameContext.getChestApp().getMapChestTypeInfo(this.mapId);
		if(!Util.isEmpty(chestTypeList)){
			List<AngelChestTypeItem> typeList = new ArrayList<AngelChestTypeItem>();
			for(ChestTypeInfo cti : chestTypeList){
				AngelChestTypeItem item = new AngelChestTypeItem();
				item.setName(cti.getChestName());
				item.setType((byte)cti.getChestType());
				item.setTime(cti.getProgressTime());
				item.setResId((short)cti.getResId());
				typeList.add(item);
			}
			respMsg.setTypeList(typeList);
		}
		try {
			// 当前所有
			if (!Util.isEmpty(chestMap)) {
				List<AngelChestInfoItem> chestList = new ArrayList<AngelChestInfoItem>();
				chestList.addAll(chestMap.values());
				respMsg.setChestList(chestList);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		respMsg.setStatus(status);
		respMsg.setNextOpenTime(nextOpenTime);
		role.getBehavior().sendMessage(respMsg);
	}
	
	
	public void damageTaken(AbstractRole attacker, AbstractRole victim, int hurt) {
		if(null == victim 
				|| victim.getRoleType() != RoleType.PLAYER){
			return ;
		}
		RoleInstance role = (RoleInstance)victim;
		this.reset(role);
	}
	
	public void reset(RoleInstance role){
		if(null == role){
			return ;
		}
		role.setAngelChestTime(System.currentTimeMillis());
		this.lastRoleReadyChest.remove(role.getRoleId());
		this.lastRoleReadyTime.remove(role.getRoleId());
	}
}
