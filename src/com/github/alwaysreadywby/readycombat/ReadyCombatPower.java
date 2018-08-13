package com.github.alwaysreadywby.readycombat;

import java.util.Hashtable;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import com.github.alwaysreadywby.readycore.player.PlayerHash;

public class ReadyCombatPower {
	
	private static Hashtable<UUID, Action> hashActed;
	
	public static void init() {
		hashActed=new Hashtable<UUID, Action>();
	}

	public static double getPower(UUID uuid) {
		return ReadyCombat.getData().getDouble(uuid.toString(),100);
	}
	
	public static void setPower(UUID uuid,double power) {
		ReadyCombat.getData().set(uuid.toString(),power);
	}
	
	public static void reducePower(UUID uuid,double reduce) {
		setPower(uuid,Math.max(getPower(uuid)-reduce, 0));
	}
	
	public static void regenPower(UUID uuid,double regen) {
		setPower(uuid,Math.min(getPower(uuid)+regen, 100));
	}

	public static double getReduce(Action action) {
		return ReadyCombat.getConf().getDouble("reduce."+action.toString().toLowerCase());
	}

	public static int getMinRegenFood() {
		return ReadyCombat.getConf().getInt("regen.food-level");
	}

	public static float getRegenCost() {
		return (float) ReadyCombat.getConf().getDouble("regen.exhaust");
	}

	public static double getRegen() {
		return ReadyCombat.getConf().getDouble("regen.power.natural");
	}

	public static double getFoodRegen() {
		return ReadyCombat.getConf().getDouble("regen.power.food");
	}
	
	public static int getRegenDur() {
		return ReadyCombat.getConf().getInt("regen.duration");
	}

	public static void exhaust(Player p,float cost) {
		p.setExhaustion(p.getExhaustion()+cost);
	}
	
	public static void listenPlayerDmg(Player p,Action act) {
		hashActed.put(p.getUniqueId(),act);
		PlayerHash.addPlayer(p);
	}
	
	public static Action onPlayerDmg(UUID id) {
		if(hashActed.containsKey(id)) {
			return hashActed.get(id);
		}else {
			return Action.PHYSICAL;
		}
	}
}
