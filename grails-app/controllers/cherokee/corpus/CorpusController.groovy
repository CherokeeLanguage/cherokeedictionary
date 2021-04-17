package cherokee.corpus

import cherokee.dictionary.Likespreadsheets
import grails.converters.JSON
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import javax.servlet.ServletOutputStream

class CorpusController {
    private static TAB_DELIMITED_FORMAT = "%s\t%s\n"
    private static CSV_DELIMITED_FORMAT = "\"%s\", \"%s\"\n"
//    def excelCreationService

    def corpusMain() {}

    def adminDownload() {
        def lst = [:]

        def likespreadsheets = Likespreadsheets.findAll('from Likespreadsheets where sentenceenglishs != ? and sentencesyllr != ?', ['',''])
        def nlpCorpus = NLPCorpus.list()
        nlpCorpus.each {

            lst.put(lst.size(), [it.syllabary, it.english])
        }

        likespreadsheets.each {
            def syll = it.sentencesyllr
            def eng = it.sentenceenglishs
            eng = process(eng ?: "")
            syll = process(syll ?: "")
            lst.put(lst.size(), [syll, eng])
        }

        ServletOutputStream outs = response.outputStream
        response.status = response.SC_OK

        if (params.id != "excel") {
            processCSVTAB(outs, lst)
        } else {
            processExcel(outs, lst)
        }

        outs.flush()
        outs.close()

        render(view:'corpusMain')
    }

    def download() {
        def lst = [:]

        def likespreadsheets = Likespreadsheets.findAll('from Likespreadsheets where sentenceenglishs != ? and sentencesyllr != ?', ['',''])

        //I don't want to include Charlotte's web for the regular viewer per request
        def corpusSourceId = CorpusSource.findAll("from CorpusSource c where c.sourceName = ?", ['Charlottes Web'])
//        println corpusSourceId.id.get(0)
        def nlpCorpus = NLPCorpus.findAll('from NLPCorpus where corpus_source_id > 45')
//        println "nlpcorpus count " + nlpCorpus.size()
        //execute the query then iterate
        nlpCorpus.each {
            lst.put(lst.size(), [it.syllabary, it.english])
        }

        likespreadsheets.each {
            def syll = it.sentencesyllr
            def eng = it.sentenceenglishs
            eng = process(eng ?: "")
            syll = process(syll ?: "")
            lst.put(lst.size(), [syll, eng])
        }

        ServletOutputStream outs = response.outputStream
        response.status = response.SC_OK

        if (params.id != "excel") {
            processCSVTAB(outs, lst)
        } else {
            processExcel(outs, lst)
        }

        outs.flush()
        outs.close()

        render(view:'corpusMain')

    }

    def viewByCorpusSource() {
        def corpusSource = CorpusSource.findById(params.corpusSource)
        def list = NLPCorpus.findAllByCorpusSource(corpusSource)
//        redirect(action: "corpusView",  params: [lst: list])
        session.setAttribute("corpusList", list)
        render("/corpus/corpusView")
//        render(view: '/corpus/corpusView', model: [lst: list])
    }

    def corpusView() {
        def lst = session.getAttribute("corpusList")
        session.removeAttribute("corpusList")
        render(view:"/corpus/corpusView", model:[lst: lst])
    }

    def corpusUpload() {}

    def process(word) {
        word = word.replaceAll("<u>", "").replaceAll("</u>", "").replaceAll("<em>", "").replaceAll("</em>", "")
        return word
    }

    def processCSVTAB(outs, lst) {
        final String filename = 'en-chr.csv'
        response.contentType = "text/csv;charset=UTF-8";
        response.setHeader "Content-disposition", "attachment; filename=${filename}"

        outs << "COUNT \t ${lst.size()}\n"

        def format = params.id == "tab" ? TAB_DELIMITED_FORMAT : CSV_DELIMITED_FORMAT

        lst.each {
            outs << String.format(format, it.value[0], it.value[1])
        }
    }

    def processExcel(outs, lst) {
        response.setHeader "Content-disposition", "attachment; filename=eng_chr.xlsx"
        response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Cherokee-English");

        lst.each { book ->
            Row row = sheet.createRow(book.key)
            Cell cell = row.createCell(0)
            cell.setCellValue(book.value[0])
            Cell cell2 = row.createCell(1)
            cell2.setCellValue(book.value[1])

        }

        workbook.write(outs);

    }

    def save() {
        def id = params.id
        def english = params.english
        def syllabary = params.syllabary

        def corpus = NLPCorpus.findById(id)
        corpus.syllabary = syllabary
        corpus.english = english

        if (!corpus.save(flush:true)) {
            def sb = new StringBuilder()
            corpus.errors.allErrors.each {
                sb << it
                sb << "<br/>"
            }

            response.status = 500
            render([error: sb.toString()] as JSON)
        }

        response.status = 200
        render([data: 'Save successful'] as JSON)
    }
}
