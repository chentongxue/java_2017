package sacred.alliance.magic.app.faction.integral;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.domain.ExchangeItem;

import sacred.alliance.magic.constant.TextId;

public class ExchangeChannel implements IntegralChannel{

	private ExchangeItem exchangeItem ;
	
	public ExchangeChannel(ExchangeItem exchangeItem){
		this.exchangeItem = exchangeItem ;
	}
	
	@Override
	public IntegralChannelType getChannelType() {
		return IntegralChannelType.EXCHANGE;
	}

	@Override
	public String getContent() {
		if(null == this.exchangeItem){
			return GameContext.getI18n().getText(TextId.Faction_Integral_Exchange);
		}
		return this.exchangeItem.getName();
	}

}
