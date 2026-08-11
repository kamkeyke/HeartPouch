package net.kamkeyke.heartpouch.item;

import net.kamkeyke.heartpouch.data.HeartPouchData;
import net.mehvahdjukaar.heartstone.Heartstone;
import net.mehvahdjukaar.heartstone.HeartstoneItem;
import net.mehvahdjukaar.heartstone.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeartstonePouchItem extends Item {
    public HeartstonePouchItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack pouch = pPlayer.getItemInHand(pUsedHand);

        if (pLevel.isClientSide) {
            return InteractionResultHolder.success(pouch);
        }

        CompoundTag heart = HeartPouchData.getActiveHeart(pouch);
        if (heart == null) {
            pLevel.playSound(null, pPlayer, SoundEvents.AMETHYST_BLOCK_FALL, pPlayer.getSoundSource(), 0.6f, 0.7f);
            pPlayer.getCooldowns().addCooldown(this, 60);
            pPlayer.displayClientMessage(
                    Component.literal("The Heartstone Pouch is empty!"),
                    true
            );
            return InteractionResultHolder.consume(pouch);
        }

        ItemStack fakeHeart = new ItemStack(Heartstone.HEARTSTONE_ITEM.get());
        fakeHeart.setTag(heart.copy());

        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        Player target = HeartstoneItem.getBoundPlayer(pPlayer, fakeHeart, true);
        if (target != null) {
            NetworkHandler.sendHeartstoneParticles(pPlayer, target);
        } else {
            pLevel.playSound(null, pPlayer, SoundEvents.AMETHYST_BLOCK_FALL, pPlayer.getSoundSource(), 0.6f, 0.7f);
        }

        pPlayer.getCooldowns().addCooldown(this, 60);
        return InteractionResultHolder.consume(pouch);
    }

    @Override
    public boolean overrideStackedOnOther(@NotNull ItemStack pStack, Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer) {
        ItemStack other = pSlot.getItem();

        if (pAction == ClickAction.SECONDARY && other.getItem() instanceof HeartstoneItem) {
            CompoundTag tag = other.getTag();
            if (tag != null && tag.contains("Id")) {
                HeartPouchData.add(pStack, tag);
                other.shrink(1);
                pPlayer.playSound(SoundEvents.BUNDLE_INSERT, 0.8f, 1.1f);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(@NotNull ItemStack pStack, @NotNull ItemStack pOther, @NotNull Slot pSlot, @NotNull ClickAction pAction, @NotNull Player pPlayer, @NotNull SlotAccess pAccess) {
        if (pAction == ClickAction.SECONDARY && pOther.isEmpty()) {
            CompoundTag removed = HeartPouchData.removeActive(pStack);
            if (removed == null) return false;

            ItemStack heart = new ItemStack(Heartstone.HEARTSTONE_ITEM.get());
            heart.setTag(removed);

            pAccess.set(heart);

            pPlayer.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8f, 1.0f);
            return true;
        } else if (pAction == ClickAction.SECONDARY && pOther.getItem() instanceof HeartstoneItem){
            CompoundTag tag = pOther.getTag();
            if (tag != null && tag.contains("Id")) {
                HeartPouchData.add(pStack, tag);
                pOther.shrink(1);
                pPlayer.playSound(SoundEvents.BUNDLE_INSERT, 0.8f, 1.1f);
                return true;
            }
        }

        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        int size = HeartPouchData.size(pStack);
        if(size <= 0){
            pTooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (!Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.literal(size + " Heartstone" + (size == 1 ? "" : "s")).withStyle(ChatFormatting.GRAY));

            CompoundTag activeTag = HeartPouchData.getActiveHeart(pStack);
            if(activeTag != null && activeTag.contains("Id")) {
                int id = activeTag.getInt("Id");
                String name = HeartPouchData.getHeartName(activeTag);
                pTooltipComponents.add(Component.literal("> ")
                        .append(Component.literal(name + " [Id: " + id + "]")).withStyle(ChatFormatting.BOLD)
                );
            }

            pTooltipComponents.add(Component.literal("Hold SHIFT for details").withStyle(ChatFormatting.DARK_GRAY));
        } else {
            pTooltipComponents.add(Component.literal(size + " Heartstone" + (size == 1 ? "" : "s")).append(Component.literal(":")).withStyle(ChatFormatting.GRAY));

            List<CompoundTag> hearts = HeartPouchData.getAllHearts(pStack);
            int active = HeartPouchData.getActive(pStack);

            for (int i = 0; i < hearts.size(); i++) {
                CompoundTag tag = hearts.get(i);

                int id = tag.getInt("Id");
                String name = HeartPouchData.getHeartName(tag);

                boolean isActive = i == active;

                MutableComponent line = Component.literal(
                        (isActive ? "> " : "  ") +
                                name +
                                " [Id: " + id + "]"
                );

                if (isActive) {
                    line = line.withStyle(
                            ChatFormatting.BOLD
                    );
                } else {
                    line = line.withStyle(ChatFormatting.GRAY);
                }

                pTooltipComponents.add(line);
            }
        }
    }

}
