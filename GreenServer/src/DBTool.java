import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBTool {
	// 数据库的用户名与密码，需要根据自己的设置
	static final String USER = "root";
	static final String PASS = "Zheng5511";

	//链接数据库
	static  Connection mysqli_connect(String tableName) {
		Connection conn = null; 
		try {
			// 注册 JDBC 驱动
			Class.forName("com.mysql.cj.jdbc.Driver"); 
			System.out.println("成功加载MySQL驱动程序");  
			
			String db_url = "jdbc:mysql://127.0.0.1:3306/"+tableName+"?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true";
			conn = (Connection) DriverManager.getConnection(db_url, USER, PASS);
		}catch(Exception e){
			// 处理 Class.forName 错误
			e.printStackTrace();
		}
		return conn;
	}
	
	//插入数据(增)
	static void mysql_insert(String tableName,String sql) {
		PreparedStatement psql = null;
		 Connection conn =  DBTool.mysqli_connect(tableName);
		 try{		
			 psql  = conn.prepareStatement(sql);		
			 psql.executeUpdate(); 
			 psql.close();   
			 conn.close();   
	        }catch(SQLException se){
	            // 处理 JDBC 错误
	            se.printStackTrace();
	        }finally{
	            // 关闭资源
	            try{
	                if(psql!=null)psql.close();
	            }catch(SQLException se2){
	            }// 什么都不做
	            try{
	                if(conn!=null) conn.close();
	            }catch(SQLException se){
	                se.printStackTrace();
	            }
	        }
	}
	
	//根据sql查询数据库返回数组（查）
	static   ArrayList<Map<String, Object>> mysqli_select(String tableName,String sql) {
		 Statement smt=null;
		 Connection conn =  DBTool.mysqli_connect(tableName);
		 try{		
			 smt = conn.prepareStatement(sql);
			 System.out.println("Statement成功");
	         
			 ResultSet rs =  ((java.sql.Statement) smt).executeQuery(sql);
	         
			 //getMetaData获得表结构，getColunmCount获得字段数
			 int num = rs.getMetaData().getColumnCount();
			 ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
	            // 展开结果集数据库
	            while(rs.next()){
	            	 Map<String, Object> mapOfColValues = new HashMap<String, Object>(num);
	                 for (int i = 1; i<= num; i++) {
	                     //getColunmName获取字段名
	                     mapOfColValues.put(rs.getMetaData().getColumnName(i),rs.getObject(i));
	                 }
	                 result.add(mapOfColValues);
	            }
	            // 完成后关闭
	            rs.close();
	            smt.close();
	            conn.close();
	            return result;
	        }catch(SQLException se){
	            // 处理 JDBC 错误
	            se.printStackTrace();
	        }finally{
	            // 关闭资源
	            try{
	                if(smt!=null)smt.close();
	            }catch(SQLException se2){
	            }// 什么都不做
	            try{
	                if(conn!=null) conn.close();
	            }catch(SQLException se){
	                se.printStackTrace();
	            }
	        }
			return null;
	    }
	}
