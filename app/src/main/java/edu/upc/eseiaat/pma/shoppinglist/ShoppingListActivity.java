package edu.upc.eseiaat.pma.shoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {
    private static final String FILENAME = "shopping_list.txt";

    private ArrayList<ShoppingItem> itemList;
    private ShoppingListAdapter adapter;

    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    private void writeItemList()
    {
        /*
        Patatas,; false
        Papel; true
        1; true
        2; false
        3;false

        */
        try {
            FileOutputStream fos=openFileOutput(FILENAME, Context.MODE_PRIVATE);
            for (int i=0; 1<itemList.size(); i++){
                ShoppingItem it = itemList.get(i);

                String line = String.format("%s; %b\n",it.getText(); it.isChecked());
                fos.write(line.getBytes());

            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("pauek", "writeItemList: FileNotFoundException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.e("pauek", "writeItemList: IO exception");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        writeItemList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        list= (ListView) findViewById(R.id.list);
        btn_add=(Button) findViewById(R.id.btn_add);
        edit_item=(EditText) findViewById(R.id.editItem);

        itemList= new ArrayList<>();
        itemList.add(new ShoppingItem("Patatas", true));
        itemList.add(new ShoppingItem("Papel de water", true));
        itemList.add(new ShoppingItem("Zanahorias"));
        itemList.add(new ShoppingItem("Copas danone"));

        adapter=new ShoppingListAdapter(
                this,
                R.layout.shopping_item,
                itemList
        );



        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });


        edit_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
               addItem();
               return true;
            }
        });

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                itemList.get(position).toggleChecked();
                adapter.notifyDataSetChanged();

            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                maybeRemoveItem(position);
                return true;

            }
        });
    }

    private void maybeRemoveItem( final int position) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String fmt =getResources().getString(R.string.confirm_message);
        builder.setMessage(String.format(fmt,itemList.get(position).getText()));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void addItem() {
        String item_text = edit_item.getText().toString();
        if (!item_text.isEmpty())
        {
            itemList.add(new ShoppingItem(item_text));
            adapter.notifyDataSetChanged();
            edit_item.setText("");
        }
        list.smoothScrollToPosition(itemList.size()-1);
    }
}
