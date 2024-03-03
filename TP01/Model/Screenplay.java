package Model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;



public class Screenplay {
    Boolean rip;
    int id;
    String type;
    String title;
    String director;
    String cast;
    long dateadded;
    int releasedate;
    char[] rating = new char[5];

    // Constructors

    public Screenplay() {
        rip = false;
        id = -1;
        type = "";
        title = "";
        director = "";
        cast = "";
        dateadded = 0;
        releasedate = 0;
        rating = new char[5];
    }

    public Screenplay(Boolean rip, int id,String type, String title, String director, String cast, String dateadded, int releasedate, char[] rating) {
        this.rip = rip;
        this.id = id;
        this.type = type;
        this.title = title;
        this.director = director;
        this.cast = cast;
        this.dateadded = dateConverter(dateadded);
        this.releasedate = releasedate;
        this.rating = rating;
    }

    public Screenplay(Boolean rip, int id,String type, String title, String director, String cast, long dateadded, int releasedate, char[] rating) {
        this.rip = rip;
        this.id = id;
        this.type = type;
        this.title = title;
        this.director = director;
        this.cast = cast;
        this.dateadded = dateadded;
        this.releasedate = releasedate;
        this.rating = rating;
    }

    // Getters
    public Boolean getRip() {
        return rip;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getCast() {
        return cast;
    }

    public String getDateadded() {
        return timestampConverter(dateadded);
    }

    public int getReleasedate() {
        return releasedate;
    }

    public char[] getRating() {
        return rating;
    }

    // Setters

    public void setRip(Boolean rip) {
        this.rip = rip;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public void setDateadded(String dateadded) {
        this.dateadded = dateConverter(dateadded);
    }

    public void setDateadded(long dateadded) {
        this.dateadded = dateadded;
    }

    public void setReleasedate(int releasedate) {
        this.releasedate = releasedate;
    }

    public void setRating(char[] rating) {
        this.rating = rating;
    }

    // dateConvert

    private long dateConverter(String data) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
            LocalDate localDate = LocalDate.parse(data, formatter); // converts string to localdate
            Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(); // converts localdate to instant
            return instant.toEpochMilli(); // returns timestamp
        } catch (Exception e) {
            return 0;
        }
        
    }

    //timestampConverter

    private String timestampConverter(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp); // converts timestamp to instant
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate(); // converts instant to localdate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    // toString

    public String toString() {
        return  "\n id= " + id +
                "\n type= " + type + 
                "\n title= " + title + 
                "\n director= " + director + 
                "\n cast= " + cast +
                "\n dateadded= " + timestampConverter(dateadded) +
                "\n releasedate= " + releasedate +
                "\n rating= " + new String(rating);
    }

    // tobytearray

    public byte[] toByteArray() throws IOException{
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeBoolean(rip);
        dos.writeInt(id);
        dos.writeUTF(type);
        dos.writeUTF(title);
        dos.writeUTF(director);
        dos.writeUTF(cast);
        dos.writeLong(dateadded);
        dos.writeInt(releasedate);
        dos.writeBytes(new String(rating));
        dos.close();
        return baos.toByteArray();

    }

    //frombytearray

    public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        type = dis.readUTF();
        title = dis.readUTF();
        director = dis.readUTF();
        cast = dis.readUTF();
        dateadded = dis.readLong();
        releasedate = dis.readInt();
        byte[] rating = new byte[5];
        dis.read(rating, 0, 5);
        this.rating = new String(rating).toCharArray();
    }

}