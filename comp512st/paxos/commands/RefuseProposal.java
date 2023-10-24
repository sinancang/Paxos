package comp512st.paxos.commands;

public record RefuseProposal(int maxBid) implements Command{
    @Override
    public boolean isProposerCommand() {
        return false;
    }

    @Override
    public int getBid() {
        return maxBid;
    }
}
