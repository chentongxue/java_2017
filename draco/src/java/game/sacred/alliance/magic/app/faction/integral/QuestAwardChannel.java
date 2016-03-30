package sacred.alliance.magic.app.faction.integral;

import java.text.MessageFormat;

import com.game.draco.GameContext;
import com.game.draco.app.quest.Quest;

import sacred.alliance.magic.constant.TextId;

public class QuestAwardChannel implements IntegralChannel{

	private Quest quest;
	
	public QuestAwardChannel(Quest quest){
		this.quest = quest ;
	}
	
	@Override
	public IntegralChannelType getChannelType() {
		return IntegralChannelType.QUEST_AWARD ;
	}

	@Override
	public String getContent() {
		if(null == this.quest){
			return GameContext.getI18n().getText(TextId.Faction_Integral_QuestAward);
		}
		return MessageFormat.format(TextId.Faction_Integral_QuestAward_Complete, this.quest.getQuestName());
	}

}
