package com.modeln.utils;

import java.sql.*;
import java.util.UUID;

public class QueryPG {

    public static String getLinearId(String name, String type)
    {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/modeln","ModelN", "ModelN");
            stmt = c.prepareStatement("select * from mn_member_state where member_name = ? and member_type = ? ;");
            stmt.setString(1, name);
            stmt.setString(2, type);
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                System.out.println(rs.getString("linear_id"));
                return rs.getString("linear_id");
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        }finally {
            try {
                rs.close();
                stmt.close();
                c.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        System.out.println(" Data Retrieved Successfully ..");
        return null;
    }

    public static boolean isLinearId(String linearId)
    {
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/modeln","ModelN", "ModelN");
            stmt = c.prepareStatement("select * from mn_member_state where linear_id = ?;");
            stmt.setString(1, linearId);
            rs = stmt.executeQuery();
            if(rs.next()){
                return true;
            }
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
        }finally {
            try {
                rs.close();
                stmt.close();
                c.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }
}
