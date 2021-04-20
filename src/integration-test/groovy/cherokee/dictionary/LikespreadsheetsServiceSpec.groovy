package cherokee.dictionary

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class LikespreadsheetsServiceSpec extends Specification {

    LikespreadsheetsService likespreadsheetsService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Likespreadsheets(...).save(flush: true, failOnError: true)
        //new Likespreadsheets(...).save(flush: true, failOnError: true)
        //Likespreadsheets likespreadsheets = new Likespreadsheets(...).save(flush: true, failOnError: true)
        //new Likespreadsheets(...).save(flush: true, failOnError: true)
        //new Likespreadsheets(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //likespreadsheets.id
    }

    void "test get"() {
        setupData()

        expect:
        likespreadsheetsService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Likespreadsheets> likespreadsheetsList = likespreadsheetsService.list(max: 2, offset: 2)

        then:
        likespreadsheetsList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        likespreadsheetsService.count() == 5
    }

    void "test delete"() {
        Long likespreadsheetsId = setupData()

        expect:
        likespreadsheetsService.count() == 5

        when:
        likespreadsheetsService.delete(likespreadsheetsId)
        sessionFactory.currentSession.flush()

        then:
        likespreadsheetsService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Likespreadsheets likespreadsheets = new Likespreadsheets()
        likespreadsheetsService.save(likespreadsheets)

        then:
        likespreadsheets.id != null
    }
}
