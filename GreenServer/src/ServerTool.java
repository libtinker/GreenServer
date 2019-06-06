import java.util.HashMap;
import java.util.Map;


public class ServerTool {

	public static Map<String, Object> getMapWithUrlParm(String urlParm) {	//解析客户端传来的数据
		Map<String, Object> map =  new HashMap<>();
		String [] stringArr= urlParm.split("&");
		for(int i=0;i<stringArr.length;i++) {
			String [] keyValueArr = stringArr[i].split("=");
			map.put(keyValueArr[0], keyValueArr[1]);
		}
		return map;
	}
	
	public static Map<String, Object> getDbData(Map<String, Object> requestParm) {
		Map<String, Object> rusultMap = null;
		String apiName = (String) requestParm.get("apiName");
		if (apiName.equals("login")) {//登录
			rusultMap = User.login(requestParm);
		}else if (apiName.equals("recommend")) {
			rusultMap = HomeServer.recommend(requestParm);
		}
		return rusultMap;
	}
	
}
