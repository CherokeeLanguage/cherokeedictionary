<%@ page import="cherokee.audio.AudioFile; cherokee.audio.AudioLink" contentType="text/html;charset=UTF-8" %>

<%
def audioFiles = AudioFile.list()
audioFiles.each {
    def audioLink = AudioLink.findByAudioFile(it)

    out << raw("\"${audioLink.likespreadsheets.entrya}\", \"${audioLink.likespreadsheets.definitiond}\", \"${it.audioFilePath}\"<br/>")
}
%>