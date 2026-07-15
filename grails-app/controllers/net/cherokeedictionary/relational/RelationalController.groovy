package net.cherokeedictionary.relational

import net.cherokeedictionary.newRelational.DictionaryEntry
import net.cherokeedictionary.newRelational.ExampleSentence
import net.cherokeedictionary.newRelational.Sense
import net.cherokeedictionary.newRelational.VerbRelationship
import net.cherokeedictionary.newRelational.VerbTense
import net.cherokeedictionary.newRelational.Word

class RelationalController {

    def index() {
        def id = 1
        // Fetch the word (we use id or syllabary depending on your URL structure)
        def word = Word.get(id)// ?: Word.findBySyllabary(id)

        if (!word) {
            flash.message = "Word not found"
//            redirect(action: "index")
            return
        }

        // Group relationships by tense for the view
        def relationships = VerbRelationship.findAllByParentWord(word)
        def groupedTenses = relationships.groupBy { it.tense.name }

        [word: word, groupedTenses: groupedTenses]

//        [wordList: Word.list()]
    }

    def show(String id) {
        // Fetch the word (we use id or syllabary depending on your URL structure)
        def word = Word.get(id) ?: Word.findBySyllabary(id)

        if (!word) {
            flash.message = "Word not found"
            redirect(action: "index")
            return
        }

        // Group relationships by tense for the view
        def relationships = VerbRelationship.findAllByParentWord(word)
        def groupedTenses = relationships.groupBy { it.tense.name }


    }

    def init() {
        // 1. Setup Lookup Tables
        def tense1 = new VerbTense(name: '1st Person Present', description: 'vfirstpres').save(failOnError: true)
        def tense2 = new VerbTense(name: '3rd Person Past', description: 'vthirdpast').save(failOnError: true)
        def tense3 = new VerbTense(name: '3rd Person Infinitive', description: 'vthirdinf').save(failOnError: true)

        // 2. Create Root Word and Inflections
        def root = new Word(syllabary: 'ᎠᏓᏱᎭ', transliteration: 'adayiha').save(failOnError: true)
        def form1 = new Word(syllabary: 'ᏥᏯᏓᏱᎭ', transliteration: 'jiyadayiha').save(failOnError: true)
        def form2 = new Word(syllabary: 'ᎦᏓᏱᎭ', transliteration: 'gadayiha').save(failOnError: true)
        def form3 = new Word(syllabary: 'ᎤᏓᏱᎸᎢ', transliteration: 'udahyilvi').save(failOnError: true)
        def form4 = new Word(syllabary: 'ᎤᏓᏴᏍᏗ', transliteration: 'udayhsdi').save(failOnError: true)

        // 3. Create Entry (Container)
        def entry = new DictionaryEntry(word: root, source: 'ced').save(failOnError: true)

        // 4. Create Sense (Definition)
        def sense = new Sense(entry: entry, definition: "he's denying him, it", partOfSpeech: 'vt').save(failOnError: true)

        // 5. Add Sentence
        new ExampleSentence(sense: sense, syllabary: 'ᎤᏃᏍᎩᏒ ᎠᎫᎢᏍᏔᎾ', english: 'When he was accused of stealing, he denied it.').save(failOnError: true)

        // 6. Create Relationships
        new VerbRelationship(parentWord: root, childWord: form1, tense: tense1).save(failOnError: true)
        new VerbRelationship(parentWord: root, childWord: form2, tense: tense1).save(failOnError: true)
        new VerbRelationship(parentWord: root, childWord: form3, tense: tense2).save(failOnError: true)
        new VerbRelationship(parentWord: root, childWord: form4, tense: tense3).save(failOnError: true)

        println "Demo data loaded successfully!"
    }
}
