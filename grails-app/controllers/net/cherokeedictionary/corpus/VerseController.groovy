package net.cherokeedictionary.corpus

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class VerseController {

    VerseService verseService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond verseService.list(params), model:[verseCount: verseService.count()]
    }

    def show(Long id) {
        respond verseService.get(id)
    }

    def create() {
        respond new Verse(params)
    }

    def save(Verse verse) {
        if (verse == null) {
            notFound()
            return
        }

        try {
            verseService.save(verse)
        } catch (ValidationException e) {
            respond verse.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'verse.label', default: 'Verse'), verse.id])
                redirect verse
            }
            '*' { respond verse, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond verseService.get(id)
    }

    def update(Verse verse) {
        if (verse == null) {
            notFound()
            return
        }

        try {
            verseService.save(verse)
        } catch (ValidationException e) {
            respond verse.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'verse.label', default: 'Verse'), verse.id])
                redirect verse
            }
            '*'{ respond verse, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        verseService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'verse.label', default: 'Verse'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'verse.label', default: 'Verse'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
