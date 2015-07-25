package me.recolance.factions.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.recolance.coins.api.CoinsAPI;
import me.recolance.factions.Factions;
import me.recolance.factions.controller.Controller;
import me.recolance.factions.controller.FactionMapRenderer;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Serialization;
import me.recolance.factions.faction.AllianceInvitation;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.faction.FactionInvitation;
import me.recolance.factions.faction.FactionStatus;
import me.recolance.factions.faction.Member;
import me.recolance.factions.faction.Rank;
import me.recolance.factions.faction.RankPermission;
import me.recolance.factions.faction.VaultTab;
import me.recolance.factions.faction.Warp;
import me.recolance.factions.menu.MenuHelper;
import me.recolance.factions.menu.MenuRenderer;
import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;
import me.recolance.globalutil.utils.HoverMessage;
import me.recolance.playerlog.api.PlayerLogAPI;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor{

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] argument){
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		int length = argument.length;
		switch(cmd.getName().toLowerCase()){
		case "factions":
			if(length == 0) return handleFactions(player);
			switch(argument[0].toLowerCase()){
			
			case "make":
			case "create":
				if(length == 1) return handleFactionsCreate(player);
				else if(length == 2) return handleFactionsCreatePre(player, argument[1]);
				else return sendNonArg(player, Command.CREATE);
			
			case "delete":
			case "disband":
				if(length != 1)	return sendNonArg(player, Command.DISBAND);
				else return handleFactionsDisband(player);
			
			case "quit":
			case "leave":
				if(length != 1) return sendNonArg(player, Command.LEAVE);
				else return handleFactionsLeave(player);
			
			case "s":
			case "info":
			case "show":
			case "who":
				if(length == 1) return handleFactionsWho(player);
				else if(length == 2) return handleFactionsWhoOther(player, argument[1]);
				else return sendNonArg(player, Command.INFO);
			
			case "h":
			case "home":
				if(length != 1) return sendNonArg(player, Command.HOME);
				else return handleFactionsHome(player);
			
			case "sh":
			case "sethome":
				if(length != 1) return sendNonArg(player, Command.SETHOME);
				else return handleFactionsSetHome(player);
			
			case "delhome":
			case "unsethome":
				if(length != 1) return sendNonArg(player, Command.UNSETHOME);
				else return handleFactionsUnsetHome(player);
				
			case "stat":
			case "stats":
				if(length != 1 && length != 2) return sendNonArg(player, Command.STATS);
				else if(length == 1) return handleFactionsStats(player);
				else return handleFactionsStatsOther(player, argument[1]);
				
			case "roster":
			case "member":
			case "members":
				if(length != 1) return sendNonArg(player, Command.MEMBERS);
				else return handleFactionsMembers(player);
			
			case "kick":
			case "remove":
				if(length != 2) return sendNonArg(player,  Command.KICK);
				else return handleFactionsKick(player, argument[1]);
			
			case "invite":
			case "add":
				if(length != 2) return sendNonArg(player,  Command.INVITE);
				else return handleFactionsInvite(player, argument[1]);
				
			case "yes":
			case "accept":
				if(length != 1) return sendNonArg(player, Command.ACCEPT);
				else return handleFactionsAccept(player);
				
			case "no":
			case "deny":
				if(length != 1) return sendNonArg(player, Command.DENY);
				else return handleFactionsDeny(player);
			
			case "promote":
			case "demote":
			case "setrank":
				if(length != 3) return sendNonArg(player, Command.SETRANK);
				else return handleFactionsSetRank(player, argument[1], argument[2]);
				
			case "permissions":
			case "perm":
			case "rank":
			case "ranks":
				if(length == 1) return handleFactionsRanks(player);
				else{
					switch(argument[1].toLowerCase()){
					case "change":
					case "edit":
						if(length != 3) return sendNonArg(player, Command.RANKS_EDIT);
						else return handleFactionsRanksEdit(player, argument[2]);
					
					case "make":
					case "add":
					case "addrank":
					case "create":
						if(length != 3) return sendNonArg(player, Command.RANKS_ADD);
						else return handleFactionsRankCreate(player, argument[2]);
						
					case "remove":
					case "delete":
						if(length != 3) return sendNonArg(player, Command.RANKS_DELETE);
						else return handleFactionsRanksDelete(player, argument[2]);
					
					default: Util.message(player, "&cUnknown factions command. Type /f help.");
					}
				}
				break;
	
			case "setowner":
			case "owner":
			case "setleader":
			case "leader":
				if(length == 2) return handleFactionsLeader(player, argument[1]);
				else return sendNonArg(player, Command.LEADER);
				
			case "alliance":
			case "ally":
				if(length != 2) return sendNonArg(player, Command.ALLY);
				else return handleFactionsAlly(player, argument[1]);
			
			case "contest":
			case "enemy":
				if(length != 2) return sendNonArg(player, Command.ENEMY);
				else return handleFactionsEnemy(player, argument[1]);
			
			case "truce":
			case "neutral":
				if(length != 2) return sendNonArg(player, Command.NEUTRAL);
				else return handleFactionsNeutral(player, argument[1]);
				
			case "relations":
				if(length != 1) return sendNonArg(player, Command.RELATIONS);
				else return handleFactionsRelations(player);
				
			case "contested":
			case "enemies":
				if(length != 1) return sendNonArg(player, Command.ENEMIES);
				else return handleFactionsEnemies(player);
				
			case "allies":
			case "alliances":
				if(length != 1) return sendNonArg(player, Command.ALLIES);
				else return handleFactionsAllies(player);
				
			case "enemiedby":
			case "enemiesof":
				if(length != 1) return sendNonArg(player, Command.ENEMIEDBY);
				else return handleFactionsEnemiesOf(player);
				
			case "v":
			case "vault":
				if(length == 1) return handleFactionsVault(player);
				switch(argument[1].toLowerCase()){
				case "edittab":
				case "edit":
					if(length != 3) return sendNonArg(player, Command.VAULT_EDIT);
					else return handleFactionsVaultEdit(player, argument[2]);
				
				case "balance":
				case "coins":
				case "money":
				case "wallet":
				case "bal":
					if(length != 2) return sendNonArg(player, Command.VAULT_BALANCE);
					else return handleFactionsVaultMoney(player);
					
				default: 
				if(length != 2) return sendNonArg(player, Command.VAULT_DEFAULT);
				else return handleFactionsVaultDefault(player, argument[1]);	
				}
				
			case "warplist":
			case "warps":
				if(length != 1) return sendNonArg(player, Command.WARPS);
				else return handleFactionsWarps(player);
				
			case "w":
			case "warp":
				if(length != 2) return sendNonArg(player, Command.WARP);
				else return handleFactionsWarp(player, argument[1]);
				
			case "sw":
			case "setw":
			case "setwarp":
				if(length != 2) return sendNonArg(player, Command.SETWARP);
				return handleFactionsSetWarp(player, argument[1]);
				
			case "dw":
			case "delw":
			case "delwarp":
				if(length != 2) return sendNonArg(player, Command.DELWARP);
				else return handleFactionsDelWarp(player, argument[1]);
				
			case "claim":
				if(length != 1) return sendNonArg(player, Command.CLAIM);
				else return handleFactionsClaim(player);
				
			case "oc":
			case "overclaim":
				if(length != 1) return sendNonArg(player, Command.OVERCLAIM);
				else return handleFactionsOverClaim(player);
				
			case "uc":
			case "unclaim":
				if(length != 1) return sendNonArg(player, Command.UNCLAIM);
				else return handleFactionsUnclaim(player);
			
			case "land":
			case "chunks":
				if(length != 1) return sendNonArg(player, Command.LAND);
				else return handleFactionsLand(player);
				
			case "sc":
			case "seechunk":
			case "chunkinfo":
			case "chunk":
				if(length != 1) return sendNonArg(player, Command.CHUNK);
				else return handleFactionsChunk(player);
					
			case "join":
			case "j":
				if(length != 2) return sendNonArg(player, Command.JOIN);
				else return handleFactionsJoin(player, argument[1]);
				
			case "open":
				if(length != 1) return sendNonArg(player, Command.OPEN);
				else return handleFactionsOpen(player);
				
			case "inviteonly":
			case "close":
				if(length != 1) return sendNonArg(player, Command.CLOSE);
				else return handleFactionsClose(player);
				
			case "tag":
			case "name":
			case "setname":
			case "rename":
				if(length != 2) return sendNonArg(player, Command.TAG);
				return handleFactionsTag(player, argument[1]);
				
			case "desc":
			case "description":
				if(length == 1) return sendNonArg(player, Command.DESC);
				else return handleFactionsDesc(player, argument);
			
			case "icon":
			case "seticon":
				if(length != 1) return sendNonArg(player, Command.ICON);
				else return handleFactionsIcon(player);
				
			case "sound":
				if(length != 1) return sendNonArg(player, Command.PLAY_SOUND);
				else return handleFactionsSound(player);
				
			case "setsound":
				if(length != 1) return sendNonArg(player, Command.SOUND);
				else return handleFactionsSetSound(player);
				
			case "chal":
			case "achievements":
			case "challenges":
			case "challenge":
				if(length != 1) return sendNonArg(player, Command.CHALLENGE);
				else return handleFactionsChallenge(player);
				
			case "chat":
			case "c":
			case "talk":
			case "msg":
				if(length == 1) return sendNonArg(player, Command.CHAT);
				else return handleFactionsChat(player, argument);
					
			case "achat":
			case "amsg":
			case "ac":
			case "allychat":
				if(length == 1) return sendNonArg(player, Command.ACHAT);
				else return handleFactionsAllyChat(player, argument);
			
			case "chattoggle":
			case "ctoggle":
			case "ct":
				if(length != 1) return sendNonArg(player, Command.CHAT_TOGGLE);
				else return handleFactionsChatToggle(player);
			case "list":
				if(length == 1) return handleFactionsList(player, "1");
				else if(length == 2) return handleFactionsList(player, argument[1]);
				else return sendNonArg(player, Command.LIST);
				
			case "map":
				if(length != 1) return sendNonArg(player, Command.MAP);
				else return handleFactionsMap(player);
				
			case "rules":
			case "rule":
				if(length == 1) return handleFactionsRules(player);
				switch(argument[1].toLowerCase()){
				case "add":
					if(length == 2) return sendNonArg(player, Command.RULES_ADD);
					else return handleFactionsRulesAdd(player, argument);
				
				case "remove":
				case "delete":
					if(length == 2) return handleFactionsRuleDelete(player, "99");
					else if(length == 3) return handleFactionsRuleDelete(player, argument[2]);
					else return sendNonArg(player, Command.RULES_DELETE);
				
				case "set":
					if(length > 3)return handleFactionsRuleSet(player, argument[2], argument);
					else return sendNonArg(player, Command.RULES_SET);
				
				case "insert":
					if(length > 3) return handleFactionsRuleInsert(player, argument[2], argument);
					else return sendNonArg(player, Command.RULES_INSERT);
				default: Util.message(player, "&cUnknown factions command. Type /f help.");
				}
				break;
			case "commands":
			case "help":
				if(length == 1) return handleFactionsHelp(player, "1");
				else if(length == 2) return handleFactionsHelp(player, argument[1]);
				else return sendNonArg(player, Command.HELP);
			
			case "notifyjoin":
			case "notify":
				if(length != 2) return sendNonArg(player, Command.NOTIFY);
				else return handleFactionsNotifyJoin(player, argument[1]);
				
			case "l":
			case "level":
				if(length == 1) return handleFactionsLevel(player);
				else if(length == 2) return handleFactionsLevelOther(player, argument[1]);
				else return sendNonArg(player, Command.LEVEL);
				
			case "power":
			case "p":
				if(length == 1) return handleFactionsPower(player);
				else if(length == 2) return handleFactionsPowerOther(player, argument[1]);
				else return sendNonArg(player, Command.POWER);
			default: Util.message(player, "&cUnknown factions command. Type /f help."); return true;
			}
			
		case "fadmin":
			if(!player.hasPermission("factions.admin")){
				Util.message(player, "&cYou do not have permission.");
				return true;
			}else if(length < 1){
				Util.message(player, "&cUnknown factions admin command.");
				return true;
			}
			switch(argument[0].toLowerCase()){
			case "setnpc":
				if(length != 1){
					Util.message(player, "&c/fadmin setnpc");
					return true;
				}else return handleAdminSetNPC(player);
				
			default: Util.message(player, "&cUnknown faction admin command."); return true;
			}
			
		}
		return false;
	}
	
	public static boolean handleFactions(Player player){
		if(!FactionUtil.isInFaction(player)) MenuRenderer.noFactionMain(player);
		else MenuRenderer.factionMain(player);
		return true;
	}

	public static boolean handleFactionsCreate(Player player){
		if(!FactionUtil.isInFaction(player)) MenuRenderer.creationName(player);
		else Util.message(player, "&cYou are already in a faction.");
		return true;
	}
	
	public static boolean handleFactionsCreatePre(Player player, String name){
		if(!FactionUtil.isInFaction(player)){
			if(!FactionUtil.isApplicableFactionName(name, player)) return true;
			else{
				List<Object> creationData = new ArrayList<Object>();
				creationData.add(name);
				MenuHelper.setCreationData(player, creationData);
				MenuRenderer.creationIcon(player);
			}
		}else Util.message(player, "&cYou are already in a faction.");
		return true;
	}
	
	public static boolean handleFactionsDisband(Player player){
		if(FactionUtil.isInFaction(player)){
			if(FactionUtil.isLeader(player)){
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(faction.getDateCreated() < (System.currentTimeMillis() - 3600000L)) MenuRenderer.disbandConfirmation(player, faction);
				else Util.message(player, "&cYou cannot disband this faction for another " + Util.getRemainingTimeString(faction.getDateCreated(), 3600000L) + ".");
			}else Util.message(player, "&cOnly the faction leader can disband the faction.");
		}else Util.message(player, "&cYou are not in a faction.");
		return true;
	}
	
	public static boolean handleFactionsLeave(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(FactionUtil.isLeader(player)) Util.message(player, "&cThe faction leader cannot leave the faction.");
		else{
			Member member = FactionUtil.getPlayerMember(player);
			if(member.getDateJoined() > (System.currentTimeMillis() - 300000L)) Util.message(player, "&cYou cannot leave the faction for another " + Util.getRemainingTimeString(member.getDateJoined(), 300000L) + ".");
			else FactionUtil.getPlayerFaction(player).leaveMember(player);
		}
		return true;
	}
	
	public static boolean handleFactionsWho(Player player){
		if(FactionUtil.isInFaction(player)) FactionUtil.getPlayerFaction(player).showInfo(player);
		else Util.message(player, "&cYou are not in a faction.");
		return true;
	}
	
	public static boolean handleFactionsWhoOther(Player player, String factionName){
		Faction faction = FactionUtil.getFactionFromName(factionName);
		if(faction != null) faction.showInfo(player);
		else{
			UUID playerId = PlayerLogAPI.nameToUUID(factionName);
			if(playerId == null || !FactionUtil.isInFaction(playerId)) Util.message(player, "&cThat faction or faction member does not exist.");
			else FactionUtil.getPlayerFaction(playerId).showInfo(player);
		}
		return true;
	}
	
	public static boolean handleFactionsHome(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else FactionUtil.getPlayerFaction(player).teleportHome(player);
		return true;
	}
	
	public static boolean handleFactionsSetHome(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else FactionUtil.getPlayerFaction(player).setHome(player);
		return true;
	}
	
	public static boolean handleFactionsUnsetHome(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else FactionUtil.getPlayerFaction(player).unsetHome(player);
		return true;
	}
	
	public static boolean handleFactionsStats(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.stats(player, FactionUtil.getPlayerFaction(player));
		return true;
	}
	
	public static boolean handleFactionsStatsOther(Player player, String factionName){
		Faction faction = FactionUtil.getFactionFromName(factionName);
		if(faction == null) Util.message(player, "&cThat faction does not exist.");
		else MenuRenderer.stats(player, faction);
		return true;
	}
	
	public static boolean handleFactionsMembers(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.members(player);
		return true;
	}
	
	public static boolean handleFactionsKick(Player player, String playerName){
		if(!FactionUtil.isInFaction(player))Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.KICK_MEMBERS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			UUID playerId = PlayerLogAPI.nameToUUID(playerName);
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(faction != FactionUtil.getPlayerFaction(playerId)) Util.message(player, "&cThat player is not in your faction.");
			else if(FactionUtil.isLeader(playerId)) Util.message(player, "&cYou cannot kick the faction leader.");
			else if(player.getUniqueId().equals(playerId)) Util.message(player, "&cYou cannot kick yourself from the faction.");
			else faction.kickMember(player, playerId);
		}
		return true;
	}
	
	public static boolean handleFactionsInvite(Player player, String playerName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.INVITE_MEMBERS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Player playerFromName = Bukkit.getPlayer(playerName);
			if(playerFromName == null) Util.message(player, "&cThat player is not online or does not exist.");
			else if(FactionInvitation.factionInvitations.containsKey(playerFromName) && FactionInvitation.factionInvitations.get(playerFromName).getFactionInvitedTo() == FactionUtil.getPlayerFaction(player)) Util.message(player, "&cYou already have a pending faction invitation with this player.");
			else if(FactionUtil.isInFaction(playerFromName)) Util.message(player, "&cThat player is already in a faction.");
			else if(FactionUtil.getPlayerFaction(player).getMembers().size() >= 45) Util.message(player, "&cThat faction is full.");
			else FactionUtil.getPlayerFaction(player).sendInvitation(player, playerFromName);
		}
		return true;
	}
	
	public static boolean handleFactionsAccept(Player player){
		boolean factionInv = FactionInvitation.factionInvitations.containsKey(player);
		boolean allyInv = false;
		if(FactionUtil.isInFaction(player) && AllianceInvitation.allianceInvitations.containsKey(FactionUtil.getPlayerFaction(player))) allyInv = true;
		if(factionInv){
			Faction.acceptInvitation(player);
			return true;
		}
		if(allyInv){
			AllianceInvitation invitation = AllianceInvitation.allianceInvitations.get(FactionUtil.getPlayerFaction(player));
			FactionUtil.getPlayerFaction(player).acceptAlly(invitation.getWhoRecievedFaction(), invitation.getWhoSentFaction());
			return true;
		}
		Util.message(player, "&cYou have nothing to accept.");
		return true;
	}
	
	public static boolean handleFactionsDeny(Player player){
		boolean factionInv = FactionInvitation.factionInvitations.containsKey(player);
		boolean allyInv = false;
		if(FactionUtil.isInFaction(player) && AllianceInvitation.allianceInvitations.containsKey(FactionUtil.getPlayerFaction(player))) allyInv = true;
		if(factionInv){
			Util.message(player, "&cYou have denied to invitation.");
			Faction.denyInvitation(player);
			return true;
		}
		if(allyInv){
			Faction faction = FactionUtil.getPlayerFaction(player);
			AllianceInvitation invitation = AllianceInvitation.allianceInvitations.get(faction);
			faction.denyAlly(invitation.getWhoRecievedFaction());
			faction.sendMessage("&c" + player.getName() + " has denied the ally request.");
			return true;
		}
		if(!factionInv) Util.message(player, "&cYou have nothing to deny.");
		return true;
	}
	
	public static boolean handleFactionsSetRank(Player player, String playerName, String rankName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_MEMBER_RANK)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			UUID playerId = PlayerLogAPI.nameToUUID(playerName);
			if(playerId == null) Util.message(player, "&cThat player does not exist.");
			else{
				Member member = FactionUtil.getPlayerMember(playerId);
				if(member == null) Util.message(player, "&cThat player is not in a faction.");
				else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(faction != FactionUtil.getPlayerFaction(playerId)) Util.message(player, "&cThat player is not in your faction.");
					else if(faction.getLeader().equals(member.getPlayer())) Util.message(player, "&cYou cannot change the faction leader's rank.");
					else{
						Rank rank = FactionUtil.getRankFromName(rankName, faction);
						if(rank == null) Util.message(player, "&cThat rank does not exist.");
						else if(rank.isLeader()) Util.message(player, "&cYou cannot set a member's rank to Leader.");
						else faction.setMemberRank(player, member, rank);
					}
				}
			}
		}
		return true;
	}	
	
	public static boolean handleFactionsRanks(Player player){
		if(FactionUtil.isInFaction(player)) MenuRenderer.ranks(player);
		else Util.message(player, "&cYou are not in a faction.");
		return true;
	}
	
	public static boolean handleFactionsRanksEdit(Player player, String rankName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Rank rank = FactionUtil.getRankFromName(rankName, FactionUtil.getPlayerFaction(player));
			if(rank == null) Util.message(player, "&cThat rank does not exist in your faction.");
			else if(rank.isLeader()) Util.message(player, "&cYou cannot edit the Leader rank.");
			else{
				MenuRenderer.editRank(player, rank);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsRankCreate(Player player, String name){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(faction.getRanks().size() >= 10) Util.message(player, "&cYour faction can not have any more ranks.");
			else if(FactionUtil.isApplicableRankName(name, player, faction)){
				faction.addRank(player, name);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsRanksDelete(Player player, String rankName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			Rank rank = FactionUtil.getRankFromName(rankName, faction);
			if(rank == null) Util.message(player, "&cThat rank does not exist.");
			else if(rank.isDefault()) Util.message(player, "&cYou can not remove the default rank.");
			else if(rank.isLeader()) Util.message(player, "&cYou cannot remove the Leader rank.");
			else faction.removeRank(player, rank);
		}
		return true;
	}
	
	public static boolean handleFactionsAlly(Player player, String factionName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction allyFaction = FactionUtil.getFactionFromName(factionName);
			if(allyFaction == null) Util.message(player, "&cThat faction does not exist.");
			else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(allyFaction == faction) Util.message(player, "&cYou cannot ally your own faction.");
				else if(faction.getAlliances().size() >= 9) Util.message(player, "&cYour faction already has the max amount of allies at one time.");
				else if(allyFaction.getAlliances().size() >= 9) Util.message(player, "&cThat faction already has that max amount of allies.");
				else if(faction.hasAlliance(allyFaction)) Util.message(player, "&cThat faction is already your ally.");
				else if(faction.isEnemiedBy(allyFaction)) Util.message(player, "&cYou cannot ally a faction that is enemying you.");
				else if(faction.hasEnemy(allyFaction)) Util.message(player, "&cYou cannot ally a faction that is your enemy.");
				else if(allyFaction.getEditRelationPlayers().isEmpty()) Util.message(player, "&cNobody online that faction can accept an ally request.");
				else if(AllianceInvitation.allianceInvitations.containsKey(allyFaction)) Util.message(player, "&cThat faction is already responding to another ally request.");
				else{
					faction.sendAllyRequest(player, allyFaction);
				}
			}
		}
		return true;
	}
	
	public static boolean handleFactionsEnemy(Player player, String factionName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction enemyFaction = FactionUtil.getFactionFromName(factionName);
			if(enemyFaction == null) Util.message(player, "&cThat faction does not exist.");
			else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(enemyFaction == faction) Util.message(player, "&cYou cannot enemy your own faction.");
				else if(faction.getEnemies().size() >= 9) Util.message(player, "&cYour faction already has the max amount of enemies at one time.");
				else if(faction.getEnemies().contains(enemyFaction.getId())) Util.message(player, "&cYou are already enemying this faction.");
				else if(faction.getAlliances().contains(enemyFaction.getId())) Util.message(player, "&cYou cannot enemy a faction that is your ally.");
				else{
					faction.setEnemy(enemyFaction);
				}
			}
		}
		return true;
	}
	
	public static boolean handleFactionsNeutral(Player player, String factionName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction neutralFaction = FactionUtil.getFactionFromName(factionName);
			if(neutralFaction == null) Util.message(player, "&cThat faction does not exist.");
			else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(!faction.getAlliances().contains(neutralFaction.getId()) && !faction.getEnemies().contains(neutralFaction.getId())) Util.message(player, "&cThat faction is not an enemy or ally of your faction.");
				else faction.setNeutral(neutralFaction);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsVault(Player player){
		if(FactionUtil.isInFaction(player)) MenuRenderer.vault(player, 1);
		else Util.message(player, "&cYou are not in a faction.");
		return true;
	}
	
	public static boolean handleFactionsVaultEdit(Player player, String tabName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction");
		else{
			if(!FactionUtil.hasPermission(player, RankPermission.EDIT_VAULT)) Util.message(player, "&cYour faction rank does not allow you to do this.");
			else{
				tabName = Util.setStringColors(tabName);
				Faction faction = FactionUtil.getPlayerFaction(player);
				VaultTab tab = FactionUtil.getVaultTabFromName(tabName, faction);
				if(tab == null) Util.message(player, "&cThat vault tab does not exist.");
				else if(faction.isTabLocked(tab)) Util.message(player, "&cYou cannot edit a locked vault tab.");
				else{
					MenuRenderer.vaultEditTab(player, tab);
				}
			}
		}
		return true;
	}
	
	public static boolean handleFactionsWarps(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.warps(player);
		return true;
	}
	
	public static boolean handleFactionsWarp(Player player, String warpName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction");
		else if(!FactionUtil.hasPermission(player, RankPermission.USE_WARPS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			Warp warp = FactionUtil.getWarpFromName(warpName, faction);
			if(warp == null) Util.message(player, "&cThat warp does not exist in your faction.");
			else faction.useWarp(player, warp);
		}
		return true;
	}
	
	public static boolean handleFactionsSetWarp(Player player, String warpName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(FactionUtil.isApplicableWarpName(warpName, player, faction)){
				faction.addWarp(warpName, player.getLocation(), player);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsDelWarp(Player player, String warpName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.DELETE_WARPS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			Warp warp = FactionUtil.getWarpFromName(warpName, faction);
			if(warp == null) Util.message(player, "&cThat warp does not exist in your faction.");
			else faction.removeWarp(player, warp);
		}
		return true;
	}
	
	public static boolean handleFactionsClaim(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.CLAIM_LAND)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(faction.getMembers().size() < 2) Util.message(player, "&cYou must have at least 2 members in your faction to claim land.");
			else if(FactionUtil.isApplicableClaim(player.getLocation().getChunk(), faction, player)){
				faction.claimLand(Serialization.chunkToString(player.getLocation().getChunk()), player);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsOverClaim(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if((faction.getDateCreated() + 172800000L) > System.currentTimeMillis()) Util.message(player, "&cYour faction cannot start overclaiming for another " + Util.getRemainingTimeString(faction.getDateCreated(), 172800000L) + ".");
			else if(faction.getMembers().size() < 3) Util.message(player, "&cYou must have at least 2 members in your faction to claim land.");
			else if(!FactionUtil.hasPermission(player, RankPermission.CLAIM_LAND)) Util.message(player, "&cYour faction rank does not allow you to do this.");
			else{
				Chunk chunk = player.getLocation().getChunk();
				if(FactionUtil.isApplicableOverClaim(chunk, faction, player))  faction.overClaimLand(DataHolder.getChunkClaimedLandMap().get(Serialization.chunkToString(chunk)), player);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsUnclaim(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.UNCLAIM_LAND)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Chunk chunk = player.getLocation().getChunk();
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(FactionUtil.isApplicableUnclaim(chunk, faction, player)) faction.unClaimLand(DataHolder.getChunkClaimedLandMap().get(Serialization.chunkToString(chunk)), player);
		}
		return true;
	}
	
	public static boolean handleFactionsRelations(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.relations(player);
		return true;
	}
	
	public static boolean handleFactionsEnemies(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.enemies(player);
		return true;
	}
	
	public static boolean handleFactionsAllies(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.alliances(player);
		return true;
	}
	
	public static boolean handleFactionsEnemiesOf(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.enemiedBy(player);
		return true;
	}
	
	public static boolean handleFactionsVaultDefault(Player player, String tabName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			VaultTab tab = FactionUtil.getVaultTabFromName(tabName, faction);
			if(tab == null) Util.message(player, "&cThat vault tab does not exist.");
			else if(faction.isTabLocked(tab)) Util.message(player, "&cThat vault tab is locked.");
			else MenuRenderer.vault(player, tab.getTabNumber());
		}
		return true;
	}
	
	public static boolean handleFactionsLand(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.claimedLand(player, 1);
		return true;
	}
	
	public static boolean handleFactionsChunk(Player player){
		if(!FactionUtil.isChunkClaimed(player.getLocation().getChunk())) Util.message(player, "&cThe chunk you are standing in is not claimed.");
		else Controller.chunkInfo(player, FactionUtil.getChunkClaimedLand(player.getLocation().getChunk()));
		return true;
	}
	
	public static boolean handleFactionsVaultMoney(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			int bal = FactionUtil.getPlayerFaction(player).getVault().getMoney();
			String c = bal == 1 ? "Coin" : "Coins";
			Util.message(player, "&eVault Balance: &6" + bal + " " + c);
		}
		return true;
	}
	
	public static boolean handleFactionsOpen(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.SET_STATUS)) Util.message(player,  "&cYour faction rank does not allow you to do this.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(faction.isOpen()) Util.message(player, "&cYour faction is already open.");
			else Controller.changeStatus(faction, player, FactionStatus.OPEN);
		}
		return true;
	}
	
	public static boolean handleFactionsClose(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.SET_STATUS)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(faction.isClosed()) Util.message(player, "&cYour faction is already closed.");
			else Controller.changeStatus(faction, player, FactionStatus.CLOSED);
		}
		return true;
	}
	
	public static boolean handleFactionsJoin(Player player, String name){
		if(FactionUtil.isInFaction(player)) Util.message(player, "&cYou are already in a faction.");
		else{
			Faction faction = FactionUtil.getFactionFromName(name);
			if(faction == null) Util.message(player, "&cThat faction does not exist.");
			else if(faction.isClosed()) Util.message(player, "&cThat faction is closed and requires an invite to join.");
			else{
				faction.addMember(player);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsTag(Player player, String name){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.isLeader(player)) Util.message(player, "&cOnly tha faction leader can do that.");
		else{
			int amount = Factions.plugin.getConfig().getInt("prices.changeName");
			if(!CoinsAPI.hasCoins(player.getUniqueId(), amount)) Util.message(player, "&cYou don't have " + amount + " coins.");
			else if(FactionUtil.isApplicableFactionName(name, player)){
				CoinsAPI.removeCoins(player.getUniqueId(), amount, "Faction name change.", "Factions name change");
				FactionUtil.getPlayerFaction(player).changeName(player, name);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsDesc(Player player, String[] descParts){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.SET_DESCRIPTION)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			StringBuilder string = new StringBuilder();
			int i = 0;
			for(String des : descParts){
				i ++;
				if(i == 1) continue;
				string.append(des + " ");
			}
			if(string.length() > 0) string.setLength(string.length() - 1);
			String desc = Util.setStringColors(string.toString());
			if(FactionUtil.isApplicableDescription(desc, player)) FactionUtil.getPlayerFaction(player).changeDescription(player, desc);
		}
		return true;
	}
	
	public static boolean handleFactionsSetSound(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.SET_SOUND)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else MenuRenderer.changeSound(player);
		return true;
	}
	
	public static boolean handleFactionsIcon(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.SET_ICON)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else MenuRenderer.changeIcon(player);
		return true;
	}
	
	public static boolean handleFactionsSound(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Util.message(player, "&ePlaying your faction's &6sound&e.");
			player.playSound(player.getLocation(), FactionUtil.getPlayerFaction(player).getSound().getSound(), 1, 1);
		}
		return true;
	}
	
	public static boolean handleFactionsChallenge(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else MenuRenderer.challenges(player);
		return true;
	}
	
	public static boolean handleFactionsChat(Player player, String[] messageParts){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			int i = 0;
			StringBuilder string = new StringBuilder();
			for(String part : messageParts){
				i++;
				if(i == 1) continue;
				string.append(part + " ");
			}
			if(string.length() > 0) string.setLength(string.length() - 1);
			String message = Util.setStringColors(string.toString());
			FactionUtil.getPlayerFaction(player).sendMessage("&a►F-Chat◄ " + player.getDisplayName() + "&f: " + message);
		}
		return true;
	}
	
	public static boolean handleFactionsAllyChat(Player player, String[] messageParts){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(faction.getAlliances().isEmpty()) Util.message(player, "&cYour faction has no allies to chat to.");
			else{
				int i = 0;
				StringBuilder string = new StringBuilder();
				for(String part : messageParts){
					i++;
					if(i == 1) continue;
					string.append(part + " ");
				}
				if(string.length() > 0) string.setLength(string.length() - 1);
				String message = Util.setStringColors(string.toString());
				Controller.sendAllyMessage(player, message);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsList(Player player, String page){
		if(!Util.stringIsNumerical(page)) Util.message(player, "&cThat is not a page number.");
		else{
			int pN = Integer.parseInt(page);
			Controller.displayFList(player, pN);
		}
		return true;
	}
	
	public static boolean handleFactionsMap(Player player){
		if(FactionMapRenderer.isUsingMap(player)){
			FactionMapRenderer.removeUsingMap(player);
			Util.message(player, "&eFaction &6map &eis now turned &6off&e.");
		}else{
			FactionMapRenderer.setUsingMap(player);
			Util.message(player, "&eFaction &6map &eis now turned &6on&e.");
		}
		return true;
	}
	
	public static boolean handleFactionsRules(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else Controller.displayRules(player);
		return true;
	}
	
	public static boolean handleFactionsRulesAdd(Player player, String[] messageParts){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RULES)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else{
			int i = 0;
			StringBuilder string = new StringBuilder();
			for(String part : messageParts){
				i++;
				if(i == 1 || i == 2) continue;
				string.append(part + " ");
			}
			if(string.length() > 0) string.setLength(string.length() - 1);
			String message = string.toString();
			FactionUtil.getPlayerFaction(player).addRule(player, message);
		}
		return true;
	}
	
	public static boolean handleFactionsRuleDelete(Player player, String line){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RULES)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else if(!Util.stringIsNumerical(line)) Util.message(player, "&cThat is not a possible line number.");
		else if(line.length() > 2) Util.message(player, "&cThat line number is too big.");
		else{
			int num = Integer.parseInt(line);
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(faction.getRules().isEmpty()) Util.message(player, "&cThere are no rules to delete.");
			else if(num > faction.getRules().size() && num != 99) Util.message(player, "&cThere is no rule at that line number.");
			else FactionUtil.getPlayerFaction(player).deleteRule(player, num);
		}
		return true;
	}
	
	public static boolean handleFactionsRuleInsert(Player player, String line, String[] messageParts){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RULES)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else if(!Util.stringIsNumerical(line)) Util.message(player, "&cThat is not a possible line number.");
		else if(line.length() > 2) Util.message(player, "&cThat line number is too big.");
		else{
			int num = Integer.parseInt(line);
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(num > faction.getRules().size()) Util.message(player, "&cThere is no rule at that line number.");
			else{
				int i = 0;
				StringBuilder string = new StringBuilder();
				for(String part : messageParts){
					i++;
					if(i == 1 || i == 2 || i == 3) continue;
					string.append(part + " ");
				}
				if(string.length() > 0) string.setLength(string.length() - 1);
				String message = string.toString();
				FactionUtil.getPlayerFaction(player).insertRule(player, message, num);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsRuleSet(Player player, String line, String[] messageParts){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RULES)) Util.message(player, "&cYour faction rank does not allow you to do this.");
		else if(!Util.stringIsNumerical(line)) Util.message(player, "&cThat is not a possible line number.");
		else if(line.length() > 2) Util.message(player, "&cThat line number is too big.");
		else{
			int num = Integer.parseInt(line);
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(num > faction.getRules().size()) Util.message(player, "&cThere is no rule at that line number.");
			else{
				int i = 3;
				StringBuilder string = new StringBuilder();
				for(String part : messageParts){
					i++;
					if(i == 1 || i == 2 || i == 3) continue;
					string.append(part + " ");
				}
				if(string.length() > 0) string.setLength(string.length() - 1);
				String message = string.toString();
				faction.setRule(player, message, num);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsLeader(Player player, String playerName){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			if(!faction.getLeader().equals(player.getUniqueId())) Util.message(player, "&cYou are not the faction's leader.");
			else{
				Player nLP = Bukkit.getPlayer(playerName);
				if(nLP == null || !nLP.isOnline()) Util.message(player, "&cThat player does not exist or is not online.");
				else if(nLP == player) Util.message(player, "&cYou are already the faction's leader.");
				else if(FactionUtil.getPlayerFaction(nLP) != faction) Util.message(player, "&cThat player is not in your faction.");
				else MenuRenderer.leaderConfirmation(player, playerName);
			}
		}
		return true;
	}
	
	public static boolean handleFactionsHelp(Player player, String page){
		if(!Util.stringIsNumerical(page)) Util.message(player, "&cThat is not a page number.");
		else Controller.displayHelpList(player, Integer.parseInt(page));
		return true;
	}
	
	public static boolean handleFactionsNotifyJoin(Player player, String fN){
		if(FactionUtil.isInFaction(player)) Util.message(player, "&cYou are already in a faction.");
		else{
			Faction faction = FactionUtil.getFactionFromName(fN);
			if(faction == null) Util.message(player, "&cThat faction does not exist.");
			else{
				for(String string : faction.getActivityLog()){
					if(string.contains(player.getName())){
						Util.message(player, "&cYou have already recently notified that faction.");
						return true;
					}
				}
				Util.message(player, "&eYou have notified &6" + faction.getName() + " &ethat you are interested in &6joining&e.");
				faction.addActivityLogEntry("&b" + player.getName() + " is interested in joining!");
			}
		}
		return true;
	}
	
	public static boolean handleFactionsLevel(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			Util.message(player, "&e" + faction.getName() + "&r&e's Level: &6" + faction.getLevel() + "/25");
			Util.message(player, "&e" + faction.getName() + "&r&e's Exp: &6" + faction.getExp() + "/" + faction.getRequiredExp());
		}
		return true;
	}
	
	public static boolean handleFactionsLevelOther(Player player, String factionName){
		Faction faction = FactionUtil.getFactionFromName(factionName);
		if(faction != null){
			Util.message(player, "&e" + faction.getName() + "&r&e's Level: &6" + faction.getLevel() + "/25");
			Util.message(player, "&e" + faction.getName() + "&r&e's Exp: &6" + faction.getExp() + "/" + faction.getRequiredExp());
		}else{
			UUID playerId = PlayerLogAPI.nameToUUID(factionName);
			if(playerId == null || !FactionUtil.isInFaction(playerId)) Util.message(player, "&cThat faction or faction member does not exist.");
			else{
				Faction factionP = FactionUtil.getPlayerFaction(playerId);
				Util.message(player, "&e" + factionP.getName() + "&r&e's Level: &6" + factionP.getLevel() + "/25");
				Util.message(player, "&e" + factionP.getName() + "&r&e's Exp: &6" + factionP.getExp() + "/" + factionP.getRequiredExp());
			}
		}
		return true;
	}
	
	public static boolean handleFactionsPower(Player player){
		if(!FactionUtil.isInFaction(player)) Util.message(player, "&cYou are not in a faction.");
		else{
			Faction faction = FactionUtil.getPlayerFaction(player);
			Util.message(player, "&e" + faction.getName() + "&r&e's Power: &6" + faction.getPower() + "/1000");
		}
		return true;
	}
	
	
	public static boolean handleFactionsPowerOther(Player player, String factionName){
		Faction faction = FactionUtil.getFactionFromName(factionName);
		if(faction != null) Util.message(player, "&e" + faction.getName() + "&r&e's Power: &6" + faction.getPower() + "/1000");
		else{
			UUID playerId = PlayerLogAPI.nameToUUID(factionName);
			if(playerId == null || !FactionUtil.isInFaction(playerId)) Util.message(player, "&cThat faction or faction member does not exist.");
			else{
				Faction factionP = FactionUtil.getPlayerFaction(playerId);
				Util.message(player, "&e" + factionP.getName() + "&r&e's Power: &6" + factionP.getPower() + "/1000");
			}
		}
		return true;
	}
	
	public static boolean handleFactionsChatToggle(Player player){
		if(!FactionUtil.isInFaction(player)){
			Util.message(player, "&cYou are not in a faction.");
		}else if(FactionUtil.isInFactionChat(player)){
			DataHolder.inFChat.remove(player.getUniqueId());
			Util.message(player, "&eYou are &6no longer &ein &6faction chat&e.");
		}else{
			DataHolder.inFChat.add(player.getUniqueId());
			Util.message(player, "&6Everything you type &ewill now be sent to your &6faction members only&e.");
		}
		return true;
	}
	
	public static boolean sendNonArg(Player player, Command command){
		new HoverMessage().messageTip("&c[Try This Instead]", command.getDescription()).send(player);
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Admin commands
	public static boolean handleAdminSetNPC(Player player){
		Controller.setNewNPC(player);
		return true;
	}
}
