package paxos.commands;

public record Promise(int bid, int maxBid, Object value, String sender) implements Command {
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
