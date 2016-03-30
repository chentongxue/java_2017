package sacred.alliance.magic.domain;

import java.util.List;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.util.Log4jManager;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.item.AttriTypeStrValueItem;
import com.game.draco.message.item.GoodsBaseHeroItem;
import com.game.draco.message.item.GoodsBaseItem;
import com.game.draco.message.item.SkillTipsItem;
import com.google.common.collect.Lists;

public @Data class GoodsHero extends GoodsBase {
    private static final int EQUIP_NUM = 6 ;

	private int shadowId;
	private int shadowNum;
	private int recycleShadowNum ;
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
	
	private byte seriesId ; //系id
	private byte gearId ; //档位id
	
	private int equip0 ;
	private int equip1 ;
	private int equip2 ;
	private int equip3 ;
	private int equip4 ;
	private int equip5 ;
	private short musicId ;
	private byte broadcast = 1;// 是否世界广播
	
	private List<Short> skillIdList = Lists.newArrayList();
	private List<Integer> equipIdList = Lists.newArrayList() ;
	
	
	public boolean isHeroSkill(short skillId){
		return skillId == commonSkill || skillIdList.contains(skillId) ;
	}
	
	public int getEquipId(int pos){
		if(pos <0 || pos >= equipIdList.size()){
			return 0 ;
		}
		return equipIdList.get(pos);
	}
	
	public int getEquipslotType(int goodsId){
		int index = -1 ;
		for(int i : equipIdList){
			index ++ ;
			if(i == goodsId){
				return index ;
			}
		}
		return -1 ;
	}

	@Override
	public List<AttriItem> getAttriItemList() {
		return null;
	}

	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseHeroItem item = new GoodsBaseHeroItem();
		this.setGoodsBaseItem(roleGoods, item);
		item.setResId((short) this.getResId());
		item.setStar(this.getStar());
		item.setGearId(gearId);
		item.setSeriesId(seriesId);
		item.setMaxStar(GameContext.getHeroApp().getMaxStar(this.qualityType));
		//基本属性
		AttriBuffer curr = GameContext.getHeroApp().getBaseAttriBuffer(id, 1,
				this.getQualityType(), this.getStar());
		List<AttriTypeStrValueItem> attriItemList = Lists.newArrayList();
		for (AttributeType at : GameContext.getHeroApp().getAttributeTypeList()) {
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
		//技能
		List<SkillTipsItem> skillList = Lists.newArrayList() ;
		for(short skillId : skillIdList){
			Skill skill = GameContext.getSkillApp().getSkill(skillId);
			if(null == skill){
				continue ;
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
		item.setSwallowExp(this.getSwallowExp());
		//情缘
		item.setLoveList(GameContext.getHeroApp().getHeroLoveItemList(this.id));
		return item;
	}

	@Override
	public void init(Object initData) {
		skillIdList.clear();
		this.initSkill(this.skill1);
		this.initSkill(this.skill2);
		this.initSkill(this.skill3);
		this.initSkill(this.skill4);
		this.checkSkill(this.commonSkill);
		
		this.equipIdList.clear();
		this.initEquipId(this.equip0);
		this.initEquipId(this.equip1);
		this.initEquipId(this.equip2);
		this.initEquipId(this.equip3);
		this.initEquipId(this.equip4);
		this.initEquipId(this.equip5);
        //判断装备是否有重复
        if(this.equipIdList.size() != EQUIP_NUM){
            Log4jManager.CHECK.error("GoodsHero equip config error,maybe have same equip id,heroId=" + this.getId());
            Log4jManager.checkFail();
        }
	}
	
	private void initEquipId(int equip){
        if(!this.equipIdList.contains(equip)){
            this.equipIdList.add(equip) ;
        }
		this.checkEquip(equip);
	}
	
	private void initSkill(short skillId){
		if(skillId <=0){
			return ;
		}
		this.skillIdList.add(skillId);
		this.checkSkill(skillId);
	}
	
	private void checkEquip(int equipId){
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(equipId);
		if(null == gb || !gb.isEquipment()){
			Log4jManager.CHECK.error("GoodsHero config error,equip not exist,heroId=" + this.getId() + " equipId=" + equipId);
			Log4jManager.checkFail(); 
		}
	}
	
	private void checkSkill(short skillId){
		Skill skill = GameContext.getSkillApp().getSkill(skillId) ;
		if(null != skill){
			return ;
		}
		Log4jManager.CHECK.error("GoodsHero config error,skill not exist,heroId=" + this.getId() + " skillId=" + skillId);
		Log4jManager.checkFail(); 
	}
}
