package com.boothousegaming.lavasource;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class LavaSource  extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockFromTo(BlockFromToEvent event) {
		Block fromBlock = event.getBlock();
		if (!isLavaSource(fromBlock))
			return;
		Block toBlock = event.getToBlock();
		if (!isLava(toBlock))
			return;
		Vector movementVector = toBlock.getLocation().toVector().subtract(fromBlock.getLocation().toVector());
		if (movementVector.getBlockY() != 0)
			return; // do not create sources vertically
		Block blockToCheck = event.getToBlock().getLocation().add(movementVector).getBlock();
		if (!isLavaSource(blockToCheck)) {
			double x = movementVector.getX();
			movementVector.setX(movementVector.getZ());
			movementVector.setZ(x);
			blockToCheck = event.getToBlock().getLocation().add(movementVector).getBlock();
			if (!isLavaSource(blockToCheck)) {
				blockToCheck = event.getToBlock().getLocation().subtract(movementVector).getBlock();
				if (!isLavaSource(blockToCheck)) {
					return; // no secondary adjacent source
				}
			}
		}
		final Block source0 = fromBlock;
		final Block source1 = blockToCheck;
		this.getServer().getScheduler().runTaskLater(this, new Runnable() {
			
			@Override
			public void run() {
				if (isLavaSource(source0) && isLavaSource(source1))
					toBlock.setType(Material.STATIONARY_LAVA);
			}
			
		}, 20L);
	}
	
	private boolean isLava(Block block) {
		return block.getType().equals(Material.LAVA) || block.getType().equals(Material.STATIONARY_LAVA);
	}
	
	private boolean isSource(Block block) {
		return block.getData() == 0x0;
	}
	
	private boolean isLavaSource(Block block) {
		// TO DO: block.getData() is deprecated; find updated method
		return isLava(block) && isSource(block);
	}
	
}
