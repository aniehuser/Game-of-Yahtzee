package factions;

import gameRunner.Player;
import java.util.function.Supplier;
import java.lang.Runnable;

public abstract class BaseFaction {
	protected Player p;
	protected String message; 
	protected boolean executed;
	protected Supplier<Boolean> condition;
	protected Runnable exec;
	
	public BaseFaction(Player p){
		this.p = p;
		executed = false;
	}
	
	public boolean isSpecialHand(){
		return condition.get().booleanValue();
	}
	
	public void executeSpecial(){
		if(executed){
			return;
		}
	
		executed = true;
		exec.run();
	}
	
	public void resetFaction(){
		executed = false;
	}
	
	public String specialInstructions(){
		return message;
	}
	
	public boolean isExecuted(){
		return executed;
	}
}
