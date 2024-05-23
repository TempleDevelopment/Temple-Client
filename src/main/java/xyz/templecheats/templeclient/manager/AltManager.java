package xyz.templecheats.templeclient.manager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import xyz.templecheats.templeclient.TempleClient;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class AltManager {
    private List<String> alts = new ArrayList<>();
    private int selectedAltIndex = -1;

    public AltManager() {
        this.alts = TempleClient.configManager.loadAlts();
    }

    public List<String> getAlts() {
        return alts;
    }

    public int getSelectedAltIndex() {
        return selectedAltIndex;
    }

    public void setSelectedAltIndex(int index) {
        this.selectedAltIndex = index;
    }

    public void addAlt(String alt) {
        alts.add(alt);
        TempleClient.configManager.saveAlts(alts);
    }

    public void deleteAlt(int index) {
        if (index >= 0 && index < alts.size()) {
            alts.remove(index);
            TempleClient.configManager.saveAlts(alts);
            this.selectedAltIndex = -1;
        }
    }

    public void loginSelectedAlt() {
        if (selectedAltIndex >= 0) {
            String username = alts.get(selectedAltIndex);
            changeName(username);
        }
    }

    private void changeName(String name) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.logOut();
        Session session = new Session(name, name, "0", "legacy");
        try {
            Field sessionField = Minecraft.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            sessionField.set(Minecraft.getMinecraft(), session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
