package sacred.alliance.magic.app.attri.config;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class AttriHurtSeries implements KeySupport<Byte>{
	private byte seriesId; //系id
	private String seriesName; //系名字
	
	
	@Override
	public Byte getKey() {
		return this.seriesId;
	}
}
