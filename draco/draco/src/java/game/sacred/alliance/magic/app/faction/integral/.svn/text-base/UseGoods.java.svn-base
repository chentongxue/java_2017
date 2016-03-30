package sacred.alliance.magic.app.faction.integral;

import java.text.MessageFormat;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;

public class UseGoods implements IntegralChannel{

	private int goodsId ;
	private int goodsNum ;
	
	public UseGoods(int goodsId,int goodsNum){
		this.goodsId = goodsId ;
		this.goodsNum = goodsNum ;
	}
	
	@Override
	public String getContent() {
		GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(this.goodsId);
		if(null == goodsBase){
			return GameContext.getI18n().getText(TextId.Faction_Integral_UseGoods);
		}
		return GameContext.getI18n().messageFormat(TextId.Faction_Integral_UseGoods_Name, this.goodsNum, goodsBase.getName());
	}

	@Override
	public IntegralChannelType getChannelType() {
		return IntegralChannelType.USE_GOODS;
	}

}
