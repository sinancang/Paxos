# Paxos

This is an implementation of the Paxos consensus algorithm originally proposed by Leslie Lamport in 1998. Here is a [link to the original paper](https://lamport.azurewebsites.net/pubs/lamport-paxos.pdf). We use this algorithm to maintain total order of actions of up to 10 independent actors in a "Treasure Island" console game.

Details on the implementation can be found in `Project Report.pdf`. Further, some performance metrics are provided in `src/analysis/performance_analysis.ipynb`.

Disclaimer: files under `src/tests` and `src/tiapp`, as well as `build_tiapp.sh` and `comp512p2.jar` do not belong to us, they belong to the instructor of the course Professor Bettina Kemme. Anything else in the repository has been written by and belongs to the owners.

## Objective

Given n client processes `{p1, ..., pn}`, each process maintains two lists of values: one is a list which contains the actions that have been accepted by the group `Ai = {a1, a2, ..., am}`, and the other is the list of actions received from the application layer (player actions) `Bi = {b1, b2, ..., bk}`. The goal of this algorithm is to ensure that the list `Ai` is non-trivially consistent among all processes, and that the players make progress (`Bi` gets smaller at each timestep if no new actions are received).

## Short Description of Algorithm

At each round, processes propose to be a leader by multicasting their `bid` or ballot id. A recipient sends back a promise (potentially containing previously accepted values) if the proposal has the highest `bid` it has seen so far. When a process receives a majority, it becomes a leader and sends out an `Accept?` message containing its proposed action. Upon receiving a majority of accept acknowledgements, the process can safely deliver the action to the application layer.

A lot of the details are omitted, they may be found in the original paper or online.
