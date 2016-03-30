package sacred.alliance.magic.app.map;

import lombok.Data;
import sacred.alliance.magic.vo.MapLineInstance;

public @Data class MapLineInstanceCreatedEvent {

	private MapLineInstance mapInstance ;
	public MapLineInstanceCreatedEvent(){
	}
	public MapLineInstanceCreatedEvent(MapLineInstance mapInstance){
		this.mapInstance = mapInstance ;
	}
	
}
