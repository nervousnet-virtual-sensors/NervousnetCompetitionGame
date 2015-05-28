import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

public class WriteJSON {
	
	public static void sendJSON(Socket socket, Object o) {
				
		try {
			Scanner in = new Scanner(socket.getInputStream());
			
			while(!in.nextLine().isEmpty());
			
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			String message = new Gson().toJson(o);			
			out.println("HTTP/1.0 200 OK");
		    out.println("Content-Type: text/json");
		    out.printf("Content-Length: %d%n",message.length());
		    out.println("Access-Control-Allow-Origin: *");
		    out.println();
		    out.println(message);
		    out.flush();
		    
		    socket.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}							
	}

}
