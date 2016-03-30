package sacred.alliance.magic.app.faction.integral;

import com.game.draco.GameContext;

import sacred.alliance.magic.constant.TextId;

public class DegreeChannel implements IntegralChannel{
	
	@Override
	public IntegralChannelType getChannelType() {
		return IntegralChannelType.DEGREE;
	}

	@Override
	public String getContent() {
		return GameContext.getI18n().getText(TextId.Faction_Integral_Degree);
	}

}
