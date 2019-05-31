
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class Server extends Thread {

	private ServerSocket ss ;
	public Server(int porm) throws IOException{
		
		 ss = new ServerSocket(porm);
	}
	
	   public void run()
	   {
			  try {
		            while(true){//接收数据
		                Socket socket=ss.accept();	                
		                StringBuffer result = new StringBuffer();
		                System.out.println(socket.getRemoteSocketAddress());
		                BufferedReader bd=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		                
		                String requestHeader ;
		                int contentLength=0;
		               
		                while((requestHeader=bd.readLine())!=null&&!requestHeader.isEmpty()){
		                    System.out.println(requestHeader);
		                    result.append(requestHeader);

		                    /**
		                     * POST方法
		                     * 1.
		                     */
		                    if(requestHeader.startsWith("Content-Length")){
		                    	
		                        int begin=requestHeader.indexOf("Content-Lengh:")+"Content-Length:".length()+1;
		                        String postParamterLength=requestHeader.substring(begin).trim();
		                        contentLength=Integer.parseInt(postParamterLength);
//		                        System.out.println("POST-------"+Integer.parseInt(postParamterLength));
		                    }
		                }
		                StringBuffer sb=new StringBuffer();
		                if(contentLength>0){
		                    for (int i = 0; i < contentLength; i++) {
		                        sb.append((char)bd.read());
		                    }
		                }
		               
		             	Map<String, Object> parmMap =ServerTool.getMapWithUrlParm(sb.toString());
		             	System.out.println(parmMap);
		           		               
		                //输出ִ
		                PrintWriter pw=new PrintWriter(socket.getOutputStream());
		                Map<String, Object> map = ServerTool.getDbData( parmMap);;
		                System.out.println("------------"+result.toString());
		               
		                String json =   JSON.toJSONString(map);
		                pw.println("HTTP/1.1 200 OK");
		                pw.println("Content-type:text/json;charset:utf-8");
		                pw.println();
		                pw.println(json);
		                pw.flush();
		                socket.close();
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
	   }
	

	
}
