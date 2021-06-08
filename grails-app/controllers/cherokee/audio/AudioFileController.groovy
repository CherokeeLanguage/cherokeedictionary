package cherokee.audio

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class AudioFileController {

    AudioFileService audioFileService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond audioFileService.list(params), model:[audioFileCount: audioFileService.count()]
    }

    def show(Long id) {
        respond audioFileService.get(id)
    }

    def create() {
        respond new AudioFile(params)
    }

    def save(AudioFile audioFile) {
        if (audioFile == null) {
            notFound()
            return
        }

        try {
            audioFileService.save(audioFile)
        } catch (ValidationException e) {
            respond audioFile.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'audioFile.label', default: 'AudioFile'), audioFile.id])
                redirect audioFile
            }
            '*' { respond audioFile, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond audioFileService.get(id)
    }

    def update(AudioFile audioFile) {
        if (audioFile == null) {
            notFound()
            return
        }

        try {
            audioFileService.save(audioFile)
        } catch (ValidationException e) {
            respond audioFile.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'audioFile.label', default: 'AudioFile'), audioFile.id])
                redirect audioFile
            }
            '*'{ respond audioFile, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        audioFileService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'audioFile.label', default: 'AudioFile'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'audioFile.label', default: 'AudioFile'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
