package player;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;


public class Player {
	
	private ArrayList<WarriorProcess> processes;
	private String name;

	public Player() {
		// we create a string of integer, then we convert it into a string of character.Used if we dont give the player name in the constuctor
		byte[] array = new byte[((new Random().nextInt(8))+1)];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		this.name = generatedString;
		this.processes = new ArrayList<WarriorProcess>();
	}
	
	public Player(ArrayList<WarriorProcess> p) {
		// we create a string of integer, then we convert it into a string of character.Used if we dont give the player name in the constuctor
		byte[] array = new byte[((new Random().nextInt(8))+1)];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		this.name = generatedString;
		this.processes = p;
	}
	
	public Player(String n) {
		n = (n.equals("") ? ((new Player()).getName()) : (n));
		this.name = n;
		this.processes = new ArrayList<WarriorProcess>();
	}
	
	public Player(String n, ArrayList<WarriorProcess> p) {
		this.name = n;
		this.processes = p;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	public ArrayList<WarriorProcess> getProcesses() {
		return this.processes;
	}
	
	public void setProcesses(ArrayList<WarriorProcess> p) {
		this.processes = p;
	}

	public void addProcess(WarriorProcess wp) {
		this.processes.add(wp);
	}

	public boolean isAlive() {
		//This checks every Process the player has, and it checks if they are alive or not, if one is alive we return True
		for(WarriorProcess w : this.processes) {
			if (w.isAlive()){
				return true;
			}
		}
		return false;
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof Player)) {
			return false;
		} else {
			Player presumedPlayer = (Player) o;
			return presumedPlayer.getName().equals(this.name);
		}
	}

}
