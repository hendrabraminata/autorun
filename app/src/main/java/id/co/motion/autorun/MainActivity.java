package id.co.motion.autorun;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import java.util.HashSet;

import id.co.motion.autorun.ListAdapter.app_data;

public class MainActivity extends ListActivity {

    static final String CONF_DATA = "conf_data";
    static final String CONF_PKG = "conf_pkg";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_FIRST_USER) {
            String str = data.getStringExtra(CONF_PKG);
            new AsyncTask<String, Void, Object>() {
                protected Object doInBackground(String... param) {
                    Context ctx = getApplicationContext();
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                    HashSet cur_data = (HashSet) pref.getStringSet(CONF_DATA, null);
                    HashSet upd_data = (cur_data != null) ? new HashSet(cur_data) : new HashSet();
                    upd_data.add(param[0]);
                    pref.edit().putStringSet(CONF_DATA, upd_data).apply();
                    return null;
                }

                protected void onPostExecute(Object obj) {
                    ((ListAdapter) getListAdapter()).refresh();
                }
            }.execute(str);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setListAdapter(new ListAdapter(getApplicationContext()));

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ListAdapter list = (ListAdapter) getListAdapter();

        if (list.isFooter(position)) {
            startActivityForResult(new Intent(this, ChooseActivity.class), 0);
        } else {
            app_data item = (app_data) list.getItem(position);
            String pkg_name = item.pkg_name;
            new AsyncTask<String, Void, Object>() {
                protected Object doInBackground(String... param) {
                    Context ctx = getApplicationContext();
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                    HashSet data = new HashSet(pref.getStringSet(CONF_DATA, null));
                    data.remove(param[0]);
                    pref.edit().putStringSet(CONF_DATA, data).apply();
                    return null;
                }

                protected void onPostExecute(Object obj) {
                    ((ListAdapter) getListAdapter()).refresh();
                }
            }.execute(pkg_name);
        }
    }

    @Override
    protected void onDestroy() {
        setListAdapter(null);

        super.onDestroy();
    }
}
