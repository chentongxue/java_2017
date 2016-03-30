package sacred.alliance.magic.app.rank.type;

import java.util.List;

import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.app.rank.RankInfo;
import sacred.alliance.magic.app.rank.RankType;
import sacred.alliance.magic.base.EquipslotType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.RankDetailItem;
/**
 * 
 * log日志格式：equipId#goodsId#score#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和campId对应，根据排行榜小类型判断是否打印日志
 */
public class RoleWingScore extends RankRoleLogic {
	
	private static RoleWingScore instance = new RoleWingScore();
	private RoleWingScore(){
	}
	
	public static RoleWingScore getInstance(){
		return instance ;
	}

	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据阵营匹配
		byte subType = rankInfo.getSubType();
		if(subType != RANK_ALL && subType != role.getCampId()){
			return false;
		}
		//判断是否在统计时间内
		return rankInfo.isInStatDate();
	}

	@Override
	public void count(RoleInstance t, RankInfo rankInfo, int data1, int data2) {

	}

	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo) {
		RoleGoods wingGoods = GameContext.getWingApp().getWingGoods(role);
		if(null == wingGoods){
			return ;
		}
		this.print(role, wingGoods, rankInfo);
	}
	
	private void print(RoleInstance role, RoleGoods wingGoods, RankInfo rankInfo){
		int score = RoleGoodsHelper.getEquipScore(wingGoods) ;
		if(score <=0){
			return ;
		}
		StringBuffer sb = new StringBuffer();
		sb.append(wingGoods.getId());
		sb.append(CAT);
		sb.append(wingGoods.getGoodsId());
		sb.append(CAT);
		sb.append(score);
		sb.append(CAT);
		sb.append(getRoleBaseInfo(role));
		rankInfo.getLogger().info(sb.toString());
	}

	@Override
	public RankType getRankType() {
		return RankType.Role_Wing_Score;
	}

	@Override
	public RankDetailItem parseLog(String row) {
		//排名 装备名 职业 所有者名 战斗力
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//装备名
		int goodsId = Integer.parseInt(this.get(cols, 2));
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
		if(null == gb){
			return null ;
		}
		item.setData1(gb.getName());
		//职业
		//item.setData2(CareerType.getType(Byte.parseByte(this.get(cols, 7))).getCnName());
		//所有者名
		item.setData3(this.get(cols, 5));
		//战斗力
		item.setData4(this.get(cols, 3));
		//roleId
		item.setKey(this.get(cols, 4)) ;
		return item ;
	}
	
	/**
	 * 取等级，经验前1000人的装备来产生日志
	 */
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleInstance> roleList = GameContext.getRankDAO().selectEquipRole("campId", rankInfo.getSubType(), 
				"limit", getRecordLimit(rankInfo)*10);
		if(Util.isEmpty(roleList)){
			return ;
		}
		for(RoleInstance role : roleList){
			if(null == role){
				continue;
			}
			List<RoleGoods> roleGoodsList = GameContext.getRankDAO().selectEquip("roleId", role.getRoleId());
			if(Util.isEmpty(roleGoodsList)){
				continue;
			}
			for(RoleGoods roleGoods : roleGoodsList){
				if(null == roleGoods){
					continue;
				}
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
				if(null == gb){
					continue;
				}
				if(!gb.isEquipment()){
					continue;
				}
				GoodsEquipment equ = (GoodsEquipment)gb;
				if(equ.getEquipslotType() != EquipslotType.wing.getType()){
					continue;
				}
				RoleGoodsHelper.init(roleGoods);
				this.print(role, roleGoods, rankInfo);
			}
		}
	}

}
