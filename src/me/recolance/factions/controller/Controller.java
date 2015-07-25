package me.recolance.factions.controller;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import me.recolance.coins.api.CoinsAPI;
import me.recolance.coins.util.CoinsUtil;
import me.recolance.factions.Factions;
import me.recolance.factions.commands.Command;
import me.recolance.factions.data.DataHandler;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Serialization;
import me.recolance.factions.faction.*;
import me.recolance.factions.menu.MenuRenderer;
import me.recolance.factions.menu.MenuUtil;
import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;
import me.recolance.globalutil.utils.HoverMessage;
import me.recolance.globalutil.utils.PlayerFirework;
import me.recolance.globalutil.utils.TimedTeleport;
import me.recolance.npcapi.npc.InteractHandler;
import me.recolance.npcapi.npc.NPCCharacter;
import me.recolance.playerlog.data.Database;
import me.recolance.stats.api.StatsAPI;

@SuppressWarnings("deprecation")
public class Controller{

	public static void disband(Faction faction, Player player){
		for(UUID factionId : faction.getEnemiedBy()){
			if(!DataHolder.getFactionIdFactionMap().containsKey(factionId)) continue;
			Faction enemiedByFaction = DataHolder.getFactionIdFactionMap().get(factionId);
			enemiedByFaction.removeEnemy(faction);
			enemiedByFaction.addActivityLogEntry("&eFaction enemy " + faction.getName() + " &r&edisbanded.");
			enemiedByFaction.sendMessage("&cYour faction's enemy " + faction.getName() + " &r&cdisbanded.");
		}
		for(UUID factionId : faction.getEnemies()){
			if(!DataHolder.getFactionIdFactionMap().containsKey(factionId)) continue;
				Faction enemyFaction = FactionUtil.getFactionFromId(factionId);
				enemyFaction.removeEnemiedBy(faction);
		}
		for(UUID factionId : faction.getAlliances()){
			if(!DataHolder.getFactionIdFactionMap().containsKey(factionId)) continue;
			Faction alliedFaction = DataHolder.getFactionIdFactionMap().get(factionId);
			alliedFaction.removeAlliance(faction);
			alliedFaction.addActivityLogEntry("&eFaction ally " + faction.getName() + " &r&edisbanded.");
			alliedFaction.sendMessage("&cYour faction's ally " + faction.getName() + " &r&cdisbanded.");
		}
		
		Connection connection = Database.getConnection();
		try{
			final PreparedStatement factionStatement = connection.prepareStatement("DELETE FROM factions WHERE id=?");
			factionStatement.setString(1, faction.getId().toString());
			final PreparedStatement membersStatement = connection.prepareStatement("DELETE FROM members WHERE faction=?");
			membersStatement.setString(1, faction.getId().toString());
			final PreparedStatement ranksStatement = connection.prepareStatement("DELETE FROM ranks WHERE faction=?");
			ranksStatement.setString(1, faction.getId().toString());
			final PreparedStatement vaultsStatement = connection.prepareStatement("DELETE FROM vaults WHERE faction=?");
			vaultsStatement.setString(1, faction.getId().toString());
			new BukkitRunnable(){
				@Override
				public void run(){
					try{
						factionStatement.executeUpdate();
						membersStatement.executeUpdate();
						ranksStatement.executeUpdate();
						vaultsStatement.executeUpdate();
					}catch(SQLException e){
						e.printStackTrace();
					}					
				}
			}.runTaskAsynchronously(Factions.plugin);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		if(player != null) faction.dropVault(player);
		
		DataHolder.clearFactionMemory(faction);
		
		for(Member member : faction.getOnlineMembers()){
			Player memberPlayer = Bukkit.getPlayer(member.getPlayer());
			new TitleObject(Util.setStringColors("&cFaction Disbanded"), Util.setStringColors("&c" + faction.getName())).send(memberPlayer);
			memberPlayer.getWorld().createExplosion(memberPlayer.getLocation(), -1F, false);
		}
		Util.broadcastMessage("&eThe faction &6" + faction.getName() + " &r&ehas been &6disbanded&e.");
		faction.sendMessage("&cYour faction was disbanded.");
	}
	
	public static void create(Player player, String name, ItemStack icon, FactionSound sound){
		name = Util.setStringColors(name);
		UUID id = UUID.randomUUID();
		Info info = new Info(name, icon, sound, player.getUniqueId());
		List<Member> members = new ArrayList<Member>();
		List<Rank> ranks = Rank.getPredefinedRanks(id);
		List<ClaimedLand> land = new ArrayList<ClaimedLand>();
		Relations relations = new Relations(new ArrayList<UUID>(), new ArrayList<UUID>(), new ArrayList<UUID>());
		Vault vault = new Vault(id);
		List<Warp> warps = new ArrayList<Warp>();
		HashMap<StatType, Long> stats = StatType.getBareStats();
		HashMap<FactionChallenge, CompletedChallenge> completedChallenges = new HashMap<FactionChallenge, CompletedChallenge>();
		Rank leaderRank = null;
		for(Rank rank : ranks){
			if(rank.isLeader()){
				leaderRank = rank;
				break;
			}
		}
		Member leader = new Member(player.getUniqueId(), id, leaderRank, System.currentTimeMillis(), true, 0, 0, 0, 0);
		members.add(leader);
		Faction faction = new Faction(id, info, members, ranks, land, relations, vault, warps, stats, completedChallenges);
		DataHandler.saveFaction(faction, false);
		DataHolder.loadFaction(faction);
		Util.broadcastMessage("&eThe faction &6" + name + " &r&ehas been &6created&e.");
		faction.addActivityLogEntry("&e" + faction.getName() + " &r&ewas created!");
		new TitleObject(Util.setStringColors("&6New Faction Created"), Util.setStringColors("&6" + name)).send(player);
		new PlayerFirework(Color.ORANGE, Color.YELLOW, Color.TEAL).send(player);
		player.playSound(player.getLocation(), sound.getSound(), 1F, 0F);
		faction.setSaveable(true);
		faction.setVaultSaveable(true);
	}
	
	public static void addMember(Player player, Faction faction){
		if(FactionUtil.isInFaction(player)){
			Util.message(player, "&cYou are already in a faction.");
		}else if(faction.getMembers().size() >= 45){
			Util.message(player, "&cThat faction is full.");
		}else{
			Member member = new Member(player.getUniqueId(), faction.getId(), faction.getDefaultRank(), System.currentTimeMillis(), true, 0, 0, 0, 0);
			faction.getMembers().add(member);
			DataHolder.getPlayerIdMemberMap().put(player.getUniqueId(), member);
			DataHandler.saveMember(member, false);
			faction.addActivityLogEntry("&e" + player.getName() + " has joined.");
			faction.sendMessage("&6" + player.getName() + " &ehas joined the faction.");
			new TitleObject(Util.setStringColors("&6Joined Faction"), Util.setStringColors("&6" + faction.getName())).send(player);
			player.playSound(player.getLocation(), faction.getSound().getSound(), 1F, 0F);
			new PlayerFirework(Color.ORANGE, Color.YELLOW, Color.TEAL).send(player);
			faction.addExp(5L, player);
		}
	}
	
	public static void leaveMember(Player player, Faction faction){
		Connection connection = Database.getConnection();
		try{
			final PreparedStatement statement = connection.prepareStatement("DELETE FROM members WHERE player_id=?");
			statement.setString(1, player.getUniqueId().toString());
			new BukkitRunnable(){
				@Override
				public void run(){
					try{
						statement.executeUpdate();
					}catch(SQLException e){
						e.printStackTrace();
					}	
				}
			}.runTaskAsynchronously(Factions.plugin);
		}catch(SQLException e){
			e.printStackTrace();
		}
		Member member = DataHolder.getPlayerIdMemberMap().get(player.getUniqueId());
		faction.getMembers().remove(member);
		DataHolder.getPlayerIdMemberMap().remove(player.getUniqueId());
		faction.addActivityLogEntry("&c" + player.getName() + " has left.");
		faction.sendMessage("&c" + player.getName() + " has left the faction.");
		Util.message(player, "&cYou are no longer a member of " + faction.getName() + "&r&c.");
		new TitleObject(Util.setStringColors("&cLeft Faction"), Util.setStringColors("&6" + faction.getName())).send(player);
		player.getWorld().createExplosion(player.getLocation(), -1F, false);
	}
	public static void leaveMember(Player player, Faction faction, Inventory inventory, int slot, ItemStack originalItem){
		if(FactionUtil.isLeader(player)) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "&cFaction leader cannot leave.");
		else{
			Connection connection = Database.getConnection();
			try{
				final PreparedStatement statement = connection.prepareStatement("DELETE FROM members WHERE player_id=?");
				statement.setString(1, player.getUniqueId().toString());
				new BukkitRunnable(){
					@Override
					public void run(){
						try{
							statement.executeUpdate();
						}catch(SQLException e){
							e.printStackTrace();
						}	
					}
				}.runTaskAsynchronously(Factions.plugin);
			}catch(SQLException e){
				e.printStackTrace();
			}
			player.closeInventory();
			Member member = DataHolder.getPlayerIdMemberMap().get(player.getUniqueId());
			faction.getMembers().remove(member);
			DataHolder.getPlayerIdMemberMap().remove(player.getUniqueId());
			faction.addActivityLogEntry("&c" + player.getName() + " has left.");
			faction.sendMessage("&c" + player.getName() + " has left the faction.");
			Util.message(player, "&cYou are no longer a member of " + faction.getName() + "&r&c.");
			new TitleObject(Util.setStringColors("&cLeft Faction"), Util.setStringColors("&6" + faction.getName())).send(player);
			player.getWorld().createExplosion(player.getLocation(), -1F, false);
		}
	}
	
	public static void kickMember(Player kicker, UUID playerId, Faction faction){
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(playerId);
		boolean isOnline = offPlayer.isOnline();
		Player player = null;
		Connection connection = Database.getConnection();
		try{
			final PreparedStatement statement = connection.prepareStatement("DELETE FROM members WHERE player_id=?");
			statement.setString(1, playerId.toString());
			new BukkitRunnable(){
				@Override
				public void run(){
					try{
						statement.execute();
					}catch(SQLException e){
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(Factions.plugin);
		}catch(SQLException e){
			e.printStackTrace();
		}
		Member member = DataHolder.getPlayerIdMemberMap().get(playerId);
		faction.getMembers().remove(member);
		DataHolder.getPlayerIdMemberMap().remove(playerId);
		faction.addActivityLogEntry("&c" + offPlayer.getName() + " has been kicked.");
		faction.sendMessage("&c" + kicker.getName() + " has kicked " + offPlayer.getName() + " from the faction.");
		if(isOnline){
			player = Bukkit.getPlayer(playerId);
			player.closeInventory();
			Util.message(player, "&cYou are no longer a member of " + faction.getName() + "&r&c.");
			new TitleObject(Util.setStringColors("&cKicked From Faction"), Util.setStringColors("&6" + faction.getName())).send(player);
			player.getWorld().createExplosion(player.getLocation(), -1F, false);
		}
	}
	public static void kickMember(Player kicker, UUID playerId, Faction faction, Inventory inventory, int slot, ItemStack originalItem){
		if(!FactionUtil.isInFaction(playerId)) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "&cNo Longer In The Faction");
		else if(FactionUtil.getPlayerFaction(playerId) != faction) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "&cNo Longer In Faction");
		else if(FactionUtil.isLeader(playerId)) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "&cCannot Kick The Leader");
		else if(kicker.getUniqueId().equals(playerId)) MenuUtil.displayMenuInvalidity(slot, inventory, originalItem, "&cCannot Kick Self");
		else{
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(playerId);
			Connection connection = Database.getConnection();
			try{
				final PreparedStatement statement = connection.prepareStatement("DELETE FROM members WHERE player_id=?");
				statement.setString(1, playerId.toString());
				new BukkitRunnable(){
					@Override
					public void run(){
						try{
							statement.executeUpdate();
						}catch(SQLException e){
							e.printStackTrace();
						}
					}
				}.runTaskAsynchronously(Factions.plugin);
			}catch(SQLException e){
				e.printStackTrace();
			}
			faction.getMembers().remove(DataHolder.getPlayerIdMemberMap().get(playerId));
			DataHolder.getPlayerIdMemberMap().remove(playerId);
			faction.addActivityLogEntry("&c" + offPlayer.getName() + " has been kicked.");
			faction.sendMessage("&c" + kicker.getName() + " has kicked " + offPlayer.getName() + " from the faction.");
			if(offPlayer.isOnline()){
				Player player = Bukkit.getPlayer(playerId);
				Util.message(player, "&cYou are no longer a member of " + faction.getName() + "&r&c.");
				new TitleObject(Util.setStringColors("&cKicked From Faction"), Util.setStringColors("&6" + faction.getName())).send(player);
				player.getWorld().createExplosion(player.getLocation(), -1F, false);
			}
			MenuRenderer.renderMembers(kicker, faction, inventory);
		}
	}
	
	public static void setMemberRank(Player whoSet, Member member, Rank rank){
		member.setRank(rank);
		String name = Bukkit.getOfflinePlayer(member.getPlayer()).getName();
		Faction faction = FactionUtil.getPlayerFaction(member.getPlayer());
		faction.addActivityLogEntry("&e" + whoSet.getName() + " set " + name + " to " + rank.getName() + ".");
		faction.sendMessage("&6" + whoSet.getName() + " &eset &6" + name + "'s &efaction rank to &6" + rank.getName() + "&e.");
	}
	
	public static void setMemberLeader(Player leaderSet, Player newLeader){
		Faction faction = FactionUtil.getPlayerFaction(leaderSet);
		FactionUtil.getPlayerMember(newLeader).setRank(FactionUtil.getRankFromName("Leader", faction));
		FactionUtil.getPlayerMember(leaderSet).setRank(faction.getDefaultRank());
		faction.sendMessage("&6" + newLeader.getName() + " &eis now the faction's &6leader&e.");
		Util.message(newLeader, "&eYou are now &6" + faction.getName() + "&r&6's leader&e.");
		Util.message(leaderSet, "&cYou are no longer " + faction.getName() + "&r&c's leader.");
		faction.addActivityLogEntry("&e" + newLeader.getName() + " is now the faction Leader.");
		faction.getInfo().setLeader(newLeader.getUniqueId());
		
	}
	
	public static Rank addRank(Player player, Faction faction, String name){
		Rank rank = new Rank(UUID.randomUUID(), faction.getId(), name, new ArrayList<Integer>(), 100, 0, 0, false, false);
		faction.getRanks().add(rank);
		faction.addActivityLogEntry("&e" + rank.getName() + " rank created by " + player.getName() + ".");
		faction.sendMessage("&6" + player.getName() + " &ehas created the rank &6" + rank.getName() + "&e.");
		return rank;
	}
	
	public static void setNeutral(Faction whoTo, Faction whoFrom){
		if(whoFrom.hasAlliance(whoTo)){
			whoTo.removeAlliance(whoFrom);
			whoTo.sendMessage("&c" + whoFrom.getName() + " &r&cis no longer your faction's ally.");
			whoTo.addActivityLogEntry("&c" + whoFrom.getName() + " &r&cis no longer an ally.");
			whoFrom.removeAlliance(whoTo);
			whoFrom.sendMessage("&eYour faction is now &6neutral &ewith &6" + whoTo.getName() + "&r&e.");
			whoFrom.addActivityLogEntry("&e" + whoTo.getName() + " &r&eis no longer an ally.");
		}else if(whoFrom.hasEnemy(whoTo)){
			whoFrom.removeEnemy(whoTo);
			whoFrom.sendMessage("&eYour is now &6neutral &ewith &6" + whoTo.getName() + "&r&e.");
			whoFrom.addActivityLogEntry("&e" + whoTo.getName() + " &r&eis no longer an enemy.");
			whoTo.removeEnemiedBy(whoFrom);
			whoTo.sendMessage("&6" + whoFrom.getName() + " &r&eis no longer &6enemying &eyour faction.");
			whoTo.addActivityLogEntry("&e" + whoFrom.getName() + " &r&eis no longer enemying you.");
		}
	}
	
	public static void removeRank(Player player, Faction faction, Rank rank){
		if(rank.isDefault() || rank.isLeader()) return;
		if(faction.getRanks().contains(rank)) faction.getRanks().remove(rank);
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("DELETE FROM ranks WHERE id=?");
			statement.setString(1, rank.getId().toString());
			statement.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		for(Member member : faction.getMembers()){
			if(member.getRank() == rank){
				member.setRank(faction.getDefaultRank());
				faction.sendMessage("&6" + Bukkit.getOfflinePlayer(member.getPlayer()).getName() + " &ewas automatically moved to the &6" + faction.getDefaultRank().getName() + " rank&e.");
			}
		}
		faction.addActivityLogEntry("&e" + rank.getName() + " rank removed.");
		faction.sendMessage("&6" + player.getName() + " &eremoved the &6" + rank.getName() + " rank&e.");
	}
	
	public static void setEnemies(Faction whoFrom, Faction whoTo){
		whoFrom.getEnemies().add(whoTo.getId());
		whoFrom.sendMessage("&6" + whoTo.getName() + " &r&eis now your faction's &6enemy&e.");
		whoFrom.addActivityLogEntry("&e" + whoTo.getName() + " &r&eis now an enemy.");
		whoTo.getEnemiedBy().add(whoFrom.getId());
		whoTo.sendMessage("&c" + whoFrom.getName() + " &r&cis now enemying your faction.");
		whoTo.addActivityLogEntry("&c" + whoFrom.getName() + " &r&cis enemying you.");
	}
	
	public static void sendAllyInvitation(Player player, final Faction whoFrom, final Faction whoTo){
		AllianceInvitation invitation = new AllianceInvitation(whoFrom, whoTo);
		AllianceInvitation.allianceInvitations.put(whoTo, invitation);
		whoFrom.sendMessage("&6" + player.getName() + " &ehas sent an &6ally request &eto " + "&6" + whoTo.getName() + "&r&e.");
		whoTo.sendMessage("&6" + whoFrom.getName() + " &r&ehas request to be your faction's &6ally&e. Use &6/f accept &eor &6/f deny &eto respond to the invitation.");
		int task = Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.plugin, new Runnable(){
			public void run(){
				denyAllyInvitation(whoTo);
				whoTo.sendMessage("&c" + whoTo.getName() + " &r&chas denied your ally request from " + whoFrom.getName() + "&r&c.");
			}
		}, 1200L);
		invitation.setTask(task);
	}
	
	public static void denyAllyInvitation(Faction whoTo){
		AllianceInvitation invitation = AllianceInvitation.allianceInvitations.get(whoTo);
		Bukkit.getScheduler().cancelTask(invitation.getTask());
		AllianceInvitation.allianceInvitations.remove(whoTo);
		if(invitation.getWhoSentFaction().exists()) invitation.getWhoSentFaction().sendMessage("&c" + invitation.getWhoRecievedFaction().getName() + " &chas denied your faction's ally request.");
	}
	
	public static void acceptAllyInvitation(Faction whoTo, Faction whoFrom){
		AllianceInvitation invitation = AllianceInvitation.allianceInvitations.get(whoTo);
		if(invitation == null || !whoTo.exists() || !whoFrom.exists()){
			whoTo.sendMessage("&cThe ally request was denied because that faction no longer exists.");
			denyAllyInvitation(whoTo);
		}else if(whoTo.getAlliances().size() >= 9){
			whoTo.sendMessage("&cThe ally request was denied because that faction has the maxiumum amount of allies.");
			whoFrom.sendMessage("&cThe ally request was denied because your faction has the maxiumum amount of allies.");
			denyAllyInvitation(whoTo);
		}else if(whoFrom.getAlliances().size() >= 9){
			whoTo.sendMessage("&cThe ally request was denied because your faction has the maxiumum amount of allies.");
			whoFrom.sendMessage("&cThe ally request was denied because that faction has the maxiumum amount of allies.");
			denyAllyInvitation(whoTo);
		}else if(whoTo.hasEnemy(whoFrom)){
			whoTo.sendMessage("&cThe ally request was denied because your faction is enemying that faction.");
			whoFrom.sendMessage("&cThe ally request was denied because that faction is enemying your faction.");
			denyAllyInvitation(whoTo);
		}else if(whoTo.isEnemiedBy(whoFrom)){
			whoTo.sendMessage("&cThe ally request was denied because that faction is enemying your faction.");
			whoFrom.sendMessage("&cThe ally request was denied because your faction is enemying that faction.");
			denyAllyInvitation(whoTo);
		}else{
			Bukkit.getScheduler().cancelTask(invitation.getTask());
			AllianceInvitation.allianceInvitations.remove(invitation);
			whoTo.getAlliances().add(whoFrom.getId());
			whoTo.sendMessage("&6" + whoFrom.getName() + " &r&eis now your faction's ally.");
			whoTo.addActivityLogEntry("&e" + whoFrom.getName() + " &r&eis now an ally.");
			whoFrom.getAlliances().add(whoTo.getId());
			whoFrom.sendMessage("&6" + whoTo.getName() + " &r&eis now your faction's ally.");
			whoFrom.addActivityLogEntry("&e" + whoFrom.getName() + " &r&eis now an ally.");
		}
	}
	
	public static void sendFactionInvitation(final Player whoTo, final Player whoFrom, final Faction faction){
		final FactionInvitation invitation = new FactionInvitation(whoFrom, whoTo, faction);
		FactionInvitation.factionInvitations.put(whoTo, invitation);
		Util.message(whoTo, "&6" + whoFrom.getName() + " &ehas invited you to join the faction &6" + faction.getName() + "&r&e. Use &6/f accept &eor &6/f deny &eto respond to the invitation.");
		Util.message(whoFrom, "&eYou have invited &6" + whoTo.getName() + " &eto join your faction.");
		int task =  Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.plugin, new Runnable(){
			public void run(){
				if(FactionInvitation.factionInvitations.containsKey(whoTo)) denyFactionInvitation(whoTo);
			}
		}, 1200L);
		invitation.setTask(task);
	}
	
	public static void acceptFactionInvitation(Player player, Faction faction){
		if(FactionUtil.isInFaction(player)){
			Util.message(player, "&cYou are already in a faction.");
			denyFactionInvitation(player);
		}else if(faction.getMembers().size() >= 45){
			Util.message(player, "&cThat faction is full.");
			denyFactionInvitation(player);
		}else if(!faction.exists()){
			Util.message(player, "&cThat faction no longer exists.");
		}else{
			Bukkit.getScheduler().cancelTask(FactionInvitation.factionInvitations.get(player).getTask());
			FactionInvitation.factionInvitations.remove(player);
			faction.addMember(player);
		}
	}
	
	public static void denyFactionInvitation(Player player){
		FactionInvitation invitation = FactionInvitation.factionInvitations.get(player);
		Bukkit.getScheduler().cancelTask(invitation.getTask());
		FactionInvitation.factionInvitations.remove(player);
		if(invitation.getWhoSent().isOnline()) Util.message(invitation.getWhoSent(), "&c" + invitation.getWhoRecieved().getName() + " has denied your faction invitation.");
	}
	
	public static void dropVault(Player player, Faction faction){
		Vault vault = faction.getVault();
		List<VaultTab> vaultTabs = new ArrayList<VaultTab>();
		vaultTabs.add(vault.getTab1()); vaultTabs.add(vault.getTab2());
		vaultTabs.add(vault.getTab3()); vaultTabs.add(vault.getTab4());
		int droppedItems = 0;
		for(VaultTab tab : vaultTabs){
			Inventory inventory = tab.getContents();
			for(int i = 0; i < 45; i++){
				ItemStack item = inventory.getItem(i);
				if(item != null && item.getType() != Material.AIR){
					player.getWorld().dropItemNaturally(player.getLocation().add(0, 1, 0), item);
					droppedItems++;
				}
			}
		}
		if(droppedItems != 0) Util.message(player, "&cThe item contents of your vault has dropped " + droppedItems + " items on the ground.");
		if(vault.getMoney() > 0){
			CoinsAPI.addCoins(player.getUniqueId(), vault.getMoney(), "Disbanded vault coins.", "Disbanded faction vault money.");
			Util.message(player, "&cThe " + vault.getMoney() + " Coins in your vault have been given to you.");
		}
	}
	
	public static void addActivityLine(Faction faction, String line){
		line = Util.setStringColors(line);
		List<String> log = faction.getActivityLog();
		int i = 0;
		for(String entry : log){
			if(entry.contains(line)){
				log.remove(i);
				break;
			}
			i++;
		}
		if(log.size() >= 30) log.remove(29);
		log.add(0, Util.setStringColors("&9" + Util.getMonthDayDate() + ": " + line));
		faction.setSaveable(true);
	}
	
	public static void sendMessage(Faction faction, String message){
		message = Util.setStringColors(message);
		for(Player player : faction.getOnlinePlayers()){
			player.sendMessage(message);
		}
	}
	
	public static void who(Player player, Faction faction){
		whoName(player, faction);
		whoDescription(player, faction);
		whoStatus(player, faction);
		whoPower(player, faction);
		whoLand(player, faction);
		whoStats(player, faction);
		whoRelations(player, faction);
		whoMembers(player, faction);
	}
	private static void whoName(Player player, Faction faction){
		Util.message(player, "&eName: &6" + faction.getName());
	}
	private static void whoDescription(Player player, Faction faction){
		Util.message(player, "&eDescription: &6" + faction.getDescription());
	}
	private static void whoStatus(Player player, Faction faction){
		Util.message(player, "&eStatus: &6" + ((faction.getStatus() == FactionStatus.CLOSED) ? "Closed" : "Open"));
	}
	private static void whoPower(Player player, Faction faction){
		Util.message(player, "&ePower: &6" + faction.getPower() + "/1000");
	}
	private static void whoLand(Player player, Faction faction){
		Util.message(player, "&eLand Claimed: &6" + faction.getClaimedLand().size() + " Chunks");
	}
	private static void whoStats(Player player, Faction faction){
		new HoverMessage().message("&eStats: ").messageTipCommand("&6[Stats]", "/f stats " + faction.getBareName(), "&6Faction Stats", "&aClick here to open this faction's", "&astats.").send(player);
	}
	private static void whoRelations(Player player, Faction faction){
		List<String> enemyNames = new ArrayList<String>();
		List<String> enemiedByNames = new ArrayList<String>();
		List<String> alliedNames = new ArrayList<String>();
		enemyNames.add(Util.setStringColors("&6Enemies"));
		enemiedByNames.add(Util.setStringColors("&6Enemied By"));
		alliedNames.add(Util.setStringColors("&6Alliances"));
		int i = 0;
		if(faction.getEnemies().isEmpty() || faction.getEnemies() == null){
			enemyNames.add(Util.setStringColors("&cThis faction has no enemies."));
		}else{
			for(UUID factionId : faction.getEnemies()){
				enemyNames.add(Util.setStringColors("&r&f" + DataHolder.getFactionIdFactionMap().get(factionId).getName()));
				if(i > 29){
					enemyNames.add(Util.setStringColors("&8And " + (faction.getEnemies().size() - 30) + " more..."));
					break;
				}
				i++;
			}i = 0;
		}
		if(faction.getEnemiedBy().isEmpty() || faction.getEnemiedBy() == null){
			enemiedByNames.add(Util.setStringColors("&cThis faction is not enemied by anyone."));
		}else{
			for(UUID factionId : faction.getEnemiedBy()){
				enemiedByNames.add(Util.setStringColors("&r&f" + DataHolder.getFactionIdFactionMap().get(factionId).getName()));
				if(i > 29){
					enemiedByNames.add(Util.setStringColors("&8And " + (faction.getEnemiedBy().size() - 30) + " more..."));
					break;
				}
				i++;
			}i = 0;
		}
		if(faction.getAlliances().isEmpty() || faction.getAlliances() == null){
			alliedNames.add(Util.setStringColors("&cThis faction has no alliances."));
		}else{
			for(UUID factionId : faction.getAlliances()){
				alliedNames.add(Util.setStringColors("&r&f" + DataHolder.getFactionIdFactionMap().get(factionId).getName()));
				if(i > 29){
					alliedNames.add(Util.setStringColors("&8And " + (faction.getAlliances().size() - 30) + " more..."));
					break;
				}
				i++;
			}
		}
		new HoverMessage().message("&eRelations: ").messageTip("&6[Alliances]", alliedNames.toArray(new String[alliedNames.size()])).message(" ").messageTip("&6[Enemies]", enemyNames.toArray(enemyNames.toArray(new String[enemyNames.size()]))).message(" ").messageTip("&6[Enemied By]", enemiedByNames.toArray(new String[enemiedByNames.size()])).send(player);
	}
	private static void whoMembers(Player player, Faction faction){
		List<String> onlineNames = new ArrayList<String>();
		List<String> offlineNames = new ArrayList<String>();
		onlineNames.add(Util.setStringColors("&6Online Members"));
		offlineNames.add(Util.setStringColors("&6Offline Members"));
		List<Member> onlineMembers = faction.getOnlineMembers();
		List<Member> offlineMembers = faction.getOfflineMembers();
		int i = 0;
		if(onlineMembers.isEmpty() || onlineMembers == null){
			onlineNames.add(Util.setStringColors("&cThis faction has no members online."));
		}else{
			for(Member member : onlineMembers){
				onlineNames.add(Util.setStringColors("&e" + Bukkit.getPlayer(member.getPlayer()).getName() + " - " + member.getRank().getName()));
				if(i > 29){
					onlineNames.add(Util.setStringColors("&8And " + (onlineMembers.size() - 30) + " more..."));
					break;
				}
				i++;
			}i = 0;
		}
		if(offlineMembers.isEmpty() || offlineMembers == null){
			offlineNames.add(Util.setStringColors("&cThis faction has no members offline."));
		}else{
			for(Member member : offlineMembers){
				offlineNames.add(Util.setStringColors("&e" + Bukkit.getOfflinePlayer(member.getPlayer()).getName() + " - " + member.getRank().getName()));
				if(i > 29){
					offlineNames.add(Util.setStringColors("&8And " + (offlineMembers.size() - 30) + " more..."));
					break;
				}
				i++;
			}
		}
		new HoverMessage().message("&eMembers: ").messageTip("&6[Online Members]", onlineNames.toArray(new String[onlineNames.size()])).message(" ").messageTip("&6[Offline Members]", offlineNames.toArray(new String[offlineNames.size()])).send(player);
	}
	
	public static long requiredEXP(Faction faction){
		if(faction.getLevel() == 25) return Long.MAX_VALUE;
		return ((faction.getLevel() * 410) + (faction.getLevel() * 40) + 230);
	}
	
	public static void teleportHome(Player player, Faction faction){
		if(FactionUtil.hasPermission(player, RankPermission.USE_HOME)) new TimedTeleport(player, faction.getHome(), 5L);
		else Util.message(player, "&cYour faction rank does not allow you to do this.");
	}
	
	public static boolean setHome(Player player, Faction faction){
		if(!FactionUtil.hasPermission(player, RankPermission.SET_HOME)){
			Util.message(player, "&cYour faction rank does not allow you to do this.");
			return false;
		}else if(!faction.hasClaimedLand(player.getLocation().getChunk())){
			Util.message(player, "&cYou can only set your faction home in your faction's claimed land.");
			return false;
		}else return true;
	}
	
	public static boolean unsetHome(Player player, Faction faction){
		if(!FactionUtil.hasPermission(player, RankPermission.SET_HOME)){
			Util.message(player, "&cYour faction rank does not allow you to do this.");
			return false;
		}else if(faction.getHome().equals(Bukkit.getWorld("world").getSpawnLocation())){
			Util.message(player, "&cYour faction's home is already unset.");
			return false;
		}else return true;
	}
	
	public static void depositCoins(int amount, Player player, Faction faction){
		if(amount <= 0) return;
		Vault vault = faction.getVault();
		CoinsUtil.removeCoins(player.getUniqueId(), amount, "Faction vault deposit.", "Faction vault deposit into " + faction.getName() + ".");
		vault.setMoney(vault.getMoney() + amount);
		vault.addLogEntry("&e" + player.getName() + " deposited " + amount + String.valueOf((amount > 1) ? " Coins." : " Coin."));
		new HoverMessage().messageTip("&6[Vault Coin Deposit]", "&6Vault Coin Deposit", "&9Amount: &e" + amount + String.valueOf(amount > 1 ? " Coins" : " Coin")).send(player);
	}
	public static void depositChallengeCoins(int amount, Faction faction){
		Vault vault = faction.getVault();
		vault.setMoney(vault.getMoney() + amount);
		vault.addLogEntry("&eChallange completed" + " rewarded " + amount + String.valueOf((amount > 1) ? " Coins." : " Coin."));
	}
	public static void depositOverFlowingPower(int amount, Faction faction){
		if(amount <= 0) return;
		Vault vault = faction.getVault();
		vault.setMoney(vault.getMoney() + amount);
		vault.addLogEntry("&e" + amount + String.valueOf((amount > 1) ? " Coins " : " Coin ") + "deposited from overflowing power.");
	}
	public static void withdrawlCoins(int amount, Player player, Member member, Vault vault){
		if(amount <= 0) return;
		CoinsUtil.addCoins(player.getUniqueId(), amount, "Faction vault withdrawl.", "Faction vault withdrawl from " + FactionUtil.getFactionFromId(vault.getFaction()).getName() + ".");
		member.setVaultMoneyTaken(member.getVaultMoneyTaken() + amount);
		vault.setMoney(vault.getMoney() - amount);
		vault.addLogEntry("&e" + player.getName() + " withdrew " + amount + String.valueOf((amount > 1) ? " Coins." : " Coin."));
		new HoverMessage().messageTip("&6[Vault Coin Withdrawl]", "&6Vault Coin Withdrawl", "&9Amount: &e" + amount + String.valueOf(amount > 1 ? " Coins" : " Coin"), "&9Limit Today: &e" + member.getVaultMoneyTaken() + "/" + member.getRank().getVaultMoneyLimit()).send(player);
	}
	
	public static void runNewDayResets(){
		for(Faction faction : DataHolder.getAllFactions()){
			for(Member member : faction.getMembers()){
				member.setVaultItemsTaken(0);
				member.setVaultMoneyTaken(0);
				member.setPowerGained(0);
				member.setPowerLost(0);
			}
		}
	}
	public static boolean isNewDay(){
		if(Factions.plugin.getConfig().getInt("day") != new Date().getDate()) return true;
		return false;
	}
	
	public static void setDay(){
		Factions.plugin.getConfig().set("day", new Date().getDate());
		Factions.plugin.saveConfig();
	}
	
	public static void addWarp(Faction faction, String name, Location loc, Player player){
		Warp warp = new Warp(name, loc, player.getUniqueId(), System.currentTimeMillis());
		faction.getWarps().add(warp);
		faction.sendMessage("&6" + player.getName() + " &eset a new faction warp called &6" + name + "&e.");
		faction.addActivityLogEntry("&e" + player.getName() + " set the " + name + " warp.");
	}
	
	public static void removeWarp(Faction faction, Player player, Warp warp){
		faction.getWarps().remove(warp);
		faction.sendMessage("&6" + player.getName() + " &eremoved the faction warp &6" + warp.getName() + "&e.");
		faction.addActivityLogEntry("&e" + player.getName() + " removed the " + warp.getName() + " warp.");
	}
	
	public static void useWarp(Player player, Warp warp){
		new TimedTeleport(player, warp.getLocation(), 8L);
	}
	
	public static void addPower(long amount, Player causer, Faction faction){
		String name = "";
		if(causer == null) name = "Server";
		else name = causer.getName();
		
		Member member = FactionUtil.getPlayerMember(causer);
		long set = faction.getPower() + amount;
		if(set > 1000){
			long over = set - 1000;
			set = 1000;
			//faction.depositOverflowingPowerCoins((int)(set / 6)); UNKNOWN YET
			ActionbarTitleObject abto = new ActionbarTitleObject(ChatColor.GOLD + "+" + over + " Overflowing Power - " + name);
			for(Player player : faction.getPowerMonitoringOnlinePlayers()) abto.send(player);
			faction.setPower(set);
			if(causer != null) member.setPowerGained((int)(member.getPowerGained() + amount));
		}else{
			ActionbarTitleObject abto = new ActionbarTitleObject(ChatColor.YELLOW + "+" + amount + " Power - " + name);
			for(Player player : faction.getPowerMonitoringOnlinePlayers()) abto.send(player);
			faction.setPower(set);
			if(causer != null) member.setPowerGained((int)(member.getPowerGained() + amount));
		}
	}
	
	public static void removePower(long amount, Player causer, Faction faction){
		Member member = FactionUtil.getPlayerMember(causer);
		long current = faction.getPower();
		long set = current - amount;
		if(current > 500 && set < 500){
			faction.sendMessage("&cYour faction is now under 500 power and can be overclaimed by a more powerful faction!");
			faction.addActivityLogEntry("&cYou are now overclaimable!");
		}
		if(set < 50){
			if(current > 50){
				faction.sendMessage("&cYour faction is now vulnerable! Anybody can now enter chests and break blocks in your land.");
				faction.addActivityLogEntry("&cYou are now vulnerable!");
			}
			if(set < 0){
				long under = amount - current;
				set = 0;
				ActionbarTitleObject abto = new ActionbarTitleObject(ChatColor.DARK_RED + "-" + under + " Underflowing Power - " + causer.getName());
				for(Player player : faction.getPowerMonitoringOnlinePlayers()) abto.send(player);
				faction.setPower(set);
				member.setPowerLost((int)(member.getPowerLost() + amount));
				return;
			}
		}
		ActionbarTitleObject abto = new ActionbarTitleObject(ChatColor.RED + "-" + amount + " Power - " + causer.getName());
		for(Player player : faction.getPowerMonitoringOnlinePlayers()) abto.send(player);
		member.setPowerLost((int)(member.getPowerLost() + amount));
		faction.setPower(set);
	}
	
	public static void claimLand(String chunk, Player player, Faction faction){
		ClaimedLand land = new ClaimedLand(faction.getId(), chunk, player.getUniqueId(), System.currentTimeMillis());
		faction.removePower(75L, player);
		faction.addActivityLogEntry("&e" + player.getName() + " claimed new land.");
		faction.getClaimedLand().add(land);
		faction.sendMessage("&6" + player.getName() + " &ehas &6claimed &ea new chunk for the faction.");
		DataHolder.getChunkClaimedLandMap().put(chunk, land);
		faction.addStat(StatType.LAND_CLAIMED, 1L);
		faction.addExp(70L, player);
	}
	
	public static void overClaim(ClaimedLand land, Player causer, Faction faction){
		Faction factionOf = FactionUtil.getFactionFromId(land.getFaction());
		factionOf.removePower(10L, causer);
		factionOf.getClaimedLand().remove(land);
		factionOf.sendMessage("&cA claimed chunk was overclaimed by " + faction.getName() + "&r&c!");
		factionOf.addActivityLogEntry("&c" + faction.getName() + " &r&coverclaimed some of your land!");
		if(land.getLocation().equals(Serialization.chunkToString(factionOf.getHome().getChunk()))) changeHomeSpawn(factionOf);
		
		ClaimedLand newLand = new ClaimedLand(faction.getId(), land.getLocation(), causer.getUniqueId(), System.currentTimeMillis());
		faction.removePower(75L, causer);
		faction.addActivityLogEntry("&e" + causer.getName() + " overclaimed " + factionOf.getName() + "&r&e's land!");
		faction.sendMessage("&6" + causer.getName() + " &ehas &6overclaimed &ea chunk from &6" + factionOf.getName() + "&r&e.");
		faction.getClaimedLand().add(newLand);
		DataHolder.getChunkClaimedLandMap().put(land.getLocation(), newLand);
		faction.addStat(StatType.LAND_OVERCLAIMED, 1L);
		faction.addStat(StatType.LAND_CLAIMED, 1L);
		FactionUtil.checkChallenge(faction, causer, new FactionChallenge[]{FactionChallenge.LAND_OVERCLAIMED_1,FactionChallenge.LAND_OVERCLAIMED_2, FactionChallenge.LAND_OVERCLAIMED_3}, StatType.LAND_OVERCLAIMED);
		faction.addExp(100L, causer);
	}
	
	public static void unClaimLand(ClaimedLand land, Player causer, Faction faction){
		faction.addPower(20L, causer);
		faction.getClaimedLand().remove(land);
		faction.sendMessage("&6" + causer.getName() + " &ehas &6unclaimed &ea chunk.");
		faction.addActivityLogEntry("&e" + causer.getName() + " has unclaimed some land.");
		DataHolder.getChunkClaimedLandMap().remove(land.getLocation());
		if(land.getLocation().equals(Serialization.chunkToString(faction.getHome().getChunk()))) changeHomeSpawn(faction);
	}
	
	public static void completeChallenge(Player player, Faction faction, FactionChallenge challenge){
 		faction.setChallengeCompleted(challenge, new CompletedChallenge(player.getUniqueId(), System.currentTimeMillis(), challenge));
 		faction.addStat(StatType.CHALLENGES_COMPLETED, 1);
 		FactionUtil.checkChallenge(faction, player, FactionChallenge.COMPLETE_ALL, StatType.CHALLENGES_COMPLETED);
		PlayerFirework firework = new PlayerFirework(Color.ORANGE, Color.BLUE, Color.YELLOW);		
		List<String> tips = new ArrayList<String>();
		tips.add("&6" + challenge.getName());
		tips.add( "&aCompleted!");
		tips.add("");
		tips.add("&9Completed By: &e" + player.getName());
		for(String string : challenge.getRewardStrings()){
			tips.add(string);
		}
		HoverMessage message = new HoverMessage().messageTip("&6[" + challenge.getName() + " Completed]", tips);
		TitleObject to = new TitleObject(Util.setStringColors("&6Challenge Completed!"), Util.setStringColors("&e" + challenge.getName()));
		for(Player fPlayer : faction.getOnlinePlayers()){
			player.playSound(fPlayer.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
			to.send(fPlayer);
			message.send(fPlayer);
			firework.send(fPlayer);
		}
		faction.addExp(1500L, player);
		faction.depositChallengeCoinsO(challenge.getCoinReward());
	}
	
	public static void levelUp(Faction faction, long level, Player p){
		if(faction.getLevel() == 25) return;
		faction.setStat(StatType.LEVEL, level);
		faction.setStat(StatType.EXPERIENCE, 0);
		FactionUtil.checkChallenge(faction, p, new FactionChallenge[]{FactionChallenge.FACTION_LEVEL_1, FactionChallenge.FACTION_LEVEL_2, FactionChallenge.FACTION_LEVEL_3}, StatType.LEVEL);
		PlayerFirework firework = new PlayerFirework(Color.LIME, Color.YELLOW, Color.BLUE);
		TitleObject to = new TitleObject(Util.setStringColors("&6Faction Level Up!"), Util.setStringColors("&eLevel " + level));
		for(Player player : faction.getOnlinePlayers()){
			Util.message(player, "&eYou faction has reached &6level " + level + "&e.");
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
			firework.send(player);
			to.send(player);
		}
	}
	
	public static void changeName(Faction faction, Player player, String name){
		name = Util.setStringColors(name);
		faction.getInfo().setName(name);
		faction.addActivityLogEntry("&eThe faction is now known as " + name + "&r&e.");
		faction.sendMessage("&6" + player.getName() + " &ehas changed the faction's &6name &eto &6" + name + "&r&e.");
		DataHolder.getFactionBareNameFactionMap().put(ChatColor.stripColor(name).toLowerCase(), faction);
		DataHolder.getFactionNameFactionMap().put(name.toLowerCase(), faction);
	}
	
	public static void changeIcon(Faction faction, Player player, ItemStack icon){
		faction.getInfo().setIcon(icon);
		faction.addActivityLogEntry("&eThe faction's icon was changed.");
		faction.sendMessage("&6" + player.getName() + " &ehas changed the faction's &6icon&e.");
	}
	
	public static void changeSound(Faction faction, Player player, FactionSound sound){
		faction.getInfo().setSound(sound);
		faction.addActivityLogEntry("&eThe faction's sound was changed.");
		faction.sendMessage("&6" + player.getName() + " &ehas changed the faction &6sound &eto &6" + sound.getName() + "&r&e.");
	}
	
	public static void changeStatus(Faction faction, Player player, FactionStatus status){
		faction.getInfo().setStatus(status);
		faction.addActivityLogEntry("&eThe faction is now " + String.valueOf(status == FactionStatus.CLOSED ? "Closed." : "Open."));
		faction.sendMessage("&6" + player.getName() + " &ehas changed the faction's &6status &eto &6" + String.valueOf(status == FactionStatus.CLOSED ? "Closed&e." : "Open&e."));
		if(status == FactionStatus.OPEN) DataHolder.getOpenFactions().add(faction);
		else if(status == FactionStatus.CLOSED) DataHolder.getOpenFactions().remove(faction);
	}
	
	public static void changeHomeSpawn(Faction faction){
		faction.getInfo().setHome(Bukkit.getWorld("world").getSpawnLocation());
		faction.addActivityLogEntry("&eFaction home unset.");
		faction.sendMessage("&6Land unclaim &ehas unset the &6faction home&e.");
	}
	
	public static void changeDescription(Faction faction, Player player, String description){
		faction.getInfo().setDescription(description);
		faction.addActivityLogEntry("&eFaction description edited.");
		faction.sendMessage("&6" + player.getName() + " &ehas changed the faction's &6description&e.");
	}
	
	public static void chunkInfo(Player player, ClaimedLand land){
		Util.message(player, "&eClaimed By: &6" + FactionUtil.getFactionFromId(land.getFaction()).getName());
		Util.message(player, "&eClaimer: &6" + Bukkit.getOfflinePlayer(land.getClaimer()).getName());
		Util.message(player, "&eClaimed On: &6" + Util.getMonthDayYearDate(land.getDateClaimed()));
	}
	
	private static HashMap<UUID, List<PotionEffect>> savedPotionEffects = new HashMap<UUID, List<PotionEffect>>();
	
	public static void applyRewardedEffects(Player player, FactionChallenge challenge){
		PotionEffect effect = challenge.getEffect();
		for(PotionEffect e : player.getActivePotionEffects()){
			if(e.getType().getName().equals(effect.getType().getName())){
				if(e.getAmplifier() > effect.getAmplifier() && e.getDuration() < 1073741823)break;
				else if(e.getDuration() > 1073741823) break;
				if(savedPotionEffects.containsKey(player.getUniqueId())){
					savedPotionEffects.get(player.getUniqueId()).add(e);
				}else{
					List<PotionEffect> effects = new ArrayList<PotionEffect>();
					effects.add(e);
					savedPotionEffects.put(player.getUniqueId(), effects);
				}
				player.removePotionEffect(e.getType());
				break;
			}
		}
		player.addPotionEffect(effect);
	}
	
	public static void removeRewardedEffects(Player player){
		for(PotionEffect effect : player.getActivePotionEffects()){
			if(effect.getDuration() > 1073741823) player.removePotionEffect(effect.getType());
		}
		if(savedPotionEffects.containsKey(player.getUniqueId())){
			for(PotionEffect effect : savedPotionEffects.get(player.getUniqueId())){
				player.addPotionEffect(effect);
			}
			savedPotionEffects.remove(player.getUniqueId());
		}
	}
	
	public static void enteredLandEffects(Player player, Faction faction){
		Set<FactionChallenge> cC = faction.getCompletedChallenges().keySet();
		
		for(FactionChallenge chal : cC){
			if(chal.getEffect() == null) continue;
			if(faction.hasChallengeCompleted(FactionChallenge.getNextTier(chal)) && (chal.getStatType() != StatType.ANIMALS_KILLED && chal.getStatType() != StatType.MONSTERS_KILLED)) continue;
			applyRewardedEffects(player, chal);
		}
	}
	
	public static void updateScores(Faction faction){
		long m = faction.getMembers().size() * 10;
		long l = faction.getClaimedLand().size() * 20;
		long o = faction.getStat(StatType.LAND_OVERCLAIMED) * 30;
		long lv = faction.getStat(StatType.LEVEL) * 50;
		long p = faction.getPower() / 2;
		float kdr = faction.getKDR();
		long k = kdr > 4 ? 4 : Math.round(kdr);
		if(k == 0) k = 1;
		faction.setStat(StatType.SCORE, (m + l + o + lv + p) * k);
	}
	
	public static void displayFList(Player player, int page){
		List<Faction> factions = DataHolder.getAllFactions();
		int dA = 10;
		int pM = factions.size() / dA + 1;
		if(page > pM){
			Util.message(player, "&cThat page does not exist.");
			return;
		}
		int sP = ((dA * page) - dA);
		int eP = sP + dA - 1 > factions.size() - 1 ? factions.size() - 1 : sP + dA - 1;
		Util.message(player, "&e-- &6Faction List " + page + "/" + pM + " &e--");
		if(sP <= factions.size()){
			for(int i = sP; i <= eP; i++){
				String name = factions.get(i).getName();
				new HoverMessage().messageTipCommand("&6[" + name + "&r&6] ", "/f info " + ChatColor.stripColor(name), "&6" + name, "&aClick here for more faction info.").message("&ePower: &6" + factions.get(i).getPower() + "/1000").send(player);
			}
			if(page < pM) Util.message(player, "&e-- &6Next Page /f list " + (page + 1) + " &e--");
			else Util.message(player, "&e -- &6Last Page &e--");
		}
	}
	
	public static void displayHelpList(Player player, int page){
		List<Command> commands = Arrays.asList(Command.values());
		int dA = 10;
		int pM = commands.size() / dA + 1;
		if(page > pM){
			Util.message(player, "&cThat page does not exist.");
			return;
		}
		int sP = ((dA * page) - dA);
		int eP = sP + dA - 1 > commands.size() - 1 ? commands.size() - 1 : sP + dA - 1;
		Util.message(player, "&e-- &6Help Page " + page + "/" + pM + " &e--");
		if(sP <= commands.size()){
			for(int i = sP; i <= eP; i++){
				Command command = commands.get(i);
				String name = "&6[" + command.getDescription()[0] + "] ";
				new HoverMessage().messageTip(name, command.getDescription()).message(command.getDescription()[1]).send(player);
			}
			if(page < pM) Util.message(player, "&e-- &6Next Page /f help " + (page + 1) + " &e--");
			else Util.message(player, "&e -- &6Last Page &e--");
		}
	}
	
	public static void displayRules(Player player){
		Faction faction = FactionUtil.getPlayerFaction(player);
		Util.message(player, "&e-- &6" + faction.getName() + " &r&6Rules &e--");
		for(String rule : FactionUtil.getPlayerFaction(player).getRules()){
			Util.message(player, rule);
		}
	}
	
	public static void addRule(Faction faction, Player player, String rule){
		if(faction.getRules().size() >= 30){
			Util.message(player, "&cYou cannot have more than 30 rules.");
			return;
		}
		rule = Util.setStringColors(rule);
		faction.getRules().add(rule);
		Util.message(player, "&eRule &6added&e.");
		faction.addActivityLogEntry("&e" + player.getName() + " edited the rules.");
	}
	
	public static void deleteRule(Faction faction, Player player, int line){
		if(line == 99) line = faction.getRules().size();
		faction.getRules().remove(line - 1);
		Util.message(player, "&eRule &6deleted&e.");
		faction.addActivityLogEntry("&e" + player.getName() + " edited the rules.");
	}
	
	public static void insertRule(Faction faction, Player player, String message, int line){
		if(faction.getRules().size() >= 30){
			Util.message(player, "&cYou cannot have more than 30 rules.");
			return;
		}
		message = Util.setStringColors(message);
		faction.getRules().add(line - 1, message);
		Util.message(player, "&eRule &6inserted&e at &6line " + line + "&e.");
		faction.addActivityLogEntry("&e" + player.getName() + " edited the rules.");
	}
	
	public static void setRule(Faction faction, Player player, String message, int line){
		message = Util.setStringColors(message);
		faction.getRules().set(line - 1, message);
		Util.message(player, "&6Set &erule at &6line " + line+ "&e.");
		faction.addActivityLogEntry("&e" + player.getName() + " edited the rules.");
	}
	
	
	
	
	
	
	
	
	
	
	public static void setNewNPC(Player player){
		if(DataHolder.getNPC() != null) DataHolder.getNPC().destroy();
		NPCCharacter c = new NPCCharacter();
		c.setSpawnLocation(player.getLocation()).setName("");
		c.setInteractHandler(new InteractHandler(){
			@Override
			public void onInteract(Player playerH){
				if(FactionUtil.isInFaction(playerH)) MenuRenderer.factionMain(playerH);
				else MenuRenderer.noFactionMain(playerH);
			}
		});
		c.spawn();
		Factions.plugin.getConfig().set("npc", c.toString());
		Factions.plugin.saveConfig();
		DataHolder.setNPC(c);
	}
	
	public static void spawnNPC(){
		String serNPC = Factions.plugin.getConfig().getString("npc");
		if(serNPC == null) return;
		NPCCharacter c = NPCCharacter.fromString(serNPC);
		c.setInteractHandler(new InteractHandler(){
			@Override
			public void onInteract(Player player){
				if(FactionUtil.isInFaction(player)) MenuRenderer.factionMain(player);
				else MenuRenderer.noFactionMain(player);
			}
		});
		c.spawn();
	}
	
	public static void handleClickedPlayerInfo(Player player, Player clicked){
		Util.message(player, "&eName: &6" + clicked.getName());
		if(FactionUtil.isInFaction(clicked)) Util.message(player, "&eFaction: &6" + FactionUtil.getPlayerFaction(clicked).getName());
		else Util.message(player, "&eFaction: &6None");
		Util.message(player, "&eCoins: &6" + CoinsAPI.getCoins(clicked.getUniqueId()));
		Util.message(player, "&eHealth: &6" + (int)clicked.getHealth() + "/20");
		Util.message(player, "&eXP Level: &6" + clicked.getLevel());
		DecimalFormat dF = new DecimalFormat("#.##");
		Util.message(player, "&eKDR: &6" + dF.format(StatsAPI.getStatsValueKDR(clicked.getUniqueId())));
	}
	
	public static void sendAllyMessage(Player player, String message){
		Faction faction = FactionUtil.getPlayerFaction(player);
		faction.sendMessage("&a►AChat◄ &r" + player.getDisplayName() + "&r&f: &r" + message);
		for(UUID ally : faction.getAlliances()){
			Faction aF = FactionUtil.getFactionFromId(ally);
			aF.sendMessage("&a►AChat◄ &r" + player.getDisplayName() + "&r&f: &r" + message);
		}
	}
	
	public static void givePowerOverTime(){
		new BukkitRunnable(){
			@Override
			public void run(){
				ArrayList<Faction> alreadyAdded = new ArrayList<Faction>();
				for(Player player : Bukkit.getOnlinePlayers()){
					if(FactionUtil.isInFaction(player)){
						Faction faction = FactionUtil.getPlayerFaction(player);
						if(alreadyAdded.contains(faction)) continue;
						faction.addPower(5, null);
					}
				}
			}
		}.runTaskTimer(Factions.plugin, 36000, 36000);
	}
	
	public static void handleMouseOverMessage(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		if(!FactionUtil.isInFaction(player)) return;
		event.setCancelled(true);
		String message = event.getMessage();
		Faction faction = FactionUtil.getPlayerFaction(player);
		Member member = FactionUtil.getPlayerMember(player);
		new HoverMessage().messageTip("&a[" + faction.getName() + "&a]", "&9Faction: &e" + faction.getName(), "&9Power: &e" + faction.getPower() + "/1000", "&9Faction KDR: &e" + faction.getKDR(), "&9Rank: &e" + member.getRank().getName()).message(" &r&e" + player.getDisplayName() + "&r&f: " + message).send(new ArrayList<Player>(Bukkit.getOnlinePlayers()));
	}
}
