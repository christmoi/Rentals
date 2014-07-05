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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Moi
 */
public class SQL {

    private final Connection conn;

    private SQL() {
        Connection tmp = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://" + Rentals.getRConfig().getString("Mysql.host") + ":"
                    + Rentals.getRConfig().getInt("Mysql.port") + "/"
                    + Rentals.getRConfig().getString("Mysql.dbName");
            String user = Rentals.getRConfig().getString("Mysql.user");
            String pwd = Rentals.getRConfig().getString("Mysql.password");
            tmp = DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
        }
        conn = tmp;
    }

    public static SQL getInstance() {
        return SQLHolder.INSTANCE;
    }

    public Connection getConnnection(){
        return this.conn;
    }
    
    public boolean init() {
        if (conn == null) return false;
        try {
            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS rentals_rent (id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "x1 INT(11) NOT NULL,y1 INT(11) NOT NULL,z1 INT(11) NOT NULL,"
                    + "x2 INT(11) NOT NULL,y2 INT(11) NOT NULL,z2 INT(11) NOT NULL,"
                    + "world varchar(32) NOT NULL, price DOUBLE(11,2) NOT NULL,"
                    + "owner varchar(32))");
            conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS rentals_sign (id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "x INT(11) NOT NULL, y INT(11) NOT NULL, z INT(11) NOT NULL,"
                    + "world varchar(255) NOT NULL, rentId INT(11) NOT NULL)");
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public ResultSet selectAll(String table) throws SQLException {
        return conn.createStatement().executeQuery("SELECT * FROM " + table);

    }

    private static class SQLHolder {

        private static final SQL INSTANCE = new SQL();
    }
}
