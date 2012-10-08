/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.docx4jrenderer.api.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.docx4jrenderer.api.Docx4jRendererService;
import org.openmrs.module.docx4jrenderer.api.db.Docx4jRendererDAO;


/**
 * It is a default implementation of {@link Docx4jRendererService}.
 */
public class Docx4jRendererServiceImpl extends BaseOpenmrsService implements Docx4jRendererService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private Docx4jRendererDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(Docx4jRendererDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public Docx4jRendererDAO getDao() {
	    return dao;
    }

	@Override
	public File generateDocument() throws FileNotFoundException, Docx4JException, IOException{
		File template = getFile("template.docx");
		if (template == null) {
			throw new FileNotFoundException("template.docx not found");
		}
		WordprocessingMLPackage wordMLPackage = null;
		try { 
			wordMLPackage = WordprocessingMLPackage.load(template);
		} catch (Docx4JException e) { 
			log.error("Error loading .docx template:", e);
		}
		// 2. Fetch the document part
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

		Document wmlDocumentEl = (Document) documentPart.getJaxbElement();

		//xml --> string
		String xml = XmlUtils.marshaltoString(wmlDocumentEl, true);
		HashMap<String,String> mappings = new HashMap<String, String>();
		
		Patient patient = Context.getPatientService().getPatient(6504);
		log.info("patientId: " + patient.getPatientId());
		log.info("firstName: " + patient.getFamilyName());
		log.info("lastName: " + patient.getGivenName());
		
		mappings.put("patientId", patient.getPatientId().toString());
		mappings.put("firstName", patient.getFamilyName());
		mappings.put("lastName", patient.getGivenName());
		
		DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
		String date = formatter.format(new Date());
		mappings.put("date", date);
		
		//valorize template
		try { 
			Object obj = XmlUtils.unmarshallFromTemplate(xml, mappings);
			
			//change  JaxbElement
			documentPart.setJaxbElement((Document) obj);
		} catch (JAXBException e) { 
			log.error("JAXBException " + e.getMessage(), e);
		}
		
		
		File tempFile = File.createTempFile("example", ".docx");
		log.info("Saving to " + tempFile.getAbsolutePath());
		wordMLPackage.save(tempFile);
		return tempFile;
	}
	
	
	File getFile(String filename) { 
		return new File("/home/jmiranda/git/docx4jrenderer/omod/src/main/resources/template.docx");
	}
	
	
	
	
}