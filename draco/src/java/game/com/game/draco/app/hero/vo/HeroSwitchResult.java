package com.game.draco.app.hero.vo;


import lombok.Data;
import sacred.alliance.magic.base.Result;

import java.util.List;

public @Data
class HeroSwitchResult extends Result {

    private List<Integer> heroList ;
}
