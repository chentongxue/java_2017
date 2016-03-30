package com.game.draco.app.luckybox;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.app.goods.behavior.result.AddGoodsBeanResult;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.KeySupport;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;
import com.game.draco.GameContext;
import com.game.draco.app.luckybox.config.LuckyBoxAppConfig;
import com.game.draco.app.luckybox.config.LuckyBoxDiamandsConsumeConfig;
import com.game.draco.app.luckybox.config.LuckyBoxOddsConfig;
import com.game.draco.app.luckybox.config.LuckyBoxRewardPoolConfig;
import com.game.draco.app.luckybox.config.LuckyBoxVipTimesConfig;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.LuckeyBoxItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C1915_LuckyBoxDisplayRespMessage;
import com.game.draco.message.response.C1916_LuckyBoxPlayRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
/**
 * 幸运宝箱，属性值value类型int,物品个数num short
 * ①每日每轮用户打开奖励面板结果一样
 * ②每日额限次日清零
 * ③每次点击宝箱，取奖池信息
 */
public class LuckyBoxAppImpl  implements LuckyBoxApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final static int NORMAL_AWARD_POOL_SIZE = 7 ;
	private final static int AWARD_POOL_SIZE = 8 ;
	private final static int AWARD_TYPE_GOODS = 1 ;
	private final static int AWARD_TYPE_ATTRIBUTE = 2 ;
	
	//缓存，K:角色Id,VALUE:每日的当前轮8个宝箱
	private Map<String,LinkedHashMap<String, LuckyBoxPoolItem>> roleAwardPoolcacheMap = Maps.newConcurrentMap();
	LuckyBoxAppConfig luckyBoxAppConfig = new LuckyBoxAppConfig();
	//幸运宝箱VIP每日可以打开次数
	private Map<String, LuckyBoxVipTimesConfig> luckyBoxVipTimesMap =  Maps.newHashMap();
	//奖池映射 KEY:vipLevel_id VALUE:奖品配置
	private Map<String, LuckyBoxRewardPoolConfig> luckyBoxRewardPoolMap =  Maps.newHashMap();
	//付费消耗 KEY：付费玩的次数，VALUE消耗  
	private Map<String, LuckyBoxDiamandsConsumeConfig> luckyBoxDiamandsConsumeMap =  Maps.newHashMap();
	//已生成奖池权重配置 Key:vipLevel_times VALUE:权重
	private Map<String, LuckyBoxOddsConfig> luckyBoxLuckyOddsMap =  Maps.newHashMap();
	//用于生成每轮专有奖池的权重 K:vipLevel, VALUE：<K:ID,VALUE:权重>
	private Map<String, Map<String,Integer>> luckyBoxLuckyOdds4GeneratePoolMap =  Maps.newLinkedHashMap();
	@Override
	public void setArgs(Object arg0) {
		
	}

	@Override
	public void start() {
		loadAllConfig();
	}

	@Override
	public void stop() {
		
	}
	private void loadAllConfig(){
		this.loadVipTimesConfig();
		this.loadRewardPoolConfig();
		this.loadDiamandsConsumeConfig();
		this.loadOddsConfig();
		this.loadLuckyBoxAppConfig();
		this.generateLuckyBoxLuckyOdds4GeneratePoolMap();
		
	}
	private void loadVipTimesConfig(){
		luckyBoxVipTimesMap = loadConfigMap(
				XlsSheetNameType.LuckyBoxVipTimesConfig,
				LuckyBoxVipTimesConfig.class, false);
		
	}
	private void loadRewardPoolConfig(){
		luckyBoxRewardPoolMap = loadConfigMap(
				XlsSheetNameType.LuckyBoxRewardPoolConfig,
				LuckyBoxRewardPoolConfig.class, false);
		
	}
	private void loadDiamandsConsumeConfig(){
		luckyBoxDiamandsConsumeMap = loadConfigMap(
				XlsSheetNameType.LuckyBoxDiamandsConsumeConfig,
				LuckyBoxDiamandsConsumeConfig.class, false);
		
	}
	private void loadOddsConfig(){
		luckyBoxLuckyOddsMap = loadConfigMap(
				XlsSheetNameType.LuckyOddsConfig,
				LuckyBoxOddsConfig.class, false);
	}
	private void loadLuckyBoxAppConfig(){
		String fileName = XlsSheetNameType.LuckyBoxAppConfig.getXlsName();
		String sheetName = XlsSheetNameType.LuckyBoxAppConfig.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		luckyBoxAppConfig = XlsPojoUtil.getEntity(sourceFile, sheetName, LuckyBoxAppConfig.class);
		if (luckyBoxAppConfig.getRoundsCount()<=0) {
			Log4jManager.CHECK.error("loadLuckyBoxAppConfig failed " + luckyBoxAppConfig.getClass().getSimpleName()
					+ " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
	}
	/**
	 * <vipLevel,<id,odds>> vipLevel = normal为普通奖品1~8
	 */
	private void generateLuckyBoxLuckyOdds4GeneratePoolMap(){
		List<LuckyBoxRewardPoolConfig> viplist = loadConfigList(XlsSheetNameType.LuckyBoxRewardPoolConfig,LuckyBoxRewardPoolConfig.class);
		for (LuckyBoxRewardPoolConfig rewardConfig : viplist) {
			String vipLevel = rewardConfig.getVipLevel();//vipLevel∈{0,1,2,...12,normal}
			Map<String, Integer> map = getLuckyBoxLuckyOddsMap(vipLevel);
			addLuckyBoxLuckyOddsMap(map,rewardConfig);
		}
	}
	private <T> Map<String, Integer> getLuckyBoxLuckyOddsMap(T vipLevel){
		String vipLevelStr = vipLevel+"";//vipLevel∈{0,1,2,...12,normal}
		Map<String, Integer> map = luckyBoxLuckyOdds4GeneratePoolMap.get(vipLevelStr);
		return map;
	}
	private <T> Map<String, Integer> getLuckyBoxLuckyOddsNormalMap(){
		String vipLevelStr = "normal";
		Map<String, Integer> map = luckyBoxLuckyOdds4GeneratePoolMap.get(vipLevelStr);
		return map;
	}
	private void addLuckyBoxLuckyOddsMap(Map<String, Integer> map,LuckyBoxRewardPoolConfig rewardConfig){
		String vipLevelStr = rewardConfig.getVipLevel()+"";
		String idStr = rewardConfig.getId()+"";
		int odds = rewardConfig.getOdds();
		if(map == null){
			map =  new HashMap<String, Integer>();
			luckyBoxLuckyOdds4GeneratePoolMap.put(vipLevelStr,map);
		}
		map.put(idStr, odds);
	}
	private <T> List<T>  loadConfigList(XlsSheetNameType xls,Class<T> t){
		List<T> list = null;
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		try {
			list = XlsPojoUtil.sheetToList(sourceFile, sheetName,
					t);
		} catch (Exception e) {
			Log4jManager.CHECK.error("load "+t.getSimpleName()+" error:fileName=" + fileName+ ",sheetName=" + sheetName);
			Log4jManager.checkFail();
			
		}
		if(list == null){
			Log4jManager.CHECK.error("load "+t.getSimpleName()+" error: result is null fileName=" + fileName+ ",sheetName=" + sheetName);
			Log4jManager.checkFail();
		}
		return list;
	}
	private <K, V extends KeySupport<K>> Map<K, V> loadConfigMap(
			XlsSheetNameType xls, Class<V> clazz, boolean linked) {
		String fileName = xls.getXlsName();
		String sheetName = xls.getSheetName();
		String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
		Map<K, V> map = XlsPojoUtil.sheetToGenericMap(sourceFile, sheetName, clazz, linked);
		if (Util.isEmpty(map)) {
			Log4jManager.CHECK.error("not config the " + clazz.getSimpleName()
					+ " ,file=" + sourceFile + " sheet=" + sheetName);
			Log4jManager.checkFail();
		}
		return map;
	}
	/**
	 * 打开幸运宝箱面板,refreshFlag = 1时获得下一轮幸运宝箱
	 * ①取得rolecount,获得存贮的已用轮次，奖号-数量Map，奖号-开启位置Map
	 * ②根据roleId取当日轮次缓存，取不到则从roleCount信息恢复。得到当前轮奖池（1-8）
	 *    |---④存贮,同步roleCount
	 * ③生成Message
	 */
	@Override
	public Message openLuckyBoxPanel(RoleInstance role, byte refreshFlag) {
		//剩余轮次
		int remainRounds = 0;
		//还可以打开的箱子个数
		int openableTimes = 0;
		Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap =  Maps.newLinkedHashMap();
		
		RoleCount rc = role.getRoleCount();
		rc.resetDay();
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		//已用轮次
		int usedRounds  = rc.getLuckyBoxUsedTimes();
		int openedBoxTimes = getOpenedBoxTimes(role);//已经打开箱子的个数
		
		int vipTimes = getConfigVipTimes(vipLevel);//luckyBoxVipTimesMap.get(vipLevel+"").getVipTimes();
		int roundsLimit = luckyBoxAppConfig.getRoundsCount();
		if(refreshFlag == 1){//1为见好就收
			if(usedRounds>=roundsLimit){
				String info = GameContext.getI18n().getText(TextId.Luckybox_OpenRounds_Not_Enough);
				String infoStr = MessageFormat.format(info,roundsLimit+"");
				return new C0003_TipNotifyMessage(infoStr);
			}
			usedRounds++;
			openedBoxTimes = 0;
		}
		//还可以打开宝箱的个数
		openableTimes = vipTimes - openedBoxTimes;
		//②根据roleId取当前轮奖池(取不到则从role恢复)
		luckyBoxItemPoolMap = this.generateRoleAwardPoolCacheMap(role,usedRounds,refreshFlag);
		//下一次是第几次付费打开箱子
		int nextOpenFeeTimes = openedBoxTimes;
		remainRounds = roundsLimit - usedRounds;
		//需要排序  
		List<LuckeyBoxItem> luckeyBoxItem4MessageList = buildLuckeyBoxItem4MessageList(luckyBoxItemPoolMap);
		C1915_LuckyBoxDisplayRespMessage message = new C1915_LuckyBoxDisplayRespMessage();
		String consumeInfo = null;
		int nextConsume = 0;
		if(nextOpenFeeTimes==0){//第一次打开，不付钻石
			consumeInfo = luckyBoxAppConfig.getConsumeSilverMoneyInfo();
			nextConsume = luckyBoxAppConfig.getFirstDrawSilverMoneyConsume();
		}else{
			consumeInfo = luckyBoxAppConfig.getConsumeDiamondsInfo();
			nextConsume = getConfigConsume(nextOpenFeeTimes);//luckyBoxDiamandsConsumeMap.get(nextOpenFeeTimes+"").getDimandsConsume();//--is null?
		}

		message.setConsumeInfo(MessageFormat.format(consumeInfo,nextConsume+""));
		message.setLuckeyBoxItemList(luckeyBoxItem4MessageList);
		message.setOpenableTimes(openableTimes);
		message.setRemainTimes(remainRounds+1);
		return message;
	}
	/**
	 * 生成角色当日奖池，并同步缓存以及roleCount
	 * 概率是下一次点击箱子的概率，当玩家点击完所有能打开的箱子后（比如8），概率不设置
	 * @param role
	 * @param rounds 
	 * @return
	 * @date 2014-4-12 下午02:15:43
	 */
	private Map<String, LuckyBoxPoolItem> generateRoleAwardPoolCacheMap(RoleInstance role,int rounds,byte freshFlag){
		if(freshFlag==1){
			return generateNewRoleAwardPoolCacheMap(role,rounds);
		}
		
		LinkedHashMap<String, LuckyBoxPoolItem> luckyBoxItemPoolMap = roleAwardPoolcacheMap.get(role.getRoleId());
		if(luckyBoxItemPoolMap!=null&&luckyBoxItemPoolMap.size()==AWARD_POOL_SIZE)
			return roleAwardPoolcacheMap.get(role.getRoleId());
		else{
			luckyBoxItemPoolMap = Maps.newLinkedHashMap();//顺序表
			RoleCount rc = role.getRoleCount();
			byte vipLevel = GameContext.getVipApp().getVipLevel(role);
			// "2_2":3,"2_3":2
			String luckyBoxCountStr  = rc.getLuckyBoxCountJsonStr();
			String luckyBoxPlaceStr  = rc.getLuckyBoxPlaceJsonStr();
			Map<String, Integer> countMap = getRoleCountMap(luckyBoxCountStr);
			Map<String, Integer> placeMap = getRoleCountMap(luckyBoxPlaceStr);
			
			if(!Util.isEmpty(countMap)){
				int i = 1;
				for(Map.Entry<String, Integer> entry:countMap.entrySet()){
					String  awardKey = entry.getKey();
					int num = entry.getValue();
					
					Integer coorInteger = placeMap.get(awardKey);//->判断空
					int coordinate = coorInteger==null?0:coorInteger;
					LuckyBoxPoolItem boxItem = buildLuckyBoxItem(vipLevel, (byte)i, num, awardKey);
					boxItem.setCoordinate((byte)coordinate);
					luckyBoxItemPoolMap.put(awardKey, boxItem);
					i++;
				}
			}else{//今天第一次打开面板,重新生成奖励
				return generateNewRoleAwardPoolCacheMap(role,rounds);
			}
			return luckyBoxItemPoolMap;
		}
	}
	/**
	 * 生成新的角色当日奖池，并同步缓存以及roleCount
	 * @param role
	 * @param rounds 第几轮
	 * @return
	 */
	private Map<String, LuckyBoxPoolItem> generateNewRoleAwardPoolCacheMap(RoleInstance role,int rounds){
		    LinkedHashMap<String, LuckyBoxPoolItem> luckyBoxItemPoolMap = Maps.newLinkedHashMap();//顺序确定其有
		    byte vipLevel = GameContext.getVipApp().getVipLevel(role);
			Map<String, Integer> countMap = Maps.newLinkedHashMap();
			Map<String, Integer> map = getLuckyBoxLuckyOddsMap(vipLevel);//luckyBoxLuckyOdds4GeneratePoolMap.get(vipLevel+"");
			//注入VIP奖励vip
			List<String> list = Util.getLuckyDraw(1,map);
			String awardKey = vipLevel + Cat.underline + list.get(0);
			LuckyBoxPoolItem boxItem = buildLuckyBoxItem(vipLevel,  (byte)1, (byte)1, awardKey);//可能返回空值
			luckyBoxItemPoolMap.put(awardKey, boxItem);
			countMap.put(awardKey, boxItem.getNum());
			//注入普通奖励
			map = getLuckyBoxLuckyOddsNormalMap();//->NULL
			list = Util.getLuckyDrawUnique(NORMAL_AWARD_POOL_SIZE,map);
			for (int i = 0; i < list.size(); i++) {               //2-8
				awardKey = "normal" + Cat.underline + list.get(i);
				boxItem = buildLuckyBoxItem(vipLevel,  (byte)(i+2), (byte)0, awardKey);
				luckyBoxItemPoolMap.put(awardKey, boxItem);
				countMap.put(awardKey, boxItem.getNum());//->null
			}
			//加到缓存
			this.buildRoleAwardPoolcacheMap(role.getRoleId(), luckyBoxItemPoolMap);
			//添加到RoleCount
			String luckyBoxCountJsonStr = Util.strIntMapToString(countMap);
			GameContext.getCountApp().setLuckyBoxCount(role, (byte)rounds, luckyBoxCountJsonStr, "");
			return luckyBoxItemPoolMap;
	}
	/**
	 * 添加缓存
	 */
	private void buildRoleAwardPoolcacheMap(String roleId, LinkedHashMap<String, LuckyBoxPoolItem> luckyBoxItemPoolMap) {
		roleAwardPoolcacheMap.put(roleId, luckyBoxItemPoolMap);
	}

	/**
	 * 要发的奖池列表(注意既有物品也有属性)
	 * @param luckyBoxItemPoolMap
	 * @return
	 * @date 2014-4-11 下午07:39:06
	 */
	private  List<LuckeyBoxItem> buildLuckeyBoxItem4MessageList(Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap){
		List<LuckeyBoxItem> list4Message = Lists.newArrayList();
		
		for (LuckyBoxPoolItem it : luckyBoxItemPoolMap.values()) {
			LuckeyBoxItem item = build4messageItem(it);
			list4Message.add(item);
		}
		return sortWell(list4Message);//按照开出的位置排序 [7, 0, 6, 0, 5, 3, 2, 0]->[0, 2, 3, 0, 5, 6, 7, 0]
	}
	/**
	 * @args arr [7, 0, 6, 0, 5, 3, 2, 0];
	 * @return    [0, 2, 3, 0, 5, 6, 7, 0]
	 * @date 2014-4-16 下午09:06:15
	 */
	private  List<LuckeyBoxItem> sortWell( List<LuckeyBoxItem> itemlist) {
		Object arr[] = itemlist.toArray();//拷贝
		LuckeyBoxItem bakArr[] = new LuckeyBoxItem[itemlist.size()];//新的数组
		List<LuckeyBoxItem> stack = new ArrayList<LuckeyBoxItem>();
		for (Object b : arr) {
			LuckeyBoxItem item = (LuckeyBoxItem) b;
			if(item.getCoordinate()==0)//未打开
			{
				stack.add(item);
			}else{
				bakArr[item.getCoordinate()-1] = item;
			}
		}
		for (int i = 0; i<bakArr.length ;i++){
			if(bakArr[i]==null){
				bakArr[i] = stack.remove(0);
			}
		}
		List<LuckeyBoxItem> rtList = new ArrayList<LuckeyBoxItem>();
		Collections.addAll(rtList, bakArr);

		return rtList;
	}
	/**
	 * @param it
	 * @return
	 * @date 2014-4-15 下午05:09:56
	 */
	private LuckeyBoxItem build4messageItem(LuckyBoxPoolItem it ){
		LuckeyBoxItem lbt = new LuckeyBoxItem();
		byte awardType = it.getAwardType();
		byte place = it.getPlace();
		byte coordinate = it.getCoordinate(); 
		lbt.setAwardType(it.getAwardType());//设置类型
		lbt.setPlace(place);
		lbt.setCoordinate(coordinate);
		if(awardType==AWARD_TYPE_GOODS){
			GoodsBase goodsBase = it.getAwardGoods();
			GoodsLiteItem item = goodsBase.getGoodsLiteItem();
			item.setBindType(it.getBind());
			item.setNum((short)it.getNum());
			lbt.setItem(item);
		}else{
			AttriTypeValueItem attItem = new AttriTypeValueItem();
			byte attriType = (byte)it.getAwardId();
			attItem.setAttriType(attriType);
			attItem.setAttriValue(it.getNum());
			lbt.setAttItem(attItem);
		}
		return lbt;
	}
	/**
	 * vipLevel和times，用于查找8个奖励的权重表,不设置权重
	 * @param vipLevel
	 * @param place
	 * @param num
	 * @param awardKey
	 * @return
	 * @date 2014-4-15 上午11:55:52
	 */
	private LuckyBoxPoolItem buildLuckyBoxItem(byte vipLevel, byte place, int num,String awardKey){
		LuckyBoxRewardPoolConfig boxRewardPoolConfig = luckyBoxRewardPoolMap.get(awardKey);
		if(boxRewardPoolConfig == null)
			return null;
		int awardId = boxRewardPoolConfig.getAwardId();
		byte awardType = boxRewardPoolConfig.getAwardType();
		byte bind = boxRewardPoolConfig.getBind();
		
		int min = boxRewardPoolConfig.getNumLower();
		int max = boxRewardPoolConfig.getNumUpper();
		if(num<=0)
			num = Util.randomInt(min, max);
		LuckyBoxPoolItem boxItem = new LuckyBoxPoolItem();
		boxItem.setAwardId(awardId);
		boxItem.setAwardType(awardType);
		boxItem.setBind(bind);
		boxItem.setPlace(place);
		boxItem.setAwardKey(awardKey);
//		boxItem.setVipFlag(vipFlag);
		
		boxItem.setNum(num);
		return boxItem;
	}
	/**
	 * 有序
	 * roleCount
	 * @param rc
	 * @return
	 * @date 2014-4-11 下午06:24:43
	 */
	private  Map<String, Integer> getRoleCountMap(String jsonStr){
		LinkedHashMap<String, Integer> map = (LinkedHashMap<String, Integer>) Util
				.parseStringIntLinkedMap(jsonStr);
		if (map == null) {
			map = Maps.newLinkedHashMap();
		}
		return map ;
	}
	/**
	 * 1916打开宝箱
	 * 验证位置是否合法，当日是否还有剩余次数，钻石是否足够
	 * ①从消息中传来的coordinate原样返回，服务器存入奖池缓存的奖励属性中，并存入RoleCount中
	 * ②奖励池获得，验证次数
	 * ③验证消耗
	 * ④根据奖励池生成权重Map,打开宝箱
	 * ⑤发奖励
	 */
	@Override
	public Message getLuckyBoxDraw(RoleInstance role, byte coordinate) {
		C1916_LuckyBoxPlayRespMessage message = new C1916_LuckyBoxPlayRespMessage();
		//验证参数
		if(coordinate>AWARD_POOL_SIZE){
			this.logger.error("AlchemysApp.getLuckyBoxDraw error: param coordinate = "+coordinate);
			message.setType(RespTypeStatus.FAILURE);
			message.setInfo(this.getText(TextId.Luckybox_Req_Param_Error));
			return message;
		}
		
		//生成的奖池1-8,每次生成一轮的时候生成,需要加在缓存
		Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap = null;;
		RoleCount rc = role.getRoleCount();
		rc.resetDay();
		
		byte vipLevel = GameContext.getVipApp().getVipLevel(role);
		int rounds  = rc.getLuckyBoxUsedTimes();
		//剩余轮次
		int remainRounds = luckyBoxAppConfig.getRoundsCount()-rounds; //null
		//已经打开的箱子的个数
		int openedBoxTimes = getOpenedBoxTimes(role);
		//这是第几次打开箱子
		int openBoxTimes = openedBoxTimes + 1;      // 第一次点击箱子，1 = 0 +1
		//②根据roleId取当日轮次缓存，取不到则从roleCount信息恢复。得到当前轮奖池（1-8）
		byte refreshFlag = 0;
		luckyBoxItemPoolMap = this.generateRoleAwardPoolCacheMap(role,rounds,refreshFlag);
		//每次抽取重置几率
		setAwardPoolOdds(luckyBoxItemPoolMap,vipLevel,openBoxTimes);
		int vipTimes = getConfigVipTimes(vipLevel);//luckyBoxVipTimesMap.get(vipLevel+"").getVipTimes();//VIP对应的每次可抽取轮次
		//还可以打开多少次
		int openableTimes = vipTimes - openedBoxTimes;
		
		if(openableTimes<=0){
			this.logger.error("AlchemysApp.getLuckyBoxDraw error: param coordinate = "+coordinate);
			message.setType(RespTypeStatus.FAILURE);
			message.setInfo(this.getText(TextId.Luckybox_OpenTimes_Not_Enough));
			return message;
		} 
		int openableTimesNext  = openableTimes - 1;//点击完这次显示给客户端的还有几次，第一次点击箱子，3 = 4 - 1
		//付费玩的第几次
		int feeTimes = 0;
		//③验证消耗是否满足
		int consume = 0;
		if(openBoxTimes==1){
			consume = luckyBoxAppConfig.getFirstDrawSilverMoneyConsume();
			if(consume>role.getSilverMoney()){//应该改为大于
				message.setType(RespTypeStatus.FAILURE);
				message.setInfo(this.getText(TextId.Luckybox_SilverMoney_Not_Enough));
				return message;
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.silverMoney, OperatorType.Decrease,
					consume, OutputConsumeType.alchemy_consume);
		}else{
			//付费玩的第几次
			feeTimes = openBoxTimes - 1;
			consume = getConfigConsume(feeTimes);//luckyBoxDiamandsConsumeMap.get(feeTimes+"");
			if(consume>role.getGoldMoney()){//改为大于
				message.setType(RespTypeStatus.FAILURE);
				message.setInfo(this.getText(TextId.Luckybox_Diamonds_Not_Enough));
				return message;
			}
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.goldMoney, OperatorType.Decrease,
					consume, OutputConsumeType.alchemy_consume);
		}
		//通知用户属性变化 消耗提示
		role.getBehavior().notifyAttribute();
		String consumeInfo = null;
		int nextConsume = 0;
		int nextFeeTimes = feeTimes+1;
		consumeInfo = luckyBoxAppConfig.getConsumeDiamondsInfo();
		nextConsume = getConfigConsume(nextFeeTimes);
		//④生成抽奖M
		String drawAwardKey = this.getDrawResult(luckyBoxItemPoolMap);
		if(drawAwardKey==null){
			this.logger.error("AlchemysApp.getLuckyBoxDraw error: ");
			message.setType(RespTypeStatus.FAILURE);
			message.setInfo(this.getText(TextId.Luckybox_Draw_Fail));
			return message;
		}
		LuckyBoxPoolItem lbpItem = luckyBoxItemPoolMap.get(drawAwardKey);
		//⑤发送奖励,直接添加到背包，背包满则发邮件
		boolean sendReslut = sendLuckyBoxAward(role, lbpItem);
		if(!sendReslut){
			this.logger.error("AlchemysApp.getLuckyBoxDraw error: ");
			message.setType(RespTypeStatus.FAILURE);
			message.setInfo(this.getText(TextId.Luckybox_Draw_Fail));
			return message;
		}
		//增加提示
		role.getBehavior().notifyAttribute();
		lbpItem.setCoordinate(coordinate);
		LuckeyBoxItem lukeyBoxItem = build4messageItem(lbpItem);
		
		message.setConsumeInfo(MessageFormat.format(consumeInfo,nextConsume+""));
		message.setOpenableTimes(openableTimesNext);
		message.setRemainTimes(remainRounds+1);
		message.setCoordinate(coordinate);
		message.setLukeyBoxItem(lukeyBoxItem);
		message.setType(RespTypeStatus.SUCCESS);
		message.setCoordinate(coordinate);
		
		//添加到RoleCount
		saveCacheMap2RoleCount(role, rounds, luckyBoxItemPoolMap);
		return message;
	}
	private void setAwardPoolOdds(Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap, byte vipLevel, int times){
		String oddsKey = vipLevel+Cat.underline+times;
		LuckyBoxOddsConfig newOddsConf =  luckyBoxLuckyOddsMap.get(oddsKey);
		if(newOddsConf == null)
			return;
		int place;
		int newOdds;
		for (LuckyBoxPoolItem it : luckyBoxItemPoolMap.values()) {
			place = it.getPlace();
			newOdds = place == 1?newOddsConf.getVipOdds():newOddsConf.getNormalOdds();
			it.setNewOdds(newOdds);
		}
	}
	//发送奖励
	private boolean sendLuckyBoxAward(RoleInstance role, LuckyBoxPoolItem lbpItem) {
		byte rewardType = lbpItem.getAwardType();
		if(rewardType==AWARD_TYPE_GOODS){
			List<GoodsOperateBean> addList = new ArrayList<GoodsOperateBean>();
			GoodsOperateBean bean = new GoodsOperateBean();
			bean.setGoodsId(lbpItem.getAwardId());
			bean.setGoodsNum(lbpItem.getNum());
			bean.setBindType(BindingType.get(lbpItem.getBind()));
			addList.add(bean);
			//直接发会弹物品
			AddGoodsBeanResult goodsResult = GameContext.getUserGoodsApp().addSomeGoodsBeanForBag(role, addList,OutputConsumeType.luckybox_output);
			// 背包满了则发邮件
			List<GoodsOperateBean> putFailureList = goodsResult.getPutFailureList();
			try {
				if(!Util.isEmpty(putFailureList)){
					String context = this.getText(TextId.Luckybox_Mail_Context);
					GameContext.getMailApp().sendMail(role.getRoleId(),
								MailSendRoleType.LuckyBox.getName(), 
								context,
								MailSendRoleType.LuckyBox.getName(), 
								OutputConsumeType.luckybox_mail_output
								.getType(),
								putFailureList);
				}
				return true;
			} catch (Exception e) {
				logger.error("", e);
			}
		}else if(rewardType==AWARD_TYPE_ATTRIBUTE){
			byte attrtType = (byte)lbpItem.getAwardId();
			AttributeType at = AttributeType.get(attrtType);
			if(null != at){
				int rewardNumber = lbpItem.getNum();
				GameContext.getUserAttributeApp().changeAttribute(role, at,OperatorType.Add, rewardNumber,OutputConsumeType.luckybox_output);
				return true;
			}
		}
		return false;
	}
	/**
	 * 当前轮已经打开箱子的个数
	 */
	private int getOpenedBoxTimes(RoleInstance role){
		RoleCount rc = role.getRoleCount();
		String luckyBoxPlaceStr  = rc.getLuckyBoxPlaceJsonStr();
		Map<String, Integer> placeMap = getRoleCountMap(luckyBoxPlaceStr);
		return placeMap.size();
	}
	/**
	 * 将缓存同步到RoleCount
	 * @param luckyBoxItemPoolMap
	 * @date 2014-4-12 下午06:04:02
	 */
	private void saveCacheMap2RoleCount(RoleInstance role, int times,Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap){
		Map<String, Integer> countMap = Maps.newLinkedHashMap();
		Map<String, Integer> placeMap = Maps.newLinkedHashMap();
		// "2_2":3,"2_3":2
		for(Map.Entry<String, LuckyBoxPoolItem> entry:luckyBoxItemPoolMap.entrySet()){
			String  awardKey = entry.getKey();
			LuckyBoxPoolItem it = entry.getValue();
			countMap.put(awardKey, it.getNum()+0);
			if(it.getCoordinate() != 0){
				placeMap.put(awardKey, it.getCoordinate()+0);
			}
		}
		String luckyBoxCountStr  = Util.strIntMapToString(countMap);
		String luckyBoxPlaceStr  = Util.strIntMapToString(placeMap);
		GameContext.getCountApp().setLuckyBoxCount(role, (byte)times, luckyBoxCountStr, luckyBoxPlaceStr);
	}
	/**
	 * 从奖池中抽奖
	 * @param luckyBoxItemPoolMap
	 * @return
	 * @date 2014-4-12 下午05:38:34
	 */
	private String getDrawResult(Map<String, LuckyBoxPoolItem> luckyBoxItemPoolMap){
		Map<String,Integer>weightMap = Maps.newHashMap();
		for(Map.Entry<String, LuckyBoxPoolItem> entry:luckyBoxItemPoolMap.entrySet()){
			String  awardKey = entry.getKey();
			LuckyBoxPoolItem it = entry.getValue();
			if(it.getCoordinate() == 0){
				weightMap.put(awardKey, it.getNewOdds());
			}
		}
		List<String> list = Util.getLuckyDraw(1,weightMap);
		return list.get(0);
	}
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	public int getRemainTimes(RoleInstance role){
		if(luckyBoxAppConfig==null)
			return 0;
		RoleCount rc = role.getRoleCount();
		int times  = rc==null? 0:rc.getLuckyBoxUsedTimes();
		times++;
		int remainTimes = luckyBoxAppConfig.getRoundsCount()-times;
		return remainTimes<0? 0:remainTimes;
	}
	private <T> int getConfigVipTimes(T vipLevel){
		return luckyBoxVipTimesMap.get(vipLevel+"").getVipTimes();//VIP对应的每次可抽取轮次
	}
	private <T> int getConfigConsume(T openFeeTimes){
		LuckyBoxDiamandsConsumeConfig c = luckyBoxDiamandsConsumeMap.get(openFeeTimes+"");
		int consume = c==null?0:c.getDimandsConsume();
		return consume;
	}
	@Override
	public void offline(RoleInstance role) {
		try {
			roleAwardPoolcacheMap.remove(role.getRoleId());
		} catch (Exception e) {
			this.logger.error("LuckyBoxApp.offline error:" + e);
		}
	}
}
