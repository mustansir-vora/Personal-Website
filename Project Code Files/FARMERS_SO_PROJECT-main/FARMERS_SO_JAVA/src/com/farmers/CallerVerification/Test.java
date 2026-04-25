package com.farmers.CallerVerification;

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONObject;

public class Test {

    public static void main(String[] args) {
        String jsonString = "{\"people\": [{\"firstName\": \"Alice\", \"lastName\": \"Smith\"}, {\"firstName\": \"Bob\", \"lastName\": \"Johnson\"}, {\"firstName\": \"Charlie\", \"lastName\": \"Brown\"}]}";

        JSONObject obj = new JSONObject(jsonString);
        JSONArray peopleArray = obj.getJSONArray("people");

        StringBuilder formattedNames = new StringBuilder();
        for (int i = 0; i < peopleArray.length(); i++) {
            JSONObject personObj = peopleArray.getJSONObject(i);
            String firstName = personObj.getString("firstName");
            String lastName = personObj.getString("lastName");
            formattedNames.append(firstName).append(" ").append(lastName);

            // Remove the leading space from the separator for all but the first name
            if (i < peopleArray.length() - 1) {
                formattedNames.append("|");
            }
        }

        System.out.println("Formatted names: " + formattedNames.toString());
    }
}
