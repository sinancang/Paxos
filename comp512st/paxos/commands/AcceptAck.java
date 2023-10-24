package comp512st.paxos.commands;

import java.io.Serializable;

public record AcceptAck(int bid) implements Command {
    @Override
    public boolean isProposerCommand() {
        return false;
    }

    @Override
    public int getBid() {
        return bid;
    }
}
