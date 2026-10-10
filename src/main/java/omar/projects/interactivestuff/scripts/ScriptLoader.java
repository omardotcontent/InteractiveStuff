package omar.projects.interactivestuff.scripts;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import omar.projects.interactivestuff.IS;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ScriptLoader {
    private ScriptLoader() {}

    public static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath(IS.MOD_ID, "script_loader");
            }

            @Override public void onResourceManagerReload(ResourceManager manager) {
                ScriptInterpreter.clearScripts();
                Map<String, ArrayList<Source>> packs = new LinkedHashMap<>();
                manager.listPacks().forEach(pack -> packs.put(pack.packId(), new ArrayList<>()));
                manager.listResources("scripts", id -> id.getNamespace().equals(IS.MOD_ID) && id.getPath().endsWith(".vyn"))
                        .keySet().stream().sorted(Comparator.comparing(Identifier::toString)).forEach(id -> {
                            for (Resource resource : manager.getResourceStack(id)) {
                                try (var stream = resource.open()) {
                                    String name = id.getPath().substring("scripts/".length(), id.getPath().length() - 4);
                                    packs.computeIfAbsent(resource.sourcePackId(), ignored -> new ArrayList<>())
                                            .add(new Source(name, new String(stream.readAllBytes(), StandardCharsets.UTF_8)));
                                } catch (Exception error) {
                                    IS.LOGGER.error("Failed to read script {} from {}", id, resource.sourcePackId(), error);
                                }
                            }
                        });
                packs.forEach((pack, sources) -> sources.forEach(source ->
                        ScriptInterpreter.addScript(pack, source.name, source.code)));
                ScriptInterpreter.loadScripts();
            }
        });
    }

    private record Source(String name, String code) {}
}
