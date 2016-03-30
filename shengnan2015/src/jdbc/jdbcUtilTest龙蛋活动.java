package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class jdbcUtilTest龙蛋活动 {
	private static Connection con;  
	public static void test() throws Exception{
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://119.29.57.138/test_peizhi4?user=dragon&password=dragon" +
				"&useUnicode=true&characterEncoding=utf-8";//指定jdbc数据源
		String tablename = "huodong_longdan";
		
		Class.forName(driver);
		con=DriverManager.getConnection(url);
		
		
//		pstmt.setDate(1, new java.sql.Date(date.getTime()));
		
		PreparedStatement pst = null;
		try {
			String sql = "INSERT INTO "+tablename+"(uid,openid,date,count,type) VALUES(?,?,?,?,?);";
			pst = con.prepareStatement(sql);
			pst.setInt(1, 2);e
			pst.setString(2, "nin");
			pst.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			pst.setInt(4, 3);
			pst.setInt(5, 2);
			pst.setInt(5, 1);
			pst.execute();
			pst.close();
			System.out.println(sql);
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
