package sacred.alliance.magic.app.rank.type;

import com.game.draco.message.item.RankDetailItem;

import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankType;

public class RankNullLogic extends RankLogic<Object> {
	
	private static RankNullLogic instance = new RankNullLogic();
	
	private RankNullLogic(){
	}
	
	public static RankNullLogic getInstance(){
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
	protected void doPrintLog(Object t, RankInfo rankInfo) {
		
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

	@Override
	public void frozenRoleOffRankLog(Object t, RankInfo rankInfo) {
	}

}
