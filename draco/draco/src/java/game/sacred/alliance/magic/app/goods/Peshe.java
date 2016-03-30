package sacred.alliance.magic.app.goods;

import lombok.Data;

public @Data class Peshe {
	public Peshe(){
		
	}
	public Peshe(int goodsId,int num,int bind,int gon){
		this.goodsId = goodsId;
		this.num = num;
		this.gon = gon;
		this.bind = bind ;
	}
	
	private int num;
	private int gon;
	private int goodsId;
	private int bind ;
	
}
