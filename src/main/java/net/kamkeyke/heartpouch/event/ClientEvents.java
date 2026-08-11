package net.kamkeyke.heartpouch.event;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.data.HeartPouchData;
import net.kamkeyke.heartpouch.item.HeartstonePouchItem;
import net.kamkeyke.heartpouch.networking.ModNetworking;
import net.kamkeyke.heartpouch.networking.packet.C2SSetActiveHeartstone;
import net.kamkeyke.heartpouch.registry.ModItems;
import net.kamkeyke.raccooncore.util.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {

    @Mod.EventBusSubscriber(modid = HeartPouch.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientForgeEvents{

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onScroll(InputEvent.MouseScrollingEvent event) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            if (!player.isShiftKeyDown()) return;

            ItemStack pouch = ItemStack.EMPTY;
            C2SSetActiveHeartstone.Source source = null;
            int slotIndex = -1;

            // No mundo
            if (pouch.isEmpty()) {
                ItemStack hand = player.getMainHandItem();
                ItemStack offhand = player.getOffhandItem();
                if (hand.getItem() instanceof HeartstonePouchItem) {
                    pouch = hand;
                    source = C2SSetActiveHeartstone.Source.MAIN_HAND;
                    slotIndex = 0;
                } else if (offhand.getItem() instanceof HeartstonePouchItem){
                    pouch = offhand;
                    source = C2SSetActiveHeartstone.Source.OFF_HAND;
                    slotIndex = 0;
                }
            }

            if (pouch.isEmpty() || source == null) return;

            int size = HeartPouchData.size(pouch);
            if (size <= 1) return;

            int dir = event.getScrollDelta() > 0 ? -1 : 1;
            int current = HeartPouchData.getActive(pouch);
            int next = Math.floorMod(current + dir, size);

            HeartPouchData.setActive(pouch, next);
            ModNetworking.INSTANCE.sendToServer(new C2SSetActiveHeartstone(source, slotIndex, next, -1, null));

            CompoundTag activeTag = HeartPouchData.getActiveHeart(pouch);
            if(activeTag != null) {
                int id = activeTag.getInt("Id");
                String name = HeartPouchData.getHeartName(activeTag);


                player.displayClientMessage(
                        Component.literal("Selected:§r§7 " + name + " [Id: " + id + "] " + "(" + (next + 1) + "/" + size + ")"),
                        true
                );
            }

            player.playSound(
                    SoundEvents.UI_BUTTON_CLICK.get(),
                    0.6f,
                    1.2f
            );

            event.setCanceled(true);
        }

        @SuppressWarnings("DataFlowIssue")
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void OnScreenScroll(ScreenEvent.MouseScrolled.Pre event){
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            if (!Screen.hasShiftDown()) return;

            ItemStack pouch = ItemStack.EMPTY;
            C2SSetActiveHeartstone.Source source = null;
            int slotIndex = -1;
            int windowId = -1;
            CompoundTag pouchTag = null;

            // No inventário
            if (mc.screen instanceof AbstractContainerScreen<?> screen) {
                Slot hovered = screen.getSlotUnderMouse();
                if (hovered != null && hovered.hasItem()) {
                    ItemStack hoveredStack = hovered.getItem();
                    if (hoveredStack.getItem() instanceof HeartstonePouchItem) {
                        pouch = hoveredStack;
                        source = screen instanceof InventoryScreen ? C2SSetActiveHeartstone.Source.INVENTORY_SLOT : C2SSetActiveHeartstone.Source.CONTAINER_SLOT;
                        slotIndex = hovered.index;
                        windowId = screen.getMenu().containerId;
                        pouchTag = pouch.hasTag() ? pouch.getTag().copy() : null;
                    }
                }
            }

            if (pouch.isEmpty() || source == null) return;

            int size = HeartPouchData.size(pouch);
            if (size <= 1) return;

            int dir = event.getScrollDelta() > 0 ? -1 : 1;
            int current = HeartPouchData.getActive(pouch);
            int next = Math.floorMod(current + dir, size);

            HeartPouchData.setActive(pouch, next);
            ModNetworking.INSTANCE.sendToServer(new C2SSetActiveHeartstone(source, slotIndex, next, windowId, pouchTag));

            player.playSound(
                    SoundEvents.UI_BUTTON_CLICK.get(),
                    0.6f,
                    1.2f
            );

            event.setCanceled(true);
        }

    }

    @Mod.EventBusSubscriber(modid = HeartPouch.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents{

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event){
            int defaultColor = ColorUtils.toRgb(DyeColor.PINK);

            event.register((
                    (pStack, pTintIndex) ->
                            pTintIndex == 0 ? ColorUtils.getColor(pStack, defaultColor) : -1
                    ),
                    ModItems.HEARTSTONE_POUCH.get()
            );
        }
    }
}
