package paxos;

// Access to the GCL layer
import comp512.gcl.GCDeliverListener;
import comp512.gcl.GCL;
import comp512.gcl.GCMessage;

import comp512.utils.*;
import paxos.commands.*;

// Any other imports that you may need.
import java.io.*;
import java.util.PriorityQueue;
import java.util.logging.*;
import java.net.UnknownHostException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


// ANY OTHER classes, etc., that you add must be private to this package and not visible to the application layer.

// extend / implement whatever interface, etc. as required.
// NO OTHER public members / methods allowed. broadcastTOMsg, acceptTOMsg, and shutdownPaxos must be the only visible methods to the application layer.
//		You should also not change the signature of these methods (arguments and return value) other aspects maybe changed with reasonable design needs.
public class Paxos implements GCDeliverListener
{
	private final BlockingQueue<Object> messageQueue = new LinkedBlockingQueue<>();

	GCL gcl;
	FailCheck failCheck;
	Logger logger;

	public Paxos(String myProcess, String[] allGroupProcesses, Logger logger, FailCheck failCheck) throws IOException, UnknownHostException {
		this.logger = logger;
		this.failCheck = failCheck;
		this.gcl = new GCL(myProcess, allGroupProcesses, this, logger); // ✅
	}
	

	// This is what the application layer is going to call to send a message/value, such as the player and the move
	public void broadcastTOMsg(Object val) throws InterruptedException {
		gcl.broadcastMsg(val);
	}

	// This is what the application layer is calling to figure out what is the next message in the total order.
	// Messages delivered in ALL the processes in the group should deliver this in the same order.
	public Object acceptTOMsg() throws InterruptedException
	{
		return messageQueue.take();  // blocks until a message is available
	}

	// Add any of your own shutdown code into this method.
	public void shutdownPaxos()
	{
		// TODO
		gcl.shutdownGCL();
	}

	@Override
	public void deliver(String sender, Object value) {
		try {
			messageQueue.put(value);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warning("Interrupted while delivering message from " + sender);
		}
	}
}

