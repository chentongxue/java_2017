package sacred.alliance.magic.app.faction.integral;

import java.text.MessageFormat;

import com.game.draco.GameContext;
import com.game.draco.app.exchange.domain.ExchangeItem;

import sacred.alliance.magic.constant.TextId;

public class ExchangeRollbackChannel implements IntegralChannel{

	private ExchangeItem exchangeItem ;
	
	public ExchangeRollbackChannel(ExchangeItem exchangeItem){
		this.exchangeItem = exchangeItem ;
	}
	
	@Override
	public IntegralChannelType getChannelType() {
		return IntegralChannelType.EXCHANGE_ROLLBACK;
	}

	@Override
	public String getContent() {
		if(null == this.exchangeItem){
			return GameContext.getI18n().getText(TextId.Faction_Integral_ExchangeRollback);
		}
		return MessageFormat.format(TextId.Faction_Integral_ExchangeRollback_Name, this.exchangeItem.getName());
	}

}
