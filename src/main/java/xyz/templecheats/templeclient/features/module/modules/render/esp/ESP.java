package xyz.templecheats.templeclient.features.module.modules.render.esp;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.esp.sub.*;

public class ESP extends Module {

    public ESP() {
        super("ESP", "Highlights entities in the world", Category.Render, true);
        /****************************************************************
         *                      Sub Module Registration
         ****************************************************************/
        submodules.add(new Block());
        submodules.add(new Break());
        submodules.add(new Hand());
        submodules.add(new Hole());
        submodules.add(new Jump());
        submodules.add(new LogoutSpots());
        submodules.add(new Entities());
        submodules.add(new Shader());
        submodules.add(new Spawn());
        submodules.add(new Storage());
        submodules.add(new Target());
    }
}
