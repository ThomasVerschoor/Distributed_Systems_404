package com.company;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.HashMap;

/*

ZET

XML.main(nodes);

MEE in main van uwe main onder uw nodes Mathijs!

*/








public class XML {


        public static void addXML(Element root, Document document, int id, String hash, String ip){


            String hashId = Integer.toString(id);

            //employee element
            Element hashmaps = document.createElement("Available_Hashmaps");

            root.appendChild(hashmaps);


            Attr attr2 = document.createAttribute("id");
            attr2.setValue(hashId);
            hashmaps.setAttributeNode(attr2);

            Element id2 = document.createElement("value");
            id2.appendChild(document.createTextNode(hash));
            hashmaps.appendChild(id2);
            Element ip2 = document.createElement("Ip_Address");
            ip2.appendChild(document.createTextNode(ip));
            hashmaps.appendChild(ip2);

            root.appendChild(hashmaps);
        }

        public static final String xmlFilePath = "C:\\Users\\thoma\\Desktop\\xmlfile.xml";


        public static void main(HashMap <Integer, String> nodes) {

            try {

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.newDocument();

                // root element: nameserver
                Element root = document.createElement("NameServer");
                document.appendChild(root);

                // employee element
                Element hashmaps = document.createElement("Available_Hashmaps");
                root.appendChild(hashmaps);

                int id=1,hash=123456;
                String ip="192.168.1.1";

                //nodes.forEach((key,value) -> System.out.println("k"+ key + "v"+value));
                //nodes.forEach((key,value) -> addXML(root,document,id,key.toString(),value));

                int ctr = 1;
                for (HashMap.Entry node : nodes.entrySet()) {
                    System.out.println("Key: "+node.getKey() + " & Value: " + node.getValue());
                    addXML(root,document,ctr,node.getKey().toString(),node.getValue().toString());
                    ctr = ctr+1;
                }

                //addXML(root,document,id,hash,ip);
                //addXML(root,document,8,120,"120.10");
                //addXML(root,document,3,15120,"150.153.5");

                System.out.println(nodes);
                System.out.println(nodes.values());
                System.out.println(nodes.keySet());

                // create the xml file
                //transform the DOM Object to an XML File
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(new java.io.File(xmlFilePath));
                transformer.transform(domSource, streamResult);

                System.out.println("Done creating XML File");

            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (TransformerException tfe) {
                tfe.printStackTrace();
            }
        }
}

