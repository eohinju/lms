package tz.mil.ngome.lms.dto;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeductionsDto {

	private int compNumber;
	private String name;
	private List<Integer> amounts;

	
	public String toString() {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        return gson.toJson(this);
		return new StringBuilder().append("DeductionsDto{\n").append("\tcompNumber: "+compNumber+",\n").append("\tname: "+name+",\n").append("\tamounts: ")
				.append(amounts).append("\n}").toString();
    }
}
