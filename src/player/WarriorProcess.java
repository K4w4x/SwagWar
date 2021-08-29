package player;

public class WarriorProcess {
    //A WarriorProcess is a process belong to a player, there can be several at the same time using the SPLIT command 
    
    private Player attachedPlayer;
    private boolean alive;
    private int token;

    public WarriorProcess() {
        this.attachedPlayer=new Player();
        this.token = 0;
        this.alive =true;
    }

    public WarriorProcess(Player p) {
        this.attachedPlayer=p;
        this.token = 0;
        this.alive=true;
    }

    public WarriorProcess(Player p, int t) {
        this.attachedPlayer=p;
        this.token = t;
        this.alive=true;
    }

    public Player getAttachedPlayer() {
        return this.attachedPlayer;
    }

    public void setPlayer(Player p) {
        this.attachedPlayer = p;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void dies() {
        this.alive=false;
    }

	public int getToken(){
		return this.token;
	}
	
	public void setToken(int to){
		this.token=to;
	}

    public boolean equals(Object o) {
        if(o == null || !(o instanceof WarriorProcess)) {
            return false;
        } else {
            WarriorProcess presumedWarriorProcess = (WarriorProcess) o;
            return presumedWarriorProcess.getAttachedPlayer().equals(this.attachedPlayer) && 
            presumedWarriorProcess.getToken() == this.token && presumedWarriorProcess.isAlive() == this.alive;
        }
    }

}
