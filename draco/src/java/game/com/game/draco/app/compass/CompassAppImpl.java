package com.game.draco.app.compass;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.app.compass.config.*;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.CompassConsumeItem;
import com.game.draco.message.item.CompassItem;
import com.game.draco.message.item.CompassStopItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C1907_CompassListRespMessage;
import com.game.draco.message.response.C1908_CompassDisplayRespMessage;
import com.game.draco.message.response.C1910_CompassStopRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.*;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import java.util.*;

public class CompassAppImpl implements CompassApp{

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/** 上古法阵列表：KEY=ID,VALUE=法阵对象 */
	private Map<Short,Compass> compassMap = new LinkedHashMap<Short,Compass>();
	/**
	 * 淘宝额外产出id
	 */
	private Map<Short,TaobaoExtra> taobaoExtraMap = new HashMap<Short,TaobaoExtra>();

	private Map<String,Map<Short,List<CompassRoleAward>>> roleCompassCache = Maps.newConcurrentMap() ;


	private List<CompassRoleAward> getAwardCache(String roleId, short taobaoId){
		return Util.fromMap(Util.fromMap(this.roleCompassCache,roleId),taobaoId);
	}

	private void cleanAwardCache(String roleId,short taobaoId){
		Map<Short,List<CompassRoleAward>> map = Util.fromMap(this.roleCompassCache,roleId) ;
		if(Util.isEmpty(map)){
			return ;
		}
		List<CompassRoleAward> list = map.remove(taobaoId);
		if(!Util.isEmpty(list)){
			list.clear();
		}
		list = null ;
	}

	private void addAwardCache(String roleId,short taobaoId,CompassRoleAward award){
		if(null == award){
			return ;
		}
		Map<Short,List<CompassRoleAward>> map = Util.fromMap(this.roleCompassCache,roleId) ;
		if(null == map){
			map = Maps.newHashMap();
			this.roleCompassCache.put(roleId,map);
		}
		List<CompassRoleAward> list = map.get(taobaoId);
		if(null == list){
			list = Lists.newArrayList() ;
			map.put(taobaoId,list);
		}
		list.add(award);
	}

	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		//首先加载额外奖励
		this.loadTaobaoExtra();
		this.loadCompassConfig();
	}

	@Override
	public void stop() {
		
	}
	
	private void loadTaobaoExtra(){
		String fileName = XlsSheetNameType.taobao_extra.getXlsName();
		String sheetName = XlsSheetNameType.taobao_extra.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<TaobaoExtra> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, TaobaoExtra.class);
			for(TaobaoExtra extra : list){
				if(null == extra){
					continue;
				}
				//初始化上古法阵配置,验证奖励物品
				extra.init();
				this.taobaoExtraMap.put(extra.getExtraId(), extra);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("load taobao_extra error:fileName=" + fileName + ",sheetName=" + sheetName,e);
			Log4jManager.checkFail();
		}
	}
	
	/** 加载上古法阵配置表 */
	private void loadCompassConfig(){
		String fileName = XlsSheetNameType.taobao_compass.getXlsName();
		String sheetName = XlsSheetNameType.taobao_compass.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<Compass> list = XlsPojoUtil.sheetToList(sourceFile, sheetName, Compass.class);
			for(Compass compass : list){
				if(null == compass){
					continue;
				}
				//初始化上古法阵配置,验证奖励物品
				compass.init();
				this.compassMap.put(compass.getTaobaoId(), compass);
			}
		}catch(Exception e){
			Log4jManager.CHECK.error("load taobao_compass error:fileName=" + fileName + ",sheetName=" + sheetName,e);
			Log4jManager.checkFail();
		}
	}
	
	@Override
	public Compass getCompass(short id) {
		return this.compassMap.get(id);
	}
	
	@Override
	public TaobaoExtra getTaobaoExtra(short extraId) {
		if(null == this.taobaoExtraMap){
			return null ;
		}
		return this.taobaoExtraMap.get(extraId);
	}
	
	private List<TaobaoExtra> getTaobaoExtraList(List<Short> extraIdList) {
		if(null == this.taobaoExtraMap){
			return null ;
		}
		List<TaobaoExtra> taobaoExtraList = new ArrayList<TaobaoExtra>();
		for(short extraId : extraIdList){
			taobaoExtraList.add(this.taobaoExtraMap.get(extraId));
		}
		return taobaoExtraList;
	}
	
	@Override
	public Result compassStop(RoleInstance role, short id) {
		Result result = new Result();
		Compass compass = this.getCompass(id);
		if(null == compass){
			return result.setInfo(Status.Compass_Not_Exist.getTips());
		}
		//奖励缓存
		List<CompassRoleAward> roleAwardList = this.getAwardCache(role.getRoleId(), id);
		if(Util.isEmpty(roleAwardList)){
			return result.setInfo(Status.Compass_Failure.getTips());
		}
		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
		for(CompassRoleAward award : roleAwardList){
			GoodsOperateBean bean = new GoodsOperateBean();
			bean.setGoodsId(award.getGoodsId());
			bean.setGoodsNum(award.getGoodsNum());
			bean.setBindType(BindingType.get(award.getBindType()));
			addList.add(bean);
			//发系统广播
			try{
				this.sendBroadcastInfo(role, award.getBroadcastInfo());
			}catch(Exception e){
				this.logger.error("CompassApp.compassStop error: ", e);
			}
		}
		//清空奖励缓存
		this.cleanAwardCache(role.getRoleId(),id);
		//向背包中添加物品
		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList, OutputConsumeType.compass_output);
		List<CompassStopItem> compassStopList = new ArrayList<CompassStopItem>();
		//背包满了发邮件
		List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
		try{
			if(!Util.isEmpty(putFailureList)){
				GameContext.getMailApp().sendMail(role.getRoleId(),
						MailSendRoleType.Compass.getName(),
						Status.Compass_Mail_Context.getTips(),
						MailSendRoleType.Compass.getName(),
						OutputConsumeType.compass_mail_output.getType(),
						putFailureList);
				for(GoodsOperateBean bean : putFailureList){
					if(null == bean){
						continue;
					}
					compassStopList.add(this.buildCompassStopItem(bean, true));
				}
			}
		}catch(Exception e){
			logger.error("",e);
		}
		//面板上通知获得物品
		List<GoodsOperateBean> putSuccessList = goodsResult.getPutSuccessList();
		if(!Util.isEmpty(putSuccessList)){
			for(GoodsOperateBean bean : putSuccessList){
				if(null == bean){
					continue;
				}
				compassStopList.add(this.buildCompassStopItem(bean, false));
			}
		}
		C1910_CompassStopRespMessage message = new C1910_CompassStopRespMessage();
		message.setCompassStopList(compassStopList);
		role.getBehavior().sendMessage(message);
		return result.success();
	}
	
	private void sendBroadcastInfo(RoleInstance role, String bradcastInfo){
		if(Util.isEmpty(bradcastInfo)){
			return;
		}
		String message = bradcastInfo.replace(Wildcard.Role_Name, role.getRoleName());
		GameContext.getChatApp().sendSysMessage(ChatSysName.Active_Compass, ChannelType.Publicize_Personal, message, null, null);
	}
	
	/**
	 * 构建面板通知消息的Item
	 * @param bean
	 * @param sendByMail 是否是通过邮件发送的
	 * @return
	 */
	private CompassStopItem buildCompassStopItem(GoodsOperateBean bean, boolean sendByMail){
		CompassStopItem item = new CompassStopItem();
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
		item.setGoodsName(goodsBase.getName());
		item.setGoodsNum((short) bean.getGoodsNum());
		item.setSendMode((byte) 0);
		if(sendByMail){
			item.setSendMode((byte) 1);
		}
		return item;
	}
	
	private List<CompassRoleAward> getExtraAwardList(Compass compass,byte place) {
		List<TaobaoExtra> extraList = this.getTaobaoExtraList(compass.getExtraIdList());
		if(Util.isEmpty(extraList)){
			return null ;
		}
		List<CompassRoleAward> award = new ArrayList<CompassRoleAward>();
		for(TaobaoExtra extra : extraList){
			CompassRoleAward a = extra.getAward(compass,place) ;
			if(null == a){
				continue ;
			}
			award.add(a);
		}
		return award;
	}
	
	private void packRoleCompassAwardMap(RoleInstance role ,short id,
			List<CompassRoleAward> extraAwardList){
		if(Util.isEmpty(extraAwardList)){
			return ;
		}
		for(CompassRoleAward extraAward : extraAwardList){
			this.addAwardCache(role.getRoleId(), id, extraAward);
		}
	}
	
	@Override
	public byte[] getCompassStopPlace(RoleInstance role, short id, byte count) {
		//清除以前cache
		List<CompassRoleAward> awardList = this.getAwardCache(role.getRoleId(),id);
		if(!Util.isEmpty(awardList)){
			//将之前的缓存奖励通过邮件发给用户
			this.sendMailByCache(role, awardList);
			//清除之前的缓存奖励
			this.cleanAwardCache(role.getRoleId(),id);
		}
		byte[] result = new byte[count];
		Compass compass = this.getCompass(id) ;
		synchronized (compass) {
			for(int i=0; i<count; i++){
				CompassRoleAward award = compass.getAward();
				result[i] = award.getPlace();
				//添加到cache
				this.addAwardCache(role.getRoleId(), id, award);
				try {
					// 额外附加奖励
					List<CompassRoleAward> extraAwardList = this
							.getExtraAwardList(compass, award.getPlace());
					this.packRoleCompassAwardMap(role, id, extraAwardList);
				}catch(Exception ex){
					this.logger.error("CompassApp.getCompassStopPlace extraAwardList error: ", ex);
				}
			}
		}
		try{
			//统计淘宝抽奖次数
			GameContext.getCountApp().updateTaobao(role, compass.getTaobaoType(), count);
			//日志
			GameContext.getStatLogApp().compassLog(role, id, count);
		}catch(Exception e){
			this.logger.error("CompassApp.getCompassStopPlace error: ", e);
		}
		return result;
	}

	@Override
	public Result checkCondition(RoleInstance role, short id, byte count) {
		Result result = new Result();
		CompassCountType countType = CompassCountType.get(count);
		if(null == countType){
			return result.setInfo(Status.Compass_Count_Error.getTips());
		}
		Compass compass = this.compassMap.get(id);
		if(null == compass){
			return result.setInfo(Status.Compass_Req_Param_Error.getTips());
		}
		if(!compass.isSuitLevel(role)){
			return result.setInfo(Status.Compass_Not_Role_Level.getTips());
		}
		if(!compass.isTimeOpen()){
			return result.setInfo(Status.Compass_Not_Time.getTips());
		}
		int goodsId = compass.getGoodsId();
        int haveGoodsNum = 0 ;
        int consumeGoodsNum = this.getConsumeGoodsNum(compass,countType);
		if(goodsId > 0 && consumeGoodsNum > 0){
            haveGoodsNum = role.getRoleBackpack().countByGoodsId(goodsId);
        }
		int consumeValue = this.getConsumeValue(compass,countType) ;
		if(consumeValue <=0 && consumeGoodsNum<=0){
			result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
			return result ;
		}
        if(haveGoodsNum >= consumeGoodsNum){
            //消耗道具
           GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBag(role,goodsId,
                   consumeGoodsNum,OutputConsumeType.compass_consume);
            if(goodsResult.isSuccess()){
                return goodsResult ;
            }
        }
        if(consumeValue <= 0){
            //提示道具不足
            result.setInfo(GameContext.getI18n().getText(TextId.GOODS_NO_ENOUGH));
            return result ;
        }
		AttributeType consumeType = AttributeType.get(compass.getConsumeAttriType());
        if(null == consumeType){
            result.setInfo(GameContext.getI18n().getText(TextId.ERROR_DATA));
            return result ;
        }
		//判断角色的属性是否足够
		result = GameContext.getUserAttributeApp().getEnoughResult(role, consumeType, consumeValue);
		if (result.isIgnore() || !result.isSuccess()) {
			return result ;
		}
		GameContext.getUserAttributeApp().changeAttribute(role, consumeType,
				OperatorType.Decrease, consumeValue, OutputConsumeType.compass_consume);
		role.getBehavior().notifyAttribute();
		return result.success();
	}
	
	/**
	 * 未领取的奖励给角色发邮件
	 * @param role
	 * @param awardList
	 */
	private void sendMailByCache(RoleInstance role, List<CompassRoleAward> awardList){
		try{
			String context = Status.Compass_Mail_Context.getTips();
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			int affixNum = 0;
			for(CompassRoleAward award : awardList){
				if(null == award){
					continue;
				}
				if(affixNum >= Mail.MaxAccessoryNum){
					GameContext.getMailApp().sendMail(mail);//发送邮件
					mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));//创建新邮件
					affixNum = 0;//重置附件物品数量
				}
				affixNum ++;//累计附件物品
				mail.setTitle(MailSendRoleType.Compass.getName());
				mail.setSendRole(MailSendRoleType.Compass.getName());
				mail.setContent(context);
				mail.setRoleId(role.getRoleId());
				mail.setSendSource(OutputConsumeType.compass_mail_output.getType());
				mail.addMailAccessory(award.getGoodsId(), award.getGoodsNum(), BindingType.get(award.getBindType()));
			}
			if(affixNum > 0){//发送附件不满的邮件
				GameContext.getMailApp().sendMail(mail);
			}
		}catch(Exception e){
			this.logger.error("CompassApp.sendMailByCache error:" + e);
		}
	}

	@Override
	public Message getCompassListMessage(RoleInstance role) {
		try {
			List<CompassItem> compassList = new ArrayList<CompassItem>();
			for(Compass compass : this.compassMap.values()){
				if(null == compass){
					continue;
				}
				//角色等级或开启时间不符
				if(!compass.isSuitLevel(role) || !compass.isTimeOpen()){
					continue;
				}
				CompassItem item = new CompassItem();
				item.setId(compass.getTaobaoId());
				item.setName(compass.getName());
				compassList.add(item);
			}
			C1907_CompassListRespMessage message = new C1907_CompassListRespMessage();
			message.setCompassList(compassList);
			return message;
		} catch (Exception e) {
			this.logger.error("CompassApp.getCompassListMessage error: ", e);
			return new C0003_TipNotifyMessage(Status.Compass_Req_Param_Error.getTips());
		}
	}



	private int getConsumeValue(Compass compass,CompassCountType count){
		if(CompassCountType.OneTimes == count){
			return compass.getConsume1() ;
		}
		if(CompassCountType.TenTimes == count){
			return compass.getConsume10() ;
		}
		return compass.getConsume50() ;
	}

    private short getConsumeGoodsNum(Compass compass,CompassCountType count){
        if(CompassCountType.OneTimes == count){
            return compass.getConsumeGoods1() ;
        }
        if(CompassCountType.TenTimes == count){
            return compass.getConsumeGoods10() ;
        }
        return compass.getConsumeGoods50() ;
    }


	private List<CompassConsumeItem> getConsumeItemList(Compass compass){
		List<CompassConsumeItem> list = Lists.newArrayList() ;
		for(CompassCountType count : CompassCountType.values()){
			CompassConsumeItem item = new CompassConsumeItem() ;
			item.setTimes(count.getType());
			item.setConsumeValue(this.getConsumeValue(compass,count));
            item.setGoodsNum(this.getConsumeGoodsNum(compass,count));
			list.add(item);
		}
		return list ;
	}

	@Override
	public Message openCompassPanel(RoleInstance role, short id) {
		try{
			Compass compass = this.getCompass(id);
			if(null == compass || !compass.isSuitLevel(role) || !compass.isTimeOpen()){
				return new C0003_TipNotifyMessage(Status.Compass_Req_Param_Error.getTips());
			}
			C1908_CompassDisplayRespMessage message = new C1908_CompassDisplayRespMessage();
			message.setId(id);
			message.setGoodsId(compass.getGoodsId());
            message.setGoodsImageId(compass.getGoodsImageId());
			message.setConsumeAttriType(compass.getConsumeAttriType());
			//消耗列表
			//message.setConsume1(this.getConsumeValue(compass,CompassCountType.OneTimes));
			//message.setConsume10(this.getConsumeValue(compass, CompassCountType.TenTimes));
			//message.setConsume50(this.getConsumeValue(compass, CompassCountType.FiftyTimes));
			message.setConsumeList(this.getConsumeItemList(compass));

			List<GoodsLiteItem> displayList = new ArrayList<GoodsLiteItem>();
			List<CompassAward> awardList = compass.getAwardList();
			for(CompassAward award : awardList){
				GoodsBase goodsBase = award.getAwardGoods();
				GoodsLiteItem item = goodsBase.getGoodsLiteItem();
				item.setBindType(award.getBindType());
				displayList.add(item);
			}
			message.setDisplayList(displayList);
			return message;
		}catch(Exception e){
			this.logger.error("CompassApp.getCompassPanelMessage error: ", e);
			return new C0003_TipNotifyMessage(Status.Compass_Req_Param_Error.getTips());
		}
	}


	@Override
	public Collection<Compass> getAllCompass() {
		return this.compassMap.values();
	}


	@Override
	public int onLogin(RoleInstance role, Object context) {
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		Map<Short,List<CompassRoleAward>> mapData = roleCompassCache.remove(role.getRoleId());
		if(Util.isEmpty(mapData)){
			return 1;
		}
		for(List<CompassRoleAward> awardList : mapData.values()){
			if(Util.isEmpty(awardList)){
				continue;
			}
			try{
				this.sendMailByCache(role, awardList);
			}catch(Exception e){
				this.logger.error("CompassApp.offline error:" + e);
			}
		}
		return 1 ;
	}

	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
	}
}
