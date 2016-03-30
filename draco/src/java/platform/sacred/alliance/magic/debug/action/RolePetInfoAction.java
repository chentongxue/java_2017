package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.RolePet;
import com.game.draco.app.rune.domain.MosaicRune;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.debug.message.item.RolePetItem;
import com.game.draco.debug.message.item.RolePetRuneItem;
import com.game.draco.debug.message.item.RolePetSkillItem;
import com.game.draco.debug.message.request.C10082_RolePetDetailReqMessage;
import com.game.draco.debug.message.request.C10084_RolePetInfoReqMessage;
import com.game.draco.debug.message.response.C10082_RolePetDetailRespMessage;
import com.game.draco.debug.message.response.C10084_RolePetInfoRespMessage;

public class RolePetInfoAction extends ActionSupport<C10084_RolePetInfoReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10084_RolePetInfoReqMessage req) {
		C10084_RolePetInfoRespMessage resp = new C10084_RolePetInfoRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		
		try {
			String roleId = req.getRoleId();
			int petId = req.getPetId();
			if(roleId==null || roleId.trim().equals("")){
				return resp;
			}
			RoleInstance role = GameContext.getUserRoleApp().getRoleByRoleId(roleId);
			RolePet pet = null;
			//玩家是否在线
			if(GameContext.getOnlineCenter().isOnlineByRoleId(roleId)){
				pet = GameContext.getUserPetApp().getRolePet(roleId, petId);
			}else{
				pet = GameContext.getBaseDAO().selectEntity(RolePet.class, RolePet.MASTER_ID, roleId, RolePet.PET_ID, petId);
				if(pet == null){
					return resp;
				}
				pet.setQuality(getPetQuality(pet));
				initSkill(pet);
			}
			
/*			RolePetItem petItem = buildRolepPetItem(pet);
			resp.setPetItem(petItem);*/
			
			//符文
			List<RolePetRuneItem> runeItems = new ArrayList<RolePetRuneItem>();
			for(MosaicRune r:pet.getMosaicRuneList()){
				RolePetRuneItem it = new RolePetRuneItem();
				int goodsId = r.getGoodsId();
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
				if(gb != null){
					it.setGoodsName(gb.getName());
				}
				RoleGoods rg = r.getRoleGoods(roleId);
				it.setBind(rg.getBind());
				it.setNum(1);
				it.setQuality(rg.getQuality());
				it.setStar(rg.getStar());
				it.setStrengthenLevel(rg.getStrengthenLevel());
				it.setGoodsId(gb.getId());
				it.setInstanceId(r.getRoleGoods(roleId).getId());
				it.setLocation(r.getHole());
				StringBuffer sb = new StringBuffer();
				sb.append("(").append(r.getHole()).append("");
				List<AttriItem> attriList = r.getAttriList();
				int size = attriList.size();
				if(size==0){
					runeItems.add(it);
					continue;
				}
				for (int i = 0; i < size - 1; i++) {
					byte type = attriList.get(i).getAttriTypeValue();
					sb.append("【")
					.append(AttributeType.get(type).getName())
					.append("+")
					.append(attriList.get(i).getValue())
					.append(",")
					.append(attriList.get(i).getPrecValue())
					.append("%】;");
				}
				byte type = attriList.get(size - 1).getAttriTypeValue();
				sb.append("【")
				.append(AttributeType.get(type).getName())
				.append("+")
				.append(attriList.get(size - 1).getValue())
				.append(",")
				.append(attriList.get(size - 1).getPrecValue())
				.append("%】");
//				sb.append(gb.getDesc());
				it.setDesc(sb.toString());
				
				runeItems.add(it);
			}
			resp.setRuneItems(runeItems);
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		} catch (Exception e) {
			this.logger.error("RoleHorseDetailAction error: ", e);
			resp.setType((byte)RespTypeStatus.FAILURE);
			return resp;
		}
	}
	private RolePetItem buildRolepPetItem(RolePet rolePet) {
		RolePetItem item = new RolePetItem();
			
		GoodsPet goodsPet = GameContext.getGoodsApp().getGoodsTemplate(GoodsPet.class, rolePet.getPetId());
		if (null != goodsPet) {
			item.setPetName(goodsPet.getName());
		}
			
		item.setPetId(rolePet.getPetId());
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
		return item;
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
