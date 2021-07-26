package cherokee.audio

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class GenericAudioController {

    GenericAudioService genericAudioService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond genericAudioService.list(params), model:[genericAudioCount: genericAudioService.count()]
    }

    def show(Long id) {
        respond genericAudioService.get(id)
    }

    def create() {
        respond new GenericAudio(params)
    }

    def save(GenericAudio genericAudio) {
        if (genericAudio == null) {
            notFound()
            return
        }

        try {
            genericAudioService.save(genericAudio)
        } catch (ValidationException e) {
            respond genericAudio.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'genericAudio.label', default: 'GenericAudio'), genericAudio.id])
                redirect genericAudio
            }
            '*' { respond genericAudio, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond genericAudioService.get(id)
    }

    def update(GenericAudio genericAudio) {
        if (genericAudio == null) {
            notFound()
            return
        }

        try {
            genericAudioService.save(genericAudio)
        } catch (ValidationException e) {
            respond genericAudio.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'genericAudio.label', default: 'GenericAudio'), genericAudio.id])
                redirect genericAudio
            }
            '*'{ respond genericAudio, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        genericAudioService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'genericAudio.label', default: 'GenericAudio'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'genericAudio.label', default: 'GenericAudio'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
