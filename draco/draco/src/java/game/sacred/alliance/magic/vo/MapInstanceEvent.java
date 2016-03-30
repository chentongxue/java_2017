package sacred.alliance.magic.vo;

public class MapInstanceEvent {

	public static enum EventType {
		chestOpen,
		chestOpenReady
	}
	public MapInstanceEvent(EventType eventType,String eventKey){
		this.eventType = eventType ;
		this.eventKey = eventKey ;
	}
	private EventType eventType ;
	private String eventKey ;
	public EventType getEventType() {
		return eventType;
	}
	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
	public String getEventKey() {
		return eventKey;
	}
	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}
	
	
}
