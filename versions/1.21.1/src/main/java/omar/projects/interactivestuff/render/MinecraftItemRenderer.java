package omar.projects.interactivestuff.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import omar.projects.interactivestuff.compat.MinecraftBindings;
import omar.projects.interactivestuff.handlers.config.ConfigHandler;
import omar.projects.interactivestuff.scripts.variables.ItemModel;
import java.util.*;

/** Legacy baked-model renderer; script transforms precede native display/centering. */
public final class MinecraftItemRenderer {
 private MinecraftItemRenderer() {}
 public static void clearCaches() {}
 public static void submit(ItemModel item,ItemRenderer renderer,LivingEntity entity,ItemDisplayContext mode,PoseStack matrices,MultiBufferSource queue,int light){
  var stack=item.getFinalStack();if(stack==null||stack.isEmpty())return;
  BakedModel model=LegacyModels.resolve(stack,renderer,entity,mode,entity.getId()+mode.ordinal()+item.getUniqueSeed());
  int effectiveLight=item.getLight()<0?light:item.getLight();
  boolean recolor=ConfigHandler.INSTANCE.resourcePackColorChanging&&item.hasColorChanges();
  matrices.pushPose();
  try{
   if(model.isCustomRenderer()||(!item.isPartialSelection()&&!recolor&&item.getGlint()<0)){
    item.apply(matrices);renderer.render(stack,mode,MinecraftBindings.leftHand(mode),matrices,queue,effectiveLight,OverlayTexture.NO_OVERLAY,model);return;
   }
   List<BakedQuad> quads=new ArrayList<>();var random=RandomSource.create();
   for(Direction side:Direction.values()){random.setSeed(42);quads.addAll(model.getQuads(null,side,random));}
   random.setSeed(42);quads.addAll(model.getQuads(null,null,random));
   var selection=item.isPartialSelection()?QuadSelection.resolve(item.getQuadStart(),item.getQuadEnd(),quads.size()):new QuadSelection(0,quads.size());
   var type=ItemBlockRenderTypes.getRenderType(stack,true);
   boolean glint=item.getGlint()<0?stack.hasFoil():item.getGlint()>0;
   var vertices=item.getGlint()==2?ItemRenderer.getCompassFoilBuffer(queue,type,matrices.last()):ItemRenderer.getFoilBufferDirect(queue,type,true,glint);
   for(int q=0;q<quads.size();q++){
    boolean selected=q>=selection.start()&&q<selection.end();if(!selected&&item.isExclusive())continue;
    matrices.pushPose();
    try{
     if(selected)item.apply(matrices);
     model.getTransforms().getTransform(mode).apply(MinecraftBindings.leftHand(mode),matrices);matrices.translate(-.5,-.5,-.5);
     BakedQuad quad=quads.get(q);int tint=quad.isTinted()?((omar.projects.interactivestuff.mixin.render.ItemRendererAccessor) renderer).interactivestuff$itemColors().getColor(stack,quad.getTintIndex()):0xFFFFFF;
     int color=0xFF000000|tint&0xFFFFFF;
     if(selected&&recolor)color=item.getUseSelectionColor()?item.getSelectionColor():item.getFinalColorForIndex(quad.getTintIndex(),color);
     int[] data=quad.getVertices();
     for(int i=0;i<4;i++){
      int offset=i*8,packed=data[offset+3];float red=(packed&255)/255f*((color>>>16)&255)/255f,green=((packed>>>8)&255)/255f*((color>>>8)&255)/255f,blue=((packed>>>16)&255)/255f*(color&255)/255f,alpha=((packed>>>24)&255)/255f*((color>>>24)&255)/255f;
      vertices.addVertex(matrices.last(),Float.intBitsToFloat(data[offset]),Float.intBitsToFloat(data[offset+1]),Float.intBitsToFloat(data[offset+2]))
       .setColor(red,green,blue,alpha).setUv(Float.intBitsToFloat(data[offset+4]),Float.intBitsToFloat(data[offset+5])).setOverlay(OverlayTexture.NO_OVERLAY).setLight(effectiveLight)
       .setNormal(matrices.last(),quad.getDirection().getStepX(),quad.getDirection().getStepY(),quad.getDirection().getStepZ());
     }
    }finally{matrices.popPose();}
   }
  }finally{matrices.popPose();}
 }
 public static org.joml.Matrix4f anchor(ItemModel item,ItemRenderer renderer,LivingEntity entity,ItemDisplayContext mode,PoseStack matrices){
  var model=LegacyModels.resolve(item.getFinalStack(),renderer,entity,mode,entity.getId()+mode.ordinal()+item.getUniqueSeed());
  matrices.pushPose();try{item.apply(matrices);model.getTransforms().getTransform(mode).apply(MinecraftBindings.leftHand(mode),matrices);matrices.translate(-.5,-.5,-.5);return ParticleView.capture(matrices.last().pose());}finally{matrices.popPose();}
 }
}
