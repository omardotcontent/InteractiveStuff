package omar.projects.interactivestuff.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
public class InteractiveStuffConfigScreen extends Screen {

    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 61, 33);

    private static final Text TITLE = Text.translatable("interactive_stuff.config");

    private static final Text SCRIPTED_PACKS_TITLE = Text.translatable("interactive_stuff.config.scripted_packs");
    private static final Text SCRIPTED_PACKS_TITLE_TOOLTIP = Text.translatable("interactive_stuff.config.scripted_packs.tooltip");


    private static final Text CONFIG_TITLE = Text.translatable("interactive_stuff.config.config");
    private static final Text CONFIG_TITLE_TOOLTIP = Text.translatable("interactive_stuff.config.config.tooltip");

    private static final Text INTERACTIVE_HITS = Text.translatable("interactive_stuff.config.interactivehits");
    private static final Text INTERACTIVE_HITS_TOOLTIP = Text.translatable("interactive_stuff.config.interactivehits.tooltip");

    private final Screen parent;

    public InteractiveStuffConfigScreen(final Screen parent) {
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

        adder.add(
                ButtonWidget.builder(CONFIG_TITLE, button -> System.out.println("Open config screen")
                        ).tooltip(Tooltip.of(CONFIG_TITLE_TOOLTIP))
                        .build()
        );

        adder.add(
                ButtonWidget.builder(INTERACTIVE_HITS, button -> System.out.println("Open Interactive Hits Settings screen")
                        ).tooltip(Tooltip.of(INTERACTIVE_HITS_TOOLTIP))
                        .build()
        );

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