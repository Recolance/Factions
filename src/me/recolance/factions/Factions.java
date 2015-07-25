package me.recolance.factions;

import me.recolance.factions.commands.CommandHandler;
import me.recolance.factions.controller.Controller;
import me.recolance.factions.controller.Events;
import me.recolance.factions.data.DataHandler;
import me.recolance.factions.data.DataHolder;
import me.recolance.factions.data.Database;
import me.recolance.factions.menu.IconSelectionContainer;
import me.recolance.factions.menu.MenuController;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Factions extends JavaPlugin{
	
	public static Plugin plugin;
	
	public void onEnable(){
		plugin = this;
		saveDefaultConfig();
		registration();
		Database.openDatabaseConnection();
		DataHandler.generateTables();
		DataHandler.loadData();
		IconSelectionContainer.loadButtons();
		if(Controller.isNewDay()){
			Controller.runNewDayResets();
			Controller.setDay();
		}
		DataHandler.saveFactionsTimer();
		Controller.spawnNPC();
		DataHolder.setXPItem();
		Controller.givePowerOverTime();
	}

	public void onDisable(){
		DataHandler.saveAllFactions(true);
		Database.closeConnection();
		plugin = null;
	}
	
	private void registration(){
		getServer().getPluginManager().registerEvents(new MenuController(), this);
		getServer().getPluginManager().registerEvents(new Events(), this);
		
		getCommand("factions").setExecutor(new CommandHandler());
		getCommand("fadmin").setExecutor(new CommandHandler());
	}
}
