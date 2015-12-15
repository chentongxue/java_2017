package jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class jdbcUtil_保留老玩家记录 {
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
		Map map = new HashMap<String, String>();
		Map dateMap = new HashMap<String, Date>();
		try {
			pst = con.prepareStatement("SELECT * from last_login_copy_20151111_xuanyifubeifen");
//			pst.setString(1, openId);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()){
				map.put(rs.getString(1), rs.getString(2));
				Date d = rs.getDate(3);
				if(d!=null)
				dateMap.put(rs.getString(1), d);
			}
			pst.close();
			System.out.println("end");
			System.out.println("map iterator start");
			Set set  = getSet() ;
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String,String> entry = (Map.Entry)iter.next(); 
				String key = entry.getKey();
				String val = entry.getValue();
				Date d = (Date)dateMap.get(key);
				if(d == null){d = new Date(System.currentTimeMillis());}
				if(set.contains(key)){
					continue;
				}
				updateOrInsert(key,val,d);
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
	  private static Set getSet() {
		  HashSet<String> set = new HashSet<String>();
		  PreparedStatement pst = null;
		  try {
				pst = con.prepareStatement("SELECT * from last_login");
//				pst.setString(1, openId);
				ResultSet rs = pst.executeQuery();
				
				while(rs.next()){
					set.add(rs.getString(1));
				}
				pst.close();
				System.out.println("end");
				System.out.println("set iterator start");
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
//				if (con != null) {
//					try {
//						con.close();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
			}
		  return set;
	      }
		  private static void updateOrInsert(String openid, String value, Date d) {
		  PreparedStatement pst = null;
		  try {
//		      pst = con.prepareStatement("select * from last_login where openid=?");
//		      pst.setString(1, openid);
//		      ResultSet rs = pst.executeQuery();
//
//		      if (rs.next()) {
//		        log("update:" + openid + ","+value);
//		        pst = con.prepareStatement("UPDATE last_login SET serverId=? , date =? WHERE openid=?");
//		        pst.setInt(1, Integer.parseInt(value));
//		        pst.setDate(2, d);
//		        pst.setString(3, openid);
//		        pst.execute();
//		        update_count++;
//		      } else {
//		        log("insert:" + openid + ","+value);
		        pst = con.prepareStatement("INSERT INTO last_login VALUES(?,?,?);");
		        pst.setString(1, openid);
		        pst.setInt(2, Integer.parseInt(value));
		        pst.setDate(3, d);
		        pst.execute();
		        insert_count++;
//		      }
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
//		  System.out.println(s);
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
