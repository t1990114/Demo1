package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private final static ThreadLocal<Connection> LOCAL = new ThreadLocal<>();
	
	public static Connection  getConnection(){
		String uri = "jdbc:oracle:thin:localhost:1521:ORCL";
		String user = "system";
		String password = "fsc";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("加载oracle驱动失败!");
		}
		Connection conn = LOCAL.get();
		if(null!=conn){
			return conn;
		}
		try {
			conn=DriverManager.getConnection(uri,user,password);
			LOCAL.set(conn);
		} catch (SQLException e) {
			System.out.println("获取连接失败！");
		}
		
		return conn;
	}
	public static void release(){
		Connection conn = LOCAL.get();
		if(null!=conn){
			DBUtils.release(conn);
			LOCAL.remove();
		}
	}
}
