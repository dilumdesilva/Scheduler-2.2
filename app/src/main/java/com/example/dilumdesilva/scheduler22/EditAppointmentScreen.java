package com.example.dilumdesilva.scheduler22;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dilumdesilva.scheduler22.DB.SQLiteHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class EditAppointmentScreen extends AppCompatActivity {

    SQLiteHandler sqLiteHandler;

    String date, appointmentNumber, changeType, popupDate;

    Button confirmBtn;
    EditText appointmentNumberET;

    TextView heading, helperText;

    //list view stuff
    ArrayAdapter adapter;
    ListView listView;

    //lists to store the resulting appointments
    List<Appointment> listArr;
    ArrayList<String> arrayList;

    //Update popup
    PopupWindow popupWindow;
    Button updateBtn;
    EditText titleET, timeET, detailsET;

    //Move Popup
    Button moveBtn;
    CalendarView calendarView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment_screen);

        //get the selected date
        Intent intent = getIntent();
        date = intent.getStringExtra("Date");

        //catching the change type i.e Delete, Edit or Move
        changeType = intent.getStringExtra("Change Type");

        //initialising the textViews
        heading = (TextView) findViewById(R.id.headingTextView);
        helperText = (TextView) findViewById(R.id.helperTextView);

        //initialising the button and edit text
        confirmBtn = (Button) findViewById(R.id.confirmButton);
        appointmentNumberET = (EditText) findViewById(R.id.appointmentNumberEditText);

        if(changeType.equals("Delete")){

            heading.setText("DELETE APPOINTMENT");
            helperText.setText("Please enter the number of the appointment you wish to delete and press the DELETE button.");
            confirmBtn.setText("DELETE");


        }else if(changeType.equals("Edit")){

            heading.setText("VIEW/EDIT APPOINTMENT");
            helperText.setText("Please enter the number of the appointment you wish to edit and press the EDIT button.");
            confirmBtn.setText("EDIT");

        }else if(changeType.equals("Move")){

            heading.setText("MOVE APPOINTMENT");
            helperText.setText("Please enter the number of the appointment you wish to move and press the MOVE button.");
            confirmBtn.setText("MOVE");

        }else {
            heading.setText("CHANGE APPOINTMENT");
            helperText.setText("Something's Wrong!");
            Toast.makeText(getBaseContext() ,"Oops! Something went wrong!" , Toast.LENGTH_SHORT ).show();
            finish();
        }

        //creates an instance of the MyDBHandler
        sqLiteHandler = new SQLiteHandler(this, null, null, 1);

        listArr = sqLiteHandler.displayAppointments(date);
        arrayList = new ArrayList<>();

        for(int j=0 ; j<listArr.size() ; j++){

            arrayList.add(j+1 + ". " + listArr.get(j).getTime() + " " + listArr.get(j).getTitle());
            //Toast.makeText(getBaseContext() ,arrayList.get(j) , Toast.LENGTH_SHORT ).show();

        }

        adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, arrayList);

        listView = (ListView) findViewById(R.id.appointmentList);
        listView.setAdapter(adapter);



        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hides the virtual keyboard when the buttons are clicked
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                appointmentNumber = appointmentNumberET.getText().toString();
                if(appointmentNumber.equals(null) || appointmentNumber.equals("")){
                    confirmBtn.setError("Please select a valid appointment number");
                    appointmentNumberET.setText("");
                    return;
                }else{
                    try{

                        //if the change type is delete
                        if(changeType.equals("Delete")) {

                            errorDialog("Would you like to delete event : “ " +
                                    listArr.get(Integer.parseInt(appointmentNumber) - 1).getTitle() + " ”?");

                        } else if (changeType.equals("Edit")){

                            updateAppointmentPopup(v);

                        } else if (changeType.equals("Move")){

                            moveAppointmentPopup(v);
                        }
                        appointmentNumberET.setText("");
                    }catch (IndexOutOfBoundsException e){
                        appointmentNumberET.setText("");
                        Toast.makeText(getBaseContext(), "There's no appointment numbered " + appointmentNumber +
                                ". Please try again with a valid number." , Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        appointmentNumberET.setText("");
                        Toast.makeText(getBaseContext(), "Invalid input. Please try again with a valid number." , Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    /**
     * This function creates a  error dialog box
     * @param error String parameter which is passed
     */
    public void errorDialog(String error)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.DilumCustomDialogTheme);
        builder.setMessage(error);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getBaseContext(), "Deleted the " +
                                listArr.get(Integer.parseInt(appointmentNumber) - 1).getTitle() +
                                " appointment.", Toast.LENGTH_SHORT).show();
                        sqLiteHandler.deleteAppointments(date , listArr.get(Integer.parseInt(appointmentNumber)-1).getTitle());
                        //adapter.notifyDataSetChanged(); //refreshes the list, NOT WORKING
                        dialog.dismiss();

                        //bad way to refresh
                        finish();
                        startActivity(getIntent());
                    }
                });
        builder.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * This junction creates a popup window with three textboxes and a button to update an
     * appointment
     *
     * @param v The current view instance is passed
     */
    private void updateAppointmentPopup (View v) {

        try {
            //get an instance of layoutinflater
            LayoutInflater inflater = (LayoutInflater) EditAppointmentScreen.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //initiate the view
            final View layout = inflater.inflate(R.layout.update_popup,
                    (ViewGroup) findViewById(R.id.updatePopup_screen));

            //initialize a size for the popup
            popupWindow = new PopupWindow(layout, 1020, 1650 ,  true);
            // display the popup in the center
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            //initialising the update popup button and edit texts
            titleET = (EditText) layout.findViewById(R.id.updateTitleEditText);
            timeET = (EditText) layout.findViewById(R.id.updateTimeEditText);
            detailsET = (EditText) layout.findViewById(R.id.updateDetailsEditText);

            //Updates the selected appointment
            updateBtn = (Button) layout.findViewById(R.id.updateButton);
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        int success = sqLiteHandler.updateAppointment(listArr.get(Integer.parseInt(appointmentNumber) - 1),
                                timeET.getText().toString(), titleET.getText().toString(), detailsET.getText().toString());

                        if (success == 1) {

                            Toast.makeText(getBaseContext(), "Successfully updated the appointment", Toast.LENGTH_LONG).show();

                        } else if (success == -1) {

                            Toast.makeText(getBaseContext(), "There's no appointment numbered " + appointmentNumber +
                                    ". Please try again with a valid number.", Toast.LENGTH_SHORT).show();

                        }

                        //refreshes the page
                        finish();
                        startActivity(getIntent());

                    }catch (IndexOutOfBoundsException e){

                        Toast.makeText(getBaseContext(), "Couldn't find the specified appointment in the database." , Toast.LENGTH_SHORT).show();

                    }catch (Exception e){

                        Toast.makeText(getBaseContext(), "Invalid input. Please try again with a valid number." , Toast.LENGTH_SHORT).show();
                    }
                    timeET.setText(""); titleET.setText(""); detailsET.setText("");
                    popupWindow.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This junction creates a popup window with a calender view and a button
     *
     * @param v The current view instance is passed
     */
    private void moveAppointmentPopup (View v) {

        try {
            //get an instance of layoutinflater
            LayoutInflater inflater = (LayoutInflater) EditAppointmentScreen.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //initiate the view
            final View layout = inflater.inflate(R.layout.move_popup,
                    (ViewGroup) findViewById(R.id.movePopup_screen));

            //initialize a size for the popup
            popupWindow = new PopupWindow(layout, 1020, 1800 ,  true);
            // display the popup in the center
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            calendarView = (CalendarView) layout.findViewById(R.id.calendarViewPopup);
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String dateSelected = simpleDateFormat.format(new GregorianCalendar(year, month, dayOfMonth).getTime());
                    popupDate = dateSelected;
                    //Toast.makeText(getBaseContext(),popupDate,Toast.LENGTH_SHORT).show();
                }
            });

            //Updates the selected appointment
            moveBtn = (Button) layout.findViewById(R.id.moveButton);
            moveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        sqLiteHandler.moveAppointment(listArr.get(Integer.parseInt(appointmentNumber) - 1) , popupDate);

                        //refreshes the page
                        finish();
                        startActivity(getIntent());

                    }catch (IndexOutOfBoundsException e){

                        Toast.makeText(getBaseContext(), "Couldn't find the specified appointment in the database." , Toast.LENGTH_SHORT).show();

                    }catch (Exception e){

                        Toast.makeText(getBaseContext(), "Invalid input. Please try again with a valid number." , Toast.LENGTH_SHORT).show();
                    }
                    popupWindow.dismiss();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
