package socketProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
	

	public static void main(String args[])
	{
		User user = new User();
		DatagramSocket sock = null;
		int port = 7777;
		byte[] b;
		byte[] data;
		DatagramPacket dp;
		byte[] buffer;
		DatagramPacket reply;
		String s = "";
		
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		Scanner scan = new Scanner(System.in);
		
		try
		{
			//this is just for my own use
			echo("Enter a port to use:");
			s = scan.nextLine();
			sock = new DatagramSocket(Integer.parseInt(s));
			
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
				s = (String)cin.readLine();
				b = s.getBytes();
				
				dp = new DatagramPacket(b , b.length , host , port);
				sock.send(dp);
				
				if(s.substring(0,1).equals("R")) {
					
					if(user.handle == null) {
					user.handle = s.substring(2);
					user.setIpv4Address(host);
					user.setPortNumberForServer(port);
					}
					else {
						echo("You have already created a user!");
					}
				}
				
				if(s.equals("whoami")) {
					System.out.println("You are: " + user.handle);
				}
				
				buffer = new byte[65536];
				 reply = new DatagramPacket(buffer, buffer.length);
				sock.receive(reply);
				data = reply.getData();
				s = new String(data, 0, reply.getLength());
			
				echo(s);
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
