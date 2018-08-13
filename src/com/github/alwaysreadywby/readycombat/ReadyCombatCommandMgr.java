package com.github.alwaysreadywby.readycombat;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReadyCombatCommandMgr {

	public static void onCommandReceive(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("rcombat") || label.equalsIgnoreCase("readycombat")) {
			if(args.length>=1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission("readycombat.admin")) {
				ReadyCombat.reload();
				ReadyCombat.info(sender,"%lang.msg.loaded%");
			}else if(args.length>=1 && args[0].equalsIgnoreCase("disp") && sender.hasPermission("readycombat.display")) {
				if(sender instanceof Player) {
					if(args.length>=2) {
						if(args[1].equalsIgnoreCase("true")) {
							ReadyCombat.getScoreboard().showFor((Player) sender);
						}else if(args[1].equalsIgnoreCase("false")) {
							ReadyCombat.getScoreboard().hideFor((Player) sender);
						}
					}else {
						ReadyCombat.getScoreboard().toggleFor((Player) sender);
					}
				}
			}else if(args.length>=2 && args[0].equalsIgnoreCase("regen") && sender.hasPermission("readycombat.change")) {
				OfflinePlayer target=null;
				if(args.length>=3) {
					if(!(target=Bukkit.getOfflinePlayer(args[2])).isOnline()) {
						ReadyCombat.info(sender, "%lang.help.regen% - %lang.msg.online-only%");
						return;
					}
				}else if(sender instanceof Player){
					target=(OfflinePlayer) sender;
				}else {
					ReadyCombat.info(sender, "%lang.msg.player-only%");
					return;
				}
				try {
					ReadyCombatPower.regenPower(target.getPlayer().getUniqueId(), Double.parseDouble(args[1]));
				}catch (NumberFormatException e) {
					ReadyCombat.info(sender, "%lang.help.regen% - %lang.msg.number-only%");
				}
			}else if(args.length>=2 && args[0].equalsIgnoreCase("cost") && sender.hasPermission("readycombat.change")){
				OfflinePlayer target=null;
				if(args.length>=3) {
					if(!(target=Bukkit.getOfflinePlayer(args[2])).isOnline()) {
						ReadyCombat.info(sender, "%lang.help.cost% - %lang.msg.online-only%");
						return;
					}
				}else if(sender instanceof Player){
					target=(OfflinePlayer) sender;
				}else {
					ReadyCombat.info(sender, "%lang.msg.player-only%");
					return;
				}
				try {
					ReadyCombatPower.reducePower(target.getPlayer().getUniqueId(), Double.parseDouble(args[1]));
				}catch (NumberFormatException e) {
					ReadyCombat.info(sender, "%lang.help.cost% - %lang.msg.number-only%");
				}
			}else if(args.length>=1 && args[0].equalsIgnoreCase("version")) {
				ReadyCombat.info(sender, "%lang.msg.title%");
				ReadyCombat.info(sender, "%lang.msg.version%",ReadyCombat.getPlugin().getDescription().getFullName());
				ReadyCombat.info(sender, "%lang.msg.author%",ReadyCombat.getPlugin().getDescription().getAuthors().get(0));
				ReadyCombat.info(sender, "%lang.msg.mail%","wby27_2006@126.com");
			}else {
				ReadyCombat.info(sender, "%lang.msg.title%");
				if(sender.hasPermission("readycombat.change")) {
					ReadyCombat.info(sender, "%lang.help.regen%");
					ReadyCombat.info(sender, "%lang.help.cost%");
				}
				if(sender.hasPermission("readycombat.admin")) {
					ReadyCombat.info(sender, "%lang.help.reload%");
					ReadyCombat.info(sender, "%lang.help.version%");
				}
				if((sender instanceof Player) && sender.hasPermission("readycombat.display")) {
					ReadyCombat.info(sender, "%lang.help.disp%");
				}
				ReadyCombat.info(sender, "%lang.help.help%");
			}
		}
	}

}
