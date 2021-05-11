package cherokee.gilt

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class GILTController {

    GILTService GILTService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond GILTService.list(params), model:[GILTCount: GILTService.count()]
    }

    def show(Long id) {
        respond GILTService.get(id)
    }

    def create() {
        respond new GILT(params)
    }

    def save(GILT GILT) {
        if (GILT == null) {
            notFound()
            return
        }

        try {
            GILTService.save(GILT)
        } catch (ValidationException e) {
            respond GILT.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'GILT.label', default: 'GILT'), GILT.id])
                redirect GILT
            }
            '*' { respond GILT, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond GILTService.get(id)
    }

    def update(GILT GILT) {
        if (GILT == null) {
            notFound()
            return
        }

        try {
            GILTService.save(GILT)
        } catch (ValidationException e) {
            respond GILT.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'GILT.label', default: 'GILT'), GILT.id])
                redirect GILT
            }
            '*'{ respond GILT, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        GILTService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'GILT.label', default: 'GILT'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'GILT.label', default: 'GILT'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
