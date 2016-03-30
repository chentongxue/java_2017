package sacred.alliance.magic.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.RoleShape;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.horse.config.HorseProp;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.npc.type.NpcType;
import com.game.draco.app.shop.config.ShopGoodsConfig;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.title.domain.TitleRecord;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.union.domain.UnionMember;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.FallItem;
import com.game.draco.message.item.GoodsLiteNamedExItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.item.MapBuffItem;
import com.game.draco.message.item.NpcBodyItem;
import com.game.draco.message.item.RoleBodyItem;
import com.game.draco.message.item.RoleSkillItem;
import com.game.draco.message.item.TitleWearingItem;
import com.game.draco.message.response.C1348_CommonRewardRespMessage;
import com.google.common.collect.Lists;

public class Converter {

	private final static Logger logger = LoggerFactory.getLogger(Converter.class);
	private final static int DEFAULT_COLOR = (int)Long.parseLong("FFFFFFFF",16);
	
	public static RoleSkillItem getRoleSkillItem(RoleInstance role,Skill skill,
			int skillLevel,long lastProcessTime ){
		RoleSkillItem item = new RoleSkillItem();
		item.setSkillId(skill.getSkillId());
		item.setSkillType(skill.getSkillApplyType()	.getType());
		if(skillLevel < 1){
			return item ;
		}
		SkillDetail detail = null;
		if(skill.isNormalAttack()){
			detail = skill.getSkillDetail(1);
		}else{
			detail = skill.getSkillDetail(skillLevel);
		}
		
		int cd = detail.getRealCd(role) ;
		item.setCd(cd);
		item.setConsumeHP(detail.getHp());
		item.setIconId(detail.getIconId());
		item.setActionId(detail.getActionId());
		item.setEffectId(detail.getEffectId());
		item.setTargetType(detail.getClientTargetType());
		item.setMinDistance((short) detail.getMinUseRange());
		item.setMaxDistance((short) detail.getMaxUseRange());
		item.setAttackType(detail.getAttackType());
		item.setPrepareArg(detail.getPrepareArg());
		item.setSkillName(skill.getName());
		item.setMusicId(detail.getMusicId());
		item.setShockRate(detail.getShockRate());
		if (0 != lastProcessTime ) {
			item.setRemainTime((int) DateUtil.dateDiffMillis(lastProcessTime,cd));
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
		int resId = npcTemplate.getResid();
		if(npc.getResid()!=0) {
			resId = npc.getResid();
		}
		npcItem.setResId(resId);
		npcItem.setDir(npc.getDir());
		npcItem.setFuncResId(npcTemplate.getFuncResId());
		npcItem.setProps(npcTemplate.getNpcProps());
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
		npcItem.setSeriesId(npcTemplate.getSeriesId());
		npcItem.setGearId(npcTemplate.getGearId());
		npcItem.setColor(npc.getColor());
		return npcItem;
	}


	public static MapBuffItem getMapBuffItem(BuffStat stat) {
		Object info = stat.getContextInfo() ;
		if(null == info){
			return null ;
		}
		if(!(info instanceof Point)){
			return null ;
		}
		MapBuffItem item = new MapBuffItem();
		item.setBuffRemainTime(stat.getRemainTime());
		item.setResId((short) stat.getBuff().getIconId());
		Point point = (Point)info ;
		item.setX((short) point.getX());
		item.setY((short) point.getY());
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
			roleItem.setBattlePet(GameContext.getPetApp().getOnBattlePetItem(role.getRoleId()));
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getOnBattleRoleHorse(role.getIntRoleId());
			if(roleHorse != null){
				HorseProp horseProp = GameContext.getRoleHorseApp().getHorseProp(roleHorse.getHorseId(),roleHorse.getQuality(),roleHorse.getStar());
				roleItem.setCurMountResId(horseProp.getResId());
			}
			roleItem.setPkStatus(role.getPkStatus());
			roleItem.setColor(role.getColor());
			roleItem.setHeroHeadId(role.getHeroHeadId());
			roleItem.setSeriesId(role.getHeroSeriesId());
			roleItem.setGearId(role.getHeroGearId());
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
			Collection<GoodsOperateBean> goodsList, int potential, int gameMoney,
			int goldMoney) {
		try {
			C1348_CommonRewardRespMessage respMsg = new C1348_CommonRewardRespMessage();
			if(!Util.isEmpty(goodsList)){
				List<GoodsLiteNamedExItem> awardGoodsList = Lists.newArrayList() ;
				for (GoodsOperateBean bean : goodsList) {
					int goodsId = bean.getGoodsId();
					GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(goodsId);
					if (goodsBase == null) {
						continue;
					}
					GoodsLiteNamedExItem item = goodsBase.getGoodsLiteNamedExItem();
					item.setNum((short)bean.getGoodsNum());
					item.setBindType(bean.getBindType().getType());
					awardGoodsList.add(item);
				}
				respMsg.setAwardGoodsList(awardGoodsList);
			}
			List<AttriTypeValueItem> awardAttrList = Lists.newArrayList() ;
			if (goldMoney > 0) {
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.goldMoney.getType());
				item.setAttriValue(goldMoney);
				awardAttrList.add(item) ;
			}
			if (potential > 0) {
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.potential.getType());
				item.setAttriValue(potential);
				awardAttrList.add(item) ;
			}
			if (gameMoney > 0) {
				AttriTypeValueItem item = new AttriTypeValueItem();
				item.setAttriType(AttributeType.gameMoney.getType());
				item.setAttriValue(gameMoney);
				awardAttrList.add(item) ;
			}
			respMsg.setAwardAttrList(awardAttrList);
			respMsg.setSuccess(RespTypeStatus.SUCCESS);
			role.getBehavior().sendMessage(respMsg);
			
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
			roleItem.setFactionName("");
			roleItem.setClothesResId((short) role.getClothesResId());
			roleItem.setForceRelation(ForceRelation.enemy.getType());
			roleItem.setCurMountResId((short)role.getHorseResId());
			roleItem.setGearId(role.getGearId());
			roleItem.setSeriesId(role.getSeriesId());
			//TODO:
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
		int resId = npcTemplate.getResid();
		if(npc.getResid()!=0) {
			resId = npc.getResid();
		}
		npcItem.setResId(resId);
		npcItem.setDir(npc.getDir());
		npcItem.setFuncResId(npcTemplate.getFuncResId());
		npcItem.setProps(npcTemplate.getNpcProps());
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
		npcItem.setSeriesId(npcTemplate.getSeriesId());
		npcItem.setGearId(npcTemplate.getGearId());
		return npcItem;
	}
	
	/**
	 * 判断物品是否在商城有售
	 * @param idAndNums
	 * @return
	 */
	public static boolean isShopSellAllGoods(java.util.Map<Integer, Integer> idAndNums){
		for(Entry<Integer, Integer> entry : idAndNums.entrySet()){
			ShopGoodsConfig shopGoods = GameContext.getShopApp().getShopGoods(entry.getKey());
			if(null == shopGoods){
				return false;
			}
		}
		return true;
	}

	
}
