package sacred.alliance.magic.vo;

import com.game.draco.app.quest.QuestAward;

import lombok.Data;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.BindingType;

public @Data class QuestAwardDetail {
	
	private int id;//任务ID
	private int mustGoods1;//固定奖励物品1
	private int mustNo1;//固定奖励1数量
	private int mustBind1 = BindingType.template.getType();//固定奖励1绑定类型
	private int mustGoods2;
	private int mustNo2;
	private int mustBind2 = BindingType.template.getType();
	private int mustGoods3;
	private int mustNo3;
	private int mustBind3 = BindingType.template.getType();
	private int mustGoods4;
	private int mustNo4;
	private int mustBind4 = BindingType.template.getType();//固定奖励1绑定类型
	private int mustGoods5;
	private int mustNo5;
	private int mustBind5 = BindingType.template.getType();
	private int mustGoods6;
	private int mustNo6;
	private int mustBind6 = BindingType.template.getType();
	private int zp;//真气
	private int gold;//绑金
	private int silver;//银币
	private int exp;//经验
	private int heroExp ; //英雄经验
	private int honor;//荣誉
	private int factionIntegral;//公会积分
	private int factionContribute;//成员贡献度
	
	public QuestAward getQuestAward(){
		QuestAward questAward = new QuestAward();
		//添加物品奖励
		questAward.addToGoodsList(this.mustGoods1, this.mustNo1, this.mustBind1);
		questAward.addToGoodsList(this.mustGoods2, this.mustNo2, this.mustBind2);
		questAward.addToGoodsList(this.mustGoods3, this.mustNo3, this.mustBind3);
		questAward.addToGoodsList(this.mustGoods4, this.mustNo4, this.mustBind4);
		questAward.addToGoodsList(this.mustGoods5, this.mustNo5, this.mustBind5);
		questAward.addToGoodsList(this.mustGoods6, this.mustNo6, this.mustBind6);
		//添加属性奖励
		questAward.addToAttributeMap(AttributeType.potential, this.zp);
		questAward.addToAttributeMap(AttributeType.gameMoney, this.silver);
		questAward.addToAttributeMap(AttributeType.exp, this.exp);
		questAward.addToAttributeMap(AttributeType.honor, this.honor);
		questAward.addToAttributeMap(AttributeType.factionIntegral, this.factionIntegral);
		questAward.addToAttributeMap(AttributeType.heroExp, this.heroExp);
//		questAward.addToAttributeMap(AttributeType.contribute, this.factionContribute);
		return questAward;
	}
	
}
