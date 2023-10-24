package comp512st.paxos.commands;

public record Promise(int bid, int maxBid, Object value) implements Command {
    @Override
    public boolean isProposerCommand() {
        return false;
    }

    @Override
    public int getBid() {
        return bid;
    }
}
