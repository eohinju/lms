package tz.mil.ngome.lms.utils;

import java.util.UUID;

import com.fasterxml.uuid.Generators;

public class CustomData {

	public static String GenerateUniqueID() {
		UUID uuid = Generators.timeBasedGenerator().generate();
		String uuidStr = uuid.toString().replace("-", "");
		return uuidStr;
	}
	
}
