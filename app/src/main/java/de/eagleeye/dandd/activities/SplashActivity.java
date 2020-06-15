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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new UpdateTask().execute("");
    }

    private void parseZip(){
//        TextView tv = findViewById(R.id.splash_status);
//        tv.setText(R.string.unpacking);
//
//        deleteDirectory(getDatabasePath("data.db"));
//        deleteDirectory(getFileStreamPath("pdf"));
//        deleteDirectory(getFileStreamPath("images"));
//
//        boolean parsed = true;
//        try {
//            File zip = new File(getFileStreamPath("download"), "dandd_version_" + newestVersion + ".zip");
//            unzip(zip.getPath(), getFileStreamPath("").getPath());
//        } catch (IOException e) {
//            Log.d(SplashActivity.class.getSimpleName(), "Unzip failed:", e);
//            parsed = false;
//        }
//
//        if(parsed){
//            parsed = moveFile(getFileStreamPath("data.db"), getDatabasePath("data.db"));
//        }
//
//        new File(getFileStreamPath("download"), "dandd_version_" + newestVersion + ".zip").delete();
//
//        if(parsed) newestVersion = version;
//        validateData();
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

    private void validateData(){
//        TextView tv = findViewById(R.id.splash_status);
//        tv.setText(R.string.check_db);
//        if(getDatabasePath("data.db").exists() && getDatabasePath("data.db").isFile()) {
//            BasicSQLiteHelper sqLiteHelper = new BasicSQLiteHelper(this, "data.db");
//            SQLRequest request = new SQLRequest("SELECT path FROM files", new SQLRequest.OnQueryResult() {
//                @Override
//                public void onMySQLQueryResult(ResultSet resultSet) {
//                }
//
//                @Override
//                public void onSQLiteQueryResult(Cursor cursor) {
//                    tv.setText(R.string.check_files);
//                    boolean valid = true;
//                    if (cursor.getCount() == 0) valid = false;
//
//                    cursor.moveToFirst();
//                    do {
//                        if (!valid) break;
//                        File f = getFileStreamPath("");
//                        for(String s : cursor.getString(0).split("/")) {
//                            f = new File(f, s);
//                        }
//                        valid = f.exists() && f.isFile();
//                    } while (cursor.moveToNext());
//
//                    if (valid) {
//                        tv.setText(R.string.finished);
//                        new Handler().postDelayed(() -> {
//                            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
//                            SplashActivity.this.startActivity(mainIntent);
//                            SplashActivity.this.finish();
//                        }, SPLASH_DISPLAY_LENGTH);
//                        version = newestVersion;
//                        getPreferences(MODE_PRIVATE).edit().putInt("version", version).apply();
//                    } else {
//                        showDataNotValidDialog();
//                    }
//                }
//            });
//            sqLiteHelper.query(request);
//        }else{
//            showDataNotValidDialog();
//        }
    }

    private void showDataNotValidDialog(){
//        AlertDialog dialog;
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setTitle("Data Not Valid!").setNeutralButton("Quit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        }).setPositiveButton("(Re)Download", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                downloadFiles();
//            }
//        }).setCancelable(false);
//
//        dialog = builder.create();
//        dialog.show();
    }

    private class UpdateTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            updateStatus("Checking for Updates");
            downloadFile("packages.json");
            if(fileExists(getFileStreamPath("download").getPath() + "/packages.json")){
                JSONArray updateList = getPackageUpdateList();
                for(int i = 0; i < updateList.length(); i++){
                    try {
                        JSONObject pack = updateList.getJSONObject(i);
                        switch (pack.getString("action")){
                            case "install":
                                updateStatus("Installing " + pack.getString("name"));
                                installPackage(pack);
                                break;
                            case "update":
                                updateStatus("Updating " + pack.getString("name"));
                                updatePackage(pack);
                                break;
                            case "remove":
                                updateStatus("Removing " + pack.getString("name"));
                                removePackage(pack);
                                break;
                        }
                    } catch (Exception e) {
                        Log.d(SplashActivity.this.getClass().getSimpleName(), "UpdateTask:", e);
                    }
                }

            }else{
                //Necessary Update not possible
                updateStatus("Check Internet connection!");
                return "";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null) {
                sendToMainActivity();
            }else{
                new Handler().postDelayed(SplashActivity.this::finish, SPLASH_DISPLAY_LENGTH);
            }
            super.onPostExecute(s);
        }

        private void sendToMainActivity(){
            updateStatus("Finished");
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }, SPLASH_DISPLAY_LENGTH);
        }

        private void updateStatus(String string){
            runOnUiThread(() -> {
                TextView tv = findViewById(R.id.splash_status);
                tv.setText(string);
            });
        }

        private boolean fileExists(String path){
            return new File(path).exists();
        }

        private JSONArray getPackageUpdateList(){
            String currentPackagesString = getSharedPreferences("packages", MODE_PRIVATE).getString("current", "[{name='BaseData', version=1, installed=False, wanted=True}]");

            try {
                File packagesFile = new File(getFileStreamPath("download").getPath() + "/packages.json");
                Scanner scanner = new Scanner(packagesFile);
                StringBuilder allPackagesString = new StringBuilder();
                while (scanner.hasNext()){
                    allPackagesString.append(scanner.next());
                }

                JSONArray currentPackages = new JSONArray(currentPackagesString);
                JSONArray allPackages = new JSONArray(allPackagesString.toString());

                for (int i = 0; i < allPackages.length(); i++){
                    allPackages.getJSONObject(i).put("action", "none");
                    String name = allPackages.getJSONObject(i).getString("name");
                    int version = allPackages.getJSONObject(i).getInt("version");
                    for(int j = 0; j < currentPackages.length(); j++){
                        JSONObject current = currentPackages.getJSONObject(j);
                        if(current.getString("name").equals(name)){
                            if(current.getBoolean("wanted")){
                                if(current.getBoolean("installed")){
                                    if(version > current.getInt("version")){
                                        allPackages.getJSONObject(i).put("action", "update");
                                    }
                                }else{
                                    allPackages.getJSONObject(i).put("action", "install");
                                }
                            }else{
                                if(!current.getBoolean("installed")){
                                    allPackages.getJSONObject(i).put("action", "remove");
                                }
                            }
                        }
                    }
                }
                return allPackages;
            } catch (Exception e){
                Log.d(SplashActivity.this.getClass().getSimpleName(), "needsUpdate():", e);
            }
            return new JSONArray();
        }

        private void downloadFile(String file){
            int count;
            try {
                URL url = new URL("http://RaspBox/download.php?file=" + file);
                URLConnection connection = url.openConnection();
                connection.connect();

                File path = getFileStreamPath("download");
                if(!path.mkdir()){
                    Log.d(SplashActivity.this.getClass().getSimpleName(), "downloadFile(): could not make directories");
                }

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(path.toString() + "/" + file);

                Log.d(SplashActivity.this.getClass().getSimpleName(), "Downloading File: " + path.toString() + "/" + file);

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
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                return true;
            }else{
                return false;
            }
        }

        private void installPackage(JSONObject pack) throws JSONException {
            downloadFile(pack.getString("file") + ".zip");
            boolean unzipped = true;
            try {
                File zip = new File(getFileStreamPath("download"), pack.getString("file") + ".zip");
                unzip(zip.getPath(), getFileStreamPath("").getPath());
            } catch (IOException e) {
                Log.d(SplashActivity.class.getSimpleName(), "Unzip failed:", e);
                unzipped = false;
            }

            String dbName = pack.getString("file").replace("_" + pack.getString("file").split("_")[pack.getString("file").split("_").length - 1], ".db");
            if(unzipped){
                unzipped = moveFile(getFileStreamPath(dbName), getDatabasePath(dbName));
            }

            if(unzipped) {
                unzipped = new File(getFileStreamPath("download"), pack.getString("file") + ".zip").delete();
            }

            mergeDatabase(getDatabasePath(dbName));

            if(unzipped){
                Log.d(SplashActivity.this.getClass().getSimpleName(), "Successfully installed " + pack.getString("name"));
            }else{
                Log.d(SplashActivity.this.getClass().getSimpleName(), "Failed to install " + pack.getString("name"));
            }
        }

        private void updatePackage(JSONObject pack) throws JSONException {
            removePackage(pack);
            installPackage(pack);
        }

        private void removePackage(JSONObject pack){
            //TODO: Remove Packages
        }

        private void mergeDatabase(File db){
            //TODO: MergeDatabase
            //TODO: Download Empty Database if needed
        }
    }
}
