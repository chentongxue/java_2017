package sacred.alliance.magic.domain;

import sacred.alliance.magic.util.KeySupport;
import lombok.Data;


public @Data class MixFormula implements KeySupport<Integer>{
	private int srcId ;
	private short srcNum ;
	private int targetId ;
	private int fee ;
	private String broadcast;
	
	//下面非xls配置字段
	private GoodsBase targetGoods ;

	@Override
	public Integer getKey() {
		return this.targetId;
	}
}
