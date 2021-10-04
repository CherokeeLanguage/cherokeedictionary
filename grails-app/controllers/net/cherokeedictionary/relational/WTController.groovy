package net.cherokeedictionary.relational

import grails.converters.JSON
import net.cherokeedictionary.dictionary.Likespreadsheets
import net.cherokeedictionary.transliteration.SyllabaryUtil
import org.grails.web.json.JSONObject

class WTController {

    def index() { }

    def barnes() {
        def count = params?.numberOfEntries

        def wordList = []
        def definition = params.definition

        if (count) {
            def defin = new Definition(definition: definition)
            defin.save()
            processParams(params, count, definition, wordList)
        }

        render(view: 'barnes', model: [wordList: wordList, definition: definition])
    }

    def wordType() {
        def tsalagiList = WordType.withCriteria {
            or {
                like 'type', '%' + params.term + '%'/*
                like 'nounadjpluraltranslit', '%' + params.term + '%'
                like 'vfirstprestranslit', '%' + params.term + '%'
                like 'vsecondimpertranslit', '%' + params.term + '%'
                like 'vthirdinftranslit', '%' + params.term + '%'
                like 'vthirdpasttranslit', '%' + params.term + '%'
                like 'vthirdprestranslit', '%' + params.term + '%'*/
            }
        }

        def returnList = []
        tsalagiList.each {
            returnList << it.type
        }

        render (returnList as JSON)
    }

    def processParams(params, count, Definition defin, wordList) {
        def errors = []
        for (def i = 0; i < count; i++) {
            def tmpCount = i
            def syllabary = params['syllabary' + tmpCount]
            if (syllabary) {
//                println syllabary
                def phonetic = params['phonetic' + tmpCount]
                def tone = params['tone' + tmpCount]
                def audioFilePath = params['controls' + tmpCount]
                def classifier = params['classifier' + tmpCount]

                WordType wt = WordType.findByType(classifier)
                if (wt == null) {
                    new WordType(type: classifier).save()
                }

                Word wrd = new Word()
                wrd.syllabary = syllabary
                wrd.tone = tone
                wrd.tsalagi = phonetic
                wrd.autotranslit = ""//SyllabaryUtil.parseSyllabary(syllabary)
                wrd.save()
                new WordTypeLink(word: wrd, wordType: wt).save()

                wordList << wrd

                def wordDefLink = new WordDefinitionLink(word: wrd, definition: defin)
                if (!wordDefLink.save(flush:true)) {
                    errors = wordDefLink.errors.allErrors
                    errors.each {
                        println it
                    }
                    println "has Errors"
                }
            }
        }

        return errors;
    }

    def definitionLookup() {
        def tsalagiList = Definition.withCriteria {
            or {
                like 'definition', '%' + params.term + '%'/*
                like 'nounadjpluraltranslit', '%' + params.term + '%'
                like 'vfirstprestranslit', '%' + params.term + '%'
                like 'vsecondimpertranslit', '%' + params.term + '%'
                like 'vthirdinftranslit', '%' + params.term + '%'
                like 'vthirdpasttranslit', '%' + params.term + '%'
                like 'vthirdprestranslit', '%' + params.term + '%'*/
            }
        }

        def returnList = []
        tsalagiList.each {
            returnList << it.definition
        }

        println returnList

        render (returnList as JSON)
    }

    def uploadFeatureImage() {
        def file = request.getFile('file')
        println file.originalFilename

        String folderPath = "d:/userUploads/"
        File folder = new File(folderPath)
        if ( !folder.exists() ) {
            folder.mkdirs()
        }

        file.transferTo(new File(folderPath + file.originalFilename))

        JSONObject obj = new JSONObject();
        obj.put("filePath", "http://cherokeedictionary.net/audio/" + file.originalFilename);
        obj.put("fileCount", params.count);
        render obj as JSON
    }

    def saveFormData() {
        println "params are here "
        println params

        def count = params?.numberOfEntries

        def wordList = []
        def definition = params.definition
        def errors = []

        if (count) {
            def defintion = Definition.findByDefinition(definition)
            if (!defintion) {
                defintion = new Definition(definition: definition)
                defintion.save()
            }

            errors = processParams(params, count, defintion, wordList)
        }

        //TODO: error handling
        //TODO: determine if a definition exists - if it does check the submitted fields for completeness and duplicates
        //TODO: update fields? or simply overwrite the fields?
        //TODO: validate that an audio file hasn't already been submitted.
        //TODO: restrict files to appropriate extensions - like mp3, mp4, wav, but not like jpg, png, etc

        JSONObject obj = new JSONObject();
        if (errors.isEmpty()) {
            obj.put("success", "true");
        } else {
            obj.put("errors", errors)
            obj.put("success", "false")
        }

        render obj as JSON;
    }
}
