package net.kamkeyke.heartpouch.registry;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.misc.recipe.PouchDyeRecipe;
import net.kamkeyke.heartpouch.misc.recipe.PouchFromWoolRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, HeartPouch.MODID);

    public static final RegistryObject<RecipeSerializer<PouchFromWoolRecipe>> POUCH_FROM_WOOL =
            SERIALIZERS.register("crafting_pouch_from_wool", () -> PouchFromWoolRecipe.SERIALIZER);

    public static final RegistryObject<RecipeSerializer<PouchDyeRecipe>> POUCH_DYE =
            SERIALIZERS.register("crafting_pouch_dye", () -> PouchDyeRecipe.SERIALIZER);

    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }
}
