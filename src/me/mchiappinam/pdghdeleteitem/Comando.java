package me.mchiappinam.pdghdeleteitem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comando implements CommandExecutor {

	private Main plugin;
	public Comando(Main main) {
		plugin=main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("deleteitem")) {
			if(sender!=plugin.getServer().getConsoleSender())
				if(!sender.hasPermission("pdgh.op")) {
					sender.sendMessage("§cSem permissões.");
					return true;
				}
			if((args.length==0)||(args.length>3)) {
				plugin.help((Player)sender);
				return true;
			}else if(args[0].equalsIgnoreCase("reload")) {
				if(sender==plugin.getServer().getConsoleSender()) {
					if(args.length>1) {
						plugin.getServer().getConsoleSender().sendMessage("§cUse /deleteitem reload");
						return true;
					}
					plugin.getServer().getConsoleSender().sendMessage("§aRecarregando...");
				}else{
					if(args.length>1) {
						sender.sendMessage("§cUse /deleteitem reload");
						return true;
					}
					sender.sendMessage("§aRecarregando...");
				}
				plugin.reloadConfig();
				plugin.banidos.clear();
				plugin.banidosNAOREMOVER.clear();
				for(String s : plugin.getConfig().getStringList("banidos"))
					plugin.banidos.add(s);
				for(String s : plugin.getConfig().getStringList("banidosNAOREMOVER"))
					plugin.banidosNAOREMOVER.add(s);
				return true;
			}else if(args[0].equalsIgnoreCase("add")) {
				if(sender==plugin.getServer().getConsoleSender()) {
					if(args.length!=3) {
						plugin.getServer().getConsoleSender().sendMessage("§cUse /deleteitem add <banidos/banidosnaoremover> <id[:data]>");
						return true;
					}
				}else {
					if(args.length!=3) {
						sender.sendMessage("§cUse /deleteitem add <banidos/banidosnaoremover> <id[:data]>");
						return true;
					}
				}
				if(args[1].equalsIgnoreCase("banidos")) {
					if(plugin.banidos.contains(args[2])) {
						if(sender==plugin.getServer().getConsoleSender())
							plugin.getServer().getConsoleSender().sendMessage("§cItem "+args[2]+" ja estava banido - banidos.");
						else
							sender.sendMessage("§cItem "+args[2]+" já estava banido - banidos.");
						return true;
					}
					plugin.banidos.add(args[2]);
					plugin.getConfig().set("banidos", plugin.banidos);
					plugin.saveConfig();
					if(sender==plugin.getServer().getConsoleSender())
						plugin.getServer().getConsoleSender().sendMessage("§aItem "+args[2]+" banido - banidos.");
					else
						sender.sendMessage("§aItem "+args[2]+" banido - banidos.");
				}else if(args[1].equalsIgnoreCase("banidosnaoremover")) {
					if(plugin.banidosNAOREMOVER.contains(args[2])) {
						if(sender==plugin.getServer().getConsoleSender())
							plugin.getServer().getConsoleSender().sendMessage("§cItem "+args[2]+" ja estava banido - banidosnaoremover.");
						else
							sender.sendMessage("§cItem "+args[2]+" já estava banido - banidosnaoremover.");
						return true;
					}
					plugin.banidosNAOREMOVER.add(args[2]);
					plugin.getConfig().set("banidosNAOREMOVER", plugin.banidosNAOREMOVER);
					plugin.saveConfig();
					if(sender==plugin.getServer().getConsoleSender())
						plugin.getServer().getConsoleSender().sendMessage("§aItem "+args[2]+" banido - banidosnaoremover.");
					else
						sender.sendMessage("§aItem "+args[2]+" banido - banidosnaoremover.");
				}
				return true;
			}else if(args[0].equalsIgnoreCase("remove")) {///////////////////////////////////////////////////////////////////////
				if(sender==plugin.getServer().getConsoleSender()) {
					if(args.length!=3) {
						plugin.getServer().getConsoleSender().sendMessage("§cUse /deleteitem remove <banidos/banidosnaoremover> <id[:data]>");
						return true;
					}
				}else {
					if(args.length!=3) {
						sender.sendMessage("§cUse /deleteitem remove <banidos/banidosnaoremover> <id[:data]>");
						return true;
					}
				}
				if(args[1].equalsIgnoreCase("banidos")) {
					if(!plugin.banidos.contains(args[2])) {
						if(sender==plugin.getServer().getConsoleSender())
							plugin.getServer().getConsoleSender().sendMessage("§cItem "+args[2]+" nao estava banido - banidos.");
						else
							sender.sendMessage("§cItem "+args[2]+" não estava banido - banidos.");
						return true;
					}
					plugin.banidos.remove(args[2]);
					plugin.getConfig().set("banidos", plugin.banidos);
					plugin.saveConfig();
					if(sender==plugin.getServer().getConsoleSender())
						plugin.getServer().getConsoleSender().sendMessage("§aItem "+args[2]+" desbanido - banidos.");
					else
						sender.sendMessage("§aItem "+args[2]+" desbanido - banidos.");
				}else if(args[1].equalsIgnoreCase("banidosnaoremover")) {
					if(!plugin.banidosNAOREMOVER.contains(args[2])) {
						if(sender==plugin.getServer().getConsoleSender())
							plugin.getServer().getConsoleSender().sendMessage("§cItem "+args[2]+" nao estava banido - banidosnaoremover.");
						else
							sender.sendMessage("§cItem "+args[2]+" não estava banido - banidosnaoremover.");
						return true;
					}
					plugin.banidosNAOREMOVER.remove(args[2]);
					plugin.getConfig().set("banidosNAOREMOVER", plugin.banidosNAOREMOVER);
					plugin.saveConfig();
					if(sender==plugin.getServer().getConsoleSender())
						plugin.getServer().getConsoleSender().sendMessage("§aItem "+args[2]+" desbanido - banidosnaoremover.");
					else
						sender.sendMessage("§aItem "+args[2]+" desbanido - banidosnaoremover.");
				}
				return true;
			}else{
				plugin.help((Player)sender);
				return true;
			}
		}
		return false;
	}
}
