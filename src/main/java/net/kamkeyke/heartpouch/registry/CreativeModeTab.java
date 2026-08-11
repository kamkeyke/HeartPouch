package net.kamkeyke.heartpouch.registry;

import net.kamkeyke.heartpouch.HeartPouch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class CreativeModeTab {
    public static final DeferredRegister<net.minecraft.world.item.CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HeartPouch.MODID);
    private static final List<RegistryObject<?>> EXCEPTIONS = List.of(

    );

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<net.minecraft.world.item.CreativeModeTab> HEARTPOUCH_TAB = CREATIVE_MODE_TAB.register("heartpouch_tab",
            () -> net.minecraft.world.item.CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.HEARTSTONE_POUCH.get()))
                    .title(Component.translatable("creativetab.heartpouch.tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        //loop que adiciona cada item do mod dentro da tab do mod, com exceção dos itens da lista:
                        for(RegistryObject<Item> item : ModItems.ITEMS.getEntries()){
                            boolean isException = EXCEPTIONS.contains(item);
                            if(!isException){
                                output.accept(item.get());
                            }
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}