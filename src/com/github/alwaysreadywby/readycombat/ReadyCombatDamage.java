package com.github.alwaysreadywby.readycombat;

import java.util.ArrayDeque;
import java.util.Hashtable;
import java.util.UUID;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import com.github.alwaysreadywby.readycore.player.PlayerHash;

public class ReadyCombatDamage { 
	
	private static Hashtable<UUID, ArrayDeque<Double>> hashRecentList;
	private static Hashtable<UUID, Double> hashRecentVal;
	
	public static void init() {
		hashRecentList=new Hashtable<UUID, ArrayDeque<Double>>();
		hashRecentVal=new Hashtable<UUID, Double>();
	}
	
	public static void clearHash() {
		hashRecentList.clear();
		hashRecentVal.clear();
	}
	
	public static void clearRecentDmg(UUID uuid) {
		if(hashRecentList.get(uuid)==null) {
			hashRecentList.put(uuid, new ArrayDeque<Double>());
		}
		hashRecentList.get(uuid).clear();
		hashRecentVal.put(uuid, 0d);
	}
	
	public static void addRecentDmg(UUID uuid,double damage) {
		if(hashRecentList.get(uuid)==null) {
			hashRecentList.put(uuid, new ArrayDeque<Double>());
		}
		hashRecentList.get(uuid).add(damage);
		hashRecentVal.put(uuid, hashRecentVal.get(uuid)+damage);
		if(hashRecentList.get(uuid).size()>getCount()) {
			liftRecentDmg(uuid);
		}
	}
	
	public static void liftRecentDmg(UUID uuid) {
		if(hashRecentList.get(uuid)==null) {
			hashRecentList.put(uuid, new ArrayDeque<Double>());
			return;
		}
		if(!hashRecentList.get(uuid).isEmpty()) {
			hashRecentVal.put(uuid, Math.max(getRecentDmg(uuid)-hashRecentList.get(uuid).pollFirst(),0));
		}else {
			hashRecentVal.put(uuid, 0d);
		}
	}
	
	public static double getRecentDmg(UUID uuid) {
		return hashRecentVal.getOrDefault(uuid, 0d);
	}

	public static double getDmgPercent(UUID id) {
		return getMaxReal()*(1-Math.exp(-getDmgFactor()*getRecentDmg(id)*getRecentDmg(id)));
	}
	
	public static void inflictRealDmg(UUID uuid,double damage) {
		Player p=PlayerHash.getPlayer(uuid);
		if(p.isOnline()) {
			p.setHealth(Math.max(((Damageable)p).getHealth()-damage,0));
		}
	}
	
	public static double inflictDmg(UUID uuid,double damage) {
		double rdmg=damage*getDmgPercent(uuid);
		inflictRealDmg(uuid,rdmg);
		addRecentDmg(uuid,damage-rdmg);
		return damage-rdmg;
	}

	public static int getDuration() {
		return ReadyCombat.getConf().getInt("recent.duration");
	}
	
	public static int getCount() {
		return ReadyCombat.getConf().getInt("recent.count");
	}
	
	public static double getDmgFactor() {
		return ReadyCombat.getConf().getDouble("realDmg.factor");
	}
	
	public static double getMaxReal() {
		return ReadyCombat.getConf().getDouble("realDmg.max")/100;
	}
}
