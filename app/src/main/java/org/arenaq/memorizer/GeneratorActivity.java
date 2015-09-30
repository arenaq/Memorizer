package org.arenaq.memorizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class GeneratorActivity extends ActionBarActivity {

    ImageView imageView;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        db = this.openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        db.execSQL("drop table if exists table1");
        db.execSQL("create table if not exists table1 (id integer primary key autoincrement, input blob not null, output text)");
    }

    public void saveImage(View view) {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/memorizer");
        File[] files = directory.listFiles();

        //int i = 1;
        try {
            for(File f : files) {
                FileInputStream fis = new FileInputStream(f);
                byte[] image = new byte[fis.available()];
                fis.read(image);

                ContentValues values = new ContentValues();
                //values.put("id", String.valueOf(i));
                values.put("input", image);
                values.put("output", "0451");
                db.insert("table1", null, values);

                fis.close();
                //i++;
            }
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(this, "Saving completed", Toast.LENGTH_SHORT).show();
    }

    public void loadRandomImage(View view) {
        Cursor c = db.rawQuery("select * from table1 order by random() limit 1", null);
        if (c.moveToNext()) {
            byte[] image = c.getBlob(1);
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(bmp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
