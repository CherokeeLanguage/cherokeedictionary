package cherokee.corpus

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class VerseServiceSpec extends Specification {

    VerseService verseService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Verse(...).save(flush: true, failOnError: true)
        //new Verse(...).save(flush: true, failOnError: true)
        //Verse verse = new Verse(...).save(flush: true, failOnError: true)
        //new Verse(...).save(flush: true, failOnError: true)
        //new Verse(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //verse.id
    }

    void "test get"() {
        setupData()

        expect:
        verseService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Verse> verseList = verseService.list(max: 2, offset: 2)

        then:
        verseList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        verseService.count() == 5
    }

    void "test delete"() {
        Long verseId = setupData()

        expect:
        verseService.count() == 5

        when:
        verseService.delete(verseId)
        sessionFactory.currentSession.flush()

        then:
        verseService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Verse verse = new Verse()
        verseService.save(verse)

        then:
        verse.id != null
    }
}
