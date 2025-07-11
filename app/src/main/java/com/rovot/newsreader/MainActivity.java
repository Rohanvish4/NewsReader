package com.rovot.newsreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ArrayList<NewsItem> news; // ArrayList to store news items
    private RecyclerView recyclerView; // RecyclerView to display news items
    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        news = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new NewsAdapter(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        new GetNews().execute(); // Start the background task to fetch and parse news
    }

    // AsyncTask to handle network and parsing operations in the background (deprecated)
    private class GetNews extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream = getInputStream(); // Get the InputStream from the URL
            if (inputStream != null) {
                try {
                    initXMLPullParser(inputStream); // Parse the XML data
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        // Get InputStream from the given URL
        private InputStream getInputStream() {
            try {
                URL url = new URL("https://timesofindia.indiatimes.com/rssfeeds/54829575.cms");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Update UI with the parsed news items
            adapter.setNews(news);
        }

        // Parsing the RSS feed using XmlPullParser
        private void initXMLPullParser(InputStream inputStream) throws XmlPullParserException, IOException {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "rss");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                parser.require(XmlPullParser.START_TAG, null, "channel");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }

                    if (parser.getName().equals("item")) {
                        parser.require(XmlPullParser.START_TAG, null, "item");

                        String title = "";
                        String description = "";
                        String imageUrl = "";
                        String link = "";
                        String date = "";

                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }

                            String tagName = parser.getName();
                            if (tagName.equals("title")) {
                                title = getContent(parser, "title");
                            } else if (tagName.equals("description")) {
                                String rawDescription = getContent(parser, "description");
                                imageUrl = extractImageUrl(rawDescription); // Extract image URL
                                description = stripHtml(rawDescription);    // Clean description
                            } else if (tagName.equals("link")) {
                                link = getContent(parser, "link");

                            }else if (tagName.equals("pubDate")) { // Ensure pubDate is correctly parsed
                                date = getContent(parser, "pubDate");
                            } else {
                                skipTag(parser);
                            }
                        }

                        NewsItem item = new NewsItem(title, description, date, imageUrl, link); // Ensure date is passed correctly
                        news.add(item);
                    } else {
                        skipTag(parser);
                    }
                }
            }
        }

        // Helper function to extract the first image URL from the <img> tag
        private String extractImageUrl(String rawDescription) {
            if (rawDescription.contains("<img ")) {
                String imgTag = rawDescription.split("<img ")[1]; // Extract <img> tag
                String imgUrl = imgTag.split("src=\"")[1]; // Extract URL part
                return imgUrl.split("\"")[0]; // Get the URL only
            }
            return ""; // No image found
        }

        // Helper function to extract content from a tag
        private String getContent(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, null, tag);
            String content = "";
            if (parser.next() == XmlPullParser.TEXT) {
                content = parser.getText();
                parser.nextTag();
            }
            parser.require(XmlPullParser.END_TAG, null, tag);
            return content;
        }

        // Helper function to skip irrelevant tags
        private void skipTag(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

        // Helper function to strip HTML tags (such as <img>) from the description
        private String stripHtml(String input) {
            return input.replaceAll("<[^>]*>", ""); // This regex removes all HTML tags
        }
    }
}
