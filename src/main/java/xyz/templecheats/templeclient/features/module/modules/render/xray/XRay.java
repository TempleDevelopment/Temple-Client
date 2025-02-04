package xyz.templecheats.templeclient.features.module.modules.render.xray;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.xray.sub.Ores;

public class XRay extends Module {
    public XRay() {
        super("XRay", "Allows you to filter what the world renders", 0, Category.Render);
        /****************************************************************
         *                      Sub Module Registration
         ****************************************************************/
        submodules.add(new Ores());
    }

    @Override
    public void onEnable() {
        mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onUpdate() {
    }

    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
    }
}