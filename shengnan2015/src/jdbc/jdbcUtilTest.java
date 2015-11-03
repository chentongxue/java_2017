package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sun.xml.internal.ws.api.server.SDDocumentFilter;


public class jdbcUtilTest {
	private static Connection con; 
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void test() throws Exception{
		Set<String> set = new HashSet<String>();
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://203.195.199.128/dragon_1?user=dragon&password=dragon" +
				"&useUnicode=true&characterEncoding=utf-8";//指定jdbc数据源
		String tablename = "last_login";
		
		Class.forName(driver);
		con=DriverManager.getConnection(url);
		
		
		PreparedStatement pst = null;
		
		pst = con.prepareStatement("select * from "+tablename+" where DATE != ? OR DATE IS NULL");
		Date date =new Date();
		pst.setDate(1, new java.sql.Date(date.getTime()));
		ResultSet rs = pst.executeQuery();
		String serverId;
		
		while(rs.next()){
			serverId = rs.getString("openid");
			set.add(serverId);
		}
		pst.close();
		con.close(); 
		
		System.out.println(set.size());
		if(date==null){
			System.out.println(date);
			return ;
		}
		System.out.println(sdf.format(date));
		


		
	}
	public static void main(String[] args) {
		try {
			test();
//			String s = "012345";
//			
//			System.out.println(s.substring(s.length()-2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
