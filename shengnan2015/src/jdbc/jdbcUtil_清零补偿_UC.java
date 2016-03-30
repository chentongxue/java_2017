package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class jdbcUtil_清零补偿_UC {
	private static Connection con;  
	private static int  insert_count;  
	private static int  update_count;  
	public static void test() throws Exception{
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://203.195.199.128/dragon_1?user=dragon&password=dragon" +
				"&useUnicode=true&characterEncoding=utf-8";//指定jdbc数据源
		
		Class.forName(driver);
		con=DriverManager.getConnection(url);
		
//		pstmt.setDate(1, new java.sql.Date(date.getTime()));
		
		PreparedStatement pst = null;
		Map map = new HashMap<String, Integer>();
		try {
			pst = con.prepareStatement("SELECT openid,SUM(amount * item_count)*30,status FROM payment GROUP BY openid having status <> 0");
//			pst.setString(1, openId);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()){
				map.put(rs.getString(1), rs.getInt(2));
			}
			pst.close();
			System.out.println("end");
			System.out.println("map iterator start");
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String,Integer> entry = (Map.Entry)iter.next(); 
				String key = entry.getKey();
				Integer val = entry.getValue();
				
				updateOrInsert(key,val);
			}
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
	}
	  private static void updateOrInsert(String openid, int value) {
		  PreparedStatement pst = null;
		  try {
		      pst = con.prepareStatement("select * from compensate where openid=?");
		      pst.setString(1, openid);
		      ResultSet rs = pst.executeQuery();

		      if (rs.next()) {
		        log("update:" + openid + ","+value);
		        pst = con.prepareStatement("UPDATE compensate SET data=? WHERE openid=?");
		        pst.setInt(1, value);
		        pst.setString(2, openid);
		        pst.execute();
		        update_count++;
		      } else {
		        log("insert:" + openid + ","+value);
		        pst = con.prepareStatement("INSERT INTO compensate VALUES(?,?,0,0,0);");
		        pst.setString(1, openid);
		        pst.setInt(2, value);
		        pst.execute();
		        insert_count++;
		      }
		      	pst.close();
		    } catch (SQLException e) {
		    	e.printStackTrace();
		    } finally {
//		    	if (con != null) {
//		    		try {
//		    			con.close();
//		    		} catch (SQLException e) {
//		    			e.printStackTrace();
//		    		}
//		    	}
		    }
	  }
	  public static void log(String s){
		  System.out.println(s);
	  }
	  public static void main(String[] args) {
		  try {
			test();
//			String s = "012345";
//			
			System.out.println(update_count+":"+insert_count);
//			System.out.println(s.substring(s.length()-2));
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	  }
}
