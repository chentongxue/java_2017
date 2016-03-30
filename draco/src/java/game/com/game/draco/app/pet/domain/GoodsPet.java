package com.game.draco.app.pet.domain;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.RoleGoods;

import com.game.draco.GameContext;
import com.game.draco.app.pet.PetAppImpl;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsBasePetItem;
import com.game.draco.message.item.SkillTipsItem;
import com.google.common.collect.Lists;

public @Data class GoodsPet extends GoodsBase {

	private int shadowId;// 碎片Id
	private int shadowNum;// 碎片数量
	private int swallowExp;// 吞噬可获得的经验
	private byte maxStar;// 最大星级
	private byte ratio;// 缩放比
	private short panelResId;// 面板资源ID
	private byte panelRatio;// 面板缩放比

	/**
	 * 宠物技能
	 */
	private short skill1;
	private short skill2;
	private short skill3;
	private short attackDistance;
	private short commonSkill;
	private List<Short> skillList = Lists.newArrayList();

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBasePetItem item = new GoodsBasePetItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setSecondType(secondType);
		item.setResId((short) this.getResId());
		//基本属性
		AttriBuffer curr = GameContext.getPetApp().getPetAttriBuffer(this.id, this.level, this.qualityType, this.star);
		List<AttriTypeStrValueItem> attriItemList = Lists.newArrayList();
		for (AttributeType at : PetAppImpl.attributeTypeList) {
            AttriItem currItem = curr.getMap().get(at.getType());
            if(null == currItem){
                continue;
            }
			AttriTypeStrValueItem asv = new AttriTypeStrValueItem();
			asv.setType(at.getType());
			asv.setValue(AttributeType.formatValue(at.getType(), currItem.getValue()));
			attriItemList.add(asv);
		}
		item.setAttriList(attriItemList);
		// 技能
		List<SkillTipsItem> skillList = Lists.newArrayList() ;
		for(short skillId : this.skillList){
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill){
				continue ;
			}
			if (skill.isNormalAttack()) {
				continue;
			}
			SkillTipsItem skillItem = new SkillTipsItem();
			skillItem.setSkillId(skillId);
			skillItem.setImageId(skill.getIconId());
			skillItem.setSkillName(skill.getName());
			SkillDetail detail = skill.getSkillDetail(1);
			skillItem.setSkillDesc(detail.getDesc());
			skillList.add(skillItem);
		}
		item.setSkillList(skillList);
		item.setSwallowExp(this.swallowExp);
		item.setMaxStar(this.maxStar);
		return item;
	}

	@Override
	public void init(Object initData) {
		this.skillList.clear();
		this.initSkill(this.skill1);
		this.initSkill(this.skill2);
		this.initSkill(this.skill3);
		this.initSkill(this.commonSkill);
	}

	private void initSkill(short skillId) {
		if (skillId <= 0) {
			return;
		}
		this.skillList.add(skillId);
	}
	
}
