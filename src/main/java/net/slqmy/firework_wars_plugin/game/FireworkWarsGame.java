package net.slqmy.firework_wars_plugin.game;

import java.util.ArrayList;
import java.util.List;

import net.slqmy.firework_wars_plugin.event.listeners.GameEventListener;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.arena.structure.Arena;
import net.slqmy.firework_wars_plugin.arena.structure.ConfiguredTeam;
import net.slqmy.firework_wars_plugin.language.Message;

public class FireworkWarsGame {

  private final FireworkWarsPlugin plugin;
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  private final Arena arena;
  private final GameEventListener eventListener;

  private GameState gameState = GameState.WAITING;

  private final List<FireworkWarsTeam> teams = new ArrayList<>();
  private final List<Player> players = new ArrayList<>();

  public Arena getArena() {
    return arena;
  }

  public GameState getGameState() {
    return gameState;
  }

  public List<Player> getPlayers() {
    return players;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public FireworkWarsGame(FireworkWarsPlugin plugin, Arena arena) {
    this.plugin = plugin;

    this.arena = arena;
    this.eventListener = new GameEventListener(plugin, this);
  }

  public void addPlayer(Player player) {
    players.add(player);
    player.teleport(arena.getLobbySpawnLocation().getBukkitLocation());

    if (gameState == GameState.WAITING) {
      if (players.size() >= arena.getMinimumPlayerCount()) {
        startCountdown();
      }
    }
  }

  public void sendMessage(Message message, Object... arguments) {
    for (Player player : players) {
      plugin.getLanguageManager().sendMessage(message, player, arguments);
    }
  }

  public void startCountdown() {
    new GameCountdown(plugin, this);
  }

  public void startGame() {
    eventListener.register();

    for (ConfiguredTeam configuredTeam : arena.getTeamInformation()) {
      teams.add(new FireworkWarsTeam(configuredTeam));
    }

    distributePlayersAcrossTeams();
  }

  public void endGame(FireworkWarsTeam winningTeam) {
    sendMessage(Message.TEAM_WON, winningTeam.getDeserializedTeamName());
  }

  public void distributePlayersAcrossTeams() {
    for (int i = 0; i < players.size(); i++) {
      int teamIndex = i % teams.size();
      FireworkWarsTeam team = teams.get(teamIndex);
      team.addPlayer(players.get(i));
    }
  }

  public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getPlayer();
    player.setGameMode(GameMode.SPECTATOR);

    FireworkWarsTeam team = getTeam(player);

    if (isTeamEliminated(team)) {
      eliminateTeam(team);
      List<FireworkWarsTeam> remainingTeams = getRemainingTeams();

      if (remainingTeams.size() == 1) {
        endGame(remainingTeams.get(0));
      }
    }
  }

  public boolean isTeamEliminated(FireworkWarsTeam team) {
    return team
      .getPlayers()
      .stream()
      .allMatch(player -> player.getGameMode() == GameMode.SPECTATOR);
  }

  public List<FireworkWarsTeam> getRemainingTeams() {
    return teams
      .stream()
      .filter(team -> !isTeamEliminated(team))
      .toList();
  }

  public FireworkWarsTeam getTeam(Player player) {
    return teams
      .stream()
      .filter(team -> team.getPlayers().contains(player))
      .findFirst()
      .orElse(null);
  }

  public void eliminateTeam(FireworkWarsTeam team) {
    sendMessage(Message.TEAM_ELIMINATED, team.getDeserializedTeamName());
  }

  public enum GameState {
    WAITING,
    STARTING,
    PLAYING;
  }
}
