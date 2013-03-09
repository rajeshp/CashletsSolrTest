package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: rajeshp
 * Date: 9/3/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity(name = "i_service")

public class iService extends Model {
   public String sid;
   public String uid;
   public Date delivery_time;
    public String title;
    public String description;
    public String location;
    public double price;

}
