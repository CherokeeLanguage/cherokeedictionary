package cherokee.gilt

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class GILTLangController {

    GILTLangService GILTLangService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond GILTLangService.list(params), model:[GILTLangCount: GILTLangService.count()]
    }

    def show(Long id) {
        respond GILTLangService.get(id)
    }

    def create() {
        respond new GILTLang(params)
    }

    def save(GILTLang GILTLang) {
        if (GILTLang == null) {
            notFound()
            return
        }

        try {
            GILTLangService.save(GILTLang)
        } catch (ValidationException e) {
            respond GILTLang.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'GILTLang.label', default: 'GILTLang'), GILTLang.id])
                redirect GILTLang
            }
            '*' { respond GILTLang, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond GILTLangService.get(id)
    }

    def update(GILTLang GILTLang) {
        if (GILTLang == null) {
            notFound()
            return
        }

        try {
            GILTLangService.save(GILTLang)
        } catch (ValidationException e) {
            respond GILTLang.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'GILTLang.label', default: 'GILTLang'), GILTLang.id])
                redirect GILTLang
            }
            '*'{ respond GILTLang, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        GILTLangService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'GILTLang.label', default: 'GILTLang'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'GILTLang.label', default: 'GILTLang'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
