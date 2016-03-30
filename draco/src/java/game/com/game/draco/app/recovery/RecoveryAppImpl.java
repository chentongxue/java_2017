package com.game.draco.app.recovery;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.StringFormulaCell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hint.vo.HintType;
import com.game.draco.app.recovery.config.RecoveryConfig;
import com.game.draco.app.recovery.config.RecoveryConsumeConfig;
import com.game.draco.app.recovery.config.RecoveryConsumeHungUpConfig;
import com.game.draco.app.recovery.config.RecoveryHangUpExpConfig;
import com.game.draco.app.recovery.config.RecoveryOutPutConfig;
import com.game.draco.app.recovery.domain.RoleRecovery;
import com.game.draco.app.recovery.logic.IRecoveryLogic;
import com.game.draco.app.recovery.type.RecoveryConsumeType;
import com.game.draco.app.recovery.type.RecoveryType;
import com.game.draco.app.recovery.vo.RecoveryResult;
import com.game.draco.message.item.RecoveryConsumeItem;
import com.game.draco.message.item.RecoveryShowItem;
import com.game.draco.message.request.C1927_RecoveryAllReqMessage;
import com.game.draco.message.response.C1925_RecoveryPanelRespMessage;
import com.game.draco.message.response.C1926_RecoveryRespMessage;
import com.game.draco.message.response.C1928_RecoveryInfoRespMessage;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * 一键追回 每次玩家当天第一次登陆计算一键追回的数值，并存储
 */
public class RecoveryAppImpl implements RecoveryApp {

	private static Logger logger = LoggerFactory
			.getLogger(RecoveryAppImpl.class);
	private final short RECOVERY_ALL_CMDID = new C1927_RecoveryAllReqMessage()
			.getCommandId();
	private final int DAY_SECONDS_ALL = 24*60*60;
	private Map<String, RecoveryConfig> recoveryConfigMap;
	private Map<String, RecoveryConsumeConfig> consumeConfigMap;
	private Map<String, RecoveryConsumeHungUpConfig> consumeHungUpConfigMap;
	private Map<String, RecoveryOutPutConfig> outPutConfigMap;
	// 消耗 key: id_consumeType
	private Multimap<String, RecoveryConsumeHungUpConfig> consumeHungUpConfigMultiMap = ArrayListMultimap
			.create();
	private Multimap<String, RecoveryConsumeConfig> consumeConfigMultiMap = ArrayListMultimap
			.create();
	// 产出 key: id_outputType
	private Multimap<String, RecoveryOutPutConfig> outPutConfigMultiMap = ArrayListMultimap
			.create();
	// <K,V>: roleId <recoveryId, RoleOneKeyRecovery> 玩家一键追回
	private Map<String, Map<String, RoleRecovery>> roleRecoveryMap = Maps
			.newConcurrentMap();

	private Map<Integer, RecoveryHangUpExpConfig> hangUpExpConfigMap;
	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		loadRecoveryConfigMap();
		loadRecoveryConsumeMap();
		loadRecoveryConsumeHungUpMap();
		loadRecoveryOutPutMap();
		loadRecoveryHangupExpConfigMap();
	}

	private void loadRecoveryHangupExpConfigMap() {
		hangUpExpConfigMap = loadConfigMap(XlsSheetNameType.recovery_exp_hang_up_config,
				RecoveryHangUpExpConfig.class, true);
		for(RecoveryHangUpExpConfig cf : hangUpExpConfigMap.values()){
			cf.init();
		}
	}
	
	public int getHangUpExpValue(int roleLevel){
		RecoveryHangUpExpConfig cf = hangUpExpConfigMap.get(roleLevel);
		if(cf == null){
			return 0;
		}
		return cf.getExp();
	}
	private void loadRecoveryConfigMap() {
		recoveryConfigMap = loadConfigMap(XlsSheetNameType.recovery_config,
				RecoveryConfig.class, true);
		checkConfigMap(recoveryConfigMap);
		checkParams(recoveryConfigMap);
	}
	
	private void checkParams(final Map<String, RecoveryConfig> map) {
		Set<String> set = new HashSet<String>();
		for (Map.Entry<String, RecoveryConfig> entry : map.entrySet()) {
			RecoveryConfig rcf = entry.getValue();
			String key = rcf.getParamKey();
			if (set.contains(key)) {
				checkFail("recoveryApp checkParams fail: RecoveryConfig have the same [recoveryType,param]"
						+ "=[+"
						+ rcf.getRecoveryType()
						+ ","
						+ rcf.getParam()
						+ "]"
						+ "plz check recovery.xls -> recovery_config sheet");
			}
			set.add(rcf.getParamKey());
		}
		set = null;
	}

	private void loadRecoveryConsumeMap() {
		consumeConfigMap = loadConfigMap(
				XlsSheetNameType.recovery_consume_config,
				RecoveryConsumeConfig.class, true);
		checkConfigMap(consumeConfigMap);
		consumeConfigMultiMap = buildMultiMap(consumeConfigMultiMap,
				consumeConfigMap.values());

	}

	private void loadRecoveryConsumeHungUpMap() {
		consumeHungUpConfigMap = loadConfigMap(
				XlsSheetNameType.recovery_hungup_consume_config,
				RecoveryConsumeHungUpConfig.class, true);
		checkConfigMap(consumeHungUpConfigMap);
		consumeHungUpConfigMultiMap = buildMultiMap(
				consumeHungUpConfigMultiMap, consumeHungUpConfigMap.values());
	}

	private void loadRecoveryOutPutMap() {
		outPutConfigMap = loadConfigMap(
				XlsSheetNameType.recovery_output_config,
				RecoveryOutPutConfig.class, true);
		checkConfigMap(outPutConfigMap);
		outPutConfigMultiMap = buildMultiMap(outPutConfigMultiMap,
				outPutConfigMap.values());
	}

	private <K, V extends MultiKeySupport<K>> Multimap<K, V> buildMultiMap(
			Multimap<K, V> multiMap, final Collection<V> values) {
		if (Util.isEmpty(multiMap)) {
			multiMap = ArrayListMultimap.create();
		}
		for (V cf : values) {
			multiMap.put(cf.getMultiKey(), cf);
		}
		return multiMap;
	}

	// 检验配置
	private void checkConfigMap(Map<String, ? extends IRecoveryInitable> map) {
		for (IRecoveryInitable cf : map.values()) {
			cf.init();
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public int onCleanup(String roleId, Object context) {
		roleRecoveryMap.remove(roleId);
		return 1;
	}

	// 登录读取玩家数据库
	@Override
	public int onLogin(RoleInstance role, Object context) {
		// 玩家数据
		List<RoleRecovery> list = GameContext.getBaseDAO().selectList(
				RoleRecovery.class, RoleRecovery.ROLE_ID, role.getRoleId());
		if (Util.isEmpty(list)) {
			return 0;
		}
		// 如果是昨天记录的(当天记录的是前一天的)则删掉,反之添加到缓存,
		Date now = new Date();
		for (RoleRecovery rc : list) {
			if (DateUtil.dateDiffDay(rc.getUpdateTime(), now) >= 1) {
				deleteRecovery(rc);
				continue;
			}
			putRecoveryMap(rc);
		}
		return 1;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		// 玩家数据下线的时候存库
		try {
			Map<String, RoleRecovery> map = roleRecoveryMap.remove(role.getRoleId());
			if (Util.isEmpty(map)) {
				return 1;
			}
			for (Map.Entry<String, RoleRecovery> entry : map.entrySet()) {
				RoleRecovery rc = entry.getValue();
				saveUpdateDb(rc);
			}
		} catch (Exception e) {
			logger.error("recovery.offline error:", e);
			return 0;
		}
		return 1;
	}

	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName,
				clazz, linked);
		if (Util.isEmpty(map)) {
			checkFail("not config the " + clazz.getSimpleName() + " ,file="
					+ sourceFile + " sheet=" + sheetName);
		}
		return map;
	}

	private void checkFail(String info) {
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}

	// 更新缓存和数据库
	@Override
	public void saveUpdateRecovery(RoleRecovery recovery) {
		putRecoveryMap(recovery);
		saveUpdateDb(recovery);
	}

	@Override
	public Message openRecoveryPanel(RoleInstance role) {
		// 处理如果登录和打开面板之间有跨越凌晨0点的情况
		C1925_RecoveryPanelRespMessage msg = new C1925_RecoveryPanelRespMessage();
		String roleId = role.getRoleId();
		// 获取玩家的一键追回
		Collection<RoleRecovery> recoverys = getRoleRecoveryList(roleId);
		//清除昨天之前记录的数据
		Date now = new Date();
		for (Iterator<RoleRecovery> iterator = recoverys.iterator(); iterator.hasNext();) {
			RoleRecovery rc = iterator.next();
			if (!DateUtil.sameDay(rc.getUpdateTime(), now)) {
				deleteRecovery(rc);
				iterator.remove();
			}
		}
		List<RoleRecovery> recoveryList = Lists.newArrayList(recoverys);
		Collections.sort(recoveryList);
		// 获得列表
		List<RecoveryShowItem> list = buildRecoveryShowItemList(role, recoveryList,
				false);

		msg.setList(list);

		// 获得钻石消耗[总]
		int diamonds = getRecoveryAllConsumeValue(role, recoveryList,
				RecoveryConsumeType.RECOVERY_CONSUME_DIAMODNS.getType());
		msg.setDiamonds(diamonds);

		return msg;
	}

	@Override
	public boolean hasFreeRecovery(RoleInstance role) {
		String roleId = role.getRoleId();
		Collection<RoleRecovery> recoverys = getRoleRecoveryList(roleId);
		if (Util.isEmpty(recoverys)) {
			return false;
		}
		return isRecoveryConsumeAvailable(role, recoverys,
				RecoveryConsumeType.RECOVERY_CONSUME_FREE.getType());
	}
	
	/**
	 * 获得一件追回的项目列表
	 * @param recoverys
	 * @param canEmpty
	 * @date 2015-1-15 下午08:09:56
	 */
	private List<RecoveryShowItem> buildRecoveryShowItemList(RoleInstance role, Collection<RoleRecovery> recoverys, boolean canEmpty) {
		List<RecoveryShowItem> list = Lists.newArrayList();
		for (RoleRecovery rc : recoverys) {
			if (!canRecovery(rc) && !canEmpty) {
				continue;
			}
			String id = rc.getRecoveryId();
			RecoveryConfig cf = getRecoveryConfig(id);
			if (cf == null) {
				continue;
			}
			RecoveryType rType = RecoveryType.getType(cf.getRecoveryType());
			if (rType == null) {
				return null;
			}
			byte vipLevel = GameContext.getVipApp().getVipLevel(role);
			RoleRecovery recovery = getRoleRecovery(role, id);
			if (recovery == null) {
				// C0003_TipNotifyMessage tips = new
				// C0003_TipNotifyMessage(getText(TextId.RECOVERY_REWARD_BY_EMAIL_TIPS));
				// role.getBehavior().sendMessage(tips);
				continue;
			}
			RecoveryShowItem it = rType.createRecoveryLogic().getRecoveryShowItem(cf, recovery, vipLevel);
			if(it != null){
				list.add(it);
			}
		}
		return list;
	}

	private boolean canRecovery(RoleRecovery rc) {
		RecoveryConfig rcf = getRecoveryConfig(rc.getRecoveryId());
		if (rcf == null) {
			return false;
		}
		RecoveryType rType = RecoveryType.getType(rcf.getRecoveryType());
		if (rType == null) {
			return false;
		}
		return rType.createRecoveryLogic().canRecovery(rc);
	}

	@Override
	public RecoveryConfig getRecoveryConfig(String id) {
		return recoveryConfigMap.get(id);
	}

	private RecoveryConfig getRecoveryConfigByTypeAndParam(byte recoveryType,
			String param) {
		String paramKey = String.valueOf(recoveryType) + Cat.underline + param;
		for (Map.Entry<String, RecoveryConfig> entry : recoveryConfigMap
				.entrySet()) {
			RecoveryConfig rcf = entry.getValue();
			if (rcf.getParamKey().equals(paramKey)) {
				return rcf;
			}
		}
		return null;
	}

	// -1926 一次一键追回
	@Override
	public Message recoveryAward(RoleInstance role, String id, byte consumeType) {
		C1926_RecoveryRespMessage msg = new C1926_RecoveryRespMessage();
		RecoveryConfig rcf = getRecoveryConfig(id);
		if (rcf == null) {
			return null;
		}
		RecoveryType rType = RecoveryType.getType(rcf.getRecoveryType());
		if (rType == null) {
			return null;
		}
		RecoveryResult rt = rType.createRecoveryLogic()
				.recoveryAwardAndConsume(role, id, consumeType, 1);
		msg.setType(rt.getResult());
		msg.setInfo(rt.getInfo());
		// 红点提示
		if (!this.hasFreeRecovery(role)) {
			GameContext.getHintApp().hintChange(role, HintType.recovery, false);
		}
		return msg;
	}

	// -1926
	// recoveryType 0,免费一键追回; 1,钻石一键追回
	@Override
	public RecoveryResult recoveryAllAwards(RoleInstance role,
			byte consumeType, byte confirm) {
		RecoveryResult result = new RecoveryResult();
		RecoveryConsumeType conType = RecoveryConsumeType.getType(consumeType);
		if (conType == null) {
			result.setInfo(getText(TextId.RECOVERY_CONSUME_PARAM_ERROR));
			return result.failure();
		}

		String roleId = role.getRoleId();
		// 获取玩家的一键追回
		Collection<RoleRecovery> recoverys = getRoleRecoveryList(roleId);

		// 消耗
		AttributeType attr = conType.getAttributeType();
		if (attr != null) {
			// 获得钻石消耗[总]
			int consumeValue = getRecoveryAllConsumeValue(role, recoverys,
					consumeType);
			if (consumeValue <= 0) {
				result.setInfo(GameContext.getI18n().messageFormat(
						TextId.RECOVERY_ONE_KEY_CONSUME_NONE_TIPS,
						attr.getName()));
				return result.failure();
			}
			// 二次确认
			if (confirm == 0) {
				confirm = 1;
				String tips = GameContext.getI18n().messageFormat(
						TextId.RECOVERY_ALL_CONFIRM_TIPS, attr.getName(),
						consumeValue);
				Message notifyMsg = QuickCostHelper.getMessage(role,
						RECOVERY_ALL_CMDID, conType.getType() + "," + confirm,
						(short) 0, "", consumeValue, 0, tips);
				role.getBehavior().sendMessage(notifyMsg);
				return result.ignore();
			}

			// 【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role,
					attr, consumeValue);
			if (ar.isIgnore()) {// 弹板
				return result.ignore();
			}
			if (!ar.isSuccess()) {// 不足
				result.setInfo(ar.getInfo());
				return result.failure();
			}
			// 消耗
			GameContext.getUserAttributeApp().changeRoleMoney(role, attr,
					OperatorType.Decrease, consumeValue,
					OutputConsumeType.recover_consume);
		}

		// 追回资源
		for (RoleRecovery rc : recoverys) {
			String id = rc.getRecoveryId();
			RecoveryConfig cf = getRecoveryConfig(id);
			if (cf == null) {
				continue;
			}
			RecoveryType rType = RecoveryType.getType(cf.getRecoveryType());
			if (rType == null) {
				return null;
			}
			rType.createRecoveryLogic().recoveryAward(role, id, consumeType,
					rc.getRecoveryNum());
		}
		// 红点提示
		if (!this.hasFreeRecovery(role)) {
			GameContext.getHintApp().hintChange(role, HintType.recovery, false);
		}
		return result.success();
	}

	private int getRecoveryAllConsumeValue(RoleInstance role,
			Collection<RoleRecovery> recoverys, byte consumeType) {
		RecoveryConfig rcf;
		int consumption = 0;
		for (RoleRecovery rc : recoverys) {
			rcf = getRecoveryConfig(rc.getRecoveryId());
			if (rcf == null) {
				continue;
			}
			RecoveryType rType = RecoveryType.getType(rcf.getRecoveryType());
			if (rType == null) {
				continue;
			}
			IRecoveryLogic logic = rType.createRecoveryLogic();
			if (logic == null) {
				continue;
			}
			int rConsumption = logic.getRecoveryAwardConsumeValue(role,
					rc.getRecoveryId(), consumeType);
			consumption += rc.getRecoveryNum() * rConsumption;

		}
		return consumption;
	}

	// 是否存在至少一种 某种消耗类型的“一键追回”
	private boolean isRecoveryConsumeAvailable(RoleInstance role,
			Collection<RoleRecovery> recoverys, byte consumeType) {
		for (RoleRecovery rc : recoverys) {
			if (!canRecovery(rc)) {
				continue;
			}
			byte vipLevel = GameContext.getVipApp().getVipLevel(role);
			// 消耗配置
			RecoveryConsumeConfig consumeCf = GameContext.getRecoveryApp()
					.getRoleRecoveryConsumeConfig(rc.getRecoveryId(),
							consumeType, vipLevel);
			if (consumeCf != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 1928 点击 "一键追回"面板中的“追回”，弹出的详情
	 */
	@Override
	public Message openRecoveryInfo(RoleInstance role, String id) {
		C1928_RecoveryInfoRespMessage msg = new C1928_RecoveryInfoRespMessage();

		RecoveryConfig rcf = getRecoveryConfig(id);
		if (rcf == null) {
			return null;
		}
		RecoveryType rType = RecoveryType.getType(rcf.getRecoveryType());
		if (rType == null) {
			return null;
		}
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		RoleRecovery recovery = getRoleRecovery(role, id);
		if (recovery == null) {
			// C0003_TipNotifyMessage tips = new
			// C0003_TipNotifyMessage(getText(TextId.RECOVERY_REWARD_BY_EMAIL_TIPS));
			// role.getBehavior().sendMessage(tips);
			return null;
		}
		List<RecoveryConsumeItem> list = rType.createRecoveryLogic()
				.getRecoveryConsumeItemList(recovery, vipLevel);
		msg.setList(list);
		return msg;
	}

	private String getText(String textId) {
		return GameContext.getI18n().getText(textId);
	}

	@Override
	public Collection<RecoveryOutPutConfig> getRecoveryOutPutConfigs(String id,
			int roleLevel) {
		Collection<RecoveryOutPutConfig> outPutCfs = outPutConfigMultiMap
				.get(id);
		if (Util.isEmpty(outPutCfs)) {
			return null;
		}
		Collection<RecoveryOutPutConfig> rtList = Lists.newArrayList();
		for (RecoveryOutPutConfig cf : outPutCfs) {
			if (cf.meetCondition(roleLevel)) {
				rtList.add(cf);
			}
		}
		return rtList;
	}

	@Override
	public Collection<RecoveryConsumeConfig> getConsumeConfigsByRecoveryId(
			String id) {
		Collection<RecoveryConsumeConfig> consumeCfs = consumeConfigMultiMap
				.get(id);
		return consumeCfs;
	}

	private RoleRecovery selectRecovery(RoleInstance role, String id) {
		RoleRecovery rc = GameContext.getBaseDAO().selectEntity(
				RoleRecovery.class, RoleRecovery.ROLE_ID, role.getRoleId(),
				RoleRecovery.RECOVER_ID, id);
		if (rc == null) {
			return null;
		}
		putRecoveryMap(rc);
		return rc;
	}

	private int deleteRecovery(RoleRecovery rc) {
		return GameContext.getBaseDAO().delete(RoleRecovery.class,
				RoleRecovery.ROLE_ID, rc.getRoleId(), RoleRecovery.RECOVER_ID,
				rc.getRecoveryId());
	}

	private void putRecoveryMap(RoleRecovery recovery) {
		if (recovery == null) {
			return;
		}
		Map<String, RoleRecovery> map = roleRecoveryMap.get(recovery
				.getRoleId());
		if (Util.isEmpty(map)) {
			map = Maps.newConcurrentMap();
			roleRecoveryMap.put(recovery.getRoleId(), map);
		}
		map.put(recovery.getKey(), recovery);
	}


	@Override
	public RoleRecovery getRoleRecovery(RoleInstance role, String id) {
		RoleRecovery recovery = getRoleRecoveryFromMap(role, id);
		if (recovery == null) {
			recovery = selectRecovery(role, id);
		}
		return recovery;
	}

	private RoleRecovery getRoleRecoveryFromMap(RoleInstance role, String id) {
		String roleId = role.getRoleId();
		return getRoleRecoveryFromMap(roleId, id);
	}

	private RoleRecovery getRoleRecoveryFromMap(String roleId, String id) {
		Map<String, RoleRecovery> map = roleRecoveryMap.get(roleId);
		if (Util.isEmpty(map)) {
			return null;
		}
		return map.get(roleId + Cat.underline + id);
	}

	private Collection<RoleRecovery> getRoleRecoveryList(String roleId) {
		Map<String, RoleRecovery> map = roleRecoveryMap.get(roleId);
		if (Util.isEmpty(map)) {
			return Lists.newArrayList();
		}
		return map.values();
	}


	@Override
	public RecoveryConsumeConfig getRecoveryConsumeConfig(String recoveryId,
			byte consumeType) {
		String key = recoveryId + Cat.underline + consumeType;
		return consumeConfigMap.get(key);
	}

	@Override
	public RecoveryConsumeConfig getRoleRecoveryConsumeConfig(
			String recoveryId, byte consumeType, byte vipLevel) {
		RecoveryConsumeConfig recoveryConsume = getRecoveryConsumeConfig(
				recoveryId, consumeType);
		if (recoveryConsume == null) {
			logger.error("one key recovery [recoveryId = "
					+ recoveryId
					+ ", consumeType = "
					+ consumeType
					+ "] "
					+ "cant find its config in recovery.xls -> recovery_consume sheet");
			return null;
		}
		if (recoveryConsume.meetCondition(vipLevel)) {
			return recoveryConsume;
		}
		return null;
	}

	@Override
	public RecoveryConsumeHungUpConfig getRecoveryConsumeHungUpConfig(
			byte consumeType, int roleLevel) {

		String key = String.valueOf(consumeType);
		Collection<RecoveryConsumeHungUpConfig> cfs = consumeHungUpConfigMultiMap
				.get(key);
		if (Util.isEmpty(cfs)) {
			return null;
		}
		for (RecoveryConsumeHungUpConfig cf : cfs) {
			if (cf.meetCondition(roleLevel)) {
				return cf;
			}
		}
		return null;
	}

	private void saveUpdateDb(RoleRecovery recovery) {
		GameContext.getBaseDAO().saveOrUpdate(recovery);
	}

	private void saveRoleRecovery(RoleInstance role, String recoveryId,
			int roleLevel, int maxNum, String data) {
		RoleRecovery hrc = getRoleRecovery(role, recoveryId);
		if (hrc != null) {
//			if (0 == DateUtil.dateDiffDay(hrc.getUpdateTime(), new Date())) {// 有记录切今天记录过
			if (DateUtil.sameDay(hrc.getUpdateTime(), new Date())) {// 有记录切今天记录过
				return;
			}
		}

		RoleRecovery recovery = new RoleRecovery();
		recovery.setData(data);

		recovery.setNum(0);
		recovery.setMaxNum(maxNum);
		recovery.setRecoveryId(recoveryId);
		recovery.setRoleId(role.getRoleId());
		recovery.setRoleLevel(roleLevel);
		saveUpdateRecovery(recovery);
	}

	private boolean isCreateDate(RoleInstance role){
		return DateUtil.sameDay(role.getCreateTime(), new Date());
	}
	

	private String getRecoveryIdByTypeAndParam(byte recoveryType, String param) {
		RecoveryConfig cf = getRecoveryConfigByTypeAndParam(recoveryType, param);
		//策划删表等
		if (cf == null) {
			logger.info("recovery RecoveryConfig() err: get recovery configure is null, [recoveryType, param] is ["
					+ recoveryType + "," + param + "]");
			return null;
		}
		return cf.getId();
	}
	
	@Override
	public void saveHungUpRecovery(RoleInstance role, int onlineSeconds) {
		if(isCreateDate(role)){
			return;
		}
		String recoveryId = getRecoveryIdByTypeAndParam(RecoveryType.HUNG_UP_EXP.getType(), "");
		if(Util.isEmpty(recoveryId)){
			return;
		}
		// if(exp <= 0){
		// return;
		// }
		RoleRecovery hrc = getRoleRecovery(role, recoveryId);
		if (hrc != null) {
			if (DateUtil.sameDay(hrc.getUpdateTime(), new Date())){
				return;
			}
		}
		RecoveryHangUpExpConfig expCf =	hangUpExpConfigMap.get(role.getLevel());
		if(expCf == null || expCf.getExp() == 0){
			return;
		}
		int expMax = expCf.getExp();
		int exp = expMax * onlineSeconds / DAY_SECONDS_ALL;
		if(exp <=0 ){
			return;
		}
		String data = exp + Cat.comma + expMax;
		RoleRecovery recovery = new RoleRecovery();
		recovery.setData(data);
		recovery.setMaxNum(1);
		recovery.setRecoveryId(recoveryId);
		recovery.setRoleId(role.getRoleId());
		recovery.setRoleLevel(role.getLevel());
		saveUpdateRecovery(recovery);
	}

	@Override
	public void saveCopyRecovery(RoleInstance role, int copyNum, short copyId) {
		if(isCreateDate(role)){
			return;
		}
		RecoveryConfig rcf = getRecoveryConfigByTypeAndParam(
				RecoveryType.COPY.getType(), String.valueOf(copyId));
		if (rcf == null) {
			return;
		}
		saveRoleRecovery(role, rcf.getId(), role.getLevel(), copyNum, null);
	}

	@Override
	public void saveArenaRecovery(RoleInstance role, int copyNum) {
		if(isCreateDate(role)){
			return;
		}
		String recoveryId = getRecoveryIdByTypeAndParam(
				RecoveryType.ARENA_RECOVERY.getType(), "");
		if(Util.isEmpty(recoveryId)){
			return;
		}
		saveRoleRecovery(role, recoveryId, role.getLevel(), copyNum, null);
	}

	@Override
	public void saveBossKillRecovery(RoleInstance role, int copyNum) {
		if(isCreateDate(role)){
			return;
		}
		String recoveryId = getRecoveryIdByTypeAndParam(
				RecoveryType.BOSS_KILL.getType(), "");
		if(Util.isEmpty(recoveryId)){
			return;
		}
		saveRoleRecovery(role, recoveryId, role.getLevel(), copyNum, null);

	}

	@Override
	public void saveCampBattleRecovery(RoleInstance role, int copyNum) {
		if(isCreateDate(role)){
			return;
		}
		String recoveryId = getRecoveryIdByTypeAndParam(
				RecoveryType.CAMP_BATTLE.getType(), "");
		if(Util.isEmpty(recoveryId)){
			return;
		}
		saveRoleRecovery(role, recoveryId, role.getLevel(), copyNum, null);

	}

	@Override
	public void saveDailyQuestRecovery(RoleInstance role, int copyNum) {
		if(isCreateDate(role)){
			return;
		}
		String recoveryId = getRecoveryIdByTypeAndParam(
				RecoveryType.DAILY_QUEST.getType(), "");
		if(Util.isEmpty(recoveryId)){
			return;
		}
		saveRoleRecovery(role, recoveryId, role.getLevel(), copyNum, null);
	}

	@Override
	public void saveAngelChestRecovery(RoleInstance role, int copyNum) {
		if(isCreateDate(role)){
			return;
		}
		String recoveryId = getRecoveryIdByTypeAndParam(
				RecoveryType.ANGEL_CHEST.getType(), "");
		if(Util.isEmpty(recoveryId)){
			return;
		}
		saveRoleRecovery(role, recoveryId, role.getLevel(), copyNum, null);
	}

	@Override
	public Set<HintType> getHintTypeSet(RoleInstance role) {
		if (this.hasFreeRecovery(role)) {
			Set<HintType> set = Sets.newHashSet();
			set.add(HintType.recovery);
			return set;
		}
		return null;
	}

}
