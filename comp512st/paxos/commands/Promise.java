package comp512st.paxos.commands;

public record Promise(int bid, int maxBid, Object value) {
}
