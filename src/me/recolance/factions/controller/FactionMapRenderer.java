package me.recolance.factions.controller;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.recolance.factions.util.Util;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.util.FactionUtil;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class FactionMapRenderer{

	private static List<UUID> inFMap = new ArrayList<UUID>();
	
	private static ScoreboardManager sM = Bukkit.getScoreboardManager();
	
	public static void setMap(Player player){
		Scoreboard sb = sM.getNewScoreboard();
		Objective o = sb.registerNewObjective("map", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatColor.GREEN + "Faction Map");
		updateMap(player, o);
	}
	
	public static void updateMap(Player player, Objective o){
		Score nS = o.getScore(Util.setStringColors("       &lN"));
		nS.setScore(15);
		Chunk cC = player.getLocation().getChunk();
		World world = cC.getWorld();
		boolean inFaction = FactionUtil.isInFaction(player);
		int sX = cC.getX() - 3;
		int sZ = cC.getZ() - 3;
		int score = 14;
		ChatColor[] iC = {ChatColor.BLACK, ChatColor.BLUE, ChatColor.AQUA, ChatColor.GRAY, ChatColor.GREEN, ChatColor.DARK_BLUE, ChatColor.YELLOW, ChatColor.DARK_AQUA, ChatColor.DARK_GREEN};
		int i = 0;
		for(int z = sZ; z < sZ + 7; z++){
			StringBuilder sB = new StringBuilder();
			for(int x = sX; x < sX + 7; x++){
				System.out.println();
				Chunk lC = world.getChunkAt(x, z);
				if(lC == cC) sB.append("&b█");
				else if(FactionUtil.isChunkClaimed(lC)){
					if(inFaction){
						Faction lF = FactionUtil.getChunkFaction(lC);
						Faction pF = FactionUtil.getPlayerFaction(player);
						if(lF == pF) sB.append("&6█");
						else if(lF.hasEnemy(pF) || lF.isEnemiedBy(pF)) sB.append("&c█");
						else if(lF.hasAlliance(pF)) sB.append("&a█");
						else sB.append("&e█");
					}else sB.append("&e█");
				}else sB.append("&7█");
			}
			Score sc = o.getScore(Util.setStringColors(sB.toString() + iC[i]));
			i++;
			sc.setScore(score);
			score--;
		}
		//Score bS = o.getScore(Util.setStringColors("       &lS")); bS.setScore(7);
		Score n = o.getScore(ChatColor.YELLOW + "█ - Neutral"); n.setScore(6);
		Score u = o.getScore(ChatColor.GRAY + "█ - Unclaimed"); u.setScore(5);
		Score c = o.getScore(ChatColor.RED + "█ - Contested"); c.setScore(4);
		Score a = o.getScore(ChatColor.GREEN + "█ - Allied"); a.setScore(3);
		Score f = o.getScore(ChatColor.GOLD + "█ - Friendly"); f.setScore(2);
		Score y = o.getScore(ChatColor.AQUA + "█ - You"); y.setScore(1);
		Score hide = o.getScore(ChatColor.WHITE + "/f map To Hide"); hide.setScore(0);
		player.setScoreboard(o.getScoreboard());
	}
	
	public static void sendFacingDirection(Player player){
		String send = ChatColor.BOLD + Util.getDirection(player);
		new ActionbarTitleObject(send).send(player);
	}
	
	public static boolean isUsingMap(Player player){
		if(inFMap.contains(player.getUniqueId())) return true;
		return false;
	}
	
	public static void setUsingMap(Player player){
		inFMap.add(player.getUniqueId());
		setMap(player);
	}
	
	public static void removeUsingMap(Player player){
		inFMap.remove(player.getUniqueId());
		player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
	}
}
