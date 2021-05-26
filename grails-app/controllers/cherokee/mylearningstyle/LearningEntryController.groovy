package cherokee.mylearningstyle

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class LearningEntryController {

    LearningEntryService learningEntryService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond learningEntryService.list(params), model:[learningEntryCount: learningEntryService.count()]
    }

    def show(Long id) {
        respond learningEntryService.get(id)
    }

    def create() {
        respond new LearningEntry(params)
    }

    def save(LearningEntry learningEntry) {
        if (learningEntry == null) {
            notFound()
            return
        }

        try {
            learningEntryService.save(learningEntry)
        } catch (ValidationException e) {
            respond learningEntry.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'learningEntry.label', default: 'LearningEntry'), learningEntry.id])
                redirect learningEntry
            }
            '*' { respond learningEntry, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond learningEntryService.get(id)
    }

    def update(LearningEntry learningEntry) {
        if (learningEntry == null) {
            notFound()
            return
        }

        try {
            learningEntryService.save(learningEntry)
        } catch (ValidationException e) {
            respond learningEntry.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'learningEntry.label', default: 'LearningEntry'), learningEntry.id])
                redirect learningEntry
            }
            '*'{ respond learningEntry, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        learningEntryService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'learningEntry.label', default: 'LearningEntry'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'learningEntry.label', default: 'LearningEntry'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
