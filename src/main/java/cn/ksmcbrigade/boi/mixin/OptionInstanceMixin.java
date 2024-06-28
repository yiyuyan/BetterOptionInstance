package cn.ksmcbrigade.boi.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin(OptionInstance.class)
public abstract class OptionInstanceMixin<T> {
    @Shadow
    T value;

    @Shadow @Final private Consumer<T> onValueUpdate;

    @Inject(method = "set",at = @At("HEAD"),cancellable = true)
    public void set(T t, CallbackInfo ci){
        if (!Minecraft.getInstance().isRunning()) {
            this.value = t;
        } else {
            if (!Objects.equals(this.value, t)) {
                this.value = t;
                this.onValueUpdate.accept(this.value);
            }
        }
        ci.cancel();
    }
}
