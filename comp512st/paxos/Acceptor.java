package comp512st.paxos;

import comp512.gcl.GCL;
import comp512.utils.FailCheck;
import comp512st.paxos.commands.*;

import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Acceptor implements Runnable{
    Queue<Object> messages;
    Queue<Object> confirmedMessages;

    int maxBid;
    Object value;

    GCL gcl;
    FailCheck failCheck;
    Logger logger;

    int numProcesses;
    String myProcess;

    public Acceptor(Queue<Object> messages, Queue<Object> confirmedMessages, String myProcess, String[] allGroupProcesses,
            Logger logger, FailCheck failCheck, GCL gcl) {
        this.messages = messages;
        this.confirmedMessages = confirmedMessages;

        this.maxBid = -1;
        this.value = null;

        this.failCheck = failCheck;
        this.gcl = gcl;
        this.logger = logger;

        this.numProcesses = allGroupProcesses.length;
        this.myProcess = myProcess;

    }

    @Override
    public void run() {
        while(true){
            while(this.messages.isEmpty()){
                Thread.yield();
            }

            Object message = messages.poll();
            if (!(message instanceof Command)) {
                this.logger.log(Level.WARNING, "Received non-command object at acceptor process " + this.myProcess + "\n" + message);
                continue;
            }
            Command cmd = (Command) message;
            int inBid = cmd.getBid();
            Object response = null;

            switch(message.getClass().getSimpleName()){
                case "Propose":
                    this.logger.log(Level.INFO,"Propose Message Received at Acceptor " + this.myProcess);
                    Propose proposeMsg = (Propose) cmd;

                    if(this.maxBid > inBid){
                        this.logger.log(Level.INFO,"Proposal with bid " + inBid + " refused at Acceptor " + this.myProcess + " with maxBid " + this.maxBid);
                        response = new RefuseProposal(this.maxBid, this.myProcess);
                    }
                    else{
                        this.logger.log(Level.INFO, "Proposal with bid " + inBid + " accepted at Acceptor " + this.myProcess);
                        response = new Promise(inBid, this.maxBid, this.value, this.myProcess);
                    }
                    gcl.sendMsg(response, proposeMsg.getSender());
                    this.maxBid = inBid;
                    break;

                case "AskForAccept":
                    this.logger.log(Level.INFO,"AskForAccept Message Received at Acceptor.");
                    AskForAccept acceptMsg = (AskForAccept) message;
                    if(inBid == this.maxBid){
                        this.value = acceptMsg.value();
                        this.logger.log(Level.FINE,"Value " + this.value + " accepted at Acceptor " + this.myProcess);
                        response = new AcceptAck(this.maxBid, this.myProcess);
                    }
                    else{
                        this.logger.log(Level.INFO,"Value " + this.value + " refused at Acceptor" + this.myProcess);
                        response = new DenyValue(this.maxBid, this.myProcess);
                    }
                    gcl.sendMsg(response, acceptMsg.sender());
                    break;

                case "Confirm":
                    //TODO:deliver message to application layer
                    this.logger.log(Level.INFO,"Confirm Message Received at Acceptor!");
                    this.maxBid = -1;
                    this.value = null;
                    break;
            }

        }
    }
}
