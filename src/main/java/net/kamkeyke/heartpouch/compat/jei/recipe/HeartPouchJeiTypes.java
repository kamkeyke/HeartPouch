package net.kamkeyke.heartpouch.compat.jei.recipe;

import mezz.jei.api.recipe.RecipeType;
import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.compat.jei.data.PouchDyeDisplay;
import net.kamkeyke.heartpouch.compat.jei.data.PouchFromWoolDisplay;

public class HeartPouchJeiTypes {
    public static final RecipeType<PouchFromWoolDisplay> POUCH_FROM_WOOL =
            RecipeType.create(HeartPouch.MODID, "pouch_from_wool", PouchFromWoolDisplay.class);

    public static final RecipeType<PouchDyeDisplay> POUCH_DYE =
            RecipeType.create(HeartPouch.MODID, "pouch_dye", PouchDyeDisplay.class);
}