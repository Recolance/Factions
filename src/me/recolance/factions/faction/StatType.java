package me.recolance.factions.faction;

import java.util.HashMap;

public enum StatType{

	POWER(1),
	SCORE(2),
	EXPERIENCE(3),
	LEVEL(4),
	BLOCKS_PLACED(5),
	BLOCKS_BROKEN(6),
	ORES_BROKEN(7),
	POTIONS_BREWED(8),
	ITEMS_FISHED(9),
	ANIMALS_KILLED(10),
	MONSTERS_KILLED(11),
	PLAYERS_KILLED(12),
	DEATHS_NON_PLAYER(13),
	DEATHS_PLAYER(14),
	LAND_CLAIMED(15),
	LAND_OVERCLAIMED(16),
	CHALLENGES_COMPLETED(17);
	
	private int id;
	
	StatType(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public static StatType getStatType(int id){
		for(StatType stat : StatType.values()){
			if(stat.getId() == id) return stat;
		}
		return null;
	}
	
	public static HashMap<StatType, Long> getBareStats(){
		HashMap<StatType, Long> stats = new HashMap<StatType, Long>();
		for(StatType stat : StatType.values()){
			if(stat == StatType.POWER) stats.put(stat, 750L);
			else if(stat == StatType.LEVEL) stats.put(stat, 1L);
			else stats.put(stat, 0L);
		}
		return stats;
	}
}
