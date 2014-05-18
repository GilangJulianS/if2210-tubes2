package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.*;
import java.util.*;
import Entity.Credential;
import Entity.TransObject;
import java.io.*;

public class ServerThread extends Thread {

    private Socket socket;
    private int id;
    private Credential credential;
    private static boolean isLoggedIn;
    private List<String> loggedinUser;
    public static final int BUF_SIZE = 2048;

    public ServerThread(Socket clientSocket, int id) {
        socket = clientSocket;
        this.id = id;
    }

    public void run() {
        ObjectInputStream ois = null;
        PrintWriter pw = null;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            pw = new PrintWriter(socket.getOutputStream(), true);
            Object obj = ois.readObject();
            if (obj instanceof TransObject) {
                if (isLoggedIn) {/*
                     pw.write("Print information received");
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     System.out.println(in.readLine());
                     out.println("Message received");
                     BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(new File("receive.pdf")));
                     int bytesread;
                     byte[] outBuf = new byte[8192];
                     int retval;
                     do{
                     retval = (bytesread = socket.getInputStream().read(outBuf));
                     bout.write(outBuf, 0, bytesread);
                     }while(retval == BUF_SIZE);\
                     */

                    InputStream in = socket.getInputStream(); //used  

                    DataInputStream clientData = new DataInputStream(in); //used   
                    OutputStream output = new FileOutputStream("receive2.pdf");

                    clientData.readLong();

                    byte[] buffer = new byte[2048];

                    int smblen;
                    while ((smblen = clientData.read(buffer)) > 0) {
                        output.write(buffer, 0, smblen);
                    }
                }
            } else if (obj instanceof Credential) {
                System.out.println("Credential information received");
                credential = (Credential) obj;
                if (checkCredential(credential)) {
                    isLoggedIn = true;
                    pw.write("Login sukses");
                    pw.flush();
                    //loggedinUser.add(credential.getId());

                } else {
                    pw.write("Wrong username or password");
                    pw.flush();
                }
                System.out.println("Message sent");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                    pw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean checkCredential(Credential c) {
        if (c.getId().equals(c.getPassword())) {
            return true;
        }
        return false;
    }

    private void finish() {
        try {
            socket.close();
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerManager.release(id);
    }
}

/*PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//System.out.println(in.readLine());
out.println("Message received");
BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(new File("ServerFolder/receive.pdf")));
int bytesread;
byte[] outBuf = new byte[2048];
int retval;
do{
	retval = (bytesread = clientSocket.getInputStream().read(outBuf));
	System.out.println(retval);
	bout.write(outBuf, 0, bytesread);
	System.out.println(bytesread);
	System.out.println("dummy1");
}while(retval == BUF_SIZE);*/