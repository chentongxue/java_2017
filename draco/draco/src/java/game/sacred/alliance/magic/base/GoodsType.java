
package sacred.alliance.magic.base;

import java.util.HashMap;
import java.util.Map;

import sacred.alliance.magic.app.goods.behavior.AbstractGoodsBehavior;
import sacred.alliance.magic.app.goods.behavior.DefaultBehavior;
import sacred.alliance.magic.app.goods.behavior.DiscardGoods;
import sacred.alliance.magic.app.goods.behavior.EquipOff;
import sacred.alliance.magic.app.goods.behavior.EquipOn;
import sacred.alliance.magic.app.goods.behavior.GoodsBehaviorType;
import sacred.alliance.magic.app.goods.behavior.GoodsSplit;
import sacred.alliance.magic.app.goods.behavior.UseGoodsBox;
import sacred.alliance.magic.app.goods.behavior.UseGoodsContain;
import sacred.alliance.magic.app.goods.behavior.UseGoodsExp;
import sacred.alliance.magic.app.goods.behavior.UseGoodsFood;
import sacred.alliance.magic.app.goods.behavior.UseGoodsGift;
import sacred.alliance.magic.app.goods.behavior.UseGoodsGoddess;
import sacred.alliance.magic.app.goods.behavior.UseGoodsHero;
import sacred.alliance.magic.app.goods.behavior.UseGoodsHorse;
import sacred.alliance.magic.app.goods.behavior.UseGoodsNostrum;
import sacred.alliance.magic.app.goods.behavior.UseGoodsRichManCard;
import sacred.alliance.magic.app.goods.behavior.UseGoodsTaskprops;
import sacred.alliance.magic.app.goods.behavior.UseGoodsTitle;
import sacred.alliance.magic.app.goods.behavior.UseGoodsTreasure;
import sacred.alliance.magic.app.goods.behavior.derive.EquipUpgrade;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsMix;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsMosaic;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsRecasting;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsRemoval;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsStrengthen;
import sacred.alliance.magic.app.goods.behavior.derive.GoodsTreasureIdentify;
import sacred.alliance.magic.app.goods.behavior.derive.StorySuitMix;
import sacred.alliance.magic.constant.TextId;

import com.game.draco.GameContext;

public enum GoodsType {
	/**
	 * 物品分类：
		1.食物(消耗品)
		2.容器表 
		3.神符
		4.英雄
		5.武器表
		6.防具饰品
		7.材料 
		8.英雄附属物 
		9.宝箱
		10 任务物品
		11 任务道具
		12 神兽外形
		13 宝石
		14 升级礼包
		15 女神
		16 时装
		17 xxx
		18 经验转轴
		19 藏宝图
		20 称号
		21 vip卡
		22 公会物品
		23 秘药
		24坐骑
		25大富翁道具卡
 
 **** 注：添加新物品类型，需添加 get(int type)方法  *****/
	
	
	GoodsDefault(-1, "", new AbstractGoodsBehavior[]{ 
			new DiscardGoods()
	}), 
	
	GoodsFood(1, TextId.GoodsType_GoodsFood_Name, new AbstractGoodsBehavior[]{
			new UseGoodsFood(),
			new DiscardGoods(),
			new GoodsSplit()
	} ), 
	
	GoodsContain(2, TextId.GoodsType_GoodsContain_Name, new AbstractGoodsBehavior[]{ 
			new UseGoodsContain(),
			new GoodsSplit(),
			new DiscardGoods(),
	}),
	
	
	/**
	 * 英雄
	 */
	GoodsHero(4,TextId.GoodsType_GoodsHero_Name, new AbstractGoodsBehavior[]{ 
			new UseGoodsHero(),
			new DiscardGoods(),
			new GoodsSplit(),
	}),
	
	
	GoodsEquHuman(5, TextId.GoodsType_GoodsEquHuman_Name, new AbstractGoodsBehavior[]{
			new EquipOn(),			//穿
			new EquipOff(),				//脱
			new GoodsStrengthen(),			//强化
			new GoodsRecasting(),           //重铸
			new GoodsMosaic(),				//镶嵌
			new GoodsRemoval(),				//摘除
			new DiscardGoods(),				//丢弃
			new GoodsSplit() ,
			new EquipUpgrade(),             //装备升级
			new StorySuitMix(),				//传说套装合成
	} ),  
	
	
	GoodsEquGoddess(6, TextId.GoodsType_GoodsEquGoddess_Name, new AbstractGoodsBehavior[]{
			new EquipOn(),			//穿
			new EquipOff(),				//脱
			new GoodsStrengthen(),			//强化
			new GoodsRecasting(),           //重铸
			new GoodsMosaic(),				//镶嵌
			new GoodsRemoval(),				//摘除
			new DiscardGoods(),				//丢弃
			new GoodsSplit(),
			new EquipUpgrade(),             //装备升级
			new StorySuitMix(),				//传说套装合成
	} ),   
	
	
	GoodsMaterial(7, TextId.GoodsType_GoodsMaterial_Name, new AbstractGoodsBehavior[]{ 
			new DiscardGoods(),
			new GoodsSplit(),
	}),
	
	/**
	 * 英雄附属物
	 */
	GoodsHeroAid(8,TextId.GoodsType_GoodsHeroAid_Name, new AbstractGoodsBehavior[]{ 
			new GoodsSplit(),
			new DiscardGoods(),
	}),
	
	GoodsBox(9, TextId.GoodsType_GoodsBox_Name, new AbstractGoodsBehavior[]{ 
			new UseGoodsBox(),
			new DiscardGoods(),
			new GoodsSplit()
	}),
	
	
	GoodsTask(10, TextId.GoodsType_GoodsTask_Name, new AbstractGoodsBehavior[]{ 
			new DiscardGoods(),
			new GoodsSplit()
	}),
	
	
	GoodsTaskprops(11, TextId.GoodsType_GoodsTaskprops_Name, new AbstractGoodsBehavior[]{ 
			new UseGoodsTaskprops(),
			new DiscardGoods(),
			new GoodsSplit()
	}), 
	
	
	GoodsGem(13, TextId.GoodsType_GoodsGem_Name, new AbstractGoodsBehavior[]{ 
			new GoodsMosaic(),				//镶嵌
			new DiscardGoods(),
			new GoodsSplit(),
			new GoodsMix(),//宝石合成
	}),

	GoodsGift(14, TextId.GoodsType_GoodsGift_Name, new AbstractGoodsBehavior[]{
 			new UseGoodsGift(), //使用礼包
	}),
	
	GoodsGoddess(15,TextId.GoodsType_GoodsGoddess_Name, new AbstractGoodsBehavior[]{ 
			new UseGoodsGoddess(),
			new DiscardGoods(),
	}),
	
	GoodsFashion(16, TextId.GoodsType_GoodsFaction_Name, new AbstractGoodsBehavior[]{ 
			/*new WearFashion(),			//穿
			new DoffFashion(),				//脱
			new DiscardGoods(),
			new FashionRenewals(), //续费
			new GoodsSplit()*/
			
			new EquipOn(),			//穿
			new EquipOff(),				//脱
			new GoodsStrengthen(),			//强化
			new GoodsRecasting(),           //重铸
			new GoodsMosaic(),				//镶嵌
			new GoodsRemoval(),				//摘除
			new DiscardGoods(),				//丢弃
			new GoodsSplit(),
			new EquipUpgrade(),             //装备升级
	}), 
	
	GoodsExp(18, TextId.GoodsType_GoodsExp_Name, new AbstractGoodsBehavior[]{
			new UseGoodsExp(),
			new DiscardGoods(),
			new GoodsSplit(),
			}),
			
   GoodsTreasure(19, TextId.GoodsType_GoodsTreasure_Name, new AbstractGoodsBehavior[]{
		   			new UseGoodsTreasure(),
					new DiscardGoods(),
					new GoodsSplit(),
					new GoodsTreasureIdentify(),
				}),
				
	GoodsTitle(20, TextId.GoodsType_GoodsTitle_Name, new AbstractGoodsBehavior[]{
			new UseGoodsTitle(),
			new DiscardGoods(),
		}),
	
		
//	GoodsFaction(22, TextId.GoodsType_GoodsFaction_Name, new AbstractGoodsBehavior[]{
//				new UseGoodsFaction(),
//				new GoodsSplit(),
//				new DiscardGoods(),
//			}),
	
	GoodsNostrum(23, TextId.GoodsType_GoodsNostrum_Name, new AbstractGoodsBehavior[]{
			new UseGoodsNostrum(),
			new DiscardGoods(),
	}),
	
	GoodsHorse(24,TextId.GoodsType_GoodsHorse_Name, new AbstractGoodsBehavior[]{ 
			new UseGoodsHorse(),
			new DiscardGoods(),
	}),
	
	GoodsRichManCard(25,TextId.GoodsType_GoodsRichManCard_Name, new AbstractGoodsBehavior[]{ 
			new UseGoodsRichManCard(),
			new DiscardGoods(),
	}),
					
	;
	
	
	
	
	private final int type;
	private final String name;
	private Map<GoodsBehaviorType, AbstractGoodsBehavior> behaviorMap ;
	
	
	
	GoodsType(int type, String name, AbstractGoodsBehavior[] goodsBehaviors){
		this.type = type;
		this.name = name;
		behaviorMap = new HashMap<GoodsBehaviorType,AbstractGoodsBehavior>();
		for(AbstractGoodsBehavior goodsBehavior : goodsBehaviors){
			behaviorMap.put(goodsBehavior.getBehaviorType(), goodsBehavior);
		}
	}
	
	public int getType(){
		return type;
	}
	
	public String getName() {
		return GameContext.getI18n().getText(this.name);
	}

	public AbstractGoodsBehavior getGoodsBehavior(GoodsBehaviorType behaviorType){
		
		AbstractGoodsBehavior goodsBehavior = behaviorMap.get(behaviorType);
		if(goodsBehavior == null){
			return new DefaultBehavior();
		}
		
		return goodsBehavior;
	}
	
	public static GoodsType get(int type){
		
		for(GoodsType goodsType : GoodsType.values()){
			if(type == goodsType.getType()){
				return goodsType;
			}
		}
		
		return GoodsDefault;
	}
}
