package algorithm;

import java.util.ArrayList;

import player.Player;
import redcode.Redcode;

public class Chromosome {
    
    private Player attachedWarrior;
    private ArrayList<Redcode> genes;
    private int fitnessScore;

    public Chromosome() {
        this.genes=new ArrayList<Redcode>();
        this.attachedWarrior = new Player();
        this.fitnessScore = 0;
    }

    public Chromosome(Player p) {
        this.genes = new ArrayList<Redcode>();
        this.attachedWarrior = p;
        this.fitnessScore = 0;
    }

    public Chromosome(ArrayList<Redcode> g, Player p) {
        this.genes = g;
        this.attachedWarrior = p;
        this.fitnessScore = 0;
    }

    public ArrayList<Redcode> getGenes() {
        return this.genes;
    }

    public void setGenes(ArrayList<Redcode> g) {
        this.genes = g;
    }

    public void addGene(Redcode r) {
        this.genes.add(r);
    }

    public Player getWarrior() {
        return this.attachedWarrior;
    }

    public void setWarrior(Player p) {
        this.attachedWarrior = p;
    }

    public int getScore() {
        return this.fitnessScore;
    }

    public void setFitnessScore(int f) {
        this.fitnessScore = f;
    }

    public Chromosome copy() {
        //We don't copy the fitness score, since everytime we use the copy() method, it is meant to create a whole new Chromosome
        Player p = this.attachedWarrior;
        ArrayList<Redcode> a = this.genes;
        Chromosome c = new Chromosome(a, p);
        return c;
    }

    public String toString() {
        String str = ""; 
        //Turn genes to commands
        for(Redcode r : this.genes) {
            str += r.getCode() + r.getModifier() + " " + r.getA() + "," + r.getB();
            str += System.lineSeparator();
        }
        return "Chromosome of : " + this.attachedWarrior.getName() + " having a fitness score of " + this.fitnessScore + " having the following commands : "
        + System.lineSeparator() + str;

    }

}
