import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeServer {


	public static Map<String, Object> recommend(Map<String, Object> requestParm) {
		
		Map<String, Object> rusultMap = new HashMap<>();
		 String sql = "SELECT video_url, title,discribe FROM recommend";
		 ArrayList<Map<String, Object>> result = DBTool.mysqli_select("test", sql);
		 if (result.isEmpty()) {
			 rusultMap.put("code", "600");
			 rusultMap.put("msg", "数据为空");
			 rusultMap.put("data", "");
		 }else {
			 rusultMap.put("code", "200");
			 rusultMap.put("msg", "请求成功");
			 rusultMap.put("data", result);
			 }
		return rusultMap;
	}
	
	
}
