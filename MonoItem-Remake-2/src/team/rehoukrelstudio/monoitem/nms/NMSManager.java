package team.rehoukrelstudio.monoitem.nms;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import team.rehoukrelstudio.monoitem.nms.nbt.NBTManager;
import team.rehoukrelstudio.monoitem.nms.nbt.NBTManager_1_13_R1;
import team.rehoukrelstudio.monoitem.nms.nbt.NBTManager_1_13_R2;

public class NMSManager {

    private NBTManager nbtManager;

    public String getNMSVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return "";
        }
        return version;
    }

    public NBTManager getNBTManager(ItemStack item){
        if (getNMSVersion().equalsIgnoreCase("v1_13_R1")) {
            return new NBTManager_1_13_R1(item);
        }else if(getNMSVersion().equalsIgnoreCase("v1_13_R2")) {
            return new NBTManager_1_13_R2(item);
        }
        return null;
    }

}
