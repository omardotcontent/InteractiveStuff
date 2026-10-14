package omar.projects.interactivestuff.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omar.projects.interactivestuff.IS;
import omar.projects.interactivestuff.render.MinecraftItemRenderer;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import omar.projects.interactivestuff.scripts.Utilities.ItemModelRenderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Intercepts the item after Minecraft has applied its native arm, swing and use transforms. */
@Mixin(value = FirstPersonHandsAndItemsRenderer.class, priority = 2000)
public abstract class HeldItemRendererMixin {
    @Unique private boolean interactivestuff$reportedRenderFailure;

    @Redirect(method = "submitArmWithItem", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"))
    private void interactivestuff$submitItem(ItemStackRenderState nativeState, PoseStack matrices,
            SubmitNodeCollector queue, int light, int overlay, int outline,
            PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state,
            float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack stack,
            float inverseArmHeight, PoseStack armMatrices, SubmitNodeCollector armQueue, int armLight) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !ScriptInterpreter.hasItemCallbacks(stack)) {
            nativeState.submit(matrices, queue, light, overlay, outline);
            return;
        }
        boolean mainHand = hand == InteractionHand.MAIN_HAND;
        boolean left = (playerState.avatarRenderState.mainArm == HumanoidArm.LEFT) == mainHand;
        ItemDisplayContext mode = left ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND : ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        try (var scope = ItemModelRenderRegistry.begin(mode, mainHand)) {
            var item = ScriptInterpreter.itemUpdate(stack, mode, mainHand);
            if (item == null) nativeState.submit(matrices, queue, light, overlay, outline);
            else MinecraftItemRenderer.submit(item, client.getItemModelResolver(), client.player, mode, matrices, queue, light);
            for (var extra : scope.models())
                MinecraftItemRenderer.submit(extra, client.getItemModelResolver(), client.player, mode, matrices, queue, light);
            for (var binding : scope.particles()) {
                var model = binding.item();
                omar.projects.interactivestuff.particles.ParticleSystem.INSTANCE.bind(binding.emitter(), mainHand,
                        net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), scope.anchor(model, () -> MinecraftItemRenderer.anchor(model, client.getItemModelResolver(), client.player, mode, matrices)), light);
            }
            for (var model : scope.pivots()) {
                omar.projects.interactivestuff.render.ModelDebugMarkers.capture(model, matrices);
            }
        } catch (Exception error) {
            if (!interactivestuff$reportedRenderFailure) {
                interactivestuff$reportedRenderFailure = true;
                IS.LOGGER.error("Script item rendering failed; falling back to Minecraft rendering", error);
            }
            nativeState.submit(matrices, queue, light, overlay, outline);
        }
    }
}
