package net.kamkeyke.heartpouch.mixin;

import net.kamkeyke.heartpouch.data.HeartPouchData;
import net.kamkeyke.heartpouch.item.HeartstonePouchItem;
import net.mehvahdjukaar.heartstone.HeartstoneItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HeartstoneItem.class)
public class HeartstoneItemMixin {

    @Inject(
            method = "hasMatchingId",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void checkPouch(Long id, ItemStack s, CallbackInfoReturnable<Boolean> cir) {
        if (!(s.getItem() instanceof HeartstonePouchItem)) return;

        if (id == null) {
            cir.setReturnValue(false);
            return;
        }

        for (CompoundTag heart : HeartPouchData.getAllHearts(s)) {
            if (!heart.contains("Id")) continue;

            long otherId = heart.getLong("Id");
            if (id.equals(otherId)) {
                cir.setReturnValue(true);
                return;
            }
        }

        cir.setReturnValue(false);
    }
}
