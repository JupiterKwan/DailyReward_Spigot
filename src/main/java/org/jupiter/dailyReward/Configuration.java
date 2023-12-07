package org.jupiter.dailyReward;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

// You can combine this page to the main class
public class Configuration {

    private final ArrayList<Object> bannedItems = new ArrayList<>();

    // DO NOT RECOMMEND to public this method, but not important in this case.
    public ArrayList<Object> readConfig(FileConfiguration fileConfig) {
        bannedItems.addAll(fileConfig.getList("ban-items"));
        return bannedItems;
    }
}
