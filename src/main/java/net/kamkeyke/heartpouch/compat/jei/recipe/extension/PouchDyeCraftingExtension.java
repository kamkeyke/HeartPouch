package net.kamkeyke.heartpouch.compat.jei.recipe.extension;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.raccooncore.util.ColorUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PouchDyeCraftingExtension implements ICraftingCategoryExtension {
    private static final String POUCH_SLOT = "pouch";
    private static final String DYE_SLOT = "dye";
    private static final String OUTPUT_SLOT = "output";

    @Override
    public void setRecipe(
            @NotNull IRecipeLayoutBuilder builder,
            ICraftingGridHelper craftingGridHelper,
            @NotNull IFocusGroup focuses
    ) {
        List<ItemStack> anyPouch = Arrays.stream(DyeColor.values())
                .map(color -> {
                    ItemStack stack = new ItemStack(ModItems.HEARTSTONE_POUCH.get());
                    ColorUtils.setColor(stack, ColorUtils.toRgb(color));
                    return stack;
                })
                .toList();

        List<ItemStack> anyDye = Arrays.stream(DyeColor.values())
                .map(color -> new ItemStack(DyeItem.byColor(color)))
                .toList();

        List<IRecipeSlotBuilder> inputs = craftingGridHelper.createAndSetInputs(
                builder,
                List.of(anyPouch, anyDye),
                0,
                0
        );

        inputs.get(0).setSlotName(POUCH_SLOT);
        inputs.get(1).setSlotName(DYE_SLOT);

        // Apenas um placeholder. A cor real será definida abaixo.
        ItemStack output = new ItemStack(ModItems.HEARTSTONE_POUCH.get());

        craftingGridHelper
                .createAndSetOutputs(builder, List.of(output))
                .setSlotName(OUTPUT_SLOT);
    }

    @Override
    public void onDisplayedIngredientsUpdate(
            List<IRecipeSlotDrawable> recipeSlots,
            @NotNull IFocusGroup focuses
    ) {
        ItemStack pouch = recipeSlots.stream()
                .filter(slot -> slot.getSlotName().map(POUCH_SLOT::equals).orElse(false))
                .findFirst()
                .flatMap(IRecipeSlotDrawable::getDisplayedItemStack)
                .orElse(ItemStack.EMPTY);

        ItemStack dye = recipeSlots.stream()
                .filter(slot -> slot.getSlotName().map(DYE_SLOT::equals).orElse(false))
                .findFirst()
                .flatMap(IRecipeSlotDrawable::getDisplayedItemStack)
                .orElse(ItemStack.EMPTY);

        if (pouch.isEmpty() || dye.isEmpty()) {
            return;
        }

        if (!(dye.getItem() instanceof DyeItem)) {
            return;
        }

        int pouchColor = ColorUtils.getColor(pouch, -1);

        ItemStack dyeForMix = dye.copy();
        dyeForMix.setCount(1);

        int mixedColor = ColorUtils.mixColors(
                pouchColor,
                List.of(dyeForMix)
        );

        ItemStack result = new ItemStack(ModItems.HEARTSTONE_POUCH.get());
        ColorUtils.setColor(result, mixedColor);

        recipeSlots.stream()
                .filter(slot -> slot.getSlotName().map(OUTPUT_SLOT::equals).orElse(false))
                .findFirst()
                .ifPresent(slot ->
                        slot.createDisplayOverrides().addItemStack(result)
                );
    }
}