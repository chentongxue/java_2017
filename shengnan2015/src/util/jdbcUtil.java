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
				"&useUnicode=true&characterEncoding=utf-8";//ָ��jdbc����Դ
		String tablename = "test";
		
		try {
			Class.forName(driver);
			conn=DriverManager.getConnection(url);
		} catch (Exception e) {e.printStackTrace();}
		
		
		
		ResultSet rset;
		try {
			Statement stmt = conn.createStatement(1004,1008);//1005 ������ɹ��� �����ݸ������� 1008
			String sql="SELECT*FROM "+tablename;                 //select ���  ע��ո�
			rset = stmt.executeQuery(sql);           //ִ��SELECT���
			conn.setAutoCommit(false);//��ֹ�Զ��ύ�����ûع�
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
			conn.commit();//�ύ�����������������Ӳ���
		} catch (SQLException e)
		{
			try
			{
				conn.rollback();//�������ɹ���Ҫ�ع�
			} catch (SQLException e1) {}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
