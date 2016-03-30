package sacred.alliance.magic.base;

public enum ChangeMapEvent {

	defaultEvent((byte)0),
	quest((byte)1),
	worldmap((byte)2),
	clientStopFindPath((byte)2),
	;
	
	private final byte eventType ;
	private ChangeMapEvent(byte eventType){
		this.eventType = eventType ;
	}
	
	public byte getEventType() {
		return eventType;
	}
	
	
}
