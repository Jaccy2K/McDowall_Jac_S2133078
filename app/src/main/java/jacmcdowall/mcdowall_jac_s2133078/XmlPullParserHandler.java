// Jac Stephen McDowall
// S2133078
// Glasgow Caledonian University

package jacmcdowall.mcdowall_jac_s2133078;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class XmlPullParserHandler extends Thread {
        String parseTitle = "test";
        String parseDescription = "test";
        String parseGeoLocation = "test";

        ArrayList<Item> itemsList = new ArrayList<>();

        String inputURLThread;

        public XmlPullParserHandler(String inputURL) {
            inputURLThread = inputURL;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(inputURLThread);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();

                XmlPullParserFactory xppFactory = XmlPullParserFactory.newInstance();
                xppFactory.setNamespaceAware(true);
                XmlPullParser xpp = xppFactory.newPullParser();
                xpp.setInput(inputStream, null);

                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    eventType = xpp.next();

                    if (eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        eventType = xpp.next();

                        while (eventType != XmlPullParser.END_TAG && xpp.getName() != "item") {
                            eventType = xpp.next();

                            if (eventType == XmlPullParser.START_TAG) {
                                if (xpp.getName().equalsIgnoreCase("title")) {
                                    parseTitle = xpp.nextText().trim();
                                } else if (xpp.getName().equalsIgnoreCase("description")) {
                                    parseDescription = xpp.nextText().trim();
                                } else if (xpp.getName().equalsIgnoreCase("georss:point")) {
                                    parseGeoLocation = xpp.nextText().trim();
                                }
                            }

                        }
                        itemsList.add(new Item(parseTitle, parseDescription, parseGeoLocation));
                    }
                }
            } catch (XmlPullParserException e) {
                Log.e("e", "XmlPullParserException error.");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("e", "IOException error.");
                e.printStackTrace();
            }
        }

        public ArrayList<Item> getItemArrayList() {
            return itemsList;
        }
    }