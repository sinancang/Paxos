package comp512st.paxos.commands;

import java.io.Serializable;

public interface Command extends Serializable {
    boolean isProposerCommand();
    int getBid();
}
