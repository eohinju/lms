package tz.mil.ngome.lms.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.fasterxml.uuid.Logger;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

public class Report {
	public final static String logo = "classpath:reports/logo.png";

	public static ResponseEntity<byte[]> generate(String jrxmlFile, List<?> items,
			Map<String, Object> params) {

		Logger.logInfo("Generating");
		JRDataSource dataSource = new JRBeanCollectionDataSource(items);
		String contentType = "application/pdf";
		byte[] bytes = null;
		JasperReport jasperReport = null;
		JasperPrint jasperPrint = null;
		try {
			if(false && jasperExists(jrxmlFile))
				jasperReport = (JasperReport) JRLoader.loadObject(loadJasperStream(jrxmlFile));
			else {
				Logger.logInfo("Creating jasper");
				InputStream jrxml = loadJrxmlFile(jrxmlFile);
				jasperReport = JasperCompileManager.compileReport(jrxml);
				JRSaver.saveObject(jasperReport, getJasperFile(jrxmlFile));
			}
			jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
			if(jasperPrint!=null)
				bytes = JasperExportManager.exportReportToPdf(jasperPrint);
			else
				Logger.logInfo("Null");
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			Logger.logInfo("Blocked");
			e.printStackTrace();
		}
		
		ContentDisposition contentDisposition = ContentDisposition.builder("inline")
				.filename(LocalDateTime.now() + ".pdf").build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(contentDisposition);
		return ResponseEntity //
				.ok() //
				.header("Content-Type", contentType + "; charset=UTF-8") //
				.headers(headers) //
				.body(bytes);
	}
	
	private static boolean jasperExists(String file) {
		return Files.exists(Paths.get("reports", file+".jasper"));
	}

	private static String getJasperFile(String file) throws IOException {
		Path path = Paths.get("reports");
		try {
			if(!Files.exists(path))
				Files.createDirectories(path);
			path = Paths.get("reports", file+".jasper");
			if(!Files.exists(path))
				Files.createFile(path);
			return path.toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		Logger.logInfo("Hakuna kitu");
		return file;
	}

	private static InputStream loadJasperStream(String file) throws IOException {
		Path path = Paths.get("reports", file+".jasper");
		return new FileInputStream(path.toFile());
	}

	private static InputStream loadJrxmlFile(String file) throws IOException {
		return new ClassPathResource("reports/"+file+".jrxml").getInputStream();
	}

}
