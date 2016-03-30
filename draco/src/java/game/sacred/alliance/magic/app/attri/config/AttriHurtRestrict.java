package sacred.alliance.magic.app.attri.config;

import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class AttriHurtRestrict implements KeySupport<String>,Initable{
    private final static float FULL = 10000f ;
	private byte seriesId;
	private byte restrictId;
    private int rate = (int)FULL;
    private float floatRate ;
	
	
	@Override
	public String getKey() {
		return this.seriesId + "_" + restrictId;
	}

    @Override
    public void init() {
        this.floatRate = rate/FULL  ;
    }
}
