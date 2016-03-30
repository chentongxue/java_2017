package sacred.alliance.magic.app.active.discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.active.discount.type.DiscountType;
import sacred.alliance.magic.app.active.vo.Discount;
import sacred.alliance.magic.app.active.vo.DiscountCond;
import sacred.alliance.magic.app.active.vo.DiscountReward;
import sacred.alliance.magic.app.hint.HintId;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.PublicNoticeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.DiscountDbInfo;
import sacred.alliance.magic.domain.PublicNotice;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.ActiveDiscountListItem;
import com.game.draco.message.response.C2315_ActiveDiscountListRespMessage;

public class ActiveDiscountAppImpl implements ActiveDiscountApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<Integer, DiscountReward> allRewardMap;
	private Map<Integer, DiscountCond> allCondMap;
	private Map<Integer, Discount> allListMap;
	private List<Discount> payFirstReturnList ;
	private String chargeDesc;
	
	public Map<Integer, Discount> getAllListMap() {
		return allListMap;
	}
	
	@Override
	public Discount getDiscount(int discountId){
		if(Util.isEmpty(this.allListMap)){
			return null ;
		}
		return this.allListMap.get(discountId);
	}

	@Override
	public void setArgs(Object arg0) {

	}

	@Override
	public void start() {
		Result result = this.loadConfig();
		if(!result.isSuccess()){
			Log4jManager.CHECK.error(result.getInfo());
			Log4jManager.checkFail();
		}
	}
	
	@Override
	public Result reLoad() {
		Result result = this.loadConfig();
		try{
			//热加载的时候如果有首充类型的活动，并且当前时间是有效的则要全服刷新下右上菜单
			if (!this.reloadRefreshMenu()) {
				return result;
			}
//			GameContext.getMenuApp().refresh(MenuIdType.first_charge);
		} catch (Exception ex) {
			logger.error("reloadRefreshMenu error", ex);
		}
		return result ;
	}
	
	private boolean reloadRefreshMenu(){
		if(Util.isEmpty(this.payFirstReturnList)){
			return false ;
		}
		for(Discount discount : this.payFirstReturnList){
			if(discount.isInDate()){
				return true ;
			}
		}
		return false ;
	}
	
	private Result loadConfig(){
		Map<Integer, DiscountReward> allRewardMap = this.loadDiscountReward();
		Map<Integer, DiscountCond> allCondMap = this.loadDiscountCond();
		Map<Integer, Discount> allListMap = this.loadDiscountList();
		this.loadChargeDesc();
		Result result = this.init(allRewardMap, allCondMap, allListMap);
		if(!result.isSuccess()){
			return result;
		}
		//构建首充类型活动列表
		List<Discount> payFirstReturnList = new ArrayList<Discount>();
		for(Discount discount : allListMap.values()){
			if(this.isPayFirstReturnDiscount(discount)){
				payFirstReturnList.add(discount);
			}
		}
		this.allRewardMap = allRewardMap;
		this.allCondMap = allCondMap;
		this.allListMap = allListMap;
		this.payFirstReturnList = payFirstReturnList ;
		return result;
	}
	
	/**
	 * 同一时刻只有一个首充类型的活动
	 * @param role
	 * @return
	 */
	@Override
	public Discount getCurrentPayFirstDiscount(RoleInstance role){
		if(Util.isEmpty(this.payFirstReturnList)){
			return null ;
		}
		for(Discount discount : this.payFirstReturnList){
			if(this.canShow(discount, role, role.getChannelId())){
				return discount ;
			}
		}
		return null ;
	}
	

	
	private Result init(Map<Integer, DiscountReward> allRewardMap, Map<Integer, DiscountCond> allCondMap, 
			Map<Integer, Discount> allListMap){
		Result loadResult = new Result();
		loadResult.failure();
		if(allRewardMap == null || allCondMap == null || allListMap == null){
			//加载配置文件异常
			return loadResult;
		}
		//奖励
		if(allRewardMap != null && allRewardMap.size() > 0){
			DiscountReward discountReward = null;
			for(Integer key : allRewardMap.keySet()){
				discountReward = allRewardMap.get(key);
				if(null == discountReward){
					continue;
				}
				if(!discountReward.init()){
					loadResult.setInfo("in active_discount.xls reward config error!");
					return loadResult;
				}
			}
		}
		
		//折扣活动项
		if(allListMap != null && allListMap.size() > 0){
			Discount discount = null;
			try{
				for(Integer key : allListMap.keySet()){
					discount = allListMap.get(key);
					if(null == discount){
						continue;
					}
					Result result = discount.init();
					if(!result.isSuccess()){
						Log4jManager.CHECK.error(result.getInfo());
						Log4jManager.checkFail();
						continue ;
					}
					//初始化条件和reward
					for(int index =1; index <= Discount.MAX_COND_NUM;index ++){
						initListCondAndReward(discount, allRewardMap, allCondMap, 
								index , discount.getCond(index ), discount.getReward(index ));
					}
					//如果配置了活动但是没有配置相应的条件和奖励
					if(Util.isEmpty(discount.getCondList()) || Util.isEmpty(discount.getRewardList())){
						Log4jManager.checkFail();
						Log4jManager.CHECK.error("activeDiscountApp init error, Discount id= " + discount.getId()
								+ "not config conds or rewards");
						loadResult.setInfo("in active_discount.xls list config error!");
						return loadResult;
					}
				}
			}catch (Exception e){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("activeDiscountApp init error, Discount id= " + discount.getId(), e);
				loadResult.setInfo(e.toString());
				return loadResult;
			}
			
		}
		
		loadResult.success();
		return loadResult;
	}
	
	private void initListCondAndReward(Discount discount, Map<Integer, DiscountReward> allRewardMap, 
			Map<Integer, DiscountCond> allCondMap, int index, int condId, int rewardId){
		if(condId <= 0){
			return ;
		}
		if(!allCondMap.containsKey(condId) || !allRewardMap.containsKey(rewardId)){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("discount active, cond" + index + " or reward "+ index +" in list id= " + discount.getId() + " don't exsit");
		}
		DiscountCond discountCond = allCondMap.get(condId);
		Result condResult = discountCond.init();
		if(!condResult.isSuccess()){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("discount active," + condResult.getInfo() +" in list id= " + discount.getId());
		}
		discount.getCondList().add(index-1, discountCond);
		discount.getRewardList().add(index-1, allRewardMap.get(rewardId));
	}

	@Override
	public void stop() {

	}
	
	private Map<Integer, DiscountReward> loadDiscountReward(){
		//加载折扣活动配置
		String fileName = XlsSheetNameType.discount_reward.getXlsName();
		String sheetName = XlsSheetNameType.discount_reward.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Integer, DiscountReward> allRewardMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DiscountReward.class);
			return allRewardMap;
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		return null;
	}
	
	private Map<Integer, DiscountCond> loadDiscountCond(){
		//加载折扣活动配置
		String fileName = XlsSheetNameType.discount_cond.getXlsName();
		String sheetName = XlsSheetNameType.discount_cond.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Integer, DiscountCond> allCondMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, DiscountCond.class);
			return allCondMap;
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		return null;
	}
	
	private Map<Integer, Discount> loadDiscountList(){
	//加载折扣活动配置
		String fileName = XlsSheetNameType.discount_list.getXlsName();
		String sheetName = XlsSheetNameType.discount_list.getSheetName();
		try{
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			Map<Integer, Discount> allListMap = XlsPojoUtil.sheetToGenericLinkedMap(sourceFile, sheetName, Discount.class);
			return allListMap;
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error: sourceFile = " + fileName + "sheetName = " + sheetName, ex);
		}
		return null;
	}
	
	/** 加载充值界面说明文字 */
	private void loadChargeDesc(){
		String fileName = XlsSheetNameType.discount_charge_desc.getXlsName();
		String sheetName = XlsSheetNameType.discount_charge_desc.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath() + fileName;
			List<String> descList = XlsPojoUtil.sheetToStringList(sourceFile, sheetName);
			if(!Util.isEmpty(descList)){
				this.chargeDesc = descList.get(0);
			}
		} catch (RuntimeException e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("load excel error: fileName = " + fileName +",sheetName =" + sheetName, e);
		}
	}

	@Override
	public Map<Integer, DiscountDbInfo> loadRoleActiveDiscount(String userId) {
		List<DiscountDbInfo> roleDiscount = GameContext.getBaseDAO().selectList(DiscountDbInfo.class, "userId", userId);
		Map<Integer, DiscountDbInfo> discountDbInfoMap = new HashMap<Integer, DiscountDbInfo>();
		if(null==roleDiscount || roleDiscount.size()==0){
			return discountDbInfoMap;
		}
		for(DiscountDbInfo discountDbInfo : roleDiscount){
			if(null == discountDbInfo){
				continue;
			}
			discountDbInfo.setExistRecord(true);
			discountDbInfoMap.put(discountDbInfo.getActiveId(), discountDbInfo);
		}
		return discountDbInfoMap;
	}

	@Override
	public void saveRoleActiveDiscount(Map<Integer, DiscountDbInfo> discountDbInfoMap) {
		if(discountDbInfoMap.size() == 0){
			return;
		}
		for(Map.Entry<Integer, DiscountDbInfo> entry : discountDbInfoMap.entrySet()){
			DiscountDbInfo discountDbInfo = entry.getValue();
			if(null == discountDbInfo){
				continue;
			}
			if(discountDbInfo.isExistRecord()){
				GameContext.getBaseDAO().update(discountDbInfo);
			}
			else{
				GameContext.getBaseDAO().insert(discountDbInfo);
				discountDbInfo.setExistRecord(true);
			}
		}
	}
	
	@Override
	public void offlineLog(Map<Integer, DiscountDbInfo> discountDbInfoMap) {
		try{
			if(discountDbInfoMap.size() == 0){
				return;
			}
			for(Map.Entry<Integer, DiscountDbInfo> entry : discountDbInfoMap.entrySet()){
				DiscountDbInfo discountDbInfo = entry.getValue();
				if(null == discountDbInfo){
					continue;
				}
				Log4jManager.OFFLINE_DISCOUNT_ACTIVE_DB_LOG.info(discountDbInfo.getSelfInfo());
			}
		}catch(Exception e){
		}
		
	}
	
	@Override
	public void updateFeeDiscount(String userId, int payValue, boolean isPay, int channelId,OutputConsumeType outputConsumeType){
		//遍历所以折扣活动
		Map<Integer, Discount> allDiscount = getAllListMap();
		if(null==allDiscount || allDiscount.size()==0){
			return;
		}
		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
		//如果用户不在线，则需要将用户的折扣信息从数据库加载,在线的话直接在角色身上取
		Map<Integer, DiscountDbInfo> discountDbInfoMap = null;
		if(null==role){
			discountDbInfoMap = loadRoleActiveDiscount(userId);
		}else{
			discountDbInfoMap = role.getDiscountDbInfo();
		}
		boolean commonEffect = false ;
		boolean firstPayEffect = false ;
		for(Discount discount : allDiscount.values()){
			DiscountType discountType = discount.getDiscountType() ;
			if(null == discountType || isPay != discountType.isPay() 
					|| discountType.getType() != DiscountType.TYPE_MONEY){
				continue ;
			}
			if(!discount.canCount(outputConsumeType)){
				continue;
			}
			if(!this.canShow(discount, role, channelId)){
				continue ;
			}
			boolean effect = discount.getDiscountTypeLogic().count(null, userId, 
					discountDbInfoMap, discount, payValue);
			if(!effect){
				continue ;
			}
			if(this.isPayFirstReturnDiscount(discount)){
				firstPayEffect = true ;
				continue ;
			}
			commonEffect = true ;
		}
		//如果用户不在线，则需要将用户的折扣信息从数据库加载
		if(null==role){
			saveRoleActiveDiscount(discountDbInfoMap);
			return ;
		}
		//在线用户判断是否需要更新可领取标识
		if(commonEffect && this.canRecvReward(role)){
			GameContext.getHintApp().hintChange(role, HintId.Discount, true);
		}
		//判断首充值
		if(firstPayEffect){
			GameContext.getHintApp().hintChange(role, HintId.First_Recharge, true);
		}
	}
	
	/**
	 * !!! 不计算首充类型
	 */
	@Override
	public boolean canRecvReward(RoleInstance role){
		Map<Integer, Discount> discountListMap = GameContext.getActiveDiscountApp().getAllListMap();
		if(Util.isEmpty(discountListMap)){
			return false ;
		}
		for(Entry<Integer, Discount> entry : discountListMap.entrySet()){
			Discount discount = entry.getValue();
			//！！！ 首充类型不
			if(!this.isCommonDiscount(discount)){
				continue ;
			}
			if(!this.canShow(discount, role, role.getChannelId())){
				continue ;
			}
			if(discount.canReward(role)){
				return true ;
			}
		}
		return false ;
	}

	private boolean canShow(Discount discount,RoleInstance role, int channelId){
		if(null == discount || !discount.isInDate()){
			return false ;
		}
		if(!discount.isServerCanShow()){
			//当前服务器不允许显示
			return false ;
		}
		if(!discount.isChannelCanShow(channelId)){
			//当前渠道不允许显示
			return false;
		}
		if(null != role && !discount.isRewardedCanShow(role)){
			//领完奖励是否显示
			return false ;
		}
		return true ;
	}
	
	
	/**
	 * 是否普通的活动（非首充值）
	 * @param discount
	 * @return
	 */
	private boolean isCommonDiscount(Discount discount){
		if(null == discount){
			return false ;
		}
		return discount.getDiscountType() != DiscountType.PAY_FIRST_RETURN ;
	}
	
	private boolean isPayFirstReturnDiscount(Discount discount){
		if(null == discount){
			return false ;
		}
		return DiscountType.PAY_FIRST_RETURN == discount.getDiscountType() ;
	}
	
	@Override
	public Message createDiscountListMsg(RoleInstance role, boolean isFromHelper) {
		C2315_ActiveDiscountListRespMessage respMsg = new C2315_ActiveDiscountListRespMessage();
		//!!!如果没有活动发defaultId=0客户端会崩溃
		respMsg.setId(-1);
		
		respMsg.setBulletinTitle(GameContext.getI18n().getText(TextId.PUBLIC_NOTICE_DEFAULT_TITLE));
		PublicNotice notice = GameContext.getPublicNoticeApp().getNotice(PublicNoticeType.System_Notice);
		if(null != notice && !Util.isEmpty(notice.getTitle())){
			respMsg.setBulletinTitle(notice.getTitle());
		}
		Map<Integer, Discount> discountListMap = GameContext.getActiveDiscountApp().getAllListMap();
		if(Util.isEmpty(discountListMap)){
			return respMsg;
		}
		List<ActiveDiscountListItem> listItems = new ArrayList<ActiveDiscountListItem>();
		int defaultId = 0;
		for(Entry<Integer, Discount> entry : discountListMap.entrySet()){
			Discount discount = entry.getValue();
			if(!this.canShow(discount, role, role.getChannelId())){
				continue ;
			}
			//首充类型不需要列出
			if(!this.isCommonDiscount(discount)){
				continue ;
			}
			boolean isCanRecv = discount.canReward(role) ;
			ActiveDiscountListItem item = new ActiveDiscountListItem();
			item.setId(discount.getId());
			item.setName(discount.getName());
			item.setTips(discount.getTips());
			item.setCanRecv(isCanRecv?(byte)1:(byte)0);
			listItems.add(item);
			if(defaultId != 0){
				continue;
			}
			if(!isFromHelper){
				defaultId = discount.getId();
			}
			if(isFromHelper && isCanRecv){
				defaultId = discount.getId();
			}
		}
		respMsg.setListItems(listItems);
		//!!!如果没有活动发defaultId=0客户端会崩溃
		if(Util.isEmpty(listItems)){
			defaultId = -1;
		}
		respMsg.setId(defaultId);
		return respMsg;
	}
	
	@Override
	public String getChargeDesc() {
		return this.chargeDesc;
	}

	
	@Override
	public void updateAttriDiscount(RoleInstance role,Discount discount) {
		DiscountType discountType = discount.getDiscountType() ;
		if(null == discountType || discountType.getType() != DiscountType.TYPE_ATTRI){
			return ;
		}
		Map<Integer, DiscountDbInfo> discountDbInfoMap = role.getDiscountDbInfo();
		discount.getDiscountTypeLogic().count(role, role.getUserId(), discountDbInfoMap, discount, 0);
	}
	
	@Override
	public void updateLoginDiscount(RoleInstance role){
		//遍历所以折扣活动
		Map<Integer, Discount> allDiscount = getAllListMap();
		if(null==allDiscount || allDiscount.size()==0){
			return;
		}
		for(Discount discount : allDiscount.values()){
			DiscountType discountType = discount.getDiscountType() ;
			if(null == discountType || discountType.getType() != DiscountType.TYPE_LOGIN){
				continue ;
			}
			if(!this.canShow(discount, role, role.getChannelId())){
				continue ;
			}
			
			Map<Integer, DiscountDbInfo> discountDbInfoMap = role.getDiscountDbInfo();
			discount.getDiscountTypeLogic().count(role, role.getUserId(), discountDbInfoMap, discount, 0);
		}
	}

	@Override
	public Set<HintId> getHintIdSet(RoleInstance role) {
		if(!this.canRecvReward(role)){
			return null ;
		}
		Set<HintId> set = new HashSet<HintId>();
		set.add(HintId.Discount);
		return set ;
	}

	@Override
	public void hintChange(RoleInstance role, HintId hintId) {
		try {
			GameContext.getHintApp().hintChange(role, hintId, this.canRecvReward(role));
		} catch (Exception e) {
			this.logger.error("ActiveDiscountApp.hintChange error: ", e);
		}
	}
	
	
}
