package dictionary

import grails.testing.web.controllers.ControllerUnitTest
import net.cherokeedictionary.relational.RelationalController
import net.cherokeedictionary.newRelational.Word
import net.cherokeedictionary.newRelational.VerbTense
import net.cherokeedictionary.newRelational.VerbRelationship
import grails.testing.gorm.DataTest
import spock.lang.Specification

class RelationalControllerSpec extends Specification implements ControllerUnitTest<RelationalController>, DataTest {

    def setup() {
        mockDomains(Word, VerbTense, VerbRelationship)
    }

    void "test index action returns correct model"() {
        given: "A parent word, a child word, a tense, and a relationship"
        def tense = new VerbTense(name: '1st Person Present', description: 'vfirstpres').save(flush: true)
        def root = new Word(syllabary: 'áŽ á“á±áŽ­', transliteration: 'adayiha').save(flush: true)
        def form1 = new Word(syllabary: 'á¥á¯á“á±áŽ­', transliteration: 'jiyadayiha').save(flush: true)
        new VerbRelationship(parentWord: root, childWord: form1, tense: tense).save(flush: true)

        when: "The index action is executed"
        def model = controller.index()

        then: "The model contains the word and grouped tenses"
        model != null
        model.word == root
        model.groupedTenses != null
        model.groupedTenses.containsKey('1st Person Present')
        model.groupedTenses['1st Person Present'].size() == 1
        model.groupedTenses['1st Person Present'][0].childWord == form1
    }
}
