package hack.skate.client.features.impl.render;

import hack.skate.client.features.Category;
import hack.skate.client.features.FeatureInfo;

import java.util.ArrayList;
import java.util.Comparator;

import hack.skate.client.Skate;
import hack.skate.client.config.ColorConfig;
import hack.skate.client.event.Subscribe;
import hack.skate.client.event.impl.EventRender2D;
import hack.skate.client.features.Feature;
import hack.skate.client.features.settings.impl.BoolSetting;
import hack.skate.client.features.settings.impl.IntSetting;
import hack.skate.client.utils.RenderUtil;

@FeatureInfo(name = "HUD", description = "Stuffs on ur screen", category = Category.RENDER)
public class HUD extends Feature {
    private final BoolSetting watermark = new BoolSetting("Watermark", true);
    private final IntSetting watermarkX = new IntSetting("Watermark X", 5, 0, 500, o -> watermark.getValue());
    private final IntSetting watermarkY = new IntSetting("Watermark Y", 5, 0, 500, o -> watermark.getValue());
    private final BoolSetting arrayList = new BoolSetting("ArrayList", true);
    private final IntSetting arrayListOffset = new IntSetting("List Offset", 5, 0, 500, o -> arrayList.getValue());

    @Subscribe
    public void onRender2D(EventRender2D event) {
        if (mc.getDebugHud().shouldShowDebugHud()) return;

        if (watermark.getValue()) {
            Skate.fonts.getArial().renderWithShadow(
                event.getMatrixStack(),
                "S", 
                watermarkX.getValue(), 
                watermarkY.getValue(), 
                9,
                ColorConfig.PRIMARY_ACCENT.getRGB()
            );
            
            Skate.fonts.getArial().renderWithShadow(
                event.getMatrixStack(),
                "kate", 
                watermarkX.getValue() + mc.textRenderer.getWidth("S"), 
                watermarkY.getValue(), 
                9,
                ColorConfig.TEXT_WHITE.getRGB()
            );
        }

        if (arrayList.getValue()) {
            List<Feature> enabledFeatures = new ArrayList<>();
        for (Feature feature : Skate.featureManager.getFeatures()) {
            if (feature.isEnabled() && feature != this) {
                enabledFeatures.add(feature);
            }
        }

        enabledFeatures.sort(Comparator.comparingDouble(f -> 
            -Skate.fonts.getArial().getWidth(getDisplayText(f), 8)));

        int screenWidth = mc.getWindow().getScaledWidth();
        int y = 2;

        for (Feature feature : enabledFeatures) {
            String displayText = getDisplayText(feature);
            float textWidth = Skate.fonts.getArial().getWidth(displayText, 8);
            int x = (int) (screenWidth - textWidth - 2);

            Skate.fonts.getArial().renderWithShadow(
                event.getMatrixStack(),
                displayText,
                x,
                y,
                8,
                ColorConfig.PRIMARY_ACCENT.getRGB()
            );

            y += 10;
        }
        }
    }

    private String getDisplayText(Feature feature) {
        String info = feature.getInfo();
        if (info != null && !info.isEmpty()) {
            return feature.getName() + " \u00A77" + info;
        }
        return feature.getName();
    }
}
