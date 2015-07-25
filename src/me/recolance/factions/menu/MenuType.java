package me.recolance.factions.menu;

public enum MenuType{

	NO_FACTION_MAIN(false, true, 0),
	CREATION_NAME(true, true, 0),
	CREATION_ICON(false, true, 0),
	CREATION_SOUND(false, true, 0),
	DISBAND_CONFIRMATION(false, true, 1),
	OPEN_FACTIONS(false, true, 0),
	FACTION_MAIN(false, true, 1),
	STATS(false, true, -1),
	MEMBERS(false, true, 1),
	MEMBERS_INVITE(true, true, 1),
	RANKS(false, true, 1),
	RANKS_EDITING(false, true, 1),
	RANKS_EDITING_COINS(true, true, 1),
	RANKS_EDITING_ITEMS(true, true, 1),
	RANKS_EDITING_THRESHOLD(true, true, 1),
	RANKS_EDITING_NAME(true, true, 1),
	RANKS_REMOVE_CONFIRMATION(false, true, 1),
	RANKS_CREATE(true, true, 1),
	RELATION_MAIN(false, true, 1),
	ALLIANCES(false, true, 1),
	ALLIANCES_ADD(true, true, 1),
	ENEMIES(false, true, 1),
	ENEMIES_ADD(true, true, 1),
	ENEMIED_BY(false, true, 1),
	VAULT(false, false, 1),
	VAULT_DEPOSIT_COINS(true, true, 1),
	VAULT_WITHDRAWL_COINS(true, true, 1),
	VAULT_EDIT(false, true, 1),
	VAULT_EDIT_NAME(true, true, 1),
	VAULT_EDIT_ICON(false, true, 1),
	WARPS(false, true, 1),
	WARPS_NAME(true, true, 1),
	CLAIMED_LAND(false, true, 1),
	CHALLENGES(false, true, 1),
	SETTINGS(false, true, 1),
	EDIT_NAME(true, true, 1),
	EDIT_ICON(false, true, 1),
	EDIT_SOUND(false, true, 1),
	LEADER_CONFIRMATION(false, true, 1);
	
	private boolean isAnvil;
	private boolean isFullCancel;
	private int requiresFaction;
	
	
	MenuType(boolean isAnvil, boolean isFullCancel, int requiresFaction){
		this.isAnvil = isAnvil;
		this.isFullCancel = isFullCancel;
		this.requiresFaction = requiresFaction;
	}
	
	public boolean isAnvil(){
		return isAnvil;
	}
	
	public boolean isFullCancel(){
		return isFullCancel;
	}
	
	public int requiresFaction(){
		return requiresFaction;
	}
}
