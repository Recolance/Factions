package me.recolance.factions.faction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.recolance.factions.controller.Controller;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Serialization;
import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;

public class Faction{

	private final UUID id;
	private Info info;
	private List<Member> members;
	private List<Rank> ranks;
	private List<ClaimedLand> claimedLand;
	private Relations relations;
	private Vault vault;
	private List<Warp> warps;
	private HashMap<StatType, Long> stats;
	private HashMap<FactionChallenge, CompletedChallenge> completedChallenges;
	private boolean save;
	private boolean saveVault;
	
	//this method should be called when loading faction from database.
	public Faction(UUID id, Info info, List<Member> members, List<Rank> ranks, List<ClaimedLand> claimedLand, Relations relations, Vault vault, List<Warp> warps, HashMap<StatType, Long> stats, HashMap<FactionChallenge, CompletedChallenge> completedChallenges){
		this.save = false;
		this.saveVault = false;
		this.id = id;
		this.info = info;
		this.members = members;
		this.ranks = ranks;
		this.claimedLand = claimedLand;
		this.relations = relations;
		this.vault = vault;
		this.warps = warps;
		this.stats = stats;
		this.completedChallenges = completedChallenges;
	}

	//
	public Info getInfo(){
		return info;
	}

	public void setInfo(Info info){
		this.info = info;
	}

	public List<Member> getMembers(){
		return members;
	}
	public void setMembers(List<Member> members){
		this.members = members;
	}
	
	public void addMember(Player player){
		Controller.addMember(player, this);
	}
	
	public void sendInvitation(Player whoFrom, Player whoTo){
		Controller.sendFactionInvitation(whoTo, whoFrom, this);
	}
	public static void acceptInvitation(Player player){
		Controller.acceptFactionInvitation(player, FactionInvitation.factionInvitations.get(player).getFactionInvitedTo());
	}
	public static void denyInvitation(Player player){
		Controller.denyFactionInvitation(player);
	}
	
	public void leaveMember(Player player){
		Controller.leaveMember(player, this);
	}
	public void leaveMember(Player player, Inventory inventory, int slot, ItemStack originalItem){
		Controller.leaveMember(player, this, inventory, slot, originalItem);
	}
	
	public void kickMember(Player kicker, UUID playerId){
		Controller.kickMember(kicker, playerId, this);
	}
	public void kickMember(Player kicker, UUID playerId, Inventory inventory, int slot, ItemStack originalItem){
		Controller.kickMember(kicker, playerId, this, inventory, slot, originalItem);
	}
	public List<Rank> getRanks(){
		return ranks;
	}
	public void setMemberRank(Player whoSet, Member member, Rank rank){
		Controller.setMemberRank(whoSet, member, rank);
	}
	public void setRanks(List<Rank> ranks){
		this.ranks = ranks;
	}
	public void removeRank(Player player, Rank rank){
		Controller.removeRank(player, this, rank);
	}
	public Rank addRank(Player player, String name){
		return Controller.addRank(player, this, name);
	}

	public List<ClaimedLand> getClaimedLand(){
		return claimedLand;
	}
	public void setClaimedLand(List<ClaimedLand> claimedLand){
		this.claimedLand = claimedLand;
	}
	public boolean hasClaimedLand(Chunk chunk){
		String chunkS = Serialization.chunkToString(chunk);
		if(DataHolder.getChunkClaimedLandMap().containsKey(chunkS) && DataHolder.getFactionIdFactionMap().get(DataHolder.getChunkClaimedLandMap().get(chunkS).getFaction()) == this) return true;
		return false;
	}
	public boolean hasClaimedLand(String chunk){
		if(DataHolder.getChunkClaimedLandMap().containsKey(chunk) && DataHolder.getFactionIdFactionMap().get(DataHolder.getChunkClaimedLandMap().get(chunk).getFaction()) == this) return true;
		return false;
	}
	public int getMaxClaimedLand(){
		int land = 30;
		if(hasChallengeCompleted(FactionChallenge.FACTION_LEVEL_1)) land = land + 20;
		if(hasChallengeCompleted(FactionChallenge.FACTION_LEVEL_2)) land = land + 20;
		if(hasChallengeCompleted(FactionChallenge.FACTION_LEVEL_3)) land = land + 20;
		return land;
	}

	public Relations getRelations(){
		return relations;
	}
	public void setRelations(Relations relations){
		this.relations = relations;
	}

	public Vault getVault(){
		return vault;
	}
	public void setVault(Vault vault){
		this.vault = vault;
	}
	public void depositCoins(int amount, Player player){
		Controller.depositCoins(amount, player, this);
	}
	public void depositChallengeCoinsO(int amount){
		Controller.depositChallengeCoins(amount, this);
	}
	public void depositOverflowingPowerCoins(int amount){
		Controller.depositOverFlowingPower(amount, this);
	}
	public void withdrawlCoins(int amount, Player player, Member member){
		Controller.withdrawlCoins(amount, player, member, this.vault);
	}

	public List<Warp> getWarps(){
		return warps;
	}
	public void setWarps(List<Warp> warps){
		this.warps = warps;
	}
	public int getMaxWarps(){
		int warps = 3;
		if(hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_1)) warps = warps + 2;
		if(hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_2)) warps = warps + 2;
		if(hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_3)) warps = warps + 2;
		return warps;
	}
	public void addWarp(String name, Location loc, Player player){
		Controller.addWarp(this, name, loc, player);
	}
	public void removeWarp(Player player, Warp warp){
		Controller.removeWarp(this, player, warp);
	}
	public void useWarp(Player player, Warp warp){
		Controller.useWarp(player, warp);
	}

	public HashMap<StatType, Long> getStats(){
		return stats;
	}
	public void setStats(HashMap<StatType, Long> stats){
		this.stats = stats;
	}
	public long getStat(StatType stat){
		return stats.get(stat);
	}
	public void setStat(StatType stat, long amount){
		stats.put(stat, amount);
	}
	public void addStat(StatType stat, long amount){
		setStat(stat, getStat(stat) + amount);
	}
	public float getKDR(){
		if(getStat(StatType.PLAYERS_KILLED) == 0.00 || (getStat(StatType.DEATHS_NON_PLAYER) == 0.00 && getStat(StatType.DEATHS_PLAYER) == 0.00)) return 0.00F;
		float value = (float)getStat(StatType.PLAYERS_KILLED) / (float)(getStat(StatType.DEATHS_NON_PLAYER) + getStat(StatType.DEATHS_PLAYER));
		DecimalFormat format = new DecimalFormat("###.##");
		return Float.valueOf(format.format(value));
	}

	public HashMap<FactionChallenge, CompletedChallenge> getCompletedChallenges(){
		return completedChallenges;
	}
	public void setCompletedChallenges(HashMap<FactionChallenge, CompletedChallenge> completedChallenges){
		this.completedChallenges = completedChallenges;
	}
	public CompletedChallenge getCompletedChallenge(FactionChallenge challenge){
		return this.completedChallenges.get(challenge);
	}
	public void setChallengeCompleted(FactionChallenge challenge, CompletedChallenge completed){
		this.completedChallenges.put(challenge, completed);
	}
	public boolean hasChallengeCompleted(FactionChallenge challenge){
		if(completedChallenges.containsKey(challenge)) return true;
		return false;
	}

	public UUID getId(){
		return id;
	}
	
	public String getName(){
		return this.info.getName();
	}
	
	public String getBareName(){
		return Util.stripStringColors(Util.setStringColors(this.info.getName()));
	}
	
	public String getDescription(){
		return this.info.getDescription();
	}
	
	public ItemStack getIcon(){
		return this.info.getIcon();
	}
	
	public FactionSound getSound(){
		return this.info.getSound();
	}
	
	public UUID getLeader(){
		return this.info.getLeader();
	}
	
	public FactionStatus getStatus(){
		return this.info.getStatus();
	}
	public boolean isOpen(){
		if(this.info.getStatus() == FactionStatus.OPEN) return true;
		return false;
	}
	public boolean isClosed(){
		if(this.info.getStatus() == FactionStatus.CLOSED) return true;
		return false;
	}
	
	public Location getHome(){
		return this.info.getHome();
	}
	public void teleportHome(Player player){
		Controller.teleportHome(player, this);
	}
	public void setHome(Player player){
		if(Controller.setHome(player, this)){
			addActivityLogEntry("&eNew faction home location set.");
			sendMessage("&6" + player.getName() + " &ehas set a new &6faction home&e.");
			this.info.setHome(player.getLocation());
		}
	}
	public void unsetHome(Player player){
		if(Controller.unsetHome(player, this)){
			addActivityLogEntry("&eFaction home unset.");
			sendMessage("&6" + player.getName() + " &ehas unset the &6faction home&e.");
			this.info.setHome(Bukkit.getWorld("world").getSpawnLocation());
		}
	}
	
	public long getLastActive(){
		return this.info.getLastActive();
	}
	
	public void updateLastActive(){
		this.info.setLastActive(System.currentTimeMillis());
	}
	
	public long getDateCreated(){
		return this.info.getDateCreated();
	}
	
	public List<String> getActivityLog(){
		return this.info.getActivityLog();
	}
	public void addActivityLogEntry(String entry){
		Controller.addActivityLine(this, entry);
	}
	
	public List<String> getRules(){
		return this.info.getRules();
	}
	
	public List<UUID> getEnemies(){
		return this.relations.getEnemies();
	}
	public void removeEnemy(Faction faction){
		this.relations.getEnemies().remove(faction.getId());
	}
	public boolean hasEnemy(Faction faction){
		if(this.relations.getEnemies().contains(faction.getId())) return true;
		return false;
	}
	
	public List<UUID> getEnemiedBy(){
		return this.relations.getEnemiedBy();
	}
	public void removeEnemiedBy(Faction faction){
		this.relations.getEnemiedBy().remove(faction.getId());
	}
	public boolean isEnemiedBy(Faction faction){
		if(this.relations.getEnemiedBy().contains(faction.getId())) return true;
		return false;
	}
	
	public List<UUID> getAlliances(){
		return this.relations.getAlliances();
	}
	public void removeAlliance(Faction faction){
		this.relations.getAlliances().remove(faction.getId());
	}
	public boolean hasAlliance(Faction faction){
		if(this.relations.getAlliances().contains(faction.getId())) return true;
		return false;
	}
	public void sendAllyRequest(Player player, Faction whoTo){
		Controller.sendAllyInvitation(player, this, whoTo);
	}
	public void acceptAlly(Faction whoTo, Faction whoFrom){
		Controller.acceptAllyInvitation(whoTo, whoFrom);
	}
	public void denyAlly(Faction whoTo){
		Controller.denyAllyInvitation(whoTo);
	}
	
	public void setNeutral(Faction whoTo){
		Controller.setNeutral(whoTo, this);
	}
	
	public void setEnemy(Faction whoTo){
		Controller.setEnemies(this, whoTo);
	}
	
	public List<Player> getEditRelationPlayers(){
		List<Player> applicable = new ArrayList<Player>();
		for(Player player : getOnlinePlayers()){
			if(FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) applicable.add(player);
		}
		return applicable;
	}
	
	public long getLevel(){
		return stats.get(StatType.LEVEL);
	}
	
	public long getExp(){
		return stats.get(StatType.EXPERIENCE);
	}
	public long getRequiredExp(){
		return Controller.requiredEXP(this);
	}
	public void addExp(long amount, Player player){
		stats.put(StatType.EXPERIENCE, getStat(StatType.EXPERIENCE) + amount);
		FactionUtil.checkFactionLevelUp(this, player);
	}
	
	public void setPower(long amount){
		stats.put(StatType.POWER, amount);
	}
	
	public long getPower(){
		return stats.get(StatType.POWER);
	}
	
	public void addPower(long amount, Player player){
		Controller.addPower(amount, player, this);
	}
	
	public void removePower(long amount, Player player){
		Controller.removePower(amount, player, this);
	}
	
	public long getScore(){
		return stats.get(StatType.SCORE);
	}
	
	public void setSaveable(boolean save){
		this.save = save;
	}
	
	public boolean isSaveable(){
		return this.save;
	}
	
	public boolean isScoreable(){
		if(isSaveable()) return true;
		return false;
	}
	
	public List<Member> getOnlineMembers(){
		List<Member> players = new ArrayList<Member>();
		for(Member member : members){
			if(Bukkit.getOfflinePlayer(member.getPlayer()).isOnline()) players.add(member);
		}
		return players;
	}
	
	public List<Player> getOnlinePlayers(){
		List<Player> players = new ArrayList<Player>();
		for(Member member : members){
			if(Bukkit.getOfflinePlayer(member.getPlayer()).isOnline()) players.add(Bukkit.getPlayer(member.getPlayer()));
		}
		return players;
	}
	
	public List<Member> getOfflineMembers(){
		List<Member> onlinePlayers = getOnlineMembers();
		List<Member> offlinePlayers = new ArrayList<Member>();
		for(Member member : members){
			if(!onlinePlayers.contains(member)) offlinePlayers.add(member);
		}
		return offlinePlayers;
	}
	
	public Rank getDefaultRank(){
		for(Rank rank : ranks){
			if(rank.isDefault()) return rank;
		}
		return null;
	}
	
	public void dropDefaultRank(){
		for(Rank rank : ranks){
			if(rank.isDefault()) rank.setDefault(false);
		}
	}
	
	public static void create(Player player, String name, ItemStack icon, FactionSound sound){
		Controller.create(player, name, icon, sound);
	}
	
	public void disband(Player player){
		Controller.disband(this, player);
	}
	
	public void dropVault(Player player){
		Controller.dropVault(player, this);
	}
	
	public void sendMessage(String message){
		Controller.sendMessage(this, message);
	}
	
	public void showInfo(Player player){
		Controller.who(player, this);
	}
	
	public boolean exists(){
		if(!DataHolder.getAllFactions().contains(this)) return false;
		return true;
	}
	
	public boolean isTabLocked(int tabNumber){
		switch(tabNumber){
		case 2:
			if(hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_1)) return false;
		case 3:
			if(hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_2)) return false;
		case 4:
			if(hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_3)) return false;
		}
		return true;
	}
	
	public boolean isTabLocked(VaultTab tab){
		if(vault.getTab2() == tab) return isTabLocked(2);
		else if(vault.getTab3() == tab) return isTabLocked(3);
		else if(vault.getTab4() == tab) return isTabLocked(4);
		return false;
	}
	
	public void sendRankMessage(Rank rank, String message){
		message = Util.setStringColors(message);
		for(Member member : getOnlineMembers()){
			if(member.getRank() == rank) Util.message(Bukkit.getPlayer(member.getPlayer()), message);
		}
	}
	
	public boolean hasWarpChallenge(int slot){
		switch(slot){
		case 0:
		case 1:
		case 2:
			return true;
		case 3:
		case 4: 
			if(hasChallengeCompleted(FactionChallenge.LAND_OVERCLAIMED_1)) return true;
			return false;
		case 5:
		case 6:
			if(hasChallengeCompleted(FactionChallenge.LAND_OVERCLAIMED_2)) return true;
			return false;
		case 7:
		case 8:
			if(hasChallengeCompleted(FactionChallenge.LAND_OVERCLAIMED_3)) return true;
			return false;
		}
		return false;
	}
	
	public int getWarpLimit(){
		int limit = 3;
		if(hasChallengeCompleted(FactionChallenge.LAND_OVERCLAIMED_1)) limit = limit + 2;
		if(hasChallengeCompleted(FactionChallenge.LAND_OVERCLAIMED_2)) limit = limit + 2;
		if(hasChallengeCompleted(FactionChallenge.LAND_OVERCLAIMED_3)) limit = limit + 2;
		return limit;
	}
	
	public boolean isLandPageLocked(int page){
		switch(page){
		case 2: if(hasChallengeCompleted(FactionChallenge.FACTION_LEVEL_1)) return false; return true;
		case 3: if(hasChallengeCompleted(FactionChallenge.FACTION_LEVEL_2)) return false; return true;
		case 4: if(hasChallengeCompleted(FactionChallenge.FACTION_LEVEL_3)) return false; return true;
		}
		return true;
	}
	
	public FactionChallenge getLandPageChallenge(int page){
		switch(page){
		case 2: return FactionChallenge.FACTION_LEVEL_1;
		case 3: return FactionChallenge.FACTION_LEVEL_2;
		case 4: return FactionChallenge.FACTION_LEVEL_3;
		}
		return null;
	}
	
	public List<Player> getPowerMonitoringOnlinePlayers(){
		List<Player> onlineMonitoring = new ArrayList<Player>();
		for(Member member: members){
			if(member.isOnline() && member.isMonitoringPower()) onlineMonitoring.add(Bukkit.getPlayer(member.getPlayer()));
		}
		return onlineMonitoring;
	}
	
	public boolean hasSoundUnlocked(FactionSound sound){
		if(getLevel() >= sound.getLevelRequirement()) return true;
		return false;
	}
	
	public void claimLand(String chunk, Player player){
		Controller.claimLand(chunk, player, this);
	}
	
	public void overClaimLand(ClaimedLand land, Player player){
		Controller.overClaim(land, player, this);
	}
	
	public void unClaimLand(ClaimedLand land, Player player){
		Controller.unClaimLand(land, player, this);
	}
	
	public void changeName(Player player, String name){
		DataHolder.getFactionBareNameFactionMap().remove(this.info.getName().toLowerCase());
		DataHolder.getFactionNameFactionMap().remove(this.info.getName().toLowerCase());
		Controller.changeName(this, player, name);
		DataHolder.getFactionBareNameFactionMap().put(this.info.getName().toLowerCase(), this);
		DataHolder.getFactionNameFactionMap().put(this.info.getName().toLowerCase(), this);
	}
	
	public void changeIcon(Player player, ItemStack icon){
		Controller.changeIcon(this, player, icon);
	}
	
	public void changeSound(Player player, FactionSound sound){
		Controller.changeSound(this, player, sound);
	}
	
	public void changeHome(Player player, Location loc){
		addActivityLogEntry("&eNew faction home location set.");
		sendMessage("&6" + player.getName() + " &ehas set a new &6faction home&e.");
		info.setHome(player.getLocation());
	}
	
	public void changeDescription(Player player, String description){
		Controller.changeDescription(this, player, description);
	}
	
	public void updateScore(){
		Controller.updateScores(this);
	}
	
	public boolean isVaultSaveable(){
		return this.saveVault;
	}
	
	public void setVaultSaveable(boolean saveVault){
		this.saveVault = saveVault;
	}
	
	public void addRule(Player player, String message){
		Controller.addRule(this, player, message);
	}
	
	public void deleteRule(Player player, int line){
		Controller.deleteRule(this, player, line);
	}
	
	public void insertRule(Player player, String message, int line){
		Controller.insertRule(this, player, message, line);
	}
	
	public void setRule(Player player, String message, int line){
		Controller.setRule(this, player, message, line);
	}
}
