package cherokee.mylearningstyle

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class LearningEntryServiceSpec extends Specification {

    LearningEntryService learningEntryService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new LearningEntry(...).save(flush: true, failOnError: true)
        //new LearningEntry(...).save(flush: true, failOnError: true)
        //LearningEntry learningEntry = new LearningEntry(...).save(flush: true, failOnError: true)
        //new LearningEntry(...).save(flush: true, failOnError: true)
        //new LearningEntry(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //learningEntry.id
    }

    void "test get"() {
        setupData()

        expect:
        learningEntryService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<LearningEntry> learningEntryList = learningEntryService.list(max: 2, offset: 2)

        then:
        learningEntryList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        learningEntryService.count() == 5
    }

    void "test delete"() {
        Long learningEntryId = setupData()

        expect:
        learningEntryService.count() == 5

        when:
        learningEntryService.delete(learningEntryId)
        sessionFactory.currentSession.flush()

        then:
        learningEntryService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        LearningEntry learningEntry = new LearningEntry()
        learningEntryService.save(learningEntry)

        then:
        learningEntry.id != null
    }
}
