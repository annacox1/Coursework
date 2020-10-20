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
    POST
    @Path("image")
    public String userImage(@CookieParam("token") Cookie sessionCookie,@FormDataParam("file") InputStream uploadedInputStream,
                            @FormDataParam("file") FormDataContentDisposition fileDetail) throws Exception {

        System.out.println("Invoked User.userImage()");

        String fileName = fileDetail.getFileName();  //file name submitted through form
        int dot = fileName.lastIndexOf('.');            //find where the . is to get the file extension
        String fileExtension = fileName.substring(dot + 1);   //get file extension from fileName
        String newFileName = "client/img/" + UUID.randomUUID() + "." + fileExtension;  //create a new unique identifier for file and append extension

        int userID = validateSessionCookie(sessionCookie);  //validate UUID sent from browser to get userID
        if (userID == -1) {
            return "Error:  Could not validate user";
        }

        PreparedStatement statement = Main.db.prepareStatement("UPDATE Users SET imagesource = ? WHERE UserID = ?" );
        statement.setString(1, newFileName);
        statement.setInt(2, userID);
        statement.executeUpdate();

        String uploadedFileLocation = "C:\\Users\\Rachel\\Desktop\\IdeaProjects\\DietTracker\\resources\\" + newFileName;

        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            System.out.println("File uploaded to server and imageLink saved to database");
            return "File uploaded to server and imageLink saved to database";

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return "{\"Error\": \"Something as gone wrong.  Please contact the administrator with the error code UC-UI. \"}";
        }


    }


}


