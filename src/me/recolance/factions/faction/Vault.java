package me.recolance.factions.faction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.recolance.factions.util.FactionUtil;
import me.recolance.factions.util.Util;

public class Vault{

	private UUID faction;
	private VaultTab tab1;
	private VaultTab tab2;
	private VaultTab tab3;
	private VaultTab tab4;
	private int money;
	private List<String> log;
	
	public Vault(UUID faction, VaultTab tab1, VaultTab tab2, VaultTab tab3, VaultTab tab4, int money, List<String> log){
		this.faction = faction;
		this.tab1 = tab1;
		this.tab2 = tab2;
		this.tab3 = tab3;
		this.tab4 = tab4;
		this.money = money;
		this.log = log;
	}
	
	//Method should be calledwhen creating a new faction.
	public Vault(UUID faction){
		this.faction = faction;
		this.tab1 = new VaultTab("Tab1", 1);
		this.tab2 = new VaultTab("Tab2", 2);
		this.tab3 = new VaultTab("Tab3", 3);
		this.tab4 = new VaultTab("Tab4", 4);
		this.money = 0;
		this.log = new ArrayList<String>();
	}
	
	public UUID getFaction(){
		return this.faction;
	}
	
	public void setFaction(UUID faction){
		this.faction = faction;
	}

	public VaultTab getTab1(){
		return tab1;
	}

	public void setTab1(VaultTab tab1){
		this.tab1 = tab1;
	}


	public VaultTab getTab2(){
		return tab2;
	}


	public void setTab2(VaultTab tab2){
		this.tab2 = tab2;
	}


	public VaultTab getTab3(){
		return tab3;
	}


	public void setTab3(VaultTab tab3){
		this.tab3 = tab3;
	}


	public VaultTab getTab4(){
		return tab4;
	}


	public void setTab4(VaultTab tab4){
		this.tab4 = tab4;
	}

	public int getMoney(){
		return money;
	}

	public void setMoney(int money){
		this.money = money;
	}

	public List<String> getLog(){
		return log;
	}
	public void setLog(List<String> log){
		this.log = log;
	}
	
	public void addLogEntry(String entry){
		Faction faction = FactionUtil.getFactionFromId(this.faction);
		faction.setVaultSaveable(true);
		faction.setSaveable(true);
		if(log.size() >= 30) log.remove(29);
		this.log.add(0, Util.setStringColors("&9" + Util.getMonthDayDate() + ": " + entry));
	}
}
