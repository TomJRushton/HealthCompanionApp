package com.cssd.ehealthcompanionapp.data.services;

import com.cssd.ehealthcompanionapp.data.services.support.ResultCheck;
import com.cssd.ehealthcompanionapp.data.services.support.ServiceResult;
import com.cssd.ehealthcompanionapp.database.PatientsRepo;
import com.cssd.ehealthcompanionapp.dtos.GenericAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class PatientsService {

    private PatientsService() {
    }
    private static PatientsService patientsService;
    synchronized public static PatientsService getInstance() {
        if (patientsService == null)
            patientsService = new PatientsService();
        return patientsService;
    }

    public void add(GenericAccount genericAccount, Consumer<ResultCheck<GenericAccount>> result) {
        ServiceResult<GenericAccount> r = new ServiceResult<>(result);
        PatientsRepo.getInstance().set(genericAccount.getUid(), genericAccount, r::accept, r::fail);
    }

    public void getOwnAccount(Consumer<ResultCheck<GenericAccount>> result){
        ServiceResult<GenericAccount> r = new ServiceResult<>(result);
        getOwnAccountId(result2 -> {
            if (result2.isSuccess())
                PatientsRepo.getInstance().get(result2.get(), r::accept, r::fail);
            else
                r.fail("No Account Found");
        });
    }

    public void getOwnAccountId(Consumer<ResultCheck<String>> result) {
        ServiceResult<String> r = new ServiceResult<>(result);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            r.accept(currentUser.getUid());
        }else{
            r.fail("No Account Found");
        }
    }

    public void find(String uid, Consumer<ResultCheck<GenericAccount>> result) {
        ServiceResult<GenericAccount> r = new ServiceResult<>(result);
        PatientsRepo.getInstance().get(uid, r::accept, r::fail);
    }

    public void update(String uid, UnaryOperator<GenericAccount> update,
                       Consumer<ResultCheck<GenericAccount>> result) {
        ServiceResult<GenericAccount> r = new ServiceResult<>(result);
        PatientsRepo instance = PatientsRepo.getInstance();
        instance.update(uid, update,r::accept, r::fail);
    }

//    public void temp(String uid, Consumer<ResultCheck<Stream<GenericAccount>>> result){
//        ServiceResult<Stream<GenericAccount>> r = new ServiceResult<>(result);
//        PatientsRepo.getInstance().stream(query -> {
//            query.orderByKey().
//        }, r::accept, r::fail);
//    }

}
