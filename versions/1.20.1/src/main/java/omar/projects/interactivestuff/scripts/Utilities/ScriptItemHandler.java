package omar.projects.interactivestuff.scripts.Utilities;

import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import java.util.*;

/** Explicit pre-component compatibility: known components map to vanilla item NBT. */
public final class ScriptItemHandler {
    public static final String MODEL_KEY="InteractiveStuffModel";
    private static final Map<String,String> KEYS=Map.of("item_model",MODEL_KEY,"custom_model_data","CustomModelData","damage","Damage","unbreakable","Unbreakable","enchantments","Enchantments","repair_cost","RepairCost");
    private ScriptItemHandler() {}
    private static String name(String id){return id.startsWith("minecraft:")?id.substring(10):id;}
    private static String key(String id){String name=name(id);if(id.startsWith("nbt:"))return id.substring(4);String key=KEYS.get(name);if(key==null)throw new IllegalArgumentException("Component "+id+" is unavailable on 1.20.1; use a supported legacy mapping or nbt:TagName.");return key;}
    public static ItemStack apply(ItemStack stack,String id,String raw){
        if(stack==null||id==null||raw==null)return stack;
        Tag value=parse(raw);String name=name(id);
        if(name.equals("custom_data")){
            if(!(value instanceof CompoundTag tag))throw new IllegalArgumentException("Custom data must be a compound SNBT value.");
            String model=stack.getTag()==null?"":stack.getTag().getString(MODEL_KEY);
            CompoundTag copy=tag.copy();if(!model.isEmpty())copy.putString(MODEL_KEY,model);stack.setTag(copy);return stack;
        }
        if(name.equals("custom_name")||name.equals("lore")){
            CompoundTag display=stack.getOrCreateTagElement("display");
            if(name.equals("custom_name"))display.putString("Name",value instanceof StringTag?v(value):value.toString());
            else if(value instanceof ListTag)display.put("Lore",value);else throw new IllegalArgumentException("Lore requires a list of text JSON strings.");
            return stack;
        }
        stack.getOrCreateTag().put(key(id),value);return stack;
    }
    public static String getComponent(ItemStack stack,String id){Tag tag=get(stack,id);return tag==null?null:tag.toString();}
    public static boolean hasComponent(ItemStack stack,String id){return get(stack,id)!=null;}
    private static Tag get(ItemStack stack,String id){
        if(stack==null||stack.isEmpty()||stack.getTag()==null)return null;
        String name=name(id);if(name.equals("custom_data")){var copy=stack.getTag().copy();copy.remove(MODEL_KEY);return copy;}
        if(name.equals("custom_name")||name.equals("lore")){CompoundTag display=stack.getTagElement("display");return display==null?null:display.get(name.equals("lore")?"Lore":"Name");}
        return stack.getTag().get(key(id));
    }
    public static void remove(ItemStack stack,String id){
        if(stack==null||stack.getTag()==null)return;String name=name(id);
        if(name.equals("custom_data")){String model=stack.getTag().getString(MODEL_KEY);stack.setTag(null);if(!model.isEmpty())stack.getOrCreateTag().putString(MODEL_KEY,model);}
        else if(name.equals("custom_name")||name.equals("lore")){var display=stack.getTagElement("display");if(display!=null)display.remove(name.equals("lore")?"Lore":"Name");}
        else stack.getTag().remove(key(id));
    }
    public static List<String> getComponentIds(ItemStack stack){
        List<String> ids=new ArrayList<>();if(stack==null||stack.getTag()==null)return ids;
        ids.add("minecraft:custom_data");for(var entry:KEYS.entrySet())if(stack.getTag().contains(entry.getValue()))ids.add("minecraft:"+entry.getKey());
        if(stack.getTagElement("display")!=null){if(stack.getTagElement("display").contains("Name"))ids.add("minecraft:custom_name");if(stack.getTagElement("display").contains("Lore"))ids.add("minecraft:lore");}
        return ids;
    }
    public static List<Map<String,String>> getComponents(ItemStack stack){return getComponentIds(stack).stream().map(id->Map.of("id",id,"value",getComponent(stack,id))).toList();}
    private static String v(Tag tag){return tag.getAsString();}
    private static Tag parse(String raw){
        String value=raw.trim();if(!value.startsWith("{")&&!value.startsWith("[")&&!value.startsWith("\"")&&!value.startsWith("'")&&value.contains(":"))return StringTag.valueOf(value);
        try{return new TagParser(new com.mojang.brigadier.StringReader(value)).readValue();}catch(com.mojang.brigadier.exceptions.CommandSyntaxException e){throw new IllegalArgumentException("Invalid legacy component SNBT: "+e.getMessage(),e);}
    }
}
