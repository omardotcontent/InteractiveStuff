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

    public static void register() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return Identifier.of(IS.MOD_ID, "script_loader");
            }

            @Override
            public void reload(final ResourceManager manager) {
                ScriptInterpreter.clearScripts();

                // 1. Find the resources based on the predicate
                Map<Identifier, Resource> foundResources = manager.findResources(
                        "scripts",
                        id -> id.getNamespace().equals(IS.MOD_ID)
                                && id.getPath().endsWith(".vyn")
                );

                // 2. Iterate through the found IDs
                for (Identifier id : foundResources.keySet()) {
                    try {
                        // 3. Get all resources for this ID (handles resource pack stacking)
                        for (final Resource resource : manager.getAllResources(id)) {

                            try (final InputStream stream = resource.getInputStream()) {

                                // Full path: scripts/utils/math.vyn
                                final String path = id.getPath();

                                // Extract filename: math.vyn
                                final String fileName = path.substring(path.lastIndexOf('/') + 1);

                                // File variable: math
                                final String fileVariable = fileName.substring(
                                        0, fileName.length() - ".vyn".length()
                                );

                                final String packId = resource.getPackId();

                                // Output / register
                                System.out.println(
                                        "Added .vyn script: " + fileVariable +
                                                " (path=" + path +
                                                ", pack=" + packId + ")"
                                );

                                ScriptInterpreter.addScript(
                                        packId,
                                        fileVariable,
                                        new String(
                                                stream.readAllBytes(),
                                                StandardCharsets.UTF_8
                                        )
                                );
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to load script: " + id);
                        e.printStackTrace();
                    }
                }

                ScriptInterpreter.loadScripts();
            }
        });
    }
}