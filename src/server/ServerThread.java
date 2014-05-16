package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import Entity.Credential;
import Entity.TransObject;

public class ServerThread extends Thread{

	private Socket socket;
	private int id;
	private Credential credential;
	private boolean isLoggedIn;
	
	public ServerThread(Socket clientSocket, int id){
		socket = clientSocket;
		this.id = id;
	}
	
	public void run(){
		ObjectInputStream ois = null;
		PrintWriter pw = null;
		try { 
			ois = new ObjectInputStream(socket.getInputStream());
	    	pw = new PrintWriter(socket.getOutputStream(), true);
    	    while(true){
    	    	System.out.println("dummy1");
    	    	Object obj = ois.readObject();
    	    	System.out.println("dummy1");
    	    	if(obj instanceof TransObject){
    	    		if(isLoggedIn)
    	    			pw.write("Print information received");
    	    	}
    	    	else if(obj instanceof Credential){
    	    		System.out.println("Credential information received");
    	    		credential = (Credential) obj;
    	    		if(checkCredential(credential)){
    	    			pw.write("Login sucess");
    	    			pw.flush();
    	    			isLoggedIn = true;
    	    		}
    	    		else{
    	    			pw.write("Wrong username or password");
    	    			pw.flush();
    	    		}
    	    		System.out.println("Message sent");
    	    	}
    	    }
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(ois!=null){
					ois.close();
					pw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private boolean checkCredential(Credential c){
		if(c.getId().equals(c.getPassword()))
			return true;
		return false;
	}
	
	private void finish(){
		try {
			socket.close();
			join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServerThreadFactory.release(id);
	}
}