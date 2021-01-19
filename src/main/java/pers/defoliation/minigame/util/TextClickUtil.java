package pers.defoliation.minigame.util;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import pers.defoliation.minigame.MiniGame;
import pers.defoliation.minigame.listener.MiniGameEventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TextClickUtil {

    private final String commandPrefix;
    private final MiniGameEventHandler miniGameEventHandler = new MiniGameEventHandler(MiniGame.INSTANCE);
    private final Map<String, ClickCommand> idMap = new HashMap<>();

    public TextClickUtil(String commandPrefix) {
        this.commandPrefix = commandPrefix;

        miniGameEventHandler.addHandle(PlayerCommandPreprocessEvent.class, playerCommandPreprocessEvent -> {
            String message = playerCommandPreprocessEvent.getMessage().substring(1);
            String[] s = message.split(" ");
            if (s.length == 2 && commandPrefix.equals(s[0])) {
                if (idMap.containsKey(s[1])) {
                    idMap.get(s[1]).runnable.accept(playerCommandPreprocessEvent.getPlayer());
                    playerCommandPreprocessEvent.setCancelled(true);
                }
            }
        });
    }

    public void unload(){
        miniGameEventHandler.removeAll();
    }

    public ClickCommand setCommand(String id, Consumer<Player> consumer) {
        ClickCommand clickCommand = new ClickCommand(id, consumer);
        idMap.put(id, clickCommand);
        return clickCommand;
    }

    public class ClickCommand {

        private final String id;
        private final Consumer<Player> runnable;

        private ClickCommand(String id, Consumer<Player> runnable) {
            this.id = id;
            this.runnable = runnable;
        }

        public String getCommandText() {
            return "/" + commandPrefix + " " + id;
        }

        public void unload() {
            idMap.remove(id);
        }

    }

}
