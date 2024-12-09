package org.sculk.command.defaults;

import org.sculk.command.Command;
import org.sculk.command.CommandSender;
import org.sculk.command.args.RawStringArgument;
import org.sculk.permission.DefaultPermissionNames;

import java.util.List;
import java.util.Map;

public class SayCommand extends Command {
    public SayCommand() {
        super("say", "Broadcasts a message to all players", "/say <message>", List.of());
        this.registerArgument(0, new RawStringArgument("message", true));
    }

    @Override
    protected void prepare() {
        this.setPermission(DefaultPermissionNames.COMMAND_SAY);
    }

    @Override
    public void onRun(CommandSender sender, String commandLabel, Map<String, Object> args) {
        if (args.containsKey("message")) {
            String message = (String) args.get("message");
            sender.getServer().broadcastMessage("§3[§sServer§3] §f" + message);
        } else {
            sender.sendMessage("§cUsage: /say <message>");
        }
    }
}