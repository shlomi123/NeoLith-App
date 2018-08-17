package com.shlomi123.chocolith;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;



import java.util.List;

public class APP_INITIALIZATION extends AppCompatActivity {

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app__initialization);

        new MyTask().execute(10);
        //TODO check for new products
        //TODO download product images
        /*FirebaseStorage storage = FirebaseStorage.getInstance();
        List<FileDownloadTask> tasks = storage.getReferenceFromUrl("gs://tutsplus-firebase.appspot.com").getActiveDownloadTasks();*/
    }

    class MyTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task Completed.";
        }
        @Override
        protected void onPostExecute(String result) {
            spinner.setVisibility(View.GONE);
        }
        @Override
        protected void onPreExecute() {
            spinner=(ProgressBar)findViewById(R.id.progressBar);
            spinner.setVisibility(View.VISIBLE);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
