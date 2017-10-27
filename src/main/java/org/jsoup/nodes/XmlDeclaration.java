package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;

public class XmlDeclaration extends Node {
    private final boolean isProcessingInstruction;
    private final String name;

    public XmlDeclaration(String name, String baseUri, boolean isProcessingInstruction) {
        super(baseUri);
        Validate.notNull(name);
        this.name = name;
        this.isProcessingInstruction = isProcessingInstruction;
    }

    public String nodeName() {
        return "#declaration";
    }

    public String name() {
        return this.name;
    }

    public String getWholeDeclaration() {
        return this.attributes.html().trim();
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        accum.append("<").append(this.isProcessingInstruction ? "!" : "?").append(this.name);
        this.attributes.html(accum, out);
        accum.append(this.isProcessingInstruction ? "!" : "?").append(">");
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    public String toString() {
        return outerHtml();
    }
}
