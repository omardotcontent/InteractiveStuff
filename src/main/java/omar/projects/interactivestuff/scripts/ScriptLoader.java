package omar.projects.interactivestuff.scripts;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import omar.projects.interactivestuff.IS;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ScriptLoader {

    private ScriptLoader() {
    }

    public static void register() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                ScriptInterpreter.clearScripts();

                for (final Identifier id : manager.listResources(
                        "scripts",
                        id -> id.getNamespace().equals(IS.MOD_ID)
                                && id.getPath().endsWith(".vyn")
                ).keySet()) {
                    try {
                        for (final Resource resource : manager.getResourceStack(id)) {
                            try (final InputStream stream = resource.open()) {
                                final String path = id.getPath();
                                final String fileName = path.substring(path.lastIndexOf('/') + 1);
                                final String fileVariable = fileName.substring(0, fileName.length() - ".vyn".length());
                                final String packId = resource.sourcePackId();

                                System.out.println(
                                        "Added .vyn script: " + fileVariable +
                                                " (path=" + path +
                                                ", pack=" + packId + ")"
                                );

                                ScriptInterpreter.addScript(packId, fileVariable,
                                        new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                            }
                        }
                    } catch (final Exception e) {
                        System.err.println("Failed to load script: " + id);
                        e.printStackTrace();
                    }
                }

                ScriptInterpreter.loadScripts();

            }

            @Override
            public Identifier getFabricId() {
                return Identifier.fromNamespaceAndPath(IS.MOD_ID, "script_loader");
            }

        });
    }
}