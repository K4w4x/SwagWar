package simulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import redcode.Redcode;

public class Mars {
    
    private HashMap<Integer, Redcode> grid;
    private int coreSize;
    
    public Mars(){
        this.coreSize = 8000;
        this.grid = initGrid(8000);
        System.out.println("Empty constructor");
    }

    public Mars(int s){
        this.coreSize = s;
        this.grid = initGrid(s);
    }

    public Mars(HashMap<Integer, Redcode> g){
        this.coreSize = g.size();
        this.grid = g;
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

    public String toString(){
        String str = "Grid Size : " + this.grid.size() + System.lineSeparator();
        for(Integer k: this.grid.keySet()){
            str += "key : " + k + System.lineSeparator();
        }
        return str;
    }

    private static HashMap<Integer, Redcode> initGrid(int coreSize){
        
        //Techniquement notre coreSize ne bouge jamais, donc notre grille ne "grandit" pas
        //Lorsqu'une hashmap "grandit", elle re-hash toutes ses valeurs, ce qui prend des ressources, et de base elle le fait lorsque la clé/valeur #(map.size() / 0.75) est atteinte
        //L'opération ci-dessous vise donc à éviter ce re-hashage
        int hashSize = (int) ((coreSize/0.75) + 1);
        HashMap<Integer, Redcode> grid = new HashMap<Integer, Redcode>(hashSize, 0.75f);

        //On remplit les clés
        for(int i = 0; i < coreSize; i++){
            grid.put(i, null);
        }
        return grid;

        // Concern :
        // Une HashMap grandit constamment et ne peut avoir de limite fixé, ce qui nous serait pourtant utile
        // Le problème est contournable en créant une class CustomMap descendant de la classe java.util.Map, mais c'est complexe et pas très propre (imo)
        // Le problème est peut-être aussi un non-problème, étant donné que chaque manipulation d'une coordonée de la grille se fait au modulo de coresize, mais ce serait une façon d'être certain que la taille de notre grille ne bouge pas
        // Pour l'instant je laisse ça de côté
    }

    // Parser
    // Implémentation basique :
    // User Input attendue de la forme suivante :
    // String : [opcode].[modifier] [operandA] [operandB]
    // MOV.I == MOV .I
    // Chaque operand doivent avoir un caractère de début représentant l'addressing mode, puis ensuite l'adresse en question
    // Le modifier est facultatif, chaque opcode ont un modifier par défaut
    // Si l'on a qu'un seul operand, il est considéré comme operand A, et l'operand B est initialisé à 0
    // Case insensitive
    // On peut voir à faire un parser qui lit un fichier et le découpe ligne par ligne pour le renvoyer à notre fonction ci-desssous
    // RegExp :
    // ^([A-Za-z]{3})(.|\s.)?([a-zA-Z]{1})?\s([{}<>#$][0-9]+)\s([{}<>#$][0-9]+)$
    public ArrayList<Redcode> reader(ArrayList<String> readerInput){
        // /!\ Cette implémentation n'a pas été testée /!\
        ArrayList<Redcode> toReturn = new ArrayList<Redcode>();
        for(String current : readerInput){
            if(current.matches("^([A-Za-z]{3})(.|\s.)?([a-zA-Z]{1})?\s([{}<>#$][0-9]+)\s([{}<>#$][0-9]+)?$")){
                //On veut une forme de components telle que : components = {[opcode], [modifier], [operandA], [operandB]}
                List<String> components = new ArrayList<String>();
                String modifier = null;
                if(current.split(".").length > 1){
                    components = Arrays.asList((current.split(".")[0].split(" ")[0] + " " + current.split(".")[1].substring(1)).split(" "));
                    modifier = current.split(".")[1].substring(0, 1);
                }
                else{
                    components = Arrays.asList(current.split(" "));
                }
                System.out.println("Debug : toString() of Redcode components as detected :" + System.lineSeparator() + components.toString());
                System.out.println("Modifier : " + modifier);
                Redcode toAdd = null;
                switch (components.size()) {
                    case 1:
                        toAdd = new Redcode(components.get(0));
                        break;
                    case 2:
                        toAdd = new Redcode(components.get(0), components.get(1));
                        break;
                    case 3:
                        toAdd = new Redcode(components.get(0), components.get(1), components.get(2));
                        break;
                    default:
                        System.out.println("Redcode constructor error : too many arguments");
                        break;
                }
                if (modifier == null){
                    toAdd.setModifier(toAdd.getDefaultModifier());
                }
                else{
                    toAdd.setModifier(modifier);
                }
                toReturn.add(toAdd);
            }
            else{
                System.out.println("MARS reader() error : No RegExp match on \""+current+"\"");
            }
        }
        return toReturn;
    }

    public void execute(Redcode r){
        
    }

}
