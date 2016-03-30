package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.goods.HeroEquipBackpack;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.debug.message.item.BagItem;
import com.game.draco.debug.message.item.RoleHeroEquipItem;
import com.game.draco.debug.message.item.RoleHeroItem;
import com.game.draco.debug.message.item.RoleHeroSkillItem;
import com.game.draco.debug.message.request.C10083_RoleHeroInfoReqMessage;
import com.game.draco.debug.message.response.C10083_RoleHeroInfoRespMessage;
import com.google.common.collect.Maps;
/**
 * 查看玩家英雄信息（主要是装备信息）
 */
public class RoleHeroInfoAction extends ActionSupport<C10083_RoleHeroInfoReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10083_RoleHeroInfoReqMessage req) {
		
		
		C10083_RoleHeroInfoRespMessage resp = new C10083_RoleHeroInfoRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		
		try {
			String roleId = req.getRoleId();
			int heroId = req.getHeroId();
			if(roleId==null || roleId.trim().equals("")){
				return resp;
			}
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleId(roleId);
			
			
			//获得英雄
			Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
			if(Util.isEmpty(heros)){
				heros = GameContext.getBaseDAO().selectList(RoleHero.class, 
						RoleHero.ROLE_ID, roleId);
			}
			//获得英雄的装备
			Map<String,List<RoleGoods>> equipMap = GameContext.getHeroApp().buildHeroEquipMap(role.getRoleId());
			if(Util.isEmpty(equipMap)){
				return resp;
			}
			List<RoleGoods>  list = equipMap.get(String.valueOf(heroId));
			
			if(Util.isEmpty(list)){
				return resp;
			}
			//保留
			HeroEquipBackpack equippack = new HeroEquipBackpack(role,
					ParasConstant.HERO_EQUIP_MAX_NUM,heroId);
			equippack.initGoods(list);
			List<BagItem> equipItems = new ArrayList<BagItem>();
			for(RoleGoods rg:equippack.getAllGoods()){
				BagItem it = new BagItem();
				int goodsId = rg.getGoodsId();
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(gb != null){
					it.setGoodsName(gb.getName());
				}
				it.setInstanceId(rg.getId());
				it.setNum(1);
				it.setGoodsId(gb.getId());
				it.setDesc(gb.getDesc());
				it.setStar(rg.getStar());
				it.setQuality(rg.getQuality());
				it.setStrengthenLevel(rg.getStrengthenLevel());
				it.setBind(rg.getBind());
				it.setDesc(gb.getDesc());
				equipItems.add(it);
			}
			resp.setEquipItems(equipItems);
			
			/*//英雄本身
			RoleHero roleHero = GameContext.getUserHeroApp().getRoleHero(roleId,heroId);
			if(roleHero == null){
				roleHero = GameContext.getBaseDAO().selectEntity(RoleHero.class, RoleHero.ROLE_ID, roleId, RoleHero.HERO_ID, heroId);
			}
			postFromStore(roleHero);
			RoleHeroItem heroItem = buildRoleHeroItem(roleHero);
			
			resp.setHeroItem(heroItem);*/
//			
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		} catch (Exception e) {
			this.logger.error("RoleHeroInfoAction error: ", e);
			e.printStackTrace();
			resp.setType((byte)RespTypeStatus.FAILURE);
			return resp;
		}
	}
	private RoleHeroItem buildRoleHeroItem(RoleHero roleHero) {
		RoleHeroItem item = new RoleHeroItem();
		
		item.setExp(roleHero.getExp());
		item.setHeroId(roleHero.getHeroId());
		item.setLevel((byte)roleHero.getLevel());
		
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(roleHero.getHeroId());
		String name = (null == gb) ? "" : gb.getName();
		
		item.setName(name);
		item.setQuality(roleHero.getQuality());
		item.setStar(roleHero.getStar());
		item.setBattleScore(roleHero.getScore());//11 06
		byte onBattle = roleHero.getOnBattle();
		item.setOnBattle(onBattle);//11 06
		Map<Short,RoleSkillStat> skillMap = roleHero.getSkillMap();
		if(!Util.isEmpty(skillMap)){
			List<RoleHeroSkillItem> list = buildRoleHeroSkillItemList(skillMap);
			item.setSkillList(list);
		}
		return item;
	}

	private List<RoleHeroSkillItem> buildRoleHeroSkillItemList(
			Map<Short, RoleSkillStat> skillMap) {
		List<RoleHeroSkillItem> list = new ArrayList<RoleHeroSkillItem>();
		for(RoleSkillStat skillStat: skillMap.values()){
			RoleHeroSkillItem item = buildRoleHeroSkillItem(skillStat);
			list.add(item);
		}
		return list;
	}

	private RoleHeroSkillItem buildRoleHeroSkillItem(RoleSkillStat stat) {
		RoleHeroSkillItem item = new RoleHeroSkillItem();
		item.setSkillId(stat.getSkillId());
		
		Skill skill = GameContext.getSkillApp().getSkill(stat.getSkillId());
		if(skill!=null){
			item.setSkillName(skill.getName());
		}
		item.setSkillLevel(stat.getSkillLevel());
		item.setAddSkillLevel(stat.getAddSkillLevel());
		return item;
	}

	private void postFromStore(RoleHero hero){
		//解析技能MAP
		Map<Short,Integer> map = Util.parseShortIntMap(hero.getSkills());
		if(!Util.isEmpty(map)){
			Map<Short,RoleSkillStat> skillMap = Maps.newHashMap() ;
			for(Iterator<Map.Entry<Short, Integer>> it = map.entrySet().iterator();it.hasNext();){
				Map.Entry<Short, Integer> entry = it.next() ;
				RoleSkillStat stat = new RoleSkillStat() ;
				stat.setSkillId(entry.getKey());
				stat.setSkillLevel(entry.getValue());
				stat.setRoleId("HERO_" + hero.getHeroId());
				stat.setLastProcessTime(this.getLastProcessTimeFromStore(stat));
				skillMap.put(stat.getSkillId(), stat) ;
			}
			hero.setSkillMap(skillMap);
		}
		this.initSkill(hero);
	}
	private long getLastProcessTimeFromStore(RoleSkillStat stat){
		return GameContext.getUserSkillApp().getLastProcessTimeFromCache(stat.getRoleId(), 
				stat.getSkillId());
	}
	public void initSkill(RoleHero hero) {
		// 普通攻击
		GoodsHero template = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsHero.class, hero.getHeroId()) ;
		if(null == template){
			logger.error("hero template not exist,heroId=" + hero.getHeroId());
			return  ;
		}
		this.initSkill(hero, template.getCommonSkill());
		for(short skillId : template.getSkillIdList() ){
			this.initSkill(hero, skillId);
		}
	}
	
	private void initSkill(RoleHero hero,short skillId){
		RoleSkillStat stat = hero.getSkillMap().get(skillId);
		if(null != stat){
			return ;
		}
		stat = new RoleSkillStat(); 
		stat.setSkillId(skillId);
		stat.setSkillLevel(1);
		stat.setRoleId("HERO_" + hero.getHeroId());
		stat.setLastProcessTime(0);
		hero.getSkillMap().put(skillId, stat);
	}
}
