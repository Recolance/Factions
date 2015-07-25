package me.recolance.factions.faction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum FactionChallenge{
	
	FACTION_LEVEL_1(0, 1, 0, 8, 500, null, StatType.LEVEL, "Faction Level 8", "&aReach faction level 8.", "", "&9Rewards:", "&e- Claim Land Page 2", "&e- 1,000 Vault Coins", ""), //Max claimable land 50
	FACTION_LEVEL_2(1, 2, 9, 16, 1000, null, StatType.LEVEL, "Faction Level 16", "&aReach faction level 16.", "", "&9Rewards:", "&e- Claim Land Page 3", "&e- 2,000 Vault Coins", ""), //Max claimable land 70
	FACTION_LEVEL_3(2, 3, 18, 25, 1500, null, StatType.LEVEL, "Faction Level 25", "&aReach faction level 25.", "", "&9Rewards:", "&e- Claim Land Page 4", "&e- 3,000 Vault Coins", ""),  //Max claimable land 90
	PLAYERS_KILLED_1(10, 1, 1, 10000, 500, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0), StatType.PLAYERS_KILLED, "10,000 Players Killed", "&aKill 10,000 players.", "", "&9Rewards:", "&e- Strength I In Claimed Land", "&e- 1,000 Vault Coins", ""), //Strength
	PLAYERS_KILLED_2(11, 2, 10, 40000, 1000, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1), StatType.PLAYERS_KILLED, "40,000 Players Killed", "&aKill 40,000 players.", "", "&9Rewards:", "&e- Strength II In Claimed Land", "&e- 2,000 Vault Coins", ""), //Strength
	PLAYERS_KILLED_3(12, 3, 19, 90000, 1500, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2), StatType.PLAYERS_KILLED, "90,000 Players Killed", "&aKill 90,000 players.", "", "&9Rewards:", "&e- Strength III In Claimed Land", "&e- 3,000 Vault Coins", ""), //Strength
	MONSTERS_KILLED_1(20, 1, 2, 50000, 500, new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), StatType.MONSTERS_KILLED, "50,000 Monsters Killed", "&aKill 50,000 monsters.", "", "&9Rewards:", "&e- Night Vision In Claimed Land", "&e- 1,000 Vault Coins", ""), //Night vision
	MONSTERS_KILLED_2(21, 2, 11, 100000, 1000, new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0), StatType.MONSTERS_KILLED, "100,000 Monsters Killed", "&aKill 100,000 monsters.", "", "&9Rewards:", "&e- Water Breathing In Claimed Land", "&e- 2,000 Vault Coins", ""), //Water Breathing
	MONSTERS_KILLED_3(22, 3, 20, 200000, 1500, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), StatType.MONSTERS_KILLED, "200,000 Monsters Killed", "&aKill 200,000 monsters.", "", "&9Rewards:", "&e- Fire Resistance In Claimed Land", "&e- 3,000 Vault Coins", ""), //Fire Resistence
	ANIMALS_KILLED_1(30, 1, 3, 25000, 500, new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 0), StatType.ANIMALS_KILLED,"25,000 Animals Killed", "&aKill 25,000 animals", "", "&9Rewards:", "&e- Saturation I In Claimed Land", "&e- 1,000 Vault Coins", ""), //Saturation
	ANIMALS_KILLED_2(31, 2, 12, 50000, 1000, new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 0), StatType.ANIMALS_KILLED,"50,000 Animals Killed", "&aKill 50,000 animals", "", "&9Rewards:", "&e- Health Boost I In Claimed Land", "&e- 2,000 Vault Coins", ""), //Saturation
	ANIMALS_KILLED_3(32, 3, 21, 100000, 1500, new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 0), StatType.ANIMALS_KILLED,"100,000 Animals Killed", "&aKill 100,000 animals", "", "&9Rewards:", "&e- Absorption I In Claimed Land", "&e- 3,000 Vault Coins", ""), //Saturation
	BLOCKS_PLACED_1(40, 1, 4, 100000, 500, new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0), StatType.BLOCKS_PLACED,"100,000 Blocks Placed", "&aPlace 100,000 blocks.", "", "&9Rewards:", "&e- Jump Boost I In Claimed Land", "&e- 1,000 Vault Coins", ""), //Jump Boost
	BLOCKS_PLACED_2(41, 2, 13, 500000, 1000, new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1), StatType.BLOCKS_PLACED,"500,000 Blocks Placed", "&aPlace 500,000 blocks.", "", "&9Rewards:", "&e- Jump Boost II In Claimed Land", "&e- 2,000 Vault Coins", ""), //Jump Boost
	BLOCKS_PLACED_3(42, 3, 22, 1000000, 1500, new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2), StatType.BLOCKS_PLACED,"1,000,000 Blocks Placed", "&aPlace 1,000,000 blocks.", "", "&9Rewards:", "&e- Jump Boost III In Claimed Land", "&e- 3,000 Vault Coins", ""), //Jump Boost
	ORES_BROKEN_1(50, 1, 5, 3000, 500, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0), StatType.ORES_BROKEN,"3,000 Ores Mined", "&aMine 3,000 Ore", "", "&9Rewards:", "&e- Mining Haste I In Claimed Land", "&e- 1,000 Vault Coins", ""), //Mining Haste
	ORES_BROKEN_2(51, 2, 14, 6000, 1000, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), StatType.ORES_BROKEN,"6,000 Ores Mined", "&aMine 6,000 Ore", "", "&9Rewards:", "&e- Mining Haste II In Claimed Land", "&e- 2,000 Vault Coins", ""), //Mining Haste
	ORES_BROKEN_3(52, 3, 23, 15000, 1500, new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2), StatType.ORES_BROKEN,"15,000 Ores Mined", "&aMine 15,000 Ore", "", "&9Rewards:", "&e- Mining Haste III In Claimed Land", "&e- 3,000 Vault Coins", ""), //Mining Haste
	POTIONS_BREWED_1(60, 1, 6, 4000, 500, new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), StatType.POTIONS_BREWED,"4,000 Potions Brewed", "&aBrew 4,000 potions.", "", "&9Rewards:", "&e- Swiftness I In Claimed Land", "&e- 1,000 Vault Coins", ""), //Swiftness
	POTIONS_BREWED_2(61, 2, 15, 8000, 1000, new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), StatType.POTIONS_BREWED,"8,000 Potions Brewed", "&aBrew 8,000 potions.", "", "&9Rewards:", "&e- Swiftness II In Claimed Land", "&e- 2,000 Vault Coins", ""), //Swiftness
	POTIONS_BREWED_3(62, 3, 24, 15000, 1500, new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), StatType.POTIONS_BREWED,"15,000 Potions Brewed", "&aBrew 15,000 potions.", "", "&9Rewards:", "&e- Swiftness III In Claimed Land", "&e- 3,000 Vault Coins", ""), //Swiftness
	ITEMS_FISHED_1(70, 1, 7, 1000, 500, null, StatType.ITEMS_FISHED,"1,000 Items Fished", "&aFish up 1,000 items.", "", "&9Rewards:", "&e- Vault Tab 2 Unlocked", "&e- 1,000 Vault Coins", ""), //Vault Tab
	ITEMS_FISHED_2(71, 2 ,16, 4000, 1000, null, StatType.ITEMS_FISHED,"4,000 Items Fished", "&aFish up 4,000 items.", "", "&9Rewards:", "&e- Vault Tab 3 Unlocked", "&e- 2,000 Vault Coins", ""), //Vault Tab
	ITEMS_FISHED_3(72, 3, 25, 9000, 1500, null, StatType.ITEMS_FISHED,"9,000 Items Fished", "&aFish up 9,000 items.", "", "&9Rewards:", "&e- Vault Tab 4 Unlocked", "&e- 3,000 Vault Coins", ""), //Vault Tab
	LAND_OVERCLAIMED_1(80, 1, 8, 10, 500, null, StatType.LAND_OVERCLAIMED,"10 Land Overclaims", "&aOverclaim 10 chunks.", "", "&9Rewards:", "&e- 2 More Faction Warps", "&e- 1,000 Vault Coins", ""), //Warps
	LAND_OVERCLAIMED_2(81, 2, 17, 50, 1000, null, StatType.LAND_OVERCLAIMED,"50 Land Overclaims", "&aOverclaim 50 chunks.", "", "&9Rewards:", "&e- 2 More Faction Warps", "&e- 2,000 Vault Coins", ""), //Warps
	LAND_OVERCLAIMED_3(82, 3, 26, 100, 1500, null, StatType.LAND_OVERCLAIMED,"100 Land Overclaims", "&aOverclaim 100 chunks.", "", "&9Rewards:", "&e- 2 More Faction Warps", "&e- 3,000 Vault Coins", ""), //Warps
	COMPLETE_ALL(999, 1, 31, 27, 5000, null, StatType.CHALLENGES_COMPLETED, "Complete Every Challenge", "&aComplete every challenge.", "", "&9Rewards:", "&e- 10,000 Vault Coins", "");
	
	private int id;
	private int tier;
	private int menuSlot;
	private long statAmount;
	private int coinReward;
	private StatType stat;
	private PotionEffect effect;
	private String name;
	private String[] lore;
	
	private static HashMap<Integer, FactionChallenge> idChallenge = new HashMap<Integer, FactionChallenge>();
	private static HashMap<StatType, List<FactionChallenge>> statChallenge = new HashMap<StatType, List<FactionChallenge>>();
	static{
		for(FactionChallenge challenge : FactionChallenge.values()){
			idChallenge.put(challenge.getId(), challenge);
			if(!statChallenge.containsKey(challenge.getStatType())){
				List<FactionChallenge> cL = new ArrayList<FactionChallenge>();
				cL.add(challenge);
				statChallenge.put(challenge.getStatType(), cL);
			}else statChallenge.get(challenge.getStatType()).add(challenge);
		}
	}
	
	FactionChallenge(int id, int tier, int menuSlot, long statAmount, int coinReward, PotionEffect effect, StatType stat, String name, String... lore){
		this.id = id;
		this.tier = tier;
		this.menuSlot = menuSlot;
		this.statAmount = statAmount;
		this.coinReward = coinReward;
		this.stat = stat;
		this.effect = effect;
		this.name = name;
		this.lore = lore;
	}
	
	public int getId(){
		return this.id;
	}
	
	public int getTier(){
		return this.tier;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getMenuSlot(){
		return this.menuSlot;
	}
	
	public long getStatAmount(){
		return this.statAmount;
	}
	
	public int getCoinReward(){
		return this.coinReward;
	}

	public String[] getLore(){
		return lore;
	}
	
	public StatType getStatType(){
		return this.stat;
	}
	
	public PotionEffect getEffect(){
		return this.effect;
	}
	
	public static FactionChallenge getNextTier(FactionChallenge chal){
		if(chal.getTier() == 3) return null;
		for(FactionChallenge c : statChallenge.get(chal.getStatType())){
			if(c.getTier() == chal.getTier() + 1) return c;
		}
		return null;
	}
	
	public static FactionChallenge getFactionChallenge(int id){
		return idChallenge.get(id);
	}
	
	public List<String> getRewardStrings(){
		int i = 0;
		int index = 0;
		List<String> lore = Arrays.asList(getLore());
		for(String string : lore){
			if(string.equals("&9Rewards:")){
				index = i;
				break;
			}
			i++;
		}
		List<String> rewardList = new ArrayList<String>();
		for(int line = index; line < lore.size(); line++){
			rewardList.add(lore.get(line));
		}
		rewardList.remove(rewardList.size() - 1);
		return rewardList;
	}
}
