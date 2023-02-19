package com.taylan.leaves.chunk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class ChunkOrder {
	
	@Getter
	private final World world;
	
	@Getter
	private final int x, z;
	
	@Getter
	private final List<Player> players;

}
