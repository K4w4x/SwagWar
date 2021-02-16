package simulation;

import java.util.HashMap;

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

}
