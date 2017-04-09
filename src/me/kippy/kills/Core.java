package me.kippy.kills;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class Core extends JavaPlugin implements Listener {
	public Logger logger = getLogger();
	ScoreboardManager sm;
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		new CustomPlayer(e.getPlayer());
		
		Scoreboard b = sm.getNewScoreboard();
		
		Objective o = b.registerNewObjective("kill", "dummy");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(getConfig().getString("ScoreboardMessage"));
		
		Score kills = o.getScore(ChatColor.GREEN + "Kills:");
		kills.setScore(0);
		
		e.getPlayer().setScoreboard(b);
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		CustomPlayer p = CustomPlayer.getPlayer(e.getPlayer());
		p.delete();
	}
	
	@EventHandler
	public void onPlayerKill(PlayerDeathEvent e) {
		if(e.getEntity().getKiller() instanceof Player) {
			Player p = e.getEntity().getKiller();
			CustomPlayer cp = CustomPlayer.getPlayer(p);
			cp.addKill();
			p.sendMessage("You killed " + e.getEntity().getName() + ". You know have " + cp.getKills() + " kills.");
			
			Scoreboard b = sm.getNewScoreboard();
			Objective o = b.registerNewObjective("kill", "dummy");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			o.setDisplayName("Kill Count Plugin");
			Score kills = o.getScore(ChatColor.GREEN + "Kills:");
			kills.setScore(cp.getKills());
			p.setScoreboard(b);
		}
	}
	
	@Override
	public void onEnable() {
		this.logger.info("Kill Counter has been enabled!");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		saveDefaultConfig();
		reloadConfig();
		
		sm = Bukkit.getScoreboardManager();
	}
	
	@Override
	public void onDisable() {
		this.logger.info("Word Changer has been disabled!");
	}
	
	
	private static class CustomPlayer {
		static ArrayList<CustomPlayer> players = new ArrayList<CustomPlayer>();
		
		String name;
		int kills;
		
		private CustomPlayer(Player p) {
			this.name = p.getName();
			this.kills = 0;
			
			players.add(this);
		}
		
		public String getName() {
			return name;
		}
		
		public void delete() {
			for(int i = 0; i < players.size(); i++) {
				if(this.getName() == players.get(i).getName()) {
					players.remove(i);
					break;
				}
			}
		}
		
		public void addKill() {
			this.kills++;
		}
		
		public int getKills() {
			return this.kills;
		}
		
		public static CustomPlayer getPlayer(Player p) {
			for(int i = 0; i < players.size(); i++) {
				if(p.getName() == players.get(i).getName()) {
					return players.get(i);
				}
			}
			return new CustomPlayer(p);
		}
		
	}
}
