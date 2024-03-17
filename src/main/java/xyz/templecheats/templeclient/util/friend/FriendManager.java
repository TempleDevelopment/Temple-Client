package xyz.templecheats.templeclient.util.friend;

import java.util.ArrayList;
import java.util.List;

public class FriendManager {
    private List<Friend> friends;

    public FriendManager() {
        this.friends = new ArrayList<>();
    }

    public void addFriend(String name) {
        this.friends.add(new Friend(name));
    }

    public void removeFriend(String name) {
        this.friends.removeIf(friend -> friend.getName().equalsIgnoreCase(name));
    }

    public boolean isFriend(String name) {
        return this.friends.stream().anyMatch(friend -> friend.getName().equalsIgnoreCase(name));
    }

    public List<Friend> getFriends() {
        return this.friends;
    }
}