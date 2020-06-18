package de.eagleeye.dandd.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private final String[] TABLES = new String[]{"abilities", "actions", "alignments", "attacks", "classes", "fileTypes", "files", "itemTypes", "items", "languages", "monsterAbilities", "monsterAction", "monsterArmors", "monsterLanguages", "monsterTypes", "monsters", "savingThrows", "sourceCover", "sources", "spellSchools", "spells", "spellsClasses", "traits"};

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new UpdateTask().execute("");
    }

    private void showDataNotValidDialog() {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Data Not Valid!").setNeutralButton("Quit", (dialog1, which) -> finish())
                .setPositiveButton("Clean Reinstall", (dialog12, which) -> new UpdateTask().execute("clean")).setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdateTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            if(strings.length != 0 && strings[0].equals("clean")){
                deleteAllData();
            }

            updateStatus("Checking for Updates");
            downloadFile("packages.json");
            if (fileExists(getFileStreamPath("download").getPath() + "/packages.json")) {
                JSONArray updateList = getPackageUpdateList();
                for (int i = 0; i < updateList.length(); i++) {
                    try {
                        JSONObject pack = updateList.getJSONObject(i);
                        switch (pack.getString("action")) {
                            case "install":
                                updateStatus("Installing " + pack.getString("name"));
                                installPackage(pack);
                                pack.put("installed", true);
                                break;
                            case "update":
                                updateStatus("Updating " + pack.getString("name"));
                                updatePackage(pack);
                                break;
                            case "remove":
                                updateStatus("Removing " + pack.getString("name"));
                                removePackage(pack);
                                pack.put("installed", false);
                                break;
                        }
                    } catch (Exception e) {
                        Log.d(SplashActivity.this.getClass().getSimpleName(), "UpdateTask:", e);
                        return "not_valid";
                    }
                }
                getSharedPreferences("packages", MODE_PRIVATE).edit().putString("current", updateList.toString()).apply();
            } else {
                //Necessary Update not possible
                updateStatus("Check Internet connection!");
                return "exit";
            }

            if(!dataIsValid()){
                return "not_valid";
            }

            return "ok";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            switch (s) {
                case "ok":
                    sendToMainActivity();
                    break;
                case "exit":
                    new Handler().postDelayed(SplashActivity.this::finish, SPLASH_DISPLAY_LENGTH);
                    break;
                case "not_valid":
                    showDataNotValidDialog();
                    break;
            }
        }

        private void sendToMainActivity() {
            updateStatus("Finished");
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, SPLASH_DISPLAY_LENGTH);
        }

        private void updateStatus(String string) {
            runOnUiThread(() -> {
                TextView tv = findViewById(R.id.splash_status);
                tv.setText(string);
            });
        }

        private boolean fileExists(String path) {
            return new File(path).exists();
        }

        private JSONArray getPackageUpdateList() {
            String currentPackagesString = getSharedPreferences("packages", MODE_PRIVATE).getString("current", "[{name='Base Data', id=0, version=1, installed=False, wanted=True}, {name='Covers', id=2, version=1, installed=False, wanted=True}]");
            //String currentPackagesString = "[{name='Base Data', id=0, version=1, installed=False, wanted=True}, {name='Players Handbook', id=1, version=1, installed=False, wanted=True}]";
            try {
                File packagesFile = new File(getFileStreamPath("download").getPath() + "/packages.json");
                Scanner scanner = new Scanner(packagesFile);
                StringBuilder allPackagesString = new StringBuilder();
                while (scanner.hasNext()) {
                    allPackagesString.append(scanner.next());
                }

                JSONArray currentPackages = new JSONArray(currentPackagesString);
                JSONArray allPackages = new JSONArray(allPackagesString.toString());

                for (int i = 0; i < allPackages.length(); i++) {
                    allPackages.getJSONObject(i).put("action", "none");
                    int id = allPackages.getJSONObject(i).getInt("id");
                    int version = allPackages.getJSONObject(i).getInt("version");
                    boolean inCurrent = false;
                    for (int j = 0; j < currentPackages.length(); j++) {
                        JSONObject current = currentPackages.getJSONObject(j);
                        if (current.getInt("id") == id) {
                            inCurrent = true;
                            if (current.getBoolean("wanted")) {
                                if (current.getBoolean("installed")) {
                                    if (version > current.getInt("version")) {
                                        allPackages.getJSONObject(i).put("action", "update");
                                    }
                                } else {
                                    allPackages.getJSONObject(i).put("action", "install");
                                }
                            } else {
                                if (current.getBoolean("installed")) {
                                    allPackages.getJSONObject(i).put("action", "remove");
                                }
                            }
                            allPackages.getJSONObject(i).put("wanted", current.getBoolean("wanted"));
                            allPackages.getJSONObject(i).put("installed", current.getBoolean("installed"));
                        }
                    }
                    if(!inCurrent){
                        allPackages.getJSONObject(i).put("wanted", false);
                        allPackages.getJSONObject(i).put("installed", false);
                    }
                }
                return allPackages;
            } catch (Exception e) {
                Log.d(SplashActivity.this.getClass().getSimpleName(), "needsUpdate():", e);
            }
            return new JSONArray();
        }

        private void downloadFile(String file) {
            int count;
            try {
                URL url = new URL("http://RaspBox/download.php?file=" + file);
                URLConnection connection = url.openConnection();
                connection.connect();

                File path = getFileStreamPath("download");
                if (!path.mkdir()) {
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
                Log.e(SplashActivity.class.getSimpleName(), "File download:", e);
            }
        }

        public void unzip(String zipFilePath, String destDirectory) throws IOException {
            File destDir = new File(destDirectory);
            if (!destDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
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
                    //noinspection ResultOfMethodCallIgnored
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
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
            bos.close();
        }

        private boolean moveFile(File file, File destinationFile) {
            if (file.renameTo(destinationFile)) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                return true;
            } else {
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
            if (unzipped) {
                unzipped = moveFile(getFileStreamPath(dbName), getDatabasePath(dbName));
            }

            if (unzipped) {
                unzipped = new File(getFileStreamPath("download"), pack.getString("file") + ".zip").delete();
            }

            mergeDatabase(dbName);

            if (unzipped) {
                unzipped = getDatabasePath(dbName).delete();
            }

            if (unzipped) {
                unzipped = getDatabasePath(dbName + "-journal").delete();
            }

            if (unzipped) {
                Log.d(SplashActivity.this.getClass().getSimpleName(), "Successfully installed " + pack.getString("name"));
            } else {
                Log.d(SplashActivity.this.getClass().getSimpleName(), "Failed to install " + pack.getString("name"));
            }
        }

        private void updatePackage(JSONObject pack) throws JSONException {
            removePackage(pack);
            installPackage(pack);
        }

        private void removePackage(JSONObject pack) throws JSONException {
            BasicSQLiteHelper data = new BasicSQLiteHelper(SplashActivity.this, "data.db");
            data.query(new SQLRequest("SELECT path FROM files WHERE sourceId=" + pack.getString("id") + ";", cursor -> {
                if(cursor != null){
                    cursor.moveToFirst();
                    do{
                        //noinspection ResultOfMethodCallIgnored
                        new File(getFileStreamPath(""), cursor.getString(0)).delete();
                    } while (cursor.moveToNext());
                }
            }), true);

            for (String table : TABLES){
                switch (table){
                    case "sources":
                        data.query(new SQLRequest("DELETE FROM sources WHERE id=" + pack.getString("id")), true);
                        break;
                    case "spellsClasses":
                        data.query(new SQLRequest("DELETE FROM " + table + " WHERE spellSourceId=" + pack.getString("id")), true);
                        break;
                    case "monsterAbilities":
                    case "monsterLanguages":
                    case "monsterAction":
                        data.query(new SQLRequest("DELETE FROM " + table + " WHERE monsterSourceId=" + pack.getString("id")), true);
                        break;
                    default:
                        data.query(new SQLRequest("DELETE FROM " + table + " WHERE sourceId=" + pack.getString("id")), true);
                        break;
                }
            }
        }

        private void mergeDatabase(String db) {
            if (!fileExists(getDatabasePath("data.db").getPath())) {
                downloadFile("data.db");
                moveFile(new File(getFileStreamPath("download"), "data.db"), getDatabasePath("data.db"));
            }
            BasicSQLiteHelper data = new BasicSQLiteHelper(SplashActivity.this, "data.db");
            BasicSQLiteHelper pack = new BasicSQLiteHelper(SplashActivity.this, db);

            for (String table : TABLES) {
                SQLRequest read = new SQLRequest("SELECT * FROM " + table, cursor -> {
                    if(cursor != null) {
                        cursor.moveToFirst();
                        do {
                            int columns = cursor.getColumnCount();
                            StringBuilder dataString = new StringBuilder();
                            for (int i = 0; i < columns; i++) {
                                if (dataString.length() != 0) dataString.append(", ");
                                dataString.append('"').append(cursor.getString(i)).append('"');
                            }
                            data.query(new SQLRequest("INSERT INTO " + table + " VALUES (" + dataString.toString() + ");"), true);
                        } while (cursor.moveToNext());
                    }
                });
                pack.query(read, true);
            }
        }

        private boolean dataIsValid(){
            updateStatus("Validating Data");

            File dbFile = getDatabasePath("data.db");
            if(!dbFile.exists() || !dbFile.isFile()) return false;

            AtomicInteger missingFiles = new AtomicInteger();
            BasicSQLiteHelper db = new BasicSQLiteHelper(SplashActivity.this, "data.db");
            SQLRequest files = new SQLRequest("SELECT path FROM files;", cursor -> {
                cursor.moveToFirst();
                do {
                    File file = new File(getFileStreamPath(""), cursor.getString(0));
                    if (!file.exists() || !file.isFile()) {
                        missingFiles.getAndIncrement();
                    }
                } while (cursor.moveToNext());
            });
            db.query(files, true);

            return missingFiles.get() <= 0;
        }

        private void deleteAllData() {
            File[] files = getFileStreamPath("").listFiles();
            File[] databases = getDatabasePath("data.db").getParentFile().listFiles();

            if(files != null) {
                for (File file : files) {
                    if(file.isFile()){
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }else{
                        deleteDirectory(file);
                    }
                }
            }

            if(databases != null) {
                for (File file : databases) {
                    if(file.isFile()){
                        //noinspection ResultOfMethodCallIgnored
                        file.delete();
                    }else{
                        deleteDirectory(file);
                    }
                }
            }

            try {
                JSONArray packages = new JSONArray(getSharedPreferences("packages", MODE_PRIVATE).getString("current","[]"));
                for(int i = 0; i < packages.length(); i++){
                    packages.getJSONObject(i).put("installed", false);
                }
                getSharedPreferences("packages", MODE_PRIVATE).edit().putString("current", packages.toString()).apply();
            } catch (JSONException e) {
                Log.d(SplashActivity.this.getClass().getSimpleName(), "deleteAlData()", e);
            }
        }

        private void deleteDirectory(File directoryToBeDeleted) {
            File[] allContents = directoryToBeDeleted.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectory(file);
                }
            }
            //noinspection ResultOfMethodCallIgnored
            directoryToBeDeleted.delete();
        }
    }
}
