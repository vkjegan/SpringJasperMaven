package net.javaonline.spring.jasper.form;

import org.hibernate.validator.constraints.NotEmpty;


public class JasperInputForm {
	@NotEmpty
	private String noofYears;
	private String rptFmt="Html";

	
	public String getRptFmt() {
		return rptFmt;
	}

	public void setRptFmt(String rptFmt) {
		this.rptFmt = rptFmt;
	}

	public String getNoofYears() {
		return noofYears;
	}

	public void setNoofYears(String noofYears) {
		this.noofYears = noofYears;
	}

	
	}
