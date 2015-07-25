package me.recolance.factions.faction;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;

public enum FactionSound{

	ANVIL_BREAK(Sound.ANVIL_BREAK, 1, 6, 10, "Anvil Break"),
	ANVIL_LAND(Sound.ANVIL_LAND, 2, 1, 0, "Anvil Slam"),
	EXPLODE(Sound.EXPLODE, 3, 18, 30, "Explosion"),
	FIRE(Sound.FIRE, 4, 1, 1, "Fire Crackle"),
	FIZZ(Sound.FIZZ, 5, 1, 2, "Fizz"),
	FUSE(Sound.FUSE, 6, 12, 20, "Fuse"),
	LEVEL_UP(Sound.LEVEL_UP, 7, 6, 11, "Level Up"),
	SPLASH(Sound.SPLASH, 8, 1, 3, "Splash"),
	BAT_TAKEOFF(Sound.BAT_TAKEOFF, 9, 1, 4, "Takeoff"),
	BLAZE_BREATH(Sound.BLAZE_BREATH, 10, 18, 31, "Flame Breath"),
	MEOW(Sound.CAT_MEOW, 11, 1, 5, "Meow"),
	ENDERDRAGON_HIT(Sound.ENDERDRAGON_HIT, 12, 6, 12, "Dragon Hit"),
	ENDERMAN_TELEPORT(Sound.ENDERMAN_TELEPORT, 13, 12, 21, "Teleport"),
	ENDERMAN_SCREAM(Sound.ENDERMAN_SCREAM, 14, 18, 32, "Screetch"),
	GHAST_SCREAM(Sound.GHAST_SCREAM, 15, 12, 22, "Scream"),
	FIREBALL(Sound.GHAST_FIREBALL, 16, 1, 6, "Fireball"),
	IRON_GOLEM_DEATH(Sound.IRONGOLEM_DEATH, 17, 12, 23, "Golem Death"),
	SILVERFISH_DEATH(Sound.SILVERFISH_KILL, 18, 1, 7, "Silverfish"),
	BONES(Sound.SKELETON_HURT, 19, 6, 13, "Bones"),
	WITHER_IDLE(Sound.WITHER_DEATH, 20, 12, 24, "Wither"),
	WITHER_SPAWN(Sound.WITHER_SPAWN, 21, 25, 40, "Domination"),
	ZOMBIE_DEATH(Sound.ZOMBIE_DEATH, 22, 6, 14, "Undead"),
	FIREWORK_EXPLOSION(Sound.FIREWORK_TWINKLE2, 23, 6, 15, "Firework"),
	LAUNCH(Sound.FIREWORK_LAUNCH, 24, 6, 16, "Launch"),
	SUCCESSFUL_HIT(Sound.SUCCESSFUL_HIT, 25, 1, 8, "Successful Hit");

	private Sound sound;
	private int soundId;
	private int levelRequired;
	private int menuSlot;
	private String name;
	
	private static HashMap<Integer, FactionSound> idSound = new HashMap<Integer, FactionSound>();
	private static HashMap<Integer, FactionSound> menuSound = new HashMap<Integer, FactionSound>();
	static{
		for(FactionSound sound : FactionSound.values()){
			idSound.put(sound.getId(), sound);
			menuSound.put(sound.getMenuSlot(), sound);
		}
	}
	
	FactionSound(Sound sound, int soundId, int levelRequired, int menuSlot, String name){
		this.sound = sound;
		this.soundId = soundId;
		this.levelRequired = levelRequired;
		this.menuSlot = menuSlot;
		this.name = name;
	}
	
	public Sound getSound(){
		return this.sound;
	}
	public int getId(){
		return this.soundId;
	}
	public int getLevelRequirement(){
		return this.levelRequired;
	}
	public int getMenuSlot(){
		return this.menuSlot;
	}
	public String getName(){
		return this.name;
	}
	
	public static FactionSound getSound(int id){
		return idSound.get(id);
	}
	
	public static FactionSound getSoundFromSlot(int slot){
		return menuSound.get(slot);
	}
	
	public static int getSoundsUnlocked(Faction faction){
		long level = faction.getLevel();
		if(level < 6) return 9;
		else if(level > 5 && level < 12) return 16;
		else if(level > 11 && level < 18) return 21;
		else if(level > 17 && level < 25) return 24;
		else if(level == 25) return 25;
		else return 0;
	}
	
	public Material getMaterial(){
		switch(this.levelRequired){
		case 1: return Material.GREEN_RECORD;
		case 6: return Material.RECORD_6;
		case 12: return Material.RECORD_3;
		case 18: return Material.RECORD_4;
		case 25: return Material.RECORD_11;
		}
		return Material.AIR;
	}
}
