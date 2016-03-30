package com.game.draco.app.rank.logic;

import java.util.List;

import sacred.alliance.magic.app.goods.RoleGoodsHelper;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：equipId#goodsId#score#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和campId对应，根据排行榜小类型判断是否打印日志
 */

public class RankRoleEquipLogic extends RankRoleLogic{

	private static RankRoleEquipLogic instance = new RankRoleEquipLogic();
	private RankRoleEquipLogic(){
	}
	
	public static RankRoleEquipLogic getInstance(){
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
	public void count(RoleInstance role, RankInfo rankInfo, int data1, int data2) {
	}

	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo,boolean schedulerFlag, String timeStr) {
		List<RoleGoods> roleGoodsList = role.getRoleBackpack().getAllGoods();
		//英雄装备
		roleGoodsList.addAll(GameContext.getHeroApp().getAllHeroEquipList(role.getRoleId()));
		for(RoleGoods roleGoods : roleGoodsList){
			this.print(role, roleGoods, rankInfo,schedulerFlag,  timeStr);
		}
	}
	/*
	 * keyId#equipscore#equiplevel#createtime#equipId#equipName#roleId#roleName#gender#level#campId#uionId#unionName
	 1 keyId
	 2 #equipscore
	 3 #equiplevel
	 4 #createtime
	 5 #equipId
	 6 #equipName
	 7 #roleId
	 8 #roleName
	 9 #gender
	 10 #level
	 11 #campId
	 12 #uionId
	 13 #unionName
	 */
	@Override
	public RankDetailItem parseLog(String row) {
		//名次、装备名、所属角色名、角色所属公会、该装备的战斗力
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//装备名
//		int goodsId = Integer.parseInt(this.get(cols, 2));
//		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
//		if(null == gb){
//			return null ;
//		}
//		item.setData1(gb.getName());
		//装备名
		item.setData1(this.get(cols, 6));
//		item.setData2(CareerType.getType(Byte.parseByte(this.get(cols, 7))).getCnName());
		//所有者名
		item.setData2(this.get(cols, 8));
		//公会名
		item.setData3(this.get(cols, 13));
		item.setData4(this.get(cols, 2));
		////roleId 便于查看角色信息
		item.setKey(this.get(cols, 7)) ;
		return item ;
	}
	@Override
	public RankType getRankType() {
		return RankType.ROLE_EQUIP ;
	}
	/**
	 * 2014-06-25
	 * keyId#equipscore#equiplevel#createtime#equipId#equipName#
	 * roleId#roleName#gender#level#campId#uionId#unionName
	 */
	private void print(RoleInstance role,RoleGoods roleGoods,RankInfo rankInfo,boolean schedulerFlag, String timeStr) {
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleGoods.getGoodsId());
		if (null == gb) {
			return;
		}
		if (!gb.isEquipment()) {
			return;
		}
		StringBuffer sb = new StringBuffer();

		GoodsEquipment equ = (GoodsEquipment) gb;

		int score = RoleGoodsHelper.getEquipScore(roleGoods);
		if (score <= 0) {
			return;
		}
		sb.append(roleGoods.getId()).append(CAT);
		sb.append(score).append(CAT);
		sb.append(equ.getLevel()).append(CAT);
//			sb.append(role.getCreateTime()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(roleGoods.getId()).append(CAT);
		sb.append(equ.getName()).append(CAT);
		sb.append(getRoleBaseInfo(role));

		doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
	}
	
	/**
	 * 取等级，经验前500人的装备来产生日志
	 */
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleInstance> roleList = GameContext.getRankDAO().selectEquipRole("campId", rankInfo.getSubType(), 
				"limit", getRecordLimit(rankInfo)*5);
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
				RoleGoodsHelper.init(roleGoods);
				this.print(role, roleGoods,rankInfo,false, null);
			}
		}
	}

}
