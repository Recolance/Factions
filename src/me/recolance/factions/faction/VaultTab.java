package me.recolance.factions.faction;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VaultTab{

	private String name;
	private ItemStack icon;
	private Inventory contents;
	private int tabNumber;
	
	public VaultTab(String name, ItemStack icon, Inventory contents, int tabNumber){
		this.name = name;
		this.icon = icon;
		this.contents = contents;
		this.tabNumber = tabNumber;
	}
	
	protected VaultTab(String name, int tabNumber){
		this.name = name;
		this.icon = new ItemStack(Material.CHEST, 1, (short)0);
		this.contents = Bukkit.createInventory(null, 54, "Vault");
		this.tabNumber = tabNumber;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public ItemStack getIcon(){
		return icon;
	}

	public void setIcon(ItemStack icon){
		this.icon = icon;
	}
	
	public Inventory getContents(){
		return contents;
	}

	public void setContents(Inventory contents){
		this.contents = contents;
	}
	
	public int getTabNumber(){
		return this.tabNumber;
	}
}
