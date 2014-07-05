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

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

/**
 *
 * @author Moi
 */
public class Region {

    private final World world;
    private final Vector vecMin;
    private final Vector vecMax;

    public Region(World world, Vector vecMin, Vector vecMax) {
        this.world = world;
        this.vecMin = vecMin;
        this.vecMax = vecMax;
    }

    public World getWorld() {
        return world;
    }

    public Vector getVecMin() {
        return vecMin;
    }

    public Vector getVecMax() {
        return vecMax;
    }
    
    

    public ArrayList<Block> getRegionsBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();
        
        for (int i = vecMin.getBlockX(); i <= vecMax.getBlockX(); i++) {
            for (int j = vecMin.getBlockY(); j <= vecMax.getBlockY(); j++) {
                for (int k = vecMin.getBlockZ(); k <= vecMax.getBlockZ(); k++) {
                    blocks.add(new Location(this.world, i, j, k).getBlock());
                }
            }
        }
        return blocks;
    }

}
