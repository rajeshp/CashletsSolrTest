package models;

import org.hibernate.annotations.GenericGenerator;
import play.db.jpa.GenericModel;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: rajeshp
 * Date: 9/3/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name="i_service")
//@AttributeOverride(name = "id", column = @Column(name = "sid"))

public class iService extends GenericModel {


    @Id
    @GeneratedValue

    public String sid;

    public String uid;
    public Date delivery_time;
    public String title;
    public String description;
    public String location;
    public double price;

}
