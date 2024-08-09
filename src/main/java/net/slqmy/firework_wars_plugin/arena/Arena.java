package net.slqmy.firework_wars_plugin.arena;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.Expose;

import net.slqmy.firework_wars_plugin.arena.BlockLocation;
import net.slqmy.firework_wars_plugin.arena.PlayerLocation;
import net.slqmy.firework_wars_plugin.arena.ConfiguredTeam;

public class Arena {

  @Expose
  private PlayerLocation lobbySpawnLocation;
  @Expose
  private @Nullable int minimumPlayerCount = 2;
  @Expose
  private @Nullable int maximumPlayerCount = PlayerCount.INFINITY.getValue();
  @Expose
  private int countDownSeconds = 15;
  @Expose
  private ConfiguredTeam[] teamInformation;
  @Expose
  private BlockLocation[] chestLocations;

  public PlayerLocation getLobbySpawnLocation() {
    return lobbySpawnLocation;
  }

  public @Nullable int getMinimumPlayerCount() {
    return minimumPlayerCount;
  }

  public @Nullable int getmaximumPlayerCount() {
    return maximumPlayerCount;
  }

  public int getCountDownSeconds() {
    return countDownSeconds;
  }

  public ConfiguredTeam[] getTeamInformation() {
    return teamInformation;
  }

  public BlockLocation[] getChestLocations() {
    return chestLocations;
  }

  public enum PlayerCount {
    INFINITY(-1);

    private int value;

    public int getValue() {
      return value;
    }

    PlayerCount(int value) {
      this.value = value;
    }
  }
}
