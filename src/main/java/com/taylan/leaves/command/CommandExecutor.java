package com.taylan.leaves.command;


import com.taylan.leaves.Leaves;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExecutor implements org.bukkit.command.CommandExecutor {
	
	private final String tag = "[" + Leaves.getInstance().getDescription().getName() +
			"/" + Leaves.getInstance().getDescription().getVersion() + "]";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!sender.isOp()) {
			sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[<aqua>Remiel<gray>] Sadece operatörler kullanabilir."));
			return true;
		}
		
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.RED + tag + "Reloadlanıyor...");
		Leaves.getInstance().getLogger().info("Reloadlanıyor...");
		
		Leaves.getInstance().reload();
		
		Leaves.getInstance().getLogger().info("Reload tamamlandı!");
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.GREEN + tag + " Reload tamamlandı!");
		
		return true;
	}
	
}