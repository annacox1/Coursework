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
            PreparedStatement ps = Main.db.prepareStatement("SELECT EventId, Title, Date, Description, Location, CategoryID FROM Events WHERE UserID = ?");
            ps.setInt(1, userID);

            ResultSet results = ps.executeQuery();
            while (results.next()==true) {
                JSONObject row = new JSONObject();
//
                row.put("EntryID", results.getString(1));
                row.put("Title", results.getString(2));
                row.put("Date", results.getString(3));
                row.put("Description", results.getString(4));
                row.put("Location", results.getString(5));
                row.put("CategoryID", results.getInt(6));   //you might want to show category name rather than ID, if so use a JOIN in SQL query above
                response.add(row);
            }
            return response.toString();
        } catch (Exception exception) {
            System.out.println("Database error: " + exception.getMessage());
            return "{\"Error\": \"Unable to list items.  Error code xx.\"}";
        }
    }

}