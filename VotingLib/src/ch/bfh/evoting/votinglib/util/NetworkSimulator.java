package ch.bfh.evoting.votinglib.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.bfh.evoting.instacirclelib.Message;
import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class NetworkSimulator {

	private Context ctx;
	private SerializationUtil su;

	private int excludeParticipant = 0;

	public NetworkSimulator(Context ctx){
		this.ctx = ctx;

		su = AndroidApplication.getInstance().getSerializationUtil();

		String packageName = ctx.getPackageName();
		if(packageName.equals("ch.bfh.evoting.voterapp")){
			simulateAdmin();
			simulateVoter();
		} else if (packageName.equals("ch.bfh.evoting.adminapp")){
			simulateVoter();
		}
	}


	private void simulateVoter(){
		//Simulate votes arrive each second
		new Thread(){

			@Override
			public void run() {
				int i=0;
				while(true){
					SystemClock.sleep(1000);
					//creation of sender ip
					int random = (int)(Math.random() * (150-0)) + 0;
					i=(i+1)%10;
					String senderAddress = "192.168.1."+i;
					//creation of vote message
					VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_VOTE, ""+random, senderAddress, 0);
					String serializedVotingMessage = su.serialize(vm);

					//creation of network message
					Message m = new Message(serializedVotingMessage, Message.MSG_CONTENT, senderAddress, 0);

					//simulate the entry of the created message
					Intent intent = new Intent("messageArrived");
					intent.putExtra("message", m);
					LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
				}	
			}
		}.start();

		//Simulate update in participants
		new Thread(){

			@Override
			public void run() {
				while(true){
					SystemClock.sleep(6000);
					//choose a participant to exclude temporarily
					excludeParticipant = (int)(Math.random() * (15-1)) + 1;
					Log.w("NetworkSimulator", "Participant "+excludeParticipant+" was excluded this time!");
					//simulate the entry of the created message
					Intent intent = new Intent(BroadcastIntentTypes.participantStateUpdate);
					LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
				}	
			}
		}.start();
	}

	private void simulateAdmin(){
		//Simulate incoming of a message containing the electorate
		new Thread(){

			@Override
			public void run() {
				while(true){
					SystemClock.sleep(10000);
					//creation of sender ip
					String senderAddress = "192.168.1.1";

					//creation of vote message
					Serializable content = (Serializable)NetworkSimulator.this.createDummyParticipants();
					VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_ELECTORATE, content, senderAddress, 0);
					String serializedVotingMessage = su.serialize(vm);

					//creation of network message
					Message m = new Message(serializedVotingMessage, Message.MSG_CONTENT, senderAddress, 0);

					//simulate the entry of the created message
					Intent intent = new Intent("messageArrived");
					intent.putExtra("message", m);
					LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

				}	
			}
		}.start();

		//Simulate incoming of a message containing the poll
		new Thread(){

			@Override
			public void run() {
				while(true){
					SystemClock.sleep(15000);
					//creation of sender ip
					String senderAddress = "192.168.1.1";

					//creation of vote message
					Serializable content = NetworkSimulator.this.createDummyPoll();
					VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_POLL_TO_REVIEW, content, senderAddress, 0);
					String serializedVotingMessage = su.serialize(vm);

					//creation of network message
					Message m = new Message(serializedVotingMessage, Message.MSG_CONTENT, senderAddress, 0);

					//simulate the entry of the created message
					Intent intent = new Intent("messageArrived");
					intent.putExtra("message", m);
					LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

				}	
			}
		}.start();

		//Simulate incoming of a message with start poll order
		new Thread(){

			@Override
			public void run() {
				while(true){
					SystemClock.sleep(15000);
					//creation of sender ip
					String senderAddress = "192.168.1.1";

					//creation of vote message
					String content = "START POLL";
					VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_START_POLL, content, senderAddress, 0);
					String serializedVotingMessage = su.serialize(vm);

					//creation of network message
					Message m = new Message(serializedVotingMessage, Message.MSG_CONTENT, senderAddress, 0);

					//simulate the entry of the created message
					Intent intent = new Intent("messageArrived");
					intent.putExtra("message", m);
					LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

				}	
			}
		}.start();

		//Simulate incoming of a message with stop poll order
		new Thread(){

			@Override
			public void run() {
				while(true){
					SystemClock.sleep(15000);
					//creation of sender ip
					String senderAddress = "192.168.1.1";

					//creation of vote message
					String content = "STOP POLL";
					VoteMessage vm = new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_STOP_POLL, content, senderAddress, 0);
					String serializedVotingMessage = su.serialize(vm);

					//creation of network message
					Message m = new Message(serializedVotingMessage, Message.MSG_CONTENT, senderAddress, 0);

					//simulate the entry of the created message
					Intent intent = new Intent("messageArrived");
					intent.putExtra("message", m);
					LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

				}	
			}
		}.start();
	}


	public Map<String,Participant> createDummyParticipants(){
		List<Participant> participants = new ArrayList<Participant>();
		if(excludeParticipant != 1){
			Participant p1 = new Participant("Administrator 1 with very very very very very very very very very very very very long name", "192.168.1.1", false, false);
			participants.add(p1);
		}
		if(excludeParticipant != 2){
			Participant p2 = new Participant("Participant 2 with very very very very very very very very very very very very long name", "192.168.1.2", false, false);
			participants.add(p2);
		}
		if(excludeParticipant != 3){
			Participant p3 = new Participant("Participant 3 with very very very very very very very very very very very very long name", "192.168.1.3", false, false);
			participants.add(p3);
		}
		if(excludeParticipant != 4){
			Participant p4 = new Participant("Participant 4 with very very very very very very very very very very very very long name", "192.168.1.4", false, false);
			participants.add(p4);
		}
		if(excludeParticipant != 5){
			Participant p5 = new Participant("Participant 5 with very very very very very very very very very very very very long name", "192.168.1.5", false, false);
			participants.add(p5);
		}
		if(excludeParticipant != 6){
			Participant p6 = new Participant("Participant 6 with very very very very very very very very very very very very long name", "192.168.1.6", false, false);
			participants.add(p6);
		}
		if(excludeParticipant != 7){
			Participant p7 = new Participant("Participant 7 with very very very very very very very very very very very very long name", "192.168.1.7", false, false);
			participants.add(p7);
		}
		if(excludeParticipant != 8){
			Participant p8 = new Participant("Participant 8 with very very very very very very very very very very very very long name", "192.168.1.8", false, false);
			participants.add(p8);
		}
		if(excludeParticipant != 9){
			Participant p9 = new Participant("Participant 9 with very very very very very very very very very very very very long name", "192.168.1.9", false, false);
			participants.add(p9);
		}
		if(excludeParticipant != 10){
			Participant p10 = new Participant("Participant 10 with very very very very very very very very very very very very long name", "192.168.1.10", false, false);
			participants.add(p10);
		}
		if(excludeParticipant != 11){
			Participant p11 = new Participant("Participant 11 with very very very very very very very very very very very very long name", "192.168.1.11", false, false);
			participants.add(p11);
		}
		if(excludeParticipant != 12){
			Participant p12 = new Participant("Participant 12 with very very very very very very very very very very very very long name", "192.168.1.12", false, false);
			participants.add(p12);
		}
		if(excludeParticipant != 13){
			Participant p13 = new Participant("Participant 13 with very very very very very very very very very very very very long name", "192.168.1.13", false, false);
			participants.add(p13);
		}
		if(excludeParticipant != 14){
			Participant p14 = new Participant("Participant 14 with very very very very very very very very very very very very long name", "192.168.1.14", false, false);
			participants.add(p14);
		}
		if(excludeParticipant != 15){
			Participant p15 = new Participant("Participant 15 with very very very very very very very very very very very very long name", "192.168.1.15", false, false);
			participants.add(p15);
		}	

		Map<String,Participant> map = new TreeMap<String,Participant>(new IPAddressComparator());
		for(Participant p:participants){
			map.put(p.getIpAddress(), p);
		}
		return map;
	}

	private Poll createDummyPoll(){

		//Create the option
		String question = "What do you think very very very long question very very very long question very very very long question very very very long question?";
		Option yes = new Option();
		yes.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no = new Option();
		no.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");
		Option yes1 = new Option();
		yes1.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no1 = new Option();
		no1.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");
		Option yes2 = new Option();
		yes2.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no2 = new Option();
		no2.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");
		Option yes3 = new Option();
		yes3.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no3 = new Option();
		no3.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");
		Option yes4 = new Option();
		yes4.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no4 = new Option();
		no4.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");
		Option yes5 = new Option();
		yes5.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no5 = new Option();
		no5.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");
		Option yes6 = new Option();
		yes6.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no6 = new Option();
		no6.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");
		Option yes7 = new Option();
		yes7.setText("Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes Yes");
		Option no7 = new Option();
		no7.setText("No No No No No No No No No No No No No No No No No No No NoNo No No No No No No No No No No No");

		List<Option> options = new ArrayList<Option>();
		options.add(yes);
		options.add(no);
		options.add(yes1);
		options.add(no1);
		options.add(yes2);
		options.add(no2);
		options.add(yes3);
		options.add(no3);
		options.add(yes4);
		options.add(no4);
		options.add(yes5);
		options.add(no5);
		options.add(yes6);
		options.add(no6);
		options.add(yes7);
		options.add(no7);

		Map<String,Participant> participantsMap = this.createDummyParticipants();
		List<Participant> participants = new ArrayList<Participant>(participantsMap.values());
		participants.get(0).setSelected(true);
		participants.get(2).setSelected(true);
		participants.get(4).setSelected(true);
		participants.get(6).setSelected(true);
		participants.get(7).setSelected(true);
		participants.get(9).setSelected(true);
		participants.get(10).setSelected(true);
		participants.get(11).setSelected(true);
		participants.get(13).setSelected(true);
		participants.get(14).setSelected(true);

		Poll poll = new Poll();
		poll.setOptions(options);
		poll.setParticipants(participantsMap);
		poll.setQuestion(question);
		poll.setTerminated(false);

		return poll;
	}
}
