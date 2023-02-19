package com.taylan.leaves.chunk;


import com.taylan.leaves.map.LeafSpot;
import com.taylan.leaves.Leaves;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class LeafChunk {

    private static int dUp = 14, dDown = 2, dTotal = dUp + dDown, plantChance = 40;

    private final int x, z, rx, rz;

    private final List<LeafSpot> spots;

    private final World world;

    private final String worldName;

    private Player[] players;

    public LeafChunk(ChunkOrder chunk) {
        world = chunk.getWorld();
        worldName = world.getName();
        x = chunk.getX();
        z = chunk.getZ();
        rx = x * 16;
        rz = z * 16;

        setPlayers(chunk.getPlayers());

        spots = new ArrayList<>();
    }

    public void reprocess() {
        Bukkit.getScheduler().runTaskAsynchronously(Leaves.getInstance(),
                () -> aReprocess(world.getChunkAt(x, z).getChunkSnapshot()));
    }

    private void aReprocess(ChunkSnapshot snapshot) {
        spots.clear();

        final List<Level> levels = calculateLevels(players);

        for (Level l : levels) {
            for (int x = 0; x < 16; x++)
                for (int z = 0; z < 16; z++) {
                    boolean fAir = false;

                    for (int y = (l.from < 0 ? 0 : l.from); y < l.to && y < 255; y++) {
                        Material n = snapshot.getBlockType(x, y, z);

                        if (n == null)
                            continue;

                        if (n.isAir() || !n.isSolid())
                            fAir = true;

                        else {
                            if (fAir) {
                                BlockData data = Leaves.getInstance().getMap().getTypes().data(n);

                                if (data != null)
                                    spots.add(new LeafSpot(x + rx, y, z + rz, data));
                            }

                            fAir = false;
                        }
                    }
                }
        }
    }

    public void plant() {
        if (players == null || players.length == 0)
            return;

        final Random r = ThreadLocalRandom.current();

        for (LeafSpot s : spots) {
            if (r.nextInt(100) > plantChance)
                continue;


            for (int i = 0; i < players.length; i++)
                if (players[i] != null)
                    if (players[i].getWorld().getName().contentEquals(worldName))
                        players[i].spawnParticle(Particle.FALLING_SPORE_BLOSSOM, s.getX() + r.nextFloat(), s.getY(), s.getZ() + r.nextFloat(), 1, 0, 0, 0, 1);

                    else
                        players[i] = null;
        }


    }

    public List<Level> calculateLevels(Player[] players) {
        List<Level> result = new ArrayList<>();

        if (players.length == 1) {
            if (players[0] != null)
                result.add(new Level(players[0].getLocation().getBlockY() - dDown));
            return result;
        }

        List<Integer> yList = (new ArrayList<Integer>() {{
            for (Player p : players)
                if (p != null)
                    add(p.getLocation().getBlockY() - dDown);
        }}).stream().sorted().collect(Collectors.toList());

        for (Integer y : yList) {
            boolean merged = false;

            for (Level l : result) {
                if (!l.tryMerge(y))
                    continue;
                merged = true;
                break;
            }

            if (!merged) {
                result.add(new Level(y));
            }
        }

        return result;
    }

    public void setPlayers(List<Player> players) {
        this.players = new Player[players.size()];
        for (int i = 0; i < players.size(); i++)
            this.players[i] = players.get(i);
    }

    public boolean is(ChunkOrder chunk) {
        return chunk.getX() == x && chunk.getZ() == z;
    }

    private class Level {

        private final int from;
        private int to;

        Level(int y) {
            from = y;
            to = from + dTotal;
        }

        boolean tryMerge(int y) {
            if (y == from)
                return true;

            if (y > from && y <= to) {
                final int adjusted = y + dTotal;

                if (adjusted > to)
                    to = adjusted;
                return true;
            }

            return false;
        }

    }

    public static void reload() {
        LeafChunk.dUp = Leaves.getInstance().getConfig().getInt("vertical_offset.up", LeafChunk.dUp);
        LeafChunk.dDown = Leaves.getInstance().getConfig().getInt("vertical_offset.down", LeafChunk.dDown);
        LeafChunk.plantChance = Leaves.getInstance().getConfig().getInt("plant_chance", LeafChunk.plantChance);
        dTotal = dDown + dUp;
    }

}