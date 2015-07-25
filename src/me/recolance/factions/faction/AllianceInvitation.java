package me.recolance.factions.faction;

import java.util.HashMap;


public class AllianceInvitation{

	public static HashMap<Faction, AllianceInvitation> allianceInvitations = new HashMap<Faction, AllianceInvitation>();
	
	private Faction whoSentFaction;
	private Faction whoRecievedFaction;
	private int task;
	
	public AllianceInvitation(Faction whoSentFaction, Faction whoRecievedFaction){
		this.whoSentFaction = whoSentFaction;
		this.whoRecievedFaction = whoRecievedFaction;
	}
	
	public Faction getWhoSentFaction(){
		return whoSentFaction;
	}
	
	public void setWhoSentFaction(Faction whoSentFaction){
		this.whoSentFaction = whoSentFaction;
	}
	
	public Faction getWhoRecievedFaction(){
		return whoRecievedFaction;
	}
	
	public void setWhoRecievedFaction(Faction whoRecievedFaction){
		this.whoRecievedFaction = whoRecievedFaction;
	}
	
	public int getTask(){
		return task;
	}
	
	public void setTask(int task){
		this.task = task;
	}
	
}
