package org.jsoup.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.jsoup.SerializationException;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.parser.Parser;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

public abstract class Node implements Cloneable {
    private static final List<Node> EMPTY_NODES = Collections.emptyList();
    Attributes attributes;
    String baseUri;
    List<Node> childNodes;
    Node parentNode;
    int siblingIndex;

    private static class OuterHtmlVisitor implements NodeVisitor {
        private Appendable accum;
        private OutputSettings out;

        OuterHtmlVisitor(Appendable accum, OutputSettings out) {
            this.accum = accum;
            this.out = out;
        }

        public void head(Node node, int depth) {
            try {
                node.outerHtmlHead(this.accum, depth, this.out);
            } catch (Throwable exception) {
                throw new SerializationException(exception);
            }
        }

        public void tail(Node node, int depth) {
            if (!node.nodeName().equals("#text")) {
                try {
                    node.outerHtmlTail(this.accum, depth, this.out);
                } catch (Throwable exception) {
                    throw new SerializationException(exception);
                }
            }
        }
    }

    public abstract String nodeName();

    abstract void outerHtmlHead(Appendable appendable, int i, OutputSettings outputSettings) throws IOException;

    abstract void outerHtmlTail(Appendable appendable, int i, OutputSettings outputSettings) throws IOException;

    protected Node(String baseUri, Attributes attributes) {
        Validate.notNull(baseUri);
        Validate.notNull(attributes);
        this.childNodes = EMPTY_NODES;
        this.baseUri = baseUri.trim();
        this.attributes = attributes;
    }

    protected Node(String baseUri) {
        this(baseUri, new Attributes());
    }

    protected Node() {
        this.childNodes = EMPTY_NODES;
        this.attributes = null;
    }

    public String attr(String attributeKey) {
        Validate.notNull(attributeKey);
        String val = this.attributes.getIgnoreCase(attributeKey);
        if (val.length() > 0) {
            return val;
        }
        if (attributeKey.toLowerCase().startsWith("abs:")) {
            return absUrl(attributeKey.substring("abs:".length()));
        }
        return "";
    }

    public Attributes attributes() {
        return this.attributes;
    }

    public Node attr(String attributeKey, String attributeValue) {
        this.attributes.put(attributeKey, attributeValue);
        return this;
    }

    public boolean hasAttr(String attributeKey) {
        Validate.notNull(attributeKey);
        if (attributeKey.startsWith("abs:")) {
            String key = attributeKey.substring("abs:".length());
            if (this.attributes.hasKeyIgnoreCase(key) && !absUrl(key).equals("")) {
                return true;
            }
        }
        return this.attributes.hasKeyIgnoreCase(attributeKey);
    }

    public Node removeAttr(String attributeKey) {
        Validate.notNull(attributeKey);
        this.attributes.removeIgnoreCase(attributeKey);
        return this;
    }

    public String baseUri() {
        return this.baseUri;
    }

    public void setBaseUri(final String baseUri) {
        Validate.notNull(baseUri);
        traverse(new NodeVisitor() {
            public void head(Node node, int depth) {
                node.baseUri = baseUri;
            }

            public void tail(Node node, int depth) {
            }
        });
    }

    public String absUrl(String attributeKey) {
        Validate.notEmpty(attributeKey);
        if (hasAttr(attributeKey)) {
            return StringUtil.resolve(this.baseUri, attr(attributeKey));
        }
        return "";
    }

    public Node childNode(int index) {
        return (Node) this.childNodes.get(index);
    }

    public List<Node> childNodes() {
        return Collections.unmodifiableList(this.childNodes);
    }

    public List<Node> childNodesCopy() {
        List<Node> children = new ArrayList(this.childNodes.size());
        for (Node node : this.childNodes) {
            children.add(node.clone());
        }
        return children;
    }

    public final int childNodeSize() {
        return this.childNodes.size();
    }

    protected Node[] childNodesAsArray() {
        return (Node[]) this.childNodes.toArray(new Node[childNodeSize()]);
    }

    public Node parent() {
        return this.parentNode;
    }

    public final Node parentNode() {
        return this.parentNode;
    }

    public Node root() {
        Node node = this;
        while (node.parentNode != null) {
            node = node.parentNode;
        }
        return node;
    }

    public Document ownerDocument() {
        Node root = root();
        return root instanceof Document ? (Document) root : null;
    }

    public void remove() {
        Validate.notNull(this.parentNode);
        this.parentNode.removeChild(this);
    }

    public Node before(String html) {
        addSiblingHtml(this.siblingIndex, html);
        return this;
    }

    public Node before(Node node) {
        Validate.notNull(node);
        Validate.notNull(this.parentNode);
        this.parentNode.addChildren(this.siblingIndex, node);
        return this;
    }

    public Node after(String html) {
        addSiblingHtml(this.siblingIndex + 1, html);
        return this;
    }

    public Node after(Node node) {
        Validate.notNull(node);
        Validate.notNull(this.parentNode);
        this.parentNode.addChildren(this.siblingIndex + 1, node);
        return this;
    }

    private void addSiblingHtml(int index, String html) {
        Validate.notNull(html);
        Validate.notNull(this.parentNode);
        List<Node> nodes = Parser.parseFragment(html, parent() instanceof Element ? (Element) parent() : null, baseUri());
        this.parentNode.addChildren(index, (Node[]) nodes.toArray(new Node[nodes.size()]));
    }

    public Node wrap(String html) {
        Element context;
        Validate.notEmpty(html);
        if (parent() instanceof Element) {
            context = (Element) parent();
        } else {
            context = null;
        }
        List<Node> wrapChildren = Parser.parseFragment(html, context, baseUri());
        Node wrapNode = (Node) wrapChildren.get(0);
        if (wrapNode == null || !(wrapNode instanceof Element)) {
            return null;
        }
        Element wrap = (Element) wrapNode;
        Element deepest = getDeepChild(wrap);
        this.parentNode.replaceChild(this, wrap);
        deepest.addChildren(this);
        if (wrapChildren.size() <= 0) {
            return this;
        }
        for (int i = 0; i < wrapChildren.size(); i++) {
            Node remainder = (Node) wrapChildren.get(i);
            remainder.parentNode.removeChild(remainder);
            wrap.appendChild(remainder);
        }
        return this;
    }

    public Node unwrap() {
        Validate.notNull(this.parentNode);
        Node firstChild = this.childNodes.size() > 0 ? (Node) this.childNodes.get(0) : null;
        this.parentNode.addChildren(this.siblingIndex, childNodesAsArray());
        remove();
        return firstChild;
    }

    private Element getDeepChild(Element el) {
        List<Element> children = el.children();
        if (children.size() > 0) {
            return getDeepChild((Element) children.get(0));
        }
        return el;
    }

    public void replaceWith(Node in) {
        Validate.notNull(in);
        Validate.notNull(this.parentNode);
        this.parentNode.replaceChild(this, in);
    }

    protected void setParentNode(Node parentNode) {
        if (this.parentNode != null) {
            this.parentNode.removeChild(this);
        }
        this.parentNode = parentNode;
    }

    protected void replaceChild(Node out, Node in) {
        Validate.isTrue(out.parentNode == this);
        Validate.notNull(in);
        if (in.parentNode != null) {
            in.parentNode.removeChild(in);
        }
        int index = out.siblingIndex;
        this.childNodes.set(index, in);
        in.parentNode = this;
        in.setSiblingIndex(index);
        out.parentNode = null;
    }

    protected void removeChild(Node out) {
        Validate.isTrue(out.parentNode == this);
        int index = out.siblingIndex;
        this.childNodes.remove(index);
        reindexChildren(index);
        out.parentNode = null;
    }

    protected void addChildren(Node... children) {
        for (Node child : children) {
            reparentChild(child);
            ensureChildNodes();
            this.childNodes.add(child);
            child.setSiblingIndex(this.childNodes.size() - 1);
        }
    }

    protected void addChildren(int index, Node... children) {
        Validate.noNullElements(children);
        ensureChildNodes();
        for (int i = children.length - 1; i >= 0; i--) {
            Node in = children[i];
            reparentChild(in);
            this.childNodes.add(index, in);
            reindexChildren(index);
        }
    }

    protected void ensureChildNodes() {
        if (this.childNodes == EMPTY_NODES) {
            this.childNodes = new ArrayList(4);
        }
    }

    protected void reparentChild(Node child) {
        if (child.parentNode != null) {
            child.parentNode.removeChild(child);
        }
        child.setParentNode(this);
    }

    private void reindexChildren(int start) {
        for (int i = start; i < this.childNodes.size(); i++) {
            ((Node) this.childNodes.get(i)).setSiblingIndex(i);
        }
    }

    public List<Node> siblingNodes() {
        if (this.parentNode == null) {
            return Collections.emptyList();
        }
        List<Node> nodes = this.parentNode.childNodes;
        List<Node> siblings = new ArrayList(nodes.size() - 1);
        for (Node node : nodes) {
            if (node != this) {
                siblings.add(node);
            }
        }
        return siblings;
    }

    public Node nextSibling() {
        if (this.parentNode == null) {
            return null;
        }
        List<Node> siblings = this.parentNode.childNodes;
        int index = this.siblingIndex + 1;
        if (siblings.size() > index) {
            return (Node) siblings.get(index);
        }
        return null;
    }

    public Node previousSibling() {
        if (this.parentNode != null && this.siblingIndex > 0) {
            return (Node) this.parentNode.childNodes.get(this.siblingIndex - 1);
        }
        return null;
    }

    public int siblingIndex() {
        return this.siblingIndex;
    }

    protected void setSiblingIndex(int siblingIndex) {
        this.siblingIndex = siblingIndex;
    }

    public Node traverse(NodeVisitor nodeVisitor) {
        Validate.notNull(nodeVisitor);
        new NodeTraversor(nodeVisitor).traverse(this);
        return this;
    }

    public String outerHtml() {
        StringBuilder accum = new StringBuilder(128);
        outerHtml(accum);
        return accum.toString();
    }

    protected void outerHtml(Appendable accum) {
        new NodeTraversor(new OuterHtmlVisitor(accum, getOutputSettings())).traverse(this);
    }

    OutputSettings getOutputSettings() {
        Document owner = ownerDocument();
        return owner != null ? owner.outputSettings() : new Document("").outputSettings();
    }

    public <T extends Appendable> T html(T appendable) {
        outerHtml(appendable);
        return appendable;
    }

    public String toString() {
        return outerHtml();
    }

    protected void indent(Appendable accum, int depth, OutputSettings out) throws IOException {
        accum.append("\n").append(StringUtil.padding(out.indentAmount() * depth));
    }

    public boolean equals(Object o) {
        return this == o;
    }

    public boolean hasSameValue(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return outerHtml().equals(((Node) o).outerHtml());
    }

    public Node clone() {
        Node thisClone = doClone(null);
        LinkedList<Node> nodesToProcess = new LinkedList();
        nodesToProcess.add(thisClone);
        while (!nodesToProcess.isEmpty()) {
            Node currParent = (Node) nodesToProcess.remove();
            for (int i = 0; i < currParent.childNodes.size(); i++) {
                Node childClone = ((Node) currParent.childNodes.get(i)).doClone(currParent);
                currParent.childNodes.set(i, childClone);
                nodesToProcess.add(childClone);
            }
        }
        return thisClone;
    }

    protected Node doClone(Node parent) {
        try {
            Node clone = (Node) super.clone();
            clone.parentNode = parent;
            clone.siblingIndex = parent == null ? 0 : this.siblingIndex;
            clone.attributes = this.attributes != null ? this.attributes.clone() : null;
            clone.baseUri = this.baseUri;
            clone.childNodes = new ArrayList(this.childNodes.size());
            for (Node child : this.childNodes) {
                clone.childNodes.add(child);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
