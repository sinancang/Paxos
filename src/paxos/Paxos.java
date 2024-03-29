package comp512st.paxos;

// Access to the GCL layer
import comp512.gcl.*;

import comp512.utils.*;
import comp512st.paxos.commands.*;

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
	GCL gcl;
	FailCheck failCheck;
	Logger logger;

	public Paxos(String myProcess, String[] allGroupProcesses, Logger logger, FailCheck failCheck) throws IOException, UnknownHostException
	{
		// TODO
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
}

