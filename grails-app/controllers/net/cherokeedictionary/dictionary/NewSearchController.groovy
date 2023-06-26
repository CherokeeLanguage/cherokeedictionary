package net.cherokeedictionary.dictionary


import net.cherokeedictionary.dictionary.Cherokeewordsearch
import net.cherokeedictionary.dictionary.Likespreadsheets
import net.cherokeedictionary.transliteration.SyllabaryUtil
import grails.converters.JSON
import org.grails.web.json.JSONObject

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import javax.servlet.ServletOutputStream

import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.Phrase
import com.itextpdf.text.Font
import com.itextpdf.text.pdf.PdfPTable

//@Transactional(readOnly = false)
class NewSearchController {
    def newSearchService
    def frontEndFormatService
    def assetResourceLocator
//    def elasticSearchService

    def index() {
//        println("params " + params);
//        elasticSearchService.index(Likespreadsheets)
    }

    def processSentences() {
        def likespreadsheets = Likespreadsheets.findAll('from Likespreadsheets where sentencetranslit=? and sentencesyllr != ?', ['', ''])
        println likespreadsheets.size();
        def su = new SyllabaryUtil();
        likespreadsheets.each {
            Likespreadsheets ls = it;
            println ls.sentencesyllr
            if (ls.sentencesyllr) {
                def tmp = SyllabaryUtil.parseSyllabaryWithPunctuationAndLatin(ls.sentencesyllr)
                ls.sentencetranslit = tmp
                println "saving";
                if (!ls.save(flush: true)) {
                    ls.errors.allErrors.each {
                        println it
                    }
                }
            }
        }
    }

    def dictionary() {
        // pagination code
//        List searchResults = session["results"]
//        List displayResults = searchResults
//        if (params.index && searchResults) {
//            def searchResultsSize = searchResults.size()
//            if (searchResultsSize >= 40) {
//                def index = 0
//                if (params.index) {
//                    index = Integer.parseInt(params.index)
//                }
//
//                if (params.goBack == "true") {
//                    println "going back a page"
//                }
//
//                if (index < searchResultsSize) {
//                    def tmpIndex = index + searchResultsSize;
//                    if (tmpIndex >= searchResultsSize) {
//                        tmpIndex = index
//                    }
//
//                    index = tmpIndex
//                }
//
//                def parmsOffset = index + 40
//
//                if (parmsOffset >= searchResultsSize) {
//                    parmsOffset = searchResultsSize
//                }
//
//                println "index ${index}"
//                println "offset ${parmsOffset}"
//
//                params.index = index;
//
////                if (index >= 40) {
////                    params.goBack = true;
////                    if (index - 40 <= 0) {
////                        params.backIndex = 0
////                        params.backOffset = 40
////                    }
////                }
//
//                displayResults = searchResults.subList(index, parmsOffset)
//            } else {
//                displayResults = searchResults.subList(0, 40)
//            }
//        } else {
//            searchResults = newSearchService.search(params)
//            searchResults = searchResults.subList(0, 40)
//            session["results"] = searchResults;

//        }

//        if (!searchResults || (searchResults && searchResults.isEmpty())) {
//            session.removeAttribute("results")
//            redirect(uri: "/", params: [noresults: true])
//        } else {
//            return [entries: searchResults, params: params]
//        }

        //original code
        def searchResults = newSearchService.search(params)
        session["results"] = searchResults;

        if (searchResults.isEmpty()) {
            redirect(uri: "/", params: [noresults: true])
        } else {
            return [entries: searchResults, params: params]
        }
    }

    def advancedSearch() {
        def searchResults = newSearchService.advancedSearch(params)

        if (searchResults.isEmpty()) {
            redirect(uri: "/advancedSearch", params: [noresults: true])
        } else {
            render(view:'dictionary', model:[entries: searchResults])
        }
    }

    def share() {
        def value = Likespreadsheets.findById(params.id);
        render(view:'dictionary', model:[entries: [value], expand:true])
    }

    def individual() {
        def value = Likespreadsheets.findById(params.id);

        return [entry:value]
    }

    def xrefdisplay() {
        def currentItem = newSearchService.xrefById(params.current)
        def xrefItem = newSearchService.xrefSearch(params.old)

        render(view:"xrefdisplay", model: [xref: xrefItem, current: currentItem])
    }

    def conjdisplay() {
        render(view:"conjdisplay")
    }

    def verbgenerator() {
        render(view:"verbgenerator")
    }

    def conjtabledisplay() {
        render(view:"conjtabledisplay")
    }

    def returnMe = {
        JSONObject userJson = JSON.parse(params.json)
        String id = userJson.get("id")

        Likespreadsheets lks = Likespreadsheets.get(id)
//        lks.id = userJson.get("id")
//        lks.version = userJson.get("version")
        lks.crossreferencet = userJson.get("crossreferencet")
        lks.definitiond = userJson.get("definitiond")
        lks.entrya = userJson.get("entrya")
        lks.nounadjplurale = userJson.get("nounadjplurale")
        lks.nounadjpluralsyllf = userJson.get("nounadjpluralsyllf")
        lks.partofspeechc = userJson.get("partofspeechc")
        lks.sentenceenglishs = userJson.get("sentenceenglishs")
        lks.sentenceq = userJson.get("sentenceq")
        lks.sentencesyllr = userJson.get("sentencesyllr")
        lks.syllabaryb = userJson.get("syllabaryb")
        lks.vfirstpresg = userJson.get("vfirstpresg")
        lks.vfirstpresh = userJson.get("vfirstpresh")
        lks.vsecondimperm = userJson.get("vsecondimperm")
        lks.vsecondimpersylln = userJson.get("vsecondimpersylln")
        lks.vthirdinfo = userJson.get("vthirdinfo")
        lks.vthirdinfsyllp = userJson.get("vthirdinfsyllp")
        lks.vthirdpasti = userJson.get("vthirdpasti")
        lks.vthirdpastsyllj = userJson.get("vthirdpastsyllj")
        lks.vthirdpresk = userJson.get("vthirdpresk")
        lks.vthirdpressylll = userJson.get("vthirdpressylll")
        /*lks.entrytranslit = userJson.get("entrytranslit")
        lks.nounadjpluraltranslit = userJson.get("nounadjpluraltranslit")
        lks.sentencetranslit = userJson.get("sentencetranslit")
        lks.vfirstprestranslit = userJson.get("vfirstprestranslit")
        lks.vsecondimpertranslit = userJson.get("vsecondimpertranslit")
        lks.vthirdinftranslit = userJson.get("vthirdinftranslit")
        lks.vthirdpasttranslit = userJson.get("vthirdpasttranslit")
        lks.vthirdprestranslit = userJson.get("vthirdprestranslit")*/
        lks.entrytone = userJson.get("entrytone")
        lks.nounadjpluraltone = userJson.get("nounadjpluraltone")
        lks.vfirstprestone = userJson.get("vfirstprestone")
        lks.vsecondimpertone = userJson.get("vsecondimpertone")
        lks.vthirdinftone = userJson.get("vthirdinftone")
        lks.vthirdpasttone = userJson.get("vthirdpasttone")
        lks.vthirdprestone = userJson.get("vthirdprestone")

        lks.save(flush:true);

        JSONObject obj = new JSONObject();
        obj.put("success", "true");
        render obj as JSON
    }

    def pronunciation() {
        def latinValue = params.latinValue;
//        println latinValue;

//        println(raw(frontEndFormatService.pronunciation(latinValue)));

        JSONObject obj = new JSONObject();
        obj.put("transvalue", raw(frontEndFormatService.pronunciation(latinValue)));
        render obj as JSON
    }

    def intonation() {
        def toneValue = params.toneValue;
        JSONObject obj = new JSONObject();
        obj.put("tonevalue", raw(frontEndFormatService.intonation(toneValue)));
        obj.put("transvalue", raw(frontEndFormatService.pronunciation(toneValue)))
        render obj as JSON
    }

    def ajaxEnglishFinder = {
        def englishresults = Likespreadsheets.withCriteria {
            like 'definitiond', '%' + params.term + '%'
        }

//        println params.term

        def returnList = []
        englishresults.each {
            returnList << it.definitiond
        }

        render (returnList as JSON)
    }

    def ajaxTsalagiFinder = {
        def tsalagiList = Likespreadsheets.withCriteria {
            or {
                like 'entrya', '%' + params.term + '%'
                like 'nounadjpluraltranslit', '%' + params.term + '%'
                like 'vfirstprestranslit', '%' + params.term + '%'
                like 'vsecondimpertranslit', '%' + params.term + '%'
                like 'vthirdinftranslit', '%' + params.term + '%'
                like 'vthirdpasttranslit', '%' + params.term + '%'
                like 'vthirdprestranslit', '%' + params.term + '%'
            }
        }

        def returnList = []
        tsalagiList.each {
            returnList << it.entrya
        }

        render (returnList as JSON)
    }

    def ajaxSyllabaryFinder = {
//        println params.term
        def syllabaryList = Likespreadsheets.withCriteria {
            or {
                like 'nounadjpluralsyllf', '%' + params.term + '%'
                like 'syllabaryb', '%' + params.term + '%'
                like 'vfirstpresh', '%' + params.term + '%'
                like 'vsecondimpersylln', '%' + params.term + '%'
                like 'vthirdinfsyllp', '%' + params.term + '%'
                like 'vthirdpastsyllj', '%' + params.term + '%'
                like 'vthirdpressylll', '%' + params.term + '%'
            }
        }

        def returnList = []
        syllabaryList.each {
            returnList << it.syllabaryb
        }

        render (returnList as JSON)
    }

    def ajaxTsalagiKeyboard = {
        def su = new SyllabaryUtil();
        def returnList = []
        returnList << su.tsalagiToSyllabary(params.term);
        return render (returnList as JSON)
    }

    def ajaxTsalagi2Finder = {
        def tsalagiList = Cherokeewordsearch.withCriteria {
            or {
                like 'entrya', '%' + params.term + '%'
                like 'syllabaryb', '%' + params.term + '%'
            }
        }

        def returnList = []
        tsalagiList.each {
            returnList << it.entrya
        }

        render (returnList as JSON)
    }

    def ajaxSyllabary2Finder = {
        def syllabaryList = Cherokeewordsearch.withCriteria {
            or {
                like 'entrya', '%' + params.term + '%'
                like 'syllabaryb', '%' + params.term + '%'
            }
        }

        def returnList = []
        syllabaryList.each {
            returnList << it.syllabaryb
        }

        render (returnList as JSON)
    }

    def allResults() {
        //select syllabaryb, definitiond,vfirstpresh,vsecondimpersylln,vthirdinfsyllp,vthirdpastsyllj,vthirdpressylll
        def lst = Likespreadsheets.findAll("from Likespreadsheets where syllabaryb != '' and (source='ced' or source='rrd') order by vfirstpresh")

        def values = []

        lst.each {
            it.syllabaryb ? values << "${it.syllabaryb}, ${it.definitiond}" : ""
            it.vfirstpresh ? values << "${it.vfirstpresh}, 1st pres ${it.definitiond}" : ""
            it.vsecondimpersylln ? values << "${it.vsecondimpersylln}, 2nd imper ${it.definitiond}" : ""
            it.vthirdinfsyllp ? values << "${it.vthirdinfsyllp}, 3rd inf ${it.definitiond}" : ""
            it.vthirdpastsyllj ? values << "${it.vthirdpastsyllj}, 3rd past ${it.definitiond}" : ""
            it.vthirdpressylll ? values << "${it.vthirdpressylll}, 3rd pres ${it.definitiond}" : ""
        }

        render(view: 'allResults', model:[results: values])
    }

    def download() {
        def lst = []

        session["results"].each {
            lst << it
        }

        ServletOutputStream outs = response.outputStream
        response.status = response.SC_OK

        if (params.id == "excel") {
            processExcel(outs, lst)
        } else if (params.id == "csv" || params.id == "tab") {
            processCSVTAB(outs, lst, params.id)
        } else {
            //need to work on the pdf still
            generatePDF(outs, lst)
        }

        outs.flush()
        outs.close()

        render(view:'dictionary')
    }

    def verbGenerator(tense, tone, syllabary, document) {
        PdfPTable verbs = new PdfPTable(4)
        verbs.setTotalWidth([ 2, 17, 40, 40] as float[]);
        verbs.getDefaultCell().setBorderColor(BaseColor.WHITE)
        verbs.addCell(new Phrase("", new Font(BaseFont.createFont(), 11)))
        verbs.addCell(new Phrase(tense, new Font(BaseFont.createFont(), 11)))
        verbs.addCell(new Phrase(tone, new Font(BaseFont.createFont(), 11)))
        verbs.addCell(new Phrase(syllabary, new Font(BaseFont.createFont(), 11)))
        document.add(verbs)
    }

    def generateTable(lks, document) {
        PdfPTable mainEntry = new PdfPTable(5)
        mainEntry.setTotalWidth([ 75, 2, 10,20, 75] as float[]);
        mainEntry.getDefaultCell().setBorderColor(BaseColor.WHITE)
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
        mainEntry.addCell(new Phrase(lks.entrya, boldFont))
        mainEntry.addCell(new Phrase(lks.syllabaryb, new Font(BaseFont.createFont(), 11)))
        Phrase source = new Phrase(lks.source, new Font(BaseFont.createFont(), 11))
        Phrase pos = new Phrase(lks.partofspeechc, new Font(BaseFont.createFont(), 11))
        mainEntry.addCell(pos)
        mainEntry.addCell(source)
        mainEntry.addCell(new Phrase(lks.definitiond, new Font(BaseFont.createFont(), 11)))
        document.add(mainEntry)

        PdfPTable mainEntry2 = new PdfPTable(2)
        mainEntry2.setTotalWidth([ 1, 50] as float[]);
        mainEntry2.getDefaultCell().setBorderColor(BaseColor.WHITE)
        mainEntry2.addCell(new Phrase("", new Font(BaseFont.createFont(), 11)))
        mainEntry2.addCell(new Phrase(lks.entrytone, new Font(BaseFont.createFont(), 11)))
        document.add(mainEntry2)

        if (lks.partofspeechc == "n" || lks.partofspeechc == "adj") {
            if (lks.nounadjplurale) {
                PdfPTable nounadj = new PdfPTable(4)
                nounadj.setTotalWidth([1, 3, 30, 30] as float[]);
                nounadj.getDefaultCell().setBorderColor(BaseColor.WHITE)
                nounadj.addCell(new Phrase("", new Font(BaseFont.createFont(), 11)))
                nounadj.addCell(new Phrase("pl.", new Font(BaseFont.createFont(), 11)))
                nounadj.addCell(new Phrase(lks.nounadjplurale, new Font(BaseFont.createFont(), 11)))
                nounadj.addCell(new Phrase(lks.nounadjpluralsyllf, new Font(BaseFont.createFont(), 11)))
                document.add(nounadj)
            }
        }

        if (lks.partofspeechc == 'vi' || lks.partofspeechc == "vt") {
            addEmpty(document)
            verbGenerator("1st Pres", lks.vfirstpresg ?: "", lks.vfirstpresh, document);
            verbGenerator("3rd Past", lks.vthirdpasti, lks.vthirdpastsyllj, document);
            verbGenerator("3rd Pres Hab", lks.vthirdpresk, lks.vthirdpressylll, document);
            verbGenerator("2nd Imp", lks.vsecondimperm, lks.vsecondimpersylln, document);
            verbGenerator("3rd Inf", lks.vthirdinfo, lks.vthirdinfsyllp, document);
        }

        if (lks.sentenceq) {
            addEmpty(document)
            PdfPTable sentences = new PdfPTable(2)
            sentences.setTotalWidth([1, 50] as float[]);
            sentences.getDefaultCell().setBorderColor(BaseColor.WHITE)
            sentences.addCell(new Phrase("", new Font(BaseFont.createFont(), 11)))
            sentences.addCell(new Phrase(lks.sentenceq, new Font(BaseFont.createFont(), 11)))

            sentences.addCell(new Phrase("", new Font(BaseFont.createFont(), 11)))
            sentences.addCell(new Phrase(lks.sentencesyllr, new Font(BaseFont.createFont(), 11)))

            sentences.addCell(new Phrase("", new Font(BaseFont.createFont(), 11)))
            sentences.addCell(new Phrase(lks.sentenceenglishs, new Font(BaseFont.createFont(), 11)))
            document.add(sentences)
        }
    }

    def addEmpty(document) {
        PdfPTable empty = new PdfPTable(1)
        empty.getDefaultCell().setBorderColor(BaseColor.WHITE)
        Phrase phrase = new Phrase("    ", new Font(BaseFont.createFont(), 11))
        empty.addCell(phrase)
        document.add(empty)
    }

    def generatePDF(outs, lst) {
//        String FILE = "~/PositionPdf.pdf";
        response.contentType = "text/pdf;charset=UTF-8";
        response.setHeader "Content-disposition", "attachment; filename=entries.pdf"

        Document document = new Document();

        PdfWriter.getInstance(document, outs);
        document.open();

        //set up for unicode font to display cherokee in pdf... does not work but does not cause an error
//        File audioFile = grailsApplication.mainContext.getResource("/font/Digohweli_1_7.ttf").file
//        BaseFont bf = BaseFont.createFont(audioFile.getAbsolutePath(), BaseFont.CP1250, BaseFont.EMBEDDED);
//        Font font = new Font(bf, 12);
////        BaseFont bf = BaseFont.createFont(audioFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        Paragraph p = new Paragraph("ᎦᏔᏍᎩᎠ", new Font(bf, 22));
//
//        document.add(p);


        //set font color
        //set bold
        // adjust table so widths of columns don't wrap or I can set the width with wrap and it breaks at a space
        // add source to each entry (later this will be a delimited field)
        // add what dialect it is
        // also parse <u></u> into an underline command
        lst.each {
            if (it instanceof Likespreadsheets) {
                generateTable(it, document)
                addEmpty(document)
            }
        }

        document.close();
    }

    def processCSVTAB(outs, lst, csvtab) {
        String extension = (csvtab == "csv") ? "csv" : "tsv"
        String filename = "en-chr.$extension"
        response.contentType = "text/csv;charset=UTF-8";
        response.setHeader "Content-disposition", "attachment; filename=${filename}"

        outs << "COUNT \t ${lst.size()}\n"

        headings.eachWithIndex {str, idx2 ->
            outs << "${str}" + (params.id == "tab" ? "\t" : ",")//String.format(format, it.value[0], it.value[1])
        }

        outs << "\n"

        lst.eachWithIndex {book, idx ->
            headings.eachWithIndex {str, idx2 ->
                def tmp = book."${str}"
                if (tmp == null) {
                    tmp = ""
                }

                outs << "\"${tmp}\"" + (params.id == "tab" ? "\t" : ",")//String.format(format, it.value[0], it.value[1])
            }

            outs << "\n"
        }
    }

    def processExcel(outs, lst) {
        response.setHeader "Content-disposition", "attachment; filename=dictionaryResults.xlsx"
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"

        XSSFWorkbook workbook = new XSSFWorkbook()
        XSSFSheet sheet = workbook.createSheet("Results")
        Row headingRow = sheet.createRow(0)
        headings.eachWithIndex {str, idx ->
            Cell cell3 = headingRow.createCell(idx)
            cell3.setCellValue(str)
        }

        lst.eachWithIndex {book, idx ->
            Row row = sheet.createRow(idx + 1)

            headings.eachWithIndex {str, idx2 ->
                Cell cell = row.createCell(idx2)
                cell.setCellValue(book."${str}")
            }
        }

        workbook.write(outs);
    }

    def medicalTerms() {
        def lst = Likespreadsheets.findAllWhere("source =? or source = ? order by definitiond, source", ["ncmed", "cnomed"])
//        select definitiond, entrya, syllabaryb, source from likespreadsheets where source='ncmed' or source='cnomed' order by definitiond, source;
        render(view: 'medicalTerms', model:[results: lst])
    }

    def headings = ["entrya",
                   "syllabaryb",
                   "partofspeechc",
                   "definitiond",
                   "nounadjplurale",
                   "nounadjpluralsyllf",
                   "vfirstpresg",
                   "vfirstpresh",
                   "vthirdpasti",
                   "vthirdpastsyllj",
                   "vthirdpresk",
                   "vthirdpressylll",
                   "vsecondimperm",
                   "vsecondimpersylln",
                   "vthirdinfo",
                   "vthirdinfsyllp",
                   "sentenceq",
                   "sentencesyllr",
                   "sentenceenglishs",
                   "crossreferencet",
                   /*"nounadjpluraltranslit",
                   "entrytranslit",
                   "vfirstprestranslit",
                   "vthirdpasttranslit",
                   "vthirdprestranslit",
                   "vsecondimpertranslit",
                   "vthirdinftranslit",
                   "sentencetranslit",*/
                   "entrytone",
                   "nounadjpluraltone",
                   "vfirstprestone",
                   "vthirdpasttone",
                   "vthirdprestone",
                   "vsecondimpertone",
                   "vthirdinftone",
                   "source",
                   "definitionlarge",
                   "etymology",
                   "notes",
                   "category"]

}
