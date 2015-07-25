package me.recolance.factions.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.recolance.factions.Factions;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Serialization;
import me.recolance.factions.faction.ClaimedLand;
import me.recolance.factions.faction.CompletedChallenge;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.faction.FactionChallenge;
import me.recolance.factions.faction.FactionSound;
import me.recolance.factions.faction.FactionStatus;
import me.recolance.factions.faction.Member;
import me.recolance.factions.faction.Rank;
import me.recolance.factions.faction.RankPermission;
import me.recolance.factions.faction.StatType;
import me.recolance.factions.faction.Vault;
import me.recolance.factions.faction.VaultTab;
import me.recolance.factions.faction.Warp;
import me.recolance.factions.menu.IconSelectionContainer.IconPage;
import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;
import me.recolance.globalutil.utils.MenuButton;
import me.recolance.globalutil.utils.PlayerUtil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuRenderer{
	
	/*
	 * No faction menu, when typing /faction without being in a faction.
	 */
	public static void noFactionMain(Player player){
		Inventory inventory = Bukkit.createInventory(null, 18, "Factions");
		ItemStack create = new MenuButton().type(Material.GRASS).name("&6Create A Faction")
			.lore("&aClick here to create a faction.", "", "&aYou will be able to create your very",
				  "&aown faction, configure it, add your", "&afriends, and begin raiding!").get();
		ItemStack join = new MenuButton().type(Material.EYE_OF_ENDER).name("&6Join A Faction")
			.lore("&aAll factions that are not open are", "&aalways invite-only, so you will need", "&aan invitation to join.",
				  "", "&aTalk to some factions and see if", "&ayou can get an invitation.",
				  "", "&aYou can also notify a faction that", "&ayou are interested in joining by",
				  "&ausing &b/f notifyjoin <faction-name>&a.").get();
		ItemStack open = new MenuButton().type(Material.IRON_BLOCK).name("&6Open Factions")
			.lore("&aClick here to view many of the open", "&afactions you can join.", "",
				  "&aOpen factions do not require you to", "&aneed an invitation to join. You will be", "&aable to join any of these factions",
				  "&aand start playing right away!", "", "&9Total Open Factions: &e" + DataHolder.getOpenFactions().size()).get();
		ItemStack whatAre = new MenuButton().type(Material.BOOK).name("&6What Are Factions?")
			.lore("&aFactions are like clans, a small group", "&aof people who want to control land,", "&akill others, and raid the loot of other",
				  "&afaction bases and players.", "", "&aWhile in a faction you can claim land,",
				  "&ainteract with other factions to become", "&aeither their enemy or ally, and try to", "&awork as a team to raid their goodies!",
				  "", "&aThere are many possibilities when in", "&aa faction so look around, find one",
				  "&athat has skilled and fun members, and", "&ahappy raiding!").get();
		inventory.setItem(1, create); inventory.setItem(4, join); inventory.setItem(7, open); inventory.setItem(13, whatAre);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.NO_FACTION_MAIN);
	}
	
	/*
	 * Faction creation step name, when creating your faction to set name.
	 */
	public static void creationName(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Faction Name")
			.lore("&aType in your desired faction name", "&aand then click the nametag.", "",
				  "&aNames must be 2 - 14 characters", "&ain length, can contain color codes", "&aand must be appropriate.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.CREATION_NAME);
	}
	
	/*
	 * Faction creation step icon, when creating your faction to set icon.
	 */
	public static void creationIcon(Player player){
		Inventory inventory = Bukkit.createInventory(null, 54, "Select A Faction Icon");
		HashMap<ItemStack, Integer> additionalButtons = new HashMap<ItemStack, Integer>();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Are Faction Icons?")
			.lore("&aFaction icons give factions a way to", "&ashow off some flair in various menus", "&aand leaderboards.",
				  "", "&aYou can choose between hundreds", "&aof icons for your faction, and are",
				  "&aable to change it whenever.", "", "&aPick an icon you think best fits your",
				  "&afaction, and you'll see how it works!").get();
		additionalButtons.put(whatIs, 49);
		IconSelectionContainer.createPage(player, inventory, IconPage.PAGE_1, additionalButtons, "&6Select This Faction Icon", "&aClick here to select this icon to be", "&ayour faction's icon.");
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.CREATION_ICON);
	}
	
	/*
	 * Faction sound step icon, when creating your faction to set sound.
	 */
	public static void creationSound(Player player){
		Inventory inventory = Bukkit.createInventory(null, 18, "Select A Faction Sound");
		for(FactionSound sound : FactionSound.values()){
			if(sound.getLevelRequirement() != 1) continue;
			ItemStack soundButton = new MenuButton().type(sound.getMaterial()).name("&6Select This Faction Sound")
				.lore("&aLeft-Click here to select this sound", "&ato be your faction's sound.", "",
					  "&aRight-Click here to listen to this", "&asound, as long as your volume is up.", "",
					  "&9Sound: &e" + sound.getName()).get();
			inventory.setItem(sound.getMenuSlot(), soundButton);
		}
		ItemStack whatAre = new MenuButton().type(Material.BOOK).name("&6What Are Faction Sounds?")
				.lore("&aFaction sounds are a sound that is", "&aplayed to a player that is killed by", "&aa member of your faction, and plays", "&awhen someone enters your land.",
					  "", "&aThere are many more sounds than", "&athis to choose from, although they",
					  "&arequire your faction to reach a", "&ahigher level. The sounds unlocked", "&aat higher levels are much more",
					  "&ataunting!", "", "&aIf you still don't understand what",
					  "&afaction sounds are, you will find", "&aout once your faction is made.", "",
					  "&aReminder: You need sounds turned", "&aon to hear faction sounds.").get();
		inventory.setItem(13, whatAre);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.CREATION_SOUND);
	}
	
	/*
	 * Disband faction confirmation, confirmation to disband a faction.
	 */
	public static void disbandConfirmation(Player player, Faction faction){
		Inventory inventory = Bukkit.createInventory(null, 9, "Disband: Are You Sure?");
		for(int i = 0; i < 4; i++){
			ItemStack yes = new MenuButton().type(Material.STAINED_GLASS_PANE).durability(5).name("&aConfirm").get();
			inventory.setItem(i, yes);
		}
		for(int i = 5; i < 9; i++){
			ItemStack no = new MenuButton().type(Material.STAINED_GLASS_PANE).durability(14).name("&cCancel").get();
			inventory.setItem(i, no);
		}
		ItemStack middle = new MenuButton().type(Material.TNT).name("&6Disband Faction")
			.lore("&aWhen you disband a faction, the faction", "&ais permanently deleted, its members are", "&aremoved, and the contents of the vault",
				  "&aare given to you.", "", "&aThis cannot be undone, are you sure",
				  "&ayou want to disband the faction?", "", "&9Faction: &e" + faction.getName()).get();
		inventory.setItem(4, middle);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.DISBAND_CONFIRMATION);
	}
	
	/*
	 * Open factions menu, open factions anyone can join.
	 */
	public static void openFactions(Player player){
		Inventory inventory = Bukkit.createInventory(null, 54, "Open Factions");
		if(DataHolder.getOpenFactions().isEmpty()){
			ItemStack noneOpen = new MenuButton().type(Material.WEB).name("&6No Open Factions")
				.lore("&aIt seems that there are no factions", "&acurrently open.", "",
					  "&aCome back later, try to join another,", "&aor create your own!").get();
			inventory.setItem(4, noneOpen);
		}else{
			int i = 0;
			for(Faction faction : DataHolder.getOpenFactions()){
				if(i > 44) break;
				if(faction.getMembers().size() > 44) continue;
				ItemStack factionIcon = new MenuButton().type(faction.getIcon().getType()).durability(faction.getIcon().getDurability()).name("&r" + faction.getName())
					.lore("&aClick here to join this faction.", "", "&9Level: &e" + faction.getLevel() + "/25",
						  "&9Score: &e" + faction.getScore(), "&9Power: &e" + faction.getPower() + "/1000", "&9Members: &e" + faction.getMembers().size() + "/45").get();
				inventory.setItem(i, factionIcon);
				i++;
			}
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack whatAre = new MenuButton().type(Material.BOOK).name("&6What Are Open Factions?")
				.lore("&aOpen factions are factions that are", "&aavailable for anyone to join. These", "&afactions do not require an invite.",
					  "", "&aMost likely these factions are open", "&abecause they are looking to recruit",
					  "&amore members.").get();
		ItemStack create = new MenuButton().type(Material.GRASS).name("&6Create A Faction")
				.lore("&aClick here to create a faction.", "", "&aYou will be able to create your very",
					  "&aown faction, configure it, add your", "&afriends, and begin raiding!").get();
		inventory.setItem(45, back); inventory.setItem(49, create); inventory.setItem(53, whatAre);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.OPEN_FACTIONS);
	}
	
	/*
	 * Faction main menu, opens when in a faction and type /f
	 */
	public static void factionMain(Player player){
		Faction faction = FactionUtil.getPlayerFaction(player);
		Inventory inventory = Bukkit.createInventory(null, 36, faction.getName());
		Member member = FactionUtil.getPlayerMember(player);
		Rank rank = member.getRank();
		MenuButton mainButton = new MenuButton().type(faction.getIcon().getType()).durability(faction.getIcon().getDurability()).name("&r" + faction.getName())
			.lore("&aClick here to view more stats about", "&athis faction.", "",
				  "&9Level: &e" + faction.getLevel() + "/25");
		if(faction.getLevel() >= 25){
			mainButton.lore("&9Exp: &e-/-");
		}else{
			mainButton.lore("&9Exp: &e" + faction.getExp() + "/" + faction.getRequiredExp());
		}
		mainButton.lore("&9Score: &e" + faction.getScore(), "&9Power: &e" + faction.getPower() + "/1000", "&9Members: &e" + faction.getMembers().size() + "/45");
		ItemStack main = mainButton.get();
		MenuButton homeButton = new MenuButton().type(Material.GRASS).name("&6Faction Home")
			.lore("&aClick here to teleport to your faction's", "&ahome location.");
		if(!FactionUtil.hasPermission(player, RankPermission.USE_HOME)) homeButton.lore("", "&cYou don't have permission.");
		ItemStack home = homeButton.get();
		ItemStack members = new MenuButton().type(Material.SKULL_ITEM).durability(3).name("&6Members")
			.lore("&aClick here to view all of the faction's", "&amembers and their information.", "",
				  "&9Members: &e" + faction.getMembers().size() + "/45", "&9Online: &e" + faction.getOnlineMembers().size() + "/" + faction.getMembers().size()).get();
		ItemStack ranks = new MenuButton().type(Material.NAME_TAG).name("&6Ranks")
			.lore("&aClick here to view all of the faction's", "&amember ranks and their permissions.", "",
				  "&aWith proper permission you can also", "&acreate, delete, and edit ranks here.", "",
				  "&9Your Rank: &e" + FactionUtil.getPlayerMember(player).getRank().getName()).get();
		ItemStack relations = new MenuButton().type(Material.BANNER).durability(10).name("&6Relations")
			.lore("&aClick here to view all of the faction's", "&aEnemies and Alliances.", "",
			      "&aWith proper permission you can also", "&arequest and remove relations here.", "",
			      "&9Alliances: &e" + faction.getAlliances().size() + "/9", "&9Enemies: &e" + faction.getEnemies().size() + "/9", "&9Enemied By: &e" + faction.getEnemiedBy().size()).get();
		ItemStack vault = new MenuButton().type(Material.ENDER_CHEST).name("&6Vault")
			.lore("&aClick here to view the contents of the", "&afaction vault.", "",
				  "&aWith proper permission you can also", "&adeposit and withdrawl coins and items", "&afrom the faction vault.",
				  "", "&9Items Removed Today: &e" + member.getVaultItemsTaken() + "/" + rank.getVaultItemsLimit(), "&9Coins Removed Today: &e" + member.getVaultMoneyTaken() + "/" + rank.getVaultMoneyLimit()).get();
		ItemStack warps = new MenuButton().type(Material.ENDER_PORTAL_FRAME).name("&6Warps")
			.lore("&aClick here to view all of the faction's", "&awarps.", "",
				  "&aWith proper permission you can also", "&ateleport to, remove, and set warps", "&afor faction members only.",
				  "", "&9Warps Set: &e" + faction.getWarps().size() + "/" + faction.getMaxWarps()).get();
		ItemStack challenges = new MenuButton().type(Material.DIAMOND).name("&6Challenges")
			.lore("&aClick here to view all of the faction", "&achallenges and their progress.", "",
				  "&9Progress: &e" + faction.getCompletedChallenges().size() + "/28").get();
		ItemStack claimedLand = new MenuButton().type(Material.BEACON).name("&6Claimed Land")
			.lore("&aClick here to view all of the faction's", "&aclaimed land.", "",
			      "&aWith proper permission you can also", "&aunclaim, claim and view the locations", "&aof claimed land.",
			      "", "&9Land Claimed: &e" + faction.getClaimedLand().size() + "/" + faction.getMaxClaimedLand()).get();
		ItemStack settings = new MenuButton().type(Material.REDSTONE).name("&6Settings")
				.lore("&aClick here to view all of the faction's", "&aoptions and settings.", "",
				      "&aWith proper permission you can also", "&amodify these settings.").get();
		ItemStack rules = new MenuButton().type(Material.PAPER).name("&6Faction Rules")
				.lore(faction.getRules()).get();
		ItemStack activity = new MenuButton().type(Material.BOOK_AND_QUILL).name("&6Faction Activity")
				.lore(faction.getActivityLog()).get();
		ItemStack help = new MenuButton().type(Material.BOOK).name("&6Need Some Help?")
				.lore("&aNeed some help with a topic? Or do", "&ayou need to see all of the faction", "&acommands? Click here.").get();
		inventory.setItem(4, main); inventory.setItem(9, home); inventory.setItem(10, members); inventory.setItem(11, ranks);
		inventory.setItem(12, relations); inventory.setItem(13, vault); inventory.setItem(14, warps); inventory.setItem(15, challenges);
		inventory.setItem(16, claimedLand); inventory.setItem(17, settings); inventory.setItem(20, rules); inventory.setItem(24, activity);
		inventory.setItem(31, help);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.FACTION_MAIN);
	}
	
	/*
	 * Faction stats menu, shows a faction's stats
	 */
	public static void stats(Player player, Faction faction){
		Inventory inventory = Bukkit.createInventory(null, 27, Util.setStringColors(faction.getName() + "&r Stats"));
		ItemStack blocksPlaced = new MenuButton().type(Material.GRASS).name("&6Blocks Placed")
			.lore("&aEarned by placing any block.", "", "&9Blocks Placed: &e" + Util.comma(faction.getStat(StatType.BLOCKS_PLACED))).get();
		ItemStack blocksBroken = new MenuButton().type(Material.COBBLESTONE).name("&6Blocks Broken")
			.lore("&aEarned by breaking any block.", "", "&9Blocks Broken: &e" + Util.comma(faction.getStat(StatType.BLOCKS_BROKEN))).get();
		ItemStack oresBroken = new MenuButton().type(Material.DIAMOND_ORE).name("&6Ores Broken")
			.lore("&aEarned by breaking any ore.", "", "&9Ores Broken: &e" + Util.comma(faction.getStat(StatType.ORES_BROKEN))).get();
		ItemStack animalsKilled = new MenuButton().type(Material.LEATHER).name("&6Animals Killed")
			.lore("&aEarned by killing any animal.", "", "&9Animals Killed: &e" + Util.comma(faction.getStat(StatType.ANIMALS_KILLED))).get();
		ItemStack monsterKilled = new MenuButton().type(Material.BONE).name("&6Monsters Killed")
			.lore("&aEarned by killing any monster", "", "&9Monsters Killed: &e" + Util.comma(faction.getStat(StatType.MONSTERS_KILLED))).get();
		ItemStack playersKilled = new MenuButton().type(Material.DIAMOND_SWORD).name("&6Players Killed")
			.lore("&aEarned by killing any player", "", "&9Players Killed: &e" + Util.comma(faction.getStat(StatType.PLAYERS_KILLED))).get();
		ItemStack nonPlayerDeaths = new MenuButton().type(Material.SKULL_ITEM).durability(2).name("&6Non Player Caused Deaths")
			.lore("&aEarned by dying from anything other", "&athan players.", "",
				  "&9Deaths: &e" + Util.comma(faction.getStat(StatType.DEATHS_NON_PLAYER))).get();
		ItemStack playerDeaths = new MenuButton().type(Material.SKULL_ITEM).name("&6Player Deaths")
			.lore("&aEarned by dying from a player.", "", "&9Deaths: &e" + Util.comma(faction.getStat(StatType.DEATHS_PLAYER))).get();
		ItemStack kdr = new MenuButton().type(Material.GOLDEN_APPLE).name("&6Kill Death Ratio")
			.lore("&aEarned by dividing your faction's", "&aplayer kills by total deaths.", "",
				  "&9KDR: &e" + Util.comma(faction.getKDR())).get();
		ItemStack potions = new MenuButton().type(Material.POTION).durability(8267).name("&6Potions Brewed")
			.lore("&aEarned by brewing potions.", "", "&9Potions: &e" + Util.comma(faction.getStat(StatType.POTIONS_BREWED))).get();
		ItemStack itemsFished = new MenuButton().type(Material.FISHING_ROD).name("&6Items Fished")
			.lore("&aEarned by fishing up items.", "", "&9Items Fished: &e" + Util.comma(faction.getStat(StatType.ITEMS_FISHED))).get();
		ItemStack claimedLand = new MenuButton().type(Material.BEACON).name("&6Total Land Claimed")
			.lore("&aEarned by claiming land.", "", "&9Land Claimed: &e" + Util.comma(faction.getStat(StatType.LAND_CLAIMED))).get();
		ItemStack overclaimedLand = new MenuButton().type(Material.NETHER_STAR).name("&6Total Land Overclaimed")
			.lore("&aEarned by overclaiming land.", "", "&9Land Overclaimed: &e" + Util.comma(faction.getStat(StatType.LAND_OVERCLAIMED))).get();
		ItemStack dateCreated = new MenuButton().type(Material.WATCH).name("&6Date Created")
			.lore("&aWhen the faction was created.", "", "&9Date: &e" + Util.getMonthDayYearDate(faction.getDateCreated())).get();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the faction stats page, you can", "&aview the stats this faction has collected", "&awhile being active here. You can also",
				  "&aview the stats of any faction with the", "&acommand &b/f stats <faction>&a.", "",
				  "&aThis page is also a good way to judge", "&athe faction's skill in PvP combat and its", "&adefenses.",
				  "", "&aTake a look at all of the stats and see", "&ahow you match up to other factions.").get();
		inventory.setItem(0, blocksPlaced); inventory.setItem(1, blocksBroken); inventory.setItem(2, oresBroken);
		inventory.setItem(3, animalsKilled); inventory.setItem(4, monsterKilled); inventory.setItem(5, playersKilled);
		inventory.setItem(6, nonPlayerDeaths); inventory.setItem(7, playerDeaths); inventory.setItem(8, kdr);
		inventory.setItem(11, potions); inventory.setItem(12, itemsFished); inventory.setItem(13, claimedLand);
		inventory.setItem(14, overclaimedLand); inventory.setItem(15, dateCreated); inventory.setItem(22, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.STATS);
	}
	
	/*
	 * Members menu, the faction members.
	 */
	public static void members(Player player){
		Faction faction = FactionUtil.getPlayerFaction(player);
		Inventory inventory = Bukkit.createInventory(null, 54, "Members");
		renderMembers(player, faction, inventory);
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		MenuButton inviteButton = new MenuButton().type(Material.SKULL_ITEM).durability(3).name("&6Invite A New Member")
			.lore("&aClick here to invite a new member to", "&ayour faction.");
		if(!FactionUtil.hasPermission(player, RankPermission.INVITE_MEMBERS)) inviteButton.lore("", "&cYou do not have permission.");
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the faction member page, here you", "&acan view all of the members in your faction,", "&aand see a bit of information about each one.",
				  "", "&aWith proper permission by your faction rank,", "&ayou can also kick and invite new members to", "&athe faction.",
				  "", "&aTake a look at all of the members here and", "&alearn more about managing faction members.").get();
		inventory.setItem(45, back); inventory.setItem(49, inviteButton.get()); inventory.setItem(53, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.MEMBERS);
	}
	/*
	 * Members menu renderer, used to refresh upon kicks as well.
	 */
	public static void renderMembers(Player player, Faction faction, Inventory inventory){
		ArrayList<Member> membersViewing = new ArrayList<Member>();
		for(int i = 0; i < 45; i++){
			inventory.setItem(i, null);
		}
		int slot = 0;
		for(Member member : faction.getOnlineMembers()){
			MenuButton memberButton = new MenuButton().type(Material.SKULL_ITEM).durability(3).skull(member.getPlayer()).name("&6" + Bukkit.getOfflinePlayer(member.getPlayer()).getName());
			if(FactionUtil.hasPermission(player, RankPermission.KICK_MEMBERS) && (!member.getRank().isLeader() && FactionUtil.getPlayerMember(player) != member)) memberButton.lore("&aShift + Right-Click here to kick this", "&amember from the faction.", "");
			memberButton.lore("&9Rank: &e" + member.getRank().getName(), "&9Power Lost Today: &e" + member.getPowerLost(), "&9Power Gained Today: &e" + member.getPowerGained(),
							  "&9Online Since: &e" + Util.timeToDHMSString((System.currentTimeMillis() - PlayerUtil.getTimeLoggedOn(Bukkit.getPlayer(member.getPlayer())))), "&9Date Joined: &e" + Util.getMonthDayYearDate(member.getDateJoined()));
			inventory.setItem(slot, memberButton.get());
			membersViewing.add(member);
			slot++;
		}
		for(Member member : faction.getOfflineMembers()){
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member.getPlayer());
			MenuButton memberButton = new MenuButton().type(Material.SKULL_ITEM).name("&6" + offlinePlayer.getName());
			if(FactionUtil.hasPermission(player, RankPermission.KICK_MEMBERS) && (!member.getRank().isLeader() && FactionUtil.getPlayerMember(player) != member)) memberButton.lore("&aShift + Right-Click here to kick this", "&amember from the faction.", "");
			memberButton.lore("&9Rank: &e" + member.getRank().getName(), "&9Power Lost Today: &e" + member.getPowerLost(), "&9Power Gained Today: &e" + member.getPowerGained(),
							  "&9Last Online: &e" + Util.timeToLastOnlineString(offlinePlayer.getLastPlayed()), "&9Date Joined: &e" + Util.getMonthDayYearDate(member.getDateJoined()));
			inventory.setItem(slot, memberButton.get());
			membersViewing.add(member);
			slot++;
		}
		MenuHelper.setMembersViewing(player, membersViewing);
	}
	
	/*
	 * Invite member text input.
	 */
	public static void inviteMember(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Type Player Name")
			.lore("&aType in the name of the player", "&ayou want to invite, and then click", "&athe nametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.MEMBERS_INVITE);
	}
	
	/*
	 * Ranks main menu, when a user clicks on the ranks button.
	 */
	public static void ranks(Player player){
		Inventory inventory = Bukkit.createInventory(null, 18, "Faction Ranks");
		int slot = 0;
		for(Rank rank : FactionUtil.getPlayerFaction(player).getRanks()){
			if(rank.isLeader()) continue;
			if(slot == 9) break;
			ItemStack rankButton = new MenuButton().type(Material.NAME_TAG).name("&6" + rank.getName())
				.lore("&aClick here to view, edit, or delete this", "&afaction rank.", "",
					  "&9Default: &e" + (rank.isDefault() ? "Yes" : "No")).get();
			inventory.setItem(slot, rankButton);
			slot++;
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		MenuButton createRank = new MenuButton().type(Material.NAME_TAG).name("&6Create A New Rank")
			.lore("&aClick here to create a new rank.");
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)) createRank.lore("", "&cYou do not have permission.");
		MenuButton setRank = new MenuButton().type(Material.SKULL_ITEM).durability(3).name("&6Set A Member's Rank")
			.lore("&aTo set a member's rank, use the command", "&b/f setrank <player> <rank>&a.", "",
				  "&aThis will change the member's faction rank", "&ato the rank you want.");
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_MEMBER_RANK)) setRank.lore("", "&cYou do not have permission.");
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
				.lore("&aThis is the faction rank management main-page,", "&ahere with proper permission by your faction", "&arank, you can create new ranks, select ranks",
					  "&ato view or edit permissions for, and learn to", "&aset a member's faction rank.", "",
					  "&aExplore this page and learn the possibilities", "&aabout creating / managing faction ranks, their", "&apermissions, and promoting / demoting members",
					  "&aof your faction.").get();
		inventory.setItem(9, back); inventory.setItem(12, createRank.get()); inventory.setItem(14, setRank.get());
		inventory.setItem(17, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS);
	}
	
	/*
	 * Edit rank menu, editing a rank
	 */
	public static void editRank(Player player, Rank rank){
		Inventory inventory = Bukkit.createInventory(null, 45, "Editing " + rank.getName());
		ArrayList<MenuButton> permButtons = new ArrayList<MenuButton>();
		MenuButton build = new MenuButton().type(Material.COBBLESTONE); permButtons.add(build);
		MenuButton container = new MenuButton().type(Material.CHEST); permButtons.add(container);
		MenuButton mechanic = new MenuButton().type(Material.LEVER); permButtons.add(mechanic);
		MenuButton name = new MenuButton().type(Material.RECORD_3); permButtons.add(name);
		MenuButton desc = new MenuButton().type(Material.EMPTY_MAP); permButtons.add(desc);
		MenuButton icon = new MenuButton().type(Material.ITEM_FRAME); permButtons.add(icon);
		MenuButton status = new MenuButton().type(Material.IRON_BLOCK); permButtons.add(status);
		MenuButton rules = new MenuButton().type(Material.PAPER); permButtons.add(rules);
		MenuButton invite = new MenuButton().type(Material.SKULL_ITEM).durability(3); permButtons.add(invite);
		MenuButton kick = new MenuButton().type(Material.SKULL_ITEM); permButtons.add(kick);
		MenuButton homeUse = new MenuButton().type(Material.GRASS); permButtons.add(homeUse);
		MenuButton homeSet = new MenuButton().type(Material.GRASS); permButtons.add(homeSet);
		MenuButton viewLoc = new MenuButton().type(Material.BEACON); permButtons.add(viewLoc);
		MenuButton claim = new MenuButton().type(Material.BEACON); permButtons.add(claim);
		MenuButton unclaim = new MenuButton().type(Material.BEACON); permButtons.add(unclaim);
		MenuButton relations = new MenuButton().type(Material.BANNER).durability(1); permButtons.add(relations);
		MenuButton rankEdit = new MenuButton().type(Material.NAME_TAG); permButtons.add(rankEdit);
		MenuButton rankMember = new MenuButton().type(Material.NAME_TAG); permButtons.add(rankMember);
		MenuButton warpUse = new MenuButton().type(Material.ENDER_PORTAL_FRAME); permButtons.add(warpUse);
		MenuButton warpSet = new MenuButton().type(Material.ENDER_PORTAL_FRAME); permButtons.add(warpSet);
		MenuButton warpDel = new MenuButton().type(Material.ENDER_PORTAL_FRAME); permButtons.add(warpDel);
		MenuButton vaultDepo = new MenuButton().type(Material.ENDER_CHEST); permButtons.add(vaultDepo);
		MenuButton vaultEdit = new MenuButton().type(Material.ENDER_CHEST); permButtons.add(vaultEdit);
		MenuButton vaultMoney = new MenuButton().type(Material.ENDER_CHEST); permButtons.add(vaultMoney);
		MenuButton vaultItems = new MenuButton().type(Material.ENDER_CHEST); permButtons.add(vaultItems);
		
		for(MenuButton button : permButtons){
			button.lore("&aClick here to edit this permission for", "&athe " + rank.getName() + " rank.", "", "&aThis permission controls access to");
		}
		build.name("&6Build In Faction Land").lore("&abuild in faction claimed land.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.BUILD));
		container.name("&6Use Containers In Faction Land").lore("&aopen chests, furnaces, and other", "&acontainers in faction claimed land.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.USE_CONTAINERS));
		mechanic.name("&6Use Mechanics In Faction Land").lore("&ause doors, buttons, levers, fences,", "&aand other mechanical utilities while", "&ain faction claimed land.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.USE_MECHANICS));
		name.name("&6Change Faction Sound").lore("&achange the faction's sound.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.SET_SOUND));
		desc.name("&6Change Faction Description").lore("&achange the faction's description.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.SET_DESCRIPTION));
		icon.name("&6Change Faction Icon").lore("&achange the faction's icon.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.SET_ICON));
		status.name("&6Change Faction Status").lore("&achange the faction's status to either", "&aopen or closed.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.SET_STATUS));
		rules.name("&6Edit Faction Rules").lore("&aedit the faction's rules.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.EDIT_RULES));
		invite.name("&6Invite Players To Faction").lore("&ainvite new players to the faction.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.INVITE_MEMBERS));
		kick.name("&6Kick Faction Members").lore("&akick members from the faction.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.KICK_MEMBERS));
		homeUse.name("&6Teleport To Faction Home").lore("&ateleport to the faction home.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.USE_HOME));
		homeSet.name("&6Change Faction Home Location").lore("&aset the faction's home location.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.SET_HOME));
		viewLoc.name("&6View Claimed Land Locations").lore("&aview the locations of all faction", "&aclaimed land in the claimed land.", "&amenu.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.VIEW_LAND_LOCATIONS));
		claim.name("&6Claim New Faction Land").lore("&aclaim new faction land.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.CLAIM_LAND));
		unclaim.name("&6Unclaim Faction Land").lore("&aunclaim faction land.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.UNCLAIM_LAND));
		relations.name("&6Edit Faction Relations").lore("&aedit, add, and remove the faction's", "&arelations with other factions.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.EDIT_RELATIONS));
		rankEdit.name("&6Edit Faction Ranks").lore("&aadd, delete, and edit faction's ranks", "&aand their permissions.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.EDIT_RANKS));
		rankMember.name("&6Set Member's Rank").lore("&apromote, demote, and set members to", "&aanother faction rank.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.EDIT_MEMBER_RANK));
		warpUse.name("&6Teleport To Faction Warps").lore("&ateleport to faction warps.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.USE_WARPS));
		warpSet.name("&6Set Faction Warps").lore("&aset faction warps.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.SET_WARPS));
		warpDel.name("&6Delete Faction Warps").lore("&adelete faction warps.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.DELETE_WARPS));
		vaultDepo.name("&6Deposit Into Faction Vault").lore("&adeposit coins and items into the", "&afaction vault.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.VAULT_DEPOSIT));
		vaultEdit.name("&6Edit Faction Vault Tabs").lore("&aedit faction vault tab info.", "", "&9Permission: &e" + MenuUtil.getPermissionStatusString(rank, RankPermission.EDIT_VAULT));
		vaultMoney.name("&6Withdrawl Coins From Faction Vault").lore("&ahow many coins a member of this", "&arank can withdrawl from the vault", "&aevery day.", "", "&9Amount Per Day: &e" + rank.getVaultMoneyLimit());
		vaultItems.name("&6Withdrawl Items From Faction Vault").lore("&ahow many items a member of this", "&arank can withdrawl from the vault", "&aevery day.", "", "&9Amount Per Day: &e" + rank.getVaultItemsLimit());
		MenuButton threshold = new MenuButton().type(Material.EYE_OF_ENDER).name("&6Power Threshold")
			.lore("&aClick here to edit this setting for", "&athe " + rank.getName() + " rank.", "", 
				  "&aThe power threshold is the amount", "&aof total power loss a member of", "&athis rank can not exceed without", 
				  "&abeing auto kicked from the faction.", "", "&9Power Threshold: &e" + rank.getPowerThreshold()); permButtons.add(threshold);
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		MenuButton changeName = new MenuButton().type(Material.NAME_TAG).name("&6Change Rank Name")
				.lore("&aClick here to rename this faction", "&arank to something else.", "",
				  	   "&9Current Name: &e" + rank.getName()); permButtons.add(changeName);
		MenuButton defaultRank = new MenuButton().type(Material.NETHER_STAR).name("&6Set As Default Rank")
				.lore("&aClick here to set this rank as the", "&afaction's default rank.", "",
					  "&aThe default rank is the rank that", "&aa new member is automatically set", "&ato after joining.", "",
					  "&9Currently Default: &e" + (rank.isDefault() ? "Yes" : "No")); permButtons.add(defaultRank);
		MenuButton removeRank = new MenuButton().type(Material.TNT).name("&6Remove Rank")
			.lore("&aClick here to delete this faction", "&arank permanently."); permButtons.add(removeRank);
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the permissions management main-page", "&ahere with proper permission by your faction", "&arank, you can view / edit the permissions for,",
				  "&adelete, rename, and set this as the default", "&afaction rank.", "",
				  "&aExplore this page and learn more about rank", "&apermission management for this rank and the", "&aother ranks in your faction.").get();
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)){
			for(MenuButton button : permButtons){
				button.lore("", "&cYou do not have permission.");
			}
		}
		inventory.setItem(0, build.get()); inventory.setItem(1, container.get()); inventory.setItem(2, mechanic.get());
		inventory.setItem(3, name.get()); inventory.setItem(4, desc.get()); inventory.setItem(5, icon.get());
		inventory.setItem(6, status.get()); inventory.setItem(7, rules.get()); inventory.setItem(8, invite.get());
		inventory.setItem(9, kick.get()); inventory.setItem(10, homeUse.get()); inventory.setItem(11, homeSet.get());
		inventory.setItem(12, viewLoc.get()); inventory.setItem(13, claim.get()); inventory.setItem(14, unclaim.get());
		inventory.setItem(15, relations.get()); inventory.setItem(16, rankEdit.get()); inventory.setItem(17, rankMember.get());
		inventory.setItem(19, warpUse.get()); inventory.setItem(20, warpSet.get()); inventory.setItem(21, warpDel.get());
		inventory.setItem(22, vaultDepo.get()); inventory.setItem(23, vaultEdit.get()); inventory.setItem(24, vaultMoney.get());
		inventory.setItem(25, vaultItems.get()); inventory.setItem(31, threshold.get()); inventory.setItem(36, back);
		inventory.setItem(39, changeName.get()); inventory.setItem(40, defaultRank.get()); inventory.setItem(41, removeRank.get());
		inventory.setItem(44, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS_EDITING);
		MenuHelper.setEditingRank(player, rank);
	}
	
	/*
	 * Rank withdrawl coins, editing how many coins per day someone can withdrawl
	 */
	public static void withdrawlCoins(Player player, Rank rank){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Amount Of Coins")
			.lore("&aType the amount of coins you want", "&ato allow this rank to remove from", "&athe vault per day, then click the", "&anametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS_EDITING_COINS);
		MenuHelper.setEditingRank(player, rank);
	}
	
	/*
	 * Rank withdrawl items, editing how many items per day someone can withdrawl
	 */
	public static void withdrawlItems(Player player, Rank rank){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Amount Of Items")
			.lore("&aType the amount of items you want", "&ato allow this rank to remove from", "&athe vault per day, then click the", "&anametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS_EDITING_ITEMS);
		MenuHelper.setEditingRank(player, rank);
	}
	
	/*
	 * Power threshold, editing how the rank's power threshold
	 */
	public static void powerThreshold(Player player, Rank rank){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Amount Of Power")
			.lore("&aType the amount of power loss that", "&aa member of this rank can not", "&aexceed without being kicked then", "&aclick the nametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS_EDITING_THRESHOLD);
		MenuHelper.setEditingRank(player, rank);
	}
	
	/*
	 * Change rank name, editing the rank's name.
	 */
	public static void changeRankName(Player player, Rank rank){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Rank Name")
			.lore("&aType in the name you want to", "&achange " + rank.getName() + " to, then click the", "&anametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS_EDITING_NAME);
		MenuHelper.setEditingRank(player, rank);
	}
	
	/*
	 * Remove rank confirmation, confirmation to remove rank.
	 */
	public static void removeRankConfirmation(Player player, Rank rank){
		Inventory inventory = Bukkit.createInventory(null, 9, "Remove Rank: Are You Sure?");
		for(int i = 0; i < 4; i++){
			ItemStack yes = new MenuButton().type(Material.STAINED_GLASS_PANE).durability(5).name("&aConfirm").get();
			inventory.setItem(i, yes);
		}
		for(int i = 5; i < 9; i++){
			ItemStack no = new MenuButton().type(Material.STAINED_GLASS_PANE).durability(14).name("&cCancel").get();
			inventory.setItem(i, no);
		}
		ItemStack middle = new MenuButton().type(Material.TNT).name("&6Remove " + rank.getName() + " Rank")
			.lore("&aWhen you remove a rank, the rank", "&ais permanently deleted, its members are", "&all set to the default rank.", "", 
				  "&aAre you sure you want to remove", "&athis rank?", "", "&9Rank: &e" + rank.getName()).get();
		inventory.setItem(4, middle);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS_REMOVE_CONFIRMATION);
		MenuHelper.setEditingRank(player, rank);
	}
	
	/*
	 * Create rank name input, when creating a rank
	 */
	public static void createRank(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Rank Name")
			.lore("&aType in the desired name of", "&ayour new faction rank, then", "&aclick the nametag.").get();
			inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.RANKS_CREATE);
	}
	
	/*
	 * Relation menu, main menu for all relations.
	 */
	public static void relations(Player player){
		Inventory inventory = Bukkit.createInventory(null, 18, "Alliances And Enemies");
		Faction faction = FactionUtil.getPlayerFaction(player);
		ItemStack alliances = new MenuButton().type(Material.BANNER).durability(10).name("&6Alliances")
			.lore("&aClick here to view and manage all of",
				  "&athe faction's alliances.",
				  "",
				  "&9Alliances: &e" + faction.getAlliances().size() + "/9").get();
		ItemStack enemies = new MenuButton().type(Material.BANNER).durability(1).name("&6Enemies")
			.lore("&aClick here to view and manage all of",
				  "&athe faction's enemies.",
				  "",
				  "&9Enemies: &e" + faction.getEnemies().size() + "/9").get();
		ItemStack enemiedBy = new MenuButton().type(Material.BANNER).durability(14).name("&6Enemied By")
				.lore("&aClick here to view all of the factions", "&athat enemy this faction.", "",
					  "&9Enemied By: &e" + faction.getEnemiedBy().size()).get();
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is a very simple page, click on a button to", "&aeither view the enemies or alliances of your", "&afaction. In those pages you can manage your",
				  "&arelations with them, view them, and be enemied", "&aor allied with more factions.","",
				  "&aBy clicking on a relation button and taking a", "&alook at the next page, you should be able to", "&afind your way around this section of relation",
				  "&amanagement very easily").get();
		inventory.setItem(1, alliances);
		inventory.setItem(4, enemies);
		inventory.setItem(7, enemiedBy);
		inventory.setItem(9, back);
		inventory.setItem(17, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.RELATION_MAIN);
	}
	
	/*
	 * Alliances, shows all faction allies.
	 */
	public static void alliances(Player player){
		Inventory inventory = Bukkit.createInventory(null, 18, "Alliances");
		renderAllies(player, inventory);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.ALLIANCES);
	}
	/*
	 * Render allies, for refreshing
	 */
	public static void renderAllies(Player player, Inventory inventory){
		for(int i = 0; i < 9; i++){
			inventory.setItem(i, null);
		}
		Faction faction = FactionUtil.getPlayerFaction(player);
		UUID[] allies = faction.getAlliances().toArray(new UUID[faction.getAlliances().size()]);
		int slot = 0;
		if(allies.length == 0){
			ItemStack alliesEmpty = new MenuButton().type(Material.WEB).name("&6No Alliances")
				.lore("&aIt seems that your faction is", "&anot allying anyone.").get();
			inventory.setItem(4, alliesEmpty);
		}else{
			for(UUID allyFactionId : allies){
				Faction allyFaction = FactionUtil.getFactionFromId(allyFactionId);
				MenuButton allyButton = new MenuButton().type(allyFaction.getIcon().getType()).durability(allyFaction.getIcon().getDurability()).name(allyFaction.getName())
					.lore("&aClick here to view more stats about", "&athis faction.", "");
				if(FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) allyButton.lore("&aShift + Right-Click here to set this", "&afaction as neutral.", "");
				ItemStack ally = allyButton.lore("&9Level: &e" + allyFaction.getLevel() + "/25", "&9Score: &e" + allyFaction.getScore(), "&9Power: &e" + allyFaction.getPower() + "/1000", 
						        "&9Members: &e" + allyFaction.getMembers().size() + "/45").get();
				inventory.setItem(slot, ally);
				slot++;
			}
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		MenuButton allyFactionSet = new MenuButton().type(Material.BANNER).durability(10).name("&6Request To Ally A Faction")
				.lore("&aClick here to request a faction to", "&abecome an allied faction.");
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) allyFactionSet.lore("", "&cYou do not have permission.");
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
				.lore("&aThis is the alliance relation page, here with", "&aproper permission by your faction rank,", "&ayou can view and edit your relationship",
					  "&awith these allied factions.", "", "&aExplore this page and learn more about",
					  "&amanaging alliance relationships with your", "&afaction.").get();
		inventory.setItem(9, back);
		inventory.setItem(13, allyFactionSet.get());
		inventory.setItem(17, whatIs);
	}
	
	/*
	 * Alliances add, menu input to add alliances
	 */
	public static void alliancesAdd(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Faction Name")
			.lore("&aType the name of the faction that", "&ayou want to alliance with.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.ALLIANCES_ADD);
	}
	
	/*
	 * Enemies, shows all faction enemies.
	 */
	public static void enemies(Player player){
		Inventory inventory = Bukkit.createInventory(null, 18, "Enemies");
		renderEnemies(player, inventory);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.ENEMIES);
	}
	/*
	 * Render enemies, for refreshing.
	 */
	public static void renderEnemies(Player player, Inventory inventory){
		for(int i = 0; i < 9; i++){
			inventory.setItem(i, null);
		}
		Faction faction = FactionUtil.getPlayerFaction(player);
		UUID[] enemies = faction.getEnemies().toArray(new UUID[faction.getEnemies().size()]);
		int slot = 0;
		if(enemies.length == 0){
			ItemStack enemiesEmpty = new MenuButton().type(Material.WEB).name("&6No Enemies")
				.lore("&aIt seems that your faction is", "&anot enemying anyone.").get();
			inventory.setItem(4, enemiesEmpty);
		}else{
			for(UUID enemyFactionId : enemies){
				Faction enemyFaction = FactionUtil.getFactionFromId(enemyFactionId);
				MenuButton enemyButton = new MenuButton().type(enemyFaction.getIcon().getType()).durability(enemyFaction.getIcon().getDurability()).name(enemyFaction.getName())
					.lore("&aClick here to view more stats about", "&athis faction.", "");
				if(FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) enemyButton.lore("&aShift + Right-Click here to set this", "&afaction as neutral.", "");
				ItemStack enemy = enemyButton.lore("&9Level: &e" + enemyFaction.getLevel() + "/25", "&9Score: &e" + enemyFaction.getScore(), "&9Power: &e" + enemyFaction.getPower() + "/1000", 
						"&9Members: &e" + enemyFaction.getMembers().size() + "/45").get();
				inventory.setItem(slot, enemy);
				slot++;
			}
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		MenuButton enemyFactionSet = new MenuButton().type(Material.BANNER).durability(1).name("&6Set An Enemy Faction")
				.lore("&aClick here to set a faction as an", "&aenemy faction.");
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)) enemyFactionSet.lore("", "&cYou do not have permission.");
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
				.lore("&aThis is the enemy relation page, here with", "&aproper permission by your faction rank,", "&ayou can view and edit your relationship",
					  "&awith these enemied factions.", "", "&aAs some additive information about faction",
					  "&aenemies, they are not mutual. Unlike when", "&ayou ally factions, when factions enemy", "&aanother, that does not mean that faction",
					  "&aenemies you back.","","&aExplore this page and learn more about",
					  "&amanaging enemy relationships with your", "&afaction.").get();
		inventory.setItem(9, back);
		inventory.setItem(13, enemyFactionSet.get());
		inventory.setItem(17, whatIs);
	}
	
	/*
	 * Alliances add, menu input to add alliances
	 */
	public static void enemiesAdd(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Faction Name")
			.lore("&aType the name of the faction that", "&ayou want to enemy.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.ENEMIES_ADD);
	}
	
	/*
	 * Enemies by, the factions that enemy this faction
	 */
	public static void enemiedBy(Player player){
		Inventory inventory = Bukkit.createInventory(null, 54, "Enemied By");
		Faction faction = FactionUtil.getPlayerFaction(player);
		UUID[] enemiedByFactions = faction.getEnemiedBy().toArray(new UUID[faction.getEnemiedBy().size()]);
		int slot = 0;
		if(enemiedByFactions.length == 0){
			ItemStack enemiedByEmpty = new MenuButton().type(Material.WEB).name("&6Enemied By Nobody")
				.lore("&aIt seems that no faction is", "&aenemying this faction.").get();
			inventory.setItem(4, enemiedByEmpty);
		}else{
			for(UUID enemiedByFactionId : enemiedByFactions){
				Faction enemiedByFaction = FactionUtil.getFactionFromId(enemiedByFactionId);
				if(slot == 44){
					ItemStack enemiedByMore = new MenuButton().type(Material.BANNER).durability(14).name("&7And " + String.valueOf(enemiedByFactions.length - 45) + "More...").get();
					inventory.setItem(44, enemiedByMore);
					break;
				}else{
					ItemStack enemiedByButton = new MenuButton().type(enemiedByFaction.getIcon().getType()).durability(enemiedByFaction.getIcon().getDurability()).name(enemiedByFaction.getName())
							.lore("&aClick here to view more stats about", "&athis faction.", "", "&9Level: &e" + enemiedByFaction.getLevel() + "/25", "&9Score: &e" + enemiedByFaction.getScore(), "&9Power: &e" + enemiedByFaction.getPower() + "/1000", 
								  "&9Members: &e" + enemiedByFaction.getMembers().size() + "/45").get();
					inventory.setItem(slot, enemiedByButton);
				}
				slot++;
			}
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the enemied by relation page, here you", "&acan view all of the factions that enemy", "&ayour faction.").get();
		inventory.setItem(45, back);
		inventory.setItem(53, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.ENEMIED_BY);
	}
	
	/*
	 * Vault, storage and other things
	 */
	public static void vault(Player player, int tabNumber){
		Faction faction = FactionUtil.getPlayerFaction(player);
		Vault vault = faction.getVault();
		VaultTab tab = null;
		switch(tabNumber){
		case 1:
			tab = vault.getTab1();
			break;
		case 2:
			tab = vault.getTab2();
			break;
		case 3:
			tab = vault.getTab3();
			break;
		case 4:
			tab = vault.getTab4();
			break;
		default:
			return;
		}
		Inventory inventory = tab.getContents();
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		inventory.setItem(45, back);
		MenuButton tab1 = new MenuButton().type(vault.getTab1().getIcon().getType()).durability(vault.getTab1().getIcon().getDurability()).name(Util.setStringColors("&6" + vault.getTab1().getName()))
			.lore("&aClick here to open this tab.");
		inventory.setItem(46, tab1.get());
		if(faction.hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_1)){
			MenuButton tab2 = new MenuButton().type(vault.getTab2().getIcon().getType()).durability(vault.getTab2().getIcon().getDurability()).name(Util.setStringColors("&6" + vault.getTab2().getName()))
					.lore("&aClick here to open this tab.");
				inventory.setItem(47, tab2.get());
		}else{
			ItemStack lockedTab = new MenuButton().type(Material.CHEST).name("&cLocked Tab")
				.lore("&aThis tab requires you to complete", "&aa faction challenge.", "", "&9Challenge Required: &e" + FactionChallenge.ITEMS_FISHED_1.getName()).get();
			inventory.setItem(47, lockedTab);
		}
		if(faction.hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_2)){
			MenuButton tab3 = new MenuButton().type(vault.getTab3().getIcon().getType()).durability(vault.getTab3().getIcon().getDurability()).name(Util.setStringColors("&6" + vault.getTab3().getName()))
					.lore("&aClick here to open this tab.");
				inventory.setItem(48, tab3.get());
		}else{
			ItemStack lockedTab = new MenuButton().type(Material.CHEST).name("&cLocked Tab")
				.lore("&aThis tab requires you to complete", "&aa faction challenge.", "", "&9Challenge Required: &e" + FactionChallenge.ITEMS_FISHED_2.getName()).get();
			inventory.setItem(48, lockedTab);
		}
		if(faction.hasChallengeCompleted(FactionChallenge.ITEMS_FISHED_3)){
			MenuButton tab4 = new MenuButton().type(vault.getTab4().getIcon().getType()).durability(vault.getTab4().getIcon().getDurability()).name(Util.setStringColors("&6" + vault.getTab4().getName()))
					.lore("&aClick here to open this tab.");
				inventory.setItem(49, tab4.get());
		}else{
			ItemStack lockedTab = new MenuButton().type(Material.CHEST).name("&cLocked Tab")
				.lore("&aThis tab requires you to complete", "&aa faction challenge.", "", "&9Challenge Required: &e" + FactionChallenge.ITEMS_FISHED_3.getName()).get();
			inventory.setItem(49, lockedTab);
		}
		ItemStack coinsD = new MenuButton().type(Material.EMERALD).name("&6Deposit Coins")
			.lore("&aClick here to deposit coins into", "&athe faction vault.", "", "&9Vault Balance: &e" + vault.getMoney()).get();
		inventory.setItem(50, coinsD);
		ItemStack coinsW = new MenuButton().type(Material.EMERALD).name("&6Withdrawl Coins")
				.lore("&aClick here to withdrawl coins from", "&athe faction vault.", "", "&9Vault Balance: &e" + vault.getMoney()).get();
			inventory.setItem(51, coinsW);
		ItemStack log = new MenuButton().type(Material.BOOK_AND_QUILL).name("&6Vault Log")
			.lore(vault.getLog()).get();
		inventory.setItem(52, log);
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the faction vault, the vault is",
				  "&aa safe storage for coins and items",
				  "&afor your faction.",
				  "",
				  "&aWith proper permission from your",
				  "&afaction rank you can withdrawl and",
				  "&adeposit items and coins that are",
				  "&ashared with members of your faction.",
				  "&a",
				  "&aTake a look at this page and learn", "&amore about the faction vault.").get();
		inventory.setItem(53, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.VAULT);
	}
	
	/*
	 * Vault deposit coins, for depositing coins
	 */
	public static void vaultDepositCoins(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Amount To Deposit")
			.lore("&aType in the amount of coins you", "&awant to deposit into the vault,", "&athen click the nametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.VAULT_DEPOSIT_COINS);
	}
	
	/*
	 * Vault withdrawl coins, for taking out coins
	 */
	public static void vaultWithdrawlCoins(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Amount To Withdrawl")
			.lore("&aType in the amount of coins you", "&awant to withdrawl from the vault,", "&athen click the nametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.VAULT_WITHDRAWL_COINS);
	}
	
	/*
	 * Vault settings, to edit vault name and icon.
	 */
	public static void vaultEditTab(Player player, VaultTab tab){
		Inventory inventory = Bukkit.createInventory(null, 18, Util.setStringColors("Editing " + tab.getName()));
		MenuButton changeName = new MenuButton().type(Material.NAME_TAG).name("&6Change Vault Tab Name")
			.lore("&aClick here to change this vault", "&atab's name.", "", "&9Current Name: &e" + tab.getName());
		MenuButton changeIcon = new MenuButton().type(Material.ITEM_FRAME).name("&6Change Vault Tab Icon")
			.lore("&aClick here to change this vault", "&atab's icon.");
		if(!FactionUtil.hasPermission(player, RankPermission.EDIT_VAULT)){
			changeName.lore("", "&cYou do not have permission.");
			changeIcon.lore("", "&cYou do not have permission.");
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the vault edit tab main-page, here",
				  "&awith proper permission from your faction",
				  "&arank you can change the name and icon",
				  "&aof your vault tab.").get();
		inventory.setItem(2, changeName.get()); inventory.setItem(6, changeIcon.get()); inventory.setItem(9, back);
		inventory.setItem(17, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.VAULT_EDIT);
		MenuHelper.setEditingVaultTab(player, tab);
	}
	
	/*
	 * Edit vault name menu, to edit the name.
	 */
	public static void vaultEditName(Player player, VaultTab tab){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Vault Tab Name")
			.lore("&aType in the name you want to", "&achange " + tab.getName() + " &r&ato.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.VAULT_EDIT_NAME);
		MenuHelper.setEditingVaultTab(player, tab);
	}
	
	/*
	 * Vault edit icon menu, to pick an icon to change your vault icon to.
	 */
	public static void vaultEditIcon(Player player, VaultTab tab){
		Inventory inventory = Bukkit.createInventory(null, 54, "Select A Vault Tab Icon");
		IconSelectionContainer.createPage(player, inventory, IconPage.PAGE_1, null, "&6Select This Vault Tab Icon", "&aClick here to select this icon to be", "&ayour vault tab's icon.");
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.VAULT_EDIT_ICON);
		MenuHelper.setEditingVaultTab(player, tab);
	}
	
	/*
	 * Warps main menu, handles all faction warp stuff
	 */
	public static void warps(Player player){
		Inventory inventory = Bukkit.createInventory(null,  18, "Warps");
		renderWarps(player, inventory);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.WARPS);
	}
	
	/*
	 * Render warps menu for updating
	 */
	public static void renderWarps(Player player, Inventory inventory){
		Faction faction = FactionUtil.getPlayerFaction(player);
		int slot = 0;
		for(Warp warp : faction.getWarps()){
			MenuButton warpButton = new MenuButton().type(Material.ENDER_PEARL).name("&6" + warp.getName())
				.lore("&aClick here to teleport to this warp.").addGlow();
			if(FactionUtil.hasPermission(player, RankPermission.DELETE_WARPS)) warpButton.lore("", "&aShift + Right-Click here to delete", "&athis warp.");
			if(!FactionUtil.hasPermission(player, RankPermission.USE_WARPS)) warpButton.lore("", "&cYou do not have permission.");
			inventory.setItem(slot, warpButton.get());
			slot++;
		}
		for(int unusedSlot = slot; unusedSlot < 9; unusedSlot++){
			MenuButton unusedButton = new MenuButton();
			if(faction.hasWarpChallenge(unusedSlot)){
				unusedButton.type(Material.ENDER_PEARL).name("&6Unset Warp")
					.lore("&aClick here to set this warp at your", "&acurrent location.");
				if(!FactionUtil.hasPermission(player, RankPermission.SET_WARPS)) unusedButton.lore("", "&cYou do not have permission.");
			}else{
				FactionChallenge chal = null;
				switch(unusedSlot){
				case 3: case 4: chal = FactionChallenge.LAND_OVERCLAIMED_1; break;
				case 5: case 6: chal = FactionChallenge.LAND_OVERCLAIMED_2; break;
				case 7: case 8: chal = FactionChallenge.LAND_OVERCLAIMED_3; break;
				}
				unusedButton.type(Material.EYE_OF_ENDER).name("&cLocked Warp")
					.lore("&aThis warp requires you to complete", "&aa faction challenge.", "", "&9Challenge Required: &e" + chal.getName());
			}
			inventory.setItem(unusedSlot, unusedButton.get());
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack mainPage = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the faction warps main-page, here",
				  "&awith proper permission from your faction",
				  "&arank you can set, delete and teleport to",
				  "&afaction warps.",
				  "",
				  "&aFaction warps are warps that only your",
				  "&afaction has access to use. They can be",
				  "&aset anywhere although have a much longer",
				  "&ateleport time.").get();
		inventory.setItem(9, back); inventory.setItem(17, mainPage);
	}
	
	public static void setWarp(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Warp Name")
			.lore("&aType the name you want for", "&ayour warp, then cick the", "&anametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.WARPS_NAME);
	}
	
	/*
	 * Claimed Land, displays all claimed land
	 */
	public static void claimedLand(Player player, int tab){
		Inventory inventory = Bukkit.createInventory(null, 54, "Claimed Land");
		Faction faction = FactionUtil.getPlayerFaction(player);
		renderClaimedLand(player, faction, tab, inventory);
		ItemStack page1 = new MenuButton().type(Material.BEACON).name("&6Claimed Land Page 1")
			.lore("&aClick here to open this claimed land", "&apage and view chunk claimed 1-45.").get();
		inventory.setItem(47, page1);
		for(int i = 2; i < 5; i++){
			MenuButton page = new MenuButton().type(Material.BEACON);
			switch(i){
			case 2:
				if(faction.isLandPageLocked(i)) page.name("&cLocked Page").lore("&aThis page requires you to complete", "&aa faction challenge.", "", "&9Challenge Required: &e" + faction.getLandPageChallenge(i).getName());
				else page.name("&6Claimed Land Page 2").lore("&aClick here to open this claimed land", "&apage and view chunk claimed 46-90.");
				inventory.setItem(48, page.get()); break;
			case 3:
				if(faction.isLandPageLocked(i)) page.name("&cLocked Page").lore("&aThis page requires you to complete", "&aa faction challenge.", "", "&9Challenge Required: &e" + faction.getLandPageChallenge(i).getName());
				else page.name("&6Claimed Land Page 3").lore("&aClick here to open this claimed land", "&apage and view chunk claimed 91-135.");
				inventory.setItem(49, page.get()); break;
			case 4:
				if(faction.isLandPageLocked(i)) page.name("&cLocked Page").lore("&aThis page requires you to complete", "&aa faction challenge.", "", "&9Challenge Required: &e" + faction.getLandPageChallenge(i).getName());
				else page.name("&6Claimed Land Page 4").lore("&aClick here to open this claimed land", "&apage and view chunk claimed 136-180.");
				inventory.setItem(50, page.get()); break;
			}
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		inventory.setItem(45, back);
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the claimed land main-page, here",
				  "&awith proper permission from your faction",
				  "&arank you can claim, unclaim, and view the",
				  "&achunk locations for your claimed land.",
				  "",
				  "&aClaiming land gives your faction a 16x16",
				  "&aarea that no other faction has access to,",
				  "&aso protect it the best you can, and claim",
				  "&aas much as you can.").get();
		inventory.setItem(53, whatIs);
		MenuButton claimLand = new MenuButton().type(Material.NETHER_STAR).name("&6Claim Land")
			.lore("&aClick here to claim the chunk you", "&aare currently standing in for", "&ayour faction.", "", "&9Costs: &e75 Power");
		if(!FactionUtil.hasPermission(player, RankPermission.CLAIM_LAND)) claimLand.lore("", "&cYou do not have permission.");
		inventory.setItem(51, claimLand.get());
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.CLAIMED_LAND);
	}
	
	/*
	 * Renderer for the claimed land tab
	 */
	public static void renderClaimedLand(Player player, Faction faction, int tab, Inventory inventory){
		for(int i = 0; i < 45; i++){
			inventory.setItem(i, null);
		}
		boolean hasView = FactionUtil.hasPermission(player, RankPermission.VIEW_LAND_LOCATIONS);
		boolean hasUnclaim = FactionUtil.hasPermission(player, RankPermission.UNCLAIM_LAND);
		List<ClaimedLand> factionLand = faction.getClaimedLand();
		int min = (tab * 45) - 45;
		int max = ((factionLand.size()) - min > 45 ? 45 : (factionLand.size() - min));
		List<ClaimedLand> viewingLand = new ArrayList<ClaimedLand>();
		if(max < min || max == 0){
			ItemStack emptyButton = new MenuButton().type(Material.WEB).name("&6Empty Page")
					.lore("&aThere is no claimed land to display", "&aon the page.").get();
				inventory.setItem(4, emptyButton);
		}else{
			int slot = 0;
			for(int i = min; i < min + max; i++){
				ClaimedLand land = factionLand.get(i);
				MenuButton landButton = new MenuButton().type(Material.NETHER_STAR).name("&6Claimed Chunk");
				if(hasUnclaim) landButton.lore("&aShift + Right-Click here to unclaim", "&athis chunk.", "");
				landButton.lore("&9Claimed By: &e" + Bukkit.getOfflinePlayer(land.getClaimer()).getName(), "&9Claimed Date: &e" + Util.getMonthDayYearDate(land.getDateClaimed()));
				if(hasView){
					Chunk chunk = Serialization.stringToChunk(land.getLocation());
					String world = "Overworld";
					if(chunk.getWorld().getName().contains("end")) world = "End";
					else if(chunk.getWorld().getName().contains("nether")) world = "Nether";
					landButton.lore("&9Chunk Location: &e" + world + ", " + chunk.getX() + "x, " + chunk.getZ() + "z");
				}else landButton.lore("&cLand locations are hidden from you.");
				inventory.setItem(slot, landButton.get());
				viewingLand.add(land);
				slot++;
			}
		}
		MenuHelper.setViewingLand(player, viewingLand);
		MenuHelper.setViewingPage(player, tab);
	}
	
	/*
	 * Faction Challenge main menu
	 */
	public static void challenges(Player player){
		Inventory inventory = Bukkit.createInventory(null, 45, "Faction Challenges");
		Faction faction = FactionUtil.getPlayerFaction(player);
		for(FactionChallenge challenge : FactionChallenge.values()){
			MenuButton challengeButton = new MenuButton();
			if(faction.hasChallengeCompleted(challenge)){
				CompletedChallenge completed = faction.getCompletedChallenge(challenge);
				challengeButton.type(Material.DIAMOND).addGlow().name("&6&m" + challenge.getName()).lore("&aChallenge Completed!", "", "&9Completed By: &e" + Bukkit.getOfflinePlayer(completed.getCompletedBy()).getName(), "&9Completed On: &e" + Util.getMonthDayYearDate(completed.getDateCompleted()));
			}else{
				challengeButton.type(Material.COAL).name("&6" + challenge.getName()).lore(challenge.getLore()).lore("&9Progress: &e" + Util.comma(faction.getStat(challenge.getStatType())) + "/" + Util.comma(challenge.getStatAmount()));
			}
			inventory.setItem(challenge.getMenuSlot(), challengeButton.get());
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the faction challenges page, here",
				  "&ayou can view all of the challenges your",
				  "&afaction has completed, thier progress, and",
				  "&athe rewards given by each challenge.",
				  "",
				  "&aFaction challenges are completed together",
				  "&aand only faction members contribute to",
				  "&atheir progress.").get();
		inventory.setItem(36, back); inventory.setItem(44, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.CHALLENGES);
	}
	
	/*
	 * Faction settings
	 */
	public static void settings(Player player){
		Inventory inventory = Bukkit.createInventory(null, 18, "Settings");
		Faction faction = FactionUtil.getPlayerFaction(player);
		MenuButton nameButton = new MenuButton().type(Material.NAME_TAG).name("&6Change Faction Name")
			.lore("&aClick here to change your faction's", "&aname.", "", "&9Current Name: &e" + faction.getName(), "&9Price: &e" + Factions.plugin.getConfig().getInt("prices.changeName") + " Coins");
		if(!faction.getLeader().equals(player.getUniqueId())) nameButton.lore("", "&cOnly the leader has permission.");
		MenuButton iconButton = new MenuButton().type(Material.ITEM_FRAME).name("&6Change Faction Icon")
			.lore("&aClick here to change your faction's", "&aicon.");
		if(!FactionUtil.hasPermission(player, RankPermission.SET_ICON)) iconButton.lore("", "&cYou do not have permission.");
		MenuButton soundButton = new MenuButton().type(Material.RECORD_5).name("&6Change Faction Sound")
			.lore("&aClick here to change your faction's", "&asound.", "", "&9Sounds Unlocked: &e" + FactionSound.getSoundsUnlocked(faction) + "/25", "&9Current Sound: &e" + faction.getSound().getName());
		if(!FactionUtil.hasPermission(player, RankPermission.SET_SOUND)) soundButton.lore("", "&cYou do not have permission.");
		MenuButton statusButton = new MenuButton().type(Material.IRON_BLOCK).name("&6Change Faction Status")
			.lore("&aClick here to change your faction's");
		if(faction.getStatus() == FactionStatus.CLOSED) statusButton.lore("&astatus to open.", "", "&aOpen factions allow anyone to join", "&awithout an invitation.");
		else statusButton.lore("&astatus to closed.", "", "&aClosed factions require members to", "&abe invited to join the faction.");
		statusButton.lore("", "&9Current Status: &e" + String.valueOf(faction.getStatus() == FactionStatus.CLOSED ? "Closed" : "Open"));
		if(!FactionUtil.hasPermission(player, RankPermission.SET_STATUS)) statusButton.lore("","&cYou do not have permission.");
		MenuButton sethomeButton = new MenuButton().type(Material.GRASS).name("&6Change Faction Home")
			.lore("&aClick here to change you faction's", "&ahome to your current location.");
		if(!FactionUtil.hasPermission(player, RankPermission.SET_HOME)) sethomeButton.lore("","&aYou do not have permission.");
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the settings main-page, here",
				  "&ayou can edit many settings for your",
				  "&afaction. For example, changing the",
				  "&afaction's name or icon.").get();
		
		inventory.setItem(0, nameButton.get()); inventory.setItem(2, iconButton.get()); inventory.setItem(4, soundButton.get());
		inventory.setItem(6, statusButton.get()); inventory.setItem(8, sethomeButton.get()); inventory.setItem(9, back);
		inventory.setItem(17, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.SETTINGS);
	}
	
	
	/*
	 * Faction ChangeName menu
	 */
	public static void changeName(Player player){
		Inventory inventory = AnvilContainer.openAnvil(player);
		ItemStack nameTag = new MenuButton().type(Material.NAME_TAG).name("Faction Name")
			.lore("&aType in the name you want to", "&arename your faction to, then", "&aclick the nametag.").get();
		inventory.setItem(0, nameTag);
		player.setLevel(player.getLevel() + 1);
		MenuHelper.setCurrentlyViewing(player, MenuType.EDIT_NAME);
	}
	
	/*
	 * Change icon selection menu
	 */
	public static void changeIcon(Player player){
		Inventory inventory = Bukkit.createInventory(null, 54, "Select A Faction Icon");
		IconSelectionContainer.createPage(player, inventory, IconPage.PAGE_1, null, "&6Select This Faction Icon", "&aClick here to select this icon to be", "&ayour faction's icon.");
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.EDIT_ICON);
	}
	
	/*
	 * Change faction sound selction menu
	 */
	public static void changeSound(Player player){
		Inventory inventory = Bukkit.createInventory(null, 54);
		Faction faction = FactionUtil.getPlayerFaction(player);
		for(FactionSound sound : FactionSound.values()){
			MenuButton disc = new MenuButton().type(sound.getMaterial());
			if(faction.hasSoundUnlocked(sound)){
				disc.name("&6" + sound.getName()).addGlow();
				if(faction.getSound() == sound) disc.lore("&aCurrent faction sound.", "", "&aRight-Click here to listen to this", "&asound, as long as your volume is up.");
				else disc.lore("&aLeft-Click here to select this to be", "&ayour faction's sound.", "", "&aRight-Click here to listen to this", "&asound, as long as your volume is up.");
			}else{
				disc.name("&cLocked Sound")
					.lore("&aThis sound requires your faction to", "&areach a certian level.", "", "&aRight-Click here to listen to this", "&asound, as long as you volume is up.", "", "&9Level Required: &e" + sound.getLevelRequirement());
			}
			inventory.setItem(sound.getMenuSlot(), disc.get());
		}
		ItemStack back = new MenuButton().type(Material.PAPER).name("&6Back").get();
		ItemStack whatIs = new MenuButton().type(Material.BOOK).name("&6What Is This Page?")
			.lore("&aThis is the select a sound page, here with",
				  "&aproper permission from your faction rank",
				  "&ayou can select your faction's sound, see",
				  "&aall of the sounds you can unlock, and hear",
				  "&aevery faction sound.").get();
		inventory.setItem(45, back);
		inventory.setItem(53, whatIs);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.EDIT_SOUND);
	}
	
	/*
	 * Set new leader confirmation
	 */
	public static void leaderConfirmation(Player player, String playerName){
		Inventory inventory = Bukkit.createInventory(null, 9, "Change Leader: Are You Sure?");
		for(int i = 0; i < 4; i++){
			ItemStack yes = new MenuButton().type(Material.STAINED_GLASS_PANE).durability(5).name("&aConfirm").get();
			inventory.setItem(i, yes);
		}
		for(int i = 5; i < 9; i++){
			ItemStack no = new MenuButton().type(Material.STAINED_GLASS_PANE).durability(14).name("&cCancel").get();
			inventory.setItem(i, no);
		}
		ItemStack middle = new MenuButton().type(Material.NAME_TAG).name("&6Change Faction Leader")
			.lore("&aWhen you change the faction leader you,", "&aare revoking your status as leader.", "", "&aYou will be automatically set to the",
				  "&afaction's default rank.", "&aThis cannot be undone, are your sure", "&ayou want to change the faction leader?", "", "&9New Leader: &e" + playerName).get();
		inventory.setItem(4, middle);
		player.openInventory(inventory);
		MenuHelper.setCurrentlyViewing(player, MenuType.LEADER_CONFIRMATION);
	}
}
