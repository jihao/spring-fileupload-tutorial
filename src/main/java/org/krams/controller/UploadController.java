package org.krams.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.krams.domain.Message;
import org.krams.domain.UploadedFile;
import org.krams.response.StatusResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/upload")
public class UploadController {

	private static Logger logger = Logger.getLogger("controller");
	
	@RequestMapping
	public String form() {
		return "form";
	}
	@RequestMapping(value="index")
	public String index(HttpSession session) {
		logger.debug( session.getServletContext().getRealPath("/resources/uuupload/nbia.png"));
		return "index";
	}
	
	@RequestMapping(value="/message", method=RequestMethod.POST)
	public @ResponseBody StatusResponse message(@RequestBody Message message) {
		// Do custom steps here
		// i.e. Persist the message to the database
		logger.debug("Service processing...done");
		
		return new StatusResponse(true, "Message received");
	}
	
	@RequestMapping(value="/file", method=RequestMethod.POST)
	public @ResponseBody List<UploadedFile> upload(
			@RequestParam("file") MultipartFile file) {
		// Do custom steps here
		// i.e. Save the file to a temporary location or database
		logger.debug("Writing file to disk...done");
		List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
		UploadedFile u = new UploadedFile(file.getOriginalFilename(),
				Long.valueOf(file.getSize()).intValue(),
				"http://localhost:8080/spring-fileupload-tutorial/resources/"+file.getOriginalFilename());

		uploadedFiles.add(u);
		return uploadedFiles;
	}
	
	@RequestMapping(value="/multifile", method=RequestMethod.POST)
	public @ResponseBody List<UploadedFile> multifile(
			@RequestParam("files[]") MultipartFile[] files, HttpSession session) {
		// Do custom steps here
		// i.e. Save the file to a temporary location or database
		logger.debug("Writing file to disk...done");
		List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
		for (MultipartFile file : files) {
			storeFile(file, getRealFileStorePath(session.getServletContext(), file.getOriginalFilename()));
			
			UploadedFile u = new UploadedFile(file.getOriginalFilename(),
					Long.valueOf(file.getSize()).intValue(),
					"http://localhost:8080/spring-fileupload-tutorial/resources/uuupload/"+file.getOriginalFilename());

			uploadedFiles.add(u);
		}
		
		return uploadedFiles;
	}
	private void storeFile(MultipartFile file, String destination) {
		try {
			file.transferTo(new File(destination));
		} catch (IllegalStateException e) {
			logger.warn("Failed to store file", e);
		} catch (IOException e) {
			logger.warn("Failed to store file", e);
		}
	}
	private String getRealFileStorePath(ServletContext context, String originalFilename) {
		return context.getRealPath("/resources/uuupload/"+originalFilename);
	}
}