package com.ontariotechu.sofe3980U;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class App {

	public static void main(String[] args) {
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		System.out.println("Running model evaluation...");
		try {
			evaluateModel("model.csv");
		} catch (Exception e) {
			System.err.println("Error occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void evaluateModel(String fileName) throws Exception {
		Reader in = new FileReader(fileName);
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

		System.out.println("Loading file: " + fileName);
		List<Integer> trueValues = new ArrayList<>();
		List<List<Double>> predictedValues = new ArrayList<>();

		if (records.iterator().hasNext()) {
			System.out.println("Data found in file.");
		} else {
			System.out.println("No data found in the file.");
		}


		for (CSVRecord record : records) {
			int actual = Integer.parseInt(record.get("true")); // The actual class
			List<Double> predicted = new ArrayList<>();
			for (int i = 1; i <= 5; i++) { // Assuming 5 classes (columns 1 to 5 are predicted probabilities for each class)
				predicted.add(Double.parseDouble(record.get("predicted_" + i)));
			}
			trueValues.add(actual);
			predictedValues.add(predicted);
		}

		System.out.println("File loaded with " + trueValues.size() + " records.");

		int n = trueValues.size();
		double ce = 0;
		int[][] confusionMatrix = new int[5][5]; // 5 classes, hence a 5x5 confusion matrix

		// Cross-Entropy and Confusion Matrix Calculation
		for (int i = 0; i < n; i++) {
			int y = trueValues.get(i);
			List<Double> y_hat = predictedValues.get(i);

			// Cross-Entropy Calculation
			ce -= Math.log(y_hat.get(y - 1)); // y-1 since the classes are 1-indexed

			// Predicted class (choose the class with highest probability)
			int predictedClass = maxIndex(y_hat) + 1; // Add 1 for 1-indexed classes

			// Confusion Matrix
			confusionMatrix[y - 1][predictedClass - 1]++;
		}

		ce /= n;

		// Printing Results
		System.out.println("For " + fileName);
		System.out.printf("\tCE =%.7f\n", ce);
		System.out.println("\tConfusion matrix");
		System.out.print("\t\t");
		for (int i = 1; i <= 5; i++) {
			System.out.print("y=" + i + "\t");
		}
		System.out.println();
		for (int i = 0; i < 5; i++) {
			System.out.print("y^=" + (i + 1) + "\t");
			for (int j = 0; j < 5; j++) {
				System.out.print(confusionMatrix[i][j] + "\t");
			}
			System.out.println();
		}
	}

	// Helper function to get the index of the maximum element (for predicted class)
	private static int maxIndex(List<Double> values) {
		int maxIdx = 0;
		double maxVal = values.get(0);
		for (int i = 1; i < values.size(); i++) {
			if (values.get(i) > maxVal) {
				maxVal = values.get(i);
				maxIdx = i;
			}
		}
		return maxIdx;
	}
}
