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
package fr.christmoi.rentals;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Moi
 */
public final class Messages extends YamlConfiguration {

    private static final String fileName = "Messages.yml";

    private Messages() {
        this.set();
        File messageFile = new File(Rentals.getPlugin().getDataFolder() + File.separator + fileName);
        if (!messageFile.exists()) {
            try {
                super.save(messageFile);
            } catch (IOException ex) {
                Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            super.load(messageFile);
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(Messages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Messages getInstance() {
        return MessagesHolder.INSTANCE;
    }

    private static class MessagesHolder {

        private static final Messages INSTANCE = new Messages();
    }

    private void set() {
        //Global
        this.set("prefix", "&1[Rentals]");
        this.set("notPerm", "&cVous n'avez pas la Permission !!!");
        this.set("cmdError", "&cUne Erreur c'est produit lors de l'execution de la commande !!!");

        //CreateCommand
        this.set("undefWeReg", "&cVous n'avez pas définit de region WorldEdit !!!");
        this.set("areaAlreadyDefined", "&cCette parcelle est déjà utilisée !!!");
        this.set("wrongNumber", "&c{0} n'est pas un nombre valide !!!");
        this.set("rentCreate", "&aVotre parcelle a été crée.");
        this.set("notingToConfirm", "&cVous n'avez aucune action en attente de comfirmation !!!");

        //Sign 
        this.set("notInRegion", "&cLe panneau doit etre sur une parcelle rentals !!!");
        this.set("signCreateError", "&cUne Erreur c'est produit lors de la creation du panneau !!!");
        this.set("signCreate", "&aVotre panneau a été crée.");
        this.set("errorSignDestroy", "&cUne Erreur c'est produit lors de la destruction du panneau !!!");
        this.set("notPermBuy", "&cVous n'avez pas la permission de vendre/acheter !!!");
        this.set("confirmSale", "&aEntrez /rentals confirm pour vendre votre parcelle.");
        this.set("confirmBuy", "&aEntrez /rentals confirm pour acheter cette parcelle.");

        //Dep
        this.set("deposit", "&a{0}$ ont été déposé sur votre compte.");
        this.set("withdraw", "&a{0}$ ont été débité sur votre compte.");
        this.set("notEnough", "&cVous n'avez pas assez d'argent !!!");
    }

    public String getMessage(String msg, Object... args) {
        String str = this.getString("prefix") + this.getString(msg);
        str = str.replace("&", "\u00a7");
        return MessageFormat.format(str, args);
    }

    public void sendMessage(Player p, String msg, Object... args) {
        p.sendMessage(getMessage(msg, args));
    }

    public void sendMessage(List<Player> players, String msg, Object... args) {
        players.stream().forEach((p) -> {
            sendMessage(p, msg, args);
        });
    }
}
