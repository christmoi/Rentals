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

import fr.christmoi.rentals.Rentals;
import static fr.christmoi.rentals.Rentals.getPlugin;
import fr.christmoi.rentals.SQL;
import fr.christmoi.rentals.rent.Rent;
import fr.christmoi.rentals.rent.RentManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getWorld;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Moi
 */
public class SignManager {

    private final HashMap<Integer, HashSet<Block>> signs;

    private SignManager() {
        signs = new HashMap<>();
        load();
    }

    public static SignManager getInstance() {
        return SignManagerHolder.INSTANCE;
    }

    boolean add(int rentId, Block block) {
        try {
            PreparedStatement ps = SQL.getInstance().getConnnection().prepareStatement(
                    "INSERT INTO rentals_sign (x, y, z, world, rentId)"
                    + "VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, block.getX());
            ps.setInt(2, block.getY());
            ps.setInt(3, block.getZ());
            ps.setString(4, block.getLocation().getWorld().getName());
            ps.setInt(5, rentId);
            ps.executeUpdate();
            if (!signs.containsKey(rentId)) {
                signs.put(rentId, new HashSet<>());
            }
            signs.get(rentId).add(block);
            block.setMetadata("rentals_sign", new FixedMetadataValue(getPlugin(), rentId));
            return true;
        } catch (SQLException ex) {
            getLogger(RentManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void purge() {
        this.signs.values().stream().forEach((signss) -> {
            signss.stream().forEach((sign) -> {
                sign.removeMetadata("rentals_sign", getPlugin());
            });
        });
    }

    private void load() {
        try {
            ResultSet res = SQL.getInstance().selectAll("rentals_sign");
            while (res.next()) {
                World world = getWorld(res.getString("world"));
                Location loc = new Location(world, res.getInt("x"), res.getInt("y"), res.getInt("z"));
                Block b = loc.getBlock();
                int rentId = res.getInt("rentId");

                if (!(b.getState() instanceof Sign)) {
                    getPlugin().getLogger().log(Level.SEVERE, "Sign {0} don''t exist !!!", res.getInt("id"));
                    continue;
                }

                if (!signs.containsKey(rentId)) {
                    signs.put(rentId, new HashSet<>());
                }
                signs.get(rentId).add(b);
                b.setMetadata("rentals_sign", new FixedMetadataValue(getPlugin(), rentId));
            }
            updateAllSign();
        } catch (SQLException ex) {
            getLogger(SignManager.class.getName()).log(Level.SEVERE, null, ex);
            getPlugin().getLogger().log(Level.SEVERE, "Failed to load signs from database.");
        }
    }

    private void updateAllSign() {
        signs.entrySet().stream().forEach((Map.Entry<Integer, HashSet<Block>> signsM) -> {
            signsM.getValue().stream().forEach((sign) -> {
                update(sign, signsM.getKey());
            });
        });
    }

    private void update(Block signBl, int rentId) {
        Sign sign = (Sign) signBl.getState();
        Rent r = RentManager.getInstance().getRent(rentId);
        if (r.hasOwner()) {
            sign.setLine(0, "");
            sign.setLine(1, "Maison de");
            sign.setLine(2, r.getOwner());
            sign.setLine(3, "");
        } else {
            sign.setLine(0, ChatColor.RED + "A vendre");
            sign.setLine(1, "");
            sign.setLine(2, "Prix");
            sign.setLine(3, ChatColor.GREEN + "" + r.getPrice() + " $");
        }
        sign.update();
    }

    boolean remove(Block block) {
        for (Map.Entry<Integer, HashSet<Block>> signsM : signs.entrySet()) {
            for (Block sign : signsM.getValue()) {
                if (sign.equals(block)) {
                    return remove(signsM.getKey(), block);
                }
            }
        }
        return false;
    }

    private boolean remove(int key, Block block) {
        try {
            PreparedStatement ps = SQL.getInstance().getConnnection().prepareStatement(
                    "DELETE FROM rentals_sign WHERE (x = ?) AND (y = ?) AND (z = ?)");
            ps.setInt(1, block.getX());
            ps.setInt(2, block.getY());
            ps.setInt(3, block.getZ());
            ps.executeUpdate();
            
            HashSet signsS = signs.get(key);
            signsS.remove(block);
            if(signsS.isEmpty()){
                signs.remove(key);
            }
            block.removeMetadata("rentals_sign", getPlugin());
            return true;
        } catch (SQLException ex) {
            getLogger(RentManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void update(int rentId) {
        HashSet<Block> signsS = signs.get(rentId);
        signsS.stream().forEach((bl) -> {
            update(bl,rentId);
        });
    }

    private static class SignManagerHolder {

        private static final SignManager INSTANCE = new SignManager();
    }
}
