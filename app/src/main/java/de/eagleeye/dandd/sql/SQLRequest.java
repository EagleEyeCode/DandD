package de.eagleeye.dandd.sql;

import android.database.Cursor;
import android.os.AsyncTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLRequest {
    private String query;
    private OnQueryResult onQueryResult;

    public SQLRequest(String query){
        this.query = query;
    }

    public SQLRequest(String query, OnQueryResult onQueryResult){
        this.query = query;
        this.onQueryResult = onQueryResult;
    }

    public String getQuery() {
        return query;
    }

    public OnQueryResult getOnQueryResult() {
        return onQueryResult;
    }

    public interface OnQueryResult{
        void onMySQLQueryResult(ResultSet resultSet);
        void onSQLiteQueryResult(Cursor cursor);
    }
}
