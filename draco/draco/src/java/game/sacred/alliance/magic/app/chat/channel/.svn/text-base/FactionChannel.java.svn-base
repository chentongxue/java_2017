//package sacred.alliance.magic.app.chat.channel;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Collection;
//
//import com.game.draco.GameContext;
//
//import sacred.alliance.magic.app.chat.Channel;
//import sacred.alliance.magic.app.chat.ChannelType;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.vo.RoleEntity;
//
//public class FactionChannel extends Channel{
//	
//	public FactionChannel() {
//		super(ChannelType.Faction);
//	}
//
//	@Override
//	protected Collection<RoleEntity> getListeners(RoleEntity speaker, Serializable target) {
//		Collection<RoleEntity> listeners = new ArrayList<RoleEntity>();
//		if(null == target){
//			return listeners;
//		}
//		listeners.addAll(GameContext.getFactionApp().getAllOnlineFactionRole((Faction)target));
//		return listeners;
//	}
//
//	@Override
//	protected Status channelRule(RoleEntity speaker, Serializable target) {
//		if(null == target){
//			return Status.Chat_Faction_Null;
//		}
//		Faction faction = (Faction) target;
//		if(null == faction){
//			return Status.Chat_Faction_Null;
//		}
//		return Status.SUCCESS;
//	}
//
//	@Override
//	protected boolean isSendToSpeaker() {
//		return false;
//	}
//
//	@Override
//	protected boolean isFilterSocialShield() {
//		return true;
//	}
//	
//}
