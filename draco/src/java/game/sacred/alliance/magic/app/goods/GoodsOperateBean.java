package sacred.alliance.magic.app.goods;

import lombok.Data;
import sacred.alliance.magic.base.BindingType;

public @Data class GoodsOperateBean implements  Cloneable{
	
	private int goodsId;
	private int goodsNum;
	private BindingType bindType = BindingType.template;
	
	public GoodsOperateBean(){
		
	}
	
	public static GoodsOperateBean createAddGoodsBean(int goodsId, int goodsNum, int bind){
		return new GoodsOperateBean(goodsId, goodsNum, bind); 
	}
	
	public GoodsOperateBean(int goodsId, int goodsNum){
		this(goodsId, goodsNum, BindingType.template);
	}
	
	public GoodsOperateBean(int goodsId, int goodsNum, int bind){
		this.goodsId = goodsId;
		this.goodsNum = goodsNum;
		BindingType bindingType = BindingType.get(bind);
		if(null != bindingType){
			this.bindType = bindingType;
		}
	}
	
	public GoodsOperateBean(int goodsId, int goodsNum, BindingType bindType){
		this.goodsId = goodsId;
		this.goodsNum = goodsNum;
		this.bindType = bindType;
	} 
	
	public void setBindType(BindingType bindType){
		if(bindType == null){
			return ;
		}
		this.bindType = bindType;
	}

    @Override
    public GoodsOperateBean clone(){
        return new GoodsOperateBean(this.goodsId,this.goodsNum,this.bindType) ;
    }
}
