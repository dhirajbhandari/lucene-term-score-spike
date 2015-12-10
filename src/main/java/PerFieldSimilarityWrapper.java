// Source: src/java/org/apache/solr/search/ext/MyPerFieldSimilarityWrapper.java
//package org.apache.solr.search.ext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;

/**
 * A delegating Similarity implementation similar to PerFieldAnalyzerWrapper.
 */
public class PerFieldSimilarityWrapper extends Similarity {

    private static final long serialVersionUID = -7777069917322737611L;

    private Similarity defaultSimilarity;
    private Map<String, Similarity> fieldSimilarityMap;

    public PerFieldSimilarityWrapper() {
        this.defaultSimilarity = new DefaultSimilarity();
        this.fieldSimilarityMap = new HashMap<String, Similarity>();
        this.fieldSimilarityMap.put("concepts", new PayloadSimilarity());
    }

    @Override
    public float coord(int overlap, int maxOverlap) {
        return defaultSimilarity.coord(overlap, maxOverlap);
    }

    @Override
    public float idf(int docFreq, int numDocs) {
        return defaultSimilarity.idf(docFreq, numDocs);
    }

    @Override
    public float lengthNorm(String fieldName, int numTokens) {
        Similarity sim = fieldSimilarityMap.get(fieldName);
        if (sim == null) {
            return defaultSimilarity.lengthNorm(fieldName, numTokens);
        } else {
            return sim.lengthNorm(fieldName, numTokens);
        }
    }

    @Override
    public float queryNorm(float sumOfSquaredWeights) {
        return defaultSimilarity.queryNorm(sumOfSquaredWeights);
    }

    @Override
    public long computeNorm(FieldInvertState state) {
        return 0;
    }

    @Override
    public SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats, TermStatistics... termStats) {
        return null;
    }

    @Override
    public SimScorer simScorer(SimWeight weight, AtomicReaderContext context) throws IOException {
        return null;
    }

    @Override
    public float sloppyFreq(int distance) {
        return defaultSimilarity.sloppyFreq(distance);
    }

    @Override
    public float tf(float freq) {
        return defaultSimilarity.tf(freq);
    }

    @Override
    public float scorePayload(int docId, String fieldName,
                              int start, int end, byte[] payload, int offset, int length) {
        Similarity sim = fieldSimilarityMap.get(fieldName);
        if (sim == null) {
            return defaultSimilarity.scorePayload(docId, fieldName,
                    start, end, payload, offset, length);
        } else {
            return sim.scorePayload(docId, fieldName,
                    start, end, payload, offset, length);
        }
    }
}
