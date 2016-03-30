package com.game.draco.app.skill.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.python.google.common.collect.Lists;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AttributeOperateBean;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.domain.SAttrC;
import com.game.draco.app.skill.domain.SBuffC;
import com.game.draco.app.skill.domain.SHurt;
import com.game.draco.app.skill.vo.SkillFormula;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.google.common.collect.Maps;

public @Data class SkillDetail {
	
	private short skillId;//技能ID 
	private String name;//技能名字 
	private int level;//等级 
	private byte sourceType;//系统来源
	private short iconId;//图标ID 
	private short effectId;//效果ID 
	private byte actionId;//动作ID 
	private short musicId;//声音ID
	private byte shockRate ; //震动几率
	private boolean buffUse;//是否为BUFF触发技能[0否|1是]
	private byte skillApplyType;//技能类型[0主|1被]
	private byte skillEffectType;//技能效果类型 [0物理|1法术|2其他辅助]
	private int hp;//消耗HP 
	private int mp;//消耗MP 
	private int cd;//CD(单位毫秒) 
	private int minUseRange;//最小释放距离 
	private int maxUseRange;//最大释放距离 
	private byte serverTargetType;//目标类型[0任意|1敌方|2己方] 
	private byte clientTargetType;//客户端目标类型 [0任意 1敌方 2友方 3自己]
	private boolean triggerPassive;//是否触发被动技能 [0否 1是] 
	private boolean useGlobalCd;//是否公用公共CD[0否 1是] 
	private int hitChange;//命中修正 
	private int critChange;//暴击修正 
	private int skyLandATKChange;//乾坤一击修正
	private byte attackType;//攻击方式 [0默认 | 1普通攻击 | 2冲锋 | 3闪现] 
	private short prepareArg;//预留参数 
	private int hatredPercent;//仇恨百分比 
	private int hatredAdd;//额外附加仇恨 
	private String desc;//描述
	private String affectSkills;//被动技能影响主动技能的id表(CD、耗蓝、耗HP、攻击距离、预留参数；用,分割；影响全部填-1)
	private short targetAnimId; //被攻击者受伤动画id
	private short targetEffectId; //被攻击者受伤特效id
	private int battleScore; //技能战斗力
	private Map<Integer,List<SBuffC>> skillBuffMap = Maps.newHashMap();
	private List<SkillScope> skillScopeList = Lists.newArrayList();
	private Map<Integer,SHurt> sHurtMap = Maps.newHashMap();
	private Map<Integer,List<SAttrC>> skillAttriMap = Maps.newHashMap();
	private boolean fixedXy;//是否固定坐标 0=否 1=固定
	private boolean guideSkill;
	//选取规则是客户端还是服务器端
	private boolean selectionRules;
	//是否蒙黑
	private boolean blackGround;
	//是否需要持续特效
	private boolean continueEffectId;
	
	//************* 技能学习条件 *************
//	private int roleLevel;//角色等级
//	private int innerLevel;//来源系统的内部等级
//	private int consumeGoodsId;//消耗物品ID
//	private short consumeGoodsNum;//消耗物品数量
	private List<AttributeOperateBean> consumeAttributeList = new ArrayList<AttributeOperateBean>();//消耗属性列表
//	private AttributeType relyAttrType;//依赖属性类型
//	private int relyAttrValue;//依赖属性值
//	private short relySkillId;//技能依赖
//	private byte relySkillLevel;//依赖技能级别
	//************* 技能学习条件 *************
	
	
	public int getRealCd(AbstractRole role){
		if(this.cd <=0){
			return 0 ;
		}
		boolean normalAttack = this.attackType == SkillAttackType.NormalAttack.getType();
		int cdRate = normalAttack ? role.get(AttributeType.normalAtkCdRate) : role.get(AttributeType.cdRate);
		return (int) (this.cd * ((float) cdRate / SkillFormula.TEN_THOUSAND));
	}
	
	public int getHatredValue(int hurt){
		return hatredAdd + (int)(hurt*hatredPercent/(float)100);
	}
	
//	public GoodsLiteNamedItem getConsumeGoodsLiteNamedItem(){
//		if(this.consumeGoodsId <= 0){
//			return null;
//		}
//		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(this.consumeGoodsId);
//		if(null == gb){
//			return null;
//		}
//		GoodsLiteNamedItem item = gb.getGoodsLiteNamedItem();
//		item.setNum(this.consumeGoodsNum);
//		return item;
//	}
	
	public List<AttriTypeValueItem> getRefreshAttrTypeValueList(RoleInstance role){
		List<AttriTypeValueItem> list = new ArrayList<AttriTypeValueItem>();
		for(AttributeOperateBean bean : this.consumeAttributeList){
			if(null == bean){
				continue;
			}
			AttributeType attrType = bean.getAttrType();
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(attrType.getType());
			item.setAttriValue(role.get(attrType));
			list.add(item);
		}
		return list;
	}
	
	public List<AttriTypeValueItem> getConsumeAttrTypeValueList(){
		List<AttriTypeValueItem> list = new ArrayList<AttriTypeValueItem>();
		for(AttributeOperateBean bean : this.consumeAttributeList){
			if(null == bean){
				continue;
			}
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(bean.getAttrType().getType());
			item.setAttriValue(bean.getValue());
			list.add(item);
		}
		return list;
	}
	
}