package me.recolance.factions.faction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.recolance.factions.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Info{

	private String name;
	private String description;
	private ItemStack icon;
	private FactionSound sound;
	private UUID leader;
	private FactionStatus status;
	private Location home;
	private long lastActive;
	private final long dateCreated;
	private List<String> activityLog;
	private List<String> rules;
	
	//This method should only run when loading info from the database.
	public Info(String name, String description, ItemStack icon, FactionSound sound, UUID leader, FactionStatus status, Location home, long lastActive,
				   long dateCreated, List<String> activityLog, List<String> rules){
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.sound = sound;
		this.leader = leader;
		this.status = status;
		this.home = home;
		this.lastActive = lastActive;
		this.dateCreated = dateCreated;
		this.activityLog = activityLog;
		this.rules = rules;
	}
	
	//This method should only run when creating a new faction.
	public Info(String name, ItemStack icon, FactionSound sound, UUID leader){
		this.name = Util.setStringColors(name);
		this.description = Util.setStringColors("&eThe &6" + name + " &efaction!");
		this.icon = icon;
		this.sound = sound;
		this.leader = leader;
		this.status = FactionStatus.CLOSED;
		this.home = Bukkit.getWorld("world").getSpawnLocation();
		this.lastActive = System.currentTimeMillis();
		this.dateCreated = System.currentTimeMillis();
		this.activityLog = new ArrayList<String>();
		this.rules = getDefaultRules();
	}
	
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public ItemStack getIcon(){
		return icon;
	}

	public void setIcon(ItemStack icon){
		this.icon = icon;
	}

	public FactionSound getSound(){
		return sound;
	}

	public void setSound(FactionSound sound){
		this.sound = sound;
	}

	public UUID getLeader(){
		return leader;
	}

	public void setLeader(UUID leader){
		this.leader = leader;
	}

	public FactionStatus getStatus(){
		return status;
	}

	public void setStatus(FactionStatus status){
		this.status = status;
	}

	public Location getHome(){
		return home;
	}

	public void setHome(Location home){
		this.home = home;
	}

	public long getLastActive(){
		return lastActive;
	}

	public void setLastActive(long lastActive){
		this.lastActive = lastActive;
	}

	public List<String> getActivityLog(){
		return activityLog;
	}

	public void setActivityLog(List<String> activityLog){
		this.activityLog = activityLog;
	}

	public List<String> getRules(){
		return rules;
	}

	public void setRules(List<String> rules){
		this.rules = rules;
	}

	public long getDateCreated(){
		return dateCreated;
	}

	//Method cleans things up a bit.
	private static ArrayList<String> getDefaultRules(){
		ArrayList<String> rules = new ArrayList<String>();
		rules.add(Util.setStringColors("&9Add Rule: &e/f rule add <rule>"));
		rules.add(Util.setStringColors("&aAdds a rule to this rule list."));
		rules.add(Util.setStringColors(""));
		rules.add(Util.setStringColors("&9Delete Rule: &e/f rule delete"));
		rules.add(Util.setStringColors("&aDeletes the last rule in this rule list."));
		rules.add(Util.setStringColors(""));
		rules.add(Util.setStringColors("&9Delete Rule Line: &e/f rule delete <line-#>"));
		rules.add(Util.setStringColors("&aDeletes the rule at the line provided."));
		rules.add(Util.setStringColors(""));
		rules.add(Util.setStringColors("&9Set Rule: &e/f rule set <line-#> <rule>"));
		rules.add(Util.setStringColors("&aSets the rule at the line provided."));
		rules.add(Util.setStringColors(""));
		rules.add(Util.setStringColors("&9Insert Rule: &e/f rule insert <line-#> <rule>"));
		rules.add(Util.setStringColors("&aInsert a rule at the line provided."));
		rules.add(Util.setStringColors(""));
		rules.add(Util.setStringColors("&8These are default rules to help you,"));
		rules.add(Util.setStringColors("&8and can be deleted."));
		return rules;
	}
}
