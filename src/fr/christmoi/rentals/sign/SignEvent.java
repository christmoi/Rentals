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
package fr.christmoi.rentals.sign;

import fr.christmoi.rentals.Messages;
import fr.christmoi.rentals.Rentals;
import fr.christmoi.rentals.rent.Rent;
import fr.christmoi.rentals.rent.RentManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Moi
 */
public class SignEvent implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void signCreate(SignChangeEvent e) {
        Player player = e.getPlayer();
        Sign sign = (Sign) e.getBlock().getState().getData();

        if (!e.getLines()[0].equalsIgnoreCase("[rentals]")) {
            return;
        }

        if (!player.hasPermission("rentals.admin.create")) {
            e.setCancelled(true);
            Messages.getInstance().sendMessage(player, "notPerm");
            return;
        }

        Block b = e.getBlock().getRelative(sign.getAttachedFace());

        if (!b.hasMetadata("rentals")) {
            Messages.getInstance().sendMessage(player, "notInRegion");
            e.setCancelled(true);
            return;
        }
        int rentId = b.getMetadata("rentals").get(0).asInt();
        Rent r = RentManager.getInstance().getRent(rentId);

        if (r.hasOwner()) {
            e.setLine(0, "");
            e.setLine(1, "Maison de");
            e.setLine(2, r.getOwner());
            e.setLine(3, "");
        } else {
            e.setLine(0, ChatColor.RED + "A vendre");
            e.setLine(1, "");
            e.setLine(2, "Prix");
            e.setLine(3, ChatColor.GREEN + "" + r.getPrice() + " $");
        }

        if (!SignManager.getInstance().add(rentId, e.getBlock())) {
            Messages.getInstance().sendMessage(player, "signCreateError");
            e.setCancelled(true);
            return;
        }
        Messages.getInstance().sendMessage(player, "signCreate");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void signDestroy(BlockBreakEvent e) {
        if (!e.getBlock().hasMetadata("rentals_sign")) {
            return;
        }

        if (!e.getPlayer().hasPermission("rentals.admin.create")) {
            e.setCancelled(true);
            return;
        }
        if (!e.getPlayer().isSneaking()) {
            e.setCancelled(true);
            return;
        }

        if (!SignManager.getInstance().remove(e.getBlock())) {
            Messages.getInstance().sendMessage(e.getPlayer(), "errorSignDestroy");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void signDestroy(BlockPhysicsEvent e) {
        Block b = e.getBlock();
        if (!b.hasMetadata("rentals_sign")) return;

        Block ab = b.getRelative(((Sign) b.getState().getData()).getAttachedFace());
        if (ab.getType() == Material.AIR) {
            ab.setType(e.getChangedType());
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void clickOnSign(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!e.hasBlock()) return;
        if (!e.getClickedBlock().hasMetadata("rentals_sign")) return;
        e.setCancelled(true);

        Player player = e.getPlayer();
        if (!player.hasPermission("rentals.user.buy")) {
            Messages.getInstance().sendMessage(player, "notPermBuy");
            return;
        }

        int rentId = e.getClickedBlock().getMetadata("rentals_sign").get(0).asInt();
        Rent rent = RentManager.getInstance().getRent(rentId);

        if (rent.hasOwner()) {
            if (!rent.getOwner().equals(player.getName())) return;
            if (player.hasMetadata("confirmSale")) {
                player.removeMetadata("confirmSale", Rentals.getPlugin());
            }
            player.setMetadata("confirmSale", new FixedMetadataValue(Rentals.getPlugin(), rentId));
            Messages.getInstance().sendMessage(player, "confirmSale");
        } else {
            if (player.hasMetadata("confirmBuy")) {
                player.removeMetadata("confirmBuy", Rentals.getPlugin());
            }
            player.setMetadata("confirmBuy", new FixedMetadataValue(Rentals.getPlugin(), rentId));
            Messages.getInstance().sendMessage(player, "confirmBuy");
        }
    }
}
