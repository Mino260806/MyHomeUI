package tn.amin.myhomeui.storage.communicator;

import android.os.FileObserver;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.util.FileUtil;
import tn.amin.myhomeui.util.LogUtil;

public class Communicator {
    private static final File dir = new File(StorageManager.getRootDir(), ".communication_channel");

    static {
        dir.mkdirs();
    }

    // When a message is sent, it's ignored when received as an event
    private static ArrayList<CommunicatedMessage> ignoredMessages = null;

    static private FileObserver fileObserver;
    public static void startListening(OnMessageListener listener){
        ignoredMessages = new ArrayList<>();
        fileObserver = new FileObserver(dir.getAbsolutePath(), FileObserver.CREATE) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                if (path == null) return;
                try {
                    CommunicatedMessage message = decodeMessage(FileUtil.getBaseName(new File(path)));
                    if (ignoredMessages.contains(message)) {
                        ignoredMessages.remove(message);
                        return;
                    }
                    listener.onMessage(message);
                } catch (IllegalArgumentException e) {
                }
                new File(path).getAbsoluteFile().delete();
            }
        };
        fileObserver.startWatching();
    }

    public interface OnMessageListener {
        void onMessage(CommunicatedMessage message);
    }

    public static void sendMessage(MessageType message) {
        sendMessage(message, "");
    }



    public static void sendMessage(MessageType message, int content) {
        sendMessage(message, String.valueOf(content));
    }

    public static void sendMessage(MessageType message, String content) {
        try {
            String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
            encodedContent = (encodedContent.isEmpty()? "": "-") + encodedContent;
            File file = new File(dir, message.toString() + encodedContent);
            if (file.exists()) file.delete();
            if (ignoredMessages != null) ignoredMessages.add(new CommunicatedMessage(message, content));
            file.createNewFile();
        } catch (IOException e) {
            LogUtil.debug("Failed to create file in communication dir");
        }
    }

    @Nullable
    public static CommunicatedMessage getMessage(MessageType message) {
        File[] files = dir.listFiles();
        if (files == null) return null;
        for (File f: files) {
            String fileName = FileUtil.getBaseName(f);
            return decodeMessage(fileName);
        }
        return null;
    }

    private static CommunicatedMessage decodeMessage(String encodedMessage) {
        String[] split = encodedMessage.split("-");
        if (split.length > 1) split[1] = new String(Base64.getDecoder().decode(split[1].getBytes()));
        try {
            return new CommunicatedMessage(MessageType.valueOf(split[0]), split.length > 1? split[1]: "");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isModuleEnabled() {
        int errorMargin = 10000; // 10s

        CommunicatedMessage bootTimeMessage = getMessage(MessageType.BOOT);
        if (bootTimeMessage == null) return false;
        String bootTimeString = bootTimeMessage.content;
        if (!bootTimeString.isEmpty()) {
            try {
                long bootTime = Long.parseLong(bootTimeString);
                long actualBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
                if (bootTime > (actualBootTime - errorMargin)) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
    }

    public static void validateModuleIsEnabled() {
        LogUtil.debug("Current time: " + System.currentTimeMillis());
        LogUtil.debug("Elapsed time: " + SystemClock.elapsedRealtime());
        String bootTime = Long.toString(System.currentTimeMillis() - SystemClock.elapsedRealtime());
        FileUtil.clearDirectory(dir);
        sendMessage(MessageType.BOOT, bootTime);
        LogUtil.debug("My Home UI BOOT-" + bootTime);
    }
}
