package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.debug.message.item.RoleHeroItem;
import com.game.draco.debug.message.item.RoleHeroSkillItem;
import com.game.draco.debug.message.request.C10081_RoleHeroDetailReqMessage;
import com.game.draco.debug.message.response.C10081_RoleHeroDetailRespMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RoleHeroDetailAction extends ActionSupport<C10081_RoleHeroDetailReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10081_RoleHeroDetailReqMessage req) {
		C10081_RoleHeroDetailRespMessage resp = new C10081_RoleHeroDetailRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		
		try {
			String roleId = req.getRoleId();
			if(roleId==null || roleId.trim().equals("")){
				String roleName = req.getRoleName();
				RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
				roleId = role.getRoleId();
			}
			Collection<RoleHero> heros = GameContext.getUserHeroApp().getAllRoleHero(roleId);
			if(Util.isEmpty(heros)){
				heros = GameContext.getBaseDAO().selectList(RoleHero.class, 
						RoleHero.ROLE_ID, roleId);
			}
			for (RoleHero roleHero : heros) {
				postFromStore(roleHero);
			}
			
			List<RoleHeroItem> list = buildRoleHeroItemList(heros);
			if(Util.isEmpty(list)){
				resp.setInfo(GameContext.getI18n().getText(TextId.GM_ROLE_HERO_EMPTY));
				return resp;
			}
			resp.setHeroNum(list.size());
			resp.setList(list);
			resp.setRoleId(roleId);
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		} catch (Exception e) {
			this.logger.error("RoleHeroDetailAction error: ", e);
			e.printStackTrace();
			resp.setType((byte)RespTypeStatus.FAILURE);
			return resp;
		}
	}

	private List<RoleHeroItem> buildRoleHeroItemList(Collection<RoleHero> heros) {
		List<RoleHeroItem> list = Lists.newArrayList();
		for (RoleHero roleHero : heros) {
			RoleHeroItem item = buildRoleHeroItem(roleHero);
			list.add(item);
		}
		return list;
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
