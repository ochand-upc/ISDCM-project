/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.isdcm.utils;

import com.isdcm.model.Video;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlDocumentBuilder {

    private static final String NS_DIDL = "urn:mpeg:mpeg21:2002:02-DIDL-NS";
    private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String SCHEMA_LOC = "urn:mpeg:mpeg21:2002:02-DIDL-NS didl.xsd";

    /**
     * Construye un Document DIDL a partir de un Video.
     */
    public static Document buildDocument(Video v) throws Exception {
        // 1) Crear DocumentBuilder con soporte a namespaces
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();

        // 2) Crear documento y elemento raíz <DIDL>
        Document doc = db.newDocument();
        Element didl = doc.createElementNS(NS_DIDL, "DIDL");
        didl.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", XSI_NS);
        didl.setAttributeNS(XSI_NS, "xsi:schemaLocation", SCHEMA_LOC);
        doc.appendChild(didl);

        // 3) <Item>
        Element item = doc.createElementNS(NS_DIDL, "Item");
        didl.appendChild(item);

        // 4) <Component>
        Element comp = doc.createElementNS(NS_DIDL, "Component");
        item.appendChild(comp);

        // 5) <Descriptor> → <Statement mimeType="text/xml"> → <metadata>…
        Element descriptor = doc.createElementNS(NS_DIDL, "Descriptor");
        comp.appendChild(descriptor);

        Element statement = doc.createElementNS(NS_DIDL, "Statement");
        statement.setAttribute("mimeType", "text/xml");
        descriptor.appendChild(statement);

        Element metadata = doc.createElement("metadata");
        statement.appendChild(metadata);

        // 6) Campos metadata
        appendTextElement(doc, metadata, "id", String.valueOf(v.getId()));
        appendTextElement(doc, metadata, "titulo", v.getTitulo());
        appendTextElement(doc, metadata, "autor", v.getAutor());
        appendTextElement(doc, metadata, "fecha", v.getFecha());
        appendTextElement(doc, metadata, "duracion", String.valueOf(v.getDuracion()));
        appendTextElement(doc, metadata, "descripcion", v.getDescripcion());

        // 7) <Resource mimeType="…"/>
        Element resource = doc.createElementNS(NS_DIDL, "Resource");
        resource.setAttribute("mimeType", v.getMimeType());
        comp.appendChild(resource);

        return doc;
    }

    /**
     * Helper para crear y añadir un hijo de texto
     */
    private static void appendTextElement(Document doc, Element parent,
            String tagName, String text) {
        Element el = doc.createElement(tagName);
        el.setTextContent(text);
        parent.appendChild(el);
    }

    /**
     * Serializa un Document a String (útil para debugging o para el Response).
     */
    public static String toXmlString(Document doc) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        tf.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }
}
