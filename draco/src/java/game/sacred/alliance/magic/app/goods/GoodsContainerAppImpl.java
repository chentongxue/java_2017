package sacred.alliance.magic.app.goods;

import java.util.List;

import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.base.XlsSheetNameType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsContain;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.XlsPojoUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.shop.config.ShopGoodsConfig;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0570_ContainerExpandExecReqMessage;

public class GoodsContainerAppImpl implements GoodsContainerApp{
	private final short EXEC_CMDID = new C0570_ContainerExpandExecReqMessage().getCommandId();
	
	private ContainerConfig config ;
	
	@Override
	public void setArgs(Object arg0) {
		
	}
	
	private int getMaxAddNum(RoleInstance role){
		int rolePackCount = role.getBackpackCapacity();
		if(rolePackCount  >= ParasConstant.ROLE_BACKPACK_MAX_NUM){
			return 0 ;
		}
		return ParasConstant.ROLE_BACKPACK_MAX_NUM-rolePackCount ;
	}
	
	public void expand(RoleInstance role){
		this.expand(role, this.config.getDefaultGoods(), false);
	}
	
	private void expand(RoleInstance role,GoodsContain contain,boolean confirm){
		int maxAddNum = this.getMaxAddNum(role);
		if(maxAddNum <=0){
			//已满
			this.sendMessage(role, Status.GOODS_BAG_FULL.getTips());
			return ;
		}
		int addNum = contain.getGrid() ;
		//最终真正能加的格子数目
		int addContainNum = Math.min(maxAddNum, addNum);
		if(!confirm){
			//二次确认
			GoodsContain realContain = contain ;
			if(addNum > maxAddNum && null != this.config.getMinGoods()){
				realContain = this.config.getMinGoods();
			}
			ShopGoodsConfig shop = GameContext.getShopApp().getShopGoods(realContain.getId());
			int price = shop.getDisPrice() ;
			String tips = GameContext.getI18n().messageFormat(TextId.CONTAIN_MONEY_TIPS,String.valueOf(price),
					"1",String.valueOf(addContainNum));
			
			Message notifyMsg = QuickCostHelper.getMessage(role, EXEC_CMDID, String.valueOf(realContain.getId()), (short)0, "", price, 0, tips);
			role.getBehavior().sendMessage(notifyMsg);
			return ;
		}
		//扣钱
		ShopGoodsConfig shop = GameContext.getShopApp().getShopGoods(contain.getId());
		if(null == shop || shop.getDisPrice() <= 0 ){
			this.sendMessage(role, GameContext.getI18n().getText(TextId.ERROR_DATA));
			return ; 
		}
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, AttributeType.goldMoney, shop.getDisPrice());
		if(ar.isIgnore()){
			return;
		}
		if(!ar.isSuccess()){
			this.sendMessage(role, GameContext.getI18n().getText(TextId.NOT_ENOUGH_GOLD_MONEY));
			return ; 
		}
//		if(role.getGoldMoney() < shop.getDisGoldPrice()){
//			//元宝不够
//			this.sendMessage(role, GameContext.getI18n().getText(TextId.NOT_ENOUGH_GOLD_MONEY));
//			return ; 
//		}
		
		role.getRoleBackpack().expansionStorage(addContainNum);
		//扣钱
		GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney, OperatorType.Decrease,
				shop.getDisPrice(), OutputConsumeType.expansion_pack) ;
		role.getBehavior().notifyAttribute();
		//通知客户端添加成功
		GameContext.getUserGoodsApp().notifyBackpackExpansionMessage(role, addContainNum);
	}
	
	public void expandExec(RoleInstance role,String info){
		if(!StringUtil.isNumber(info)){
			this.sendMessage(role, GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return ;
		}
		int goodsId = Integer.valueOf(info);
		GoodsContain contain = this.getGoodsContain(goodsId);
		if(null == contain){
			this.sendMessage(role, GameContext.getI18n().getText(TextId.ERROR_INPUT));
			return ;
		}
		this.expand(role, contain, true);
	}
	
	private GoodsContain getGoodsContain(int goodsId){
		if(goodsId == this.config.getDefaultGoodsId()){
			return config.getDefaultGoods();
		}
		return config.getMinGoods() ;
	}
	
	private void sendMessage(RoleInstance role,String tips){
		if(Util.isEmpty(tips)){
			return ;
		}
		role.getBehavior().sendMessage(new C0003_TipNotifyMessage(tips));
	}

	@Override
	public void start() {
		this.loadContainerConfig();
		if(null == config){
			return ;
		}
		GoodsContain defaultGoods = this.checkGoodsId(config.getDefaultGoodsId());
		config.setDefaultGoods(defaultGoods);
		if(null == defaultGoods){
			Log4jManager.CHECK.error("ContainerConfig config error,goods not exist " +
					",or not exist in shop,or he disGoldPrice is error. defaultGoodsId=" + defaultGoods.getId());
			Log4jManager.checkFail();
		}
		int minGoodsId = config.getMinGoodsId();
		if(minGoodsId <=0 ){
			return ;
		}
		GoodsContain minGoods = this.checkGoodsId(minGoodsId);
		config.setMinGoods(minGoods);
		if(null == minGoods){
			Log4jManager.CHECK.error("ContainerConfig config error,goods not exist " +
					",or not exist in shop,or he disGoldPrice is error. minGoodsId=" + minGoodsId);
			Log4jManager.checkFail();
		}
	}
	
	private GoodsContain checkGoodsId(int goodsId){
		GoodsContain goods = GameContext.getGoodsApp().getGoodsTemplate(GoodsContain.class, goodsId);
		ShopGoodsConfig shop = GameContext.getShopApp().getShopGoods(goodsId);
		if(null == goods || null == shop || shop.getDisPrice() <=0 ){
			Log4jManager.CHECK.error("ContainerConfig config error,goods not exist " +
					",or not exist in shop,or he disGoldPrice is error. goodsId=" + goodsId);
			Log4jManager.checkFail();
		}
		return goods ;
		
	}

	@Override
	public void stop() {
		
	}

	
	private void loadContainerConfig(){
		String fileName = "";
		String sheetName = "";
		String xlsPath = GameContext.getPathConfig().getXlsPath();
		try{
			fileName = XlsSheetNameType.container_config.getXlsName();
			sheetName = XlsSheetNameType.container_config.getSheetName();
			String sourceFile = xlsPath + fileName;
			List<ContainerConfig> list = XlsPojoUtil.sheetToList(sourceFile,sheetName, ContainerConfig.class);
			if(Util.isEmpty(list)){
				Log4jManager.CHECK.error("ContainerConfig config error,have any data : sourceFile=" + fileName + " sheetName=" + sheetName);
				Log4jManager.checkFail();
				return ;
			}
			config = list.get(0);
		}catch(Exception e){
			Log4jManager.CHECK.error("loadContainerConfig error : sourceFile=" + fileName + " sheetName=" + sheetName, e);
			Log4jManager.checkFail();
		}
	}
}
