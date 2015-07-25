package me.recolance.factions.menu;

import java.util.List;

import me.recolance.factions.Factions;
import me.recolance.factions.faction.Rank;
import me.recolance.factions.faction.RankPermission;
import me.recolance.factions.util.Util;
import me.recolance.globalutil.utils.MenuButton;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuUtil{

	private static Plugin plugin = Factions.plugin;
	
	public static boolean isCancellableClick(Inventory inventory, int slot, ClickType click){
		if(slot < inventory.getSize()) return true;
		if(click != ClickType.LEFT && click != ClickType.RIGHT) return true;
		return false;
	}
	
	public static void displayMenuInvalidity(final int slot, final Inventory inventory, final ItemStack originalItem, String message){
		message = Util.setStringColors(message);
		if(originalItem.getType() == Material.BARRIER) return;
		inventory.setItem(slot, new MenuButton().type(Material.BARRIER).name("&c" + message).get());
		new BukkitRunnable(){
			@Override
			public void run(){
				inventory.setItem(slot, originalItem);
			}
		}.runTaskLater(plugin, 20L);
	}
	public static void displayAnvilInvalidity(Inventory inventory, String message){
		displayMenuInvalidity(0, inventory, inventory.getItem(0), message);
	}
	
	public static String getPermissionStatusString(Rank rank, RankPermission permission){
		return rank.hasPermission(permission) ? "Allowed" : "Denied";
	}
	
	public static void handleSwapPermissionLore(int slot, Inventory inventory, ItemStack item){
		ItemMeta meta = item.getItemMeta();
		List<String> itemLore = meta.getLore();
		int line = 0;
		for(String string : itemLore){
			if(string.contains(ChatColor.BLUE + "Permission: " + ChatColor.YELLOW + "Allowed")){
				itemLore.set(line, ChatColor.BLUE + "Permission: " + ChatColor.YELLOW + "Denied");
				break;
			}
			if(string.contains(ChatColor.BLUE + "Permission: " + ChatColor.YELLOW + "Denied")){
				itemLore.set(line, ChatColor.BLUE + "Permission: " + ChatColor.YELLOW + "Allowed");
				break;
			}
			line++;
		}
		meta.setLore(itemLore);
		item.setItemMeta(meta);
		inventory.setItem(slot, item);
	}
	
	public static void swapLore(int slot, Inventory inventory, ItemStack item, String from, String to){
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		int line = 0;
		for(String string : lore){
			if(string.contains(from)){
				lore.set(line, to);
				break;
			}
			line++;
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		inventory.setItem(slot, item);
	}
}
