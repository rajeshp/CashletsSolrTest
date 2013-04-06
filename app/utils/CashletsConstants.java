package utils;

/**
 * Created with IntelliJ IDEA.
 * User: prajesh
 * Date: 6/4/13
 * Time: 5:09 PM
 *
 * This is the constants class created for CasheltsService application for maintanability
 */
public class CashletsConstants {

    //solr related constants
    public static final int SOLR_DEFAULT_OFFSET = 0;

    public static final int SOLR_DEFAULT_LIMIT = 100;

    public static final String SOLR_LOCATION_FIELD="location";

    public static final String SOLR_PRICE_FIELD="price";

    public static final String SOLR_DEFAULT_SEARCH_FIELD="text";

    public static final String SOLR_ALL_MATCH_CHAR = "*";


    // request parameters related constatnts

    public static final String DEFAULT_OFFSET_REQUEST_PARAM = "offset";

    public static final String DEFAULT_LIMIT_REQUEST_PARAM = "limit";

    public static final String FACET_BY_PRICE_REQUEST_PARAM = "facetByPrice";

    public static final String MIN_PRICE_REQUEST_PARAM = "min";

    public static final String MAX_PRICE_REQUEST_PARAM = "max";


}
