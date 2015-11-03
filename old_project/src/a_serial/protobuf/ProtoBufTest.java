package a_serial.protobuf;

import java.io.IOException;
import java.util.ArrayList;

import a_serial.ByteUtil;

import com.google.common.collect.Lists;

public class ProtoBufTest {

	public static void main0(String[] args) {
		Shop shop = new Shop();
		shop.setShopId("10010");
		GoodsRecord r = new GoodsRecord(5, 22);
		GoodsRecord r2 = new GoodsRecord(5, 22);
		ArrayList<GoodsRecord> goodsRecordList = Lists.newArrayList(r, r2);
		shop.setGoodsRecordList(goodsRecordList);
		
		shop.preToData();
		byte[] data = shop.getData();
		try {
			ByteUtil.writeFile(data, "protoBuf_" +DateUtil.INSTANCE.getTodayStamp()+ ".os");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		try {
			byte[] data = 	ByteUtil.getData( "protoBuf_" +DateUtil.INSTANCE.getTodayStamp()+ ".os");
			Shop s = new Shop();
			s = s.initFromData(data);
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
