package org.ssyp.theforceawakens.json;

import org.ssyp.theforceawakens.game.Player;
import org.ssyp.theforceawakens.game.Team;

import java.util.LinkedList;
import java.util.List;

public class JSONRoster {
    private List<JSONRosterPlayer> jediList = new LinkedList<>();
    private List<JSONRosterPlayer> sithList = new LinkedList<>();

    public List<JSONRosterPlayer> getJediList() {
        return jediList;
    }

    public List<JSONRosterPlayer> getSithList() {
        return sithList;
    }

    public void addPlayer(Player player) {
        JSONRosterPlayer jsonRosterPlayer = new JSONRosterPlayer(player);
        Team playerTeam = player.getTeam();
        switch (playerTeam) {
            case Jedi:
                jediList.add(jsonRosterPlayer);
                break;
            case Sith:
                sithList.add(jsonRosterPlayer);
                break;
        }
    }
}
