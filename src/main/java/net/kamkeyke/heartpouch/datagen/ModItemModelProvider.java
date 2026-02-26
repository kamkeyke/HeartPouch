package net.kamkeyke.heartpouch.datagen;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.raccooncore.datagen.RaccoonItemModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends RaccoonItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, HeartPouch.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.HEARTSTONE_POUCH);
    }
}
