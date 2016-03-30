package sacred.alliance.magic.domain;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;

import com.game.draco.GameContext;
import com.game.draco.app.hero.HeroLoveType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsBaseHeroItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.HeroTipsSkillItem;
import com.google.common.collect.Lists;

public @Data
class GoodsHero extends GoodsBase {

	private int shadowId;
	private int shadowNum;
	private byte maxQuality ;
	private byte bornStar ;
	private byte maxStar ;
	/**
	 * 资源缩放百分比
	 * 10=100%
	 */
	private byte resRate = 10;
	/**
	 * 吞噬物品可获得的基本经验
	 */
	private int swallowExp ;
	/**
	 * 武器资源ID
	 */
	private int weaponResId ;
	/**
	 * 头像ID
	 */
	private short headId ;
	private short commonSkill ;
	private short skill1;
	private short skill2;
	private short skill3;
	private short skill4;
	
	
	private List<Short> skillIdList = Lists.newArrayList();

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseHeroItem item = new GoodsBaseHeroItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setResId((short) this.getResId());
		item.setStar(this.bornStar);
		//基本属性
		AttriBuffer curr = GameContext.getHeroApp().getBaseAttriBuffer(id, 1,
				this.getQualityType(), this.getBornStar());
		List<AttriTypeStrValueItem> attriItemList = Lists.newArrayList();
		for (AttriItem currItem : curr.getMap().values()) {
			AttriTypeStrValueItem asv = new AttriTypeStrValueItem();
			asv.setType(currItem.getAttriTypeValue());
			asv.setValue(AttributeType.formatValue(
					currItem.getAttriTypeValue(), currItem.getAttriTypeValue()));
			attriItemList.add(asv);
		}
		item.setAttriList(attriItemList);
		//技能
		List<HeroTipsSkillItem> skillList = Lists.newArrayList() ;
		for(short skillId : skillIdList){
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill){
				continue ;
			}
			HeroTipsSkillItem skillItem = new HeroTipsSkillItem();
			skillItem.setSkillId(skillId);
			skillItem.setImageId(skill.getIconId());
			skillItem.setSkillName(skill.getName());
			SkillDetail detail = skill.getSkillDetail(1);
			skillItem.setSkillDesc(detail.getDesc());
			skillList.add(skillItem);
		}
		item.setSkillList(skillList);
		item.setSwallowExp(this.getSwallowExp());
		//情缘
		item.setMount(GameContext.getHeroApp().getHeroLoveStatus(null,
				HeroLoveType.horse.getType()));
		item.setGoddess(GameContext.getHeroApp().getHeroLoveStatus(
				null, HeroLoveType.goddess.getType()));
		item.setGodWeapon(GameContext.getHeroApp().getHeroLoveStatus(
				null, HeroLoveType.godWeapon.getType()));
		return item;
	}

	@Override
	public void init(Object initData) {
		skillIdList.clear();
		this.initSkill(this.skill1);
		this.initSkill(this.skill2);
		this.initSkill(this.skill3);
		this.initSkill(this.skill4);
	}
	
	private void initSkill(short skillId){
		if(skillId <=0){
			return ;
		}
		this.skillIdList.add(skillId);
	}
}
