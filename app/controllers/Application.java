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
import utils.CashletsConstants;

public class Application extends Controller {

    public static void index() {
        search(null,null,"json");
    }

    /*
    *  @author prajesh
    *
    *  this method is used to add services to mysql db and solr index.
    *
    *
    * */
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

      /*
      * @author prajesh
      *
      * this method is used to get the search results from solr
      *
      * expected parameters :
      * query = is the query string
      * location= for location based search
      * responseType = json or xml, default responseType is json
      *
      * other optional request parameters
      *  offset - default 0, used for pagination of results
      *  limit  - default 100, used for pagination of results,
      *
      *  min - to filter results by minimum price, default 0
      *  max - to filter results by maximum price, default NO LIMIT
      *
      * */
    public static void search(String query,String location, String responseType )
    {
        Logger.info("The Search Query is :"+query+" and the location is : "+location);

        List serviceList=null;

        int offset=CashletsConstants.SOLR_DEFAULT_OFFSET,limit=CashletsConstants.SOLR_DEFAULT_LIMIT;
        int minPrice=0,maxPrice=-1;
        boolean facetByPrice=false;

        if(params._contains(CashletsConstants.DEFAULT_OFFSET_REQUEST_PARAM)) {
            offset=Integer.parseInt(params.get(CashletsConstants.DEFAULT_OFFSET_REQUEST_PARAM));
        }

        if(params._contains(CashletsConstants.DEFAULT_LIMIT_REQUEST_PARAM)) {
            offset=Integer.parseInt(params.get(CashletsConstants.DEFAULT_LIMIT_REQUEST_PARAM));
        }


        if(params._contains(CashletsConstants.MIN_PRICE_REQUEST_PARAM))
        {
            minPrice=Integer.parseInt(params.get(CashletsConstants.MIN_PRICE_REQUEST_PARAM));
            facetByPrice=true;
        }
        if(params._contains(CashletsConstants.MAX_PRICE_REQUEST_PARAM))
        {
            maxPrice=Integer.parseInt(params.get(CashletsConstants.MAX_PRICE_REQUEST_PARAM));
            facetByPrice=true;
        }


        if(query!=null && !query.equals(""))
        {
            //do solr search

            SolrServer solrServer = new HttpSolrServer(CashletsConfig.SOLR_SEVER_URL);
            SolrQuery solrQuery = new SolrQuery();

            solrQuery.setStart(offset);
            solrQuery.setRows(limit);

            Logger.info("The Search Query is : text:"+query);
            Logger.info("responseType"+responseType);
            solrQuery.setQuery(CashletsConstants.SOLR_DEFAULT_SEARCH_FIELD+":"+query);


            try
            {

                if(location!=null && !location.equals(""))
                {
                    Logger.info("adding facets to the query for location field");
                    solrQuery.setFacet(true);
                    solrQuery.addFacetField(CashletsConstants.SOLR_LOCATION_FIELD);
                    solrQuery.addFacetQuery(CashletsConstants.SOLR_LOCATION_FIELD+":"+location);
                    solrQuery.setFilterQueries(CashletsConstants.SOLR_LOCATION_FIELD+":"+location);
                }

                if(facetByPrice)
                {
                    Logger.info("****adding facet by price to the query****");
                    solrQuery.setFacet(true);
                    solrQuery.addFacetField(CashletsConstants.SOLR_PRICE_FIELD);

                    StringBuilder sb = new StringBuilder();

                    sb.append("[");

                    if(minPrice>0)
                        sb.append(minPrice);
                    else
                        sb.append("*");

                    sb.append(" TO ");

                    if(maxPrice!=-1)
                        sb.append(maxPrice);
                    else
                        sb.append("*");

                    sb.append("]");

                    solrQuery.addFacetQuery(CashletsConstants.SOLR_PRICE_FIELD+":"+sb.toString());
                    solrQuery.setFilterQueries(CashletsConstants.SOLR_PRICE_FIELD+":"+sb.toString());

                }

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

            Logger.info("Showing records from mysql db");
           // List<iService> iServicesList = iService.findAll();
            List<iService> iServicesList = iService.find("order by createdOn desc").from(offset).fetch(limit);

            serviceList = iServicesList;

        }


        if(responseType!=null)
        {
            if(responseType.equals("xml"))
                renderXml(serviceList);
            else
                renderJSON(serviceList);
        }
        else
            renderJSON(serviceList);


        //render(query, serviceList);
    }


     /*
     *
     * @author prajesh
     * util method for clearing all data from mysql db and solr index.
     *
     * NOTE******* This method should be removed before going into production, this is only for development
     * and testing purposes.
     *
     * */
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
