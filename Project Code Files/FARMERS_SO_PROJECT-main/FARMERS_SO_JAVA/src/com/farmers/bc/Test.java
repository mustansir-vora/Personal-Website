package com.farmers.bc;

public class Test {

	public static void main(String[] args) {
		String prompt = " an Auto policy"; 
		 prompt = prompt+" a Home Policy"; 
		 prompt = prompt+" or an Umbrella Policy"; 
		String lang = "SP";
		if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("an auto policy")) {
			prompt = prompt.replace(" an Auto policy", " una póliza de auto");
		}
		if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("a home policy")) {
			prompt = prompt.replace(" a Home Policy", " una póliza de casa");
		}
		if(lang.equalsIgnoreCase("SP") && prompt.toLowerCase().contains("or an umbrella policy")) {
			prompt = prompt.replace(" or an Umbrella Policy"," o una póliza paraguas");
		}
		
		if (prompt.contains(" ")) {
			prompt = prompt.replaceAll(" ", ".");
		} 
				
				System.out.println(prompt);
	}
}
