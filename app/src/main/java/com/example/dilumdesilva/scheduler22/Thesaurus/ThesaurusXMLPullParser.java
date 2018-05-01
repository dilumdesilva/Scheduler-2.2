package com.example.dilumdesilva.scheduler22.Thesaurus;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ThesaurusXMLPullParser {

    static final String KEY_LIST = "list"; //start tag of a synonym in the XML
    static final String KEY_CATEGORY = "category";
    static final String KEY_SYNONYMS = "synonyms";

    public static List<Synonym> getSynonymsFromFile(Context ctx) {

        // List of synonyms that we will return
        List<Synonym> synonymList;
        synonymList = new ArrayList<Synonym>();

        // temp holder for the current synonym while parsing
        Synonym currentSynonym = null;
        // temp holder for the current text value while parsing
        String currentText = "";

        try {
            // Get factory and PullParser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            // Open up InputStream and Reader for the xml file
            FileInputStream fis = ctx.openFileInput("synonyms.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            // point the parser to our file.
            xpp.setInput(reader);

            // get initial eventType
            int eventType = xpp.getEventType();

            // Loop through pull events until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Get the current tag
                String tagName = xpp.getName();

                // React to different event types appropriately
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(KEY_LIST)) {
                            // If we are starting a new <list> block we need
                            //a new synonym object to represent it
                            currentSynonym = new Synonym();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        //grab the current text so we can use it in END_TAG event
                        currentText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase(KEY_LIST)) {
                            // if </list> then we are done with current synonym
                            // add it to the list.
                            synonymList.add(currentSynonym);
                        } else if (tagName.equalsIgnoreCase(KEY_CATEGORY)) {
                            // if </category> , set the category for the current object using setCategory()
                            currentSynonym.setCategory(currentText);
                        } else if (tagName.equalsIgnoreCase(KEY_SYNONYMS)) {
                            // if </synonyms> , set the synonym for the current object using setSynonym()
                            currentSynonym.setSynonyms(currentText);
                        }
                        break;

                    default:
                        break;
                }
                //move on to next iteration
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return the populated list.
        return synonymList;
    }
}
