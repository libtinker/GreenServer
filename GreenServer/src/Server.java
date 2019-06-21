
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.mysql.cj.util.StringUtils;

public class Server extends Thread {

	private ServerSocket ss ;
	private static Map<String, Object> headers;
	public Server(int porm) throws IOException{
		
		 ss = new ServerSocket(porm);
	}
	
	   public void run()
	   {
			  try {
		            while(true){//接收数据
		                Socket socket=ss.accept();	
		 
//		                receiveParm(socket);
//		                receiveFile(socket);
		                parseRequest(socket);
		                
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
	   }
	   
	   public static void receiveParm(Socket socket) throws IOException  {
		   StringBuffer result = new StringBuffer();
//           System.out.println(socket.getRemoteSocketAddress());
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
                   System.out.println("POST-------"+Integer.parseInt(postParamterLength));
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
      		       
        	serverOutput(socket, parmMap);
	}
	   
	   public static void receiveFile(Socket socket) throws IOException {
		    byte[] inputByte = null;
		    int length = 0;
		    DataInputStream din = null;
		    FileOutputStream fout = null;
		    try {
		        din = new DataInputStream(socket.getInputStream());
		        System.out.println("hahahhahahh...");
//System.out.println(din.readUTF());
		        
System.out.println("aaaaaaaaaa");
// System.out.println("hahahhahahh...");
		        fout = new FileOutputStream(new File("///Users/tiankongxiyinwo/Desktop/javapic/test.png"));
		        System.out.println("开始接收数据...");

		        inputByte = new byte[1024];
		        while (true) {
		            if (din != null) {
		                length = din.read(inputByte, 0, inputByte.length);
		            }
		            if (length == -1) {
		                break;
		            }
		            System.out.println(length);
		            fout.write(inputByte, 0, length);
		            fout.flush();
		        }
		        System.out.println("完成接收");
		    } catch (Exception ex) {
		        ex.printStackTrace();
		    } finally {
		        if (fout != null)
		            fout.close();
		        if (din != null)
		            din.close();
		        if (socket != null)
		            socket.close();
		    }
		}
	   
	   public static void serverOutput(Socket socket,Map<String, Object> parmMap) throws IOException  {
		 //输出ִ
           PrintWriter pw=new PrintWriter(socket.getOutputStream());
           Map<String, Object> map = ServerTool.getDbData( parmMap);;          
           String json =   JSON.toJSONString(map);
           pw.println("HTTP/1.1 200 OK");
           pw.println("Content-type:text/json;charset:utf-8");
           pw.println();
           pw.println(json);
           pw.flush();
           socket.close();
	}
	   public static void parseRequest( Socket socket ) throws IOException { 
//           BufferedReader bd=new BufferedReader(new InputStreamReader(socket.getInputStream()));

		   LineNumberReader br = new LineNumberReader(new InputStreamReader(socket.getInputStream())); 
		   StringBuffer sb = new StringBuffer(); 
		   String str = null; 
		   try { 
		     //读取请求行 
		     String requestLine = br.readLine(); 
		     String method = null;
			if (!StringUtils.isNullOrEmpty(requestLine)) { 
		       sb.append(requestLine); 
		       String[] reqs = requestLine.split(" "); 
		       if (reqs != null && reqs.length > 0) { 
		         if ("GET".equals(reqs[0])) { 
		           method = "GET"; 
		         } else { 
		           method = "POST"; 
		         } 
		       } 
		     } 
		 
			headers =  new HashMap<>();

			//读取请求头 
		     while ((str = br.readLine()) != null) { 
		       if ("".equals(str)) { 
		         break; 
		       } 
		       if (!StringUtils.isNullOrEmpty(str)) { 
		         if (str.indexOf(":") > 0) { 
		           String[] strs = str.split(":"); 
		           System.out.println("---------"+str);
		           headers.put(strs[0].toLowerCase(), strs[1].trim()); 
		         } 
		       } 
		       sb.append(str).append("\n"); 
		     } 
		     //POST请求，Content-type为 multipart/form-data 
		     String contentType = null; 
		     Map<String, Object> parameters = new HashMap<>();
			if ("POST".equals(method) && ((contentType = (String) headers.get("content-type")) != null
		         && ((String) headers.get("content-type")).startsWith("multipart/form-data"))) { 
		       //文件上传的分割位 这里只处理单个文件的上传 
		       String boundary = contentType.substring(contentType.indexOf("boundary") + 
		           "boundary=".length()); 
		       //解析消息体 
		       while ((str = br.readLine()) != null) { 
		         //解析结束的标记 
		         do { 
		           //读取boundary中的内容 
		           //读取Content-Disposition 
		           str = br.readLine(); 
		           //说明是文件上传 
		           if (str.indexOf("Content-Disposition:") >= 0 && str.indexOf("filename") > 0) { 

		             str = str.substring("Content-Disposition:".length()); 
		             String[] strs = str.split(";"); 
		             String fileName = strs[strs.length - 1].replace("\"", "").split("=")[1]; 
		             System.out.println("fileName = " + fileName); 
		             //这一行是Content-Type 
		             br.readLine(); 
		             //这一行是换行 
		             br.readLine(); 
		             //正式去读文件的内容 
		             BufferedWriter bw = null; 
		             try { 
		               bw = new BufferedWriter(new OutputStreamWriter(new
		                   FileOutputStream("///Users/tiankongxiyinwo/Desktop/javapic" + 
		                   File.separator + fileName), "UTF-8")); 
		               while (true) { 
		                 str = br.readLine(); 
		                 if (str.startsWith("--" + boundary)) { 
		                   break; 
		                 } 
		                 bw.write(str); 
		                 bw.newLine(); 
		               } 
		               bw.flush(); 
		             } catch (Exception e) { 
		   
		             } finally { 
		               if (bw != null) { 
		                 bw.close(); 
		               } 
		             } 
		           } 
		           if (str.indexOf("Content-Disposition:") >= 0) { 
		             str = str.substring("Content-Disposition:".length()); 
		             String[] strs = str.split(";"); 
		             String name = strs[strs.length - 1].replace("\"", "").split("=")[1]; 
		             br.readLine(); 
		             StringBuilder stringBuilder = new StringBuilder(); 
		             while (true) { 
		               str = br.readLine(); 
		               if (str.startsWith("--" + boundary)) { 
		                 break; 
		               } 
		               stringBuilder.append(str); 
		             } 
		             parameters.put(name, stringBuilder.toString()); 
		           } 
		         } while (("--" + boundary).equals(str)); 
		         //解析结束 
		         if (str.equals("--" + boundary + "--")) { 
		           break; 
		         } 
		       } 
		     } 
		     System.out.println(sb.toString()); 
		     //获取URI 
//		     uri = StringUtils.parserUri(sb.toString(), " "); 
//		     int flag = -1; 
//		     //说明有参数 
//		     if ((flag = uri.indexOf('?')) >= 0) { 
//		       String oldUri = uri; 
//		       uri = uri.substring(0,flag); 
//		       String parameterPath = oldUri.substring(flag+1); 
//		       String[] parameter = parameterPath.split("&"); 
//		       if (parameter != null && parameter.length > 0) { 
//		         for (int i = 0; i < parameter.length; i++) { 
//		           String str1 = parameter[i]; 
//		           if((flag = str1.indexOf('=')) >= 0){ 
//		             String key = str1.substring(0,flag); 
//		             String value = str1.substring(flag+1); 
//		             parameters.put(key,value); 
//		           }else{ 
//		             parameters.put(str,null); 
//		           } 
//		         } 
//		       } 
//		     } 
		   } catch (IOException e) { 
		     e.printStackTrace(); 
		   } 
		 } 
}




