package tz.mil.ngome.lms.utils;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public class Configuration {

	private String unit = "DFHQ-AU";
	private List<String> subUnits = Arrays.asList("HL","OT Branch","DI Branch","KU","PD Branch","C Branch","P Branch","IG");
	private int monthlyContribution = 10000;
	private List<String> ranks = Arrays.asList("CIV","PTE","L/CPL","CPL","SGT","SSGT","WOII","WOI","O/CDT","2LT","A/LT","LT","A/CAPT","CAPT","A/MAJ","MAJ","A/LT COL","LT COL","A/COL","COL","BRIG GEN","MAJ GEN","LT GEN","GEN");
	private int defaultPageSize = 20;
}
