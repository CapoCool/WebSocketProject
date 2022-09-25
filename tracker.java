package socketProject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Tracker 
{
	private Hashtable<String, User> users = new Hashtable<String, User>();
	private Socket socket = null;
	private final int serverPortNumber = 1234; //made it this to make it easier to remember
	private ServerSocket server = null;
	private DataInputStream in = null;
	
	//maintains the list of followers
	private Hashtable<String, List<User>> followers = new Hashtable<String, List<User>>();
	
	
	public String Register(String handle, InetAddress ip4Address, int portNumber) 
	{
		String message = "";
		User tempUser = new User();
		
		if(!users.containsKey(handle) && handle.length() <= 15)
		{
			tempUser.setHandle(handle);
			tempUser.setIpv4Address(ip4Address);
			tempUser.setPortNumberForServer(portNumber);
			users.put(tempUser.getHandle(), tempUser);
			
			message = "SUCCESS";
		}
		else {
			message = "FAILURE";
		}
		
		return message;
	}
	
	
	public String Query() 
	{
		User userToGrab = null;
		Set<String> setOfKeys = users.keySet();
		String res = "";
		
		for(String key : setOfKeys) {
			
			userToGrab = users.get(key);
			
			res += users.get(key).getHandle() + "\n";
			
			
		}
		
		return res;
		
		
	}
	
	public String Follow(String handle, String personToFollow) {
		
		String message = "";
		User tempUser = new User();
		List<User> tempListOfUsers;
		
		if(users.containsKey(handle) && users.containsKey(personToFollow))
		{
			
			tempUser = users.get(handle);
			tempListOfUsers = followers.get(personToFollow);
			
			if(tempListOfUsers == null) {
				
				tempListOfUsers = new ArrayList<User>();
				tempListOfUsers.add(tempUser);
				followers.put(personToFollow, tempListOfUsers);
				
			}
			else {
				tempListOfUsers.add(tempUser);
				followers.replace(personToFollow, tempListOfUsers);
			}
				
			message = "SUCCESS";
				
			
		}
		else {
			message = "FAILURE";
		}
		
		return message;
		
	}
	
	public String Drop(String handle, String personToDrop) {
		
		String message = "";
		User tempUser = new User();
		List<User> tempListOfUsers;
		
		//means both persons exists
		if(users.containsKey(handle) && users.containsKey(personToDrop) && followers.containsKey(personToDrop))
		{
			
			tempUser = users.get(handle);
			tempListOfUsers = followers.get(personToDrop);
			
			if(tempListOfUsers.contains(tempUser)) {
				tempListOfUsers.remove(tempUser);
				followers.replace(personToDrop, tempListOfUsers);
				message = "SUCCESS";
			}
			else {
				message = "Sorry, you don't follow this person";
			}			
		}
		else {
			message = "Either this person doesn't exist, you have not registered, or this person has no followers yet";
		}
		
		return message;
	}
	
	public void Tweet(String handle) {
		
	}
	
	public void EndTweet(String handle) {
		
	}
	
	public void Exit(String handle) {
		
		
	}
	
	public User getUser(int portNumber) {
		
		User userToGrab = null;
		Set<String> setOfKeys = users.keySet();
		
		for(String key : setOfKeys) {
			
			userToGrab = users.get(key);
			
			if(users.get(key).getPortNumberForServer() == portNumber) {
				userToGrab = users.get(key);
			}
			
		}
		
		return userToGrab;
	}
	
	public static void main(String args[])
	{
		DatagramSocket sock = null;
		Tracker tracker = new Tracker();
		DatagramPacket dp;
		
		try
		{
			String s = "";
			//1. creating a server socket, parameter is local port number
			sock = new DatagramSocket(7777);

			
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];
			//DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
			
			//2. Wait for an incoming data
			System.out.println("Server socket created. Waiting for incoming data...");
			//communication loop
			while(!s.equals("Quit"))
			{
				DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
				sock.receive(incoming);
				byte[] data = incoming.getData();
				s = new String(data, 0, incoming.getLength());
				
				if(s.equals("R")) {
					
					String reply = "Enter a handle:";
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
					sock.receive(incoming);
					data = incoming.getData();
					s = new String(data, 0, incoming.getLength());
					
					reply = tracker.Register(s, incoming.getAddress(), incoming.getPort());
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
				}
				
				if(s.equals("F")) {
					String reply = "Enter the user you wish to follow:";
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
					sock.receive(incoming);
					data = incoming.getData();
					s = new String(data, 0, incoming.getLength());
					
					reply = tracker.Follow(s, tracker.getUser(incoming.getPort()).getHandle());
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
				}
				
				if(s.equals("D")) {
					String reply = "Enter the user you wish to drop:";
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
					sock.receive(incoming);
					data = incoming.getData();
					s = new String(data, 0, incoming.getLength());
					
					reply = tracker.Drop(s, tracker.getUser(incoming.getPort()).getHandle());
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
					
				}
				
				if(s.equals("T")) {
					
				}
				
				if(s.equals("Q")) {
					
					String reply = "Here's a list of current users!";
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
					reply = tracker.Query();
					dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
					sock.send(dp);
				}
				
				//echo the details of incoming data - client ip : client port - client message
				//System.out.println(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
				
				//s = "OK : " + s;
				//DatagramPacket dp = new DatagramPacket(s.getBytes() , s.getBytes().length , incoming.getAddress() , incoming.getPort());
				//sock.send(dp);
			}
			
			sock.close();
		}
		
		catch(IOException e)
		{
			System.err.println("IOException " + e);
		}
	}
}
