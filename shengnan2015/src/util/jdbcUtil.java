package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class jdbcUtil {
	private static Connection conn;  
	public static void test(){
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://localhost/storage?user=root&password=123123" +
				"&useUnicode=true&characterEncoding=utf-8";//指定jdbc数据源
		String tablename = "test";
		
		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url);
		} catch (Exception e) {e.printStackTrace();}
		
		
		
		ResultSet rset;
		try {
			Statement stmt = conn.createStatement(1004,1008);//1005 结果集可滚动 对数据更新敏感 1008
			String sql="SELECT*FROM "+tablename;                 //select 语句  注意空格
			rset = stmt.executeQuery(sql);           //执行SELECT语句
			conn.setAutoCommit(false);//禁止自动提交，设置回滚
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		} 
		try
		{
			rset.moveToInsertRow();
			for(int i = 0; i<2; i++)
			{
				rset.updateString(i+1, textfield[i].getText());
			}
			rset.insertRow();
			rset.moveToCurrentRow();
			conn.commit();//提交，在这里最终完成添加操作
		} catch (SQLException e)
		{
			try
			{
				conn.rollback();//操作不成功就要回滚
			} catch (SQLException e1) {}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
