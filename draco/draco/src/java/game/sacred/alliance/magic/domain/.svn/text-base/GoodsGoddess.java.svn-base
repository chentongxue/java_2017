package sacred.alliance.magic.domain;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.config.GoddessLevelup;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoddessTipsSkillItem;
import com.game.draco.message.item.GoodsBaseGoddessItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.google.common.collect.Lists;

public @Data class GoodsGoddess extends GoodsBase {
	
	private int startLevel;
	private int goodsId; //兑换道具id
	private short goodsNum; //兑换道具数量
	
	private short commonSkill ;
	private short skill1;
	private short skill2;
	private String broadcast; //活动女神时广播
	
	private List<Short> skillIdList = Lists.newArrayList();

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseGoddessItem item = new GoodsBaseGoddessItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setResId((short) this.getResId());
		//属性
		GoddessLevelup curr = GameContext.getGoddessApp().getGoddessLevelup(this.id, this.startLevel) ;
		if(null != curr){
			List<AttriTypeStrValueItem> attriItemList = Lists.newArrayList() ;
			for(AttriItem currItem : curr.getAttriItemList()){
				AttriTypeStrValueItem asv = new AttriTypeStrValueItem();
				asv.setType(currItem.getAttriTypeValue());
				asv.setValue(AttributeType.formatValue(currItem.getAttriTypeValue(),
						currItem.getAttriTypeValue()));
				attriItemList.add(asv);
			}
			item.setAttriList(attriItemList);
		}
		item.setSkillList(getGoddessTipSkillList());
		return item;
	}

	@Override
	public void init(Object initData) {
		skillIdList.clear();
		this.initSkill(this.skill1);
		this.initSkill(this.skill2);
	}
	
	private void initSkill(short skillId){
		if(skillId <=0){
			return ;
		}
		this.skillIdList.add(skillId);
	}
	
	private List<GoddessTipsSkillItem> getGoddessTipSkillList() {
		// 技能
		List<GoddessTipsSkillItem> skillList = Lists.newArrayList();
		try {
			for (short skillId : skillIdList) {
				Skill skill = GameContext.getSkillApp().getSkill(skillId);
				if (null == skill) {
					continue;
				}
				GoddessTipsSkillItem skillItem = new GoddessTipsSkillItem();
				skillItem.setSkillId(skillId);
				skillItem.setImageId(skill.getIconId());
				skillItem.setSkillName(skill.getName());
				SkillDetail detail = skill.getSkillDetail(1);
				skillItem.setSkillDesc(detail.getDesc());
				skillList.add(skillItem);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		}
		return skillList;
	}
	
	public GoodsLiteNamedItem getEnlistGoodsLiteNamedItem(){
		if(this.goodsId <= 0){
			return null;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.goodsId);
		if(null == gb){
			return null;
		}
		GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
		item.setNum(this.goodsNum);
		return item;
	} 

}
