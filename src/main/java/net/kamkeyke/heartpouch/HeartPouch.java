package net.kamkeyke.heartpouch;

import com.mojang.logging.LogUtils;
import net.kamkeyke.heartpouch.networking.ModNetworking;
import net.kamkeyke.heartpouch.registry.CreativeModeTab;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.heartpouch.registry.ModRecipeSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
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
        ModRecipeSerializers.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
    }

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
