package com.example.dilumdesilva.scheduler22;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.dilumdesilva.scheduler22.DB.SQLiteHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @DevelopedBy Dilum De Silva 2016142 | w16266371
 *
 */
public class MainScreen extends AppCompatActivity implements View.OnClickListener {

    //declaring the GUI components
    private Button btn_create, btn_viewEdit, btn_delete, btn_move, btn_search;
    Button btn_deleteAll, btn_deleteSelected;
    CalendarView calendarView;

    private String date;
    PopupWindow popupWindow;

    SQLiteHandler sqLiteHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //an instance of db handling class
        sqLiteHandler = new SQLiteHandler(this, null,null, 1);

        //initializing the GUI components
        btn_create = (Button) findViewById(R.id.btn_createAppointment);
        btn_create.setOnClickListener(this);

        btn_viewEdit = (Button) findViewById(R.id.btn_viewEditAppointment);
        btn_viewEdit.setOnClickListener(this);

        btn_delete = (Button) findViewById(R.id.btn_deleteAppointment);
        btn_delete.setOnClickListener(this);

        btn_move = (Button) findViewById(R.id.btn_moveAppointment);
        btn_move.setOnClickListener(this);

        btn_search = (Button) findViewById(R.id.btn_searchAppointment);
        btn_search.setOnClickListener(this);

        calendarView = (CalendarView) findViewById(R.id.ms_calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateSelected = simpleDateFormat.format(new GregorianCalendar(year, month, dayOfMonth).getTime());
                date = dateSelected;

                //Toast.makeText(getBaseContext(),dateSelected,Toast.LENGTH_SHORT).show();
            }
        });


        //initialize the default date  and assign it to the date variable in case if he user doesn't
        //click on any date.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateSelected = simpleDateFormat.format(new Date(calendarView.getDate()));
        date = dateSelected;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_createAppointment :{

                Intent intent = new Intent(getBaseContext() , CreateAppointmentScreen.class);
                intent.putExtra("Date" , date ); // format - dd/MM/yyyy
                startActivity(intent);
                break;

            }
            case R.id.btn_deleteAppointment : {
                deleteAppointmentPopup(v);
                break;
            }
            case R.id.btn_viewEditAppointment:{
                Intent intent = new Intent(getBaseContext() , EditAppointmentScreen.class);
                intent.putExtra("Date" , date ); // format - dd/MM/yyyy
                intent.putExtra("Change Type" , "Edit" );
                startActivity(intent);
                break;
            }
            case R.id.btn_moveAppointment :{
                Intent intent = new Intent(getBaseContext() , EditAppointmentScreen.class);
                intent.putExtra("Date" , date ); // format - dd/MM/yyyy
                intent.putExtra("Change Type" , "Move" );
                startActivity(intent);
                break;
            }
            case R.id.btn_searchAppointment :{

                Intent intent = new Intent(getBaseContext() , SearchAppoinmentScreen.class);
                startActivity(intent);
                break;

            }
        }
    }

    private void deleteAppointmentPopup(View v) {
        try {
            //to get an instance of layoutinflater
            LayoutInflater inflater = (LayoutInflater) MainScreen.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //initiate the view
            final View layout = inflater.inflate(R.layout.delete_popup,
                    (ViewGroup) findViewById(R.id.delete_popup));

            //initialize a size for the popup
            popupWindow = new PopupWindow(layout, 1000, 900 ,  true);
            // display the popup in the center
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            //Deletes all the appointments for a given date
            btn_deleteAll = (Button) layout.findViewById(R.id.btn_popup_deleteAll);
            btn_deleteAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(),"Deleted all the appointments on "+ date,Toast.LENGTH_LONG).show();
                    sqLiteHandler.deleteAppointments(date);
                    popupWindow.dismiss();
                }
            });

            //Opens up the list of appointments for the given date
            btn_deleteSelected = (Button) layout.findViewById(R.id.btn_popup_deleteSelected);
            btn_deleteSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getBaseContext() , EditAppointmentScreen.class);
                    intent.putExtra("Date" , date ); // format - dd/MM/yyyy
                    intent.putExtra("Change Type" , "Delete" );
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });



        }catch (Exception e){
            System.out.println("An error has been occurred inside method deleteAppointmentPopup");
            e.printStackTrace();
        }
    }
}
