package net.kamkeyke.heartpouch.misc.recipe;

import net.kamkeyke.heartpouch.item.HeartstonePouchItem;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.raccooncore.util.ColorUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PouchDyeRecipe extends CustomRecipe {

    public static final RecipeSerializer<PouchDyeRecipe> SERIALIZER =
            new SimpleCraftingRecipeSerializer<>(PouchDyeRecipe::new);

    public PouchDyeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        boolean foundPouch = false;
        boolean foundDye = false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof HeartstonePouchItem) {
                // Only one pouch can be used in the recipe.
                if (foundPouch) return false;

                foundPouch = true;
            } else if (stack.getItem() instanceof DyeItem) {
                foundDye = true;
            } else {
                return false;
            }
        }

        return foundPouch && foundDye;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return new ItemStack(ModItems.HEARTSTONE_POUCH.get());
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, @NotNull RegistryAccess registryAccess) {
        ItemStack pouch = ItemStack.EMPTY;
        List<ItemStack> dyes = new ArrayList<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);

            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof HeartstonePouchItem) {
                pouch = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                // Only one dye from each slot contributes to the color.
                ItemStack dye = stack.copy();
                dye.setCount(1);
                dyes.add(dye);
            }
        }

        if (pouch.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = pouch.copy();

        int baseColor = ColorUtils.getColor(pouch, -1);
        int mixedColor = ColorUtils.mixColors(baseColor, dyes);

        ColorUtils.setColor(result, mixedColor);

        return result;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(
                Ingredient.of(ModItems.HEARTSTONE_POUCH.get()),
                Ingredient.of(Tags.Items.DYES)
        );
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}