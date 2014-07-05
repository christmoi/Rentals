/* 
 * Copyright (C) 2014 Moi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.christmoi.rentals.commands;

import fr.christmoi.rentals.Messages;
import fr.christmoi.rentals.commands.command.AdminCreateCommand;
import fr.christmoi.rentals.commands.command.UserConfirmCommand;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Moi
 */
public final class RCommandsManager implements CommandExecutor {

    private final HashMap<String, Class<? extends RCommand>> commandMap;

    public RCommandsManager() {
        this.commandMap = new HashMap<>();
        register();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String cmd, String[] args) {
        if (!(cs instanceof Player)) return true;
        Player p = (Player) cs;
        if (args.length < 1) return help(p);

        Class<? extends RCommand> commandClass = this.commandMap.get(args[0]);
        if (commandClass == null) return help(p);
        RCommandAnnotation anno = commandClass.getAnnotation(RCommandAnnotation.class);
        if (!p.hasPermission(anno.permission())) {
            Messages.getInstance().sendMessage(p, "notPerm");
            return true;
        }
        
        args = Arrays.copyOfRange(args, 1, args.length);
        if (args.length != anno.argsLenght()) {
            p.sendMessage(anno.help());
            return true;
        }
        try {
            if (!commandClass.newInstance().execute(p, args)) {
                p.sendMessage(anno.help());
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(RCommandsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private boolean help(Player player) {
        commandMap.values().stream().map((cmd) -> cmd.getAnnotation(RCommandAnnotation.class))
                .filter((anno) -> (player.hasPermission(anno.permission()))).forEach((anno) -> {
            player.sendMessage(anno.help());
        });
        return true;
    }

    private void register() {
        addCommand(AdminCreateCommand.class);
        addCommand(UserConfirmCommand.class);
    }

    private void addCommand(Class<? extends RCommand> command) {
        RCommandAnnotation annotation = command.getAnnotation(RCommandAnnotation.class);
        this.commandMap.put(annotation.command(), command);
    }
}