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
package fr.christmoi.rentals.dependencies;

import fr.christmoi.rentals.Messages;
import fr.christmoi.rentals.Rentals;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Moi
 */
public class Vault {

    private final Economy econ;

    private Vault() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
    }

    private static Economy getEconomy() {
        return VaultHolder.INSTANCE.econ;
    }

    private static class VaultHolder {

        private static final Vault INSTANCE = new Vault();
    }
    
    public static boolean hasEnough(Player player, double amount){
        return getEconomy().bankHas(player.getName(), amount).transactionSuccess();
    }

    public static void deposit(Player p, double amount) {
        getEconomy().bankDeposit(p.getName(), amount);
        getEconomy().bankWithdraw(Rentals.getRConfig().getString("bankAccount"), amount);
        Messages.getInstance().sendMessage(p, "deposit", amount);
    }

    public static void withdraw(Player p, double amount) {
        getEconomy().bankWithdraw(p.getName(), amount);
        getEconomy().bankDeposit(Rentals.getRConfig().getString("bankAccount"), amount);
        Messages.getInstance().sendMessage(p, "withdraw", amount);
    }

}
