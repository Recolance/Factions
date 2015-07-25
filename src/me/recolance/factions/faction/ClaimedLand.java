package me.recolance.factions.faction;

import java.util.UUID;

public class ClaimedLand{

	private final UUID faction;
	private final String location;
	private final UUID claimer;
	private final long dateClaimed;
	
	public ClaimedLand(UUID faction, String location, UUID claimer, long dateClaimed){
		this.faction = faction;
		this.location = location;
		this.claimer = claimer;
		this.dateClaimed = dateClaimed;
	}
	
	public UUID getFaction(){
		return faction;
	}
	
	public String getLocation(){
		return location;
	}
	
	public UUID getClaimer(){
		return claimer;
	}
	
	public long getDateClaimed(){
		return this.dateClaimed;
	}
}
