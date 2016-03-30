/**
Author zhong by 2008-5-14
zhongmingyu@msn.com 
**/
package sacred.alliance.magic.util;

import java.io.File;
import java.net.MalformedURLException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XmlUtil {
	
	public static <T> T loadFromXml(String xsdFileName,String xmlFileName,Class<T> clazz) throws Exception {
		//首先验证格式是否正确
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory.newSchema(new File(xsdFileName));
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(xmlFileName));
		
		JAXBContext jc = JAXBContext.newInstance(clazz); 
        Unmarshaller u = jc.createUnmarshaller(); 
        JAXBElement<T> je = (JAXBElement<T>) u.unmarshal(new StreamSource(xmlFileName),clazz); 
        return je.getValue();
	}
	
	public static <T> T loadFromXml(String xmlFileName,Class<T> clazz) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(clazz); 
        Unmarshaller u = jc.createUnmarshaller(); 
        JAXBElement<T> je = (JAXBElement<T>) u.unmarshal(new StreamSource(xmlFileName),clazz); 
        return je.getValue();
	}
	
	public static <T> void writeToXml(String xmlFileName,T entity) throws Exception {
		JAXBContext jc = JAXBContext.newInstance(entity.getClass());
		jc.createMarshaller().marshal(entity, (Result) new File(xmlFileName));
	}
	
	public static String makeUrl(String filename) throws MalformedURLException {
        final String url;
        if (filename.indexOf("://") > 0 || filename.startsWith("file:")) {
            url = filename;
        } else {
            url = (new File(filename)).toURL().toString();
        }
        return url;
    }
    
    public static String getAttributeString(Node node, String attribname) {
        NamedNodeMap attributes = node.getAttributes();
        String att = null;
        if (attributes != null) {
            Node attribute = attributes.getNamedItem(attribname);
            if (attribute != null) {
                att = attribute.getNodeValue();
            }
        }
        return att;
    }

    public static int getAttributeInt(Node node, String attribname, int def) {
        String attr = getAttributeString(node, attribname);
        if (attr != null) {
            return Integer.parseInt(attr);
        } else {
            return def;
        }
    }
    
    public static float getAttributeFloat(Node node, String attribname, float def) {
        String attr = getAttributeString(node, attribname);
        if (attr != null) {
            return Float.parseFloat(attr);
        } else {
            return def;
        }
    }
    
    public static boolean getAttributeBool(Node node, String attribname, boolean def) {
        String attr = getAttributeString(node, attribname);
        if (attr != null) {
            return Boolean.parseBoolean(attr);
        } else {
            return def;
        }
    }
    
    public static byte getAttributeByte(Node node, String attribname, byte def) {
        String attr = getAttributeString(node, attribname);
        if (attr != null) {
            return Byte.parseByte(attr);
        } else {
            return def;
        }
    }
}
