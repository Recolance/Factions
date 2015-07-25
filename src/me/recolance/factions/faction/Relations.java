package me.recolance.factions.faction;

import java.util.List;
import java.util.UUID;

public class Relations{

	private List<UUID> alliances;
	private List<UUID> enemies;
	private List<UUID> enemiedBy;
	
	public Relations(List<UUID> alliances, List<UUID> enemies, List<UUID> enemiedBy){
		this.alliances = alliances;
		this.enemies = enemies;
		this.enemiedBy = enemiedBy;
	}

	public List<UUID> getAlliances(){
		return alliances;
	}

	public void setAlliances(List<UUID> alliances){
		this.alliances = alliances;
	}

	public List<UUID> getEnemies(){
		return enemies;
	}

	public void setEnemies(List<UUID> enemies){
		this.enemies = enemies;
	}

	public List<UUID> getEnemiedBy(){
		return enemiedBy;
	}

	public void setEnemiesdBy(List<UUID> enemiedBy){
		this.enemiedBy = enemiedBy;
	}
}
