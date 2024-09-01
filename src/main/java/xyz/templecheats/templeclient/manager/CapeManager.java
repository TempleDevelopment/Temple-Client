package xyz.templecheats.templeclient.manager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CapeManager {

    /****************************************************************
     *                      Constants
     ****************************************************************/

    private static final String CAPE_UUIDS = "https://raw.githubusercontent.com/TempleDevelopment/Temple-Client-Assets/main/assets/uuids/cape-uuids.txt";
    private static final String CAPE_DIR = "https://raw.githubusercontent.com/TempleDevelopment/Temple-Client-Assets/main/assets/images/capes/%s.png";

    /****************************************************************
     *                      Fields
     ****************************************************************/

    private final Map<UUID, String> capeUsers;
    private final Map<String, ResourceLocation> capeTextures;

    /****************************************************************
     *                      Constructor
     ****************************************************************/

    public CapeManager() {
        this.capeUsers = new HashMap<>();
        this.capeTextures = new HashMap<>();

        CompletableFuture.runAsync(this::loadCapeData);
    }

    /****************************************************************
     *                      Cape Data Loading
     ****************************************************************/

    private void loadCapeData() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(CAPE_UUIDS).openStream(), StandardCharsets.UTF_8))) {
            for (String line; (line = reader.readLine()) != null; ) {
                String[] split = line.split(":");
                this.addCape(UUID.fromString(split[0]), split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /****************************************************************
     *                      Cape Management
     ****************************************************************/

    public void addCape(UUID uuid, String capeName) {
        this.capeUsers.put(uuid, capeName);

        if (!this.capeTextures.containsKey(capeName)) {
            Minecraft.getMinecraft().addScheduledTask(() -> loadCapeTexture(capeName));
        }
    }

    private void loadCapeTexture(String capeName) {
        try {
            URL url = new URL(String.format(CAPE_DIR, capeName));
            InputStream stream = url.openStream();
            BufferedImage image = ImageIO.read(stream);
            DynamicTexture texture = new DynamicTexture(image);
            this.capeTextures.put(capeName, Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(capeName, texture));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************************************************
     *                      Cape Retrieval
     ****************************************************************/

    public ResourceLocation getCapeByUuid(UUID uuid) {
        String capeName = this.capeUsers.get(uuid);
        return capeName != null ? this.capeTextures.get(capeName) : null;
    }
}
