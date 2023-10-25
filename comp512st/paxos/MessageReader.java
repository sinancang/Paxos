package comp512st.paxos;

import comp512.gcl.*;
import comp512st.paxos.commands.Command;

import java.util.Queue;

public class MessageReader implements Runnable{
    GCL gcl;
    Queue<Object> ProposerMessageQueue;
    Queue<Object> AcceptorMessageQueue;

    public MessageReader(GCL gcl, Queue<Object> ProposerMessageQueue, Queue<Object> AcceptorMessageQueue) {
        this.gcl = gcl;
        this.ProposerMessageQueue = ProposerMessageQueue;
        this.AcceptorMessageQueue = AcceptorMessageQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object msg = this.gcl.readGCMessage();
                assert msg instanceof Command;
                Command cmd = (Command) msg;
                if (cmd.isProposerCommand()) {
                    ProposerMessageQueue.add(cmd);
                } else {
                    AcceptorMessageQueue.add(cmd);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
