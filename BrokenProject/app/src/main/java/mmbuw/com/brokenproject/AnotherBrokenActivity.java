package mmbuw.com.brokenproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import mmbuw.com.brokenproject.R;

public class AnotherBrokenActivity extends Activity {

    EditText urlText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_broken);

        Intent intent = getIntent();
        String message = intent.getStringExtra(BrokenActivity.EXTRA_MESSAGE);
        //What happens here? What is this? It feels like this is wrong.
        //Maybe the weird programmer who wrote this forgot to do something?

        urlText = (EditText) findViewById(R.id.edittext);
        textView = (TextView) findViewById(R.id.brokenTextView);

        textView.setText(message);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.another_broken, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public class Fetcher implements Runnable {

        String url;

        public Fetcher(String theURL) {
            url = theURL;
        }

        public String fetch() throws IOException{
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(new HttpGet(url));
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                response.getEntity().writeTo(outStream);
                return outStream.toString();
            }else {
                //Well, this didn't work.
                response.getEntity().getContent().close();
                throw new IOException(status.getReasonPhrase());
            }
        }

        public void run() {
            Boolean success = false;
            String response;

            try {
                response = fetch();
                success = true;
            } catch (IOException e) {
                response = e.getMessage();
            } catch (Exception e) {
                response = "Unknown error. Did you forget to put http:// in front of the address?";
            }

            final Boolean finalSuccess = success;
            final String finalResponse = response;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (finalSuccess) {
                        textView.setText(finalResponse);
                    } else {
                        Context context = getApplicationContext();
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, finalResponse, duration);
                        toast.show();
                    }
                }
            });
        }
    }


    public void fetchHTML(View view) throws IOException {
        (new Thread(new Fetcher(urlText.getText().toString()))).start();
    }
}
