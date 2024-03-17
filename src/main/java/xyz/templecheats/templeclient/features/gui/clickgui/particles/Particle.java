package xyz.templecheats.templeclient.features.gui.clickgui.particles;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class Particle {
    private final Vector2f pos;
    private static final Random random = new Random();
    private float size;
    private final Vector2f velocity;

    public Particle(Vector2f velocity, float x, float y, float size) {
        this.velocity = velocity;
        pos = new Vector2f(x, y);
        this.size = size;
    }

    public static Particle getParticle() {
        Vector2f velocity = new Vector2f((float)(Math.random() * 3.0 - 1.0), (float)(Math.random() * 3.0 - 1.0));
        float x = (float) random.nextInt(Display.getWidth());
        float y = (float) random.nextInt(Display.getHeight());
        float size = (float)(Math.random() * 4.0) + 2.0f;
        return new Particle(velocity, x, y, size);
    }

    public float getX() {
        return pos.getX();
    }

    public float getY() {
        return pos.getY();
    }

    public void setX(float f) {
        pos.setX(f);
    }

    public void setY(float f) {
        pos.setY(f);
    }

    public void setup(int delta, float speed) {
        Vector2f pos = this.pos;
        pos.x += velocity.getX() * delta * (speed / 2);

        Vector2f pos2 = this.pos;
        pos2.y += velocity.getY() * delta * (speed / 2);

        if (this.pos.getX() > Display.getWidth()) {
            this.pos.setX(0.0f);
        }
        if (this.pos.getX() < 0.0f) {
            this.pos.setX((float)Display.getWidth());
        }
        if (this.pos.getY() > Display.getHeight()) {
            this.pos.setY(0.0f);
        }
        if (this.pos.getY() < 0.0f) {
            this.pos.setY((float)Display.getHeight());
        }
    }

    public static class Util {

        private final List<Particle> particles;

        public Util(int in) {
            particles = new ArrayList<>();
            addParticle(in);
        }

        public void addParticle(int in) {
            for (int i = 0; i < in; ++i) particles.add(getParticle());
        }

        public static double getDistance(float x, float y, float x1, float y1) {
            return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        }

        public void drawParticles() {
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);

            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            for (Particle particle : particles) {
                particle.setup(2, 0.1f);
                glPointSize(particle.size);
                glBegin(GL_POINTS);
                glVertex2f(particle.getX(), particle.getY());
                glEnd();
            }

            glEnable(GL_TEXTURE_2D);
            glDepthMask(true);
            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);
            glPopMatrix();
        }
    }
}