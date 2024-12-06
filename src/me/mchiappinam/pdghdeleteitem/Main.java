package me.mchiappinam.pdghdeleteitem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	List<String> banidos = new ArrayList<String>();
	List<String> banidosNAOREMOVER = new ArrayList<String>();

	public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2iniciando...");
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2verificando se a config existe...");
		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2config inexistente, criando config...");
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
				getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2config criada");
			}catch(Exception e) {getLogger().warning("ERRO: Não foi possível criar a config. Mais detalhes: "+e.toString());}
		}
		getServer().getPluginManager().registerEvents(this, this);
		checkInventory();
		//checkEnchants();
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2definindo comandos...");
	    getServer().getPluginCommand("deleteitem").setExecutor(new Comando(this));
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2comandos definidos");
		for(String s : getConfig().getStringList("banidos"))
			banidos.add(s);
		for(String s : getConfig().getStringList("banidosNAOREMOVER"))
			banidosNAOREMOVER.add(s);
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2ativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2Acesse: http://pdgh.com.br/");
	}

	public void onDisable() {
		banidos.clear();
		banidosNAOREMOVER.clear();
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2desativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHDeleteItem] §2Acesse: http://pdgh.com.br/");
	}

	public void help(Player p) {
		if(p.hasPermission("pdgh.op")) {
			p.sendMessage("§3§lPDGH Delete Item - Ajuda");
			p.sendMessage("§d<obrigatório> [opcional]");
			p.sendMessage("§2/deleteitem add <banidos/banidosnaoremover> <id[:data]> -§a- Adiciona um ID. (se não colocar data (322:1), por exemplo, se colocar somente 322, vai banir todos)");
			p.sendMessage("§2/deleteitem remove <banidos/banidosnaoremover> <id[:data]> -§a- Remove um ID.");
			p.sendMessage("§2/deleteitem reload -§a- Recarrega a config do plugin.");
		}else{
			p.sendMessage("§cSem permissões.");
		}
		/**if(p.hasPermission("pdgh.moderador")) {
			p.sendMessage("§4>=Moderador...");
			p.sendMessage("§4Seus comandos são diferentes...:");
			p.sendMessage("§4/reportar lista -- Mostra todos os reportes não resolvidos.");
			p.sendMessage("§4/reportar buscar <id> -- Mostra informações do reporte desejado.");
			p.sendMessage("§4/reportar resolver <id> <resposta da staff. exemplo: 'punido por uso de hacker'> -- Resolve um reporte pendente.");
		}*/
	}
	
	public boolean checkBan(int item, int dt) {
		for(String lo : banidos) {
			String[] me=lo.split(":");
			if(Integer.parseInt(me[0])==item) {
				if(lo.contains(":")) {
					if(Integer.parseInt(me[1])==dt)
						return true;
				}else{
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean checkBanNAOREMOVER(int item, int dt) {
		for(String lo : banidosNAOREMOVER) {
			String[] me=lo.split(":");
			if(Integer.parseInt(me[0])==item) {
				if(lo.contains(":")) {
					if(Integer.parseInt(me[1])==dt)
						return true;
				}else{
					return true;
				}
			}
		}
		return false;
	}

	@EventHandler
	private void onInventoryOpen(InventoryOpenEvent e) {
		Player p = (Player)e.getPlayer();
		for(ItemStack i : e.getPlayer().getInventory().getContents()) {
			if(i!=null) {
				if(checkBan(i.getType().getId(), (byte)i.getData().getData())) {
					p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
					p.sendMessage("§c[PDGH] §e"+i.getType().getId()+":"+(byte)i.getData().getData());
					p.getInventory().removeItem(new ItemStack(i));
					p.closeInventory();
				}
			}
		}
	}
	
	private void checkInventory() { // 
	  	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			for(Player p : getServer().getOnlinePlayers())
	  				for(ItemStack i : p.getInventory().getContents()) {
	  					if(i!=null) {
	  						if(checkBan(i.getType().getId(), (byte)i.getData().getData())) {
	  							p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
	  							p.sendMessage("§c[PDGH] §e"+i.getType().getId()+":"+(byte)i.getData().getData());
	  							p.getInventory().removeItem(new ItemStack(i));
	  							p.closeInventory();
	  						}
	  						if(i.getType().getId()==18586) {
	  							if(i.getAmount()>=2) {
	  								p.getInventory().removeItem(new ItemStack(i));
	  					    		getServer().dispatchCommand(getServer().getConsoleSender(), "ban "+p.getName()+" 1200 Dup de itens");
	  							}
	  						}
	  					}
	  				}
	  		}
	  	}, 0, 2*20);
	}
	
	/**private void checkEnchants() {
	  	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			for(Player p : getServer().getOnlinePlayers()) {
	  				for(ItemStack i : p.getInventory().getContents()) {
	  					if(i!=null) {
	  						if(i.getEnchantmentLevel(Enchantment.getById(247))>=1) {
	  							i.removeEnchantment(Enchantment.getById(247));
	  							p.sendMessage("§c[PDGH] §eVocê tinha um item com o encantamento Soul Tether e o mesmo foi removido.");
	  						}
	  					}
	  				}
	  				if((p.getInventory().getHelmet()!=null)&&(p.getInventory().getHelmet().getEnchantmentLevel(Enchantment.getById(247))>=1)) {
						p.getInventory().getHelmet().removeEnchantment(Enchantment.getById(247));
						p.sendMessage("§c[PDGH] §eVocê tinha um item com o encantamento Soul Tether e o mesmo foi removido.");
					}
					if((p.getInventory().getChestplate()!=null)&&(p.getInventory().getChestplate().getEnchantmentLevel(Enchantment.getById(247))>=1)) {
						p.getInventory().getChestplate().removeEnchantment(Enchantment.getById(247));
						p.sendMessage("§c[PDGH] §eVocê tinha um item com o encantamento Soul Tether e o mesmo foi removido.");
					}
					if((p.getInventory().getLeggings()!=null)&&(p.getInventory().getLeggings().getEnchantmentLevel(Enchantment.getById(247))>=1)) {
						p.getInventory().getLeggings().removeEnchantment(Enchantment.getById(247));
						p.sendMessage("§c[PDGH] §eVocê tinha um item com o encantamento Soul Tether e o mesmo foi removido.");
					}
					if((p.getInventory().getBoots()!=null)&&(p.getInventory().getBoots().getEnchantmentLevel(Enchantment.getById(247))>=1)) {
						p.getInventory().getBoots().removeEnchantment(Enchantment.getById(247));
						p.sendMessage("§c[PDGH] §eVocê tinha um item com o encantamento Soul Tether e o mesmo foi removido.");
					}
	  			}
	  		}
	  	}, 0, 60*20);
	}*/

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
	    final Player p = e.getPlayer();
	    ItemStack item = e.getItem().getItemStack();
		if(checkBan(item.getType().getId(), (byte)item.getData().getData())) {
			e.setCancelled(true);
			p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
			p.sendMessage("§c[PDGH] §e"+item.getType().getId()+":"+(byte)item.getData().getData());
			e.getItem().remove();
			p.closeInventory();
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					p.closeInventory();
				}
			}, 1);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
	    final Player p = (Player)e.getWhoClicked();
	    ItemStack item = e.getCurrentItem();
	    if ((e.getCurrentItem() != null)&&(!e.getCurrentItem().getType().equals(Material.AIR)))
			if(checkBan(item.getType().getId(), (byte)item.getData().getData())) {
				e.setCancelled(true);
				p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
				p.sendMessage("§c[PDGH] §e"+item.getType().getId()+":"+(byte)item.getData().getData());
				p.getInventory().removeItem(new ItemStack(item));
				p.closeInventory();
				getServer().getScheduler().runTaskLater(this, new Runnable() {
					public void run() {
						p.closeInventory();
					}
				}, 1);
		    }
	}

	@EventHandler
	public void onItemCrafted(CraftItemEvent e) {
	    final Player p = (Player)e.getWhoClicked();
	    ItemStack item = e.getRecipe().getResult();
		if(checkBan(item.getType().getId(), (byte)item.getData().getData())) {
			e.setCancelled(true);
			p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
			p.sendMessage("§c[PDGH] §e"+item.getType().getId()+":"+(byte)item.getData().getData());
			p.closeInventory();
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					p.closeInventory();
				}
			}, 1);
	    }
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		final Player p = e.getPlayer();
		ItemStack item = e.getPlayer().getItemInHand();
		if(checkBan(item.getType().getId(), (byte)item.getData().getData())) {
			e.setCancelled(true);
			p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
			p.sendMessage("§c[PDGH] §e"+item.getType().getId()+":"+(byte)item.getData().getData());
			p.getInventory().removeItem(new ItemStack(item));
			p.closeInventory();
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					p.closeInventory();
				}
			}, 1);
	    }

		if(checkBanNAOREMOVER(item.getType().getId(), (byte)item.getData().getData())) {
			e.setCancelled(true);
			p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
			p.sendMessage("§c[PDGH] §e"+item.getType().getId()+":"+(byte)item.getData().getData());
	    }
	}

	/**@EventHandler(priority=EventPriority.MONITOR)
	public void onEnchant(EnchantItemEvent e) {
		if(e.getEnchantsToAdd().containsKey(247)) {
			getServer().broadcastMessage("247");
		}
		Map<Enchantment, Integer> ec=e.getEnchantsToAdd();
		if(ec.containsKey(Enchantment.getById(247))) {
			getServer().broadcastMessage(">> 247");
		}
	}*/
	  
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
	    final Player p = e.getPlayer();
	    ItemStack item = e.getItem();
	    Block block = e.getClickedBlock();
	    if((e.getAction() == Action.RIGHT_CLICK_BLOCK)||(e.getAction() == Action.LEFT_CLICK_BLOCK)) {
	    	if(checkBan(block.getType().getId(), (byte)block.getData())) {
				e.setCancelled(true);
				p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
				p.sendMessage("§c[PDGH] §e"+block.getType().getId()+":"+(byte)block.getData());
				for(Player stf : getServer().getOnlinePlayers()) {
					if(stf.hasPermission("pdgh.admin")) {
						stf.sendMessage("§c[PDGH] §cItem banido no mundo. Mais informações:");
						stf.sendMessage("§c[PDGH] §cJogador: "+p.getName()+". Item: "+block.getType().getId()+":"+(byte)block.getData()+". Mundo: "+block.getWorld().getName()+". X: "+block.getX()+", Y: "+block.getY()+", Z: "+block.getZ());
					}
				}
				block.setType(Material.AIR);
				p.closeInventory();
				getServer().getScheduler().runTaskLater(this, new Runnable() {
					public void run() {
						p.closeInventory();
					}
				}, 1);
	    	}


			if(checkBanNAOREMOVER(block.getType().getId(), (byte)block.getData())) {
				e.setCancelled(true);
				p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
				p.sendMessage("§c[PDGH] §e"+block.getType().getId()+":"+(byte)block.getData());
				for(Player stf : getServer().getOnlinePlayers()) {
					if(stf.hasPermission("pdgh.admin")) {
						stf.sendMessage("§c[PDGH] §cItem banido no mundo. Mais informações:");
						stf.sendMessage("§c[PDGH] §cJogador: "+p.getName()+". Item: "+block.getType().getId()+":"+(byte)block.getData()+". Mundo: "+block.getWorld().getName()+". X: "+block.getX()+", Y: "+block.getY()+", Z: "+block.getZ());
					}
				}
				block.setType(Material.AIR);
		    }
			
	    }else if((item!=null)) {
	    	if(checkBan(item.getType().getId(), (byte)item.getData().getData())) {
				e.setCancelled(true);
				p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
				p.sendMessage("§c[PDGH] §e"+item.getType().getId()+":"+(byte)item.getData().getData());
				p.getInventory().removeItem(new ItemStack(item));
				p.closeInventory();
				getServer().getScheduler().runTaskLater(this, new Runnable() {
					public void run() {
						p.closeInventory();
					}
				}, 1);
	    	}
	    	


	    	if(checkBanNAOREMOVER(item.getType().getId(), (byte)item.getData().getData())) {
				e.setCancelled(true);
				p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
				p.sendMessage("§c[PDGH] §e"+item.getType().getId()+":"+(byte)item.getData().getData());
		    }
	    }
	}
    
	/**@EventHandler
    private void onInteract(PlayerInteractEvent e) {
		if((e.getAction() == Action.RIGHT_CLICK_BLOCK)||(e.getAction() == Action.LEFT_CLICK_BLOCK)) {
			if(checkBan(e.getClickedBlock().getType().getId(), (byte)e.getClickedBlock().getData())) {
				e.setCancelled(true);
				e.getClickedBlock().setType(Material.AIR);
				final Player p = e.getPlayer();
				p.closeInventory();
				getServer().getScheduler().runTaskLater(this, new Runnable() {
					public void run() {
						p.closeInventory();
					}
				}, 1);
				p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
				p.sendMessage("§c[PDGH] §e"+e.getClickedBlock().getType().getId()+":"+(byte)e.getClickedBlock().getData());
			}
		}
	}*/
    
	@EventHandler(priority=EventPriority.LOWEST)
    private void onBlockBreak(BlockBreakEvent e) {
		if(checkBan(e.getBlock().getType().getId(), (byte)e.getBlock().getData())) {
			//e.setCancelled(true);
			final Player p = e.getPlayer();
			p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
			p.sendMessage("§c[PDGH] §e"+e.getBlock().getType().getId()+":"+(byte)e.getBlock().getData());
			e.getBlock().setType(Material.AIR);
			p.closeInventory();
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					p.closeInventory();
				}
			}, 1);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
    private void onPlace(BlockPlaceEvent e) {
		if(checkBan(e.getBlock().getType().getId(), (byte)e.getBlock().getData())) {
			e.setCancelled(true);
			final Player p = e.getPlayer();
			p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
			p.sendMessage("§c[PDGH] §e"+e.getBlock().getType().getId()+":"+(byte)e.getBlock().getData());
			p.getInventory().removeItem(new ItemStack(e.getItemInHand()));
			p.closeInventory();
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				public void run() {
					p.closeInventory();
				}
			}, 1);
		}

		if(checkBanNAOREMOVER(e.getBlock().getType().getId(), (byte)e.getBlock().getData())) {
			e.setCancelled(true);
			final Player p = e.getPlayer();
			p.sendMessage("§c[PDGH] §eItem banido. Mais informações:");
			p.sendMessage("§c[PDGH] §e"+e.getBlock().getType().getId()+":"+(byte)e.getBlock().getData());
		}
		
		
		
		/**if(e.getBlock().getType().getId()==19416)
			tubo(e.getBlock(), e.getPlayer(), 19416);
		else if(e.getBlock().getType().getId()==19417)
			tubo(e.getBlock(), e.getPlayer(), 19417);
		else if(e.getBlock().getType().getId()==19418)
			tubo(e.getBlock(), e.getPlayer(), 19418);
		else if(e.getBlock().getType().getId()==19419)
			tubo(e.getBlock(), e.getPlayer(), 19419);
		else if(e.getBlock().getType().getId()==19420)
			tubo(e.getBlock(), e.getPlayer(), 19420);
		else if(e.getBlock().getType().getId()==19421)
			tubo(e.getBlock(), e.getPlayer(), 19421);
		else if(e.getBlock().getType().getId()==19422)
			tubo(e.getBlock(), e.getPlayer(), 19422);
		else if(e.getBlock().getType().getId()==19436)
			tubo(e.getBlock(), e.getPlayer(), 19436);
		else if(e.getBlock().getType().getId()==19437)
			tubo(e.getBlock(), e.getPlayer(), 19437);
		else if(e.getBlock().getType().getId()==19438)
			tubo(e.getBlock(), e.getPlayer(), 19438);
		else if(e.getBlock().getType().getId()==19439)
			tubo(e.getBlock(), e.getPlayer(), 19439);
		else if(e.getBlock().getType().getId()==19440)
			tubo(e.getBlock(), e.getPlayer(), 19440);
		else if(e.getBlock().getType().getId()==19456)
			tubo(e.getBlock(), e.getPlayer(), 19456);
		else if(e.getBlock().getType().getId()==19458)
			tubo(e.getBlock(), e.getPlayer(), 19458);
		else if(e.getBlock().getType().getId()==19460)
			tubo(e.getBlock(), e.getPlayer(), 19460);
		else if(e.getBlock().getType().getId()==19476)
			tubo(e.getBlock(), e.getPlayer(), 19476);
		else if(e.getBlock().getType().getId()==19478)
			tubo(e.getBlock(), e.getPlayer(), 19478);
		else if(e.getBlock().getType().getId()==19479)
			tubo(e.getBlock(), e.getPlayer(), 19479);
		else if(e.getBlock().getType().getId()==19480)
			tubo(e.getBlock(), e.getPlayer(), 19480);
	}
	
	public void tubo(Block b, Player p, int id) {
		b.setType(Material.AIR);
		b.setTypeId(id);
		p.sendMessage("§3[ITEM ESPECIAL] §cVocê colocou um tubo de transporte!");*/
	}
    
	/**@SuppressWarnings("deprecation")
	@EventHandler
    private void onMove(PlayerMoveEvent e) {
    	for(int x=-5;x<6;x++)
    		for(int y=-5;y<6;y++)
    			for(int z=-5;z<6;z++) {
    				Block b = e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation().getBlockX()+x, e.getPlayer().getLocation().getBlockY()+y, e.getPlayer().getLocation().getBlockZ()+z);
			    	if((b.getTypeId()==154)||(b.getTypeId()==122)) {
			    		e.setCancelled(true);
			    		b.setType(Material.AIR);
			    	}
    			}
    }*/
}

