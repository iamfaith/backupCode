package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;

public class DocumentType extends Node {
    private static final String NAME = "name";
    private static final String PUBLIC_ID = "publicId";
    public static final String PUBLIC_KEY = "PUBLIC";
    private static final String PUB_SYS_KEY = "pubSysKey";
    private static final String SYSTEM_ID = "systemId";
    public static final String SYSTEM_KEY = "SYSTEM";

    public DocumentType(String name, String publicId, String systemId, String baseUri) {
        super(baseUri);
        attr(NAME, name);
        attr(PUBLIC_ID, publicId);
        if (has(PUBLIC_ID)) {
            attr(PUB_SYS_KEY, PUBLIC_KEY);
        }
        attr(SYSTEM_ID, systemId);
    }

    public DocumentType(String name, String pubSysKey, String publicId, String systemId, String baseUri) {
        super(baseUri);
        attr(NAME, name);
        if (pubSysKey != null) {
            attr(PUB_SYS_KEY, pubSysKey);
        }
        attr(PUBLIC_ID, publicId);
        attr(SYSTEM_ID, systemId);
    }

    public String nodeName() {
        return "#doctype";
    }

    void outerHtmlHead(Appendable accum, int depth, OutputSettings out) throws IOException {
        if (out.syntax() != Syntax.html || has(PUBLIC_ID) || has(SYSTEM_ID)) {
            accum.append("<!DOCTYPE");
        } else {
            accum.append("<!doctype");
        }
        if (has(NAME)) {
            accum.append(" ").append(attr(NAME));
        }
        if (has(PUB_SYS_KEY)) {
            accum.append(" ").append(attr(PUB_SYS_KEY));
        }
        if (has(PUBLIC_ID)) {
            accum.append(" \"").append(attr(PUBLIC_ID)).append('\"');
        }
        if (has(SYSTEM_ID)) {
            accum.append(" \"").append(attr(SYSTEM_ID)).append('\"');
        }
        accum.append('>');
    }

    void outerHtmlTail(Appendable accum, int depth, OutputSettings out) {
    }

    private boolean has(String attribute) {
        return !StringUtil.isBlank(attr(attribute));
    }
}
