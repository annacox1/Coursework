package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("entry/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)


public class Entry {
    @GET
    @Path("list")
    public String entryList() {
        System.out.println("Invoked Entry.entryList()");
        JSONArray response = new JSONArray();

        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT EntryID, Title, Date, Content, CategoryID FROM Entries");
            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                JSONObject row = new JSONObject();
                row.put("EntryID", results.getInt(1));
                row.put("Title", results.getString(2));
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
    @POST
    @Path("update")
    public String updateEntry(@FormDataParam("EntryID") Integer entryID, @FormDataParam("Title") String title, @FormDataParam("Date") Integer date, @FormDataParam("Content") Integer content, @FormDataParam("CategoryID") Integer categoryID) {
        try {
            System.out.println("Invoked Entry.updateEntry/update id=" + entryID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Entry SET Title = ?, Date = ? , Content = ?, CategoryID = ? WHERE FoodID = ?");
            ps.setString(2, title);
            ps.setInt(4, date);
            ps.setInt(1, entryID);
            ps.setInt(5, content);
            ps.execute();
            return "{\"OK\": \"Entry updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }
    }




        }


