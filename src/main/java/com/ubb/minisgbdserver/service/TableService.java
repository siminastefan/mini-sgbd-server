package com.ubb.minisgbdserver.service;

import com.ubb.minisgbdserver.dto.TableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.Arrays;
import java.util.List;

@Service
public class TableService {

    private DatabaseService databaseService;
    public static final String FILENAME = "files/dbms.xml";

    @Autowired
    public TableService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public boolean createTable(TableDTO tableDTO) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(FILENAME);

        NodeList databases = document.getElementsByTagName("Database");
        for (int index = 0; index < databases.getLength(); index++) {
            String databaseName = databases.item(index).getAttributes().getNamedItem("databaseName").getNodeValue();
            if (databaseName.equals(tableDTO.getDatabaseName())) {

                if (findTable(tableDTO.getDatabaseName(), tableDTO.getTableName()) != null)
                    return false;

                Element newTable = document.createElement("Table");
                newTable.setAttribute("tableName", tableDTO.getTableName());
                Element newStructure = document.createElement("Structure");
                Element newPrimaryKey = document.createElement("PrimaryKey");
                List<String> fields = Arrays.asList(tableDTO.getFields().split(","));
                String pkColumns = tableDTO.getFields().substring(tableDTO.getFields().indexOf("primary key"));
                if (pkColumns.contains("primary key")) {
                    String pks = pkColumns.substring(pkColumns.indexOf("(") + 1, pkColumns.lastIndexOf(")"));
                    for (String col : pks.split(",")) {
                        Element newPrimaryKeyAttribute = document.createElement("pkAttribute");
                        newPrimaryKeyAttribute.setTextContent(col);
                        newPrimaryKey.appendChild(newPrimaryKeyAttribute);
                    }
                }
                newTable.appendChild(newPrimaryKey);
                for (int j = 0; j < fields.size() - 2; j ++) {
                    //TODO: check for unique string to add unique attributes in xml
                    String name = fields.get(j).split(" ")[0];
                    String type = fields.get(j).split(" ")[1];
                    Element newAttribute = document.createElement("Attribute");
                    newAttribute.setAttribute("attributeName", name);
                    newAttribute.setAttribute("type", type);
                    newStructure.appendChild(newAttribute);
                }
                newTable.appendChild(newStructure);
                databases.item(index).appendChild(newTable);

                DOMSource source = new DOMSource(document);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                StreamResult result = new StreamResult(FILENAME);
                transformer.transform(source, result);
                return true;
            }
        }
        return false;
    }

    public Node findTable(String databaseName, String tableName) throws Exception {
        Node database = databaseService.findDatabase(databaseName);
        for (int i = 0; i < database.getChildNodes().getLength(); i++) {
            System.out.println(database.getAttributes().getNamedItem("databaseName").getNodeValue());
            System.out.println(database.getChildNodes().getLength());
            if (database.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (database.getChildNodes().item(i).getAttributes().getNamedItem("tableName").getNodeValue().equals(tableName))
                    return database.getChildNodes().item(i);
            }
        }
        return null;
    }

}
