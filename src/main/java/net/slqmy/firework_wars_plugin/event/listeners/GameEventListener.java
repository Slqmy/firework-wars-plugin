package net.slqmy.firework_wars_plugin.event.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;

public class GameEventListener implements Listener {
  
  private final FireworkWarsPlugin plugin;
  private final FireworkWarsGame game;

  public GameEventListener(FireworkWarsPlugin plugin, FireworkWarsGame game) {
    this.plugin = plugin;
    this.game = game;
  }

  public void register() {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getPlayer();

    if (!isInGame(player)) {
      return;
    }

    game.onPlayerDeath(event);
  }

  private boolean isInGame(Player player) {
    return game.getPlayers().contains(player);
  }
}
