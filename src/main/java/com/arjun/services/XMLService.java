package com.arjun.services;

import com.arjun.models.Employees;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;

@Service
public class XMLService {
    public Employees parseXML(String xmlData) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Employees.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Employees) unmarshaller.unmarshal(new StringReader(xmlData));
    }


}
