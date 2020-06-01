package de.eagleeye.dandd.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class SplashActivity extends AppCompatActivity {
    private int version;
    private int newestVersion;

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        version = getPreferences(MODE_PRIVATE).getInt("version", 0);
        newestVersion = version;

        startQueue();
    }

    private void startQueue() {
        TextView tv = findViewById(R.id.splash_status);
        tv.setText(R.string.check_updates);
        new DownloadFileFromURL().execute("versions.txt");
    }

    private void parseVersionsFile() {
        File path = getFileStreamPath("download");
        path = new File(path, "versions.txt");
        try {
            List<String> versions = Files.readAllLines(path.toPath());
            newestVersion = Integer.parseInt(versions.get(versions.size() - 1));
           if(version < newestVersion){
                version = Integer.parseInt(versions.get(versions.size() - 1));
                path.delete();
                downloadFiles();
            }else{
                validateData();
            }
        } catch (IOException e) {
            validateData();
        }
    }

    private void parseZip(){
        TextView tv = findViewById(R.id.splash_status);
        tv.setText(R.string.unpacking);

        deleteDirectory(getDatabasePath("data.db"));
        deleteDirectory(getFileStreamPath("pdf"));
        deleteDirectory(getFileStreamPath("images"));

        boolean parsed = true;
        try {
            File zip = new File(getFileStreamPath("download"), "dandd_version_" + newestVersion + ".zip");
            unzip(zip.getPath(), getFileStreamPath("").getPath());
        } catch (IOException e) {
            Log.d(SplashActivity.class.getSimpleName(), "Unzip failed:", e);
            parsed = false;
        }

        if(parsed){
            parsed = moveFile(getFileStreamPath("data.db"), getDatabasePath("data.db"));
        }

        new File(getFileStreamPath("download"), "dandd_version_" + newestVersion + ".zip").delete();

        if(parsed) newestVersion = version;
        validateData();
    }

    private boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private boolean moveFile(File file, File destinationFile){
        if(file.renameTo(destinationFile)) {
            file.delete();
            return true;
        }else{
            return false;
        }
    }

    private void downloadFiles(){
        TextView tv = findViewById(R.id.splash_status);
        tv.setText(R.string.downloading_files);
        new DownloadFileFromURL().execute("dandd_version_" + newestVersion + ".zip");
    }

    private void validateData(){
        TextView tv = findViewById(R.id.splash_status);
        tv.setText(R.string.check_db);
        if(getDatabasePath("data.db").exists() && getDatabasePath("data.db").isFile()) {
            BasicSQLiteHelper sqLiteHelper = new BasicSQLiteHelper(this, "data.db");
            SQLRequest request = new SQLRequest("SELECT path FROM files", new SQLRequest.OnQueryResult() {
                @Override
                public void onMySQLQueryResult(ResultSet resultSet) {
                }

                @Override
                public void onSQLiteQueryResult(Cursor cursor) {
                    tv.setText(R.string.check_files);
                    boolean valid = true;
                    if (cursor.getCount() == 0) valid = false;

                    cursor.moveToFirst();
                    do {
                        if (!valid) break;
                        File f = getFileStreamPath("");
                        for(String s : cursor.getString(0).split("/")) {
                            f = new File(f, s);
                        }
                        valid = f.exists() && f.isFile();
                    } while (cursor.moveToNext());

                    if (valid) {
                        tv.setText(R.string.finished);
                        new Handler().postDelayed(() -> {
                            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                            SplashActivity.this.startActivity(mainIntent);
                            SplashActivity.this.finish();
                        }, SPLASH_DISPLAY_LENGTH);
                        version = newestVersion;
                        getPreferences(MODE_PRIVATE).edit().putInt("version", version).apply();
                    } else {
                        showDataNotValidDialog();
                    }
                }
            });
            sqLiteHelper.query(request);
        }else{
            showDataNotValidDialog();
        }
    }

    private void showDataNotValidDialog(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Data Not Valid!").setNeutralButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setPositiveButton("(Re)Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadFiles();
            }
        }).setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    private class DownloadFileFromURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... file) {
            int count;
            try {
                URL url = new URL("http://RaspBox/download.php?file=" + file[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                File path = getFileStreamPath("download");
                path.mkdirs();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(path.toString() + "/" + file[0]);

                byte[] data = new byte[1024];
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
                Log.e(SplashActivity.class.getSimpleName(), "File download:",  e);
            }

            return file[0];
        }

        @Override
        protected void onPostExecute(String file) {
            if(file.equals("versions.txt")){
                parseVersionsFile();
            }else{
                parseZip();
            }
        }

    }
}
