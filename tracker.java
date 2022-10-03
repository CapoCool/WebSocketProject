package socketProject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
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
		
		//makes sure we aren't trying to place multiple values
		if(!users.containsKey(handle) && handle.length() <= 15)
		{
			//set the new user
			tempUser.setHandle(handle);
			tempUser.setIpv4Address(ip4Address);
			tempUser.setPortNumberForServer(portNumber);
			
			//adds then to the list that maintains the user
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
		
		//This just formulates a reply of the list of users
		for(String key : setOfKeys) {
			
			userToGrab = users.get(key);
			
			res += users.get(key).getHandle() + "\n";
			
			
		}
		
		return res;
		
		
	}
	
	public String Follow(String handle, String personToFollow) {
		
		/* Users are maintained as follows: 3 users, dp, rj, gh
		 * dp and rj decide to follow gh then they they are in the list as
		 * The key as the followee and then a list of followers
		 * gh: dp -> rj
		 */
		
		String message = "";
		User tempUser = new User();
		List<User> tempListOfUsers;
		
		//checks to see if both lists have the users
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
		
		/* Users are maintained as follows: 3 users, dp, rj, gh
		 * dp and rj decide to follow gh then they they are in the list as
		 * The key as the followee and then a list of followers
		 * gh: dp -> rj
		 */
		
		String message = "";
		User tempUser = new User();
		List<User> tempListOfUsers;
		
		//means both persons exists
		if(users.containsKey(handle) && users.containsKey(personToDrop) && followers.containsKey(personToDrop))
		{
			//grab the follower
			tempUser = users.get(handle);
			
			//grab the list of followers for the followee
			tempListOfUsers = followers.get(personToDrop);
			
			//if the list has the user, then we remove them from the list
			// and replace the current list of followers
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
	
	public String Exit(String handle) {
		
		// on exit we need to remove the user from everything
		User temp = new User();
		String res = "Failure";
		List<User> tempList = new ArrayList<User>();
		System.out.println(users.contains(handle));
		if(users.containsKey(handle)) {
			
			//grab the user
			temp = users.get(handle);
			
			//this is so we can loop over the list of followers
			Set<String> setOfKeys = followers.keySet();
			
			for(String userKey : setOfKeys) {
				
				//pull each list in the hash table and then
				//remove the user from the list if they are in the list
				tempList = followers.get(userKey);
				if(tempList.contains(temp)) {
					tempList.remove(temp);
					followers.replace(userKey, tempList);
				}
			}
			
			//If the user has followers we remove that too
			if(followers.containsKey(handle)) {
				followers.remove(handle);
			}
			
			//Then we finally remove the user from the followers
			users.remove(handle);
			
			res = "Success";
		}
		
		return res;
	}
	
	//this is just a helper function to grab a user by the port number
	public User getUser(int portNumber) {
		
		User userToGrab = null;
		Set<String> setOfKeys = users.keySet();
		
		for(String key : setOfKeys) {

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
			sock = new DatagramSocket(null);
			
			sock.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 11000));
			System.out.println(sock.getLocalSocketAddress());
			
			//buffer to receive incoming data
			byte[] buffer = new byte[65536];

			System.out.println("Server socket created. Waiting for incoming data...");
			
			//communication loop
			while(true)
			{
				//create packet and wait for incoming packets
				DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
				sock.receive(incoming);
				byte[] data = incoming.getData();
				s = new String(data, 0, incoming.getLength());
				
				//checks for R in command
				if(s.substring(0,1).equals("R")) {

					reply = tracker.Register(s.substring(2), incoming.getAddress(), incoming.getPort());
					reply += "\nUser: " + s.substring(2) + " IP: " + incoming.getAddress() + " Port: " + incoming.getPort();
				}
				
				//checks for F for command
				else if(s.substring(0,1).equals("F")) {
					
					reply = tracker.Follow(tracker.getUser(incoming.getPort()).getHandle(), s.substring(2));

				}
				
				//checks for D in command
				else if(s.substring(0,1).equals("D")) {
					
					reply = tracker.Drop(tracker.getUser(incoming.getPort()).getHandle(), s.substring(2));
					
				}
				
				//checks for e in command
				else if(s.substring(0,1).equals("E")){
					reply = tracker.Exit(tracker.getUser(incoming.getPort()).getHandle());
				}
				
				//checks for Q in command
				else if(s.equals("Q")) {

					reply = tracker.Query();

				}
				else {
					reply = "Incorrect Command!";
				}
				
				//after formulating the reply, we send it out.
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
