package com.redbubble.spike;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;

public class PayloadSimilarity extends DefaultSimilarity {
    //Note the new similarity signature, giving much more information about the field name, etc.

    @Override
    public float scorePayload(int doc, int start, int end, BytesRef payload) {
        return PayloadHelper.decodeFloat(payload.bytes, payload.offset);
        //we can ignore length here, because we know it is encoded as 4 bytes
    }
}
