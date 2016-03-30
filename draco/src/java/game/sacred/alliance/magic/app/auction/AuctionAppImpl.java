package sacred.alliance.magic.app.auction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.project.mercury.api.IndexType;
import org.project.mercury.api.IndexableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import platform.message.item.MercuryRecordItem;
import platform.message.item.MercurySearchIndexItem;
import platform.message.item.MercurySearchSortItem;
import platform.message.request.C5401_MercurySearchReqMessage;
import platform.message.request.C5402_MercuryGetReqMessage;
import platform.message.request.C5403_MercuryPutReqMessage;
import platform.message.request.C5404_MercuryRemoveReqMessage;
import platform.message.request.C5405_MercuryExpiredReqMessage;
import platform.message.response.C5400_MercuryRespMessage;
import platform.message.response.C5401_MercurySearchRespMessage;
import platform.message.response.C5402_MercuryRecordInfoRespMessage;
import platform.message.response.C5405_MercuryExpiredRespMessage;
import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.Money;
import sacred.alliance.magic.base.MoneyType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.client.Client;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.util.Wildcard;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.mail.domain.Mail;
import com.game.draco.app.mail.type.MailSendRoleType;
import com.game.draco.message.item.AuctionMyShelfItem;
import com.game.draco.message.item.AuctionSearchBaseItem;
import com.game.draco.message.item.AuctionSearchItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0850_AuctionSearchReqMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AuctionAppImpl implements AuctionApp {
	private static final Logger logger = LoggerFactory.getLogger(AuctionAppImpl.class);
	private static final int SUCCESS_CODE = 1;
	private static final int FAILURE_CODE = 0;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	private final static String CAT = "#";
	/**
	 * 最少上架元宝数目
	 */
	private static final int HOURS_8 = 8 ;
	private static final int HOURS_24 = 24;
	private static final int HOURS_48 = 48;
	
	/**
	 * 钱币类型为100
	 */
	private static final byte MONEY_TYPE = 100 ;
	private static final byte SUCCESS = (byte) 1;
	private static final byte PAGE_SIZE = 4 ;
	private static final byte MAX_RECORD_SIZE = 10 ;

	private static final String ITEM_NAME = IndexableRow.ITEM_NAME;
	private static final String PRICE_TYPE = IndexableRow.PRICE_TYPE;
	private static final String PRICE_NUM = IndexableRow.PRICE_NUM;
	private static final String SINGLE_PRICE = IndexableRow.PRICE_NUM + "/" + IndexableRow.ITEM_NUM;

	private static final String ID = "_UNIQUEID_";
	private static final String REMAIN_TIME = IndexableRow.EXPIRED_TIME;
	private static final String BIG_TYPE = IndexableRow.INDEX_1;
	private static final String SMALL_TYPE = IndexableRow.INDEX_2;
	private static final String LEVEL = IndexableRow.INDEX_3;
	private static final String QUALITY = IndexableRow.INDEX_4;
	private static final String CAREER = IndexableRow.INDEX_5;

	private List<AuctionMenu> auctionMenuList = Lists.newArrayList();
	private boolean matchServerId = false;
	private int searchCd = 500;
	private Client mercuryClient;
	private int expiredMaxRecords = 10;
	private FeeInfoConfig feeInfoConfig = new FeeInfoConfig();
	private Map<String,AuctionMenuAdapter> adapterMap  ;
	/**
	 * 默认适配
	 */
	private final AuctionMenuAdapter defaultAdapter = new AuctionMenuAdapter();
	
	private volatile long lastTimestamp = -1L;
    private volatile int sequence = 0;
    private final int sequenceMax = 4096;

    private long tilNextMillis(long lastTimestamp){
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
    
    private synchronized String generateMoneyId(int serverId) {
        long timestamp = System.currentTimeMillis();
        if(timestamp<lastTimestamp){
           logger.error("Clock moved backwards.  Refusing to generate id for "+ (
                    lastTimestamp - timestamp) +" milliseconds.");
            return null ;
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return Integer.toString(serverId,36) 
        		+ Cat.underline 
        		+ Long.toString(timestamp, 36)
        		+ Cat.underline
        		+ Integer.toString(sequence, 36);
    }
    

	public void setExpiredMaxRecords(int expiredMaxRecords) {
		this.expiredMaxRecords = expiredMaxRecords;
	}

	public void setSearchCd(int searchCd) {
		this.searchCd = searchCd;
	}

	@Override
	public void setArgs(Object arg0) {
	}

	@Override
	public void start() {
		this.initAuctionMenu();
		this.initAutionAdapter();
		this.initFeeInfoConfig();
	}

	@Override
	public void stop() {
	}
	
	
	private AuctionMenuAdapter getAutionAdapter(int goodsBigType,
			int goodsSmallType){
		if(null == this.adapterMap){
			return this.defaultAdapter ;
		}
		String key = goodsBigType + Cat.underline + goodsSmallType ;
		AuctionMenuAdapter result = this.adapterMap.get(key);
		if(null == result){
			key =  goodsBigType + Cat.underline + "-1" ;
		}
		result = this.adapterMap.get(key);
		return (null == result)?this.defaultAdapter : result ;
	}
	
	
	private void initAutionAdapter(){
		String fileName = XlsSheetNameType.auction_adapter.getXlsName();
		String sheetName = XlsSheetNameType.auction_adapter.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			adapterMap = XlsPojoUtil.sheetToGenericMap(sourceFile,
					sheetName, AuctionMenuAdapter.class);
			if(Util.isEmpty(adapterMap)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("init auctionAdapter error,have any config record");
			}
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("init auctionAdapter error",ex);
			
		}
	}
	
	
	private void initFeeInfoConfig(){
		String fileName = XlsSheetNameType.auction_fee_info.getXlsName();
		String sheetName = XlsSheetNameType.auction_fee_info.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			List<FeeInfoConfig> list = XlsPojoUtil.sheetToList(sourceFile,
					sheetName, FeeInfoConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.checkFail();
				Log4jManager.CHECK.error("init FeeInfoConfig error,have any config record");
			}
			this.feeInfoConfig = list.get(0);
		}catch(Exception ex){
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("init FeeInfoConfig error",ex);
			
		}
	}
	

	/** 加载拍卖行菜单分类结构 */
	private void initAuctionMenu() {
		String fileName = XlsSheetNameType.auction_menu.getXlsName();
		String sheetName = XlsSheetNameType.auction_menu.getSheetName();
		try {
			String sourceFile = GameContext.getPathConfig().getXlsPath()
					+ fileName;
			List<AuctionMenu> menuList = XlsPojoUtil.sheetToList(sourceFile,
					sheetName, AuctionMenu.class);
			// 避免策划配置顺序没有按照父节点先于子节点,遍历2次
			Map<Short, AuctionMenu> result = Maps.newLinkedHashMap();
			for (AuctionMenu menu : menuList) {
				// 遍历父节点
				if (null == menu) {
					continue;
				}
				if (!menu.isParentMenu()) {
					continue;
				}
				result.put(menu.getId(), menu);
			}
			for (AuctionMenu menu : menuList) {
				// 遍历子节点
				if (null == menu) {
					continue;
				}
				if (menu.isParentMenu()) {
					continue;
				}
				short parentId = menu.getParentId();
				AuctionMenu parentMenu = result.get(parentId);
				if (null == parentMenu) {
					Log4jManager.checkFail();
					Log4jManager.CHECK
							.error("auction menu conf error,parentMenu pid="
									+ parentId);
					continue;
				}
				parentMenu.addSubMenu(menu);
			}
			this.auctionMenuList.clear();
			this.auctionMenuList.addAll(result.values());
			result.clear();
			result = null;
		} catch (Exception e) {
			Log4jManager.checkFail();
			Log4jManager.CHECK.error("loadExel error : sourceFile = "
					+ fileName + " sheetName =" + sheetName
					+ ",the system will shutdown .... ", e);
		}
	}

	@Override
	public List<AuctionMenu> getAuctionMenuList() {
		return this.auctionMenuList;
	}

	public Client getMercuryClient() {
		return mercuryClient;
	}

	public void setMercuryClient(Client mercuryClient) {
		this.mercuryClient = mercuryClient;
	}

	
	private int roleAuctionSize(RoleInstance role){
		try{
			C5401_MercurySearchRespMessage respMsg = this.queryRoleAuctionList(role.getRoleId());
			if(null == respMsg || respMsg.getStatus() != RespTypeStatus.SUCCESS){
				return -1 ;
			}
			return respMsg.getTotalRecordNum();
		}catch(Exception ex){
			logger.error("",ex);
			return -1 ;
		}
	}
	
	@Override
	public C5401_MercurySearchRespMessage queryRoleAuctionList(String roleId) throws Exception{
		C5401_MercurySearchReqMessage reqMsg = new C5401_MercurySearchReqMessage();
		reqMsg.setPageSize(MAX_RECORD_SIZE*2);
		reqMsg.setPageIndex(0);
		List<MercurySearchIndexItem> indexItems = new ArrayList<MercurySearchIndexItem>();
		// appId,serverId
		indexItems.addAll(this.getGlobalSearchItems());
		// roleId
		MercurySearchIndexItem item = new MercurySearchIndexItem();
		item.setIndexType(IndexType.NOT_BLUR_MATCH);
		item.setIndexName(IndexableRow.OWNER_ID);
		item.setIndexValue(roleId);
		indexItems.add(item);
		reqMsg.setIndexItems(indexItems);
		// 首要条件为按照id降序排列
		reqMsg.setSortItems(this.buildDefaultSort(null,false,true));
		//包含过期的
		reqMsg.setIncludeExpired((byte)1);
		return (C5401_MercurySearchRespMessage) this.getMercuryClient().sendMessage(reqMsg);
	}
	
	
	@Override
	public SearchResult getRoleAuctionList(RoleInstance role) {
		return this.getRoleAuctionList(role,true);
	}
	
	private String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	private String messageFormat(String textId,Object... args){
		return GameContext.getI18n().messageFormat(textId, args);
	}

	@Override
	public void notifyHaveExpiredGoods(RoleInstance role){
		try {
			if (null == role) {
				return;
			}
			SearchResult result = this.getRoleAuctionList(role, false);
			if (null == result || !result.isSuccess()) {
				return;
			}
			if (Util.isEmpty(result.getRecords())) {
				return;
			}
			for (MercuryRecordItem record : result.getRecords()) {
				// 设置剩余有效时间(分钟)
				Date date = record.getExpiredTime();
				if (null == date) {
					continue;
				}
				long diffMin = DateUtil.dateDiffMinute(date, new Date());
				if (diffMin <= 0) {
					// 发送有过期物品
					C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
					msg.setMsgContext(this.getText(TextId.Auction_Have_Expired_Goods));
					role.getBehavior().sendMessage(msg);
					return;
				}
			}
		}catch(Exception ex){
			logger.error("notifyhaveExpiredGoods error",ex);
		}
	}
	
	private SearchResult getRoleAuctionList(RoleInstance role,boolean setSearchTime) {
		SearchResult result = new SearchResult();
		if(setSearchTime){
			long now = System.currentTimeMillis();
			if (now - role.getAuctionMyShelfSearchTime() <= this.searchCd) {
				result.setTips(Status.Auction_Search_Frequently.getTips());
				return result;
			}
			role.setAuctionMyShelfSearchTime(now);
		}
		try {
			C5401_MercurySearchRespMessage mercuyResp = this.queryRoleAuctionList(role.getRoleId()) ;
			if (SUCCESS != mercuyResp.getStatus()) {
				result.setTips(mercuyResp.getInfo());
				return result;
			}
			result.setTotalPage(this.getTotalPage(mercuyResp
					.getTotalRecordNum(), PAGE_SIZE));
			result.setRecords(mercuyResp.getRecords());
			result.flagSuccess();
		} catch (Exception e) {
			result.setTips(Status.Auction_Sys_Frequently.getTips());
			logger.error("", e);
		}
		return result;
	}

	private int getTotalPage(int recordNum, int pageSize) {
		if (0 >= recordNum) {
			return 0;
		}
		return (0 == recordNum % pageSize) ? recordNum / pageSize : recordNum
				/ pageSize + 1;
	}

	public AuctionSearchItem convertSearchItem(MercuryRecordItem record) {
		if (null == record) {
			return null;
		}
		AuctionSearchItem item = new AuctionSearchItem();
		this.setSearchBaseItem(item, record);
		item.setSaleRole(record.getOwnerName());
		return item;
	}

	public AuctionMyShelfItem convertMyShelfItem(MercuryRecordItem record) {
		if (null == record) {
			return null;
		}
		AuctionMyShelfItem item = new AuctionMyShelfItem();
		this.setSearchBaseItem(item, record);
		// 设置剩余有效时间(分钟)
		Date date = record.getExpiredTime();
		if (null != date) {
			long diffMin = DateUtil.dateDiffMinute(date, new Date());
			item.setEffectTime(diffMin > 0 ? (short)diffMin : (short) 0);
		}
		return item;
	}

	private void setSearchBaseItem(AuctionSearchBaseItem item,
			MercuryRecordItem record) {
		item.setId(String.valueOf(record.get__id__()));
		item.setPriceType((byte) record.getPriceType());
		item.setPrice(record.getPriceNum());
		
		RoleGoods rg = this.toRoleGoods(record);
		if (null == rg) {
			// 钱
			Money money = this.toMoney(record);
			item.setType(MONEY_TYPE);
			item.setSecondType(money.getMoneyType().getType());
			GoodsLiteNamedItem goodsLiteItem = new GoodsLiteNamedItem();
			goodsLiteItem.setGoodsName(money.getName());
			goodsLiteItem.setGoodsImageId(money.getMoneyType().getImageId());
			goodsLiteItem.setQualityType((byte)1);
			item.setGoodsInfo(goodsLiteItem);
			return;
		}
		// 物品
		int goodsId = rg.getGoodsId();
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if (gb == null) {
			return;
		}
		// 物品类型
		item.setType((byte) gb.getGoodsType());
		item.setSecondType((byte) gb.getSecondType());
		GoodsLiteNamedItem goodsInfo = gb.getGoodsLiteNamedItem();
		// 获得强化等级
		int starLv = record.getIndex7();
		if (starLv > 0) {
			goodsInfo.setGoodsName(goodsInfo.getGoodsName() + "+" + starLv);
		}
		goodsInfo.setNum((short)record.getItemNum());
		
		//绑定类型
		goodsInfo.setBindType(this.getBindType(gb));
		//物品信息
		item.setGoodsInfo(goodsInfo);
		item.setResId((short) gb.getResId());
		item.setLvLimit((byte) gb.getLvLimit());
	}

	private List<MercurySearchIndexItem> buildIndex(RoleInstance role,
			C0850_AuctionSearchReqMessage searchReq) {
		List<MercurySearchIndexItem> indexItems = new ArrayList<MercurySearchIndexItem>();
		// appId,serverId
		indexItems.addAll(this.getGlobalSearchItems());
		// 其他条件
		byte bigType = searchReq.getBigType();
		if (bigType >= 0) {
			MercurySearchIndexItem item = new MercurySearchIndexItem();
			item.setIndexType(IndexType.NOT_BLUR_MATCH);
			item.setIndexName(BIG_TYPE);
			item.setIndexValue(String.valueOf(bigType));
			indexItems.add(item);
		}
		byte smallType = searchReq.getSmallType();
		if (smallType >= 0) {
			MercurySearchIndexItem item = new MercurySearchIndexItem();
			item.setIndexType(IndexType.NOT_BLUR_MATCH);
			item.setIndexName(SMALL_TYPE);
			item.setIndexValue(String.valueOf(smallType));
			indexItems.add(item);
		}
		if (bigType == MONEY_TYPE) {
			return indexItems;
		}
		String name = searchReq.getName();
		if (!Util.isEmpty(name)) {
			MercurySearchIndexItem item = new MercurySearchIndexItem();
			item.setIndexType(IndexType.BLUR_MATCH);
			item.setIndexName(ITEM_NAME);
			item.setIndexValue(name);
			indexItems.add(item);
		}
		byte quality = searchReq.getQualityType();
		if (quality >= 0) {
			MercurySearchIndexItem item = new MercurySearchIndexItem();
			item.setIndexType(IndexType.NOT_BLUR_MATCH);
			item.setIndexName(QUALITY);
			item.setIndexValue(String.valueOf(quality));
			indexItems.add(item);
		}
		byte priceType = searchReq.getMoneyType();
		if (priceType >= 0) {
			MercurySearchIndexItem item = new MercurySearchIndexItem();
			item.setIndexType(IndexType.NOT_BLUR_MATCH);
			item.setIndexName(PRICE_TYPE);
			item.setIndexValue(String.valueOf(priceType));
			indexItems.add(item);
		}
		boolean usable = (1 == searchReq.getUsable());
		int minLv = Math.max(0, searchReq.getMinLevel());
		int maxLv = searchReq.getMaxLevel();
		// 等级需求
		if (usable) {
			// 等级
			maxLv = role.getLevel();
			// 加职业
			MercurySearchIndexItem item = new MercurySearchIndexItem();
			item.setIndexType(IndexType.OR_MATCH);
			item.setIndexName(CAREER);
			item.setIndexValue("-1," + role.getCareer());
			indexItems.add(item);
		}
		if (minLv <= 0 && maxLv <= 0) {
			return indexItems;
		}
		MercurySearchIndexItem lvItem = new MercurySearchIndexItem();
		lvItem.setIndexType(IndexType.RANGE_MATCH);
		lvItem.setIndexName(LEVEL);
		lvItem.setIndexValue(minLv + "," + maxLv);
		indexItems.add(lvItem);
		return indexItems;
	}

	private void appendSortItem(List<MercurySearchSortItem> sortItems,
			String sortName, boolean desc, String firstSortName) {
		if (sortName.equals(firstSortName)) {
			return;
		}
		MercurySearchSortItem item = new MercurySearchSortItem();
		item.setDesc(desc);
		item.setSortName(sortName);
		sortItems.add(item);
	}

	private List<MercurySearchSortItem> buildDefaultSort(String firstSort,
			boolean firstSortDesc, boolean idSort) {
		List<MercurySearchSortItem> sortItems = new ArrayList<MercurySearchSortItem>();
		if (null == firstSort) {
			firstSort = "";
		}
		if (firstSort.trim().length() > 0) {
			MercurySearchSortItem item = new MercurySearchSortItem();
			item.setDesc(firstSortDesc);
			item.setSortName(firstSort);
			sortItems.add(item);
		}
		if (idSort) {
			this.appendSortItem(sortItems, ID, true, firstSort);
		}
		//this.appendSortItem(sortItems, VIP, true, firstSort);
		this.appendSortItem(sortItems, QUALITY, true, firstSort);
		this.appendSortItem(sortItems, LEVEL, true, firstSort);
		this.appendSortItem(sortItems, PRICE_NUM, false, firstSort);
		return sortItems;
	}

	private String getFirstSort(byte sortType) {
		// 手动排序字段
		String firstField = null;
		if (sortType >= 0) {
			if (0 == sortType) {
				// 等级
				firstField = LEVEL;
			} else if (1 == sortType) {
				// 价格
				firstField = PRICE_NUM;
			} else if (2 == sortType) {
				// 剩余时间
				firstField = REMAIN_TIME;
			} else if (3 == sortType) {
				// 单价
				firstField = SINGLE_PRICE;
			}
		}
		return firstField;
	}

	@Override
	public SearchResult search(RoleInstance role,
			C0850_AuctionSearchReqMessage searchReq) {
		SearchResult result = new SearchResult();
		String name = searchReq.getName();
		if (null != name && name.trim().length() > 0) {
			if (!GameContext.getIllegalWordsService().isCNorENorFigure(name)) {
				result.setTips(Status.Auction_Input_Keyword.getTips());
				return result;
			}
		}
		if (searchReq.getMaxLevel() < searchReq.getMinLevel()) {
			result.setTips(Status.Auction_Input_Level_Error.getTips());
			return result;
		}
		long now = System.currentTimeMillis();
		if (now - role.getAuctionSearchTime() <= this.searchCd) {
			result.setTips(Status.Auction_Search_Frequently.getTips());
			return result;
		}
		// 设置最后search时间
		role.setAuctionSearchTime(now);

		C5401_MercurySearchReqMessage reqMsg = new C5401_MercurySearchReqMessage();
		reqMsg.setPageIndex(searchReq.getCurrPage());
		reqMsg.setPageSize(PAGE_SIZE);
		reqMsg.setIndexItems(this.buildIndex(role, searchReq));

		reqMsg.setSortItems(this.buildDefaultSort(this.getFirstSort(searchReq
				.getSortFiled()), searchReq.getDesc() == 0 ? false : true,
				false));
		try {
			C5401_MercurySearchRespMessage respMsg = (C5401_MercurySearchRespMessage) this
					.getMercuryClient().sendMessage(reqMsg);
			if (null == respMsg) {
				return result;
			}
			result.setStatus(respMsg.getStatus());
			result.setTips(respMsg.getInfo());
			result.setTotalPage(this.getTotalPage(respMsg.getTotalRecordNum(),
					PAGE_SIZE));
			result.setRecords(respMsg.getRecords());
		} catch (Exception e) {
			logger.error("", e);
		}
		return result;
	}

	private boolean isCorrectTime(int timeHours) {
		return timeHours == HOURS_8 || timeHours == HOURS_24 || timeHours == HOURS_48;
	}

	private int getManageFee(int timeHours) {
		if(timeHours == HOURS_8){
			return this.feeInfoConfig.getFee8() ;
		}
		if(timeHours == HOURS_24){
			return this.feeInfoConfig.getFee24() ;
		}
		return this.feeInfoConfig.getFee48() ;
	}
	
	
	private Result putRemote(MercuryRecordItem item){
		Result result = new Result() ;
		if (null == item) {
			result.setInfo(Status.Auction_Input_Error.getTips());
			return result;
		}
		
		result.setInfo(this.getText(TextId.SYSTEM_ERROR));
		
		C5403_MercuryPutReqMessage reqMsg = new C5403_MercuryPutReqMessage();
		reqMsg.setItem(item);
		try {
			C5400_MercuryRespMessage respMsg = (C5400_MercuryRespMessage)this.getMercuryClient().sendMessage(
					reqMsg);
			result.setResult(respMsg.getStatus());
			if (SUCCESS != respMsg.getStatus()) {
				//失败
				result.setInfo(respMsg.getInfo());
				this.logPut(item, FAILURE_CODE);
			}
		} catch (Ice.ConnectTimeoutException ex) {
			logger.error("Ice.ConnectTimeoutException", ex);
			this.logPut(item, FAILURE_CODE);
			return result;
		} catch (Ice.TimeoutException ex) {
			logger.error("Ice.TimeoutException", ex);
			// !!! 这种情况 流程继续删除用户物品,否则会发生刷物品bug
			//!!! 需要视为成功
		} catch (Exception e) {
			logger.error("", e);
			this.logPut(item, FAILURE_CODE);
			return result;
		}
		//成功
		this.logPut(item, SUCCESS_CODE);
		result.setResult(SUCCESS);
		result.setInfo("");
		return result ;
	}

	@Override
	public Result putGoods(RoleInstance role, String goodsId, Money money,
			int timeHours) {
		Result result = new Result();
		//判断开启等级
		int upOpenLevel = GameContext.getParasConfig().getAuctionUpOpenRoleLevel() ;
		if(role.getLevel() < upOpenLevel){
			result.setInfo(this.messageFormat(TextId.AUCTION_UP_ROLE_LEVEL_TIPS,upOpenLevel));
			return result ;
		}
		if (null == money || !money.isCorrect() || !money.canExchange()
				|| !this.isCorrectTime(timeHours)) {
			result.setInfo(Status.Auction_Input_Error.getTips());
			return result;
		}
		RoleGoods rg = role.getRoleBackpack().getRoleGoodsByInstanceId(goodsId);
		if (null == rg) {
			result.setInfo(Status.Auction_Goods_Not_Exist.getTips());
			return result;
		}
		if (RoleGoodsHelper.hadBind(rg)) {
			result.setInfo(Status.Auction_Bind_Goods_Not_Up.getTips());
			return result;
		}
		if (!RoleGoodsHelper.isForever(rg)) {
			result.setInfo(Status.Auction_Time_Goods_Not_Up.getTips());
			return result;
		}
		//元宝价格必须>=2
		int minGlodMoney = this.getFeeInfoConfig().getMinGlodMoney();
		int less_money = this.getFeeInfoConfig().getLessMoney(MoneyType.rmb);
		if(money.getMoneyType() == MoneyType.rmb && money.getNum() < minGlodMoney){
			result.setInfo(this.messageFormat(TextId.Auction_Too_Less_Gold_Money_For_Up, 
					String.valueOf(less_money)));
			return result;
		}else{
			//金币必须>=1000
			int minGameMoney = this.getFeeInfoConfig().getMinGameMoney();
			less_money = this.getFeeInfoConfig().getLessMoney(MoneyType.game);
			if(money.getMoneyType() == MoneyType.game && money.getNum() < minGameMoney){
				result.setInfo(this.messageFormat(TextId.Auction_Too_Less_Game_Money_For_Up, 
						String.valueOf(less_money)));
				return result;
			}
		}
		
		// 计算手续费
		int fee = this.getManageFee(timeHours);
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, fee);
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess()){
			result.setInfo(Status.Auction_Money_Not_Enough.getTips());
			return result;
		}
//		if (role.getSilverMoney() < fee) {
//			result.setInfo(Status.Auction_Money_Not_Enough.getTips());
//			return result;
//		}
		//判断是否达上限
		if(this.roleAuctionSize(role) >= MAX_RECORD_SIZE){
			result.setInfo(this.messageFormat(TextId.AUCTION_UP_FULL_TIPS, MAX_RECORD_SIZE));
			return result;
		}
		
		MercuryRecordItem item = this.toRecordItem(role, rg, money, timeHours);
		result = this.putRemote(item);
		if(!result.isSuccess()){
			return result ;
		}
		result.setInfo(Status.Auction_Up_Is_Check.getTips());
		// 删除背包内物品
		GameContext.getUserGoodsApp().deleteForBagByInstanceId(role, goodsId,
				OutputConsumeType.auction_shop_goods_up);
		if (fee > 0) {
			// 扣钱
			GameContext.getUserAttributeApp().changeRoleMoney(role,
					AttributeType.gameMoney, OperatorType.Decrease, fee,
					OutputConsumeType.auction_shop_manage_fee);
			// 刷新客户端钱币
			role.getBehavior().notifyAttribute();
		}
		return result;
	}
	
	

	/**
	 * 此方法不会调用
	 */
	@Override
	public void expiredRecords() {
		long minItemId = 0;
		while (true) {
			C5405_MercuryExpiredReqMessage reqMsg = new C5405_MercuryExpiredReqMessage();
			reqMsg.setAppId(GameContext.getAppId());
			reqMsg.setServerId(GameContext.getServerId());
			reqMsg.setMinRecordId(minItemId);
			reqMsg.setMaxRecords(expiredMaxRecords);
			try {
				C5405_MercuryExpiredRespMessage respMsg = (C5405_MercuryExpiredRespMessage) this
						.getMercuryClient().sendMessage(reqMsg);
				if (null == respMsg) {
					return;
				}
				List<MercuryRecordItem> records = respMsg.getRecords();
				if (Util.isEmpty(records)) {
					return;
				}
				for (MercuryRecordItem item : records) {
					this.sendExpiredRecord(item);
				}
				int size = records.size();
				if (0 == respMsg.getHaveNext() || size < expiredMaxRecords) {
					// 已经完毕
					return;
				}
				minItemId = records.get(size - 1).get__id__();
			} catch (Exception e) {
				logger.error("", e);
				return;
			}
		}

	}
	
	/*private  boolean isCloseAuctionMoneyChannel(int channelId){
		String channels = GameContext.getParasConfig().getCloseAuctionMoneyChannels();
		if(Util.isEmpty(channels)){
			return false ;
		}
		String all = "," + channels.trim() + "," ;
		String the = "," + channelId + "," ;
		return all.indexOf(the) >=0 ;
	}*/

	@Override
	public Result putMoney(RoleInstance role, Money money, Money price,
			int timeHours) {
		Result result = new Result();
		/*if(this.isCloseAuctionMoneyChannel(role.getChannelId())){
			result.setInfo(this.getText(RespStatus.AUCTION_UP_MONEY_CLOSED));
			return result ;
		}*/
		if(!this.feeInfoConfig.hasUpGameMoney() && money.getMoneyType() == MoneyType.game){
			result.setInfo(this.getText(TextId.AUCTION_UP_MONEY_CLOSED));
			return result ;
		}
		if(!this.feeInfoConfig.hasUpGoldMoney() && money.getMoneyType() == MoneyType.rmb){
			result.setInfo(this.getText(TextId.AUCTION_UP_MONEY_CLOSED));
			return result ;
		}
		//判断开启等级
		int upOpenLevel = GameContext.getParasConfig().getAuctionUpOpenRoleLevel() ;
		if(role.getLevel() < upOpenLevel){
			result.setInfo(this.messageFormat(TextId.AUCTION_UP_ROLE_LEVEL_TIPS,upOpenLevel));
			return result ;
		}
		if (null == money || null == price || !money.isCorrect()
				|| !price.isCorrect() || !money.canExchange()
				|| !price.canExchange()
				|| money.getMoneyType() == price.getMoneyType()
				|| !this.isCorrectTime(timeHours)) {
			result.setInfo(Status.Auction_Input_Error.getTips());
			return result;
		}
		//元宝价格必须>=2
		int minGlodMoney = this.getFeeInfoConfig().getMinGlodMoney();
		int less_money = this.getFeeInfoConfig().getLessMoney(MoneyType.rmb);
		if(price.getMoneyType() == MoneyType.rmb && price.getNum() < minGlodMoney){
			result.setInfo(this.messageFormat(TextId.Auction_Too_Less_Gold_Money_For_Up, 
					String.valueOf(less_money)));
			return result;
		}else{
			//金币必须>=1000
			int minGameMoney = this.getFeeInfoConfig().getMinGameMoney();
			less_money = this.getFeeInfoConfig().getLessMoney(MoneyType.game);
			if(money.getMoneyType() == MoneyType.game && money.getNum() < minGameMoney){
				result.setInfo(this.messageFormat(TextId.Auction_Too_Less_Game_Money_For_Up, 
						String.valueOf(less_money)));
				return result;
			}
		}
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, money.getMoneyType().getAttributeType(), money.getNum());
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess()){
			result.setInfo(Status.Auction_Money_Name_Not_Enough.getTips().replace(Wildcard.MoneyName, money.getMoneyType().getAttributeType().getName()));
			return result;
		}
		// 判断钱是否足够
//		if (role.get(money.getMoneyType().getAttributeType()) < money.getNum()) {
//			result.setInfo(Status.Auction_Money_Name_Not_Enough.getTips()
//					.replace(Wildcard.MoneyName,
//							money.getMoneyType().getAttributeType().getName()));
//			return result;
//		}
		//判断手续费是否足够
		int fee = this.getManageFee(timeHours) ;
		int add = money.getMoneyType() == MoneyType.game ? money.getNum():0 ;
		ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.gameMoney, (fee + add));
		if(ar.isIgnore()){
			return ar;
		}
		if(!ar.isSuccess()){
			result.setInfo(Status.Auction_Money_Not_Enough.getTips());
			return result;
		}
		
//		if (role.getSilverMoney() < (fee + add)) {
//			result.setInfo(Status.Auction_Money_Not_Enough.getTips());
//			return result;
//		}
		//判断是否达上限
		if(this.roleAuctionSize(role) >= MAX_RECORD_SIZE){
			result.setInfo(this.messageFormat(TextId.AUCTION_UP_FULL_TIPS, MAX_RECORD_SIZE));
			return result;
		}
		String moneyItemId = this.generateMoneyId(GameContext.getServerId());
		if(null == moneyItemId){
			result.setInfo(this.getText(TextId.SYSTEM_ERROR));
			return result ;
		}
		MercuryRecordItem item = this.toRecordItem(role, money, price,
				timeHours,moneyItemId);
		result = this.putRemote(item);
		if(!result.isSuccess()){
			return result ;
		}
		result.setInfo(Status.Auction_Up_Is_Check.getTips());
		// 删除钱币
		GameContext.getUserAttributeApp().changeRoleMoney(role,
				money.getMoneyType().getAttributeType(), OperatorType.Decrease,
				money.getNum(), OutputConsumeType.auction_shop_goods_up);
		//扣除手续费
		GameContext.getUserAttributeApp().changeRoleMoney(role,
				AttributeType.gameMoney, OperatorType.Decrease, fee,
				OutputConsumeType.auction_shop_manage_fee);
		// 刷新客户端钱币
		role.getBehavior().notifyAttribute();
		return result;
	}

	@Override
	public Result delisting(RoleInstance role, String id) {
		return this.removeMercury(role, id, false);
	}

	private Result removeMercury(RoleInstance role, String id, boolean buy) {
		Result result = new Result();
		MercuryRecordItem item = this.getRecord(id);
		if (null == item) {
			result.setInfo(Status.Auction_Record_Not_Exist.getTips());
			return result;
		}
		// 判断是否有权限
		if (!this.isServerMatch(item)) {
			result.setInfo(Status.Auction_No_Power_Goods.getTips());
			return result;
		}
		if (!buy && !this.isRoleMatch(item, role.getRoleId())) {
			result.setInfo(Status.Auction_No_Power_Goods.getTips());
			return result;
		}
		if (buy && this.isRoleMatch(item, role.getRoleId())) {
			result.setInfo(Status.Auction_Not_Buy_Self_Goods.getTips());
			return result;
		}
		if (buy) {
			//判断购买开启等级
			int buyOpenLevel = GameContext.getParasConfig().getAuctionBuyOpenRoleLevel() ;
			if(role.getLevel() < buyOpenLevel){
				result.setInfo(this.messageFormat(TextId.AUCTION_BUY_ROLE_LEVEL_TIPS,buyOpenLevel));
				return result ;
			}
			Money price = new Money((byte) item.getPriceType(), item
					.getPriceNum());
			if (!price.isCorrect()) {
				result.setInfo(Status.Auction_Record_Error.getTips());
				return result;
			}
			//【游戏币/潜能/钻石不足弹板】 判断
			Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, price.getMoneyType().getAttributeType(), price.getNum());
			if(ar.isIgnore()){  //弹板
				return ar;
			}
			if(!ar.isSuccess()){//不足
				result.setInfo(Status.Auction_Money_Name_Not_Enough_Buy.getTips().replace(
								Wildcard.MoneyName,
								price.getMoneyType().getAttributeType().getName()));
				return result;
			}
			// 判断购买者钱币是否足够
//			if (!price.isEnough(role)) {
//				result.setInfo(Status.Auction_Money_Name_Not_Enough_Buy
//						.getTips().replace(
//								Wildcard.MoneyName,
//								price.getMoneyType().getAttributeType()
//										.getName()));
//				return result;
//			}
		}
		Result removeResult = this.sendMercuryRemoveMsg(role.getRoleId(), id);
		if(!removeResult.isSuccess()){
			return removeResult;
		}
		// 日志
		this.logRemove(item, role.getRoleId(), SUCCESS_CODE);
		if (buy) {
			this.buyMail(role, item);
			result.setInfo(Status.Auction_Buy_Success.getTips());
		} else {
			this.delistingMail(role.getRoleId(), item);
			result.setInfo(Status.Auction_Down_Success.getTips());
		}
		result.setResult(SUCCESS);
		return result;
	}
	
	private Result sendMercuryRemoveMsg(String roleId, String id){
		// 发送删除信息
		Result result = new Result();
		result.failure();
		C5404_MercuryRemoveReqMessage reqMsg = new C5404_MercuryRemoveReqMessage();
		reqMsg.setId(id);
		reqMsg.setAppId(GameContext.getAppId());
		reqMsg.setServerId(GameContext.getServerId());
		reqMsg.setOwnerId(roleId);
		try {
			C5402_MercuryRecordInfoRespMessage respMsg = (C5402_MercuryRecordInfoRespMessage) this
					.getMercuryClient().sendMessage(reqMsg);
			if (null == respMsg) {
				result.setInfo(Status.Auction_Record_Not_Exist.getTips());
				return result;
			}
			result.setResult(respMsg.getStatus());
			result.setInfo(respMsg.getInfo());
			return result;
		} catch (Exception e) {
			logger.error("", e);
			return result;
		}
	}

	private void delistingMail(String roleId, MercuryRecordItem item) {
		// 将物品/金钱邮寄到用户邮箱
		this.sendRecord(roleId, item, Status.Auction_Sell_Down.getTips(),
				OutputConsumeType.auction_shop_goods_down,false);
	}

	private void sendRecord(String roleId, MercuryRecordItem item,
			String title, OutputConsumeType ocType,boolean buy) {
		RoleGoods rg = this.toRoleGoods(item);
		Money money = this.toMoney(item);
		String mailContext = "" ;
		if(buy){
			Money price = new Money((byte) item.getPriceType(), item.getPriceNum());
			mailContext = this.messageFormat(TextId.AUCTION_BUY_SUCESS_MAIL_TEXT,
					price.getName(),
					item.getItemName() + "*" + item.getItemNum());
		}else{
			mailContext = this.messageFormat(TextId.AUCTION_DOWN_SUCCESS_MAIL_TEXT,
					item.getItemName() + "*" + item.getItemNum());
		}
		this.sendMoneyAndGoods(roleId, money, rg, title, mailContext,
				ocType);
	}

	private void sendExpiredRecord(MercuryRecordItem item) {
		// 发送到期物品
		if (null == item) {
			return;
		}
		try {
			this.logExpired(item, SUCCESS_CODE);
		} catch (Exception ex) {
			logger.error("", ex);
		}
		try {
			RoleGoods rg = this.toRoleGoods(item);
			Money money = this.toMoney(item);
			this.sendMoneyAndGoods(item.getOwnerId(), money, rg,
					Status.Auction_Sell_Down.getTips(),
					Status.Auction_Goods_Down.getTips(),
					OutputConsumeType.auction_shop_goods_due);
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}

	private void buyMail(RoleInstance buyer, MercuryRecordItem item) {
		Money price = new Money((byte) item.getPriceType(), item.getPriceNum());
		if (price.isCorrect()) {
			// 扣除钱币
			GameContext.getUserAttributeApp().changeRoleMoney(buyer,
					price.getMoneyType().getAttributeType(),
					OperatorType.Decrease, price.getNum(),
					OutputConsumeType.auction_shop_buy_minus_money);
			buyer.getBehavior().notifyAttribute();
		}
		// 将物品/金钱邮寄到购买者邮箱中
		this.sendRecord(buyer.getRoleId(), item, Status.Auction_Buy_Success.getTips(),
				OutputConsumeType.auction_shop_buy_goods,true);
		
		// 将所得金钱邮寄到出售者邮箱中
		if (price.isCorrect()) {
			//扣除交易税
			int fee = 0 ;
			float businessFeeRate = this.getFeeInfoConfig().getBusinessFeeRate();
			if(price.getMoneyType() == MoneyType.rmb){
				//取上整
				fee = (int)Math.ceil(price.getNum()*businessFeeRate);
			}else{
				//取下整
				fee = (int)(price.getNum()*businessFeeRate);
			}
			price.setNum(price.getNum() - fee);
			String moneyName = price.getMoneyType().getAttributeType().getName() ;
			String mailText = this.messageFormat(TextId.AUCTION_SELL_SUCESS_MAIL_TEXT,
					item.getItemName() + "*" + item.getItemNum(), 
					buyer.getRoleName(),
					moneyName + "*" + fee ,
					moneyName + "*" + price.getNum()) ;
			this.sendMoneyAndGoods(item.getOwnerId(), price, null,
					Status.Auction_Sell_Money.getTips(),
					mailText,
					OutputConsumeType.auction_shop_sell_add_money);
		}
	}
	
	

	private void sendMoneyAndGoods(String roleId, Money money,
			RoleGoods roleGoods, String title, String context,
			OutputConsumeType ocType) {
		if (null == money && null == roleGoods) {
			return;
		}
		try {
			Mail mail = new Mail(IdFactory.getInstance().nextId(IdType.MAIL));
			mail.setSendRole(MailSendRoleType.Auction.getName());
			mail.setTitle(title);
			mail.setContent(context);
			mail.setRoleId(roleId);
			mail.setSendSource(ocType.getType());
			if (null != money) {
				if (MoneyType.rmb == money.getMoneyType()) {
					mail.setGold(money.getNum());
				} else if (MoneyType.game == money.getMoneyType()) {
					mail.setSilverMoney(money.getNum());
				}
			}
			if (null != roleGoods) {
				mail.addRoleGoods(roleGoods);
			}
			GameContext.getMailApp().sendMail(mail);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Override
	public Result buy(RoleInstance role, String id) {
		return this.removeMercury(role, id, true);
	}

	@Override
	public MercuryRecordItem getRecord(String id) {
		C5402_MercuryGetReqMessage reqMsg = new C5402_MercuryGetReqMessage();
		reqMsg.setId(id);
		try {
			C5402_MercuryRecordInfoRespMessage respMsg = (C5402_MercuryRecordInfoRespMessage) this
					.getMercuryClient().sendMessage(reqMsg);
			if (null == respMsg || SUCCESS != respMsg.getStatus()) {
				return null;
			}
			return respMsg.getRecordItem();
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	private Money toMoney(MercuryRecordItem record) {
		if (null == record) {
			return null;
		}
		if (MONEY_TYPE != record.getIndex1()) {
			return null;
		}
		return new Money((byte) record.getIndex2(), record.getItemNum());
	}

	@Override
	public RoleGoods toRoleGoods(MercuryRecordItem item) {
		// 当前数目
		// attrVar
		// otherParm
		if (null == item) {
			return null;
		}
		if (MONEY_TYPE == item.getIndex1()) {
			return null;
		}
		RoleGoods rg = new RoleGoods();
		int templateId = item.getItemTemplateId();
		rg.setGoodsId(templateId);
		rg.setId(item.getItemId());
		rg.setRoleId(item.getOwnerId());
		rg.setCurrOverlapCount((short) item.getItemNum());
		rg.setAttrVar(item.getStore1());
		rg.setOtherParm(item.getStore2());
		rg.setMosaic(item.getStore3());
		// rg.setPunched(item.getStore4());
		String bindInfo = item.getIndex8();
		if (Util.isEmpty(bindInfo)) {
			// 根据模板
			rg.setBind(BindingType.equip_binding.getType());
		} else {
			rg.setBind(Byte.parseByte(bindInfo));
		}
		rg.setStrengthenLevel((byte) item.getIndex7());// 强化等级
		
		// 调用init 生成基本属性值
		RoleGoodsHelper.init(rg);
		return rg;
	}
	
	
	private MercuryRecordItem toRecordItem(RoleInstance role, Money money,
			Money price, int timeHours,String moneyItemId) {
		MercuryRecordItem item = new MercuryRecordItem();
		item.setAppId(GameContext.getAppId());
		item.setServerId(GameContext.getServerId());
		item.setOwnerId(role.getRoleId());
		item.setOwnerName(role.getRoleName());
		item.setPriceNum(price.getNum());
		item.setPriceType(price.getMoneyType().getType());
		item.setItemNum(money.getNum());
		item.setItemName(money.getMoneyType().getAttributeType().getName());
		item.setItemId(moneyItemId);

		item.setIndex1(MONEY_TYPE); // 大类
		item.setIndex2(money.getMoneyType().getType()); // 小类
		//index5为职业，钱币的情况必须设置为-1
		item.setIndex5(-1);
		//item.setIndex6(role.getVipLevel()); // vip等级
		item
				.setExpiredTime(DateUtil.addSecond(new Date(),
						timeHours * 60 * 60));
		return item;
	}

	private MercuryRecordItem toRecordItem(RoleInstance role, RoleGoods rg,
			Money money, int timeHours) {
		if (null == rg) {
			return null;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(rg.getGoodsId());
		if (null == gb) {
			return null;
		}
		// 必须调用否则一些衍生信息将丢失
		RoleGoodsHelper.destructor(rg);
		
		MercuryRecordItem item = new MercuryRecordItem();
		item.setAppId(GameContext.getAppId());
		item.setServerId(GameContext.getServerId());
		item.setOwnerId(rg.getRoleId());
		item.setOwnerName(role.getRoleName());
		item.setItemId(rg.getId());
		item.setItemName(gb.getName());
		item.setItemNum(rg.getCurrOverlapCount());
		item.setItemTemplateId(gb.getId());
		item.setPriceType(money.getMoneyType().getType());
		item.setPriceNum(money.getNum());

		AuctionMenuAdapter adapter = this.getAutionAdapter(gb.getGoodsType(), 
				gb.getSecondType());
		item.setIndex1(adapter.getBigType()); // 大类
		item.setIndex2(adapter.getSmallType());// 小类
		item.setIndex3(gb.getLvLimit());
		item.setIndex4(gb.getQualityType());
		item.setIndex5(gb.getCareer());
		//item.setIndex6(role.getVipLevel());// vip等级
		item.setIndex7(rg.getStrengthenLevel());// 强化等级
		item.setIndex8(String.valueOf(rg.getBind()));// 绑定类型

		// 随机属性
		item.setStore1(rg.getAttrVar());
		// 其他属性
		item.setStore2(rg.getOtherParm());
		// 镶嵌
		item.setStore3(rg.getMosaic());
		item.setExpiredTime(DateUtil.addSecond(new Date(),
						timeHours * 60 * 60));
		return item;
	}

	private List<MercurySearchIndexItem> getGlobalSearchItems() {
		List<MercurySearchIndexItem> indexItems = new ArrayList<MercurySearchIndexItem>();
		MercurySearchIndexItem item = new MercurySearchIndexItem();
		item.setIndexType(IndexType.NOT_BLUR_MATCH);
		item.setIndexName(IndexableRow.APP_ID);
		item.setIndexValue(String.valueOf(GameContext.getAppId()));
		indexItems.add(item);
		if (matchServerId) {
			item = new MercurySearchIndexItem();
			item.setIndexType(IndexType.NOT_BLUR_MATCH);
			item.setIndexName(IndexableRow.SERVER_ID);
			item.setIndexValue(String.valueOf(GameContext.getServerId()));
			indexItems.add(item);
		}
		return indexItems;
	}

	private boolean isServerMatch(MercuryRecordItem item) {
		if (null == item) {
			return false;
		}
		int appId = GameContext.getAppId();
		if (appId != item.getAppId()) {
			return false;
		}
		if (this.matchServerId) {
			int serverId = GameContext.getServerId();
			return serverId == item.getServerId();
		}
		return true;
	}

	private boolean isRoleMatch(MercuryRecordItem item, String roleId) {
		return item.getOwnerId().equals(roleId);
	}

	public void setMatchServerId(boolean matchServerId) {
		this.matchServerId = matchServerId;
	}

	private void logExpired(MercuryRecordItem row, int code) {
		if (null == row) {
			return;
		}
		StringBuffer buffer = this.log(row);
		buffer.append(CAT).append(code);
		Log4jManager.LOG_AUCTION_EXPIRED.info(buffer.toString());
	}

	private void logPut(MercuryRecordItem row, int code) {
		if (null == row) {
			return;
		}
		StringBuffer buffer = this.log(row);
		buffer.append(CAT).append(code);
		Log4jManager.LOG_AUCTION_PUT.info(buffer.toString());
	}

	/**
	 * 打印拍卖行物品移除日志
	 * @param row
	 * @param ownerId -1:表示系统操作
	 * @param code
	 */
	private void logRemove(MercuryRecordItem row, String ownerId, int code) {
		if (null == row) {
			return;
		}
		StringBuffer buffer = this.log(row);
		buffer.append(CAT).append(ownerId).append(CAT).append(code);
		Log4jManager.LOG_AUCTION_REMOVE.info(buffer.toString());
	}

	private StringBuffer log(MercuryRecordItem row) {
		StringBuffer buffer = new StringBuffer("");
		buffer.append(row.getAppId());
		buffer.append(CAT);
		buffer.append(row.getServerId());
		buffer.append(CAT);
		buffer.append(row.getOwnerId());
		buffer.append(CAT);
		buffer.append(row.getOwnerName());
		buffer.append(CAT);
		buffer.append(row.getPriceType());
		buffer.append(CAT);
		buffer.append(row.getPriceNum());
		buffer.append(CAT);
		buffer.append(row.getItemId());
		buffer.append(CAT);
		buffer.append(row.getItemName());
		buffer.append(CAT);
		buffer.append(row.getItemNum());
		buffer.append(CAT);
		buffer.append(row.getItemTemplateId());
		buffer.append(CAT);
		buffer.append(row.getIndex1());
		buffer.append(CAT);
		buffer.append(row.getIndex2());
		buffer.append(CAT);
		buffer.append(row.getIndex3());
		buffer.append(CAT);
		buffer.append(row.getIndex4());
		buffer.append(CAT);
		buffer.append(row.getIndex5());
		buffer.append(CAT);
		buffer.append(row.getIndex6());
		buffer.append(CAT);
		buffer.append(row.getIndex7());
		buffer.append(CAT);
		buffer.append(row.getIndex8());
		buffer.append(CAT);
		buffer.append(row.getIndex9());
		buffer.append(CAT);
		buffer.append(row.getIndex10());
		buffer.append(CAT);
		buffer.append(row.getStore1());
		buffer.append(CAT);
		buffer.append(row.getStore2());
		buffer.append(CAT);
		buffer.append(row.getStore3());
		buffer.append(CAT);
		buffer.append(row.getStore4());
		buffer.append(CAT);
		buffer.append(row.getStore5());
		buffer.append(CAT);
		Date expiredTime = row.getExpiredTime();
		if (null != expiredTime) {
			buffer.append(sdf.format(expiredTime));
		} else {
			buffer.append("");
		}
		buffer.append(CAT);
		buffer.append(row.get__id__());
		return buffer;
	}

	@Override
	public FeeInfoConfig getFeeInfoConfig() {
		return this.feeInfoConfig ;
	}

	@Override
	public byte getBindType(GoodsBase goodsBase) {
		if(goodsBase.isEquipment()){
			//装备，时装为装备绑定
			return BindingType.equip_binding.getType();
		}
		//拍卖行肯定是没有绑定的物品
		return BindingType.no_binding.getType() ;
	}

	@Override
	public void frozenRoleDownShelf(String roleId) {
		try {
			C5401_MercurySearchRespMessage message = GameContext.getAuctionApp().queryRoleAuctionList(roleId);
			List<MercuryRecordItem> records = message.getRecords();
			if(Util.isEmpty(records)){
				return ;
			}
			
			for(MercuryRecordItem record : records){
				try{
					//删除记录
					Result removeResult = this.sendMercuryRemoveMsg(roleId, String.valueOf(record.get__id__()));
					if(!removeResult.isSuccess()){
						continue;
					}
					// 日志
					this.logRemove(record, "-1", SUCCESS_CODE);
					this.delistingMail(roleId, record);
				} catch (Exception ex){
					logger.error("auction frozen role down shelf delete goods error ", ex);
				}
			}
		}catch (Exception e) {
			logger.error("auction frozen role down shelf error ", e);
		}
	}
}
