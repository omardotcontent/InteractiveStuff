package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omar.projects.interactivestuff.IS;
import omar.projects.interactivestuff.render.MinecraftItemRenderer;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemInHandRenderer.class, priority = 2000)
public abstract class HeldItemRendererMixin {
    @Final @Shadow private ItemModelResolver itemModelResolver;
    @Unique private boolean interactivestuff$reportedRenderFailure;

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void interactivestuff$renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext mode,
                                            PoseStack matrices, MultiBufferSource queue, int light, CallbackInfo ci) {
        if (!mode.firstPerson() || entity != Minecraft.getInstance().player || !ScriptInterpreter.hasItemCallbacks(stack)) return;
        boolean mainHand = mode.leftHand() == (entity.getMainArm() == HumanoidArm.LEFT);
        try (var scope = ItemModelRenderRegistry.begin(mode, mainHand)) {
            ItemModel item = ScriptInterpreter.itemUpdate(stack, mode, mainHand);
            if (item != null) MinecraftItemRenderer.submit(item, itemModelResolver, entity, mode, matrices, queue, light);
            for (ItemModel extra : scope.models()) {
                MinecraftItemRenderer.submit(extra, itemModelResolver, entity, mode, matrices, queue, light);
            }
            // Extra-only callbacks must keep the original item visible through Minecraft's normal renderer.
            if (item != null) ci.cancel();
        } catch (Exception error) {
            if (!interactivestuff$reportedRenderFailure) {
                interactivestuff$reportedRenderFailure = true;
                IS.LOGGER.error("Script item rendering failed; falling back to Minecraft rendering", error);
            }
        }
    }
}
