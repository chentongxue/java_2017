package com.game.draco.app.rank.logic;

import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;


public class RankNoneLogic extends RankLogic<Object> {
	
	private static RankNoneLogic instance = new RankNoneLogic();
	
	private RankNoneLogic(){
	}
	
	public static RankNoneLogic getInstance(){
		return instance ;
	}

	@Override
	protected boolean canPrintLog(Object t, RankInfo rankInfo) {
		return false;
	}

	@Override
	public void count(Object t, RankInfo rankInfo, int data1, int data2) {
		
	}

	@Override
	protected void doPrintLog(Object t, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {
		
	}

	@Override
	public RankType getRankType() {
		return null;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		return null;
	}

	@Override
	public void initLogData(RankInfo rankInfo) {
		
	}

}
