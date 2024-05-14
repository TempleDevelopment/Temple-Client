package xyz.templecheats.templeclient.features.module.modules.render.norender;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.norender.sub.Overlays;
import xyz.templecheats.templeclient.features.module.modules.render.norender.sub.Player;
import xyz.templecheats.templeclient.features.module.modules.render.norender.sub.UI;
import xyz.templecheats.templeclient.features.module.modules.render.norender.sub.World;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", "Prevents rendering of certain things", Category.Render, true);
        submodules.add(new Overlays());
        submodules.add(new Player());
        submodules.add(new UI());
        submodules.add(new World());
    }
}