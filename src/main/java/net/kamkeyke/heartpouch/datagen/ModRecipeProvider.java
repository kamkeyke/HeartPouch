package net.kamkeyke.heartpouch.datagen;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.heartpouch.registry.ModRecipeSerializers;
import net.kamkeyke.raccooncore.datagen.RaccoonRecipeProvider;
import net.kamkeyke.raccooncore.datagen.RaccoonSpecialRecipeBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModRecipeProvider extends RaccoonRecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput, HeartPouch.MODID);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        RaccoonSpecialRecipeBuilder.special(ModRecipeSerializers.POUCH_FROM_WOOL.get())
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
                .save(pWriter, HeartPouch.id("heartstone_pouch_from_wool"));
        RaccoonSpecialRecipeBuilder.special(ModRecipeSerializers.POUCH_DYE.get())
                .unlockedBy(getHasName(ModItems.HEARTSTONE_POUCH.get()), has(ModItems.HEARTSTONE_POUCH.get()))
                .save(pWriter, HeartPouch.id("heartstone_pouch_dyeing"));
    }
}