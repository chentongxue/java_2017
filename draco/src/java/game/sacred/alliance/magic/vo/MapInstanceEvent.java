package sacred.alliance.magic.vo;

import lombok.Data;

public @Data class MapInstanceEvent {

	public static enum EventType {
		chestOpen,
		chestOpenReady,
		refReshRule,
		heroOnBattle,
        chestOpenSuccess,
	}
	public MapInstanceEvent(){
		
	}
	public MapInstanceEvent(EventType eventType,String eventKey){
		this.eventType = eventType ;
		this.eventKey = eventKey ;
	}
	
	protected EventType eventType ;
	protected String eventKey ;
	
}
