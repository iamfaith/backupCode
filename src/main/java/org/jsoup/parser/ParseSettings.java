package org.jsoup.parser;

import java.util.Iterator;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;

public class ParseSettings {
    public static final ParseSettings htmlDefault = new ParseSettings(false, false);
    public static final ParseSettings preserveCase = new ParseSettings(true, true);
    private final boolean preserveAttributeCase;
    private final boolean preserveTagCase;

    public ParseSettings(boolean tag, boolean attribute) {
        this.preserveTagCase = tag;
        this.preserveAttributeCase = attribute;
    }

    String normalizeTag(String name) {
        name = name.trim();
        if (this.preserveTagCase) {
            return name;
        }
        return name.toLowerCase();
    }

    String normalizeAttribute(String name) {
        name = name.trim();
        if (this.preserveAttributeCase) {
            return name;
        }
        return name.toLowerCase();
    }

    Attributes normalizeAttributes(Attributes attributes) {
        if (!this.preserveAttributeCase) {
            Iterator it = attributes.iterator();
            while (it.hasNext()) {
                Attribute attr = (Attribute) it.next();
                attr.setKey(attr.getKey().toLowerCase());
            }
        }
        return attributes;
    }
}
