package tiapp;

import comp512.gcl.*;
import comp512.ti.*;
import comp512.utils.*;

import paxos.*;

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;

import java.util.logging.*;

public class TreasureIslandApp implements Runnable {
	TreasureIsland ti;
	private Logger logger;

	Thread tiThread;
	boolean keepExploring;

	Paxos paxos;

	public TreasureIslandApp(Paxos paxos, Logger logger, String gameId, int numPlayers, int yourPlayer) {
		this.paxos = paxos;
		this.logger = logger;
		this.keepExploring = true;
		ti = new TreasureIsland(logger, gameId, numPlayers, yourPlayer);
		tiThread = new Thread(this);
		tiThread.start();
	}

	public void run() {
		while (keepExploring) // TODO: Make sure all the remaining messages are processed in the case of a
								// graceful shutdown.
		{
			try {
				Object[] info = (Object[]) paxos.acceptTOMsg();
				logger.fine("Received :" + Arrays.toString(info));
				move((Integer) info[0], (Character) info[1]);
				displayIsland();
			} catch (InterruptedException ie) {
				if (keepExploring)
					logger.log(Level.SEVERE, "Encountered InterruptedException while waiting for messages.", ie);
				break;
			}
		}
	}

	public void displayIsland() {
		ti.displayIsland();
	}

	public synchronized void move(int playerNum, char direction) {
		move(playerNum, direction, true);
	}

	public synchronized void move(int playerNum, char direction, boolean displayIsland) {
		ti.move(playerNum, direction);
		if (displayIsland)
			ti.displayIsland();
	}

	public static void main(String args[]) throws IOException, InterruptedException {

		if (args.length != 5) {
			System.err.println(
					"Usage: java comp512st.tiapp.TreasureIslandApp processhost:port processhost:port,processhost:port,... gameid numplayers playernum");
			System.exit(1);
		}

		// Setup logger and logging levels.
		System.setProperty("java.util.logging.SimpleFormatter.format",
				"%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS.%1$tN %1$Tp %2$s %4$s: %5$s%6$s%n");
		Logger logger = Logger.getLogger("TreasureIsland");
		logger.setLevel(Level.FINE);

		/*
		 * Handler consoleHandler = new ConsoleHandler();
		 * //logger.setLevel(Level.WARNING);
		 * //logger.setLevel(Level.INFO);
		 * //logger.setLevel(Level.ALL);
		 * consoleHandler.setLevel(Level.FINE);
		 * logger.addHandler(consoleHandler);
		 * logger.setUseParentHandlers(false);
		 */

		// Send logging to a file.
		try {
			new File("logs").mkdirs();
			String logFile = String.format("logs/%s-%s-%s-process.log",
					args[2], args[0].replace(':', '.'), args[4]);
			FileHandler fh = new FileHandler(logFile);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.setUseParentHandlers(false);
		} catch (SecurityException se) {
			throw new RuntimeException("SecurityException while initializing process log file.");
		} catch (IOException ie) {
			throw new RuntimeException("IOException while initializing process log file.");
		}

		logger.info("Started with arguments : " + Arrays.toString(args));

		// For simulating any failure conditions.
		FailCheck failCheck = new FailCheck(logger);

		String gameid = args[2]; // pass a different gameid to get a different island map.
		int numPlayers = Integer.parseInt(args[3]); // total number of players in the game
		int playerNum = Integer.parseInt(args[4]); // your player number / id

		Paxos paxos = new Paxos(args[0], args[1].split(","), logger, failCheck);
		TreasureIslandApp ta = new TreasureIslandApp(paxos, logger, gameid, numPlayers, playerNum);
		ta.displayIsland();

		Scanner sc = new Scanner(System.in);
		System.out.println("""
			Controls:
			  W = up    A = left    S = down    D = right
			  E = exit
			Failure Injection:
			  FI  = fail immediately
			  FRP = fail on receive propose
			  FSV = fail after sending vote
			  FSP = fail after sending propose
			  FOL = fail after becoming leader
			  FMV = fail after value accepted
			""");
		while (true) {
			String cmd = sc.next().trim().toUpperCase();
			logger.fine("cmd is : " + cmd);
			if (cmd.equals("E"))
				break;

			switch (cmd) {
				case "W":
				case "A":
				case "S":
				case "D":
					char direction = switch (cmd) {
						case "W" -> 'U';
						case "A" -> 'L';
						case "S" -> 'D';
						case "D" -> 'R';
						default -> '?'; // will never hit
					};
					paxos.broadcastTOMsg(new Object[] { playerNum, direction });
					break;
				case "FI":
					failCheck.setFailurePoint(FailCheck.FailureType.IMMEDIATE);
					break;
				case "FRP":
					failCheck.setFailurePoint(FailCheck.FailureType.RECEIVEPROPOSE);
					break;
				case "FSV":
					failCheck.setFailurePoint(FailCheck.FailureType.AFTERSENDVOTE);
					break;
				case "FSP":
					failCheck.setFailurePoint(FailCheck.FailureType.AFTERSENDPROPOSE);
					break;
				case "FOL":
					failCheck.setFailurePoint(FailCheck.FailureType.AFTERBECOMINGLEADER);
					break;
				case "FMV":
					failCheck.setFailurePoint(FailCheck.FailureType.AFTERVALUEACCEPT);
					break;
				default:
					logger.warning("Command " + cmd + " is not a valid command for this app.");
			}
		}

		logger.info("Shutting down Paxos");
		ta.keepExploring = false;
		ta.tiThread.join(1000); // Wait maximum 1s for the app to process any more incomming messages that was
								// in the queue.
		paxos.shutdownPaxos(); // shutdown paxos.
		ta.tiThread.interrupt(); // interrupt the app thread if it has not terminated.
		ta.displayIsland(); // display the final map
		logger.info("Process terminated.");
		System.exit(0);
	}
}
