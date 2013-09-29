package ch.bfh.evoting.votinglib;

import java.io.Serializable;

public class VoteMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Type messageType;
	
	public VoteMessage(){

	}
	
	public Type getMessageType() {
		return messageType;
	}
	
	public String getSenderIPAddress () {
		return null;
		
	}
	
	public String getSender(){
		return null;
		
	}
	
	public long getTimestamp(){
		return 0;
		
	}
	
	public enum Type {
		TYPE1,
		TYPE2;
	}
}