package sacred.alliance.magic.app.fall;
import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.ProbabilityMachine;

public @Data class LootGroup {
	private final static int DEF_BIND_TYPE = -1;
	protected int groupId;
	protected int itemId1;
	protected int num1;
	protected int weight1;
	protected int bindType1 = DEF_BIND_TYPE;
	protected int itemId2;
	protected int num2;
	protected int weight2;
	protected int bindType2 = DEF_BIND_TYPE;
	protected int itemId3;
	protected int num3;
	protected int weight3;
	protected int bindType3 = DEF_BIND_TYPE;
	protected int itemId4;
	protected int num4;
	protected int weight4;
	protected int bindType4 = DEF_BIND_TYPE;
	protected int itemId5;
	protected int num5;
	protected int weight5;
	protected int bindType5 = DEF_BIND_TYPE;
	protected int itemId6;
	protected int num6;
	protected int weight6;
	protected int bindType6 = DEF_BIND_TYPE;
	protected int itemId7;
	protected int num7;
	protected int weight7;
	protected int bindType7 = DEF_BIND_TYPE;
	protected int itemId8;
	protected int num8;
	protected int weight8;
	protected int bindType8 = DEF_BIND_TYPE;
	protected int itemId9;
	protected int num9;
	protected int weight9;
	protected int bindType9 = DEF_BIND_TYPE;
	private int itemId10;
	private int num10;
	private int weight10;		
	protected int bindType10 = DEF_BIND_TYPE;
	private int itemId11;
	private int num11;
	private int weight11;		
	protected int bindType11 = DEF_BIND_TYPE;
	private int itemId12;
	private int num12;
	private int weight12;		
	protected int bindType12 = DEF_BIND_TYPE;
	
	private int itemId13;
	private int num13;
	private int weight13;		
	protected int bindType13 = DEF_BIND_TYPE;
	private int itemId14;
	private int num14;
	private int weight14;		
	protected int bindType14 = DEF_BIND_TYPE;
	private int itemId15;
	private int num15;
	private int weight15;		
	protected int bindType15 = DEF_BIND_TYPE;
	private int itemId16;
	private int num16;
	private int weight16;		
	protected int bindType16 = DEF_BIND_TYPE;
	private int itemId17;
	private int num17;
	private int weight17;		
	protected int bindType17 = DEF_BIND_TYPE;
	private int itemId18;
	private int num18;
	private int weight18;		
	protected int bindType18 = DEF_BIND_TYPE;
	
	protected int weightSum = 0 ;
	protected List<ItemData> groupList = new ArrayList<ItemData>();
	
	
	
	public GoodsOperateBean getItem() {
		if(this.weightSum <=0){
			return null ;
		}
		int random = ProbabilityMachine.getRandomNum(1, weightSum);
		int count = 0;
		for(ItemData data:groupList){
			int key = data.itemId;
			int dataWeight = data.weight;
			if((count< random) && (random<= (count+ dataWeight))){
				if(0 != key){
					return GoodsOperateBean.createAddGoodsBean(data.itemId, data.num, data.bindType);
				}
				break;
			}
			count += dataWeight;
		}
		return null ;
	}
	
	
	
	public void init(){
		this.addItemData(itemId1,num1,weight1,bindType1);
		this.addItemData(itemId2,num2,weight2,bindType2);
		this.addItemData(itemId3,num3,weight3,bindType3);
		this.addItemData(itemId4,num4,weight4,bindType4);
		this.addItemData(itemId5,num5,weight5,bindType5);
		this.addItemData(itemId6,num6,weight6,bindType6);
		this.addItemData(itemId7,num7,weight7,bindType7);
		this.addItemData(itemId8,num8,weight8,bindType8);
		this.addItemData(itemId9,num9,weight9,bindType9);
		this.addItemData(itemId10, num10, weight10,bindType10);
		this.addItemData(itemId11, num11, weight11,bindType11);
		this.addItemData(itemId12, num12, weight12,bindType12);
		this.addItemData(itemId13, num13, weight13,bindType13);
		this.addItemData(itemId14, num14, weight14,bindType14);
		this.addItemData(itemId15, num15, weight15,bindType15);
		this.addItemData(itemId16, num16, weight16,bindType16);
		this.addItemData(itemId17, num17, weight17,bindType17);
		this.addItemData(itemId18, num18, weight18,bindType18);
		this.weightSum = this.getSum();
	}
	
	private void addItemData(int itemId, int num, int weight,int bindType) {
		if(weight <= 0 || itemId <= 0 || num <= 0){
			return ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(itemId);
		if(null == gb){
			Log4jManager.CHECK.error("LootGroup config error,goods not exist,groupId=" 
					+ this.groupId + " goodsId=" + itemId);
			Log4jManager.checkFail() ;
		}
		BindingType bt = this.getBindingType(itemId, bindType);
		this.groupList.add(new ItemData(itemId,num,weight,bt.getType()));
	}

	private BindingType getBindingType(int goodsId,int bindType){
		if(bindType == DEF_BIND_TYPE){
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(goodsId);
			return gb.getBindingType();
		}
		return BindingType.get(bindType);
	}
	protected int getSum(){
		int num = 0;
		for(ItemData i : groupList){
			num += i.getWeight();
		}
		return num;
	}
	
	
	protected @Data class ItemData{
		private int itemId;
		private int num;
		private int weight;
		private int bindType;
		
		public ItemData(int itemId,int num,int weight,int bindType){
			this.itemId = itemId;
			this.num = num;
			this.weight = weight;
			this.bindType = bindType;
		}
	}
	
}
