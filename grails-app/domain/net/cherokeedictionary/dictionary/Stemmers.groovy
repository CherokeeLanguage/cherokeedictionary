package net.cherokeedictionary.dictionary

class Stemmers {
    static constraints = {
        partofspeech blank: true, nullable: true
        definition blank: true, nullable: true
        vthirdpresKing blank: true, nullable: true
        vthirdpres blank: true, nullable: true
        vthirdpresStemmer blank: true, nullable: true
        vfirstpres blank: true, nullable: true
        vfirstpresKing blank: true, nullable: true
        vthirdpast blank: true, nullable: true
        vthirdpastKing blank: true, nullable: true
        vthirdpreshabitual blank: true, nullable: true
        vthirdpresKinghabitual blank: true, nullable: true
        vsecondimper blank: true, nullable: true
        vsecondimperKing blank: true, nullable: true
        vthirdinf blank: true, nullable: true
        vthirdinfKing blank: true, nullable: true
        vfirstpresStemmer blank: true, nullable: true
        vthirdpastStemmer blank: true, nullable: true
        vthirdpresStemmerhabitual blank: true, nullable: true
        vsecondimperStemmer blank: true, nullable: true
        vthirdinfStemmer blank: true, nullable: true
    }

    String likespreadsheetsid = ""
    String partofspeech = ""
    String definition = ""
    String source = ""

    String vthirdpres = ""
    String vthirdpresKing = ""
    String vthirdpresStemmer = ""

    String vfirstpres = ""
    String vfirstpresKing = ""
    String vfirstpresStemmer = ""

    String vthirdpast = ""
    String vthirdpastKing = ""
    String vthirdpastStemmer = ""

    String vthirdpreshabitual = ""
    String vthirdpresKinghabitual = ""
    String vthirdpresStemmerhabitual = ""

    String vsecondimper = ""
    String vsecondimperKing = ""
    String vsecondimperStemmer = ""

    String vthirdinf = ""
    String vthirdinfKing = ""
    String vthirdinfStemmer = ""


    @Override
    public String toString() {
        return "Stemmers{" +
                "id=" + id +
                ", partofspeech='" + partofspeech + '\'' +
                ", definition='" + definition + '\'' +
                ", vfirstpres='" + vfirstpres + '\'' +
                ", vfirstpresKing='" + vfirstpresKing + '\'' +
                ", vfirstpresStemmer='" + vfirstpresStemmer + '\'' +
                ", vthirdpast='" + vthirdpast + '\'' +
                ", vthirdpastKing='" + vthirdpastKing + '\'' +
                ", vthirdpastStemmer='" + vthirdpastStemmer + '\'' +
                ", vthirdpres='" + vthirdpreshabitual + '\'' +
                ", vthirdpresKing='" + vthirdpresKinghabitual + '\'' +
                ", vthirdpresStemmer='" + vthirdpresStemmerhabitual + '\'' +
                ", vsecondimper='" + vsecondimper + '\'' +
                ", vsecondimperKing='" + vsecondimperKing + '\'' +
                ", vsecondimperStemmer='" + vsecondimperStemmer + '\'' +
                ", vthirdinf='" + vthirdinf + '\'' +
                ", vthirdinfKing='" + vthirdinfKing + '\'' +
                ", vthirdinfStemmer='" + vthirdinfStemmer + '\'' +
                ", version=" + version +
                '}';
    }
}
