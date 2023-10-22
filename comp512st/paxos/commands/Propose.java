package comp512st.paxos.commands;

import java.io.Serializable;

public record Propose(int bid) implements Serializable {
}
