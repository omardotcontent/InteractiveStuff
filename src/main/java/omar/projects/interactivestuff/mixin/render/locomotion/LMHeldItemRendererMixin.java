package omar.projects.interactivestuff.mixin.render.locomotion;

import com.trainguy9512.locomotion.animation.joint.JointChannel;
import com.trainguy9512.locomotion.render.FirstPersonPlayerRenderer;
import com.trainguy9512.locomotion.render.ItemRenderType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(value = FirstPersonPlayerRenderer.class, priority = 2000)
public class LMHeldItemRendererMixin {

    private static Method renderItemHandle;

    // RECURSION GUARD: Prevents infinite loops when we call the method again
    private static final ThreadLocal<Boolean> IS_RENDERING = ThreadLocal.withInitial(() -> false);

    @Inject(
            method = "renderItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void interactivestuff$renderItemHead(
            LivingEntity entity,
            ItemStack originalStack,
            ItemDisplayContext displayContext,
            MatrixStack matrices,
            JointChannel jointChannel,
            @Coerce Object vertexConsumers,
            int light,
            Arm side,
            Hand interactionHand,
            ItemRenderType renderType,
            CallbackInfo ci
    ) {
        // 1. CHECK GUARD: If we are already handling this call, return immediately
        // This allows the reflection call (step 4) to actually execute the method body.
        if (IS_RENDERING.get()) {
            return;
        }

        ItemStack modifiedStack = ScriptInterpreter.itemUpdate(originalStack, matrices);

        // If unchanged, let the original run normally
        if (modifiedStack == originalStack) {
            return;
        }

        // If empty, cancel and stop
        if (modifiedStack.isEmpty()) {
            ci.cancel();
            return;
        }

        try {
            // 2. SET GUARD: Mark that we are actively modifying the render
            IS_RENDERING.set(true);

            if (renderItemHandle == null) {
                for (Method m : FirstPersonPlayerRenderer.class.getDeclaredMethods()) {
                    if (m.getName().equals("renderItem") && m.getParameterCount() == 10) {
                        m.setAccessible(true);
                        renderItemHandle = m;
                        break;
                    }
                }
            }

            if (renderItemHandle != null) {
                // 3. INVOKE REFLECTION: This triggers 'renderItem' again.
                // Because IS_RENDERING is true, the Mixin will skip logic and let it run.
                renderItemHandle.invoke(
                        this,
                        entity,
                        modifiedStack,
                        displayContext,
                        matrices,
                        jointChannel,
                        vertexConsumers,
                        light,
                        side,
                        interactionHand,
                        renderType
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 4. RESET GUARD: Important! Reset so the next frame works
            IS_RENDERING.set(false);
        }

        // 5. CANCEL ORIGINAL: Stop the original renderItem from drawing the old stack
        ci.cancel();
    }
}