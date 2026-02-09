package net.kamkeyke.heartpouch.networking.packet;

import net.kamkeyke.heartpouch.HeartPouch;
import net.kamkeyke.heartpouch.api.HeartPouchData;
import net.kamkeyke.heartpouch.item.HeartstonePouchItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SSetActiveHeartstone {
    public enum Source { MAIN_HAND, OFF_HAND, CONTAINER_SLOT, INVENTORY_SLOT }

    public final Source source;
    public final int slotIndex;
    public final int activeIndex;
    public final int windowId;
    public final CompoundTag pouchTag;

    public C2SSetActiveHeartstone(Source source, int slotIndex, int activeIndex, int windowId, CompoundTag pouchTag){
        this.source = source;
        this.slotIndex = slotIndex;
        this.activeIndex = activeIndex;
        this.windowId = windowId;
        this.pouchTag = pouchTag != null ? pouchTag.copy() : null;
    }

    public C2SSetActiveHeartstone(FriendlyByteBuf buf){
        this.source = buf.readEnum(Source.class);
        this.slotIndex = buf.readInt();
        this.activeIndex = buf.readInt();
        this.windowId = buf.readInt();
        this.pouchTag = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeEnum(source);
        buf.writeInt(slotIndex);
        buf.writeInt(activeIndex);
        buf.writeInt(windowId);
        buf.writeNbt(pouchTag);
    }

    public static void handle(C2SSetActiveHeartstone msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack pouch = ItemStack.EMPTY;

            if (msg.source == Source.MAIN_HAND) {
                pouch = player.getMainHandItem();
            } else if (msg.source == Source.OFF_HAND) {
                pouch = player.getOffhandItem();
            } else {
                if (msg.windowId == player.containerMenu.containerId) {
                    int i = msg.slotIndex;
                    if (i >= 0 && i < player.containerMenu.slots.size()) {
                        pouch = player.containerMenu.getSlot(i).getItem();
                    }
                }

                if ((pouch.isEmpty() || !(pouch.getItem() instanceof HeartstonePouchItem)) && msg.pouchTag != null) {
                    pouch = findPouchByTagFallback(player, msg.pouchTag);
                }
            }

            if (pouch.isEmpty() || !(pouch.getItem() instanceof HeartstonePouchItem)) {
                HeartPouch.LOGGER.warn("[C2SSetActiveHeartstone]: Pouch not found! Source: {}, Index: {}, windowId: {}", msg.source, msg.slotIndex, msg.windowId);
                return;
            }

            int size = HeartPouchData.size(pouch);
            if (size <= 0) return;

            int clamped = Math.min(Math.max(msg.activeIndex, 0), size - 1);
            HeartPouchData.setActive(pouch, clamped);

            player.containerMenu.broadcastChanges();
            player.inventoryMenu.broadcastChanges();
        });

        contextSupplier.get().setPacketHandled(true);
    }

    /**
     * Fallback "bruteforce" para localizar o pouch no jogador copiando o CompoundTag enviado.
     * Roda por último, porque é relativamente custoso. Retorna o ItemStack encontrado (referência do server),
     * ou {@link ItemStack}.EMPTY se não achou.
     */
    private static ItemStack findPouchByTagFallback(ServerPlayer player, CompoundTag tagToMatch) {
        if (tagToMatch == null) return ItemStack.EMPTY;

        ItemStack main = player.getMainHandItem();
        if (matchesTag(main, tagToMatch) && main.getItem() instanceof HeartstonePouchItem) return main;

        ItemStack off = player.getOffhandItem();
        if (matchesTag(off, tagToMatch) && off.getItem() instanceof HeartstonePouchItem) return off;

        for (ItemStack inv : player.getInventory().items) {
            if (matchesTag(inv, tagToMatch) && inv.getItem() instanceof HeartstonePouchItem) return inv;
        }

        for (Slot s : player.containerMenu.slots) {
            ItemStack sItem = s.getItem();
            if (matchesTag(sItem, tagToMatch) && sItem.getItem() instanceof HeartstonePouchItem) return sItem;
        }

        return ItemStack.EMPTY;
    }

    @SuppressWarnings("DataFlowIssue")
    private static boolean matchesTag(ItemStack stack, CompoundTag tag) {
        if (stack == null || stack.isEmpty() || !stack.hasTag()) return false;
        CompoundTag t = stack.getTag();
        return t.equals(tag);
    }
}
