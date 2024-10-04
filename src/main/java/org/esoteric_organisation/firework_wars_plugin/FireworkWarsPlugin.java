package org.esoteric_organisation.firework_wars_plugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.manager.ArenaManager;
import org.esoteric_organisation.firework_wars_plugin.commands.ArenaCommand;
import org.esoteric_organisation.firework_wars_plugin.commands.ResetInventoryCommand;
import org.esoteric_organisation.firework_wars_plugin.commands.SetLanguageCommand;
import org.esoteric_organisation.firework_wars_plugin.data.player.PlayerDataManager;
import org.esoteric_organisation.firework_wars_plugin.game.GameManager;
import org.esoteric_organisation.firework_wars_plugin.items.manager.CustomItemManager;
import org.esoteric_organisation.firework_wars_plugin.language.LanguageManager;
import org.esoteric_organisation.firework_wars_plugin.util.PersistentDataManager;

import java.util.logging.Logger;

public final class FireworkWarsPlugin extends JavaPlugin {
  private static FireworkWarsPlugin instance;
  public static Logger LOGGER;

  private final CustomItemManager customItemManager;
  private PlayerDataManager playerDataManager;
  private LanguageManager languageManager;
  private ArenaManager arenaManager;
  private GameManager gameManager;
  private PersistentDataManager pdcManager;

  public static FireworkWarsPlugin getInstance() {
    return instance;
  }

  public PlayerDataManager getPlayerDataManager() {
    return this.playerDataManager;
  }

  public LanguageManager getLanguageManager() {
    return this.languageManager;
  }

  public ArenaManager getArenaManager() {
    return this.arenaManager;
  }

  public GameManager getGameManager() {
    return this.gameManager;
  }

  public CustomItemManager getCustomItemManager() {
    return this.customItemManager;
  }

  public PersistentDataManager getPdcManager() {
    return this.pdcManager;
  }

  public FireworkWarsPlugin(CustomItemManager customItemManager) {
    instance = this;
    LOGGER = getLogger();

    this.customItemManager = customItemManager;
  }

  @Override
  public void onEnable() {
    getDataFolder().mkdir();
    saveDefaultConfig();

    CommandAPIBukkitConfig commandAPIConfig = new CommandAPIBukkitConfig(this);

    CommandAPI.onLoad(commandAPIConfig);
    CommandAPI.onEnable();

    this.playerDataManager = new PlayerDataManager(this);
    this.languageManager = new LanguageManager(this);
    this.arenaManager = new ArenaManager(this);
    this.gameManager = new GameManager(this);
    this.pdcManager = new PersistentDataManager();

    customItemManager.setPlugin(this);
    customItemManager.registerCustomItems();

    new SetLanguageCommand(this);
    new ArenaCommand(this);
    new ResetInventoryCommand(this);
  }

  @Override
  public void onDisable() {
    if (playerDataManager != null) {
      playerDataManager.save();
    }
  }
}
