package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.debug.message.item.RolePetItem;
import com.game.draco.debug.message.item.RolePetSkillItem;
import com.game.draco.debug.message.request.C10082_RolePetDetailReqMessage;
import com.game.draco.debug.message.response.C10082_RolePetDetailRespMessage;

public class RolePetDetailAction extends ActionSupport<C10082_RolePetDetailReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10082_RolePetDetailReqMessage req) {
		C10082_RolePetDetailRespMessage resp = new C10082_RolePetDetailRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		
		try {
			String roleId = req.getRoleId();
			if(roleId==null || roleId.trim().equals("")){
				String roleName = req.getRoleName();
				RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
				roleId = role.getRoleId();
			}
			
			List<RolePet> petList = null;
			//玩家是否在线
			if(GameContext.getOnlineCenter().isOnlineByRoleId(roleId)){
				Map<Integer, RolePet> petsMap = GameContext.getUserPetApp().getAllRolePet(roleId);
				petList = new ArrayList<RolePet>(petsMap.values());
			}else{
				petList = GameContext.getBaseDAO().selectList(RolePet.class, RolePet.MASTER_ID, roleId);
				if(!Util.isEmpty(petList)){
					for (RolePet pet : petList) {
						pet.setQuality(getPetQuality(pet));
						initSkill(pet);
					}
				}
			}
			if(Util.isEmpty(petList)){
				resp.setInfo(GameContext.getI18n().getText(TextId.GM_ROLE_PET_EMPTY));
				return resp;
			}
			
			List<RolePetItem> list = buildRolepPetItemList(petList);
			resp.setPetNum(petList.size());
			resp.setList(list);
			resp.setType((byte)RespTypeStatus.SUCCESS);
			resp.setRoleId(roleId);
			return resp;
		} catch (Exception e) {
			this.logger.error("RoleHorseDetailAction error: ", e);
			resp.setType((byte)RespTypeStatus.FAILURE);
			return resp;
		}
	}
	private List<RolePetItem> buildRolepPetItemList(List<RolePet> petList) {
		List<RolePetItem> skillItemList = new ArrayList<RolePetItem>();
		for (RolePet rolePet : petList) {
			RolePetItem item = new RolePetItem();
			
			GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
			if (null != goodsPet) {
				item.setPetName(goodsPet.getName());
			}
			
			item.setPetId(rolePet.getPetId());
//			item.setPetName(petName);
			item.setExp(rolePet.getExp());
			item.setQuality(rolePet.getQuality());
			item.setLevel(rolePet.getLevel());
			item.setStar(rolePet.getStar());
			item.setStarProgress(rolePet.getStarProgress());
			
			item.setBattleScore(rolePet.getScore());//11 06
			item.setOnBattle(rolePet.getOnBattle());   //11 06
			List<RoleSkillStat> list = new ArrayList<RoleSkillStat>(rolePet.getSkillMap().values());
			if(!Util.isEmpty(list)){
				List<RolePetSkillItem> skillList = buildRolePetSkillItemList(list);
				item.setSkillList(skillList);
			}
			
			skillItemList.add(item);
		}
		return skillItemList;
	}
	private List<RolePetSkillItem> buildRolePetSkillItemList(List<RoleSkillStat> list) {
		List<RolePetSkillItem> itemList = new ArrayList<RolePetSkillItem>();
		for (RoleSkillStat stat : list) {
			RolePetSkillItem item = new RolePetSkillItem();
			item.setSkillId(stat.getSkillId());
			
			//取技能名称
			Skill petSkill = GameContext.getSkillApp().getSkill(stat.getSkillId());
			if(petSkill!=null)
				item.setSkillName(petSkill.getName());
			item.setSkillLevel(stat.getSkillLevel());
			itemList.add(item);
		}
		return itemList;
	}
	// 获得宠物品质
	private byte getPetQuality(RolePet rolePet) {
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		if (null == goodsPet) {
			return 0;
		}
		return goodsPet.getQualityType();
	}
	private void initSkill(RolePet pet) {
		// 普通攻击
		GoodsPet template = GameContext.getGoodsApp().getGoodsTemplate(
				GoodsPet.class, pet.getPetId()) ;
		if(null == template){
			logger.error("pet template not exist,heroId=" + pet.getPetId());
			return  ;
		}
		this.initSkill(pet, template.getCommonSkill());
		for(short skillId : template.getSkillList() ){
			this.initSkill(pet, skillId);
		}
	}
	
	private void initSkill(RolePet pet,short skillId){
		RoleSkillStat stat = pet.getSkillMap().get(skillId);
		if(null != stat){
			return ;
		}
		stat = new RoleSkillStat(); 
		stat.setSkillId(skillId);
		stat.setSkillLevel(GameContext.getPetApp().getPetSkillLevel(pet, skillId));
		pet.getSkillMap().put(skillId, stat);
	}
}
