package com.redbubble.spike;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.payloads.DelimitedPayloadTokenFilter;
import org.apache.lucene.analysis.payloads.PayloadEncoder;

import java.io.Reader;

public class PayloadAnalyzer extends Analyzer {

    private PayloadEncoder encoder;

    PayloadAnalyzer(PayloadEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        WhitespaceTokenizer whitespaceTokenizer =  new WhitespaceTokenizer(reader);
        final Tokenizer source = whitespaceTokenizer;

        TokenStream result = new LowerCaseFilter(whitespaceTokenizer);
        result = new DelimitedPayloadTokenFilter(result, DelimitedPayloadTokenFilter.DEFAULT_DELIMITER, encoder);
        return new TokenStreamComponents(source, result);
    }
}
