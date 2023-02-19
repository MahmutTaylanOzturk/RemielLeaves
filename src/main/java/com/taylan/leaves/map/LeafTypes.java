package com.taylan.leaves.map;


import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;

public class LeafTypes {

    private final HashMap<Material, BlockData> types = new HashMap<>();

    public void reload() {
        types.clear();

        types.put(Material.OAK_LEAVES, getIBlockData(Material.OAK_LEAVES));
        types.put(Material.BIRCH_LEAVES, getIBlockData(Material.BIRCH_LEAVES));
        types.put(Material.SPRUCE_LEAVES, getIBlockData(Material.SPRUCE_LEAVES));
        types.put(Material.JUNGLE_LEAVES, getIBlockData(Material.JUNGLE_LEAVES));
        types.put(Material.ACACIA_LEAVES, getIBlockData(Material.ACACIA_LEAVES));
        types.put(Material.DARK_OAK_LEAVES, getIBlockData(Material.DARK_OAK_LEAVES));

    }

    public BlockData data(Material material) {
        for (Map.Entry<Material, BlockData> e : types.entrySet())
            if (e.getKey().equals(material))
                return e.getValue();
        return null;
    }

    private BlockData getIBlockData(Material material) {
        return material.createBlockData();
    }

}
