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
		String reply = "";
		
		try
		{
			String s = "";
			//1. creating a server socket, parameter is local port number
			sock = new DatagramSocket(7777);

			
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];

			
			//2. Wait for an incoming data
			System.out.println("Server socket created. Waiting for incoming data...");
			
			//communication loop
			while(true)
			{
				DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
				sock.receive(incoming);
				byte[] data = incoming.getData();
				s = new String(data, 0, incoming.getLength());
				
				if(s.substring(0,1).equals("R")) {

					reply = tracker.Register(s.substring(2), incoming.getAddress(), incoming.getPort());
				}
				
				if(s.substring(0,1).equals("F")) {
					
					reply = tracker.Follow(tracker.getUser(incoming.getPort()).getHandle(), s.substring(2));

				}
		
				if(s.substring(0,1).equals("D")) {
					
					reply = tracker.Drop(s.substring(2), tracker.getUser(incoming.getPort()).getHandle());
					
				}
			
				if(s.equals("Q")) {

					reply = tracker.Query();

				}

				dp = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
				sock.send(dp);
			}
		}
		
		catch(IOException e)
		{
			sock.close();
			System.err.println("IOException " + e);
		}
	}
}
