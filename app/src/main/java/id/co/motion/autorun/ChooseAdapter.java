package id.co.motion.autorun;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ChooseAdapter extends BaseAdapter {
    private Context ctx;
    private ResolveInfo[] data;
    private data_comparator data_compare;

    class data_comparator implements Comparator<ResolveInfo> {
        private PackageManager pm;

        data_comparator(PackageManager pm) {
            this.pm = pm;
        }

        @Override
        public int compare(ResolveInfo ri1, ResolveInfo ri2) {
            return ri1.loadLabel(pm).toString().compareTo(ri2.loadLabel(pm).toString());
        }
    }

    @Override
    public int getCount() {
        return (data != null) ? data.length : 0;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(ctx, R.layout.app_list_item, null);

        ResolveInfo ri = data[position];
        PackageManager pm = ctx.getPackageManager();
        ((TextView) convertView.findViewById(R.id.name)).setText(ri.loadLabel(pm));
        ((ImageView) convertView.findViewById(R.id.icon)).setImageDrawable(ri.loadIcon(pm));

        return convertView;
    }

    private void load_data() {
        PackageManager pm = ctx.getPackageManager();
        new AsyncTask<PackageManager, Void, ResolveInfo[]>() {
            protected ResolveInfo[] doInBackground(PackageManager... pm) {
                Intent app = new Intent("android.intent.action.MAIN");
                app.addCategory("android.intent.category.LAUNCHER");
                List list = pm[0].queryIntentActivities(app, 0);

                ResolveInfo[] ri = (ResolveInfo[]) list.toArray(new ResolveInfo[list.size()]);
                Arrays.sort(ri, data_compare);

                return ri;
            }

            protected void onPostExecute(ResolveInfo[] ri) {
                data = ri;
                notifyDataSetChanged();
            }
        }.execute(pm);
    }

    ChooseAdapter(Context ctx) {
        this.data = null;
        this.ctx = ctx;
        this.data_compare = new data_comparator(ctx.getPackageManager());
        load_data();
    }
}
