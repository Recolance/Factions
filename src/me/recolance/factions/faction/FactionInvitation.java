package me.recolance.factions.faction;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class FactionInvitation{

	public static HashMap<Player, FactionInvitation> factionInvitations = new HashMap<Player, FactionInvitation>();
	
	private Player whoSent;
	private Player whoRecieved;
	private Faction factionInvitedTo;
	private int task;
	
	public FactionInvitation(Player whoSent, Player whoRecieved, Faction factionInvitedTo){
		this.whoSent = whoSent;
		this.whoRecieved = whoRecieved;
		this.factionInvitedTo = factionInvitedTo;
	}

	public Player getWhoSent(){
		return whoSent;
	}

	public void setWhoSent(Player whoSent){
		this.whoSent = whoSent;
	}
	
	public Player getWhoRecieved(){
		return whoRecieved;
	}

	public void setWhoRecieved(Player whoRecieved){
		this.whoRecieved = whoRecieved;
	}

	public Faction getFactionInvitedTo(){
		return factionInvitedTo;
	}

	public void setFactionInvitedTo(Faction factionInvitedTo){
		this.factionInvitedTo = factionInvitedTo;
	}

	public int getTask(){
		return task;
	}

	public void setTask(int task){
		this.task = task;
	}
	
}
