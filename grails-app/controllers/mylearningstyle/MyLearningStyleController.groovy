package mylearningstyle

import cherokee.dictionary.Cherokeewordsearch
import cherokee.dictionary.myLearningStyle.MLS
import net.cherokeedictionary.transliteration.SyllabaryUtil
import org.springframework.core.io.Resource

class MyLearningStyleController {
    def audioService

    def index() {
        //create a model to pass to the page
        def model = [:]

//        translate()

        //render the index page with the model map we've created
        render(view : "index", model : model)
    }

    //https://stackoverflow.com/questions/21756532/how-to-download-an-image-from-url-in-grails
//    def downloadImage = {
//        def fileURL = "http://www.google.com/images/logo.gif"
//        def thisUrl = new URL(fileURL);
//        def connection = thisUrl.openConnection();
//        def dataStream = connection.inputStream
//
//        response.setContentType("application/octet-stream")
//        response.setHeader('Content-disposition', 'Attachment; filename=logo.gif')
//        response.outputStream << dataStream
//        response.outputStream.flush()
//    }

    //does not work because outputstream was already called
//    def downloadAudioFromServer() {
//        def fileUrl = "http://cherokeedictionary.net/audio/audio.mp3"
//        def thisUrl = new URL(fileUrl)
//        def connection = thisUrl.openConnection();
//        def dataStream = connection.inputStream
//        response.setContentType('audio/mp3')
//        response.setContentLength (dataStream.bytes.length)
//        response.setHeader("Content-disposition", "attachment;filename=audio.mp3")
//        response.outputStream << dataStream // Performing a binary stream copys
//        response.reset();
//        response.setStatus(206);
//        response.setHeader("Accept-Ranges", "bytes");
//        response.outputStream.flush()
//    }

//    def translate() {
//        def transliterated = SyllabaryUtil.mixedTransliteration("Jani: Osiyo, Meli.  Osigwuju?")
//
//        def splits = transliterated.split(" ")
//
//        splits.each {
//            it = it.replaceAll(",", "").replaceAll(":", "").replaceAll("/.", "").replaceAll("/?", "").trim()
//            if (it.endsWith(".")) {
//                it = it.substring(0, it.size() - 1)
//            }
//
//            if (it.endsWith("?")) {
//                it = it.substring(0, it.size() - 1)
//            }
//
//            if (it.endsWith("Ꮷ")) {
//                it = it.substring(0, it.size() - 1)
//            }
//
//            println it
//            println "result is " + Cherokeewordsearch.findAll("from Cherokeewordsearch where syllabaryb = ?", [it]).syllabaryb
//        }
//    }

    //pulls from grails server -- not optimal for a lot of audio files
    def downloadAudioLocally() {
        File audioFile = grailsApplication.mainContext.getResource("/mystyle/chap1/CherokeeConsortiumbyAnnaSixkiller.mp3").file

        response.setContentType('audio/mp3')
        response.setContentLength (audioFile.bytes.length)
        response.setHeader("Content-disposition", "attachment;filename=${audioFile.getName()}")
        response.outputStream << audioFile.newInputStream() // Performing a binary stream copys
        response.reset();
        response.setStatus(206);
        response.setHeader("Accept-Ranges", "bytes");
    }

    //this is to eventually play audio
    def audio() {
        downloadAudioLocally();
    }
}
