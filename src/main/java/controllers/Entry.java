package controllers;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;


import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("entry/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)


public class Entry {

    @GET
    @Path("list")
    public String entryList(@CookieParam("token") Cookie cookie) {
        System.out.println("Invoked Entry.entryList()");
        int userID = User.validateToken(cookie);
        if (userID == -1) {
            return "{\"Error\": \"Please log in.  Error code EC-EL\"}";
        }
        JSONArray response = new JSONArray();

        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT EntryID, Title, Date, Content, CategoryID FROM Entries WHERE UserID = ?");
            ps.setInt(1, userID);

            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                JSONObject row = new JSONObject();
                row.put("EntryID", results.getInt(1));   // 1 means the first column returned from the SQL query
                row.put("Title", results.getString(2));  //  2 means the second columns returned from the SQL query
                row.put("Date", results.getString(3));
                row.put("Content", results.getString(4));
                row.put("CategoryID", results.getInt(5));
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
    @GET
    @Path("category")
    public String entryList(@CookieParam("token") Cookie cookie) {
        System.out.println("Invoked Entry.categoryList()");
        int userID = User.validateToken(cookie);
        if (userID == -1) {
            return "{\"Error\": \"Please log in.  Error code EC-EL\"}";
        }
        JSONArray response = new JSONArray();

        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title FROM Categories");
            ps.setInt(1, userID);

            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                JSONObject row = new JSONObject();
                row.put("Title", results.getInt(1));   // 1 means the first column returned from the SQL query

                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }
    @POST
    @Path("add")
    public String entryAdd(@FormDataParam("entryID") Integer entryID, @FormDataParam("categoryID") Integer categoryID, @FormDataParam("title") String title, @FormDataParam("date") String date, @FormDataParam("content") String content, @CookieParam("token") Cookie cookie) {
        System.out.println("Invoked Entry.entryAdd()");
        int userID = User.validateToken(cookie);
        if (userID == -1) {
            return "{\"Error\": \"Please log in.  Error code EC-EL\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Entries (entryID, categoryID, title, date, content, userID, ) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, entryID);
            ps.setInt(2, categoryID);
            ps.setString(3, title);
            ps.setString(4, date);
            ps.setString(5, content);
            ps.setInt(6, userID);
            ps.execute();
            return "{\"OK\": \"Added entry.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }





}


