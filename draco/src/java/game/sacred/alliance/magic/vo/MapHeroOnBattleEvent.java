package sacred.alliance.magic.vo;

import lombok.Data;

import com.game.draco.app.hero.domain.RoleHero;

public @Data class MapHeroOnBattleEvent extends MapInstanceEvent{

	private RoleHero currentHero ;
	private RoleHero preHero ;
	public MapHeroOnBattleEvent(RoleHero currentHero,RoleHero preHero) {
		this.eventType = EventType.heroOnBattle ;
		this.currentHero = currentHero ;
		this.preHero = preHero ;
	}

}
