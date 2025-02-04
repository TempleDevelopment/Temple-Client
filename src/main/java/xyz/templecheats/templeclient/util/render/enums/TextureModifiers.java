package xyz.templecheats.templeclient.util.render.enums;

import net.minecraft.util.ResourceLocation;

public enum TextureModifiers {
    Normal(new ResourceLocation("textures/particles/circle.png")),
    Text(new ResourceLocation("textures/particles/text.png")),
    Heart(new ResourceLocation("textures/particles/heart.png")),
    Dollar(new ResourceLocation("textures/particles/dollar.png"));

    private final ResourceLocation resourceLocation;

    TextureModifiers(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation() {
        return this.resourceLocation;
    }
}
