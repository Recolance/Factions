package me.recolance.factions.controller;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;

import java.util.HashMap;
import java.util.UUID;

import me.recolance.betterutilities.events.BrewedPotionEvent;
import me.recolance.breachedmcmisc.events.FishingItemCaughtEvent;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Serialization;
import me.recolance.factions.events.EnteredOwnLandEvent;
import me.recolance.factions.events.LeftOwnLandEvent;
import me.recolance.factions.faction.Faction;
import me.recolance.factions.faction.FactionChallenge;
import me.recolance.factions.faction.RankPermission;
import me.recolance.factions.faction.StatType;
import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;
import me.recolance.globalutil.events.TimeUpdateEvent;
import me.recolance.globalutil.events.TimeUpdateEvent.Time;
import net.citizensnpcs.api.CitizensAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener{
	
	@EventHandler(ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		if(FactionUtil.isInFactionChat(player)){
			if(!FactionUtil.isInFaction(player)) DataHolder.inFChat.remove(player.getUniqueId());
			else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				event.setCancelled(true);
				faction.sendMessage("&a►F-Chat◄ " + player.getDisplayName() + "&f: " + event.getMessage());
			}
		}else Controller.handleMouseOverMessage(event);
	}
	
	@EventHandler
	public void onTimeUpdate(TimeUpdateEvent event){
		Time time = event.getTime();
		if(time.getHour() == 0 && time.getMinute() == 0 && time.getSecond() == 0){
			Controller.runNewDayResets();
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(!player.hasPlayedBefore()) FactionMapRenderer.setUsingMap(player);
		if(FactionUtil.isInFaction(player)){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.setSaveable(true);
			faction.updateLastActive();
			if(FactionUtil.getChunkFaction(player.getLocation().getChunk()) == faction){
				Controller.enteredLandEffects(player, faction);
				previousChunk.put(player.getUniqueId(), true);
				previousChunkChunk.put(player.getUniqueId(), player.getLocation().getChunk());
			}else Controller.removeRewardedEffects(player);

		}
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event){
		Player player = event.getPlayer();
		previousChunk.remove(player);
		if(FactionUtil.isInFaction(player)){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.setSaveable(true);
			faction.updateLastActive();
		}
		Controller.removeRewardedEffects(player);
	}
	
	@EventHandler
	public void onPotionBrew(BrewedPotionEvent event){
		Player player = event.getPlayer();
		if(FactionUtil.isInFaction(player)){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.addStat(StatType.POTIONS_BREWED, 1);
			FactionUtil.checkChallenge(faction, player, new FactionChallenge[]{FactionChallenge.POTIONS_BREWED_1,FactionChallenge.POTIONS_BREWED_2, FactionChallenge.POTIONS_BREWED_3}, StatType.POTIONS_BREWED);
		}
	}
	
	@EventHandler
	public void onFish(FishingItemCaughtEvent event){
		Player player = event.getPlayer();
		if(FactionUtil.isInFaction(player)){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.addStat(StatType.ITEMS_FISHED, 1);
			FactionUtil.checkChallenge(faction, player, new FactionChallenge[]{FactionChallenge.ITEMS_FISHED_1, FactionChallenge.ITEMS_FISHED_2, FactionChallenge.ITEMS_FISHED_3}, StatType.ITEMS_FISHED);
		}
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event){
		Chunk chunk = event.getChunk();
		if(DataHolder.getAlreadySerializedChunks().containsKey(chunk)) DataHolder.getAlreadySerializedChunks().remove(chunk); 
	}

	private static HashMap<UUID, Boolean> previousChunk = new HashMap<UUID, Boolean>();
	private static HashMap<UUID, Chunk> previousChunkChunk = new HashMap<UUID, Chunk>();
	private static HashMap<Player, Long> timer = new HashMap<Player, Long>();
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(!timer.containsKey(player)) timer.put(player, System.currentTimeMillis());
		if(System.currentTimeMillis() - timer.get(player) < 600) return;
		timer.put(player, System.currentTimeMillis());
		
		boolean uM = FactionMapRenderer.isUsingMap(player);
		if(uM) FactionMapRenderer.sendFacingDirection(player);
		
		if((float)event.getFrom().getX() == (float)event.getTo().getX()) return;
		Chunk chunk = player.getLocation().getChunk();
		if(!previousChunk.containsKey(player.getUniqueId())){
			previousChunk.put(player.getUniqueId(), false);
			previousChunkChunk.put(player.getUniqueId(), chunk);
		}else{
			if(previousChunkChunk.get(player.getUniqueId()) == chunk) return;
			if(uM) FactionMapRenderer.setMap(player);
			boolean chunkIs = FactionUtil.isChunkClaimed(chunk);
			boolean previousIs = previousChunk.get(player.getUniqueId());
			if(chunkIs && !previousIs){
				String territory = ChatColor.YELLOW + "Entered Neutral Territory";
				Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
				if(FactionUtil.isInFaction(player)){
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(faction.hasAlliance(chunkFaction)) territory = ChatColor.GREEN + "Entered Ally Territory";
					else if(faction.isEnemiedBy(chunkFaction) || faction.hasEnemy(chunkFaction)) territory = ChatColor.RED + "Entered Contested Territory";
					else if(faction == chunkFaction){
						territory = ChatColor.GOLD + "Entered Friendly Territory";
						Bukkit.getPluginManager().callEvent(new EnteredOwnLandEvent(player, faction));
					}
				}
				new TitleObject("", chunkFaction.getName()).setStay(50).send(player);
				new ActionbarTitleObject(territory).send(player);
				player.playSound(player.getLocation(), chunkFaction.getSound().getSound(), 1, 1);
			}else if(previousIs && !chunkIs){
				new TitleObject("", ChatColor.DARK_GREEN + "Wilderness").send(player);
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(FactionUtil.getChunkFaction(previousChunkChunk.get(player.getUniqueId())) == faction) Bukkit.getPluginManager().callEvent(new LeftOwnLandEvent(player, faction));
			}
			previousChunk.put(player.getUniqueId(), chunkIs);
			previousChunkChunk.put(player.getUniqueId(), chunk);
		}
	}
	
	@EventHandler
	public void onOwnLandEnter(EnteredOwnLandEvent event){
		Controller.enteredLandEffects(event.getPlayer(), event.getFaction());
	}
	
	@EventHandler
	public void onOwnLandLeave(LeftOwnLandEvent event){
		Controller.removeRewardedEffects(event.getPlayer());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event){
		LivingEntity entity = event.getEntity();
		if(entity.getKiller() instanceof Player){
			Player killer = (Player)event.getEntity().getKiller();
			Faction killerFac = FactionUtil.getPlayerFaction(killer);
			if(entity instanceof Player && killerFac != null){
				Player player = (Player)entity;
				Faction faction = FactionUtil.getPlayerFaction(player);
					killerFac.addStat(StatType.PLAYERS_KILLED, 1);
					player.playSound(player.getLocation(), killerFac.getSound().getSound(), 1F, 1F);
					FactionUtil.checkChallenge(killerFac, killer, new FactionChallenge[]{FactionChallenge.PLAYERS_KILLED_1, FactionChallenge.PLAYERS_KILLED_2, FactionChallenge.PLAYERS_KILLED_3}, StatType.PLAYERS_KILLED);
					long pG = FactionUtil.calculatePlayerKillPowerGain(player.getLocation().getChunk(), killer, player);
					if(pG > 0) killerFac.addPower(pG, killer);
					killerFac.addExp(3L, player);
					if(faction != null){
						faction.addStat(StatType.DEATHS_PLAYER, 1);
						long pL = FactionUtil.calculatePlayerDeathPowerLoss(player.getLocation().getChunk(), killer, player);
						if(pL > 0) faction.removePower(pL, player);
					}
			}else if(entity instanceof Animals && killerFac != null){
				killerFac.addStat(StatType.ANIMALS_KILLED, 1);
				FactionUtil.checkChallenge(killerFac, killer, new FactionChallenge[]{FactionChallenge.ANIMALS_KILLED_1,FactionChallenge.ANIMALS_KILLED_2,FactionChallenge.ANIMALS_KILLED_3}, StatType.ANIMALS_KILLED);
			}else if(entity instanceof Monster && killerFac != null){
				killerFac.addStat(StatType.MONSTERS_KILLED, 1);
				FactionUtil.checkChallenge(killerFac, killer, new FactionChallenge[]{FactionChallenge.MONSTERS_KILLED_1, FactionChallenge.MONSTERS_KILLED_2, FactionChallenge.MONSTERS_KILLED_3}, StatType.MONSTERS_KILLED);
			}
		}else if(entity instanceof Player){
			Faction faction = FactionUtil.getPlayerFaction((Player)entity);
			if(faction != null){
				faction.addStat(StatType.DEATHS_NON_PLAYER, 1);
				long pL = FactionUtil.calculateOtherDeathPowerLoss(entity.getLastDamageCause().getCause());
				if(pL > 0)faction.removePower(FactionUtil.calculateOtherDeathPowerLoss(entity.getLastDamageCause().getCause()), (Player)entity);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageByEntityEvent event){
		Player attacked = null;
		Player damager = null;
		
		if(event.getEntity() instanceof Player) attacked = (Player)event.getEntity();
		else return;
		
		if(event.getDamager() instanceof Player) damager = (Player)event.getDamager();
		else if(event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) damager = (Player)((Projectile)event.getDamager()).getShooter();
		else return;
		
		if(attacked == null || damager == null) return;
		
		Faction aF = FactionUtil.getPlayerFaction(attacked);
		Faction dF = FactionUtil.getPlayerFaction(damager);
		
		if(aF == null || dF == null) return;
		
		if(aF == dF){
			event.setCancelled(true);
			Util.message(damager, "&cYou cannot attack a member of your faction.");
		}else if(aF.hasAlliance(dF)){
			event.setCancelled(true);
			Util.message(damager, "&cYou cannot attack a member of an ally faction.");
		}
	}
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer();
		if(player.isOp()){
			event.setCancelled(false);
			return;
		}
		String chunk = Serialization.chunkToString(event.getBlock().getLocation().getChunk());
		boolean isInFaction = FactionUtil.isInFaction(player);
		boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
	
		//Handle building permissions
		if(chunkIsClaimed){
			Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
			if(!isInFaction){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(chunkFaction != faction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.BUILD)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}
			}
		}
		//Handle stats
		if(isInFaction && !event.isCancelled()){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.addStat(StatType.BLOCKS_PLACED, 1);
			FactionUtil.checkChallenge(faction, player, new FactionChallenge[]{FactionChallenge.BLOCKS_PLACED_1, FactionChallenge.BLOCKS_PLACED_2, FactionChallenge.BLOCKS_PLACED_3}, StatType.BLOCKS_PLACED);
		}	
	}
	
	@EventHandler
	public void onInteractEntityBuilding(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.isOp()){
			event.setCancelled(false);
			return;
		}
		ItemStack item = event.getItem();
		if(item != null && item.isSimilar(DataHolder.getXPItem().get())){
			event.setCancelled(true);
			if(FactionUtil.isInFaction(player)){
				FactionUtil.getPlayerFaction(player).addExp(2500L, player);
				boolean isOne = item.getAmount() == 1 ? true : false;
				if(isOne) player.setItemInHand(null);
				else{
					item.setAmount(item.getAmount() - 1);
					player.setItemInHand(item);
				}
			}else Util.message(player, "&cYou are not in a faction.");
			return;
		}
		if(item != null &&(((item.getType() == Material.WATER_BUCKET || item.getType() == Material.LAVA_BUCKET) && event.getAction() == Action.RIGHT_CLICK_BLOCK) || (item.getType() == Material.BUCKET && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)))){
			String chunk = Serialization.chunkToString(Util.getBorderClickedLocation(event.getBlockFace(), event.getClickedBlock().getLocation()).getChunk());
			//NPE
			boolean isInFaction = FactionUtil.isInFaction(player);
			boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
			//Handle building permissions
			if(chunkIsClaimed){
				Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
				if(!isInFaction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
				}else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(chunkFaction != faction){
						event.setCancelled(true);
						Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
					}else if(!FactionUtil.hasPermission(player, RankPermission.BUILD)){
						event.setCancelled(true);
						Util.message(player, "&cYour faction rank does not allow you to do this.");
					}
				}
			}
		}
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(item == null || item.getType() == Material.AIR) return;
		if(Util.isPlaceableEntity(item)){
			String chunk = Serialization.chunkToString(Util.getBorderClickedLocation(event.getBlockFace(), event.getClickedBlock().getLocation()).getChunk());
			boolean isInFaction = FactionUtil.isInFaction(player);
			boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
			//Handle building permissions
			if(chunkIsClaimed){
				Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
				if(!isInFaction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
				}else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(chunkFaction != faction){
						event.setCancelled(true);
						Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
					}else if(!FactionUtil.hasPermission(player, RankPermission.BUILD)){
						event.setCancelled(true);
						Util.message(player, "&cYour faction rank does not allow you to do this.");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteractOpeningContainers(PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		if(player.isOp()){
			event.setCancelled(false);
			return;
		}
		if(Util.isContainer(event.getClickedBlock())){
			String chunk = Serialization.chunkToString(Util.getBorderClickedLocation(event.getBlockFace(), event.getClickedBlock().getLocation()).getChunk());
			boolean isInFaction = FactionUtil.isInFaction(player);
			boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
			//Handle building permissions
			if(chunkIsClaimed){
				Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
				if(!isInFaction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot open containers in " + chunkFaction.getName() + "&r&c's land.");
				}else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(chunkFaction != faction){
						event.setCancelled(true);
						Util.message(player, "&cYou cannot open containers in " + chunkFaction.getName() + "&r&c's land.");
					}else if(!FactionUtil.hasPermission(player, RankPermission.USE_CONTAINERS)){
						event.setCancelled(true);
						Util.message(player, "&cYour faction rank does not allow you to do this.");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteractUseMechanicals(PlayerInteractEvent event){
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		if(player.isOp()){
			event.setCancelled(false);
			return;
		}
		if(Util.isMechanical(event.getClickedBlock())){
			String chunk = Serialization.chunkToString(Util.getBorderClickedLocation(event.getBlockFace(), event.getClickedBlock().getLocation()).getChunk());
			boolean isInFaction = FactionUtil.isInFaction(player);
			boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
			//Handle building permissions
			if(chunkIsClaimed){
				Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
				if(!isInFaction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot use mechanics in " + chunkFaction.getName() + "&r&c's land.");
				}else{
					Faction faction = FactionUtil.getPlayerFaction(player);
					if(chunkFaction != faction){
						event.setCancelled(true);
						Util.message(player, "&cYou cannot use mechanics in " + chunkFaction.getName() + "&r&c's land.");
					}else if(!FactionUtil.hasPermission(player, RankPermission.USE_MECHANICS)){
						event.setCancelled(true);
						Util.message(player, "&cYour faction rank does not allow you to do this.");
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInteractContainerOpening(PlayerInteractEntityEvent event){
		Player player = event.getPlayer();
		if(player.isSneaking() && event.getRightClicked() instanceof Player){
			Player clicked = (Player)event.getRightClicked();
			if(!CitizensAPI.getNPCRegistry().isNPC(clicked)) Controller.handleClickedPlayerInfo(player, clicked);
		}
		
		if(!Util.isContainerEntity(event.getRightClicked())) return;
		String chunk = Serialization.chunkToString(event.getRightClicked().getLocation().getChunk());
		boolean isInFaction = FactionUtil.isInFaction(player);
		boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
		//Handle building permissions
		if(chunkIsClaimed){
			Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
			if(!isInFaction){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot open containers in " + chunkFaction.getName() + "&r&c's land.");
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(chunkFaction != faction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot open containers in " + chunkFaction.getName() + "&r&c's land.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.USE_CONTAINERS)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInteractContainerOpening(PlayerArmorStandManipulateEvent event){
		Player player = event.getPlayer();
		String chunk = Serialization.chunkToString(event.getRightClicked().getLocation().getChunk());
		boolean isInFaction = FactionUtil.isInFaction(player);
		boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
		//Handle building permissions
		if(chunkIsClaimed){
			Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
			if(!isInFaction){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot open containers in " + chunkFaction.getName() + "&r&c's land.");
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(chunkFaction != faction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot open containers in " + chunkFaction.getName() + "&r&c's land.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.USE_CONTAINERS)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(player.isOp()){
			event.setCancelled(false);
			return;
		}
		String chunk = Serialization.chunkToString(event.getBlock().getLocation().getChunk());
		boolean isInFaction = FactionUtil.isInFaction(player);
		boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
		
		//Handle chunk building permissions
		if(chunkIsClaimed){
			Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
			if(!isInFaction){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(chunkFaction != faction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.BUILD)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.USE_CONTAINERS) && Util.isContainer(event.getBlock())){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}
			}
		}
		//Handle stats
		if(isInFaction && !event.isCancelled()){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.addStat(StatType.BLOCKS_BROKEN, 1);
			if(Util.isOre(event.getBlock()) && player.getItemInHand() != null && !player.getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)){
				faction.addStat(StatType.ORES_BROKEN, 1);
				FactionUtil.checkChallenge(faction, player, new FactionChallenge[]{FactionChallenge.ORES_BROKEN_1, FactionChallenge.ORES_BROKEN_2, FactionChallenge.ORES_BROKEN_3}, StatType.ORES_BROKEN);
			}
		}	
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityBreak(EntityDamageByEntityEvent event){
		Entity entity = event.getEntity();
		if(!Util.isCointainerDamageableEntity(entity)) return;
		Player player = null;
		if(event.getDamager() instanceof Player) player = (Player)event.getDamager();
		else if(event.getDamager() instanceof Arrow){
			Arrow a = (Arrow)event.getDamager();
			if(a.getShooter() instanceof Player) player = (Player)a.getShooter();
			else return;
		}else return;
		
		String chunk = Serialization.chunkToString(entity.getLocation().getChunk());
		boolean isInFaction = FactionUtil.isInFaction(player);
		boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
		
		//Handle chunk building permissions
		if(chunkIsClaimed){
			Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
			if(!isInFaction){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(chunkFaction != faction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.BUILD)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.USE_CONTAINERS)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}
			}
		}
		//Handle stats
		if(isInFaction && !event.isCancelled()){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.addStat(StatType.BLOCKS_BROKEN, 1);
		}	
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHangingBreak(HangingBreakByEntityEvent event){
		Entity entity = event.getEntity();
		
		Player player = null;
		if(event.getRemover() instanceof Player) player = (Player)event.getRemover();
		else if(event.getRemover() instanceof Arrow){
			Arrow a = (Arrow)event.getRemover();
			if(a.getShooter() instanceof Player) player = (Player)a.getShooter();
			else return;
		}else return;
		
		String chunk = Serialization.chunkToString(entity.getLocation().getChunk());
		boolean isInFaction = FactionUtil.isInFaction(player);
		boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
		
		//Handle chunk building permissions
		if(chunkIsClaimed){
			Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
			if(!isInFaction){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(chunkFaction != faction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.BUILD)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.USE_CONTAINERS) && Util.isHangingContainer(entity)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}
			}
		}
		//Handle stats
		if(isInFaction && !event.isCancelled()){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.addStat(StatType.BLOCKS_BROKEN, 1);
		}	
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onVehicleDestroy(VehicleDestroyEvent event){
		Entity entity = event.getVehicle();
		
		Player player = null;
		if(event.getAttacker() instanceof Player) player = (Player)event.getAttacker();
		else if(event.getAttacker() instanceof Arrow){
			Arrow a = (Arrow)event.getAttacker();
			if(a.getShooter() instanceof Player) player = (Player)a.getShooter();
			else return;
		}else return;

		String chunk = Serialization.chunkToString(entity.getLocation().getChunk());
		boolean isInFaction = FactionUtil.isInFaction(player);
		boolean chunkIsClaimed = FactionUtil.isChunkClaimed(chunk);
		
		//Handle chunk building permissions
		if(chunkIsClaimed){
			Faction chunkFaction = FactionUtil.getChunkFaction(chunk);
			if(!isInFaction){
				event.setCancelled(true);
				Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
			}else{
				Faction faction = FactionUtil.getPlayerFaction(player);
				if(chunkFaction != faction){
					event.setCancelled(true);
					Util.message(player, "&cYou cannot build in " + chunkFaction.getName() + "&r&c's land.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.BUILD)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}else if(!FactionUtil.hasPermission(player, RankPermission.USE_CONTAINERS) && Util.isVehicleContainer(entity)){
					event.setCancelled(true);
					Util.message(player, "&cYour faction rank does not allow you to do this.");
				}
			}
		}
		//Handle stats
		if(isInFaction && !event.isCancelled()){
			Faction faction = FactionUtil.getPlayerFaction(player);
			faction.addStat(StatType.BLOCKS_BROKEN, 1);
		}
	}
}
