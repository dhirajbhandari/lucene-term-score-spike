package com.redbubble.spike;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import java.io.IOException;

public class PayloadTest {
    public static void main(String[] args) {
        JUnitCore.main(args);
    }

    public static String[] DOCS = {
            "The quick|2.0 red|2.0 fox|5.0 jumped|5.0 over the lazy|2.0 brown|2.0 dogs|10.0",
            "The quick red fox jumped over the lazy brown dogs",//no boosts
            "The quick|2.0 red|2.0 fox|10.0 jumped|5.0 over the old|2.0 brown|2.0 box|10.0",
            "Mary|10.0 had a little|2.0 lamb|10.0 whose fleece|10.0 was|5.0 white|2.0 as snow|10.0",
            "Mary had a little lamb whose fleece was white as snow",
            "Mary|10.0 takes on Wolf|10.0 Restoration|10.0 project|10.0 despite ties|10.0 to sheep|10.0 farming|10.0",
            "Mary|10.0 who lives|5.0 on a farm|10.0 is|5.0 happy|2.0 that she|10.0 takes|5.0 a walk|10.0 every day|10.0",
            "Moby|10.0 Dick|10.0 is|5.0 a story|10.0 of a whale|10.0 and a man|10.0 obsessed|10.0",
            "The robber|10.0 wore|5.0 a black|2.0 fleece|10.0 jacket|10.0 and a baseball|10.0 cap|10.0",
            "The English|10.0 Springer|10.0 Spaniel|10.0 is|5.0 the best|2.0 of all dogs|10.0"
    };


    protected PayloadSpike payloadSpike;

    @Before
    public void setUp() throws Exception {
        payloadSpike = new PayloadSpike();
        payloadSpike.buildIndex();
        payloadSpike.addDocuments(DOCS);
    }

    @Test
    public void testPayloadFox() throws Exception {
        search("fox");
    }

    @Test
    public void testPayloadQuick() throws Exception {
        search("quick");
    }

    @Test
    public void testPayloadDog() throws Exception {
        search("dogs");
    }

    private void search(String term) throws Exception {
        IndexSearcher searcher = payloadSpike.getIndexSearcher();
        PayloadTermQuery btq = new PayloadTermQuery(new Term("body", term), new AveragePayloadFunction());// was BoostingTermQuery(new Term("body", "fox"));
        TopDocs topDocs = searcher.search(btq, 10);
        printResults(searcher, btq, topDocs);

        TermQuery tq = new TermQuery(new Term("body", term));
        topDocs = searcher.search(tq, 10);
        printResults(searcher, tq, topDocs);
    }

    private void printResults(IndexSearcher searcher, Query query, TopDocs topDocs) throws IOException {
        System.out.println("-----------");
        System.out.println("Results for " + query + " of type: " + query.getClass().getName());
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            ScoreDoc doc = topDocs.scoreDocs[i];
            System.out.println("Doc: " + doc.toString());
            System.out.println("Explain: " + searcher.explain(query, doc.doc));
        }
    }
}