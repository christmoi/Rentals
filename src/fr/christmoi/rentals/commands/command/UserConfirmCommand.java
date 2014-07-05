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
import fr.christmoi.rentals.Rentals;
import fr.christmoi.rentals.commands.RCommand;
import fr.christmoi.rentals.commands.RCommandAnnotation;
import fr.christmoi.rentals.rent.Rent;
import fr.christmoi.rentals.rent.RentManager;
import org.bukkit.entity.Player;

/**
 *
 * @author Moi
 */
@RCommandAnnotation(
        command = "confirm",
        help = "/rentals confim",
        permission = "rentals.user.buy",
        argsLenght = 0
)
public class UserConfirmCommand implements RCommand {

    @Override
    public boolean execute(Player p, String[] args) {
        if (p.hasMetadata("confirmSale")) {
            int rentId = p.getMetadata("confirmSale").get(0).asInt();
            Rent r = RentManager.getInstance().getRent(rentId);
            if (!r.sell(p)) {
                Messages.getInstance().sendMessage(p, "cmdError");
            }
            p.removeMetadata("confirmSale", Rentals.getPlugin());
        } else if (p.hasMetadata("confirmBuy")) {
            int rentId = p.getMetadata("confirmBuy").get(0).asInt();
            Rent r = RentManager.getInstance().getRent(rentId);
            if (!r.buy(p)) Messages.getInstance().sendMessage(p, "cmdError");
            p.removeMetadata("confirmBuy", Rentals.getPlugin());
        } else {
            Messages.getInstance().sendMessage(p, "notingToConfirm");
        }
        return true;
    }

}
