package me.recolance.factions.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Material;

import me.recolance.factions.faction.ClaimedLand;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.faction.Member;
import me.recolance.globalutil.utils.Item;
import me.recolance.npcapi.npc.NPCCharacter;

public class DataHolder{

	private static ArrayList<Faction> factions = new ArrayList<Faction>();
	private static HashMap<UUID, Faction> factionIdFactionMap = new HashMap<UUID, Faction>();
	private static HashMap<String, Faction> factionNameFactionMap = new HashMap<String, Faction>();
	private static HashMap<String, Faction> factionBareNameFactionMap = new HashMap<String, Faction>();
	private static ArrayList<Faction> openFactions = new ArrayList<Faction>();
	private static HashMap<UUID, Member> playerIdMemberMap = new HashMap<UUID, Member>();
	private static HashMap<String, ClaimedLand> chunkClaimedLandMap = new HashMap<String, ClaimedLand>();
	
	public static ArrayList<UUID> inFChat = new ArrayList<UUID>();
	
	private static Item xpItem = null;
	
	private static NPCCharacter npc;
	
	
	public static ArrayList<Faction> getAllFactions(){
		return factions;
	}
	public static void setAllFactions(ArrayList<Faction> list){
		factions = list;
	}
	
	public static HashMap<UUID, Faction> getFactionIdFactionMap(){
		return factionIdFactionMap;
	}
	public static void setFactionIdFactionMap(HashMap<UUID, Faction> map){
		factionIdFactionMap = map;
	}
	public static void setFactionIdFactionMap(ArrayList<Faction> list){
		for(Faction faction : list){
			factionIdFactionMap.put(faction.getId(), faction);
		}
	}
	
	public static HashMap<String, Faction> getFactionNameFactionMap(){
		return factionNameFactionMap;
	}
	public static void setFactionNameFactionMap(HashMap<String, Faction> map){
		factionNameFactionMap = map;
	}
	
	public static HashMap<String, Faction> getFactionBareNameFactionMap(){
		return factionBareNameFactionMap;
	}
	public static void setFactionBareNameFactionMap(HashMap<String, Faction> map){
		factionBareNameFactionMap = map;
	}
	
	public static ArrayList<Faction> getOpenFactions(){
		return openFactions;
	}
	public static void setOpenFactions(ArrayList<Faction> list){
		openFactions = list;
	}
	
	public static HashMap<UUID, Member> getPlayerIdMemberMap(){
		return playerIdMemberMap;
	}
	public static void setPlayerIdMemberMap(HashMap<UUID, Member> map){
		playerIdMemberMap = map;
	}
	
	public static HashMap<String, ClaimedLand> getChunkClaimedLandMap(){
		return chunkClaimedLandMap;
	}
	public static void setChunkClaimedLandMap(HashMap<String, ClaimedLand> map){
		chunkClaimedLandMap = map;
	}
	
	public static void clearFactionMemory(Faction faction){
		factions.remove(faction);
		factionIdFactionMap.remove(faction.getId());
		factionNameFactionMap.remove(faction.getName().toLowerCase());
		factionBareNameFactionMap.remove(faction.getBareName().toLowerCase());
		openFactions.remove(faction);
		for(Member member : faction.getMembers()){
			playerIdMemberMap.remove(member.getPlayer());
		}
		for(ClaimedLand land : faction.getClaimedLand()){
			chunkClaimedLandMap.remove(land.getLocation());
		}
	}
	
	public static void loadFactions(List<Faction> factions){
		for(Faction faction : factions){
			loadFaction(faction);
		}
	}
	public static void loadFaction(Faction faction){
		factions.add(faction);
		factionIdFactionMap.put(faction.getId(), faction);
		factionNameFactionMap.put(faction.getName().toLowerCase(), faction);
		factionBareNameFactionMap.put(faction.getBareName().toLowerCase(), faction);
		if(faction.isOpen()) openFactions.add(faction);
		for(Member member : faction.getMembers()){
			playerIdMemberMap.put(member.getPlayer(), member);
		}
		for(ClaimedLand land : faction.getClaimedLand()){
			chunkClaimedLandMap.put(land.getLocation(), land);
		}
		Collections.sort(factions, (f1, f2) -> ((Long)f1.getScore()).compareTo(f2.getScore()));
	}
	
	private static HashMap<Chunk, String> alreadySerializedChunks = new HashMap<Chunk, String>();
	
	
	public static HashMap<Chunk, String> getAlreadySerializedChunks(){
		return alreadySerializedChunks;
	}
	
	public static void setAlreadySerializedChunks(HashMap<Chunk, String> serializedChunks){
		alreadySerializedChunks = serializedChunks;
	}
	
	public static void setNPC(NPCCharacter npcCharacter){
		npc = npcCharacter;
	}
	public static NPCCharacter getNPC(){
		return npc;
	}
	
	public static void setXPItem(){
		Item item = new Item().setType(Material.EYE_OF_ENDER).setName("&62500 Faction XP")
			.addLore("&aClick while holding this item to", "&agrant your faction 2500XP.");
		xpItem = item;
	}
	
	public static Item getXPItem(){
		return xpItem;
	}
}
