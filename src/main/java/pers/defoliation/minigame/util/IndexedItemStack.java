package pers.defoliation.minigame.util;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class IndexedItemStack {
	
	private final ItemStack itemStack;
	private final int index;
	
	public IndexedItemStack(ItemStack itemStack, int index) {
		this.itemStack = itemStack;
		this.index = index;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public int getIndex() {
		return index;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if(!(obj instanceof IndexedItemStack))
			return false;
		
		IndexedItemStack value = (IndexedItemStack) obj;
		if(getIndex() != value.getIndex())
			return false;

		//noinspection RedundantIfStatement
		if(!getItemStack().equals(value.getItemStack()))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getItemStack(),getIndex());
	}
}
