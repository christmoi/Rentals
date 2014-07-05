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
package fr.christmoi.rentals.commands.command;

import fr.christmoi.rentals.Messages;
import fr.christmoi.rentals.Region;
import fr.christmoi.rentals.commands.RCommand;
import fr.christmoi.rentals.commands.RCommandAnnotation;
import fr.christmoi.rentals.dependencies.WorldEdit;
import fr.christmoi.rentals.rent.Rent;
import fr.christmoi.rentals.rent.RentManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author Moi
 */
@RCommandAnnotation(
        command = "create",
        help = "/rentals create <price>",
        permission = "rentals.admin.create",
        argsLenght = 1
)
public class AdminCreateCommand implements RCommand {

    @Override
    public boolean execute(Player p, String[] args) {
        double price;
        try {
            price = Double.parseDouble(args[0]);
        } catch (NumberFormatException ex) {
            Messages.getInstance().sendMessage(p, "wrongNumber", args[0]);
            return false;
        }

        if (!WorldEdit.getInstance().isRegionDefined(p)) {
            Messages.getInstance().sendMessage(p, "undefWeReg");
            return true;
        }

        World w = p.getWorld();
        Vector vec1 = WorldEdit.getInstance().getPos1(p).toVector();
        Vector vec2 = WorldEdit.getInstance().getPos2(p).toVector();
        Region reg = new Region(w, vec1, vec2);
        if (RentManager.getInstance().exist(reg)) {
            Messages.getInstance().sendMessage(p, "areaAlreadyDefined");
            return true;
        }

        Rent r = new Rent(0, reg, price, null);
        if (!RentManager.getInstance().add(r)) {
            Messages.getInstance().sendMessage(p, "cmdError");
            return true;
        }

        Messages.getInstance().sendMessage(p, "rentCreate");
        WorldEdit.getInstance().purgeWorldEdit(p);
        return true;
    }
}
