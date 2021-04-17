package cherokee.dictionary

import cherokee.Admin

class History {
    static constraints = {
        entrya blank: true, nullable: true
        syllabaryb blank: true, nullable: true
        partofspeechc blank: true, nullable: true
        definitiond blank: true, nullable: true
        nounadjplurale blank: true, nullable: true
        nounadjpluralsyllf blank: true, nullable: true
        vfirstpresg blank: true, nullable: true
        vfirstpresh blank: true, nullable: true
        vthirdpasti blank: true, nullable: true
        vthirdpastsyllj blank: true, nullable: true
        vthirdpresk blank: true, nullable: true
        vthirdpressylll blank: true, nullable: true
        vsecondimperm blank: true, nullable: true
        vsecondimpersylln blank: true, nullable: true
        vthirdinfo blank: true, nullable: true
        vthirdinfsyllp blank: true, nullable: true
        sentenceq blank: true, nullable: true
        sentencesyllr blank: true, nullable: true
        sentenceenglishs blank: true, nullable: true
        crossreferencet blank: true, nullable: true
        nounadjpluraltranslit blank: true, nullable: true
        entrytranslit blank: true, nullable: true
        vfirstprestranslit blank: true, nullable: true
        vthirdpasttranslit blank: true, nullable: true
        vthirdprestranslit blank: true, nullable: true
        vsecondimpertranslit blank: true, nullable: true
        vthirdinftranslit blank: true, nullable: true
        sentencetranslit blank: true, nullable: true

        entrytone blank: true, nullable: true
        nounadjpluraltone blank: true, nullable: true
        vfirstprestone blank: true, nullable: true
        vthirdpasttone blank: true, nullable: true
        vthirdprestone blank: true, nullable: true
        vsecondimpertone blank: true, nullable: true
        vthirdinftone blank: true, nullable: true

        source blank: true, nullable: true
        definitionlarge blank: true, nullable: true
        etymology blank: true, nullable: true
        notes blank: true, nullable: true
        category blank: true, nullable: true
    }

    Admin admin
    String entrya;
    String syllabaryb;
    String partofspeechc;
    String definitiond;
    String nounadjplurale;
    String nounadjpluralsyllf;
    String vfirstpresg;
    String vfirstpresh;
    String vthirdpasti;
    String vthirdpastsyllj;
    String vthirdpresk;
    String vthirdpressylll;
    String vsecondimperm;
    String vsecondimpersylln;
    String vthirdinfo;
    String vthirdinfsyllp;
    String sentenceq;
    String sentencesyllr;
    String sentenceenglishs;
    String crossreferencet;

    String nounadjpluraltranslit
    String entrytranslit
    String vfirstprestranslit
    String vthirdpasttranslit
    String vthirdprestranslit
    String vsecondimpertranslit
    String vthirdinftranslit
    String sentencetranslit

    String entrytone
    String nounadjpluraltone
    String vfirstprestone
    String vthirdpasttone
    String vthirdprestone
    String vsecondimpertone
    String vthirdinftone

    String source;
    String definitionlarge;
    String etymology;
    String notes;
    String category;
    Date modified
}
