package main;

import java.util.Scanner;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

import algorithm.Algorithm;
import algorithm.Chromosome;
import player.Player;
import redcode.Redcode;
import simulation.Mars;

public class Main {

	public static File readFile(Scanner sc, String q) {
		// It reads a file (duh)
		File f = new File("");
		boolean b = false;
		String absoluteDirPath = (new File("")).getAbsolutePath() + FileSystems.getDefault().getSeparator();
		while (!b) {
			try {
				System.out.println(q);
				String filename = sc.next();
				String file = absoluteDirPath + filename;
				f = new File(file);
				if (f.exists()) {
					b = true;
				} else {
					System.out.println("test readFile() error : file does not exists or can't be found");
				}
			} catch (Exception e) {
				System.out.println("test readFile() error : " + e);
				sc.next();
			}
		}
		System.out.println("Got the file !");
		return f;
	}

	public static ArrayList<Redcode> redcode_choice1(Scanner sc, Mars m, Player p, String absoluteDirPath)
			throws InterruptedException {
		// Method name is pretty self-explanatory
		// The user chose to upload file with Redcode instructions
		boolean confirmation = true;
		ArrayList<Redcode> warrior_redcode = null;
		while (confirmation) {
			System.out.println("Alright lad, the file needs to be in this directory : " + System.lineSeparator()
					+ absoluteDirPath);
			File f = readFile(sc,
					"Please input the filename that needs to be uploaded (don't forget the file extension) :");
			System.out.println("Processing file...");
			warrior_redcode = m.parser(m.reader(f), p);
			TimeUnit.SECONDS.sleep(2);
			System.out.println("From what we've read, your warrior will do the commands as follow :");
			warrior_redcode.forEach((r) -> {
				System.out.println(r.getCode() + r.getModifier() + " " + r.getA() + ", " + r.getB());
			});
			confirmation = askConfirmation(sc, "Do you want to change your file for this warrior ? (y/n)");
		}
		return warrior_redcode;
	}

	public static ArrayList<Redcode> redcode_choice2(Scanner sc, Mars m, Player p) throws InterruptedException {
		// Method name is pretty self-explanatory
		// The user chose to write Redcode instructions himself (what a madlad)
		boolean confirmation = true;
		ArrayList<Redcode> warrior_redcode = null;
		sc.nextLine();
		while (confirmation) {
			int instruction_counter = 1;
			ArrayList<String> userInput = new ArrayList<String>();
			boolean end = false;
			while (!end) {
				try {
					System.out.println("Instruction n°" + instruction_counter);
					String q = sc.nextLine();
					if (q.equals("END")) {
						end = true;
					} else if (!q.equals("")) {
						userInput.add(q);
						instruction_counter += 1;
					}
				} catch (Exception e) {
					System.out.println("test redcode_choice2() error : " + e);
				}
			}
			System.out.println("You're done ?");
			System.out.println("Nice !");
			warrior_redcode = m.parser(userInput, p);
			TimeUnit.SECONDS.sleep(2);
			System.out.println("This is what we've understood :");
			warrior_redcode.forEach((r) -> {
				System.out.println(r.getCode() + r.getModifier() + " " + r.getA() + ", " + r.getB());
			});
			confirmation = askConfirmation(sc, "Do you want to retry and make some changes ? (y/n)");
		}
		return warrior_redcode;
	}

	// There is surely a way to refactor all those askX methods, but this is gonna
	// be for future Kawax if he has time for this ****
	// They all kinda do the same thing, they make sure the user actually inputs
	// what we're asking for
	// (If users were actually smart, we wouldn't have to do this kind of things)

	public static int askNumber(Scanner sc, String q) {
		int nombre = 0;
		boolean b = false;
		while (!b) {
			try {
				nombre = 0;
				System.out.println(q);
				nombre = sc.nextInt();
				b = true;
			} catch (InputMismatchException e) {
				System.out.println("You need to input an integer");
				sc.next();
			}
		}
		return nombre;
	}

	public static int askChoice(Scanner sc, String q, ArrayList<Integer> choices) {
		int nombre = 0;
		boolean b = false;
		while (!b) {
			try {
				nombre = 0;
				System.out.println(q);
				nombre = sc.nextInt();
				if (choices.contains(nombre)) {
					b = true;
				} else {
					System.out.println("You need to input an integer corresponding to one of the choices");
				}
			} catch (Exception e) {
				System.out.println("You need to input an integer");
				sc.next();
			}
		}
		return nombre;
	}

	public static boolean askConfirmation(Scanner sc, String q) {
		String c = "";
		boolean b = false;
		while (!b) {
			try {
				System.out.println(q);
				c = sc.next();
				if (c.equals("y") || c.equals("n")) {
					b = true;
				} else {
					System.out.println("Vous devez saisir 'y' ou 'n'");
				}
			} catch (Exception e) {
				System.out.println("Vous devez saisir 'y' ou 'n'");
				sc.next();
			}
		}
		if (c.equals("y")) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) throws InterruptedException {

		Scanner sc = new Scanner(System.in);
		String absoluteDirPath = (new File("")).getAbsolutePath() + FileSystems.getDefault().getSeparator();
		Algorithm bigbrain = new Algorithm();
		ArrayList<Chromosome> twentyones = new ArrayList<Chromosome>();
		ArrayList<Player> players = new ArrayList<Player>();
		boolean ah_yes_algo_time = false;
		int nbIteration = 1;

		System.out.println("Welcome to SwagWar™ ! (⌐■_■)" + System.lineSeparator());

		if (!askConfirmation(sc, "Are you familiar with the game of Corewar ? (y/n)")) {
			System.out.println("Sorry but SwagWar™ is too swag for uniniated people");
			System.out.println(
					"Luckily, if you are connected to the Internet and have the ability to read and understand English, maybe we can help you out !");
			System.out.println(
					"The following websites have proven very useful to our own understanding of the Corewar game, and we believe it can help you too :");
			System.out.println("https://corewar-docs.readthedocs.io/en/latest/" + System.lineSeparator()
					+ "https://vyznev.net/corewar/guide.html" + System.lineSeparator());
			TimeUnit.SECONDS.sleep(5);
			System.out.println(
					"Once you've read enough to understand how this all works and how you can create your own warriors with a bit of Redcode, then"
							+ System.lineSeparator() + "you'll be able to restart the game and finally play");
			System.out.println(
					"Also you can just download a bunch of premade warriors on the Internet and just play with them here, but surely "
							+ System.lineSeparator() + "that ain't as fun as learning about how Redcode works");
			System.out.println("Surely...");
			TimeUnit.SECONDS.sleep(5);
			System.out.println(System.lineSeparator() + System.lineSeparator()
					+ "You are actually stuck here, so you need to restart the game in order to actually play.");
		} else {
			int player_number = askNumber(sc, "How many warriors are gonna fight in the arena ?");

			for (int i = 0; i < player_number; i++) {
				System.out.println("How would like to call Warrior n°" + (i + 1) + " ?");
				String q = sc.next();
				players.add(new Player(q));
			}

			Mars m = new Mars(players);
			if (askConfirmation(sc, "Do you want to change the match settings from the default ones ? (y/n)")) {
				int grid_size = askNumber(sc, "What grid size are we looking at ? (default is 8000)");
				m.setCoreSize(grid_size);
				int max_cycles = askNumber(sc,
						"What is the maximum number of cycles you wanna watch ? (default is 80000)");
				m.setMax_cycles(max_cycles);
			}
			m.updateSettings();

			if (player_number == 10) {
				int choice = askChoice(sc, "Now you've got a choice to make :" + System.lineSeparator()
						+ "1. You take the red pill, and you'll witness a ruthless SwagWar battle royale between your warriors, just so you can pick the winner warrior which is obviously better than everyone else (genetic algorithm)"
						+ System.lineSeparator()
						+ "2. You take the blue pill, and you simply watch your warriors fight on the grid, gladiator style",
						new ArrayList<Integer>(Arrays.asList(1, 2)));
				ah_yes_algo_time = (choice == 1) ? (true) : (false);
				if (ah_yes_algo_time) {
					System.out.println(
							"When giving Redcode to your warriors, keep in mind they all need to have the same number of instruction each");
					for (Player p : players) {
						twentyones.add(new Chromosome(p));
					}
					nbIteration = askNumber(sc, "How many rounds of battle royale you want them to do ?");
				}
			}

			boolean skipTutorial = false;
			System.out.println(System.lineSeparator()
					+ "Alright now that we've got some sick names playing on the grid, we're gonna need some pieces of Redcode slapped on those bad bois"
					+ System.lineSeparator());
			for (int i = 0; i < players.size(); i++) {
				int choice = askChoice(sc,
						"For our boi " + players.get(i).getName() + ", what do ?" + System.lineSeparator()
								+ "1. Upload a file with Redcode instructions for our boi" + System.lineSeparator()
								+ "2. Write the Redcode instructions yourself line by line right now",
						new ArrayList<Integer>(Arrays.asList(1, 2)));
				ArrayList<Redcode> warrior_redcode = null;
				if (choice == 0) {
					System.out.println("Something's wrong, I can feel it." + System.lineSeparator()
							+ "(You should restart the program)");
					break;
				} else if (choice == 1) {
					warrior_redcode = redcode_choice1(sc, m, players.get(i), absoluteDirPath);
				} else if (choice == 2) {
					if (!skipTutorial) {
						System.out.println("I see you're going the freestyle way, you unprepared madlad");
						TimeUnit.SECONDS.sleep(1);
						System.out.println("Here are the rules mate :");
						System.out.println(
								"We'll register each line as a Redcode instruction, and you'll have to write every line in this format :");
						System.out.println("[opcode] .[modifier] [operandA] [operandB]");
						System.out.println("If we don't like the way you write your Redcode, we'll tell ya anyway"
								+ System.lineSeparator());
						System.out.println("When you're done on Redcoding our little boi " + players.get(i).getName()
								+ " just write \"END\" and you'll be done");
						System.out.println(
								"And since we're quite the sympathetic bunch of lads, we'll give you a quick recap of what we've understood so you can confirm your piece of art");
						TimeUnit.SECONDS.sleep(3);
						System.out.println("Now that you should be all set, let's get to Redcoding :");
						skipTutorial = true;
					}
					warrior_redcode = redcode_choice2(sc, m, players.get(i));
				}
				if (warrior_redcode != null) {
					if (ah_yes_algo_time) {
						twentyones.get(i).setGenes(warrior_redcode);
					}
					warrior_redcode.forEach((r) -> {
						m.addToGrid(r);
					});
				}
			}

			int choice_cycles = 0;
			if (ah_yes_algo_time) {
				bigbrain.setMars(m);
				bigbrain.setPopulation(twentyones);
				// Register the size of genes, must be the same for every Warrior
				int size = bigbrain.getPopulation().get(0).getGenes().size();
				// Asserts that the genes size is the same for each Warrior, throws
				// AssertionError
				for (Chromosome c : bigbrain.getPopulation()) {
					assert c.getGenes().size() == size : "Warriors don't have the same size";
				}
			}
			else{
				ArrayList<Integer> cycles = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
				choice_cycles = askChoice(sc,
						"So, let's now talk about printing the grid !" + System.lineSeparator()
								+ "Since printing the grid slows the program, when do you want to print it ?"
								+ System.lineSeparator() + "1. Every cycle" + System.lineSeparator() + "2. Every ten cycles"
								+ System.lineSeparator() + "3. Every fifty cycles" + System.lineSeparator()
								+ "4. Every hundred cycles" + System.lineSeparator() + "5. Every five hundred cycles",
						cycles);
				switch (choice_cycles) {
				case 1:
					choice_cycles = 1;
					break;
				case 2:
					choice_cycles = 10;
					break;
				case 3:
					choice_cycles = 50;
					break;
				case 4:
					choice_cycles = 100;
					break;
				case 5:

					choice_cycles = 500;
					break;
				}
			}

			System.out.println(System.lineSeparator() + "Well, everything looks quite fine and dandy innit ?");
			System.out.println("I feel like it's actually time for the warriors to get ready to rumble");
			System.out.println("Let's get the good ol' countdown kick in :");
			TimeUnit.MILLISECONDS.sleep(1000);
			for (int i = 5; i > 0; i--) {
				System.out.println(i + "..");
				TimeUnit.SECONDS.sleep(1);
			}
			System.out.println("Start !");

			// Main loop
			while (nbIteration >= 0) {
				// Reset MARS cycles
				m.setNbCycle(0);
				// Update Players
				players = m.getPlayers();
				// Keeps track of each players' current process
				HashMap<Player, Integer> process_token_map = new HashMap<Player, Integer>();
				players.forEach((p) -> {
					process_token_map.put(p, 0);
				});
				// Game Loop
				while (!(m.isOver())) {
					m.incrementCycle();
					// Displaying the current state of the game
					if (m.getNbCycle() % choice_cycles == 0 && !ah_yes_algo_time) {
						System.out.println("Cycle n°" + m.getNbCycle());
						m.situationToGrid();
					}
					// Execution loop (effectively a turn)
					for (int i = 0; i < players.size(); i++) {
						Player currentPlayer = players.get(i);
						if (currentPlayer.isAlive()) {
							// get the executing process' index and update the token
							int process_token = (process_token_map.get(currentPlayer))
									% (currentPlayer.getProcesses().size());
							process_token_map.put(currentPlayer, process_token_map.get(currentPlayer) + 1);
							// Actually execute the damn Redcode object
							m.execute(
									m.getGrid()
											.get((currentPlayer.getProcesses().get(process_token).getToken())
													% m.getCoreSize()),
									currentPlayer.getProcesses().get(process_token));
						}
					}
				}
				nbIteration = nbIteration - 1;
				if (ah_yes_algo_time) {
					// Update the algorithm
					bigbrain.setMars(m);
					bigbrain.rankPopulation();
					// If we are at the end, we don't want to erase the last population for a new
					// one
					// Since the big winner will be in the top 4, it shouldn't be a problem to
					// replace it
					// But we want safety, because safety is good, very good, all my homies like
					// safety. Do you like safety ?
					if (nbIteration != 0) {
						bigbrain.replacePopulation();
						// Restart with the new population

						// Reset the grid
						m.setGrid(Mars.initGrid(m.getCoreSize()));
						// Refill the grid with the new population
						bigbrain.getPopulation().forEach((c) -> {
							c.getGenes().forEach((r) -> {
								m.addToGrid(r);
							});
						});
					}
				}
			}
			if (ah_yes_algo_time) {
				// Set fitness scores to the final population, and rank it
				bigbrain.rankPopulation();
				// Now is the time for the big reveal !
				System.out.println(
						"The final Warrior, the piece of art you just created is ready, be sure to keep your eyes wide open : ");
				// We ensure there is a Winner, if not, the Chromosome with the biggest fitness
				// score is chosen
				if (m.getWinner() == null) {
					System.out.println((bigbrain.getPopulation().get(bigbrain.getPopulation().size() - 1)).toString());
				}
				// If not, we search what Chromosome is the big winner
				for (Chromosome c : bigbrain.getPopulation()) {
					if (c.getWarrior().equals(m.getWinner())) {
						// And we print its commands and stuff
						System.out.println(c.toString());
						break;
					}
				}
			} else {
				if (m.getWinner() != null) {
					System.out.println("And the winner is our homeboy : " + m.getWinner().getName());
				} else {
					System.out.println("And that's a draw unfortunately");
				}
			}
		}
	}
}
