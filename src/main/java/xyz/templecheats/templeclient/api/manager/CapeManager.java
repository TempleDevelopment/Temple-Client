package xyz.templecheats.templeclient.api.manager;

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
    private final Map<UUID, String> capeUsers;
    private final Map<String, ResourceLocation> capeTextures;
    private static final String CAPE_UUIDS = "https://raw.githubusercontent.com/PhilipPanda/Temple-Client/main/github/assets/capes.txt";
    private static final String CAPE_DIR = "https://raw.githubusercontent.com/PhilipPanda/Temple-Client/main/github/assets/%s.png";

    public CapeManager() {
        this.capeUsers = new HashMap<>();
        this.capeTextures = new HashMap<>();

        CompletableFuture.runAsync(() -> {
            try(final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(CAPE_UUIDS).openStream(), StandardCharsets.UTF_8))) {
                for(String line; (line = reader.readLine()) != null; ) {
                    final String[] split = line.split(":");
                    this.addCape(UUID.fromString(split[0]), split[1]);
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addCape(UUID uuid, String capeName) {
        this.capeUsers.put(uuid, capeName);

        if(!this.capeTextures.containsKey(capeName)) {
            try {
                final URL url = new URL(String.format(CAPE_DIR, capeName));
                final InputStream stream = url.openStream();
                final BufferedImage image = ImageIO.read(stream);
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    final DynamicTexture texture = new DynamicTexture(image);
                    this.capeTextures.put(capeName, Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(capeName, texture));
                });
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ResourceLocation getCapeByUuid(UUID uuid) {
        final String capeName = this.capeUsers.get(uuid);
        return capeName != null ? this.capeTextures.get(capeName) : null;
    }
}