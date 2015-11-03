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
// * @date 2014-3-28 ����03:59:53
// * @version V1.0
// */
//public class CompassAppImpl implements CompassApp {
//	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	/**
//	 * �����б�KEY=����Id,VALUE=��Ӧ��Compass
//	 */
//	private Map<Short, Compass> compassMap = new LinkedHashMap<Short, Compass>();
//
//	/**
//	 * ������������EXCEL��
//	 * 
//	 * @author gaibaoning@moogame.cn
//	 * @date 2014-3-28 ����06:14:35
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
//				// ��ʼ����������,��֤������Ʒ
//				compass.init();
//				// ��������Ϣ����ӵ�Map
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
//	 * ����ֹͣ��Ĵ���
//	 * 
//	 * @param bean
//	 * @param sendByMail
//	 *            �Ƿ����ʼ�
//	 * @return
//	 * @author gaibaoning@moogame.cn
//	 * @date 2014-3-28 ����06:13:24
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
//	 * δ��ȡ�Ľ�������ɫ���ʼ�
//	 * 
//	 * @param role
//	 * @param awardList
//	 * @author gaibaoning@moogame.cn
//	 * @date 2014-3-28 ����06:44:17
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
//	 * �жϵõ����������� �����Ƿ���ڣ���ɫ�ȼ��Ƿ����㣬�ʱ���Ƿ��ǿ���״̬ �����Ա�����������
//	 */
//	@Override
//	public Result checkCondition(RoleInstance role, short id, byte count) {
//		  Result result = new Result();
//		  //��������
//		  Compass compass = this.getCompass(id);
//		  if (compass == null) {
//			  return result.setInfo(Status.Compass_Req_Param_Error.getTips());
//		  }
//		  if(compass.getSortId() == 1){
//			  
//		  }else{//�Ա���
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
//		  //����ת������                                    ���ָ�������ID�����ĵĽ��
//		  CompassConsumeItem compassConsumeItem = compass.getCompassConsumeItem(count);
//		  if (compassConsumeItem == null) {
//		      return result.setInfo(Status.Compass_Req_Param_Error.getTips());
//		  }
//		  //����ID
//		  int goodsId = compassConsumeItem.getGoodsId();
//		  // ת�����ĵĽ��
//		  int goldMoney = compassConsumeItem.getGoldMoney();
//		  //֧��ʹ�õ��ߵģ�����ʹ�õ��ߣ��޵��ߵ�����ý�ң�
//		  if (goodsId > 0) {
//			  //��ɫӵ�еĵ����������е���ʹ�õ��ߣ��м����ۼ���
//			  int roleGoodsNum = role.getRoleBackpack().countByGoodsId(goodsId);
//			  if (roleGoodsNum > 0) {
//				  GoodsResult goodsResult = GameContext.getUserGoodsApp().deleteForBag(role, goodsId, roleGoodsNum,
//		          OutputConsumeType.compass_consume);
//				  if (!goodsResult.isSuccess()) {// ���ɹ�
//					  return goodsResult;
//					  }
//				  } else 
//				  {
//					  // ���߲���
//					  int roleMoney = role.getGoldMoney();
//					  // ���ĵ�Ǯ��
//					  int expendMoney = goldMoney;
//					  if (roleMoney < expendMoney) {
//						  return result.setInfo(Status.Compass_Money_Not_Enough.getTips());
//					  }
//					  // ��Ǯ
//					  GameContext.getUserAttributeApp().changeRoleMoney(role,AttributeType.goldMoney, OperatorType.Decrease,expendMoney, OutputConsumeType.compass_consume);
//		              role.getBehavior().notifyAttribute();
//		              }
//			  } else {// ֻ֧�����Ľ�ҵ�
//				  int roleMoney = role.getGoldMoney();
//				  // ���ĵĽ����Ŀ
//				  int expendMoney = goldMoney;
////				  if (roleMoney < expendMoney) {//Ԫ������ <Ted>
////					  return result.setInfo(Status.Compass_Money_Not_Enough.getTips());
////				  }
////				  // ��Ǯ
////				  GameContext.getUserAttributeApp().changeRoleMoney(role,AttributeType.goldMoney, OperatorType.Decrease,expendMoney, OutputConsumeType.compass_consume);
////				  role.getBehavior().notifyAttribute();
//			  }
//		  return result.success();
//
//	}
//
//	/**
//	 * 1910 ����ҷ�������ȡ�ý����б�
//	 */
//	@Override
//	public Result compassStop(RoleInstance role, short id) {
//		Result result = new Result();
//		Compass compass = this.getCompass(id);
//		if (compass == null) {
//			return result.setInfo(Status.Compass_Not_Exist.getTips());
//		}
//		// ���������Ƿ�Ϊ��
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
//			// ��ϵͳ �㲥
//			try {
//				this.sendBroadcastInfo(role, award.getBroadcastInfo());
//			} catch (Exception e) {
//				this.logger.error("CompassApp.compassStop error: ", e);
//			}
//		}
//		// ��ս�������
//		roleAwardList.clear();
//		// �򱳰��������Ʒ
//		AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp()
//				.addSomeGoodsBeanForBag(role, addList,
//						OutputConsumeType.compass_output);
//
//		// �����������ʼ�
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
//		// ����֪ͨ��һ�ý���
//		List<GoodsOperateBean> putSuccessList = goodsResult.getPutSuccessList();
//		//ֱ����ӵ���������Ʒ
//		this.appendCompassStopItem(compassStopList, putSuccessList, false);
//		//�ʼ����͵���Ʒ
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
//	 * ������е������б�
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
//				// ��ɫ�ȼ�����ʱ�䲻��
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
//	 * 1909 �����������ֹͣλ���б� δͳ�Ƴ齱����
//	 */
//	@Override
//	public byte[] getCompassStopPlace(RoleInstance role, short id, byte count) {
//		// �����б��Ȼ�ȡ��ɫ���ϵĽ����б�
//		List<CompassRoleAward> awardList = role.getCompassAwardMap().get(id);
//		if (null == awardList) {
//			role.getCompassAwardMap()
//					.put(id, new ArrayList<CompassRoleAward>());
//		} else if (awardList.size() > 0) {
//			this.sendMailByCache(role, awardList);// ��֮ǰ�Ļ��潱��ͨ���ʼ������û�
//			awardList.clear();// ���֮ǰ�Ļ��潱��
//		}
//
//		// ��ý��
//		byte[] result = new byte[count];
//		Compass compass = this.compassMap.get(id);
//		// ���Ȩ���б�
//		HashMap<Integer, Integer> placeMap = compass.getPlaceMap();
//		// �齱�߼�
//		Set<Integer> resultSet = Util.getWeightCalct(count, placeMap);
//		Iterator<Integer> it = resultSet.iterator();
//
//		// ���ɽ����б���1910��
//		byte index = 0;
//		while (it.hasNext()) {
//			int value = it.next();
//			result[index++] = (byte) value;
//			// ���н���,��ɽ����б�
//			CompassAward award = compass.getAwardList().get(value);
//			CompassRoleAward roleAward = new CompassRoleAward();
//			roleAward.setId(id);// ���̣ɣ�
//			roleAward.setPlace((byte) value);// �齱�������0��ʼ
//			roleAward.setGoodsId(award.getAward());
//			roleAward.setGoodsNum(award.getNum());
//			roleAward.setBindType(award.getBindType());
//			roleAward.setBroadcastInfo(award.getBroadcastInfo());
//			role.getCompassAwardMap().get(id).add(roleAward);
//		}
//
//		// ��¼��־
//		try {
//			GameContext.getStatLogApp().compassLog(role, id, count);
//		} catch (Exception e) {
//			this.logger.error("CompassApp.getCompassStopPlace error: ", e);
//		}
//		return result;
//	}
//
//	/**
//	 * �Ա����������ֹͣλ���б�<re>
//	 */
//	public byte[] getCompassTaobaoStopPlace(RoleInstance role, short id,
//			byte count) {
//		List<CompassRoleAward> awardList = role.getCompassAwardMap().get(id);
//		if (null == awardList) {
//			role.getCompassAwardMap()
//					.put(id, new ArrayList<CompassRoleAward>());
//		} else if (awardList.size() > 0) {
//			this.sendMailByCache(role, awardList);// ��֮ǰ�Ļ��潱��ͨ���ʼ������û�
//			awardList.clear();// ���֮ǰ�Ļ��潱��
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
//			// ͳ�����̳齱����
//			GameContext.getCountApp().updateTaobao(role, id, count);
//			// ��־
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
//	 * ������Ϣ1098 ��������id������Id����ʼ���ڣ���������,�����б�{����id,���Ľ��������ת������}�Լ�����12��������Ʒ
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
//			//���ڸ�ʽ��Compass.init()ʱ�Ѿ���֤
//			String startDateString = this.getCompass(id).getStartDateAbs();
//			String endDateString = this.getCompass(id).getEndDateAbs();
//			message.setId(id);
//			message.setSortId(sortId);
//			//���͵����ڸ�ʽ 2023-05-31
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
