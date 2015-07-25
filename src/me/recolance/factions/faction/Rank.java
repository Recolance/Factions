package me.recolance.factions.faction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Rank{

	private final UUID id;
	private final UUID faction;
	private String name;
	private List<Integer> permissions;
	private int powerThreshold;
	private int vaultItemsLimit;
	private int vaultMoneyLimit;
	private boolean isDefault;
	private final boolean isLeader;
	
	//Method should be called when loading the rank from disc.
	public Rank(UUID id, UUID faction, String name, List<Integer> permissions, int powerThreshold, int vaultItemsLimit, int vaultMoneyLimit, boolean isDefault, boolean isLeader){
		this.id = id;
		this.faction = faction;
		this.name = name;
		this.permissions = permissions;
		this.powerThreshold = powerThreshold;
		this.vaultItemsLimit = vaultItemsLimit;
		this.vaultMoneyLimit = vaultMoneyLimit;
		this.isDefault = isDefault;
		this.isLeader = isLeader;
	}
	
	//Method should be called upon creating a new rank.
	public Rank(UUID faction, String name, boolean isDefault){
		this.id = UUID.randomUUID();
		this.faction = faction;
		this.name = name;
		this.permissions = new ArrayList<Integer>();
		this.powerThreshold = 1000;
		this.vaultItemsLimit = 0;
		this.vaultMoneyLimit = 0;
		this.isDefault = isDefault;
		this.isLeader = false;
	}

	public UUID getId(){
		return id;
	}

	public UUID getFaction(){
		return faction;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public List<Integer> getPermissions(){
		return permissions;
	}

	public void setPermissions(List<Integer> permissions){
		this.permissions = permissions;
	}
	public boolean hasPermission(RankPermission permission){
		if(getPermissions().contains(permission.getId())) return true;
		return false;
	}

	public int getVaultItemsLimit(){
		return vaultItemsLimit;
	}

	public void setVaultItemsLimit(int vaultItemsLimit){
		this.vaultItemsLimit = vaultItemsLimit;
	}

	public int getVaultMoneyLimit(){
		return vaultMoneyLimit;
	}

	public void setVaultMoneyLimit(int vaultMoneyLimit){
		this.vaultMoneyLimit = vaultMoneyLimit;
	}

	public boolean isDefault(){
		return isDefault;
	}

	public void setDefault(boolean isDefault){
		this.isDefault = isDefault;
	}

	public boolean isLeader(){
		return isLeader;
	}

	public int getPowerThreshold(){
		return powerThreshold;
	}

	public void setPowerThreshold(int powerTreshold){
		this.powerThreshold = powerTreshold;
	}
	
	public static Rank getRankFromId(UUID id, List<Rank> ranks){
		for(Rank rank : ranks){
			if(rank.getId().equals(id)) return rank;
		}
		return null;
	}
	
	public void swapPermission(RankPermission permission){
		if(this.permissions.contains((Integer)permission.getId())) this.permissions.remove((Integer)permission.getId());
		else this.permissions.add(permission.getId());
	}
	
	public static List<Rank> getPredefinedRanks(UUID factionId){
		List<Rank> ranks = new ArrayList<Rank>();
		ArrayList<Integer> leaderPermissions = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23));
		ArrayList<Integer> officerPermissions = new ArrayList<Integer>(Arrays.asList(1,2,3,5,6,8,9,10,11,12,13,14,15,16,18,19,20,21,22,23));
		ArrayList<Integer> memberPermissions = new ArrayList<Integer>(Arrays.asList(1,2,3,9,11,19,22,23));
		ArrayList<Integer> recruitPermissions = new ArrayList<Integer>(Arrays.asList(3,22,23));
		Rank leaderRank = new Rank(UUID.randomUUID(), factionId, "Leader", leaderPermissions, 1000000, 1000000, 1000000, false, true); ranks.add(leaderRank);
		Rank officerRank = new Rank(UUID.randomUUID(), factionId, "Officer", officerPermissions, 1000, 25, 1000, false, false); ranks.add(officerRank);
		Rank memberRank = new Rank(UUID.randomUUID(), factionId, "Member", memberPermissions, 500, 5, 250, false, false); ranks.add(memberRank);
		Rank recruitRank = new Rank(UUID.randomUUID(), factionId, "Recruit", recruitPermissions, 250, 0, 0, true, false); ranks.add(recruitRank);
		return ranks;
	}
}
