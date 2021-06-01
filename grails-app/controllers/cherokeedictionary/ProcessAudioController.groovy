package cherokeedictionary

import cherokee.audio.AudioFile
import cherokee.audio.AudioLink
import cherokee.dictionary.Likespreadsheets

class ProcessAudioController {
    def sql = "from Likespreadsheets where definitiond = ?0 and source=?1"

    def index() {
        process()
    }

    def process() {
        def fileName = grailsApplication.mainContext.getResource("process.txt").file
        def lines = fileName.readLines()
        lines.each {
            def spl = it.split("\\|")
            def file = spl[4].trim()

            if (file == 'null') {
//        processAlphabet(spl[1].trim().replaceAll(" ", ""))
            } else {
                def splitter = file.substring(file.lastIndexOf("/") + 1)//, file.indexOf("."))
                //create an audio file for insertion
                //get back info and then link the new audio file with

                def audioFile = new AudioFile(audioFilePath:file)
                audioFile.save()

                def name = splitter.substring(0, splitter.indexOf("."))
//                println sql
//                println name
                def lookup = Likespreadsheets.findAll(sql, [name, 'noq'])[0]
                def id = lookup?.id

//                println id

                if (id) {
                    def audioLink = new AudioLink(audioFile:audioFile, likespreadsheets:lookup)
//                    println audioFile.id + " " + lookup.id
                    if (!audioLink.save()) {
                        audioLink.errors.each {
                            println it
                        }
                    }
                } else {
                    if (name) {
                        def newName = name.replaceAll("_", "")
                        lookup = Likespreadsheets.findAll(sql, [newName, 'noq'])[0]
                        id = lookup?.id
                        if (id) {
                            def audioLink = new AudioLink(audioFile:audioFile, likespreadsheets:lookup)
                            if (!audioLink.save()) {
                                audioLink.errors.each {
                                    println it
                                }
                            }
                        } else {
                            def phonetic = spl[1].replaceAll(" ", "")
                            def syllabary = spl[2]
                            def definition = name.replaceAll("_", " ")
                            def mp3link = file
                            def lks = new Likespreadsheets(entrya:phonetic, definitiond:definition, syllabaryb: syllabary, source:'cno')
                            if (!lks.save()) {
                                lks.errors.each {
                                    println it
                                }
                            } else {
                                def audioLink = new AudioLink(audioFile:audioFile, likespreadsheets:lks)
                                if (!audioLink.save()) {
                                    audioLink.errors.each {
                                        println it
                                    }
                                }
                            }
                        }
                    } else {
                        println file
                    }
                }
            }
        }
    }
}
