package org.esoteric_organisation.firework_wars_plugin.game.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.arena.json.data_holder.TeamData;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;

import java.util.ArrayList;
import java.util.List;

public class FireworkWarsTeam {
    private final TeamData teamData;
    private final FireworkWarsGame game;

    private final FireworkWarsPlugin plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final List<TeamPlayer> players = new ArrayList<>();

    public TeamData getTeamData() {
        return teamData;
    }

    public List<TeamPlayer> getPlayers() {
        return players;
    }

    public FireworkWarsTeam(TeamData teamData, FireworkWarsGame game, FireworkWarsPlugin plugin) {
        this.teamData = teamData;
        this.game = game;
        this.plugin = plugin;
    }

    public void addPlayer(TeamPlayer teamPlayer) {
        players.add(teamPlayer);
    }

    public Component getColoredTeamName() {
        return miniMessage.deserialize(teamData.getMiniMessageString());
    }

    public TextColor getTeamColor() {
        return getColoredTeamName().color();
    }

    public Material getWoolMaterial() {
        return Material.valueOf(teamData.getColorData().getColor() + "_WOOL");
    }

    public List<TeamPlayer> getRemainingPlayers() {
        return players.stream()
                .filter(TeamPlayer::isAlive)
                .toList();
    }
}
