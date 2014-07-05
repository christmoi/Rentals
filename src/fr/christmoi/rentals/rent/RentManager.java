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

import fr.christmoi.rentals.Region;
import fr.christmoi.rentals.Rentals;
import fr.christmoi.rentals.SQL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 *
 * @author Moi
 */
public class RentManager {

    private final HashMap<Integer, Rent> rents;

    private RentManager() {
        rents = new HashMap<>();
        load();
    }

    public static RentManager getInstance() {
        return RentManagerHolder.INSTANCE;
    }

    public boolean exist(Region reg) {
        return reg.getRegionsBlocks().stream().anyMatch((bl) -> bl.hasMetadata("rentals"));
    }

    public boolean add(Rent r) {
        Vector vec1 = r.getRegion().getVecMin();
        Vector vec2 = r.getRegion().getVecMax();

        try {
            PreparedStatement ps = SQL.getInstance().getConnnection().prepareStatement(
                    "INSERT INTO rentals_rent (x1, y1, z1, x2, y2, z2, world, price)"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, vec1.getBlockX());
            ps.setInt(2, vec1.getBlockY());
            ps.setInt(3, vec1.getBlockZ());
            ps.setInt(4, vec2.getBlockX());
            ps.setInt(5, vec2.getBlockY());
            ps.setInt(6, vec2.getBlockZ());
            ps.setString(7, r.getRegion().getWorld().getName());
            ps.setDouble(8, r.getPrice());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (!rs.next()) return false;
            int id = rs.getInt(1);
            r.setId(id);
            rents.put(id, r);
            addMetadata(r.getRegion(), id);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(RentManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private void load() {
        try {
            ResultSet res = SQL.getInstance().selectAll("rentals_rent");
            while (res.next()) {
                World world = Bukkit.getWorld(res.getString("world"));
                Region reg = new Region(world,
                        new Vector(res.getInt("x1"), res.getInt("y1"), res.getInt("z1")),
                        new Vector(res.getInt("x2"), res.getInt("y2"), res.getInt("z2")));
                rents.put(res.getInt("id"), new Rent(res.getInt("id"), reg, res.getDouble("price"), res.getString("owner")));
                addMetadata(reg, res.getInt("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RentManager.class.getName()).log(Level.SEVERE, null, ex);
            Rentals.getPlugin().getLogger().log(Level.SEVERE, "Failed to load rents from database.");
        }
    }

    private void addMetadata(Region reg, int id) {
        reg.getRegionsBlocks().stream().forEach((bl) -> {
            bl.setMetadata("rentals", new FixedMetadataValue(Rentals.getPlugin(),id));
        });

    }
    
    public Rent getRent(int rentId){
        return rents.get(rentId);
    }

    public void purge() {
        rents.values().stream().forEach((r) -> {
            r.getRegion().getRegionsBlocks().stream().forEach((bl) -> {
                bl.removeMetadata("rentals", Rentals.getPlugin());
            });
        });
    }

    private static class RentManagerHolder {

        private static final RentManager INSTANCE = new RentManager();
    }
}
