package me.recolance.factions.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import net.minecraft.server.v1_8_R2.NBTTagCompound;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

@SuppressWarnings("deprecation")
public class Util{

	public static void broadcastMessage(String message){
		Bukkit.broadcastMessage(setStringColors(message));
	}	
	
	public static String setStringColors(String string){
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public static String stripStringColors(String string){
		return ChatColor.stripColor(string);
	}
	
	public static boolean stringHasMin(String string, int min){
		if(string.length() >= min) return true;
		return false;
	}
	public static boolean stringHasMinWithoutColor(String string, int min){
		return stringHasMin(Util.stripStringColors(string), 2);
	}
	public static boolean stringHasMax(String string, int max){
		if(string.length() <= max) return true;
		return false;
	}
	public static boolean stringHasMaxWithoutColor(String string, int max){
		return stringHasMax(stripStringColors(string), max);
	}
	
	public static boolean stringHasMagic(String string){
		string = setStringColors(string);
		if(string.contains("§k")) return true;
		return false;
	}
	
	public static int getStringColors(String string){
		string = setStringColors(string);
		int colors = 0;
		for(char character : string.toCharArray()){
			if(character == '§') colors++; 
		}
		return colors;
	}
	
	public static boolean stringIsAppropriate(String string){
		String[] words = {"anal", "anus", "bastard", "bitch", "boner", "buttplug", "cock", "cunt", "dick", "dildo", "fag", 
				 "fuck", "jizz", "nigger", "nigga", "penis", "pussy", "scrotum", "shit", "slut", "vagina", "whore",
				 "rape", "nazi"};
		for(String word : words){
			if(string.toLowerCase().contains(word)) return false;
		}
		return true;
	}
	
	public static boolean stringIsAlphabetical(String string){
		Pattern pattern = Pattern.compile("[^A-Za-z]");
		return pattern.matcher(string).find() ? false : true;
	}
	
	public static boolean stringIsAlphanumerical(String string){
		Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
		return pattern.matcher(string).find() ? false : true;
	}
	
	public static boolean stringIsNumerical(String string){
		Pattern pattern = Pattern.compile("[^0-9]");
		return pattern.matcher(string).find() ? false : true;
	}
	
	public static void setName(ItemStack item, String name){
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(setStringColors(name));
		item.setItemMeta(meta);
	}
	
	public static void addLore(ItemStack item, String... lore){
		ItemMeta meta = item.getItemMeta();
		List<String> loreList = new ArrayList<String>();
		if(meta.hasLore()) loreList.addAll(meta.getLore());
		for(String loreLine : lore){
			loreList.add(setStringColors(loreLine));
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
	}
	
	public static ItemStack removeAttributes(ItemStack item){
		net.minecraft.server.v1_8_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = stack.getTag();
		if(compound == null){
			compound = new NBTTagCompound();
			stack.setTag(compound);
			compound = stack.getTag();
		}
		compound.setInt("HideFlags", 63);
		stack.setTag(compound);
		return CraftItemStack.asCraftMirror(stack);
	}
	
	public static String getAnvilName(ItemStack item){
		if(item.getItemMeta().hasDisplayName()) return setStringColors(item.getItemMeta().getDisplayName());
		else return null;
	}
	
	public static String getItemName(ItemStack item){
		if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) return item.getItemMeta().getDisplayName();
		else{
			String name = item.getType().name();
			name = name.replace('_', ' ');
			name = name.toLowerCase();
			StringBuilder sb = new StringBuilder(name);
			if(sb.length() > 0) sb.setCharAt(0, Character.toUpperCase(name.charAt(0)));
			int i = 0;
			for(char character : name.toCharArray()){
				if(character == ' '){
					sb.setCharAt(i + 1, Character.toUpperCase(sb.charAt(i + 1)));
				}
				i++;
			}
			return sb.toString();
		}
	}
	
	public static void message(Player player, String message){
		if(player == null) return;
		player.sendMessage(setStringColors(message));
	}
	
	public static void consoleMessage(String message){
		Bukkit.getServer().getConsoleSender().sendMessage(setStringColors(message));
	}
	
	public static String getMonthDayDate(){
		return getMonthAbbr() + " " + getDay();
	}
	public static String getMonthDayYearDate(long time){
		Date date = new Date(time);
		return getMonthAbbr(date.getMonth()) + " " + date.getDate() + " " + (date.getYear() + 1900);
	}
	public static String getMonthAbbr(){
		return getMonthAbbr(new Date().getMonth());
	}
	public static String getMonthAbbr(int month){
		switch(month){
		case 0:
			return "Jan";
		case 1:
			return "Feb";
		case 2:
			return "Mar";
		case 3:
			return "Apr";
		case 4:
			return "May";
		case 5:
			return "Jun";
		case 6:
			return "Jul";
		case 7:
			return "Aug";
		case 8:
			return "Sep";
		case 9:
			return "Oct";
		case 10:
			return "Nov";
		case 11:
			return "Dec";
		default:
			return "Unk";
		}
	}
	public static String getDay(){
		return String.valueOf(new Date().getDate());
	}
	
	public static String getRemainingTimeString(long startTime, long lengthOfTime){
		return timeToDHMSString((startTime - System.currentTimeMillis()) + lengthOfTime);
	}
	
	public static String timeToDHMSString(long time){
    	int second = 1000; int minute = second * 60; int hour = minute * 60; int day = hour * 24;
    	long playTimeManipulator = time;
    	StringBuilder timeString = new StringBuilder();
    	if(playTimeManipulator >= day){
    		long finalDay = (playTimeManipulator / day); 
    		playTimeManipulator = (playTimeManipulator - (finalDay * day));
    		timeString.append(finalDay + (finalDay > 1 ? "d " : "d "));
    	}
    	if(playTimeManipulator >= hour){
    		long finalHour = (playTimeManipulator / hour); 
    		playTimeManipulator = (playTimeManipulator - (finalHour * hour));
    		timeString.append(finalHour + (finalHour > 1 ? "h " : "h "));
    	}
    	if(playTimeManipulator >= minute){
    		long finalMinute = (playTimeManipulator / minute); 
    		playTimeManipulator = (playTimeManipulator - (finalMinute * minute));
    		timeString.append(finalMinute + (finalMinute > 1 ? "m " : "m "));
    	}
    	if(playTimeManipulator >= second){
    		long finalSecond = (playTimeManipulator / second); 
    		playTimeManipulator = (playTimeManipulator - (finalSecond * second));
    		timeString.append(finalSecond + (finalSecond > 1 ? "s" : "s"));
    	}
    	return timeString.toString();
	}
	
	public static String timeToLastOnlineString(long lastOnline){
		long currentTime = System.currentTimeMillis();
		String lastOnlineTimeString = "";
		if((currentTime - 60000) < lastOnline){
			long seconds = (currentTime - lastOnline) / 1000;
			lastOnlineTimeString = String.valueOf(seconds) + String.valueOf((seconds > 1) ? " Seconds" : " Second");
		}else if((currentTime - 3600000) < lastOnline){
			long minutes = (currentTime - lastOnline) / 60000;
			lastOnlineTimeString = String.valueOf(minutes) + String.valueOf((minutes > 1) ? " Minutes" : " Minute");
		}else if((currentTime - 86400000) < lastOnline){
			long hours = (currentTime - lastOnline) / 3600000;
			lastOnlineTimeString = String.valueOf(hours) + String.valueOf((hours > 1) ? " Hours" : " Hour");
		}else{
			long days = (currentTime - lastOnline) / 86400000;
			lastOnlineTimeString = String.valueOf(days) + String.valueOf((days > 1) ? " Days" : " Day");
		}
		return lastOnlineTimeString;
	}
	
	public static WorldGuardPlugin worldGuard(){
		return WorldGuardPlugin.inst();
	}
	
	public static String comma(int number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
	public static String comma(long number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
	public static String comma(double number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
	
	public static boolean isOre(Block block){
		switch(block.getType()){
		case COAL_ORE:
		case IRON_ORE:
		case QUARTZ_ORE:
		case REDSTONE_ORE:
		case GOLD_ORE:
		case LAPIS_ORE:
		case EMERALD_ORE:
		case DIAMOND_ORE:
			return true;
		default: return false;
		}
	}
	
	public static boolean isContainer(Block block){
		switch( block.getType()){
		case DISPENSER:
		case DROPPER:
		case CHEST:
		case FURNACE:
		case BEACON:
		case TRAPPED_CHEST:
		case HOPPER:
			return true;
		default: return false;
		}
	}
	
	public static boolean isCointainerDamageableEntity(Entity entity){
		switch(entity.getType()){
		case ITEM_FRAME:
		case ARMOR_STAND:
			return true;
		default: return false;
		}
	}
	
	public static boolean isContainerEntity(Entity entity){
		switch(entity.getType()){
		case MINECART_CHEST:
		case MINECART_HOPPER:
		case ARMOR_STAND:
			return true;
		default: return false;
		}
	}
	
	public static boolean isHanging(Entity entity){
		switch(entity.getType()){
		case ITEM_FRAME:
		case PAINTING:
			return true;
		default: return false;
		}
	}
	
	public static boolean isHangingContainer(Entity entity){
		switch(entity.getType()){
		case ITEM_FRAME:
			return true;
		default: return false;
		}
	}
	
	public static boolean isVehicleContainer(Entity entity){
		switch(entity.getType()){
		case MINECART_CHEST:
		case MINECART_FURNACE:
		case MINECART_HOPPER:
			return true;
		default: return false;
		}
	}
	
	public static boolean isPlaceableEntity(ItemStack item){
		switch(item.getType()){
		case BOAT:
		case ITEM_FRAME:
		case PAINTING:
		case ARMOR_STAND:
		case MINECART:
		case STORAGE_MINECART:
		case POWERED_MINECART:
		case HOPPER_MINECART:
		case EXPLOSIVE_MINECART:
		case DIODE:
		case REDSTONE_COMPARATOR:
			return true;
		default: return false;
		}
	}
	
	public static Location getBorderClickedLocation(BlockFace face, Location loc){
		switch(face){
		case SOUTH: return loc.add(0,0,1);
		case WEST: return loc.add(-1,0,0);
		case NORTH: return loc.add(0,0,-1);
		case EAST: return loc.add(1,0,0);
		default: return loc;
		}
	}
	
	public static boolean isMechanical(Block block){
		switch(block.getType()){
		case LEVER:
		case FENCE_GATE:
		case STONE_BUTTON:
		case WOOD_BUTTON:
		case DIODE:
		case REDSTONE_COMPARATOR:
		case TRAP_DOOR:
		case IRON_TRAPDOOR:
		case WOODEN_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case DARK_OAK_DOOR:
		case SPRUCE_FENCE_GATE:
		case BIRCH_FENCE_GATE:
		case JUNGLE_FENCE_GATE:
		case DARK_OAK_FENCE_GATE:
		case ACACIA_FENCE_GATE:
			return true;
		default: return false;
		}
	}
	
	public static String getDirection(Player player){
		final int bearing = (int)(player.getLocation().getYaw() + 180 + 360) % 360;
		if(bearing < 23)return "North";
		else if (bearing < 68)return "North East";
		else if (bearing < 113)return "East";
		else if (bearing < 158)return "South East";
		else if (bearing < 203)return "South";
		else if (bearing < 248)return "South West";
		else if (bearing < 293)return "West";
		else if (bearing < 338)return "North West";
		else return "North";
	}
}
