package cherokee.dictionary.relational

import groovy.sql.Sql

class SelectData {

//    def DRIVER_NAME = "com.mysql.cj.jdbc.Driver"//'org.gjt.mm.mysql.Driver'
//    def SQL = Sql.newInstance('jdbc:mysql://localhost/cherokeedictionary?useUnicode=yes&characterEncoding=UTF-8&serverTimezone=America/Chicago', 'root', 'Tk02030#', DRIVER_NAME)
//
//    SQL.eachRow ( "select * from likespreadsheets where id = 73250" ) {
//        println it
//    }

/*String syllabary
String phonetic
String tone
String autotranslit

thirdpresent
nounadj plural
vfirstpres
vthirdpast
vthirdpres
vsecondimper
vthirdinf

*/
/*
syllabaryb
entrya
entrytone
entrytranslit

nounadjpluralsyllf
nounadjplurale
nounadjpluraltone
nounadjpluraltranslit

vfirstpresh
vfirstpresg
vfirstprestone
vfirstprestranslit

vthirdpastsyllj
vthirdpasti
vthirdpasttone
vthirdpasttranslit

vthirdpressylll
vthirdpresk
vthirdprestone
vthirdprestranslit

vsecondimpersylln
vsecondimperm
vsecondimpertone
vsecondimpertranslit

vthirdinfsyllp
vthirdinfo
vthirdinftone
vthirdinftranslit



sentencesyllr
sentenceq
sentenceenglishs
sentencetranslit

partofspeechc
definitiond

crossreferencet

source
definitionlarge
etymology
notes
category
*/


/*

def str = """
entrya
syllabaryb
partofspeechc
definitiond
nounadjplurale
nounadjpluralsyllf
vfirstpresg
vfirstpresh
vthirdpasti
vthirdpastsyllj
vthirdpresk
vthirdpressylll
vsecondimperm
vsecondimpersylln
vthirdinfo
vthirdinfsyllp
sentenceq
sentencesyllr
sentenceenglishs
crossreferencet

nounadjpluraltranslit
entrytranslit
vfirstprestranslit
vthirdpasttranslit
vthirdprestranslit
vsecondimpertranslit
vthirdinftranslit
sentencetranslit

entrytone
nounadjpluraltone
vfirstprestone
vthirdpasttone
vthirdprestone
vsecondimpertone
vthirdinftone

source
definitionlarge
etymology
notes
category
"""

str.eachLine {
    if (it != "") {
        print " or ${it} like '%,%'"
    }
}*/

}