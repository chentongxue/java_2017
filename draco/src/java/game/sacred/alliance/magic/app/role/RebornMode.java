package sacred.alliance.magic.app.role;

import java.util.HashSet;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.app.shop.config.ShopGoodsConfig;

import lombok.Data;
import sacred.alliance.magic.base.RebornType;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.StringUtil;

public @Data class RebornMode {
	
	private int id ;//复活方式
	private String name ;//复活方式名称
	private String buff ;
	private int goodsId ;//复活道具id
	private String goodsTips ;//道具不足提示
	private int hpRate ;
	private Set<Short> buffIds = new HashSet<Short>();
	private int goldMoney;//道具价格
	
	public boolean isNeedGood(){
		return 0 != this.goodsId;
	}
	
	public void init(String xlsInfo){
		if(StringUtil.nullOrEmpty(buff)){
			return ;
		}
		String[] buffList = StringUtil.splitString(buff);
		if(null != buffList && buffList.length >0){
			for(String buffId : buffList){
				if(!StringUtil.isNumber(buffId)){
					continue ;
				}
				buffIds.add(Short.parseShort(buffId));
			}
		}
		//原地复活，必须配复活道具或者元宝数
		if(RebornType.situ.getId() == this.id){
			if(this.goodsId <= 0 || this.goldMoney <= 0){
				this.checkFail(xlsInfo + ",id=" + this.id + ",it must config goodsId and goldMoney.");
			}
			//如果商城里出售复活道具，这里的价格必须和商城里价格相等；如果商城不出售此道具，则使用这里配置的价格。
			ShopGoodsConfig shopGoods = GameContext.getShopApp().getShopGoods(this.goodsId);
			if(null != shopGoods && shopGoods.getDisPrice() != this.goldMoney){
				this.checkFail(xlsInfo + ",id=" + this.id + "goodsId=" + this.goodsId + ", goldMoney not equal to shop price(disGoldPrice in shop).");
			}
		}
	}
	
	private void checkFail(String info){
		Log4jManager.CHECK.error(info);
		Log4jManager.checkFail();
	}
	
}
