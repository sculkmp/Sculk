package org.sculk.command.defaults;

import org.sculk.Server;
import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.permission.DefaultPermissionNames;
import org.sculk.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ListCommand extends Command {
    public ListCommand() {
        super("list", "Gives the list of players", "/list", new ArrayList<>());
    }

    @Override
    protected void prepare() {
        this.setPermission(DefaultPermissionNames.COMMAND_LIST);
    }

    public void onRun(CommandSender sender, String commandLabel, Map<String, Object> args) {
        List<String> playerNames = Server.getInstance().getOnlinePlayers().values().stream()
                .filter(Objects::nonNull)
                .map(Player::getName).sorted().collect(Collectors.toList());

        String message = new StringBuilder()
                .append("§6-------------- §fOnline Players §6--------------\n")
                .append("§fTotal: §a").append(playerNames.size()).append("\n")
                .append("§fPlayers: §a").append(String.join(", ", playerNames)).append("\n")
                .append("§6---------------------------------------------")
                .toString();

        sender.sendMessage(message);
    }
}