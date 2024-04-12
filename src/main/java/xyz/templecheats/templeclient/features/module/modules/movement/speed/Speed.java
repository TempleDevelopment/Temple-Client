package xyz.templecheats.templeclient.features.module.modules.movement.speed;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.sub.Bhop;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.sub.Strafe;

public class Speed extends Module {

    public Speed() {
        super("Speed", "Speeds up the player", Category.Movement, true);
        submodules.add(new Strafe());
        submodules.add(new Bhop());
    }
}
