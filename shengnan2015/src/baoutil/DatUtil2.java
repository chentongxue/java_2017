//package sacred.alliance.magic.app.compass;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import sacred.alliance.magic.app.chat.ChannelType;
//import sacred.alliance.magic.app.chat.ChatSysName;
//import sacred.alliance.magic.app.goods.GoodsOperateBean;
//import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
//import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
//import sacred.alliance.magic.app.mail.MailSendRoleType;
//import sacred.alliance.magic.base.AttributeType;
//import sacred.alliance.magic.base.BindingType;
//import sacred.alliance.magic.base.OperatorType;
//import sacred.alliance.magic.base.OutputConsumeType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.base.XlsSheetNameType;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.domain.GoodsBase;
//import sacred.alliance.magic.util.Log4jManager;
//import sacred.alliance.magic.util.Util;
//import sacred.alliance.magic.util.Wildcard;
//import sacred.alliance.magic.util.XlsPojoUtil;
//import sacred.alliance.magic.vo.RoleInstance;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.CompassConsumeItem;
//import com.game.draco.message.item.CompassItem;
//import com.game.draco.message.item.CompassStopItem;
//import com.game.draco.message.item.GoodsLiteItem;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//import com.game.draco.message.response.C1907_CompassListRespMessage;
//import com.game.draco.message.response.C1908_CompassDisplayRespMessage;
//import com.game.draco.message.response.C1910_CompassStopRespMessage;
//import com.google.common.collect.Lists;
//
///**
// * @author gaibaoning@moogame.cn
// * @date 2014-3-28 下午03:59:53
// * @version V1.0
// */
//public class CompassAppImpl implements CompassApp {
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	/**
//	 * 罗盘列表：KEY=罗盘Id,VALUE=对应的Compass
//	 */
//	private Map<Short, Compass> compassMap = new LinkedHashMap<Short, Compass>();
//
//	/**
//	 * 加载罗盘配置EXCEL表
//	 * 
//	 * @author gaibaoning@moogame.cn
//	 * @date 2014-3-28 下午06:14:35
//	 * @version V1.0
//	 */
//	private void loadCompassConfig() {
//		String fileName = XlsSheetNameType.compass.getXlsName();
//		String sheetName = XlsSheetNameType.compass.getSheetName();
//		try {
//			String sourceFile = GameContext.getPathConfig().getXlsPath()
//					+ fileName;
//			List<Compass> list = XlsPojoUtil.sheetToList(sourceFile, sheetName,
//					Compass.class);
//			for (Compass compass : list) {
//				if (compass == null) {
//					continue;
//				}
//				// 初始化罗盘配置,验证奖励物品
//				compass.init();
//				// 将罗盘信息，添加到Map
//				this.compassMap.put(compass.getId(), compass);
//			}
//		} catch (Exception e) {
//			Log4jManager.CHECK.error("load compass error:fileName=" + fileName
//					+ ",sheetName=" + sheetName);
//			Log4jManager.checkFail();
//		}
//	}
//
//	private void sendBroadcastInfo(RoleInstance role, String bradcastInfo) {
//		if (Util.isEmpty(bradcastInfo)) {
//			return;
//		}
//		String message = bradcastInfo.replace(Wildcard.Role_Name,
//				role.getRoleName());
//		GameContext.getChatApp().sendSysMessage(ChatSysName.Active_Compass,
//				ChannelType.Publicize_Personal, message, null, null);
//	}
//
//	/**
//	 * 罗盘停止后的处理
//	 * 
//	 * @param bean
//	 * @param sendByMail
//	 *            是否发送邮件
//	 * @return
//	 * @author gaibaoning@moogame.cn
//	 * @date 2014-3-28 下午06:13:24
//	 * @version V1.0
//	 */
//	private CompassStopItem buildCompassStopItem(GoodsOperateBean bean,
//			boolean sendByMail) {
//		CompassStopItem item = new CompassStopItem();
//		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(
//				bean.getGoodsId());
//		item.setGoodsName(goodsBase.getName());
//		item.setGoodsNum((short) bean.getGoodsNum());
//		item.setSendMode((byte) 0);
//		if (sendByMail) {
//			item.setSendMode((byte) 1);
//		}
//		return item;
//	}
//
//	/**
//	 * 未领取的奖励给角色发邮件
//	 * 
//	 * @param role
//	 * @param awardList
//	 * @author gaibaoning@moogame.cn
//	 * @date 2014-3-28 下午06:44:17
//	 * @version V1.0
//	 */
//	private void sendMailByCache(RoleInstance role,
//			List<CompassRoleAward> awardList) {
//		try {
//			if (Util.isEmpty(awardList)) {
//				return;
//			}
//			List<GoodsOperateBean> goodsList = Lists.newArrayList();
//			for (CompassRoleAward award : awardList) {
//				goodsList.add(new GoodsOperateBean(award.getGoodsId(), award
//						.getGoodsNum(), BindingType.get(award.getBindType())));
//			}
//			String context = Status.Compass_Mail_Context.getTips();
//			GameContext.getMailApp().sendMail(role.getRoleId(),
//					MailSendRoleType.Compass.getName(), context,
//					MailSendRoleType.Compass.getName(),
//					OutputConsumeType.compass_mail_output.getType(), goodsList);
//		} catch (Exception e) {
//			this.logger.error("CompassApp.sendMailByCache error:" + e);
//		}
//	}
//
//	@Override
//	public void setArgs(Object arg0) {
//
//	}
//
//	/**
//	 * 判断得到奖励的条件 罗盘是否存在，角色等级是否满足，活动时间是否是开启状态 区分淘宝和幸运罗盘
//	 */
//	@Override
//	public Result checkCondition(RoleInstance role, short id, byte count) {
//		  Result result = new Result();
//		  //幸运罗盘
//		  Compass compass = this.getCompass(id);
//		  if (compass == null) {
//			  return result.setInfo(Status.Compass_Req_Param_Error.getTips());
//		  }
//		  if(compass.getSortId() == 1){
//			  
//		  }else{//淘宝用
//			  CompassCountType countType = CompassCountType.get(count);
//			  if (countType == null) {
//				  return result.setInfo(Status.Compass_Count_Error.getTips());
//			  }
//		  }
//		  
//		 
//		  if (!compass.isSuitLevel(role)) {
//			  return result.setInfo(Status.Compass_Not_Role_Level.getTips());
//		  }
//		  if (!compass.isTimeOpen()) {
//			  return result.setInfo(Status.Compass_Not_Time.getTips());
//		  }
//		  //根据转动次数                                    获得指定卷轴的ID和消耗的金币
//		  CompassConsumeItem compassConsumeItem = compass.getCompassConsumeItem(count);
//		  if (compassConsumeItem == null) {
//		      return result.setInfo(Status.Compass_Req_Param_Error.getTips());
//		  }
//		  //道具ID
//		  int goodsId = compassConsumeItem.getGoodsId();
//		  // 转动消耗的金币
//		  int goldMoney = compassConsumeItem.getGoldMoney();
//		  //支持使用道具的（优先使用道具，无道具的情况用金币）
//		  if (goodsId > 0) {
//			  //角色拥有的道具数量，有道具使用道具，有几个扣几个
//			  int roleGoodsNum = role.getRoleBackpack().countByGoodsId(goodsId);
//			  if (roleGoodsNum > 0) {
//				  GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBag(role, goodsId, roleGoodsNum,
//		          OutputConsumeType.compass_consume);
//				  if (!goodsResult.isSuccess()) {// 不成功
//					  return goodsResult;
//					  }
//				  } else 
//				  {
//					  // 道具不足
//					  int roleMoney = role.getGoldMoney();
//					  // 消耗的钱数
//					  int expendMoney = goldMoney;
//					  if (roleMoney < expendMoney) {
//						  return result.setInfo(Status.Compass_Money_Not_Enough.getTips());
//					  }
//					  // 扣钱
//					  GameContext.getUserAttributeApp().changeRoleMoney(role,AttributeType.goldMoney, OperatorType.Decrease,expendMoney, OutputConsumeType.compass_consume);
//		              role.getBehavior().notifyAttribute();
//		              }
//			  } else {// 只支持消耗金币的
//				  int roleMoney = role.getGoldMoney();
//				  // 消耗的金币数目
//				  int expendMoney = goldMoney;
////				  if (roleMoney < expendMoney) {//元宝不足 <Ted>
////					  return result.setInfo(Status.Compass_Money_Not_Enough.getTips());
////				  }
////				  // 扣钱
////				  GameContext.getUserAttributeApp().changeRoleMoney(role,AttributeType.goldMoney, OperatorType.Decrease,expendMoney, OutputConsumeType.compass_consume);
////				  role.getBehavior().notifyAttribute();
//			  }
//		  return result.success();
//
//	}
//
//	/**
//	 * 1910 对玩家发奖励，取得奖励列表
//	 */
//	@Override
//	public Result compassStop(RoleInstance role, short id) {
//		Result result = new Result();
//		Compass compass = this.getCompass(id);
//		if (compass == null) {
//			return result.setInfo(Status.Compass_Not_Exist.getTips());
//		}
//		// 奖励缓存是否为空
//		List<CompassRoleAward> roleAwardList = role.getCompassAwardMap()
//				.get(id);
//		if (Util.isEmpty(roleAwardList)) {
//			return result.setInfo(Status.Compass_Failure.getTips());
//		}
//		List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
//		for (CompassRoleAward award : roleAwardList) {
//			GoodsOperateBean bean = new GoodsOperateBean();
//			bean.setGoodsId(award.getGoodsId());
//			bean.setGoodsNum(award.getGoodsNum());
//			bean.setBindType(BindingType.get(award.getBindType()));
//			addList.add(bean);
//			// 发系统 广播
//			try {
//				this.sendBroadcastInfo(role, award.getBroadcastInfo());
//			} catch (Exception e) {
//				this.logger.error("CompassApp.compassStop error: ", e);
//			}
//		}
//		// 清空奖励缓存
//		roleAwardList.clear();
//		// 向背包中添加物品
//		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp()
//				.addSomeGoodsBeanForBag(role, addList,
//						OutputConsumeType.compass_output);
//
//		// 背包满了则发邮件
//		List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
//		try {
//			if(!Util.isEmpty(putFailureList)){
//				String context = Status.Compass_Mail_Context.getTips();
//				GameContext.getMailApp().sendMail(role.getRoleId(),
//							MailSendRoleType.Compass.getName(), 
//							context,
//							MailSendRoleType.Compass.getName(), 
//							OutputConsumeType.compass_mail_output
//							.getType(),
//							putFailureList);
//			}
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//		
//		List<CompassStopItem> compassStopList = new ArrayList<CompassStopItem>();
//		// 界面通知玩家获得奖励
//		List<GoodsOperateBean> putSuccessList = goodsResult.getPutSuccessList();
//		//直接添加到背包的物品
//		this.appendCompassStopItem(compassStopList, putSuccessList, false);
//		//邮件发送的物品
//		this.appendCompassStopItem(compassStopList, putFailureList, true);
//		C1910_CompassStopRespMessage message = new C1910_CompassStopRespMessage();
//		message.setCompassStopList(compassStopList);
//		role.getBehavior().sendMessage(message);
//		return result.success();
//	}
//	
//	private void appendCompassStopItem(List<CompassStopItem> compassStopList,
//			List<GoodsOperateBean> goodsList,boolean sendByMail){
//		if(Util.isEmpty(goodsList)){
//			return ;
//		}
//		for (GoodsOperateBean bean : goodsList) {
//			if (bean == null) {
//				continue;
//			}
//			compassStopList.add(this.buildCompassStopItem(bean, sendByMail));
//		}
//	}
//
//	/**
//	 * 获得所有的罗盘列表
//	 */
//	@Override
//	public Collection<Compass> getAllCompass() {
//		return this.compassMap.values();
//	}
//
//	@Override
//	public Compass getCompass(short id) {
//		return this.compassMap.get(id);
//	}
//
//	@Override
//	public Message getCompassListMessage(RoleInstance role) {
//		try {
//			List<CompassItem> compassList = new ArrayList<CompassItem>();
//			for (Compass compass : this.compassMap.values()) {
//				if (null == compass) {
//					continue;
//				}
//				// 角色等级或开启时间不符
//				if (!compass.isSuitLevel(role) || !compass.isTimeOpen()) {
//					continue;
//				}
//				CompassItem item = new CompassItem();
//				item.setId(compass.getId());
//				item.setImageId(compass.getImageId());
//				compassList.add(item);
//			}
//			C1907_CompassListRespMessage message = new C1907_CompassListRespMessage();
//			message.setCompassList(compassList);
//			return message;
//		} catch (Exception e) {
//			this.logger.error("CompassApp.getCompassListMessage error: ", e);
//			return new C0003_TipNotifyMessage(
//					Status.Compass_Req_Param_Error.getTips());
//		}
//	}
//
//	/**
//	 * 1909 获得他、罗盘停止位置列表 未统计抽奖个数
//	 */
//	@Override
//	public byte[] getCompassStopPlace(RoleInstance role, short id, byte count) {
//		// 奖励列表，先获取角色身上的奖励列表
//		List<CompassRoleAward> awardList = role.getCompassAwardMap().get(id);
//		if (null == awardList) {
//			role.getCompassAwardMap()
//					.put(id, new ArrayList<CompassRoleAward>());
//		} else if (awardList.size() > 0) {
//			this.sendMailByCache(role, awardList);// 将之前的缓存奖励通过邮件发给用户
//			awardList.clear();// 清除之前的缓存奖励
//		}
//
//		// 获得结果
//		byte[] result = new byte[count];
//		Compass compass = this.compassMap.get(id);
//		// 获得权重列表
//		HashMap<Integer, Integer> placeMap = compass.getPlaceMap();
//		// 抽奖逻辑
//		Set<Integer> resultSet = Util.getWeightCalct(count, placeMap);
//		Iterator<Integer> it = resultSet.iterator();
//
//		// 生成奖励列表，给1910用
//		byte index = 0;
//		while (it.hasNext()) {
//			int value = it.next();
//			result[index++] = (byte) value;
//			// 抽中奖励,组成奖励列表
//			CompassAward award = compass.getAwardList().get(value);
//			CompassRoleAward roleAward = new CompassRoleAward();
//			roleAward.setId(id);// 罗盘ＩＤ
//			roleAward.setPlace((byte) value);// 抽奖结果，从0开始
//			roleAward.setGoodsId(award.getAward());
//			roleAward.setGoodsNum(award.getNum());
//			roleAward.setBindType(award.getBindType());
//			roleAward.setBroadcastInfo(award.getBroadcastInfo());
//			role.getCompassAwardMap().get(id).add(roleAward);
//		}
//
//		// 记录日志
//		try {
//			GameContext.getStatLogApp().compassLog(role, id, count);
//		} catch (Exception e) {
//			this.logger.error("CompassApp.getCompassStopPlace error: ", e);
//		}
//		return result;
//	}
//
//	/**
//	 * 淘宝，获得罗盘停止位置列表<re>
//	 */
//	public byte[] getCompassTaobaoStopPlace(RoleInstance role, short id,
//			byte count) {
//		List<CompassRoleAward> awardList = role.getCompassAwardMap().get(id);
//		if (null == awardList) {
//			role.getCompassAwardMap()
//					.put(id, new ArrayList<CompassRoleAward>());
//		} else if (awardList.size() > 0) {
//			this.sendMailByCache(role, awardList);// 将之前的缓存奖励通过邮件发给用户
//			awardList.clear();// 清除之前的缓存奖励
//		}
//		byte[] result = new byte[count];// --
//		Compass compass = this.compassMap.get(id);
//		synchronized (compass) //
//		{
//			for (int i = 0; i < count; i++) {
//				CompassRoleAward award = compass.getAward();
//				result[i] = award.getPlace();
//				role.getCompassAwardMap().get(id).add(award);
//			}
//		}
//		try {
//			// 统计罗盘抽奖次数
//			GameContext.getCountApp().updateTaobao(role, id, count);
//			// 日志
//			GameContext.getStatLogApp().compassLog(role, id, count);
//		} catch (Exception e) {
//			this.logger.error("CompassApp.getCompassStopPlace error: ", e);
//		}
//		return result;
//	}
//
//	@Override
//	public void offline(RoleInstance role) {
//		for (List<CompassRoleAward> awardList : role.getCompassAwardMap()
//				.values()) {
//			if (Util.isEmpty(awardList)) {
//				continue;
//			}
//			try {
//				this.sendMailByCache(role, awardList);
//			} catch (Exception e) {
//				this.logger.error("CompassApp.offline error:" + e);
//			}
//		}
//	}
//
//	/**
//	 * 处理消息1098 返回罗盘id，类型Id，开始日期，结束日期,消耗列表{卷轴id,消耗金币数量，转动几次}以及奖池12个奖励物品
//	 */
//	@Override
//	public Message openCompassPanel(RoleInstance role, short id) {
//		try {
//			Compass compass = this.getCompass(id);
//			if (null == compass || !compass.isSuitLevel(role)
//					|| !compass.isTimeOpen()) {
//				return new C0003_TipNotifyMessage(
//						Status.Compass_Req_Param_Error.getTips());
//			}
//			C1908_CompassDisplayRespMessage message = new C1908_CompassDisplayRespMessage();
//			   
//			short sortId = compass.getSortId();
//			//日期格式在Compass.init()时已经验证
//			String startDateString = this.getCompass(id).getStartDateAbs();
//			String endDateString = this.getCompass(id).getEndDateAbs();
//			message.setId(id);
//			message.setSortId(sortId);
//			//发送的日期格式 2023-05-31
//			message.setStartDateAbs(startDateString);
//			message.setEndDateAbs(endDateString);
//
//			List<CompassConsumeItem> consumeList = Lists.newArrayList();
//			consumeList.addAll(compass.getConsumeMap().values());
//
//			List<GoodsLiteItem> displayList = new ArrayList<GoodsLiteItem>();
//			List<CompassAward> awardList = compass.getAwardList();
//			for (CompassAward award : awardList) {
//				GoodsBase goodsBase = award.getAwardGoods();
//				GoodsLiteItem item = goodsBase.getGoodsLiteItem();
//				item.setBindType(award.getBindType());
//				displayList.add(item);
//			}
//			message.setConsumeList(consumeList);
//			message.setDisplayList(displayList);
//			return message;
//		} catch (Exception e) {
//			this.logger.error("CompassApp.getCompassPanelMessage error: ", e);
//			return new C0003_TipNotifyMessage(
//					Status.Compass_Req_Param_Error.getTips());
//		}
//	}
//
//	@Override
//	public void start() {
//		this.loadCompassConfig();
//
//	}
//
//	@Override
//	public void stop() {
//
//	}
//
//}
