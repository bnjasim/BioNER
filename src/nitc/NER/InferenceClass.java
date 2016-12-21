/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nitc.NER;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Scanner;

/**
 *
 * @author binu
 */

public class InferenceClass {

    private static int featureCount_morpho, featureCount_ortho, featureCount_seq;
    private  static String featureList[];
    private  static int class_count[];
    private  static double featureProbabilityTable[][];
    private  static double featureAbsentProbabilityTable[][];
    private static double totalProbability[];
    private static String tokenList[];
    private static int forward_class_type[], backward_class_type[];

    private static String mostCommonWords[];
    private static int dictionarySize;


    public static int[] sentenceInfer(String sentence, boolean eval)
    {
        if(TrainingClass.newlyTrained)
        {
            TrainingClass trainedObject = (TrainingClass)deSerialize();
            TrainingClass.newlyTrained = false;

            featureCount_morpho = trainedObject.featureCount_morpho;
            featureCount_ortho =  trainedObject.featureCount_ortho;
            featureCount_seq = trainedObject.featureCount_seq;
            featureList = trainedObject.morpho_featureList;
            class_count = trainedObject.class_count;
            featureProbabilityTable = trainedObject.featureProbabilityTable;
            featureAbsentProbabilityTable = trainedObject.featureAbsentProbabilityTable;
            mostCommonWords = trainedObject.mostCommonWords;
            dictionarySize = trainedObject.dictionarySize;
            //System.out.println("DeSerialized and feature count morpho = "+ featureCount_morpho);
        }



        forward_class_type = new int[400];
        tokenList = new String[400];
        int countToken = 0;
        StringTokenizer st = new StringTokenizer(sentence);
        if(!eval) BioNERFrame.textArea.setText("Forward Prediction:\n");

	while (st.hasMoreElements()) 
        {
            String token = st.nextToken();
            tokenList[countToken] = token;
            int class_type = infer(token, false, false, 0);
            forward_class_type[countToken] = class_type;
            countToken++;
            if(!eval) {
            BioNERFrame.textArea.append(token);
            switch(class_type)
            {
                case 0: BioNERFrame.textArea.append("<B-protein> "); break;
                case 1: BioNERFrame.textArea.append("<I-protein> "); break;
                case 2: BioNERFrame.textArea.append("<B-DNA> "); break;
                case 3: BioNERFrame.textArea.append("<I-DNA> "); break;
                case 4: BioNERFrame.textArea.append("<B-RNA> "); break;
                case 5: BioNERFrame.textArea.append("<I-RNA> "); break;
                case 6: BioNERFrame.textArea.append("<B-cell_type> "); break;
                case 7: BioNERFrame.textArea.append("<I-cell_type> "); break;
                case 8: BioNERFrame.textArea.append("<B-cell_line> "); break;
                case 9: BioNERFrame.textArea.append("<I-cell_line> "); break;
                case 10: BioNERFrame.textArea.append(" "); 

            } }
            
        }

        /*
         *
         *
         Start Backward Inference
        */

        backward_class_type = new int[countToken];
        int forward[] = new int[countToken];
        for(int i=0;i<countToken;i++)
          forward[i] = forward_class_type[i];
        backward_class_type[countToken-1] = forward_class_type[countToken-1];
        for(int i=countToken-2;i>=0;i--)
        {
            String token = tokenList[i];
            backward_class_type[i] = infer(token, true, false, backward_class_type[i+1]);

        }
        
        if(!eval) {
        BioNERFrame.textArea.append("\n\n");
        BioNERFrame.textArea.append("After Backward Processing:\n");
        for(int i=0;i<countToken;i++)
        {
            BioNERFrame.textArea.append(tokenList[i]);
            switch(backward_class_type[i])
            {
                case 0: BioNERFrame.textArea.append("<B-protein> "); break;
                case 1: BioNERFrame.textArea.append("<I-protein> "); break;
                case 2: BioNERFrame.textArea.append("<B-DNA> "); break;
                case 3: BioNERFrame.textArea.append("<I-DNA> "); break;
                case 4: BioNERFrame.textArea.append("<B-RNA> "); break;
                case 5: BioNERFrame.textArea.append("<I-RNA> "); break;
                case 6: BioNERFrame.textArea.append("<B-cell_type> "); break;
                case 7: BioNERFrame.textArea.append("<I-cell_type> "); break;
                case 8: BioNERFrame.textArea.append("<B-cell_line> "); break;
                case 9: BioNERFrame.textArea.append("<I-cell_line> "); break;
                case 10: BioNERFrame.textArea.append(" ");

            }
        }

        }
        // Post Processing - Optional

        for(int i=countToken-2;i >= 0;i--)
        {
           /* if(backward_class_type[i] == 10 && backward_class_type[i+1]%2==0 && forward_class_type[i-1]%2==10 && backward_class_type[i+1]!=10)
                backward_class_type[i] = backward_class_type[i+1];
            if(tokenList[i].charAt(0)==')' || tokenList[i].charAt(0)=='(')
                backward_class_type[i] = backward_class_type[i+1];
            if(tokenList[i].equals("and"))
            {
                backward_class_type[i-1] = backward_class_type[i+1];
            }*/

            if(forward_class_type[i] != 10 && backward_class_type[i] == 10 && backward_class_type[i+1]%2 == 0 && backward_class_type[i+1] != 10)
                backward_class_type[i] = forward_class_type[i];
        }


        // Final Output
        if(!eval) {
        BioNERFrame.textArea.append("\n\n");
        BioNERFrame.textArea.append("Final Output:\n");
        switch(backward_class_type[0] / 2)
        {
              case 0: BioNERFrame.textArea.append("<Protein > "); break;
              case 1: BioNERFrame.textArea.append("<DNA > "); break;
              case 2: BioNERFrame.textArea.append("<RNA > "); break;
              case 3: BioNERFrame.textArea.append("<Cell > "); break;
              case 4: BioNERFrame.textArea.append("<Cell > "); break;
              case 5: BioNERFrame.textArea.append(" "); break;
        }
        for(int i=0;i<countToken-1;i++)
        {
             BioNERFrame.textArea.append(tokenList[i]+" ");
             if(backward_class_type[i]/2 != backward_class_type[i+1]/2)
             {
                 switch(backward_class_type[i] / 2)
                 {
                     case 0: BioNERFrame.textArea.append("</Protein > "); break;
                     case 1: BioNERFrame.textArea.append("</DNA > "); break;
                     case 2: BioNERFrame.textArea.append("</RNA > "); break;
                     case 3: BioNERFrame.textArea.append("</Cell > "); break;
                     case 4: BioNERFrame.textArea.append("</Cell > "); break;
                     case 5: BioNERFrame.textArea.append(" "); break;
                 }

                 //Next Type
                 switch(backward_class_type[i+1] / 2)
                 {
                     case 0: BioNERFrame.textArea.append("<Protein > "); break;
                     case 1: BioNERFrame.textArea.append("<DNA > "); break;
                     case 2: BioNERFrame.textArea.append("<RNA > "); break;
                     case 3: BioNERFrame.textArea.append("<Cell > "); break;
                     case 4: BioNERFrame.textArea.append("<Cell > "); break;
                     case 5: BioNERFrame.textArea.append(" "); break;
                 }
             }
         }
             BioNERFrame.textArea.append(tokenList[countToken-1]+" ");
             switch(backward_class_type[countToken-1] / 2)
                 {
                     case 0: BioNERFrame.textArea.append("</Protein > "); break;
                     case 1: BioNERFrame.textArea.append("</DNA > "); break;
                     case 2: BioNERFrame.textArea.append("</RNA > "); break;
                     case 3: BioNERFrame.textArea.append("</Cell > "); break;
                     case 4: BioNERFrame.textArea.append("</Cell > "); break;
                     case 5: BioNERFrame.textArea.append(" "); break;
                 }
        }
         if(eval)
             return backward_class_type;
         else
             return null;
        

    }
    
    //Call infer for inference of each small tokens
    public static int infer(String entity, boolean backward, boolean last, int next_class)
    {
       
        
        // Initialization
        totalProbability = new double[11];
        double sum = 0;
        for(int i=0;i<11;i++)
            sum += class_count[i];
        for(int i=0;i<11;i++)
             totalProbability[i] = Math.log((double)class_count[i]/sum);
        
        //Inference begins here
        for(int i=0;i<dictionarySize;i++)
                    {
                       if(mostCommonWords[i].equals(entity))
                        {
                            return 10;
                        }
                    }
        if(last)
           check_features(entity, backward, true, 10);
        else
            check_features(entity, backward, false, next_class);

        // Which class has the highest probability

        double highest = totalProbability[0];
        int pos = 0;
        for(int i=1;i<11;i++)
        {
            if(totalProbability[i] > highest)
            {
                highest = totalProbability[i];
                pos = i;
            }
        }

      /*  switch(pos)
        {
            case 0: BioNERFrame.textArea.setText("Class is B-protein"); break;
            case 1: BioNERFrame.textArea.setText("Class is I-protein"); break;
            case 2: BioNERFrame.textArea.setText("Class is B-DNA"); break;
            case 3: BioNERFrame.textArea.setText("Class is I-DNA"); break;
            case 4: BioNERFrame.textArea.setText("Class is B-RNA"); break;
            case 5: BioNERFrame.textArea.setText("Class is I-RNA"); break;
            case 6: BioNERFrame.textArea.setText("Class is B-cell_type"); break;
            case 7: BioNERFrame.textArea.setText("Class is I-cell_type"); break;
            case 8: BioNERFrame.textArea.setText("Class is B-cell_line"); break;
            case 9: BioNERFrame.textArea.setText("Class is I-cell_line"); break;
            case 10: BioNERFrame.textArea.setText("Class is Other............"); 

        } */
        
        return pos;
    }

   
   public static void check_features(String entity, boolean backward, boolean last, int next_class)
   {

       if(FeaturesChecker.allCap(entity))  // 0. ALL CAPS
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[0][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[0][i];

            }

        }

       if(FeaturesChecker.greekLetter(entity))  // 1
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[1][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[1][i];

            }

        }

       if(FeaturesChecker.ATCG_Sequence(entity))  // 2
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[2][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[2][i];

            }

        }

       if(FeaturesChecker.isDigit(entity))  // 3
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[3][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[3][i];

            }

        }

       if(FeaturesChecker.singleCap(entity))  // 4
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[4][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[4][i];

            }

        }

       if(FeaturesChecker.allSmall(entity))  // 5
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[5][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[5][i];

            }

        }

       if(FeaturesChecker.capsAndDigit(entity))  // 6
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[6][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[6][i];

            }

        }

       if(FeaturesChecker.InitCapDigit(entity))  // 7
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[7][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[7][i];

            }

        }

       if(FeaturesChecker.twoCaps(entity))  // 8
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[8][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[8][i];

            }

        }

       if(FeaturesChecker.initCapLower(entity))  // 9
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[9][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[9][i];

            }

        }

       if(FeaturesChecker.lowCapsMix(entity))  // 10
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[10][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[10][i];

            }

        }

       if(FeaturesChecker.romanLetter(entity))  // 11
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[11][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[11][i];

            }

        }

       if(FeaturesChecker.hyphen(entity))  // 12
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[12][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[12][i];

            }

        }

       if(FeaturesChecker.initDigit(entity))  // 13
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[13][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[13][i];

            }

        }

       if(FeaturesChecker.letterAndDigit(entity))  // 14
       {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureProbabilityTable[14][i];

            }
       }
        else
        {
            for(int i=0;i<11;i++)
            {
            //log(ab...) = log a + log b + ...
               totalProbability[i] += featureAbsentProbabilityTable[14][i];

            }

        }

       

       // Checking for morphological features

        for(int j=0; j<featureCount_morpho; j++)
        {
            if(FeaturesChecker.check_prefix_suffix(entity, featureList[j]))
            {
               for(int i=0;i<11;i++)
               {
                     //log(ab...) = log a + log b + ...
                     totalProbability[i] += featureProbabilityTable[featureCount_ortho+j][i];

               }

            }
            else
            {
               for(int i=0;i<11;i++)
                {
                    //log(ab...) = log a + log b + ...
                     totalProbability[i] += featureAbsentProbabilityTable[featureCount_ortho+j][i];

                }
            }
        }

       if(backward)
       {
             for(int i=0;i<11;i++)
               {
                     //log(ab...) = log a + log b + ...
                     if(last)
                     {
                        totalProbability[i] += featureProbabilityTable[featureCount_morpho+featureCount_ortho+11+10][i];
                        //totalProbability[i] += featureProbabilityTable[featureCount_morpho+featureCount_ortho+tokenType[tokenPos-1]][i];
                     }

                     else
                     {
                         totalProbability[i] += featureProbabilityTable[featureCount_morpho+featureCount_ortho+11+next_class][i];
                         //totalProbability[i] += featureProbabilityTable[featureCount_morpho+featureCount_ortho+tokenType[tokenPos-1]][i];
                     }

               }
       }
   }

    
   private static Object deSerialize()
    {
       Object readObject = null;
       try {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("files/trained model.ser"));
        readObject = ois.readObject();
       }
       catch(IOException e)
       {
           System.out.println("The System is NOT TRAINED yet\n");
           e.printStackTrace();
       }
       catch(ClassNotFoundException e)
       {
           System.out.println("Class Not Found Exception");
           e.printStackTrace();
       }

       return readObject; 
    }




    public static void evaluate(File fileName)
    {
        // Reading the Training Data
        /*
         * 0 - protein
         * 1 - DNA
         * 2 - RNA
         * 3 - Cell
         * */
        int line = 0;
        String sentence = "";
        int countToken = 0;
        int boundaryCorrect = 0, totalEntities = 0;
        int originalTypes[] = new int[400], inferredTypes[], totalRecognized = 0; // of a sentence
        int originalCount[] = new int[4], inferredCount[] = new int[4], correctCount[]  = new int[4]; // How many proteins detected etc..
        Scanner input = null;
        try {
            input = new Scanner(fileName);
        }
        catch(Exception e)
        {
           e.printStackTrace();
        }
        while(input.hasNextLine())
        {
            String token = input.nextLine();
            line++;
            if(token.length() != 0)
            {
                if(token.charAt(0)=='#')
                    continue;
                StringTokenizer st = new StringTokenizer(token);
                String entity = "",eType = "";
                try {
                entity = st.nextToken();
                eType = st.nextToken();
                }
                catch(java.util.NoSuchElementException e)
                {
                     System.out.println("\nError in evaluate file format at line no: "+line);
                     sentence = "";
                     countToken = 0;
                     originalTypes = new int[400];
                }
                sentence = sentence.concat(" ");
                sentence = sentence.concat(entity);
                if(entity.equals(".")) // A complete sentence has been read
                {
                      //System.out.println("Sentence = "+sentence);
                      inferredTypes = sentenceInfer(sentence, true);
                      int numTokens = inferredTypes.length-1;
                      if(countToken == numTokens) {
                         /*System.out.println("Both inferred and read sentence has same no of tokens");
                         System.out.print("Original Sentence Type = ");printArray(originalTypes, numTokens);
                         System.out.println("Inferred = ");printArray(inferredTypes, numTokens);*/
                         boolean inside = false;
                          //Calculating Precision Recall and F-factor
                         for(int i=0;i<numTokens;i++)
                         {

                             switch(originalTypes[i]/2)
                             {
                                 case 0: originalCount[0]++; break;
                                 case 1: originalCount[1]++; break;
                                 case 2: originalCount[2]++;break;
                                 case 3:
                                 case 4: originalCount[3]++;
                             }

                             switch(inferredTypes[i]/2)
                             {
                                 case 0: inferredCount[0]++; break;
                                 case 1: inferredCount[1]++; break;
                                 case 2: inferredCount[2]++;break;
                                 case 3:
                                 case 4: inferredCount[3]++;
                             }

                             if(originalTypes[i]/2 == inferredTypes[i]/2)
                             {
                                 switch(inferredTypes[i]/2)
                                {
                                 case 0: correctCount[0]++; break;
                                 case 1: correctCount[1]++; break;
                                 case 2: correctCount[2]++;break;
                                 case 3:
                                 case 4: correctCount[3]++;
                                }
                             }

                             if(originalTypes[i] != 10 && inferredTypes[i] != 10)
                                 totalRecognized++;

                             //Calculating boundary precision
                             if(inferredTypes[i] < 10 && originalTypes[i] < 10 && inferredTypes[i]/2==originalTypes[i]/2 && !inside)
                                 inside = true;
                             else
                             {
                                 if(inferredTypes[i] == 10 && originalTypes[i] == 10 && inside)
                                 {
                                     inside = false;
                                     boundaryCorrect++;
                                     totalEntities++;
                                 }
                                 else if(inside)
                                 {
                                     inside = false;
                                     totalEntities++;
                                 }

                             }
                         }
                      }
                      else
                          System.out.println("inferred type length = "+inferredTypes.length+" and original sentence length = "+originalTypes.length+" countToken = "+countToken);
                      originalTypes = new int[400];
                      countToken = 0;
                      sentence = "";
                }

                else // A sentence is not over
                {

                    if(eType.equals("B-protein")) {
                        originalTypes[countToken] = 0;
                        }
                    if(eType.equals("I-protein")) {
                        originalTypes[countToken] = 1;
                        }
                    if(eType.equals("B-DNA")) {
                        originalTypes[countToken] = 2;
                        }
                    if(eType.equals("I-DNA")) {
                        originalTypes[countToken] = 3;
                       }
                    if(eType.equals("B-RNA")) {
                        originalTypes[countToken] = 4;
                        }
                    if(eType.equals("I-RNA")) {
                        originalTypes[countToken] = 5;
                        }
                    if(eType.equals("B-cell_type")) {
                        originalTypes[countToken] = 6;
                        }
                    if(eType.equals("I-cell_type")) {
                        originalTypes[countToken] = 7;
                        }
                    if(eType.equals("B-cell_line")) {
                        originalTypes[countToken] = 8;
                        }
                    if(eType.equals("I-cell_line")) {
                        originalTypes[countToken] = 9;
                        }
                    if(eType.equals("O"))
                        originalTypes[countToken] = 10;

                    
                    
                    countToken++;
                }


            }

        }


        //Printing F-factor
        System.out.println("original count of protein = "+originalCount[0]+"\ncorrect count = "+correctCount[0]+"\ninferred = "+inferredCount[0]);
        System.out.println("original count of DNA = "+originalCount[1]+"\ncorrect count = "+correctCount[1]+"\ninferred = "+inferredCount[1]);
        System.out.println("original count of RNA = "+originalCount[2]+"\ncorrect count = "+correctCount[2]+"\ninferred = "+inferredCount[2]);
        System.out.println("original count of Cell = "+originalCount[3]+"\ncorrect count = "+correctCount[3]+"\ninferred = "+inferredCount[3]);
        double precision[] = new double[4], recall[] = new double[4], fFactor[] = new double[4];
        int totalCorrect = 0, totalInferred = 0, totalOriginal = 0;
        for(int i=0;i<4;i++)
        {
            precision[i] = (double)correctCount[i] / (inferredCount[i]+0.000001);
            recall[i] = (double)correctCount[i] / (originalCount[i]+0.000001);
            fFactor[i] = (double)2*precision[i]*recall[i] / (precision[i]+recall[i]+0.000001);

            totalCorrect += correctCount[i];
            totalInferred += inferredCount[i];
            totalOriginal += originalCount[i];
        }

        System.out.println("Evaluation Statistics\n");
        System.out.printf("%-12s%-12s%-12s%-12s\n","Entity","Precision","Recall","F factor");

        for(int i=0;i<4;i++)
        {

            switch(i)
            {
                case 0: System.out.print("Protein       ");break;
                case 1: System.out.print("DNA           ");break;
                case 2: System.out.print("RNA           ");break;
                case 3: System.out.print("Cell          ");
            }
            System.out.printf("%-12f%-12f%-12f\n",precision[i],recall[i],fFactor[i]);
        }
        double totalPrecision = (double)totalCorrect/totalInferred;
        double totalRecall = (double)totalCorrect/totalOriginal;
        double totalFfactor = (double)2*totalPrecision*totalRecall/(totalPrecision+totalRecall);

        double recognizePrecision = (double)totalRecognized / totalInferred;
        double recognizeRecall = (double) totalRecognized / totalOriginal;
        double recognizef = (double) 2*recognizePrecision*recognizeRecall/(recognizePrecision+recognizeRecall);

        System.out.print("Total         ");
        System.out.printf("%-12f%-12f%-12f\n",totalPrecision,totalRecall,totalFfactor);

        System.out.print("Recognition   ");
        System.out.printf("%-12f%-12f%-12f\n",recognizePrecision,recognizeRecall,recognizef);

        System.out.println("\nPrecision of boundary detection = "+(double)boundaryCorrect/totalEntities);
    }

    public static void printArray(int a[],int l)
    {
        for(int i=0;i<l;i++)
        {
            System.out.println(a[i]+" ");
        }
    }


}
