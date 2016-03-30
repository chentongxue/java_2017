package sacred.alliance.magic.app.goods;

import lombok.Data;
import sacred.alliance.magic.base.BindingType;

/**
 * 封装装备镶嵌上的宝石信息
 * @author wangkun
 *
 */
public @Data class MosaicHole {
	private int goodsId; //所镶嵌的宝石模板ID
	private BindingType bindType = BindingType.already_binding;//镶嵌时宝石的绑定状态
	
	public MosaicHole(){
		
	}
	
	public MosaicHole(int goodsId, BindingType bindType){
		this.goodsId = goodsId;
		this.bindType = bindType;
	}
}
