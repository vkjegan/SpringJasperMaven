package net.javaonline.spring.jasper.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.javaonline.spring.jasper.dao.JasperReportDAO;
import net.javaonline.spring.jasper.form.JasperInputForm;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoadJasperReport {

	@ModelAttribute("jasperRptFormats")
	public ArrayList getJasperRptFormats() {
		ArrayList<String> jasperRptFormats = new ArrayList<String>();
		jasperRptFormats.add("Html");
		jasperRptFormats.add("PDF");

		return jasperRptFormats;
	}

	@RequestMapping(value = "/loadJasper", method = RequestMethod.GET)
	public String loadSurveyPg(
			@ModelAttribute("jasperInputForm") JasperInputForm jasperInputForm,
			Model model) {
		model.addAttribute("JasperInputForm", jasperInputForm);

		return "loadJasper";
	}

	@RequestMapping(value = "/generateReport", method = RequestMethod.POST)
	public String generateReport(
			@Valid @ModelAttribute("jasperInputForm") JasperInputForm jasperInputForm,
			BindingResult result, Model model, HttpServletRequest request,
			HttpServletResponse response) throws JRException, IOException,
			NamingException {

		if (result.hasErrors()) {
			System.out.println("validation error occured in jasper input form");
			return "loadJasper";

		}

		String reportFileName = "JREmp1";
		JasperReportDAO jrdao = new JasperReportDAO();

		Connection conn = null;

		try {
			conn = jrdao.getConnection();

			String rptFormat = jasperInputForm.getRptFmt();
			String noy = jasperInputForm.getNoofYears();

			System.out.println("rpt format " + rptFormat);
			System.out.println("no of years " + noy);

			HashMap<String, Object> hmParams = new HashMap<String, Object>();

			hmParams.put("noy", new Integer(noy));

			hmParams.put("Title", "Employees working more than " + noy
					+ " Years");

			JasperReport jasperReport = jrdao.getCompiledFile(reportFileName,
					request);

			if (rptFormat.equalsIgnoreCase("html")) {

				JasperPrint jasperPrint = JasperFillManager.fillReport(
						jasperReport, hmParams, conn);
				jrdao.generateReportHtml(jasperPrint, request, response); // For
																			// HTML
																			// report

			}

			else if (rptFormat.equalsIgnoreCase("pdf")) {

				jrdao.generateReportPDF(response, hmParams, jasperReport, conn); // For
																					// PDF
																					// report

			}

		} catch (SQLException sqlExp) {
			System.out.println("Exception::" + sqlExp.toString());
		} finally {
			if (conn != null) {
				try {
					conn.close();
					conn = null;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		return null;

	}

}