package player;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Player {
	
	private Map<Integer, String[]> playerInput;
	private String name;
	private boolean alive;
	
	public Player() {
		// On crée une chaîne de nombres entiers que l'on converti en chaîne de caractères si lors de la construction d'un humain l'on ne donne pas de nom
		byte[] array = new byte[((new Random().nextInt(8))+1)];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		this.name = generatedString;
		this.playerInput = new HashMap<Integer,String[]>();
		this.alive = true;
	}
	
	public Player(Map<Integer,String[]> p) {
		// On crée une chaîne de nombres entiers que l'on converti en chaîne de caractères si lors de la construction d'un humain l'on ne donne pas de nom
		byte[] array = new byte[((new Random().nextInt(8))+1)];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		this.name = generatedString;
		this.playerInput = p;
		this.alive = true;
	}
	
	public Player(String n) {
		// On crée une chaîne de nombres entiers que l'on converti en chaîne de caractères si lors de la construction d'un humain l'on ne donne pas de nom
		byte[] array = new byte[((new Random().nextInt(8))+1)];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));
		this.name = generatedString;
		this.playerInput = new HashMap<Integer,String[]>();
		this.alive = true;
	}
	
	public Player(Map<Integer,String[]> p, String n) {
		this.name = n;
		this.playerInput = p;
		this.alive = true;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	public Map<Integer,String[]> getPlayerInput() {
		return this.playerInput;
	}
	
	public void setPlayerInput(HashMap<Integer, String[]> p) {
		this.playerInput = p;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public void dies() {
		this.alive = false;
	}
	
}
