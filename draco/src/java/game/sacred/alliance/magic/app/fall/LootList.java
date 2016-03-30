package sacred.alliance.magic.app.fall;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.util.ProbabilityMachine;
import sacred.alliance.magic.util.Util;

public @Data class LootList {
	private static int PERCENT = 10000;
	private int lootId;
	private int group1;
	private int groupProb1;
	private int group2;
	private int groupProb2;
	private int group3;
	private int groupProb3;
	private int group4;
	private int groupProb4;
	private int group5;
	private int groupProb5;
	private int group6;
	private int groupProb6;
	private int group7;
	private int groupProb7;
	private int group8;
	private int groupProb8;
	private int group9;
	private int groupProb9;
	private int group10;
	private int groupProb10;
	private int group11;
	private int groupProb11;
	private int group12;
	private int groupProb12;
	private int group13;
	private int groupProb13;
	private int group14;
	private int groupProb14;
	private int group15;
	private int groupProb15;
	private List<Item> itemList = new ArrayList<Item>();
	private int lootType;
	
	public class Item {
		private LootGroup lootGroup ;
		private int groupProb ;
		public Item(LootGroup lootGroup,int groupProb){
			this.lootGroup = lootGroup ;
			this.groupProb = groupProb ;
		}
		public LootGroup getLootGroup() {
			return lootGroup;
		}
		public int getGroupProb() {
			return groupProb;
		}
		
	}
	/**
	 * 封装到itemList
	 */
	public void init(Map<String, LootGroup> lootGroupMap){
		this.addItem(group1,groupProb1,lootGroupMap);
		this.addItem(group2,groupProb2,lootGroupMap);
		this.addItem(group3,groupProb3,lootGroupMap);
		this.addItem(group4,groupProb4,lootGroupMap);
		this.addItem(group5,groupProb5,lootGroupMap);
		this.addItem(group6,groupProb6,lootGroupMap);
		this.addItem(group7,groupProb7,lootGroupMap);
		this.addItem(group8,groupProb8,lootGroupMap);
		this.addItem(group9,groupProb9,lootGroupMap);
		this.addItem(group10,groupProb10,lootGroupMap);
		this.addItem(group11,groupProb12,lootGroupMap);
		this.addItem(group12,groupProb13,lootGroupMap);
		this.addItem(group13,groupProb13,lootGroupMap);
		this.addItem(group14,groupProb14,lootGroupMap);
		this.addItem(group15,groupProb15,lootGroupMap);
	}
	private void addItem(int group,int groupProb,Map<String, LootGroup> lootGroupMap){
		if(group <= 0 || groupProb <= 0){
			return ;
		}
		LootGroup lg = lootGroupMap.get(String.valueOf(group));
		this.itemList.add(new Item(lg,groupProb));
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(getClass().getSimpleName());
		buffer.append("[");
		buffer.append("lootId=").append(this.lootId).append(" ");
		buffer.append("group1=").append(this.group1).append(" ");
		buffer.append("groupProb1=").append(this.groupProb1).append(" ");
		buffer.append("group2=").append(this.group2).append(" ");
		buffer.append("groupProb2=").append(this.groupProb2).append(" ");
		buffer.append("group3=").append(this.group3).append(" ");
		buffer.append("groupProb3=").append(this.groupProb3).append(" ");
		buffer.append("group4=").append(this.group4).append(" ");
		buffer.append("groupProb4=").append(this.groupProb4).append(" ");
		buffer.append("group5=").append(this.group5).append(" ");
		buffer.append("groupProb5=").append(this.groupProb5).append(" ");
		buffer.append("group6=").append(this.group6).append(" ");
		buffer.append("groupProb6=").append(this.groupProb6).append(" ");
		buffer.append("group7=").append(this.group7).append(" ");
		buffer.append("groupProb7=").append(this.groupProb7).append(" ");
		buffer.append("group8=").append(this.group8).append(" ");
		buffer.append("groupProb8=").append(this.groupProb8).append(" ");
		buffer.append("group9=").append(this.group9).append(" ");
		buffer.append("groupProb9=").append(this.groupProb9).append(" ");
		buffer.append("group10=").append(this.group10).append(" ");
		buffer.append("groupProb10=").append(this.groupProb10).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
	/**
	 * 得到满足掉落组的ID(Set)
	 * @param randProb
	 * @return
	 */
	/*public Set<Integer> getGroupId(){
		Set<Integer> groupIdSet = new HashSet<Integer>();
		for(Item i: itemList){
			if(Util.randomInRange(1, PERCENT) <= i.groupProb){
				groupIdSet.add(i.getGroup());
			}
		}
		return groupIdSet;
	}*/
	
	public List<GoodsOperateBean> getGoodsBean(){
		List<GoodsOperateBean> values = new ArrayList<GoodsOperateBean>();
		for(Item i: itemList){
			if(ProbabilityMachine.getRandomNum(1, PERCENT) > i.groupProb){
				continue ;
			}
			LootGroup lootGruop = i.getLootGroup();
			if(null == lootGruop){
				continue ;
			}
			GoodsOperateBean goodsItem = lootGruop.getItem();
			if(null == goodsItem || goodsItem.getGoodsNum()<=0){
				continue ;
			}
			GoodsOperateBean agb = GoodsOperateBean.createAddGoodsBean(goodsItem.getGoodsId(),
					goodsItem.getGoodsNum(), goodsItem.getBindType().getType());
			values.add(agb);
		}
		return values;
	}
	
	public Map<Integer,Integer> getGoods(){
		Map<Integer,Integer> values = new HashMap<Integer,Integer>();
		for(Item i: itemList){
			if(ProbabilityMachine.getRandomNum(1, PERCENT) > i.groupProb){
				continue ;
			}
			LootGroup lootGruop  = i.getLootGroup();
			if(null == lootGruop){
				continue ;
			}
			GoodsOperateBean goodsItem = lootGruop.getItem();
			if(null == goodsItem || goodsItem.getGoodsNum()<=0){
				continue ;
			}
			int key = goodsItem.getGoodsId() ;
			int oriValue = 0 ;
			if(values.containsKey(key)){
				oriValue = values.get(key);
			}
			values.put(key, oriValue+goodsItem.getGoodsNum());
		}
		return values;
	}
	
	
	/*private @Data class Item{
		private int group;
		private int groupProb;
		Item(int group,int groupProb){
			this.group = group;
			this.groupProb = groupProb;
		}
	}*/
	public static void main(String[] args) {
		int temp = Util.randomInRange(1, 10);
		System.out.println("temp=" + temp);
	}
}


