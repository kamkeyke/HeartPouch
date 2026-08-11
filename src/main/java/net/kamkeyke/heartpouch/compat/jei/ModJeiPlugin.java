package net.kamkeyke.heartpouch.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.extensions.IExtendableRecipeCategory;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.compat.jei.recipe.extension.PouchDyeCraftingExtension;
import net.kamkeyke.heartpouch.compat.jei.recipe.extension.PouchFromWoolCraftingExtension;
import net.kamkeyke.heartpouch.misc.recipe.PouchDyeRecipe;
import net.kamkeyke.heartpouch.misc.recipe.PouchFromWoolRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return HeartPouch.id("jei_plugin");
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        IExtendableRecipeCategory<CraftingRecipe, ICraftingCategoryExtension> craftingCategory =
                registration.getCraftingCategory();

        craftingCategory.addCategoryExtension(PouchFromWoolRecipe.class, recipe -> new PouchFromWoolCraftingExtension());
        craftingCategory.addCategoryExtension(PouchDyeRecipe.class, recipe -> new PouchDyeCraftingExtension());
    }
}