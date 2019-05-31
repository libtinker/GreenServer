

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

	
	public static Map<String, Object> login(Map<String, Object> requestParm) {
		
		Map<String, Object> rusultMap = new HashMap<>();
		 //根据user_id返回 这条用户的信息
		 String select_sql = "SELECT * FROM user WHERE user_name='"+requestParm.get("username")+"'";
		 ArrayList<Map<String, Object>> result2 = DBTool.mysqli_select("test", select_sql);
		 if (result2.isEmpty()) {
			 rusultMap.put("code", "300");
			 rusultMap.put("msg", "账号不存在");
			 rusultMap.put("data", "");
		 }else {
			 String sql = "SELECT user_name, password,user_id FROM user";
			 ArrayList<Map<String, Object>> result = DBTool.mysqli_select("test", sql);
			 Map<String, Object> userMap = result.get(0);
			 String password = (String) userMap.get("password");			 
		if (password.equals(requestParm.get("passwold"))) {
			 rusultMap.put("code", "200");
			 rusultMap.put("msg", "登录成功");
			 rusultMap.put("data", userMap);
		}else {
			 rusultMap.put("code", "400");
			 rusultMap.put("msg", "账号或密码错误");
			 rusultMap.put("data", "");
		}	
		}
		return rusultMap;
	}
	
	public static Map<String, Object> register(Map<String, Object> requestParm) {//注册
		
		 String userName = (String) requestParm.get("username");//前段判断是否合法
		 String password = (String) requestParm.get("password");//前段判断是否合法
		 Map<String, Object> dbMap = new HashMap<>();
		 String select_sql = "SELECT * FROM user WHERE user_name='"+requestParm.get("username")+"'";
		 ArrayList<Map<String, Object>> dbList = DBTool.mysqli_select("test", select_sql);
		 if (dbList.isEmpty()) {
			 String userId = java.util.UUID.randomUUID().toString();
			 String insert_sql = "insert into user (user_name,password,user_id)"+"values("+userName+","+password+","+userId + 
						"')";	
			 DBTool.mysql_insert("test", insert_sql);
			 //读取数据库
			 String sql = "SELECT user_name, password,user_id FROM user";
			 ArrayList<Map<String, Object>> array = DBTool.mysqli_select("test", sql);
			 Map<String, Object> userMap = array.get(0);
			 dbMap.put("code", "200");
			 dbMap.put("msg", "注册成功");
			 dbMap.put("data", userMap);
		 }else {
			 dbMap.put("code", "500");
			 dbMap.put("msg", "账号已存在");
			 dbMap.put("data", "");
		}
		return dbMap;
	}
}
