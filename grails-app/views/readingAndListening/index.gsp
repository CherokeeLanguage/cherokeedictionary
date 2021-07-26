<br/>
<%
    def walc = [:]
    walc.put("Greetings Ch3S1","http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_3_section_1_Greetings.mp3")
    walc.put("Beginning Phrases and Questions C3S2","http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_3_section_2_Beginning_Phrases_and_Questions.mp3")
    walc.put("Classroom Phrases C3S3","http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_3_section_3_Classroom_Phrases.mp3")
    walc.put("Asking Questions C3S4","http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_3_section_4_Asking_Questions.mp3")
    walc.put("Cardinal Numbers C4S1","http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_1_Cardinal_Numbers.mp3")
    walc.put("Ordinal Numbers C4S2", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_2_Ordinal_Numbers.mp3")
    walc.put("Time C4S3", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_3_Time.mp3")
    walc.put("Money C4S4", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_4_Money.mp3")
    walc.put("Shapes C4S5", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_5_Shapes.mp3")
    walc.put("Days of the Week C4S6", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_6_Days_of_the_Week.mp3")
    walc.put("Time of Day C4S7", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_7_Time_of_Day.mp3")
    walc.put("Months C4S8", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_8_Months.mp3")
    walc.put("Seasons C4S9", "http://cherokeedictionary.net/audio/walc1/berlitz/Chapter_4_section_9_Seasons.mp3")
%>

<div style="display:table">
    <div style="display:table-row">
        <div style="display:table-cell">We Are Learning Cherokee<br/> <a href="https://language.cherokee.org/media/nn0puwfc/we-are-learning-cherokee.zip">workbook and audio zip file</a><br/><br/>
        Each audio will state the chapter and section before starting.<br/>
        The speaker will present English once with a pause followed by<br/>
        Cherokee three times with pauses in between for practice.<br/>
        Annotations are C#S# where C = Chapter and S = Section so <br/>
        C3S3 is Chapter 3 Section 3
        <br/><br/>
            <g:each var="audio" in="${walc}">
            <div style="display:table-row">
                <div style="display:table-cell">
                    ${audio.key}
                </div>
                <div style="display:table-cell">
                    <div style="display:table-cell" class="audiolink">
                        <audio controls style="width: 100px;height:20px">
                            <source src="${audio.value}" type="audio/mpeg">
                        </audio>
                    </div>
                </div>
            </div>
            </g:each>
        </div>
    </div>
    <br/><br/>
    <div style="display:table-row">
        <div style="display:table-cell">

            <strong>All Cherokee 2019 Volume 3</strong><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Documents/2019/8/103490_Vol43All-Cherokee.2019.pdf">Volume 3 All Cherokee 2019 PDF</a> of all articles listed in this section

            pp1 ᎠᏔᏄᏣ ᏣᎳᎩ ᎦᏔᏅᎥᏍᎩ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/LisaRutherfordbyJohnRoss.mp3">audio</a>The article text starts about 4:22<br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103534">English and Cherokee Page</a><br/>
            <br/>
            pp2 ᏣᎳᎩ ᏓᎾᏁᎶᏗᏍᎬ ᎠᏛᏍᎦ ᏌᏊᎭᎢ ᎧᏁᎢᏍᏗ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/CherokeeConsortiumbyAnnaSixkiller.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103538">English and Cherokee Page</a><br/>
            <br/>
            pp2 ᏗᎧᎸᎬ ᎠᏁᎯ ᎠᏂᏣᎳᎩ ᎠᎾᏕᎶᏆᎢ ᎤᎾᏤᎵ ᎤᏂᏬᏂᎯᏍᏗ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/MatthewToonibyAnnaSixkiller.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103547">English and Cherokee Page</a><br/>
            <br/>
            pp3 ᎤᏍᏗ ᏗᎾᏓᏢᏟ ᎠᏓᎾᏩ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/StickballbyAnnaSixkiller.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103548">English and Cherokee Page</a><br/>
            <br/>
            pp4 ᎪᎱᏍᏗ ᎠᏓᏁᏗ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/BarbaraMcAlisterbyJohnRoss.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103554">English and Cherokee Page</a><br/>
            <br/>
            pp5 ᏣᎳᎩ ᎦᏬᏂᎯᏍᏗ ᏓᎾᏠᏍᎬᎢ ᏖᎦᎾᎶᏥ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/LanguageTechnologybyDavidCrawler.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103556">English and Cherokee Page</a><br/>
            <br/>
            pp6 3 ᎠᏂᏣᎳᎩ ᏅᏬᏘ ᎠᎾᏕᎶᏆᏍᎩ ᎠᏂᎦᏖᏃᎭ ᎾᏍᎩ ᏰᎵᎢ ᏗᎬᏩᏂᏍᏕᎸᏗ ᏧᎾᏤᎵᎢ ᏴᏫ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/OSUStudentsbyDavidCrawler.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103560">English and Cherokee Page</a><br/>
            <br/>
            pp7 ᎾᎿ ᎠᏆᏓᏣᏁᏗ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/TranstDavidCrawler.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103564">English and Cherokee Page</a><br/>
            <br/>
            pp8 ᏬᏌᏂᏴ<br/>
            <a target= "_BLANK" href="https://data.cherokee.org/phoenix/audio/2019/StompingGroundsbyAnnaSixkiller.mp3">audio</a><br/>
            <a target= "_BLANK" href="https://www.cherokeephoenix.org/Article/index/103572">English and Cherokee Page</a><br/>
        </div>
    </div>
</div>
