package tz.mil.ngome.lms.utils;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public class Configuration {

	private String unit = "DFHQ-AU";
	private List<String> subUnits = Arrays.asList("OT Branch","DI Branch","KU","PD Branch","C Branch","P Branch","IG");
	private int monthlyContribution = 10000;
	
}
