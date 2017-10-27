package org.jsoup.parser;

import java.util.ArrayList;
import java.util.Iterator;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document.QuirksMode;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

enum HtmlTreeBuilderState {
    Initial {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                Doctype d = t.asDoctype();
                tb.getDocument().appendChild(new DocumentType(tb.settings.normalizeTag(d.getName()), d.getPubSysKey(), d.getPublicIdentifier(), d.getSystemIdentifier(), tb.getBaseUri()));
                if (d.isForceQuirks()) {
                    tb.getDocument().quirksMode(QuirksMode.quirks);
                }
                tb.transition(BeforeHtml);
            } else {
                tb.transition(BeforeHtml);
                return tb.process(t);
            }
            return true;
        }
    },
    BeforeHtml {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isDoctype()) {
                tb.error(this);
                return false;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            } else {
                if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                    tb.insert(t.asStartTag());
                    tb.transition(BeforeHead);
                } else {
                    if (t.isEndTag()) {
                        if (StringUtil.in(t.asEndTag().normalName(), "head", "body", "html", "br")) {
                            return anythingElse(t, tb);
                        }
                    }
                    if (!t.isEndTag()) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                }
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.insertStartTag("html");
            tb.transition(BeforeHead);
            return tb.process(t);
        }
    },
    BeforeHead {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return true;
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
                return true;
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return InBody.process(t, tb);
            } else {
                if (t.isStartTag() && t.asStartTag().normalName().equals("head")) {
                    tb.setHeadElement(tb.insert(t.asStartTag()));
                    tb.transition(InHead);
                    return true;
                }
                if (t.isEndTag()) {
                    if (StringUtil.in(t.asEndTag().normalName(), "head", "body", "html", "br")) {
                        tb.processStartTag("head");
                        return tb.process(t);
                    }
                }
                if (t.isEndTag()) {
                    tb.error(this);
                    return false;
                }
                tb.processStartTag("head");
                return tb.process(t);
            }
        }
    },
    InHead {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
                return true;
            }
            String name;
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    return true;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    StartTag start = t.asStartTag();
                    name = start.normalName();
                    if (name.equals("html")) {
                        return InBody.process(t, tb);
                    }
                    if (StringUtil.in(name, "base", "basefont", "bgsound", "command", "link")) {
                        Element el = tb.insertEmpty(start);
                        if (!name.equals("base") || !el.hasAttr("href")) {
                            return true;
                        }
                        tb.maybeSetBaseUri(el);
                        return true;
                    } else if (name.equals("meta")) {
                        tb.insertEmpty(start);
                        return true;
                    } else if (name.equals("title")) {
                        HtmlTreeBuilderState.handleRcData(start, tb);
                        return true;
                    } else {
                        if (StringUtil.in(name, "noframes", "style")) {
                            HtmlTreeBuilderState.handleRawtext(start, tb);
                            return true;
                        } else if (name.equals("noscript")) {
                            tb.insert(start);
                            tb.transition(InHeadNoscript);
                            return true;
                        } else if (name.equals("script")) {
                            tb.tokeniser.transition(TokeniserState.ScriptData);
                            tb.markInsertionMode();
                            tb.transition(Text);
                            tb.insert(start);
                            return true;
                        } else if (!name.equals("head")) {
                            return anythingElse(t, tb);
                        } else {
                            tb.error(this);
                            return false;
                        }
                    }
                case EndTag:
                    name = t.asEndTag().normalName();
                    if (name.equals("head")) {
                        tb.pop();
                        tb.transition(AfterHead);
                        return true;
                    }
                    if (StringUtil.in(name, "body", "html", "br")) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                default:
                    return anythingElse(t, tb);
            }
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            tb.processEndTag("head");
            return tb.process(t);
        }
    },
    InHeadNoscript {
        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isDoctype()) {
                tb.error(this);
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else {
                if (t.isEndTag() && t.asEndTag().normalName().equals("noscript")) {
                    tb.pop();
                    tb.transition(InHead);
                } else {
                    if (!(HtmlTreeBuilderState.isWhitespace(t) || t.isComment())) {
                        if (t.isStartTag()) {
                        }
                        if (t.isEndTag() && t.asEndTag().normalName().equals("br")) {
                            return anythingElse(t, tb);
                        }
                        if (t.isStartTag()) {
                        }
                        if (!t.isEndTag()) {
                            return anythingElse(t, tb);
                        }
                        tb.error(this);
                        return false;
                    }
                    return tb.process(t, InHead);
                }
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            tb.insert(new Character().data(t.toString()));
            return true;
        }
    },
    AfterHead {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
            } else if (t.isStartTag()) {
                StartTag startTag = t.asStartTag();
                String name = startTag.normalName();
                if (name.equals("html")) {
                    return tb.process(t, InBody);
                }
                if (name.equals("body")) {
                    tb.insert(startTag);
                    tb.framesetOk(false);
                    tb.transition(InBody);
                } else if (name.equals("frameset")) {
                    tb.insert(startTag);
                    tb.transition(InFrameset);
                } else {
                    if (StringUtil.in(name, "base", "basefont", "bgsound", "link", "meta", "noframes", "script", "style", "title")) {
                        tb.error(this);
                        Element head = tb.getHeadElement();
                        tb.push(head);
                        tb.process(t, InHead);
                        tb.removeFromStack(head);
                    } else if (name.equals("head")) {
                        tb.error(this);
                        return false;
                    } else {
                        anythingElse(t, tb);
                    }
                }
            } else if (t.isEndTag()) {
                if (StringUtil.in(t.asEndTag().normalName(), "body", "html")) {
                    anythingElse(t, tb);
                } else {
                    tb.error(this);
                    return false;
                }
            } else {
                anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.processStartTag("body");
            tb.framesetOk(true);
            return tb.process(t);
        }
    },
    InBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            ArrayList<Element> stack;
            int i;
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    break;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    Token startTag = t.asStartTag();
                    name = startTag.normalName();
                    if (!name.equals("a")) {
                        if (!StringUtil.inSorted(name, Constants.InBodyStartEmptyFormatters)) {
                            if (!StringUtil.inSorted(name, Constants.InBodyStartPClosers)) {
                                if (!name.equals("span")) {
                                    Element el;
                                    if (!name.equals("li")) {
                                        Iterator it;
                                        Attribute attribute;
                                        if (!name.equals("html")) {
                                            if (!StringUtil.inSorted(name, Constants.InBodyStartToHead)) {
                                                if (!name.equals("body")) {
                                                    if (!name.equals("frameset")) {
                                                        if (!StringUtil.inSorted(name, Constants.Headings)) {
                                                            if (!StringUtil.inSorted(name, Constants.InBodyStartPreListing)) {
                                                                if (!name.equals("form")) {
                                                                    if (!StringUtil.inSorted(name, Constants.DdDt)) {
                                                                        if (!name.equals("plaintext")) {
                                                                            if (!name.equals("button")) {
                                                                                if (!StringUtil.inSorted(name, Constants.Formatters)) {
                                                                                    if (!name.equals("nobr")) {
                                                                                        if (!StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
                                                                                            if (!name.equals("table")) {
                                                                                                if (!name.equals("input")) {
                                                                                                    if (!StringUtil.inSorted(name, Constants.InBodyStartMedia)) {
                                                                                                        if (!name.equals("hr")) {
                                                                                                            if (!name.equals("image")) {
                                                                                                                if (!name.equals("isindex")) {
                                                                                                                    if (!name.equals("textarea")) {
                                                                                                                        if (!name.equals("xmp")) {
                                                                                                                            if (!name.equals("iframe")) {
                                                                                                                                if (!name.equals("noembed")) {
                                                                                                                                    if (!name.equals("select")) {
                                                                                                                                        if (!StringUtil.inSorted(name, Constants.InBodyStartOptions)) {
                                                                                                                                            if (!StringUtil.inSorted(name, Constants.InBodyStartRuby)) {
                                                                                                                                                if (!name.equals("math")) {
                                                                                                                                                    if (!name.equals("svg")) {
                                                                                                                                                        if (!StringUtil.inSorted(name, Constants.InBodyStartDrop)) {
                                                                                                                                                            tb.reconstructFormattingElements();
                                                                                                                                                            tb.insert((StartTag) startTag);
                                                                                                                                                            break;
                                                                                                                                                        }
                                                                                                                                                        tb.error(this);
                                                                                                                                                        return false;
                                                                                                                                                    }
                                                                                                                                                    tb.reconstructFormattingElements();
                                                                                                                                                    tb.insert((StartTag) startTag);
                                                                                                                                                    tb.tokeniser.acknowledgeSelfClosingFlag();
                                                                                                                                                    break;
                                                                                                                                                }
                                                                                                                                                tb.reconstructFormattingElements();
                                                                                                                                                tb.insert((StartTag) startTag);
                                                                                                                                                tb.tokeniser.acknowledgeSelfClosingFlag();
                                                                                                                                                break;
                                                                                                                                            } else if (tb.inScope("ruby")) {
                                                                                                                                                tb.generateImpliedEndTags();
                                                                                                                                                if (!tb.currentElement().nodeName().equals("ruby")) {
                                                                                                                                                    tb.error(this);
                                                                                                                                                    tb.popStackToBefore("ruby");
                                                                                                                                                }
                                                                                                                                                tb.insert((StartTag) startTag);
                                                                                                                                                break;
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                        if (tb.currentElement().nodeName().equals("option")) {
                                                                                                                                            tb.processEndTag("option");
                                                                                                                                        }
                                                                                                                                        tb.reconstructFormattingElements();
                                                                                                                                        tb.insert((StartTag) startTag);
                                                                                                                                        break;
                                                                                                                                    }
                                                                                                                                    tb.reconstructFormattingElements();
                                                                                                                                    tb.insert((StartTag) startTag);
                                                                                                                                    tb.framesetOk(false);
                                                                                                                                    HtmlTreeBuilderState state = tb.state();
                                                                                                                                    if (!state.equals(InTable) && !state.equals(InCaption) && !state.equals(InTableBody) && !state.equals(InRow) && !state.equals(InCell)) {
                                                                                                                                        tb.transition(InSelect);
                                                                                                                                        break;
                                                                                                                                    }
                                                                                                                                    tb.transition(InSelectInTable);
                                                                                                                                    break;
                                                                                                                                }
                                                                                                                                HtmlTreeBuilderState.handleRawtext(startTag, tb);
                                                                                                                                break;
                                                                                                                            }
                                                                                                                            tb.framesetOk(false);
                                                                                                                            HtmlTreeBuilderState.handleRawtext(startTag, tb);
                                                                                                                            break;
                                                                                                                        }
                                                                                                                        if (tb.inButtonScope("p")) {
                                                                                                                            tb.processEndTag("p");
                                                                                                                        }
                                                                                                                        tb.reconstructFormattingElements();
                                                                                                                        tb.framesetOk(false);
                                                                                                                        HtmlTreeBuilderState.handleRawtext(startTag, tb);
                                                                                                                        break;
                                                                                                                    }
                                                                                                                    tb.insert((StartTag) startTag);
                                                                                                                    tb.tokeniser.transition(TokeniserState.Rcdata);
                                                                                                                    tb.markInsertionMode();
                                                                                                                    tb.framesetOk(false);
                                                                                                                    tb.transition(Text);
                                                                                                                    break;
                                                                                                                }
                                                                                                                tb.error(this);
                                                                                                                if (tb.getFormElement() == null) {
                                                                                                                    tb.tokeniser.acknowledgeSelfClosingFlag();
                                                                                                                    tb.processStartTag("form");
                                                                                                                    if (startTag.attributes.hasKey("action")) {
                                                                                                                        tb.getFormElement().attr("action", startTag.attributes.get("action"));
                                                                                                                    }
                                                                                                                    tb.processStartTag("hr");
                                                                                                                    tb.processStartTag("label");
                                                                                                                    tb.process(new Character().data(startTag.attributes.hasKey("prompt") ? startTag.attributes.get("prompt") : "This is a searchable index. Enter search keywords: "));
                                                                                                                    Attributes inputAttribs = new Attributes();
                                                                                                                    it = startTag.attributes.iterator();
                                                                                                                    while (it.hasNext()) {
                                                                                                                        Attribute attr = (Attribute) it.next();
                                                                                                                        if (!StringUtil.inSorted(attr.getKey(), Constants.InBodyStartInputAttribs)) {
                                                                                                                            inputAttribs.put(attr);
                                                                                                                        }
                                                                                                                    }
                                                                                                                    inputAttribs.put("name", "isindex");
                                                                                                                    tb.processStartTag("input", inputAttribs);
                                                                                                                    tb.processEndTag("label");
                                                                                                                    tb.processStartTag("hr");
                                                                                                                    tb.processEndTag("form");
                                                                                                                    break;
                                                                                                                }
                                                                                                                return false;
                                                                                                            } else if (tb.getFromStack("svg") != null) {
                                                                                                                tb.insert((StartTag) startTag);
                                                                                                                break;
                                                                                                            } else {
                                                                                                                return tb.process(startTag.name("img"));
                                                                                                            }
                                                                                                        }
                                                                                                        if (tb.inButtonScope("p")) {
                                                                                                            tb.processEndTag("p");
                                                                                                        }
                                                                                                        tb.insertEmpty(startTag);
                                                                                                        tb.framesetOk(false);
                                                                                                        break;
                                                                                                    }
                                                                                                    tb.insertEmpty(startTag);
                                                                                                    break;
                                                                                                }
                                                                                                tb.reconstructFormattingElements();
                                                                                                if (!tb.insertEmpty(startTag).attr("type").equalsIgnoreCase("hidden")) {
                                                                                                    tb.framesetOk(false);
                                                                                                    break;
                                                                                                }
                                                                                            }
                                                                                            if (tb.getDocument().quirksMode() != QuirksMode.quirks && tb.inButtonScope("p")) {
                                                                                                tb.processEndTag("p");
                                                                                            }
                                                                                            tb.insert((StartTag) startTag);
                                                                                            tb.framesetOk(false);
                                                                                            tb.transition(InTable);
                                                                                            break;
                                                                                        }
                                                                                        tb.reconstructFormattingElements();
                                                                                        tb.insert((StartTag) startTag);
                                                                                        tb.insertMarkerToFormattingElements();
                                                                                        tb.framesetOk(false);
                                                                                        break;
                                                                                    }
                                                                                    tb.reconstructFormattingElements();
                                                                                    if (tb.inScope("nobr")) {
                                                                                        tb.error(this);
                                                                                        tb.processEndTag("nobr");
                                                                                        tb.reconstructFormattingElements();
                                                                                    }
                                                                                    tb.pushActiveFormattingElements(tb.insert((StartTag) startTag));
                                                                                    break;
                                                                                }
                                                                                tb.reconstructFormattingElements();
                                                                                tb.pushActiveFormattingElements(tb.insert((StartTag) startTag));
                                                                                break;
                                                                            } else if (!tb.inButtonScope("button")) {
                                                                                tb.reconstructFormattingElements();
                                                                                tb.insert((StartTag) startTag);
                                                                                tb.framesetOk(false);
                                                                                break;
                                                                            } else {
                                                                                tb.error(this);
                                                                                tb.processEndTag("button");
                                                                                tb.process(startTag);
                                                                                break;
                                                                            }
                                                                        }
                                                                        if (tb.inButtonScope("p")) {
                                                                            tb.processEndTag("p");
                                                                        }
                                                                        tb.insert((StartTag) startTag);
                                                                        tb.tokeniser.transition(TokeniserState.PLAINTEXT);
                                                                        break;
                                                                    }
                                                                    tb.framesetOk(false);
                                                                    stack = tb.getStack();
                                                                    i = stack.size() - 1;
                                                                    while (i > 0) {
                                                                        el = (Element) stack.get(i);
                                                                        if (StringUtil.inSorted(el.nodeName(), Constants.DdDt)) {
                                                                            tb.processEndTag(el.nodeName());
                                                                        } else if (!tb.isSpecial(el) || StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
                                                                            i--;
                                                                        }
                                                                        if (tb.inButtonScope("p")) {
                                                                            tb.processEndTag("p");
                                                                        }
                                                                        tb.insert((StartTag) startTag);
                                                                        break;
                                                                    }
                                                                    if (tb.inButtonScope("p")) {
                                                                        tb.processEndTag("p");
                                                                    }
                                                                    tb.insert((StartTag) startTag);
                                                                } else if (tb.getFormElement() == null) {
                                                                    if (tb.inButtonScope("p")) {
                                                                        tb.processEndTag("p");
                                                                    }
                                                                    tb.insertForm(startTag, true);
                                                                    break;
                                                                } else {
                                                                    tb.error(this);
                                                                    return false;
                                                                }
                                                            }
                                                            if (tb.inButtonScope("p")) {
                                                                tb.processEndTag("p");
                                                            }
                                                            tb.insert((StartTag) startTag);
                                                            tb.framesetOk(false);
                                                            break;
                                                        }
                                                        if (tb.inButtonScope("p")) {
                                                            tb.processEndTag("p");
                                                        }
                                                        if (StringUtil.inSorted(tb.currentElement().nodeName(), Constants.Headings)) {
                                                            tb.error(this);
                                                            tb.pop();
                                                        }
                                                        tb.insert((StartTag) startTag);
                                                        break;
                                                    }
                                                    tb.error(this);
                                                    stack = tb.getStack();
                                                    if (stack.size() != 1 && (stack.size() <= 2 || ((Element) stack.get(1)).nodeName().equals("body"))) {
                                                        if (tb.framesetOk()) {
                                                            Element second = (Element) stack.get(1);
                                                            if (second.parent() != null) {
                                                                second.remove();
                                                            }
                                                            while (stack.size() > 1) {
                                                                stack.remove(stack.size() - 1);
                                                            }
                                                            tb.insert((StartTag) startTag);
                                                            tb.transition(InFrameset);
                                                            break;
                                                        }
                                                        return false;
                                                    }
                                                    return false;
                                                }
                                                tb.error(this);
                                                stack = tb.getStack();
                                                if (stack.size() != 1 && (stack.size() <= 2 || ((Element) stack.get(1)).nodeName().equals("body"))) {
                                                    tb.framesetOk(false);
                                                    Element body = (Element) stack.get(1);
                                                    it = startTag.getAttributes().iterator();
                                                    while (it.hasNext()) {
                                                        attribute = (Attribute) it.next();
                                                        if (!body.hasAttr(attribute.getKey())) {
                                                            body.attributes().put(attribute);
                                                        }
                                                    }
                                                    break;
                                                }
                                                return false;
                                            }
                                            return tb.process(t, InHead);
                                        }
                                        tb.error(this);
                                        Element html = (Element) tb.getStack().get(0);
                                        it = startTag.getAttributes().iterator();
                                        while (it.hasNext()) {
                                            attribute = (Attribute) it.next();
                                            if (!html.hasAttr(attribute.getKey())) {
                                                html.attributes().put(attribute);
                                            }
                                        }
                                        break;
                                    }
                                    tb.framesetOk(false);
                                    stack = tb.getStack();
                                    i = stack.size() - 1;
                                    while (i > 0) {
                                        el = (Element) stack.get(i);
                                        if (el.nodeName().equals("li")) {
                                            tb.processEndTag("li");
                                        } else if (!tb.isSpecial(el) || StringUtil.inSorted(el.nodeName(), Constants.InBodyStartLiBreakers)) {
                                            i--;
                                        }
                                        if (tb.inButtonScope("p")) {
                                            tb.processEndTag("p");
                                        }
                                        tb.insert((StartTag) startTag);
                                        break;
                                    }
                                    if (tb.inButtonScope("p")) {
                                        tb.processEndTag("p");
                                    }
                                    tb.insert((StartTag) startTag);
                                } else {
                                    tb.reconstructFormattingElements();
                                    tb.insert((StartTag) startTag);
                                    break;
                                }
                            }
                            if (tb.inButtonScope("p")) {
                                tb.processEndTag("p");
                            }
                            tb.insert((StartTag) startTag);
                            break;
                        }
                        tb.reconstructFormattingElements();
                        tb.insertEmpty(startTag);
                        tb.framesetOk(false);
                        break;
                    }
                    if (tb.getActiveFormattingElement("a") != null) {
                        tb.error(this);
                        tb.processEndTag("a");
                        Element remainingA = tb.getFromStack("a");
                        if (remainingA != null) {
                            tb.removeFromActiveFormattingElements(remainingA);
                            tb.removeFromStack(remainingA);
                        }
                    }
                    tb.reconstructFormattingElements();
                    tb.pushActiveFormattingElements(tb.insert((StartTag) startTag));
                    break;
                    break;
                case EndTag:
                    EndTag endTag = t.asEndTag();
                    name = endTag.normalName();
                    if (StringUtil.inSorted(name, Constants.InBodyEndAdoptionFormatters)) {
                        i = 0;
                        while (i < 8) {
                            Element formatEl = tb.getActiveFormattingElement(name);
                            if (formatEl == null) {
                                return anyOtherEndTag(t, tb);
                            }
                            if (!tb.onStack(formatEl)) {
                                tb.error(this);
                                tb.removeFromActiveFormattingElements(formatEl);
                                return true;
                            } else if (tb.inScope(formatEl.nodeName())) {
                                if (tb.currentElement() != formatEl) {
                                    tb.error(this);
                                }
                                Node furthestBlock = null;
                                Element commonAncestor = null;
                                boolean seenFormattingElement = false;
                                stack = tb.getStack();
                                int stackSize = stack.size();
                                int si = 0;
                                while (si < stackSize && si < 64) {
                                    Node el2 = (Element) stack.get(si);
                                    if (el2 == formatEl) {
                                        commonAncestor = (Element) stack.get(si - 1);
                                        seenFormattingElement = true;
                                    } else if (seenFormattingElement && tb.isSpecial(el2)) {
                                        furthestBlock = el2;
                                    }
                                    si++;
                                }
                                if (furthestBlock == null) {
                                    tb.popStackToClose(formatEl.nodeName());
                                    tb.removeFromActiveFormattingElements(formatEl);
                                    return true;
                                }
                                Element adopter;
                                Element node = furthestBlock;
                                Node lastNode = furthestBlock;
                                for (int j = 0; j < 3; j++) {
                                    if (tb.onStack(node)) {
                                        node = tb.aboveOnStack(node);
                                    }
                                    if (!tb.isInActiveFormattingElements(node)) {
                                        tb.removeFromStack(node);
                                    } else if (node == formatEl) {
                                        if (StringUtil.inSorted(commonAncestor.nodeName(), Constants.InBodyEndTableFosters)) {
                                            if (lastNode.parent() != null) {
                                                lastNode.remove();
                                            }
                                            commonAncestor.appendChild(lastNode);
                                        } else {
                                            if (lastNode.parent() != null) {
                                                lastNode.remove();
                                            }
                                            tb.insertInFosterParent(lastNode);
                                        }
                                        adopter = new Element(formatEl.tag(), tb.getBaseUri());
                                        adopter.attributes().addAll(formatEl.attributes());
                                        for (Node childNode : (Node[]) furthestBlock.childNodes().toArray(new Node[furthestBlock.childNodeSize()])) {
                                            adopter.appendChild(childNode);
                                        }
                                        furthestBlock.appendChild(adopter);
                                        tb.removeFromActiveFormattingElements(formatEl);
                                        tb.removeFromStack(formatEl);
                                        tb.insertOnStackAfter(furthestBlock, adopter);
                                        i++;
                                    } else {
                                        Element element = new Element(Tag.valueOf(node.nodeName(), ParseSettings.preserveCase), tb.getBaseUri());
                                        tb.replaceActiveFormattingElement(node, element);
                                        tb.replaceOnStack(node, element);
                                        Node node2 = element;
                                        if (lastNode == furthestBlock) {
                                        }
                                        if (lastNode.parent() != null) {
                                            lastNode.remove();
                                        }
                                        node2.appendChild(lastNode);
                                        lastNode = node2;
                                    }
                                }
                                if (StringUtil.inSorted(commonAncestor.nodeName(), Constants.InBodyEndTableFosters)) {
                                    if (lastNode.parent() != null) {
                                        lastNode.remove();
                                    }
                                    commonAncestor.appendChild(lastNode);
                                } else {
                                    if (lastNode.parent() != null) {
                                        lastNode.remove();
                                    }
                                    tb.insertInFosterParent(lastNode);
                                }
                                adopter = new Element(formatEl.tag(), tb.getBaseUri());
                                adopter.attributes().addAll(formatEl.attributes());
                                while (r36 < r37) {
                                    adopter.appendChild(childNode);
                                }
                                furthestBlock.appendChild(adopter);
                                tb.removeFromActiveFormattingElements(formatEl);
                                tb.removeFromStack(formatEl);
                                tb.insertOnStackAfter(furthestBlock, adopter);
                                i++;
                            } else {
                                tb.error(this);
                                return false;
                            }
                        }
                        break;
                    } else if (StringUtil.inSorted(name, Constants.InBodyEndClosers)) {
                        if (tb.inScope(name)) {
                            tb.generateImpliedEndTags();
                            if (!tb.currentElement().nodeName().equals(name)) {
                                tb.error(this);
                            }
                            tb.popStackToClose(name);
                            break;
                        }
                        tb.error(this);
                        return false;
                    } else if (name.equals("span")) {
                        return anyOtherEndTag(t, tb);
                    } else {
                        if (name.equals("li")) {
                            if (tb.inListItemScope(name)) {
                                tb.generateImpliedEndTags(name);
                                if (!tb.currentElement().nodeName().equals(name)) {
                                    tb.error(this);
                                }
                                tb.popStackToClose(name);
                                break;
                            }
                            tb.error(this);
                            return false;
                        } else if (name.equals("body")) {
                            if (tb.inScope("body")) {
                                tb.transition(AfterBody);
                                break;
                            }
                            tb.error(this);
                            return false;
                        } else if (name.equals("html")) {
                            if (tb.processEndTag("body")) {
                                return tb.process(endTag);
                            }
                        } else if (name.equals("form")) {
                            Element currentForm = tb.getFormElement();
                            tb.setFormElement(null);
                            if (currentForm != null && tb.inScope(name)) {
                                tb.generateImpliedEndTags();
                                if (!tb.currentElement().nodeName().equals(name)) {
                                    tb.error(this);
                                }
                                tb.removeFromStack(currentForm);
                                break;
                            }
                            tb.error(this);
                            return false;
                        } else if (name.equals("p")) {
                            if (tb.inButtonScope(name)) {
                                tb.generateImpliedEndTags(name);
                                if (!tb.currentElement().nodeName().equals(name)) {
                                    tb.error(this);
                                }
                                tb.popStackToClose(name);
                                break;
                            }
                            tb.error(this);
                            tb.processStartTag(name);
                            return tb.process(endTag);
                        } else if (StringUtil.inSorted(name, Constants.DdDt)) {
                            if (tb.inScope(name)) {
                                tb.generateImpliedEndTags(name);
                                if (!tb.currentElement().nodeName().equals(name)) {
                                    tb.error(this);
                                }
                                tb.popStackToClose(name);
                                break;
                            }
                            tb.error(this);
                            return false;
                        } else if (StringUtil.inSorted(name, Constants.Headings)) {
                            if (tb.inScope(Constants.Headings)) {
                                tb.generateImpliedEndTags(name);
                                if (!tb.currentElement().nodeName().equals(name)) {
                                    tb.error(this);
                                }
                                tb.popStackToClose(Constants.Headings);
                                break;
                            }
                            tb.error(this);
                            return false;
                        } else if (name.equals("sarcasm")) {
                            return anyOtherEndTag(t, tb);
                        } else {
                            if (StringUtil.inSorted(name, Constants.InBodyStartApplets)) {
                                if (!tb.inScope("name")) {
                                    if (tb.inScope(name)) {
                                        tb.generateImpliedEndTags();
                                        if (!tb.currentElement().nodeName().equals(name)) {
                                            tb.error(this);
                                        }
                                        tb.popStackToClose(name);
                                        tb.clearFormattingElementsToLastMarker();
                                        break;
                                    }
                                    tb.error(this);
                                    return false;
                                }
                            } else if (!name.equals("br")) {
                                return anyOtherEndTag(t, tb);
                            } else {
                                tb.error(this);
                                tb.processStartTag("br");
                                return false;
                            }
                        }
                    }
                    break;
                case Character:
                    Character c = t.asCharacter();
                    if (!c.getData().equals(HtmlTreeBuilderState.nullString)) {
                        if (!tb.framesetOk() || !HtmlTreeBuilderState.isWhitespace((Token) c)) {
                            tb.reconstructFormattingElements();
                            tb.insert(c);
                            tb.framesetOk(false);
                            break;
                        }
                        tb.reconstructFormattingElements();
                        tb.insert(c);
                        break;
                    }
                    tb.error(this);
                    return false;
                    break;
            }
            return true;
        }

        boolean anyOtherEndTag(Token t, HtmlTreeBuilder tb) {
            String name = t.asEndTag().normalName();
            ArrayList<Element> stack = tb.getStack();
            int pos = stack.size() - 1;
            while (pos >= 0) {
                Element node = (Element) stack.get(pos);
                if (node.nodeName().equals(name)) {
                    tb.generateImpliedEndTags(name);
                    if (!name.equals(tb.currentElement().nodeName())) {
                        tb.error(this);
                    }
                    tb.popStackToClose(name);
                    return true;
                } else if (tb.isSpecial(node)) {
                    tb.error(this);
                    return false;
                } else {
                    pos--;
                }
            }
            return true;
        }
    },
    Text {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.insert(t.asCharacter());
            } else if (t.isEOF()) {
                tb.error(this);
                tb.pop();
                tb.transition(tb.originalState());
                return tb.process(t);
            } else if (t.isEndTag()) {
                tb.pop();
                tb.transition(tb.originalState());
            }
            return true;
        }
    },
    InTable {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isCharacter()) {
                tb.newPendingTableCharacters();
                tb.markInsertionMode();
                tb.transition(InTableText);
                return tb.process(t);
            } else if (t.isComment()) {
                tb.insert(t.asComment());
                return true;
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                StartTag startTag = t.asStartTag();
                name = startTag.normalName();
                if (name.equals("caption")) {
                    tb.clearStackToTableContext();
                    tb.insertMarkerToFormattingElements();
                    tb.insert(startTag);
                    tb.transition(InCaption);
                    return true;
                } else if (name.equals("colgroup")) {
                    tb.clearStackToTableContext();
                    tb.insert(startTag);
                    tb.transition(InColumnGroup);
                    return true;
                } else if (name.equals("col")) {
                    tb.processStartTag("colgroup");
                    return tb.process(t);
                } else {
                    if (StringUtil.in(name, "tbody", "tfoot", "thead")) {
                        tb.clearStackToTableContext();
                        tb.insert(startTag);
                        tb.transition(InTableBody);
                        return true;
                    }
                    if (StringUtil.in(name, "td", "th", "tr")) {
                        tb.processStartTag("tbody");
                        return tb.process(t);
                    } else if (name.equals("table")) {
                        tb.error(this);
                        if (tb.processEndTag("table")) {
                            return tb.process(t);
                        }
                        return true;
                    } else {
                        if (StringUtil.in(name, "style", "script")) {
                            return tb.process(t, InHead);
                        }
                        if (name.equals("input")) {
                            if (!startTag.attributes.get("type").equalsIgnoreCase("hidden")) {
                                return anythingElse(t, tb);
                            }
                            tb.insertEmpty(startTag);
                            return true;
                        } else if (!name.equals("form")) {
                            return anythingElse(t, tb);
                        } else {
                            tb.error(this);
                            if (tb.getFormElement() != null) {
                                return false;
                            }
                            tb.insertForm(startTag, false);
                            return true;
                        }
                    }
                }
            } else if (t.isEndTag()) {
                name = t.asEndTag().normalName();
                if (!name.equals("table")) {
                    if (!StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                        return anythingElse(t, tb);
                    }
                    tb.error(this);
                    return false;
                } else if (tb.inTableScope(name)) {
                    tb.popStackToClose("table");
                    tb.resetInsertionMode();
                    return true;
                } else {
                    tb.error(this);
                    return false;
                }
            } else if (!t.isEOF()) {
                return anythingElse(t, tb);
            } else {
                if (!tb.currentElement().nodeName().equals("html")) {
                    return true;
                }
                tb.error(this);
                return true;
            }
        }

        boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            if (!StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                return tb.process(t, InBody);
            }
            tb.setFosterInserts(true);
            boolean processed = tb.process(t, InBody);
            tb.setFosterInserts(false);
            return processed;
        }
    },
    InTableText {
        boolean process(Token t, HtmlTreeBuilder tb) {
            switch (t.type) {
                case Character:
                    Character c = t.asCharacter();
                    if (c.getData().equals(HtmlTreeBuilderState.nullString)) {
                        tb.error(this);
                        return false;
                    }
                    tb.getPendingTableCharacters().add(c.getData());
                    return true;
                default:
                    if (tb.getPendingTableCharacters().size() > 0) {
                        for (String character : tb.getPendingTableCharacters()) {
                            if (HtmlTreeBuilderState.isWhitespace(character)) {
                                tb.insert(new Character().data(character));
                            } else {
                                tb.error(this);
                                if (StringUtil.in(tb.currentElement().nodeName(), "table", "tbody", "tfoot", "thead", "tr")) {
                                    tb.setFosterInserts(true);
                                    tb.process(new Character().data(character), InBody);
                                    tb.setFosterInserts(false);
                                } else {
                                    tb.process(new Character().data(character), InBody);
                                }
                            }
                        }
                        tb.newPendingTableCharacters();
                    }
                    tb.transition(tb.originalState());
                    return tb.process(t);
            }
        }
    },
    InCaption {
        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (!t.isEndTag() || !t.asEndTag().normalName().equals("caption")) {
                if (t.isStartTag()) {
                }
                if (!(t.isEndTag() && t.asEndTag().normalName().equals("table"))) {
                    if (t.isEndTag()) {
                        if (StringUtil.in(t.asEndTag().normalName(), "body", "col", "colgroup", "html", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                            tb.error(this);
                            return false;
                        }
                    }
                    return tb.process(t, InBody);
                }
                tb.error(this);
                if (tb.processEndTag("caption")) {
                    return tb.process(t);
                }
            } else if (tb.inTableScope(t.asEndTag().normalName())) {
                tb.generateImpliedEndTags();
                if (!tb.currentElement().nodeName().equals("caption")) {
                    tb.error(this);
                }
                tb.popStackToClose("caption");
                tb.clearFormattingElementsToLastMarker();
                tb.transition(InTable);
            } else {
                tb.error(this);
                return false;
            }
            return true;
        }
    },
    InColumnGroup {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
                return true;
            }
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    return true;
                case Doctype:
                    tb.error(this);
                    return true;
                case StartTag:
                    StartTag startTag = t.asStartTag();
                    String name = startTag.normalName();
                    if (name.equals("html")) {
                        return tb.process(t, InBody);
                    }
                    if (!name.equals("col")) {
                        return anythingElse(t, tb);
                    }
                    tb.insertEmpty(startTag);
                    return true;
                case EndTag:
                    if (!t.asEndTag().normalName().equals("colgroup")) {
                        return anythingElse(t, tb);
                    }
                    if (tb.currentElement().nodeName().equals("html")) {
                        tb.error(this);
                        return false;
                    }
                    tb.pop();
                    tb.transition(InTable);
                    return true;
                case EOF:
                    if (tb.currentElement().nodeName().equals("html")) {
                        return true;
                    }
                    return anythingElse(t, tb);
                default:
                    return anythingElse(t, tb);
            }
        }

        private boolean anythingElse(Token t, TreeBuilder tb) {
            if (tb.processEndTag("colgroup")) {
                return tb.process(t);
            }
            return true;
        }
    },
    InTableBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            switch (t.type) {
                case StartTag:
                    StartTag startTag = t.asStartTag();
                    name = startTag.normalName();
                    if (name.equals("tr")) {
                        tb.clearStackToTableBodyContext();
                        tb.insert(startTag);
                        tb.transition(InRow);
                        break;
                    }
                    if (StringUtil.in(name, "th", "td")) {
                        tb.error(this);
                        tb.processStartTag("tr");
                        return tb.process(startTag);
                    }
                    if (StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead")) {
                        return exitTableBody(t, tb);
                    }
                    return anythingElse(t, tb);
                case EndTag:
                    name = t.asEndTag().normalName();
                    if (StringUtil.in(name, "tbody", "tfoot", "thead")) {
                        if (tb.inTableScope(name)) {
                            tb.clearStackToTableBodyContext();
                            tb.pop();
                            tb.transition(InTable);
                            break;
                        }
                        tb.error(this);
                        return false;
                    } else if (name.equals("table")) {
                        return exitTableBody(t, tb);
                    } else {
                        if (!StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "td", "th", "tr")) {
                            return anythingElse(t, tb);
                        }
                        tb.error(this);
                        return false;
                    }
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean exitTableBody(Token t, HtmlTreeBuilder tb) {
            if (tb.inTableScope("tbody") || tb.inTableScope("thead") || tb.inScope("tfoot")) {
                tb.clearStackToTableBodyContext();
                tb.processEndTag(tb.currentElement().nodeName());
                return tb.process(t);
            }
            tb.error(this);
            return false;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }
    },
    InRow {
        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            if (t.isStartTag()) {
                StartTag startTag = t.asStartTag();
                name = startTag.normalName();
                if (StringUtil.in(name, "th", "td")) {
                    tb.clearStackToTableRowContext();
                    tb.insert(startTag);
                    tb.transition(InCell);
                    tb.insertMarkerToFormattingElements();
                } else {
                    if (StringUtil.in(name, "caption", "col", "colgroup", "tbody", "tfoot", "thead", "tr")) {
                        return handleMissingTr(t, tb);
                    }
                    return anythingElse(t, tb);
                }
            } else if (!t.isEndTag()) {
                return anythingElse(t, tb);
            } else {
                name = t.asEndTag().normalName();
                if (name.equals("tr")) {
                    if (tb.inTableScope(name)) {
                        tb.clearStackToTableRowContext();
                        tb.pop();
                        tb.transition(InTableBody);
                    } else {
                        tb.error(this);
                        return false;
                    }
                } else if (name.equals("table")) {
                    return handleMissingTr(t, tb);
                } else {
                    if (!StringUtil.in(name, "tbody", "tfoot", "thead")) {
                        if (!StringUtil.in(name, "body", "caption", "col", "colgroup", "html", "td", "th")) {
                            return anythingElse(t, tb);
                        }
                        tb.error(this);
                        return false;
                    } else if (tb.inTableScope(name)) {
                        tb.processEndTag("tr");
                        return tb.process(t);
                    } else {
                        tb.error(this);
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InTable);
        }

        private boolean handleMissingTr(Token t, TreeBuilder tb) {
            if (tb.processEndTag("tr")) {
                return tb.process(t);
            }
            return false;
        }
    },
    InCell {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isEndTag()) {
                String name = t.asEndTag().normalName();
                if (!StringUtil.in(name, "td", "th")) {
                    if (StringUtil.in(name, "body", "caption", "col", "colgroup", "html")) {
                        tb.error(this);
                        return false;
                    }
                    if (!StringUtil.in(name, "table", "tbody", "tfoot", "thead", "tr")) {
                        return anythingElse(t, tb);
                    }
                    if (tb.inTableScope(name)) {
                        closeCell(tb);
                        return tb.process(t);
                    }
                    tb.error(this);
                    return false;
                } else if (tb.inTableScope(name)) {
                    tb.generateImpliedEndTags();
                    if (!tb.currentElement().nodeName().equals(name)) {
                        tb.error(this);
                    }
                    tb.popStackToClose(name);
                    tb.clearFormattingElementsToLastMarker();
                    tb.transition(InRow);
                    return true;
                } else {
                    tb.error(this);
                    tb.transition(InRow);
                    return false;
                }
            }
            if (t.isStartTag()) {
                if (StringUtil.in(t.asStartTag().normalName(), "caption", "col", "colgroup", "tbody", "td", "tfoot", "th", "thead", "tr")) {
                    if (tb.inTableScope("td") || tb.inTableScope("th")) {
                        closeCell(tb);
                        return tb.process(t);
                    }
                    tb.error(this);
                    return false;
                }
            }
            return anythingElse(t, tb);
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            return tb.process(t, InBody);
        }

        private void closeCell(HtmlTreeBuilder tb) {
            if (tb.inTableScope("td")) {
                tb.processEndTag("td");
            } else {
                tb.processEndTag("th");
            }
        }
    },
    InSelect {
        boolean process(Token t, HtmlTreeBuilder tb) {
            String name;
            switch (t.type) {
                case Comment:
                    tb.insert(t.asComment());
                    break;
                case Doctype:
                    tb.error(this);
                    return false;
                case StartTag:
                    StartTag start = t.asStartTag();
                    name = start.normalName();
                    if (name.equals("html")) {
                        return tb.process(start, InBody);
                    }
                    if (name.equals("option")) {
                        tb.processEndTag("option");
                        tb.insert(start);
                        break;
                    } else if (name.equals("optgroup")) {
                        if (tb.currentElement().nodeName().equals("option")) {
                            tb.processEndTag("option");
                        } else if (tb.currentElement().nodeName().equals("optgroup")) {
                            tb.processEndTag("optgroup");
                        }
                        tb.insert(start);
                        break;
                    } else if (name.equals("select")) {
                        tb.error(this);
                        return tb.processEndTag("select");
                    } else {
                        if (StringUtil.in(name, "input", "keygen", "textarea")) {
                            tb.error(this);
                            if (!tb.inSelectScope("select")) {
                                return false;
                            }
                            tb.processEndTag("select");
                            return tb.process(start);
                        } else if (name.equals("script")) {
                            return tb.process(t, InHead);
                        } else {
                            return anythingElse(t, tb);
                        }
                    }
                case EndTag:
                    name = t.asEndTag().normalName();
                    if (name.equals("optgroup")) {
                        if (tb.currentElement().nodeName().equals("option") && tb.aboveOnStack(tb.currentElement()) != null && tb.aboveOnStack(tb.currentElement()).nodeName().equals("optgroup")) {
                            tb.processEndTag("option");
                        }
                        if (!tb.currentElement().nodeName().equals("optgroup")) {
                            tb.error(this);
                            break;
                        }
                        tb.pop();
                        break;
                    } else if (name.equals("option")) {
                        if (!tb.currentElement().nodeName().equals("option")) {
                            tb.error(this);
                            break;
                        }
                        tb.pop();
                        break;
                    } else if (name.equals("select")) {
                        if (tb.inSelectScope(name)) {
                            tb.popStackToClose(name);
                            tb.resetInsertionMode();
                            break;
                        }
                        tb.error(this);
                        return false;
                    } else {
                        return anythingElse(t, tb);
                    }
                case Character:
                    Character c = t.asCharacter();
                    if (!c.getData().equals(HtmlTreeBuilderState.nullString)) {
                        tb.insert(c);
                        break;
                    }
                    tb.error(this);
                    return false;
                case EOF:
                    if (!tb.currentElement().nodeName().equals("html")) {
                        tb.error(this);
                        break;
                    }
                    break;
                default:
                    return anythingElse(t, tb);
            }
            return true;
        }

        private boolean anythingElse(Token t, HtmlTreeBuilder tb) {
            tb.error(this);
            return false;
        }
    },
    InSelectInTable {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isStartTag()) {
                if (StringUtil.in(t.asStartTag().normalName(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                    tb.error(this);
                    tb.processEndTag("select");
                    return tb.process(t);
                }
            }
            if (t.isEndTag()) {
                if (StringUtil.in(t.asEndTag().normalName(), "caption", "table", "tbody", "tfoot", "thead", "tr", "td", "th")) {
                    tb.error(this);
                    if (!tb.inTableScope(t.asEndTag().normalName())) {
                        return false;
                    }
                    tb.processEndTag("select");
                    return tb.process(t);
                }
            }
            return tb.process(t, InSelect);
        }
    },
    AfterBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                return tb.process(t, InBody);
            }
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else {
                if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
                    if (tb.isFragmentParsing()) {
                        tb.error(this);
                        return false;
                    }
                    tb.transition(AfterAfterBody);
                } else if (!t.isEOF()) {
                    tb.error(this);
                    tb.transition(InBody);
                    return tb.process(t);
                }
            }
            return true;
        }
    },
    InFrameset {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag()) {
                StartTag start = t.asStartTag();
                String name = start.normalName();
                if (name.equals("html")) {
                    return tb.process(start, InBody);
                }
                if (name.equals("frameset")) {
                    tb.insert(start);
                } else if (name.equals("frame")) {
                    tb.insertEmpty(start);
                } else if (name.equals("noframes")) {
                    return tb.process(start, InHead);
                } else {
                    tb.error(this);
                    return false;
                }
            } else if (t.isEndTag() && t.asEndTag().normalName().equals("frameset")) {
                if (tb.currentElement().nodeName().equals("html")) {
                    tb.error(this);
                    return false;
                }
                tb.pop();
                if (!(tb.isFragmentParsing() || tb.currentElement().nodeName().equals("frameset"))) {
                    tb.transition(AfterFrameset);
                }
            } else if (!t.isEOF()) {
                tb.error(this);
                return false;
            } else if (!tb.currentElement().nodeName().equals("html")) {
                tb.error(this);
                return true;
            }
            return true;
        }
    },
    AfterFrameset {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (HtmlTreeBuilderState.isWhitespace(t)) {
                tb.insert(t.asCharacter());
            } else if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype()) {
                tb.error(this);
                return false;
            } else if (t.isStartTag() && t.asStartTag().normalName().equals("html")) {
                return tb.process(t, InBody);
            } else {
                if (t.isEndTag() && t.asEndTag().normalName().equals("html")) {
                    tb.transition(AfterAfterFrameset);
                } else if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                    return tb.process(t, InHead);
                } else {
                    if (!t.isEOF()) {
                        tb.error(this);
                        return false;
                    }
                }
            }
            return true;
        }
    },
    AfterAfterBody {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype() || HtmlTreeBuilderState.isWhitespace(t) || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
                return tb.process(t, InBody);
            } else {
                if (!t.isEOF()) {
                    tb.error(this);
                    tb.transition(InBody);
                    return tb.process(t);
                }
            }
            return true;
        }
    },
    AfterAfterFrameset {
        boolean process(Token t, HtmlTreeBuilder tb) {
            if (t.isComment()) {
                tb.insert(t.asComment());
            } else if (t.isDoctype() || HtmlTreeBuilderState.isWhitespace(t) || (t.isStartTag() && t.asStartTag().normalName().equals("html"))) {
                return tb.process(t, InBody);
            } else {
                if (!t.isEOF()) {
                    if (t.isStartTag() && t.asStartTag().normalName().equals("noframes")) {
                        return tb.process(t, InHead);
                    }
                    tb.error(this);
                    return false;
                }
            }
            return true;
        }
    },
    ForeignContent {
        boolean process(Token t, HtmlTreeBuilder tb) {
            return true;
        }
    };
    
    private static String nullString;

    private static final class Constants {
        private static final String[] DdDt = null;
        private static final String[] Formatters = null;
        private static final String[] Headings = null;
        private static final String[] InBodyEndAdoptionFormatters = null;
        private static final String[] InBodyEndClosers = null;
        private static final String[] InBodyEndTableFosters = null;
        private static final String[] InBodyStartApplets = null;
        private static final String[] InBodyStartDrop = null;
        private static final String[] InBodyStartEmptyFormatters = null;
        private static final String[] InBodyStartInputAttribs = null;
        private static final String[] InBodyStartLiBreakers = null;
        private static final String[] InBodyStartMedia = null;
        private static final String[] InBodyStartOptions = null;
        private static final String[] InBodyStartPClosers = null;
        private static final String[] InBodyStartPreListing = null;
        private static final String[] InBodyStartRuby = null;
        private static final String[] InBodyStartToHead = null;

        private Constants() {
        }

        static {
            InBodyStartToHead = new String[]{"base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title"};
            InBodyStartPClosers = new String[]{"address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", "summary", "ul"};
            Headings = new String[]{"h1", "h2", "h3", "h4", "h5", "h6"};
            InBodyStartPreListing = new String[]{"pre", "listing"};
            InBodyStartLiBreakers = new String[]{"address", "div", "p"};
            DdDt = new String[]{"dd", "dt"};
            Formatters = new String[]{"b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", "tt", "u"};
            InBodyStartApplets = new String[]{"applet", "marquee", "object"};
            InBodyStartEmptyFormatters = new String[]{"area", "br", "embed", "img", "keygen", "wbr"};
            InBodyStartMedia = new String[]{"param", "source", "track"};
            InBodyStartInputAttribs = new String[]{"name", "action", "prompt"};
            InBodyStartOptions = new String[]{"optgroup", "option"};
            InBodyStartRuby = new String[]{"rp", "rt"};
            InBodyStartDrop = new String[]{"caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr"};
            InBodyEndClosers = new String[]{"address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", "pre", "section", "summary", "ul"};
            InBodyEndAdoptionFormatters = new String[]{"a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u"};
            InBodyEndTableFosters = new String[]{"table", "tbody", "tfoot", "thead", "tr"};
        }
    }

    abstract boolean process(Token token, HtmlTreeBuilder htmlTreeBuilder);

    static {
        nullString = String.valueOf('\u0000');
    }

    private static boolean isWhitespace(Token t) {
        if (t.isCharacter()) {
            return isWhitespace(t.asCharacter().getData());
        }
        return false;
    }

    private static boolean isWhitespace(String data) {
        for (int i = 0; i < data.length(); i++) {
            if (!StringUtil.isWhitespace(data.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static void handleRcData(StartTag startTag, HtmlTreeBuilder tb) {
        tb.insert(startTag);
        tb.tokeniser.transition(TokeniserState.Rcdata);
        tb.markInsertionMode();
        tb.transition(Text);
    }

    private static void handleRawtext(StartTag startTag, HtmlTreeBuilder tb) {
        tb.insert(startTag);
        tb.tokeniser.transition(TokeniserState.Rawtext);
        tb.markInsertionMode();
        tb.transition(Text);
    }
}
