package sacred.alliance.magic.vo;

import lombok.Data;
import sacred.alliance.magic.util.KeySupport;

public @Data class RoleBornHero implements KeySupport<Integer>{

	private int heroId ;
	private short imageId ;
	private String desc ;
	
	@Override
	public Integer getKey() {
		return this.heroId ;
	}
}
