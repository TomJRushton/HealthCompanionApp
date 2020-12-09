package com.cssd.ehealthcompanionapp.data.services;

import com.cssd.ehealthcompanionapp.data.services.support.ResultCheck;
import com.cssd.ehealthcompanionapp.data.services.support.ServiceResult;
import com.cssd.ehealthcompanionapp.database.AppointmentsRepo;
import com.cssd.ehealthcompanionapp.database.PatientsRepo;
import com.cssd.ehealthcompanionapp.dtos.Appointment;
import com.cssd.ehealthcompanionapp.dtos.BloodPressure;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

public class AppointmentService {
    private AppointmentService() {
    }

    private static AppointmentService appointmentService;

    //provides instance / inistialised service
    synchronized public static AppointmentService getInstance() {
        if (appointmentService == null)
            appointmentService = new AppointmentService();
        return appointmentService;
    }

    //function for adding to the firebase database in the appointment section
    public void add(Appointment appointment, Consumer<ResultCheck<Appointment>> result) {
        ServiceResult<Appointment> r = new ServiceResult<>(result);
        AppointmentsRepo.getInstance().add(appointment, r::accept, r::fail);
    }


    //filters the firebase appointment section by messages sent only by the doctor and then takes the
    //most recent one to be displayed
    public void getMostRecentAppointmentMessage(Consumer<ResultCheck<Appointment>> result){
        ServiceResult<Appointment> r = new ServiceResult<>(result);
        PatientsService.getInstance().getOwnAccountId(result2 -> {
            String patientId = result2.get();
            AppointmentsRepo.getInstance().stream(query -> {
                        return query.orderByChild("patientId").equalTo(patientId);
            }, success -> {
                        Optional<Appointment> maxAppointment = success.filter(appointment -> {
                           return appointment.getFrom().equals("doctor");
                        }).max((appointment, t1) -> (int) (appointment.getTime() - t1.getTime()));
                        if (maxAppointment.isPresent()) {
                            r.accept(maxAppointment.get());
                        }else{
                            r.fail("No appointment found");
                        }
                    },
                    r::fail);
        });


    }
}
