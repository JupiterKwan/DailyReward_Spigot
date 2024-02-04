package org.jupiter.dailyReward;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.util.*;

public class DailyRewardPlugin extends JavaPlugin implements Listener {
    private HashMap<String, Integer> playerLoginDates;

    private static final Configuration CONFIGURATION = new Configuration();

    private ArrayList<Object> bannedItems = new ArrayList<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
        getLogger().info("Initializing...");
        bannedItems = CONFIGURATION.readConfig(this.getConfig());
        getLogger().info("Banned items are following: ");
        getLogger().info(bannedItems.toString());
        playerLoginDates = new HashMap<>();
        getLogger().info("Daily Reward is enabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerLang = player.getLocale();
        String playerName = player.getName();

        Calendar now = Calendar.getInstance();
        int currentDayOfYear = now.get(Calendar.DAY_OF_YEAR);
        if (playerLoginDates.containsKey(playerName)) {
            int lastLoginDay = playerLoginDates.get(playerName);
            // Already login today
            if (lastLoginDay == currentDayOfYear) {
                player.sendMessage(ChatColor.AQUA + "今日已經登錄過啦！無獎勵啦！");
                return;
            }
        }

        // DailyReward
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        boolean isItem = false;
        while (!isItem) {
            Material[] materials = Material.values();
            int amount = random.nextInt(5) + 1;
            Material randomMaterial = materials[random.nextInt(materials.length)];

            ItemStack rewardItem = new ItemStack(randomMaterial, amount);
            if (randomMaterial.isItem()) {
                TranslatableComponent giveMessage = new TranslatableComponent(randomMaterial.getItemTranslationKey());

                // If item in bannedList
                if (bannedItems.contains(randomMaterial.name().toLowerCase())) {
                    TextComponent noGiveMessage = new TextComponent("系統無法獎勵你");
                    noGiveMessage.addExtra(String.valueOf(amount));
                    noGiveMessage.addExtra("個");
                    noGiveMessage.addExtra(giveMessage);
                    noGiveMessage.addExtra("喔！重新roll過啦！");
                    noGiveMessage.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
                    player.spigot().sendMessage(noGiveMessage);
                    continue;
                }

                isItem = true;
                // If player inventory is full
                if (player.getInventory().firstEmpty() == -1) {
                    World playerWorld = player.getWorld();
                    playerWorld.dropItem(player.getLocation(), rewardItem);
                    player.sendMessage(ChatColor.RED + "注意啦，獎勵喺你嘅腳下喔");
                } else {
                    player.getInventory().addItem(rewardItem);
                }
                getLogger().info(randomMaterial.name());
                getLogger().info(String.valueOf(amount));
                for (org.bukkit.entity.Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer != null) {
                        TextComponent textComponent = new TextComponent(playerName);
                        textComponent.addExtra(" 今日登錄獎勵係：");
                        textComponent.addExtra("[");
                        textComponent.addExtra(giveMessage);
                        textComponent.addExtra("] * ");
                        textComponent.addExtra(String.valueOf(amount));
                        textComponent.setColor(net.md_5.bungee.api.ChatColor.GOLD);
                        onlinePlayer.spigot().sendMessage(textComponent);
                    }
                }
                playerLoginDates.put(playerName, currentDayOfYear);
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Daily Reward is disabled!");
    }
}
