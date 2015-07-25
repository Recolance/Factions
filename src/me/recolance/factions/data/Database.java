package me.recolance.factions.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.recolance.factions.Factions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Database{
	
	private static Connection connection;
	private static Plugin plugin = Factions.plugin;
	private static FileConfiguration config = plugin.getConfig();
	
	public static void openDatabaseConnection(){
		try{
			connection = DriverManager.getConnection("jdbc:mysql://" + config.getString("sql.host") + ":" + config.getString("sql.port") + "/" + config.getString("sql.database"), config.getString("sql.user"), config.getString("sql.pass"));
		}catch(SQLException e){
			System.out.println("ERROR CONNECTING TO DATABASE!");
			e.printStackTrace();
		}
	}

	public static void closeConnection(){
		try{
			connection.close();
		}catch (SQLException e){
			System.out.println("COULD NOT CLOSE CONNECTION!");
			e.printStackTrace();
		}
	}

	public static Connection getConnection(){
		try{
			if(connection == null || connection.isClosed()) openDatabaseConnection();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return connection;
	}
}
