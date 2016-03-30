package sacred.alliance.magic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.OneKeyOpType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.domain.TitleRecord;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleShape;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.buff.stat.MapBuffStat;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.shop.domain.ShopGoods;
import com.game.draco.app.shop.type.ShopMoneyType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.message.item.FallItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.MapBuffItem;
import com.game.draco.message.item.NpcBodyItem;
import com.game.draco.message.item.OneKeyOperateFailItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.item.RoleSkillItem;
import com.game.draco.message.item.ShopGoodsItem;
import com.game.draco.message.item.TitleWearingItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0576_OneKeyOpertateFailRespMessage;

public class Converter {

	private final static Logger logger = LoggerFactory.getLogger(Converter.class);
	private final static int DEFAULT_COLOR = (int)Long.parseLong("FFFFFFFF",16);
	
	public static RoleSkillItem getRoleSkillItem(RoleInstance role,Skill skill,
			int skillLevel,long lastProcessTime ){
		RoleSkillItem item = new RoleSkillItem();
		item.setSkillId(skill.getSkillId());
		item.setSkillType(skill.getSkillApplyType()
				.getType());
		if(skillLevel < 1){
			return item ;
		}
		SkillDetail detail = skill.getSkillDetail(skillLevel);
		int cd = detail.getRealCd(role) ;
		item.setCd(cd);
		item.setConsumeHP(detail.getHp());
		item.setConsumeMP(detail.getMp());
		item.setIconId(detail.getIconId());
		item.setActionId(detail.getActionId());
		item.setEffectId(detail.getEffectId());
		item.setEffectTime(detail.getEffectTime());
		item.setTargetType(detail.getClientTargetType());
		item.setMinDistance((short) detail.getMinUseRange());
		item.setMaxDistance((short) detail.getMaxUseRange());
		item.setAttackType(detail.getAttackType());
		item.setPrepareArg(detail.getPrepareArg());
		item.setSkillName(skill.getName());
		item.setMusicId(detail.getMusicId());
		item.setShockRate(detail.getShockRate());
		if (0 != lastProcessTime ) {
			item.setRemainTime((int) DateUtil.getTimeDiff(lastProcessTime,cd));
		}
		return item ;
	}

	public static NpcBodyItem getNpcBodyItem(NpcInstance npc,
			AbstractRole viewRole) {

		NpcTemplate npcTemplate = npc.getNpc();

		/*
		 * NpcBodyItem npcItem = null; if(NpcType.building.getType() ==
		 * npcTemplate.getNpctype()){ npcItem = new NpcBuildItem(); } else{
		 * npcItem = new NpcBodyItem();
		 * npcItem.setType((byte)npcTemplate.getNpctype()); }
		 */

		NpcBodyItem npcItem = new NpcBodyItem();
		npcItem.setType((byte) npcTemplate.getNpctype());
		if(npcTemplate.getNpctype() == NpcType.monster.getType()){
			npcItem.setType((byte)NpcType.npc.getType());
		}
		npcItem.setBoss((byte) npcTemplate.buildFlags());

		npcItem.setForceRelation(viewRole.getForceRelation(npc).getType());
		npcItem.setLevel((byte) npc.getLevel());
		npcItem.setMapx((short) npc.getMapX());
		npcItem.setMapy((short) npc.getMapY());
		npcItem.setNpcname(npcTemplate.getNpcname());
		npcItem.setRoleId(npc.getIntRoleId());
		npcItem.setCurrHP(npc.getCurHP());
		npcItem.setMaxHP(npc.getMaxHP());
		npcItem.setCurrMP(npc.getCurMP());
		npcItem.setMaxMP(npc.getCurMP());
		int resId = npcTemplate.getResid();
		if(npc.getResid()!=0) {
			resId = npc.getResid();
		}
		npcItem.setResId(resId);
		npcItem.setDir((byte) npc.getDir().getType());
		npcItem.setFuncResId(npcTemplate.getFuncResId());
		npcItem.setLockType(npcTemplate.getLockType());
		/*
		 * if (npc.getTarget() != null) {
		 * npcItem.setTargetRoleId(npc.getTarget().getIntRoleId()); }
		 */
		byte resRate = npcTemplate.getResRate();
		if (resRate <= 0) {
			resRate = 10;
		}
		npcItem.setResRate(resRate);
		npcItem.setMusicId(npcTemplate.getMusicId());
		return npcItem;
	}

	/*
	 * public static NpcBodyItem getNpcBodyItem(NpcInstance npc, ForceType
	 * viewForce) { NpcBodyItem npcItem = getNpcBodyItem(npc); //
	 * npcItem.setForceRelation((byte)ForceRelationAdaptor.getForceRelation(viewForce, //
	 * ForceType.getType(npc.getForce())).getType());
	 * //npcItem.setForceRelation((byte) ForceRelationAdaptor.getForceRelation( //
	 * viewForce.getValue(), npc.getForce()).getType()); return npcItem; }
	 */

	public static MapBuffItem getMapBuffItem(MapBuffStat stat) {
		MapBuffItem item = new MapBuffItem();
		item.setBuffRemainTime(stat.getRemainTime());
		item.setResId((short) stat.getBuff().getIconId());
		item.setX((short) stat.getEffectPoint().getX());
		item.setY((short) stat.getEffectPoint().getY());
		return item;
	}
	
	public static List<TitleWearingItem> getTitleItems(RoleInstance role){
		List<TitleRecord> titleList = role.getCurrTitleList();
		if(Util.isEmpty(titleList)){
			return null ;
		}
		List<TitleWearingItem> values = new ArrayList<TitleWearingItem>();
		for(TitleRecord record : titleList){
			GoodsTitle title = GameContext.getGoodsApp().getGoodsTemplate(GoodsTitle.class, record.getTitleId()) ;
			if(null == title){
				continue ;
			}
			values.add(getTitleWearingItem(title));
		}
		return values ;
	}
	
	public static TitleWearingItem getTitleWearingItem(GoodsTitle title){
		TitleWearingItem item = new TitleWearingItem();
		item.setBackImageId((byte)title.getBackImageId());
		item.setId(title.getId());
		item.setName(title.getName());
		item.setNameColor((int)title.getLongNameColor());
		item.setStrokeColor((int)title.getLongStrokeColor());
		item.setTitleEffectId((short)title.getTitleEffectId());
		return item ;
	}
	
	public static RoleBodyItem getRoleBodyItem(RoleInstance role) {
		try {
			RoleBodyItem roleItem = new RoleBodyItem();
			roleItem.setCamp(role.getCampId());
			roleItem.setLevel((byte) role.getLevel());
			roleItem.setMapX((short) role.getMapX());
			roleItem.setMapY((short) role.getMapY());
			roleItem.setRoleId(role.getIntRoleId());
			roleItem.setRoleName(role.getRoleName());
			roleItem.setSex(role.getSex());
			roleItem.setCurrHP(role.getCurHP());
			roleItem.setMaxHP(role.getMaxHP());
			roleItem.setCurrMP(role.getCurMP());
			roleItem.setMaxMP(role.getMaxMP());

			if (role.hasUnion()) {
				Union union = GameContext.getUnionApp().getUnion(role);
				UnionMember member = union.getUnionMember(role.getIntRoleId());
				String color = GameContext.getFactionConfig().getViewColor();
				if (union != null && member != null) {
					roleItem.setFactionName("<" + union.getUnionName()
							+ ">" + member.getPositionNick(member.getPosition()));
					roleItem.setFactionColors((int) Long.parseLong(
							color != null ? color : "ffffffff", 16));
				} else {
					roleItem.setFactionName("");
				}
			} else {
				roleItem.setFactionName("");
			}
			try {
				RoleShape info = GameContext.getUserRoleApp().getRoleShape(role.getRoleId());
				if (null != info) {
					roleItem.setEquipResId((short) info.getEquipResId());
					roleItem.setClothesResId((short) info.getClothesResId());
					roleItem.setWingResId((short) info.getWingResId());
				}
			} catch (Exception ex) {
				logger.error("", ex);
			}
			
			//称号
			roleItem.setTitleItems(getTitleItems(role));

			// 装备特效
			short[] effects = GameContext.getMedalApp()
					.getRoleMedalEffects(role);
			roleItem.setEffectItems(effects);
			// vip等级
			roleItem.setVipLevel(GameContext.getVipApp().getVipLevel(role.getRoleId()));
			//女神
			roleItem.setBattleGoddess(GameContext.getGoddessApp().getOnBattleGoddessItem(role));
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getOnBattleRoleHorse(role.getIntRoleId());
			if(roleHorse != null){
				HorseBase horseBase = GameContext.getHorseApp().getHorseBaseById(roleHorse.getHorseId());
				if(null != horseBase) {
					roleItem.setCurMountResId(horseBase.getImageId());
				}
			}
			roleItem.setPkStatus(role.getPkStatus());
			roleItem.setColor(role.getColor());
			roleItem.setHeroHeadId(GameContext.getHeroApp().getRoleHeroHeadId(role.getRoleId()));
			return roleItem;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	

	public static RoleBodyItem getRoleBodyItem(RoleInstance role,
			RoleInstance player) {
		RoleBodyItem roleItem = getRoleBodyItem(role);
		roleItem.setForceRelation(player.getForceRelation(role).getType());
		return roleItem;
	}

	public static FallItem getFallItem(GoodsOperateBean item) {
		FallItem fallItem = new FallItem();
		if (null == item) {
			return fallItem;
		}
		GoodsBase gb = GameContext.getGoodsApp()
				.getGoodsBase(item.getGoodsId());
		if (null != gb) {
			GoodsLiteNamedItem goodsItem = gb.getGoodsLiteNamedItem();
			goodsItem.setNum((short)item.getGoodsNum());
			goodsItem.setBindType(item.getBindType().getType());
			fallItem.setGoodsItem(goodsItem);
		}
		return fallItem;
	}

	public static List<FallItem> getFallItemList(List<GoodsOperateBean> itemList) {
		List<FallItem> list = new ArrayList<FallItem>();
		if (null == itemList) {
			return list;
		}
		for (GoodsOperateBean item : itemList) {
			int goodsId = item.getGoodsId();
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if (null == gb) {
				continue ;
			}
			GoodsLiteNamedItem goodsItem = gb.getGoodsLiteNamedItem();
			goodsItem.setNum((short)item.getGoodsNum());
			goodsItem.setBindType(item.getBindType().getType());
			FallItem fallItem = new FallItem();
			fallItem.setGoodsItem(goodsItem);
			list.add(fallItem);
		}
		return list;
	}

	public static void pushIncomeMessage(RoleInstance role,
			List<GoodsOperateBean> goodsList, int bindMoney, int gameMoney,
			int goldMoney) {
		try {
			StringBuffer buffer = new StringBuffer("");
			buffer.append(GameContext.getI18n().getText(TextId.BOX_OPEN_WON));
			buffer.append("\n") ;
			if (!Util.isEmpty(goodsList)) {
				for (GoodsOperateBean bean : goodsList) {
					int goodsId = bean.getGoodsId();
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
					if (goodsBase == null) {
						continue;
					}
					buffer.append(goodsBase.getName());
					buffer.append("*").append(bean.getGoodsNum());
					buffer.append("\n");
				}
			}
			if (goldMoney > 0) {
				buffer.append(GameContext.getI18n().getText(TextId.Attri_goldMoney));
				buffer.append(" +").append(goldMoney).append("\n");
			}
			if (bindMoney > 0) {
				buffer.append(GameContext.getI18n().getText(TextId.Attri_bindingGoldMoney));
				buffer.append(" +").append(bindMoney).append("\n");
			}
			if (gameMoney > 0) {
				buffer.append(GameContext.getI18n().getText(TextId.Attri_silverMoney));
				buffer.append(" +").append(gameMoney).append("\n");
			}
			String pushIncome = buffer.toString();
			if (!Util.isEmpty(pushIncome)) {
				C0002_ErrorRespMessage push = new C0002_ErrorRespMessage();
				push.setInfo(pushIncome);
				role.getBehavior().sendMessage(push);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
	}
	
	public static RoleBodyItem getAsyncPvpRoleBodyItem(String roleId, AsyncPvpRoleAttr role, short mapX, short mapY) {
		try {
			RoleBodyItem roleItem = new RoleBodyItem();
			roleItem.setCamp(role.getCamp());
			roleItem.setLevel((byte) role.getLevel());
			roleItem.setMapX(mapX);
			roleItem.setMapY(mapY);
			roleItem.setRoleId(Integer.parseInt(roleId));
			roleItem.setRoleName(role.getRoleName());
			roleItem.setSex(role.getSex());
			roleItem.setCurrHP(role.getMaxHP());
			roleItem.setMaxHP(role.getMaxHP());
			roleItem.setCurrMP(role.getMaxMP());
			roleItem.setMaxMP(role.getMaxMP());
			roleItem.setFactionName("");
			roleItem.setEquipResId(role.getEquipResId());
			roleItem.setClothesResId((short) role.getClothesResId());
//			roleItem.setWingResId((short) role.getWingResId());
			roleItem.setForceRelation(ForceRelation.enemy.getType());
			roleItem.setCurMountResId((short)role.getMountsResId());
			roleItem.setColor(DEFAULT_COLOR);
			return roleItem;
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	public static NpcBodyItem getCampNpcBodyItem(NpcInstance npc,
			AbstractRole viewRole, String factionName, String npcId) {

		NpcTemplate npcTemplate = npc.getNpc();
		NpcBodyItem npcItem = new NpcBodyItem();
		npcItem.setType((byte) npcTemplate.getNpctype());
		if(npcTemplate.getNpctype() == NpcType.monster.getType()){
			npcItem.setType((byte)NpcType.npc.getType());
		}
		npcItem.setBoss((byte) npcTemplate.buildFlags());

		npcItem.setForceRelation(viewRole.getForceRelation(npc).getType());
		npcItem.setLevel((byte) npc.getLevel());
		npcItem.setMapx((short) npc.getMapX());
		npcItem.setMapy((short) npc.getMapY());
		if(npcId.equals(npc.getNpc().getNpcid())){
			npcItem.setNpcname(npc.getNpcname());
		}else{
			npcItem.setNpcname("<" + factionName + ">" + npc.getNpcname());
		}
		npcItem.setRoleId(npc.getIntRoleId());
		npcItem.setCurrHP(npc.getCurHP());
		npcItem.setMaxHP(npc.getMaxHP());
		npcItem.setCurrMP(npc.getCurMP());
		npcItem.setMaxMP(npc.getCurMP());
		int resId = npcTemplate.getResid();
		if(npc.getResid()!=0) {
			resId = npc.getResid();
		}
		npcItem.setResId(resId);
		npcItem.setDir((byte) npc.getDir().getType());
		npcItem.setFuncResId(npcTemplate.getFuncResId());
		npcItem.setLockType(npcTemplate.getLockType());
		/*
		 * if (npc.getTarget() != null) {
		 * npcItem.setTargetRoleId(npc.getTarget().getIntRoleId()); }
		 */
		byte resRate = npcTemplate.getResRate();
		if (resRate <= 0) {
			resRate = 10;
		}
		npcItem.setResRate(resRate);
		npcItem.setMusicId(npcTemplate.getMusicId());
		return npcItem;
	}
	
	/**
	 * 判断物品是否在商城有售
	 * @param idAndNums
	 * @return
	 */
	public static boolean isShopSellAllGoods(java.util.Map<Integer, Integer> idAndNums){
		for(Entry<Integer, Integer> entry : idAndNums.entrySet()){
			ShopGoods shopGoods = GameContext.getShopApp().getShopGoods(entry.getKey());
			if(null == shopGoods){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 一键操作游戏币或道具不足时返回-576协议
	 * @param oneKeyType
	 * @param fee
	 * @param idAndNums
	 * @return
	 */
	public static Message buildOneKeyOpFailMessage(OneKeyOpType oneKeyType
			, int fee, java.util.Map<Integer, Integer> idAndNums){
		
		boolean isNeedGoods = !Util.isEmpty(idAndNums);
		boolean isNeedMoney = fee > 0;
		if(!isNeedMoney && !isNeedGoods){
			return null;
		}
		
		if((isNeedMoney && !isNeedGoods) || (isNeedGoods && !Converter.isShopSellAllGoods(idAndNums))){
			C0003_TipNotifyMessage tips = new C0003_TipNotifyMessage();
			tips.setMsgContext(GameContext.getI18n().getText(TextId.GOODS_SLIVER_NO_ENOUGH));
			return tips;
		}
		
		C0576_OneKeyOpertateFailRespMessage respMsg = new C0576_OneKeyOpertateFailRespMessage();
		respMsg.setOneKeyOpType(oneKeyType.getType());
		respMsg.setOneKeyOpName(oneKeyType.getName());
		respMsg.setFee(fee);
		List<OneKeyOperateFailItem> failItemList = new ArrayList<OneKeyOperateFailItem>();
		for(Entry<Integer, Integer> entry : idAndNums.entrySet()){
			int goodsId = entry.getKey();
			ShopGoods shopGoods = GameContext.getShopApp().getShopGoods(goodsId);
			if(null == shopGoods){
				continue;
			}
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			if(null == gb){
				continue;
			}
			OneKeyOperateFailItem failItem = new OneKeyOperateFailItem();
			ShopMoneyType priceType = shopGoods.getPriceType();
			failItem.setPriceType(priceType.getType());
			ShopGoodsItem sgItem = new ShopGoodsItem();
			byte bindType = gb.getBindType();
			if(priceType == ShopMoneyType.GoldMoney){
				//金币购买绑定类型根据配置
				if(shopGoods.getGoldBindType() != BindingType.template.getType()){
					bindType = shopGoods.getGoldBindType();
				}
				sgItem.setPrice(shopGoods.getGoldPrice());
				sgItem.setDisPrice(shopGoods.getDisGoldPrice());
			}else{
				//绑金购买一定绑定
				bindType = BindingType.already_binding.getType();
				sgItem.setPrice(shopGoods.getBindPrice());
				sgItem.setDisPrice(shopGoods.getDisBindPrice());
			}
			sgItem.setStatus(shopGoods.getStatus(priceType).getType());
			
			GoodsLiteNamedItem liteItem = gb.getGoodsLiteNamedItem();
			//绑定类型
			liteItem.setBindType(bindType);
			//默认数目
			/*int stackNum = Math.min(gb.getOverlapCount(), 
					shopGoods.getDefaultBuyNum());
			liteItem.setNum((short)stackNum);*/
			liteItem.setNum(entry.getValue().shortValue());
			sgItem.setGoodsItem(liteItem);
			//试穿资源ID
			sgItem.setResId((short) gb.getResId());
			failItem.setShopGoodsItem(sgItem);
			failItemList.add(failItem);
		}
		respMsg.setItemList(failItemList);
		return respMsg;
	}
	
	/**
	 * 构建一键喂养面板信息
	 * @param totalExp
	 * @param critMap
	 * @return
	 */
	public static String mountSoulQualityIncrTip(int totalExp, java.util.Map<Integer, Integer> critMap){
		StringBuffer buffer = new StringBuffer("");
		buffer.append(GameContext.getI18n().messageFormat(TextId.ONE_KEY_INCR_TIPS,totalExp));
		if(!Util.isEmpty(critMap)){
			for(Entry<Integer, Integer> entry : critMap.entrySet()){
				buffer.append(buildCritTip(entry.getKey(), entry.getValue()));
			}
		}
		return buffer.toString();
	}
	
	/**
	 * 构建培养过程中暴击信息
	 * @param crit
	 * @param num
	 * @return
	 */
	private static  String buildCritTip(int crit, int num){
		String color = "";
		if(crit < 4){ //bule
			color = "FF00a8ff";
		}
		else if(crit < 10){ //purple
			color = "FFae00ff";
		}
		else{ //gold
			color = "FFffb400";
		}
		return GameContext.getI18n().messageFormat(TextId.ONE_KEY_CRIT_TIPS,color, crit, num);
	}
}
