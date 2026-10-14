package omar.projects.interactivestuff.render;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
public final class ParticleVertex {
 private ParticleVertex() {}
 public static void write(VertexConsumer vertices,PoseStack.Pose pose,float x,float y,float z,int color,float u,float v,int light){vertices.vertex(pose.pose(),x,y,z).color(color).uv(u,v).uv2(light).endVertex();}
}
