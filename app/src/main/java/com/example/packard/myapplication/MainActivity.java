package com.example.packard.myapplication;



        import java.util.ArrayList;
        import java.util.List;

        //import com.example.packard.myapplication.database.oganizerDbAdapter;
       // import com.example.packard.myapplication.model.oganizerzadanie;
        import android.app.Activity;
        import android.content.Context;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.LinearLayout;
        import android.widget.ListView;

        import javax.crypto.NullCipher;

public class MainActivity extends Activity {
    private Button btnAddNew;
    private Button btnClearCompleted;
    private Button btnSave;
    private Button btnCancel;
    private EditText etNewzadanie;
    private ListView lvoganizers;
    private LinearLayout llControlButtons;
    private LinearLayout llNewzadanieButtons;

    private oganizerDbAdapter oganizerDbAdapter;
    private Cursor oganizerCursor;
    private List<oganizerzadanie> zadanies;
    private oganizerzadaniesAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUiElements();
        initListView();
        initButtonsOnClickListeners();
    }

    private void initUiElements() {
        btnAddNew = (Button) findViewById(R.id.btnAddNew);
        btnClearCompleted = (Button) findViewById(R.id.btnClearCompleted);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        etNewzadanie = (EditText) findViewById(R.id.etNewzadanie);
        lvoganizers = (ListView) findViewById(R.id.lvoganizers);
        llControlButtons = (LinearLayout) findViewById(R.id.llControlButtons);
        llNewzadanieButtons = (LinearLayout) findViewById(R.id.llNewzadanieButtons);
    }

    private void initListView() {
        fillListViewData();
        initListViewOnItemClick();
    }

    private void fillListViewData() {
        oganizerDbAdapter = new oganizerDbAdapter(getApplicationContext());
        oganizerDbAdapter.open();
        getAllzadanies();
        listAdapter = new oganizerzadaniesAdapter(this, zadanies);
        lvoganizers.setAdapter(listAdapter);
    }

    private void getAllzadanies() {
        zadanies = new ArrayList<oganizerzadanie>();
        oganizerCursor = getAllEntriesFromDb();
        updatezadanieList();
    }

    private Cursor getAllEntriesFromDb() {
        oganizerCursor = oganizerDbAdapter.getAlloganizers();
        if(oganizerCursor != null) {
            startManagingCursor(oganizerCursor);
            oganizerCursor.moveToFirst();
        }
        return oganizerCursor;
    }

    private void updatezadanieList() {
        if(oganizerCursor != null && oganizerCursor.moveToFirst()) {
            do {
                long id = oganizerCursor.getLong(oganizerDbAdapter.ID_COLUMN);
                String description = oganizerCursor.getString(oganizerDbAdapter.DESCRIPTION_COLUMN);
                boolean completed = oganizerCursor.getInt(oganizerDbAdapter.COMPLETED_COLUMN) > 0 ? true : false;
                zadanies.add(new oganizerzadanie(id, description, completed));
            } while(oganizerCursor.moveToNext());
        }
    }

    @Override
    protected void onDestroy() {
        if(oganizerDbAdapter != null)

            oganizerDbAdapter.close();
        super.onDestroy();
    }

    private void initListViewOnItemClick() {
        lvoganizers.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {
                oganizerzadanie zadanie = zadanies.get(position);
                if(zadanie.isCompleted()){
                    oganizerDbAdapter.updateoganizer(zadanie.getId(), zadanie.getDescription(), false);
                } else {
                    oganizerDbAdapter.updateoganizer(zadanie.getId(), zadanie.getDescription(), true);
                }
                updateListViewData();
            }
        });
    }

    private void updateListViewData() {
        oganizerCursor.requery();
        zadanies.clear();
        updatezadanieList();
        listAdapter.notifyDataSetChanged();
    }

    private void initButtonsOnClickListeners() {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnAddNew:
                        addNewzadanie();
                        break;
                    case R.id.btnSave:
                        saveNewzadanie();
                        break;
                    case R.id.btnCancel:
                        cancelNewzadanie();
                        break;
                    case R.id.btnClearCompleted:
                        clearCompletedzadanies();
                        break;
                    default:
                        break;
                }
            }
        };
        btnAddNew.setOnClickListener(onClickListener);
        btnClearCompleted.setOnClickListener(onClickListener);
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void showOnlyNewzadaniePanel() {
        setVisibilityOf(llControlButtons, false);
        setVisibilityOf(llNewzadanieButtons, true);
        setVisibilityOf(etNewzadanie, true);
    }

    private void showOnlyControlPanel() {
        setVisibilityOf(llControlButtons, true);
        setVisibilityOf(llNewzadanieButtons, false);
        setVisibilityOf(etNewzadanie, false);
    }

    private void setVisibilityOf(View v, boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        v.setVisibility(visibility);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etNewzadanie.getWindowToken(), 0);
    }

    private void addNewzadanie(){
        showOnlyNewzadaniePanel();
    }

    private void saveNewzadanie(){
        String zadanieDescription = etNewzadanie.getText().toString();
        if(zadanieDescription.equals("")){
            etNewzadanie.setError("Your zadanie description couldn't be empty string.");
        } else {
            oganizerDbAdapter.insertoganizer(zadanieDescription);
            etNewzadanie.setText("");
            hideKeyboard();
            showOnlyControlPanel();
        }
        updateListViewData();
    }

    private void cancelNewzadanie() {
        etNewzadanie.setText("");
        hideKeyboard();
        showOnlyControlPanel();
    }

    private void clearCompletedzadanies(){
        if(oganizerCursor != null && oganizerCursor.moveToFirst()) {
            do {
                if(oganizerCursor.getInt(oganizerDbAdapter.COMPLETED_COLUMN) == 1) {
                    long id = oganizerCursor.getLong(oganizerDbAdapter.ID_COLUMN);
                    oganizerDbAdapter.deleteoganizer(id);
                }
            } while (oganizerCursor.moveToNext());
        }
        updateListViewData();
    }
}