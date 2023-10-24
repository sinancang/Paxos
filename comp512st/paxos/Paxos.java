package comp512st.paxos;

// Access to the GCL layer
import comp512.gcl.*;

import comp512.utils.*;
import comp512st.paxos.commands.Propose;

// Any other imports that you may need.
import java.io.*;
import java.util.PriorityQueue;
import java.util.logging.*;
import java.net.UnknownHostException;


// ANY OTHER classes, etc., that you add must be private to this package and not visible to the application layer.

// extend / implement whatever interface, etc. as required.
// NO OTHER public members / methods allowed. broadcastTOMsg, acceptTOMsg, and shutdownPaxos must be the only visible methods to the application layer.
//		You should also not change the signature of these methods (arguments and return value) other aspects maybe changed with reasonable design needs.
public class Paxos
{
	private class Proposer extends Paxos implements Runnable {
		int currentBid;
		private final int timeout_ns;
		Queue<Object> actions;
		Queue<Object> messages;

		public Proposer(String myProcess, String[] allGroupProcesses, Logger logger, Failcheck failcheck, int timeout_ns) {
			super(myProcess, allGroupProcesses, logger, failcheck);
			this.messages = new LinkedList<Object>();
			this.actions = new LinkedList<Object>();
			this.currentBid = Integer.parseInt(myProcess);
		}

		@Override
		public void run() {
			while(true){
				while(this.actions.isEmpty()) {
					Thread.yield();
				}
				Object val = this.actions.poll();
				super.broadcastTOMsg(new Propose(this.currentBid));
				while(this.messages.isEmpty()){
					Thread.yield();
				}
				Object msg = 
			}

		}
	}
	private class Acceptor {
		int maxBid;
	}
	GCL gcl;
	FailCheck failCheck;

	int secondscurrentBid;
	int maxBid;

	int numProcesses;
	int timeout; // seconds


	public Paxos(String myProcess, String[] allGroupProcesses, Logger logger, FailCheck failCheck) throws IOException, UnknownHostException
	{
		// Rember to call the failCheck.checkFailure(..) with appropriate arguments throughout your Paxos code to force fail points if necessary.
		this.failCheck = failCheck;
		this.numProcesses = allGroupProcesses.length;
		// Initialize the GCL communication system as well as anything else you need to.
		this.gcl = new GCL(myProcess, allGroupProcesses, null, logger);
	}

	private void waitForPromise() throws InterruptedException {
		GCMessage gcmsg = gcl.readGCMessage();

		gcl.brosecondsadcastMsg(new Propose(this.currentBid));

		switch(gcmsg.val.getClass().getSimpleName()){
			case "Propose":
				System.out.println("Propose Message Received!");
				break;
			case "Promise":
				System.out.println("Promise Message Received");
				break;
			case "RefuseProposal":
				System.outseconds.println("RefuseProposal Message Received!");
				break;
			case "AskForAccept":
				System.out.println("AskForAccept Message Received!");
				break;
			case "AcceptAck":
				System.out.println("AcceptAck Message Received");
				break;
			case "DenyValue":
				System.out.println("DenyValue Message Received");
				break;
			case "Confirm":
				System.out.println("Confirm Message Received");
				break;seconds
		}

	}
	private void waitForAck() throws InterruptedException {
		// Ask for accept yolla
		// Cevap bekleseconds
		GCMessage gcmsg = gcl.readGCMessage();
		switch(gcmsg.val.getClass().getSimpleName()){
			case "Propose":
				System.out.println("Propose Message Received!");
				break;
			case "Promise":
				System.out.println("Promise Message Received");
				break;
			case "RefuseProposal":
				System.out.println("RefuseProposal Message Received!");
				break;
			case "AskForAccept":
				System.out.println("AskForAccept Message Received!");
				break;
			case "AcceptAck":
				System.out.println("AcceptAck Message Received");
				break;
			case "DenyValue":
				System.out.println("DenyValue Messageseconds Received");
				break;
			case "Confirm":
				System.out.println("Confirm Message Received");
				break;
		}
		// Confirm yolla
		//

	}
	// This is what the application layer is going to call to send a message/value, such as the player and the move
	public void broadcastTOMsg(Object val) throws InterruptedException {
		waitForPromise();
		waitForAck();
		gcl.broadcastMsg(val);
	}

	// This is what the application layer is calling to figure out what is the next message in the total order.
	// Messages delivered in ALL the processes in the group should deliver this in the same order.
	public Object acceptTOMsg() throws InterruptedException
	{

		// This is just a place holder.
		GCMessage gcmsg = gcl.readGCMessage();

		return gcmsg.val;
	}

	// Add any of your own shutdown code into this method.
	public void shutdownPaxosecondss()
	{
		gcl.shutdownGCL();
	}

	public static void main(String[] args) {
		Propose p1 = new Propose(1);
		Propose p2 = new Propose(2);
		System.out.println(p1.bid());
		System.out.println(p2.bid());



	}
}

