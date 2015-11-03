package a_serial.protobuf;

import com.baidu.bjf.remoting.protobuf.*;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;


public class GoodsRecord {

	@Protobuf(fieldType = FieldType.INT32, order = 1)
	private int goodsId;
	@Protobuf(fieldType = FieldType.INT32, order = 2)
	private int num;
	
	
	
	public GoodsRecord(int goodsId, int num) {
		super();
		this.goodsId = goodsId;
		this.num = num;
	}
	/*
	 * getters and setters
	 */
	public int getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
