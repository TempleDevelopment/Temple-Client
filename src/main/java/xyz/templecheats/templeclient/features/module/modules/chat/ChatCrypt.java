package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.module.Module;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
public class ChatCrypt extends Module {
    private static final String PASSWORD = "temple";
    private static final byte[] SALT = new byte[16];
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 128;

    private static String chatKey;

    static {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(PASSWORD.toCharArray(), SALT, ITERATION_COUNT, KEY_LENGTH);
            byte[] key = factory.generateSecret(spec).getEncoded();
            chatKey = Base64.getEncoder().encodeToString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ChatCrypt() {
        super("ChatCrypt", "Encrypt and Decrypt chat messages that users with Decrypt online can see", 0, Module.Category.Chat);
    }

    public static String encrypt(String value) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(chatKey.toCharArray(), chatKey.getBytes(), 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(chatKey.toCharArray(), chatKey.getBytes(), 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey key = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        String s = event.getMessage();
        s = encrypt(s);
        if (s.length() > 256) {
            System.out.println("Encrypted message is too long.");
            event.setCanceled(true);
            return;
        }
        event.setMessage("[TempleClient] " + s);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String fullMessage = event.getMessage().getUnformattedText();
        int nameEndIndex = fullMessage.indexOf('>');
        if (nameEndIndex == -1) {
            return;
        }

        String playerName = fullMessage.substring(0, nameEndIndex);
        String encryptedMessage = fullMessage.substring(nameEndIndex + 1).trim();

        if (!encryptedMessage.startsWith("[TempleClient] ")) {
            return;
        }

        encryptedMessage = encryptedMessage.substring("[TempleClient] ".length()).trim();

        String decryptedMessage = decrypt(encryptedMessage);

        if (decryptedMessage != null) {
            event.setMessage(new TextComponentString(playerName + "> [TempleClient] " + decryptedMessage));
        }
    }
}