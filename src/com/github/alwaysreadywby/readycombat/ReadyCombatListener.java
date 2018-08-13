package com.github.alwaysreadywby.readycombat;

import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.alwaysreadywby.readycore.player.PlayerHash;

public class ReadyCombatListener implements Listener{
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	public void onPlayerInteract(PlayerInteractEvent ev) {
		ReadyCombatPower.listenPlayerDmg(ev.getPlayer(), ev.getAction());
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent ev) {
		UUID id;
		//reduce damage according to players' power level
		if(ev.getDamager().getType().equals(EntityType.PLAYER) && !((Player)ev.getDamager()).hasPermission("readycombat.bypass.power")) {
			id=ev.getDamager().getUniqueId();
			ev.setDamage(ev.getDamage()*ReadyCombatPower.getPower(ev.getDamager().getUniqueId())/100);
			double origPower=ReadyCombatPower.getPower(id);
			ReadyCombatPower.reducePower(id,ReadyCombatPower.getReduce(ReadyCombatPower.onPlayerDmg(id)));
			double newPower=ReadyCombatPower.getPower(id);
			if(newPower<20) {
				ReadyCombat.info(PlayerHash.getPlayer(id), "%lang.msg.power.low%");
			}else if(newPower<50 && origPower>=50) {
				ReadyCombat.info(PlayerHash.getPlayer(id), "%lang.msg.power.mid%");
			}else if(newPower<80 && origPower>=80) {
				ReadyCombat.info(PlayerHash.getPlayer(id), "%lang.msg.power.high%");
			}
			ReadyCombatPower.listenPlayerDmg(PlayerHash.getPlayer(id), Action.PHYSICAL);
		}
		//inflict real damage according to players' recently received damage
		if(ev.getEntity().getType().equals(EntityType.PLAYER) && !((Player)ev.getEntity()).hasPermission("readycombat.bypass.penetrate")) {
			id=ev.getEntity().getUniqueId();
			double left=ReadyCombatDamage.inflictDmg(id, ev.getDamage());
			if(ReadyCombatDamage.getRecentDmg(id)>=100) {
				ReadyCombat.info(PlayerHash.getPlayer(id), "%lang.msg.damage.high%");
			}
			ev.setDamage(left);
		}
	}

}
