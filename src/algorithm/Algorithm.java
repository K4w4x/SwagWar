package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

import player.Player;
import player.WarriorProcess;
import simulation.Mars;
import redcode.Redcode;

public class Algorithm {
    
    private ArrayList<Chromosome> population; //The population of Chromosomes, will be updated at each iteration
    private Mars mars; //The mars where the games will happen

    public Algorithm() {
        this.population = new ArrayList<Chromosome>();
        this.mars = new Mars();
    }

    public Algorithm(ArrayList<Chromosome> c, Mars m) {
        this.population = c;
        this.mars = m;
    }

    public ArrayList<Chromosome> getPopulation() {
        return this.population;
    }

    public void setPopulation(ArrayList<Chromosome> c) {
        this.population = c;
    }

    public Mars getMars() {
        return this.mars;
    }

    public void setMars(Mars m) {
        this.mars = m;
    }

    public int getFitnessScore(Chromosome c) { //Will return the fitness score of the Chromosome c
        int score = 0;
        for (Map.Entry<Integer, Redcode> set : this.mars.getGrid().entrySet()) { //For each case in the Mars grid, will do +1 if the Warrior is the Chromosome's one
            if(set.getValue().getWarriorProcess() != null) {
                if(set.getValue().getWarriorProcess().getAttachedPlayer().equals(c.getWarrior())) {
                    score += 1;
                }
            }
        }
        if(c.getWarrior().equals(this.mars.getWinner())) {
            score += 150;
        }
        return score;
    }

    public Chromosome mutation(Chromosome p1, Chromosome p2) { //Mutation phase
        //We define a cursor, it will determinate from where we change commands with the second parent
        int cursor = new Random().nextInt(p1.getGenes().size());
        //We create a Chromosome which is a copy of the first parent
        Chromosome c = p1.copy();
        //We set the attachedWarrior to a new Player, since we don't want multiple Chromosome to have only one Player
        c.setWarrior(new Player());
        for(int i=0; i < p1.getGenes().size(); i++) { //For each gene
            //If we are before the cursor
            if(i < cursor) {
                //We take a Random boolean, it's a part of the mutation phase, sometimes children won't heritate some commands, it's random 
                int chances = new Random().nextInt(10);
                //If chances is below seven
                if(chances < 7) {
                    //We set the current gene to the second parent's gene
                    c.getGenes().set(i, p2.getGenes().get(i));
                }
            }
        }
        //Now, we have to ensures that our Chromosome respects a rule
        //Does it have a jump ? If not, we must save it from a certain death.
        boolean b = false;
        //Travels across every gene
        for(Redcode r : c.getGenes()) {
            //If one is a JMP, then b is true, and we break the loop
            if(r.getCode().equals("JMP")) {
                b = true;
                break;
            }
        }
        //If there was no JMP at all
        if(!b) {
            //Creates a new WarriorProcess which belongs to the c Player
            WarriorProcess w = new WarriorProcess(c.getWarrior());
            //Creates a JMP Redcode object, pointing to the first gene and having the area of the last gene + 1
            Redcode newRedcode = new Redcode(w, "JMP", ".B", "$" + (c.getGenes().size()-1) * -1, "$0", c.getGenes().get(c.getGenes().size()-1).getArea()+1);
            //Removes the last gene
            c.getGenes().remove(c.getGenes().size()-1);
            //To replace it with the JMP
            c.addGene(newRedcode);
        }
        return c;
    }

    public void replacePopulation() {
        //Get the four best Chromosomes
        Chromosome c1 = this.population.get(this.population.size() - 1);
        Chromosome c2 = this.population.get(this.population.size() - 2);
        Chromosome c3 = this.population.get(this.population.size() - 3);
        Chromosome c4 = this.population.get(this.population.size() - 4);
        //Add them into the new population, and does the mutation phase
        ArrayList<Chromosome> newPopulation = new ArrayList<Chromosome>(Arrays.asList(c1,c2,c3,c4,mutation(c1,c2),mutation(c1,c3),
        mutation(c1,c4),mutation(c2,c3),mutation(c2,c4),mutation(c3,c4)));
        //We now have a population of 10 ! Congrats !
        //Set the population to the new one
        this.population = newPopulation;
    }

    public void setPopulationScores() {
        //Travels across every member of the population, and set its fitnessScore
        for(Chromosome c : this.population) {
            c.setFitnessScore(getFitnessScore(c));
        }
    }

    public void rankPopulation() {
        //First, we calculate every fitness score, and set them to each Chromosome
        this.setPopulationScores();
        //We use the Collection sort() method, but with a different comparator
        //Means we define another comparator using fitness scores of Chromosomes to sort the ArrayList
        Collections.sort(this.population, new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome z1, Chromosome z2) {
                if (z1.getScore() > z2.getScore())
                    return 1;
                if (z1.getScore() < z2.getScore())
                    return -1;
                return 0;
            }
        });
        //Now that our population is sorted, when we have a ranking !
    }
    
}
