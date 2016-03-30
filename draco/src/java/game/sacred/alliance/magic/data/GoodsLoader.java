package sacred.alliance.magic.data;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsBaseInitCallback;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.domain.GoodsBox;
import sacred.alliance.magic.domain.GoodsContain;
import sacred.alliance.magic.domain.GoodsEquipment;
import sacred.alliance.magic.domain.GoodsExp;
import sacred.alliance.magic.domain.GoodsFood;
import sacred.alliance.magic.domain.GoodsHero;
import sacred.alliance.magic.domain.GoodsHeroAid;
import sacred.alliance.magic.domain.GoodsHorse;
import sacred.alliance.magic.domain.GoodsMaterial;
import sacred.alliance.magic.domain.GoodsNostrum;
import sacred.alliance.magic.domain.GoodsRune;
import sacred.alliance.magic.domain.GoodsTask;
import sacred.alliance.magic.domain.GoodsTaskprops;
import sacred.alliance.magic.domain.GoodsTitle;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.XlsPojoUtil;

import com.game.draco.app.pet.domain.GoodsPet;
import com.game.draco.app.pet.domain.GoodsPetAid;
import com.game.draco.app.rune.RuneInitCallback;
import com.game.draco.app.title.TitleInitCallback;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GoodsLoader extends DefaultDataLoader<String, GoodsBase> {
	private @Data class LoadMapping{
		private String xlsName ;
		private String sheetName ;
		private Class<? extends GoodsBase> clazz ;
		private Object initData ;
		private GoodsType goodsType;
		private GoodsBaseInitCallback initCallback ;
		
		public LoadMapping(String xlsName,String sheetName,
				Class<? extends GoodsBase> clazz,Object initData, 
				GoodsType type,GoodsBaseInitCallback initCallback){
			this.xlsName = xlsName ;
			this.sheetName = sheetName ;
			this.clazz = clazz ;
			this.initData = initData ;
			this.goodsType = type;
			this.initCallback = initCallback ;
		}
		
		public LoadMapping(String xlsName,String sheetName,
				Class<? extends GoodsBase> clazz,Object initData, 
				GoodsType type){
			this(xlsName,sheetName,clazz,initData,type,null);
		}
	}
	
	private List<LoadMapping> getMappings(){
		List<LoadMapping> mappings = Lists.newArrayList();
		//装备列表
		mappings.add(new LoadMapping("goods_equipment.xls","equip_list", GoodsEquipment.class, null, null));
		//容器
		mappings.add(new LoadMapping("goods_contain.xls","goods_contain",GoodsContain.class,null, GoodsType.GoodsContain));
		//任务物品
		mappings.add(new LoadMapping("goods_task.xls","goods_task",GoodsTask.class,null,GoodsType.GoodsTask));
		//任务道具
		mappings.add(new LoadMapping("goods_taskprops.xls","goods_taskprops",GoodsTaskprops.class,null,GoodsType.GoodsTaskprops));
		//符文
		mappings.add(new LoadMapping("goods_rune.xls","goods_rune",GoodsRune.class,null,GoodsType.GoodsRune,new RuneInitCallback()));
		//材料
		mappings.add(new LoadMapping("goods_material.xls","goods_material",GoodsMaterial.class,null,GoodsType.GoodsMaterial));
		//烹饪食物
		mappings.add(new LoadMapping("goods_food.xls","goods_food",GoodsFood.class,null,GoodsType.GoodsFood));
		//经验转轴
		mappings.add(new LoadMapping("goods_exp.xls","goods_exp",GoodsExp.class,null,GoodsType.GoodsExp));
		//藏宝图
		mappings.add(new LoadMapping("goods_treasure.xls","treasure",GoodsTreasure.class,null,GoodsType.GoodsTreasure));
		//称号
		mappings.add(new LoadMapping("goods_title.xls","title",GoodsTitle.class,null,GoodsType.GoodsTitle,new TitleInitCallback()));
		//公会
//		mappings.add(new LoadMapping("goods_faction.xls","faction",GoodsFaction.class,null,GoodsType.GoodsFaction));
		
		//英雄
		mappings.add(new LoadMapping("goods_hero.xls","list",GoodsHero.class,null,GoodsType.GoodsHero));
		//英雄附属
		mappings.add(new LoadMapping("goods_hero_aid.xls","list",GoodsHeroAid.class,null,GoodsType.GoodsHeroAid));
		// 宠物
		mappings.add(new LoadMapping("goods_pet.xls","list",GoodsPet.class,null,GoodsType.GoodsPet));
		// 宠物附属
		mappings.add(new LoadMapping("goods_pet_aid.xls","list",GoodsPetAid.class,null,GoodsType.GoodsPetAid));
		//秘药物品
		mappings.add(new LoadMapping("goods_nostrum.xls", "goods_nostrum", GoodsNostrum.class, null, GoodsType.GoodsNostrum));
		//坐骑
		mappings.add(new LoadMapping("goods_horse.xls","list",GoodsHorse.class,null,GoodsType.GoodsHorse));
		//!!!!
		//宝箱(宝箱最后加载,因为宝箱会包含其他类型物品)
		mappings.add(new LoadMapping("goods_box.xls","goods_box",GoodsBox.class,null,GoodsType.GoodsBox));
		return mappings ;
	}
	
	@Override
	public Map<String, GoodsBase> loadData() {
		Map<String, GoodsBase> values = Maps.newHashMap();
		List<LoadMapping> mappings = this.getMappings();
		String path = this.getXlsPath() ;
		for(LoadMapping lm : mappings){
			//用list,而不用map的putAll是因为要验证模板ID是否唯一
			List<? extends GoodsBase> list = XlsPojoUtil.sheetToList(path + lm.getXlsName(), lm.getSheetName(), lm.getClazz());
			if(null == list){
				continue ;
			}
			for(GoodsBase gb:list){
				if(gb.getId()<=0){
					continue ;
				}
				String goodsId = String.valueOf(gb.getId());
				GoodsBase existGoodsBase = values.get(goodsId);
				if(null != existGoodsBase){
					//提示模板id重复
					Log4jManager.CHECK.error("too many same key,goodsId=" + gb.getId() 
							+ " exist class=" + existGoodsBase.getClass().getSimpleName()
							+ " exist name=" + existGoodsBase.getName() 
							+ " class=" + gb.getClass().getSimpleName()
							+ " name=" + gb.getName() );
					Log4jManager.checkFail();
					continue ;
				}
//				gb.initDecomposeDesc();
				gb.setInitData(lm.getInitData());
				//强制初始化物品类型（除防具及武器的物品配置表goods_equipment.xls外，限制每个excel文件只能配置一种类型的物品，如不同，会被强制更改）
				if(null != lm.goodsType){
					gb.setGoodsType(lm.goodsType.getType());
				}
				values.put(goodsId, gb);
			}
			try {
				GoodsBaseInitCallback initCallback = lm.getInitCallback();
				if (null != initCallback) {
					initCallback.callback(list);
				}
			}catch(Exception ex){
				Log4jManager.CHECK.error("",ex);
				Log4jManager.checkFail();
			}
		}
		return values;
	}
	
}
