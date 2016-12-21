/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nitc.NER;

/**
 *
 * @author binu
 */

//Morphological and Head Noun Features
public class FeaturesChecker {


    public static void orthographicFeatures(String entity, int type)
    {
        if(allCap(entity))  // 0. ALL CAPS
            TrainingClass.featureCountTable[0][type]++;

        if(greekLetter(entity)) //1. alpha, beta, gamma etc..
            TrainingClass.featureCountTable[1][type]++;

        if(ATCG_Sequence(entity)) //2. ATCG sequence
            TrainingClass.featureCountTable[2][type]++;

        if(isDigit(entity)) //3. contains digit or not
            TrainingClass.featureCountTable[3][type]++;

        if(singleCap(entity)) //4. B M T
            TrainingClass.featureCountTable[4][type]++;

        if(allSmall(entity)) //5. protein
            TrainingClass.featureCountTable[5][type]++;

        if(capsAndDigit(entity)) //6. MEK1
            TrainingClass.featureCountTable[6][type]++;

        if(InitCapDigit(entity)) //7. Am80
            TrainingClass.featureCountTable[7][type]++;

        if(twoCaps(entity)) //8. FasL
            TrainingClass.featureCountTable[8][type]++;

        if(initCapLower(entity)) //9.Ras Ctx
            TrainingClass.featureCountTable[9][type]++;

        if(lowCapsMix(entity)) //10. dNTPs
            TrainingClass.featureCountTable[10][type]++;

        if(romanLetter(entity)) //11. I II III
            TrainingClass.featureCountTable[11][type]++;

        if(hyphen(entity)) //12. -
            TrainingClass.featureCountTable[12][type]++;

        if(initDigit(entity)) //13. 15B7
            TrainingClass.featureCountTable[13][type]++;

        if(letterAndDigit(entity)) //14. ETh1
            TrainingClass.featureCountTable[14][type]++;

    }

    public static void morphologicalFeatures(String entity, int type, String featureList[], int m, int o)
    {
        for(int i=0;i<m;i++)
        {
            if(check_prefix_suffix(entity, featureList[i]))
                TrainingClass.featureCountTable[i+o][type]++;
        }
    }


    public static boolean check_prefix_suffix(String entity, String str)
    {
        str = str.trim();
        if(entity.contains(str))
           return true;
        else
           return false;
    
    }


    //Orthographic Feature
    public static boolean allCap(String entity)
    {
        if(entity.matches("[A-Z][A-Z]+"))
            return true;
        else
            return false;
    }

    public static boolean greekLetter(String entity)
    {
        boolean isGreek = false;
        String gLetters[] = {"alpha","beta","gamma","delta","epsilon","zeta","eta","theta","iota","kappa","lambda","mu","sigma","pi","phi","omega"};
        for(int i=0;i<gLetters.length;i++)
            if(entity.toLowerCase().contains(gLetters[i].toLowerCase()))
            {
                isGreek = true;
               break;
            }
        return isGreek;
    }

    public static boolean ATCG_Sequence(String entity)
    {
        if(entity.matches(".*[ATCG][ATCG][ATCG]+.*"))
            return true;
        else
            return false;
    }

    public static boolean isDigit(String entity)
    {
        if(entity.matches(".*[0-9]+.*"))
            return true;
        else
            return false;
    }

    public static boolean singleCap(String entity)
    {
        if(entity.matches("[A-Z]"))
            return true;
        else
            return false;
    }

    public static boolean allSmall(String entity)
    {
        if(entity.matches("[a-z][a-z]+"))
            return true;
        else
            return false;
    }

    public static boolean capsAndDigit(String entity)
    {
        if(entity.matches("[A-Z]+[0-9]+"))
            return true;
        else
            return false;
    }

    public static boolean InitCapDigit(String entity)
    {
        if(entity.matches("[A-Z][a-z]+[0-9]+"))
            return true;
        else
            return false;
    }

    public static boolean twoCaps(String entity)
    {
        if(entity.matches("[A-Z][a-z]+[A-Z]"))
            return true;
        else
            return false;
    }

    public static boolean initCapLower(String entity)
    {
        if(entity.matches("[A-Z][a-z]+"))
            return true;
        else
            return false;
    }

    public static boolean lowCapsMix(String entity)
    {
        if(entity.matches("[a-zA-Z]+"))
            if(entity.matches("[a-z]+") || entity.matches("[A-Z]+") || entity.matches("[A-Z].*"))
               return false;
            else
                return true;
        else
            return false;
    }

    public static boolean romanLetter(String entity)
    {
        if(entity.matches("[IVXivx]+"))
            return true;
        else
            return false;
    }

    public static boolean hyphen(String entity)
    {
        if(entity.contains("-"))
            return true;
        else
            return false;
    }

    public static boolean initDigit(String entity)
    {
        if(entity.matches("^[0-9]+.*"))
            return true;
        else
            return false;
    }

    public static boolean letterAndDigit(String entity)
    {
        if(entity.matches("[a-zA-Z0-9]+"))
            if(entity.matches("[a-zA-z]+") || entity.matches("[0-9]+"))
                return false;
            else
                return true;
        else
            return false;
    }

}