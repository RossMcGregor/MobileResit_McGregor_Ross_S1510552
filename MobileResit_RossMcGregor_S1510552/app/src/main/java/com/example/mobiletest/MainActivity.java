package com.example.mobiletest;
//Mobile Resit Ross McGregor S1510552

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Button mGlasgowButton;
    private Button mLondonButton;
    private Button mNewYorkButton;
    private Button mOmanButton;
    private Button mMauritiusButton;
    private Button mBangladeshButton;
    private SwipeRefreshLayout mSwipeLayout;
    private TextView mFeedFound;

    private List<WeatherDisplayModel> mFeedModelList;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mGlasgowButton = (Button) findViewById(R.id.GlasgowButton);
        mLondonButton = (Button) findViewById(R.id.LondonButton);
        mNewYorkButton = (Button) findViewById(R.id.NewYorkButton);
        mOmanButton = (Button) findViewById(R.id.OmanButton);
        mMauritiusButton = (Button) findViewById(R.id.MauritiusButton);
        mBangladeshButton = (Button) findViewById(R.id.BangladeshButton);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mFeedFound = (TextView) findViewById(R.id.feedSuccess);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mGlasgowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GlasgowFeed().execute((Void) null);
            }
        });

        mLondonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LondonFeed().execute((Void) null);
            }
        });

        mNewYorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewYorkFeed().execute((Void) null);
            }
        });

        mOmanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new OmanFeed().execute((Void) null);
            }
        });

        mMauritiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MauritiusFeed().execute((Void) null);
            }
        });

        mBangladeshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BangladeshFeed().execute((Void) null);
            }
        });
    }

    public List<WeatherDisplayModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<WeatherDisplayModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        WeatherDisplayModel item = new WeatherDisplayModel(title, link, description);
                        ((ArrayList) items).add(item);
                    }
                    else {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }


    private class GlasgowFeed extends AsyncTask<Void, Void, Boolean> {

        private String GlasgowURL;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            GlasgowURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2648579";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(GlasgowURL))
                return false;

            try {
                URL url = new URL(GlasgowURL);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } catch (XmlPullParserException e) {
                Log.e("Error", String.valueOf(e));
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mFeedFound.setText("The Glasgow Weather For the next 3 days.");
                mRecyclerView.setAdapter(new WeatherFeedView(mFeedModelList));
            } else {
                Toast.makeText(MainActivity.this,
                        "Enter a valid Rss feed url",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class LondonFeed extends AsyncTask<Void, Void, Boolean> {

        private String LondonURL;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            LondonURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643743";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(LondonURL))
                return false;

            try {
                URL url = new URL(LondonURL);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } catch (XmlPullParserException e) {
                Log.e("Error", String.valueOf(e));
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mFeedFound.setText("The London Weather For the next 3 days.");
                mRecyclerView.setAdapter(new WeatherFeedView(mFeedModelList));
            } else {
                Toast.makeText(MainActivity.this,
                        "URL Error",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class NewYorkFeed extends AsyncTask<Void, Void, Boolean> {

        private String NewYorkURL;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            NewYorkURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/5128581";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(NewYorkURL))
                return false;

            try {
                URL url = new URL(NewYorkURL);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } catch (XmlPullParserException e) {
                Log.e("Error", String.valueOf(e));
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mFeedFound.setText("The New York Weather For the next 3 days.");
                mRecyclerView.setAdapter(new WeatherFeedView(mFeedModelList));
            } else {
                Toast.makeText(MainActivity.this,
                        "URL Error",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class OmanFeed extends AsyncTask<Void, Void, Boolean> {

        private String OmanURL;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            OmanURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/287286";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(OmanURL))
                return false;

            try {
                URL url = new URL(OmanURL);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } catch (XmlPullParserException e) {
                Log.e("Error", String.valueOf(e));
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mFeedFound.setText("The Oman Weather For the next 3 days.");
                mRecyclerView.setAdapter(new WeatherFeedView(mFeedModelList));
            } else {
                Toast.makeText(MainActivity.this,
                        "URL Error",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class MauritiusFeed extends AsyncTask<Void, Void, Boolean> {

        private String MauritiusURL;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            MauritiusURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/934154";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(MauritiusURL))
                return false;

            try {
                URL url = new URL(MauritiusURL);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } catch (XmlPullParserException e) {
                Log.e("Error", String.valueOf(e));
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mFeedFound.setText("The Mauritius Weather For the next 3 days.");
                mRecyclerView.setAdapter(new WeatherFeedView(mFeedModelList));
            } else {
                Toast.makeText(MainActivity.this,
                        "URL Error",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private class BangladeshFeed extends AsyncTask<Void, Void, Boolean> {

        private String BangladeshURL;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            BangladeshURL = "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/1185241";
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(BangladeshURL))
                return false;

            try {
                URL url = new URL(BangladeshURL);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("Error", String.valueOf(e));
            } catch (XmlPullParserException e) {
                Log.e("Error", String.valueOf(e));
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mFeedFound.setText("The Bangladesh Weather For the next 3 days.");
                mRecyclerView.setAdapter(new WeatherFeedView(mFeedModelList));
            } else {
                Toast.makeText(MainActivity.this,
                        "URL Error",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

}
