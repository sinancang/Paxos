package comp512st.paxos;

import comp512.gcl.GCL;
import comp512.utils.FailCheck;
import comp512st.paxos.commands.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Acceptor implements Runnable{
    int maxBid;
    Object value;
    Queue<Object> messages;

    GCL gcl;
    FailCheck failCheck;

    int currentBid;

    int numProcesses;
    int timeout; // seconds
    Logger logger;
    String myProcess;
    public Acceptor(Queue<Object> messages, String myProcess, String[] allGroupProcesses, Logger logger, FailCheck failCheck, GCL gcl) throws IOException, UnknownHostException {
        this.failCheck = failCheck;
        this.currentBid = 0;
        this.numProcesses = allGroupProcesses.length;
        // Initialize the GCL communication system as well as anything else you need to.
        this.gcl = gcl;
        this.logger = logger;
        this.myProcess = myProcess;
        this.messages = new LinkedList<Object>();

    }
    @Override
    public void run() {
        //wait for a message
        while(true){
            while(this.messages.isEmpty()){
                Thread.yield();
            }
            Object message = messages.poll();

            //declare variables commonly used
            int inBid;
            Object response;
            switch(message.getClass().getSimpleName()){
                case "Propose":
                    this.logger.log(Level.INFO,"Propose Message Received at Acceptor!");
                    Propose proposeMsg = (Propose) message;
                    inBid = proposeMsg.getBid();

                    if(this.maxBid >= inBid){
                        this.logger.log(Level.INFO,"Proposal refused at Acceptor" + this.myProcess);
                        response = new RefuseProposal(this.maxBid, this.myProcess);
                    }
                    else{
                        this.logger.log(Level.FINE,"Proposal accepted at Acceptor " + this.myProcess);
                        response = new Promise(inBid, maxBid, value, this.myProcess);
                    }
                    gcl.sendMsg(response, proposeMsg.getSender());
                    this.maxBid = inBid;
                    break;
                case "AskForAccept":
                    this.logger.log(Level.INFO,"AskForAccept Message Received at Acceptor!");
                    AskForAccept acceptMsg = (AskForAccept) message;
                    inBid = acceptMsg.getBid();
                    if(inBid == this.maxBid){
                        this.value = acceptMsg.value();
                        this.logger.log(Level.FINE,"Value accepted at Acceptor " + this.myProcess);
                        response = new AcceptAck(this.maxBid,this.myProcess);
                    }
                    else{
                        this.logger.log(Level.INFO,"Value refused at Acceptor" + this.myProcess);
                        response = new DenyValue(this.maxBid, this.myProcess);
                    }
                    gcl.sendMsg(response, acceptMsg.sender());
                    break;
                case "Confirm":
                    //TODO:deliver message to application layer
                    this.logger.log(Level.INFO,"Confirm Message Received at Acceptor!");
                    break;
            }

        }
    }
}