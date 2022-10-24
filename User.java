//package socketProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class User extends Thread{
	
	private String handle;
	private InetAddress ipv4Address;
	private int portNumberForServer;
	private int portNumberForLeftPort;
	private String tweet;
	//We'll need a few things to send this out.
	private User userToSendTo;

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
	public int getPortNumberForLeftPort() {
		return portNumberForLeftPort;
	}
	public void setPortNumberForLeftPort(int portNumberForLeftPort) {
		this.portNumberForLeftPort = portNumberForLeftPort;
	}
	public User getPortNumberForRightPort() {
		return userToSendTo;
	}
	public void setPortNumberForRightPort(User userToSendTo) {
		this.userToSendTo = userToSendTo;
	}
	
	//The only thing this thread will be doing is listening.
	public void run() {
		DatagramSocket sock = null;
		DatagramPacket dp;
		String reply = "";
		byte[] b;
		this.userToSendTo = new User();
		Scanner scan = new Scanner(System.in);
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		//left is for receiving, right is for sending.
		
		try
		{
			String s = "";
			//1. creating a server socket, parameter is local port number
			sock = new DatagramSocket(null);
			
			sock.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), this.portNumberForLeftPort));
			System.out.println("Thread: " + sock.getLocalSocketAddress());
			
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];
			
			//communication loop
			while(true)
			{
				//create packet and wait for incoming packets
				DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
				sock.receive(incoming);
				byte[] data = incoming.getData();
				s = new String(data, 0, incoming.getLength());
				//System.out.println(s);
				
				if(s.length() > 6 && s.substring(0, 6).equals("Change")) {
					String[] info = s.split(" ");
					String ip = "";
					char[] tempString = info[1].toCharArray();
					
					
					for(int i = 0; i < tempString.length;i++) 
					{
						if(i != 0) {
							ip += tempString[i];
						}
					}
					this.userToSendTo.setIpv4Address(InetAddress.getByName(ip));
					this.userToSendTo.setPortNumberForLeftPort(Integer.parseInt(info[2]));
				}
				
				if(s.length() >= 5 && s.substring(0, 5).equals("Tweet")) {
					reply = "User " + this.handle + " Tweeted " + s.substring(6);
					reply += "\nPrevious Hop"+ " " + this.handle;
					//System.out.println(reply);
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , this.userToSendTo.getIpv4Address(), this.userToSendTo.getPortNumberForLeftPort());
					sock.send(dp);
				}
				
				if(s.length() >=4 && s.substring(0, 4).equals("User")) {
					String[] tweet = s.split(" ");
					if(tweet[1].equals(this.handle)) {
						System.out.println("\nTweet has returned to sender: " + s + "\n");
						InetAddress host = InetAddress.getByName("128.110.219.14");
						reply = "EndTweet";
						dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , host , 11000);
						sock.send(dp);
						
					}
					else {
						s = new String(data, 0, incoming.getLength());
						System.out.println(s);
						reply = s;
						reply += "\nPrevious Hop"+ " " + this.handle;
						dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , this.userToSendTo.getIpv4Address(), this.userToSendTo.getPortNumberForLeftPort());
						sock.send(dp);
						
					}
				}
			}
		}
		
		catch(IOException e)
		{
			sock.close();
			System.err.println("IOException " + e);
		}
	}

	public static void main(String args[])
	{
		User user = new User();
		DatagramSocket sock = null;
		Boolean isConnected = true;
		Boolean dontSend = false;
		int port = 11000;
		byte[] b;
		byte[] data;
		DatagramPacket dp;
		byte[] buffer;
		DatagramPacket reply;
		String s = "";
		String leftPort = "";
		
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		Scanner scan = new Scanner(System.in);
		
		try
		{
			//this is just for my own use
			echo("Enter a port to use:");
			s = scan.nextLine();
			echo("Enter a left port to use:");
			leftPort = scan.nextLine();
			user.setPortNumberForLeftPort(Integer.parseInt(leftPort));
			sock = new DatagramSocket(null);
			
			sock.bind(new InetSocketAddress(InetAddress.getLocalHost(), Integer.parseInt(s)));
			InetAddress host = InetAddress.getByName("128.110.219.14");
			echo("Address: " + sock.getLocalSocketAddress() + "Port:");
			echo("Welcome to Tweeter! Here's a list of commands:");
			echo("R: Registers a new user");
			echo("F: to follow another user");
			echo("D: to drop another user");
			echo("T: to tweet");
			echo("Q: to quit");

			user.start();
			
			while(isConnected)
			{
				//take input and send the packet
				s = (String)cin.readLine();

				
				if(s != "" && s.substring(0,1).equals("R")) {
					
					if(user.handle == null) {
					user.handle = s.substring(2);
					user.setIpv4Address(host);
					user.setPortNumberForServer(port);
					s += " " + Integer.toString(user.getPortNumberForLeftPort());
					}
					else {
						echo("You have already created a user!");
					}
				}
				
				b = s.getBytes();
				
				dp = new DatagramPacket(b , b.length , host , port);
				sock.send(dp);
				
				
				if(s.equals("whoami") && user.handle != null) {
					if(user.handle != null)
					{
						System.out.println("You are: " + user.handle);
					}
					else {
						System.out.println("You have not created a user");
					}
				}
				
				if(s.substring(0,1).equals("E")) {
					isConnected = false;
					sock.close();
					echo("Client Closed");
				}
				
				
				buffer = new byte[65536];
				 reply = new DatagramPacket(buffer, buffer.length);
				
				if(isConnected) {
					sock.receive(reply);
					data = reply.getData();
					s = new String(data, 0, reply.getLength());
			
					echo(s);
				}
			}
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
	public String getTweet() {
		return tweet;
	}
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
}

