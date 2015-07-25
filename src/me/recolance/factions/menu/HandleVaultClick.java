package me.recolance.factions.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.recolance.factions.faction.Member;
import me.recolance.factions.faction.RankPermission;
import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;
import me.recolance.globalutil.utils.HoverMessage;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class HandleVaultClick{

	private static HashMap<Player, Integer> itemClick = new HashMap<Player, Integer>();
	private static HashMap<Player, ItemStack> onCursor = new HashMap<Player, ItemStack>();
	
	@SuppressWarnings("incomplete-switch")
	public static void handleClick(Player player, int slot, ClickType click, ItemStack item, ItemStack cursor, Inventory inventory, InventoryClickEvent event){
		Member member = FactionUtil.getPlayerMember(player);
		Material mat = item.getType();
		if(slot > 54 && (mat == Material.MOB_SPAWNER || mat == Material.MONSTER_EGG || (mat == Material.GOLDEN_APPLE && item.getDurability() == 1))){
			Util.message(player, "&cYou cannot place this item in a vault.");
			event.setCancelled(true);
			return;
		}
		if(!member.getRank().hasPermission(RankPermission.VAULT_DEPOSIT) && (!isInVault(slot) && !isInVault(getClick(player)))){
			System.out.println(true);
			Util.message(player, "&cYour faction rank does not allow you to do this.");
			event.setCancelled(true);
			return;
		}else if(isInVault(slot) && member.getVaultItemsTaken() >= member.getRank().getVaultItemsLimit()){
			if((!hasClick(player) || !isInInventory(getClick(player))) || ((hasClick(player) && isInInventory(getClick(player)) && !isAir(item)))){
				Util.message(player, "&cYou can not remove any more items from the vault today.");
				event.setCancelled(true);
				return;
			}
		}
		
		if(!hasClick(player) && !isAir(item)){
			if(isInVault(slot)) setClick(player, slot, item);
			else setClick(player, slot);
		}
		
		switch(click){
		
		case DOUBLE_CLICK:
			event.setCancelled(true);
			return;
			
			
		case DROP:
			if(isAir(item)) removeClick(player);
			else if(isInVault(slot) && isAir(cursor)){
				removeClick(player);
				itemRemoved(player, item, 1, member);
			}
			break;
		
		case CONTROL_DROP:
			if(isAir(item))	removeClick(player);
			else if(isInVault(slot) && isAir(cursor)){
				removeClick(player);
				itemRemoved(player, item, member);
			}
			break;
		
		case NUMBER_KEY:
			ItemStack hotItem = player.getInventory().getItem(event.getHotbarButton());
			if(isInVault(slot)){
				if(isAir(item)){
					if(isAir(hotItem)) removeClick(player);
					else{
						removeClick(player);
						itemPlaced(player, hotItem, event);
					}
				}else{
					if(isPlayerInventoryFull(player)) removeClick(player);
					else{
						removeClick(player);
						itemRemoved(player, item, member);
					}
				}
			}
			break;
		
		case SHIFT_LEFT:
		case SHIFT_RIGHT:
			if(isAir(item)){
				removeClick(player);
				return;
			}
			if(isInVault(slot)){
				int availableStack = 0;
				for(int itemSlot : isPlayerInventoryFull(player.getInventory(), item)){
					if(isAir(player.getInventory().getItem(itemSlot))){
						if(isAir(cursor)) removeClick(player);
						itemRemoved(player, item, member);
						return;
					}else availableStack = availableStack + (item.getMaxStackSize() - player.getInventory().getItem(itemSlot).getAmount());
				}
				if(availableStack != 0){
					if(availableStack > item.getAmount()){
						if(isAir(cursor)) removeClick(player);
						itemRemoved(player, item, member);
					}else{
						if(isAir(cursor)) removeClick(player);
						itemRemoved(player, item, item.getAmount() - (item.getAmount() - availableStack), member);
					}
				}
			}else if(isInInventory(slot)){
				int availableStack = 0;
				for(int itemSlot : isVaultFull(inventory, item)){
					if(isAir(inventory.getItem(itemSlot))){
						if(isAir(cursor)) removeClick(player);
						itemPlaced(player, item, event);
						return;
					}else availableStack = availableStack + (item.getMaxStackSize() - inventory.getItem(itemSlot).getAmount());
				}
				if(availableStack != 0){
					if(availableStack > item.getAmount()){
						if(isAir(cursor)) removeClick(player);
						itemPlaced(player, item, event);
					}else{
						if(isAir(cursor)) removeClick(player);
						itemPlaced(player, item, item.getAmount() - (item.getAmount() - availableStack), event);
					}
				}
			}
			break;
		
		case LEFT:
			if(isOutOfInventory(slot) && hasClick(player) && isInVault(getClick(player))){
				if(isAir(cursor)) removeClick(player);
				else{
					removeClick(player);
					itemRemoved(player, cursor, member);
				}
			}
			else if(isOutOfInventory(slot) && hasClick(player) && isInInventory(getClick(player))) removeClick(player);
			else if(isInInventory(slot) && hasClick(player) && isInInventory(getClick(player))){
				if(isAir(item)) removeClick(player);
				else if(!item.isSimilar(cursor)) setClick(player, slot);
				else if(item.getAmount() + cursor.getAmount() <= item.getMaxStackSize()) removeClick(player);
				else setClick(player, slot);
			}else if(isInVault(slot) && hasClick(player) && isInVault(getClick(player))){
				if(isAir(item)) removeClick(player);
				else if(!item.isSimilar(cursor)) setClick(player, slot, item);
				else if(item.getAmount() + cursor.getAmount() <= item.getMaxStackSize()) removeClick(player);
				else setClick(player, slot, cursor);
			}else if(isInInventory(slot) && hasClick(player) && isInVault(getClick(player))){
				if(isAir(item)){
					removeClick(player);
					itemRemoved(player, cursor, member);
				}else if(!item.isSimilar(cursor)){
					itemRemoved(player, cursor, member);
					setClick(player, slot);
				}else if(item.getAmount() + cursor.getAmount() <= item.getMaxStackSize()){
					removeClick(player);
					itemRemoved(player, cursor, member);
				}else if(item.getMaxStackSize() != item.getAmount()) itemRemoved(player, cursor, item.getMaxStackSize() - item.getAmount(), member);
			}else if(isInVault(slot) && hasClick(player) && isInInventory(getClick(player))){
				if(isAir(item)){
					removeClick(player);
					itemPlaced(player, cursor, event);
				}else if(!item.isSimilar(cursor)){
					itemPlaced(player, cursor, event);
					setClick(player, slot, item);
				}else if(item.getAmount() + cursor.getAmount() <= item.getMaxStackSize()){
					removeClick(player);
					itemPlaced(player, cursor, event);
				}else if(item.getMaxStackSize() != item.getAmount()) itemPlaced(player, cursor, item.getMaxStackSize() - item.getAmount(), event);
			}
			break;
		
		case RIGHT:
			if(isOutOfInventory(slot) && hasClick(player) && isInVault(getClick(player))){
				if(isAir(cursor)) removeClick(player);
				else{
					itemRemoved(player, cursor, 1, member);
					if(cursor.getAmount() == 1) removeClick(player);
				}
			}else if(isOutOfInventory(slot) && hasClick(player) && isInInventory(getClick(player))){
				if(!isAir(cursor) && cursor.getAmount() == 1) removeClick(player);
				else if(isAir(cursor)) removeClick(player);
			}else if(isInInventory(slot) && hasClick(player) && isInInventory(getClick(player))){
				if(isAir(item) && cursor.getAmount() == 1) removeClick(player);
				else if(isAir(item) && cursor.getAmount() != 1) setClick(player, getClick(player));
				else if(!item.isSimilar(cursor)) setClick(player, slot);
				else if(cursor.getAmount() == 1 && item.getAmount() != item.getMaxStackSize()) removeClick(player);
				else if(cursor.getAmount() == 1 && (item.getAmount() + cursor.getAmount() == item.getMaxStackSize())) removeClick(player);
			}else if(isInVault(slot) && hasClick(player) && isInVault(getClick(player))){
				if(isAir(item) && cursor.getAmount() == 1) removeClick(player);
				else if(isAir(item) && cursor.getAmount() != 1) setClick(player, getClick(player), cursor);
				else if(!item.isSimilar(cursor)) setClick(player, slot, item);
				else if(cursor.getAmount() == 1 && item.getAmount() != item.getMaxStackSize()) removeClick(player);
				else if(cursor.getAmount() == 1 && (item.getAmount() + cursor.getAmount() == item.getMaxStackSize())) removeClick(player);
			}else if(isInInventory(slot) && hasClick(player) && isInVault(getClick(player))){
				if(isAir(item) && cursor.getAmount() == 1){
					removeClick(player);
					itemRemoved(player, cursor, member);
				}else if(isAir(item) && cursor.getAmount() != 1){
					itemRemoved(player, cursor, 1, member);
					setClick(player, getClick(player));
				}else if(!item.isSimilar(cursor)){
					itemRemoved(player, cursor, member);
					setClick(player, slot);
				}else if(cursor.getAmount() != 1 && item.getAmount() != item.getMaxStackSize()){
					itemRemoved(player, cursor, 1, member);
					setClick(player, getClick(player));
				}else if(cursor.getAmount() == 1 && item.getAmount() != item.getMaxStackSize()){
					removeClick(player);
					itemRemoved(player, cursor, member);
				}
			}else if(isInVault(slot) && hasClick(player) && isInInventory(getClick(player))){
				if(isAir(item) && cursor.getAmount() == 1){
					removeClick(player);
					itemPlaced(player, cursor, event);
				}else if(isAir(item) && cursor.getAmount() != 1){
					itemPlaced(player, cursor, 1, event);
					setClick(player, getClick(player));
				}else if(!item.isSimilar(cursor)){
					itemPlaced(player, cursor, event);
					setClick(player, slot, item);
				}else if(cursor.getAmount() != 1 && item.getAmount() != item.getMaxStackSize()){
					itemPlaced(player, cursor, 1, event);
					setClick(player, getClick(player), cursor);
				}else if(cursor.getAmount() == 1 && item.getAmount() != item.getMaxStackSize()){
					removeClick(player);
					itemPlaced(player, cursor, event);
				}
			}
			break;
		}
	}
	
	public static void handleDrag(Player player, InventoryDragEvent event){
		if(hasClick(player) && isInVault(getClick(player))){
			Member member = FactionUtil.getPlayerMember(player);
			if(member.getVaultItemsTaken() + 1 == member.getRank().getVaultItemsLimit()){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot perform item drag withdrawls with only one item left to withdrawl today. This prevents stealing.");
				return;
			}
			int amountRemoved = 0;
			InventoryView inventory = event.getView();
			Map<Integer, ItemStack> draggedItems = event.getNewItems();
			for(int slot : draggedItems.keySet()){
				if(isInInventory(slot)){
					if(!isAir(inventory.getItem(slot))) amountRemoved = amountRemoved + (draggedItems.get(slot).getAmount() - inventory.getItem(slot).getAmount());
					else amountRemoved = amountRemoved + draggedItems.get(slot).getAmount();
				}
			}
			if(amountRemoved != 0){
				if(isAir(event.getCursor())) removeClick(player);
				itemRemoved(player, event.getOldCursor(), amountRemoved, member);
			}
		}
		
		else if(hasClick(player) && isInInventory(getClick(player))){
			int amountPlaced = 0;
			InventoryView inventory = event.getView();
			Map<Integer, ItemStack> draggedItems = event.getNewItems();
			for(int slot : draggedItems.keySet()){
				if(isInVault(slot)){
					if(!isAir(inventory.getItem(slot))) amountPlaced = amountPlaced + (draggedItems.get(slot).getAmount() - inventory.getItem(slot).getAmount());
					else amountPlaced = amountPlaced + draggedItems.get(slot).getAmount();
				}
			}
			if(amountPlaced != 0){
				if(isAir(event.getCursor())) removeClick(player);
				itemPlaced(player, event.getOldCursor(), amountPlaced, event);
			}
		}
	}
	
	public static void handleClose(Player player){
		if(hasCursor(player)) itemRemoved(player, getCursor(player), FactionUtil.getPlayerMember(player));
	}

	public static void itemRemoved(Player player, ItemStack item, Member member){
		itemRemoved(player, item, item.getAmount(), member);
	}
	public static void itemPlaced(Player player, ItemStack item, InventoryClickEvent event){
		itemPlaced(player, item, item.getAmount(), event);
	}
	public static void itemPlaced(Player player, ItemStack item, InventoryDragEvent event){
		itemPlaced(player, item, item.getAmount(), event);
	}
	public static void itemRemoved(Player player, ItemStack item, int amount, Member member){
		int vaultItems = member.getVaultItemsTaken();
		member.setVaultItemsTaken(vaultItems + 1);
		vaultItems++;
		String itemName = Util.getItemName(item);
		new HoverMessage().messageTip("&6[Vault Item Withdrawl]", "&6Vault Item Withdrawl", "&9Item: &e" + itemName, "&9Amount: &e" + amount, "&9Limit Today: &e" + vaultItems + "/" + member.getRank().getVaultItemsLimit()).send(player);
		FactionUtil.getPlayerFaction(player).getVault().addLogEntry("&e" + player.getName() + " withdrew " + itemName + " x" + amount + ".");
	}
	public static void itemPlaced(Player player, ItemStack item, int amount, InventoryClickEvent event){
		if(!FactionUtil.hasPermission(player, RankPermission.VAULT_DEPOSIT)){
			Util.message(player, "&cYour faction rank does not allow you to do this.");
			event.setCancelled(true);
		}else{
			String itemName = Util.getItemName(item);
			new HoverMessage().messageTip("&6[Vault Item Deposit]", "&6Vault Item Deposit", "&9Item: &e" + itemName, "&9Amount: &e" + amount).send(player);
			FactionUtil.getPlayerFaction(player).getVault().addLogEntry("&e" + player.getName() + " deposited " + itemName + " x" + amount + ".");
		}
	}
	public static void itemPlaced(Player player, ItemStack item, int amount, InventoryDragEvent event){
		if(!FactionUtil.hasPermission(player, RankPermission.VAULT_DEPOSIT)){
			Util.message(player, "&cYour faction rank does not allow you to do this.");
			event.setCancelled(true);
		}else{
			String itemName = Util.getItemName(item);
			new HoverMessage().messageTip("&6[Vault Item Deposit]", "&6Vault Item Deposit", "&9Item: &e" + itemName, "&9Amount: &e" + amount).send(player);
			FactionUtil.getPlayerFaction(player).getVault().addLogEntry("&e" + player.getName() + " deposited " + itemName + " x" + amount + ".");
		}
	}
	
	
	public static void setClick(Player player, int slot, ItemStack item){
		itemClick.put(player, slot);
		setCursor(player, item);
	}
	public static void setClick(Player player, int slot){
		itemClick.put(player, slot);
	}
	public static void removeClick(Player player){
		itemClick.remove(player);
		onCursor.remove(player);
	}
	public static int getClick(Player player){
		if(hasClick(player)) return itemClick.get(player);
		else return Integer.MAX_VALUE;
	}
	public static boolean hasClick(Player player){
		if(itemClick.containsKey(player)) return true;
		return false;
	}
	public static void setCursor(Player player, ItemStack item){
		onCursor.put(player, item);
	}
	public static void removeCursor(Player player){
		onCursor.remove(player);
	}
	public static ItemStack getCursor(Player player){
		if(hasCursor(player)) return onCursor.get(player);
		return null;
	}
	public static boolean hasCursor(Player player){
		if(onCursor.containsKey(player)) return true;
		return false;
	}
	public static boolean isInVault(int slot){
		if(slot < 45 && slot != -999) return true;
		return false;
	}
	public static boolean isInInventory(int slot){
		if(slot > 53 && slot < 999) return true;
		return false;
	}
	public static boolean isOutOfInventory(int slot){
		if(slot == -999) return true;
		return false;
	}
	public static boolean isAir(ItemStack item){
		if(item == null || item.getType() == Material.AIR) return true;
		return false;
	}
	public static boolean isVaultFull(Inventory inventory){
		for(int i = 0; i < 45; i++){
			ItemStack invItem = inventory.getItem(i);
			if(invItem == null || invItem.getType() == Material.AIR) return false;
		}
		return true;
	}
	public static List<Integer> isVaultFull(Inventory inventory, ItemStack item){
		List<Integer> slotsAvailable = new ArrayList<Integer>();
		for(int i = 0; i < 45; i++){
			ItemStack invItem = inventory.getItem(i);
			if(isAir(invItem)){
				slotsAvailable.add(i);
				return slotsAvailable;
			}
			if(invItem.isSimilar(item) && invItem.getAmount() < invItem.getMaxStackSize()){
				slotsAvailable.add(i);
			}
		}
		return slotsAvailable;
	}
	public static boolean isPlayerInventoryFull(Player player){
		for(int i = 0; i < 36; i++){
			ItemStack invItem = player.getInventory().getItem(i);
			if(invItem == null || invItem.getType() == Material.AIR) return false;
		}
		return true;
	}
	public static List<Integer> isPlayerInventoryFull(Inventory inventory, ItemStack item){
		List<Integer> slotsAvailable = new ArrayList<Integer>();
		for(int i = 0; i < 36; i++){
			ItemStack invItem = inventory.getItem(i);
			if(isAir(invItem)){
				slotsAvailable.add(i);
				return slotsAvailable;
			}
			if(invItem.isSimilar(item) && invItem.getAmount() < invItem.getMaxStackSize()){
				slotsAvailable.add(i);
			}
		}
		return slotsAvailable;
	}
}
