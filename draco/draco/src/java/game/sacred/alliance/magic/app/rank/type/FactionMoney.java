//package sacred.alliance.magic.app.rank.type;
//
//import java.util.Map;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.item.RankDetailItem;
//
//import sacred.alliance.magic.app.goods.Util;
//import sacred.alliance.magic.app.rank.RankInfo;
//import sacred.alliance.magic.app.rank.RankType;
//import sacred.alliance.magic.domain.Faction;
//
///**
// * 
// * log日志格式：factionId#门派资金#factionName#factionMemberNum#leaderId#leaderName#
// * 返回的日志格式：sort#log日志格式
// */
//public class FactionMoney extends RankFactionLogic{
//
//	private static FactionMoney instance = new FactionMoney();
//	private FactionMoney(){
//	}
//	
//	public static FactionMoney getInstance(){
//		return instance ;
//	}
//	
//	@Override
//	protected boolean canPrintLog(Faction t, RankInfo rankInfo) {
//		return true;
//	}
//
//	@Override
//	public void count(Faction t, RankInfo rankInfo, int data1, int data2) {
//		//无需实现此接口
//	}
//
//	@Override
//	protected void doPrintLog(Faction t, RankInfo rankInfo) {
//		StringBuilder buffer = new StringBuilder("");
//		buffer.append(t.getFactionId());
//		buffer.append(CAT);
//		buffer.append(t.getFactionMoney()); 
//		buffer.append(CAT);
//		buffer.append(t.getFactionName());
//		buffer.append(CAT);
//		buffer.append(t.getMemberNum());
//		buffer.append(CAT);
//		buffer.append(t.getLeaderId());
//		buffer.append(CAT);
//		buffer.append(t.getLeaderName());
//		rankInfo.getLogger().info(buffer.toString()) ;
//	}
//
//	@Override
//	public RankType getRankType() {
//		return RankType.Faction_Money ;
//	}
//
//	@Override
//	public void initLogData(RankInfo rankInfo) {
//		Map<String, Faction> factionMap = GameContext.getFactionApp().getFactionMap();
//		if(Util.isEmpty(factionMap)){
//			return ;
//		}
//		for(Faction t : factionMap.values()){
//			this.printLog(t, rankInfo);
//		}
//	}
//
//	@Override
//	public RankDetailItem parseLog(String row) {
//		//排名 门派名 掌门 门派人数 门派资金
//		String[] cols = Util.splitStr(row, CAT);
//		if(Util.isEmpty(cols)){
//			return null;
//		}
//		RankDetailItem item = new RankDetailItem();
//		short nowRank = Short.valueOf(cols[0]); 
//		item.setRank(nowRank);
//		//门派名
//		item.setData1(this.get(cols, 3));
//		//掌门
//		item.setData2(this.get(cols, 6));
//		//门派人数
//		item.setData3(this.get(cols, 4));
//		//门派资金
//		item.setData4(this.get(cols, 2));
//		//factionId
//		item.setKey(this.get(cols, 1)) ;
//		return item ;
//	}
//
//}
