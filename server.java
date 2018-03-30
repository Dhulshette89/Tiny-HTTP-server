import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Server {
	
	private static String SERVER_BASEPATH;
	private static int SERVER_PORT;
	
	
	static class ServerThread implements Runnable {

		Socket connection = null;
		
		public ServerThread(Socket conn)
		{
			connection=conn;
		}
		
		private static final Map<String, String> codeMap = new HashMap<String, String>() {{
			put("200", "OK");
			put("404", "Not Found");
			put("400", "Bad Request");
			put("403", "Forbidden");
		}};
		
		public static void updateResponseHeader(String code,
										  String mime,
										  int length,
										  DataOutputStream outStream) throws IOException
		{
			StringBuilder responseHeader = new StringBuilder();
			outStream.writeBytes("HTTP " + code + " " + codeMap.get(code) + "\r\n");
			responseHeader.append("HTTP " + code + " " + codeMap.get(code) + "\r\n");
			outStream.writeBytes("Content-Type: " + mime + "\r\n");
			responseHeader.append("Content-Type: " + mime + "\r\n");
			outStream.writeBytes("Content-Length: " + length + "\r\n");
			responseHeader.append("Content-Length: " + length + "\r\n");
			outStream.writeBytes("Date: " + Calendar.getInstance().getTime() + "\r\n");
			responseHeader.append("Date: " + Calendar.getInstance().getTime() + "\r\n");
			outStream.writeBytes("Server: HTTP Server");
			responseHeader.append("Server: HTTP Server");
			outStream.writeBytes("\r\n\r\n");
			log("Outgoing response header:");
			System.out.println(responseHeader);
		}
		
		public static void generateResponse(DataOutputStream outStream,
										    Socket connection,
										    String input
										   ) throws IOException 
		{
			log("Incoming HTTP request:" + input);
			String  tokens[] = input.split("\\s");
			String filePath = "";
			String protocol = "";
			String httpMethod = "";
			boolean keepAlive = false;
			File page = null;
			try
			{
			    for(int i=0; i<tokens.length; i++)
			    {
			    		if(i==0)
			    		{
			    			httpMethod = tokens[i];
			    		}
			    		if(i==1)
			    		{
			    			filePath = SERVER_BASEPATH + tokens[i];
			    		}
			    		
			    		if(i==2)
			    		{
			    			if(!(tokens[i].contains("HTTP/1.0") || tokens[i].contains("HTTP/1.1")))
			    			{
			    				log("Only HTTP/1.0 or HTTP/1.1 protocol is supported");
			    				String responseString = "400 Bad Request";
			    				updateResponseHeader("400", "text/plain", responseString.length(), outStream);
			    				outStream.write(responseString.getBytes());
			    				return;
			    			}
			    			
			    			protocol = tokens[i];
			    		}
			    		
			    		if(tokens[i].contains("keep-alive"))
			    		{
			    			keepAlive = true;
			    		}
			    }
			    
			    if(!httpMethod.equals("GET"))
	    			{
	    				log("Only GET method is supported");
	    				String responseString = "400 Bad Request";
	    				updateResponseHeader("400", "text/plain", responseString.length(), outStream);
	    				outStream.write(responseString.getBytes());
	    				return;
	    			}

			    if(filePath.endsWith("/"))
					filePath = filePath + "index.html";
				page = new File(filePath);

				if(!page.exists() || !page.canRead())
				{
					String response = " 403 Forbidden";
					updateResponseHeader("403","text/plain",response.length(),outStream);
					outStream.write(response.getBytes());
					log("The requested page either does not exist or is not accessible");
					return;
				}
				
				InputStream is = new FileInputStream(filePath);
				byte[] data = new byte[is.available()];
				is.read(data);
				
				String mime="text/plain";
		       if (filePath.endsWith(".html") || filePath.endsWith(".htm"))
		    	   	 mime="text/html";
		       else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg"))
		    	   	 mime="image/jpeg";
		       else if (filePath.endsWith(".gif"))
		    	   	 mime="image/gif";
		       else if (filePath.endsWith(".class"))
		    	   	 mime="application/octet-stream";
				
				updateResponseHeader("200",mime,data.length,outStream);
				outStream.write(data);
				outStream.write("\r\n".getBytes());
			}
			catch(FileNotFoundException fe)
			{
				String responseString = "404 File Not Found";
				updateResponseHeader("404","text/plain",responseString.length(),outStream);
				outStream.write(responseString.getBytes());
			}
			catch(IOException ioe)
			{
				String responseString = "400 Bad Request";
				updateResponseHeader("400","text/plain",responseString.length(),outStream);
				outStream.write(responseString.getBytes());
			}
			finally{
				
				if(protocol.contains("HTTP/1.1") || keepAlive)
				{
					connection.setKeepAlive(true);
					connection.setSoTimeout(10000);
				}
				else
				{
					connection.close();
				}
			}
		}
		
		public static String getTime()
		{
			Calendar cal = Calendar.getInstance();
			cal.getTime();
			System.out.println(cal.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			String time = "[" + sdf.format(cal.getTime()) + "] ";
			return time;
		}
		
		public static void log(String message)
		{
			if(message==null) return;
			System.out.println(getTime() + message);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			BufferedReader inReader;
			DataOutputStream outStream = null;
			String inputString;
			
			try{
				while(!connection.isClosed())
				{
					inReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					outStream = new DataOutputStream(connection.getOutputStream());
					String httpRequest = "";
					inputString = inReader.readLine();
					int countHeaders = 1;
					
					
					while(inputString!=null)
					{
						if(httpRequest.length() > 0 )
							httpRequest = httpRequest + " " + inputString;
						else
							httpRequest = inputString;
						inputString = inReader.readLine();
						countHeaders++;
						if(inputString.contains("Connection") || countHeaders>2)
						{
							httpRequest = httpRequest + " " + inputString;
							break;
						}
					}
					
					if(inputString!=null)
						generateResponse(outStream,connection,httpRequest);
					
					outStream.flush();
				}
				
			}
			catch(SocketTimeoutException soe)
			{
				try {
					log("Socket connection timed out");
					connection.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally{
			}
		}
		
	}
	
	public static void main(String args[]) throws IOException
	{
		if(args.length < 4)
		{
			SERVER_BASEPATH = System.getProperty("user.dir");
			SERVER_PORT = 8085;
		}
		else
		{
			if(!args[0].equalsIgnoreCase("-document_root") || !args[2].equalsIgnoreCase("-port"))
			{
				System.out.println("Correct usage: ./server -document_root \"/home/user\" -port 8085");
				return;
			}
			SERVER_BASEPATH = args[1];
			SERVER_PORT = Integer.parseInt(args[3]);
		}
		
		System.out.println("Server running at port:" + SERVER_PORT);
		System.out.println("Server base html directory:" + SERVER_BASEPATH);
		System.out.println("Waiting for connection requests...");
	
		ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
		while(true)
		{
			Socket connection = serverSocket.accept();
			new Thread(new ServerThread(connection)).start();	
		}
	}
	
	

}
