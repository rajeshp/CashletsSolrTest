package controllers;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import play.*;
import play.mvc.*;

import java.io.IOException;
import java.util.*;

import models.*;
import utils.CashletsConfig;

public class Application extends Controller {

    public static void index() {
        search(null);
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
				
				Logger.info("The solr server url is : "+url);
				
                SolrServer solrserver = new HttpSolrServer(url);

                try
                {
                solrserver.add(doc);
                UpdateResponse response = solrserver.commit();
                Logger.info("Document added to solr ["+iServiceObj.sid+"] responseCode="+response.getStatus());

                }
                catch(Exception e)
                {
                    Logger.error("Exception occured while adding Document to SOLR : "+e , e);
                }



                Application.index();



            }

        }

    }


    public static void search(String query)
    {
       Logger.info("The Search Query is :"+query);

       List serviceList=null;

       if(query!=null)
       {
           //do solr search

           SolrServer solrServer = new HttpSolrServer(CashletsConfig.SOLR_SEVER_URL);

           SolrQuery solrQuery = new SolrQuery();



           Logger.info("The Search Query is : *:"+query);
           solrQuery.setQuery(query);


           try
           {

            QueryResponse qresp = solrServer.query(solrQuery);
            serviceList = qresp.getResults();



           }
           catch(SolrServerException se)
           {
               Logger.error("Solr Server Exception while Querying :"+se);
           }

       }
        else
       {
           //show all records from mysql db

           List<iService> iServicesList = iService.findAll();


           serviceList = iServicesList;

       }



       render(query, serviceList);
    }


    public static void clean()
    {

        iService.deleteAll();

        SolrServer solrServer = new HttpSolrServer(CashletsConfig.SOLR_SEVER_URL);

        try
        {
        solrServer.deleteByQuery("*:*");

        solrServer.commit();

        }
        catch(Exception e)
        {
         Logger.error("Exception occured while cleaning application data :"+e);
        }




    }


}
