package xyz.templecheats.templeclient.util.color;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.util.Globals;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil implements Globals {
    private final int programID;

    public ShaderUtil(String shader) {
        int program = glCreateProgram();
        try {
            int fragmentShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation("textures/shaders/" + shader + ".frag")).getInputStream(), GL_FRAGMENT_SHADER);
            glAttachShader(program, fragmentShaderID);

            int vertexShaderID = createShader(mc.getResourceManager().getResource(new ResourceLocation("textures/shaders/vertex.vsh")).getInputStream(), GL_VERTEX_SHADER);
            glAttachShader(program, vertexShaderID);
        } catch (Exception ignored) {
        }

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }
        this.programID = program;
    }

    public void attachShader() {
        glUseProgram(programID);
    }

    public void releaseShader() {
        glUseProgram(0);
    }

    public void colorUniform(String name, Color color) {
        setUniformf(name, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1:
                glUniform1f(loc, args[0]);
                break;
            case 2:
                glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) {
            glUniform2i(loc, args[0], args[1]);
        } else {
            glUniform1i(loc, args[0]);
        }
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readInputStream(inputStream));
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }
        return shader;
    }


    public String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public void render(float x, float y, float width, float height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(x, (y + height));
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f((x + width), (y + height));
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f((x + width), y);
        GL11.glEnd();
    }
}