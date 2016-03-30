package sacred.alliance.magic.app.count.vo;

import lombok.Data;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.KV;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
/**
 * 
 */
public @Data class CountRecord implements KV<Integer,String>{
	
	@Protobuf(fieldType=FieldType.INT32,order=1)
	private int id;
	@Protobuf(fieldType=FieldType.STRING,order=2)
	private String v;
	

	@Override
	public Integer $key() {
		return id;
	}


	@Override
	public void $key(Integer value) {
		this.id = value ;
	}


	@Override
	public String $value() {
		return this.v;
	}


	@Override
	public void $value(String value) {
		this.v = value ;
	}

}
