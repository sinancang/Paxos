package paxos.commands;

import java.io.Serializable;

public interface Command extends Serializable {
    boolean isProposerCommand();
    int getBid();
    String getSender();

}
