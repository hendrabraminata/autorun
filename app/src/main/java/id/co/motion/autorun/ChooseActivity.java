package id.co.motion.autorun;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ChooseActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setListAdapter(new ChooseAdapter(getApplicationContext()));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        setListAdapter(null);
        super.onDestroy();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfo item = (ResolveInfo) ((ChooseAdapter) getListAdapter()).getItem(position);
        Intent itn = new Intent();
        itn.putExtra(MainActivity.CONF_PKG, item.activityInfo.packageName);
        setResult(RESULT_FIRST_USER, itn);
        finish();
    }
}