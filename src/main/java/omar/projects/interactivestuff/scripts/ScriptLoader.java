package omar.projects.interactivestuff.scripts;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import omar.projects.interactivestuff.IS;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ScriptLoader {

    private ScriptLoader() {
    }

    public static void register() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return Identifier.of(IS.MOD_ID, "script_loader");
            }

            @Override
            public void reload(final ResourceManager manager) {
                ScriptInterpreter.clearScripts();

                for (final Identifier id : manager.findResources(
                        "scripts",
                        id -> id.getNamespace().equals(IS.MOD_ID)
                                && id.getPath().endsWith(".vyn")
                ).keySet()) {
                    try {
                        for (final Resource resource : manager.getAllResources(id)) {
                            try (final InputStream stream = resource.getInputStream()) {
                                final String path = id.getPath();
                                final String fileName = path.substring(path.lastIndexOf('/') + 1);
                                final String fileVariable = fileName.substring(0, fileName.length() - ".vyn".length());
                                final String packId = resource.getPackId();

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
        });
    }
}