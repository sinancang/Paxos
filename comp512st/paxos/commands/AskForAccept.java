package comp512st.paxos.commands;

public record AskForAccept(int bid, Object value) {
}
