package paxos.commands;

import java.io.Serializable;

public record DenyValue(int bid,String sender) implements Command {
    @Override
    public boolean isProposerCommand() {
        return false;
    }

    @Override
    public int getBid() {
        return bid;
    }

    @Override
    public String getSender() {
        return sender;
    }
}
