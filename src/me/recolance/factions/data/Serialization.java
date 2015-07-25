package me.recolance.factions.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.recolance.factions.faction.ClaimedLand;
import me.recolance.factions.faction.CompletedChallenge;
import me.recolance.factions.faction.FactionChallenge;
import me.recolance.factions.faction.FactionSound;
import me.recolance.factions.faction.FactionStatus;
import me.recolance.factions.faction.StatType;
import me.recolance.factions.faction.VaultTab;
import me.recolance.factions.faction.Warp;
import me.recolance.globalutil.utils.InventorySerialization;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("deprecation")
public class Serialization{

	public static String iconToString(ItemStack icon){
		return icon.getTypeId() + ":" + icon.getDurability();
	}
	public static ItemStack stringToIcon(String iconAsString){
		String[] iconPart = iconAsString.split(":");
		return new ItemStack(Integer.parseInt(iconPart[0]), 1, Short.parseShort(iconPart[1]));
	}
	
	public static int soundToInt(FactionSound sound){
		return sound.getId();
	}
	public static FactionSound intToSound(int soundAsInt){
		return FactionSound.getSound(soundAsInt);
	}
	
	public static int statusToInt(FactionStatus status){
		return status.getId();
	}
	public static FactionStatus intToStatus(int statusAsInt){
		return FactionStatus.getFactionStatus(statusAsInt);
	}
	
	public static String homeToString(Location home){
		return home.getWorld().getName() + ":" + home.getX() + ":" + home.getY() + ":" + home.getZ() + ":" + home.getYaw() + ":" + home.getPitch();
	}
	public static Location stringToHome(String homeAsString){
		String[] homePart = homeAsString.split(":");
		return new Location(Bukkit.getWorld(homePart[0]), Double.parseDouble(homePart[1]), Double.parseDouble(homePart[2]), Double.parseDouble(homePart[3]), Float.parseFloat(homePart[4]), Float.parseFloat(homePart[5]));
	}
	
	public static String activityLogToString(List<String> log){
		if(log == null || log.isEmpty()) return "";
		StringBuilder logString = new StringBuilder();
		for(String logPart : log){
			logString.append(logPart + "¼");
		}
		logString.setLength(logString.length() - 1);
		return logString.toString();
	}
	public static ArrayList<String> stringToActivityLog(String logAsString){
		ArrayList<String> activityLog = new ArrayList<String>();
		if(logAsString == null || logAsString.equals("")) return activityLog;
		for(String logPart : logAsString.split("¼")){
			activityLog.add(logPart);
		}
		return activityLog;
	}
	
	public static String rulesToString(List<String> rules){
		if(rules == null || rules.isEmpty()) return "";
		StringBuilder rulesString = new StringBuilder();
		for(String rulePart : rules){
			rulesString.append(rulePart + "¼");
		}
		rulesString.setLength(rulesString.length() - 1);
		return rulesString.toString();
	}
	public static ArrayList<String> stringToRules(String rulesAsString){
		ArrayList<String> rules = new ArrayList<String>();
		if(rulesAsString == null || rulesAsString.equals("")) return rules;
		for(String rulesPart : rulesAsString.split("¼")){
			rules.add(rulesPart);
		}
		return rules; 
	}
	
	public static int monitoringPowerToInt(boolean isMonitoringPower){
		return (isMonitoringPower == true) ? 1 : 0;
	}
	public static boolean intToMonitoringPower(int monitoringPowerAsInt){
		return (monitoringPowerAsInt == 1) ? true : false;
	}
	
	public static String permissionsToString(List<Integer> permissions){
		if(permissions == null || permissions.isEmpty()) return "";
		StringBuilder permissionsString = new StringBuilder();
		for(int permission : permissions){
			permissionsString.append(permission + ",");
		}
		permissionsString.setLength(permissionsString.length() - 1);
		return permissionsString.toString();
	}
	public static List<Integer> stringToPermissions(String permissionsAsString){
		ArrayList<Integer> permissions = new ArrayList<Integer>();
		if(permissionsAsString == null || permissionsAsString.equals("")) return permissions;
		for(String permissionsPart : permissionsAsString.split(",")){
			permissions.add(Integer.parseInt(permissionsPart));
		}
		return permissions;
	}
	
	public static int defaultToInt(boolean isDefault){
		return (isDefault == true) ? 1 : 0;
	}
	public static boolean intToDefault(int defaultAsInt){
		return (defaultAsInt == 1) ? true : false;
	}
	
	public static int leaderToInt(boolean isLeader){
		return (isLeader == true) ? 1 : 0;
	}
	public static boolean intToLeader(int leaderAsInt){
		return (leaderAsInt == 1) ? true : false;
	}
	
	public static String claimedLandToString(List<ClaimedLand> claimedLand){
		if(claimedLand == null || claimedLand.isEmpty()) return "";
		StringBuilder claimedLandString = new StringBuilder();
		for(ClaimedLand land : claimedLand){
			Chunk chunk = Serialization.stringToChunk(land.getLocation());
			claimedLandString.append(land.getFaction().toString() + ";" + chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ() + ";" + land.getClaimer().toString() + ";" + land.getDateClaimed() + "¼");
		}
		claimedLandString.setLength(claimedLandString.length() - 1);
		return claimedLandString.toString();
	}
	public static List<ClaimedLand> stringToClaimedLand(String claimedLandAsString){
		ArrayList<ClaimedLand> claimedLand = new ArrayList<ClaimedLand>();
		if(claimedLandAsString == null || claimedLandAsString.equals("")) return claimedLand;
		for(String claimedLandSection : claimedLandAsString.split("¼")){
			String[] claimedLandPart = claimedLandSection.split(";");
			claimedLand.add(new ClaimedLand(UUID.fromString(claimedLandPart[0]), claimedLandPart[1], UUID.fromString(claimedLandPart[2]), Long.valueOf(claimedLandPart[3])));
		}
		return claimedLand;
	}
	
	public static String enemiesToString(List<UUID> enemies){
		if(enemies == null || enemies.isEmpty()) return "";
		StringBuilder enemiesString = new StringBuilder();
		for(UUID enemy : enemies){
			enemiesString.append(enemy.toString() + ",");
		}
		enemiesString.setLength(enemiesString.length() - 1);
		return enemiesString.toString();
	}
	public static List<UUID> stringToEnemies(String enemiesAsString){
		ArrayList<UUID> enemies = new ArrayList<UUID>();
		if(enemiesAsString == null || enemiesAsString.equals("")) return enemies;
		for(String enemy : enemiesAsString.split(",")){
			enemies.add(UUID.fromString(enemy));
		}
		return enemies;
	}
	
	public static String enemiedByToString(List<UUID> enemiedBy){
		if(enemiedBy == null || enemiedBy.isEmpty()) return "";
		StringBuilder enemiedByString = new StringBuilder();
		for(UUID alliance : enemiedBy){
			enemiedByString.append(alliance.toString() + ",");
		}
		enemiedByString.setLength(enemiedByString.length() - 1);
		return enemiedByString.toString();
	}
	public static List<UUID> stringToEnemiedBy(String enemiedByAsString){
		ArrayList<UUID> enemiedBy = new ArrayList<UUID>();
		if(enemiedByAsString == null || enemiedByAsString.equals("")) return enemiedBy;
		for(String allliance : enemiedByAsString.split(",")){
			enemiedBy.add(UUID.fromString(allliance));
		}
		return enemiedBy;
	}
	
	public static String alliancesToString(List<UUID> alliances){
		if(alliances == null || alliances.isEmpty()) return "";
		StringBuilder alliancesString = new StringBuilder();
		for(UUID alliance : alliances){
			alliancesString.append(alliance.toString() + ",");
		}
		alliancesString.setLength(alliancesString.length() - 1);
		return alliancesString.toString();
	}
	public static List<UUID> stringToAlliances(String alliancesAsString){
		ArrayList<UUID> alliances = new ArrayList<UUID>();
		if(alliancesAsString == null || alliancesAsString.equals("")) return alliances;
		for(String allliance : alliancesAsString.split(",")){
			alliances.add(UUID.fromString(allliance));
		}
		return alliances;
	}
	
	public static String vaultTabToString(VaultTab tab){
		return InventorySerialization.toBase64(tab.getContents()) + "¼" + tab.getName() + "¼" + tab.getIcon().getTypeId() + "¼" + tab.getIcon().getDurability();
	}
	public static VaultTab stringToVaultTab(String tabAsString, int tabNumber){
		String[] tabPart = tabAsString.split("¼");
		Inventory vaultContents = Bukkit.createInventory(null, 54, "Vault");
		Inventory tempInv = InventorySerialization.fromBase64(tabPart[0]);
		vaultContents.setContents(tempInv.getContents());
		return new VaultTab(tabPart[1], new ItemStack(Integer.parseInt(tabPart[2]), 1, Short.parseShort(tabPart[3])), vaultContents, tabNumber);
	}
	
	public static String vaultLogToString(List<String> log){
		if(log == null || log.isEmpty()) return "";
		StringBuilder logString = new StringBuilder();
		for(String logPart : log){
			logString.append(logPart + "¼");
		}
		logString.setLength(logString.length() - 1);
		return logString.toString();
	}
	public static ArrayList<String> stringToVaultLog(String logAsString){
		ArrayList<String> activityLog = new ArrayList<String>();
		if(logAsString == null || logAsString.equals("")) return activityLog;
		for(String logPart : logAsString.split("¼")){
			activityLog.add(logPart);
		}
		return activityLog;
	}
	
	public static String warpsToString(List<Warp> warps){
		if(warps == null || warps.isEmpty()) return "";
		StringBuilder warpsString = new StringBuilder();
		for(Warp warp : warps){
			warpsString.append(warp.getName() + ";" + warp.getLocation().getWorld().getName() + ":" + warp.getLocation().getX() + ":" + warp.getLocation().getY() + ":" + warp.getLocation().getZ() + ":" + warp.getLocation().getYaw() + ":" + warp.getLocation().getPitch() + ";" + warp.getCreator().toString() + ";" + warp.getDateCreated() + "¼");
		}
		warpsString.setLength(warpsString.length() - 1);
		return warpsString.toString();
	}
	public static List<Warp> stringToWarps(String warpsAsString){
		ArrayList<Warp> warps = new ArrayList<Warp>();
		if(warpsAsString == null || warpsAsString.equals("")) return warps;
		for(String warp : warpsAsString.split("¼")){
			String[] warpPart = warp.split(";");
			String[] locationPart = warpPart[1].split(":");
			Location location = new Location(Bukkit.getWorld(locationPart[0]), Double.parseDouble(locationPart[1]), Double.parseDouble(locationPart[2]), Double.parseDouble(locationPart[3]), Float.parseFloat(locationPart[4]), Float.parseFloat(locationPart[5]));
			warps.add(new Warp(warpPart[0], location, UUID.fromString(warpPart[2]), Long.parseLong(warpPart[3])));
		}
		return warps;
	}
	
	public static String statsToString(HashMap<StatType, Long> stats){
		if(stats == null || stats.isEmpty()) return "";
		StringBuilder statsString = new StringBuilder();
		for(StatType stat : stats.keySet()){
			statsString.append(stat.getId() + "," + stats.get(stat) + ";");
		}
		statsString.setLength(statsString.length() - 1);
		return statsString.toString();
	}
	public static HashMap<StatType, Long> stringToStats(String statsAsString){
		HashMap<StatType, Long> stats = new HashMap<StatType, Long>();
		if(statsAsString == null || statsAsString.equals("")) return stats;
		for(String statSection : statsAsString.split(";")){
			String[] statPart = statSection.split(",");
			stats.put(StatType.getStatType(Integer.parseInt(statPart[0])), Long.parseLong(statPart[1]));
		}
		return stats;
	}
	
	public static String completedChallengesToString(HashMap<FactionChallenge, CompletedChallenge> challenges){
		if(challenges == null || challenges.isEmpty()) return "";
		StringBuilder challengesString = new StringBuilder();
		for(CompletedChallenge challenge : challenges.values()){
			challengesString.append(challenge.getChallenge().getId() + "," + challenge.getDateCompleted() + "," + challenge.getCompletedBy() + ";");
		}
		challengesString.setLength(challengesString.length() - 1);
		return challengesString.toString();
	}
	public static HashMap<FactionChallenge, CompletedChallenge> stringToCompletedChallenges(String challengesAsString){
		HashMap<FactionChallenge, CompletedChallenge> completedChallenges = new HashMap<FactionChallenge, CompletedChallenge>();
		if(challengesAsString == null || challengesAsString.equals("")) return completedChallenges;
		for(String challenge : challengesAsString.split(";")){
			String[] challengePart = challenge.split(",");
			FactionChallenge chal = FactionChallenge.getFactionChallenge(Integer.parseInt(challengePart[0]));
			completedChallenges.put(chal, new CompletedChallenge(UUID.fromString(challengePart[2]), Long.parseLong(challengePart[1]), chal));
		}
		return completedChallenges;
	}
	
	public static String chunkToString(Chunk chunk){
		HashMap<Chunk, String> loadedChunks = DataHolder.getAlreadySerializedChunks();
		if(loadedChunks.containsKey(chunk)){
			return DataHolder.getAlreadySerializedChunks().get(chunk);
		}else{
			String ser = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
			loadedChunks.put(chunk, ser);
			return ser;
		}
	}
	public static Chunk stringToChunk(String cs){
		String[] cp = cs.split(":");
		return Bukkit.getWorld(cp[0]).getChunkAt(Integer.parseInt(cp[1]), Integer.parseInt(cp[2]));
	}
}
