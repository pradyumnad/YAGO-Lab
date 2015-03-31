package com.ibm.cloudoe.samples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Path("/service")
public class HelloResource {

	@GET
	public String getInformation() {

		// 'VCAP_APPLICATION' is in JSON format, it contains useful information
		// about a deployed application
		// String envApp = System.getenv("VCAP_APPLICATION");

		// 'VCAP_SERVICES' contains all the credentials of services bound to
		// this application.
		// String envServices = System.getenv("VCAP_SERVICES");
		// JSONObject sysEnv = new JSONObject(System.getenv());

		return "Hi World!";

	}

	@GET
	@Produces("application/json")
	@Path("/yago")
	public String getYagoservice(@QueryParam("text") String text)
			throws MalformedURLException, IOException, ParseException {
		
		JSONParser parser = new JSONParser();
		String data = "text="+text;

		HttpURLConnection con = (HttpURLConnection) new URL(
				"https://gate.d5.mpi-inf.mpg.de/aida/service/disambiguate")
				.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.getOutputStream().write(data.getBytes("UTF-8"));

		// Get the inputstream
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				con.getInputStream()));

		StringBuffer tmpBfr = new StringBuffer();
		String tmp = "";

		while ((tmp = reader.readLine()) != null) {

			tmpBfr.append(tmp);

			Object obj = parser.parse(tmp);
			JSONObject jsonObject = (JSONObject) obj;
			String originalFileName = (String) jsonObject
					.get("originalFileName");
			String originalText = (String) jsonObject.get("originalText");
			String overallTime = (String) jsonObject.get("overallTime");
			JSONArray mentions = (JSONArray) jsonObject.get("mentions");

			for (int i = 0; i < mentions.size(); i++) {
				// System.out.println(mentions.get(i));
				Object obj2 = parser.parse(mentions.get(i).toString());
				JSONObject jsonObject2 = (JSONObject) obj2;
				JSONArray allEntities = (JSONArray) jsonObject2
						.get("allEntities");

				for (int j = 0; j < allEntities.size(); j++) {
					Object obj3 = parser.parse(allEntities.get(j).toString());
					JSONObject jsonObject3 = (JSONObject) obj3;
					String kdId = (String) jsonObject3.get("kbIdentifier");
					String disambiguationScore = (String) jsonObject3
							.get("disambiguationScore");
					// System.out.println("KbID: " + kdId + "====" + "score: "
					// + disambiguationScore);
				}
			}
		}
		
		return tmpBfr.toString();
	}
	
	public static void main(String args[]) throws MalformedURLException, IOException, ParseException {
		String res = new HelloResource().getYagoservice("Alexander is the greatest emperor.");
		System.out.println(res);
	}
}