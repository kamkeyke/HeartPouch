package net.kamkeyke.heartpouch.compat.jei.recipe.extension;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.raccooncore.util.ColorUtils;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PouchFromWoolCraftingExtension implements ICraftingCategoryExtension {

    private static final String WOOL_DRIVER = "wool_driver";
    private static final Set<String> WOOL_FOLLOWERS = Set.of("wool_1", "wool_2", "wool_3");
    private static final String OUTPUT_SLOT = "output";

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, ICraftingGridHelper craftingGridHelper, @NotNull IFocusGroup focuses) {
        List<ItemStack> anyWool = Arrays.stream(DyeColor.values())
                .map(color -> new ItemStack(ColorUtils.resolveVariant(color, "minecraft", "_wool")))
                .toList();

        List<ItemStack> anyPouch = Arrays.stream(DyeColor.values())
                .map(color -> {
                    ItemStack stack = new ItemStack(ModItems.HEARTSTONE_POUCH.get());
                    ColorUtils.setColor(stack, ColorUtils.toRgb(color));
                    return stack;
                }).toList();

        ItemStack string = new ItemStack(Items.STRING);
        ItemStack amethyst = new ItemStack(Items.AMETHYST_SHARD);

        // índices: 0=S 1=lã 2=S / 3=lã 4=A 5=lã / 6=S 7=lã 8=S
        List<List<ItemStack>> inputs = List.of(
                List.of(string), anyWool, List.of(string),
                anyWool, List.of(amethyst), anyWool,
                List.of(string), anyWool, List.of(string)
        );

        List<IRecipeSlotBuilder> slots = craftingGridHelper.createAndSetInputs(builder, inputs, 3, 3);
        slots.get(1).setSlotName(WOOL_DRIVER);
        slots.get(3).setSlotName("wool_1");
        slots.get(5).setSlotName("wool_2");
        slots.get(7).setSlotName("wool_3");

        craftingGridHelper.createAndSetOutputs(builder, anyPouch).setSlotName(OUTPUT_SLOT);
    }

    @Override
    public void onDisplayedIngredientsUpdate(List<IRecipeSlotDrawable> recipeSlots, @NotNull IFocusGroup focuses) {
        recipeSlots.stream()
                .filter(slot -> slot.getSlotName().map(WOOL_DRIVER::equals).orElse(false))
                .findFirst()
                .flatMap(IRecipeSlotDrawable::getDisplayedItemStack)
                .flatMap(ColorUtils::getWoolColor)
                .ifPresent(color -> {
                    ItemStack wool = new ItemStack(ColorUtils.resolveVariant(color, "minecraft", "_wool"));
                    ItemStack pouch = new ItemStack(ModItems.HEARTSTONE_POUCH.get());
                    ColorUtils.setColor(pouch, ColorUtils.toRgb(color));

                    for (IRecipeSlotDrawable slot : recipeSlots) {
                        String name = slot.getSlotName().orElse(null);
                        if (name == null) continue;

                        if (WOOL_FOLLOWERS.contains(name)) {
                            slot.createDisplayOverrides().addItemStack(wool);
                        } else if (OUTPUT_SLOT.equals(name)) {
                            slot.createDisplayOverrides().addItemStack(pouch);
                        }
                    }
                });
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }
}