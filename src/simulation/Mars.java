package simulation;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import player.Player;
import player.WarriorProcess;
import redcode.Redcode;

public class Mars {
    
    public HashMap<Integer, Redcode> grid;
    private ArrayList<Player> players;
    private final ArrayList<String> redcode_opcode_whitelist = new ArrayList<String>(Arrays.asList(
        "DAT", "MOV", "ADD", "SUB", "MUL", "DIV", "MOD", "JMP", "JMZ", "JMN", "DJN", "SPL", "SLT", "SEQ", "SNE", "NOP"
    ));
    //Match parameters
    private int nbCycle = 0;
    private int coreSize;
    private int min_player_gap;
    private int max_cycles = 80000;
    
    public Mars(){
        this.coreSize = 8000;
        this.grid = initGrid(8000);
        this.players = new ArrayList<Player>();
        System.out.println("Empty constructor");
    }

    public int getNbCycle() {
        return nbCycle;
    }

    public void setNbCycle(int nbc) {
        this.nbCycle = nbc;
    }

    public Mars(int s){
        this.coreSize = s;
        this.grid = initGrid(s);
        this.players = new ArrayList<Player>();
    }
    
    public Mars(HashMap<Integer, Redcode> g){
        this.coreSize = g.size();
        this.grid = g;
        this.players = new ArrayList<Player>();
    }
    
    public Mars(ArrayList<Player> p){
        this.coreSize = 8000;
        this.grid = initGrid(8000);
        this.players = p;
    }
    
    public Mars(HashMap<Integer, Redcode> g, ArrayList<Player> p){
        this.coreSize = g.size();
        this.grid = g;
        this.players = p;
    }
    
    public void setPlayers(ArrayList<Player> p){
        this.players = p;
    }
    
    public ArrayList<Player> getPlayers(){
        return this.players;
    }
    
    public void setGrid(HashMap<Integer, Redcode> g){
        this.grid = g;
    }
    
    public HashMap<Integer, Redcode> getGrid(){
        return this.grid;
    }
    
    public void setCoreSize(int s){
        this.coreSize = s;
    }
    
    public int getCoreSize(){
        return this.coreSize;
    }
    
    public int getMin_player_gap() {
        return min_player_gap;
    }

    public void setMin_player_gap(int m) {
        this.min_player_gap = m;
    }

    public int getMax_cycles() {
        return max_cycles;
    }

    public void setMax_cycles(int m) {
        this.max_cycles = m;
    }

    public void incrementCycle(){
        this.nbCycle += 1;
    }
    
    public String toString(){
        String str = "Grid Size : " + this.grid.size() + System.lineSeparator();
        for(Integer k: this.grid.keySet()){
            str += "key : " + k + System.lineSeparator();
        }
        return str;
    }
    
    //Do not execute after initialization
    public void updateSettings(){
        //grid
        this.grid = initGrid(this.coreSize);
        //min_player_gap
        this.setMin_player_gap(this.coreSize / (this.players.size()*this.players.size()));
    }
    
    //Initialize the grid given a coreSize
    public static HashMap<Integer, Redcode> initGrid(int coreSize){
        //Since our coreSize should never change, our HashMap never grows
        //However, when a hashmap grows, it re-hashes all its values, taking resources and processing time
        //An HashMap does that when the key/value #(map.size() / 0.75) is given
        //The following code avoids any HashMap growing and ensures the HashMap doesn't have to re-hash its values
        int hashSize = (int) ((coreSize/0.75) + 1);
        HashMap<Integer, Redcode> grid = new HashMap<Integer, Redcode>(hashSize, 0.75f);
        //Filling the grid with neutral DAT instructions
        for(int i = 0; i < coreSize; i++){
            Redcode fill = new Redcode(null, "DAT", "", "$0", "$0", i);
            grid.put(i, fill);
        }
        return grid;
    }

    //Adds a redcode element to the grid
    public void addToGrid(Redcode r){
        this.grid.put(r.getArea(), r);
    }

    // Parser
    // Takes an ArrayList of user input that represents Redcode instructions and turns it into actual Redcode instructions that we can work with
    // Returns an ArrayList of Redcode instructions attached to the same process
    // Since it's only used for initializing a game, we're setting random areas already so we don't have to do it elsewhere
    // Expected User Input representing a Redcode instruction :
    // String : [opcode].[modifier] [operandA], [operandB]
    // Should be quite flexible with syntax so it doesn't annoy the user, while still perfectly understanding the Redcode instruction
    // Case insensitive (should be)
    // Modifiers and Addressing Modes aren't mandatory since we have default modifiers and Adressing modes
    // Same for operand B, it can be initialized to 0 since it isn't always useful
    // redcode_opcode_whitelist is a bandaid solution so "ORG" and "END" from the Redcode-94' standard or other illegal instructions aren't recognized as legit instructions
    // RegExp for each line :
    // ^([A-Za-z]{3})([\.]|(\s[\.]))?([a-zA-Z]{1,2})?[\s]+([{}<>#*@$]?[-]?[0-9]+)[\s]*[,]?[\s]+([{}<>#*@$]?[-]?[0-9]+)?$
    public ArrayList<Redcode> parser(ArrayList<String> readerInput, Player p) {
        ArrayList<Redcode> toReturn = new ArrayList<Redcode>();
        Pattern pattern = Pattern.compile("^([A-Za-z]{3})([\\.]|\\s[\\.])?([a-zA-Z]{1,2})?[\\s]+([{}<>#*@$]?[-]?[0-9]+)[\\s]*[,]?[\\s]*([{}<>#*@$]?[-]?[0-9]+)?$");
        //Set to a final attribute

        WarriorProcess wp = new WarriorProcess(p);

        //For each string of input
        for (String current : readerInput) {
            //We try to match it to our big Regexp
            Matcher matcher = pattern.matcher(current);
            //We do something if have a match that isn't empty
            if (matcher.find() && (matcher.groupCount() > 1)) {
                ArrayList<String> components = new ArrayList<String>();
                String modifier = null;

                //Checks if the detected opCode is found as a legit opCode
                //Then goes through all the detected elements and adds them to the list of components
                //Modifier is handled a bit differently, so when we detect it, it gets its own String
                if(this.redcode_opcode_whitelist.indexOf(matcher.group(1).toUpperCase()) != -1){
                    for(int i = 1; i <= matcher.groupCount(); i++){
                        String g = matcher.group(i);
                        if(g != null && !(g.strip().equals("."))){
                            if(g.matches("^[a-zA-Z]{1,2}$")){
                                modifier = "." + g;
                            }
                            else{
                                components.add(g);
                            }
                        }
                    }
                    
                    Redcode toAdd = null;
                    //Creates the Redcode object depending on how many elements we detected
                    switch (components.size()) {
                        case 1:
                            toAdd = new Redcode(wp, components.get(0));
                            break;
                        case 2:
                            toAdd = new Redcode(wp, components.get(0), components.get(1));
                            break;
                        case 3:
                            toAdd = new Redcode(wp, components.get(0), components.get(1), components.get(2));
                            break;
                        default:
                            System.out.println("Redcode constructor error : too many arguments");
                            break;
                    }
                    if (toAdd != null){
                        //Sets the modifier as default if none has been given
                        if (modifier == null) {
                            toAdd.setModifier(toAdd.getDefaultModifier());
                        } else {
                            toAdd.setModifier(modifier);
                        }
                        //Transform negative values into corewar usable positive values
                        if(toAdd.getA().contains("-")){
                            int substring = (Pattern.matches("^([{}<>#*@$][-][0-9]+)$", toAdd.getA())) ? (1) : (0);
                            int value = (Integer.parseInt(toAdd.getA().substring(substring)) + this.coreSize) % this.coreSize;
                            toAdd.setA(toAdd.getA().substring(0, substring) + value);
                        }
                        if(toAdd.getB().contains("-")){
                            int substring = (Pattern.matches("^([{}<>#*@$][-][0-9]+)$", toAdd.getB())) ? (1) : (0);
                            int value = (Integer.parseInt(toAdd.getB().substring(substring)) + this.coreSize)% this.coreSize;
                            toAdd.setB(toAdd.getB().substring(0, substring) + value);
                        }
                        //Checks if there is an adressing mode : if not, the default is "$"
                        if(!(Arrays.asList(new String[]{"{", "}", "<", ">", "#", "*", "@", "$"}).contains(toAdd.getA().substring(0,1)))){
                            toAdd.setA("$" + toAdd.getA());
                        }
                        if (!(Arrays.asList(new String[] { "{", "}", "<", ">", "#", "*", "@", "$" }).contains(toAdd.getB().substring(0, 1)))) {
                            toAdd.setB("$" + toAdd.getB());
                        }
                        toReturn.add(toAdd);
                    }
                    else{
                        System.out.println("MARS parser() error : Returning null Redcode object");
                        break;
                    }
                }
            } else {
                System.out.println("MARS parser() error : No RegExp match on \"" + current + "\"");
            }
        }
        // Get a valid random area for the process
        // Checks if MARS.min_player_gap is respected
        // There might be a less ressource-intensive solution out there, but we're in a rush
        int randomArea = new Random().nextInt(this.coreSize);
        boolean check_min_player_gap = false;
        while(!check_min_player_gap){
            check_min_player_gap = true;
            for (int i = ((randomArea - this.min_player_gap + this.coreSize)%this.coreSize); i <= ((randomArea + this.min_player_gap + toReturn.size())%this.coreSize); i++) {
                if (!(this.grid.get((i%this.coreSize)).getCode().equalsIgnoreCase("DAT"))) {
                    check_min_player_gap = false;
                }
            }
            if(!check_min_player_gap){
                randomArea = new Random().nextInt(this.coreSize);
            }
        }
        //Sets the starting point for the process and adds it to the list of process for the player
        wp.setToken(randomArea);
        p.addProcess(wp);
        //Adds the area to the redcode objects
        for(int i = 0; i < toReturn.size(); i++){
            toReturn.get(i).setArea(randomArea + i);
        }
        return toReturn;
    }

    public ArrayList<String> reader(File readerInput) {
        // Reads a redcode file
        // Ideally it would read Redcode-94' standard files, but actually can't because too complicated for now
        // Doesn't really sort anything, just dumps the file line by line into an arrayList so we can send it to the MARS parser
        ArrayList<String> redcodeList = new ArrayList<String>();
        if (readerInput.exists()) {
            try{

                //Disclaimer on expected input
                System.out.println("Reminder : File reader does not directly support Redcode-94' parser standard");
                System.out.println("You are supposed to feed a file with one Redcode instruction per line, with numeric operands only");
                System.out.println("Expected line format for Redcode : [opcode] .[modifier] [operandA] [operandB]");
                System.out.println("Comments are supported and printed when read");

                //A problem may occur with the scanner from the main loop
                Scanner myReader = new Scanner(readerInput);
                //Goes through the file line by line
                while (myReader.hasNextLine()){
                    String data = myReader.nextLine();
                    if(data.strip().length() > 0){
                        //Checks if the line starts with an ; then it's a comment
                        if(data.substring(0,1).equals(";")){
                            System.out.println("Comment : " + data.substring(1));
                        }
                        else{
                            redcodeList.add(data.strip().replace(";", ""));
                        }
                    }
                }
                myReader.close();
            }
            catch(Exception e){
                System.out.println("MARS reader() error : " + e);
            }
        } else {
            System.out.println("MARS reader() error : The file does not exist.");
        }
        return redcodeList;
    }

    public void execute(Redcode r, WarriorProcess w){
        //Executes a redcode instruction based on its opCode
        //Should work like magic, let's see if it does
        try{
            r.setWarriorProcess(w);
            Method opCode = r.getClass().getMethod(r.getCode().toLowerCase(), this.grid.getClass());
            opCode.invoke(r, this.grid);
        }
        catch (Exception e){
            System.out.println(r);
            System.out.println("MARS execute() error : " + e);
        }
    }

    public void situationToGrid(){
        //Displays in the console the actual state of the grid
        //Quite janky, doesn't work well for 10+ players on the grid
        //Needs to be replaced with a proper GUI if we have time
        
        //Creates an HashMap that links each player to a different color
        HashMap<Player, String> colorMap = new HashMap<Player, String>();
        for (int i = 0; i < this.players.size(); i++){
            colorMap.put(this.players.get(i), "\033[0;3"+(i+1)+"mX \033[0;37m");
        }
        String closure = ". ";
        String str = "";
        // Fills the grid with dots and player-colored 'X'
        for(int i = 0; i < this.coreSize; i++){
            if (i % 40 == 0) {
                System.out.println(str);
                str = "";
            }
            if(this.grid.get(i).getWarriorProcess() == null){
                str += closure;
            }
            else{
                for (Player p : this.players){
                    if (this.grid.get(i).getWarriorProcess().getAttachedPlayer().equals(p)){
                        str += colorMap.get(p);
                    }
                }
            }
        }
        System.out.println(str + "\033[0m" + System.lineSeparator());
    }

    public boolean isOver(){
        //Checks if the game is over, to do that we check how many players are alive and if the cycles are at max
        int players_alive = 0;
        for (Player p : this.players) {
            if (p.isAlive()) {
                players_alive += 1;
            }
        }
        if(players_alive > 1 && this.nbCycle < this.max_cycles){
            return false;
        }
        else{
            return true;
        }
    }

    public Player getWinner(){
        //Gets the last standing Player 
        for (Player p : this.players){
            if(p.isAlive()){
                return p;
            }
        }
        return null;
    }
}