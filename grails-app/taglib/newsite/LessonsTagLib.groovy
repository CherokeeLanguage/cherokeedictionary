package newsite

import net.cherokeedictionary.transliteration.SyllabaryUtil

class LessonsTagLib {
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    def dialog = {params ->
        def sb = new StringBuilder()

        File leftF = grailsApplication.mainContext.getResource(params.chr as String).file
        File rightF = grailsApplication.mainContext.getResource(params.en as String).file

        def lstLeft = leftF.readLines()
        def lstRight = rightF.readLines()

        //left file refers to the table cell on the left of the screen - right the right side of the table
        def leftFile = new StringBuilder()
        def rightFile = new StringBuilder()

        lstRight.eachWithIndex {str, idx ->
            rightFile << "${idx + 1}. $str<br/>"
        }

        lstLeft.eachWithIndex {str, idx ->
            def transliterated = SyllabaryUtil.mixedTransliteration(str)

            leftFile << "${idx + 1}. ${transliterated}<br/>"
        }

        sb << "<div style=\"display:table\"><div style=\"display:table-row\"><div style=\"display:table-cell;padding-right:10px;vertical-align: top\" id=\"${params.left}\">" +
                "${leftFile.toString()}" +
                "</div><div style=\"display:table-cell;vertical-align: top;line-height:20px\" id=\"${params.right}\">" +
                "${rightFile.toString()}" +
                "</div></div></div>"

        out << raw(sb.toString())
    }

    def chapter = { params ->
        out << raw("<h2>${params.en} - ${g.translit(src: params.chr)}</h2>")
    }

    def vocabulary = {params ->
        def sb = new StringBuilder()
        sb << "<h4>Vocabulary - </h4>"

        params.src.each { key, value ->
            def translit = value
            sb << "<div style=\"display:table-row\"><div style=\"display:table-cell;padding-right:10px\">${key}</div><div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(translit)}"
            sb << "<br/><span style=\"color:red\">${translit}</span>"
            sb << "</div></div>"
        }

        out << raw(sb.toString())
    }

    def dialogLine = {params ->
        def sb = new StringBuilder()
        def name = params.name
        def dialog = params.dialog
        def showRedHelper = params.phonetic ? true : false

        sb << "<div style=\"display:table-row\">\n" +
                "    <div style=\"display:table-cell;padding-right:10px\">\n" +
                "        <div style=\"display:table-row\">\n" +
                "            <div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(name)}:</div>\n" +
                "        </div>\n"
        if (showRedHelper) {
            sb << "        <div style=\"display:table-row\">\n" +
                    "            <div style=\"display:table-cell\"><span style=\"color:red\">${name}:</span></div>\n" +
                    "        </div>\n"
        }
            sb << "    </div>\n" +
                "    <div style=\"display:table-cell\">\n" +
                "        <div style=\"display:table-row\">\n" +
                "            <div style=\"display:table-cell\">${SyllabaryUtil.mixedTransliteration(dialog)}</div>\n" +
                "        </div>\n"
        if (showRedHelper) {
            sb << "        <div style=\"display:table-row\">\n" +
                    "            <div style=\"display:table-cell\"><span style=\"color:red\">${dialog}</span></div>\n" +
                    "        </div>\n"
        }

        sb <<        "    </div>\n" +
                "</div>"


        out << raw(sb.toString())
    }

//    def audio = {params ->
//        File audioFile = grailsApplication.mainContext.getResource(params.fileName).file
//        println audioFile != null ? "audio not null" : "audio is null"
//        println audioFile.absolutePath
//
//        response.setContentType('audio/mp3')
//        response.setContentLength (audioFile.bytes.length)
//        response.setHeader("Content-disposition", "attachment;filename=${audioFile.getName()}")
//        response.outputStream << audioFile.newInputStream() // Performing a binary stream copys
//        response.reset();
//        response.setStatus(206);
//        response.setHeader("Accept-Ranges", "bytes");
//        def sb = new StringBuilder()
//        sb << "<audio controls>"
//        sb << "<source src=\"$audioFile\" type=\"audio/mp3\">"
//        sb << "Your browser does not support the audio element."
//        sb << "</audio>"
//
//        out << raw(sb.toString())
//    }
}