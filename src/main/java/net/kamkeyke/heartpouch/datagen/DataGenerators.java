package net.kamkeyke.heartpouch.datagen;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.raccooncore.datagen.RaccoonData;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HeartPouch.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        RaccoonData data = new RaccoonData(event);

        data.server(new ModRecipeProvider(data.output()));
    }
}
