package com.yong.codonanalyzer;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity 
{
	int selectedCode = 0;
	int locateAUG = 0;
	String inputCode = "";
	String resultText = "";
	boolean foundAUG = false;
	boolean foundEnding = false;
	
	TextView inputTextView;
	Button uButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		RadioGroup codeSelect = findViewById(R.id.main_select_code);
		codeSelect.check(R.id.main_code_codon);
		inputTextView = findViewById(R.id.main_codon_input);
		uButton = findViewById(R.id.main_input_u);
    }
	
	public void codon(View v){
		selectedCode = 0;
		inputCode =  "";
		uButton.setText("\nU\n");
		inputTextView.setText(getResources().getString(R.string.main_input_info));
	}
	
	public void antiCodon(View v){
		selectedCode = 1;
		inputCode = "";
		uButton.setText("\nU\n");
		inputTextView.setText(getResources().getString(R.string.main_input_info));
	}
	
	public void tripletCode(View v){
		selectedCode = 2;
		inputCode = "";
		uButton.setText("\nT\n");
		inputTextView.setText(getResources().getString(R.string.main_input_info));
	}
	
	public void input_a(View v){
		inputCode = inputCode + "A";
		inputTextView.setText(inputCode);
	}
	
	public void input_u(View v){
		if(selectedCode == 2){
			inputCode = inputCode + "T";
		}else{
			inputCode = inputCode + "U";
		}
		inputTextView.setText(inputCode);
	}
	
	public void input_g(View v){
		inputCode = inputCode + "G";
		inputTextView.setText(inputCode);
	}
	
	public void input_c(View v){
		inputCode = inputCode + "C";
		inputTextView.setText(inputCode);
	}
	
	public void analyze(View v){
		 switch(selectedCode){
			 default:
			 	break;
			 case 1:
				inputCode = reverseString(inputCode);
				for(int i=0; i<inputCode.length(); i++){
					if(inputCode.charAt(i) == 'A'){
						inputCode  = inputCode.substring(0, i) + "U" + inputCode.substring(i+1);
					}else if(inputCode.charAt(i) == 'U'){
						 inputCode  = inputCode.substring(0, i) + "A" + inputCode.substring(i+1);
					}else if(inputCode.charAt(i) == 'G'){
						 inputCode  = inputCode.substring(0, i) + "C" + inputCode.substring(i+1);
					}else if(inputCode.charAt(i) == 'C'){
						 inputCode  = inputCode.substring(0, i) + "G" + inputCode.substring(i+1);
					}
				}
				break;
			 case 2:
				 inputCode = reverseString(inputCode);
				 for(int i=0; i<inputCode.length(); i++){
					 if(inputCode.charAt(i) == 'A'){
						 inputCode  = inputCode.substring(0, i) + "U" + inputCode.substring(i+1);
					 }else if(inputCode.charAt(i) == 'T'){
						 inputCode  = inputCode.substring(0, i) + "A" + inputCode.substring(i+1);
					 }else if(inputCode.charAt(i) == 'G'){
						 inputCode  = inputCode.substring(0, i) + "C" + inputCode.substring(i+1);
					 }else if(inputCode.charAt(i) == 'C'){
						 inputCode  = inputCode.substring(0, i) + "G" + inputCode.substring(i+1);
					 }
				 }
				break;
		 }
		 analyzeTask task = new analyzeTask();
		 task.execute();
	}
	
	public void erase(View v){
		if(inputCode.length() != 0){
			inputCode = inputCode.substring(0, inputCode.length()-1);
			inputTextView.setText(inputCode);
		}
	}
	
	private class analyzeTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog analyzingDialog = new ProgressDialog(MainActivity.this);

		@Override
		protected void onPreExecute() {
			analyzingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			analyzingDialog.setMessage("분석중입니다...");
			analyzingDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			for(int i=0; i<inputCode.length()-2; i++){
				if(inputCode.charAt(i) == 'A'){
					if(inputCode.charAt(i+1) == 'U'){
						if(inputCode.charAt(i+2) == 'G'){
							foundAUG = true;
							locateAUG = i;
							break;
						}
					}
				}
			}
			
			if(foundAUG){
				for(int j=locateAUG+3; j<inputCode.length()-2; j+=3){
					if(inputCode.charAt(j) == 'U'){
						if(inputCode.charAt(j+1) == 'A'){
							if(inputCode.charAt(j+2) == 'A' || inputCode.charAt(j+2) == 'G'){
								foundEnding = true;
								try{
									inputCode = inputCode.substring(locateAUG, j+3);
								}catch(StringIndexOutOfBoundsException e){
									inputCode = inputCode.substring(locateAUG);
								}
								break;
							}
						}else if(inputCode.charAt(j+1) == 'G'){
							if(inputCode.charAt(j+2) == 'A'){
								foundEnding = true;
								try{
									inputCode = inputCode.substring(locateAUG, j+3);
								}catch(StringIndexOutOfBoundsException e){
									inputCode = inputCode.substring(locateAUG);
								}
								break;
							}
						}
					}
				}
				 
				if(foundEnding){
					for(int k=0; k<inputCode.length()-2; k+=3){
						if(resultText.length() == 0){
							resultText += changeAmino(inputCode.substring(k, k+3));
						}else{
							resultText += " - " + changeAmino(inputCode.substring(k, k+3));
						}
					}
				}else{
					resultText = getResources().getString(R.string.analyze_no_ending);
				}
			}else{
				resultText = getResources().getString(R.string.analyze_no_aug);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			AlertDialog.Builder resultDialog = new AlertDialog.Builder(MainActivity.this);
			resultDialog.setTitle(getResources().getString(R.string.analyze_result));
			if(foundAUG && foundEnding){
				resultDialog.setMessage(getResources().getString(R.string.analyze_result_input) + "\n5\' - " + inputCode + " - 3\'\n\n" + getResources().getString(R.string.analyze_result_output) + "\n5\' - " + resultText + " - 3\'");
			}else{
				resultDialog.setMessage(resultText);
			}
			resultDialog.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						p1.dismiss();
					}				
			});
			resultDialog.show();
			analyzingDialog.dismiss();
			inputCode = "";
			resultText = "";
			foundAUG = false;
			foundEnding = false;
			inputTextView.setText(getResources().getString(R.string.main_input_info));
			super.onPostExecute(result);
		}
	}
	
	String reverseString(String input){
		return new StringBuffer(input).reverse().toString();
	}

	String changeAmino(String input){
		String result = "";
		switch(input){
			case "UUU":
				result = getResources().getString(R.string.codon_phenylalanine);
				break;
			case "UUC":
				result = getResources().getString(R.string.codon_phenylalanine);
				break;
			case "UUA":
				result = getResources().getString(R.string.codon_leucine);
				break;
			case "UUG":
				result = getResources().getString(R.string.codon_leucine);
				break;
			case "CUU":
				result = getResources().getString(R.string.codon_leucine);
				break;
			case "CUC":
				result = getResources().getString(R.string.codon_leucine);
				break;
			case "CUA":
				result = getResources().getString(R.string.codon_leucine);
				break;
			case "CUG":
				result = getResources().getString(R.string.codon_leucine);
				break;
			case "AUU":
				result = getResources().getString(R.string.codon_isoleucine);
				break;
			case "AUC":
				result = getResources().getString(R.string.codon_isoleucine);
				break;
			case "AUA":
				result = getResources().getString(R.string.codon_isoleucine);
				break;
			case "AUG":
				result = getResources().getString(R.string.codon_mesythonine);
				break;
			case "GUU":
				result = getResources().getString(R.string.codon_valin);
				break;
			case "GUC":
				result = getResources().getString(R.string.codon_valin);
				break;
			case "GUA":
				result = getResources().getString(R.string.codon_valin);
				break;
			case "GUG":
				result = getResources().getString(R.string.codon_valin);
				break;
			case "UCU":
				result = getResources().getString(R.string.codon_serine);
				break;
			case "UCC":
				result = getResources().getString(R.string.codon_serine);
				break;
			case "UCA":
				result = getResources().getString(R.string.codon_serine);
				break;
			case "UCG":
				result = getResources().getString(R.string.codon_serine);
				break;
			case "CCU":
				result = getResources().getString(R.string.codon_proline);
				break;
			case "CCC":
				result = getResources().getString(R.string.codon_proline);
				break;
			case "CCA":
				result = getResources().getString(R.string.codon_proline);
				break;
			case "CCG":
				result = getResources().getString(R.string.codon_proline);
				break;
			case "ACU":
				result = getResources().getString(R.string.codon_threonine);
				break;
			case "ACC":
				result = getResources().getString(R.string.codon_threonine);
				break;
			case "ACA":
				result = getResources().getString(R.string.codon_threonine);
				break;
			case "ACG":
				result = getResources().getString(R.string.codon_threonine);
				break;
			case "GCU":
				result = getResources().getString(R.string.codon_alanine);
				break;
			case "GCC":
				result = getResources().getString(R.string.codon_alanine);
				break;
			case "GCA":
				result = getResources().getString(R.string.codon_alanine);
				break;
			case "GCG":
				result = getResources().getString(R.string.codon_alanine);
				break;
			case "UAU":
				result = getResources().getString(R.string.codon_tyrosine);
				break;
			case "UAC":
				result = getResources().getString(R.string.codon_tyrosine);
				break;
			case "UAA":
				result = getResources().getString(R.string.codon_term_uaa);
				break;
			case "UAG":
				result = getResources().getString(R.string.codon_term_uag);
				break;
			case "CAU":
				result = getResources().getString(R.string.codon_histidine);
				break;
			case "CAC":
				result = getResources().getString(R.string.codon_histidine);
				break;
			case "CAA":
				result = getResources().getString(R.string.codon_glutamine);
				break;
			case "CAG":
				result = getResources().getString(R.string.codon_glutamine);
				break;
			case "AAU":
				result = getResources().getString(R.string.codon_aspartic);
				break;
			case "AAC":
				result = getResources().getString(R.string.codon_aspartic);
				break;
			case "AAA":
				result = getResources().getString(R.string.codon_lysine);
				break;
			case "AAG":
				result = getResources().getString(R.string.codon_lysine);
				break;
			case "GAU":
				result = getResources().getString(R.string.codon_aspartic);
				break;
			case "GAC":
				result = getResources().getString(R.string.codon_aspartic);
				break;
			case "GAA":
				result = getResources().getString(R.string.codon_glutamic);
				break;
			case "GAG":
				result = getResources().getString(R.string.codon_glutamic);
				break;
			case "UGU":
				result = getResources().getString(R.string.codon_cysteine);
				break;
			case "UGC":
				result = getResources().getString(R.string.codon_cysteine);
				break;
			case "UGA":
				result = getResources().getString(R.string.codon_term_uga);
				break;
			case "UGG":
				result = getResources().getString(R.string.codon_tryptophan);
				break;
			case "CGU":
				result = getResources().getString(R.string.codon_arginine);
				break;
			case "CGC":
				result = getResources().getString(R.string.codon_arginine);
				break;
			case "CGA":
				result = getResources().getString(R.string.codon_arginine);
				break;
			case "CGG":
				result = getResources().getString(R.string.codon_arginine);
				break;
			case "AGU":
				result = getResources().getString(R.string.codon_serine);
				break;
			case "AGC":
				result = getResources().getString(R.string.codon_serine);
				break;
			case "AGA":
				result = getResources().getString(R.string.codon_arginine);
				break;
			case "AGG":
				result = getResources().getString(R.string.codon_arginine);
				break;
			case "GGU":
				result = getResources().getString(R.string.codon_glycine);
				break;
			case "GGC":
				result = getResources().getString(R.string.codon_glycine);
				break;
			case "GGA":
				result = getResources().getString(R.string.codon_glycine);
				break;
			case "GGG":
				result = getResources().getString(R.string.codon_glycine);
				break;
			default :
				result = getResources().getString(R.string.codon_unknown);
				break;
		}
		return result;
	}
}
