package net.cherokeedictionary.audio

import grails.gorm.transactions.Transactional
import org.springframework.core.io.Resource

//@Transactional
class AudioService {
    def assetResourceLocator

    def audioPull(fileName) {
        println "here"
//        Resource res = assetResourceLocator?.findAssetForURI("/images/apple-touch-icon.png")//images/audio/${params.file}.mp3
//        println res != null ? "not null" : "null"
//        println res.getURL()
//        println res.getURI()

        return file
    }
}
