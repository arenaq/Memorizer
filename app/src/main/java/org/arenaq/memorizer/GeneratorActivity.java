package org.arenaq.memorizer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
    Cursor cursor_ordered;
    Cursor cursor_random;
    String output, comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO change dialog to something editable (picker, custom layout,...) - or figure out how to get user's input
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneratorActivity.this);
                builder.setTitle(R.string.dialog_answer_title)
                        // TODO comment following one line
                        .setMessage(output)
                        .setPositiveButton(R.string.dialog_answer_positive, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO check whether the answer is correct
                            }
                        })
                        .setNegativeButton(R.string.dialog_answer_negative, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(GeneratorActivity.this);
                                builder2.setTitle(R.string.dialog_answer_negative_title)
                                        .setMessage(output);
                                builder2.create().show();
                            }
                        });
                builder.create().show();
            }
        });
        db = this.openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists table1 (id integer primary key autoincrement, input blob not null, output text not null, comments text)");
        cursor_ordered = db.rawQuery("select * from table1", null);
        cursor_random = db.rawQuery("select * from table1 order by random()", null);
    }

    public void updateDatabase() {
        File directory = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/memorizer");
        File[] files = directory.listFiles();

        try {
            for(File f : files) {
                FileInputStream fis = new FileInputStream(f);
                byte[] image = new byte[fis.available()];
                fis.read(image);

                ContentValues values = new ContentValues();
                values.put("input", image);

                String name = f.getName();
                String id_rest[] = name.split("_");
                int id = Integer.valueOf(id_rest[0]);
                values.put("id", id);

                int pos = id_rest[1].lastIndexOf(".");
                if (pos > 0) {
                    id_rest[1] = id_rest[1].substring(0, pos);
                }

                pos = id_rest[1].lastIndexOf("-");
                if (pos > 0) {
                    values.put("comments", id_rest[1].substring(0, pos));
                    values.put("output", id_rest[1].substring(pos+1));
                } else {
                    values.put("output", id_rest[1]);
                }

                db.insert("table1", null, values);

                fis.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        cursor_ordered = db.rawQuery("select * from table1", null);
        Toast.makeText(this, "Saving completed", Toast.LENGTH_SHORT).show();
    }

    public void loadRandomImage(View view) {
        if (cursor_random.moveToNext()) {
            byte[] image = cursor_random.getBlob(1);
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(bmp);
            output = cursor_random.getString(2);
            comments = cursor_random.getString(3);
            if (comments != null) comments = comments.replace("-", "\n");
        }
    }

    public void loadNextImage(View view) {
        if (cursor_ordered.moveToNext()) {
            byte[] image = cursor_ordered.getBlob(1);
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(bmp);
            output = cursor_ordered.getString(2);
            comments = cursor_ordered.getString(3);
            if (comments != null) comments = comments.replace("-", "\n");
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
