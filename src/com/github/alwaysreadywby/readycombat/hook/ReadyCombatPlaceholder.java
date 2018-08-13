package com.github.alwaysreadywby.readycombat.hook;

import org.bukkit.entity.Player;

import com.github.alwaysreadywby.readycombat.ReadyCombat;
import com.github.alwaysreadywby.readycombat.ReadyCombatDamage;
import com.github.alwaysreadywby.readycombat.ReadyCombatPower;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class ReadyCombatPlaceholder extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return ReadyCombat.getPlugin().getDescription().getAuthors().get(0);
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return "rcombat";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return ReadyCombat.getPlugin().getDescription().getVersion();
	}
	
	@Override
	public String getPlugin() {
		// TODO Auto-generated method stub
		return "ReadyCombat";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String id) {
		// TODO Auto-generated method stub
		if(p==null) {
			return "";
		}else {
			if(id.equalsIgnoreCase("power")) {
				return Double.toString(ReadyCombatPower.getPower(p.getUniqueId()));
			}else if(id.equalsIgnoreCase("armor")){
				return Double.toString(100*(1-ReadyCombatDamage.getDmgPercent(p.getUniqueId())));
			}
		}
		return null;
	}

}
