package sacred.alliance.magic.app.auction;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

public @Data class AuctionMenuAdapter implements KeySupport<String>{

	private int goodsBigType ;
	private int goodsSmallType ;
	private int bigType ;
	private int smallType ;
	
	@Override
	public String getKey(){
		return this.goodsBigType + Cat.underline + this.goodsSmallType ;
	}
}
