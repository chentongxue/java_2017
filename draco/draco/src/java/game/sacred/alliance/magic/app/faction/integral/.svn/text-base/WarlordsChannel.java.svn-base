package sacred.alliance.magic.app.faction.integral;

import java.text.MessageFormat;

import com.game.draco.GameContext;

import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.constant.TextId;

public class WarlordsChannel implements IntegralChannel{
	
	private Active active;
	
	public WarlordsChannel(Active active){
		this.active = active;
	}
	
	@Override
	public IntegralChannelType getChannelType() {
		return IntegralChannelType.WARLORDS;
	}

	@Override
	public String getContent() {
		if(null != this.active){
			return "报名" + this.active.getName();
		}
		return GameContext.getI18n().messageFormat(TextId.Faction_Integral_Warlords_Name, this.active.getName());
	}

}
