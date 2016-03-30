package sacred.alliance.magic.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.base.BindingType;
import sacred.alliance.magic.util.ProbabilityMachine;


public @Data class CollectPoint {
	private String id ;
	private String name ;
	private int type ;
	private int imageId ;
	private int refreshInteval ;
	private boolean unique ;
	private int masterId ;
	private String desc ; 

	 private int awardsId1;
	 private int num1;
	 private int prob1;
	 private int awardsId2;
	 private int num2;
	 private int prob2;
	 private int awardsId3;
	 private int num3;
	 private int prob3;
	 
	 
	 public boolean isFallable(){
		 return this.isFallable(awardsId1, num1, prob1) ||
		 this.isFallable(awardsId2, num2, prob2) ||
		 this.isFallable(awardsId3, num3, prob3) ;
	 }
	 
	 private boolean isFallable(int goodsId,int num,int prob1){
		 return (goodsId > 0 && num > 0) ;
	 }

	 
	 public List<GoodsOperateBean> getFall() {
		if (!this.isFallable()) {
			return null;
		}
		List<GoodsOperateBean> list = new ArrayList<GoodsOperateBean>();
		if (awardsId1 > 0
				&& num1 > 0
				&& ProbabilityMachine.isProbability(prob1
						* ProbabilityMachine.RATE_MODULUS_HUNDRED_MULTI)) {
			list.add(GoodsOperateBean.createAddGoodsBean(awardsId1, num1,
					BindingType.template.getType()));
		}
		if (awardsId2 > 0
				&& num2 > 0
				&& ProbabilityMachine.isProbability(prob2
						* ProbabilityMachine.RATE_MODULUS_HUNDRED_MULTI)) {
			list.add(GoodsOperateBean.createAddGoodsBean(awardsId2, num2,
					BindingType.template.getType()));
		}
		if (awardsId3 > 0
				&& num3 > 0
				&& ProbabilityMachine.isProbability(prob3
						* ProbabilityMachine.RATE_MODULUS_HUNDRED_MULTI)) {
			list.add(GoodsOperateBean.createAddGoodsBean(awardsId3, num3,
					BindingType.template.getType()));
		}

		return list;
	}
	 
}
