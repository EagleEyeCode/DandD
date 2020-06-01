package de.eagleeye.dandd.sql;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnection {
    public static final int NOT_CONNECTED = 0;
    public static final int CONNECTED = 1;
    public static final int CONNECTING = 2;

    private Connection con;
    private int state;
    private String url; // "jdbc:mysql://ip:port/db"
    private String user;
    private String password;

    private OnStateChangedListener onStateChangedListener;

    public void connect(String ip, String database, String username, String password){
        connect(ip, 3306, database, username, password);
    }

    public void connect(String ip, int port, String database, String username, String password){
        this.url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
        this.user = username;
        this.password = password;

        new SQLConnectionConnector().execute("");
    }

    public int getState(){
        return state;
    }

    public void query(SQLRequest request){
        new SQLRequestTask(request).execute("");
    }

    public void close(){
        try {
            con.close();
            con = null;
        } catch (Exception e) {
            Log.e(MySQLConnection.class.getSimpleName(), e.toString());
        }
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener){
        this.onStateChangedListener = onStateChangedListener;
    }

    public interface OnStateChangedListener{
        void OnStateChanged(int state);
    }

    private class SQLConnectionConnector extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            state = CONNECTING;
            if(onStateChangedListener != null) onStateChangedListener.OnStateChanged(state);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                con = DriverManager.getConnection(url, user, password);
                state = CONNECTED;
                if(onStateChangedListener != null) onStateChangedListener.OnStateChanged(state);
            } catch (Exception e) {
                Log.d(MySQLConnection.class.getSimpleName(), e.toString());
                result = e.toString();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(MySQLConnection.class.getSimpleName(), result);
            if(state == CONNECTING) {
                state = NOT_CONNECTED;
                if (onStateChangedListener != null) onStateChangedListener.OnStateChanged(state);
            }
        }
    }

    private class SQLRequestTask extends AsyncTask<String, Void, String> {
        private SQLRequest request;
        private ResultSet resultSet;

        public SQLRequestTask(SQLRequest request){
            this.request = request;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                Statement st = con.createStatement();
                resultSet = st.executeQuery(request.getQuery());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(request.getOnQueryResult() != null && resultSet != null) request.getOnQueryResult().onMySQLQueryResult(resultSet);
        }
    }
}
