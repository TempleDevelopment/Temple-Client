package xyz.templecheats.templeclient.features.module.modules.render.esp;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.esp.sub.*;

public class ESP extends Module {

    public ESP() {
        super("ESP", "Highlights entities in the world", Category.Render, true);
        submodules.add(new Block());
        submodules.add(new Hole());
        submodules.add(new Item());
        submodules.add(new Player());
        submodules.add(new Shader());
        submodules.add(new Spawner());
        submodules.add(new Storage());
        submodules.add(new Target());
    }
}
