/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nitc.NER;

import java.util.Scanner;
import java.io.*;
import java.util.StringTokenizer;


/**
 *different classes
 0. B-protein
 1. I-protein
 2. B-DNA
 3. I-DNA
 4. B-RNA
 5. I-RNA
 6. B-cell_type
 7. I-cell_type
 8. B-cell_line
 9. I-cell_line
 10. Others
 * */
public class TrainingClass implements Serializable {

    public int featureCount_morpho, featureCount_ortho = 15, featureCount_seq = 22;
    public String morpho_featureList[];
    public double featureProbabilityTable[][];
    public double featureAbsentProbabilityTable[][];
    public int class_count[] = new int[11];
    public String mostCommonWords[];
    public int dictionarySize;
    transient public static boolean newlyTrained = true;
    transient public static int featureCountTable[][]; // 1.Orthographic 2.Morphological 3.Sequential

    public TrainingClass(File fileName)
    {
       // System.out.println(fileName);
        train(fileName);
    }

    public void train(File fileName)
    {
        int countCommonWords = 0;
        dictionarySize = 0;
        mostCommonWords = new String[3000];
        String feature; // current entity read from file
        boolean emptyline; // whether a line is empty
        int tagLength;
        morpho_featureList = new String[8000];
        Scanner featureInput = null;

        //Loading Most Common Words in English to an Array mostCommonWords[]
        try {
        featureInput = new Scanner(new File("files/comWord.txt"));
        }
        catch (IOException e) {
        e.printStackTrace();
        }
        int i=0;
        while(featureInput.hasNextLine())
        {
            mostCommonWords[i] = featureInput.nextLine();
            i++;
        }
        dictionarySize = i;
        
        //Collecting features to an array featureList from the file prefix_suffix_list
        featureInput.close();
        try {
        featureInput = new Scanner(new File("files/final.txt"));
        }
        catch (IOException e) {
        e.printStackTrace();
        }
         // Loading All Features into memory
        while(featureInput.hasNextLine())
             {
                 emptyline = true;
                 tagLength = 0;
                 feature = featureInput.nextLine();
                 for(int j = 0; j<feature.length(); j++)
                 {
                     char c = feature.charAt(j);
                     if(c == ' ' || c == '\t')
                     {
                         tagLength++;
                         continue;
                     }

                     if(c != '\n')
                     {
                         emptyline = false;
                         break;
                     }
                 }

                 if(emptyline)
                     continue;
                 feature = feature.substring(tagLength);
                 morpho_featureList[featureCount_morpho] = feature;
                 featureCount_morpho++;

             }
        //System.out.println("Morphological features = "+ featureCount_morpho);
        int totalf = featureCount_morpho+featureCount_ortho+featureCount_seq;
        featureCountTable = new int[totalf][11];
        featureProbabilityTable = new double[totalf][11];
        featureAbsentProbabilityTable = new double[totalf][11];






        Scanner input = null;
        int num_sentences = 1, type = 10;
        boolean other = true;
        int prevType = 10;
        try {
        input = new Scanner(fileName);
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
        // Reading the Training Data
        while(input.hasNextLine())
        {
            String token = input.nextLine();
            if(token.length() == 0)
                num_sentences++;
            else
            {
                StringTokenizer st = new StringTokenizer(token);
                String entity = st.nextToken();
                String eType = st.nextToken();
                other = true;
                if(eType.equals("B-protein")) {
                    class_count[0]++;
                    type = 0; }
                if(eType.equals("I-protein")) {
                    class_count[1]++;
                    type = 1; }
                if(eType.equals("B-DNA")) {
                    class_count[2]++;
                    type = 2; }
                if(eType.equals("I-DNA")) {
                    class_count[3]++;
                    type = 3; }
                if(eType.equals("B-RNA")) {
                    class_count[4]++;
                    type = 4; }
                if(eType.equals("I-RNA")) {
                    class_count[5]++;
                    type = 5; }
                if(eType.equals("B-cell_type")) {
                    class_count[6]++;
                    type = 6; }
                if(eType.equals("I-cell_type")) {
                    class_count[7]++;
                    type = 7; }
                if(eType.equals("B-cell_line")) {
                    class_count[8]++;
                    type = 8; }
                if(eType.equals("I-cell_line")) {
                    class_count[9]++;
                    type = 9; }
                if(eType.equals("O")) {    
                    for(i=0;i<dictionarySize;i++)
                    {
                        if(mostCommonWords[i].equals(entity))
                        {
                            countCommonWords++;
                            other = false;
                            featureCountTable[featureCount_ortho+featureCount_morpho+prevType][10]++;
                            featureCountTable[featureCount_ortho+featureCount_morpho+10+11][prevType]++;
                            prevType = 10;
                            break;
                  
                        }
                    }
                   
                    class_count[10]++;
                    type = 10;
                }
               //Checking for features 1. Orthographic 2. Morphological 3. Sequential
               if(other)
               {
                 FeaturesChecker.orthographicFeatures(entity, type);
                 FeaturesChecker.morphologicalFeatures(entity, type, morpho_featureList,featureCount_morpho,featureCount_ortho);

                 // Sequence Feature

                 featureCountTable[featureCount_ortho+featureCount_morpho+prevType][type]++;
                 featureCountTable[featureCount_ortho+featureCount_morpho+type+11][prevType]++;
                 prevType = type;

               }
            }
            
        }
        
        System.out.println("number of sentences = "+num_sentences);
        System.out.printf("%-10s%-10s%-10s%-10s%-10s\n","protein","DNA","RNA","cell_type","cell_line");
        for(i=0;i<10;i+=2)
            System.out.printf("%-10d",class_count[i]);
        System.out.println();
        System.out.println("Count of Most Common Words = "+countCommonWords);
           

        //Calculating the featureProbability Table from Featurecount Table with Laplace Smoothing
        for(int j=0;j<totalf;j++)
        {
            for(i=0;i<11;i++)
            {
                featureProbabilityTable[j][i] = Math.log((double)(featureCountTable[j][i] + 1) / (class_count[i] + totalf));
                featureAbsentProbabilityTable[j][i] = Math.log(1-(double)(featureCountTable[j][i] + 1) / (class_count[i] + totalf));
            }
            
        }

      //Printing the Feature Count Table
        i = 0;
        System.out.printf("\nFeature Probability Table\n%-16s%-16s%-16s%-16s%-16s%-16s%-16s\n", "feature","protein","DNA","RNA","cell_type","cell_line","others");
        for(int j=0;j<15;j++)
             System.out.printf("ortho %-10d%-16d%-16d%-16d%-16d%-16d%-16d\n",j,featureCountTable[j][0],featureCountTable[j][2],featureCountTable[j][4],featureCountTable[j][6],featureCountTable[j][8],featureCountTable[j][10]);
        for(int j=featureCount_morpho+featureCount_ortho;j<totalf;j++,i++)
            System.out.printf("seque %-10d%-16d%-16d%-16d%-16d%-16d%-16d%-16d%-16d%-16d%-16d%-16d\n",i,featureCountTable[j][0],featureCountTable[j][1],featureCountTable[j][2],featureCountTable[j][3],featureCountTable[j][4],featureCountTable[j][5],featureCountTable[j][6],featureCountTable[j][7],featureCountTable[j][8],featureCountTable[j][9],featureCountTable[j][10]);
        newlyTrained = true; 

    }

}
