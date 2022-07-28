package tn.amin.myhomeui.storage.communicator;

import androidx.annotation.NonNull;

import java.util.Objects;

public class CommunicatedMessage {
    public final MessageType type;
    public final String content;

    public CommunicatedMessage(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "CommunicatedMessage{" +
                "type=" + type +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunicatedMessage that = (CommunicatedMessage) o;
        return type == that.type && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, content);
    }
}
