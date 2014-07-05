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

import fr.christmoi.rentals.commands.RCommandsManager;
import fr.christmoi.rentals.rent.RentManager;
import fr.christmoi.rentals.sign.SignEvent;
import fr.christmoi.rentals.sign.SignManager;
import java.io.File;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Rentals extends JavaPlugin {

    private static String pluginName;

    @Override
    public void onEnable() {
        pluginName = this.getName();
        loadConfig();
        if(!SQL.getInstance().init()){
            this.getLogger().log(Level.SEVERE, "Could not connect to database");
            this.setEnabled(false);
            return;
        }
        getCommand("rentals").setExecutor(new RCommandsManager());
        getServer().getPluginManager().registerEvents(new SignEvent(), this);
        SignManager.getInstance();
    }

    @Override
    public void onDisable() {
     //   PlayerManager.getInstance().purge();
        RentManager.getInstance().purge();
        SignManager.getInstance().purge();        
    }

    
    
    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }
    
    public static FileConfiguration getRConfig(){
        return getPlugin().getConfig();
    }

    public void loadConfig() {
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
        }
    }
}
