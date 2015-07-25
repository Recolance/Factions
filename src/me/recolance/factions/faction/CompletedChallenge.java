package me.recolance.factions.faction;

import java.util.UUID;

public class CompletedChallenge{

	private UUID completedBy;
	private long dateCompleted;
	private FactionChallenge challenge;
	
	public CompletedChallenge(UUID completedBy, long dateCompleted, FactionChallenge challenge){
		this.completedBy = completedBy;
		this.dateCompleted = dateCompleted;
		this.challenge = challenge;
	}
	
	public UUID getCompletedBy(){
		return this.completedBy;
	}
	
	public long getDateCompleted(){
		return this.dateCompleted;
	}
	
	public FactionChallenge getChallenge(){
		return this.challenge;
	}
}
