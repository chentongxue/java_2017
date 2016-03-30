package sacred.alliance.magic.app.attri.config;

import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Initable;
import sacred.alliance.magic.util.KeySupport;
import lombok.Data;

public @Data class AttriRestrictGear implements KeySupport<String>,Initable{
    private final static float FULL = 10000f ;
	private byte battleType; //0：人打人1：人打怪2:怪打人
	private int gearSum; //档位和
	private int rate; //伤害系数 100%=10000
    private float floatRate ;
	
	
	@Override
	public String getKey() {
		return this.battleType + Cat.underline + this.gearSum;
	}

    @Override
    public void init() {
        this.floatRate = rate/FULL  ;
    }
}
