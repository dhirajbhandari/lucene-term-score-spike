// Source: src/java/org/apache/solr/search/ext/PayloadQParserPlugin.java
//package org.apache.solr.search.ext;

//import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;


/**
 * Parser plugin to parse payload queries.
 */
public class PayloadQParserPlugin extends QParserPlugin {

    @Override
    public QParser createParser(String qstr, SolrParams localParams,
                                SolrParams params, SolrQueryRequest req) {
        return new PayloadQParser(qstr, localParams, params, req);
    }

    @Override
    public void init(NamedList args) {
    }
}

class StringUtils {
    public static String[] split(String str, String separator) {
        return str.split(separator);
    }
}

class PayloadQParser extends QParser {

    public PayloadQParser(String qstr, SolrParams localParams, SolrParams params,
                          SolrQueryRequest req) {
        super(qstr, localParams, params, req);
    }

    @Override
    public Query parse() throws SyntaxError {
        BooleanQuery q = new BooleanQuery();
        String[] nvps = StringUtils.split(qstr, " ");
        for (int i = 0; i < nvps.length; i++) {
            String[] nv = StringUtils.split(nvps[i], ":");
            if (nv[0].startsWith("+")) {
                q.add(new PayloadTermQuery(new Term(nv[0].substring(1), nv[1]),
                        new AveragePayloadFunction(), false), Occur.MUST);
            } else {
                q.add(new PayloadTermQuery(new Term(nv[0], nv[1]),
                        new AveragePayloadFunction(), false), Occur.SHOULD);
            }
        }
        return q;
    }
}
