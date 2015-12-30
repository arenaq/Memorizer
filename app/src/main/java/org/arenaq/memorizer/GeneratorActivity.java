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

    public void updateDatabase() {
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_update:
                updateDatabase();
                break;
            case R.id.action_delete:
                db.execSQL("drop table if exists table1");
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
