package sacred.alliance.magic.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.game.draco.message.item.GoodsBaseFoodItem;
import com.game.draco.message.item.GoodsBaseItem;

import lombok.Data;
import sacred.alliance.magic.app.attri.AttriItem;
import sacred.alliance.magic.util.Util;

/**
 * 食物物品
 * 
 * @author Ljm
 * 
 */
public @Data class GoodsFood extends GoodsApply {
	private static final String CAT = "," ;
	private short triggerBuffId;// 产生BUFFid
	private int triggerBuffLv;// 产生BUFF等级
	private String cleanBuffIds ; //清除的buffId
	private Set<Short> cleanBuffSet = null ;

	
	@Override
	public GoodsBaseItem getGoodsBaseInfo(RoleGoods roleGoods) {
		GoodsBaseFoodItem goodsApplyItem = new GoodsBaseFoodItem();
		this.setGoodsBaseItem(roleGoods, goodsApplyItem);
		goodsApplyItem.setApplyIntervalTime(this.getIntervalTime());
		goodsApplyItem.setCoolingTimeType((byte)this.getIntervalId());
		goodsApplyItem.setSecondType(((byte)secondType));
		goodsApplyItem.setDesc(desc);
		goodsApplyItem.setLvLimit((byte)lvLimit);
		return goodsApplyItem;
	}
	

	@Override
	public List<AttriItem> getAttriItemList() {
		return null ;
	}

	@Override
	public void init(Object initData) {
		if(Util.isEmpty(cleanBuffIds)){
			return ;
		}
		String[] arrs = Util.splitString(cleanBuffIds,CAT);
		if(null == arrs || 0 == arrs.length){
			return ;
		}
		this.cleanBuffSet = new HashSet<Short>();
		for(String s : arrs){
			if(!Util.isNumeric(s)){
				continue ;
			}
			short id = Short.parseShort(s);
			this.cleanBuffSet.add(id);
		}
	}

	

	
}
