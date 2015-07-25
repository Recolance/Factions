package me.recolance.factions.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.recolance.coins.api.CoinsAPI;
import me.recolance.coins.util.CoinsUtil;
import me.recolance.factions.Factions;
import me.recolance.factions.controller.Controller;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Serialization;
import me.recolance.factions.faction.ClaimedLand;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.faction.FactionInvitation;
import me.recolance.factions.faction.FactionSound;
import me.recolance.factions.faction.FactionStatus;
import me.recolance.factions.faction.Member;
import me.recolance.factions.faction.Rank;
import me.recolance.factions.faction.RankPermission;
import me.recolance.factions.faction.Vault;
import me.recolance.factions.faction.VaultTab;
import me.recolance.factions.faction.Warp;
import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuController implements Listener{

	private static HashMap<UUID, Long> timer = new HashMap<UUID, Long>();
	
	@SuppressWarnings("incomplete-switch")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInvetoryClick(InventoryClickEvent event){
		Player player = (Player)event.getWhoClicked();
		if(!MenuHelper.isViewing(player)) return;

		if(!timer.containsKey(player.getUniqueId())) timer.put(player.getUniqueId(), System.currentTimeMillis());
		else if(System.currentTimeMillis() - timer.get(player.getUniqueId()) < 400){
			event.setCancelled(true);
			return;
		}
		timer.put(player.getUniqueId(), System.currentTimeMillis());
		
		Inventory inventory = event.getInventory();
		ClickType click = event.getClick();
		MenuType menu = MenuHelper.getCurrentlyViewing(player);
		int slot = event.getRawSlot();
		if(menu.isFullCancel() && MenuUtil.isCancellableClick(inventory, slot, click)) event.setCancelled(true);
		ItemStack item = event.getCurrentItem();
		if((item == null || item.getType() == Material.AIR) && menu != MenuType.VAULT) return;
		if(FactionUtil.isInFaction(player) && menu.requiresFaction() == 0){
			player.closeInventory();
			Util.message(player, "&cYou cannot do that while being in a faction.");
			return;
		}else if(!FactionUtil.isInFaction(player) && menu.requiresFaction() == 1){
			player.closeInventory();
			Util.message(player, "&cYou must be in a faction to do that.");
			return;
		}
		
		switch(menu){
		case NO_FACTION_MAIN:
			if(slot == 1){
				player.closeInventory();
				MenuRenderer.creationName(player);
			}else if(slot == 7){
				player.closeInventory();
				MenuRenderer.openFactions(player);
			}
			break;
		case CREATION_NAME:
			if(slot == 2){
				if(FactionUtil.isApplicableFactionName(Util.getAnvilName(item), inventory)){
					List<Object> creationData = new ArrayList<Object>();
					creationData.add(Util.getAnvilName(item));
					MenuHelper.setCreationData(player, creationData);
					player.closeInventory();
					MenuRenderer.creationIcon(player);
				}
			}
			break;
		case CREATION_ICON:
			if(slot < 45){
				List<Object> creationData = MenuHelper.getCreationData(player);
				creationData.add(new ItemStack(item.getType(), 1, item.getDurability()));
				player.closeInventory();
				MenuHelper.setCreationData(player, creationData);
				MenuRenderer.creationSound(player);
			}else if(slot == 45) IconSelectionContainer.setPage(player, inventory, IconSelectionContainer.getPreviousPage(IconSelectionContainer.getViewingPage(player)), "&6Select This Faction Icon", "&aClick here to select this icon to be", "&ayour faction's icon.");
			 else if(slot == 53) IconSelectionContainer.setPage(player, inventory, IconSelectionContainer.getNextPage(IconSelectionContainer.getViewingPage(player)), "&6Select This Faction Icon", "&aClick here to select this icon to be", "&ayour faction's icon.");
			break;
		case CREATION_SOUND:
			if(slot < 9){
				if(click.isLeftClick()){
					List<Object> creationData = MenuHelper.getCreationData(player);
					creationData.add(FactionSound.getSoundFromSlot(slot));
					System.out.println(creationData.get(0) + "  " + creationData.get(1) + "   " + creationData.get(2));
					player.closeInventory();
					Faction.create(player, (String)creationData.get(0), (ItemStack)creationData.get(1), (FactionSound)creationData.get(2));
				}else if(click.isRightClick()){
					player.playSound(player.getLocation(), FactionSound.getSoundFromSlot(slot).getSound(), 1F, 0F);
				}
			}
			break;
		case DISBAND_CONFIRMATION:
			if(slot < 4){
				if(FactionUtil.isLeader(player)){
					player.closeInventory();
					FactionUtil.getPlayerFaction(player).disband(player);
				}else if(slot > 4 && slot < 9){
					player.closeInventory();
					Util.message(player, "&cOnly the faction leader can disband the faction.");
				}
			}else if(slot < 4) player.closeInventory();
			break;
		case OPEN_FACTIONS:
			if(slot < 45 && !DataHolder.getOpenFactions().isEmpty()){
				Faction faction = FactionUtil.getFactionFromName(Util.stripStringColors(item.getItemMeta().getDisplayName()));
				if(faction == null)	MenuUtil.displayMenuInvalidity(slot, inventory, item, "Faction No Longer Exists");
				else if(FactionUtil.isInFaction(player)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Are Already In A Faction");
				else if(faction.getMembers().size() > 44) MenuUtil.displayMenuInvalidity(slot, inventory, item, "&cThat Faction Is Full");
				else{
					player.closeInventory();
					faction.addMember(player);
				}
			}else if(slot == 45){
				player.closeInventory();
				MenuRenderer.noFactionMain(player);
			}else if(slot == 49){
				player.closeInventory();
				MenuRenderer.creationName(player);
			}
			break;
		case FACTION_MAIN:
			if(slot == 4){
				player.closeInventory();
				MenuRenderer.stats(player, FactionUtil.getPlayerFaction(player));
			}
			else if(slot == 9){
				player.closeInventory();
				FactionUtil.getPlayerFaction(player).teleportHome(player);
			}else if(slot == 10){
				player.closeInventory();
				MenuRenderer.members(player);
			}else if(slot == 11){
				player.closeInventory();
				MenuRenderer.ranks(player);
			}else if(slot == 12){
				player.closeInventory();
				MenuRenderer.relations(player);
			}else if(slot == 13){
				player.closeInventory();
				MenuRenderer.vault(player, 1);
			}else if(slot == 14){
				player.closeInventory();
				MenuRenderer.warps(player);
			}else if(slot == 15){
					player.closeInventory();
					MenuRenderer.challenges(player);
			}else if(slot == 16){
				player.closeInventory();
				MenuRenderer.claimedLand(player, 1);
			}else if(slot == 17){
				player.closeInventory();
				MenuRenderer.settings(player);
			}else if(slot == 31){
				player.closeInventory();
				player.chat("/f help");
			}
			break;
		case MEMBERS:
			if(slot < 45 && click.isShiftClick() && click.isRightClick() && FactionUtil.hasPermission(player, RankPermission.KICK_MEMBERS)){
				System.out.println(MenuHelper.getMembersViewing(player).get(slot).getPlayer());
				FactionUtil.getPlayerFaction(player).kickMember(player, MenuHelper.getMembersViewing(player).get(slot).getPlayer(), inventory, slot, inventory.getItem(slot));
			}
			else if(slot == 45){
				player.closeInventory();
				MenuRenderer.factionMain(player);
			}else if(slot == 49){
				if(!FactionUtil.hasPermission(player, RankPermission.INVITE_MEMBERS)) MenuUtil.displayMenuInvalidity(slot, inventory, inventory.getItem(slot), "&cYou Don't Have Permission");
				else{
					player.closeInventory();
					MenuRenderer.inviteMember(player);
				}
			}
			break;
		case MEMBERS_INVITE:
			if(slot == 2){
				if(Util.getAnvilName(item) == null) MenuUtil.displayAnvilInvalidity(inventory, "&cCannot Be Empty.");
				else{
					Player invitingPlayer = Bukkit.getPlayer(Util.getAnvilName(item));
					if(!FactionUtil.hasPermission(player, RankPermission.INVITE_MEMBERS)) MenuUtil.displayAnvilInvalidity(inventory, "&cNo Permission");
					else if(invitingPlayer == null || !invitingPlayer.isOnline()) MenuUtil.displayAnvilInvalidity(inventory, "&cNot Online");
					else if(FactionUtil.isInFaction(invitingPlayer)) MenuUtil.displayAnvilInvalidity(inventory, "&cAlready In Faction");
					else if(FactionInvitation.factionInvitations.containsKey(invitingPlayer) && FactionInvitation.factionInvitations.get(invitingPlayer).getFactionInvitedTo() == FactionUtil.getPlayerFaction(player)) MenuUtil.displayAnvilInvalidity(inventory, "&cAlready Pending");
					else{
						player.closeInventory();
						FactionUtil.getPlayerFaction(player).sendInvitation(player, invitingPlayer);
					}
				}
			}
			break;
		case RANKS:
			if(slot < 9){
				player.closeInventory();
				Rank rank = FactionUtil.getRankFromName(ChatColor.stripColor(item.getItemMeta().getDisplayName()), FactionUtil.getPlayerFaction(player));
				if(rank == null) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Rank No Longer Exists");
				else MenuRenderer.editRank(player, rank);
			}else if(slot == 9){
				player.closeInventory();
				MenuRenderer.factionMain(player);
			}else if(slot == 12){
				if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					player.closeInventory();
					MenuRenderer.createRank(player);
				}
			}
			break;
		case RANKS_EDITING:
			if(slot < 24 || slot == 24 || slot == 25 || slot == 31 || slot == 39 || slot == 40 || slot == 41){
				if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					Rank editingRank = MenuHelper.getEditingRank(player);
					Faction faction = FactionUtil.getFactionFromId(editingRank.getFaction());
					if(!faction.getRanks().contains(editingRank)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "This Rank No Longer Exists");
					else if(FactionUtil.getPlayerMember(player).getRank() == editingRank) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Cannot Edit Own Rank");
					else if(slot < 24){
						faction.addActivityLogEntry("&e" + player.getName() + " edited the " + editingRank.getName() + " rank.");
						RankPermission permission = RankPermission.getRankPermissionFromSlot(slot);
						editingRank.swapPermission(permission);
						MenuUtil.handleSwapPermissionLore(slot, inventory, item);
						String edit = (editingRank.hasPermission(permission) ? "Allowed" : "Denied");
						faction.sendRankMessage(editingRank, "&eYour faction rank was &6edited&e.");
						Util.message(player, "&eYou have set " + "&6" + permission.getName() + " &eto &6" + edit + "&e for the rank &6" + editingRank.getName() + "&e.");
					}else if(slot == 24){
						player.closeInventory();
						MenuRenderer.withdrawlCoins(player, editingRank);
					}else if(slot == 25){
						player.closeInventory();
						MenuRenderer.withdrawlItems(player, editingRank);
					}else if(slot == 31){
						player.closeInventory();
						MenuRenderer.powerThreshold(player, editingRank);
					}else if(slot == 39){
						player.closeInventory();
						MenuRenderer.changeRankName(player, editingRank);
					}else if(slot == 40){
						if(editingRank.isDefault()) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Must Always Have a Default Rank");
						else{
							faction.sendMessage("&6" + player.getName() + " &ehas set the default rank from &6" + faction.getDefaultRank().getName() + " &eto &6" + editingRank.getName());
							faction.dropDefaultRank();
							editingRank.setDefault(true);
							MenuUtil.swapLore(slot, inventory, item, ChatColor.BLUE + "Currently Default: ", ChatColor.BLUE + "Currently Default: " + ChatColor.YELLOW + "Yes");
							faction.addActivityLogEntry("&e" + editingRank.getName() + " is now the default rank");
						}
					}else if(slot == 41){
						if(editingRank.isDefault()) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Must Always Have A Default Rank");
						else{
							player.closeInventory();
							MenuRenderer.removeRankConfirmation(player, editingRank);
						}
					}
				}
			}else if(slot == 36){
				player.closeInventory();
				MenuRenderer.ranks(player);
			}
			break;
		case RANKS_EDITING_COINS:
			if(slot == 2){
				Rank rank = MenuHelper.getEditingRank(player);
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(!faction.getRanks().contains(rank)) MenuUtil.displayAnvilInvalidity(inventory, "Rank Not Exists");
				else if(!item.getItemMeta().hasDisplayName()) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
				else if(!Util.stringIsNumerical(Util.getAnvilName(item))) MenuUtil.displayAnvilInvalidity(inventory, "Numbers Only");
				else if(!Util.stringHasMax(Util.getAnvilName(item), 6)) MenuUtil.displayAnvilInvalidity(inventory, "Under 1000k Only");
				else{
					int coins = Integer.parseInt(Util.getAnvilName(item));
					faction.addActivityLogEntry("&e" + player.getName() + " edited the " + rank.getName() + " rank.");
					Util.message(player, "&eYou have set &6Withdrawl Coins From Faction Vault &eto &6" + coins + " &efor the rank &6" + rank.getName() + "&e.");
					faction.sendRankMessage(rank, "&eYour faction rank was &6edited&e.");
					rank.setVaultMoneyLimit(coins);
					player.closeInventory();
					MenuRenderer.editRank(player, rank);
				}
			}
			break;
		case RANKS_EDITING_ITEMS:
			if(slot == 2){
				Rank rank = MenuHelper.getEditingRank(player);
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(!faction.getRanks().contains(rank)) MenuUtil.displayAnvilInvalidity(inventory, "Rank Not Exists");
				else{
					String amount = Util.getAnvilName(item);
					if(amount == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
					else if(!Util.stringIsNumerical(amount)) MenuUtil.displayAnvilInvalidity(inventory, "Numbers Only");
					else if(!Util.stringHasMax(amount, 6)) MenuUtil.displayAnvilInvalidity(inventory, "Under 1000k Only");
					else{
						int items = Integer.parseInt(amount);
						faction.addActivityLogEntry("&e" + player.getName() + " edited the " + rank.getName() + " rank.");
						Util.message(player, "&eYou have set &6Withdrawl Items From Faction Vault &eto &6" + items + " &efor the rank &6" + rank.getName() + "&e.");
						faction.sendRankMessage(rank, "&eYour faction rank was &6edited&e.");
						rank.setVaultItemsLimit(items);
						player.closeInventory();
						MenuRenderer.editRank(player, rank);
					}
				}
				
			}
			break;
		case RANKS_EDITING_THRESHOLD:
			if(slot == 2){
				Rank rank = MenuHelper.getEditingRank(player);
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(!faction.getRanks().contains(rank)) MenuUtil.displayAnvilInvalidity(inventory, "Rank Not Exists");
				else{
					String amount = Util.getAnvilName(item);
					if(amount == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
					else if(!Util.stringIsNumerical(amount)) MenuUtil.displayAnvilInvalidity(inventory, "Numbers Only");
					else if(!Util.stringHasMax(amount, 6)) MenuUtil.displayAnvilInvalidity(inventory, "Under 1000k Only");
					else if(Integer.parseInt(amount) < 100) MenuUtil.displayAnvilInvalidity(inventory, "Above 99 Only");
					else{
						int threshold = Integer.parseInt(amount);
						faction.addActivityLogEntry("&e" + player.getName() + " edited the " + rank.getName() + " rank.");
						Util.message(player, "&eYou have set &6Power Threshold &eto &6" + threshold + " &efor the rank &6" + rank.getName() + "&e.");
						faction.sendRankMessage(rank, "&eYour faction rank was &6edited&e.");
						rank.setPowerThreshold(threshold);
						player.closeInventory();
						MenuRenderer.editRank(player, rank);
					}
				}
			}
			break;
		case RANKS_EDITING_NAME:
			if(slot == 2){
				Rank rank = MenuHelper.getEditingRank(player);
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(!faction.getRanks().contains(rank)) MenuUtil.displayAnvilInvalidity(inventory, "Rank Not Exists");
				else{
					String name = Util.stripStringColors(Util.getAnvilName(item));
					if(FactionUtil.isApplicableRankName(name, inventory, faction)){
						faction.sendMessage("&6" + player.getName() + " &ehas renamed the rank &6" + rank.getName() + " &eto &6" + name + "&e.");
						faction.addActivityLogEntry("&e" + player.getName() + " edited the " + rank.getName() + " rank.");
						rank.setName(name);
						player.closeInventory();
						MenuRenderer.editRank(player, rank);
					}
				}
			}
			break;
		case RANKS_REMOVE_CONFIRMATION:
			if(slot < 4){
				if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					Rank rank = MenuHelper.getEditingRank(player);
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(!faction.getRanks().contains(rank)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "This Rank No Longer Exists");
					else if(rank.isDefault()) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Must Always Have a Default Rank");
					else if(rank.isLeader()) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Cannot Delete Leader Rank");
					else{
						faction.removeRank(player, rank);
						player.closeInventory();
						MenuRenderer.ranks(player);
					}
				}
			}else if(slot > 4 && slot < 9){
				Rank rank = MenuHelper.getEditingRank(player);
				player.closeInventory();
				MenuRenderer.editRank(player, rank);
			}
			break;
		case RANKS_CREATE:
			if(slot == 2){
				if(!FactionUtil.hasPermission(player, RankPermission.EDIT_RANKS)) MenuUtil.displayAnvilInvalidity(inventory, "No Permission");
				else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(faction.getRanks().size() >= 10) MenuUtil.displayAnvilInvalidity(inventory, "Max Ranks");
					else if(FactionUtil.isApplicableRankName(Util.getAnvilName(item), inventory, faction)){
						Rank rank = faction.addRank(player, Util.getAnvilName(item));
						player.closeInventory();
						MenuRenderer.editRank(player, rank);
					}
				}
			}
			break;
		case RELATION_MAIN:
			if(slot == 1){
				player.closeInventory();
				MenuRenderer.alliances(player);
			}else if(slot == 4){
				player.closeInventory();
				MenuRenderer.enemies(player);
			}else if(slot == 7){
				player.closeInventory();
				MenuRenderer.enemiedBy(player);
			}else if(slot == 9){
				player.closeInventory();
				MenuRenderer.factionMain(player);
			}
			break;
		case ALLIANCES:
			if(slot < 9){
				if(!(click.isShiftClick() && click.isRightClick())){
					Faction faction = FactionUtil.getFactionFromName(Util.stripStringColors(item.getItemMeta().getDisplayName()));
					if(faction != null){
						player.closeInventory();
						MenuRenderer.stats(player, faction);
					}
				}else if(FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)){
					Faction neutralFaction = FactionUtil.getFactionFromName(Util.stripStringColors(Util.getAnvilName(item)));
					if(neutralFaction == null) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Faction No Longer Exists");
					else if(!FactionUtil.getPlayerFaction(player).getAlliances().contains(neutralFaction.getId())) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Faction No Longer An Ally");
					else{
						FactionUtil.getPlayerFaction(player).setNeutral(neutralFaction);
						MenuRenderer.renderAllies(player, inventory);
					}
				}
			}else if(slot == 9){
				player.closeInventory();
				MenuRenderer.relations(player);
			}else if(slot == 13){
				if(FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)){
					player.closeInventory();
					MenuRenderer.alliancesAdd(player);
				}else MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
			}
			break;
		case ALLIANCES_ADD:
			if(slot == 2){
				String name = Util.getAnvilName(item);
				if(FactionUtil.isApplicableAlly(name, player, inventory)){
					player.closeInventory();
					FactionUtil.getPlayerFaction(player).sendAllyRequest(player, FactionUtil.getFactionFromName(name));
				}
			}
			break;
		case ENEMIES:
			if(slot < 9){
				if(!(click.isShiftClick() && click.isRightClick())){
					Faction faction = FactionUtil.getFactionFromName(Util.stripStringColors(Util.getAnvilName(item)));
					if(faction != null){
						player.closeInventory();
						MenuRenderer.stats(player, faction);
					}
				}else{
					if(FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)){
						Faction neutralFaction = FactionUtil.getFactionFromName(Util.stripStringColors(Util.getAnvilName(item)));
						if(neutralFaction == null) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Faction No Longer Exists");
						else if(!FactionUtil.getPlayerFaction(player).getEnemies().contains(neutralFaction.getId())) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Faction No Longer An Enemy");
						else{
							FactionUtil.getPlayerFaction(player).setNeutral(neutralFaction);
							MenuRenderer.renderEnemies(player, inventory);
						}
					}
				}
			}else if(slot == 9){
				player.closeInventory();
				MenuRenderer.relations(player);
			}else if(slot == 13){
				if(FactionUtil.hasPermission(player, RankPermission.EDIT_RELATIONS)){
					player.closeInventory();
					MenuRenderer.enemiesAdd(player);
				}else MenuUtil.displayMenuInvalidity(slot, inventory, item, "&cYou Don't Have Permission");
			}
			break;
		case ENEMIES_ADD:
			if(slot == 2){
				String name = Util.getAnvilName(item);
				if(FactionUtil.isApplicableEnemy(name, player, inventory)){
					player.closeInventory();
					FactionUtil.getPlayerFaction(player).setEnemy(FactionUtil.getFactionFromName(name));
				}
			}
			break;
		case ENEMIED_BY:
			if(slot < 9){
				Faction faction = FactionUtil.getFactionFromName(Util.stripStringColors(item.getItemMeta().getDisplayName()));
				if(faction != null){
					player.closeInventory();
					MenuRenderer.stats(player, faction);
				}
			}else if(slot == 45){
				player.closeInventory();
				MenuRenderer.relations(player);
			}
			break;
		case VAULT:
			if(slot > 44 && slot < 54){
				event.setCancelled(true);
				if(event.getCursor() != null && event.getCursor().getType() != Material.AIR) return;
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(slot == 45){
					player.closeInventory();
					MenuRenderer.factionMain(player);	
				}else if(slot == 46){
					player.closeInventory();
					MenuRenderer.vault(player, 1);
				}else if(slot == 47){
					if(!faction.isTabLocked(2)){
						player.closeInventory();
						MenuRenderer.vault(player, 2);
					}
				}else if(slot == 48){
					if(!faction.isTabLocked(3)){
						player.closeInventory();
						MenuRenderer.vault(player, 3);
					}
				}else if(slot == 49){
					if(!faction.isTabLocked(4)){
						player.closeInventory();
						MenuRenderer.vault(player, 4);
					}
				}else if(slot == 50){
					player.closeInventory();
					MenuRenderer.vaultDepositCoins(player);
				}else if(slot == 51){
					player.closeInventory();
					MenuRenderer.vaultWithdrawlCoins(player);
				}
			}else{
				HandleVaultClick.handleClick(player, slot, click, item, event.getCursor(), inventory, event);
			}
			break;
		case VAULT_DEPOSIT_COINS:
			if(slot == 2){
				if(!FactionUtil.hasPermission(player, RankPermission.VAULT_DEPOSIT)) MenuUtil.displayAnvilInvalidity(inventory, "No Permission");
				else{
					String amountString = Util.getAnvilName(item);
					if(amountString == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
					else if(!Util.stringIsNumerical(amountString)) MenuUtil.displayAnvilInvalidity(inventory, "Numbers Only");
					else if(!Util.stringHasMax(amountString, 6)) MenuUtil.displayAnvilInvalidity(inventory, "Under 1000k Only");
					else{
						int amount = Integer.parseInt(amountString);
						if(amount < 1) MenuUtil.displayAnvilInvalidity(inventory, "Above 0 Only");
						else{
							if(CoinsUtil.hasCoins(player.getUniqueId(), amount)){
								FactionUtil.getPlayerFaction(player).depositCoins(amount, player);
								player.closeInventory();
								MenuRenderer.vault(player, 1);
							}else MenuUtil.displayAnvilInvalidity(inventory, "Not Enough Coins");
						}
					}
				}
			}
			break;
		case VAULT_WITHDRAWL_COINS:
			if(slot == 2){
				String amountString = Util.getAnvilName(item);
				if(amountString == null) MenuUtil.displayAnvilInvalidity(inventory, "Cannot Be Empty");
				else if(!Util.stringIsNumerical(amountString)) MenuUtil.displayAnvilInvalidity(inventory, "Numbers Only");
				else if(!Util.stringHasMax(amountString, 6)) MenuUtil.displayAnvilInvalidity(inventory, "Under 1000k");
				else{
					int amount = Integer.parseInt(amountString);
					if(amount < 1) MenuUtil.displayAnvilInvalidity(inventory, "Above 0 Only");
					else{
						Faction faction = FactionUtil.getPlayerFaction(player);
						Vault vault = faction.getVault();
						Member member = FactionUtil.getPlayerMember(player);
						if((member.getVaultMoneyTaken() + amount) > member.getRank().getVaultMoneyLimit()) MenuUtil.displayAnvilInvalidity(inventory, "Too Much For Today");
						else if(vault.getMoney() < amount) MenuUtil.displayAnvilInvalidity(inventory, "Not Enough In Vault");
						else{
							faction.withdrawlCoins(amount, player, member);
							player.closeInventory();
							MenuRenderer.vault(player, 1);
						}
					}
				}
			}
			break;
		case VAULT_EDIT:
			if(slot == 2){
				VaultTab tab = MenuHelper.getEditingVaultTab(player);
				player.closeInventory();
				MenuRenderer.vaultEditName(player, tab);
			}else if(slot == 6){
				VaultTab tab = MenuHelper.getEditingVaultTab(player);
				player.closeInventory();
				MenuRenderer.vaultEditIcon(player, tab);
			}else if(slot == 9){
				player.closeInventory();
				MenuRenderer.vault(player, 1);
			}
			break;
		case VAULT_EDIT_NAME:
			if(slot == 2){
				String name = Util.getAnvilName(item);
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(FactionUtil.isApplicableTabName(name, player, inventory, faction)){
					VaultTab tab = MenuHelper.getEditingVaultTab(player);
					Util.message(player, "&eYou have changed the vault tab's &6name &efrom &6" + tab.getName() + " &eto &6" + name + "&e.");
					faction.getVault().addLogEntry("&eTab " + tab.getName() + " &r&erenamed to " + name + "&r&e.");
					tab.setName(Util.setStringColors(name));
					player.closeInventory();
				}
			}
			break;
		case VAULT_EDIT_ICON:
			if(slot < 45){
				Util.message(player, "&eYou have changed the vault tab's &6icon&e.");
				Faction faction = FactionUtil.getPlayerFaction(player);
				VaultTab tab = MenuHelper.getEditingVaultTab(player);
				faction.getVault().addLogEntry("&eTab " + tab.getName() + " &r&ehas a new icon.");
				tab.setIcon(new ItemStack(item.getType(), 1, item.getDurability()));
				player.closeInventory();
			}else if(slot == 45) IconSelectionContainer.setPage(player, inventory, IconSelectionContainer.getPreviousPage(IconSelectionContainer.getViewingPage(player)), "&6Select This Vault Tab Icon", "&aClick here to select this icon to be", "&ayour vault tab's icon.");
			else if(slot == 53) IconSelectionContainer.setPage(player, inventory, IconSelectionContainer.getNextPage(IconSelectionContainer.getViewingPage(player)), "&6Select This Vault Tab Icon", "&aClick here to select this icon to be", "&ayour vault tab's icon.");
			break;
		case WARPS:
			if(slot == 9){
				player.closeInventory();
				MenuRenderer.factionMain(player);
			}else if(slot < 9 && item.getType() == Material.ENDER_PEARL){
				if(item.getItemMeta().getDisplayName().equals("§6Unset Warp")){
					if(!FactionUtil.hasPermission(player, RankPermission.SET_WARPS)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "&cYou Don't Have Permission");
					else{
						player.closeInventory();
						MenuRenderer.setWarp(player);
					}
				}else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					Warp warp = FactionUtil.getWarpFromName(Util.stripStringColors(item.getItemMeta().getDisplayName()), faction);
					if(click == ClickType.SHIFT_RIGHT && FactionUtil.hasPermission(player, RankPermission.DELETE_WARPS)){
						faction.removeWarp(player, warp);
						MenuRenderer.renderWarps(player, inventory);
					}else{
						if(warp == null) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Warp Does Not Exist");
						else if(!FactionUtil.hasPermission(player, RankPermission.USE_WARPS)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
						else{
							player.closeInventory();
							faction.useWarp(player, warp);
						}
					}
				}
			}
			break;
		case WARPS_NAME:
			if(slot == 2){
				String name = Util.getAnvilName(item);
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(FactionUtil.isApplicableWarpName(name, player, faction, inventory)){
					faction.addWarp(name, player.getLocation(), player);
					player.closeInventory();
					MenuRenderer.warps(player);
				}
			}
			break;
		case CLAIMED_LAND:
			if(slot == 45){
				player.closeInventory();
				MenuRenderer.factionMain(player);
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(slot == 47 && MenuHelper.getViewingPage(player) != 1){
					MenuRenderer.renderClaimedLand(player, faction, 1, inventory);
				}else if(slot == 48 && !faction.isLandPageLocked(2) && MenuHelper.getViewingPage(player) != 2){
					MenuRenderer.renderClaimedLand(player, faction, 2, inventory);
				}else if(slot == 49 && !faction.isLandPageLocked(3) && MenuHelper.getViewingPage(player) != 3){
					MenuRenderer.renderClaimedLand(player, faction, 3, inventory);
				}else if(slot == 50 && !faction.isLandPageLocked(4) && MenuHelper.getViewingPage(player) != 4){
					MenuRenderer.renderClaimedLand(player, faction, 4, inventory);
				}else if(slot == 51){
					if(!FactionUtil.hasPermission(player, RankPermission.CLAIM_LAND)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
					else{
						Chunk chunk = player.getLocation().getChunk();
						if(FactionUtil.isApplicableClaim(chunk, faction, player, inventory, item, slot)){
							faction.claimLand(Serialization.chunkToString(chunk), player);
							player.closeInventory();
						}
					}
				}
				else if(slot < 45 && click == ClickType.SHIFT_RIGHT && FactionUtil.hasPermission(player, RankPermission.UNCLAIM_LAND) && !MenuHelper.getViewingLand(player).isEmpty()){
					ClaimedLand land = MenuHelper.getViewingLand(player).get(slot);
					if(land == null) MenuUtil.displayMenuInvalidity(slot, inventory, item, "That Chunk Is No Longer Claimed");
					else if(FactionUtil.isApplicableUnclaim(Serialization.stringToChunk(land.getLocation()), faction, player, inventory, item, slot)){
						faction.unClaimLand(land, player);
						MenuRenderer.renderClaimedLand(player, faction, MenuHelper.getViewingPage(player), inventory);
					}
				}
			}
			break;
		case CHALLENGES:
			if(slot == 36){
				player.closeInventory();
				MenuRenderer.factionMain(player);
			}
			break;
		case SETTINGS:
			if(slot == 9){
				player.closeInventory();
				MenuRenderer.factionMain(player);
			}else if(slot == 0){
				if(FactionUtil.getPlayerFaction(player).getLeader().equals(player.getUniqueId())){
					player.closeInventory();
					MenuRenderer.changeName(player);
				}else MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
			}else if(slot == 2){
				if(!FactionUtil.hasPermission(player, RankPermission.SET_ICON)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					player.closeInventory();
					MenuRenderer.changeIcon(player);
				}
			}else if(slot == 4){
				if(!FactionUtil.hasPermission(player, RankPermission.SET_SOUND)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					player.closeInventory();
					MenuRenderer.changeSound(player);
				}
			}else if(slot == 6){
				if(!FactionUtil.hasPermission(player, RankPermission.SET_STATUS)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					FactionStatus status = faction.getStatus() == FactionStatus.CLOSED ? FactionStatus.OPEN : FactionStatus.CLOSED;
					Controller.changeStatus(faction, player, status);
					MenuUtil.swapLore(slot, inventory, item, ChatColor.BLUE + "Current Status: ", ChatColor.BLUE + "Current Status: " + ChatColor.YELLOW + String.valueOf(status == FactionStatus.CLOSED ? "Closed" : "Open"));
				}
			}else if(slot == 8){
				if(!FactionUtil.hasPermission(player, RankPermission.SET_HOME)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(!faction.hasClaimedLand(player.getLocation().getChunk())) MenuUtil.displayMenuInvalidity(slot, inventory, item, "Can Only Set In Own Land");
					else faction.changeHome(player, player.getLocation());
				}
			}
			break;
		case EDIT_NAME:
			if(slot == 2){
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(!faction.getLeader().equals(player.getUniqueId())) MenuUtil.displayAnvilInvalidity(inventory, "Only Owner");
				else if(!CoinsAPI.hasCoins(player.getUniqueId(), Factions.plugin.getConfig().getInt("prices.changeName"))) MenuUtil.displayAnvilInvalidity(inventory, "Not Enough Coins");
				else{
					String name = Util.getAnvilName(item);
					if(FactionUtil.isApplicableFactionName(Util.getAnvilName(item), inventory)){
						CoinsAPI.removeCoins(player.getUniqueId(), Factions.plugin.getConfig().getInt("prices.changeName"), "Faction name change.");
						FactionUtil.getPlayerFaction(player).changeName(player, name);	
						player.closeInventory();
						MenuRenderer.settings(player);
					}
				}
			}
			break;
		case EDIT_ICON:
			if(slot < 45){
				if(!FactionUtil.hasPermission(player, RankPermission.SET_ICON)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
				else{
					FactionUtil.getPlayerFaction(player).changeIcon(player, item);
					player.closeInventory();
					MenuRenderer.settings(player);
				}
			}else if(slot == 45)IconSelectionContainer.setPage(player, inventory, IconSelectionContainer.getPreviousPage(IconSelectionContainer.getViewingPage(player)), "&6Select This Faction Icon", "&aClick here to select this icon to be", "&ayour faction's icon.");
			else if(slot == 53)IconSelectionContainer.setPage(player, inventory, IconSelectionContainer.getNextPage(IconSelectionContainer.getViewingPage(player)), "&6Select This Faction Icon", "&aClick here to select this icon to be", "&ayour faction's icon.");
			break;
		case EDIT_SOUND:
			if(slot < 45){
				if(click.isLeftClick()){
					if(!FactionUtil.hasPermission(player, RankPermission.SET_SOUND)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "You Don't Have Permission");
					else{
						Faction faction = FactionUtil.getPlayerFaction(player);
						FactionSound sound = FactionSound.getSoundFromSlot(slot);
						if(!faction.hasSoundUnlocked(sound)) MenuUtil.displayMenuInvalidity(slot, inventory, item, "That Sound Is Locked");
						else{
							faction.changeSound(player, sound);
							player.closeInventory();
							MenuRenderer.settings(player);
						}
					}
				}else if(click.isRightClick()) player.playSound(player.getLocation(), FactionSound.getSoundFromSlot(slot).getSound(), 1, 1);
			}else if(slot == 45){
				player.closeInventory();
				MenuRenderer.settings(player);
			}
			break;
		case LEADER_CONFIRMATION:
			if(slot < 4){
				String newLeaderName = "";
				for(String loreString : inventory.getItem(4).getItemMeta().getLore()){
					if(loreString.contains("New Leader: ")){
						newLeaderName = loreString.replace(ChatColor.BLUE + "New Leader: " + ChatColor.YELLOW, "");
						break;
					}
				}
				Player nLP = Bukkit.getPlayer(newLeaderName);
				if(nLP == null || !nLP.isOnline()) MenuUtil.displayMenuInvalidity(slot, inventory, item, "New Leader Must Be Online");
				else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(FactionUtil.getPlayerFaction(nLP) != faction) MenuUtil.displayMenuInvalidity(slot, inventory, item, "New Leader Not In Faction");
					else Controller.setMemberLeader(player, nLP);
				}
			}else if(slot > 4 && slot < 9) player.closeInventory();
			break;
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event){
		Player player = (Player)event.getPlayer();
		if(!MenuHelper.isViewing(player)) return;
		else{
			MenuType menu = MenuHelper.getCurrentlyViewing(player);
			if(menu.isAnvil()){
				player.setLevel(player.getLevel() - 1);
				event.getInventory().setItem(0, null); 
				event.getInventory().setItem(1, null); 
				event.getInventory().setItem(2, null);
			}
			switch(menu){
			case CREATION_ICON:
				IconSelectionContainer.removeViewingPage(player);
				MenuHelper.removeCreationData(player);
				break;
				
			case CREATION_SOUND:
				MenuHelper.removeCreationData(player);
				break;
				
			case MEMBERS:
				MenuHelper.removeMembersViewing(player);
				break;
				
			case RANKS_EDITING:
			case RANKS_EDITING_COINS:
			case RANKS_EDITING_ITEMS:
			case RANKS_EDITING_THRESHOLD:
			case RANKS_EDITING_NAME:
			case RANKS_REMOVE_CONFIRMATION:
				MenuHelper.removeEditingRank(player);
				break;
				
			case VAULT_EDIT:
			case VAULT_EDIT_NAME:
				MenuHelper.removeEditingTab(player);
				break;
				
			case VAULT_EDIT_ICON:
				MenuHelper.removeEditingTab(player);
				IconSelectionContainer.removeViewingPage(player);
				break;
				
			case VAULT:
				HandleVaultClick.handleClose(player);
				HandleVaultClick.removeClick(player);
				break;
				
			case CLAIMED_LAND:
				MenuHelper.removeVieiwngLand(player);
				MenuHelper.removeViewingPage(player);
				break;
				
			case EDIT_ICON:
				IconSelectionContainer.removeViewingPage(player);
				break;
			}
			MenuHelper.removeViewing(player);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event){
		Player player = (Player)event.getWhoClicked();
		if(!MenuHelper.isViewing(player) || (!MenuHelper.getCurrentlyViewing(player).isFullCancel() && MenuHelper.getCurrentlyViewing(player) != MenuType.VAULT)) return;
		if(MenuHelper.getCurrentlyViewing(player) == MenuType.VAULT){
			HandleVaultClick.handleDrag(player, event);
		}else{
			Inventory inventory = event.getInventory();
			for(int slot : event.getRawSlots()){
				if(slot < inventory.getSize()){
					event.setCancelled(true);
					break;
				}
			}
		}
	}
}
