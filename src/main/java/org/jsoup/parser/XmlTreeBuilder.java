package org.jsoup.parser;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;

public class XmlTreeBuilder extends TreeBuilder {
    public /* bridge */ /* synthetic */ boolean processStartTag(String str, Attributes attributes) {
        return super.processStartTag(str, attributes);
    }

    ParseSettings defaultSettings() {
        return ParseSettings.preserveCase;
    }

    Document parse(String input, String baseUri) {
        return parse(input, baseUri, ParseErrorList.noTracking(), ParseSettings.preserveCase);
    }

    protected void initialiseParse(String input, String baseUri, ParseErrorList errors, ParseSettings settings) {
        super.initialiseParse(input, baseUri, errors, settings);
        this.stack.add(this.doc);
        this.doc.outputSettings().syntax(Syntax.xml);
    }

    protected boolean process(Token token) {
        switch (token.type) {
            case StartTag:
                insert(token.asStartTag());
                break;
            case EndTag:
                popStackToClose(token.asEndTag());
                break;
            case Comment:
                insert(token.asComment());
                break;
            case Character:
                insert(token.asCharacter());
                break;
            case Doctype:
                insert(token.asDoctype());
                break;
            case EOF:
                break;
            default:
                Validate.fail("Unexpected token type: " + token.type);
                break;
        }
        return true;
    }

    private void insertNode(Node node) {
        currentElement().appendChild(node);
    }

    Element insert(StartTag startTag) {
        Tag tag = Tag.valueOf(startTag.name(), this.settings);
        Element el = new Element(tag, this.baseUri, this.settings.normalizeAttributes(startTag.attributes));
        insertNode(el);
        if (startTag.isSelfClosing()) {
            this.tokeniser.acknowledgeSelfClosingFlag();
            if (!tag.isKnownTag()) {
                tag.setSelfClosing();
            }
        } else {
            this.stack.add(el);
        }
        return el;
    }

    void insert(Comment commentToken) {
        Node comment = new Comment(commentToken.getData(), this.baseUri);
        Node insert = comment;
        if (commentToken.bogus) {
            String data = comment.getData();
            if (data.length() > 1 && (data.startsWith("!") || data.startsWith("?"))) {
                Element el = Jsoup.parse("<" + data.substring(1, data.length() - 1) + ">", this.baseUri, Parser.xmlParser()).child(0);
                insert = new XmlDeclaration(this.settings.normalizeTag(el.tagName()), comment.baseUri(), data.startsWith("!"));
                insert.attributes().addAll(el.attributes());
            }
        }
        insertNode(insert);
    }

    void insert(Character characterToken) {
        insertNode(new TextNode(characterToken.getData(), this.baseUri));
    }

    void insert(Doctype d) {
        insertNode(new DocumentType(this.settings.normalizeTag(d.getName()), d.getPubSysKey(), d.getPublicIdentifier(), d.getSystemIdentifier(), this.baseUri));
    }

    private void popStackToClose(EndTag endTag) {
        int pos;
        String elName = endTag.name();
        Element firstFound = null;
        for (pos = this.stack.size() - 1; pos >= 0; pos--) {
            Element next = (Element) this.stack.get(pos);
            if (next.nodeName().equals(elName)) {
                firstFound = next;
                break;
            }
        }
        if (firstFound != null) {
            pos = this.stack.size() - 1;
            while (pos >= 0) {
                next = (Element) this.stack.get(pos);
                this.stack.remove(pos);
                if (next != firstFound) {
                    pos--;
                } else {
                    return;
                }
            }
        }
    }

    List<Node> parseFragment(String inputFragment, String baseUri, ParseErrorList errors, ParseSettings settings) {
        initialiseParse(inputFragment, baseUri, errors, settings);
        runParser();
        return this.doc.childNodes();
    }
}
