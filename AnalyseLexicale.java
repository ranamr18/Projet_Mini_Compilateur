package Projet_Mini_Compilateur;

import static Projet_Mini_Compilateur.AnalyseSyntaxique.Z;
import java.util.Arrays;
import java.util.Scanner;

public class AnalyseLexicale {

    static String[] MotClePerso = {"Rania", "Mansouri"};
    static String[] MotCle = {
        "if", "else", "while", "for",
        "function", "return", "do",
        "elseif", "foreach",
        "switch", "case",
        "break", "continue", "echo"
    };

    static int[][] matrice = {
   // 0: espace/tab,1:lettre,2:chiffre,3:$,4:_,5:op simple,6:&,7:|,8:/,9:*,10:sep,11:autre,12:\n,13:#,14:EOF
        { 0, 1, 2, 3, 1, 5,12,13,14, 5,10,-1, 0,15,0 }, //0 initial
        {-1,1,1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //1 mot
        {-1,-1,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //2 nombre
        {-1,4,4,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}, //$var
        {-1,4,4,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},  //4 identificateur
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//5 opérateur simple
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//6 ==
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//7 <=
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//8 >=
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//9 !=
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//10 ++
        {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},//11 --
        {-1,-1,-1,-1,-1,-1,12,-1,-1,-1,-1,-1,-1,-1,-1},//12 &
        {-1,-1,-1,-1,-1,-1,-1,13,-1,-1,-1,-1,-1,-1,-1},//13 |
        { -1, -1, -1, -1, -1, -1, -1, -1, 15, 16, -1, -1, -1, -1, -1 }, //14 / -> / seul ou début commentaire
        {15,15,15,15,15,15,15,15,15,15,15,15,0,15,15}, //15 commentaire ligne
        {16,16,16,16,16,16,16,16,16,17,16,16,16,16,16}, //16 commentaire bloc
        {16,16,16,16,16,16,16,16,16,17,16,16,16,16,16}  //17 * dans commentaire bloc
    };

    static boolean[] EtatFin = {
        false, true, true, false, true,
        true, true, true, true, true,
        true, true, true, true, true,
        false, false, false
    };

    static int chartocol(char c) {
        if (c == ' ' || c == '\t') return 0;
        if (Character.isLetter(c)) return 1;
        if (Character.isDigit(c)) return 2;
        if (c == '$') return 3;
        if (c == '_') return 4;
        if ("=+-<>!%".indexOf(c) != -1) return 5;
        if (c == '&') return 6;
        if (c == '|') return 7;
        if (c == '/') return 8;
        if (c == '*') return 9;
        if (";,(){}[]".indexOf(c) != -1) return 10;
        if (c == '\n') return 12;
        if (c == '#') return 13;
        return 11;
    }

    static String getType(String token) {
        if (Arrays.asList(MotCle).contains(token)) return "MOT_CLE";
        if (Arrays.asList(MotClePerso).contains(token)) return "MOT_CLE_PERSONNALISE";
        if (token.matches("\\$[a-zA-Z_][a-zA-Z0-9_]*")) return "VARIABLE";
        if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return "IDENTIFICATEUR";
        if (token.matches("[0-9]+")) return "NOMBRE";
        if (token.matches("(==|<=|>=|!=|\\+\\+|--|&&|\\|\\||[+\\-*/%=&<>!])")) return "OPERATEUR";
        if (token.matches("[;,(){}\\[\\]]")) return "SEPARATEUR";
        return "AUTRE";
    }

    static class LexResult {
        String[] tokens;
        String[] types;
    }

    static LexResult analyserLexicalement(String code) {
        int i = 0;
        int Ec = 0;
        String token = "";
        boolean commentaireLigne = false;
        boolean commentaireBloc  = false;

        String[] tempTokens = new String[code.length()];
        String[] tempTypes = new String[code.length()];
        int tokenCount = 0;

        while (i < code.length()) {

            char c = code.charAt(i);

            if (commentaireLigne) {
                if (c == '\n') commentaireLigne = false;
                i++; continue;
            }
            if (commentaireBloc) {
                if (c == '*' && i+1 < code.length() && code.charAt(i+1) == '/') {
                    commentaireBloc = false; 
                    i += 2; 
                    continue;
                }
                i++; 
                continue;
            }

            if (c == '/' && i+1 < code.length()) {
                if (code.charAt(i+1) == '/') { commentaireLigne = true; i += 2; continue; }
                if (code.charAt(i+1) == '*') { commentaireBloc = true; i += 2; continue; }
            }

            if (c == '#') { commentaireLigne = true; i++; continue; }

            if (Character.isWhitespace(c)) {
                if (!token.isEmpty() && EtatFin[Ec]) {
                    System.out.println("Token: \"" + token + "\" Type: " + getType(token));
                    tempTokens[tokenCount] = token;
                    tempTypes[tokenCount] = getType(token);
                    tokenCount++;
                }
                token = "";
                Ec = 0;
                i++;
                continue;
            }

            int col = chartocol(c);
            int next = matrice[Ec][col];

            if (next == -1) {

                if (!token.isEmpty() && EtatFin[Ec]) {

                    if (i < code.length()) {
                        char nx = code.charAt(i);
                        String two = "" + token + nx;

                        if (two.equals("==") || two.equals("!=") ||
                            two.equals("<=") || two.equals(">=") ||
                            two.equals("++") || two.equals("--") ||
                            two.equals("&&") || two.equals("||")) {
                            token += nx;
                            i++;
                        }
                    }

                    System.out.println("Token: \"" + token + "\" Type: " + getType(token));
                    tempTokens[tokenCount] = token;
                    tempTypes[tokenCount] = getType(token);
                    tokenCount++;
                }

                token = "";
                Ec = 0;

                // ✅ FIX : relire le même caractère
                continue;
            }

            token += c;
            Ec = next;
            i++;
        }

        if (!token.isEmpty() && EtatFin[Ec]) {
            System.out.println("Token: \"" + token + "\" Type: " + getType(token));
            tempTokens[tokenCount] = token;
            tempTypes[tokenCount] = getType(token);
            tokenCount++;
        }

        LexResult res = new LexResult();
        res.tokens = Arrays.copyOf(tempTokens, tokenCount);
        res.types  = Arrays.copyOf(tempTypes, tokenCount);

        return res;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez la chaine :");
        String chaine = sc.nextLine();
        AnalyseLexicale.LexResult res = analyserLexicalement(chaine);
        AnalyseSyntaxique.tokens = res.tokens;
        AnalyseSyntaxique.types  = res.types;

        AnalyseSyntaxique.i = 0;
        AnalyseSyntaxique.erreur = false;
        Z();
    }
    
}
