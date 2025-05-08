package paxos.commands;

public record RefuseProposal(int maxBid, String sender) implements Command{
    @Override
    public boolean isProposerCommand() {
        return false;
    }

    @Override
    public int getBid() {
        return maxBid;
    }

    @Override
    public String getSender() {
        return sender;
    }
}
