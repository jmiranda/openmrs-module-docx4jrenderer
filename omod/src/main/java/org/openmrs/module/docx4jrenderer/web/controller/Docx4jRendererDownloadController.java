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
package org.openmrs.module.docx4jrenderer.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.docx4jrenderer.api.Docx4jRendererService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The main controller.
 */
@Controller
public class Docx4jRendererDownloadController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/docx4jrenderer/download", method = RequestMethod.GET)
	public void download(HttpServletResponse response) {
		//model.addAttribute("user", Context.getAuthenticatedUser());
		Docx4jRendererService service = Context.getService(Docx4jRendererService.class);
		try {
			File file = service.generateDocument();
			log.info("file: " + file.getAbsolutePath());
			FileInputStream fileInputStream = new FileInputStream(file);
			response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			response.setHeader("Content-Disposition","Attachment;filename=template.docx");
			response.getOutputStream().write(IOUtils.toByteArray(fileInputStream));	
			return;
		} catch (Exception e) { 
			log.error("exception generating docx4j: " + e.getMessage(), e);
		}
	}
}
