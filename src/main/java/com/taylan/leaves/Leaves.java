package com.taylan.leaves;


import com.taylan.leaves.map.LeafMap;
import com.taylan.leaves.command.CommandExecutor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Leaves extends JavaPlugin {
	
	@Getter
	private static Leaves instance;

	@Getter
	private LeafMap map;
	
	{
		instance = this;
	}
	
	@Override
    public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		map = new LeafMap();
		
		getCommand("leavesreload").setExecutor(new CommandExecutor());
	}

	@Override
    public void onDisable() {
		map.removeProcesses();
		Bukkit.getScheduler().cancelTasks(this);
	}

	public void reload() {
		reloadConfig();
		map.reload();
	}
}
