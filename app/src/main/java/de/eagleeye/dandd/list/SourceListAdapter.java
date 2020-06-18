package de.eagleeye.dandd.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.activities.SplashActivity;
import de.eagleeye.dandd.sql.BasicSQLiteHelper;
import de.eagleeye.dandd.sql.SQLRequest;

public class SourceListAdapter extends RecyclerView.Adapter<SourceListAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<SourceListItem> items;

    public SourceListAdapter(Activity activity){
        this.activity = activity;
        this.items = new ArrayList<>();
        refresh();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_source_item, parent, false);
        return new SourceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.image.setImageBitmap(BitmapFactory.decodeFile(activity.getFileStreamPath("").getPath() + "/" + items.get(position).getImage()));
        if(items.get(position).isInstalled()){
            holder.installed.setVisibility(View.VISIBLE);
            holder.download.setVisibility(View.GONE);
        }else{
            holder.installed.setVisibility(View.GONE);
            holder.download.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            if(!items.get(position).isInstalled()){
                showInstallDialog(items.get(position).getName(), items.get(position).getId());
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if(items.get(position).isInstalled()){
                showRemoveDialog(items.get(position).getName(), items.get(position).getId());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void refresh(){
        new RefreshTask().execute();
    }

    private void showInstallDialog(String name, int id){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Install " + name).setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    updateWantedParameter(id, true);
                    sendToSplashScreen();
                });
        dialog = builder.create();
        dialog.show();
    }

    private void showRemoveDialog(String name, int id){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Remove " + name).setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    updateWantedParameter(id, false);
                    sendToSplashScreen();
                });
        dialog = builder.create();
        dialog.show();
    }

    private void updateWantedParameter(int id, boolean value){
        try {
            JSONArray packages = new JSONArray(activity.getSharedPreferences("packages", Context.MODE_PRIVATE).getString("current", "[]"));
            for (int i = 0; i < packages.length(); i++){
                JSONObject pack = packages.getJSONObject(i);
                if(pack.getInt("id") == id){
                    pack.put("wanted", value);
                }
            }
            activity.getSharedPreferences("packages", Context.MODE_PRIVATE).edit().putString("current", packages.toString()).apply();
        } catch (JSONException e) {
            Log.d(getClass().getSimpleName(), "updateWantedParameter()", e);
        }

    }

    private void sendToSplashScreen(){
        Intent intent = new Intent(activity, SplashActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView image;
        TextView name;
        TextView installed;
        ImageView download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            image = view.findViewById(R.id.list_source_image);
            name = view.findViewById(R.id.list_source_name);
            installed = view.findViewById(R.id.list_source_installed);
            download = view.findViewById(R.id.list_source_download);
        }
    }

    class RefreshTask extends AsyncTask<Void, Void, Void>{
        ArrayList<SourceListItem> newList;
        JSONArray packages;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                packages = new JSONArray(activity.getSharedPreferences("packages", Context.MODE_PRIVATE).getString("current", "[]"));
            } catch (JSONException e) {
                Log.d(getClass().getSimpleName(), "onReExecute()", e);
                packages = new JSONArray();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            BasicSQLiteHelper db = new BasicSQLiteHelper(activity, "data.db");
            newList = new ArrayList<>();
            for(int i = 0; i < packages.length(); i++){
                try {
                    JSONObject pack = packages.getJSONObject(i);
                    int id = pack.getInt("id");
                    if(id != 0) {
                        AtomicReference<String> imagePath = new AtomicReference<>();
                        SQLRequest imagePathRequest = new SQLRequest("SELECT path FROM files JOIN sources ON files.id=sources.imageId AND files.sourceId=sources.imageSourceId WHERE sources.id=" + id + " AND sources.imageId NOT NULL;", cursor -> {
                            if(cursor != null && cursor.getCount() != 0) {
                                cursor.moveToFirst();
                                imagePath.set(cursor.getString(0));
                            }else{
                                imagePath.set(null);
                            }
                        });
                        db.query(imagePathRequest, true);
                        newList.add(new SourceListItem(pack.getString("name"), pack.getInt("id"), imagePath.get(), pack.getBoolean("installed"), pack.getBoolean("wanted")));
                    }
                } catch (JSONException e) {
                    Log.d(getClass().getSimpleName(), "doInBackground()", e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            items = newList;
            notifyDataSetChanged();
        }
    }
}
