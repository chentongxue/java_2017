package com.game.draco.app.rank;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public @Data class RankInitResult {

	private List<Integer> successList = new ArrayList<Integer>() ;
	private List<Integer> failureList = new ArrayList<Integer>() ;
}
