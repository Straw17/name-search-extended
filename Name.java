import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;

public class Name {
	public String name;
	public Display mainDisplay;
	
	public Object[] maleData, femaleData, maleRank, femaleRank;
	
	BufferedReader br;
	String currentStrLine;
	
	DecimalFormat df = new DecimalFormat("##.000");
	
	int index1, index2;
	int births;
	String currentName;
	String gender;
	
	int rankM; int rankF; int savedRank;
	int lastNumBirths;
	boolean reset;
	boolean skipF;
	
	public Name(String name, Display mainDisplay) {
		this.name = name;
		this.mainDisplay = mainDisplay;
		name.replaceAll("\\s+","");
		
		if(mainDisplay.showF) {
			femaleRank = new Integer[Display.yearsTotal];
			if(mainDisplay.showPercent) {
				femaleData = new Double[Display.yearsTotal];
			} else {
				femaleData = new Integer[Display.yearsTotal];
			}
		}
		if(mainDisplay.showM) {
			maleRank = new Integer[Display.yearsTotal];
			if(mainDisplay.showPercent) {
				maleData = new Double[Display.yearsTotal];
			} else {
				maleData = new Integer[Display.yearsTotal];
			}
		}
	}
	
	private void scanYear(int date) throws IOException {
		br = Display.brList[date-1880];
		rankM = 0; rankF = 0;  savedRank = 0; lastNumBirths = 0;
		reset = false; skipF = false;
		
		while((currentStrLine = br.readLine()) != null) {
			
			index1 = currentStrLine.indexOf(',');
			index2 = currentStrLine.indexOf(',', index1+1);
			
			currentName = currentStrLine.substring(0, index1);
			gender = currentStrLine.substring(index1+1, index2);
			births = Integer.parseInt(currentStrLine.substring(index2+1));
			
			if(gender.equals("F")) {
				if(skipF || !mainDisplay.showF) {
					continue;
				}
				
				if(births == lastNumBirths) {
					savedRank++;
				} else {
					rankF += savedRank;
					savedRank = 0;
					rankF++;
				}
				lastNumBirths = births;
			} else {
				if(!mainDisplay.showM) {
					break;
				}
				if(!reset) {
					reset = true;
					lastNumBirths = 0;
					savedRank = 0;
				}
				if(births == lastNumBirths) {
					savedRank++;
				} else {
					rankM += savedRank;
					savedRank = 0;
					rankM++;
				}
				lastNumBirths = births;
			}
			
			if(currentName.equals(name)) {
				if(gender.equals("F")) {
					skipF = true;
					if(mainDisplay.showPercent) {
						femaleData[date-1880] = Double.parseDouble(df.format((100.0 * (double) births / (double) Display.birthTotalsF[date-1880])));
					} else if(mainDisplay.normalize){
						femaleData[date-1880] = (int) Math.round(births * 
								(double)Display.birthTotalsF[Display.yearsTotal-1]/(double)Display.birthTotalsF[date-1880]);
					} else {
						femaleData[date-1880] = births;
					}
					femaleRank[date-1880] = rankF;
				} else {
					if(mainDisplay.showPercent) {
						maleData[date-1880] = Double.parseDouble(df.format((100.0 * (double) births / (double) Display.birthTotalsM[date-1880])));
					} else if(mainDisplay.normalize){
						maleData[date-1880] = (int) Math.round(births * 
								(double)Display.birthTotalsM[Display.yearsTotal-1]/(double)Display.birthTotalsM[date-1880]);
					} else {
						maleData[date-1880] = births;
					}
					maleRank[date-1880] = rankM;
					break;
				}
			}
		}
		br.close();
	}
	
	public void getData() throws IOException {
		for(int date = 1880; date <= 2020; date++) {
			scanYear(date);
		}
	}
}