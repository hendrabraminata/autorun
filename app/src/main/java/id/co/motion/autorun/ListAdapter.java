package id.co.motion.autorun;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class ListAdapter extends BaseAdapter {
    private Context ctx;
    private List data;

    ListAdapter(Context ctx) {
        this.ctx = ctx;
        this.data = null;
        load_data();
    }

    private void load_data() {
        new AsyncTask<Context, Void, ArrayList>() {
            protected ArrayList doInBackground(Context... param) {
                ArrayList list = null;

                Context ctx = param[0];
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                HashSet cur_data = (HashSet) pref.getStringSet(MainActivity.CONF_DATA, null);
                if( cur_data != null ) {
                    HashSet err_data = null;
                    list = new ArrayList();
                    PackageManager pm = ctx.getPackageManager();

                    Iterator itr = cur_data.iterator();
                    while( itr.hasNext() ) {
                        String pkg_name = (String) itr.next();
                        try {
                            ApplicationInfo app = pm.getApplicationInfo(pkg_name, 0);
                            String name = pm.getApplicationLabel(app).toString();
                            Drawable icon = pm.getApplicationIcon(app);
                            Intent launch = pm.getLaunchIntentForPackage(pkg_name);

                            app_data item = new app_data();
                            item.pkg_name = pkg_name;
                            item.name = name;
                            item.icon = icon;
                            item.launch = launch;

                            list.add(item);
                        } catch( NameNotFoundException exc ) {
                            if( err_data == null ) err_data = new HashSet();
                            err_data.add(pkg_name);
                        }
                    }

                    if( err_data != null ) {
                        HashSet upd_data = new HashSet(cur_data);
                        upd_data.removeAll(err_data);
                        pref.edit().putStringSet(MainActivity.CONF_DATA, upd_data).apply();
                    }
                }

                return list;
            }

            protected void onPostExecute(ArrayList arl) {
                data = arl;
                notifyDataSetChanged();
            }
        }.execute(ctx);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if( convertView == null )
            convertView = View.inflate(ctx, R.layout.app_list_item, null);

        if( isFooter(position) ) {
            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(R.string.add);
            name.setTextColor(ctx.getResources().getColor(android.R.color.holo_blue_dark));

            ((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(null);
        } else {
            app_data ad = ((app_data) data.get(position));

            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(ad.name);
            name.setTextColor(ctx.getResources().getColor(android.R.color.black));

            ((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(ad.icon);
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) { return -1; }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public int getCount() { return (data != null) ? (data.size()+1) : 1; }

    boolean isFooter(int position) { return ( position != (getCount()-1) ) ? false : true; }

    class app_data {
        Drawable icon;
        Intent launch;
        String name;
        String pkg_name;
    }

    void refresh() { load_data(); }
}
