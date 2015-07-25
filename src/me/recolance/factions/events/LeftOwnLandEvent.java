package me.recolance.factions.events;

import me.recolance.factions.faction.Faction;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeftOwnLandEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	private Faction faction;
	
	public LeftOwnLandEvent(Player player, Faction faction){
		this.player = player;
		this.faction = faction;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public Faction getFaction(){
		return this.faction;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
