package comp512st.paxos;

import comp512st.paxos.commands.*;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.LinkedList;
import java.util.Queue;

public class Proposer implements Runnable {
    String myProcess;
    int numProcesses;

    Queue<Object> actions;
    Queue<Object> messages;
    Queue<Object> values;

    long timeout_ms;
    Failcheck failcheck;
    Gcl gcl;
    Logger logger;

    Object curVal;
    int currentBid;

    /**
     * @param gcl
     * @param actions: A queue used to deliver actions from AL to Proposer
     * @param messages: A queue used to deliver messages from msgReader to Proposer
     * @param myProcess: ID of current process (same with acceptor)
     * @param logger
     * @param failcheck
     * @param timeout_ms: Timeout for proposer in ms
     * @param numProcesses: Number of paxos instances in TI game
     */
    public Proposer(Gcl gcl, Queue<Object> actions, Queue<Object> messages, String myProcess,
            Logger logger, Failcheck failcheck, long timeout_ms, int numProcesses) {
        this.actions = actions;
        this.messages = messages;
        this.values = new LinkedList<Object>();

        this.myProcess = myProcess;
        this.numProcesses = numProcesses;

        this.timeout_ms = timeout_ms;
        this.failcheck = failcheck;
        this.gcl = gcl;
        this.logger = logger;

        this.currentBid = Integer.parseInt(myProcess);
        this.curVal = null;
    }

    @Override
    public void run() {
        this.logger.log(Level.INFO, "Running Proposer thread in process " + this.myProcess);
        while(true){
            while(this.actions.isEmpty()) {
                Thread.yield();
            }
            Object val = this.actions.poll();
            this.logger.log(Level.INFO, "Got value " + val + " from AL");
            this.currentBid = Integer.parseInt(this.myProcess);
            this.suggest(val);
        }
    }

    /**
     * @param val
     * Suggest value val as proposer.
     * Important: update bid before calling.
     */
    public void suggest(Object val) {
        this.logger.log(Level.INFO, "Suggesting: " + val + " with bid: " + this.currentBid + " and timeout: " + timeout_ms);
        this.curVal = val;
        gcl.broadcastMsg(new Propose(this.currentBid, this.myProcess));
        boolean majority = this.waitForPromise();
        if (!majority) {
            this.currentBid += this.numProcesses;
            this.logger.log(Level.WARNING, "Did not receive majority! Retrying with bid " + this.currentBid);
            suggest(val);
            return;
        }
        this.logger.log(Level.INFO, "Received majority vote.");
        gcl.broadcastMsg(new AskForAccept(this.currentBid, this.curVal, this.myProcess));

        // TODO wait for ack
        waitForAck();

        if (!values.isEmpty()) {
            suggest(values.poll());
        }
    }

    public boolean waitForPromise() {
        this.logger.log(Level.INFO, "Waiting for promises on bid: " + this.currentBid);
        long startTime = System.currentTimeMillis();
        boolean majority = false;
        int refusals = 0;
        int promises = 0;
        int maxPromiseBid = -1;
        Object maxPromiseVal = null;

        while (refusals < Math.ceil(numProcesses/2) && !majority && System.currentTimeMillis() - startTime > timeout_ms) {
            while (this.messages.isEmpty()) {
                Thread.yield();
            }

            Object message = this.messages.poll();
            if (!(message instanceof Command)) {
                this.logger.log(Level.WARNING, "Received non-command object at acceptor process " + this.myProcess + "\n" + message);
                continue;
            }

            Command msg = (Command) message;
            if (msg.getBid() != this.currentBid) {
                this.logger.log(Level.WARNING, "Received message with bid " + msg.getBid() + ", but current bid is " + this.currentBid);
                continue;
            }
            if (msg instanceof RefuseProposal) {
                refusals++;
                continue;
            }
            if (!(msg instanceof Promise)) {
                this.logger.log(Level.WARNING, "Received a message that is not a promise or a refusal at proposer" +
                    this.myProcess + "\nMessage has class " + msg.getClass());
                continue;
            }

            promises++;
            Promise prms = (Promise) msg;
            if (prms.value() != null && prms.getBid() > maxPromiseBid) {
                maxPromiseBid = prms.getBid();
                maxPromiseVal = prms.value();
            }

            if (promises >= Math.ceil(numProcesses/2)) {
                majority = true;
                break;
            }
        }

        if (majority && maxPromiseVal != null) {
            values.add(this.curVal);
            this.curVal = maxPromiseVal;
        }
        return majority;
    }

    public boolean waitForAck() {
        // TODO
    }
}
