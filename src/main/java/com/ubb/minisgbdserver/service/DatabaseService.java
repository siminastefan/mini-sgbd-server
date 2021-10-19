package com.ubb.minisgbdserver.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@Service
public class DatabaseService {

    public static final String FILENAME = "files/dbms.xml";

    public void createDatabase(String databaseName) throws Exception{

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(FILENAME);
        Element root = document.getDocumentElement();

        //TODO: check if the database with the given name already exists

        Element newDatabase = document.createElement("Database");
        newDatabase.setAttribute("databaseName", databaseName);


        root.appendChild(newDatabase);

        DOMSource source = new DOMSource(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(FILENAME);
        transformer.transform(source, result);

    }

    public Node findDatabase(String databaseName) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(FILENAME);
        NodeList databases = document.getElementsByTagName("Database");
        for (int index = 0 ; index < databases.getLength(); index++) {
            String name = databases.item(index).getAttributes().getNamedItem("databaseName").getNodeValue();
            if (name.equals(databaseName))
                return databases.item(index);
        }
        return null;
    }

}
