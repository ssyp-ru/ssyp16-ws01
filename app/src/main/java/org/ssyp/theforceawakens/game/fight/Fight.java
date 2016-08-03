package org.ssyp.theforceawakens.game.fight;

import org.ssyp.theforceawakens.commands.clientcommands.MsgCommand;
import org.ssyp.theforceawakens.commands.servercommands.FightCommand;
import org.ssyp.theforceawakens.commands.servercommands.FightCommandStarted;
import org.ssyp.theforceawakens.game.Player;
import org.ssyp.theforceawakens.game.PlayerState;
import org.ssyp.theforceawakens.game.World;

import java.util.Timer;
import java.util.TimerTask;

import static org.ssyp.theforceawakens.game.fight.FightStage.*;

public class Fight {
    private final static int START_HP = 5;
    private final static int ATTACK_WAIT_TIME = 1500;
    int jediHp = 0;
    int sithHp = 0;
    private Player jedi; // It is said like so: "player jedi attacks up"... why is name "jedi" bad then?
    private Player sith;
    private FightRespond jediMove;
    private FightRespond sithMove;
    private Timer timer; //TODO (v.bog): think about names of all this variables

    public Fight(Player jedi, Player sith) {
        if ((jedi.getPlayerState() != PlayerState.Dead) && (sith.getPlayerState() != PlayerState.Dead)) {
            // Init private variables
            this.jedi = jedi;
            this.sith = sith;

            jedi.setPlayerState(PlayerState.Fighting);
            sith.setPlayerState(PlayerState.Fighting);

            jediHp = START_HP;
            sithHp = START_HP;

            jediMove = null;
            sithMove = null;
            //Outer world acknowledge of new fight
            FightCommandStarted command = new FightCommandStarted(jedi, sith);
            jedi.getConnection().sendCommand(command);
            sith.getConnection().sendCommand(command);

            jedi.startFight(this);
            sith.startFight(this);
        }
    }

    private void checkWin() {
        FightCommand command;

        if (jediHp <= 0) {
            jedi.endFight(PlayerState.Dead);
            sith.endFight(PlayerState.Roaming);
            command = new FightCommand(SithWins);

        } else if (sithHp <= 0) {
            sith.endFight(PlayerState.Dead);
            jedi.endFight(PlayerState.Roaming);
            command = new FightCommand(JediWins);

        } else {
            return; // Players isn't dead, fight continues
        }

        jedi.getConnection().sendCommand(command);
        sith.getConnection().sendCommand(command);
    }

    private void calculate() {
        timer.cancel();
        FightCommand command;
        if (jediMove == null) {
            if (sithMove == null) return; // No one made a move, no one was hurt.
            jediHp--;
            command = new FightCommand(SithHurts);
        } else if (sithMove == null) {
            sithHp--;
            command = new FightCommand(JediHurts);
        } else {
            if (jediMove == sithMove) {
                sithHp--;
                command = new FightCommand(JediHurts);
            } else {
                jediHp--;
                command = new FightCommand(SithHurts);
            }
        }

        jedi.getConnection().sendCommand(command);
        sith.getConnection().sendCommand(command);

        jediMove = null;
        sithMove = null;
        checkWin();
    }

    // TODO (v.bog): create FightRespondCommand (with FightRespond enum inside) and correct respond on it in Server;
    public void playerAttack(Player player, FightRespond respond) {
        if (jediMove == null) {
            jediMove = respond;
            if (sithMove == null) {
                startAttackTimer(); // Nobody had make a move, so we start wait timer.
            } else {
                calculate();
            }

        } else if (sithMove == null) {
            sithMove = respond;
            calculate();
        }
    }

    private void startAttackTimer() {
        timer = new Timer("Fight timer of jedi " + jedi.getName() + " and sith " + sith.getName());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                calculate();
            }
        }, ATTACK_WAIT_TIME);
        FightCommand command = new FightCommand(TimerStarted);
        jedi.getConnection().sendCommand(command);
        sith.getConnection().sendCommand(command);
    }
}