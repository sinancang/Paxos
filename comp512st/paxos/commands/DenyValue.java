package comp512st.paxos.commands;

import java.io.Serializable;

public record DenyValue(int bid) implements Serializable {
}
