package controllers;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import utils.CashletsConfig;

public class Application extends Controller {

    public static void index() {
        render();
    }


    public static void addService()
    {
        if(request.method.equals("GET"))
        render();

        if(request.method.equals("POST"))
        {
            Logger.info("POST REquest to add service ");

            Logger.info("Requested URL : "+request.url);

            if(request.url.equals("/addService"))
            {
               String title = params.get("title");
               String desc = params.get("description");
               String location = params.get("location");
               String price = params.get("price");
               String day = params.get("day");
               String month=params.get("month");
               String year = params.get("year");

               Logger.info("title = "+title);
               Logger.info("description = "+desc);
               Logger.info("location = "+location);
               Logger.info("price = "+price);
               Logger.info("DOB = " + day + "/" + month + "/" + year);

               iService iServiceObj = new iService();

               iServiceObj.title=title;
               iServiceObj.description=desc;
               iServiceObj.location=location;
               iServiceObj.price= new Double(price);

               iServiceObj.save();


                SolrInputDocument  doc = new SolrInputDocument();
                doc.addField("id",iServiceObj.sid);
                doc.addField("title",title);
                doc.addField("description",desc);
                doc.addField("location",location);
                doc.addField("price",price);

                String url = CashletsConfig.SOLR_SEVER_URL;
                SolrServer solrserver = new HttpSolrServer(url);

                try
                {
                solrserver.add(doc);
                solrserver.commit();
                }
                catch(Exception e)
                {
                    Logger.error("Exception occured while adding Document to SOLR : ", e);
                }







            }

        }

    }

}