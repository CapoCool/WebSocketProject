package socketProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String handle;
	private InetAddress ipv4Address;
	private int portNumberForServer;
	private int portNumberForLeftPort;
	private int portNumberForRightPort;
	
	private List<User> follows = new ArrayList<User>();
	private List<String> tweets = new ArrayList<String>();
	
	public String getHandle() {
		return handle;
	}
	public void setHandle(String handle) {
		this.handle = handle;
	}
	public InetAddress getIpv4Address() {
		return ipv4Address;
	}
	public void setIpv4Address(InetAddress ipv4Address) {
		this.ipv4Address = ipv4Address;
	}
	public int getPortNumberForServer() {
		return portNumberForServer;
	}
	public void setPortNumberForServer(int portNumberForServer) {
		this.portNumberForServer = portNumberForServer;
	}
	public List<User> getFollows() {
		return follows;
	}
	public void setFollows(List<User> follows) {
		this.follows = follows;
	}
	public List<String> getTweets() {
		return tweets;
	}
	public void setTweets(List<String> tweets) {
		this.tweets = tweets;
	}
	public int getPortNumberForLeftPort() {
		return portNumberForLeftPort;
	}
	public void setPortNumberForLeftPort(int portNumberForLeftPort) {
		this.portNumberForLeftPort = portNumberForLeftPort;
	}
	public int getPortNumberForRightPort() {
		return portNumberForRightPort;
	}
	public void setPortNumberForRightPort(int portNumberForRightPort) {
		this.portNumberForRightPort = portNumberForRightPort;
	}
	
//	public void RunClient() {
//		
//		User user = new User();
//		DatagramSocket sock = null;
//		int port = 7777;
//		byte[] b;
//		DatagramPacket dp;
//		byte[] buffer;
//		DatagramPacket reply;
//		String s = "";
//		
//		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
//		
//		try
//		{
//			sock = new DatagramSocket();
//			
//			InetAddress host = InetAddress.getByName("localhost");
//			echo("Welcome to Tweeter! Here's a list of commands:");
//			echo("R: Registers a new user");
//			echo("F: to follow another user");
//			echo("D: to drop another user");
//			echo("T: to tweet");
//			echo("Q: to quit");
//			
//			
//			while(!s.equals("Q"))
//			{
//				//take input and send the packet
//				//echo("Enter message to send : ");
//				s = (String)cin.readLine();
//				b = s.getBytes();
//				
//				dp = new DatagramPacket(b , b.length , host , port);
//				sock.send(dp);
//				
//				//now receive reply
//				//buffer to receive incoming data
//				buffer = new byte[65536];
//				 reply = new DatagramPacket(buffer, buffer.length);
//				sock.receive(reply);
//				
//				if(s.equals("R")) {
//					byte[] data = reply.getData();
//					s = new String(data, 0, reply.getLength());
//					echo(s);
//					
//					s = (String)cin.readLine();
//					b = s.getBytes();
//					
//					dp = new DatagramPacket(b , b.length , host , port);
//					sock.send(dp);
//					
//					buffer = new byte[65536];
//					 reply = new DatagramPacket(buffer, buffer.length);
//					sock.receive(reply);
//					
//					data = reply.getData();
//					s = new String(data, 0, reply.getLength());
//					user.handle = s;
//					user.setIpv4Address(host);
//					user.setPortNumberForServer(port);
//					
//					echo(s);
//				}
//				
//				//byte[] data = reply.getData();
//				//s = new String(data, 0, reply.getLength());
//				
//				//echo the details of incoming data - client ip : client port - client message
//				//echo(s);
//			}
//			sock.close();
//		}
//		
//		catch(IOException e)
//		{
//			System.err.println("IOException " + e);
//		}
//		
//	}
	public static void main(String args[])
	{
		User user = new User();
		DatagramSocket sock = null;
		int port = 7777;
		byte[] b;
		DatagramPacket dp;
		byte[] buffer;
		DatagramPacket reply;
		String s = "";
		
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			sock = new DatagramSocket();
			
			InetAddress host = InetAddress.getByName("localhost");
			echo("Welcome to Tweeter! Here's a list of commands:");
			echo("R: Registers a new user");
			echo("F: to follow another user");
			echo("D: to drop another user");
			echo("T: to tweet");
			echo("Q: to quit");
			
			
			while(!s.equals("Quit"))
			{
				//take input and send the packet
				//echo("Enter message to send : ");
				s = (String)cin.readLine();
				b = s.getBytes();
				
				dp = new DatagramPacket(b , b.length , host , port);
				sock.send(dp);
				
				//now receive reply
				//buffer to receive incoming data
				buffer = new byte[65536];
				 reply = new DatagramPacket(buffer, buffer.length);
				sock.receive(reply);
				
				if(s.equals("R")) {
					
					if(user.handle == null) {
					byte[] data = reply.getData();
					s = new String(data, 0, reply.getLength());
					echo(s);
					
					s = (String)cin.readLine();
					b = s.getBytes();
					
					user.handle = s;
					user.setIpv4Address(host);
					user.setPortNumberForServer(port);
					
					dp = new DatagramPacket(b , b.length , host , port);
					sock.send(dp);
					
					buffer = new byte[65536];
					 reply = new DatagramPacket(buffer, buffer.length);
					sock.receive(reply);
					
					data = reply.getData();
					s = new String(data, 0, reply.getLength());
					
					echo(s);
					}
					else {
						echo("You have already created a user!");
					}
				}
				
				if(s.equals("Q")) {
					byte[] data = reply.getData();
					s = new String(data, 0, reply.getLength());
					echo(s);
					
					buffer = new byte[65536];
					 reply = new DatagramPacket(buffer, buffer.length);
					sock.receive(reply);
					
					data = reply.getData();
					s = new String(data, 0, reply.getLength());
					
					echo(s);
				}
				
				if(s.equals("D")) {
					byte[] data = reply.getData();
					s = new String(data, 0, reply.getLength());
					echo(s);
					
					buffer = new byte[65536];
					 reply = new DatagramPacket(buffer, buffer.length);
					sock.receive(reply);
					
					data = reply.getData();
					s = new String(data, 0, reply.getLength());
					
					echo(s);
				}
				
				if(s.equals("F")) {
					byte[] data = reply.getData();
					s = new String(data, 0, reply.getLength());
					echo(s);
					
					s = (String)cin.readLine();
					b = s.getBytes();
					
					dp = new DatagramPacket(b , b.length , host , port);
					sock.send(dp);
					
					buffer = new byte[65536];
					 reply = new DatagramPacket(buffer, buffer.length);
					sock.receive(reply);
					
					data = reply.getData();
					s = new String(data, 0, reply.getLength());
					
					echo(s);
				}
				
				if(s.equals("whoami")) {
					System.out.println("You are: " + user.handle);
				}
				//byte[] data = reply.getData();
				//s = new String(data, 0, reply.getLength());
				
				//echo the details of incoming data - client ip : client port - client message
				//echo(s);
			}
			sock.close();
			echo("Client Closed");
		}
		
		catch(IOException e)
		{
			System.err.println("IOException " + e);
		}
	}
	
	//simple function to echo data to terminal
	public static void echo(String msg)
	{
		System.out.println(msg);
	}
}
