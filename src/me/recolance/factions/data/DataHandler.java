package me.recolance.factions.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.recolance.factions.Factions;
import me.recolance.factions.faction.ClaimedLand;
import me.recolance.factions.faction.CompletedChallenge;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.faction.FactionChallenge;
import me.recolance.factions.faction.Info;
import me.recolance.factions.faction.Member;
import me.recolance.factions.faction.Rank;
import me.recolance.factions.faction.Relations;
import me.recolance.factions.faction.StatType;
import me.recolance.factions.faction.Vault;
import me.recolance.factions.faction.VaultTab;
import me.recolance.factions.faction.Warp;

public class DataHandler{

	public static void generateTables(){
		factionsTable();
		membersTable();
		ranksTable();
		vaultTable();
	}
	
	private static void factionsTable(){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS factions ("
					+ "id VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255) NOT NULL,"
					+ "description VARCHAR(255) NOT NULL,"
					+ "icon VARCHAR(255) NOT NULL,"
					+ "sound INT(10) NOT NULL,"
					+ "leader VARCHAR(255) NOT NULL,"
					+ "status INT(10) NOT NULL,"
					+ "home VARCHAR(255) NOT NULL,"
					+ "last_active BIGINT(19) NOT NULL,"
					+ "activity_log TEXT,"
					+ "rules TEXT,"
					+ "date_created BIGINT(19) NOT NULL,"
					+ "claimed_land TEXT,"
					+ "enemies TEXT,"
					+ "enemied_by TEXT,"
					+ "alliances TEXT,"
					+ "stats TEXT NOT NULL,"
					+ "warps TEXT,"
					+ "challenges TEXT,"
					+ "PRIMARY KEY(id))");
			statement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private static void membersTable(){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS members ("
					+ "player_id VARCHAR(255) NOT NULL,"
					+ "faction VARCHAR(255) NOT NULL,"
					+ "rank VARCHAR(255) NOT NULL,"
					+ "date_joined BIGINT(19) NOT NULL,"
					+ "monitoring_power INT(10) NOT NULL,"
					+ "power_lost INT(10) NOT NULL,"
					+ "power_gained INT(10) NOT NULL,"
					+ "vault_items_taken INT(10) NOT NULL,"
					+ "vault_money_taken INT(10) NOT NULL,"
					+ "PRIMARY KEY(player_id))");
			statement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE INDEX INX_MEMBERS ON members (faction)");
			statement.execute();
		}catch(SQLException e){
		}
	}
	
	private static void ranksTable(){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS ranks ("
					+ "id VARCHAR(255) NOT NULL,"
					+ "faction VARCHAR(255) NOT NULL,"
					+ "name VARCHAR(255) NOT NULL,"
					+ "permissions VARCHAR(255),"
					+ "power_threshold INT(10) NOT NULL,"
					+ "vault_items_limit INT(10) NOT NULL,"
					+ "vault_money_limit INT(10) NOT NULL,"
					+ "is_default INT(10) NOT NULL,"
					+ "leader INT(10) NOT NULL,"
					+ "PRIMARY KEY (id))");
			statement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE INDEX INX_RANKS ON ranks (faction)");
			statement.execute();
		}catch(SQLException e){
		}
	}
	
	private static void vaultTable(){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS vaults ("
					+ "faction VARCHAR(255) NOT NULL,"
					+ "tab1 TEXT NOT NULL,"
					+ "tab2 TEXT NOT NULL,"
					+ "tab3 TEXT NOT NULL,"
					+ "tab4 TEXT NOT NULL,"
					+ "money INT NOT NULL,"
					+ "log TEXT,"
					+ "PRIMARY KEY (faction))");
			statement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void loadData(){
		long start = System.currentTimeMillis();
		ArrayList<Faction> factions = loadFactions();
		if(factions == null || factions.isEmpty()) return;
		DataHolder.loadFactions(factions);
		int purged = purgeInactivity(factions);
		long end = System.currentTimeMillis();
		
		System.out.println("********************** Faction Load **********************");
		System.out.println("* Loaded Factions: " + DataHolder.getAllFactions().size());
		System.out.println("* Loaded Members: " + DataHolder.getPlayerIdMemberMap().size());
		System.out.println("* Loaded Chunks: " + DataHolder.getChunkClaimedLandMap().size());
		System.out.println("* Purged Factions: " + purged);
		System.out.println("* Time To Load: " + (end - start) + "ms");
		System.out.println("**********************************************************");
	}
	
	private static ArrayList<Faction> loadFactions(){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM factions");
			ResultSet result = statement.executeQuery();
			ArrayList<Faction> factions = new ArrayList<Faction>();
			while(result.next()){
				Info info = new Info(result.getString("name"), result.getString("description"), Serialization.stringToIcon(result.getString("icon")),
							Serialization.intToSound(result.getInt("sound")), UUID.fromString(result.getString("leader")), Serialization.intToStatus(result.getInt("status")),
							Serialization.stringToHome(result.getString("home")), result.getLong("last_active"), result.getLong("date_created"),
							Serialization.stringToActivityLog(result.getString("activity_log")), Serialization.stringToRules(result.getString("rules")));
				List<ClaimedLand> land = Serialization.stringToClaimedLand(result.getString("claimed_land"));
				Relations relations = new Relations(Serialization.stringToAlliances(result.getString("alliances")), Serialization.stringToAlliances(result.getString("enemies")), Serialization.stringToEnemiedBy(result.getString("enemied_by")));
				List<Warp> warps = Serialization.stringToWarps(result.getString("warps"));
				HashMap<StatType, Long> stats = Serialization.stringToStats(result.getString("stats"));
				HashMap<FactionChallenge, CompletedChallenge> challenges = Serialization.stringToCompletedChallenges(result.getString("challenges"));
				List<Rank> ranks = loadRanks(UUID.fromString(result.getString("id")));
				List<Member> members = loadMembers(UUID.fromString(result.getString("id")), ranks);
				Vault vault = loadVault(UUID.fromString(result.getString("id")));
				factions.add(new Faction(UUID.fromString(result.getString("id")), info, members, ranks, land, relations, vault, warps, stats, challenges));
				Collections.sort(factions, (f1, f2) -> ((Long)f1.getScore()).compareTo(f2.getScore()));
			}
			return factions;
		}catch(SQLException e){
		}
		return null;
	}
	
	private static List<Rank> loadRanks(UUID factionId){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM ranks WHERE faction=?");
			statement.setString(1, factionId.toString());
			ResultSet result = statement.executeQuery();
			ArrayList<Rank> ranks = new ArrayList<Rank>();
			while(result.next()){
				Rank rank = new Rank(UUID.fromString(result.getString("id")), UUID.fromString(result.getString("faction")),
									 result.getString("name"), Serialization.stringToPermissions(result.getString("permissions")),
									 result.getInt("power_threshold"), result.getInt("vault_items_limit"), result.getInt("vault_money_limit"),
									 Serialization.intToDefault(result.getInt("is_default")), Serialization.intToLeader(result.getInt("leader")));
				ranks.add(rank);
			}
			return ranks;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<Member> loadMembers(UUID factionId, List<Rank> ranks){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM members WHERE faction=?");
			statement.setString(1, factionId.toString());
			ResultSet result = statement.executeQuery();
			ArrayList<Member> members = new ArrayList<Member>();
			while(result.next()){
				members.add(new Member(UUID.fromString(result.getString("player_id")), UUID.fromString(result.getString("faction")), 
							     	   Rank.getRankFromId(UUID.fromString(result.getString("rank")), ranks), result.getLong("date_joined"), 
							     	   Serialization.intToMonitoringPower(result.getInt("monitoring_power")), result.getInt("power_lost"),
							     	   result.getInt("power_gained"), result.getInt("vault_items_taken"), result.getInt("vault_money_taken")));
			}
			return members;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static Vault loadVault(UUID factionId){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM vaults WHERE faction=?");
			statement.setString(1, factionId.toString());
			ResultSet result = statement.executeQuery();
			while(result.next()){
				VaultTab vaultTab1 = Serialization.stringToVaultTab(result.getString("tab1"), 1);
				VaultTab vaultTab2 = Serialization.stringToVaultTab(result.getString("tab2"), 2);
				VaultTab vaultTab3 = Serialization.stringToVaultTab(result.getString("tab3"), 3);
				VaultTab vaultTab4 = Serialization.stringToVaultTab(result.getString("tab4"), 4);
				return new Vault(factionId, vaultTab1, vaultTab2, vaultTab3, vaultTab4, result.getInt("money"), Serialization.stringToVaultLog(result.getString("log")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static int purgeInactivity(List<Faction> factions){
		int i = 0;
		for(Faction faction : factions){
			if(System.currentTimeMillis() - faction.getLastActive() > (10 * 86400000)){
				faction.disband(null);
				i++;
			}
		}
		return i;
	}
	
	public static void saveAllFactions(boolean sync){
		long start = System.currentTimeMillis();
		int i = 0;
		for(Faction faction : DataHolder.getAllFactions()){
			if(faction.isSaveable()){
				faction.updateScore();
				saveFaction(faction, sync);
				i++;
				faction.setSaveable(false);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("********************** Faction Save **********************");
		System.out.println("* Saved Factions: " + i);
		System.out.println("* Unsaved Factions: " + (DataHolder.getAllFactions().size() - i));
		System.out.println("* Time To Save: " + (end - start) + "ms");
		System.out.println("**********************************************************");
	}
	
	public static void saveFaction(Faction faction, boolean sync){
		saveFactionDB(faction, sync);
		saveMembersDB(faction.getMembers(), sync);
		saveRanksDB(faction.getRanks(), sync);
		if(faction.isVaultSaveable()){
			saveVaultDB(faction.getVault(), sync);
			faction.setVaultSaveable(false);
		}
	}
	
	public static void saveFactionDB(Faction faction, boolean sync){
		Connection connection = Database.getConnection();
		try{
			final PreparedStatement statement = connection.prepareStatement("REPLACE INTO factions ("
					+ "id, name, description, icon, sound, leader, status, home, last_active, activity_log, rules, date_created, claimed_land, enemies, enemied_by, alliances, stats, warps, challenges)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			statement.setString(1, faction.getId().toString()); statement.setString(2, faction.getName());
			statement.setString(3, faction.getDescription()); statement.setString(4, Serialization.iconToString(faction.getIcon()));
			statement.setInt(5, Serialization.soundToInt(faction.getSound())); statement.setString(6, faction.getLeader().toString());
			statement.setInt(7, Serialization.statusToInt(faction.getStatus())); statement.setString(8, Serialization.homeToString(faction.getHome()));
			statement.setLong(9, faction.getLastActive()); statement.setString(10, Serialization.activityLogToString(faction.getActivityLog()));
			statement.setString(11, Serialization.rulesToString(faction.getRules())); statement.setLong(12, faction.getDateCreated());
			statement.setString(13, Serialization.claimedLandToString(faction.getClaimedLand())); statement.setString(14, Serialization.enemiesToString(faction.getEnemies()));
			statement.setString(15, Serialization.enemiedByToString(faction.getEnemiedBy())); statement.setString(16, Serialization.alliancesToString(faction.getAlliances()));
			statement.setString(17, Serialization.statsToString(faction.getStats())); statement.setString(18, Serialization.warpsToString(faction.getWarps()));
			statement.setString(19, Serialization.completedChallengesToString(faction.getCompletedChallenges()));
			if(!sync){
				new BukkitRunnable(){
					@Override
					public void run(){
						try{
							statement.executeUpdate();
						}catch(SQLException e){
							e.printStackTrace();
						}	
					}
				}.runTaskAsynchronously(Factions.plugin);
			}else{
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void saveMembersDB(List<Member> members, boolean sync){
		for(Member member : members){
			saveMember(member, sync);
		}
	}
	
	public static void saveMember(Member member, boolean sync){
		Connection connection = Database.getConnection();
		try{
			final PreparedStatement statement = connection.prepareStatement("REPLACE INTO members ("
					+ "player_id, faction, rank, date_joined, monitoring_power, power_lost, power_gained, vault_items_taken, vault_money_taken)"
					+ "VALUES(?,?,?,?,?,?,?,?,?)");
			statement.setString(1, member.getPlayer().toString()); statement.setString(2, member.getFaction().toString());
			statement.setString(3, member.getRank().getId().toString()); statement.setLong(4, member.getDateJoined());
			statement.setInt(5, Serialization.monitoringPowerToInt(member.isMonitoringPower())); statement.setInt(6, member.getPowerLost());
			statement.setInt(7, member.getPowerGained()); statement.setInt(8, member.getVaultItemsTaken());
			statement.setInt(9, member.getVaultMoneyTaken());
			if(!sync){
				new BukkitRunnable(){
					@Override
					public void run(){
						try{
							statement.executeUpdate();
						}catch(SQLException e){
							e.printStackTrace();
						}	
					}
				}.runTaskAsynchronously(Factions.plugin);
			}else{
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void saveRanksDB(List<Rank> ranks, boolean sync){
		Connection connection = Database.getConnection();
		for(Rank rank : ranks){
			try{
				final PreparedStatement statement = connection.prepareStatement("REPLACE INTO ranks ("
						+ "id, faction, name, permissions, power_threshold, vault_items_limit, vault_money_limit, is_default, leader)"
						+ "VALUES(?,?,?,?,?,?,?,?,?)");
				statement.setString(1, rank.getId().toString()); statement.setString(2, rank.getFaction().toString());
				statement.setString(3, rank.getName()); statement.setString(4, Serialization.permissionsToString(rank.getPermissions()));
				statement.setInt(5, rank.getPowerThreshold()); statement.setInt(6, rank.getVaultItemsLimit());
				statement.setInt(7, rank.getVaultMoneyLimit()); statement.setInt(8, Serialization.defaultToInt(rank.isDefault()));
				statement.setInt(9, Serialization.leaderToInt(rank.isLeader()));
				if(!sync){
					new BukkitRunnable(){
						@Override
						public void run(){
							try{
								statement.executeUpdate();
							}catch(SQLException e){
								e.printStackTrace();
							}	
						}
					}.runTaskAsynchronously(Factions.plugin);
				}else{
					statement.executeUpdate();
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	public static void saveVaultDB(Vault vault, boolean sync){
		Connection connection = Database.getConnection();
		try{
			final PreparedStatement statement = connection.prepareStatement("REPLACE INTO vaults ("
					+ "faction, tab1, tab2, tab3, tab4, money, log)"
					+ "VALUES(?,?,?,?,?,?,?)");
			statement.setString(1, vault.getFaction().toString()); statement.setString(2, Serialization.vaultTabToString(vault.getTab1()));
			statement.setString(3, Serialization.vaultTabToString(vault.getTab2())); statement.setString(4, Serialization.vaultTabToString(vault.getTab3()));
			statement.setString(5, Serialization.vaultTabToString(vault.getTab4())); statement.setInt(6, vault.getMoney());
			statement.setString(7, Serialization.vaultLogToString(vault.getLog()));
			if(!sync){
				new BukkitRunnable(){
					@Override
					public void run(){
						try{
							statement.executeUpdate();
						}catch(SQLException e){
							e.printStackTrace();
						}	
					}
				}.runTaskAsynchronously(Factions.plugin);
			}else{
				statement.executeUpdate();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void saveFactionsTimer(){
		new BukkitRunnable(){	
			@Override
			public void run(){
				saveAllFactions(false);
			}
		}.runTaskTimer(Factions.plugin, 0L, 36000L);
	}
}
