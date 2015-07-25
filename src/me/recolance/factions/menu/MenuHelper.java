package me.recolance.factions.menu;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.recolance.factions.faction.ClaimedLand;
import me.recolance.factions.faction.Member;
import me.recolance.factions.faction.Rank;
import me.recolance.factions.faction.VaultTab;

import org.bukkit.entity.Player;

public class MenuHelper{

	
	/*
	 * Holds data for the current menu a player is viewing.
	 */
	private static HashMap<UUID, MenuType> menuViewing = new HashMap<UUID, MenuType>();
	
	public static boolean isViewing(Player player){
		if(menuViewing.containsKey(player.getUniqueId())) return true;
		return false;
	}
	
	public static MenuType getCurrentlyViewing(Player player){
		return menuViewing.get(player.getUniqueId());
	}
	
	public static void setCurrentlyViewing(Player player, MenuType menu){
		menuViewing.put(player.getUniqueId(), menu);
	}

	public static void removeViewing(Player player){
		menuViewing.remove(player.getUniqueId());
	}
	
	/*
	 * Holds data for the step by step of creating a faction.
	 */
	private static HashMap<Player, List<Object>> creationData = new HashMap<Player, List<Object>>();
	
	public static boolean isCreating(Player player){
		if(creationData.containsKey(player)) return true;
		return false;
	}
	
	public static List<Object> getCreationData(Player player){
		return creationData.get(player);
	}
	
	public static void setCreationData(Player player, List<Object> data){
		creationData.put(player, data);
	}
	
	public static void removeCreationData(Player player){
		creationData.remove(player);
	}
	
	/*
	 * Holds data for member locations in the menu
	 */
	private static HashMap<Player, List<Member>> membersViewing = new HashMap<Player, List<Member>>();
	
	public static boolean hasMembersViewing(Player player){
		if(membersViewing.containsKey(player)) return true;
		return false;
	}
	
	public static List<Member> getMembersViewing(Player player){
		return membersViewing.get(player);
	}
	
	public static void setMembersViewing(Player player, List<Member> members){
		membersViewing.put(player, members);
	}
	public static void removeMembersViewing(Player player){
		membersViewing.remove(player);
	}
	
	/*
	 * Holds the currently editing rank.
	 */
	private static HashMap<Player, Rank> editingRank = new HashMap<Player, Rank>();
	
	public static boolean isEditingRank(Player player){
		if(editingRank.containsKey(player)) return true;
		return false;
	}
	
	public static Rank getEditingRank(Player player){
		if(editingRank.containsKey(player)) return editingRank.get(player);
		return null;
	}
	
	public static void setEditingRank(Player player, Rank rank){
		editingRank.put(player, rank);
	}
	
	public static void removeEditingRank(Player player){
		editingRank.remove(player);
	}
	
	/*
	 * Holds current editing vault tab
	 */
	private static HashMap<Player, VaultTab> editingTab = new HashMap<Player, VaultTab>();
	
	public static boolean isEditingTab(Player player){
		if(editingTab.containsKey(player)) return true;
		return false;
	}
	
	public static VaultTab getEditingVaultTab(Player player){
		if(editingTab.containsKey(player)) return editingTab.get(player);
		return null;
	}
	
	public static void setEditingVaultTab(Player player, VaultTab tab){
		editingTab.put(player, tab);
	}
	
	public static void removeEditingTab(Player player){
		editingTab.remove(player);
	}
	
	/*
	 * Holds the current land being viewed
	 */
	private static HashMap<Player, List<ClaimedLand>> viewingLand = new HashMap<Player, List<ClaimedLand>>();
	
	public static boolean isViewingLand(Player player){
		if(viewingLand.containsKey(player)) return true;
		return false;
	}
	
	public static List<ClaimedLand> getViewingLand(Player player){
		if(isViewingLand(player)) return viewingLand.get(player);
		return null;
	}
	
	public static void setViewingLand(Player player, List<ClaimedLand> land){
		viewingLand.put(player, land);
	}
	
	public static void removeVieiwngLand(Player player){
		viewingLand.remove(player);
	}
	
	/*
	 * Holds the current viewing land page
	 */
	private static HashMap<Player, Integer> viewingLandPage = new HashMap<Player, Integer>();
	
	public static boolean isViewingPage(Player player){
		if(viewingLandPage.containsKey(player)) return true;
		return false;
	}
	
	public static int getViewingPage(Player player){
		if(isViewingPage(player)) return viewingLandPage.get(player);
		return 0;
	}
	
	public static void setViewingPage(Player player, int page){
		viewingLandPage.put(player, page);
	}
	
	public static void removeViewingPage(Player player){
		viewingLandPage.remove(player);
	}
}
