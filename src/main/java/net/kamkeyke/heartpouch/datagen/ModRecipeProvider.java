package net.kamkeyke.heartpouch.datagen;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.raccooncore.datagen.RaccoonRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends RaccoonRecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput, HeartPouch.MODID);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HEARTSTONE_POUCH.get())
                .pattern("SPS")
                .pattern("PAP")
                .pattern("SPS")
                .define('S', Items.STRING).define('P', Items.PINK_WOOL).define('A', Items.AMETHYST_SHARD)
                .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
                .save(pWriter);
    }
}