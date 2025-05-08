
# 🏝️ Treasure Island - Paxos Implementation

This project implements a distributed multiplayer Treasure Island game using the Paxos consensus algorithm for message ordering and failure tolerance.

---

### 📦 Project Structure

```
Paxos/
├── src/
│   ├── paxos/                  # Paxos logic (Proposer, Acceptor, etc.)
│   ├── tiapp/                  # Interactive + auto player apps
│   └── ...                     # Supporting classes (commands, utils)
├── comp512p2.jar              # Provided framework (GCL, TI, FailCheck)
├── build.xml                  # Ant build and run setup
├── run_sim.sh                 # Runs 1 interactive + N bots locally
└── README.md                  # You're here
```

---

### 🚀 Getting Started

#### 🔧 Requirements

* Java 21+ (use `--release 21` in build)
* Ant (for building/running)
* `comp512p2.jar` in the project root

---

#### 🛠️ Build the project

```bash
ant compile
```

---

#### 👤 Run a single player

```bash
ant run
```

This launches the interactive version (`TreasureIslandApp`) with hardcoded arguments inside `build.xml`.

---

#### 🤖 Simulate multiplayer locally

```bash
./run_sim.sh
```

This:

* Launches bots (using `TreasureIslandAppAuto`) on different ports
* Starts the interactive player (you) as player 1
* Bots send moves at random or interval-based timing

You can adjust:

```bash
MAXMOVES=0     # 0 = infinite moves
INTERVAL=500   # ms between moves
```

Inside `run_sim.sh`.

---

### 🧠 Paxos Interface

The app uses a custom Paxos implementation:

```java
Paxos paxos = new Paxos(myAddr, groupAddrs[], logger, failCheck);
```

Public interface:

```java
paxos.broadcastTOMsg(Object val);
Object paxos.acceptTOMsg();
paxos.shutdownPaxos();
```

---

### ⚙️ Auto Player Modes

You can launch a bot manually:

```bash
java -cp comp512p2.jar:build tiapp.TreasureIslandAppAuto \
  192.168.1.66:4002 \
  192.168.1.66:4001,192.168.1.66:4002,192.168.1.66:4003 \
  game42 3 2 0 500 seed2
```

Arguments:

```
<myaddr> <group> <gameid> <numplayers> <playernum> <maxmoves> <interval> <randseed> [FAILMODE]
```

---

### 🪵 Logging

* Logs are written to `logs/<gameid>-<host.port>-<player>.log`
* To toggle display from bots, set `UPDATEDISPLAY=true` in the environment

---

### 💥 Failure Simulation

You can trigger failures in the interactive or auto app using commands like:

```
FI   → Fail immediately
FRP  → Fail on receiving propose
FSV  → Fail after sending vote
FSP  → Fail after sending propose
FOL  → Fail after becoming leader
FMV  → Fail after value accepted
```

The system must handle  **real-world failure conditions** , like:

* A leader crashing
* A participant dropping offline mid-vote
* A message being dropped or duplicated

Failure simulation is built into the app to test how robust the Paxos implementation is under stress.

---

### 👥 Multiplayer

To run on multiple machines:

* Set each player’s `myProcess` to their real IP:PORT
* Ensure all machines use the same `<group>` list
* Avoid using `127.0.0.1` — Paxos/GCL will reject it

---

### 📋 TODO / Ideas

* Smarter bot strategies (greedy, pathfinding)
* Dynamic game configuration (JSON/TOML)
* Web-based UI overlay
* Game replay recorder
