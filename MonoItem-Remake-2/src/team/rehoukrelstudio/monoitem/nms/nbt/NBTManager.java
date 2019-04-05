package team.rehoukrelstudio.monoitem.nms.nbt;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class NBTManager {

    private ItemStack item;

    public NBTManager(){
        this.item = new ItemStack(Material.SHEARS);
    }

    public NBTManager(ItemStack item){
        this.item = item;
    }

    public abstract boolean hasNbt(String key);
    public abstract void customNbtData(String key, Object obj);
    public abstract void deleteCustomNbtData(String key);

    public abstract int getInteger(String key);
    public abstract double getDouble(String key);
    public abstract byte getByte(String key);
    public abstract boolean getBoolean(String key);
    public abstract String getString(String key);

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }
}
