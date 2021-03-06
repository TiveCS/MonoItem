package team.rehoukrelstudio.monoitem.nms.nbt;

import net.minecraft.server.v1_13_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTManager_1_13_R1 extends NBTManager{
    public NBTManager_1_13_R1(ItemStack item) {
        super(item);
    }

    @Override
    public boolean hasNbt(String key) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        return compound.get(key) != null;
    }

    @Override
    public void customNbtData(String key, Object obj) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        if (obj instanceof Double){
            compound.setDouble(key, (Double) obj);
        }else if (obj instanceof Byte){
            compound.setByte(key, (Byte) obj);
        }else if (obj instanceof Integer){
            compound.setInt(key, (Integer) obj);
        }else if (obj instanceof Boolean){
            compound.setBoolean(key, (Boolean) obj);
        }else{
            compound.setString(key, obj.toString());
        }

        nmsItem.setTag(compound);
        setItem(CraftItemStack.asBukkitCopy(nmsItem));
    }

    @Override
    public void deleteCustomNbtData(String key) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();

        compound.remove(key);
        nmsItem.setTag(compound);
        setItem(CraftItemStack.asBukkitCopy(nmsItem));
    }

    @Override
    public byte getTypeId() {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getTypeId();
    }

    @Override
    public Object get(String key) {
        Object obj = null;
        try{
            obj = getDouble(key);
        }catch(Exception e){}
        if (obj == null){
            try{
                obj = getByte(key);
            }catch(Exception e){}
        }
        if (obj == null){
            try{
                obj = getInteger(key);
            }catch(Exception e){}
        }
        if (obj == null){
            try{
                obj = getBoolean(key);
            }catch(Exception e){}
        }
        if (obj == null){
            try{
                obj = getString(key);
            }catch(Exception e){}
        }
        return obj;
    }

    @Override
    public int getInteger(String key) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getInt(key);
    }

    @Override
    public double getDouble(String key) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getDouble(key);
    }

    @Override
    public byte getByte(String key) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getByte(key);
    }

    @Override
    public boolean getBoolean(String key) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getBoolean(key);
    }

    @Override
    public String getString(String key) {
        net.minecraft.server.v1_13_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(getItem());
        NBTTagCompound compound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return compound.getString(key);
    }
}
