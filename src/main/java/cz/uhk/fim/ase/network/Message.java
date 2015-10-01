package cz.uhk.fim.ase.network;

import java.io.Serializable;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class Message implements Serializable {

    final public static String DEFAULT_CONTENT = "Br0lqjuf9quMRnIp6QHyGqpN21pJRMDD4QJfu4MYr9nNeEWne5qikB9ltmi2"; // 60

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
