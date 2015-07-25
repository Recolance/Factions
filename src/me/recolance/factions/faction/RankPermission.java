package me.recolance.factions.faction;

import java.util.HashMap;

public enum RankPermission{

	BUILD(1, 0, "Build In Faction Land"),
	USE_CONTAINERS(2, 1, "Use Containers In Faction Land"),
	USE_MECHANICS(3, 2, "Use Mechanics In Faction Land"),
	SET_SOUND(4, 3, "Change Faction Sound"),
	SET_DESCRIPTION(5, 4, "Change Faction Description"),
	SET_ICON(6, 5, "Change Faction Icon"),
	SET_STATUS(7, 6, "Change Faction Status"),
	EDIT_RULES(8, 7, "Edit Faction Rules"),
	INVITE_MEMBERS(9, 8, "Invite Players To Faction"),
	KICK_MEMBERS(10, 9, "Kick Members From Faction"),
	USE_HOME(11, 10, "Teleport To Faction Home"),
	SET_HOME(12, 11, "Change Faction Home Location"),
	VIEW_LAND_LOCATIONS(13, 12, "View Claimed Land Locations"),
	CLAIM_LAND(14, 13, "Claim New Faction Land"),
	UNCLAIM_LAND(15, 14, "Unclaim Faction Land"),
	EDIT_RELATIONS(16, 15, "Manage Faction Relations"),
	EDIT_RANKS(17, 16, "Manage Faction Ranks"),
	EDIT_MEMBER_RANK(18, 17, "Change Member's Rank"),
	USE_WARPS(19, 19, "Teleport To Faction Warps"),
	SET_WARPS(20, 20, "Set Faction Warps"),
	DELETE_WARPS(21, 21, "Delete Faction Warps"),
	VAULT_DEPOSIT(22, 22, "Deposit Into Faction Vault"),
	EDIT_VAULT(23, 23, "Edit Faction Vault Tabs");

	private int id;
	private int menuSlot;
	private String name;
	
	private static HashMap<Integer, RankPermission> idPerm = new HashMap<Integer, RankPermission>();
	private static HashMap<Integer, RankPermission> slotPerm = new HashMap<Integer, RankPermission>();
	static{
		for(RankPermission perm : RankPermission.values()){
			idPerm.put(perm.getId(), perm);
			slotPerm.put(perm.getMenuSlot(), perm);
		}
	}
	
	RankPermission(int id, int menuSlot, String name){
		this.id = id;
		this.menuSlot = menuSlot;
		this.name = name;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getMenuSlot(){
		return this.menuSlot;
	}
	
	public String getName(){
		return this.name;
	}
	
	public static RankPermission getRankPermissionFromSlot(int slot){
		return slotPerm.get(slot);
	}
	
	public static RankPermission getRankPermission(int id){
		return idPerm.get(id);
	}
}
