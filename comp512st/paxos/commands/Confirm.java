package comp512st.paxos.commands;

public record Confirm(int bid) implements Command{
    @Override
    public boolean isProposerCommand() {
        return true;
    }

    @Override
    public int getBid() {
        return bid;
    }
}
