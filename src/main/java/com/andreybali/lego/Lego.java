package com.andreybali.lego;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lego extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new LookAt(this), this);
        System.out.println("Plugin security enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("Plugin disabled");
    }
}
