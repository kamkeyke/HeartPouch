package net.kamkeyke.heartpouch.registry;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.item.HeartstonePouchItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, HeartPouch.MODID);

    public static final RegistryObject<Item> HEARTSTONE_POUCH = ITEMS.register("heartstone_pouch",
            () -> new HeartstonePouchItem(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}