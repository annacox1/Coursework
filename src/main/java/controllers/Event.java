package controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Main;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Path("event/")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)


public class Event {
    @GET
    @Path("list")
    public String eventList(@CookieParam("token") Cookie cookie) {
        System.out.println("Invoked Event.eventList()");
        int userID = User.validateToken(cookie);
        if (userID == -1) {
            return "{\"Error\": \"Please log in.  Error code EC-EL\"}";
        }
        JSONArray response = new JSONArray();

        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT EventID, Title, Date, Description, Location, CategoryID FROM Events WHERE UserID = ?");
            ps.setInt(1, userID);

            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                JSONObject row = new JSONObject();

                row.put("EventID", results.getString(1));
                row.put("Title", results.getString(2));
                row.put("Date", results.getString(3));
                row.put("Description", results.getString(4));
                row.put("Location", results.getString(5));
                row.put("CategoryID", results.getInt(6));
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
    public String eventAdd(@FormDataParam("title") String title, @FormDataParam("description") String description, @FormDataParam("location") String location, @FormDataParam("date") String date, @FormDataParam("ddlCategories") int categoryID,  @CookieParam("token") Cookie cookie) {
        System.out.println("Invoked Event.eventAdd()");
        int userID = User.validateToken(cookie);
        if (userID == -1) {
            return "{\"Error\": \"Please log in.  Error code EC-EL\"}";
        }
        try {
            PreparedStatement ps = Main.db.prepareStatement("INSERT INTO Events (title, description, location, date, categoryID, userID) VALUES (?, ?, ?, ?, ?,?)");
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, date);
            ps.setInt(5, categoryID);
            ps.setInt(6, userID);
            ps.execute();
            return "{\"OK\": \"Added event.\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to create new item, please see server console for more info.\"}";
        }

    }
    @POST
    @Path("delete/{eventID}")

    public String deleteEvent(@PathParam("eventID") Integer eventID) {
        System.out.println("Invoked deleteEntry()");
        try {
            PreparedStatement ps = Main.db.prepareStatement("DELETE FROM Events WHERE EventID = ?");
            ps.setInt(1, eventID);
            ps.execute();
            return "{\"OK\": \"Event deleted\"}";
        }    catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to delete item, please see server console for more info.\"}";
        }
    }

    @POST
    @Path("update")
    public String updateEntry(@FormDataParam("eventID") int eventID, @FormDataParam("title") String title, @FormDataParam("description") String description, @FormDataParam("location") String location, @FormDataParam("date") String date, @FormDataParam("ddlCategories") int categoryID, @CookieParam("token") Cookie cookie) {
        try {
            System.out.println("Invoked Event.updateEvent/update id=" + eventID);
            PreparedStatement ps = Main.db.prepareStatement("UPDATE Entries SET Title = ?, Description = ? , Location = ?, Date = ?, CategoryID =?  WHERE EventID = ?");
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, location);
            ps.setString(4, date);
            ps.setInt(5, categoryID);
            ps.setInt(6, eventID);
            ps.execute();
            return "{\"OK\": \"Event updated\"}";
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to update item, please see server console for more info.\"}";
        }


    }

    @GET
    @Path("get/{eventID}")
    public String getEntry(@PathParam("eventID") Integer eventID) {
        System.out.println("Invoked Event.getEvent() with eventID " + eventID);
        try {
            PreparedStatement ps = Main.db.prepareStatement("SELECT Title, Description, Location, Date, CategoryID FROM Events WHERE EventID = ?");
            ps.setInt(1, eventID);
            ResultSet results = ps.executeQuery();
            JSONObject response = new JSONObject();
            if (results.next() == true) {
                response.put("Title", results.getString(1));
                response.put("Description", results.getString(2));
                response.put("Location", results.getString(3));
                response.put("Date", results.getString(4));
                response.put("CategoryID", results.getInt(5));
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to get item, please see server console for more info.\"}";
        }

    }


}