package http;


public class HttpTest {
	 public static void main(String args[]){
		 String uid = "819238912893";
		 String key = "sdf";
//    	 String path = "http://203.195.180.143:8090/cdkey/ios";
    	 String md5 = MD5Util.md5WithUTF8("abf3e3b4f7d0f1685ef1fa0641cb8d2c"+uid+key);
    	 String params = "uid="+uid+"&key="+key+"&sign="+md5;
    	 String url="http://active.gz.1251006671.clb.myqcloud.com:8001/cdkey/ios";
    	 url +=  params;
    	 System.out.println("url:" + url);
    	// String search = HttpUtil.getContent(url, charset)("http://203.195.180.143:8090/cdkey/ios", params);
    		 
    	String str = HttpUtil.getContent(url, "utf-8");
    	System.out.println("str:" + str);
    	
    	// System.out.println(params);
    	// System.out.println(search);
//    	 String search = post("http://203.195.180.143:8090/cdkey/ios", "uid=72620548291132686&key=9f46kPnyuZrpg&sign=" + MD5Util.md5WithUTF8("abf3e3b4f7d0f1685ef1 fa0641cb8d2c726205482911326869f46kPnyuZrpgW"));
// 		System.out.println(search);

		
	 }
}
