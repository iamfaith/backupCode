package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.nodes.Document.OutputSettings;

public class Comment extends Node {
    private static final String COMMENT_KEY = "comment";

    public Comment(String data, String baseUri) {
        super(baseUri);
        this.attributes.put("comment", data);
    }

    public String nodeName() {
        return "#comment";
    }

    public String getData() {
        return this.attributes.get("comment");
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        if (out.prettyPrint()) {
            indent(accum, depth, out);
        }
        accum.append("<!--").append(getData()).append("-->");
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }
}
