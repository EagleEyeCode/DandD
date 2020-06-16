package de.eagleeye.dandd.sql;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class BasicSQLiteHelper extends SQLiteOpenHelper {
    public BasicSQLiteHelper(@Nullable Context context, @NonNull String name){
        super(context, name, null, 1);
    }

    public BasicSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BasicSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public BasicSQLiteHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //No Upgrade Behavior
    }

    public void query(@NonNull SQLRequest request){
        new QueryTask().execute(request);
    }

    public void query(@NonNull SQLRequest request, boolean wait){
        if(wait){
            Cursor cursor = null;
            SQLiteDatabase db;
            Log.d("Performing Query", request.getQuery());
            if((db = getWritableDatabase()) != null){
                cursor = db.rawQuery(request.getQuery(), null);
            }

            if(cursor == null || cursor.getCount() == 0){
                if(request.getOnQueryResult() != null) request.getOnQueryResult().onSQLiteQueryResult(null);
            }else{
                if(request.getOnQueryResult() != null) request.getOnQueryResult().onSQLiteQueryResult(cursor);
            }
        }else{
            new QueryTask().execute(request);
        }
    }

    private class QueryTask extends AsyncTask<SQLRequest, Void, Void>{
        SQLRequest request;
        Cursor cursor;

        @Override
        protected Void doInBackground(SQLRequest... requests) {
            SQLiteDatabase db;
            request = requests[0];
            Log.d("Performing Query", request.getQuery());
            if((db = getWritableDatabase()) != null){
                cursor = db.rawQuery(request.getQuery(), null);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(cursor.getCount() == 0){
                if(request.getOnQueryResult() != null) request.getOnQueryResult().onSQLiteQueryResult(null);
            }else{
                if(request.getOnQueryResult() != null) request.getOnQueryResult().onSQLiteQueryResult(cursor);
            }
        }
    }
}
