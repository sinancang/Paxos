package comp512st.paxos.commands;

public record AskForAccept(int bid, Object value) implements Command{
    @Override
    public boolean isProposerCommand() {
        return true;
    }

    @Override
    public int getBid() {
        return bid;
    }
}
