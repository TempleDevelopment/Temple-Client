package xyz.templecheats.templeclient.features.module.modules.chat;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.Random;

public class FancyChat extends Module {
    private static final Random random = new Random();
    private static final Char2ObjectMap<String> MORSE_CODE_MAP = new Char2ObjectOpenHashMap<>();

    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Uwu);

    public FancyChat() {
        super("FancyChat", "Makes your text look gamer", 0, Category.Chat);

        registerSettings(mode);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        String message = event.getMessage();
        if (message.startsWith("/") || message.startsWith(".")) {
            return;
        }

        switch (mode.value()) {
            case Uwu:
                event.setMessage(translateToUwu(message));
                break;
            case MorseCode:
                event.setMessage(translateToMorse(message));
                break;
            case Reverse:
                event.setMessage(translateToReverse(message));
                break;
            case Byte:
                event.setMessage(translateToByte(message));
                break;
        }
    }

    public String translateToUwu(String message) {
        String uwuMessage = message
                .replace("ove", "uv")
                .replace("the", "da")
                .replace("is", "ish")
                .replace("r", "w")
                .replace("ve", "v")
                .replace("l", "w");

        if(random.nextBoolean()) {
            uwuMessage += " " + getRandomUwuExpression();
        }

        return uwuMessage;
    }

    private String getRandomUwuExpression() {
        String[] expressions = {"(*^.^*)", "owo", "uwu", ">w<", "^w^"};
        int index = random.nextInt(expressions.length);
        return expressions[index];
    }

    private String translateToMorse(String message) {
        StringBuilder morseMessage = new StringBuilder();
        for (char c : message.toLowerCase().toCharArray()) {
            String morseCode = MORSE_CODE_MAP.get(c);
            if (morseCode != null) {
                morseMessage.append(morseCode).append(" ");
            }
        }
        return morseMessage.toString();
    }
    private String translateToReverse(String message) {
        return new StringBuilder(message).reverse().toString();
    }

    private String translateToByte(String message) {
        StringBuilder byteMessage = new StringBuilder();
        for (char c : message.toCharArray()) {
            byteMessage.append(Integer.toBinaryString(c)).append(" ");
        }
        return byteMessage.toString();
    }

    static {
        MORSE_CODE_MAP.put('a', ".-");
        MORSE_CODE_MAP.put('b', "-...");
        MORSE_CODE_MAP.put('c', "-.-.");
        MORSE_CODE_MAP.put('d', "-..");
        MORSE_CODE_MAP.put('e', ".");
        MORSE_CODE_MAP.put('f', "..-.");
        MORSE_CODE_MAP.put('g', "--.");
        MORSE_CODE_MAP.put('h', "....");
        MORSE_CODE_MAP.put('i', "..");
        MORSE_CODE_MAP.put('j', ".---");
        MORSE_CODE_MAP.put('k', "-.-");
        MORSE_CODE_MAP.put('l', ".-..");
        MORSE_CODE_MAP.put('m', "--");
        MORSE_CODE_MAP.put('n', "-.");
        MORSE_CODE_MAP.put('o', "---");
        MORSE_CODE_MAP.put('p', ".--.");
        MORSE_CODE_MAP.put('q', "--.-");
        MORSE_CODE_MAP.put('r', ".-.");
        MORSE_CODE_MAP.put('s', "...");
        MORSE_CODE_MAP.put('t', "-");
        MORSE_CODE_MAP.put('u', "..-");
        MORSE_CODE_MAP.put('v', "...-");
        MORSE_CODE_MAP.put('w', ".--");
        MORSE_CODE_MAP.put('x', "-..-");
        MORSE_CODE_MAP.put('y', "-.--");
        MORSE_CODE_MAP.put('z', "--..");
        MORSE_CODE_MAP.put('0', "-----");
        MORSE_CODE_MAP.put('1', ".----");
        MORSE_CODE_MAP.put('2', "..---");
        MORSE_CODE_MAP.put('3', "...--");
        MORSE_CODE_MAP.put('4', "....-");
        MORSE_CODE_MAP.put('5', ".....");
        MORSE_CODE_MAP.put('6', "-....");
        MORSE_CODE_MAP.put('7', "--...");
        MORSE_CODE_MAP.put('8', "---..");
        MORSE_CODE_MAP.put('9', "----.");
    }

    private enum Mode {
        Uwu,
        MorseCode,
        Reverse,
        Byte
    }
}

