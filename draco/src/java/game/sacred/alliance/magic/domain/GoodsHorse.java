package sacred.alliance.magic.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.horse.config.HorseBase;
import com.game.draco.app.horse.config.HorseSkill;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsBaseHorseItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.SkillTipsItem;
import com.google.common.collect.Lists;

public @Data class GoodsHorse extends GoodsBase {

	//坐骑ID
	private int horseId;
	
	private int shadowId;
	
	private int shadowNum;
	
	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseHorseItem item = new GoodsBaseHorseItem();
		this.setGoodsBaseItem(roleGoods, item);
		
		
		List<AttriTypeStrValueItem> attrItemList = new ArrayList<AttriTypeStrValueItem>();
		HorseBase base = GameContext.getHorseApp().getHorseBaseById(horseId);
		
		if(base == null){
			return item;
		}
		
		for(AttriItem attr: base.getAttriItemList()){
			AttriTypeStrValueItem attrItem = new AttriTypeStrValueItem();
			attrItem.setType(attr.getAttriTypeValue());
			attrItem.setValue(String.valueOf(attr.getValue()));
			attrItemList.add(attrItem);
		}
		
		item.setAttriList(attrItemList);
		
		List<HorseSkill> skillList = GameContext.getHorseApp().getHorseSkillList(horseId);
		if(Util.isEmpty(skillList)){
			return item;
		}
		
		List<SkillTipsItem> skillItemList = Lists.newArrayList();
		
		for(HorseSkill horseSkill : skillList){
			SkillTipsItem skillItem = new SkillTipsItem();
			skillItem.setSkillId(horseSkill.getSkillId());
			
			Skill skill = GameContext.getSkillApp().getSkill(horseSkill.getSkillId());
			if(null == skill){
				continue ;
			}
			skillItem.setSkillId(horseSkill.getSkillId());
			skillItem.setImageId(skill.getIconId());
			skillItem.setSkillName(skill.getName());
			SkillDetail detail = skill.getSkillDetail(1);
			skillItem.setSkillDesc(detail.getDesc());
			skillItemList.add(skillItem);
		}
		
		item.setSkillList(skillItemList);
		
		return item;
	}

	@Override
	public void init(Object initData) {
	}
	
}
