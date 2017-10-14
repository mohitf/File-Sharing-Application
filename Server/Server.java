import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
	
	private ArrayList<String> nameList;
	private ArrayList<PeerThread> peerList;
	private ServerSocket serverSocket;
	int port;
	
	public Server(int port){
		nameList = new ArrayList<String>();
		peerList = new ArrayList<PeerThread>();
		this.port = port;
	}
	
	public void startRunning(){
		try{
			serverSocket = new ServerSocket(port);
		}catch(IOException e){
			System.out.println("Unable to create a serversocket at " + port);
		}
		while(true){
			Socket socket1 = null;
			Socket socket2 = null;
			Socket socket3 = null;
			Socket socket4 = null;
			try{
				socket1 = serverSocket.accept();
				socket2 = serverSocket.accept();
				socket3 = serverSocket.accept();
				socket4 = serverSocket.accept();
			}catch(IOException e){
				System.out.println("Unable to establish connection with the peer");
				continue;
			}
			String add1 = socket1.getInetAddress().toString();
			String add2 = socket2.getInetAddress().toString();
			String add3 = socket3.getInetAddress().toString();
			String add4 = socket4.getInetAddress().toString();
			if(add1.equals(add2)&&add2.equals(add3)&&add3.equals(add4)){
				PeerThread pt = new PeerThread(socket1,socket2,socket3,socket4,this);
				pt.start();
			}else{
				System.out.println("Unable to establish connection with the peer");
			}
		}
	}
	
	public ArrayList<String> getList(String str){
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<nameList.size();i++){
		    if(match(str,nameList.get(i))){
			list.add(nameList.get(i));
		    }
		}
		return list;
	}
    
    private boolean match(String str1,String str2){
		int len1 = str1.length();
		int len2 = str2.length();
		if(len1>len2){
			return false;
		}
		for(int i=0;i<=len2-len1;i++){
			if((str1.toLowerCase()).equals((str2.substring(i,i+len1)).toLowerCase())){
		    	return true;
			}
		}
		return false;
    }
    
	public void addFile(String fname,PeerThread pt){
		nameList.add(fname);
		peerList.add(pt);
	}
	
	public void removeFiles(PeerThread pt){
		int i = 0;
		while(i<peerList.size()){
			if(peerList.get(i)==pt){
				peerList.remove(i);
				nameList.remove(i);
			}else
				i++;
		}
	}
	
	public void sendFile(OutputStream out1,ObjectOutputStream out2,String str){
        for(int i=0;i<nameList.size();i++){
            if(nameList.get(i).equals(str)){
                peerList.get(i).sendFile(out1,out2,str);
                return;
            }
        }
    }
}

class PeerThread implements Runnable{
	
	private OutputStream out1;
	private InputStream in2;
	private ObjectOutputStream out3;
	private ObjectInputStream in3;
	private ObjectInputStream in4;
	private ObjectOutputStream out4;
	private Server server;
	
	public PeerThread(Socket socket1,Socket socket2,Socket socket3,Socket socket4,Server server){
		this.server = server;
		try{
			out1 = socket1.getOutputStream();
			out1.flush();
			in2 = socket2.getInputStream();
			out3 = new ObjectOutputStream(socket3.getOutputStream());
			out3.flush();
			in3 = new ObjectInputStream(socket3.getInputStream());
			out4 = new ObjectOutputStream(socket4.getOutputStream());
			out4.flush();
			in4 = new ObjectInputStream(socket4.getInputStream());
		}catch(IOException e){
			System.out.println("Unable to establish streams in PeerThread");
		}
	}
	
	public void start(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	public void run(){
		shareFiles();
		try{
			while(true){
				String str = (String)in3.readObject();
				requestFile(str);
			}
		}catch(Exception e){
			server.removeFiles(this);
			System.out.println("Peerthread stopped working");
		}
		closeStreams();
	}
	
	private void shareFiles(){
		System.out.println();
		System.out.println("Receiving files for sharing");
		try{
			while(true){
				String str = (String)in3.readObject();
				if(str.equals("DONE SHARING FILES")){
					break;
				}
				System.out.println(str);
				server.addFile(str,this);
			}
		}catch(Exception e){
			System.out.println("Unable to receive anymore files");
		}
		System.out.println("Received all files");
		System.out.println();
    }
	
	private void requestFile(String str){
		ArrayList<String> results = server.getList(str);
		try{
			System.out.println();
			System.out.println("Matching results");
			for(int i=0;i<results.size();i++){
				System.out.println(results.get(i));
				out3.writeObject(results.get(i));
				out3.flush();
			}
			System.out.println("End of results");
			System.out.println();
			String temp = "DONE SENDING RESULTS";
			out3.writeObject(temp);
			out3.flush();
			int num = (int)in3.readObject();
			if(num == 0)
				return;
			num--;
			server.sendFile(out1,out3,results.get(num));
		}catch(Exception e){
			System.out.println("Cannot send the requested file");
		}
	}
	
	public void sendFile(OutputStream oout1,ObjectOutputStream oout2,String fname){
		try{
			System.out.println("Sending file " + fname);
			out4.writeObject(fname);
			out4.flush();
			long len = (Long)in4.readObject();
			oout2.writeObject(fname);
			oout2.flush();
			oout2.writeObject(len);
			oout2.flush();
			byte[] bytes = new byte[16384];
			int count;
			long sum = 0;
			while ((count = in2.read(bytes)) > 0) {
				sum += count;
				oout1.write(bytes, 0, count);
				oout1.flush();
				if(sum==len)
					break;
			}
			System.out.println("Finished sending " + fname);
		}catch(Exception e){
			System.out.println("Unable to send file");
		}
	}
	
	private void closeStreams(){
		try{
			out1.close();
			in2.close();
			out3.close();
			in3.close();
			out4.close();
			in4.close();
		}catch(IOException e){
			System.out.println("Unable to close streams");
		}
	}
}
