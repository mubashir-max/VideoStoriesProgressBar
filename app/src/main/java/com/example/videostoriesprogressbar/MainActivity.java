package com.example.videostoriesprogressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> videoList = new ArrayList<>();
    Button btn_play_status;
    TextView download_Status;
    int videon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_play_status = findViewById(R.id.btn_play_status);
        download_Status = findViewById(R.id.download_Status);
        videoList.add("https://firebasestorage.googleapis.com/v0/b/storiesprogressbar.appspot.com/o/videos%2F7213(1).mp4?alt=media&token=605784f7-d8a0-4bb0-af71-231e3f02a012");
        videoList.add("https://firebasestorage.googleapis.com/v0/b/storiesprogressbar.appspot.com/o/videos%2F15047(2).mp4?alt=media&token=4add1994-337b-437a-abd1-cb8db2bc246d");
        videoList.add("https://firebasestorage.googleapis.com/v0/b/storiesprogressbar.appspot.com/o/videos%2F11376(3).MOV?alt=media&token=f9166eb4-a821-474e-b05b-95c612a9b386");
        videoList.add("https://firebasestorage.googleapis.com/v0/b/storiesprogressbar.appspot.com/o/videos%2F15047(4).mp4?alt=media&token=487a0b82-bd82-4ea3-a1e6-586b0460e70a");
        videoList.add("https://firebasestorage.googleapis.com/v0/b/storiesprogressbar.appspot.com/o/videos%2F15047(5).mp4?alt=media&token=f9cde2c6-4f36-4566-b278-79fa60d0846b");
        videoList.add("https://firebasestorage.googleapis.com/v0/b/storiesprogressbar.appspot.com/o/videos%2F15047(6).mp4?alt=media&token=3023eeae-b74c-48ea-9135-4620e324eb31");
        bgdowntask(videoList, "TestVideo_");
        btn_play_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StatusView.class));
            }
        });
    }

    private void bgdowntask(ArrayList<String> videoList, String tempPath) {

        class DownloadNotExistingVideos extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                for (int i = 0; i < videoList.size(); i++) {
                    videon++;
                    downloadManager(videoList.get(i), tempPath + i, videon);
                    if (i == 5) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                download_Status.setText("Downloading Videos Completed");
                                btn_play_status.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
                return null;
            }
        }
        DownloadNotExistingVideos downloadNotExistingVideos = new DownloadNotExistingVideos();
        downloadNotExistingVideos.execute();
    }

    private void downloadManager(String url, String flname, int videono) {
        InputStream input = null;
        FileOutputStream output = null;
        HttpsURLConnection connection = null;

        String fileName = flname + ".mp4";
        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/StatusVideos/";
        File file = new File(path);
        if (file.exists() == false) {
            file.mkdirs();
        }
        File rootFile = new File(path, fileName);
        if (rootFile.exists()) {
            Log.d("FileExist", "" + fileName);
        } else {
            try {
                rootFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Log.d("Downloading...", url);
                URL urlll = new URL(url);
                connection = (HttpsURLConnection) urlll.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                    Log.d("Server returned HTTP", "" + connection.getResponseCode() + " " + connection.getResponseMessage());
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                output = new FileOutputStream(rootFile);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {

                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
//                    Log.d("Progress % ", "" + (int) (total * 100 / fileLength));
                    {
                        long finalTotal = total;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                download_Status.setText("6 Videos Total\nDownloading Vdeo " + videono + "\nCurrentProgress :: " + (int) (finalTotal * 100 / fileLength) + " %");
                            }
                        });
                    }
                    output.write(data, 0, count);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    Log.d("DownloadComplete", "" + fileName);
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return;
        }
    }
}