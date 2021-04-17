package cherokee

import grails.gorm.transactions.Transactional
//import opennlp.tools.lemmatizer.DictionaryLemmatizer
//import opennlp.tools.postag.POSModel
//import opennlp.tools.postag.POSTaggerME

//@Transactional
class ONLPService {


    def serviceMethod() {
//        // Parts-Of-Speech Tagging
//        // reading parts-of-speech model to a stream
//        InputStream posModelIn = getClass().getClassLoader().getResourceAsStream("/models/en-pos-maxent.bin")
//
//        // loading the dictionary to input stream
//        InputStream dictLemmatizer = getClass().getClassLoader().getResourceAsStream("/dictionary/en-lemmatizer.dict.txt")
//
//        String[] tokens = ["Most", "large", "cities", "in", "the", "US", "had", "morning", "and", "afternoon", "newspapers", "."]
//
//        // loading the parts-of-speech model from stream
//        POSModel posModel = new POSModel(posModelIn);
//        // initializing the parts-of-speech tagger with model
//        POSTaggerME posTagger = new POSTaggerME(posModel);
//        // Tagger tagging the tokens
//        String[] tags = posTagger.tag(tokens);
//
//        // loading the lemmatizer with dictionary
//        DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
//
//        // finding the lemmas
//        String[] lemmas = lemmatizer.lemmatize(tokens, tags);
//
        // printing the results
        def sb = new StringBuilder();

        sb << ("\nPrinting lemmas for the given sentence...<br/>");
        sb << ("WORD -POSTAG : LEMMA<br/>");

//        for(int i=0;i< tokens.length;i++){
//            sb << (tokens[i]+" -"+tags[i]+" : "+lemmas[i] +"<br/>");
//        }

        return sb
    }
}
