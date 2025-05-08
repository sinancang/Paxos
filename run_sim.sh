#!/bin/bash

# --- Game config ---
GAMEID="simtest"
HOST="192.168.1.66"
NUMPLAYERS=3
INTERVAL=500    # in ms
MAXMOVES=100000000      # 0 means no limit

ADDR1="$HOST:4001"
ADDR2="$HOST:4002"
ADDR3="$HOST:4003"
GROUP="$ADDR1,$ADDR2,$ADDR3"

# --- Compile app + bots ---
ant compile || exit 1

# --- Launch auto players in background ---
echo "[*] Launching auto players..."

ant compile || exit 1

java -cp comp512p2.jar:build tiapp.TreasureIslandAppAuto $ADDR2 $GROUP $GAMEID $NUMPLAYERS 2 $MAXMOVES $INTERVAL seed2 &
BOT2_PID=$!

java -cp comp512p2.jar:build tiapp.TreasureIslandAppAuto $ADDR3 $GROUP $GAMEID $NUMPLAYERS 3 $MAXMOVES $INTERVAL seed3 &
BOT3_PID=$!

# --- Start interactive player ---
echo "[*] Starting interactive player (Player 1)..."
ant run -Dgameid=$GAMEID

# --- Optional: clean up bots after you quit ---
kill $BOT2_PID $BOT3_PID 2>/dev/null
