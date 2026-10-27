package me.dmt.horsefly.mixin;

import me.dmt.horsefly.modules.HorseFly;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void horsefly$travel(Vec3 input, CallbackInfo ci) {
        HorseFly hf = Modules.get() == null ? null : Modules.get().get(HorseFly.class);
        if (hf != null && hf.travel((LivingEntity) (Object) this)) ci.cancel();
    }
}
