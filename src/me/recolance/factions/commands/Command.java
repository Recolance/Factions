package me.recolance.factions.commands;

public enum Command{

	FACTIONS("&6Faction Menu", "&9/faction", "&eOpens the main factions menu.", "", "&9Aliases:", "&e- /fac", "&e- /f"),
	CREATE("&6Create Faction", "&9/f create", "&eBegin creating a faction.", "", "&9Aliases:", "&e- /f make"),
	DISBAND("&6Disband Faction", "&9/f disband", "&eDisband your faction.", "", "&9Aliases:", "&e- /f delete"),
	LEAVE("&6Leave Faction", "&9/f leave", "&eLeave your faction", "", "&9Aliases:", "&e- /f quit"),
	INFO("&6Faction Info", "&9/f info <faction>", "&eShow information about a faction.", "", "&9Aliases:", "&e- /f who <faction>", "&e- /f show <faction>"),
	HOME("&6Teleport to Faction Home", "&9/f home", "&eTeleport to your faction home location.", "", "&9Aliases:", "&e- /f h"),
	SETHOME("&6Set Faction Home", "&9/f sethome", "&eSet a new faction home location.", "", "&9Aliases:", "&e- /f sh"),
	UNSETHOME("&6Unset Faction Home", "&9/f delhome", "&eRelocate the faction home location to spawn.", "", "&9Aliases:", "&e - /f unsethome"),
	CHAT("&6Faction Chat", "&9/f chat <message>", "&eSend a message to all your faction members.", "", "&9Aliases:", "&e- /f c <message>", "&e- /f talk <message>.", "&e- /f msg <message>"),
	CHAT_TOGGLE("&6Toggle Faction Chat", "&9/f chattoggle", "&eToggle faction chat to always send there.", "", "&9Aliases:", "&e- /f ctoggle", "&e- /f ct"),
	ACHAT("&6Ally Chat", "&9/f achat <message>", "&eSend a message to all ally members.", "", "&9Aliases:", "&e- /f ac <message>", "&e- /f amsg <message>", "&e- /f allychat <message>"),
	STATS("&6View Faction's Stats", "&9/f stats <faction>", "&eView a faction's stats.", "", "&9Aliases:", "&e- /f stat <faction>"),
	MEMBERS("&6View Faction Members", "&9/f members", "&eView your faction's members.", "", "&9Aliases:", "&e- /f member", "&e- /f roster"),
	KICK("&6Kick Faction Member", "&9/f kick <player>", "&eKick a member from your faction.", "", "&9Aliases:", "&e- /f remove <player>"),
	INVITE("&6Invite Faction Member", "&9/f invite <player>", "&eInvite a player to your faction.", "", "&9Aliases:", "&e- /f add <player>"),
	ACCEPT("&6Faction Accept", "&9/f accept", "&eUsed to accept a number of faction requests.", "", "&9Aliases:", "&e- /f yes"),
	DENY("&6Faction Deny", "&9/f deny", "&eused to deny a number of faction requests.", "", "&9Aliases:", "&e- /f no"),
	SETRANK("&6Set Member's Rank", "&9/f setrank <player> <rank>", "&eSet a member to a specific faction rank.", "", "&9Aliases:", "&e- /f promote <player> <rank>", "&e- /f demote <player> <rank>"),
	RANKS("&6Open Faction Ranks Menu", "&9/f ranks", "&eOpen your faction's rank menu.", "", "&9Aliases:", "&e- /f rank", "&e- /f perm", "&e- /f permissions"),
	RANKS_EDIT("&6Edit Faction Rank", "&9/f rank edit <rank>", "&eEdit the specified faction rank.", "", "&9Aliases:", "&e- /f rank change <rank>"),
	RANKS_ADD("&6Add Faction Rank", "&9/f rank add <name>", "&eAdd a new faction rank with the specified name.", "", "&9Aliases:", "&e- /f rank make <name>", "&e- /f rank create <name>", "&e- /f rank addrank <name>"),
	RANKS_DELETE("&6Delete Faction Rank", "&9/f rank remove <rank>", "&eRemove a faction rank.", "", "&9Aliases:", "&e- /f rank delete <rank>"),
	LEADER("&6Promote New Leader", "&9/f leader <player>", "&eRevoke and set a new faction leader.", "", "&9Aliases:", "&e- /f setleader <player>", "&e- /f owner <player>", "&e- /f setowner <player>"),
	ALLY("&6Ally Another Faction", "&9/f ally <faction>", "&eRequest to ally another faction.", "", "&9Aliases:", "&e- /f alliance <faction>"),
	ENEMY("&6Enemy Another Faction", "&9/f enemy <faction>", "&eSet a faction as your faction's enemy.", "", "&9Aliases:", "&e- /f contest <faction>"),
	NEUTRAL("&6Neutral Another Faction", "&9/f neutral <faction>", "&eRemove enemy or alliances with a faction.", "", "&9Aliases:", "&e- /f truce <faction>"),
	RELATIONS("&6Open Faction Relations Menu", "&9/f relations", "&eOpen your faction's relations menu."),
	ENEMIES("&6Open Faction Enemies Menu", "&9/f enemies", "&eOpen your faction's enemies menu.", "", "&9Aliases:", "&e- /f contested"),
	ALLIES("&6Open Faction Alliances Menu", "&9/f alliances", "&eOpen your faction's alliances menu.", "", "&9Aliases:", "&e- /f allies"),
	ENEMIEDBY("&6Open Faction Enemied By Menu", "&9/f enemiedby", "&eOpen your faction's enemied by menu.", "", "&9Aliases:", "&e- /f enemiesof"),
	VAULT("&6Open Faction Vault", "&9/f vault", "&eOpen your faction's vault.", "", "&9Aliases:", "&e- /f v", "&e- /f v <tab name>"),
	VAULT_EDIT("&6Edit Vault Tab", "&9/f v edit <tab name>", "&eEdit the name and icon for your vault.", "", "&9Aliases:", "&e- /f v edittab <tab name>"),
	VAULT_BALANCE("&6View Vault Balance", "&9/f v bal", "&eView your faction's vault coin balance.", "", "&9Aliases:", "&e- /f v coins", "&e- /f v money"),
	VAULT_DEFAULT("&6Open Vault Tab", "&9/f v <tab name>", "&eDirectly open a faction vault tab."),
	WARPS("&6Open Faction Warps Menu", "&9/f warps", "&eOpen your faction's warp menu.", "", "&9Aliases:", "&e- /f warplist"),
	WARP("&6Teleport to Faction Warp", "&9/f warp <warp>", "&eTeleport to a faction warp.", "", "&9Aliases:", "&e- /f warp"),
	SETWARP("&6Set Faction Warp", "&9/f setwarp <name>", "&eSet a new faction warp at your location.", "", "&9Aliases:", "&e- /f sw <name."),
	DELWARP("&6Delete Faction Warp", "&9/f delwarp <warp>", "&eRemove a faction warp.", "", "&9Aliases:", "&e- /f dw <warp>"),
	CLAIM("&6Claim Faction Land", "&9/f claim", "&eClaim the chunk you are istanding in for your faction."),
	OVERCLAIM("&6Overclaim Faction Land", "&9/f overclaim", "&eOverclaim the land you are standing in.", "", "&9Aliases:", "&e- /f oc"),
	UNCLAIM("&6Unclaim Faction Land", "&9/f unclaim", "&eUnclaim the land you are standing in.", "", "&9Aliases:", "&e- /f uc"),
	LAND("&6Open Faction Land Menu", "&9/f land", "&eOpen your faction's claimed land menu.", "", "&9Aliases:", "&e- /f chunks"),
	CHUNK("&6View Chunk Info", "&9/f chunk", "&eView information about the claim you are standing in.", "", "&9Aliases:", "&e- /f chunkinfo", "&e- /f sc", "&e- /f seechunk"),
	JOIN("&6Join Open Faction", "&9/f join <faction>", "&eJoin an open faction instantly.", "","&9Aliases:", "&e- /f j <faction>"),
	OPEN("&6Open Faction", "&9/f open", "&eOpen your faction so anyone can join."),
	CLOSE("&6Close Faction", "&9/f close", "&eClose your faction as ivite only.", "", "&9Aliases:", "&e- /f inviteonly"),
	TAG("&6Change Faction Name", "&9/f tag <name>", "&eChange your faction's name.", "", "&9Aliases:", "&e- /f name <name>", "&e- /f rename <name>"),
	DESC("&6Change Faction Description", "&9/f desc <description>", "&eChange your faction's description.", "", "&9Aliases:", "&e- /f description <description>"),
	ICON("&6Change Faction Icon", "&9/f icon", "&eChange your faction's icon.", "", "&9Aliases:", "&e- /f seticon"),
	SOUND("&6Change Faction Sound", "&9/f setsound", "&eChange your faction's sound."),
	PLAY_SOUND("&6Play Faction Sound", "&9/f sound", "&eHear your faction's sound."),
	CHALLENGE("&6Open Faction Challenge Menu", "&9/f challenges", "&eOpen your faction's challenge menu.", "", "&9Aliases:", "&e- /f chal", "&e- /f challenges", "&e- /f achievements"),
	LIST("&6List Factions", "&9/f list <page>", "&eList all factions."),
	MAP("&6Faction Map", "&9/f map", "&eOpen or close your faction land map."),
	RULES("&6View Faction Rules", "&9/f rules", "&eView your faction's rules.", "", "&9Aliases:", "&e- /f rule"),
	RULES_ADD("&6Add Faction Rule", "&9/f rule add <message>", "&eAdd a rule to your faction's rules."),
	RULES_DELETE("&6Delete Faction Rule", "&9/f rule delete <line #>", "&eDelete the faction rule at the given line number.", "", "&9Aliases:", "&e- /f remove <line #>", "&e- /f delete"),
	RULES_SET("&6Set Faction Rule", "&9/f rule set <line #> <message>", "&eSet the faction rule at the given line number."),
	RULES_INSERT("&6Insert Faction Rule", "&9/f insert <line #> <message>", "&eInsert the faction rule at the given line number."),
	NOTIFY("&6Notify Interest", "&9/f notifyjoin <faction>", "&eNotify a faction that you would like to join."),
	LEVEL("&6View Faction Level", "&9/f level <faction>", "&eView the level and exp of a faction.", "", "&9Aliases:", "&e- /f l <faction>"),
	POWER("&6View Faction Power", "&9/f power <faction>", "&eView the power of a faction", "", "&9Aliases:", "&e- /f p <faction>"),
	HELP("&6Factions Help", "&9/f help <page>", "&eView this help menu.", "", "&9Aliases", "&e- /f commands <page>");
	
	
	private String[] desc;
	
	Command(String... desc){
		this.desc = desc;
	}
	
	public String[] getDescription(){
		return this.desc;
	}	
}
