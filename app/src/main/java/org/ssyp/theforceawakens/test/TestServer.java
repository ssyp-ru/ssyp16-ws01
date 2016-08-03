package org.ssyp.theforceawakens.test;

import org.ssyp.theforceawakens.game.World;
import org.ssyp.theforceawakens.map.Map1;

public class TestServer {
    public static void main(String [] args) {
        World world = World.getInstance();

        // Load map 1
        for (org.ssyp.theforceawakens.game.Checkpoint c : Map1.getCheckpoints()) {
            world.addCheckpoint(c);
        }
    }
}
