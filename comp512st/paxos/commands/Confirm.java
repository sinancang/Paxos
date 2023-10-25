package comp512st.paxos.commands;

public record Confirm(int bid, Object value, String sender) implements Command{
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
