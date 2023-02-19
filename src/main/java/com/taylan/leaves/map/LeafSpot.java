package com.taylan.leaves.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.block.data.BlockData;

@AllArgsConstructor
public class LeafSpot {
	
	@Getter
	private final int x, y, z;
	
	@Getter
	private BlockData data;
	
}
