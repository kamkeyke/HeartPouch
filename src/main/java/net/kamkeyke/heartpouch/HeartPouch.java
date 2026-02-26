package net.kamkeyke.heartpouch;

import com.mojang.logging.LogUtils;
import net.kamkeyke.heartpouch.networking.ModNetworking;
import net.kamkeyke.heartpouch.registry.CreativeModeTab;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(HeartPouch.MODID)
public class HeartPouch
{
    public static final String MODID = "heartpouch";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HeartPouch(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        CreativeModeTab.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
    }
}
