package sacred.alliance.magic.domain;

import lombok.Data;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.KeySupport;

public @Data class EquStrengthenEffect implements KeySupport<String> {
	
	private int level;//等级
	private int qualityType;//品质
	private int addRate1; //100% =10000
	private int addRate2; //100% =10000
	
	public String getKey(){
		return this.level + Cat.colon + this.qualityType ;
	}
}
