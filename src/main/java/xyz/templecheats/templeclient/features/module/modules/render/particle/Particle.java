package xyz.templecheats.templeclient.features.module.modules.render.particle;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.particle.sub.FireFlies;
import xyz.templecheats.templeclient.features.module.modules.render.particle.sub.HitParticle;

public class Particle extends Module {
    public Particle() {
        super("Particle", "Renders particles around you (might reduce performance)", Category.Render, true);
        submodules.add(new FireFlies());
        submodules.add(new HitParticle());
    }
}
