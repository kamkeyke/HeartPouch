package net.kamkeyke.heartpouch.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HeartPouchData {
    private static final String LIST = "heartstones";
    private static final String ACTIVE = "activeIndex";

    public static ListTag getList(ItemStack pouch) {
        return pouch.getOrCreateTag().getList(LIST, CompoundTag.TAG_COMPOUND);
    }

    public static void saveList(ItemStack pouch, ListTag list) {
        pouch.getOrCreateTag().put(LIST, list);
    }

    public static int size(ItemStack pouch) {
        return getList(pouch).size();
    }

    public static int getActive(ItemStack pouch) {
        return pouch.getOrCreateTag().getInt(ACTIVE);
    }

    public static void setActive(ItemStack pouch, int index) {
        pouch.getOrCreateTag().putInt(ACTIVE, index);
    }

    public static CompoundTag getActiveHeart(ItemStack pouch) {
        ListTag list = getList(pouch);
        if (list.isEmpty()) return null;

        int idx = clamp(getActive(pouch), 0, list.size() - 1);
        setActive(pouch, idx);
        return list.getCompound(idx);
    }

    public static List<CompoundTag> getAllHearts(ItemStack pouch){
        ListTag list = getList(pouch);
        List<CompoundTag> result = new ArrayList<>(list.size());

        for(int i = 0; i < list.size(); i++){
            result.add(list.getCompound(i).copy());
        }

        return result;
    }

    public static void add(ItemStack pouch, CompoundTag heartTag) {
        ListTag list = getList(pouch);
        list.add(heartTag.copy());
        saveList(pouch, list);

        if (list.size() == 1) {
            setActive(pouch, 0);
        }
    }

    public static CompoundTag removeActive(ItemStack pouch) {
        ListTag list = getList(pouch);
        if (list.isEmpty()) return null;

        int idx = getActive(pouch);
        CompoundTag removed = list.getCompound(idx);
        list.remove(idx);

        if (!list.isEmpty()) {
            setActive(pouch, Math.min(idx, list.size() - 1));
        } else {
            setActive(pouch, 0);
        }

        saveList(pouch, list);
        return removed;
    }



    public static String getHeartName(CompoundTag tag) {
        if (tag == null) return null;

        if (tag.contains("Name", Tag.TAG_STRING)) {
            return tag.getString("Name");
        }

        int id = tag.getInt("Id");

        if (tag.contains("display", Tag.TAG_COMPOUND)) {
            CompoundTag display = tag.getCompound("display");
            if (display.contains("Name", Tag.TAG_STRING)) {
                String json = display.getString("Name");
                MutableComponent comp = Component.Serializer.fromJson(json);
                return comp != null ? comp.getString() : "Heartstone";
            }
        }

        return "Heartstone";
    }

    public static int clamp(int value, int min, int max) {
        return Math.min(max, Math.max(value, min));
    }
    public static double clamp(double value, double min, double max) {
        return Math.min(max, Math.max(value, min));
    }
}
