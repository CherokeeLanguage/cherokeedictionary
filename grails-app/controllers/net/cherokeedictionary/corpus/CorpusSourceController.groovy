package net.cherokeedictionary.corpus

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class CorpusSourceController {

    CorpusSourceService corpusSourceService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond corpusSourceService.list(params), model:[corpusSourceCount: corpusSourceService.count()]
    }

    def show(Long id) {
        respond corpusSourceService.get(id)
    }

    def create() {
        respond new CorpusSource(params)
    }

    def save(CorpusSource corpusSource) {
        if (corpusSource == null) {
            notFound()
            return
        }

        try {
            corpusSourceService.save(corpusSource)
        } catch (ValidationException e) {
            respond corpusSource.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'corpusSource.label', default: 'CorpusSource'), corpusSource.id])
                redirect corpusSource
            }
            '*' { respond corpusSource, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond corpusSourceService.get(id)
    }

    def update(CorpusSource corpusSource) {
        if (corpusSource == null) {
            notFound()
            return
        }

        try {
            corpusSourceService.save(corpusSource)
        } catch (ValidationException e) {
            respond corpusSource.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'corpusSource.label', default: 'CorpusSource'), corpusSource.id])
                redirect corpusSource
            }
            '*'{ respond corpusSource, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        corpusSourceService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'corpusSource.label', default: 'CorpusSource'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'corpusSource.label', default: 'CorpusSource'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
