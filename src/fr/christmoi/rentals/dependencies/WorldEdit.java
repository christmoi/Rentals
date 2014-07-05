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

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Moi
 */
public class WorldEdit {

    private final WorldEditPlugin we;

    private WorldEdit() {
        we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
    }

    public static WorldEdit getInstance() {
        return new WorldEdit();
    }

    public boolean isRegionDefined(Player p) {
        return we.getSession(p).isSelectionDefined(we.getSession(p).getSelectionWorld());
    }

    public Location getPos1(Player p) {
        return we.getSelection(p).getMinimumPoint();
    }

    public Location getPos2(Player p) {
        return we.getSelection(p).getMaximumPoint();
    }

    public void purgeWorldEdit(Player p) {
        we.getSelection(p).getRegionSelector().clear();
    }

}
