package me.recolance.factions.faction;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class Member{

	private final UUID player;
	private final UUID faction;
	private Rank rank;
	private final long dateJoined;
	private boolean isMonitoringPower;
	private int powerLost;
	private int powerGained;
	private int vaultItemsTaken;
	private int vaultMoneyTaken;
	private long lastOnline;

	//Method should only be used when loading from database.
	public Member(UUID player, UUID faction, Rank rank, long dateJoined, boolean isMonitoringPower, int powerLost, int powerGained, int vaultItemsTaken, int vaultMoneyTaken){
		this.player = player;
		this.faction = faction;
		this.rank = rank;
		this.dateJoined = dateJoined;
		this.setMonitoringPower(isMonitoringPower);
		this.powerLost = powerLost;
		this.powerGained = powerGained;
		this.vaultItemsTaken = vaultItemsTaken;
		this.vaultMoneyTaken = vaultMoneyTaken;
	}
	
	//Method should only be used when creating a new member.
	protected Member(UUID player, UUID faction){
		this.player = player;
		this.faction = faction;
		//
		this.dateJoined = System.currentTimeMillis();
		this.setMonitoringPower(false);
		this.powerLost = 0;
		this.powerGained = 0;
		this.vaultItemsTaken = 0;
		this.vaultMoneyTaken = 0;
	}
	
	public UUID getPlayer(){
		return player;
	}

	public UUID getFaction(){
		return faction;
	}

	public long getDateJoined(){
		return dateJoined;
	}

	public Rank getRank(){
		return rank;
	}

	public void setRank(Rank rank){
		this.rank = rank;
	}

	public int getVaultItemsTaken(){
		return vaultItemsTaken;
	}

	public void setVaultItemsTaken(int vaultItemsTaken){
		this.vaultItemsTaken = vaultItemsTaken;
	}

	public int getVaultMoneyTaken(){
		return vaultMoneyTaken;
	}

	public void setVaultMoneyTaken(int vaultMoneyTaken){
		this.vaultMoneyTaken = vaultMoneyTaken;
	}

	public long getLastOnline(){
		return lastOnline;
	}

	public void setLastOnline(long lastOnline){
		this.lastOnline = lastOnline;
	}

	public int getPowerLost(){
		return powerLost;
	}

	public void setPowerLost(int powerLost){
		this.powerLost = powerLost;
	}

	public int getPowerGained(){
		return powerGained;
	}

	public void setPowerGained(int powerGained){
		this.powerGained = powerGained;
	}

	public boolean isMonitoringPower(){
		return isMonitoringPower;
	}

	public void setMonitoringPower(boolean isMonitoringPower){
		this.isMonitoringPower = isMonitoringPower;
	}
	
	public boolean isOnline(){
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.player);
		if(offlinePlayer != null && offlinePlayer.isOnline()) return true;
		return false;
	}
}
