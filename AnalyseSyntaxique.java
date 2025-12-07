package Projet_Mini_Compilateur;

public class AnalyseSyntaxique {

    public static String[] tokens;
    public static String[] types;

    public static int i = 0;
    public static String tc;
    public static String ttype;
    public static boolean erreur = false;

    // Avancer dans les tokens
    public static void nextToken() {
        if (i < tokens.length) {
            tc = tokens[i];
            ttype = types[i];
            i++;
        } else {
            tc = "#";
            ttype = "EOF";
        }
    }

    // Z → S EOF
    public static void Z() {
        nextToken();
        S();
        if (ttype.equals("EOF") && !erreur) {
            System.out.println("Chaine acceptee");
        } else {
            System.out.println("Chaine non acceptee");
        }
    }

    // S → while ( COND ) { INST_LIST }
    public static void S() {
        if (!tc.equals("while")) {
            signalerErreur("mot cle 'while' attendu");
            nextToken();
        } else {
            nextToken();
        }

        if (!tc.equals("(")) {
            signalerErreur("'(' attendu");
        }
        nextToken();

        COND();

        if (!tc.equals(")")) {
            signalerErreur("')' attendu");
        }
        nextToken();

        if (!tc.equals("{")) {
            signalerErreur("'{' attendu");
        }
        nextToken();

        INST_LIST();

        if (!tc.equals("}")) {
            signalerErreur("'}' attendu");
        } else {
            nextToken();
        }
    }

    // COND → VARIABLE OPERATEUR VARIABLE | VARIABLE OPERATEUR NOMBRE
    public static void COND() {
        if (!ttype.equals("VARIABLE")) {
            signalerErreur("pas une variable");
            nextToken();
        } else {
            nextToken();
        }

        if (!ttype.equals("OPERATEUR")) {
            signalerErreur("pas un operateur");
            nextToken();
        } else {
            nextToken();
        }

        if (!ttype.equals("VARIABLE") && !ttype.equals("NOMBRE")) {
            signalerErreur("pas de variable ou nombre");
            nextToken();
        } else {
            nextToken();
        }
    }

    // INST_LIST → INST INST_LIST | ε
    public static void INST_LIST() {
        while (ttype.equals("VARIABLE") || tc.equals("while")) {
            INST();
        }
    }

    // INST → VARIABLE ;  | VARIABLE OPERATEUR VARIABLE; | VARIABLE OPERATEUR NOMBRE ; | S
    public static void INST() {
        if (tc.equals("while")) {
            S();
            return;
        }

        if (ttype.equals("VARIABLE")) {
            nextToken();

            if (tc.equals(";")) {
                nextToken();
                return;
            }

            if (!ttype.equals("OPERATEUR")) {
                signalerErreur("pas un operateur ou ';'");
                nextToken();
            } else {
                nextToken();
            }

            if (!ttype.equals("VARIABLE") && !ttype.equals("NOMBRE")) {
                signalerErreur("pas de nombre apres variable");
                nextToken();
            } else {
                nextToken();
            }

            if (!tc.equals(";")) {
                signalerErreur("';' attendu");
                if (!tc.equals("#") && !tc.equals("}")) nextToken();
            } else {
                nextToken();
            }
            return;
        }

        // Instruction invalide
        signalerErreur("instruction invalide");
        nextToken();
    }

    // Fonction message d’erreur
    public static void signalerErreur(String msg) {
        System.out.println("Erreur position " + i + " : " + msg);
        erreur = true;
    }
}
