package sacred.alliance.magic.app.faction.war.config;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

public @Data class FactionWarGambleConfig {
	private int minLevel;
	private int maxLevel;
	private int money1;
	private int money2;
	private int money3;
	private Set<Integer> moneySet = new HashSet<Integer>();
	
	public void init(){
		if(money1 > 0){
			moneySet.add(money1);
		}
		if(money2 > 0){
			moneySet.add(money2);
		}
		if(money3 > 0){
			moneySet.add(money3);
		}
	}
	
	public int getMaxMoney(){
		return money3;
	}
}
