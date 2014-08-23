package com.rishidua.ocr;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class UndirectedGraphicalModel.
 *
 * @author rishi
 */
public class UndirectedGraphicalModel {

	/**
	 * The numlines.
	 *
	 */
	static int numlines = 14; //change this for the new file
	
	/** The strlengths. */
	static int[] strlengths= new int[numlines*2];

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {

		//read input and initialise
		int[][] small_images = Utilities.readImages("OCRdataset/data/data-loops.dat");
		int[][] small_chars = Utilities.readWords("OCRdataset/data/truth-loops.dat");
		float[][] ocrpotentials = Utilities.readOCRPotentials("OCRdataset/potentials/ocr.dat");
		float[][] transpotentials = Utilities.readTransitionPotentials("OCRdataset/potentials/trans.dat");
		
		//Check if directory exist
		Utilities.checkDir("results", "Checking directory and write permissions");
		
		//OCR Model 
		System.out.println("OCR Model");
		float[] oModelScore = new float[numlines];
		float[] oModelScore_true = new float[numlines];
		long[] duration = new long[4];
		long startTime = System.nanoTime();
		for (int k = 0; k<numlines; k++) {
			oModelScore[k] = ocrscore(ocrpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);
			oModelScore_true[k] = ocrscore(ocrpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);
		}
		long endTime = System.nanoTime();
		duration[0] = endTime - startTime;
		for (int i = 0; i<oModelScore.length; i++) {
			oModelScore[i]=(float) Math.log(oModelScore[i]); 
		}
		Utilities.writeFile(oModelScore, "results/oscores.dat");
		

		//OCR + Transition Model
		System.out.println("OCR + T Model");
		float[] tModelScore = new float[numlines];
		float[] tModelScore_true = new float[numlines];
		startTime = System.nanoTime();
		for (int k = 0; k<numlines; k++) {
			tModelScore[k] = transscore(ocrpotentials, transpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);
			tModelScore_true[k] = transscore(ocrpotentials, transpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);
		}
		endTime = System.nanoTime();
		duration[1] = endTime - startTime;
		for (int i = 0; i<tModelScore.length; i++) {
			tModelScore[i]=(float) Math.log(tModelScore[i]); 
		}
		Utilities.writeFile(tModelScore, "results/tscores.dat");
		
		//OCR + Transition + Skip Model
		System.out.println("OCR + T + S Model");
		float[] sModelScore = new float[numlines];
		float[] sModelScore_true = new float[numlines];
		startTime = System.nanoTime();
		for (int k = 0; k<numlines; k++) {
			sModelScore[k] = calculateSkipScore(ocrpotentials, transpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);	
			sModelScore_true[k] = calculateSkipScore(ocrpotentials, transpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);
		}
		endTime = System.nanoTime();
		duration[2] = endTime - startTime;


		
		//OCR + Transition + Skip + Pair Model
		System.out.println("OCR + T + S + P Model");
		float[] pModelScore = new float[numlines];
		float[] pModelScore_true = new float[numlines];
		startTime = System.nanoTime();
		for (int k = 0; k<numlines; k++) {
			pModelScore[k] = calculatePairScore(ocrpotentials, transpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);	
			pModelScore_true[k] = calculatePairScore(ocrpotentials, transpotentials, small_images[2*k], small_images[2*k+1], small_chars[2*k], small_chars[2*k+1], strlengths[2*k], strlengths[2*k+1]);
		}
		endTime = System.nanoTime();
		duration[3] = endTime - startTime;
		for (int i = 0; i<pModelScore.length; i++) {
			pModelScore[i]=(float) Math.log(pModelScore[i]); 
		}
		Utilities.writeFile(pModelScore, "results/pscores.dat");
		float[] floatArray = new float[duration.length];
		for (int i = 0 ; i < duration.length; i++)
		{
		    floatArray[i] = (float) duration[i];
		}
		Utilities.writeFile(floatArray, "results/time.dat");
		
		for (int i = 0; i<sModelScore.length; i++) {
			sModelScore[i]=(float) Math.log(sModelScore[i]); 
		}
		Utilities.writeFile(sModelScore, "results/sscores.dat");
		System.out.println("Done");
		
	}
	
	// helper function
	/**
	 * Calculate pair score.
	 *
	 * @param ocrpotentials the ocrpotentials
	 * @param transpotentials the transpotentials
	 * @param small_image1 the small_image1
	 * @param small_image2 the small_image2
	 * @param small_char1 the small_char1
	 * @param small_char2 the small_char2
	 * @param lenw1 the lenw1
	 * @param lenw2 the lenw2
	 * @return the float
	 */
	private static float calculatePairScore(float[][] ocrpotentials,	float[][] transpotentials, int[] small_image1, int[] small_image2, int[] small_char1, int[] small_char2, int lenw1, int lenw2) {
		float modelscore = 1;
		for (int i=0; i<lenw1; i++) { 
			modelscore = modelscore * ocrpotentials[small_image1[i]][small_char1[i]];
		}
		for (int i=0; i<lenw2; i++) { 
			modelscore = modelscore * ocrpotentials[small_image2[i]][small_char2[i]];
		}
		
		for (int i=0; i<lenw1-1; i++) { 
			modelscore = modelscore * transpotentials[small_char1[i]][small_char1[i+1]];
		}
		for (int i=0; i<lenw2-1; i++) { 
			modelscore = modelscore * transpotentials[small_char2[i]][small_char2[i+1]];
		}
		
		for (int j=0; j<lenw1-1; j++) {
			for (int i=0; i<j; i++) { 
				if ((small_char1[i] == small_char1[j]) && (small_image1[i]==small_image1[j])) {
					modelscore = modelscore * 5;
				}
			}
		}
		for (int j=0; j<lenw2-1; j++) {
			for (int i=0; i<j; i++) { 
				if ((small_char2[i] == small_char2[j]) && (small_image2[i]==small_image2[j])) {
					modelscore = modelscore * 5;
				}
			}
		}
		for (int j=0; j<lenw1; j++) {
			for (int i=0; i<lenw2; i++) { 
				if ((small_char1[j] == small_char2[i]) && (small_image1[j]==small_image2[i])) { 
					modelscore = modelscore * 5;
				}
			}
		}
		return modelscore;
	}
	
	/**
	 * Calculate skip score.
	 *
	 * @param ocrpotentials the ocrpotentials
	 * @param transpotentials the transpotentials
	 * @param small_image1 the small_image1
	 * @param small_image2 the small_image2
	 * @param small_char1 the small_char1
	 * @param small_char2 the small_char2
	 * @param lenw1 the lenw1
	 * @param lenw2 the lenw2
	 * @return the float
	 */
	private static float calculateSkipScore(float[][] ocrpotentials,	float[][] transpotentials, int[] small_image1, int[] small_image2, int[] small_char1, int[] small_char2, int lenw1, int lenw2) {
		float modelscore = 1;
		for (int i=0; i<lenw1; i++) { 
			modelscore = modelscore * ocrpotentials[small_image1[i]][small_char1[i]];
		}
		for (int i=0; i<lenw2; i++) { 
			modelscore = modelscore * ocrpotentials[small_image2[i]][small_char2[i]];
		}
		
		for (int i=0; i<lenw1-1; i++) { 
			modelscore = modelscore * transpotentials[small_char1[i]][small_char1[i+1]];
		}
		for (int i=0; i<lenw2-1; i++) { 
			modelscore = modelscore * transpotentials[small_char2[i]][small_char2[i+1]];
		}
		
		for (int j=0; j<lenw1-1; j++) {
			for (int i=0; i<j; i++) { 
				if ((small_char1[i] == small_char1[j]) && (small_image1[i]==small_image1[j])) {
					modelscore = modelscore * 5;
				}
			}
		}
		for (int j=0; j<lenw2-1; j++) {
			for (int i=0; i<j; i++) { 
				if ((small_char2[i] == small_char2[j]) && (small_image2[i]==small_image2[j])) {
					modelscore = modelscore * 5;
				}
			}
		}
		
		return modelscore;
	}

	// helper function
	/**
	 * Transscore.
	 *
	 * @param ocrpotentials the ocrpotentials
	 * @param transpotentials the transpotentials
	 * @param small_image1 the small_image1
	 * @param small_image2 the small_image2
	 * @param small_char1 the small_char1
	 * @param small_char2 the small_char2
	 * @param lenw1 the lenw1
	 * @param lenw2 the lenw2
	 * @return the float
	 */
	private static float transscore(float[][] ocrpotentials,	float[][] transpotentials, int[] small_image1, int[] small_image2, int[] small_char1, int[] small_char2, int lenw1, int lenw2) {
		float modelscore = 1;
		for (int i=0; i<lenw1; i++) { 
			modelscore = modelscore * ocrpotentials[small_image1[i]][small_char1[i]];
		}
		for (int i=0; i<lenw2; i++) { 
			modelscore = modelscore * ocrpotentials[small_image2[i]][small_char2[i]];
		}
		for (int i=0; i<lenw1-1; i++) { 
			modelscore = modelscore * transpotentials[small_char1[i]][small_char1[i+1]];
		}
		for (int i=0; i<lenw2-1; i++) { 
			modelscore = modelscore * transpotentials[small_char2[i]][small_char2[i+1]];
		}
		return modelscore;
	}

	//helper function
	/**
	 * Ocrscore.
	 *
	 * @param ocrpotentials the ocrpotentials
	 * @param small_image1 the small_image1
	 * @param small_image2 the small_image2
	 * @param small_char1 the small_char1
	 * @param small_char2 the small_char2
	 * @param lenw1 the lenw1
	 * @param lenw2 the lenw2
	 * @return the float
	 */
	private static float ocrscore(float[][] ocrpotentials, int[] small_image1, int[] small_image2, int[] small_char1, int[] small_char2, int lenw1, int lenw2) {
		float modelscore = 1;
		for (int i=0; i<lenw1; i++) {
			modelscore = modelscore * ocrpotentials[small_image1[i]][small_char1[i]];
		}
		for (int i=0; i<lenw2; i++) {
			modelscore = modelscore * ocrpotentials[small_image2[i]][small_char2[i]];
		}
		return modelscore;
	}
}

