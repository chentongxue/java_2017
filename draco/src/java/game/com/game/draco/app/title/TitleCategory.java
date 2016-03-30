package com.game.draco.app.title;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.domain.GoodsTitle;

public @Data class TitleCategory {

	private int categoryId ;
	private String categoryName ;
	private List<GoodsTitle> titleList = new ArrayList<GoodsTitle>() ;
}
