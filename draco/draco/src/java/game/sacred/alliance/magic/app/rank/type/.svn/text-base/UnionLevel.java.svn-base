package sacred.alliance.magic.app.rank.type;

import java.util.Map;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankType;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：UnionId#UnionLevel#UnionExp#UnionName#UnionMemberNum#leaderId#leaderName
 * 返回的日志格式：sort#log日志格式
 */
public class UnionLevel extends RankUnionLogic{

	private static UnionLevel instance = new UnionLevel();
	private UnionLevel(){
	}
	
	public static UnionLevel getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(Union t, RankInfo rankInfo) {
		return true;
	}

	@Override
	public void count(Union t, RankInfo rankInfo, int data1, int data2) {
		//无需实现此接口
	}

	@Override
	protected void doPrintLog(Union t, RankInfo rankInfo) {
		StringBuilder buffer = new StringBuilder("");
		buffer.append(t.getUnionId());
		buffer.append(CAT);
		buffer.append(t.getUnionLevel());
		buffer.append(CAT);
		buffer.append(t.getPopularity()); //公会经验?
		buffer.append(CAT);
		buffer.append(t.getUnionName());
		buffer.append(CAT);
		buffer.append(t.getUnionMemberMap().size());
		buffer.append(CAT);
		buffer.append(t.getLeaderId());
		buffer.append(CAT);
		buffer.append(t.getLeaderName());
		rankInfo.getLogger().info(buffer.toString()) ;
	}

	@Override
	public RankType getRankType() {
		return RankType.Union_Level ;
	}

	@Override
	public void initLogData(RankInfo rankInfo) {
		Map<String, Union> UnionMap = GameContext.getUnionApp().getUnionMap();
		if(Util.isEmpty(UnionMap)){
			return ;
		}
		for(Union t : UnionMap.values()){
			this.printLog(t, rankInfo);
		}
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 门派名 掌门 门派人数 门派等级
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//门派名
		item.setData1(this.get(cols, 4));
		//掌门
		item.setData2(this.get(cols, 7));
		//门派人数
		item.setData3(this.get(cols, 5));
		//门派等级
		item.setData4(this.get(cols, 2));
		//UnionId
		item.setKey(this.get(cols, 1)) ;
		return item ;
	}

}
