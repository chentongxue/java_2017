package jdbc;

import java.sql.DriverManager;

public class Test {
	public static void main(String args){
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://localhost/storage?user=root&password=daofeng" +
				"&useUnicode=true&characterEncoding=GBK";//指定jdbc数据源
		String tablename = "stockinfo";
		
		try {
			Class.forName(driver);
//			this.conn=DriverManager.getConnection(url);
		} catch (Exception e) {e.printStackTrace();}
	}

}
