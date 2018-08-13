package com.github.alwaysreadywby.readycombat;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scoreboard.ScoreboardManager;

public class ReadyCombatServerTickTask implements Runnable {
	
	private TaskType eType;
	
	public ReadyCombatServerTickTask(TaskType type) {
		eType=type;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		switch(eType) {
		case REGEN:
			onRegen();
			break;
		case LIFT:
			onLiftDmg();
			break;
		}
	}
	
	public void onRegen() {
		OfflinePlayer[] lPlayers=Bukkit.getOfflinePlayers();
		UUID id;
		for(OfflinePlayer poff:lPlayers) {
			if(poff.isOnline()) {
				Player p=poff.getPlayer();
				id=p.getUniqueId();
				if(p.getFoodLevel()>=ReadyCombatPower.getMinRegenFood() && ReadyCombatPower.getPower(id)<=90) {
					ReadyCombatPower.exhaust(p,ReadyCombatPower.getRegenCost());
					ReadyCombatPower.regenPower(id, ReadyCombatPower.getFoodRegen());
				}
				ReadyCombatPower.regenPower(id, ReadyCombatPower.getRegen());
				ReadyCombatPower.listenPlayerDmg(p, Action.PHYSICAL);
				ReadyCombat.getScoreboard().getItem("power").setScore(p, (int) Math.round(ReadyCombatPower.getPower(id)));
				ReadyCombat.getScoreboard().getItem("dmgrcv").setScore(p, (int) Math.round(100*(1-ReadyCombatDamage.getDmgPercent(id))));
			}
		}
		ReadyCombat.getScoreboard().update();
	    ReadyCombat.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(ReadyCombat.getPlugin(), this, ReadyCombatPower.getRegenDur());
	    
	}
	
	public void onLiftDmg() {
		OfflinePlayer[] lPlayers=Bukkit.getOfflinePlayers();
		UUID id;
		for(OfflinePlayer poff:lPlayers) {
			if(poff.isOnline()) {
				Player p=poff.getPlayer();
				id=p.getUniqueId();
				ReadyCombatDamage.liftRecentDmg(id);
			}
		}
	    ReadyCombat.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(ReadyCombat.getPlugin(), this, ReadyCombatDamage.getDuration());
	}
	
	public enum TaskType{
		REGEN,
		LIFT
	}

}
