package net.cherokeedictionary.relational

import grails.converters.JSON
import net.cherokeedictionary.dictionary.Likespreadsheets
import net.cherokeedictionary.transliteration.SyllabaryUtil

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

    def processParams(params,count, defin, wordList) {
        for (def i = 0; i < count; i++) {
            def tmpCount = i
            def syllabary = params['syllabary' + tmpCount]
            if (syllabary) {
//                println syllabary
                def phonetic = params['phonetic' + tmpCount]
                def tone = params['tone' + tmpCount]
                def audioFilePath = params['audioFilePath' + tmpCount]
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

                new WordDefinitionLink(word: wrd, definition: defin).save()
            }
        }
    }
}
