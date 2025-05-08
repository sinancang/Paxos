package paxos.commands;

public record Propose(int bid, String sender) implements Command {
    @Override
    public boolean isProposerCommand() {
        return true;
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
