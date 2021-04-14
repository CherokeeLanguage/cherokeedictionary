package cherokee.dictionary

import cherokee.corpus.CorpusSource
import cherokee.corpus.NLPCorpus
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

class NLPUploadController {

    def index() {
    }

    def upload() {
        render(view:"/corpus/corpusUpload")
//        saveNewCorpus("asdfasdfasdf", "asdfasdfasdfasdf", new CorpusSource(sourceName: "test1"))
        def file = request.getFile('file')

        XSSFWorkbook book = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = book.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        def lst = [:]
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            def tmp = writeRowToReport(row);
            if (tmp && tmp[0] && tmp[1]) {
                lst.put(lst.size(), [tmp[0], tmp[1]])
            }
        }

        //3100 seems to be the max rows we can process before the page timesout sending to the database during review
        // this may need to be adjusted later
        if (lst.size() > 500) {
            processListAsIs(lst)
            render(view:'/corpus/correct', model: [lst: lst, source:"TMP"])
        } else {
            render(view: '/corpus/corpusDoubleCheck', model: [lst: lst])
        }
    }

    public static String[] writeRowToReport(Row row) {
        //For each row, iterate through all the columns
        Iterator<Cell> cellIterator = row.cellIterator();

        def chr
        def eng

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            int columnIndex = cell.getColumnIndex();

            if (columnIndex == 0) {
                chr = ""
                eng = ""
                if (cell.getStringCellValue().trim() == '') {
                    return;
                }
            }

            CellType cellType = cell.getCellType();
            String stringValue = "";
            switch (cellType) {
                case CellType.NUMERIC:
                    stringValue = String.valueOf(cell.getNumericCellValue());
                    break;
                case CellType.ERROR:
                    stringValue = String.valueOf(cell.getErrorCellValue());
                    break;
                case CellType.STRING:
                    stringValue = cell.getStringCellValue();
                    break;
            }

//            println stringValue;

            if (columnIndex == 0) {
                chr = stringValue
            } else {

                eng = stringValue.replaceAll("`", "\'").replaceAll("'", "\'");
                //if the cell on the left or the cell on the right don't have any value then don't include them because we need parity
                // also if the cherokee string contains the word 'cherokee' we don't want to use it since it's a header
                if (chr?.size() > 0 && eng?.size() > 0 && !eng?.toLowerCase().contains("cherokee")) {
//                    f.append(eng.trim() + "\t" + chr.trim() + "\n", "UTF-8")
                    return [chr,eng]
                }
            }
        }
    }

    def correct() {
        def corpusSource = getCorpusSourceFromParams(params)

        process(params, corpusSource)

        render(view:'/corpus/correct', model:[lst: NLPCorpus.findAllByCorpusSource(corpusSource)])
    }

    def getCorpusSourceFromParams(params) {
        def corpusSource

        if (params.corpusS) {
            corpusSource = new CorpusSource(sourceName: params.corpusS)
            if (!corpusSource.save(flush:true)) {
                corpusSource.errors.allErrors.each {
                    println it
                }
            }
        } else if (params.cSource) {
            corpusSource = CorpusSource.findById(Integer.parseInt(params.cSource))
        } else {
            //send back to the previous page with an error message
            //        redirect(uri: "/nlpCorpus")
        }

        return corpusSource
    }

    def processListAsIs(lst) {
        def corpusSource = getCorpusSourceFromParams([corpusS:'TEMPSOURCE'])
        lst.each {
            def chr = it.value[0]
            def en = it.value[1]

            if (chr =~ /[ᎠᎡᎢᎣᎤᎥᎦᎧᎨᎩᎪᎫᎬᎭᎮᎯᎰᎱᎲᎳᎴᎵᎶᎷᎸᎹᎺᎻᎼᎽᎾᎿᏀᏁᏂᏃᏄᏅᏆᏇᏈᏉᏊᏋᏌᏍᏎᏏᏐᏑᏒᏓᏔᏕᏖᏗᏘᏙᏚᏛᏜᏝᏞᏟᏠᏡᏢᏣᏤᏥᏦᏧᏨᏩᏪᏫᏬᏭᏮᏯᏰᏱᏲᏳᏴ]/) {
                saveNewCorpus(chr, en, corpusSource)
            } else {
                saveNewCorpus(en, chr, corpusSource)
            }
        }
    }

    def process(params, corpusSource) {
        params.each {
            String tmp = it.key

            if (tmp.contains('Chr')) {
                def en = params."${tmp.replace("Chr", "Eng")}"
                def chr = params."${tmp}"
                if (en && chr) {
                    //check and make sure that the value doesn't contain english
                    // if there are mixed translations then this should be changed
                    // TODO:^^
                    if (it.value =~ /[ᎠᎡᎢᎣᎤᎥᎦᎧᎨᎩᎪᎫᎬᎭᎮᎯᎰᎱᎲᎳᎴᎵᎶᎷᎸᎹᎺᎻᎼᎽᎾᎿᏀᏁᏂᏃᏄᏅᏆᏇᏈᏉᏊᏋᏌᏍᏎᏏᏐᏑᏒᏓᏔᏕᏖᏗᏘᏙᏚᏛᏜᏝᏞᏟᏠᏡᏢᏣᏤᏥᏦᏧᏨᏩᏪᏫᏬᏭᏮᏯᏰᏱᏲᏳᏴ]/) {
                        saveNewCorpus(chr, en, corpusSource)
                    } else {
                        saveNewCorpus(en, chr, corpusSource)
                    }
                }
            }
        }

    }

    def saveNewCorpus(syllabary, english, source) {
        if (english && syllabary && source) {
            def nlpCorpus = new NLPCorpus(english: english, syllabary: syllabary, corpusSource: source)

//            println "syllabary " + nlpCorpus.syllabary
//            println "english " + nlpCorpus.english

            if (!nlpCorpus.save(flush: true)) {
                nlpCorpus.errors.allErrors.each {
                    println it
                }
            }
        }
    }

    def changeCorpusName() {
        def option = params.cSource
        def cSource
        if (option) {
            cSource = CorpusSource.findById(option)
        }

        def corpusSource = CorpusSource.findBySourceName('TEMPSOURCE')

//        def saveSuccessful = false

        if (cSource) {
            def nlpCorpus = NLPCorpus.findAllByCorpusSource(corpusSource)
            nlpCorpus.each {
                it.corpusSource = cSource
                if (!it.save(flush:true)) {
                    it.errors.allErrors.each {
                        println it
                    }
                }
            }

            corpusSource.delete();
        } else {
            corpusSource.sourceName = params.corpusSource
            if (!corpusSource.save(flush: true)) {
                corpusSource.errors.allErrors.each {
                    println it
                }

                render('error changing try again')
            }

            println corpusSource
        }

        render("changed");
    }
}
