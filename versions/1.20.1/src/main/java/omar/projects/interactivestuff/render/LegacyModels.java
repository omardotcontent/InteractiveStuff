package omar.projects.interactivestuff.render;

import com.google.gson.*;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import omar.projects.interactivestuff.mixin.render.ModelManagerAccessor;
import omar.projects.interactivestuff.scripts.Utilities.ScriptItemHandler;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/** Bake standalone script models during reload; understand this pack's basic modern definitions. */
public final class LegacyModels {
    private static volatile Map<ResourceLocation,JsonObject> definitions=Map.of();
    private LegacyModels() {}
    private record Prepared(Set<ResourceLocation> models,Map<ResourceLocation,JsonObject> definitions) {}
    public static void register(){
        PreparableModelLoadingPlugin.register((manager,executor)->CompletableFuture.supplyAsync(()->{
            Set<ResourceLocation> models=new LinkedHashSet<>();Map<ResourceLocation,JsonObject> defs=new LinkedHashMap<>();
            manager.listResources("models",id->id.getPath().endsWith(".json")&&!id.getNamespace().equals("minecraft")).keySet().forEach(id->models.add(new ResourceLocation(id.getNamespace(),id.getPath().substring(7,id.getPath().length()-5))));
            manager.listResources("items",id->id.getPath().endsWith(".json")).forEach((id,resource)->{
                try(var reader=new InputStreamReader(resource.open(),StandardCharsets.UTF_8)){
                    JsonObject definition=JsonParser.parseReader(reader).getAsJsonObject().getAsJsonObject("model");
                    defs.put(new ResourceLocation(id.getNamespace(),id.getPath().substring(6,id.getPath().length()-5)),definition);collect(definition,models);
                }catch(Exception error){omar.projects.interactivestuff.IS.LOGGER.warn("Unable to adapt item definition {} for 1.20.1",id,error);}
            });
            return new Prepared(models,Map.copyOf(defs));
        },executor),(data,context)->{definitions=data.definitions();context.addModels(data.models());});
    }
    private static void collect(JsonObject node,Set<ResourceLocation> result){
        if(node==null||!node.has("type"))return;
        if(node.get("type").getAsString().equals("minecraft:model"))result.add(new ResourceLocation(node.get("model").getAsString()));
        else if(node.get("type").getAsString().equals("minecraft:select")&&node.has("property")&&node.get("property").getAsString().equals("minecraft:display_context")){
            if(node.has("fallback"))collect(node.getAsJsonObject("fallback"),result);
            if(node.has("cases"))for(var entry:node.getAsJsonArray("cases"))collect(entry.getAsJsonObject().getAsJsonObject("model"),result);
        }
    }
    private static ResourceLocation model(JsonObject node,ItemDisplayContext mode){
        if(node==null||!node.has("type"))return null;
        String type=node.get("type").getAsString();
        if(type.equals("minecraft:model"))return new ResourceLocation(node.get("model").getAsString());
        if(type.equals("minecraft:select")&&node.get("property").getAsString().equals("minecraft:display_context")){
            for(var element:node.getAsJsonArray("cases")){
                var entry=element.getAsJsonObject();var when=entry.get("when");
                boolean matched=when.isJsonPrimitive()?when.getAsString().equals(mode.getSerializedName()):java.util.stream.StreamSupport.stream(when.getAsJsonArray().spliterator(),false).anyMatch(x->x.getAsString().equals(mode.getSerializedName()));
                if(matched)return model(entry.getAsJsonObject("model"),mode);
            }
            return node.has("fallback")?model(node.getAsJsonObject("fallback"),mode):null;
        }
        throw new IllegalArgumentException("This item definition requires a legacy 1.20.1 model override: "+type);
    }
    public static BakedModel resolve(ItemStack stack,ItemRenderer renderer,net.minecraft.world.entity.LivingEntity entity,ItemDisplayContext mode,int seed){
        String custom=stack.getTag()==null?"":stack.getTag().getString(ScriptItemHandler.MODEL_KEY);
        ResourceLocation itemId=custom.isEmpty()?BuiltInRegistries.ITEM.getKey(stack.getItem()):new ResourceLocation(custom);
        ResourceLocation modelId=model(definitions.get(itemId),mode);
        if(modelId==null){
            if(custom.isEmpty())return renderer.getModel(stack,entity.level(),entity,seed);
            // Vanilla item IDs resolve through inventory models, not standalone item/... keys.
            // Keep the original stack for self overrides so native item predicates still apply.
            if(itemId.getNamespace().equals("minecraft")&&BuiltInRegistries.ITEM.containsKey(itemId)){
                ItemStack modelStack=stack.getItem()==BuiltInRegistries.ITEM.get(itemId)?stack:new ItemStack(BuiltInRegistries.ITEM.get(itemId));
                return renderer.getModel(modelStack,entity.level(),entity,seed);
            }
        }
        if(modelId==null)modelId=new ResourceLocation(itemId.getNamespace(),"item/"+itemId.getPath());
        var manager=Minecraft.getInstance().getModelManager();
        BakedModel baked=((ModelManagerAccessor)manager).interactivestuff$models().get(modelId);
        if(baked==null)throw new IllegalArgumentException("No baked script model "+modelId);
        return baked;
    }
}
