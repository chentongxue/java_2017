package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class jdbcUtilTest2 {
	private static Connection con;  
	public static void test() throws Exception{
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://203.195.199.128/dragon_1?user=dragon&password=dragon" +
				"&useUnicode=true&characterEncoding=utf-8";//指定jdbc数据源
		String tablename = "last_login_copy_20150917";
		
		Class.forName(driver);
		con=DriverManager.getConnection(url);
		
		
//		pstmt.setDate(1, new java.sql.Date(date.getTime()));
		
		PreparedStatement pst = null;
		try {
			
			pst = con.prepareStatement("INSERT INTO "+tablename+" VALUES(?,?,?);");
			pst.setString(1, "bao");
			pst.setInt(2, 1);
			pst.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			pst.execute();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("end");
		


		
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
