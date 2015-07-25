package me.recolance.factions.faction;

public enum FactionStatus{

	CLOSED(1),
	OPEN(2);
	
	int id;
	
	FactionStatus(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public static FactionStatus getFactionStatus(int id){
		for(FactionStatus status : FactionStatus.values()){
			if(status.getId() == id) return status;
		}
		return null;
	}
}
