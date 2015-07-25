package me.recolance.factions.faction;

import java.util.UUID;

import org.bukkit.Location;

public class Warp{

	private final String name;
	private final Location location;
	private final UUID creator;
	private final long dateCreated;
	
	public Warp(String name, Location location, UUID creator, long dateCreated){
		this.name = name;
		this.location = location;
		this.creator = creator;
		this.dateCreated = dateCreated;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Location getLocation(){
		return this.location;
	}
	
	public UUID getCreator(){
		return this.creator;
	}
	
	public long getDateCreated(){
		return this.dateCreated;
	}
}
