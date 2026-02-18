package omar.projects.interactivestuff.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import omar.projects.interactivestuff.scripts.ScriptInterpreter;
public class ActiveResourcePacksScreen extends Screen {

    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 61, 33);

    private static final Text TITLE = Text.translatable("interactive_stuff.scripted_packs");

    private final Screen parent;

    public ActiveResourcePacksScreen(final Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        DirectionalLayoutWidget header = this.layout.addHeader(
                DirectionalLayoutWidget.vertical().spacing(8)
        );

        header.add(
                new TextWidget(TITLE, this.textRenderer),
                Positioner::alignHorizontalCenter
        );

        final GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner()
                .marginX(4)
                .marginBottom(4)
                .alignHorizontalCenter();

        final GridWidget.Adder adder = gridWidget.createAdder(2);

        for (final String packId : ScriptInterpreter.getPackScripts().keySet()) {
            adder.add(
                    ButtonWidget.builder(Text.of(packId), button -> System.out.println(ScriptInterpreter.getPackScripts().get(packId).toString())
                    ).build()
            );

        }

        this.layout.addBody(gridWidget);

        this.layout.addFooter(
                ButtonWidget.builder(ScreenTexts.DONE, button -> this.close())
                        .width(200)
                        .build()
        );

        this.layout.forEachChild(this::addDrawableChild);

        this.layout.refreshPositions();
    }


    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        this.layout.refreshPositions();
    }


    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

}