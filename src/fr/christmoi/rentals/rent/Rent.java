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
package fr.christmoi.rentals.rent;

import fr.christmoi.rentals.Messages;
import fr.christmoi.rentals.Region;
import fr.christmoi.rentals.SQL;
import fr.christmoi.rentals.dependencies.Vault;
import fr.christmoi.rentals.sign.SignManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 *
 * @author Moi
 */
public class Rent {

    private int id;
    private final Region region;
    private final double price;
    private String owner;

    public Rent(int id, Region region, double price, String owner) {
        this.id = id;
        this.region = region;
        this.price = price;
        this.owner = owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Region getRegion() {
        return region;
    }

    public double getPrice() {
        return price;
    }

    public String getOwner() {
        return owner;
    }

    public boolean hasOwner() {
        return (this.getOwner() != null);
    }

    public boolean sell(Player p) {
        try {
            SQL.getInstance().getConnnection().createStatement().executeUpdate(
                    "UPDATE rentals_rent SET owner = null WHERE id = " + this.id);
            Vault.deposit(p, this.price);
            this.owner = null;
            SignManager.getInstance().update(this.id);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Rent.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean buy(Player p) {
        try {
            if (!Vault.hasEnough(p, price)) {
                Messages.getInstance().sendMessage(p, "notEnough");
            } else {
                SQL.getInstance().getConnnection().createStatement().executeUpdate(
                        "UPDATE rentals_rent SET owner = '" + p.getName() + "' WHERE id = " + this.id);
                Vault.withdraw(p, this.price);
                this.owner = p.getName();
                SignManager.getInstance().update(this.id);
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Rent.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
