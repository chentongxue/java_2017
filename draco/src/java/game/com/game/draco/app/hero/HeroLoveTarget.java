package com.game.draco.app.hero;

import lombok.Data;

public @Data class HeroLoveTarget {

	private int id = 0 ;
	private int quality = -1 ;
	private int star = -1 ;
	
	
	public HeroLoveTarget(){
		
	}
	
	public HeroLoveTarget(int id,int quality,int star){
		this.id = id ;
		this.quality = quality ;
		this.star = star ;
	}
}
