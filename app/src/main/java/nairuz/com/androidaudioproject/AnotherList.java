package nairuz.com.androidaudioproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


public class AnotherList extends Activity {

    private ListView listView2;
    private String [] items;
    int position ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_list);


        listView2 = (ListView) findViewById(R.id.listView2);
        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());
        items = new String [mySongs.size()];

        for (int i = 0 ; i < mySongs.size(); i++){
            items[i] = mySongs.get(i).getName().toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.song_item,R.id.textView,items);
        listView2.setAdapter(adapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), Player.class).putExtra("pos",position).putExtra("songlist",mySongs));
            }
        });

    }

    public ArrayList<File > findSongs (File root){

        ArrayList<File> all = new ArrayList<File>();

        File [] files = root.listFiles();

        for (File singleFile: files){

            if (singleFile.isDirectory() && !singleFile.isHidden()){
                all.addAll(findSongs(singleFile));
            }
            else
            if (singleFile.getName().endsWith(".mp3")){
                all.add(singleFile);
            }
        }
        return all;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_another_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
