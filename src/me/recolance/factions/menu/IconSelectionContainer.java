package me.recolance.factions.menu;

import java.util.ArrayList;
import java.util.HashMap;

import me.recolance.factions.util.Util;
import me.recolance.globalutil.utils.MenuButton;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class IconSelectionContainer{

	private static ArrayList<ItemStack> buttons = new ArrayList<ItemStack>();
	private static ArrayList<Material> excluded = new ArrayList<Material>();
	private static HashMap<Material, Short[]> durabilities = new HashMap<Material, Short[]>();
	
	private static HashMap<Player, IconPage> currentPage = new HashMap<Player, IconPage>();
	
	public static void createPage(Player player, Inventory inventory, IconPage page, HashMap<ItemStack, Integer> addedButtons, String name, String... lore){
		int displaying = (((buttons.size() - 1) - page.getStartingIndex()) > 45) ? 44 : ((buttons.size() - 1) - page.getStartingIndex());
		int slot = 0;
		for(int i = page.getStartingIndex(); i <= page.getStartingIndex() + displaying; i++){
			ItemStack button = new ItemStack(buttons.get(i));
			Util.setName(button, name);
			Util.addLore(button, lore);
			inventory.setItem(slot, button);
			slot++;
		}
		if(addedButtons != null && !addedButtons.isEmpty()){
			for(ItemStack item : addedButtons.keySet()){
				inventory.setItem(addedButtons.get(item), item);
			}
		}
		ItemStack previousPage = new MenuButton().type(Material.PAPER).name("&6Previous Page").get();
		ItemStack nextPage = new MenuButton().type(Material.PAPER).name("&6Next Page").get();
		inventory.setItem(45, previousPage);
		inventory.setItem(53, nextPage);
		currentPage.put(player, page);
	}
	
	public static void setPage(Player player, Inventory inventory, IconPage page, String name, String... lore){
		for(int i = 0; i < 45; i++){
			inventory.setItem(i, null);
		}
		int displaying = (((buttons.size() - 1) - page.getStartingIndex()) > 45) ? 44 : ((buttons.size() - 1) - page.getStartingIndex());
		int slot = 0;
		for(int i = page.getStartingIndex(); i <= page.getStartingIndex() + displaying; i++){
			ItemStack button = new ItemStack(buttons.get(i));
			Util.setName(button, name);
			Util.addLore(button, lore);
			inventory.setItem(slot, button);
			slot++;
		}
		currentPage.put(player, page);
	}
	
	public static IconPage getNextPage(IconPage currentPage){
		if(IconPage.values().length == currentPage.getPageNumber()) return IconPage.PAGE_1;
		else{
			for(IconPage page : IconPage.values()){
				if((currentPage.getPageNumber() + 1) == page.getPageNumber()) return page;
			}
		}
		return null;
	}
	
	public static IconPage getPreviousPage(IconPage currentPage){
		if(currentPage == IconPage.PAGE_1){
			int pages = IconPage.values().length;
			for(IconPage page : IconPage.values()){
				if(page.getPageNumber() == pages) return page;
			}
		}else{
			for(IconPage page : IconPage.values()){
				if(currentPage.getPageNumber() - 1 == page.getPageNumber()) return page;
			}
		}
		return null;
	}
	
	public static boolean isViewingPage(Player player){
		if(currentPage.containsKey(player)) return true;
		else return false;
	}
	
	public static void setViewingPage(Player player, IconPage page){
		currentPage.put(player, page);
	}
	
	public static IconPage getViewingPage(Player player){
		if(!currentPage.containsKey(player)) return null;
		return currentPage.get(player);
	}
	
	public static void removeViewingPage(Player player){
		if(!currentPage.containsKey(player)) return;
		currentPage.remove(player);
	}
	
	public static void loadButtons(){
		excludeMaterials();
		durabilities();
		for(Material material : Material.values()){
			if(excluded.contains(material)) continue;
			ItemStack button = new MenuButton().type(material).durability(0).get();
			buttons.add(button);
			if(durabilities.containsKey(material)){
				for(short durability : durabilities.get(material)){
					ItemStack buttonDur = new MenuButton().type(material).durability(durability).get();
					buttons.add(buttonDur);
				}
			}
		}
	}
	
	private static void excludeMaterials(){
		excluded.add(Material.WATER);
		excluded.add(Material.PORTAL);
		excluded.add(Material.ENDER_PORTAL);
		excluded.add(Material.WOOD_DOUBLE_STEP);
		excluded.add(Material.COCOA);
		excluded.add(Material.STANDING_BANNER);
		excluded.add(Material.WALL_BANNER);
		excluded.add(Material.DAYLIGHT_DETECTOR_INVERTED);
		excluded.add(Material.DOUBLE_STONE_SLAB2);
		excluded.add(Material.SPRUCE_DOOR);
		excluded.add(Material.BIRCH_DOOR);
		excluded.add(Material.JUNGLE_DOOR);
		excluded.add(Material.ACACIA_DOOR);
		excluded.add(Material.DARK_OAK_DOOR);
		excluded.add(Material.STATIONARY_WATER);
		excluded.add(Material.LAVA);
		excluded.add(Material.STATIONARY_LAVA);
		excluded.add(Material.DOUBLE_STEP);
		excluded.add(Material.FIRE);
		excluded.add(Material.AIR);
	    excluded.add(Material.WATER);
	    excluded.add(Material.LAVA);
	    excluded.add(Material.BED_BLOCK);
	    excluded.add(Material.PISTON_EXTENSION);
	    excluded.add(Material.PISTON_MOVING_PIECE);
	    excluded.add(Material.REDSTONE_WIRE);
	    excluded.add(Material.CROPS);
	    excluded.add(Material.SOIL);
	    excluded.add(Material.BURNING_FURNACE);
	    excluded.add(Material.SIGN_POST);
	    excluded.add(Material.WOODEN_DOOR);
	    excluded.add(Material.WALL_SIGN);
	    excluded.add(Material.IRON_DOOR_BLOCK);
	    excluded.add(Material.GLOWING_REDSTONE_ORE);
	    excluded.add(Material.REDSTONE_TORCH_OFF);
	    excluded.add(Material.SUGAR_CANE_BLOCK);
	    excluded.add(Material.CAKE_BLOCK);
	    excluded.add(Material.DIODE_BLOCK_OFF);
	    excluded.add(Material.DIODE_BLOCK_ON);
	    excluded.add(Material.TRAPPED_CHEST);
	    excluded.add(Material.PUMPKIN_STEM);
	    excluded.add(Material.MELON_STEM);
	    excluded.add(Material.NETHER_WARTS);
	    excluded.add(Material.BREWING_STAND);
	    excluded.add(Material.CAULDRON);
	    excluded.add(Material.REDSTONE_LAMP_ON);
	    excluded.add(Material.TRIPWIRE);
	    excluded.add(Material.FLOWER_POT);
	    excluded.add(Material.CARROT);
	    excluded.add(Material.POTATO);
	    excluded.add(Material.SKULL);
	    excluded.add(Material.REDSTONE_COMPARATOR_OFF);
	    excluded.add(Material.REDSTONE_COMPARATOR_ON);
	}
	
	private static void durabilities(){
		durabilities.put(Material.STONE, new Short[]{1,2,3,4,5,6});
		durabilities.put(Material.DIRT, new Short[]{1,2});
		durabilities.put(Material.WOOD, new Short[]{1,2,3,4,5});
		durabilities.put(Material.SAPLING, new Short[]{1,2,3,4,5});
		durabilities.put(Material.LOG, new Short[]{1,2,3});
		durabilities.put(Material.LEAVES, new Short[]{1,2,3});
		durabilities.put(Material.SPONGE, new Short[]{1});
		durabilities.put(Material.SANDSTONE, new Short[]{1,2});
		durabilities.put(Material.LONG_GRASS, new Short[]{1,2});
		durabilities.put(Material.WOOL, new Short[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
		durabilities.put(Material.RED_ROSE, new Short[]{1,2,3,4,5,6,7,8});
		durabilities.put(Material.STEP, new Short[]{1,3,4,5,6,7});
		durabilities.put(Material.STAINED_GLASS, new Short[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
		durabilities.put(Material.SMOOTH_BRICK, new Short[]{1,2,3});
		durabilities.put(Material.WOOD_STEP, new Short[]{1,2,3,4,5});
		durabilities.put(Material.COBBLE_WALL, new Short[]{1});
		durabilities.put(Material.SKULL_ITEM, new Short[]{1,2,3,4});
		durabilities.put(Material.QUARTZ_BLOCK, new Short[]{1,2});
		durabilities.put(Material.STAINED_CLAY, new Short[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
		durabilities.put(Material.STAINED_GLASS_PANE, new Short[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
		durabilities.put(Material.LEAVES_2, new Short[]{1});
		durabilities.put(Material.LOG_2, new Short[]{1});
		durabilities.put(Material.PRISMARINE, new Short[]{1,2});
		durabilities.put(Material.CARPET, new Short[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
		durabilities.put(Material.DOUBLE_PLANT, new Short[]{1,2,3,4,5});
		durabilities.put(Material.RED_SANDSTONE, new Short[]{1,2});
		durabilities.put(Material.COAL, new Short[]{1});
		durabilities.put(Material.GOLDEN_APPLE, new Short[]{1});
		durabilities.put(Material.RAW_FISH, new Short[]{1,2,3});
		durabilities.put(Material.COOKED_FISH, new Short[]{1});
		durabilities.put(Material.INK_SACK, new Short[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
		durabilities.put(Material.BANNER, new Short[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
		durabilities.put(Material.MONSTER_EGG, new Short[]{50,51,52,54,55,56,57,58,59,61,62,65,66,67,68,90,91,92});
		durabilities.put(Material.POTION, new Short[]{8193,8194,8195,8196,8197,8198,8199,8200,8201,8202,8204,8205,8206,16385,16386,16387,16388,16389,16390,16392,16393,16394,16396,16397,16398});
	}
	
	public enum IconPage{
		
		PAGE_1(0, 1),
		PAGE_2(45, 2),
		PAGE_3(90, 3),
		PAGE_4(135, 4),
		PAGE_5(180, 5),
		PAGE_6(225, 6),
		PAGE_7(270, 7),
		PAGE_8(315, 8),
		PAGE_9(360, 9),
		PAGE_10(405, 10),
		PAGE_11(450, 11),
		PAGE_12(495, 12),
		PAGE_13(535, 13);
		
		private int startingIndex;
		private int pageNumber;
		
		IconPage(int startingIndex, int pageNumber){
			this.startingIndex = startingIndex;
			this.pageNumber = pageNumber;
		}
		
		public int getStartingIndex(){
			return this.startingIndex;
		}
		public int getPageNumber(){
			return this.pageNumber;
		}
	}
}
