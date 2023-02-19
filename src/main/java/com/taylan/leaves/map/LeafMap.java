package com.taylan.leaves.map;

import com.taylan.leaves.Leaves;
import com.taylan.leaves.chunk.ChunkOrder;
import com.taylan.leaves.chunk.LeafChunk;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

public class LeafMap {
	
	private final List<LeafChunk> map;
	
	private long redistributeSpeed = 50L, reprocessSpeed = 50L, replantSpeed = 10L;
	private int redistributeID = -1, reprocessID = -1, replantID = -1;
	
	@Getter
	private final LeafTypes types;
	
	{
		types = new LeafTypes();
		map = new ArrayList<>();

		reload();
	}

	public void redistribute() {
		final HashMap<Chunk, List<Player>> chunkPile = new HashMap<>();
		
		for(Player p : Leaves.getInstance().getServer().getOnlinePlayers()) {
			final Chunk[] surr = getSurroundings(p.getLocation());
			
			for(Chunk c : surr) {
				if(chunkPile.containsKey(c))
					chunkPile.get(c).add(p);
				else
					chunkPile.put(c, new ArrayList<Player>() {{add(p);}});
			}
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(Leaves.getInstance(), 
				() -> aRedistribute(chunkPile));
	}
	
	public Chunk[] getSurroundings(Location l) {
		final Chunk[] result = new Chunk[9];
		final Chunk active = l.getChunk();
		result[0] = active;
		result[1] = l.getWorld().getChunkAt(active.getX() -1, active.getZ() -1);
		result[2] = l.getWorld().getChunkAt(active.getX()   , active.getZ() -1);
		result[3] = l.getWorld().getChunkAt(active.getX() +1, active.getZ() -1);
		result[4] = l.getWorld().getChunkAt(active.getX() -1, active.getZ()   );
		result[5] = l.getWorld().getChunkAt(active.getX() +1, active.getZ()   );
		result[6] = l.getWorld().getChunkAt(active.getX() -1, active.getZ() +1);
		result[7] = l.getWorld().getChunkAt(active.getX()   , active.getZ() +1);
		result[8] = l.getWorld().getChunkAt(active.getX() +1, active.getZ() +1);
		return result;
	}
	
	private synchronized void aRedistribute(final HashMap<Chunk, List<Player>> chunkPile) {
		final List<ChunkOrder> processed = new ArrayList<>();

		final List<LeafChunk> toRemove = new ArrayList<>(map);
		chunkPile.forEach((key, value) -> processed.add(new ChunkOrder(key.getWorld(), key.getX(), key.getZ(), value)));
		
		for(ChunkOrder o : processed) {
			LeafChunk c = null;
			
			for(LeafChunk i : map)
				if(i.is(o))
					c = i;
			
			if(c == null)
				map.add(new LeafChunk(o));
			else {
				c.setPlayers(o.getPlayers());
				toRemove.remove(c);
			}
		}
		
		map.removeAll(toRemove);
	}
	
	public synchronized void reprocess() {
		try {
			map.forEach(LeafChunk::reprocess);
		} catch (ConcurrentModificationException e) {
			//Bukkit.getLogger().info("TAYLANA ULAŞ KNK");
		}
	}
	
	public void plant() {
		Bukkit.getScheduler().runTaskAsynchronously(Leaves.getInstance(),
				this::aPlant);
	}
	
	public synchronized void aPlant() {
		try {
			map.forEach(LeafChunk::plant);
		} catch (ConcurrentModificationException e) {
			//Bukkit.getLogger().info("TAYLANA ULAŞ KNK");
		}
	}
	
	public void removeProcesses() {
		if(redistributeID != -1)
			Bukkit.getScheduler().cancelTask(redistributeID);
		if(reprocessID != -1)
			Bukkit.getScheduler().cancelTask(reprocessID);
		if(replantID != -1)
			Bukkit.getScheduler().cancelTask(replantID);
	}

	public void reload() {
		
		removeProcesses();
		
		types.reload();
		
		redistributeSpeed = Leaves.getInstance().getConfig().getLong("process.redistribute", redistributeSpeed);
		reprocessSpeed = Leaves.getInstance().getConfig().getLong("process.reprocess", reprocessSpeed);
		replantSpeed = Leaves.getInstance().getConfig().getLong("process.replant", replantSpeed);
		
		LeafChunk.reload();
		
		redistributeID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Leaves.getInstance(),
				this::redistribute, 21L, redistributeSpeed);
		reprocessID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Leaves.getInstance(),
				this::reprocess, 22L, reprocessSpeed);
		replantID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Leaves.getInstance(),
				this::plant, 23L, replantSpeed);
	}

}
