package com.example.dilumdesilva.scheduler22;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dilumdesilva.scheduler22.Appointment;

public class AppointmentAdaptor extends ArrayAdapter<Appointment> {

    TextView titleTV , detailsTV;
    
    public AppointmentAdaptor(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
