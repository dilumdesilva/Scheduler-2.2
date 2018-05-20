package com.example.dilumdesilva.scheduler22;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dilumdesilva.scheduler22.DB.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

public class SearchAppoinmentScreen extends AppCompatActivity implements View.OnClickListener {

    private Button searchBtn;
    private EditText searchET;

    SQLiteHandler myDBHandler;

    //list view stuff
    AppointmentAdaptor appointmentAdaptor;
    ListView listView;

    //lists to store all the resulting appointments
    List<Appointment> listArr;
    //list to store matching appointments
    List<Appointment> listMatches;

    //variable to store the value input from the textbox
    String searchKeywords;

    //search popup stuff
    private PopupWindow popupWindow;
    TextView titleTV,timeTV,detailsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appoinment_screen);


        //initialising the button and edit text
        searchBtn = (Button) findViewById(R.id.confirmButton);
        searchBtn.setOnClickListener(this);
        searchET = (EditText) findViewById(R.id.searchEditText);

        //creates an instance of the MyDBHandler
        myDBHandler = new SQLiteHandler(this, null, null, 1);

        //call the displayappointment method and store all the appointments in a list
        listArr = myDBHandler.displayAppointments();

        //initialize the list view
        listView = (ListView) findViewById(R.id.searchList);
        //adding a list item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                displayClickedSearch(appointmentAdaptor.getItem(position) , view);

            }
        });
    }

    @Override
    public void onClick(View v) {
        //Hides the virtual keyboard when the buttons are clicked
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        switch (v.getId()){
            case R.id.confirmButton : {

                try {
                    if (searchET.getText().toString().equals("") || searchET.getText().toString().equals(null)) {

                        searchET.setError("Please input a Keyword");

                    } else {
                        //initialize a new list of apppointments
                        listMatches = new ArrayList<>();

                        //assign the edit text value to the searchKeywords variable
                        searchKeywords = searchET.getText().toString();


                        //see if the arraylist objectcts contain any of the keywords
                        for (Appointment appointment : listArr) {

                            if (appointment.getTitle().contains(searchKeywords)) {

                                listMatches.add(appointment);
                            }

                        }


                        appointmentAdaptor = new AppointmentAdaptor(getBaseContext(), -1, listMatches);
                        listView.setAdapter(appointmentAdaptor);

                        if(listMatches.size() == 0){

                            Toast.makeText(getBaseContext(),"Couldn't find any matches", Toast.LENGTH_SHORT).show();

                        }
                    }
                }catch (Exception e){

                    Toast.makeText(getBaseContext(),"Couldn't find any matches", Toast.LENGTH_SHORT).show();

                }
                searchET.setText("");
                break;
            }
        }
    }

    public void displayClickedSearch(Appointment appointment , View v){

        Toast.makeText(getBaseContext(),appointment.getTitle(), Toast.LENGTH_SHORT).show();


        //get an instance of layoutinflater
        LayoutInflater inflater = (LayoutInflater) SearchAppoinmentScreen.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //initiate the view
        final View layout = inflater.inflate(R.layout.search_popup,
                (ViewGroup) findViewById(R.id.searchPoppup_screen));

        //initialising the textviews in search popup
        titleTV = (TextView) layout.findViewById(R.id.searchedTitle) ;
        timeTV = (TextView) layout.findViewById(R.id.searchedTime) ;
        detailsTV = (TextView) layout.findViewById(R.id.searchedDetails) ;

        //initialize a size for the popup
        popupWindow = new PopupWindow(layout, 1000, 900 ,  true);
        // display the popup in the center
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        //setting the textviews
        titleTV.setText(appointment.getTitle());
        timeTV.setText(appointment.getTime());
        detailsTV.setText(appointment.getDetails());



    }
}
