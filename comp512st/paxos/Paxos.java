package comp512st.paxos;

// Access to the GCL layer
import comp512.gcl.*;

import comp512.utils.*;

// Any other imports that you may need.
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.*;
import java.net.UnknownHostException;


// ANY OTHER classes, etc., that you add must be private to this package and not visible to the application layer.

// extend / implement whatever interface, etc. as required.
// NO OTHER public members / methods allowed. broadcastTOMsg, acceptTOMsg, and shutdownPaxos must be the only visible methods to the application layer.
//		You should also not change the signature of these methods (arguments and return value) other aspects maybe changed with reasonable design needs.
public class Paxos
{
	GCL gcl;
	FailCheck failCheck;
	Logger logger;

	public Paxos(String myProcess, String[] allGroupProcesses, Logger logger, FailCheck failCheck) throws IOException, UnknownHostException
	{
		long timeout_ms = 20;
		this.logger = logger;
		this.failCheck = failCheck;
		GCL gcl = new GCL(myProcess, allGroupProcesses, null, logger);

		Queue<Object> ProposerMessageQueue = new LinkedList<>();
		Queue<Object> AcceptorMessageQueue = new LinkedList<>();
		MessageReader messageReader = new MessageReader(gcl, ProposerMessageQueue, AcceptorMessageQueue);

		Queue<Object> ProposerActionQueue = new LinkedList<>();
		Proposer proposer = new Proposer(gcl, ProposerActionQueue, ProposerMessageQueue, myProcess, logger,
				failCheck, timeout_ms, allGroupProcesses.length);
		// Acceptor acceptor = new Acceptor(gcl);
	}

	// This is what the application layer is going to call to send a message/value, such as the player and the move
	public void broadcastTOMsg(Object val) throws InterruptedException {
		// TODO
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
	public void shutdownPaxos()
	{
		// TODO
		gcl.shutdownGCL();
	}
	public static void main(String[] args) {
		class mutableTester {
			Queue<Integer> seqnums;
			mutableTester(Queue<Integer> seqnums) {
				this.seqnums = seqnums;
			}
			public int getFirstElement() {
				return seqnums.poll();
			}
		}
		Queue<Integer> messages = new LinkedList<>();
		mutableTester test = new mutableTester(messages);
		messages.add(6);
		messages.add(156);
		System.out.println(test.getFirstElement());
	}
}

