package a_serial.protobuf;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
public class Shop{  
	  
    @Protobuf(fieldType = FieldType.STRING, order = 1)  
    private String shopId;  
    private byte[] data;  
      
    @Protobuf(fieldType = FieldType.OBJECT, order = 2)  
    private List<GoodsRecord> goodsRecordList;  
  
    public Shop(){  
        super();  
    }  
      
    public byte[] getData(){  
        return data;  
    }  
    public String getShopId() {  
        return shopId;  
    }  
  
    public void setShopId(String shopId) {  
        this.shopId = shopId;  
    }  
  
    public List<GoodsRecord> getGoodsRecordList() {  
        return goodsRecordList;  
    }  
  
    public void setGoodsRecordList(List<GoodsRecord> goodsRecordList) {  
        this.goodsRecordList = goodsRecordList;  
    }  
  
    private Shop parseData(byte[] targetData)  
            throws IOException {  
        Codec<Shop> codec = ProtobufProxy.create(Shop.class);  
        return codec.decode(targetData) ;  
    }  
  
    private byte[] buildData() {  
        try {  
            Codec<Shop> codec = ProtobufProxy.create(Shop.class);  
            return codec.encode(this);  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
    public void preToData() {  
        this.data = buildData();  
    }  
  
    public Shop postFromData() {  
        Shop s = null;  
        try {  
            s = parseData(this.data);  
            this.shopId = s.getShopId();  
            this.goodsRecordList = s.getGoodsRecordList();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return s;  
    }  
    public Shop initFromData(byte[] data) {  
        Shop s = null;  
        try {  
            s = parseData(data);  
            this.shopId = s.getShopId();  
            this.goodsRecordList = s.getGoodsRecordList();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return s;  
    }  
    @Override  
    public String toString() {  
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);  
    }  
}  