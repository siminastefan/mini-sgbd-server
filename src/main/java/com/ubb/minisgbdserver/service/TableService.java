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
                Element newForeignKeys = document.createElement("ForeignKeys");

                for (String field : fields) {
                    //TODO: check for unique string to add unique attributes in xml
                    //TODO: check for not null string
                    String name = field.split(" ")[0];
                    String type = field.split(" ")[1];
                    if (field.contains("primary key")) {
                        createPrimaryKey(document, newPrimaryKey, name);
                    } else if (field.contains("references")) {
                        //GroupId int REFERENCES groups (GroupID)
                        Element newForeignKey = createForeignKey(document, field, name);
                        newForeignKeys.appendChild(newForeignKey);
                    }
                    Element newAttribute = document.createElement("Attribute");
                    if (type.contains("char")) {
                        newAttribute.setAttribute("type", "char");
                        newAttribute.setAttribute("length", type.substring(type.indexOf("(") + 1, type.lastIndexOf(")")));
                    } else {
                        newAttribute.setAttribute("type", type);
                    }
                    newAttribute.setAttribute("attributeName", name);
                    newStructure.appendChild(newAttribute);
                }
                newTable.appendChild(newPrimaryKey);
                newTable.appendChild(newStructure);
                newTable.appendChild(newForeignKeys);
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

    private void createPrimaryKey(Document document, Element newPrimaryKey, String name) {
        Element newPrimaryKeyAttribute = document.createElement("pkAttribute");
        newPrimaryKeyAttribute.setTextContent(name);
        newPrimaryKey.appendChild(newPrimaryKeyAttribute);
    }

    private Element createForeignKey(Document document, String field, String name) {
        String table = field.split(" ")[3];
        String col = field.substring(field.indexOf("(") + 1, field.lastIndexOf(")"));
        Element newForeignKey = document.createElement("ForeignKey");
        Element newFqAttribute = document.createElement("fqAttribute");
        newFqAttribute.setTextContent(name);
        Element references = document.createElement("references");
        Element refTable = document.createElement("refTable");
        refTable.setTextContent(table);
        Element refAttribute = document.createElement("refAttribute");
        refAttribute.setTextContent(col);
        references.appendChild(refTable);
        references.appendChild(refAttribute);
        newForeignKey.appendChild(newFqAttribute);
        newForeignKey.appendChild(references);
        return newForeignKey;
    }

    public Node findTable(String databaseName, String tableName) throws Exception {
        Node database = databaseService.findDatabase(databaseName);
        for (int i = 0; i < database.getChildNodes().getLength(); i++) {
            if (database.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                if (database.getChildNodes().item(i).getAttributes().getNamedItem("tableName").getNodeValue().equals(tableName))
                    return database.getChildNodes().item(i);
            }
        }
        return null;
    }

}
