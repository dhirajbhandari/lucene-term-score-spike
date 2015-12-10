package com.redbubble.spike;

import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

public class PayloadSpike {
    private IndexWriter writer;
    private Directory dir;

    protected PayloadSimilarity payloadSimilarity;

    public PayloadSpike() {

    }

    public void buildIndex() throws Exception {
        dir = new RAMDirectory();

        PayloadEncoder encoder = new FloatEncoder();
        payloadSimilarity = new PayloadSimilarity();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, new PayloadAnalyzer(encoder));
        indexWriterConfig.setSimilarity(payloadSimilarity);

        writer = new IndexWriter(dir, indexWriterConfig);
    }

    public void addDocuments(String[] docs) throws Exception {
        for (int i = 0; i < docs.length; i++) {
            Document doc = new Document();
            Field id = new Field("id", "doc_" + i, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
            doc.add(id);
            //Store both position and offset information
            Field text = new Field("body", docs[i], Field.Store.NO, Field.Index.ANALYZED);
            doc.add(text);

            writer.addDocument(doc);
        }
        writer.close();
    }

    public IndexSearcher getIndexSearcher() throws IOException {
        DirectoryReader directoryReader = DirectoryReader.open(dir);
        //IndexSearcher searcher = new IndexSearcher(dir, true);
        IndexSearcher searcher = new IndexSearcher(directoryReader);
        searcher.setSimilarity(payloadSimilarity);//set the similarity.  Very important
        return searcher;
    }

    public TopDocs termQuery(String term) throws Exception {
        IndexSearcher searcher = getIndexSearcher();
        TermQuery tq = new TermQuery(new Term("body", term));
        return searcher.search(tq, 10);
    }

    public TopDocs payloadQuery(String term) throws Exception {
        IndexSearcher searcher = getIndexSearcher();
        PayloadTermQuery btq = new PayloadTermQuery(new Term("body", term), new AveragePayloadFunction());// was BoostingTermQuery(new Term("body", "fox"));
        TopDocs topDocs = searcher.search(btq, 10);
        return topDocs;
    }


}
