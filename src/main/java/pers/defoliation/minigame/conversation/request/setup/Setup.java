package pers.defoliation.minigame.conversation.request.setup;

import pers.defoliation.minigame.conversation.Conversation;

import java.util.function.Consumer;

public interface Setup {

    void setOnQuit(Consumer<Conversation> quit);

}
