package com.game.draco.app.rank.logic;

import java.util.Collection;
import java.util.List;

import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.vo.RoleInstance;
import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.rank.domain.RankInfo;
import com.game.draco.app.rank.type.RankType;
import com.game.draco.message.item.RankDetailItem;

/**
 * 
 * log日志格式：roleId#exp#roleId#roleName#gender#career#level#campId#factionId
 * 返回的日志格式：sort#log日志格式
 * 排行榜小类型和careerType对应，根据排行榜小类型判断是否打印日志
 * 客户端显示的项
 * 名次、英雄名、所属角色名、角色所属公会、英雄的战斗力
 */
public class RanKRoleHeroLogic extends RankRoleLogic {

	private static RanKRoleHeroLogic instance = new RanKRoleHeroLogic();
	private RanKRoleHeroLogic(){
	}
	
	public static RanKRoleHeroLogic getInstance(){
		return instance ;
	}
	
	@Override
	protected boolean canPrintLog(RoleInstance role, RankInfo rankInfo) {
		//根据职业匹配
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
	/**
	 * keyId#heroscore#herolevel#heroexp#createtime#heroId#heroName#
	 * roleId#roleName#gender#heroname#campId#uionId#
	 */
	@Override
	protected void doPrintLog(RoleInstance role, RankInfo rankInfo, boolean schedulerFlag, String timeStr) {
		if(role==null)
			return;
		Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(role.getRoleId());
		if(Util.isEmpty(heros)){
			return;
		}
		for (RoleHero hero : heros) {
			if(hero==null)
				continue;
			try{
				StringBuilder sb = getLogMessage(role, hero);
				doWriteLogFile(rankInfo, schedulerFlag, timeStr, sb.toString());
			}catch (Exception e) {
				continue;
			}

		}
	}

	private StringBuilder getLogMessage(RoleInstance role, RoleHero hero)
			throws Exception {
		StringBuilder sb = new StringBuilder();
		//keyId#
		sb.append(role.getRoleId()).append(UNDERLINE_CAT).append(hero.getHeroId()).append(CAT);
		//英雄的战斗力
		sb.append(GameContext.getHeroApp().getBattleScore(hero)).append(CAT);
		sb.append(hero.getLevel()).append(CAT);
		sb.append(hero.getExp()).append(CAT);
		sb.append(getRoleCreatTimeStr(role)).append(CAT);
		sb.append(hero.getHeroId()).append(CAT);
		//英雄的名字
		String heroName = GameContext.getGoodsApp().getGoodsTemplate(GoodsHero.class, hero.getHeroId()).getName();
		sb.append(heroName).append(CAT);
		sb.append(getRoleBaseInfo(role));
		return sb;
	}
    /*
     *名次、英雄名、所属角色名、角色所属公会、英雄的战斗力 
     *#keyId#heroscore#herolevel#heroexp#createtime#heroId#heroName#roleId#roleName#gender#level#campId#uionId#unionName
     0 RANK  1
     1#keyId 17000002_10020101619
     2#heroscore 1
     3#herolevel 0
     4#heroexp
     5#createtime
     6#heroId
     7#heroName
     8#roleId
     9#roleName
     10#gender
     11#level
     12#campId
     13#uionId
     14#unionName
     ------
     1#17000002_10020101619#1#0#1403173897000#1002010#光头法师#17000002#麦什洛克·追求#1#2#-1##
     */
	@Override
	public RankDetailItem parseLog(String row) {
		String[] cols = Util.splitStr(row, CAT);
		if(Util.isEmpty(cols)){
			return null;
		}
		RankDetailItem item = new RankDetailItem();
		short nowRank = Short.valueOf(cols[0]); 
		item.setRank(nowRank);
		//英雄名字
		item.setData1(this.get(cols, 7));
		//角色名
		item.setData2(this.get(cols, 9));
		//公会名
		item.setData3(this.get(cols, 14));
		//英雄战斗力
		item.setData4(this.get(cols, 2));
		//roleId 便于查看角色信息
		item.setKey(this.get(cols, 8)) ;
		return item ;
	}
	
	@Override
	public void initLogData(RankInfo rankInfo){
		List<RoleHero> roleHeroList = GameContext.getRankDAO().selectHeroRole("campId", 
				rankInfo.getSubType(), 
				"limit", getRecordLimit(rankInfo));
		if(Util.isEmpty(roleHeroList)){
			return ;
		}
		for(RoleHero roleHero : roleHeroList){
			if(null == roleHero){
				continue;
			}
			
			RoleInstance role = GameContext.getRankDAO().selectRole("roleId", roleHero.getRoleId()+"");
			if(null == role){
				continue ;
			}
			try{
				StringBuilder sb = getLogMessage(role, roleHero);
				doWriteLogFile(rankInfo, false, null, sb.toString());
			}catch (Exception e) {
				continue;
			}
		}
	}
	
	@Override
	public RankType getRankType() {
		return RankType.ROLE_HERO ;
	}

}
