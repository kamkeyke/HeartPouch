package net.kamkeyke.heartpouch.misc.recipe;

import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.raccooncore.util.ColorUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PouchFromWoolRecipe extends CustomRecipe {
    public static final RecipeSerializer<PouchFromWoolRecipe> SERIALIZER =
            new SimpleCraftingRecipeSerializer<>(PouchFromWoolRecipe::new);

    public PouchFromWoolRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        if (container.getWidth() != 3 || container.getHeight() != 3) return false;

        boolean[] stringSlots = {
                true, false, true,
                false, false, false,
                true, false, true
        };
        boolean[] woolSlots = {
                false, true, false,
                true, false, true,
                false, true, false
        };

        DyeColor sharedColor = null;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = container.getItem(i);

            if (stringSlots[i]) {
                if (stack.getItem() != Items.STRING) return false;
            } else if (woolSlots[i]) {
                Optional<DyeColor> color = ColorUtils.getWoolColor(stack);
                if (color.isEmpty()) return false;
                if (sharedColor == null) sharedColor = color.get();
                else if (sharedColor != color.get()) return false;
            } else {
                if (stack.getItem() != Items.AMETHYST_SHARD) return false;
            }
        }

        return sharedColor != null;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return new ItemStack(ModItems.HEARTSTONE_POUCH.get());
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, @NotNull RegistryAccess registryAccess) {
        DyeColor color = null;
        for (int i = 0; i < container.getContainerSize(); i++) {
            Optional<DyeColor> found = ColorUtils.getWoolColor(container.getItem(i));
            if (found.isPresent()) {
                color = found.get();
                break;
            }
        }

        ItemStack result = new ItemStack(ModItems.HEARTSTONE_POUCH.get());
        if (color != null) {
            ColorUtils.setColor(result, ColorUtils.toRgb(color));
        }
        return result;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        ingredients.add(Ingredient.of(Items.STRING));
        ingredients.add(Ingredient.of(ItemTags.WOOL));
        ingredients.add(Ingredient.of(Items.STRING));

        ingredients.add(Ingredient.of(ItemTags.WOOL));
        ingredients.add(Ingredient.of(Items.AMETHYST_SHARD));
        ingredients.add(Ingredient.of(ItemTags.WOOL));

        ingredients.add(Ingredient.of(Items.STRING));
        ingredients.add(Ingredient.of(ItemTags.WOOL));
        ingredients.add(Ingredient.of(Items.STRING));

        return ingredients;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}