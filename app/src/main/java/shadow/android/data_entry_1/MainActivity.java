package shadow.android.data_entry_1;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shadow.android.data_entry_1.about.AboutFragment;
import shadow.android.data_entry_1.client.ClientFragment;
import shadow.android.data_entry_1.db.Client;
import shadow.android.data_entry_1.db.DBController;
import shadow.android.data_entry_1.ui.ClientIconAdapter;
import shadow.android.data_entry_1.ui.DisplayHelper;
import shadow.android.data_entry_1.ui.MediaHelper;
import shadow.android.data_entry_1.ui.Offset;

import static shadow.android.data_entry_1.ui.DisplayHelper.dimPopupParent;

public class MainActivity extends AppCompatActivity implements ClientFragment.TaskIsDoneInterface {
    private static final int SD_REQUEST = 1;
    private TextView tv_toolbar;
    private ImageButton btn_options;
    private ImageButton btn_delete;
    private ImageButton btn_thump;
    private Spinner spinner_select;

    private ArrayAdapter<String> spinnerAdapter;
    private GridView gv1;
    private ClientIconAdapter adapter;
    private List<Integer> selectedNoteIcons = new ArrayList<>();
    private PopupWindow popup_add;
    private ViewSwitcher vs_toolbar;
    private boolean inSelectionMode = false;
    private Bitmap selectedBitmap;


    // gv1 OnItemClickListener
    final AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (inSelectionMode) {
                CheckBox checkBox = view.findViewById(R.id.cbox_select);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    Log.i("deselect", "" + position);
                    selectedNoteIcons.remove(position);
                } else {
                    checkBox.setChecked(true);
                    Log.i("select", "" + position);
                    selectedNoteIcons.add(position);
                }
                //modify the options of the spinner
                updateSpinner();
                return;
            }
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = ClientFragment.newInstance(adapter.getItem(position));
            fm.beginTransaction().replace(R.id.container, fragment, "client")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("client").commit();

        }

    };
    //btn_delete OnClickListener
    View.OnClickListener deleteIconsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int size = selectedNoteIcons.size();
            for (int i = 0; i < size; i++) {
                Client client = adapter.getItem(selectedNoteIcons.get(i));
                DBController.removeClient(getApplicationContext(), client.getId());//step 1 remove from the database
                adapter.getClients().remove(client);//step 2 remove from the model
                adapter.notifyDataSetChanged();//step 3 remove from the view
                clearSelection();
            }
        }
    };
    //gv1 OnItemLongClickListener
    final AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            inSelectionMode = true;
            selectedNoteIcons.add(position);//because the long clicked item is checked
            ((CheckBox) (gv1.getChildAt(position).findViewById(R.id.cbox_select))).setChecked(true);
            CheckBox checkBox;
            //show the 2nd toolbar first
            if (vs_toolbar.getCurrentView().getId() == R.id.toolbar_1)
                vs_toolbar.showNext();
            //initialize and display the spinner

            updateSpinner();
            for (int x = 0; x < gv1.getChildCount(); x++) {
                checkBox = gv1.getChildAt(x).findViewById(R.id.cbox_select);
                checkBox.setVisibility(View.VISIBLE);
            }
            return true;//to indicate you don't want further processing as onClick
        }
    };
    // btn_options OnClickListener
    View.OnClickListener btnOptionsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu popup_options = new PopupMenu(MainActivity.this, btn_options);
            popup_options.inflate(R.menu.options_main);
            popup_options.setOnMenuItemClickListener(onMenuItemClickListener);
            popup_options.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBController.getDays(MainActivity.this, 1, 1);
        spinner_select = findViewById(R.id.spinner_select);
        gv1 = findViewById(R.id.gv1);
        vs_toolbar = findViewById(R.id.vs_toolbar);
        btn_delete = vs_toolbar.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(deleteIconsClick);
        tv_toolbar = findViewById(R.id.tv_toolbar);
        tv_toolbar.setText(R.string.home);
        btn_options = findViewById(R.id.btn_options);
        btn_options.setOnClickListener(btnOptionsListener);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.selection)));

        spinnerAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                list);
        spinner_select.setAdapter(spinnerAdapter);

        spinner_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1) {
                    selectedNoteIcons.clear();
                    for (int x = 0; x < gv1.getChildCount(); x++) {
                        ((CheckBox) gv1.getChildAt(x).findViewById(R.id.cbox_select)).setChecked(true);
                        selectedNoteIcons.add(x);
                    }
                    updateSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gv1.setOnItemLongClickListener(longClickListener);
        gv1.setOnItemClickListener(clickListener);

        adapter = new ClientIconAdapter(this);
        gv1.setAdapter(adapter);
    }

    //to control UI onclick actions within the main activity
    public void control(final View view) {
        switch (view.getId()) {
            default:
            case R.id.btn_delete:
                clearSelection();
                break;
            case R.id.btn_thump:
                if (checkPermissionAccessStorage()) {
                    //if u try to add another client, this intent wont fire again as the value of permission is reset again
                    Log.i(MainActivity.class.getSimpleName(), "permission Access Storage granted");
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SD_REQUEST);
                }
                break;
            case R.id.btn_submit_user:
                EditText et_name = popup_add.getContentView().findViewById(R.id.et_name);
                if (et_name.getText().toString().equals("") || et_name.getText() == null) {
                    Toast.makeText(MainActivity.this,R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean ok = DBController.addClient(getApplicationContext(),
                        String.valueOf(et_name.getText()), MediaHelper.bitmapToByteArray(selectedBitmap)
                );
                dimPopupParent(popup_add.getContentView().getRootView(),false);
                if (ok) {
                    adapter.refreshNoteIcons();
                    adapter.notifyDataSetChanged();
                    popup_add.dismiss();
                    selectedBitmap = null;
                    Toast.makeText(MainActivity.this, "saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getOrder()) {
                case 0:
                    popup_add = new PopupWindow(MainActivity.this);
                    popup_add.setContentView(LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_add, null));
                    popup_add.setFocusable(true);
                    popup_add.setBackgroundDrawable(null);
                    //to center the popup window on the screen
                    Offset offset = DisplayHelper.centerOnParent(MainActivity.this, popup_add.getContentView());
                    popup_add.showAtLocation(btn_options.getRootView(), Gravity.NO_GRAVITY, offset.getX(), offset.getY());
                    dimPopupParent(popup_add.getContentView().getRootView(),true);
                    break;
                case 1:
                    FragmentManager fm = getSupportFragmentManager();
                    AboutFragment fragment = AboutFragment.newInstance();
                    fm.beginTransaction().replace(R.id.container, fragment, "about")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack("about").commit();

                    break;
            }
            return true;
        }
    };



    public void clearSelection() {
        CheckBox checkBox;
        for (int x = 0; x < gv1.getCount(); x++) {
            checkBox = gv1.getChildAt(x).findViewById(R.id.cbox_select);
            checkBox.setChecked(false);
            checkBox.setVisibility(View.INVISIBLE);
        }
        selectedNoteIcons.clear();
        updateSpinner();
        if (vs_toolbar.getCurrentView().getId() == R.id.toolbar_2)
            vs_toolbar.showNext();
        gv1.requestFocus();
        inSelectionMode = false;
    }

    public void updateSpinner() {
        spinnerAdapter.remove(spinnerAdapter.getItem(0));
        spinnerAdapter.insert(selectedNoteIcons.size() + " selected", 0);
        spinnerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (selectedNoteIcons.size() != 0||inSelectionMode) clearSelection();
        else super.onBackPressed();
        //getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {});
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SD_REQUEST) {
            Log.i("result arrived in ", "ProfilePicFragment");
            if (data == null)
                return;
            Uri selectedImage = data.getData();//ex.  content://media/external/images/media/397
            String[] projection = {MediaStore.Images.Media.DATA};//  _data     //length is 1
            Cursor cursor;
            if (selectedImage != null) {
                cursor = getContentResolver().query(selectedImage,//Uri->From table
                        projection,//projection--> columns to be returned by the query
                        null,//selection
                        null,//selection args
                        null//sortOrder
                );
            } else return;
            assert cursor != null;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);// we only specified one column in the projection that's the _data column that stores the filepath
            String filePath = cursor.getString(columnIndex);//ex. /storage/sdcard1/DCIM/Camera/IMG_20170706_184224_1.jpg
            cursor.close();
            selectedBitmap = MediaHelper.decodeSampledBitmapFromPath(filePath, 400, 400);
            btn_thump = popup_add.getContentView().findViewById(R.id.btn_thump);
            btn_thump.setImageBitmap(selectedBitmap);
        }
    }

    boolean checkPermissionAccessStorage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return false;
        } else {//when the permission is already granted
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SD_REQUEST);

                } else {
                    Toast.makeText(MainActivity.this, "can't access storage", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void done() {
        //called from any added fragment to restore MainActivity state
        tv_toolbar.setText(R.string.home);
        btn_options.setVisibility(View.VISIBLE);
        btn_options.setOnClickListener(btnOptionsListener);
    }

    @Override
    public void recreateFragment() {
        FragmentManager fm = getSupportFragmentManager();
        ClientFragment oldFragment = (ClientFragment) fm.findFragmentByTag(ClientFragment.TAG);
        Client client = null;
        if (oldFragment != null&&oldFragment.getArguments()!=null) {
            client = (Client) oldFragment.getArguments().getSerializable("client");
        }
        ClientFragment newFragment = ClientFragment.newInstance(client);
        fm.beginTransaction().remove(oldFragment).commit();
        fm.beginTransaction().replace(R.id.container, newFragment, ClientFragment.TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(ClientFragment.TAG).commit();
    }
}
