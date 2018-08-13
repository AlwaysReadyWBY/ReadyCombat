package com.github.alwaysreadywby.readycombat;

import java.io.File;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import com.github.alwaysreadywby.readycombat.ReadyCombatServerTickTask.TaskType;
import com.github.alwaysreadywby.readycombat.hook.ReadyCombatPlaceholder;
import com.github.alwaysreadywby.readycore.file.ReadyConfig;
import com.github.alwaysreadywby.readycore.format.FormatException;
import com.github.alwaysreadywby.readycore.format.SimpleStringFormat;
import com.github.alwaysreadywby.readycore.player.PlayerHash;
import com.github.alwaysreadywby.readycore.util.ReadyScoreboard;

public class ReadyCombat extends JavaPlugin {
	
	private static ReadyCombat iPlugin;
	
	private static ReadyConfig fConf;
	private static ReadyConfig fLang;
	private static ReadyConfig fData;
	
	private static SimpleStringFormat iInfoFormat;
	private static ReadyCombatServerTickTask iTaskRegen;
	private static ReadyCombatServerTickTask iTaskLift;
	
	private static ReadyScoreboard scbPower;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	    ReadyCombatCommandMgr.onCommandReceive(sender, command, label, args);
		return false;
	}

	@Override
	public void onEnable() {
		init();
	}
	
	public void init() {
		iPlugin=this;
		PlayerHash.init();
		ReadyCombatPower.init();
		ReadyCombatDamage.init();
		iInfoFormat=new SimpleStringFormat();
		fConf=new ReadyConfig(this, new File(getDataFolder(),"config.yml")) {
			@Override
			public void loadDefault() {
				writeConfig("zh_CN.yml","lang",false);
				writeConfig(5.0,"reduce.left_click_air",false);
				writeConfig(5.0,"reduce.right_click_air",false);
				writeConfig(5.0,"reduce.left_click_block",false);
				writeConfig(5.0,"reduce.right_click_block",false);
				writeConfig(0.0,"reduce.physical",false);
				writeConfig(1.6,"regen.power.food",false);
				writeConfig(0.4,"regen.power.natural",false);
				writeConfig(0.8,"regen.exhaust",false);
				writeConfig(14,"regen.food-level",false);
				writeConfig(4,"regen.duration",false);
				writeConfig(20,"recent.duration",false);
				writeConfig(10,"recent.count",false);
				writeConfig(0.01,"realDmg.factor",false);
				writeConfig(50,"realDmg.max",false);
			}
		};
		fLang=new ReadyConfig(this, new File(getDataFolder(),fConf.getData().getString("lang"))) {
			@Override
			public void loadDefault() {
				writeConfig("战斗预备","lang.name.plugin",false);
				writeConfig("&6%lang.name.plugin%插件加载完毕！","lang.msg.loaded",false);
				writeConfig("&a你有点累了，攻击力下降了","lang.msg.power.high",false);
				writeConfig("&e你很累了，攻击力大幅下降了","lang.msg.power.mid",false);
				writeConfig("&c你累得要死，几乎没有攻击力了","lang.msg.power.low",false);
				writeConfig("&c你受到了太多的攻击，你的防具防御力下降了","lang.msg.damage.high",false);
				writeConfig("&c指定玩家不在线","lang.msg.online-only",false);
				writeConfig("&c该指令不能在控制台使用","lang.msg.player-only",false);
				writeConfig("&c不合法的数字！","lang.msg.number-only",false);
				writeConfig("&a&l——————&e%lang.name.plugin%&a——————","lang.msg.title",false);
				writeConfig("&a插件版本:&e%arg0%","lang.msg.version",false);
				writeConfig("&a作者:&e%arg0%","lang.msg.author",false);
				writeConfig("&a如有bug，请致信&e%arg0%","lang.msg.mail",false);
				writeConfig("&a/rcombat regen <数量> [玩家] - &e恢复%lang.board.power%","lang.help.regen",false);
				writeConfig("&a/rcombat cost <数量> [玩家] - &e消耗%lang.board.power%","lang.help.cost",false);
				writeConfig("&4/rcombat reload - &e重载插件","lang.help.reload",false);
				writeConfig("&a/rcombat version - &e显示插件版本","lang.help.version",false);
				writeConfig("&a/rcombat disp [true/false] - &e打开/关闭计分板显示功能","lang.help.disp",false);
				writeConfig("&a/rcombat help - &e查看%lang.name.plugin%指令帮助","lang.help.help",false);
				writeConfig("&a力量值","lang.board.power",false);
				writeConfig("&a护甲比","lang.board.dmgrcv",false);
			}
		};
		fData=new ReadyConfig(this, new File(getDataFolder(),"playerdata.yml")) {
			@Override
			public void loadDefault() {
				
			}
		};
		Set<String> lLang=fLang.getData().getKeys(true);
		for(String sLang: lLang) {
			iInfoFormat.addPlaceHolder(sLang, fLang.getData().getString(sLang));
		}
	    getServer().getPluginManager().registerEvents(new ReadyCombatListener(), this);
	    iTaskRegen=new ReadyCombatServerTickTask(TaskType.REGEN);
	    getServer().getScheduler().scheduleSyncDelayedTask(this, iTaskRegen, ReadyCombatPower.getRegenDur());
	    iTaskLift=new ReadyCombatServerTickTask(TaskType.LIFT);
	    getServer().getScheduler().scheduleSyncDelayedTask(this, iTaskLift, ReadyCombatDamage.getDuration());
	    scbPower=new ReadyScoreboard();
	    try {
			scbPower.registerNewItem("power", iInfoFormat.process("%lang.board.power%"),DisplaySlot.SIDEBAR);
			scbPower.registerNewItem("dmgrcv", iInfoFormat.process("%lang.board.dmgrcv%"),DisplaySlot.SIDEBAR);
		} catch (IllegalArgumentException | FormatException e) {
			e.printStackTrace();
		}
		info("%lang.msg.loaded%");
		if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new ReadyCombatPlaceholder().register();
		}
	}
	
	public void reInit() {
		fConf.load();
		fLang.load();
		iInfoFormat=new SimpleStringFormat();
		ReadyCombatDamage.clearHash();
		Set<String> lLang=fLang.getData().getKeys(true);
		for(String sLang: lLang) {
			iInfoFormat.addPlaceHolder(sLang, fLang.getData().getString(sLang));
		}
	    try {
			scbPower.getItem("power").setDisp(iInfoFormat.process("%lang.board.power%"));
			scbPower.getItem("dmgrcv").setDisp(iInfoFormat.process("%lang.board.dmgrcv%"));
		} catch (IllegalArgumentException | FormatException e) {
			e.printStackTrace();
		}
		info("%lang.msg.loaded%");
	}
	
	public static void reload() {
		getPlugin().reInit();
	}
	
	public static ReadyCombat getPlugin() {
		return iPlugin;
	}
	
	public static void info(String source,Object... args) {
		try {
			iPlugin.getLogger().info(iInfoFormat.process(source, args));
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}
	
	public static void info(CommandSender p,String source,Object... args) {
		try {
			p.sendMessage(iInfoFormat.process(source, args));
		} catch (FormatException e) {
			p.sendMessage(e.getResult().toString());
		}
	}

	public static ReadyScoreboard getScoreboard() {
		// TODO Auto-generated method stub
		return scbPower;
	}

	public static YamlConfiguration getConf() {
		return fConf.getData();
	}
	
	public static YamlConfiguration getData() {
		return fData.getData();
	}
}
