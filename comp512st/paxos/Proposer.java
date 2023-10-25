package comp512st.paxos;

import java.util.LinkedList;

protected class Proposer implements Runnable {
    String myProcess;
    int numProcesses;

    Queue<Object> actions;
    Queue<Object> messages;
    Queue<Object> values;

    int timeout_ms;
    Failcheck failcheck;
    Gcl gcl;

    Object curVal;
    int currentBid;

    public Proposer(Gcl gcl, Queue<Object> actions, Queue<Object> messages, String myProcess, Logger logger, Failcheck failcheck, int timeout_ms, int numProcesses) {
        this.actions = actions;
        this.messages = messages;
        this.values = new LinkedList<Object>();

        this.myProcess = myProcess;
        this.numProcesses = numProcesses;

        this.timeout_ms = timeout_ms;
        this.failcheck = failcheck;
        this.gcl = gcl;

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

    public void suggest(Object val) {
        this.logger.log(Level.INFO, "Suggesting: " + val + " with bid: " + this.currentBid + " and timeout: " + timeout_ms);
        this.curVal = val;
        gcl.broadcastMsg(new Propose(this.currentBid, this.myProcess));
        boolean majority = this.waitForPromise(val);
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
        int startTime = System.currentTimeMillis();
        boolean majority = false;
        int refusals = 0;
        int promises = 0;
        int maxPromiseBid = -1;
        Object maxPromiseVal = null;

        while (refusals < Math.floor(numProcesses/2) && !majority && System.currentTimeMillis() - startTime > timeout_ms) {
            while (this.messages.isEmpty()) {
                Command msg = this.messages.poll();
                if (msg.getBid() != this.currentBid) {
                    this.logger.log(Level.WARNING, "Received message with bid " + msg.getBid() + ", but current bid is " + this.currentBid);
                    continue;
                }
                if (msg instanceof RefuseProposal) {
                    refusals++;
                    continue;
                }
                if (!(msg instanceof Promise)) {
                    this.logger.log(Level.WARNING, "Received non-promise message with class " + msg.getClass());
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
        }

        if (majority && maxPromiseVal != null) {
            values.add(this.curVal);
            this.curVal = maxPromiseVal;
        }
        return false;
    }

    public boolean waitForAck() {
        // TODO
    }
}
