package me.recolance.factions.util;

import java.util.UUID;

import me.recolance.factions.controller.Controller;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Serialization;
import me.recolance.factions.faction.AllianceInvitation;
import me.recolance.factions.faction.ClaimedLand;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.faction.FactionChallenge;
import me.recolance.factions.faction.Member;
import me.recolance.factions.faction.Rank;
import me.recolance.factions.faction.RankPermission;
import me.recolance.factions.faction.StatType;
import me.recolance.factions.faction.Vault;
import me.recolance.factions.faction.VaultTab;
import me.recolance.factions.faction.Warp;
import me.recolance.factions.menu.MenuUtil;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.bukkit.BukkitUtil;

public class FactionUtil{
	
	public static boolean isInFaction(Player player){
		return isInFaction(player.getUniqueId());
	}
	public static boolean isInFaction(UUID playerId){
		if(DataHolder.getPlayerIdMemberMap().containsKey(playerId)) return true;
		return false;
	}
	
	public static Faction getPlayerFaction(Player player){
		return getPlayerFaction(player.getUniqueId());
	}
	public static Faction getPlayerFaction(Member member){
		return getPlayerFaction(member.getPlayer());
	}
	public static Faction getPlayerFaction(UUID playerId){
		if(!isInFaction(playerId)) return null;
		return DataHolder.getFactionIdFactionMap().get(DataHolder.getPlayerIdMemberMap().get(playerId).getFaction());
	}
	
	public static Member getPlayerMember(Player player){
		return getPlayerMember(player.getUniqueId());
	}
	public static Member getPlayerMember(UUID playerId){
		return DataHolder.getPlayerIdMemberMap().get(playerId);
	}
	
	public static boolean isChunkClaimed(String chunk){
		if(DataHolder.getChunkClaimedLandMap().containsKey(chunk)) return true;
		return false;
	}
	public static boolean isChunkClaimed(Chunk chunk){
		if(DataHolder.getChunkClaimedLandMap().containsKey(Serialization.chunkToString(chunk))) return true;
		return false;
	}
	
	public static Faction getChunkFaction(String chunk){
		ClaimedLand land = DataHolder.getChunkClaimedLandMap().get(chunk);
		if(land == null) return null;
		return DataHolder.getFactionIdFactionMap().get(land.getFaction());
	}
	public static Faction getChunkFaction(Chunk chunk){
		ClaimedLand land = DataHolder.getChunkClaimedLandMap().get(Serialization.chunkToString(chunk));
		if(land == null) return null;
		return DataHolder.getFactionIdFactionMap().get(land.getFaction());
	}
	
	public static ClaimedLand getChunkClaimedLand(Chunk chunk){
		return DataHolder.getChunkClaimedLandMap().get(Serialization.chunkToString(chunk));
	}
	
	public static boolean isEnemyChunk(Chunk chunk, Faction faction){
		if(!isChunkClaimed(chunk)) return false;
		else if(faction.hasEnemy(getChunkFaction(chunk))) return true;
		return false;
	}
	
	public static boolean isAllyChunk(Chunk chunk, Faction faction){
		if(!isChunkClaimed(chunk)) return false;
		else if(getChunkFaction(chunk).hasAlliance(faction)) return true;
		return false;
	}
	
	public static boolean isEnemiedByChunk(Chunk chunk, Faction faction){
		if(!isChunkClaimed(chunk)) return false;
		else if(faction.isEnemiedBy(getChunkFaction(chunk))) return true;
		return false;
	}
	
	public static boolean hasPermission(Player player, RankPermission permission){
		return hasPermission(getPlayerMember(player), permission);
	}
	public static boolean hasPermission(Member member, RankPermission permission){
		if(member == null) return false;
		if(member.getRank().getPermissions().contains(permission.getId())) return true;
		return false;
	}
	
	public static boolean isInFactionChat(Player player){
		if(DataHolder.inFChat.contains(player.getUniqueId())) return true;
		return false;
	}
	
	public static Faction getFactionFromId(UUID id){
		return DataHolder.getFactionIdFactionMap().get(id);
	}
	
	public static Faction getFactionFromName(String name){
		if(DataHolder.getFactionNameFactionMap().containsKey(Util.setStringColors(name.toLowerCase()))) return DataHolder.getFactionNameFactionMap().get(Util.setStringColors(name.toLowerCase()));
		if(DataHolder.getFactionBareNameFactionMap().containsKey(Util.stripStringColors(Util.setStringColors(name.toLowerCase())))) return DataHolder.getFactionBareNameFactionMap().get(Util.stripStringColors(name.toLowerCase()));
		return null;
	}
	
	public static Rank getRankFromName(String name, Faction faction){
		for(Rank rank : faction.getRanks()){
			if(rank.getName().toLowerCase().equals(name.toLowerCase())) return rank;
		}
		return null;
	}
	
	public static boolean isLeader(Player player){
		return isLeader(player.getUniqueId());
	}
	public static boolean isLeader(UUID playerId){
		if(!isInFaction(playerId)) return false;
		if(DataHolder.getPlayerIdMemberMap().get(playerId).getRank().isLeader()) return true;
		else return false;
	}
	
	public static boolean isNameInUse(String name){
		if(DataHolder.getFactionNameFactionMap().containsKey(Util.setStringColors(name))) return true;
		if(DataHolder.getFactionBareNameFactionMap().containsKey(Util.stripStringColors(name))) return true;
		return false;
	}
	
	public static boolean isApplicableFactionName(String name, Player player){
		name = Util.setStringColors(name);
		if(isNameInUse(name)) Util.message(player, "&cAnother faction already has that name.");
		else if(!Util.stringHasMaxWithoutColor(name, 14)) Util.message(player, "&cFaction names must be less than 14 characters. Colors are not included.");
		else if(!Util.stringHasMinWithoutColor(name, 2)) Util.message(player, "&cFaction names must be at least 2 characters. Colors are not included.");
		else if(Util.stringHasMagic(name)) Util.message(player, "&cFaction names cannot contain the magic color code.");
		else if(Util.getStringColors(name) > 5) Util.message(player, "&cFaction names cannot have more than 5 color codes.");
		else if(!Util.stringIsAlphabetical(Util.stripStringColors(name))) Util.message(player, "&cFaction names must only contain letters.");
		else if(!Util.stringIsAppropriate(name)) Util.message(player, "&cFaction names must be appropriate.");
		else{
			String n = name.toLowerCase();
			if(n.contains("admin") || name.contains("moderator") || name.contains("helper") || name.contains("trial") || name.contains("owner")) return false;
			else return true;
		}
		return false;
	}
	public static boolean isApplicableFactionName(String name, Inventory inventory){
		if(name == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
		else{
			name = Util.setStringColors(name);
			if(isNameInUse(name)) MenuUtil.displayAnvilInvalidity(inventory, "Already In Use");
			else if(!Util.stringHasMaxWithoutColor(name, 14)) MenuUtil.displayAnvilInvalidity(inventory, "Name Too Long");
			else if(!Util.stringHasMinWithoutColor(name, 2)) MenuUtil.displayAnvilInvalidity(inventory, "Name Too Short");
			else if(Util.stringHasMagic(name)) MenuUtil.displayAnvilInvalidity(inventory, "No Magic");
			else if(Util.getStringColors(name) > 5) MenuUtil.displayAnvilInvalidity(inventory, "Too Many Colors");
			else if(!Util.stringIsAlphabetical(Util.stripStringColors(name))) MenuUtil.displayAnvilInvalidity(inventory, "Letters Only");
			else if(!Util.stringIsAppropriate(name)) MenuUtil.displayAnvilInvalidity(inventory, "Inappropriate");
			else return true;
		}
		return false;
	}
	
	public static boolean isApplicableDescription(String name, Player player){
		name = Util.setStringColors(name);
		if(!Util.stringIsAppropriate(name)) Util.message(player, "&cFaction descriptions must be appropriate.");
		else return true;
		return false;
	}
	
	public static boolean isRankNameInUse(String name, Faction faction){
		for(Rank rank : faction.getRanks()){
			if(rank.getName().toLowerCase().equals(name.toLowerCase())) return true;
		}
		return false;
	}
	
	public static boolean isApplicableRankName(String name, Inventory inventory, Faction faction){
		if(name == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
		else{
			name = Util.stripStringColors(name);
		    if(!Util.stringIsAlphabetical(name)) MenuUtil.displayAnvilInvalidity(inventory, "Letters Only");
			else if(!Util.stringHasMax(name, 20)) MenuUtil.displayAnvilInvalidity(inventory, "Name Too Long");
			else if(isRankNameInUse(name, faction)) MenuUtil.displayAnvilInvalidity(inventory, "Already In Use");
			else if(!Util.stringIsAppropriate(name)) MenuUtil.displayAnvilInvalidity(inventory, "Inappropriate");
			else return true;
		}
		return false;
	}
	public static boolean isApplicableRankName(String name, Player player, Faction faction){
		name = Util.stripStringColors(name);
		if(!Util.stringIsAlphabetical(name)) Util.message(player, "&cRank names must only contain letters.");		
		else if(!Util.stringHasMax(name, 20)) Util.message(player, "&cRank names must be less than 20 characters.");
		else if(isRankNameInUse(name, faction)) Util.message(player, "&cYour faction already has that rank.");
		else if(!Util.stringIsAppropriate(name)) Util.message(player, "&cRank names must be appropriate.");
		else return true;
		return false;
	}
	
	public static boolean isApplicableAlly(String name, Player player, Inventory inventory){
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) MenuUtil.displayAnvilInvalidity(inventory, "No Permission");
		else if(name == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
		else{
			Faction allyFaction = FactionUtil.getFactionFromName(name);
			if(allyFaction == null) MenuUtil.displayAnvilInvalidity(inventory, "Faction Not Exists");
			else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(allyFaction == faction) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Ally Self");
				else if(faction.getAlliances().size() >= 9) MenuUtil.displayAnvilInvalidity(inventory, "Max Allies");
				else if(allyFaction.getAlliances().size() >= 9) MenuUtil.displayAnvilInvalidity(inventory, "Them Max Allies");
				else if(faction.hasAlliance(allyFaction)) MenuUtil.displayAnvilInvalidity(inventory, "Already Allied");
				else if(faction.isEnemiedBy(allyFaction)) MenuUtil.displayAnvilInvalidity(inventory, "They Enemy You");
				else if(faction.hasEnemy(allyFaction)) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Ally Enemy");
				else if(allyFaction.getEditRelationPlayers().isEmpty()) MenuUtil.displayAnvilInvalidity(inventory, "Nobody Can Accept");
				else if(AllianceInvitation.allianceInvitations.containsKey(allyFaction)) MenuUtil.displayAnvilInvalidity(inventory, "Already Pending");
				else return true;
			}
		}
		return false;
	}
	
	public static boolean isApplicableEnemy(String name, Player player, Inventory inventory){
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) MenuUtil.displayAnvilInvalidity(inventory, "No Permission");
		else{
			Faction enemyFaction = FactionUtil.getFactionFromName(name);
			if(enemyFaction == null) MenuUtil.displayAnvilInvalidity(inventory, "Faction Not Exists");
			else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(enemyFaction == faction) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Enemy Self");
				else if(faction.getEnemies().size() >= 9) MenuUtil.displayAnvilInvalidity(inventory, "Max Enemies");
				else if(faction.getEnemies().contains(enemyFaction.getId())) MenuUtil.displayAnvilInvalidity(inventory, "Already Enemied");
				else if(faction.getAlliances().contains(enemyFaction.getId())) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Enemy Ally");
				else return true;
			}
		}
		return false;
	}
	
	public static boolean isTabNameInUse(String name, Faction faction){
		name = Util.stripStringColors(Util.setStringColors(name));
		Vault vault = faction.getVault();
		if(Util.stripStringColors(vault.getTab1().getName()).equalsIgnoreCase(name)) return true;
		if(Util.stripStringColors(vault.getTab2().getName()).equalsIgnoreCase(name)) return true;
		if(Util.stripStringColors(vault.getTab3().getName()).equalsIgnoreCase(name)) return true;
		if(Util.stripStringColors(vault.getTab4().getName()).equalsIgnoreCase(name)) return true;
		return false;
	}
	
	public static VaultTab getVaultTabFromName(String name, Faction faction){
		name = Util.stripStringColors(name.toLowerCase());
		Vault vault = faction.getVault();
		if(Util.stripStringColors(vault.getTab1().getName().toLowerCase()).equals(name)) return vault.getTab1();
		if(Util.stripStringColors(vault.getTab2().getName().toLowerCase()).equals(name)) return vault.getTab2();
		if(Util.stripStringColors(vault.getTab3().getName().toLowerCase()).equals(name)) return vault.getTab3();
		if(Util.stripStringColors(vault.getTab4().getName().toLowerCase()).equals(name)) return vault.getTab4();
		return null;
	}
	
	public static boolean isApplicableTabName(String name, Player player, Inventory inventory, Faction faction){
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_VAULT)) MenuUtil.displayAnvilInvalidity(inventory, "No Permission");
		else if(name == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
		else{
			name = Util.setStringColors(name);
			if(!Util.stringHasMaxWithoutColor(name, 14)) MenuUtil.displayAnvilInvalidity(inventory, "Too Long");
			else if(Util.stringHasMagic(name)) MenuUtil.displayAnvilInvalidity(inventory, "No Magic");
			else if(Util.getStringColors(name) > 5) MenuUtil.displayAnvilInvalidity(inventory, "Too Many Colors");
			else if(isTabNameInUse(name, faction)) MenuUtil.displayAnvilInvalidity(inventory, "Already In Use");
			else if(!Util.stringIsAlphanumerical(Util.stripStringColors(name))) MenuUtil.displayAnvilInvalidity(inventory, "Alphanumeric Only");
			else return true;
		}
		return false;
	}
	
	public static boolean isWarpNameInUse(String name, Faction faction){
		for(Warp warp : faction.getWarps()){
			if(name.equalsIgnoreCase(warp.getName())) return true;
		}
		return false;
	}
	
	public static boolean isApplicableWarpName(String name, Player player, Faction faction){
		if(!FactionUtil.hasPermission(player, RankPermission.SET_WARPS)) Util.message(player,  "&cYour faction rank does not allow you to do this.");
		else if(faction.getWarps().size() >= faction.getWarpLimit()) Util.message(player, "&cYour faction already has the maximum warps.");
		else if(!Util.stringIsAlphanumerical(name)) Util.message(player,  "&cWarp names must only contain letters and numbers.");
		else if(!Util.stringHasMax(name, 20)) Util.message(player, "&cWarp names must be less than 20 characters.");
		else if(isWarpNameInUse(name, faction)) Util.message(player, "&cYour faction already has that warp.");
		else return true;
		return false;
	}
	
	public static boolean isApplicableWarpName(String name, Player player, Faction faction, Inventory inventory){
		if(!FactionUtil.hasPermission(player, RankPermission.SET_WARPS)) MenuUtil.displayAnvilInvalidity(inventory, "No Permission");
		else if(faction.getWarps().size() >= faction.getWarpLimit()) MenuUtil.displayAnvilInvalidity(inventory, "Max Warps");
		else if(name == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
		else if(!Util.stringIsAlphanumerical(name)) MenuUtil.displayAnvilInvalidity(inventory, "Alphanumeric Only");
		else if(!Util.stringHasMax(name, 20)) MenuUtil.displayAnvilInvalidity(inventory, "Too Long");
		else if(isWarpNameInUse(name, faction)) MenuUtil.displayAnvilInvalidity(inventory, "Already In Use");
		else return true;
		return false;
	}
	
	public static Warp getWarpFromName(String name, Faction faction){
		for(Warp warp : faction.getWarps()){
			if(name.equalsIgnoreCase(warp.getName())) return warp;
		}
		return null;
	}
	
	public static boolean isApplicableClaim(Chunk chunk, Faction faction, Player player){
		String chunkS = Serialization.chunkToString(chunk);
		if(!Util.worldGuard().getRegionManager(chunk.getWorld()).getApplicableRegions(BukkitUtil.toRegion(chunk)).getRegions().isEmpty()) Util.message(player, "&cYou can not claim a chunk within a server protected region.");
		else if(faction.getClaimedLand().size() >= faction.getMaxClaimedLand()) Util.message(player, "&cYour faction already has the maximum land.");
		else if(faction.hasClaimedLand(chunkS)) Util.message(player, "&cYour faction already owns this chunk.");
		else if(isChunkClaimed(chunkS)) Util.message(player, "&cAnother faction owns this chunk, if you want to overclaim them use /f overclaim.");
		else if(faction.getPower() < 350) Util.message(player, "&cYour faction must have at least 350 power to claim a chunk.");
		else return true;
		return false;
	}
	
	public static boolean isApplicableClaim(Chunk chunk, Faction faction, Player player, Inventory inventory, ItemStack originalItem, int slot){
		String chunkS = Serialization.chunkToString(chunk);
		if(!Util.worldGuard().getRegionManager(chunk.getWorld()).getApplicableRegions(BukkitUtil.toRegion(chunk)).getRegions().isEmpty()) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "Cannot Claim In Server Protected Region");
		else if(faction.getClaimedLand().size() >= faction.getMaxClaimedLand()) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "Already At Max Claimed Land");
		else if(faction.hasClaimedLand(chunkS)) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "Your Faction Already Owns Chunk");
		else if(isChunkClaimed(chunkS)) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "Use '/f overclaim' To Overclaim");
		else if(faction.getPower() < 350) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "Must Have At Least 350 Power");
		else return true;
		return false;
	}
	
	public static boolean isApplicableOverClaim(Chunk chunk, Faction faction, Player player){
		String chunkS = Serialization.chunkToString(chunk);
		if(!isChunkClaimed(chunkS)) Util.message(player, "&cThis chunk is not claimed by a faction.");
		else{
			Faction chunkFaction = getChunkFaction(chunkS);
			if(faction == chunkFaction) Util.message(player, "&cYou cannot overclaim your own faction.");
			else if(!chunkFaction.isEnemiedBy(faction)) Util.message(player, "&cYour faction does not enemy this land's faction.");
			else if(faction.hasAlliance(chunkFaction)) Util.message(player, "&cYou cannot overclaim an ally faction's land.");
			else{
				long chunkFactionPower = chunkFaction.getPower();
				if(chunkFactionPower > 499) Util.message(player, "&cThe faction you are overclaiming must have less than 500 power to be overclaimed.");
				else if((faction.getPower() - chunkFactionPower) < 200) Util.message(player, "&cYour faction must have at least 200 power more than the faction your are overclaiming to overclaim.");
				else return true;
			}
		}
		return false;
	}
	
	public static boolean isApplicableUnclaim(Chunk chunk, Faction faction, Player player){
		String chunkS = Serialization.chunkToString(chunk);
		if(!isChunkClaimed(chunkS)) Util.message(player, "&cThis chunk is not claimed by a faction");
		else if(getChunkFaction(chunkS) != faction) Util.message(player, "&cThis chunk is not claimed by your faction.");
		else return true;
		return false;
	}
	
	public static boolean isApplicableUnclaim(Chunk chunk, Faction faction, Player player, Inventory inventory, ItemStack originalItem, int slot){
		String chunkS = Serialization.chunkToString(chunk);
		if(!isChunkClaimed(chunkS)) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "That Chunk Is Not Claimed");
		else if(getChunkFaction(chunkS) != faction) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "That Chunk Is Not Claimed By Your Faction");
		else return true;
		return false;
	}
	
	public static void checkChallenge(Faction faction, Player player, FactionChallenge challenge, StatType stat){
		long statAmount = faction.getStat(stat);
		if(challenge.getStatAmount() > statAmount) return;
		else if(faction.hasChallengeCompleted(challenge)) return;
		Controller.completeChallenge(player, faction, challenge);	
	}
	
	public static void checkChallenge(Faction faction, Player player, FactionChallenge[] challenges, StatType stat){
		long statAmount = faction.getStat(stat);
		for(FactionChallenge challenge : challenges){
			if(challenge.getStatAmount() > statAmount) continue;
			else if(faction.hasChallengeCompleted(challenge)) continue;
			Controller.completeChallenge(player, faction, challenge);
		}
	}
	
	public static long calculatePlayerKillPowerGain(Chunk chunk, Player killer, Player killed){
		if(!FactionUtil.isInFaction(killer)) return 0;
		long power = 8;
		Faction killerF = getPlayerFaction(killer);
		Faction killedF = getPlayerFaction(killed);
		Faction chunkF = getChunkFaction(chunk);
		if(killedF == null && chunkF == killerF) return 5L;
		else if(killedF == null) return 3L;
		if(killerF.hasEnemy(killedF)) power = power + 2;
		if(chunkF == killerF) power = power + 2;
		return power;
	}
	
	public static long calculatePlayerDeathPowerLoss(Chunk chunk, Player killer, Player killed){
		if(!FactionUtil.isInFaction(killed)) return 0;
		long power = 7;
		Faction killerF = getPlayerFaction(killer);
		Faction killedF = getPlayerFaction(killed);
		Faction chunkF = getChunkFaction(chunk);
		if(killerF == null && chunkF == killedF) return 5L;
		else if(killerF == null) return 6L;
		
		
		if(killedF.isEnemiedBy(killerF)) power = power + 2;
		if(chunkF == killedF) power = power + 2;
		return power;
	}
	
	public static long calculateOtherDeathPowerLoss(DamageCause cause){
		switch(cause){
		case FALL: return 3L;
		case ENTITY_EXPLOSION: return 3L;
		case BLOCK_EXPLOSION: return 3L;
		case SUFFOCATION: return 3L;
		case FALLING_BLOCK: return 4L;
		case LAVA: return 3L;
		default: return 2L;
		}
	}
	
	public static void checkFactionLevelUp(Faction faction, Player player){
		if(faction.getExp() >= faction.getRequiredExp()){
			long overXP = faction.getExp() - faction.getRequiredExp();
			Controller.levelUp(faction, faction.getLevel() + 1, player);
			faction.addExp(overXP, player);
		}
	}

}
